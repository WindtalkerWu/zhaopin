package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.CommentAdapter;
import com.qianniu.zhaopin.app.adapter.InsidersListAdapter;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.CommentInfo;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.RequestInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.widget.PullToRefreshFooter;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentListActivity extends BaseActivity implements OnClickListener{
	private static final String TAG ="CommentListActivity";
	private ImageButton back;
	private ImageView comment;
	private TextView name;
	private PullToRefreshListView list;
	private CommentAdapter commentAdapter;
	private List<CommentInfo> commentInfos;
	
	private InsidersAndCompany ic;
	private int type;
	
	private static int commentRequest = 1;
	
	private static final int REFREASHEXCEPTION = 0;
	private static final int REFREASHSUCCESS = 1;
	private static final int LAODMOREEXCEPTION = 2;
	private static final int LAODMORESUCCESS = 3;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFREASHEXCEPTION: {
				list.onRefreshComplete();
				Object obj = msg.obj;
				if (obj instanceof AppException) {
					AppException e = (AppException)obj;
					e.makeToast(mContext);
				}
				break;
			}
			case REFREASHSUCCESS: {
				Object obj = msg.obj;
				if (obj instanceof Result) {
					Result result = (Result)obj;
					initCommentSuccess(result);
				}
				break;
			}
			case LAODMOREEXCEPTION: {
				list.completeLoadMore(PullToRefreshFooter.STATE_NORMAL);
				Object obj = msg.obj;
				if (obj instanceof AppException) {
					AppException e = (AppException)obj;
					e.makeToast(mContext);
				}
				break;
			}
			case LAODMORESUCCESS: {
				Object obj = msg.obj;
				if (obj instanceof Result) {
					Result result = (Result)obj;
					loadMoreCommentSuccess(result);
				}
				break;
			}
			default:
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_list);
		
		// 友盟统计
		UmShare.UmsetDebugMode(this);
	
		initView();
		initListView();
		getIntentData();
		firstRefreashing();
	}
	private void initListView() {
		commentInfos = new ArrayList<CommentInfo>();
		commentAdapter = new CommentAdapter(mContext, commentInfos);
		list.setLoadMoreAdapter(commentAdapter);
		
		list.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
					list.onRefreshComplete();
					return;
				}
				doRefreash();
			}
		});
		list.setOnLoadMoreListener(new PullToRefreshListView.OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				doLoadMore();
			}
		});
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.comment_list_goback);
		comment = (ImageView) findViewById(R.id.comment_list_comment);
//		name = (TextView) findViewById(R.id.comment_list_tips);
		list = (PullToRefreshListView) findViewById(R.id.comment_list_content);
		name = (TextView) LayoutInflater.from(mContext).inflate(R.layout.comment_head_name, null);
		list.addHeaderView(name);
		
		back.setOnClickListener(this);
		comment.setOnClickListener(this);
	}
	private void doRefreash() {
		ApiClient.pool.execute(new Runnable() {
			@Override
			public void run() {
				Result result = refreshCommentList();
				if (result != null) {
					handler.sendMessage(handler.obtainMessage(REFREASHSUCCESS, result));
				}
			}
		});
	}
	private void doLoadMore() {
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			list.completeLoadMore(PullToRefreshFooter.STATE_NORMAL);
			return;
		}
		ApiClient.pool.execute(new Runnable() {
			@Override
			public void run() {
				Result result = loadMoreCommentList();
				if (result != null) {
					handler.sendMessage(handler.obtainMessage(LAODMORESUCCESS, result));
				}
			}
		});
	}
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			ic = (InsidersAndCompany) intent.getSerializableExtra("ic");
			type = ic.getType();
			MyLogger.i(TAG, "type##" + type);
		}
		String nameStr = getResources().getString(R.string.comment_list_tips);
		name.setText(String.format(nameStr, ic.getName()));
	}
	private Result refreshCommentList() {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setCount(InsidersListAdapter.onePageCount);
		try {
			return getCommentList(requestInfo);
		} catch (AppException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(REFREASHEXCEPTION, e));
		}
		return null;
	}
	private Result loadMoreCommentList() {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setCount(InsidersListAdapter.onePageCount);
		requestInfo.setDirection(0);
		int size = commentInfos.size();
		if (size > 0) {
			requestInfo.setOffsetid(commentInfos.get(size - 1).getId());
		}
		try {
			return getCommentList(requestInfo);
		} catch (AppException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(LAODMOREEXCEPTION, e));
		}
		return null;
	}
	private Result getCommentList(RequestInfo requestInfo) throws AppException {
		return ApiClient.getCommentList(mContext, ic.getId(),
				type, requestInfo);
	}
	private void firstRefreashing() {
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			return;
		} 
		list.firstRefreshing();
		doRefreash();
	}
	private void initCommentSuccess(Result result) {
		if (result != null) {
			int status = PullToRefreshFooter.STATE_NORMAL;
			if (result.OK()) {
				String jsonStr = result.getJsonStr();
				if (!TextUtils.isEmpty(jsonStr)) {
					commentInfos = JSONArray.parseArray(jsonStr, CommentInfo.class);
					commentAdapter.setCommentInfos(commentInfos);
					commentAdapter.notifyDataSetChanged();
					if (commentInfos.size() < CommentAdapter.onePageCount) {
						status = PullToRefreshFooter.STATE_LOADFULL;
					}
				}
			} else if (Result.CODE_DATA_EMPTY == result.getErrorCode()) {
				status = PullToRefreshFooter.STATE_NULL;
				if (commentInfos.size() > 0) {
					commentInfos.clear();
					commentAdapter.notifyDataSetChanged();
				}
				UIHelper.ToastMessage(mContext, R.string.industry_insiders_no_data);
			} else {
				status = PullToRefreshFooter.STATE_NULL;
				result.handleErrcode(CommentListActivity.this);
			}
			list.onRefreshComplete(status);
		}
	}
	private void loadMoreCommentSuccess(Result result) {
		if (result != null) {
			int status = PullToRefreshFooter.STATE_NORMAL;
			if (result.OK()) {
				String jsonStr = result.getJsonStr();
				if (!TextUtils.isEmpty(jsonStr)) {
					List<CommentInfo> commentInfosMore = JSONArray.parseArray(jsonStr, CommentInfo.class);
					commentInfos.addAll(commentInfosMore);
					commentAdapter.notifyDataSetChanged();
					if (commentInfos.size() < CommentAdapter.onePageCount) {
						status = PullToRefreshFooter.STATE_LOADFULL;
					}
				}
			} else if (Result.CODE_DATA_EMPTY == result.getErrorCode()) {
				status = PullToRefreshFooter.STATE_LOADFULL;
			} else {
				result.handleErrcode(CommentListActivity.this);
			}
			list.completeLoadMore(status);
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_list_goback:
			finish();
			break;
		case R.id.comment_list_comment:
			// 友盟统计--评论墙--评论入口按钮
			UmShare.UmStatistics(this, "CommentList_CommentButton");

			startCommentActivity();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == ActivityRequestCode.RESULT_ACTIVITY_LOGIN) {
				startCommentActivity();
			}
			if (requestCode == commentRequest) {
				list.setSelection(0);
				firstRefreashing();
			}
		}
	}
	private void startCommentActivity() {
		if (((AppContext)mContext).isLoginAndToLogin(CommentListActivity.this)) {
			Intent intent = new Intent(CommentListActivity.this, CommentActivity.class);
			intent.putExtra("ic", ic);
			startActivityForResult(intent, commentRequest);
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// 友盟统计
		UmShare.UmPause(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
		// 友盟统计
		UmShare.UmResume(this);
	}
}

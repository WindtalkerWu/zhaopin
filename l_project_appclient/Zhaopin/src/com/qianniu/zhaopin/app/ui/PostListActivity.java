package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.RewardInfoListAdapter;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.CompanyInfo;
import com.qianniu.zhaopin.app.bean.RequestInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.widget.PullToRefreshFooter;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;

public class PostListActivity extends BaseActivity implements OnClickListener{
	private ImageView back;
	private PullToRefreshListView postList;
	private RewardInfoListAdapter adapter;
	private List<RewardData> posts;
	
	private int selectIndex;
	
	private CompanyInfo companyInfo;
	
	private static final int REFREASHEXCEPTION = 0;
	private static final int REFREASHSUCCESS = 1;
	private static final int LAODMOREEXCEPTION = 2;
	private static final int LAODMORESUCCESS = 3;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFREASHEXCEPTION: {
				postList.onRefreshComplete(PullToRefreshFooter.STATE_NULL);
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
					initPostSuccess(result);
				}
				break;
			}
			case LAODMOREEXCEPTION: {
				postList.completeLoadMore(PullToRefreshFooter.STATE_NORMAL);
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
					morePostSuccess(result);
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
		setContentView(R.layout.activity_post_list);
		getIntentData();
		initView();
		initListView();
		initData();
	}
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			companyInfo = (CompanyInfo) intent.getSerializableExtra("companyInfo");
		}
	}
	private void initView() {
		back = (ImageView) findViewById(R.id.post_list_goback);
		postList = (PullToRefreshListView) findViewById(R.id.post_list);
		
		back.setOnClickListener(this);
	}
	private void initData() {
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			return;
		}
		postList.firstRefreshing();
		doRefresh();
	}
	
	private void initListView() {
		posts = new ArrayList<RewardData>();
		adapter = new RewardInfoListAdapter(mContext, HeadhunterPublic.ACTIVITY_TYPE_POST, posts);
		postList.setLoadMoreAdapter(adapter);
		
		postList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
					postList.onRefreshComplete();
					return;
				}
				doRefresh();
			}
		});
		postList.setOnLoadMoreListener(new PullToRefreshListView.OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
					postList.onRefreshComplete(PullToRefreshFooter.STATE_NORMAL);
					return;
				}
				doLoadMore();
			}
		});
		postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectIndex = arg2 - 1;
				if (selectIndex < posts.size() && selectIndex >= 0) {
					RewardData selectData = posts.get(selectIndex);
					// 数据传输
					Bundle bundle = new Bundle();
					bundle.putSerializable(HeadhunterPublic.REWARD_DATATRANSFER_REWARDDATA, selectData);
					bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_ACTIYITYTYPE,
							HeadhunterPublic.ACTIVITY_TYPE_POST);

					// 进入悬赏详细界面
					Intent intent = new Intent();
					intent.setClass(mContext, RewardInfoActivity.class);
					intent.putExtras(bundle);
					startActivityForResult(intent,
							HeadhunterPublic.RESULT_ACTIVITY_CODE);
				}
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode) {
			if (resultCode == HeadhunterPublic.RESULT_REWARDINOF_OK) {
				Bundle bundle = data.getExtras();
				// 获取悬赏任务详细界面传递过来的数据
				if (null != bundle) {
					// 获取收藏状态
					String strCollectionStatus = bundle
							.getString(HeadhunterPublic.REWARD_DATATRANSFER_COLLECTIONFLAG);
					// 获取应聘状态
					String strCandidateStatus = bundle
							.getString(HeadhunterPublic.REWARD_DATATRANSFER_CANDIDATEFLAG);
					// 获取是否已读状态
					String strReadStatus = bundle
							.getString(HeadhunterPublic.REWARD_DATATRANSFER_REWARDREAD);
					// 更新状态
					RewardData rewardData = posts.get(selectIndex);
					if (!TextUtils.isEmpty(strCollectionStatus)) {
						rewardData.setAction_3(strCollectionStatus);
					}
					if (!TextUtils.isEmpty(strCandidateStatus)) {
						rewardData.setAction_1(strCandidateStatus);
					}
					if (!TextUtils.isEmpty(strReadStatus)) {
						rewardData.setAction_5(strReadStatus);
						RewardData.saveReadRewardID((AppContext)mContext, rewardData.getTask_Id());
						adapter.notifyDataSetChanged();
					}
				}
			}
		}
	}
	private void checkIsRead(List<RewardData> posts) {
		for (RewardData rewardData : posts) {
			boolean isRead = RewardData.checkRewardIsRead((AppContext)mContext, rewardData.getTask_Id());
			if (isRead) {
				rewardData.setAction_5(HeadhunterPublic.REWARD_READ_FLAG);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.post_list_goback:
			finish();
			break;

		default:
			break;
		}
	}

	private Result getPost(RequestInfo requestInfo) throws AppException {
		return ApiClient.getPosts(mContext, companyInfo.getId(), requestInfo);
//		return ApiClient.getPostsByType(mContext, "5", requestInfo, InsidersAndCompanyActivity.TYPECOMPANY);
	}
	private Result refreashPosts() {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setCount(RewardInfoListAdapter.onePageCount);
		try {
			return getPost(requestInfo);
		} catch (AppException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(REFREASHEXCEPTION, e));
		}
		return null;
	}
	private Result getMorePosts() {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setCount(RewardInfoListAdapter.onePageCount);
		requestInfo.setDirection(0);
		int size = posts.size();
		if (size > 0) {
			requestInfo.setOffsetid(posts.get(size - 1).getTask_Id());
		}
		try {
			return getPost(requestInfo);
		} catch (AppException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(LAODMOREEXCEPTION, e));
		}
		return null;
	}
	private void doLoadMore() {
		ApiClient.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				Result result = getMorePosts();
				if (result != null) {
					handler.sendMessage(handler.obtainMessage(LAODMORESUCCESS, result));
				}
			}
		});
	}
	private void doRefresh() {
		ApiClient.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				Result result = refreashPosts();
				if (result != null) {
					handler.sendMessage(handler.obtainMessage(REFREASHSUCCESS, result));
				}
			}
		});
	}
	private void initPostSuccess(Result result) {
		if (result != null) {
			int state = PullToRefreshFooter.STATE_NULL;
			if (result.OK()) {
				String resultStr = result.getJsonStr();
				if (!TextUtils.isEmpty(resultStr)) {
					posts = JSONArray.parseArray(resultStr, RewardData.class);
					checkIsRead(posts);
					adapter.setM_RewardLI(posts);
					adapter.notifyDataSetChanged();
					if (RewardInfoListAdapter.onePageCount > posts.size()) {
						state = PullToRefreshFooter.STATE_LOADFULL;
					} else {
						state = PullToRefreshFooter.STATE_NORMAL;
					}
				}
			} else if (result.getErrorCode() == Result.CODE_DATA_EMPTY) {
				UIHelper.ToastMessage(mContext, R.string.industry_insiders_no_data);
			} else {
				result.handleErrcode(PostListActivity.this);
			}
			postList.onRefreshComplete(state);
		}
	}
	private void morePostSuccess(Result result) {
		if (result != null) {
			int state = PullToRefreshFooter.STATE_NORMAL;
			if (result.OK()) {
				String resultStr = result.getJsonStr();
				if (!TextUtils.isEmpty(resultStr)) {
					List<RewardData> more = JSONObject.parseArray(resultStr, RewardData.class);
					checkIsRead(more);
					posts.addAll(more);
					adapter.notifyDataSetChanged();
					if (more.size() < RewardInfoListAdapter.onePageCount) {
						state = PullToRefreshFooter.STATE_LOADFULL;
					}
				}
			} else if (result.getErrorCode() == Result.CODE_DATA_EMPTY) {
				state = PullToRefreshFooter.STATE_LOADFULL;
			} else {
				result.handleErrcode(PostListActivity.this);
			}
			postList.completeLoadMore(state);
		}
	}
	
}

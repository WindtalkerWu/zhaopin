package com.qianniu.zhaopin.app.ui;

import java.util.List;
import java.util.zip.Inflater;

import com.alibaba.fastjson.JSONObject;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.InsidersListAdapter;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.RequestInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.ui.exposurewage.ExposureWageHomeActivity;
import com.qianniu.zhaopin.app.view.AdZoneView;
import com.qianniu.zhaopin.app.widget.PullToRefreshFooter;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class InsidersListActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = InsidersListActivity.class.getName();
	private ImageButton back;
	private TextView title;
	private Button apply;
	private PullToRefreshListView list;
	private InsidersListAdapter insidersAdapter;
	private List<InsidersAndCompany> insidersDatas;
	
	private AdZoneView adView;
	
//	public static final int TODETAILREQUEST = 1;
	
	private Context m_Context;
	
	private int type;
	
	private static final int EXCEPTIONCODE = 0;
	private static final int REFRESHSUCCESS = 1;
	private static final int LOADMORSUCCESS = 2;
	private Handler handler = new Handler(){ 
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case REFRESHSUCCESS: {
				Object obj = msg.obj;
				if (obj != null) {
					if (obj instanceof Result) {
						Result result = (Result)obj;
						initIndustryInsidersSuccess(result);
					}
				}
				break;
			}
			case LOADMORSUCCESS: {
				Object obj = msg.obj;
				if (obj != null) {
					if (obj instanceof Result) {
						Result result = (Result)obj;
						moreIndustryInsidersSuccess(result);
					}
				}
				break;
			}
			case EXCEPTIONCODE: {
				if (insidersDatas.size() == 0) {
					list.onRefreshComplete(PullToRefreshFooter.STATE_NULL);
				} else if (insidersDatas.size() > 0) {
					list.onRefreshComplete(PullToRefreshFooter.STATE_NORMAL);
					list.completeLoadMore(PullToRefreshFooter.STATE_NORMAL);
				} else {
					list.onRefreshComplete(PullToRefreshFooter.STATE_NULL);
					list.completeLoadMore(PullToRefreshFooter.STATE_NULL);
				}
				Object obj = msg.obj;
				if (obj != null) {
					if (obj instanceof AppException) {
						AppException exception = (AppException)obj;
						exception.makeToast(mContext);
					}
				}
				break;
			}
			default:
				break;
			}
		}
	};
	
//	private int _start_index = 0;
//	private int _end_index = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_industry_company);
		
		m_Context = this;
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
				
		initView();
		getIntentData();
		initListView();
		initData();
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			type = intent.getIntExtra("type", InsidersAndCompany.TYPEINSIDERS);
		}
		if (type == InsidersAndCompany.TYPECOMPANY) {
			title.setText(R.string.company_title_txt);
		}
		
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.industry_insiders_goback);
		title = (TextView) findViewById(R.id.industry_insiders_title_txt);
		apply = (Button) findViewById(R.id.industry_insiders_apply);
		list = (PullToRefreshListView) findViewById(R.id.insiders_list);
		list.setVisibility(View.VISIBLE);
		back.setOnClickListener(this);
		apply.setOnClickListener(this);
	}
	
	private void initListView() {
//		insidersAndCompanyDatas = new ArrayList<InsidersAndCompany>();
		insidersDatas = AppContext.getInsidersAndCompanyInDB(mContext, type);
		
		MyLogger.i("test", "insidersDatas++" + ObjectUtils.getJsonStringFromObject(insidersDatas));
		addADView();
		
		MyLogger.i(TAG, "insidersAndCompanyDatas##" + insidersDatas.size());
		insidersAdapter = new InsidersListAdapter(this, insidersDatas);
		insidersAdapter.setType(type);
		list.setLoadMoreAdapter(insidersAdapter);
		
		list.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
					list.onRefreshComplete();
					return;
				}
				doRefresh();
			}
		});
		list.setOnLoadMoreListener(new  PullToRefreshListView.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
					list.completeLoadMore(PullToRefreshFooter.STATE_NORMAL);
					return;
				}
				doLoadMore();
			}
		});
		if (insidersDatas == null || insidersDatas.size() == 0) {
			list.setFooterState(PullToRefreshFooter.STATE_NULL);
		} else if (insidersDatas.size() < InsidersListAdapter.onePageCount) {
			list.setFooterState(PullToRefreshFooter.STATE_LOADFULL);
		} else {
			list.setFooterState(PullToRefreshFooter.STATE_NORMAL);
		}
		//暂时不用
		list.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				list.settingOnScrollStateChanged(view, scrollState);
				insidersAdapter.setLoading(true);
//				if ((scrollState != AbsListView.OnScrollListener.SCROLL_STATE_FLING) 
//						&& list.getState() != PullToRefreshListView.REFRESHING) {//list停止滚动时加载图片
////					loadVisiableImage();
//	            }
//				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
//					insidersAdapter.setLoading(false);
//					MyLogger.i(TAG, "SCROLL_STATE_FLING");
//				} else {
//					insidersAdapter.setLoading(true);
//					MyLogger.i(TAG, "！！！SCROLL_STATE_FLING");
//					if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE 
//							&& list.getState() != PullToRefreshListView.REFRESHING) {
//						loadVisiableImage();
//					}
//				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				list.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
	}
	private void addADView() {
		ViewGroup headview = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.banner_layout, null);
		adView = new AdZoneView(InsidersListActivity.this, AdZoneView.ADZONE_ID_FAMOUS_MAN);
		headview.addView(adView);
		((LinearLayout.LayoutParams) adView.getLayoutParams()).rightMargin = CommonUtils.dip2px(mContext, 6.6f);
		list.addHeaderView(headview);
	}
	private void loadVisiableImage() {
		 int start = list.getFirstVisiblePosition();
         int last = list.getLastVisiblePosition();
         int visiable = last - start + 1;
         MyLogger.i("hjk", "start##" + start);
         MyLogger.i("hjk", "last##" + last);
         for (int i = 0; i < visiable; i++) {
         	 View visiableView = list.getChildAt(i);
//	             MyLogger.i("hjk", "visiableView##" + visiableView);
	             insidersAdapter.loadImage(start + i, visiableView);
			}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 友盟统计
		UmShare.UmResume(m_Context);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// 友盟统计
		UmShare.UmPause(m_Context);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapManager.shutDownAllLoad();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.industry_insiders_goback:
			finish();
			break;
		case R.id.industry_insiders_apply:
			// 友盟统计--圈内人/名企--申请按钮
			UmShare.UmStatistics(m_Context, "InsidersList_ApplyButton");
			
			UIHelper.sendEmail(InsidersListActivity.this,
					getResources().getString(R.string.apply_insiders_or_company_email),
					getResources().getString(R.string.apply_insiders_title), "");
			break;
		default:
			break;
		}
	}
	private void initData() {
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			return;
		} 
		list.firstRefreshing();
		doRefresh();
	}

	private void doLoadMore() {
		ApiClient.pool.execute(new Runnable() {
			@Override
			public void run() {
				Result result = getMoreIndustryInsiders();
				if (result != null) {
					handler.sendMessage(handler.obtainMessage(LOADMORSUCCESS, result));
				}
			}
		});
	}
	private void doRefresh() {
		ApiClient.pool.execute(new Runnable() {
			@Override
			public void run() {
				Result result = refreashIndustryInsiders();
				if (result != null) {
					handler.sendMessage(handler.obtainMessage(REFRESHSUCCESS, result));
				}
			}
		});
	}
	//获取最新数据
	private Result refreashIndustryInsiders() {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setCount(InsidersListAdapter.onePageCount);
		return getInsidersOrCompany(requestInfo);
	}

	//获取更多数据
	private Result getMoreIndustryInsiders() {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setCount(InsidersListAdapter.onePageCount);
		int size = insidersDatas.size();
		requestInfo.setDirection(0);
		if (size > 0) {
			requestInfo.setOffsetid(insidersDatas.get(size - 1).getId());
		}
		return getInsidersOrCompany(requestInfo);
	}
	private Result getInsidersOrCompany(RequestInfo requestInfo) {
		Result result = null;
		try {
			if (type == InsidersAndCompany.TYPECOMPANY) {
				result = ApiClient.getCompanyRecruitment((AppContext)mContext, requestInfo);
			}
			if (type == InsidersAndCompany.TYPEINSIDERS) {
				result = ApiClient.getIndustryInsiders((AppContext)mContext, requestInfo);
			}
		} catch (AppException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(EXCEPTIONCODE, e));
		}
		return result;
	}

	private void initIndustryInsidersSuccess(Result result) {
		if (result != null) {
			int state = PullToRefreshFooter.STATE_NORMAL;
			if (result.OK()) {
				String resultStr = result.getJsonStr();
				if (!TextUtils.isEmpty(resultStr)) {
					insidersDatas = JSONObject.parseArray(resultStr, InsidersAndCompany.class);
					insidersAdapter.setLoading(true);
					insidersAdapter.setIndustryInsiderPeoples(insidersDatas);
					AppContext.refreashInsidersAndCompanyInDB(mContext, insidersDatas, type);
					insidersAdapter.notifyDataSetChanged();
					if (insidersDatas.size() < InsidersListAdapter.onePageCount) {
						state = PullToRefreshFooter.STATE_LOADFULL;
					}
				} else {
					state = PullToRefreshFooter.STATE_NORMAL;
				}
			} else if (result.getErrorCode() == Result.CODE_DATA_EMPTY) {//表示没有数据，这样需要与服务器保持一致 
				state = PullToRefreshFooter.STATE_NULL;
				if (insidersDatas.size() > 0) {
					insidersDatas.clear();
					AppContext.clearInsidersAndCompanyInDB(mContext, type);
					insidersAdapter.notifyDataSetChanged();
				}
				UIHelper.ToastMessage(mContext, R.string.industry_insiders_no_data);
			} else {
				result.handleErrcode(InsidersListActivity.this);
			}
			list.onRefreshComplete(state);
		}
	}
	private void moreIndustryInsidersSuccess(Result result) {
		if (result != null) {
			int state = PullToRefreshFooter.STATE_NORMAL;
			if (result.OK()) {
				String resultStr = result.getJsonStr();
				if (!TextUtils.isEmpty(resultStr)) {
					List<InsidersAndCompany> more = JSONObject.parseArray(resultStr, InsidersAndCompany.class);
					insidersAdapter.setLoading(true);
					insidersDatas.addAll(more);
					insidersAdapter.notifyDataSetChanged();
					if (more.size() < InsidersListAdapter.onePageCount 
							|| insidersDatas.size() >= InsidersListAdapter.totalCount) {
						state = PullToRefreshFooter.STATE_LOADFULL;
					}
				}
			} else if (result.getErrorCode() == Result.CODE_DATA_EMPTY) {
				state = PullToRefreshFooter.STATE_LOADFULL;
			} else {
				result.handleErrcode(InsidersListActivity.this);
			}
			list.completeLoadMore(state);
		}
	}
}

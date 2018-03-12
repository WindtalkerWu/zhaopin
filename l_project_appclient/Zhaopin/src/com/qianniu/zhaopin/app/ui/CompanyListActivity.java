package com.qianniu.zhaopin.app.ui;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huewu.pla.lib.MultiColumnListView.OnLoadMoreListener;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView.OnRefreshListener;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.CompanyListAdapter;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.RequestInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.view.AdZoneView;
import com.qianniu.zhaopin.app.widget.PullToRefreshFooter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CompanyListActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "InsidersAndCompanyActivity";
	private ImageButton back;
	private TextView title;
	private Button apply;
	private MultiColumnPullToRefreshListView list;
	private CompanyListAdapter companyAdapter;
	private List<InsidersAndCompany> companyDatas;
	
	private AdZoneView adView;
	
	public static final int TODETAILREQUEST = 1;
	
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
				if (companyDatas.size() == 0) {
					list.onRefreshComplete(PullToRefreshFooter.STATE_NULL);
				} else if (companyDatas.size() > 0) {
					list.onRefreshComplete(PullToRefreshFooter.STATE_NORMAL);
					list.onLoadMoreComplete(PullToRefreshFooter.STATE_NORMAL);
				} else {
					list.onRefreshComplete(PullToRefreshFooter.STATE_NULL);
					list.onLoadMoreComplete(PullToRefreshFooter.STATE_NORMAL);
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
		list = (MultiColumnPullToRefreshListView) findViewById(R.id.company_list);
		list.setVisibility(View.VISIBLE);
		back.setOnClickListener(this);
		apply.setOnClickListener(this);
	}
	private void initListView() {
//		insidersAndCompanyDatas = new ArrayList<InsidersAndCompany>();
		companyDatas = AppContext.getInsidersAndCompanyInDB(mContext, type);
		
		addADView();
		
		MyLogger.i(TAG, "insidersAndCompanyDatas##" + companyDatas.size());
		companyAdapter = new CompanyListAdapter(this, companyDatas);
		companyAdapter.setType(type);
		list.setAdapter(companyAdapter);
		list.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
					list.onRefreshComplete();
					return;
				}
				doRefresh();
			}
		});
		list.setOnLoadMoreListener(new  OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
					list.onLoadMoreComplete(PullToRefreshFooter.STATE_NORMAL);
					return;
				}
				doLoadMore();
			}
		});
		if (companyDatas == null || companyDatas.size() == 0) {
			list.setFooterState(PullToRefreshFooter.STATE_NULL);
		} else if (companyDatas.size() < CompanyListAdapter.onePageCount) {
			list.setFooterState(PullToRefreshFooter.STATE_LOADFULL);
		} else {
			list.setFooterState(PullToRefreshFooter.STATE_NORMAL);
		}
		//暂时不用
//		list.setOnScrollListener(new AbsListView.OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				list.settingOnScrollStateChanged(view, scrollState);
//				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE 
//						&& list.getState() != PullToRefreshListView.REFRESHING) {//list停止滚动时加载图片 
////					loadVisiableImage();
//	            } 
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				list.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//				insidersAndCompanyAdapter.setLoading(false);
//			}
//		});
	}
	private void addADView() {
		ViewGroup headview = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.banner_layout, null);
		adView = new AdZoneView(CompanyListActivity.this, AdZoneView.ADZONE_ID_FAMOUS_COMPANY);
		headview.addView(adView);
		((LinearLayout.LayoutParams) adView.getLayoutParams()).rightMargin = CommonUtils.dip2px(mContext, 6.6f);
		((LinearLayout.LayoutParams) adView.getLayoutParams()).leftMargin = CommonUtils.dip2px(mContext, 6.6f);
		((LinearLayout.LayoutParams) adView.getLayoutParams()).topMargin = CommonUtils.dip2px(mContext, 6.6f);
		list.addHeaderView(headview);
	}
	private void loadVisiableImage() {
		 int start = list.getFirstVisiblePosition();
         int last = list.getLastVisiblePosition();
         int visiable = last - start + 1;
//         MyLogger.i("hjk", "start##" + start);
//         MyLogger.i("hjk", "last##" + last);
         for (int i = 0; i < visiable; i++) {
         	 View visiableView = list.getChildAt(i);
//	             MyLogger.i("hjk", "visiableView##" + visiableView);
	             companyAdapter.loadImage(start + i, visiableView);
			}
	}
	@Override
	protected void onResume() {
		super.onResume();
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
			switch (requestCode) {
			case TODETAILREQUEST:
//				if (data != null) {
//					int attentionCount = data.getIntExtra("attention", 0);
//					companyDatas.get(companyAdapter.getSelectIndex())
//						.setAttention_count(attentionCount);
//					companyAdapter.notifyDataSetChanged();
//				}
				break;

			default:
				break;
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.industry_insiders_goback:
			finish();
			break;
		case R.id.industry_insiders_apply:
			UIHelper.sendEmail(CompanyListActivity.this,
					getResources().getString(R.string.apply_insiders_or_company_email),
					getResources().getString(R.string.apply_company_title), "");
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
		requestInfo.setCount(CompanyListAdapter.onePageCount);
		return getInsidersOrCompany(requestInfo);
	}

	//获取更多数据
	private Result getMoreIndustryInsiders() {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setCount(CompanyListAdapter.onePageCount);
		int size = companyDatas.size();
		requestInfo.setDirection(0);
		if (size > 0) {
			requestInfo.setOffsetid(companyDatas.get(size - 1).getId());
//			requestInfo.setOffsetid(companyDatas.get(0).getId());
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
					companyDatas = JSONObject.parseArray(resultStr, InsidersAndCompany.class);
//					insidersAndCompanyAdapter.setLoading(true);
					companyAdapter.setIndustryInsiderPeoples(companyDatas);
					AppContext.refreashInsidersAndCompanyInDB(mContext, companyDatas, type);
					companyAdapter.notifyDataSetChanged();
					if (companyDatas.size() < CompanyListAdapter.onePageCount) {
						state = PullToRefreshFooter.STATE_LOADFULL;
					}
				} else {
					state = PullToRefreshFooter.STATE_NORMAL;
				}
			} else if (result.getErrorCode() == Result.CODE_DATA_EMPTY) {//表示没有数据，这样需要与服务器保持一致 
				state = PullToRefreshFooter.STATE_NULL;
				if (companyDatas.size() > 0) {
					companyDatas.clear();
					AppContext.clearInsidersAndCompanyInDB(mContext, type);
					companyAdapter.notifyDataSetChanged();
				}
				UIHelper.ToastMessage(mContext, R.string.industry_insiders_no_data);
			} else {
				result.handleErrcode(CompanyListActivity.this);
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
//					insidersAndCompanyAdapter.setLoading(true);
					companyDatas.addAll(more);
					companyAdapter.notifyDataSetChanged();
					if (more.size() < CompanyListAdapter.onePageCount 
							|| companyDatas.size() >= CompanyListAdapter.totalCount) {
						state = PullToRefreshFooter.STATE_LOADFULL;
					}
				}
			} else if (result.getErrorCode() == Result.CODE_DATA_EMPTY) {
				state = PullToRefreshFooter.STATE_LOADFULL;
			} else {
				result.handleErrcode(CompanyListActivity.this);
			}
			list.onLoadMoreComplete(state);
		}
	}
}

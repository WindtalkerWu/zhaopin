package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.SeekWorthyAdapter;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.CompanyInfo;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.RSSInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.SeekWorthyCommentInfo;
import com.qianniu.zhaopin.app.bean.SeekWorthyInfo;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.view.SeekWorthyBottomView;
import com.qianniu.zhaopin.app.view.SeekWorthyTopView;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

public class SeekWorthyActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "SeekWorthyActivity";
	
	private ImageView back;
	private TextView title;
	private ImageView toCommentWall;
	private ImageView subscription;
	
	private SeekWorthyTopView topView;
	private SeekWorthyBottomView bottomView;
	private PullToRefreshListView list;
	private SeekWorthyAdapter seekWorthyAdapter;

	private List<CompanyInfo> companys;
	private InsidersAndCompany boss;
	private CompanyInfo companyInfo;
	
	private int type;
	
	private SeekWorthyInfo seekWorthInfo;
	
	private static final int EXCEPTION = 1;
	private static final int INITSUCCESS = 2;
	private static final int SUBSCRIPTIONSUCCESS = 3;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case EXCEPTION: {
				dismissProgressDialog();
				Object obj = msg.obj;
				if (obj instanceof AppException) {
					AppException exception = (AppException)obj;
					exception.makeToast(mContext);
				}
				break;
			}
			case INITSUCCESS: {
				Object obj = msg.obj;
				if (obj instanceof Result) {
					Result result = (Result)obj;
					getDetailSuccess(result);
				}
				break;
			}
			case SUBSCRIPTIONSUCCESS: {
				dismissProgressDialog();
				Object obj = msg.obj;
				if (obj instanceof Result) {
					Result result = (Result)obj;
					doSubscriptionSuccess(result);
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
		setContentView(R.layout.activity_industry_seek_worthy);
		// 友盟统计
		UmShare.UmsetDebugMode(this);
		
		getIntentData();
		initView();
		initData();
	}
	private void initView() {
		back = (ImageView) findViewById(R.id.industry_detail_title_goback);
		title = (TextView) findViewById(R.id.industry_detail_title);
		toCommentWall = (ImageView) findViewById(R.id.industry_detail_title_commentwall);
		subscription = (ImageView) findViewById(R.id.industry_detail_subscription);
		list = (PullToRefreshListView) findViewById(R.id.industry_detail_list);

		if (type == InsidersAndCompany.TYPECOMPANY) {
			title.setText(R.string.company_detail_title);
		}
		initList();

		back.setOnClickListener(this);
		toCommentWall.setOnClickListener(this);
		subscription.setOnClickListener(this);
	}
	private void initList() {
		companys = new ArrayList<CompanyInfo>();
		refreshListTopView();
		topView.setVisibility(View.GONE);

		seekWorthyAdapter = new SeekWorthyAdapter(this, companys);
		list.setAdapter(seekWorthyAdapter);
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
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 > 1) {
					startPostActivity(companys.get(arg2 - 2));
				}
			}
		});
	}
	private void startPostActivity(CompanyInfo companyInfo) {
		Intent intent = new Intent(this, PostListActivity.class);
//		boss.setType(type);
		intent.putExtra("companyInfo", companyInfo);
		startActivity(intent);
	}
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			boss = (InsidersAndCompany) intent.getSerializableExtra("ic");
			type = boss.getType();
			MyLogger.i(TAG, "type##" + type);
		}
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.industry_detail_title_goback:
			finish();
			break;
		case R.id.industry_detail_title_commentwall:
			// 友盟统计--圈内人/名企--评论墙入口按钮
			UmShare.UmStatistics(this, "SeekWorthy_CommentwallButton");
			
			startCommentWall();
			break;
		case R.id.industry_detail_subscription:
			// 友盟统计--圈内人/名企--订阅按钮
			UmShare.UmStatistics(this, "SeekWorthy_SubscriptionButton");
			
			if (boss.getRss_flag() == InsidersAndCompany.RSS_UNAUTH) {
				UIHelper.showLoginActivityForResult(SeekWorthyActivity.this);
				return;
			}
			doSubscription();
			break;
		default:
			break;
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == ActivityRequestCode.RESULT_ACTIVITY_LOGIN) { //登录成功
				doSubscription();
			}
		}
	}
	private void doSubscription() {
		if (!UIHelper.isNetworkConnected((AppContext)mContext)) {
			return;
		}
		showProgressDialog();
		ApiClient.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				String id = boss.getId();
				int rss_status = boss.getRss_flag();
				String RssType = URLs.RSS_TYPEBOSS;
				if (type == InsidersAndCompany.TYPECOMPANY) {
					RssType = URLs.RSS_TYPECOMPANY;
					id = companyInfo.getId();
					rss_status = companyInfo.getRss_flag();
				}
				String status = "2";
				if (rss_status == InsidersAndCompany.RSS_NO) {
					status = "1";
				}
				Result result = null;
				try {
					
					result = ApiClient.submitRSS(mContext, id, status, RssType);
				} catch (AppException e) {
					e.printStackTrace();
					handler.sendMessage(handler.obtainMessage(EXCEPTION, result));
				}
				if (result != null) {
					handler.sendMessage(handler.obtainMessage(SUBSCRIPTIONSUCCESS, result));
				}
			}
		});
	}
	private void doSubscriptionSuccess(Result result) {
		if (result != null) {
			if (result.OK()) {
				if (type == InsidersAndCompany.TYPECOMPANY) {
					if (companyInfo.getRss_flag() == InsidersAndCompany.RSS_YES) {
						UIHelper.ToastMessage(mContext, R.string.seek_worthy_subscription_cancel_success);
						companyInfo.setRss_flag(InsidersAndCompany.RSS_NO);
					} else if (companyInfo.getRss_flag() == InsidersAndCompany.RSS_NO) {
						UIHelper.ToastMessage(mContext, R.string.seek_worthy_subscription_success);
						companyInfo.setRss_flag(InsidersAndCompany.RSS_YES);
					}
				} else {
					if (boss.getRss_flag() == InsidersAndCompany.RSS_YES) {
						UIHelper.ToastMessage(mContext, R.string.seek_worthy_subscription_cancel_success);
						boss.setRss_flag(InsidersAndCompany.RSS_NO);
					} else if (boss.getRss_flag() == InsidersAndCompany.RSS_NO) {
						UIHelper.ToastMessage(mContext, R.string.seek_worthy_subscription_success);
						boss.setRss_flag(InsidersAndCompany.RSS_YES);
					}
				}
				refreshRss();
			} else {
				result.handleErrcode(this);
			}
		}
	}
	public void startCommentWall() {
		boss.setType(type);
		UIHelper.startCommentWall(SeekWorthyActivity.this, boss);
	}

	private void initData() {
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			return;
		} 
		list.firstRefreshing();
		doRefresh();
	}
	private void doRefresh() {
		ApiClient.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				Result result = getDetail();
				if (result != null) {
					handler.sendMessage(handler.obtainMessage(INITSUCCESS, result));
				}
			}
		});
	}
	private Result getDetail() {
		try {
			return ApiClient.getIndustryInsiderDetail((AppContext)mContext, boss.getId(), type);
		} catch (AppException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(EXCEPTION, e));
		}
		return null;
	}
	private void refreshListTopView() {
		if (topView == null) {
			if (type == InsidersAndCompany.TYPECOMPANY) {
				topView = new SeekWorthyTopView(this, companyInfo);
			} else {
				topView = new SeekWorthyTopView(this, boss);
			}
			list.addHeaderView(topView);
		} else {
			if (type == InsidersAndCompany.TYPECOMPANY) {
				topView.setCompanyInfo(companyInfo);
			} else {
				topView.setBoss(boss);
			}
		}
		topView.setVisibility(View.VISIBLE);
	}
	private void refreshListBottomView() {
		SeekWorthyCommentInfo seekWorthyCommentInfo = seekWorthInfo.getComment();
		if (seekWorthyCommentInfo == null || seekWorthyCommentInfo.isEmpty()) {
			return;
		}
		if ( bottomView == null) {
			bottomView = new SeekWorthyBottomView(SeekWorthyActivity.this, seekWorthyCommentInfo);
			list.addFooterView(bottomView);
		} else {
			bottomView.setSeekWorthyCommentInfo(seekWorthyCommentInfo);
		}
		bottomView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCommentWall();
			}
		});
	}
	private void refreashView() {
		refreshRss();
		refreshListTopView();
		refreshListBottomView();
		seekWorthyAdapter.setCompanys(companys);
		seekWorthyAdapter.notifyDataSetChanged();
	}
	//刷新订阅状态
	private void refreshRss() {
		int rss_flag = boss.getRss_flag();
		if (type == InsidersAndCompany.TYPECOMPANY && companyInfo != null) {
			rss_flag = companyInfo.getRss_flag();
//			title.setText(companyInfo.getTitle());
		}
		switch (rss_flag) {
		case InsidersAndCompany.RSS_NO:
			subscription.setImageResource(R.drawable.common_button_subscription_add);
			break;
		case InsidersAndCompany.RSS_YES:
			subscription.setImageResource(R.drawable.common_button_subscription_cancel);
			break;

		default:
			break;
		}
		subscription.setVisibility(View.VISIBLE);
	}
	private void getDetailSuccess(Result result) {
		if (result != null) {
			if (result.OK()) {
				String resultStr = result.getJsonStr();
				if (!TextUtils.isEmpty(resultStr)) {
					seekWorthInfo = (SeekWorthyInfo) ObjectUtils
							.getObjectFromJsonString(resultStr, SeekWorthyInfo.class);
					if (seekWorthInfo != null) {
						companyInfo = seekWorthInfo.getRecommend_company();
						InsidersAndCompany ic = seekWorthInfo.getBoss();
						if (ic != null)  {
							boss = ic;
						}
						if (seekWorthInfo.getCompany() != null) {
							companys = seekWorthInfo.getCompany();
						}
						
						refreashView();
					} else {
						UIHelper.ToastMessage(mContext, R.string.seek_worthy_error);
					}
				}
			} else {
				
			}
			list.onRefreshComplete();
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
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapManager.shutDownAllLoad();
	}
}

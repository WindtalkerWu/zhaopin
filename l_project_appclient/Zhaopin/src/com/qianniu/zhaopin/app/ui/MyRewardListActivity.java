package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.MyRewardListAdapter;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.bean.RewardInfo;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MyRewardListActivity extends BaseActivity implements 
OnScrollListener, PullToRefreshListView.OnRefreshListener, OnItemClickListener{
	
	private TextView title;
	private ImageView back;
	private ImageView addReward;
	private PullToRefreshListView rewardList;
	private MyRewardListAdapter rewardAdapter;
	private List<RewardInfo> myRewardList;
	
	private AsyncTask myRewardAsyncTask;
	
	private int modifyRequest = 1;
	private int mobileAuthRequest = 2;
	public static String myReward = "myReward";
	public static String PAY_FLAG = "pay_flag";
	
	private int m_nChooseItem = 0;

	private final static int GETMYREWARDSUCCESS = 1;
	private final static int GETMYREWARDERROR = 2;
//	private final static int MOBILEAUTHESUCCESS = 3;
//	private final static int MOBILEAUTHERROR = 4;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
//			case MOBILEAUTHESUCCESS:
//			{
//				dismissProgressDialog();
//				Object obj = msg.obj;
//				if (obj != null) {
//					if (obj instanceof ReqUserInfo) {
//						ReqUserInfo userInfo = (ReqUserInfo)obj;
//						if ("1".equals(userInfo.getPhone_verified())) { //如果是认证过的 就存下来
//							startPublishRewardActivity();
//							DMSharedPreferencesUtil.putSharePre(getApplicationContext(),
//									DMSharedPreferencesUtil.DM_AUTH_INFO,
//									((AppContext)getApplicationContext()).getAccount(),
//									ObjectUtils.getJsonStringFromObject(userInfo));
//						} else {
//							showMobileAuthDialog();
//						}
//					}
//				}
//			}
//				break;
//			case MOBILEAUTHERROR:
//			 {
//					dismissProgressDialog();
//					Object obj = msg.obj;
//					if (obj != null) {
//						if (obj instanceof Result) {
//							Result result = (Result) obj;
//							result.handleErrcode(MyRewardListActivity.this);
//						} else if (obj instanceof AppException) {
//							AppException exception = (AppException) obj;
//							exception.makeToast(getApplicationContext());
//						}
//					}
//				}
//				break;
			case GETMYREWARDSUCCESS:
				if (rewardAdapter == null) {
					rewardAdapter = new MyRewardListAdapter(getApplicationContext(), myRewardList);
					rewardList.setAdapter(rewardAdapter);
				} else {
					rewardAdapter.setRewardList(myRewardList);
				}
				
				rewardList.onRefreshComplete();
				rewardList.setSelection(0);
				

				int count = myRewardList.size();
				if (myRewardList.size() == 1 && TextUtils.isEmpty(myRewardList.get(0).getTask_id())) {
					count = 0;
				}
				title.setText(String.format(getResources().getString(R.string.my_reward_list_title_txt, count)));
				rewardAdapter.notifyDataSetChanged();
				break;
			case GETMYREWARDERROR:
				Object obj = msg.obj;
				if (obj != null) {
					if (obj instanceof Result) {
						Result result = (Result) msg.obj;
						if (result.getErrorCode() == Result.CODE_DATA_EMPTY) {
							UIHelper.ToastMessage(mContext, R.string.my_reward_list_null);
						} else {
							result.handleErrcode(MyRewardListActivity.this);
						}
					} else if (obj instanceof AppException) {
						AppException exception = (AppException) msg.obj;
						exception.makeToast(getApplicationContext());
					}
				}
				rewardList.onRefreshComplete();
				rewardList.setSelection(0);
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_reward_list);
//		getIntentData();
	
		// 友盟统计
		UmShare.UmsetDebugMode(mContext);
	
		initView();
		setListener();
		firstRefreash();
	}
//	private void getIntentData() {
//		Intent intent = getIntent();
//		if (intent != null && intent.getBooleanExtra(Fragment_reward.fromMainReward, false)) {
//			Intent intentnew = new Intent(MyRewarListActivity.this, PublishRewardActivity.class);
//			intentnew.putExtra(Fragment_reward.fromMainReward, true);
//			startActivityForResult(intentnew, modifyRequest);
//		}
//	}
	@Override
	protected void onStart() {
		super.onStart();
	}
	private void firstRefreash() {
		rewardList.firstRefreshing();
		rewardList.setSelection(0);
		initData();
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		// 友盟统计
		UmShare.UmResume(mContext);
	}
	private void startPublishRewardActivity() {
		Intent intent = new Intent(MyRewardListActivity.this, PublishRewardActivity.class);
		startActivityForResult(intent, modifyRequest);
	}
//	private void startMobileAuthActivity() {
//		Intent intent = new Intent(MyRewardListActivity.this, MobileAuthActivity.class);
//		startActivityForResult(intent, mobileAuthRequest);
//	}
//	private void showMobileAuthDialog() {
//		UIHelper.showCommonDialog(MyRewardListActivity.this, 
//				R.string.my_reward_list_auth_dialog_content,
//				R.string.my_reward_list_auth_dialog_ok,
//				R.string.my_reward_list_auth_dialog_cancel,
//				new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which == DialogInterface.BUTTON_POSITIVE) {
//					startMobileAuthActivity();
//				}
//				dialog.dismiss();
//			}
//		});
//	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode){
		case RESULT_OK:
			{
				if (requestCode == modifyRequest) {
					firstRefreash();
				}
				if (requestCode == ActivityRequestCode.RESULT_ACTIVITY_LOGIN) {
					firstRefreash();
				}
				if (requestCode == mobileAuthRequest) {
					startPublishRewardActivity();
				}			
			}
			break;
		case HeadhunterPublic.RESULT_FINISH:
			{
				finish();
			}
			break;
		case HeadhunterPublic.RESULT_MYREWARDDETAIL_CHANGE:
			{
				// 获取个人悬赏详情界面传递过来的数据
				Bundle bundle = data.getExtras();
				if (null != bundle) {
					String strPayFlag = bundle.getString(PAY_FLAG);
					if(null == strPayFlag){
						return;
					}
					
					if(strPayFlag.isEmpty()){
						return;
					}
					
					if(null == myRewardList){
						return;
					}
					
					// 更新状态
					RewardInfo ri = myRewardList.get(m_nChooseItem);
					if (null != ri) {
						// 更新支付状态
						if(!strPayFlag.equals(ri.getPay_flag())){
							ri.setPay_flag(strPayFlag);
							// 刷新list表
							if(null != rewardAdapter){
								rewardAdapter.notifyDataSetChanged();
							}
						}
					}
				}
			}
			break;
		}
	}
	
	private void initData() {
		myRewardAsyncTask = new MyRewardListAsyncTask().execute();

	}
	private void getMyReward() {
		try {
			Result result = ApiClient.getMyReward((AppContext)getApplicationContext());
			if (result.OK()) {
				myRewardList = com.alibaba.fastjson.JSONArray
						.parseArray(result.getJsonStr(), RewardInfo.class);
				handler.sendEmptyMessage(GETMYREWARDSUCCESS);
			} else {
				handler.sendMessage(handler.obtainMessage(GETMYREWARDERROR, result));
			}
		} catch (AppException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(GETMYREWARDERROR, e));
		}
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					Result result = ApiClient.getMyReward((AppContext)getApplicationContext());
////					myRewardList = ApiClient.getMyReward((AppContext)getApplicationContext());
//					if (result.OK()) {
//						myRewardList = com.alibaba.fastjson.JSONArray
//								.parseArray(result.getJsonStr(), RewardInfo.class);
//						handler.sendEmptyMessage(GETMYREWARDSUCCESS);
//					} else {
//						Message message = new Message();
//						message.what = GETMYREWARDERROR;
//						message.obj = result;
//						handler.sendMessage(message);
//					}
////					if (myRewardList != null) {
////						handler.sendEmptyMessage(GETMYREWARDSUCCESS);
////					} else {
////						handler.sendEmptyMessage(GETMYREWARDERROR);
////					}
//				} catch (AppException e) {
//					e.printStackTrace();
//					handler.sendEmptyMessage(GETMYREWARDERROR);
//				}
//			}
//		}).start();
	}
	public void initView() {
		title = (TextView) findViewById(R.id.my_reward_list_title);
		title.setText(String.format(getResources().getString(R.string.my_reward_list_title_txt, 0)));
		back = (ImageView) findViewById(R.id.my_reward_list_goback);
		addReward = (ImageView) findViewById(R.id.my_reward_list_add);
		rewardList = (PullToRefreshListView) findViewById(R.id.my_reward_list);
		
		initNull();
	}
	private void initNull() {
		myRewardList = new ArrayList<RewardInfo>();
//		RewardListData data = new RewardListData();
//		myRewardList.add(data);
		rewardAdapter = new MyRewardListAdapter(getApplicationContext(), myRewardList);
		rewardList.setAdapter(rewardAdapter);
	}
	public void setListener() {
		rewardList.setOnRefreshListener(this);
		rewardList.setOnScrollListener(this);
		rewardList.setOnItemClickListener(this);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		addReward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计--我的悬赏--新建悬赏按钮
				UmShare.UmStatistics(mContext, "MyRewarList_AddRewardButton");
				startPublishRewardActivity();
//				addReward();
			}
		});
	}
//	private void addReward() {
//		String authInfo = DMSharedPreferencesUtil.getSharePreStr(getApplicationContext(),
//				DMSharedPreferencesUtil.DM_AUTH_INFO,
//				((AppContext)getApplicationContext()).getAccount());
//
//		if (!TextUtils.isEmpty(authInfo)) {
//			ReqUserInfo userInfo = (ReqUserInfo) ObjectUtils.getObjectFromJsonString(authInfo, ReqUserInfo.class);
//			if ("1".equals(userInfo.getPhone_verified())) {
//				startPublishRewardActivity();
//				return;
//			}
//		}
//		showProgressDialog();
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				Result mobileAuthStatus = getMobileAuthStatus();
//				if (mobileAuthStatus == null) {
//					return;
//				}
//				if (mobileAuthStatus.OK()) {
//					String resultStr = mobileAuthStatus.getJsonStr();
//					ReqUserInfo userInfo = (ReqUserInfo) ObjectUtils
//							.getObjectFromJsonString(resultStr,
//									ReqUserInfo.class);
//					handler.sendMessage(handler.obtainMessage(MOBILEAUTHESUCCESS,
//							userInfo));
//				} else {
//					handler.sendMessage(handler.obtainMessage(MOBILEAUTHERROR,
//							mobileAuthStatus));
//				}
//			}
//		}).start();
//	}
//	// 获取手机认证状态
//		private Result getMobileAuthStatus() {
//			try {
//				Result result = ApiClient
//						.getUserInfo((AppContext) getApplicationContext());
//				return result;
//			} catch (AppException e) {
//				e.printStackTrace();
//				handler.sendMessage(handler.obtainMessage(MOBILEAUTHERROR, e));
//			}
//			return null;
//		}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		rewardList.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		rewardList.onScrollStateChanged(view, scrollState);
	}
	@Override
	public void onRefresh() {
		initData();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (0 == arg2){
			return;
		}
		
		m_nChooseItem = arg2 - 1;
		Intent intent = new Intent(MyRewardListActivity.this, MyRewardDetailActivity.class);
		intent.putExtra(myReward, myRewardList.get(m_nChooseItem));
		startActivityForResult(intent, modifyRequest);
	}
	private class MyRewardListAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(Void... params) {
			getMyReward();
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 友盟统计
		UmShare.UmPause(mContext);
	}
}

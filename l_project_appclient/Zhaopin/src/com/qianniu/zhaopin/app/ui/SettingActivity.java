package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.VersionData;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.service.ConnectionService;
import com.qianniu.zhaopin.app.widget.SlideSwitchView;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;
import com.qianniu.zhaopin.util.NumberUtils;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends BaseActivity {
	
	private ImageButton back;
	private TextView versionName;
	private RelativeLayout setting_select_city;
	private RelativeLayout setting_select_to_sorce;
	private RelativeLayout setting_features_introduction;
	private RelativeLayout setting_feedback;
	private RelativeLayout setting_check_version;
	private RelativeLayout setting_modify_pwd;
	private RelativeLayout setting_secure_pwd;
	private RelativeLayout setting_subscription_management;
	private RelativeLayout setting_qn_recommend;
	private RelativeLayout setting_task_showtype;
	private SlideSwitchView showTypeSwitch;
	
	private Button exitAccount;
	
	private VersionData versionData;
	
	private Context m_Context;
	
	private static int modifyPwdRequestCode = 1;
	
	private static int modifyPwdloginRequestcode = 2;
	private static int subscriptionManageloginRequestcode = 3;
	
//	private final static int CHECK_VERSION_SUCCESS = 1;
//	private final static int CHECK_VERSION_FAIL = 2;
//	private Handler handler = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//				case CHECK_VERSION_SUCCESS:
//					dismissProgressDialog();
//					if (NumberUtils.isHighVersion(getCurrentVersion().getVersion(), versionData.getVersion())) { //有新版本
//						AlertDialog.Builder builder = null;
//						DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
//							
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								if (which == DialogInterface.BUTTON_POSITIVE) {
//									ConnectionService.startServiceDownload(getApplicationContext(), versionData.getDownloadurl(), null);
//								}
//								dialog.dismiss();
//							}
//						};
//						UIHelper.showCommonDialog(SettingActivity.this, R.string.check_version_has_new, onClickListener);
//					} else { //没有新版本
//						UIHelper.ToastMessage(getApplicationContext(), R.string.check_version_already_new);
//					}
//					break;
//				case CHECK_VERSION_FAIL:
//					
//					UIHelper.ToastMessage(getApplicationContext(), R.string.check_version_fail);
//					break;
//
//				default:
//					break;
//			}
//		}
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		m_Context = this;
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	
		initView();
		setListener();
		
	}
	@Override
	protected void onResume() {
		super.onResume();

		initLoginStatus();
		// 友盟统计
		UmShare.UmResume(m_Context);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 友盟统计
		UmShare.UmPause(m_Context);
	}

	private void initLoginStatus() {
		if (!(((AppContext)getApplicationContext())).isLogin()) {
			exitAccount.setText(getResources().getString(R.string.setting_toLogin));
		} else {
			exitAccount.setText(getResources().getString(R.string.setting_exit_account));
		}
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.setting_goback);
		versionName = (TextView) findViewById(R.id.setting_app_versionname);
		setting_select_city = (RelativeLayout) findViewById(R.id.setting_select_city_item);
		setting_select_to_sorce = (RelativeLayout) findViewById(R.id.setting_select_to_sorce_item);
		setting_features_introduction = (RelativeLayout) findViewById(R.id.setting_features_introduction_item);
		setting_feedback = (RelativeLayout) findViewById(R.id.setting_feedback_item);
		setting_check_version = (RelativeLayout) findViewById(R.id.setting_check_version_item);
		setting_modify_pwd = (RelativeLayout) findViewById(R.id.setting_modify_pwd_item);
		setting_secure_pwd = (RelativeLayout) findViewById(R.id.setting_secure_pwd_item);
		setting_subscription_management = (RelativeLayout) findViewById(R.id.setting_subscription_management_item);
		setting_qn_recommend = (RelativeLayout) findViewById(R.id.setting_qn_recommend_item);
		setting_task_showtype = (RelativeLayout) findViewById(R.id.setting_task_showtype_item);
		showTypeSwitch = (SlideSwitchView) findViewById(R.id.setting_task_switch);
		exitAccount = (Button) findViewById(R.id.setting_exit_account);
		
		versionName.setText(String.format(getResources().getString(R.string.setting_version_name,
				getCurrentVersion().getVersion())));
		initShowTypeSwitch();
	}
	private void initShowTypeSwitch() {
		int type = DMSharedPreferencesUtil.getSharePreInt(mContext, DMSharedPreferencesUtil.DM_APP_DB,
				DMSharedPreferencesUtil.taskShowType);
		showTypeSwitch.setCheck(false);
		if (type == HeadhunterPublic.TASKSHOWTYPE_LIST) {
			showTypeSwitch.setCheck(true);
		}
	}

	private void setListener() {
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setting_select_city.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 友盟统计--设置--城市选择按钮
				UmShare.UmStatistics(m_Context, "Set_CityChooseButton");
				
				startCityChooseActivity();
			}
		});
		setting_feedback.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计--设置--意见反馈按钮
				UmShare.UmStatistics(m_Context, "Set_FeedBackButton");
				startFeedbackActivity();
			}
		});
		setting_modify_pwd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计--设置--修改密码按钮
				UmShare.UmStatistics(m_Context, "Set_ModifyPwdButton");
				if (UIHelper.checkIsLogin(mContext, SettingActivity.this, modifyPwdloginRequestcode)) {
					startModifyPwdActivity();
				}
			}
		});
		setting_secure_pwd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		setting_select_to_sorce.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计--设置--去评分按钮
				UmShare.UmStatistics(m_Context, "Set_SelectToSorceButton");
				startAppStoreActivity();
			}
		});
		setting_features_introduction.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计--设置--功能介绍按钮
				UmShare.UmStatistics(m_Context, "Set_FeaturesIntroductionButton");
				startFeaturesIntroActivity();
			}
		});
		setting_qn_recommend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计--设置--牵牛推荐按钮
				UmShare.UmStatistics(m_Context, "Set_QNRecommendButton");
				startAppRecommentActivity();
			}
		});
		setting_check_version.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计--设置--检查新版本按钮
				UmShare.UmStatistics(m_Context, "Set_CheckVersionButton");
				checkVersion();
			}
		});
		setting_subscription_management.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UmShare.UmStatistics(m_Context, "Set_SubscriptionManagementButton");
				if (UIHelper.checkIsLogin(mContext, SettingActivity.this, subscriptionManageloginRequestcode)) {
					// 友盟统计--设置--订阅管理按钮
					//UIHelper.showCustomManagerActivity(SettingActivity.this);
					UIHelper.showSubscriptionActivityBehindCheck(SettingActivity.this);
				}
			}
		});
//		setting_task_showtype.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				MyLogger.i("hjk", "isChecked##" + showTypeSwitch.isChecked());
//				showTypeSwitch.setCheck(!showTypeSwitch.isChecked());
//			}
//		});
		showTypeSwitch.SetOnChangedListener(new SlideSwitchView.OnChangedListener() {
			
			@Override
			public void OnChanged(boolean CheckState) {
				if (CheckState) {
					DMSharedPreferencesUtil.putSharePre(m_Context, DMSharedPreferencesUtil.DM_APP_DB,
							DMSharedPreferencesUtil.taskShowType, HeadhunterPublic.TASKSHOWTYPE_LIST);
				} else {
					DMSharedPreferencesUtil.putSharePre(m_Context, DMSharedPreferencesUtil.DM_APP_DB,
							DMSharedPreferencesUtil.taskShowType, HeadhunterPublic.TASKSHOWTYPE_WATERFALL);
				}
			}
		});
		exitAccount.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!(((AppContext)getApplicationContext())).isLogin()) {
					// 友盟统计--设置--登录按钮
					UmShare.UmStatistics(m_Context, "Set_LoginButton");
					UIHelper.showLoginActivity(SettingActivity.this);
				} else {
					// 友盟统计--设置--退出账号按钮
					UmShare.UmStatistics(m_Context, "Set_ExitButton");
					
					AlertDialog.Builder dialog = null;
					DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == dialog.BUTTON_POSITIVE) {
								(((AppContext)getApplicationContext())).Logout();
								UIHelper.showLoginActivity(SettingActivity.this);
								initLoginStatus();
							}
							if (which == dialog.BUTTON_NEGATIVE) {
								
							}
							dialog.dismiss();
						}
					};
					dialog = UIHelper.showCommonDialog(SettingActivity.this, R.string.exit_account_title, onClickListener);
				}
			}
		});
	}
	private void checkVersion() {

		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			return;
		}
		new CheckVersionAsyncTask().execute();
//		showProgressDialog(R.string.check_version_message);
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				try {
//					Result result = ApiClient.checkVersion((AppContext)getApplicationContext());
//					if (result.OK()) {
//						String jsonStr = result.getJsonStr();
//						versionData = (VersionData) ObjectUtils.getObjectFromJsonString(jsonStr,
//								VersionData.class);
//						handler.sendEmptyMessage(CHECK_VERSION_SUCCESS);
//					} else {
//						handler.sendEmptyMessage(CHECK_VERSION_FAIL);
//					}
//				} catch (AppException e) {
//					e.printStackTrace();
//					final AppException ee = e;
//					setting_check_version.post(new Runnable() {
//						
//						@Override
//						public void run() {
//							ee.makeToast(getApplicationContext());
//						}
//					});
//					handler.sendEmptyMessage(CHECK_VERSION_FAIL);
//				}
//			}
//		}).start();
	}
	private Result doCheckVersion() {
		try {
			Result result = ApiClient.checkVersion((AppContext)getApplicationContext());
			return result;
		} catch (AppException e) {
			final AppException ee = e;
			setting_check_version.post(new Runnable() {
				@Override
				public void run() {
					ee.makeToast(getApplicationContext());
				}
			});
		}
		return null;
	}
	private class CheckVersionAsyncTask extends AsyncTask<Void, Void, Result> {
		@Override
		protected void onPreExecute() {
			showProgressDialog(R.string.check_version_message);
			super.onPreExecute();
		}
		@Override
		protected Result doInBackground(Void... params) {
			return doCheckVersion();
		}

		@Override
		protected void onPostExecute(Result result) {
			dismissProgressDialog();
			if (result != null) {
				
				if (!mContext.checkIsOriginalApp()){
					UIHelper.ToastMessage(getApplicationContext(), R.string.check_version_already_new);
					return ;
				}
				if (result.OK()) {
					String jsonStr = result.getJsonStr();
					versionData = (VersionData) ObjectUtils.getObjectFromJsonString(jsonStr,
							VersionData.class);
					if (!TextUtils.isEmpty(versionData.getDownloadurl()) && NumberUtils.isHighVersion(getCurrentVersion().getVersion(), versionData.getVersion())) { //有新版本

//						UIHelper.startUpdateVersionActivity(mContext, versionData);
						UIHelper.showUpdateVersionDialog(SettingActivity.this, versionData);

					} else { //没有新版本
						UIHelper.ToastMessage(getApplicationContext(), R.string.check_version_already_new);
					}
				} else if (Result.CODE_DATA_EMPTY == result.getErrorCode()) {
					UIHelper.ToastMessage(getApplicationContext(), R.string.check_version_already_new);
				} else {
					result.handleErrcode(SettingActivity.this);
				}
			}
			super.onPostExecute(result);
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == modifyPwdRequestCode) {
				UIHelper.showLoginActivity(SettingActivity.this);
//				initLoginStatus();
			}
			if (requestCode == modifyPwdloginRequestcode) {
				startModifyPwdActivity();
			}
			if (requestCode == subscriptionManageloginRequestcode) {		
				UIHelper.showSubscriptionActivityBehindCheck(SettingActivity.this);
			}
		}
	}
	private void startAppRecommentActivity() {
		Intent intent = new Intent(SettingActivity.this, AppRecommentActivity.class);
		startActivity(intent);
	}
	private void startFeaturesIntroActivity() {
		Intent intent = new Intent(SettingActivity.this, FeaturesIntroductionActivity.class);
		startActivity(intent);
	}
	private void startAppStoreActivity() {
		try {
			String mAddress = "market://details?id=" + getPackageName(); 
		    Intent marketIntent = new Intent("android.intent.action.VIEW");  
		    marketIntent.setData(Uri.parse(mAddress ));  
		    startActivity(marketIntent);
		} catch(ActivityNotFoundException e) {
			UIHelper.ToastMessage(mContext, "您还没有安装任何应用市场");
		}
	}
	
	private void startModifyPwdActivity() {
		Intent intent = new Intent(SettingActivity.this, ModifyPwdActivity.class);
		startActivityForResult(intent, modifyPwdRequestCode);
	}
	private void startFeedbackActivity() {
		Intent intent = new Intent(SettingActivity.this, FeedbackActivity.class);
		startActivity(intent);
	}
	private void startCityChooseActivity() {
		Intent intent = new Intent(SettingActivity.this, CityChooseActivity.class);
		startActivity(intent);
	}
}

package com.qianniu.zhaopin.app.ui;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.alipay.android.app.sdk.AliPay;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.CityChooseData;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntityList;
import com.qianniu.zhaopin.app.bean.RewardInfo;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.view.ExplainInfoLeftPop;
import com.qianniu.zhaopin.app.view.Util;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.Rsa;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PublishRewardActivity extends BaseActivity {

	private static final String TAG = PublishRewardActivity.class.getName();
	public static final String REWARDAUDITION = "2"; // 面试悬赏
	public static final String REWARDENTRY = "3"; // 入职悬赏

	private ImageView back;
	private ImageView titleSubmit;
	private RadioGroup typeRadioGroup;
	private RadioButton auditionRadiobutton;
	private RadioButton entryRadiobutton;
	private LinearLayout rewardBonusTextBg;
	private TextView rewardBonusText;
	private EditText rewardBonus;
	private TextView rewardTimes;
	private RelativeLayout rewardTimesBg;
	private TextView rewardExpectation;
	private RelativeLayout rewardExpectationBg;
	private TextView rewardIndustry;
	private RelativeLayout rewardIndustryBg;
	private RelativeLayout rewardPostBg;
	private TextView rewardPost;
	private TextView rewardLocation;
	private RelativeLayout rewardLocationBg;
	private TextView rewardResume;
	private RelativeLayout rewardResumeBg;
	private TextView rewardIntroduce;

	private LinearLayout rewardContentBg;

	private CheckBox agreementCheckBox;

	private Button publishBtn;

	/******************** 数据库中全局数据 ********************/
	private List<OneLevelChooseData> timesData;// 周期
	private List<OneLevelChooseData> expectationData;// 期望年薪
	private List<OneLevelChooseData> industryData;// 行业

	/******************** 缓存已选中的数据 ********************/
	private List<CityChooseData> selectCities;//选中的城市 （多个）
	private ResumeSimpleEntity selectResumeEntity; //选中的 简历
	private OneLevelChooseData selectTimesData; //选中的悬赏周期
	private OneLevelChooseData selectExpectationData; //选中的期望的年薪
	private List<OneLevelChooseData> selectIndustryData; //选中的行业（多个）
	private String[] selectIndustryDataArray; //选中的行业的数组形式
	private String[] selectCitiesArray; //选中的城市的数组形式
	private String selectPostId = ""; //选中的职位的id

	private String rewardTaskId = "";
	private RewardInfo rewardData; //当前的我的悬赏实例

	// private PromptPopupWindow tipsWindows;

	private boolean isFromMainReward = false; //是否是从主界面的任务模块过来

	private static int requestMobileAuth = 1;

	private String m_strTotal;			// 总计支付的金额
	private String m_strTaskId;			// 悬赏任务id
	private String m_strPaymentOrderNo;	// 支付订单号
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLogger.i(TAG, "##onCreate##");
		setContentView(R.layout.activity_publish_reward);
		
		// 友盟统计
		UmShare.UmsetDebugMode(this);

		initView();
		setListener(); 
		if (savedInstanceState != null) {
			initSavedInstanceState(savedInstanceState);
		} else {
			getIntentData();
		}
		initDatas();
	}
	@Override
	protected void onResume() {
		super.onResume();
		// 友盟统计
		UmShare.UmResume(mContext);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 友盟统计
		UmShare.UmPause(mContext);
	}

	@Override
	public void finish() {
		MyLogger.i(TAG, "##finish##");
		super.finish();
	}

	@Override
	protected void onDestroy() {
		MyLogger.i(TAG, "##onDestroy##");
		super.onDestroy();
	}
	private void showMobileAuthDialog() {
		UIHelper.showCommonDialog(PublishRewardActivity.this, 
				R.string.my_reward_list_auth_dialog_content,
				R.string.my_reward_list_auth_dialog_ok,
				R.string.my_reward_list_auth_dialog_cancel,
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					startMobileAuthActivity();
				}
				dialog.dismiss();
			}
		});
	}
	private void startMobileAuthActivity() {
		Intent intent = new Intent(PublishRewardActivity.this, MobileAuthActivity.class);
		startActivityForResult(intent, requestMobileAuth);
	}
	private void initSavedInstanceState(Bundle savedInstanceState) {
		rewardData = (RewardInfo) savedInstanceState.get("rewardCache");
		initViewData();
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) { //intent中有数据
			if (isFromMainReward = intent.getBooleanExtra(
					Fragment_reward.fromMainReward, false)) {
			} else {
				rewardTaskId = intent
						.getStringExtra(MyRewardDetailActivity.rewardTaskId);
			}
			//从网络获取数据
			if (!TextUtils.isEmpty(rewardTaskId)) { 
				getrewardData();
			}
		}
		if (TextUtils.isEmpty(rewardTaskId)) { 
			String rewardDataStr = DMSharedPreferencesUtil.getByUserId(mContext, DMSharedPreferencesUtil.DM_MYREWARD_INFO,
					DMSharedPreferencesUtil.myRewardInfo);
			MyLogger.i(TAG, "rewardDataStr##" + rewardDataStr);
			if (!TextUtils.isEmpty(rewardDataStr)) {
				rewardData = (RewardInfo) ObjectUtils.getObjectFromJsonString(rewardDataStr, RewardInfo.class);
				initViewData();
			}
		}
	}

	// 初始化界面上的数据
	private void initViewData() {
		if (rewardData != null) {
			MyLogger.i(TAG, "rewardData is not null");
			initTypeView(rewardData.getTask_type());
			// 悬赏金额
			initBonusView(rewardData.getTask_bonus());
			// 悬赏周期
			initTimesView(rewardData.getTask_lifecycle());
			// 悬赏年薪
			initExpectationView(rewardData.getTask_expectation_id());

			// 悬赏行业
			selectIndustryDataArray = rewardData.getTask_category_id();
			initIndustryView();

			// 悬赏职位
			initPostView(rewardData.getJflv3_id());

			// 悬赏地点
			selectCitiesArray = rewardData.getTask_city();
			initCitiesView();
			// 选择的简历
			initResumeView(rewardData.getResume_id());
			initIntroduceView(rewardData.getTask_memo());

		}
	}

	private void initDatas() {
		getGlobalDataFromDB(DBUtils.GLOBALDATA_TYPE_SALARY);
		getGlobalDataFromDB(DBUtils.GLOBALDATA_TYPE_REWARDCYCLE);
		getGlobalDataFromDB(DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY);
		getGlobalDataFromDB(DBUtils.GLOBALDATA_TYPE_SPECIALTY);
	}

	private void initView() {
		back = (ImageView) findViewById(R.id.publish_reward_goback);
		titleSubmit = (ImageView) findViewById(R.id.publish_reward_title_submit);
		typeRadioGroup = (RadioGroup) findViewById(R.id.publish_reward_method);
		auditionRadiobutton = (RadioButton) findViewById(R.id.publish_reward_radiobutton_audition);
		entryRadiobutton = (RadioButton) findViewById(R.id.publish_reward_radiobutton_entry);
		rewardBonusTextBg = (LinearLayout) findViewById(R.id.publish_reward_money_txt);
		rewardBonusText = (TextView) findViewById(R.id.publish_reward_money_txtview);
		rewardBonus = (EditText) findViewById(R.id.publish_reward_money);
		rewardTimes = (TextView) findViewById(R.id.publish_reward_times);
		rewardTimesBg = (RelativeLayout) findViewById(R.id.publish_reward_times_bg);
		rewardExpectation = (TextView) findViewById(R.id.publish_reward_salary);
		rewardExpectationBg = (RelativeLayout) findViewById(R.id.publish_reward_salary_bg);
		rewardIndustry = (TextView) findViewById(R.id.publish_reward_industry);
		rewardIndustryBg = (RelativeLayout) findViewById(R.id.publish_reward_industry_bg);
		rewardPostBg = (RelativeLayout) findViewById(R.id.publish_reward_post_bg);
		rewardPost = (TextView) findViewById(R.id.publish_reward_post);
		rewardLocation = (TextView) findViewById(R.id.publish_reward_location);
		rewardLocationBg = (RelativeLayout) findViewById(R.id.publish_reward_location_bg);
		rewardResume = (TextView) findViewById(R.id.publish_reward_resume);
		rewardResumeBg = (RelativeLayout) findViewById(R.id.publish_reward_select_resume_bg);
		rewardIntroduce = (TextView) findViewById(R.id.publish_reward_introduce);
		publishBtn = (Button) findViewById(R.id.publish_reward_publish);

		rewardContentBg = (LinearLayout) findViewById(R.id.publish_reward_content_layout);
		agreementCheckBox = (CheckBox) findViewById(R.id.publish_reward_agreement_checkbox);
		
		initAgreementCheckBox();

	}
	private void initAgreementCheckBox() {
		TextView tv = (TextView) findViewById(R.id.publish_reward_agreement_text);
		String agreementNameStr = getResources().getString(R.string.publish_reward_agreement);
		SpannableString agreementName = new SpannableString(agreementNameStr);
		agreementName.setSpan(new ClickableSpan() {
	            @Override
	            public void onClick(View widget) {
	            	showTipsWindows(rewardContentBg);
	            }
	        }, 8, agreementName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(agreementName);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void showBonusTips(View v) {
		ExplainInfoLeftPop pop = new ExplainInfoLeftPop(mContext);
		pop.setExplainInfoText(R.string.popup_window_tips_content);
		int btn_width = v.getWidth()/2;
		DisplayMetrics dm = AppContext.getPhoneDisplayMetrics((AppContext)(mContext.getApplicationContext()));
//		int xoffset = (int) (-29 +btn_width);
//		int yoffset = (int) (3*dm.density);
//		pop.showAsDropDown(v ,xoffset,0);
		pop.showAsDropDown(v , 0,
				CommonUtils.dip2px(getApplicationContext(), -6));
		pop.update();
	}

	private void saveCache() {
		if (TextUtils.isEmpty(rewardTaskId)) {
			RewardInfo rewardInfo = getAddInfo();
			String rewardInfoStr = ObjectUtils.getJsonStringFromObject(rewardInfo);
			DMSharedPreferencesUtil.putByUserId(mContext, DMSharedPreferencesUtil.DM_MYREWARD_INFO,
					DMSharedPreferencesUtil.myRewardInfo,
					rewardInfoStr);
		}
	}
	private void setListener() {
		auditionRadiobutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				rewardBonus.setHint(R.string.publish_reward_money_entry_hint);
			}
		});
		entryRadiobutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				rewardBonus.setHint(R.string.publish_reward_money_audition_hint);
			}
		});
		rewardBonusTextBg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showBonusTips(rewardBonusText);
			}
		});
		titleSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				doSubmit();
			}
		});
		rewardContentBg.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(rewardBonus.getWindowToken(), 0);
				rewardContentBg.requestFocus();
				return false;
			}
		});
		rewardBonus.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
				}
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isFromMainReward) {
					isFromMainReward = false;
					setResult(HeadhunterPublic.RESULT_FINISH);
				}
				saveCache();
				finish();
			}
		});
		publishBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				doSubmit();
			}
		});
		rewardLocationBg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rewardContentBg.requestFocus();
				Intent intent = new Intent(PublishRewardActivity.this,
						CityChooseActivity.class);
				intent.putExtra(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_TYPE,
						HeadhunterPublic.CITYCHOOSE_TYPE_MULTIPLE);
				if (selectCities != null) {
					intent.putExtra(
							HeadhunterPublic.CITYCHOOSE_DATATRANSFER_DATA,
							(ArrayList) selectCities);
				}
				startActivityForResult(intent,
						HeadhunterPublic.RESULT_ACTIVITY_CODE);
				// Intent intent = new Intent(PublishRewardActivity.this,
				// ResumeListActivity.class);
				// intent.putExtra(GETRESUMEKEY, GETRESUMEVALUE);
				// startActivityForResult(intent,
				// HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
		});
		rewardResumeBg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rewardContentBg.requestFocus();
				Intent intent = new Intent(PublishRewardActivity.this,
						ResumeListActivity.class);
				intent.putExtra(HeadhunterPublic.RESUMELIST_CALLTYPE, HeadhunterPublic.RESUMELIST_CALLTYPE_PUBLISHREWARD);
				if (selectResumeEntity != null) {
					intent.putExtra(HeadhunterPublic.RESUMELIST_RESUMESELECTID,
							selectResumeEntity.getResumeId());
				}
				startActivityForResult(intent,
						HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
		});
		rewardIndustryBg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rewardContentBg.requestFocus();
				Intent intent = new Intent(PublishRewardActivity.this,
						IndustryChooseActivity.class);
				intent.putExtra(
						HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_TYPE,
						HeadhunterPublic.INDUSTRYCHOOSE_TYPE_MULTIPLE);
				if (selectIndustryData != null) {
					intent.putExtra(
							HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_DATA,
							(ArrayList) selectIndustryData);
				}
				startActivityForResult(intent,
						HeadhunterPublic.REQUEST_SELECT_INDUSTRY);
			}
		});
		rewardPostBg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(HeadhunterPublic.JOBFUNCTIONCHOOSE_DATA, selectPostId);

				Intent intent = new Intent();
				intent.setClass(mContext, JobFunctionsChooseActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent,
						HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
		});
		rewardExpectationBg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rewardContentBg.requestFocus();
				Intent intent = new Intent(PublishRewardActivity.this,
						OneLevelChooseActivity.class);
				intent.putExtra(
						HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_TITLE,
						getString(R.string.publish_reward_select_expectation_title));
				intent.putExtra(
						HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_DATA,
						(ArrayList) expectationData);
				if (selectExpectationData != null) {
					intent.putExtra(
							HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_SELECTED,
							selectExpectationData.getID());
				}
				startActivityForResult(intent,
						HeadhunterPublic.REQUEST_SELECT_EXPECTATION);
			}
		});
		rewardTimesBg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rewardContentBg.requestFocus();
				Intent intent = new Intent(PublishRewardActivity.this,
						OneLevelChooseActivity.class);
				intent.putExtra(
						HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_TITLE,
						getString(R.string.publish_reward_select_times_title));
				intent.putExtra(
						HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_DATA,
						(ArrayList) timesData);
				if (selectTimesData != null) {
					intent.putExtra(
							HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_SELECTED,
							selectTimesData.getID());
				}
				startActivityForResult(intent,
						HeadhunterPublic.REQUEST_SELECT_TIMES);
			}
		});
	}

	private void getGlobalDataFromDB(int type) {
		DBUtils dbUtils = DBUtils.getInstance(getApplicationContext());
		String sql = "select * from " + DBUtils.globaldataTableName + " where "
				+ DBUtils.KEY_GLOBALDATA_TYPE + "=" + type;
		Cursor cursor = dbUtils.rawQuery(sql, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				List<OneLevelChooseData> result = new ArrayList<OneLevelChooseData>();
				cursor.moveToFirst();
				do {
					OneLevelChooseData data = new OneLevelChooseData();
					data.setID(cursor.getString(cursor
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
					data.setName(cursor.getString(cursor
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
					data.setNamePinYin(cursor.getString(cursor
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
					result.add(data);
				} while (cursor.moveToNext());

				switch (type) {
				case DBUtils.GLOBALDATA_TYPE_SALARY:// 年薪
					expectationData = result;
					break;
				case DBUtils.GLOBALDATA_TYPE_REWARDCYCLE:// 悬赏周期
					timesData = result;
					break;
				case DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY:// 行业
					industryData = result;
					break;
				default:
					break;
				}
			}
			cursor.close();
		}
	}

	private void doSubmit() {
		// 友盟统计--发布悬赏--发布按钮
		UmShare.UmStatistics(mContext, "PublishReward_PublishButton");
		if (checkNull()) {
			// showTipsWindows(publishBtn);
			checkAuthStatus();
		}
	}
	//显示发布确认框
	private void showPublishPrompt() {
		boolean isShow = DMSharedPreferencesUtil.getSharePreBoolean(mContext,
				DMSharedPreferencesUtil.DM_APP_DB, DMSharedPreferencesUtil.publishRewardPro, true);
		if (isShow) {
			DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						publish();
					}
					if (which == DialogInterface.BUTTON_NEGATIVE) {

					}
					dialog.dismiss();
				}
			};
			View view = LayoutInflater.from(mContext).inflate(R.layout.dailog_publish_prompt, null);
			TextView tv = (TextView) view.findViewById(R.id.publish_prompt_content);
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.publish_prompt_checkbox);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				checkBox.setTextColor(getResources().getColorStateList(R.color.list_subTitle));
				tv.setTextColor(getResources().getColorStateList(R.color.list_title));
			}
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					DMSharedPreferencesUtil.putSharePreBoolean(mContext,
						DMSharedPreferencesUtil.DM_APP_DB, DMSharedPreferencesUtil.publishRewardPro, !isChecked);
				}
			});
			UIHelper.showCommonDialog(PublishRewardActivity.this,
					view, onClickListener);
		} else {
			publish();
		}
	}

	private void getrewardData() {
		if (!UIHelper.isNetworkConnected((AppContext) getApplicationContext())) {
			return;
		}
		showProgressDialog();
		new Thread(new Runnable() {
			public void run() {
				try {
					Result result = ApiClient.getMoreRewardInfo(
							(AppContext) getApplicationContext(),
							rewardTaskId);
					// String resultJson =
					// ApiClient.getMoreRewardInfo((AppContext)getApplicationContext(),
					// rewardData.getTask_Id());
					if (result.OK()) {
						rewardData = (RewardInfo) ObjectUtils
								.getObjectFromJsonString(result.getJsonStr(),
										RewardInfo.class);
						handler.sendEmptyMessage(GETREWARDDATASUCCESS);
					} else {
						handler.sendMessage(handler.obtainMessage(GETREWARDDATAERROR, result));
					}
				} catch (AppException e) {
					e.printStackTrace();
					handler.sendMessage(handler.obtainMessage(GETREWARDDATAERROR, e));
				}
			}
		}).start();
	}

	private RewardInfo getAddInfo() {
		RewardInfo rewardAddInfo = new RewardInfo();
//		RewardAddInfo rewardAddInfo = new RewardAddInfo();
		if (rewardData != null) {
			rewardAddInfo.setTask_id(rewardData.getTask_id());
		}
		if (selectResumeEntity != null) {
			rewardAddInfo.setResume_id(selectResumeEntity.getResumeId());
		}
//		rewardAddInfo.setTask_title(rewardPost.getText().toString());
		rewardAddInfo.setJflv3_id(selectPostId);
		rewardAddInfo.setTask_category_id(selectIndustryDataArray);
		if (selectExpectationData != null) {
			rewardAddInfo.setTask_expectation_id(selectExpectationData.getID());
		}
		rewardAddInfo.setTask_city(selectCitiesArray);
		rewardAddInfo.setTask_bonus(rewardBonus.getText().toString());
		if (selectTimesData != null) {
			rewardAddInfo.setTask_lifecycle(selectTimesData.getID());
		}
		rewardAddInfo.setTask_type(REWARDAUDITION);
		if (entryRadiobutton.isChecked()) {
			rewardAddInfo.setTask_type(REWARDENTRY + "");
		}
		rewardAddInfo.setTask_memo(rewardIntroduce.getText().toString());
		return rewardAddInfo;
	}
	//检查账户手机认证状态
	private void checkAuthStatus() {
		String authInfo = DMSharedPreferencesUtil.getSharePreStr(getApplicationContext(),
				DMSharedPreferencesUtil.DM_AUTH_INFO,
				((AppContext)getApplicationContext()).getUserId());

		if (!TextUtils.isEmpty(authInfo)) {
			ReqUserInfo userInfo = (ReqUserInfo) ObjectUtils.getObjectFromJsonString(authInfo, ReqUserInfo.class);
			if (ReqUserInfo.PHONE_VERIFIED.equals(userInfo.getPhone_verified())) {
				showPublishPrompt();
				return;
			}
		}
		showProgressDialog();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Result mobileAuthStatus = getMobileAuthStatus();
				if (mobileAuthStatus == null) {
					return;
				}
				if (mobileAuthStatus.OK()) {
					String resultStr = mobileAuthStatus.getJsonStr();
					ReqUserInfo userInfo = (ReqUserInfo) ObjectUtils
							.getObjectFromJsonString(resultStr,
									ReqUserInfo.class);
					handler.sendMessage(handler.obtainMessage(MOBILEAUTHESUCCESS,
							userInfo));
				} else {
					handler.sendMessage(handler.obtainMessage(MOBILEAUTHERROR,
							mobileAuthStatus));
				}
			}
		}).start();
	}
	// 发布
	private void publish() {
		if (!UIHelper.isNetworkConnected((AppContext) getApplicationContext())) {
			return;
		}
		showProgressDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				saveReward();
			}
		}).start();
	}

	//向网络发送请求，保存我的悬赏
	private void saveReward() {
		try {
			Result result = ApiClient.saveReward(
					(AppContext) getApplicationContext(),
					getAddInfo());
			if (result.getErrorCode() == 1) {
				handler.sendMessage(handler.obtainMessage(
						PUBLISHSUCCESS, result));
			} else {
				handler.sendMessage(handler.obtainMessage(
						PUBLISHERROR, result));
			}
		} catch (AppException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(PUBLISHERROR,
					e));
		}
	}
	// 获取手机认证状态
	private Result getMobileAuthStatus() {
		try {
			Result result = ApiClient
					.getUserInfo((AppContext) getApplicationContext());
			return result;
		} catch (AppException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(PUBLISHERROR, e));
			// final AppException ee = e;
			// publishBtn.post(new Runnable() {
			// @Override
			// public void run() {
			// ee.makeToast(getApplicationContext());
			// }
			// });
		}
		return null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isFromMainReward) {
				isFromMainReward = false;
				setResult(HeadhunterPublic.RESULT_FINISH);
			}
			saveCache();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == requestMobileAuth) {
				showPublishPrompt();
			}
		}
		if (HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode) {
			// 选择城市返回
			if (resultCode == HeadhunterPublic.RESULT_CHOOSECITY_OK) {
				selectCities = (List<CityChooseData>) data
						.getSerializableExtra(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_DATA);
				if (selectCities != null) {
					selectCitiesArray = new String[selectCities.size()];
					StringBuffer cities = new StringBuffer();
					for (int i = 0; i < selectCities.size(); i++) {
						if (i == selectCities.size() - 1) {
							cities.append(selectCities.get(i).getName());
						} else {
							cities.append(selectCities.get(i).getName() + ",");
						}
						selectCitiesArray[i] = selectCities.get(i).getID();
					}
					rewardLocation.setText(cities.toString());
				}

			}
			// 选择简历返回
			if (resultCode == HeadhunterPublic.RESUME_SELECT_RESULT_OK) {
				selectResumeEntity = (ResumeSimpleEntity) data
						.getSerializableExtra(HeadhunterPublic.RESUME_SELECT_RESULT);
				if (TextUtils.isEmpty(selectResumeEntity.getName())) {
					rewardResume.setText("姓名");
				} else {
					rewardResume.setText(selectResumeEntity.getName());
				}
				String selfmemo = selectResumeEntity.getSelfmemo();
				if ("null".equals(selfmemo)) {
					rewardIntroduce.setText("");
				} else {
					rewardIntroduce.setText(selfmemo);
				}
			}
		}
		// 选择周期返回
		if (requestCode == HeadhunterPublic.REQUEST_SELECT_TIMES) {
			if (resultCode == HeadhunterPublic.RESULT_RECOMMENDREASON_OK) {
				selectTimesData = (OneLevelChooseData) data
						.getSerializableExtra(HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_BACKDATA);
				rewardTimes.setText(selectTimesData.getName());
			}
		}
		// 选择周期返回
		if (requestCode == HeadhunterPublic.REQUEST_SELECT_EXPECTATION) {
			if (resultCode == HeadhunterPublic.RESULT_RECOMMENDREASON_OK) {
				selectExpectationData = (OneLevelChooseData) data
						.getSerializableExtra(HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_BACKDATA);
				rewardExpectation.setText(selectExpectationData.getName());
			}
		}
		// 选择行业返回
		if (requestCode == HeadhunterPublic.REQUEST_SELECT_INDUSTRY) {
			if (resultCode == HeadhunterPublic.RESULT_INDUSTRYCITY_OK) {
				selectIndustryData = (List<OneLevelChooseData>) data
						.getSerializableExtra(HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_DATA);
				StringBuffer cities = new StringBuffer();
				if (selectIndustryData != null) {
					selectIndustryDataArray = new String[selectIndustryData
							.size()];
					for (int i = 0; i < selectIndustryData.size(); i++) {
						if (i == selectIndustryData.size() - 1) {
							cities.append(selectIndustryData.get(i).getName());
						} else {
							cities.append(selectIndustryData.get(i).getName()
									+ ",");
						}
						selectIndustryDataArray[i] = selectIndustryData.get(i)
								.getID();
					}
					rewardIndustry.setText(cities.toString());
				}

			}
		}
		if (HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode) {
			if (HeadhunterPublic.RESULT_JOBFUNCTIONS_OK == resultCode) {
				OneLevelChooseData choosedata = null;
				choosedata = (OneLevelChooseData) data
						.getSerializableExtra(HeadhunterPublic.JOBFUNCTIONCHOOSE_DATA);
				if (choosedata != null) {
					String str = choosedata.getName();
					selectPostId = choosedata.getID();
					rewardPost.setText(choosedata.getName());
				}
			}
		}
	}

	// 检查非空
	private boolean checkNull() {
		if (typeRadioGroup.getCheckedRadioButtonId() == -1) {
			UIHelper.ToastMessage(getApplicationContext(),
					R.string.publish_reward_type_isnull);
			return false;
		}
		if (TextUtils.isEmpty(rewardBonus.getText())) {
			UIHelper.ToastMessage(getApplicationContext(),
					R.string.publish_reward_bouns_isnull);
			rewardBonus.requestFocus();
			return false;
		} else if (!checkBouns()) {
			rewardBonus.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(rewardTimes.getText())) {
			UIHelper.ToastMessage(getApplicationContext(),
					R.string.publish_reward_times_isnull);
			return false;
		}
		if (TextUtils.isEmpty(rewardExpectation.getText())) {
			UIHelper.ToastMessage(getApplicationContext(),
					R.string.publish_reward_expectation_isnull);
			return false;
		}
		if (TextUtils.isEmpty(rewardIndustry.getText())) {
			UIHelper.ToastMessage(getApplicationContext(),
					R.string.publish_reward_industry_isnull);
			return false;
		}
		if (TextUtils.isEmpty(rewardPost.getText())) {
			UIHelper.ToastMessage(getApplicationContext(),
					R.string.publish_reward_post_isnull);
			rewardPost.requestFocus();
			return false;
		}
		if (TextUtils.isEmpty(rewardLocation.getText())) {
			UIHelper.ToastMessage(getApplicationContext(),
					R.string.publish_reward_location_isnull);
			return false;
		}
		// if (TextUtils.isEmpty(rewardResume.getText())) {
		if (selectResumeEntity == null) {
			UIHelper.ToastMessage(getApplicationContext(),
					R.string.publish_reward_resume_isnull);
			return false;
		}
		if (!agreementCheckBox.isChecked()) {
			UIHelper.ToastMessage(getApplicationContext(),
					R.string.publish_reward_agreement_isnull);
			return false;
		}
		// if (TextUtils.isEmpty(rewardIntroduce.getText())) {
		// UIHelper.ToastMessage(getApplicationContext(),
		// R.string.publish_reward_introduce_isnull);
		// return false;
		// }
		return true;
	}

	// 初始化我的推荐
	private void initIntroduceView(String taskMemo) {
		rewardIntroduce.setText(taskMemo);
	}

	int count = 0;

	// 初始化简历
	private void initResumeView(String resumeId) {
		if (selectResumeEntity == null) {
			try {
				selectResumeEntity = ((AppContext) getApplicationContext())
						.getResumeInfoByIdFromBD(resumeId);
				if (selectResumeEntity == null) {
					if (count == 0) {
						getResumeList();
					}
				} else {
					if (TextUtils.isEmpty(selectResumeEntity.getName())) {
						rewardResume.setText("姓名");
					} else {
						rewardResume.setText(selectResumeEntity.getName());
					}
				}
			} catch (AppException e) {
				e.printStackTrace();
			}
		} else {
			MyLogger.i(TAG, "selectResumeEntity.getName()##"
					+ selectResumeEntity.getName());
			if (TextUtils.isEmpty(selectResumeEntity.getName())) {
				rewardResume.setText("姓名");
			} else {
				rewardResume.setText(selectResumeEntity.getName());
			}
		}
	}

	// 从网络获取所有的简历列表
	private void getResumeList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				count = 1;
				try {
					ResumeSimpleEntityList resumeList = ((AppContext) getApplication())
							.getResumeSimpleEntityListFromNet(
									(AppContext) getApplication(), true);// true表示更新数据库
					handler.sendEmptyMessage(ASYNCRESUMEDATASUCCESS);
				} catch (AppException e) {
					e.printStackTrace();
					final AppException ee = e;
					rewardResume.post(new Runnable() {
						@Override
						public void run() {
							ee.makeToast(getApplicationContext());
						}
					});
				}
			}
		}).start();
	}

	// 初始化悬赏方式
	private void initTypeView(String type) {
		if (REWARDAUDITION.equals(type)) {
			auditionRadiobutton.setChecked(true);
		}
		if (REWARDENTRY.equals(type)) {
			entryRadiobutton.setChecked(true);
		}
	}

	// 初始化悬赏金额
	private void initBonusView(String rewardBonusStr) {
		rewardBonus.setText(rewardBonusStr);
	}

	// 初始化悬赏周期
	private void initTimesView(String taskLifecycle) {
		if (!TextUtils.isEmpty(taskLifecycle)) {
			GlobalDataTable lifecycle = GlobalDataTable.getTypeDataById(
					(AppContext) getApplicationContext(),
					DBUtils.GLOBALDATA_TYPE_REWARDCYCLE, taskLifecycle);
			if (lifecycle != null) {
				rewardTimes.setText(lifecycle.getName());
				if (rewardData != null) {
					selectTimesData = new OneLevelChooseData();
					selectTimesData.setID(rewardData.getTask_lifecycle());
				}
			}
		}
	}

	// 初始化悬赏年薪
	private void initExpectationView(String taskExpectionId) {
		if (!TextUtils.isEmpty(taskExpectionId)) {
			GlobalDataTable expectation = GlobalDataTable.getTypeDataById(
					(AppContext) getApplicationContext(),
					DBUtils.GLOBALDATA_TYPE_SALARY, taskExpectionId);
			rewardExpectation.setText(expectation.getName());
			if (rewardData != null) {
				selectExpectationData = new OneLevelChooseData();
				selectExpectationData
						.setID(rewardData.getTask_expectation_id());
			}
		}
	}

	// 初始化悬赏行业
	private void initIndustryView() {
		String industryStr = "";
		if (selectIndustryDataArray != null) {
			if (selectIndustryData == null) {
				selectIndustryData = new ArrayList<OneLevelChooseData>();
			}
			for (int i = 0; i < selectIndustryDataArray.length; i++) {
				GlobalDataTable industry = GlobalDataTable.getTypeDataById(
						(AppContext) getApplicationContext(),
						DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY,
						selectIndustryDataArray[i]);
				OneLevelChooseData chooseData = new OneLevelChooseData();
				chooseData.setID(industry.getID());
				chooseData.setParentID(industry.getParentID());
				selectIndustryData.add(chooseData);
				if (i < selectIndustryDataArray.length - 1) {
					industryStr = industryStr + industry.getName() + ",";
				} else {
					industryStr = industryStr + industry.getName();
				}
			}
			rewardIndustry.setText(industryStr);
		}
	}

	// 初始化悬赏职位
	private void initPostView(String jflv3_id) {
		this.selectPostId = jflv3_id;
		if (!TextUtils.isEmpty(selectPostId)) {
			//职位名称是3级数据
			GlobalDataTable table = GlobalDataTable.getGlobalDataTable(
					(AppContext) getApplicationContext(), DBUtils.GLOBALDATA_TYPE_JOBFUNCTION, 3,
					selectPostId);
			if (table != null) {
				rewardPost.setText(table.getName());
			}
		}
	}

	// 初始化悬赏地点
	private void initCitiesView() {
		if (selectCitiesArray != null) {
			if (selectCities == null) {
				selectCities = new ArrayList<CityChooseData>();
			}
			String str = "";
			for (int i = 0; i < selectCitiesArray.length; i++) {
				GlobalDataTable city = GlobalDataTable.getTypeDataById(
						(AppContext) getApplicationContext(),
						DBUtils.GLOBALDATA_TYPE_CITY, selectCitiesArray[i]);
				CityChooseData chooseData = new CityChooseData();
				chooseData.setID(city.getID());
				selectCities.add(chooseData);
				if (i == selectCitiesArray.length - 1) {
					str = str + city.getName();
				} else {
					str = str + city.getName() + ",";
				}
			}
			rewardLocation.setText(str);
		}
	}

	private boolean checkBouns() {
		String bonusStr = rewardBonus.getText().toString();
		if (!TextUtils.isEmpty(bonusStr)) {
			int bonus = Integer.parseInt(bonusStr);
			if (bonus < 100) {
				UIHelper.ToastMessage(getApplicationContext(),
						R.string.bouns_tips_low);
				return false;
			} else if ((auditionRadiobutton.isChecked() && bonus > 5000)
					|| (entryRadiobutton.isChecked() && bonus > 20000)) {
				AlertDialog.Builder builder = null;
				DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							Intent intent = new Intent(
									"android.intent.action.CALL",
									Uri.parse("tel:021-60727869"));
							startActivity(intent);
						}
						dialog.dismiss();
					}
				};
				builder = UIHelper.showCommonDialog(PublishRewardActivity.this,
						R.string.bouns_tips, R.string.sure_phone,
						R.string.cancle, onClickListener);
				return false;
			}
		}

		return true;
	}

	private void showTipsWindows(View view) {
		UIHelper.showAgreementDialog(PublishRewardActivity.this,
				R.string.publish_reward_agreement_content_title, R.string.publish_reward_agreement_content,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
						}
						dialog.dismiss();
					}
				});
		// AgreementDialog dialog = new
		// AgreementDialog(PublishRewardActivity.this);
		// dialog.initView(R.string.publish_reward_agreement_content);
		// dialog.setListener(new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// if (which == DialogInterface.BUTTON_POSITIVE) {
		// }
		// dialog.dismiss();
		// }
		// });
		// dialog.show();
		// if (tipsWindows == null) {
		//
		// tipsWindows = new PromptPopupWindow(getApplicationContext(), true);
		// View.OnClickListener onclickListener = new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// switch (v.getId()) {
		// case R.id.prompt_title_cancel:
		// hideTipsWindows();
		// break;
		// case R.id.prompt_title_ok:
		// hideTipsWindows();
		// break;
		// default:
		// break;
		// }
		// }
		// };
		// tipsWindows.setListener(onclickListener);
		// tipsWindows.initView(R.string.publish_reward_agreement_content_title,
		// R.string.publish_reward_agreement_content, false);
		// }
		// tipsWindows.show(view);
	}

	// private void hideTipsWindows() {
	// if (tipsWindows != null) {
	// tipsWindows.dismiss();
	// }
	// }
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("rewardCache", getAddInfo());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);
	}
	
	/*************************handler*********************/
	public final static int PUBLISHSUCCESS = 1;
	public final static int PUBLISHERROR = 6;
	private final static int MOBILEAUTHESUCCESS = 9;
	private final static int MOBILEAUTHERROR = 8;
	public final static int GETREWARDDATASUCCESS = 7;
	public final static int GETREWARDDATAERROR = 4;
	public final static int ASYNCRESUMEDATASUCCESS = 5;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MOBILEAUTHESUCCESS:
				{
					dismissProgressDialog();
					Object obj = msg.obj;
					if (obj != null) {
						if (obj instanceof ReqUserInfo) {
							ReqUserInfo userInfo = (ReqUserInfo)obj;
							if (ReqUserInfo.PHONE_VERIFIED.equals(userInfo.getPhone_verified())) { //如果是认证过的 就存下来并发布
								showPublishPrompt();
								DMSharedPreferencesUtil.putSharePre(getApplicationContext(),
										DMSharedPreferencesUtil.DM_AUTH_INFO,
										((AppContext)getApplicationContext()).getUserId(),
										ObjectUtils.getJsonStringFromObject(userInfo));
							} else {
								showMobileAuthDialog();
							}
						}
					}
				}
					break;
				case MOBILEAUTHERROR:
				 {
						dismissProgressDialog();
						Object obj = msg.obj;
						if (obj != null) {
							if (obj instanceof Result) {
								Result result = (Result) obj;
								result.handleErrcode(PublishRewardActivity.this);
							} else if (obj instanceof AppException) {
								AppException exception = (AppException) obj;
								exception.makeToast(getApplicationContext());
							}
						}
					}
				break;
			case PUBLISHSUCCESS:
			{
				dismissProgressDialog();

				Result result = (Result)msg.obj;
				m_strTaskId = result.getJsonStr();
				
				Util.toast(
						getApplicationContext(),
						getResources().getString(
								R.string.publish_reward_publish_success), true);
				if(null != m_strTaskId && !m_strTaskId.isEmpty()){
					// 询问是否马上付款
					showPayAlertDialog();
				}else{
					finishPulishReward();
				}
			}
				break;
			case PUBLISHERROR:
			case GETREWARDDATAERROR: {
				dismissProgressDialog();
				Object obj = msg.obj;
				if (obj != null) {
					if (obj instanceof Result) {
						Result result = (Result) msg.obj;
						result.handleErrcode(PublishRewardActivity.this);
					} else if (obj instanceof AppException) {
						AppException exception = (AppException) msg.obj;
						exception.makeToast(getApplicationContext());
					}
				}
			}
				break;
			case GETREWARDDATASUCCESS:
				dismissProgressDialog();
				initViewData();
				break;
			case ASYNCRESUMEDATASUCCESS:
				initResumeView(rewardData.getResume_id());
				break;
			case HeadhunterPublic.MSG_PAYMENTORDERNO_SUCCESS:	// 获取支付订单号成功
				{
					dismissProgressDialog();
					
					Result result = (Result)msg.obj;
					m_strPaymentOrderNo = result.getJsonStr();
					
					if(null != m_strPaymentOrderNo && !m_strPaymentOrderNo.isEmpty()){
						// 付款
						pay();
					}else{
						UIHelper.ToastMessage(getApplicationContext(),
								R.string.publish_reward_getpaymentorderno_fail);
						finishPulishReward();
					}
				}
				break;
			case HeadhunterPublic.MSG_PAYMENTORDERNO_FAIL:		// 获取支付订单号失败
			case HeadhunterPublic.MSG_PAYMENTORDERNO_ABNORMAL:	// 获取支付订单号异常
				{
					dismissProgressDialog();
					UIHelper.ToastMessage(getApplicationContext(),
							R.string.publish_reward_getpaymentorderno_fail);
					finishPulishReward();
				}
				break;
			default:
				break;
			}
		}
	};
	
	private Handler m_payhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case HeadhunterPublic.MSG_PAY_SUCCESS:
			case 1:
				{
					UIHelper.ToastMessage(PublishRewardActivity.this, R.string.msg_pay_success);
					finishPulishReward();
				}
				break;
			case HeadhunterPublic.MSG_PAY_ABNORAL:
				{
					UIHelper.ToastMessage(PublishRewardActivity.this, R.string.msg_pay_abnormal);
					finishPulishReward();
				}
				break;
			
			case HeadhunterPublic.MSG_PAY_FAIL:
			default:
				{
					UIHelper.ToastMessage(PublishRewardActivity.this, R.string.msg_pay_fail);
					finishPulishReward();
				}
				break;
			}
		}
	};
	
	/**
	 * 付款询问对话框
	 */
	private void showPayAlertDialog(){
		// 获取金额
		String strPrice = rewardBonus.getText().toString();
		double nPrice = Double.valueOf(strPrice);
		// 手续费
		double nFee = nPrice * HeadhunterPublic.PAY_RATE;
		String strFee = String.valueOf(nFee);
		// 总计
		double nTotal = nPrice + nFee;
		m_strTotal = String.valueOf(nTotal);

		// 提示信息
		String strPayMessage = String.format(getString(R.string.str_play_prompt),
				strPrice, strFee, m_strTotal);
		
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.publish_reward_payprompt_title));
		//dl.setMessage(getString(R.string.publish_reward_payprompt));
		dl.setMessage(strPayMessage);
		dl.setPositiveButton(getString(R.string.publish_reward_pay),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						// 获取支付单号
						runGetPaymentOrderNo();
					}
			
			});
		dl.setNegativeButton(getString(R.string.publish_reward_nopay),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						finishPulishReward();
					}
			
			});
		dl.show();
	}
	
	/**
	 * 发布成功后, 关闭界面
	 */
	private void finishPulishReward(){
		if (isFromMainReward) {
			isFromMainReward = false;
			Intent intent = new Intent(PublishRewardActivity.this,
					MyRewardListActivity.class);
			startActivity(intent);
		}
		if (rewardData == null || TextUtils.isEmpty(rewardData.getTask_id())) {//如果没有id证明是新增的
			MyLogger.i(TAG, "clear");
			DMSharedPreferencesUtil.putByUserId(mContext, DMSharedPreferencesUtil.DM_MYREWARD_INFO,
					DMSharedPreferencesUtil.myRewardInfo,
					"");
		}

		setResult(RESULT_OK);
		finish();		
	}
	
	/**
	 * 付款
	 */
	private void pay(){
		try {
			// 获取金额
			if(null == m_strTotal || m_strTotal.isEmpty()){
				// 支付失败
				UIHelper.ToastMessage(PublishRewardActivity.this, R.string.msg_pay_fail);
				
				return;
			}
			
			// 测试用
			if(ConfigOptions.debug){
				m_strTotal = "0.01";
			}
			
//			String strOutTradeNo = UIHelper.getOutTradeNo();
			String strSubject = getSubject();
			String strBody = getSubject();
			
			String info = UIHelper.getNewOrderInfo(m_strPaymentOrderNo, strSubject, strBody, m_strTotal);
			String sign = Rsa.sign(info, HeadhunterPublic.ALIPAY_PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + UIHelper.getSignType();
			
			final String orderInfo = info;
	    	new Thread(){
	        	public void run(){
	        		AliPay alipay = new AliPay(PublishRewardActivity.this, m_payhandler);
	        		
					//设置为沙箱模式，不设置默认为线上环境
					//alipay.setSandBox(true);

	        		// 
					String result = alipay.pay(orderInfo);
					// 获取支付返回状态
					String resultStatus = UIHelper.getAlipayResultStatus(result);
					
					if(null != resultStatus && resultStatus.equals("9000")){
						// 支付成功
						m_payhandler.sendMessage(m_payhandler.obtainMessage(
								HeadhunterPublic.MSG_PAY_SUCCESS));
					}else{
						// 支付失败
						m_payhandler.sendMessage(m_payhandler.obtainMessage(
								HeadhunterPublic.MSG_PAY_FAIL));
					}
	        	}
	        }.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			m_payhandler.sendMessage(m_payhandler.obtainMessage(
					HeadhunterPublic.MSG_PAY_ABNORAL, ex));
		}
	}
	
	/**
	 * @return
	 */
	private String getSubject(){
		String strTemp = "";
		
		// 默认悬赏方式为面试
		strTemp = getString(R.string.publish_reward_type_interview);
		
		if(null != entryRadiobutton){
			if(entryRadiobutton.isChecked()){
				// 悬赏方式为入职
				strTemp = getString(R.string.publish_reward_type_entry);
			}
		}
		
		strTemp = getString(R.string.app_name) + "-" + strTemp;
		
		return strTemp;
	}
	
	/**
	 * 获取支付订单号
	 */
	private void runGetPaymentOrderNo(){

		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			finishPulishReward();
			return;
		}
		
		showProgressDialog();
		
		new Thread() {
			public void run() {
				UIHelper.getPaymentOrderNo(mContext, handler,
						"task", m_strTaskId);
			}
		}.start();
	}
}

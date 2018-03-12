package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.PersonalInfo;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.app.view.ExplainInfoMiddlePop;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Fragment_my extends BaseFragment {

	private static final int HANDCODE_LOADRESUME_OK = 1;
	private static final int HANDCODE_LOADRESUME_FAIL = 2;
	private static final int PERSIONAL_INIT_ERROR = 3;

	private Context m_Context;
	private AppContext mappcontext;

	private ImageView m_btnSet;
	private ImageView completeImgview;
	private ImageView photoImgview;
	private ImageView defaultresumeIv;
	private ImageView creditIv;

	private ImageView account_cplt_iv;
	private ImageView account_photo_iv;

	private Button loginbtn;

	private TextView nameText;
	private TextView hopeJobText;
	private TextView changeTimeText;

	private RelativeLayout m_rReward;
	private TextView rewardText;
	private TextView rewardCount;
	private RelativeLayout m_rlCollection;
	private TextView collectionText;
	private TextView collectionCount;
	private RelativeLayout m_rlRecord;
	private TextView recordText;
	private TextView recordCount;
	private RelativeLayout m_rAccount;
	private TextView accountText;
	private TextView accountCount;
	private RelativeLayout settingItem; // 设置
	private RelativeLayout mobileAccount; // 手机认证
	private TextView mobileStatus;
	private RelativeLayout emailAccount; // 邮箱认证
	private TextView emailStatus;
	private RelativeLayout m_resumesRecord;// 我的简历库
	private TextView resumeCount;
	private TextView resumeText;
	
	private RelativeLayout m_rIntegral;		// 积分管理
	private TextView integralText;			
	private TextView integralCount;			// 积分

	private ResumeSimpleEntity mentity;
	private BitmapManager bmpManager;
	private PersonalInfo mpersonalInfo;

	private int recordRequest = 1;
	private int collectionRequest = 2;
	private int rewardRequest = 3;
	private int myAccountRequest = 4;
	private int mobileAccountRequest = 5;
	private int mailAccountRequest = 6;
	private int loginRequest = 7;
	private int myIntegralRequest = 8;

	public static BaseFragment newInstance(int index) {
		BaseFragment fragment = new Fragment_my();
		Bundle args = new Bundle();
		args.putInt("index", index);
		fragment.setArguments(args);
		fragment.setIndex(index);
		return fragment;
	}

	private View layoutView;
	private FragmentTabHost mTabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_Context = this.getActivity();
		mappcontext = (AppContext) this.getActivity().getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (layoutView != null) {
			ViewGroup p = (ViewGroup) layoutView.getParent();
			if (p != null) {
				p.removeAllViewsInLayout();
			}
			// refreshResumeView();
			refreshUI();
		} else {
			layoutView = inflater.inflate(R.layout.fragment_my, container,
					false);
			bmpManager = new BitmapManager(BitmapFactory.decodeResource(
					getResources(), R.drawable.person_photo_normal_big));

			account_cplt_iv = (ImageView) layoutView
					.findViewById(R.id.account_complete_imgview);
			account_photo_iv = (ImageView) layoutView
					.findViewById(R.id.account_headphoto_imgview);
			completeImgview = (ImageView) layoutView
					.findViewById(R.id.complete_imgview);
			Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(
					this.getActivity(), 0,
					Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
					Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
					Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
					Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
			completeImgview.setImageBitmap(completebmp);
			account_cplt_iv.setImageBitmap(completebmp);

			photoImgview = (ImageView) layoutView
					.findViewById(R.id.headphoto_imgview);
			Bitmap photobmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.person_photo_normal_big);
			photobmp = ImageUtils.createRoundHeadPhoto(this.getActivity(),
					photobmp);
			photoImgview.setImageBitmap(photobmp);
			account_photo_iv.setImageBitmap(photobmp);

			ViewGroup myresume = (ViewGroup) layoutView
					.findViewById(R.id.resume_photo_item);
			myresume.setOnClickListener(mlistener);
			// 默认简历
			layoutView.findViewById(R.id.default_imgview).setOnClickListener(
					mlistener);
			// 信用评级
			layoutView.findViewById(R.id.credit_imgview).setOnClickListener(
					mlistener);

			nameText = (TextView) layoutView.findViewById(R.id.resume_name_tv);
			hopeJobText = (TextView) layoutView
					.findViewById(R.id.resume_job_tv);
			changeTimeText = (TextView) layoutView
					.findViewById(R.id.resume_time_tv);
			changeTimeText.setVisibility(View.GONE);

			defaultresumeIv = (ImageView) layoutView
					.findViewById(R.id.default_imgview);
			creditIv = (ImageView) layoutView.findViewById(R.id.credit_imgview);

			// 我的悬赏
			m_rReward = (RelativeLayout) layoutView
					.findViewById(R.id.myreward_item);
			rewardText = (TextView) layoutView
					.findViewById(R.id.myreward_item_text);
			rewardCount = (TextView) layoutView
					.findViewById(R.id.myreward_item_count);
			m_rReward.setOnClickListener(mlistener);
			m_rReward.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					UIHelper.showPopTips(m_Context, rewardText,
							"您发布的悬赏都在“我的悬赏”列表内，可随时查看进度");
					return false;
				}
			});
			// 我的收藏
			m_rlCollection = (RelativeLayout) layoutView
					.findViewById(R.id.mycollection_item);
			collectionText = (TextView) layoutView
					.findViewById(R.id.mycollection_item_text);
			collectionCount = (TextView) layoutView
					.findViewById(R.id.mycollection_item_count);
			m_rlCollection.setOnClickListener(mlistener);
			m_rlCollection
					.setOnLongClickListener(new View.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							UIHelper.showPopTips(m_Context, collectionText,
									"您收藏的各种任务，发布悬赏，职位，活动信息都在这里哦！");
							return false;
						}
					});

			// 任务记录(我的纪录)
			m_rlRecord = (RelativeLayout) layoutView
					.findViewById(R.id.myrecord_item);
			recordText = (TextView) layoutView
					.findViewById(R.id.myrecord_item_text);
			recordCount = (TextView) layoutView
					.findViewById(R.id.myrecord_item_count);
			m_rlRecord.setOnClickListener(mlistener);
			m_rlRecord.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					UIHelper.showPopTips(m_Context, recordText,
							"可查看全部应聘或推荐记录，全部悬赏记录，实时跟踪赏金状态");
					return false;
				}
			});

			// 我的账号
			m_rAccount = (RelativeLayout) layoutView
					.findViewById(R.id.myaccount_item);
			accountText = (TextView) layoutView
					.findViewById(R.id.myaccount_item_text);
			accountCount = (TextView) layoutView
					.findViewById(R.id.myaccount_item_count);
			m_rAccount.setOnClickListener(mlistener);
			m_rAccount.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					UIHelper.showPopTips(m_Context, accountText,
							"您的账号管理、账号绑定、赏金结算、账户余额都在这里查看");
					return false;
				}
			});
			
			// 积分管理
			m_rIntegral = (RelativeLayout) layoutView
					.findViewById(R.id.myintegral_item);
			accountText = (TextView) layoutView
					.findViewById(R.id.myintegral_item_text);
			accountCount = (TextView) layoutView
					.findViewById(R.id.myintegral_item_count);
			m_rIntegral.setOnClickListener(mlistener);
			m_rIntegral.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					UIHelper.showPopTips(m_Context, accountText,
							"您的积分管理、兑换记录、积分充值，积分规则等请在这里查询");
					return false;
				}
			});

			// 设置
			settingItem = (RelativeLayout) layoutView
					.findViewById(R.id.setting_item);
			settingItem.setOnClickListener(mlistener);

			mobileAccount = (RelativeLayout) layoutView
					.findViewById(R.id.mailaccount_item);
			mobileStatus = (TextView) layoutView
					.findViewById(R.id.phoneaccount_item_status);
			emailAccount = (RelativeLayout) layoutView
					.findViewById(R.id.phoneaccount_item);
			emailStatus = (TextView) layoutView
					.findViewById(R.id.mailaccount_item_status);
			mobileAccount.setOnClickListener(mlistener);
			emailAccount.setOnClickListener(mlistener);
			// refreshResumeView();
			// 我的简历库
			m_resumesRecord = (RelativeLayout) layoutView
					.findViewById(R.id.myresume_item);
			m_resumesRecord.setOnClickListener(mlistener);
			resumeText = (TextView) layoutView
					.findViewById(R.id.myresume_item_text);
			resumeCount = (TextView) layoutView
					.findViewById(R.id.myresume_item_count);
			m_resumesRecord
					.setOnLongClickListener(new View.OnLongClickListener() {

						@Override
						public boolean onLongClick(View v) {
							UIHelper.showPopTips(m_Context, resumeText,
									"您新增的简历都在\"我的简历库\"列表内，可随时查看");
							return true;
						}
					});

			// title栏设置按钮
			m_btnSet = (ImageView) layoutView
					.findViewById(R.id.fragment_my_set);
			m_btnSet.setOnClickListener(mlistener);
			loginbtn = (Button) layoutView
					.findViewById(R.id.fragment_my_topright_btn);
			loginbtn.setOnClickListener(mlistener);
		}
		refreshPersonalInfo();
		return layoutView;
	}

	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDCODE_LOADRESUME_OK: {
				ResumeSimpleEntity entity = (ResumeSimpleEntity) msg.obj;
				// if (entity != null)
				displayResume(entity);
			}
				break;
			case HANDCODE_LOADRESUME_FAIL: {
				displayResume(null);
			}
				break;
			case PERSIONAL_INIT_ERROR:
				Object obj = msg.obj;
				if (obj instanceof AppException) {
					AppException appException = (AppException) obj;
					appException.makeToast(mappcontext);
				}
				break;
			}
		}

	};

	private void refreshPersonalInfo() {
		if (mappcontext.isLogin()) {
			new PersonalInfoAsyncTask().execute();
		} else {
			clearPersonalInfoView();
		}
	}

	private void startMyAccountActivity() {
		Intent intent = new Intent(getActivity(), MyAccountActivity.class);
		startActivity(intent);
	}

	private void startMyCollectionActivity() {
		Intent intent = new Intent(getActivity(), MyCollectionActivity.class);
		startActivity(intent);
	}

	private void startMyRecordActivity() {
		Intent intent = new Intent(getActivity(), MyRecordActivity.class);
		startActivity(intent);
	}

	private void startMyRewardListActivity() {
		Intent intent = new Intent(getActivity(), MyRewardListActivity.class);
		startActivity(intent);
	}

	private void startMailAuthActivity() {
		Intent intent = new Intent(getActivity(), MailAuthActivity.class);
		startActivity(intent);
	}

	private void startMobileAuthActivity() {
		Intent intent = new Intent(getActivity(), MobileAuthActivity.class);
		startActivity(intent);
	}

	/**
	 * 进入积分管理界面
	 */
	private void startMyIntegralActivity(){
		Intent intent = new Intent(getActivity(), MyIntegralActivity.class);
		
		startActivity(intent);
	}
	
	private OnClickListener mlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.resume_photo_item:
				// 友盟统计--我--简历库按钮
				UmShare.UmStatistics(m_Context, "My_ResumePhotoButton");

				// UIHelper.showResumeListActivity(mcontext);
				UIHelper.showResumeListActivityBehindCheck(getActivity());
				break;
			case R.id.myresume_item: { // 友盟统计--我--简历库按钮
				UmShare.UmStatistics(m_Context, "My_ResumePhotoButton");

				// UIHelper.showResumeListActivity(mcontext);
				UIHelper.showResumeListActivityBehindCheck(getActivity());
			}
				break;
			case R.id.default_imgview: {
				ExplainInfoMiddlePop pop = new ExplainInfoMiddlePop(m_Context);
				pop.setExplainInfoText("默认简历");
				int btn_width = v.getWidth() / 2;
				DisplayMetrics dm = AppContext
						.getPhoneDisplayMetrics(mappcontext);
				int xoffset = (int) ((-50) * dm.density + btn_width);
				int yoffset = (int) (3 * dm.density);
				pop.showAsDropDown(v, xoffset, 0);
				pop.update();
			}
				break;

			case R.id.credit_imgview: {
				ExplainInfoMiddlePop pop = new ExplainInfoMiddlePop(m_Context);
				pop.setExplainInfoText("信用等级");
				int btn_width = v.getWidth() / 2;
				DisplayMetrics dm = AppContext
						.getPhoneDisplayMetrics(mappcontext);
				int xoffset = (int) ((-50) * dm.density + btn_width);
				int yoffset = (int) (3 * dm.density);
				pop.showAsDropDown(v, xoffset, 0);
				pop.update();
			}
				break;
			case R.id.myaccount_item: {// 我的账号
				// 友盟统计--我--我的账号按钮
				UmShare.UmStatistics(m_Context, "My_MyAccountButton");
				if (checkIsLogin(myAccountRequest)) {
					startMyAccountActivity();
				}
			}
				break;
			case R.id.mycollection_item: {// 我的收藏
				// 友盟统计--我--我的收藏按钮
				UmShare.UmStatistics(m_Context, "My_MyCollectionButton");
				if (checkIsLogin(collectionRequest)) {
					startMyCollectionActivity();
				}
			}
				break;
			case R.id.myrecord_item: {// 我的纪录
				// 友盟统计--我--我的纪录按钮
				UmShare.UmStatistics(m_Context, "My_MyRecordButton");
				if (checkIsLogin(recordRequest)) {
					startMyRecordActivity();
				}
			}
				break;
			case R.id.myreward_item: {// 我的悬赏
				// 友盟统计--我--我的悬赏按钮
				UmShare.UmStatistics(m_Context, "My_MyRewardButton");
				if (checkIsLogin(rewardRequest)) {
					startMyRewardListActivity();
				}
			}
				break;
			case R.id.mailaccount_item: {
				// 友盟统计--我--邮箱绑定按钮
				UmShare.UmStatistics(m_Context, "My_MailAccountButton");
				if (checkIsLogin(mailAccountRequest)) {
					startMailAuthActivity();
				}
				break;
			}
			case R.id.phoneaccount_item: {
				// 友盟统计--我--手机绑定按钮
				UmShare.UmStatistics(m_Context, "My_PhoneAccountButton");
				if (checkIsLogin(mobileAccountRequest)) {
					startMobileAuthActivity();
				}
				break;
			}
			case R.id.setting_item:

				// 友盟统计--我--设置按钮
				UmShare.UmStatistics(m_Context, "My_SettingButton");
				Intent intent = new Intent(getActivity(), SettingActivity.class);
				startActivity(intent);
				break;
			case R.id.fragment_my_set: {

				// TODO Auto-generated method stub


	
//				Intent startintent = new Intent(getActivity(), ResumeEditMainActivity.class);			
//				getActivity().startActivityForResult(startintent,
//						ActivityRequestCode.RESULT_ACTIVITY_NEWRESUME);
				if (mappcontext.isLoginAndToLogin(getActivity())) {
	
					
					if (mpersonalInfo == null) {
						UIHelper.showMyResumeEditActivityForResult(
								getActivity(), 0);
					} else if (mpersonalInfo.getResume_num() < ResumeListActivity.RESUMES_NUMBER_MAX) {
						UIHelper.showMyResumeEditActivityForResult(
								getActivity(), 0);
					} else {
						UIHelper.ToastMessage(getActivity(), String.format(
								"您最多可新建%d份简历",
								ResumeListActivity.RESUMES_NUMBER_MAX));
					}
				}

			}
				break;
			case R.id.fragment_my_topright_btn: {

				if (!mappcontext.isLogin()) {
					// 友盟统计--设置--登录按钮
					UmShare.UmStatistics(m_Context, "Set_LoginButton");
					// UIHelper.startLoginActivity(getActivity(), loginRequest);
					Intent intent1 = new Intent(getActivity(),
							UserLoginActivity.class);
					startActivityForResult(intent1, loginRequest);
				} else {
					// 友盟统计--设置--退出账号按钮
					UmShare.UmStatistics(m_Context, "Set_ExitButton");

					AlertDialog.Builder dialog = null;
					DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == dialog.BUTTON_POSITIVE) {
								(mappcontext).Logout();
								// UIHelper.showLoginActivity(getActivity());
								UIHelper.ToastMessage(m_Context,
										R.string.msg_logout_success);
								refreshUI();
								clearPersonalInfoView();
							}
							if (which == dialog.BUTTON_NEGATIVE) {

							}
							dialog.dismiss();
						}
					};
					dialog = UIHelper.showCommonDialog(getActivity(),
							R.string.exit_account_title, onClickListener);
				}

			}
				break;
			case R.id.myintegral_item: {// 积分管理
				// 友盟统计--我--积分管理按钮
				UmShare.UmStatistics(m_Context, "My_MyIntegralButton");
				if (checkIsLogin(myIntegralRequest)) {
					startMyIntegralActivity();
				}
			}
				break;
			}
		}
	};

	private void initLoginStatus() {
		if (!(mappcontext).isLogin()) {
			loginbtn.setText(R.string.frame_btn_login);
		} else {
			loginbtn.setText(R.string.frame_btn_logout);
		}
	}

	// 刷新简历信息
	private void refreshResumeView() {
		if (true)
			return;
		if (!mappcontext.isLogin()) {
			nameText.setText(R.string.resume_unlogin);
			hopeJobText.setText("");

		} else {
			nameText.setText(R.string.str_quickrecommend_name);
			hopeJobText.setText(R.string.resume_hopetitle);
		}
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				ResumeSimpleEntity defaultResumeEntity = null;
				try {
					defaultResumeEntity = (mappcontext)
							.getDefaultResumeSimpleInfoFromDb(mappcontext);

				} catch (AppException e) {
					e.printStackTrace();
				}
				if (defaultResumeEntity != null) {
					Message msg = new Message();
					msg.what = HANDCODE_LOADRESUME_OK;
					msg.obj = defaultResumeEntity;
					mhandler.sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.what = HANDCODE_LOADRESUME_FAIL;
					msg.obj = null;
					mhandler.sendMessage(msg);
				}
			}
		});
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);

	}

	private void displayResume(ResumeSimpleEntity entity) {

		mentity = entity;

		int completedegree = 0;
		if (entity != null) {
			String time = entity.getModifyTime();
			if (time != null) {
				time = StringUtils.formatDateStringToModifyYMD(time);
				if (time != null && time.length() > 0) {
					changeTimeText.setText(time);
				} else {
					changeTimeText.setText("");
				}
			}

			String name = entity.getName();
			if (name != null && name.length() > 0) {
				nameText.setText(name);
			} else {
				nameText.setText("");
			}

			String job = entity.getPersonalTitle();
			if (job != null && job.length() > 0) {
				hopeJobText.setText(job);
			} else {
				hopeJobText.setText("");
			}
			completedegree = entity.getPercent();
			Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(
					this.getActivity(), completedegree,
					Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
					Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
					Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
					Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
			completeImgview.setImageBitmap(completebmp);

			CommonRoundImgCreator creator = new CommonRoundImgCreator(
					this.getActivity(),
					Constants.ResumeModule.HEADPHOTO_STAND_RADIUS,
					Constants.ResumeModule.HEADPHOTO_STAND_BODER_WIDTH,
					Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

			Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.person_photo_normal_big);
			bmpManager.loadBitmap(entity.getHeadphotoUrl(), photoImgview,
					defaultbmp, creator);

			int defaultstatus = entity.getDefaultAuthenticat();
			if (defaultstatus == ResumeSimpleEntity.RESUME_DEFAULT_UNSELECTED)
				defaultresumeIv.setImageResource(R.drawable.default_normal);
			else
				defaultresumeIv.setImageResource(R.drawable.default_light);
		} else {
			// changeTimeText.setText(R.string.resume_modifytime);
			changeTimeText.setText("");
			nameText.setText(R.string.str_quickrecommend_name);
			hopeJobText.setText(R.string.resume_hopetitle);

			Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(
					this.getActivity(), completedegree,
					Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
					Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
					Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
					Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
			completeImgview.setImageBitmap(completebmp);

			CommonRoundImgCreator creator = new CommonRoundImgCreator(
					this.getActivity(),
					Constants.ResumeModule.HEADPHOTO_STAND_RADIUS,
					Constants.ResumeModule.HEADPHOTO_STAND_BODER_WIDTH,
					Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

			Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.person_photo_normal_big);
			Bitmap photobmp = creator.creator(defaultbmp);
			photoImgview.setImageBitmap(photobmp);

			defaultresumeIv.setImageResource(R.drawable.default_normal);
		}

	}

	private boolean checkIsLogin(int requestCode) {
		if (mappcontext.isLogin()) {
			return true;
		} else {
			startLoginActivity(requestCode);
			return false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == recordRequest) {
				startMyRecordActivity();
			}
			if (requestCode == collectionRequest) {
				startMyCollectionActivity();
			}
			if (requestCode == rewardRequest) {
				startMyRewardListActivity();
			}
			if (requestCode == myAccountRequest) {
				startMyAccountActivity();
			}
			if (requestCode == mobileAccountRequest) {
				startMobileAuthActivity();
			}
			if (requestCode == mailAccountRequest) {
				startMailAuthActivity();
			}
			if (requestCode >= recordRequest && requestCode <= loginRequest) {
				refreshPersonalInfo();
			}
			if (requestCode == myIntegralRequest) {
				startMyIntegralActivity();
			}
			/*
			 * if (requestCode == loginRequest) { // 登录成功 initPersonalInfo(); }
			 */
		}
	}

	private void startLoginActivity(int requestCode) {
		Intent intent = new Intent(getActivity(), UserLoginActivity.class);
		startActivityForResult(intent, requestCode);
	}

	private void refreshUI() {
		// refreshResumeView();
		initLoginStatus();
		refreshPersonalInfo();
		if(!mappcontext.isLogin()){
			Bitmap photobmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.person_photo_normal_big);
			photobmp = ImageUtils.createRoundHeadPhoto(this.getActivity(),
					photobmp);
			account_photo_iv.setImageBitmap(photobmp);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshUI();
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

	private Result getPersonalInfo() {
		try {
			Result result = ApiClient.getPersonalInfo(mappcontext);
			return result;
		} catch (AppException e) {
			/*
			 * e.printStackTrace(); mhandler.sendMessage(mhandler
			 * .obtainMessage(PERSIONAL_INIT_ERROR, e));
			 */
		}
		return null;
	}

	private class PersonalInfoAsyncTask extends AsyncTask<Void, Void, Result> {

		@Override
		protected Result doInBackground(Void... params) {
			return getPersonalInfo();
		}

		@Override
		protected void onPostExecute(Result result) {
			super.onPostExecute(result);
			if (result != null) {
				if (result.OK()) {
					String personalInfoStr = result.getJsonStr();
					if (!TextUtils.isEmpty(personalInfoStr)) {
						mpersonalInfo = (PersonalInfo) ObjectUtils
								.getObjectFromJsonString(personalInfoStr,
										PersonalInfo.class);
						initPersonalInfoView(mpersonalInfo);
					}
				} else {
					result.handleErrcode(getActivity());
				}
			}
		}
	}

	private void initResume(PersonalInfo personalInfo) {
		nameText.setText(personalInfo.getName());
		hopeJobText.setText(personalInfo.getTitle());
		defaultresumeIv.setImageResource(R.drawable.default_light);
	}

	private void initPersonalInfoView(PersonalInfo personalInfo) {
		initResume(personalInfo);

		int recommendStr = personalInfo.getJob_recommend_nums();
		int applyStr = personalInfo.getJob_apply_nums();
		int recommendCount = personalInfo.getJob_recommend_nums(); // Integer.parseInt(recommendStr);
		int applyCount = personalInfo.getJob_apply_nums();// Integer.parseInt(applyStr);
		recordCount.setVisibility(View.VISIBLE);
		String rc = (recommendCount + applyCount) > 99 ? "99+" : String
				.valueOf(recommendCount + applyCount);
		recordCount.setText(rc);

		resumeCount.setVisibility(View.VISIBLE);
		String rsmc = personalInfo.getResume_num() > 99 ? "99+" : String
				.valueOf(personalInfo.getResume_num());
		resumeCount.setText(rsmc);

		String clec = personalInfo.getJob_favor_nums() > 99 ? "99+" : String
				.valueOf(personalInfo.getJob_favor_nums());
		collectionCount.setText(clec);
		collectionCount.setVisibility(View.VISIBLE);
		String rwdc = personalInfo.getJob_reward_nums() > 99 ? "99+" : String
				.valueOf(personalInfo.getJob_reward_nums());
		rewardCount.setText(rwdc);
		rewardCount.setVisibility(View.VISIBLE);
		
		float accountnums = personalInfo.getAccount();
		if (accountnums/* != null && accountnums.length() */> 0) {
			accountCount.setText("余额：" + String.format("%.2f", accountnums)
					+ "元");
			accountCount.setVisibility(View.VISIBLE);
		}
		accountCount.setVisibility(View.GONE);

		int mobilestat = personalInfo.getPhone_status();
		mobileStatus.setVisibility(View.VISIBLE);
		if (1 == mobilestat) {
			mobileStatus.setText(R.string.fragment_my_auth_status_1);
		} else {
			mobileStatus.setText(R.string.fragment_my_auth_status_2);
		}

		int emailStat = personalInfo.getMail_status();
		emailStatus.setVisibility(View.VISIBLE);
		if (1 == emailStat) {
			emailStatus.setText(R.string.fragment_my_auth_status_1);
		} else {
			emailStatus.setText(R.string.fragment_my_auth_status_2);
		}
		if (personalInfo.getPicture() != null) {
			CommonRoundImgCreator creator = new CommonRoundImgCreator(
					this.getActivity(),
					Constants.ResumeModule.HEADPHOTO_STAND_RADIUS,
					Constants.ResumeModule.HEADPHOTO_STAND_BODER_WIDTH,
					Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

			Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.person_photo_normal_big);
			bmpManager.loadBitmap(personalInfo.getPicture(), account_photo_iv,
					defaultbmp, creator);
		}
	}

	private void clearPersonalInfoView() {
		// nameText.setText(R.string.str_quickrecommend_name);
		// hopeJobText.setText(R.string.resume_hopetitle);
		// defaultresumeIv.setImageResource(R.drawable.default_normal);
		refreshResumeView();
		
		resumeCount.setVisibility(View.INVISIBLE);
		recordCount.setVisibility(View.INVISIBLE);
		collectionCount.setVisibility(View.INVISIBLE);
		rewardCount.setVisibility(View.INVISIBLE);
		accountCount.setVisibility(View.INVISIBLE);
		mobileStatus.setVisibility(View.INVISIBLE);
		emailStatus.setVisibility(View.INVISIBLE);
	}
}

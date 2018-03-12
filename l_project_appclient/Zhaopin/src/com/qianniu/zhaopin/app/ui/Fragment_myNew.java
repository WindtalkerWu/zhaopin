package com.qianniu.zhaopin.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.PersonalInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.thp.UmShare;

public class Fragment_myNew extends BaseFragment {
	private static final int REQUEST_MYRECORD = 1;				// 我的记录
	private static final int REQUEST_MYCOLLECTION = 2;			// 我的收藏
	private static final int REQUEST_MYREWARD = 3;				// 我的悬赏
	private static final int REQUEST_MYACCOUNT = 4;				// 我的账号
	private static final int REQUEST_MOBILEACCOUNT = 5;			// 手机绑定
	private static final int REQUEST_MAILACCOUNT = 6;			// 邮箱绑定
	private static final int REQUEST_LOGIN = 7;
	private static final int REQUEST_MYINTEGRAL = 8;			// 积分管理
	private static final int REQUEST_ADDRESUME = 9;				// 创建简历
	
	// 头像圆角弧度
	private static final int  HEAD_IMG_ROUNDPX = 10;
	
	private Context m_Context;
	private AppContext m_appContext;
	
	private View m_viewMy;
	
	private ImageView m_btnAdd;		// 添加简历按钮
	private Button m_btnTopRight;	// 登录/注销按钮
	
	// 我的账号
	private RelativeLayout m_rlMyAccount;
	private ImageView m_imgAccountHead;
	// 姓名/昵称
	private TextView m_tvAccountName;
	// 用户名
	private TextView m_tvUserName;
	// 余额
	private TextView m_tvBalance;
	
	// 积分管理
	private RelativeLayout m_rlIntegral;
	// 积分标题名
	private TextView m_tvIntegralTitle;
	// 积分数
	private TextView m_tvIntegralCount;
	
	// 我的简历库
	private RelativeLayout m_rlMyResume;
	// 我的简历库标题
	private TextView m_tvMyResumeTitle;
	// 简历数量
	private TextView m_tvMyResumeCount;
	
	// 我的悬赏
	private RelativeLayout m_rlMyReward;
	// 我的悬赏标题
	private TextView m_tvMyRewardTitle;
	// 简历数量
	private TextView m_tvMyRewardCount;
	
	// 我的记录
	private RelativeLayout m_rlMyRecord;
	// 我的记录标题
	private TextView m_tvMyRecordTitle;
	// 我的记录数量
	private TextView m_tvMyRecordCount;
	
	// 我的收藏
	private RelativeLayout m_rlMyCollection;
	// 我的收藏标题
	private TextView m_tvMyCollectionTitle;
	// 我的收藏数量
	private TextView m_tvMyCollectionCount;
	
	// 手机绑定
	private RelativeLayout m_rlPhoneAccount;
	// 手机绑定标题
	//private TextView m_tvPhoneAccountTitle;
	// 手机绑定状态
	private TextView m_tvPhoneAccountStatus;
	
	
	// 邮箱绑定
	private RelativeLayout m_rlMailAccount;
	// 邮箱绑定标题
	//private TextView m_tvMailAccountTitle;
	// 邮箱绑定状态
	private TextView m_tvMailAccountStatus;
	
	// 设置
	private RelativeLayout m_rlSetting;
			 
	// 个人信息
	private PersonalInfo m_personalInfo;
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == getActivity().RESULT_OK) {
			// 更新个人信息
			refreshUI();
			
			switch(requestCode){
				case REQUEST_MYRECORD:
					{
						// 进入我的记录界面
						startMyRecordActivity();
					}
					break;
				case REQUEST_MYCOLLECTION:
					{
						// 进入我的收藏界面
						startMyCollectionActivity();
					}
					break;
				case REQUEST_MYREWARD: // 我的悬赏
					{
						// 进入我的悬赏界面
						startMyRewardListActivity();
					}
					break;
				case REQUEST_MYACCOUNT: // 我的账号
					{
						// 进入我的账号界面
						startMyAccountActivity();
					}
					break;
				case REQUEST_MOBILEACCOUNT: // 手机绑定
					{
						// 进入手机绑定界面
						startMobileAuthActivity();
					}
					break;
				case REQUEST_MAILACCOUNT:	// 邮箱绑定
					{
						// 进入邮箱绑定界面
						startMailAuthActivity();
					}
					break;
				case REQUEST_LOGIN:
					{
						// 更新个人信息
						//refreshUI();
					}
					break;
				case REQUEST_MYINTEGRAL: // 积分管理
					{
						// 进入积分管理界面
						startMyIntegralActivity();
					}
					break;
				case REQUEST_ADDRESUME:  // 创建简历
					{
						addResume();
					}
					break;
				default:
					break;
			}
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		m_Context = this.getActivity();
		m_appContext = (AppContext) this.getActivity().getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (m_viewMy != null) {
			ViewGroup p = (ViewGroup) m_viewMy.getParent();
			if (p != null) {
				p.removeAllViewsInLayout();
			}
		} else {
			m_viewMy = inflater.inflate(R.layout.fragment_mynew, container,
					false);
			// 初始化控件
			initControl();
		}
		
		refreshUI();
		return m_viewMy;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		// 友盟统计
		UmShare.UmPause(m_Context);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 更新个人信息
		refreshUI();
		
		// 友盟统计
		UmShare.UmResume(m_Context);
	}
	
	private OnClickListener m_listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.my_btn_add:			// 添加简历
				{
					if (checkIsLogin(REQUEST_ADDRESUME)) {
						// 添加简历
						addResume();
					}
				}
				break;
			case R.id.my_rl_setting:		// 设置
				{
					 // 进入设置界面
					startSettingActivity();
				}
				break;
			case R.id.my_btn_topright:	// 登录/注销
				{
					if (!m_appContext.isLogin()) {
						// 友盟统计--设置--登录按钮
						UmShare.UmStatistics(m_Context, "Set_LoginButton");
						Intent intent = new Intent(getActivity(),
								UserLoginActivity.class);
						startActivityForResult(intent, REQUEST_LOGIN);
					} else {
						// 友盟统计--设置--退出账号按钮
						UmShare.UmStatistics(m_Context, "Set_ExitButton");
						
						AlertDialog.Builder dialog = null;
						DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (which == dialog.BUTTON_POSITIVE) {
									(m_appContext).Logout();
									// UIHelper.showLoginActivity(getActivity());
									UIHelper.ToastMessage(m_Context,
											R.string.msg_logout_success);
									refreshUI();
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
			case R.id.my_rl_myaccount:	// 我的账号
				{
					// 友盟统计--我--我的账号按钮
					UmShare.UmStatistics(m_Context, "My_MyAccountButton");
					if (checkIsLogin(REQUEST_MYACCOUNT)) {
						startMyAccountActivity();
					}
				}
				break;
			case R.id.my_rl_integral:	// 积分管理
				{
					// 友盟统计--我--积分管理按钮
					UmShare.UmStatistics(m_Context, "My_MyIntegralButton");
					if (checkIsLogin(REQUEST_MYINTEGRAL)) {
						startMyIntegralActivity();
					}
				}
				break;
			case R.id.my_rl_myresume:	// 我的简历库
				{
					// 友盟统计--我--简历库按钮
					UmShare.UmStatistics(m_Context, "My_ResumePhotoButton");

					// UIHelper.showResumeListActivity(mcontext);
					UIHelper.showResumeListActivityBehindCheck(getActivity());
				}
				break;
			case R.id.my_rl_myreward:	// 我的悬赏
				{
					// 友盟统计--我--我的悬赏按钮
					UmShare.UmStatistics(m_Context, "My_MyRewardButton");
					if (checkIsLogin(REQUEST_MYREWARD)) {
						startMyRewardListActivity();
					}
				}
				break;
			case R.id.my_rl_myrecord:	// 我的记录
				{
					// 友盟统计--我--我的纪录按钮
					UmShare.UmStatistics(m_Context, "My_MyRecordButton");
					if (checkIsLogin(REQUEST_MYRECORD)) {
						startMyRecordActivity();
					}
				}
				break;
			case R.id.my_rl_mycollection:	// 我的收藏 
				{
					// 友盟统计--我--我的收藏按钮
					UmShare.UmStatistics(m_Context, "My_MyCollectionButton");
					if (checkIsLogin(REQUEST_MYCOLLECTION)) {
						startMyCollectionActivity();
					}
				}
				break;
			case R.id.my_rl_phoneaccount:	// 手机绑定
				{
					// 友盟统计--我--手机绑定按钮
					UmShare.UmStatistics(m_Context, "My_PhoneAccountButton");
					if (checkIsLogin(REQUEST_MOBILEACCOUNT)) {
						startMobileAuthActivity();
					}
				}
				break;
			case R.id.my_rl_mailaccount:	// 邮箱绑定
				{
					// 友盟统计--我--邮箱绑定按钮
					UmShare.UmStatistics(m_Context, "My_MailAccountButton");
					if (checkIsLogin(REQUEST_MAILACCOUNT)) {
						startMailAuthActivity();
					}
					break;
				}
			}
		}
		
	};
	
	private OnLongClickListener m_longListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.my_rl_myaccount:	// 我的账号
				{
					UIHelper.showPopTips(m_Context, m_imgAccountHead,
							"您的账号管理、账号绑定、赏金结算、账户余额都在这里查看");
				}
				break;
			case R.id.my_rl_integral:	// 积分管理
				{
					UIHelper.showPopTips(m_Context, m_tvIntegralTitle,
							"您的积分管理、兑换记录、积分充值，积分规则等请在这里查询");
				}
				break;
			case R.id.my_rl_myresume:	// 我的简历库
				{
					UIHelper.showPopTips(m_Context, m_tvMyResumeTitle,
							"您新增的简历都在\"我的简历库\"列表内，可随时查看");
				}
				break;
			case R.id.my_rl_myreward:	// 我的悬赏
				{
					UIHelper.showPopTips(m_Context, m_tvMyRewardTitle,
							"您发布的悬赏都在“我的悬赏”列表内，可随时查看进度");
				}
				break;
			case R.id.my_rl_myrecord:	// 我的记录
				{
					UIHelper.showPopTips(m_Context, m_tvMyRecordTitle,
							"可查看全部应聘或推荐记录，全部悬赏记录，实时跟踪赏金状态");
				}
				break;
			case R.id.my_rl_mycollection:	// 我的收藏 
				{
					UIHelper.showPopTips(m_Context, m_tvMyCollectionTitle,
							"您收藏的各种任务，发布悬赏，职位，活动信息都在这里哦！");
				}
				break;
			case R.id.my_rl_phoneaccount:	// 手机绑定
				{

				}
				break;
			case R.id.my_rl_mailaccount:	// 邮箱绑定
				{

				}
				break;
			}
			
			return false;
		}
		
	};
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 添加简历按钮
		m_btnAdd = (ImageView)m_viewMy.findViewById(R.id.my_btn_add);
		m_btnAdd.setOnClickListener(m_listener);
		
		// 登录/注销
		m_btnTopRight = (Button)m_viewMy.findViewById(R.id.my_btn_topright);
		m_btnTopRight.setOnClickListener(m_listener);
		
		// 我的账号
		m_rlMyAccount = (RelativeLayout)m_viewMy.findViewById(R.id.my_rl_myaccount);
		// 姓名/昵称
		m_tvAccountName = (TextView)m_viewMy.findViewById(R.id.my_tv_accountname);
		// 用户名
		m_tvUserName = (TextView)m_viewMy.findViewById(R.id.my_tv_username);
		// 余额
		m_tvBalance = (TextView)m_viewMy.findViewById(R.id.my_tv_balance);
		
		// 个人头像
		m_imgAccountHead = (ImageView) m_viewMy
				.findViewById(R.id.my_img_head);
		// 设置默认头像
		setDefaultHeadBmp();
		
		m_rlMyAccount.setOnClickListener(m_listener);
		m_rlMyAccount.setOnLongClickListener(m_longListener);
		
		// 积分管理
		m_rlIntegral = (RelativeLayout)m_viewMy.findViewById(R.id.my_rl_integral);
		// 积分标题
		m_tvIntegralTitle = (TextView)m_viewMy.findViewById(R.id.my_tv_myintegraltitle);
		// 积分数
		m_tvIntegralCount = (TextView)m_viewMy.findViewById(R.id.my_tv_myintegralcount);
		
		m_rlIntegral.setOnClickListener(m_listener);
		m_rlIntegral.setOnLongClickListener(m_longListener);
		
		// 我的简历库
		m_rlMyResume = (RelativeLayout)m_viewMy.findViewById(R.id.my_rl_myresume);
		// 我的简历库标题
		m_tvMyResumeTitle = (TextView)m_viewMy.findViewById(R.id.my_tv_myresumetitle);
		// 简历数量
		m_tvMyResumeCount = (TextView)m_viewMy.findViewById(R.id.my_tv_myresumecount);
		
		m_rlMyResume.setOnClickListener(m_listener);
		m_rlMyResume.setOnLongClickListener(m_longListener);
		
		// 我的悬赏
		m_rlMyReward = (RelativeLayout)m_viewMy.findViewById(R.id.my_rl_myreward);
		// 我的悬赏标题
		m_tvMyRewardTitle = (TextView)m_viewMy.findViewById(R.id.my_tv_myrewardtitle);
		// 我的悬赏数量
		m_tvMyRewardCount = (TextView)m_viewMy.findViewById(R.id.my_tv_myrewardcount);
		
		m_rlMyReward.setOnClickListener(m_listener);
		m_rlMyReward.setOnLongClickListener(m_longListener);
		
		// 我的记录
		m_rlMyRecord = (RelativeLayout)m_viewMy.findViewById(R.id.my_rl_myrecord);
		// 我的记录标题
		m_tvMyRecordTitle = (TextView)m_viewMy.findViewById(R.id.my_tv_myrecordtitle);
		// 我的记录数量
		m_tvMyRecordCount = (TextView)m_viewMy.findViewById(R.id.my_tv_myrecordcount);
		
		m_rlMyRecord.setOnClickListener(m_listener);
		m_rlMyRecord.setOnLongClickListener(m_longListener);
		
		// 我的收藏
		m_rlMyCollection = (RelativeLayout)m_viewMy.findViewById(R.id.my_rl_mycollection);
		// 我的收藏标题
		m_tvMyCollectionTitle = (TextView)m_viewMy.findViewById(R.id.my_tv_mycollectiontitle);
		// 我的收藏数量
		m_tvMyCollectionCount = (TextView)m_viewMy.findViewById(R.id.my_tv_mycollectioncount);
		
		m_rlMyCollection.setOnClickListener(m_listener);
		m_rlMyCollection.setOnLongClickListener(m_longListener);
		
		// 手机绑定
		m_rlPhoneAccount = (RelativeLayout)m_viewMy.findViewById(R.id.my_rl_phoneaccount);
		// 手机绑定状态
		m_tvPhoneAccountStatus = (TextView)m_viewMy.findViewById(R.id.my_tv_phoneaccountstatus);
		
		m_rlPhoneAccount.setOnClickListener(m_listener);
		
		// 邮箱绑定
		m_rlMailAccount = (RelativeLayout)m_viewMy.findViewById(R.id.my_rl_mailaccount);
		// 邮箱绑定状态
		m_tvMailAccountStatus = (TextView)m_viewMy.findViewById(R.id.my_tv_mailaccountstatus);
		m_rlMailAccount.setOnClickListener(m_listener);
		
		// 设置
		m_rlSetting = (RelativeLayout)m_viewMy.findViewById(R.id.my_rl_setting);
		m_rlSetting.setOnClickListener(m_listener);
		m_rlSetting.setOnLongClickListener(m_longListener);
	}
	
	/**
	 * 判断是否登录
	 * @param nRequestCode
	 * @return
	 */
	private boolean checkIsLogin(int nRequestCode) {
		if (m_appContext.isLogin()) {
			return true;
		} else {
			startLoginActivity(nRequestCode);
			return false;
		}
	}
	
	/**
	 * 进入登录界面
	 * @param nRequestCode
	 */
	private void startLoginActivity(int nRequestCode) {
		Intent intent = new Intent(getActivity(), UserLoginActivity.class);
		startActivityForResult(intent, nRequestCode);
	}
	
	/**
	 * 进入我的账号
	 */
	private void startMyAccountActivity() {
		Intent intent = new Intent(getActivity(), MyAccountActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 进入我的收藏
	 */
	private void startMyCollectionActivity() {
		Intent intent = new Intent(getActivity(), MyCollectionActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 进入我的记录
	 */
	private void startMyRecordActivity() {
		Intent intent = new Intent(getActivity(), MyRecordActivity.class);
		startActivity(intent);
	}

	/**
	 * 进入我的悬赏列表
	 */
	private void startMyRewardListActivity() {
		Intent intent = new Intent(getActivity(), MyRewardListActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 进入积分管理界面
	 */
	private void startMyIntegralActivity(){
		Intent intent = new Intent(getActivity(), MyIntegralActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 进入邮箱绑定界面
	 */
	private void startMailAuthActivity() {
		Intent intent = new Intent(getActivity(), MailAuthActivity.class);
		startActivity(intent);
	}

	/**
	 * 进入手机绑定界面
	 */
	private void startMobileAuthActivity() {
		Intent intent = new Intent(getActivity(), MobileAuthActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 进入设置界面
	 */
	private void startSettingActivity() {
		Intent intent = new Intent(getActivity(), SettingActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 更新个人信息
	 */
	private void refreshUI() {
		// 设置登录状态
		initLoginStatus();
		refreshPersonalInfo();
	}
	
	/**
	 * 设置登录状态
	 */
	private void initLoginStatus() {
		if (!(m_appContext).isLogin()) {
			m_btnTopRight.setText(R.string.frame_btn_login);
		} else {
			m_btnTopRight.setText(R.string.frame_btn_logout);
		}
	}
	
	/**
	 * 刷新个人信息
	 */
	private void refreshPersonalInfo(){
		if(m_appContext.isLogin()){
			new PersonalInfoAsyncTask().execute();
		}else{
			// 清除个人信息
			clearPersonalInfo();
		}
	}
	
	/**
	 * 清除个人信息
	 */
	private void clearPersonalInfo(){
		// 姓名/昵称
		m_tvAccountName.setText("");
		// 用户名
		m_tvUserName.setText("");
		// 余额
		m_tvBalance.setText("0");
		
		m_tvIntegralCount.setVisibility(View.INVISIBLE);
		m_tvMyResumeCount.setVisibility(View.INVISIBLE);
		m_tvMyRewardCount.setVisibility(View.INVISIBLE);
		m_tvMyRecordCount.setVisibility(View.INVISIBLE);
		m_tvMyCollectionCount.setVisibility(View.INVISIBLE);
		m_tvPhoneAccountStatus.setVisibility(View.INVISIBLE);
		m_tvMailAccountStatus.setVisibility(View.INVISIBLE);
		
		// 设置默认头像
		setDefaultHeadBmp();
	}
	
	private Result getPersonalInfo() {
		try {
			Result result = ApiClient.getPersonalInfo(m_appContext);
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
						m_personalInfo = (PersonalInfo) ObjectUtils
								.getObjectFromJsonString(personalInfoStr,
										PersonalInfo.class);
						initPersonalInfoView(m_personalInfo);
					}
				} else {
					result.handleErrcode(getActivity());
				}
			}
		}
	}
	
	/**
	 * 更新UI上的数据
	 * @param personalInfo
	 */
	private void initPersonalInfoView(PersonalInfo personalInfo) {
		int recommendCount = personalInfo.getJob_recommend_nums(); // Integer.parseInt(recommendStr);
		int applyCount = personalInfo.getJob_apply_nums();// Integer.parseInt(applyStr);
		m_tvMyRecordCount.setVisibility(View.VISIBLE);
		String rc = (recommendCount + applyCount) > 99 ? "99+" : String
				.valueOf(recommendCount + applyCount);
		m_tvMyRecordCount.setText(rc);

		m_tvMyResumeCount.setVisibility(View.VISIBLE);
		String rsmc = personalInfo.getResume_num() > 99 ? "99+" : String
				.valueOf(personalInfo.getResume_num());
		m_tvMyResumeCount.setText(rsmc);

		String clec = personalInfo.getJob_favor_nums() > 99 ? "99+" : String
				.valueOf(personalInfo.getJob_favor_nums());
		m_tvMyCollectionCount.setText(clec);
		m_tvMyCollectionCount.setVisibility(View.VISIBLE);
		String rwdc = personalInfo.getJob_reward_nums() > 99 ? "99+" : String
				.valueOf(personalInfo.getJob_reward_nums());
		m_tvMyRewardCount.setText(rwdc);
		m_tvMyRewardCount.setVisibility(View.VISIBLE);

		int mobilestat = personalInfo.getPhone_status();
		m_tvPhoneAccountStatus.setVisibility(View.VISIBLE);
		if (1 == mobilestat) {
			m_tvPhoneAccountStatus.setText(R.string.fragment_my_auth_status_1);
		} else {
			m_tvPhoneAccountStatus.setText(R.string.fragment_my_auth_status_2);
		}

		int emailStat = personalInfo.getMail_status();
		m_tvMailAccountStatus.setVisibility(View.VISIBLE);
		if (1 == emailStat) {
			m_tvMailAccountStatus.setText(R.string.fragment_my_auth_status_1);
		} else {
			m_tvMailAccountStatus.setText(R.string.fragment_my_auth_status_2);
		}
		
		// 头像
		if (null != personalInfo.getPicture() && !
				personalInfo.getPicture().isEmpty()) {
			BitmapManager bmpManager = new BitmapManager(BitmapFactory.decodeResource(
					getResources(), R.drawable.person_photo_normal_big));
			
			Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.person_photo_normal_big);
			// 正方形圆角
//			bmpManager.loadFilletFrameBitmap(this.getActivity(),
//					personalInfo.getPicture(),
//					m_imgAccountHead,
//					defaultbmp,
//					HEAD_IMG_ROUNDPX,
//					Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);
			// 圆形
			bmpManager.loadFilletRoundBitmap(this.getActivity(),
					personalInfo.getPicture(),
					m_imgAccountHead,
					defaultbmp);
		}
		
		// 姓名/昵称
		if(null != personalInfo.getDisplay_name() && 
				!personalInfo.getDisplay_name().isEmpty()){
			m_tvAccountName.setText(personalInfo.getDisplay_name());
		}
		
		// 用户名
		if(null != personalInfo.getName() && 
				!personalInfo.getName().isEmpty()){
			m_tvUserName.setText(String.format(getString(R.string.str_my_username),
					personalInfo.getName()));
		}

		// 余额
		if(0 != personalInfo.getAccount() &&
				0.0 != personalInfo.getAccount() &&
				0.00 != personalInfo.getAccount()
				){
			m_tvBalance.setText(Float.toString(personalInfo.getAccount()));
		}
	}
	
	/**
	 * 设置默认头像
	 */
	private void setDefaultHeadBmp(){
		// 默认头像
		Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.person_photo_normal_big);
		
		// 正方形圆角
//		m_imgAccountHead.setImageBitmap(ImageUtils.filletFrameBitmap(this.getActivity(),
//				defaultbmp, HEAD_IMG_ROUNDPX, Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR));	
		// 圆形
//		m_imgAccountHead.setImageBitmap(ImageUtils.createRoundHeadPhoto(this.getActivity(),
//				defaultbmp));
		m_imgAccountHead.setImageBitmap(ImageUtils.filletRoundBitmap(this.getActivity(),
				defaultbmp));
	}
	
	/**
	 * 添加简历
	 */
	private void addResume(){
		if (null == m_personalInfo ) {
			UIHelper.showMyResumeEditActivityForResult(
					getActivity(), 0);
		} else if (m_personalInfo.getResume_num() < ResumeListActivity.RESUMES_NUMBER_MAX) {
			UIHelper.showMyResumeEditActivityForResult(
					getActivity(), 0);
		} else {
			UIHelper.ToastMessage(getActivity(), String.format(
					"您最多可新建%d份简历",
					ResumeListActivity.RESUMES_NUMBER_MAX));
		}
	}
}

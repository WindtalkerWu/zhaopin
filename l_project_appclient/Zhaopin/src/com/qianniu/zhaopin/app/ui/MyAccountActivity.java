
package com.qianniu.zhaopin.app.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.MyAccontInfoData;
import com.qianniu.zhaopin.app.bean.MyAccontInfoDataEntity;
import com.qianniu.zhaopin.app.bean.MyAccountPersonalProfile;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.app.view.Util;
import com.qianniu.zhaopin.thp.UmShare;

public class MyAccountActivity extends BaseActivity {
	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageView m_imgComplete;
	private ImageView m_imgPhoto;
	
	private ImageView m_imgLine;					// 我的余额上面的线
//	private ImageView m_imgAuthCodeLine;			// 验证码上面的线
	
	private ImageView m_btnBack;					// 返回按钮
//	private RelativeLayout m_rlPhone;  				// 手机号绑定
	private RelativeLayout m_rlMyBalance;  			// 我的余额
	private RelativeLayout m_rlMyBalanceAll;  		// 我的余额(整个我的余额控件)
//	private RelativeLayout m_rlAuthCode;  			// 验证码(整个验证码控件)
//	private Button m_btnGetAuthcode;				// 获取验证码
	private Button m_btnSubmit;						// 提交按钮
	private ImageButton m_btnRightSubmit;			// 提交按钮(右上角)
	
	private EditText m_etUserName;					// 姓名输入框
//	private EditText m_etPhone;						// 手机号输入框
//	private EditText m_etAuthcode;					// 验证码输入框
	
//	private TextView m_tvPhone;						// 手机号码文本框
//	private TextView m_tvPhoneBinding;				// 手机号码绑定文本框
	private TextView m_tvMyBalance;					// 我的余额文本框
	private TextView m_tvTitle;						// 标题
	
	private RadioButton m_rbtnMale;					// 单选框男
	private RadioButton m_rbtnFamale;				// 单选框女
	
	private MyAccontInfoData m_maid;				// 我的账号信息
	private MyAccountPersonalProfile m_MAPP;		// 我的账号信息(提交时的参数)
	
	private int m_nType;
	
	private Bitmap m_bmpHeadsculptur;				// 头像
	private String m_strHeadsculpturUrl = "";		// 个人头像url
	
//	private String m_strOldPhone = "";				// 保存最初的手机号
	private String m_strOldName = "";				// 保存最初的姓名
	private String m_strOldGender = "";				// 保存最初的性别
	
	private boolean m_bShowSumitControl = false; 	// 提交控件是否显示
//	private boolean m_bShowAuthCodeControl = false; // 验证码相关控件是否显示
	
//	private boolean m_bVerifyPhone = false; 		// 是否要绑定手机号
	
//	private boolean m_bPhoneChange = false;			// 手机号是否变动过
	private boolean m_bNameChange  = false;			// 姓名是否变动过
//	private boolean m_bGenderChange = false;				// 性别是否变动过
	
	/**
	 * 照相机拍摄照片转化为该File对象
	 */
	private File mCurrentPhotoFile;
	
	/** 通过图库获取图片 */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	/** 照相机拍照获取图片 */
	private static final int CAMERA_WITH_DATA = 3022;
	private static final int ICON_SIZE = 96;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myaccountactivity);
		
		m_Context = this;
		m_appContext = (AppContext) this.getApplication();
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);

		initControl();
		initData();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case CAMERA_WITH_DATA:
			{
				doCropPhoto(mCurrentPhotoFile);
			}
			break;
		case PHOTO_PICKED_WITH_DATA:
			{
				if(null != data){
					m_bmpHeadsculptur = data.getParcelableExtra("data");
					if(null != m_bmpHeadsculptur){
						updataHeadsculptur();
					}					
				}
			}
			break;
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		back();
		super.onBackPressed();
	}

	/**
	 * 初始化头像控件
	 */
	private void initHeadSculpture(){
		m_imgComplete = (ImageView)findViewById(R.id.myaccount_img_complete);
		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(
				this, 0,
				Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
				Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
				Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
				Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
		m_imgComplete.setImageBitmap(completebmp);

		m_imgPhoto = (ImageView) findViewById(R.id.myaccount_img_headphoto);
		Bitmap photobmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.person_photo_normal_big);
		photobmp = ImageUtils.createRoundHeadPhoto(this,
				photobmp);
		m_imgPhoto.setImageBitmap(photobmp);
		m_imgPhoto.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--我的账号--上传头像按钮
				UmShare.UmStatistics(m_Context, "MyAccount_UploadHeadButton");
				
				showSetPhotoDialog();
			}
		});
	}
	
	/**
	 * 初始化文本控件
	 */
	private void initTextControl(){
		// 姓名输入框
		m_etUserName = (EditText)findViewById(R.id.myaccount_et_name);
		m_etUserName.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(!m_etUserName.getText().toString().equals(m_strOldName)){
					// 显示提交按钮
					showEditControl();
					
					m_bNameChange = true;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		// 手机号输入框
//		m_etPhone = (EditText)findViewById(R.id.myaccount_tv_phone);
//		m_etPhone.addTextChangedListener(new TextWatcher(){
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//				
//				if(!m_etPhone.getText().toString().equals(m_strOldPhone)){
//					// 显示提交按钮
//					showEditControl();
//					// 显示验证码
//					showAuthCodeControl();
//					
//					m_bPhoneChange = true;
//				}
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
//		
//		// 验证码输入框
//		m_etAuthcode = (EditText)findViewById(R.id.myaccount_et_authcode);	
		
		// 标题
		m_tvTitle = (TextView)findViewById(R.id.myaccount_tv_title);
//		// 手机号码文本框
//		m_tvPhone = (TextView)findViewById(R.id.mybalance_tv_phone);
//		// 手机号码绑定文本框
//		m_tvPhoneBinding = (TextView)findViewById(R.id.mybalance_tv_phone_binding);
		// 我的余额文本框
		m_tvMyBalance = (TextView)findViewById(R.id.myaccount_tv_mybalance);
	}
	
	
	/**
	 * 初始化按钮控件
	 */
	private void initBtnControl(){
		// 单选框男
		m_rbtnMale = (RadioButton)findViewById(R.id.myaccount_rb_male);	
		m_rbtnMale.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!m_strOldGender.equals(HeadhunterPublic.MYACCOUNT_GENDER_MALE)){//男
					// 显示提交按钮
					showEditControl();
				}
			}
			
		});
		
		// 单选框女
		m_rbtnFamale = (RadioButton)findViewById(R.id.myaccount_rb_female);	
		m_rbtnFamale.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!m_strOldGender.equals(HeadhunterPublic.MYACCOUNT_GENDER_FEMALE)){//女
					// 显示提交按钮
					showEditControl();
				}
			}
		});
		
		// 我的余额
		m_rlMyBalanceAll = (RelativeLayout)findViewById(R.id.myaccount_rl_mybalanceall);
		
		// 验证码(整个验证码控件)
//		m_rlAuthCode = (RelativeLayout)findViewById(R.id.myaccount_rl_authcodeall); 			
				
		// 返回按钮
		m_btnBack = (ImageView)findViewById(R.id.myaccount_btn_back);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				back();
			}
		});
		
		// 手机号绑定
//		m_rlPhone = (RelativeLayout)findViewById(R.id.myaccount_rl_phone);
//		m_rlPhone.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				// 友盟统计--我的账号--手机号绑定按钮
//				UmShare.UmStatistics(m_Context, "MyAccount_PhoneBindingButton");
//				
////				showSecurePopupWindow(v);
//				doPhoneBinding();
//			}
//		});
		
		// 我的余额
		m_rlMyBalance = (RelativeLayout)findViewById(R.id.myaccount_rl_mybalance);
		m_rlMyBalance.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--我的账号--我的余额按钮
				UmShare.UmStatistics(m_Context, "MyAccount_MyBalanceButton");
				
				// 进入我的余额界面
				startMyBalanceActivity();
			}
		});
		
		// 获取验证码
//		m_btnGetAuthcode = (Button)findViewById(R.id.myaccount_btn_getauthcode);
//		m_btnGetAuthcode.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// 检查手机号是否合法
//				if(checkPhone()){
//					// 开始获取验证码
//					startGetAuthCode();
//				}
//			}
//		});
		
		// 提交按钮
		m_btnSubmit = (Button)findViewById(R.id.myaccount_btn_submit);
		m_btnSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--我的账号--提交按钮
				UmShare.UmStatistics(m_Context, "MyAccount_SubmitButton");
				
				// 判断控件上的值
				if(checkControlValue()){
//					if(!m_bVerifyPhone){
//						// 提交
//						Sumit();
//					}else{
//						// 绑定手机
//						startVerifyPhone();
//					}
					// 提交
					Sumit();
				}
			}
		});
		
		// 提交按钮(右上角)
		m_btnRightSubmit = (ImageButton)findViewById(R.id.myaccount_btn_rightsubmit);
		m_btnRightSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--我的账号--提交按钮(右上角)
				UmShare.UmStatistics(m_Context, "MyAccount_RightSubmitButton");
				
				// 判断控件上的值
//				if(checkControlValue()){
//					// 绑定手机
//					startVerifyPhone();
//				}
				// 提交
				Sumit();
			}
		});
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		//初始化头像相关控件
		initHeadSculpture();
		
		//初始化文本控件
		initTextControl();
		
		//初始化按钮控件
		initBtnControl();
		
		// 我的余额上面的线
		m_imgLine = (ImageView)findViewById(R.id.myaccount_img_mybalanceallline);
		
		// 验证码上面的线
//		m_imgAuthCodeLine = (ImageView)findViewById(R.id.myaccount_img_authcodeline);			
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		m_nType = HeadhunterPublic.MYACCOUNT_TYPE_DEFAULT;
		
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_nType = bundle.getInt(
					HeadhunterPublic.MYACCOUNT_DATATRANSFER_TYPE);
			
			m_maid = (MyAccontInfoData)bundle.getSerializable(
					HeadhunterPublic.MYACCOUNT_DATATRANSFER_USERINFO);
			if(null != m_maid){
				updataControl(m_maid);
			}
		}
		
		switch(m_nType){
		case HeadhunterPublic.MYACCOUNT_TYPE_FIRST:
			{
//				m_bPhoneChange = true;
				m_bNameChange = true;
				showControl(true);
				
				if(null != m_tvTitle){
					m_tvTitle.setText(getString(
							R.string.str_myaccount_perfectinformationtitle));
				}
			}
			break;
		case HeadhunterPublic.MYACCOUNT_TYPE_DEFAULT:
		default:
			{
				showControl(false);
				
				// 开始获取我的账号信息
				startGetMyAccountInfo();
			}
			break;
		}
	}
	
	/**
	 * 显示/隐藏命令按钮
	 * @param bShow
	 */
	private void showControl(boolean bShow){
		if(null != m_btnRightSubmit){
			if(bShow){
				m_btnRightSubmit.setVisibility(View.VISIBLE);
			}else{
				m_btnRightSubmit.setVisibility(View.GONE);
			}
		}
		
		if(null != m_btnSubmit){
			if(bShow){
				m_btnSubmit.setVisibility(View.VISIBLE);
			}else{
				m_btnSubmit.setVisibility(View.GONE);
			}
		}
		
		if(null != m_imgLine){
			if(bShow){	// 隐藏我的余额上面的线
				m_imgLine.setVisibility(View.GONE);
			}else{		// 显示我的余额上面的线
				m_imgLine.setVisibility(View.VISIBLE);
			}
		}
		
		if(null != m_rlMyBalanceAll){
			if(bShow){	// 隐藏我的余额
				m_rlMyBalanceAll.setVisibility(View.GONE);
			}else{		// 显示我的余额
				m_rlMyBalanceAll.setVisibility(View.VISIBLE);
			}
		}
		
//		if(null != m_imgAuthCodeLine){
//			if(bShow){// 显示验证码上面的线
//				m_imgAuthCodeLine.setVisibility(View.VISIBLE);
//			}else{// 隐藏验证码上面的线
//				m_imgAuthCodeLine.setVisibility(View.GONE);
//			}
//		}
//		
//		if(null != m_rlAuthCode){
//			if(bShow){// 显示验证码
//				m_rlAuthCode.setVisibility(View.VISIBLE);
//			}else{// 隐藏验证码
//				m_rlAuthCode.setVisibility(View.GONE);
//			}
//		}
	}
	
	/**
	 * 显示编辑需要的相关控件
	 */
	private void showEditControl(){
		if(HeadhunterPublic.MYACCOUNT_TYPE_FIRST == m_nType
				|| m_bShowSumitControl){
			return;
		}
		
		// 显示右上角提交按钮
		m_btnRightSubmit.setVisibility(View.VISIBLE);
		// 显示提交按钮
		m_btnSubmit.setVisibility(View.VISIBLE);
		
		m_bShowSumitControl = true;
	}
	
	/**
	 * 
	 */
//	private void showAuthCodeControl(){
//		if(HeadhunterPublic.MYACCOUNT_TYPE_FIRST == m_nType
//				|| m_bShowAuthCodeControl){
//			return;
//		}
//		
//		// 显示验证码上面的线
//		m_imgAuthCodeLine.setVisibility(View.VISIBLE);
//		// 显示验证码
//		m_rlAuthCode.setVisibility(View.VISIBLE);
//		
//		m_bShowAuthCodeControl = true;
//	}
	
	/**
	 * 手机号绑定
	 */
	private void doPhoneBinding() {
//		String strAccount = getAliplayAccount();
//		
//		Intent intent = new Intent(MyBalanceActivity.this, AlipayAccountBindingActivity.class);
//		intent.putExtra(AlipayAccountBindingActivity.ALIPAY_ACCOUNT, strAccount);
//		startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
		
		Intent intent = new Intent(MyAccountActivity.this, MobileAuthActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 进入我的余额界面
	 */
	private void startMyBalanceActivity(){
		Intent intent = new Intent(MyAccountActivity.this, MyBalanceActivity.class);
		
		startActivity(intent);
	}
	
	/**
	 * 提交我的账号信息
	 */
	private void Sumit(){
		if (!UIHelper.isNetworkConnected(m_appContext)) {
			return;
		}
		
		new Thread() {
			public void run() {
				submitPersonalProfile();
			}
		}.start();
	}
	
	/**
	 * 刷新头像
	 * @param entity
	 */
	private void refreshHeadSculpture(String strPhotoUrl){
		// 
		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(
				this, 0,
				Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
				Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
				Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
				Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
		m_imgComplete.setImageBitmap(completebmp);

		CommonRoundImgCreator creator = new CommonRoundImgCreator(
				this,
				Constants.ResumeModule.HEADPHOTO_STAND_RADIUS,
				Constants.ResumeModule.HEADPHOTO_STAND_BODER_WIDTH,
				Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

		Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.person_photo_normal_big);
		
		BitmapManager bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				getResources(), R.drawable.widget_dface_loading));
		bmpManager.loadBitmap(strPhotoUrl, m_imgPhoto,
				defaultbmp, creator);
	}
	
	/**
	 * 更新控件上的数据
	 * @param uid
	 */
	private void updataControl(MyAccontInfoData mid){
		// 个人头像
		if(null != mid.getAvatar() && !mid.getAvatar().isEmpty()){
			m_strHeadsculpturUrl = mid.getAvatar();
			refreshHeadSculpture(mid.getAvatar());
		}
		
		// 昵称
		if(null != mid.getDisplay_name() && !mid.getDisplay_name().isEmpty()){
			m_strOldName = mid.getDisplay_name();
			m_etUserName.setText(m_strOldName);
		}
		
		// 性别
		if(null != mid.getGender() && !mid.getGender().isEmpty()){
			m_strOldGender = mid.getGender();
			if(mid.getGender().equals(HeadhunterPublic.MYACCOUNT_GENDER_MALE)){//男
				m_rbtnMale.setChecked(true);
				m_rbtnFamale.setChecked(false);
			}else if(mid.getGender().equals(HeadhunterPublic.MYACCOUNT_GENDER_FEMALE)){//女
				m_rbtnFamale.setChecked(true);
				m_rbtnMale.setChecked(false);
			}else{
				m_rbtnFamale.setChecked(false);
				m_rbtnMale.setChecked(false);
			}
		}
		
		// 账号余额
		if(null != mid.getBonussum() && !mid.getBonussum().isEmpty()){
			m_tvMyBalance.setText(String.format(getString(R.string.str_myaccount_mybalance),
					mid.getBonussum()));
		}
	
		// 手机号
//		if(null != mid.getPhone() && !mid.getPhone().isEmpty()){
//			m_strOldPhone = mid.getPhone();
//			m_etPhone.setText(m_strOldPhone);
//		}else{
//			m_bPhoneChange = true;
//		}
		
		// 判断手机号是否认证
//		if(null != mid.getPhone_verified()){
//			if(mid.getPhone_verified().equals(ReqUserInfo.PHONE_VERIFIED)){
//				m_tvPhoneBinding.setTextColor(m_Context.getResources().getColor(R.color.green));
//				m_tvPhoneBinding.setText(getString(R.string.str_myaccount_binding));
//			}else{
//				m_tvPhoneBinding.setTextColor(m_Context.getResources().getColor(R.color.red));
//				m_tvPhoneBinding.setText(getString(R.string.str_myaccount_nobinding));
//			}
//		}
	}
	
	/**
	 * 头像照片选择对话框
	 */
	private void showSetPhotoDialog() {
		final AlertDialog imageDialog = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT)
				.setTitle("上传头像")
				.setIcon(android.R.drawable.btn_star)
				.setItems(new String[] { "本地图片", "拍照" },
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								// 相册选图
								if (item == 0) {

									doPickPhotoFromGallery();
								}
								// 手机拍照
								else if (item == 1) {
									doTakePhoto();
								}
		
								// imageDialog.dismiss();
							}
						}).create();
		imageDialog.setCanceledOnTouchOutside(true);
		imageDialog.show();
	}
	
	/**
	 * 进入相册
	 */
	protected void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {

		}
	}
	
	/**
	 * @return
	 */
	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", ICON_SIZE);
		intent.putExtra("outputY", ICON_SIZE);
		intent.putExtra("return-data", true);
		return intent;
	}
	
	/**
	 * 启动照相机
	 */
	protected void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			ResumeEditHomeActivity.PHOTO_DIR.mkdirs();
			mCurrentPhotoFile = new File(ResumeEditHomeActivity.PHOTO_DIR, getPhotoFileName());
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
		}
	}
	
	/**
	 * 获取照片的名称
	 * @return
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
	
	/**
	 * @param f
	 * @return
	 */
	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}
	
	/**
	 * 
	 * @param f
	 */
	protected void doCropPhoto(File f) {
		try {
			// Add the image to the media store
			MediaScannerConnection.scanFile(this,
					new String[] { f.getAbsolutePath() },
					new String[] { null }, null);

			// Launch gallery to crop the photo
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (Exception e) {

		}
	}
	
	/**
	 * 
	 * @param photoUri
	 * @return
	 */
	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", ICON_SIZE);
		intent.putExtra("outputY", ICON_SIZE);
		intent.putExtra("return-data", true);
		return intent;
	}
	
	/**
	 * 修改头像后更新控件上的头像
	 */
	private void updataHeadsculptur(){
		if (!UIHelper.isNetworkConnected(m_appContext)) {
			return;
		}
		
		showProgressDialog();
		
		new Thread() {
			public void run() {
				uploadHeadsculptur();
			}
		}.start();
	}
	
	/**
	 * 上传头像
	 */
	private void uploadHeadsculptur(){
		String filename = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/"
				+ "headhphoto_"
				+ System.currentTimeMillis() + ".jpg";

		// 压缩图片
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(
					filename);
			int totalbytes = m_bmpHeadsculptur.getRowBytes()*m_bmpHeadsculptur.getHeight();
			byte[] bytes = ImageUtils.getBitmapByte(m_bmpHeadsculptur);
			int quality = 100;
			if(totalbytes > 50*1024){
				quality = (50*1024)/totalbytes;
			}
			m_bmpHeadsculptur.compress(Bitmap.CompressFormat.JPEG, quality,
					fileOutputStream);
			fileOutputStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL));
			
			return;
		}
		
		String url = URLs.RESUME_PICTURE_SUBMIT;
		if (url == null || url.length() == 0) {
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL));
			
			return;
		}
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("pic_type", "JPEG");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL));
			return;
		}
		
		Map<String, Object> params = AppContext.getHttpPostParams(m_appContext, obj.toString());
		Map<String, File> files = new HashMap<String, File>();
		File photofile = new File(filename);
		long filesize = photofile.length();
		files.put("uploadfile", photofile);
		
		String strTemp = "";
		try {
			strTemp = parseUploadHeadsculptur(m_appContext, 
					ApiClient._post(m_appContext, url, params, files));
			
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_SUCCESS, strTemp));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL));
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL));
		}
	}
	
	/**
	 * @param appContext
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	private static String parseUploadHeadsculptur(AppContext appContext,
			InputStream inputStream) throws IOException, AppException {
		String strTemp = "";
		
		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}
		
		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");
			Result res = new Result(status, statusmsg);

			if (res.OK()) {
				strTemp = jsonObj.getString("data");
				strTemp = URLs.RESUME_PICTURE_BASESUBMIT + strTemp;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return strTemp;
	}
	
	/**
	 * 
	 */
	private Handler m_Handler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what){
			case HeadhunterPublic.MSG_MYACCOUN_GETTDATA_NONETWORKCONNECT:
				{
					// 从缓存中读取数据
					MyAccontInfoData mid = MyAccontInfoData.
							getMyAccontInfoData(m_appContext, "");
					if(null != mid){
						updataControl(mid);
					}
				}
				break;
			case HeadhunterPublic.MSG_MYACCOUN_GETTDATA_SUCCESS:	// 获取我的账号相关信息成功
				{
					dismissProgressDialog();
					
					MyAccontInfoData mid = (MyAccontInfoData)msg.obj;
					if(null != mid){
						// 保存我的账号信息到数据库中
						MyAccontInfoData.saveMyAccontInfoData(m_appContext, mid);
						// 更新界面上的控件
						updataControl(mid);
					}
				}
				break;
			case HeadhunterPublic.MSG_MYACCOUN_GETTDATA_FAIL:		//  获取我的账号相关信息失败
				{
					dismissProgressDialog();
					
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_myaccount_getrecord_fail));
				}
				break;
			case HeadhunterPublic.MSG_MYACCOUN_GETTDATA_ABNORMAL:	// 获取我的账号相关信息异常
				{
					dismissProgressDialog();
					
					((AppException)msg.obj).makeToast(m_Context);
				}
				break;
			case HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL:	// 上传个人头像失败
				{
					dismissProgressDialog();
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_myaccount_uploadheadsulptur_fail));
				}
				break;
			case HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_SUCCESS:	// 上传个人头像成功
				{
					dismissProgressDialog();
					
					String strUrl = (String)msg.obj;
					if(strUrl != null && !strUrl.isEmpty()){
						m_strHeadsculpturUrl = strUrl;
						refreshHeadSculpture(m_strHeadsculpturUrl);
						
						// 显示编辑需要的相关控件
						showEditControl();
					}
				}
				break;
			case HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_SUCCESS:	// 提交个人资料成功(主要是个人头像)
				{
					dismissProgressDialog();
					
					// 保存我的账号信息到数据库中
					if(null != m_MAPP){
						if(null == m_maid){
							m_maid = new MyAccontInfoData();
						}
						
						// 头像
						if(null != m_MAPP.getAvatar() && !m_MAPP.getAvatar().isEmpty()){
							m_maid.setAvatar(m_MAPP.getAvatar());
						}
						
						// 昵称
						if(null != m_MAPP.getDisplay_name() && !m_MAPP.getDisplay_name().isEmpty()){
							m_maid.setDisplay_name(m_MAPP.getDisplay_name());
							m_maid.setUsername(m_MAPP.getDisplay_name());
						}
						
						// 性别
						if(null != m_MAPP.getGender() && !m_MAPP.getGender().isEmpty()){
							m_maid.setGender(m_MAPP.getGender());
						}
						
						MyAccontInfoData.saveMyAccontInfoData(m_appContext, m_maid);
					}
					
					// 
					switch(m_nType){
					case HeadhunterPublic.MYACCOUNT_TYPE_FIRST:
						{
							//
							UIHelper.ToastMessage(m_Context, 
									getString(R.string.msg_myaccount_sumitformation_success));
							
							setResult(HeadhunterPublic.RESULT_MYACCOUNT_SUBMITSUCCESS);
							finish();
						}
						break;
					case HeadhunterPublic.MYACCOUNT_TYPE_DEFAULT:
					default:
						{
							UIHelper.ToastMessage(m_Context, 
									getString(R.string.msg_myaccount_sumitformation_success));
							
							// 
							//m_bPhoneChange = false;
							m_bNameChange  = false;
							
							// 保存修改后的手机号
							// m_strOldPhone = m_etPhone.getText().toString();
							// 保存修改后的姓名
							m_strOldName = m_etUserName.getText().toString();
							
							// 隐藏提交按钮
							showControl(false);
							
							finish();
						}
						break;
					}
				}
				break;

			case HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_FAIL:		// 提交个人资料失败(主要是个人头像)
			case HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_ABNORMAL:	// 提交个人资料异常(主要是个人头像)
				{
					dismissProgressDialog();
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_myaccount_sumitformation_fail));
				}
				break;
			case HeadhunterPublic.MSG_GETAUTHCODE_SUCCESS:	// 获取验证码成功
				{
					dismissProgressDialog();
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.mobile_auth_getcode_success));
				}
				break;
			case HeadhunterPublic.MSG_GETAUTHCODE_FAIL_1949:	// 获取验证码失败(手机号被占用)
				{
					dismissProgressDialog();
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.mobile_auth_getcode_1949));
				}
				break;
			case HeadhunterPublic.MSG_GETAUTHCODE_FAIL:		// 获取验证码失败
			case HeadhunterPublic.MSG_GETAUTHCODE_ABNORMAL:	// 获取验证码异常
				{
					dismissProgressDialog();
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.mobile_auth_getcode_fail));
				}
				break;
			case HeadhunterPublic.MSG_VERIFYPHONE_SUCCESS:	// 绑定手机成功
				{
					dismissProgressDialog();
					
					// 提交
					Sumit();
				}
				break;
			case HeadhunterPublic.MSG_VERIFYPHONE_FAIL:		// 绑定手机失败
			case HeadhunterPublic.MSG_VERIFYPHONE_ABNORMAL:	// 绑定手机异常
				{
					dismissProgressDialog();
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_myaccount_verifyphone_fail));
				}
				break;
			}
		}

	};
	
	/**
	 * 提交个人资料(主要是个人头像)
	 */
	private void submitPersonalProfile(){
		if(null == m_MAPP){
			m_MAPP = new MyAccountPersonalProfile();
		}
		
		// 设置头像URL
		if(null != m_strHeadsculpturUrl && !m_strHeadsculpturUrl.isEmpty()){
			m_MAPP.setAvatar(m_strHeadsculpturUrl);
		}
		
		// 设置用户名
		if(null != m_etUserName.getText().toString() &&
				!m_etUserName.getText().toString().isEmpty()){
			m_MAPP.setDisplay_name(m_etUserName.getText().toString());
		}
		
		// 设置性别
		if(null != m_rbtnMale){
			if(m_rbtnMale.isChecked()){// 男
				m_MAPP.setGender(HeadhunterPublic.MYACCOUNT_GENDER_MALE);
			}else{// 女
				m_MAPP.setGender(HeadhunterPublic.MYACCOUNT_GENDER_FEMALE);
			}
		}
		
		try {
			Result res = m_appContext.submitPersonalProfile(m_MAPP);

	        if( res.OK()){			
	        	// 成功
	        	m_Handler.sendMessage(m_Handler.obtainMessage(
						HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_SUCCESS, res));
	        }else{
				// 失败
	        	m_Handler.sendMessage(m_Handler.obtainMessage(
						HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_FAIL, res.getErrorMessage()));
	        }
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 异常
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_ABNORMAL, e));
		}
		
	}
	
	/**
	 * 开始获取我的账号信息
	 */
	private void startGetMyAccountInfo(){
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_MYACCOUN_GETTDATA_NONETWORKCONNECT));
			return;
		}
		
		showProgressDialog();
		
		new Thread() {
			public void run() {
				getMyAccountInfo();
			}
		}.start();
	}
	
	/**
	 * 获取我的账号相关信息
	 */
	private void getMyAccountInfo(){
		try {
			// 获取我的账号相关信息
			MyAccontInfoDataEntity uide = m_appContext.getMyAccountInfoData();

			Result res = uide.getValidate();
			
			if (res.OK()) {
				// 成功
				m_Handler.sendMessage(m_Handler.obtainMessage(
						HeadhunterPublic.MSG_MYACCOUN_GETTDATA_SUCCESS, uide.getData()));
			} else {
					// 失败
				m_Handler.sendMessage(m_Handler.obtainMessage(
							HeadhunterPublic.MSG_MYACCOUN_GETTDATA_FAIL, res.getErrorMessage()));
			}
		} catch (AppException e) {
			e.printStackTrace();
			// 异常
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_MYACCOUN_GETTDATA_ABNORMAL, e));
		}		
	}
	
	/**
	 * 开始获取验证码
	 */
//	private void startGetAuthCode(){
////		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
////			m_Handler.sendMessage(m_Handler.obtainMessage(
////					HeadhunterPublic.MSG_GETAUTHCODE_NONETWORKCONNECT));
////			return;
////		}
//		if (!UIHelper.isNetworkConnected(m_appContext)) {
//			return;
//		}
//		
//		showProgressDialog();
//		
//		new Thread() {
//			public void run() {
//				getAuthCode();
//			}
//		}.start();
//	}
	
	/**
	 * 获取验证码
	 */
//	private void getAuthCode(){
//		try {
//			Result res = ApiClient.verifyMobile((AppContext)getApplicationContext(),
//					m_etPhone.getText().toString());
//			if(res.OK()){
//				// 成功
//				m_Handler.sendMessage(m_Handler.obtainMessage(
//							HeadhunterPublic.MSG_GETAUTHCODE_SUCCESS));
//			}else{
//				if(1949 == res.getErrorCode()){
//					// 手机号被占用
//					m_Handler.sendMessage(m_Handler.obtainMessage(
//								HeadhunterPublic.MSG_GETAUTHCODE_FAIL_1949, res.getErrorMessage()));
//				}else{
//					// 失败
//					m_Handler.sendMessage(m_Handler.obtainMessage(
//								HeadhunterPublic.MSG_GETAUTHCODE_FAIL, res.getErrorMessage()));
//				}
//			}
//			
//		} catch (AppException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			// 异常
//			m_Handler.sendMessage(m_Handler.obtainMessage(
//					HeadhunterPublic.MSG_GETAUTHCODE_ABNORMAL, e));
//		}
//	}
//	
	/**
	 * 开始绑定手机
	 */
//	private void startVerifyPhone(){
////		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
////			m_Handler.sendMessage(m_Handler.obtainMessage(
////					HeadhunterPublic.MSG_VERIFYPHONE_NONETWORKCONNECT));
////			return;
////		}
//		if (!UIHelper.isNetworkConnected(m_appContext)) {
//			return;
//		}
//		
//		showProgressDialog();
//		
//		new Thread() {
//			public void run() {
//				verifyPhone();
//			}
//		}.start();
//	}
	
	/**
	 * 绑定手机
	 */
//	private void verifyPhone(){
//		try {
//			Result res = ApiClient.verifyMobileCode((AppContext)getApplicationContext(),
//					m_etAuthcode.getText().toString());
//			if(res.OK()){
//				// 成功
//				m_Handler.sendMessage(m_Handler.obtainMessage(
//							HeadhunterPublic.MSG_VERIFYPHONE_SUCCESS));
//			}else{
//				// 失败
//				m_Handler.sendMessage(m_Handler.obtainMessage(
//							HeadhunterPublic.MSG_VERIFYPHONE_FAIL, res.getErrorMessage()));
//			}
//			
//		} catch (AppException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			// 异常
//			m_Handler.sendMessage(m_Handler.obtainMessage(
//					HeadhunterPublic.MSG_VERIFYPHONE_ABNORMAL, e));
//		}
//	}
	
	/**
	 * 判断控件上的值
	 * @return
	 */
	private boolean checkControlValue(){
		if( m_bNameChange && null != m_etUserName){
			// 判断姓名是否为空
			if(null == m_etUserName.getText().toString() ||
					m_etUserName.getText().toString().isEmpty()){
				UIHelper.ToastMessage(m_Context, 
						getString(R.string.msg_myaccount_nameisnull));
				return false;
			}
		}
		
//		if(m_bPhoneChange){
//			// 检查手机号是否合法
//			if(!checkPhone()){
//				return false;
//			}
//			
//			if( null != m_etAuthcode){
//				// 验证码是否为空
//				if(null == m_etAuthcode.getText().toString() ||
//						m_etAuthcode.getText().toString().isEmpty()){
//					UIHelper.ToastMessage(m_Context, 
//							getString(R.string.msg_myaccount_authcodeisnull));
//					return false;
//				}
//			}
//		}
		
		return true;
	}
	
	/**
	 * 检查手机号是否合法
	 * @return
	 */
//	private boolean checkPhone(){
//		if( null != m_etPhone){
//			// 手机号是否为空
//			if(null == m_etPhone.getText().toString() ||
//					m_etPhone.getText().toString().isEmpty()){
//				UIHelper.ToastMessage(m_Context, 
//						getString(R.string.msg_myaccount_phoneisnull));
//				return false;
//			}else if(!Util.checkMobileFormate(m_etPhone.getText().toString())){// 判断手机号是否合法
//				UIHelper.ToastMessage(m_Context, 
//						getString(R.string.msg_myaccount_phoneerror));
//				return false;
//			}else if(!m_etPhone.getText().toString().equals(m_strOldPhone)){
//				m_bVerifyPhone = true;
//			}
//		}else{
//			return false;
//		}
//		
//		return true;
//	}
	
	private void back(){
		switch(m_nType){
		case HeadhunterPublic.MYACCOUNT_TYPE_FIRST:
			{
				setResult(HeadhunterPublic.RESULT_MYACCOUNT_CANCEL);
				finish();
			}
			break;
		case HeadhunterPublic.MYACCOUNT_TYPE_DEFAULT:
		default:
			{
				finish();
			}
			break;
		}
	}
}

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

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.MyAccountPersonalProfile;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.MyAccontInfoData;
import com.qianniu.zhaopin.app.bean.MyAccontInfoDataEntity;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.thp.UmShare;
import com.qianniu.zhaopin.thp.ui.AlipayAccountBindingActivity;

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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyBalanceActivity extends BaseActivity{
	private Context m_Context;
	private AppContext m_appContext;
	
	private TextView m_tvQianniuintegral;			// 积分规则文本框
	private ImageView m_btnBack;					// 返回按钮
	private Button m_btnWithdrawals;				// 提现按钮
	private RelativeLayout m_rlAliplayaccount;  	// 支付宝绑定
	private RelativeLayout m_rlTransactionrecords; 	// 交易记录
	
	// 头像相关
//	private ImageView m_imgComplete;
//	private ImageView m_imgPhoto;
	
	private TextView m_tvAccountbalance;			// 账号余额
	private TextView m_tvIntegral;					// 积分余额
	private TextView m_tvQianniuaccount;			// 牵牛账号
	private TextView m_tvAuthentication;			// 是否认证
	private TextView m_tvAliplayaccount;			// 支付宝账号
	private TextView m_tvBinding;					// 支付宝账号是否绑定
	private TextView m_tvAccumulatedincome;			// 累计收入
	private TextView m_tvTotalexpenditure;			// 累计支出
	private TextView m_tvTransactionrecords;		// 交易记录条数

	private String m_strAccountbalance;				// 账号余额
	private String m_strIntegral;					// 积分余额
	private String m_strQianniuaccount;				// 牵牛账号
	private String m_strAuthentication;				// 是否认证
	private String m_strAliplayaccount;				// 支付宝账号
	private String m_strBinding;					// 支付宝账号是否绑定
	private String m_strAccumulatedincome;			// 累计收入
	private String m_strTotalexpenditure;			// 累计支出
	private String m_strTransactionrecords;			// 交易记录条数
	
	private EditText m_etSecurePwd;					// 安全密码输入框
	private String m_strSecurePwd;					// 安全密码
	
	private DialogInterface m_dialog;
	
	private WebView m_wvAlipayPage;
	
//	private Bitmap m_bmpHeadsculptur;
//	private String m_strHeadsculpturUrl = "";		// 个人头像url
	
	private MyAccountPersonalProfile m_MAPP;		// 个人资料
	
	/**
	 * 照相机拍摄照片转化为该File对象
	 */
//	private File mCurrentPhotoFile;
	
	/** 通过图库获取图片 */
//	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	/** 照相机拍照获取图片 */
//	private static final int CAMERA_WITH_DATA = 3022;
//	private static final int ICON_SIZE = 96;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mybalanceactivity);

		m_Context = this;
		m_appContext = (AppContext) getApplication();

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
		case HeadhunterPublic.RESULT_ACTIVITY_CODE:
			{
				switch(resultCode){
				case HeadhunterPublic.RESULT_ALIPAYACCOUNT_CHANGE:		// 绑定支付宝账号成功
					{
						// 获取Activity传递过来的数据
						Bundle bundle = data.getExtras();
						if(null != bundle){
							// 支付宝账号
							String strAccout = bundle.getString(
									AlipayAccountBindingActivity.ALIPAY_ACCOUNT);
							
							if(null != strAccout && !strAccout.isEmpty()){
								m_tvAliplayaccount.setText(strAccout);

								m_tvBinding.setTextColor(m_Context.getResources().getColor(R.color.green));
								m_tvBinding.setText(getString(R.string.str_mybalance_aliplayaccount_binding));
							}
						}
					}
					break;
				default:
					break;
				}				
			}
			break;
//		case CAMERA_WITH_DATA:
//			{
//				doCropPhoto(mCurrentPhotoFile);
//			}
//			break;
//		case PHOTO_PICKED_WITH_DATA:
//			{
//				if(null != data){
//					m_bmpHeadsculptur = data.getParcelableExtra("data");
//					if(null != m_bmpHeadsculptur){
//						updataHeadsculptur();
//					}					
//				}
//			}
//			break;
		}
	}
	
	/**
	 * 
	 */
	private void initData(){
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
	 * 
	 */
	private void initTextView(){
		// 账号余额
		m_tvAccountbalance = (TextView)findViewById(R.id.mybalance_tv_accountbalance);
		if(null != m_tvAccountbalance && null != m_strAccountbalance
				&& !m_strAccountbalance.isEmpty()){
			m_tvAccountbalance.setText(m_strAccountbalance);
		}
		
		// 积分余额
		m_tvIntegral = (TextView)findViewById(R.id.mybalance_tv_integral);
		if(null != m_tvIntegral && null != m_strIntegral
				&& !m_strIntegral.isEmpty()){
			m_tvIntegral.setText(m_strIntegral);
		}
		
		// 牵牛账号
		m_tvQianniuaccount = (TextView)findViewById(R.id.mybalance_tv_qianniuaccount);
		if(null != m_tvQianniuaccount && null != m_strQianniuaccount
				&& !m_strQianniuaccount.isEmpty()){
			m_tvQianniuaccount.setText(m_strQianniuaccount);
		}
		
		// 是否认证
		m_tvAuthentication = (TextView)findViewById(R.id.mybalance_tv_qianniuaccount_authentication);
		if(null != m_tvAuthentication){
			if(null != m_strAuthentication && !m_strAuthentication.isEmpty()){
				// 已认证
				m_tvAuthentication.setTextColor(
						m_Context.getResources().getColor(R.color.green));
				m_tvAuthentication.setText(getString(
						R.string.str_mybalance_aliplayaccount_authentication));
			}else{
				// 未认证
				m_tvAuthentication.setTextColor(
						m_Context.getResources().getColor(R.color.red));
				m_tvAuthentication.setText(getString(
						R.string.str_mybalance_aliplayaccount_noauthentication));
			}
		}
		
		// 支付宝账号
		m_tvAliplayaccount = (TextView)findViewById(R.id.mybalance_tv_aliplayaccount);
		if(null != m_tvAliplayaccount && null != m_strAliplayaccount
				&& !m_strAliplayaccount.isEmpty()){
			m_tvAliplayaccount.setText(m_strAliplayaccount);
		}
		
		// 支付宝账号是否绑定
		m_tvBinding = (TextView)findViewById(R.id.mybalance_tv_aliplayaccount_binding);
		if(null != m_tvBinding){
			if(null != m_strBinding && !m_strBinding.isEmpty()){
				// 支付宝账号已绑定
				m_tvBinding.setTextColor(m_Context.getResources().getColor(R.color.green));
				m_tvBinding.setText(getString(R.string.str_mybalance_aliplayaccount_binding));				
			}else{
				// 支付宝账号未绑定
				m_tvBinding.setTextColor(m_Context.getResources().getColor(R.color.red));
				m_tvBinding.setText(getString(R.string.str_mybalance_aliplayaccount_nobinding));				
			}
		}
		
		// 累计收入
		m_tvAccumulatedincome = (TextView)findViewById(R.id.mybalance_tv_accumulatedincome);
		if(null != m_tvAccumulatedincome && null != m_strAccumulatedincome
				&& !m_strAccumulatedincome.isEmpty()){
			m_tvAccumulatedincome.setText(m_strAccumulatedincome);
		}
		
		// 累计支出
		m_tvTotalexpenditure = (TextView)findViewById(R.id.mybalance_tv_totalexpenditure);
		if(null != m_tvTotalexpenditure && null != m_strTotalexpenditure
				&& !m_strTotalexpenditure.isEmpty()){
			m_tvTotalexpenditure.setText(m_strTotalexpenditure);
		}	
		
		// 交易记录条数
		m_tvTransactionrecords = (TextView)findViewById(R.id.mybalance_tv_transactionrecords);
		if(null != m_tvTransactionrecords && null != m_strTransactionrecords
				&& !m_strTransactionrecords.isEmpty()){
			m_tvTransactionrecords.setText(String.format(getString(
					R.string.str_mybalance_transactionrecordsnum), m_strTransactionrecords));
		}		
	}
	
//	private void initHeadSculpture(){
//		m_imgComplete = (ImageView)findViewById(R.id.complete_imgview);
//		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(
//				this, 0,
//				Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
//				Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
//				Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
//				Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
//		m_imgComplete.setImageBitmap(completebmp);
//
//		m_imgPhoto = (ImageView) findViewById(R.id.headphoto_imgview);
//		Bitmap photobmp = BitmapFactory.decodeResource(getResources(),
//				R.drawable.person_photo_normal_big);
//		photobmp = ImageUtils.createRoundHeadPhoto(this,
//				photobmp);
//		m_imgPhoto.setImageBitmap(photobmp);
//		m_imgPhoto.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				// 友盟统计--我的账号--上传头像按钮
//				UmShare.UmStatistics(m_Context, "MyAccount_UploadHeadButton");
//				
//				showSetPhotoDialog();
//			}
//		});
//	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		initTextView();
//		initHeadSculpture();
		
		// 积分规则
		m_tvQianniuintegral = (TextView)findViewById(R.id.mybalance_tv_qianniuintegral);
		m_tvQianniuintegral.setText(Html.fromHtml("<u>"+getString(R.string.str_qianniuintegral_title)+"</u>"));
		m_tvQianniuintegral.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTipsWindows(v);
			}
		});
		
		// 返回按钮
		m_btnBack = (ImageView)findViewById(R.id.mybalance_btn_back);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 提现按钮
		m_btnWithdrawals = (Button)findViewById(R.id.mybalance_btn_withdrawals);
		m_btnWithdrawals.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--我的账号--提现按钮
				UmShare.UmStatistics(m_Context, "MyAccount_WithdrawalsButton");
				
				// 进入支付宝网页界面
				showAlipayPage();
			}
		});
		
		// 支付宝绑定
		m_rlAliplayaccount = (RelativeLayout)findViewById(R.id.mybalance_rl_aliplayaccount);
		m_rlAliplayaccount.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--我的账号--支付宝绑定按钮
				UmShare.UmStatistics(m_Context, "MyAccount_AliplayaccounButton");
				
//				showSecurePopupWindow(v);
				doBinding();
			}
		});
		
		// 交易记录条数
		m_rlTransactionrecords = (RelativeLayout)findViewById(R.id.mybalance_rl_transactionrecords);
		m_rlTransactionrecords.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--我的账号--查询交易记录按钮
				UmShare.UmStatistics(m_Context, "MyAccount_TransactionrecordsButton");
				
				showTransactionRecords();
			}
		});
		
		m_wvAlipayPage = (WebView)findViewById(R.id.mybalance_webview);
	}
	
	/**
	 * 进入支付宝网页界面
	 */
	private void showAlipayPage(){
		if(null != m_wvAlipayPage){
			m_wvAlipayPage.loadUrl("https://www.alipay.com/");	
		}
	}
	
	/**
	 * 进入交易记录界面
	 */
	private void showTransactionRecords(){
		Intent intent = new Intent(MyBalanceActivity.this, TransactionRecordsActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 弹出积分规则对话框
	 * @param view
	 */
	private void showTipsWindows(View view) {
		UIHelper.showAgreementDialog(MyBalanceActivity.this,
				R.string.str_qianniuintegral_title, R.string.str_qianniuintegral_rule,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
						}
						dialog.dismiss();
					}
				});
	}
	
	/**
	 * 支付宝账号绑定
	 */
	private void doBinding() {
		String strAccount = getAliplayAccount();
		
		Intent intent = new Intent(MyBalanceActivity.this, AlipayAccountBindingActivity.class);
		intent.putExtra(AlipayAccountBindingActivity.ALIPAY_ACCOUNT, strAccount);
		startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 检查安全密码是否正确
	 * @param strSecurePwd
	 */
	private void checkSecurePwd(){
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_SECUREPWD_NONETWORKCONNECT));
			return;
		}
		
		showProgressDialog();
		
		new Thread() {
			public void run() {
				lineCheckSecurePwd(m_strSecurePwd);
			}
		}.start();
	}
	
	/**
	 * 连接服务器检查安全密码是否正确
	 * @param strSecurePwd 安全密码
	 */
	private void lineCheckSecurePwd(String strSecurePwd){
		try{
			
			Result res = m_appContext.checkSecurePwd(strSecurePwd);

			if (res.OK()) {
				if(res.getJsonStr().equals(HeadhunterPublic.SECUREPWD_PWD_TRUE)){
					// 成功
					m_Handler.sendMessage(m_Handler.obtainMessage(
							HeadhunterPublic.MSG_SECUREPWD_SUCCESS));	
				}else{
					// 验证密码错误
					m_Handler.sendMessage(m_Handler.obtainMessage(
							HeadhunterPublic.MSG_SECUREPWD_PWDFALSE));						
				}
			} else {
				// 失败
				m_Handler.sendMessage(m_Handler.obtainMessage(
							HeadhunterPublic.MSG_SECUREPWD_FAIL, res.getErrorMessage()));
			}
		}catch (AppException e) {
			e.printStackTrace();
			// 异常
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_SECUREPWD_ABNORMAL, e));
		}	
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
			case HeadhunterPublic.MSG_SECUREPWD_SUCCESS:	// 安全密码正确
				{
					dismissProgressDialog();
					doBinding();	
				}
				break;
			case HeadhunterPublic.MSG_SECUREPWD_PWDFALSE:	// 安全密码不正确
				{
					dismissProgressDialog();
					
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_securepwd_pwdfalse));
				}
				break;
			case HeadhunterPublic.MSG_SECUREPWD_FAIL:// 验证安全密码失败
				{
					dismissProgressDialog();
					
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_securepwd_fail));
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
			case HeadhunterPublic.MSG_SECUREPWD_ABNORMAL:			// 验证安全密码异常
				{
					dismissProgressDialog();
					
					((AppException)msg.obj).makeToast(m_Context);
				}
				break;
//			case HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL:	// 上传个人头像失败
//			case HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_FAIL:		// 提交个人资料失败(主要是个人头像)
//			case HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_ABNORMAL:	// 提交个人资料异常(主要是个人头像)
//				{
//					dismissProgressDialog();
//					UIHelper.ToastMessage(m_Context, 
//							getString(R.string.msg_myaccount_uploadheadsulptur_fail));
//				}
//				break;
//			case HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_SUCCESS:	// 上传个人头像成功
//				{
//					String strUrl = (String)msg.obj;
//					if(strUrl != null && !strUrl.isEmpty()){
//						m_strHeadsculpturUrl = strUrl;
//						updataPersonalProfile();
//					}
//				}
//				break;
//			case HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_SUCCESS:	// 提交个人资料成功(主要是个人头像)
//				{
//					dismissProgressDialog();
//					refreshHeadSculpture(m_strHeadsculpturUrl);
//				}
//				break;
			}
		}

	};
	
	/**
	 * @return
	 */
	private String getAliplayAccount() {
		return m_tvAliplayaccount.getText().toString();
	}
	
	private void showSecurePopupWindow(View v) {
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.str_mybalance_securepop_title));
		LayoutInflater inflater = (LayoutInflater) m_Context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		dl.setMessage(getString(R.string.str_mybalance_securepop_msg));
		View editView = (View) inflater.inflate(R.layout.alertdialog_prompt_securepwd, null);
		
		m_etSecurePwd = (EditText)editView.findViewById(R.id.alertdialog_prompt_securepwd_et_pwd);
			
		dl.setView(editView);
		dl.setPositiveButton(getString(R.string.dialog_ok),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						m_strSecurePwd = m_etSecurePwd.getText().toString();
						if(m_strSecurePwd.isEmpty()){
							UIHelper.ToastMessage(m_Context, getString(
									R.string.msg_myaccount_securepwd_isempty));
						}else{
							dialog.dismiss();
							
							checkSecurePwd();
						}
					}
			});
		
		dl.setNegativeButton(getString(R.string.dialog_cancel),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
			
			});
		m_dialog = dl.show();
	}
	
	/**
	 * 刷新头像
	 * @param entity
	 */
//	private void refreshHeadSculpture(String strPhotoUrl){
//		// 
//		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(
//				this, 0,
//				Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
//				Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
//				Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
//				Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
//		m_imgComplete.setImageBitmap(completebmp);
//
//		CommonRoundImgCreator creator = new CommonRoundImgCreator(
//				this,
//				Constants.ResumeModule.HEADPHOTO_STAND_RADIUS,
//				Constants.ResumeModule.HEADPHOTO_STAND_BODER_WIDTH,
//				Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);
//
//		Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
//				R.drawable.person_photo_normal_big);
//		
//		BitmapManager bmpManager = new BitmapManager(BitmapFactory.decodeResource(
//				getResources(), R.drawable.widget_dface_loading));
//		bmpManager.loadBitmap(strPhotoUrl, m_imgPhoto,
//				defaultbmp, creator);
//
//	}
	
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
	 * 更新控件上的数据
	 * @param uid
	 */
	private void updataControl(MyAccontInfoData mid){		
		// 账号余额
		if(null != mid.getBonussum() && !mid.getBonussum().isEmpty()){
			m_tvAccountbalance.setText(mid.getBonussum());
		}
		
		// 积分余额
		if(null != mid.getCredit() && !mid.getCredit().isEmpty()){
			m_tvIntegral.setText(mid.getCredit());
		}
		
		// 牵牛账号
		if(null != mid.getUsername() && !mid.getUsername().isEmpty()){
			m_tvQianniuaccount.setText(mid.getUsername());
		}
		
		// 牵牛账号是否认证
		boolean isAuthentication = false;
		// 判断手机号是否认证
		if(null != mid.getPhone_verified()){
			if(mid.getPhone_verified().equals(ReqUserInfo.PHONE_VERIFIED)){
				isAuthentication = true;
			}
		}
		// 判断Mail是否认证
		if(!isAuthentication){
			if(null != mid.getEmail_verified()){
				if(mid.getEmail_verified().equals(ReqUserInfo.EMAIL_VERIFIED)){
					isAuthentication = true;
				}
			}			
		}
		
		if(isAuthentication){
			//m_tvAuthentication.setTextColor(Color.rgb(116, 194, 86));
			m_tvAuthentication.setTextColor(m_Context.getResources().getColor(R.color.green));
			m_tvAuthentication.setText(getString(R.string.str_mybalance_aliplayaccount_authentication));
		}else{
			m_tvAuthentication.setTextColor(m_Context.getResources().getColor(R.color.red));
			m_tvAuthentication.setText(getString(R.string.str_mybalance_aliplayaccount_noauthentication));			
		}
		
		// 支付宝账号
		if(null != mid.getAlipay_acc() && !mid.getAlipay_acc().isEmpty()){
			m_tvAliplayaccount.setText(mid.getAlipay_acc());
			
			// 支付宝账号已绑定
			m_tvBinding.setTextColor(m_Context.getResources().getColor(R.color.green));
			m_tvBinding.setText(getString(R.string.str_mybalance_aliplayaccount_binding));
		}else{
			// 支付宝账号未绑定
			m_tvBinding.setTextColor(m_Context.getResources().getColor(R.color.red));
			m_tvBinding.setText(getString(R.string.str_mybalance_aliplayaccount_nobinding));			
		}
		
		// 累计收入
		if(null != mid.getBonussum_get() && !mid.getBonussum_get().isEmpty()){
			m_tvAccumulatedincome.setText(mid.getBonussum_get());
		}
		
		// 累计支出
		if(null != mid.getBonussum_pay() && !mid.getBonussum_pay().isEmpty()){
			m_tvTotalexpenditure.setText(mid.getBonussum_pay());
		}
		
		// 交易记录条数
		String strTemp = "";
		if(null != mid.getBonus_count() && !mid.getBonus_count().isEmpty()){
			strTemp = String.format(getString(
					R.string.str_mybalance_transactionrecordsnum), mid.getBonus_count());
		}else{
			strTemp = String.format(getString(
					R.string.str_mybalance_transactionrecordsnum), "0");			
		}
		m_tvTransactionrecords.setText(strTemp);
		
		if(null == m_MAPP){
			m_MAPP = new MyAccountPersonalProfile();
		}
		
		// 姓名
		if(null != mid.getDisplay_name() && !mid.getDisplay_name().isEmpty()){
			m_MAPP.setDisplay_name(mid.getDisplay_name());
		}
		
		// 个人头像
		if(null != mid.getAvatar() && !mid.getAvatar().isEmpty()){
			m_MAPP.setAvatar(mid.getAvatar());
//			refreshHeadSculpture(mid.getAvatar());
		}
		
		// 性别
		if(null != mid.getGender() && !mid.getGender().isEmpty()){
			m_MAPP.setGender(mid.getGender());
		}
		
		// 生日
		if(null != mid.getBirthday() && !mid.getBirthday().isEmpty()){
			m_MAPP.setBirthday(mid.getBirthday());
		}
		
		// 居住地ID
		if(null != mid.getM104_id() && !mid.getM104_id().isEmpty()){
			m_MAPP.setM104_id(mid.getM104_id());
		}
		
		// 学历ID
		if(null != mid.getM105_id() && !mid.getM105_id().isEmpty()){
			m_MAPP.setM105_id(mid.getM105_id());
		}
		
		// 自我描述
		if(null != mid.getMemo() && !mid.getMemo().isEmpty()){
			m_MAPP.setMemo(mid.getMemo());
		}
	}
	
//	private void showSetPhotoDialog() {
//		final AlertDialog imageDialog = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT)
//				.setTitle("上传头像")
//				.setIcon(android.R.drawable.btn_star)
//				.setItems(new String[] { "本地图片", "拍照" },
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int item) {
//								// 相册选图
//								if (item == 0) {
//
//									doPickPhotoFromGallery();
//								}
//								// 手机拍照
//								else if (item == 1) {
//									doTakePhoto();
//								}
//								// imageDialog.dismiss();
//							}
//						}).create();
//		imageDialog.setCanceledOnTouchOutside(true);
//		imageDialog.show();
//	}
	
//	protected void doTakePhoto() {
//		try {
//			// Launch camera to take photo for selected contact
//			ResumeEditHomeActivity.PHOTO_DIR.mkdirs();
//			mCurrentPhotoFile = new File(ResumeEditHomeActivity.PHOTO_DIR, getPhotoFileName());
//			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
//			startActivityForResult(intent, CAMERA_WITH_DATA);
//		} catch (ActivityNotFoundException e) {
//		}
//	}
	
//	private String getPhotoFileName() {
//		Date date = new Date(System.currentTimeMillis());
//		SimpleDateFormat dateFormat = new SimpleDateFormat(
//				"'IMG'_yyyyMMdd_HHmmss");
//		return dateFormat.format(date) + ".jpg";
//	}
//	
//	public static Intent getTakePickIntent(File f) {
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//		return intent;
//	}
//	
//	protected void doPickPhotoFromGallery() {
//		try {
//			// Launch picker to choose photo for selected contact
//			final Intent intent = getPhotoPickIntent();
//			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
//		} catch (ActivityNotFoundException e) {
//
//		}
//	}
//	
//	public static Intent getPhotoPickIntent() {
//		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//		intent.setType("image/*");
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
//		intent.putExtra("outputX", ICON_SIZE);
//		intent.putExtra("outputY", ICON_SIZE);
//		intent.putExtra("return-data", true);
//		return intent;
//	}
//	
//	protected void doCropPhoto(File f) {
//		try {
//			// Add the image to the media store
//			MediaScannerConnection.scanFile(this,
//					new String[] { f.getAbsolutePath() },
//					new String[] { null }, null);
//
//			// Launch gallery to crop the photo
//			final Intent intent = getCropImageIntent(Uri.fromFile(f));
//			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
//		} catch (Exception e) {
//
//		}
//	}
//	
//	public static Intent getCropImageIntent(Uri photoUri) {
//		Intent intent = new Intent("com.android.camera.action.CROP");
//		intent.setDataAndType(photoUri, "image/*");
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
//		intent.putExtra("outputX", ICON_SIZE);
//		intent.putExtra("outputY", ICON_SIZE);
//		intent.putExtra("return-data", true);
//		return intent;
//	}
//	
//	private void updataHeadsculptur(){
//		if (!UIHelper.isNetworkConnected(m_appContext)) {
//			return;
//		}
//		
//		showProgressDialog();
//		
//		new Thread() {
//			public void run() {
//				uploadHeadsculptur();
//			}
//		}.start();
//	}
	
	/**
	 * 上传头像
	 */
//	private void uploadHeadsculptur(){
//		String filename = Environment.getExternalStorageDirectory()
//				.getAbsolutePath()
//				+ "/"
//				+ "headhphoto_"
//				+ System.currentTimeMillis() + ".jpg";
//
//		// 压缩图片
//		try {
//			FileOutputStream fileOutputStream = new FileOutputStream(
//					filename);
//			int totalbytes = m_bmpHeadsculptur.getRowBytes()*m_bmpHeadsculptur.getHeight();
//			byte[] bytes = ImageUtils.getBitmapByte(m_bmpHeadsculptur);
//			int quality = 100;
//			if(totalbytes > 50*1024){
//				quality = (50*1024)/totalbytes;
//			}
//			m_bmpHeadsculptur.compress(Bitmap.CompressFormat.JPEG, quality,
//					fileOutputStream);
//			fileOutputStream.close();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			m_Handler.sendMessage(m_Handler.obtainMessage(
//					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL));
//			
//			return;
//		}
//		
//		String url = URLs.RESUME_PICTURE_SUBMIT;
//		if (url == null || url.length() == 0) {
//			m_Handler.sendMessage(m_Handler.obtainMessage(
//					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL));
//			
//			return;
//		}
//		
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("pic_type", "JPEG");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			m_Handler.sendMessage(m_Handler.obtainMessage(
//					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL));
//			return;
//		}
//		
//		Map<String, Object> params = AppContext.getHttpPostParams(m_appContext, obj.toString());
//		Map<String, File> files = new HashMap<String, File>();
//		File photofile = new File(filename);
//		long filesize = photofile.length();
//		files.put("uploadfile", photofile);
//		
//		String strTemp = "";
//		try {
//			strTemp = parseUploadHeadsculptur(m_appContext, 
//					ApiClient._post(m_appContext, url, params, files));
//			
//			m_Handler.sendMessage(m_Handler.obtainMessage(
//					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_SUCCESS, strTemp));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			m_Handler.sendMessage(m_Handler.obtainMessage(
//					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL));
//		} catch (AppException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			m_Handler.sendMessage(m_Handler.obtainMessage(
//					HeadhunterPublic.MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL));
//		}
//	}
	
	/**
	 * @param appContext
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
//	private static String parseUploadHeadsculptur(AppContext appContext,
//			InputStream inputStream) throws IOException, AppException {
//		String strTemp = "";
//		
//		StringBuilder builder = new StringBuilder();
//		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
//				inputStream));
//		for (String s = bufreader.readLine(); s != null; s = bufreader
//				.readLine()) {
//			builder.append(s);
//		}
//		
//		try {
//			JSONObject jsonObj = new JSONObject(builder.toString());
//			int status = jsonObj.getInt("status");
//			String statusmsg = jsonObj.getString("msg");
//			Result res = new Result(status, statusmsg);
//
//			if (res.OK()) {
//				strTemp = jsonObj.getString("data");
//				strTemp = URLs.RESUME_PICTURE_BASESUBMIT + strTemp;
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return strTemp;
//	}
	
	/**
	 * 更新个人资料(主要是个人头像)
	 */
//	private void updataPersonalProfile(){
//		if (!UIHelper.isNetworkConnected(m_appContext)) {
//			return;
//		}
//		
//		new Thread() {
//			public void run() {
//				submitPersonalProfile();
//			}
//		}.start();
//	}
	
	/**
	 * 提交个人资料(主要是个人头像)
	 */
//	private void submitPersonalProfile(){
//		if(m_strHeadsculpturUrl == null || m_strHeadsculpturUrl.isEmpty()){
//			return;
//		}
//		
//		if(null == m_MAPP){
//			m_MAPP = new MyAccountPersonalProfile();
//		}
//		m_MAPP.setAvatar(m_strHeadsculpturUrl);
//		
//		try {
//			Result res = m_appContext.submitPersonalProfile(m_MAPP);
//
//	        if( res.OK()){			
//	        	// 成功
//	        	m_Handler.sendMessage(m_Handler.obtainMessage(
//						HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_SUCCESS, res));
//	        }else{
//				// 失败
//	        	m_Handler.sendMessage(m_Handler.obtainMessage(
//						HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_FAIL, res.getErrorMessage()));
//	        }
//		} catch (AppException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			// 异常
//			m_Handler.sendMessage(m_Handler.obtainMessage(
//					HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_ABNORMAL, e));
//		}
//		
//	}
	
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

		// 友盟统计
		UmShare.UmResume(m_Context);
	}
}

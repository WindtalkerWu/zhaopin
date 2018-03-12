package com.qianniu.zhaopin.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.User;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.thp.UmShare;

/**
 * 登录界面(纯牵牛用户)
 * @author wuzy
 *
 */
public class QianniuLoginActivity extends BaseActivity {
	private Context m_Context;
	private AppContext m_appContext; //
	
	private String m_strAccount; 	// 用户名
	private String m_strPwd; 		// 用户密码
	
	private EditText m_etAccount;	// 用户名输入框
	private EditText m_etPwd;		// 用户密码输入框
	
//	private TextView m_tvRegister;		// 注册
	private TextView m_tvForgetpass;	// 忘记密码
	
	private Button m_btnRegister;		// 注册按钮
	
	private ImageButton m_btnBack;		  // 返回按钮
	private RelativeLayout m_rlLogin;     // 登录按钮
//	private ImageButton m_btnLogin;       // 右上角登录按钮
	
	private ReqUserInfo m_req;
	
	/***************************************************/
	// 找回密码相关
	private String m_strUserName;
	private int m_nType;
	
	/***************************************************/
	private EditText m_etUserName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qianniulogin);

		m_Context = this;
		m_appContext = (AppContext) this.getApplication();
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// 友盟统计
		UmShare.UmPause(m_Context);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 友盟统计
		UmShare.UmResume(m_Context);
		
		// 初始化控件
		initControl();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case HeadhunterPublic.RESULT_ACTIVITY_CODE: {
			// 注册成功
			if (HeadhunterPublic.RESULT_REGISTER_SUCCESS_OK == resultCode) {
				setResult(HeadhunterPublic.RESULT_REGISTER_SUCCESS_OK);
				finish();
			}
		}
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化输入框控件
	 */
	private void initEditText(){
		// 用户名输入框
		m_etAccount = (EditText)findViewById(R.id.qianniulogin_et_username);
		// 用户密码输入框
		m_etPwd = (EditText)findViewById(R.id.qianniulogin_et_password);
		
		// 是否显示登录信息
		AppContext ac = (AppContext) getApplication();
		User user = ac.getLoginInfo();
		if (user == null || !user.isRememberMe())
			return;
		if (!StringUtils.isEmpty(user.getAccount())) {
			m_etAccount.setText(user.getAccount());
			// mAccount.selectAll();
		}
		if (!StringUtils.isEmpty(user.getPwd())) {
			m_etPwd.setText(user.getPwd());
		}
	}
	
	/**
	 * 初始化文本框控件
	 */
	private void initTextView(){
		// 忘记密码
		m_tvForgetpass = (TextView)findViewById(R.id.qianniulogin_tv_forgetpass);
		m_tvForgetpass.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// 友盟统计--登录--忘记密码按钮
				UmShare.UmStatistics(m_Context, "Login_ForgetPassButton");
				
				showFindpwdPopupWindow(v);
			}
		});
		
		// 注册
//		m_tvRegister = (TextView)findViewById(R.id.qianniulogin_tv_register);
//		m_tvRegister.setText(Html.fromHtml("<u>"+getString(
//				R.string.usermanager_login_button_register)+"</u>"));
//		m_tvRegister.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// 友盟统计--登录--注册按钮
//				UmShare.UmStatistics(m_Context, "Login_RegisterButton");
//				
//				// 进入注册界面
//				showUserRegisterActivity();
//			}
//		});
	}
	
	/**
	 * 初始化按钮控件
	 */
	private void initButtonControl(){
		// 返回按钮
		m_btnBack = (ImageButton)findViewById(R.id.qianniulogin_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 登录按钮
		m_rlLogin = (RelativeLayout)findViewById(R.id.qianniulogin_rl_login);
		m_rlLogin.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (CheckInput()) {
					// 友盟统计--登录--登录按钮
					UmShare.UmStatistics(m_Context, "Login_LoginButton");
					
					if (null == m_req) {
						m_req = new ReqUserInfo();
					}

					m_req.setUserName(m_strAccount);
					m_req.setUserPassword(m_strPwd);
					m_req.setThirdPartId("");
					m_req.setThirdPartToken("");
					m_req.setLoginType(HeadhunterPublic.LOGINTYPE_DEFAULT);

					login();
				}
			}
		});
		
		// 右上角登录
//		m_btnLogin = (ImageButton)findViewById(R.id.qianniulogin_btn_login);
//		m_btnLogin.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (CheckInput()) {
//					// 友盟统计--登录--登录按钮
//					UmShare.UmStatistics(m_Context, "Login_LoginButton");
//					
//					if (null == m_req) {
//						m_req = new ReqUserInfo();
//					}
//
//					m_req.setUserName(m_strAccount);
//					m_req.setUserPassword(m_strPwd);
//					m_req.setThirdPartId("");
//					m_req.setThirdPartToken("");
//					m_req.setLoginType(HeadhunterPublic.LOGINTYPE_DEFAULT);
//
//					login();
//				}
//			}
//		});
		
		// 注册
		m_btnRegister = (Button)findViewById(R.id.qianniulogin_btn_register);
		m_btnRegister.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// 友盟统计--登录--注册按钮
				UmShare.UmStatistics(m_Context, "Login_RegisterButton");
				
				// 进入注册界面
				showUserRegisterActivity();
			}
		});
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 初始化输入框控件
		initEditText();
		
		// 初始化文本框控件
		initTextView();
		
		// 初始化按钮控件
		initButtonControl();
		
	}
	
	/**
	 * 检查输入的用户名和密码
	 * @return
	 */
	private boolean CheckInput() {
		// 获取输入信息
		getInputUserInfo();

		// 检查输入户名
		if (StringUtils.isEmpty(m_strAccount)) {
			UIHelper.ToastMessage(this,
					getString(R.string.msg_login_account_null));
			return false;
		}

		// 检查输入的密码
		if (StringUtils.isEmpty(m_strPwd)) {
			UIHelper.ToastMessage(this, getString(R.string.msg_login_pwd_null));
			return false;
		}

		return true;
	}

	/**
	 * 获取输入信息
	 */
	private void getInputUserInfo() {
		m_strAccount = m_etAccount.getText().toString();
		m_strPwd = m_etPwd.getText().toString();
	}
	
	/**
	 * 判断网络是否连接
	 */
	private void login() {
		// 判断网络是否连接
		if (!UIHelper.isNetworkConnected(m_appContext)) {
			return;
		}
		
		showProgressDialog();
		new Thread() {
			public void run() {
				LinkLogin();
			}
		}.start();
	}
	
	/**
	 * 向服务器发出登录请求
	 */
	public void LinkLogin() {
		if (null != m_req) {
			try {
				AppContext ac = (AppContext) getApplication();

				User user = ac.loginVerify(m_req);
				user.setAccount(m_strAccount);
				user.setPwd(m_strPwd);
				user.setRememberMe(true);
				user.setThirdPartId(m_req.getThirdPartId());
				user.setThirdPartToken(m_req.getThirdPartToken());
				user.setLoginType(Integer.toString(m_req.getLoginType()));

				Result res = user.getValidate();
				if (res.OK()) {
					ac.saveLoginInfo(user); // 保存登录信息
					// 成功
					m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.LOGINMSG_SUCCESS, user));
				} else {
					// 失败
					m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.LOGINMSG_FAIL, res.getErrorMessage()));
				}
			} catch (AppException e) {
				e.printStackTrace();
				// 异常
				m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.LOGINMSG_ABNORMAL, e));
			}
		}
	}
	
	private Handler m_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HeadhunterPublic.LOGINMSG_ABNORMAL:
			case HeadhunterPublic.FINDPASSWORDMSG_ABNORMAL: 
				 {
					 dismissProgressDialog();
					 ((AppException)msg.obj).makeToast(QianniuLoginActivity.this);
				 }
				 break;
			case HeadhunterPublic.LOGINMSG_FAIL: {
				dismissProgressDialog();

				UIHelper.ToastMessage(QianniuLoginActivity.this,
						getString(R.string.msg_login_fail));
			}
				break;
			case HeadhunterPublic.LOGINMSG_SUCCESS: {
				dismissProgressDialog();
				User user = (User) msg.obj;
				if (user != null) {
					// 清空原先cookie
					ApiClient.cleanCookie();
					// 发送通知广播
					// UIHelper.sendBroadCast(UserLoginActivity.this,
					// user.getNotice());
					// 提示登陆成功
					UIHelper.ToastMessage(QianniuLoginActivity.this,
							R.string.msg_login_success);

					setResult(HeadhunterPublic.RESULT_QIANNIULOGIN_SUCCESS_OK);
					finish();
				}
			}
				break;
			case HeadhunterPublic.LOGINMSG_THP_SUCCESS: {
				m_req = (ReqUserInfo) msg.obj;
				getInputUserInfo();
				login();
			}
				break;
			case HeadhunterPublic.FINDPWDPOPUPWINDOW_MSG_MAIL: {
				m_strUserName = (String) msg.obj;
				m_nType = 0;
				findPassword();
			}
				break;
			case HeadhunterPublic.FINDPWDPOPUPWINDOW_MSG_MOBILE: {
				m_strUserName = (String) msg.obj;
				m_nType = 1;
				findPassword();
			}
				break;
			case HeadhunterPublic.FINDPASSWORDMSG_SUCCESS: {
				dismissProgressDialog();

				UIHelper.ToastMessage(QianniuLoginActivity.this,
						R.string.msg_findpwd_success);
			}
				break;
			case HeadhunterPublic.FINDPASSWORDMSG_FAIL:
				{
					dismissProgressDialog();
	
					UIHelper.ToastMessage(QianniuLoginActivity.this,
							R.string.msg_findpwd_fail);
				}
				break;
			case HeadhunterPublic.FINDPASSWORDMSG_EMAILORMOBLILEISNULL:
				{// 邮箱或者手机为空
					dismissProgressDialog();
	
//					UIHelper.ToastMessage(UserLoginActivity.this,
//							R.string.msg_findpwd_fail_username);
					Toast.makeText(QianniuLoginActivity.this, R.string.msg_findpwd_fail_username, 
							Toast.LENGTH_LONG).show();
				}
				break;
			case HeadhunterPublic.FINDPASSWORDMSG_EMAILORMOBLILEISNOT:
				{// 没有要找回的账号(邮箱或者手机)
					dismissProgressDialog();
	
	//				UIHelper.ToastMessage(UserLoginActivity.this,
	//						R.string.msg_findpwd_fail_username);
					Toast.makeText(QianniuLoginActivity.this, R.string.msg_findpwd_fail_isnot, 
							Toast.LENGTH_LONG).show();
				}
				break;
			default:
				break;
			}
		}
	};
	

	/**
	 * 手机找回密码
	 */
	private void findPassword() {
		showProgressDialog();
		
		new Thread() {
			public void run() {
				LinkFindPassword();
			}
		}.start();
	}

	/**
	 * 向服务器发出密码修改请求
	 */
	private void LinkFindPassword() {

		try {
			AppContext ac = (AppContext) getApplication();

			User user = ac.findpassword(m_strUserName, m_nType);

			Result res = user.getValidate();
			if (res.OK()) {
				// 成功
				m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.FINDPASSWORDMSG_SUCCESS));
			} else {
				if(HeadhunterPublic.LINK_RESULT_EMAILORMOBLILEISNULL == res.getErrorCode()){
					// 邮箱或手机为空
	            	m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.FINDPASSWORDMSG_EMAILORMOBLILEISNULL, res.getErrorMessage()));
				}else if(HeadhunterPublic.LINK_RESULT_EMAILORMOBLILEISNOT == res.getErrorCode()){
					// 没有要找回的账号(邮箱或者手机)
	            	m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.FINDPASSWORDMSG_EMAILORMOBLILEISNOT, res.getErrorMessage()));
				}else{
					// 失败
	            	m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.FINDPASSWORDMSG_FAIL, res.getErrorMessage()));
				}
			}
		} catch (AppException e) {
			e.printStackTrace();
			// 异常
        	m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.FINDPASSWORDMSG_ABNORMAL, e));
		}
	}
	
	/**
	 * 显示忘记密码界面
	 * 
	 * @param v
	 */
	private void showFindpwdPopupWindow(View v) {
//		if (m_fpw == null) {
//			m_fpw = new FindPwdPromptPopupWindow(m_Context, m_handler);
//		}
//
//		m_fpw.showAtLocation(v, Gravity.TOP, 0, 0);
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.str_findpwdpop_title));
		LayoutInflater inflater = (LayoutInflater) m_Context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View editView = (View) inflater.inflate(R.layout.alertdialog_prompt_findpwd, null);
		m_etUserName = (EditText)editView.findViewById(R.id.alertdialog_prompt_findpwd_et_username);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
        		null != m_etUserName) {
        	m_etUserName.setBackgroundResource(R.drawable.common_search_edittext_bg_default);
        }
		
		ImageView imgClean = (ImageView)editView.findViewById(R.id.alertdialog_prompt_findpwd_iv_usernameclean);
		imgClean.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != m_etUserName){
					m_etUserName.setText("");
				}
			}
			
		});
		
		dl.setView(editView);
		dl.setPositiveButton(getString(R.string.str_findpwdpop_button_mail),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						if(null == m_etUserName){
							return;
						}
						
						m_strUserName = m_etUserName.getText().toString();
						
						// 判断输入不能为空
//						if(null == m_strUserName || m_strUserName.isEmpty()){
//							Toast.makeText(UserLoginActivity.this, R.string.msg_findpwd_mail_username_empty, 
//									Toast.LENGTH_LONG).show();
//							return;
//						}else if(!Util.checkEmail(m_strUserName)){ // 判断邮箱格式是否正确
//							Toast.makeText(UserLoginActivity.this, R.string.msg_findpwd_mail_username_formateerror, 
//									Toast.LENGTH_LONG).show();
//							return;
//						}
						
						m_nType = 0;
						findPassword();
					}
			});
		
		dl.setNegativeButton(getString(R.string.str_findpwdpop_button_mobile),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						if(null == m_etUserName){
							return;
						}
						
						m_strUserName = m_etUserName.getText().toString();

						// 判断输入不能为空
//						if(null == m_strUserName || m_strUserName.isEmpty()){
//							Toast.makeText(UserLoginActivity.this, R.string.msg_findpwd_mail_username_empty, 
//									Toast.LENGTH_LONG).show();
//							return;
//						}else if(!Util.checkMobileFormate(m_strUserName)){ // 判断手机号格式是否正确
//							Toast.makeText(UserLoginActivity.this, R.string.msg_findpwd_mail_username_formateerror, 
//									Toast.LENGTH_LONG).show();
//							return;
//						}
						
						m_nType = 1;
						findPassword();
					}
			
			});
		dl.show();
	}
	
	/**
	 *  进入注册界面 
	 */
	private void showUserRegisterActivity(){

		getInputUserInfo();
		// 数据传输
		Bundle bundle = new Bundle();
		bundle.putString(UserRegisterActivity.USER_NAME, m_strAccount);
		//bundle.putString(UserRegisterActivity.USER_PWD, m_strPwd);
		
		// 进入注册界面
		Intent intent = new Intent();
		intent.setClass(QianniuLoginActivity.this,
				UserRegisterActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent,
				HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
}

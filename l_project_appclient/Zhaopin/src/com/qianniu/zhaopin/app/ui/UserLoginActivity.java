package com.qianniu.zhaopin.app.ui;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.MyAccontInfoData;
import com.qianniu.zhaopin.app.bean.MyAccontInfoDataEntity;
import com.qianniu.zhaopin.app.bean.MyAccountPersonalProfile;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.User;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.view.AdZoneView;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;
import com.qianniu.zhaopin.thp.Listener.QQUiListener;
import com.qianniu.zhaopin.thp.Listener.SinaAuthListener;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.tauth.Constants;
import com.tencent.tauth.Tencent;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserLoginActivity extends BaseActivity {

	private Context m_Context;
	private AppContext m_appContext; //

//	private TextView m_tvRegister; 		  // 注册
	private Button m_btnRegister;		  // 注册按钮
	
	private RelativeLayout m_rlLogin;     // 登录
//	private ImageButton m_btnLogin;       // 右上角登录

	private RelativeLayout m_btnThpQQ; // QQ登录
	private RelativeLayout m_bthThpWeibo; // 微博登录

//	private FindPwdPromptPopupWindow m_fpw; // 忘记密码界面

	private TextView tv_contact;
	private ImageButton btn_back;

	private String m_strAccount; // 用户名
	private String m_strPwd; 	// 用户密码

	private WeiboAuth m_SinaWA;
//	private Weibo m_SinaWB; // 新浪微博类
	private SsoHandler m_ssoSinaHandler; // sso认证功能
	// private SinaWeibo m_weiboSinas;
	// private SinaSsoHandler m_ssoSina;

	private ReqUserInfo m_req;

	private Tencent m_Tencent;

	private int m_nLoginType;   	// 当前登录类型
	
	private MyAccontInfoData m_maid;	// 我的账号信息
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		m_strAccount = "";
		m_strPwd = "";

		m_Context = this;
		m_appContext = (AppContext) this.getApplication();
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);

		Intent intent = getIntent();
		String startaction = intent.getAction();

		// 宣传位
		ViewGroup vg = (ViewGroup) findViewById(R.id.login_rl_container);
		AdZoneView adzoneView = new AdZoneView(this, AdZoneView.ADZONE_ID_LOGIN);
		vg.addView(adzoneView);

		m_btnRegister= (Button) findViewById(R.id.login_btn_register);
		m_btnRegister.setOnClickListener(mclickListener);
//		m_tvRegister = (TextView) findViewById(R.id.login_tv_register);
//		m_tvRegister.setOnClickListener(mclickListener);
//		m_btnLogin = (ImageButton) findViewById(R.id.login_btn_login);
//		m_btnLogin.setOnClickListener(mclickListener);
		m_rlLogin = (RelativeLayout) findViewById(R.id.login_rl_login);
		m_rlLogin.setOnClickListener(mclickListener);

		m_btnThpQQ = (RelativeLayout) findViewById(R.id.login_rl_sina);
		m_btnThpQQ.setOnClickListener(mclickListener);
		m_bthThpWeibo = (RelativeLayout) findViewById(R.id.login_rl_qq);
		m_bthThpWeibo.setOnClickListener(mclickListener);

		tv_contact = (TextView) findViewById(R.id.login_contact);
		tv_contact.setOnClickListener(mclickListener);

		btn_back = (ImageButton) findViewById(R.id.login_goback);
		btn_back.setOnClickListener(UIHelper.finish(this));
		// 是否显示登录信息
		AppContext ac = (AppContext) getApplication();
		User user = ac.getLoginInfo();
		if (user == null || !user.isRememberMe())
			return;
		if (!StringUtils.isEmpty(user.getAccount())) {
			m_strAccount = user.getAccount();
		}
		if (!StringUtils.isEmpty(user.getPwd())) {
			m_strPwd = user.getPwd();
		}
	}

	View.OnClickListener mclickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// 隐藏软键盘
			// imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			int vid = v.getId();
			switch (vid) {
			case R.id.login_goback:		// 返回
				{
					finish();
				}
				break;
//			case R.id.login_btn_login:
			case R.id.login_rl_login: {
				// 跳到登录界面
				Intent intent = new Intent();
				intent.setClass(UserLoginActivity.this,
						QianniuLoginActivity.class);
				startActivityForResult(intent,
						HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
				break;
//			case R.id.login_tv_register:
			case R.id.login_btn_register:
				{
					// 友盟统计--登录--注册按钮
					UmShare.UmStatistics(m_Context, "Login_RegisterButton");
					
					// 进入注册界面
					showUserRegisterActivity();
				}
				break;
			case R.id.login_contact: {
				// 友盟统计--登录--拨打客服电话按钮
				UmShare.UmStatistics(m_Context, "Login_ContactButton");
				if (checkCallingOrSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
					return;
				}
				Uri uri = Uri.parse("tel:" + "021-60727869");
				Intent it = new Intent(Intent.ACTION_CALL, uri);
				// it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(it);
			}
				break;
			case R.id.login_rl_qq: {
				// 友盟统计--登录--QQ登录按钮
				UmShare.UmStatistics(m_Context, "Login_QQLoginButton");
				QQLogin();
			}
				break;
			case R.id.login_rl_sina: {
				// 友盟统计--登录--新浪登录按钮
				UmShare.UmStatistics(m_Context, "Login_SinaLoginButton");
				weiboLogin();
			}
				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.onDestroy();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case HeadhunterPublic.RESULT_ACTIVITY_CODE: {
			switch(resultCode){
			case HeadhunterPublic.RESULT_REGISTER_SUCCESS_OK:	// 注册成功
				{
					m_nLoginType = HeadhunterPublic.LOGINTYPE_DEFAULT;
					// 从服务器中获取我的账号相关信息
					// runHttpGetMyAccountInfo();
					
					// 结束
					loginSuccessFinish();
				}
				break;
			case HeadhunterPublic.RESULT_QIANNIULOGIN_SUCCESS_OK:		// 普通(牵牛)登录成功
				{
//					m_nLoginType = HeadhunterPublic.LOGINTYPE_DEFAULT;
//					// 判断我的账号信息是否完善
//					getMyAcconutInfo();
					// 结束
					loginSuccessFinish();
				}
				break;
			case HeadhunterPublic.RESULT_MYACCOUNT_CANCEL:			// 完善资料(我的账号)按返回键返回
			case HeadhunterPublic.RESULT_MYACCOUNT_SUBMITSUCCESS:	// 完善资料(我的账号)中提交成功
				{
					// 结束
					loginSuccessFinish();
				}
				break;
			default:
				break;
			}
		}
			break;
		case HeadhunterPublic.SINAWEIBO_FOR_RESULT: // Sina微博
			{
				if (null != m_ssoSinaHandler) {
					m_ssoSinaHandler.authorizeCallBack(requestCode, resultCode,
							data);
				}
			}
			break;
		case HeadhunterPublic.QQ_FOR_RESULT: // QQ
			{
				m_Tencent.onActivityResult(requestCode, resultCode, data);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
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

	private Handler m_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HeadhunterPublic.LOGINMSG_ABNORMAL:
				 {
					 dismissProgressDialog();
					 ((AppException)msg.obj).makeToast(UserLoginActivity.this);
				 }
				 break;
			case HeadhunterPublic.LOGINMSG_FAIL: {
				dismissProgressDialog();

				UIHelper.ToastMessage(UserLoginActivity.this,
						getString(R.string.msg_login_fail));
			}
				break;
			case HeadhunterPublic.LOGINMSG_SUCCESS: {
				User user = (User) msg.obj;
				if (user != null) {
					// 提示登陆成功
					UIHelper.ToastMessage(UserLoginActivity.this,
							R.string.msg_login_success);
					
					// 获取当前登录类型
					if(null != user.getLoginType()){
						m_nLoginType = Integer.valueOf(user.getLoginType());
					}
					
					// 清空原先cookie
					ApiClient.cleanCookie();
					
					// 判断我的账号信息是否完善
					// getMyAcconutInfo();
					
					// 获取第三方信息
					getTHPInfo();
					
				}else{
					dismissProgressDialog();
				}
			}
				break;
			case HeadhunterPublic.LOGINMSG_THP_SUCCESS: {
				m_req = (ReqUserInfo) msg.obj;
				login();
			}
				break;
			case HeadhunterPublic.MSG_MYACCOUN_GETTDATA_SUCCESS:	// 获取我的账号相关信息成功
				{
					MyAccontInfoData mid = (MyAccontInfoData)msg.obj;
					if(null != mid){
						if(checkCompleteFlag(mid)){
							// 保存我的账号信息到数据库中
							MyAccontInfoData.saveMyAccontInfoData(m_appContext, mid);
							
							dismissProgressDialog();
							// 结束
							loginSuccessFinish();
						}
					}else{
						dismissProgressDialog();
						// 结束
						loginSuccessFinish();
					}
				}
				break;
			case HeadhunterPublic.MSG_MYACCOUN_GETTDATA_FAIL:		//  获取我的账号相关信息失败
			case HeadhunterPublic.MSG_MYACCOUN_GETTDATA_NONETWORKCONNECT:	// 获取我的账号相关信息时无网络
				{
					dismissProgressDialog();
					
//					UIHelper.ToastMessage(m_Context, 
//							getString(R.string.msg_myaccount_getrecord_fail));
					
					// 结束
					loginSuccessFinish();
				}
				break;
			case HeadhunterPublic.MSG_MYACCOUN_GETTDATA_ABNORMAL:	// 获取我的账号相关信息异常
				{
					dismissProgressDialog();
					
//					((AppException)msg.obj).makeToast(m_Context);

					// 结束
					loginSuccessFinish();
				}
				break;
			case HeadhunterPublic.MSG_GETSINAUSERINFO_FAIL:				// 获取新浪微博用户相关信息失败
			case HeadhunterPublic.MSG_GETSINAUSERINFO_ABNORMAL:			// 获取新浪微博用户相关信息异常
			case HeadhunterPublic.MSG_GETSINAUSERINFO_NONETWORKCONNECT:	// 获取新浪微博用户相关信息时无网络
			case HeadhunterPublic.MSG_GETQQUSERINFO_FAIL:				// 获取QQ用户相关信息失败
			case HeadhunterPublic.MSG_GETQQUSERINFO_ABNORMAL:			// 获取QQ用户相关信息异常
			case HeadhunterPublic.MSG_GETQQUSERINFO_NONETWORKCONNECT:	// 获取QQ用户相关信息时无网络
				{
					dismissProgressDialog();
					
					// 结束
					loginSuccessFinish();
				}
				break;
			case HeadhunterPublic.MSG_GETSINAUSERINFO_SUCCESS:		// 获取新浪微博用户相关信息成功
			case HeadhunterPublic.MSG_GETQQUSERINFO_SUCCESS:		// 获取QQ用户相关信息成功
				{
					// 进入完善资料界面
					//startMyAccountActivity(m_maid);
					
					if(null == m_maid){
						dismissProgressDialog();
						// 结束
						loginSuccessFinish();
					}else{
						// 提交第三方资料
						Sumit();
					}
					
				}
				break;
			case HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_SUCCESS:
			case HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_FAIL:
			case HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_ABNORMAL:
			case HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_NONETWORKCONNECT:
				{
					dismissProgressDialog();
					// 结束
					loginSuccessFinish();
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 *  登录验证
	 */
	private void login() {
		// 判断网络是否连接
		if (!UIHelper.isNetworkConnected(m_appContext)) {
			return;
		}
		showProgressDialog();
		Thread t = new Thread() {
			public void run() {
				LinkLogin();
			}
		};
		
		if(threadPool == null){
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
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
					 // 清除登录信息
					if(HeadhunterPublic.LOGINTYPE_QQ == Integer.valueOf(user.getLoginType())){
						ac.cleanQQLoginInfo(m_Tencent, m_Context);
					}else{
						ac.cleanLoginInfo();	
					}
					
					// 失败
					m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.LOGINMSG_FAIL, res.getErrorMessage()));
				}
			} catch (AppException e) {

				 // 清除登录信息
				if(HeadhunterPublic.LOGINTYPE_QQ == m_req.getLoginType()){
					AppContext ac = (AppContext) getApplication();
					ac.cleanQQLoginInfo(m_Tencent, m_Context);
				}
				
				e.printStackTrace();
				// 异常
				m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.LOGINMSG_ABNORMAL, e));
			}
		}
	}

	/**
	 * 微博登录
	 */
	private void weiboLogin() {
		m_SinaWA = new WeiboAuth(this,
				UIHelper.getTHPAPPIDForNum(
						m_appContext, HeadhunterPublic.THPKEY_SINAWEIBO_APPID),
				HeadhunterPublic.SINAWEIBO_REDIRECT_URL,
				HeadhunterPublic.SINAWEIBO_SCOPE);

		m_ssoSinaHandler = new SsoHandler(this, m_SinaWA);
		m_ssoSinaHandler.authorize(new SinaAuthListener(this, m_handler));
	}

	/**
	 * QQ登录
	 */
	private void QQLogin() {
		if (null == m_Tencent) {
			m_Tencent = Tencent.createInstance(UIHelper.getTHPAPPIDForNum(
					m_appContext, HeadhunterPublic.THPKEY_QQ_APPID),
					m_Context);
//					this.getApplicationContext());
		}

		if (null != m_Tencent) {
			if (!m_Tencent.isSessionValid()) {
				m_Tencent.login(this, HeadhunterPublic.QQ_SCOPE,
						new QQUiListener(this, m_handler));
			} else {
				m_Tencent.logout(this);

				m_Tencent.login(this, HeadhunterPublic.QQ_SCOPE,
						new QQUiListener(this, m_handler));
				
				// String strAccessToken = m_Tencent.getAccessToken();
				// String strOpenID = m_Tencent.getOpenId();
				//
				// String strTemp = "OLD openid: " + strOpenID +
				// "\naccess_token: " + strAccessToken;
				// Toast.makeText(this, strTemp, Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "QQ登录失败", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 从服务器中获取我的账号相关信息
	 */
	private void getMyAcconutInfo(){
		// 从缓存中读取数据
		MyAccontInfoData mid = MyAccontInfoData.
				getMyAccontInfoData(m_appContext, "");
		if(null != mid){
			dismissProgressDialog();
			
			if(checkCompleteFlag(mid)){
				// 结束
				loginSuccessFinish();
			}
		}else{
			// 从服务器中获取我的账号相关信息
			runHttpGetMyAccountInfo();
		}
	}
	
	/**
	 * 从服务器中获取我的账号相关信息
	 */
	private void runHttpGetMyAccountInfo(){
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_MYACCOUN_GETTDATA_NONETWORKCONNECT));
			return;
		}
		
		showProgressDialog();
		
		new Thread() {
			public void run() {
				httpGetMyAccountInfo();
			}
		}.start();
	}
	
	/**
	 * 从服务器中获取我的账号相关信息
	 */
	private void httpGetMyAccountInfo(){
		try {
			// 获取我的账号相关信息
			MyAccontInfoDataEntity uide = m_appContext.getMyAccountInfoData();

			Result res = uide.getValidate();
			
			if (res.OK()) {
				// 成功
				m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.MSG_MYACCOUN_GETTDATA_SUCCESS, uide.getData()));
			} else {
					// 失败
				m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.MSG_MYACCOUN_GETTDATA_FAIL, res.getErrorMessage()));
			}
		} catch (AppException e) {
			e.printStackTrace();
			// 异常
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_MYACCOUN_GETTDATA_ABNORMAL, e));
		}		
	}
	
	/**
	 * 检查我的账号信息是否完善
	 * @param mid
	 */
	private boolean checkCompleteFlag(MyAccontInfoData mid){
		if(null != mid){
			if(null != mid.getComplete_flag() &&
					!mid.getComplete_flag().isEmpty()){
				if(mid.getComplete_flag().equals(
						HeadhunterPublic.MYACCOUNT_COMPLETEFLAG_FALSE)){
					switch(m_nLoginType)
					{
					case HeadhunterPublic.LOGINTYPE_QQ:			// QQ登录
						{
							m_maid = mid;
							
							getQQUserInfo(false);
							return false;
						}
					case HeadhunterPublic.LOGINTYPE_SINAWEIBO:	//  新浪微博登录
						{
							m_maid = mid;
							// 获取新浪微博信息
							getSinaUserInfo(false);
							return false;
						}
					case HeadhunterPublic.LOGINTYPE_DEFAULT:	// 普通登录
						{
							dismissProgressDialog();
							// 进入完善资料界面
							startMyAccountActivity(mid);
							return false;
						}
					default:
						break;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 进入完善资料界面
	 * @param mid 我的账号相关信息
	 */
	private void startMyAccountActivity(MyAccontInfoData mid) {
		// 数据传输
		Bundle bundle = new Bundle();
		bundle.putSerializable(HeadhunterPublic.MYACCOUNT_DATATRANSFER_USERINFO, mid);
		bundle.putInt(HeadhunterPublic.MYACCOUNT_DATATRANSFER_TYPE,
				HeadhunterPublic.MYACCOUNT_TYPE_FIRST);
		
		// 进入完善资料界面
		Intent intent = new Intent();
		intent.setClass(m_Context, MyAccountActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent,
				HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 结束(登录成功)
	 */
	private void loginSuccessFinish(){
		setResult(RESULT_OK);
		finish();
	}
	
	/**
	 * 获取新浪用户信息
	 */
	private void getSinaUserInfo(boolean bShowProgressDialog){
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_GETSINAUSERINFO_NONETWORKCONNECT));
			
			return;
		}
		
		if(bShowProgressDialog){
			showProgressDialog();
		}
		
    	new Thread(){
        	public void run(){
        		httpGetSinaUserInfo();
        	}
        }.start();
	}
	
	/**
	 * 获取新浪微博用户信息
	 */
	private void httpGetSinaUserInfo(){
        try {
        	String url = "https://api.weibo.com/2/users/show.json?uid=" +
        			m_req.getThirdPartId() + "&access_token=" + m_req.getThirdPartToken();
        	
            HttpGet get=new HttpGet(URI.create(url));  
            HttpClient httpClient=new DefaultHttpClient();  
            HttpResponse response= httpClient.execute(get); 
            
            if (response.getStatusLine().getStatusCode() == 200) {  
                  
                    String temp=EntityUtils.toString(response.getEntity()); 
                    
                    JSONObject o = new JSONObject(temp); 
                    
                    String strGender = o.getString("gender"); 				// 性别
                    String strAvaterUrl = o.getString("avatar_large");		// 用户头像
                    String strDisplayName = o.getString("screen_name");		// 用户昵称
                    
                    if(null == m_maid){
                    	m_maid = new MyAccontInfoData();
                    }
                    
                    if(null != strGender && !strGender.isEmpty()){
                    	if(strGender.equals("m")){// 男
                    		m_maid.setGender(
                    				HeadhunterPublic.MYACCOUNT_GENDER_MALE);
                    	}else if(strGender.equals("f")){// 女
                    		m_maid.setGender(                   				
                    				HeadhunterPublic.MYACCOUNT_GENDER_FEMALE);
                    	}
                    	
                    }
                    
                    // 头像
                    if(null != strAvaterUrl && !strAvaterUrl.isEmpty()){
                    	m_maid.setAvatar(strAvaterUrl);
                    }
                    
                    // 昵称
                    if(null != strDisplayName && !strDisplayName.isEmpty()){
                    	m_maid.setDisplay_name(strDisplayName);
                    }                 	
                    
    				// 成功
    				m_handler.sendMessage(m_handler.obtainMessage(
    						HeadhunterPublic.MSG_GETSINAUSERINFO_SUCCESS));
            }else{
				// 失败
				m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.MSG_GETSINAUSERINFO_FAIL));
            }
        }catch (ClientProtocolException e) {  
            e.printStackTrace();  
			// 异常
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_GETSINAUSERINFO_ABNORMAL, e));
        } catch (ParseException e) {  
            e.printStackTrace(); 
			// 异常
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_GETSINAUSERINFO_ABNORMAL, e)); 
        } catch (IOException e) {  
            e.printStackTrace(); 
			// 异常
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_GETSINAUSERINFO_ABNORMAL, e)); 
        } catch (JSONException e) {  
            e.printStackTrace();  
			// 异常
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_GETSINAUSERINFO_ABNORMAL, e));
        }  
	}
	
	/**
	 * 获取QQ用户信息
	 */
	private void getQQUserInfo(boolean bShowProgressDialog){
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_GETQQUSERINFO_NONETWORKCONNECT));
			
			return;
		}
		
		if(bShowProgressDialog){
			showProgressDialog();
		}
		
    	new Thread(){
        	public void run(){
        		httpGetQQUserInfo();
        	}
        }.start();
	}
	
	/**
	 * 获取QQ用户信息
	 */
	private void httpGetQQUserInfo(){
        try {
        	JSONObject json = m_Tencent.request(Constants.GRAPH_SIMPLE_USER_INFO, null,
    				Constants.HTTP_GET);
        	if(null != json){
				String strGender = json.getString("gender");				// 性别
	            String strAvaterUrl = json.getString("figureurl_qq_2");		// 用户头像
	            String strDisplayName = json.getString("nickname");			// 用户昵称
	            
                if(null == m_maid){
                	m_maid = new MyAccontInfoData();
                }
                
                if(null != strGender && !strGender.isEmpty()){
                	if(strGender.equals("m")){// 男
                		m_maid.setGender(
                				HeadhunterPublic.MYACCOUNT_GENDER_MALE);
                	}else if(strGender.equals("f")){// 女
                		m_maid.setGender(
                				HeadhunterPublic.MYACCOUNT_GENDER_FEMALE);
                	}
                }
                
                // 头像
                if(null != strAvaterUrl && !strAvaterUrl.isEmpty()){
                	m_maid.setAvatar(strAvaterUrl);
                }
                
                // 昵称
                if(null != strDisplayName && !strDisplayName.isEmpty()){
                	m_maid.setDisplay_name(strDisplayName);
                }
                
				// 成功
				m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.MSG_GETQQUSERINFO_SUCCESS));
        	}else{
				// 失败
				m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.MSG_GETQQUSERINFO_FAIL));
        	}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 异常
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_GETQQUSERINFO_ABNORMAL, e));
		} 				
	}
	
	/**
	 * 进入注册界面
	 */
	private void showUserRegisterActivity(){
		// 数据传输
		Bundle bundle = new Bundle();
		bundle.putString(UserRegisterActivity.USER_NAME, m_strAccount);
		//bundle.putString(UserRegisterActivity.USER_PWD, m_strPwd);
		
		// 进入注册界面
		Intent intent = new Intent();
		intent.setClass(UserLoginActivity.this,
				UserRegisterActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent,
				HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 获取第三方账号相关信息
	 */
	private void getTHPInfo(){
		switch(m_nLoginType)
		{
		case HeadhunterPublic.LOGINTYPE_QQ:			// QQ登录
			{
				getQQUserInfo(false);
			}
			break;
		case HeadhunterPublic.LOGINTYPE_SINAWEIBO:	//  新浪微博登录
			{
				// 获取新浪微博信息
				getSinaUserInfo(false);
			}
			break;
		case HeadhunterPublic.LOGINTYPE_DEFAULT:	// 普通登录
		default:
			{
				dismissProgressDialog();
				loginSuccessFinish();
			}
			break;
		}
		
		return;
	}
	
	/**
	 * 提交我的账号信息
	 */
	private void Sumit(){
		if (!UIHelper.isNetworkConnected(m_appContext)) {
			// 失败
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_NONETWORKCONNECT));
        	
			return;
		}
		
		new Thread() {
			public void run() {
				submitPersonalProfile();
			}
		}.start();
	}
	
	/**
	 * 提交个人资料(主要是个人头像)
	 */
	private void submitPersonalProfile(){	
		try {
			MyAccountPersonalProfile mApp = new MyAccountPersonalProfile();
			mApp.setAvatar(m_maid.getAvatar());
			mApp.setGender(m_maid.getGender());
			mApp.setDisplay_name(m_maid.getDisplay_name());
			
			Result res = m_appContext.submitPersonalProfile(mApp);

	        if( res.OK()){			
	        	// 成功
	        	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_SUCCESS, res));
	        }else{
				// 失败
	        	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_FAIL, res.getErrorMessage()));
	        }
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 异常
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_SUBMITPERSONALPROFILE_ABNORMAL, e));
		}
		
	}
}

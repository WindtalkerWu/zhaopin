package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.User;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.view.Util;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserRegisterActivity extends BaseActivity{
	
	private Context m_Context;
	private AppContext m_appContext;		//
	
//	private View m_vRegisterloading;
//	private AnimationDrawable m_loadingAnimation;
	
	private ImageButton m_btnBack;					// 返回按钮
	private ImageButton m_btnSubmit;				// 右上角提交按钮
	private RelativeLayout m_rlSubmit;				// 提交按钮
	
	private CheckBox m_chbRead;
	private TextView m_tvRead;
	
	private EditText m_etUserName;					// 用户名
	private EditText m_etUserPwd;					// 用户密码
	private EditText m_etUserPwdConfirm;			// 用户密码确认
	
	private String m_strUserName;
	private String m_strUserPwd;
	
	private PromptPopupWindow m_PPWindow;
	
	public static final String USER_NAME = "user_name";
	//public static final String USER_PWD = "user_pwd";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		m_Context = this;
		m_appContext = (AppContext) this.getApplication();
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
		
		ininData();
		init();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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

	private void ininData(){
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_strUserName = bundle.getString(USER_NAME);
			//m_strUserPwd =  bundle.getString(USER_PWD);
		}
	}
	
	/**
	 * 
	 */
	private void init(){
		m_btnBack = (ImageButton)findViewById(R.id.register_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// UIHelper.showLoginActivity(m_Context);
				finish();
			}
			
		});
		
		m_rlSubmit = (RelativeLayout)findViewById(R.id.register_lp_submit);
		m_rlSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--注册--提交按钮
				UmShare.UmStatistics(m_Context, "Register_SubmitButton");
				
				if(checkUserInfo()){
					register();
				}
			}
		});
		
		// 右上角提交按钮
		m_btnSubmit = (ImageButton)findViewById(R.id.register_btn_submit);
		m_btnSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkUserInfo()){
					// 友盟统计--注册--提交按钮
					UmShare.UmStatistics(m_Context, "Register_SubmitButton");
					
					register();
				}
			}
		});
		
		m_tvRead = (TextView)findViewById(R.id.register_tv_read);
		m_tvRead.setText(Html.fromHtml("<u>"+getString(R.string.register_tv_read)+"</u>"));
		m_tvRead.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTipsWindows(v);
			}
			
		});
		
		m_chbRead = (CheckBox)findViewById(R.id.register_cb_read);
		m_chbRead.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!m_chbRead.isChecked()){
					// 友盟统计--注册--勾选用户求职协议
					UmShare.UmStatistics(m_Context, "Register_CheckUserAgreement");
				}else{
					// 友盟统计--注册--去掉勾选用户求职协议
					UmShare.UmStatistics(m_Context, "Register_NoCheckUserAgreement");
				}
			}
			
		});
		
		// 
		m_etUserName = (EditText)findViewById(R.id.register_et_username);
		if(null != m_strUserName && !m_strUserName.isEmpty()){
			m_etUserName.setText(m_strUserName);
		}
		
		// 
		m_etUserPwd = (EditText)findViewById(R.id.register_et_userpwd);
//		if(null != m_strUserPwd && !m_strUserPwd.isEmpty()){
//			m_etUserPwd.setText(m_strUserPwd);
//		}
		
		m_etUserPwdConfirm = (EditText)findViewById(R.id.register_et_userpwd_confirm);
//		if(null != m_strUserPwd && !m_strUserPwd.isEmpty()){
//			m_etUserPwdConfirm.setText(m_strUserPwd);
//		}
	}
	private boolean checkUserNameFormat(String userName) {
		boolean isMobile = Util.checkMobileFormate(userName);
		boolean isEmail = Util.checkEmail(userName);
		if ( !isMobile && !isEmail) {
			UIHelper.ToastMessage(this, getString(R.string.msg_register_username_formate_error));
			return false;
		}
		return true;
	}
	private boolean checkUserInfo(){
		// 检查用户名是否为空
		m_strUserName = m_etUserName.getText().toString();
		if(m_strUserName.isEmpty()){
			UIHelper.ToastMessage(this, getString(R.string.msg_register_username_empty));
			return false;
		} else if(!checkUserNameFormat(m_strUserName)) {
			return false;
		}
		
		// 检查密码是否为空
		String strUserPwd = m_etUserPwd.getText().toString();
		if(strUserPwd.isEmpty()){
			UIHelper.ToastMessage(this, getString(R.string.msg_register_usernamepwd_empty));
			
			return false;
		}
		
		// 检查第二次输入密码是否为空
		String strUserPwdC = m_etUserPwdConfirm.getText().toString();
		if(strUserPwdC.isEmpty()){
			UIHelper.ToastMessage(this, getString(R.string.msg_register_usernamepwd_confirm_empty));
			
			return false;
		}
		
		// 检查两次输入的密码是否一致
		if(!strUserPwd.equals(strUserPwdC)){
			UIHelper.ToastMessage(this, getString(R.string.msg_register_usernamepwd_notthesame));
			
			return false;
		}
		
		m_strUserPwd = strUserPwd;
		
		if(!m_chbRead.isChecked()){
			UIHelper.ToastMessage(this, getString(R.string.msg_register_no_read_agreement));
			
			return false;			
		}
		
		return true;
	}
	
	/*****************************************************************
	 * 消息处理
	 * 
	 ****************************************************************/
    private Handler m_handler = new Handler() {
    	public void handleMessage(Message msg){
    		switch(msg.what){
			case HeadhunterPublic.REGISTERMSG_ABNORMAL:
				{
					dismissProgressDialog();
					((AppException)msg.obj).makeToast(UserRegisterActivity.this);
				}
				break;
			case HeadhunterPublic.REGISTERMSG_FAIL:
				{
					dismissProgressDialog();
					Result result = (Result) msg.obj;
					if (result != null) {
						if (result.getErrorCode() == Result.CODE_USERNAME_HASREGISTER) {
							UIHelper.ToastMessage(UserRegisterActivity.this, getString(R.string.msg_register_fail));
						} else {
							result.handleErrcode(UserRegisterActivity.this);
						}
					}		
				}
				break;
			case HeadhunterPublic.REGISTERMSG_SUCCESS:
				{
					dismissProgressDialog();
					User user = (User)msg.obj;
					if(user != null){
						//清空原先cookie
						ApiClient.cleanCookie();
						// 提示注册成功
						UIHelper.ToastMessage(UserRegisterActivity.this, R.string.msg_register_success);
						setResult(HeadhunterPublic.RESULT_REGISTER_SUCCESS_OK);
						
						finish();
					}
				}
				break;
			default:
				break;
			}	    		
    	}
    };
    
	/*****************************************************************
	 * 登录验证
	 * 
	 ****************************************************************/
    private void register() {
    	// 判断网络是否连接
		if(!UIHelper.isNetworkConnected(m_appContext)){
			return;
		}
    	showProgressDialog();
        
    	new Thread(){
        	public void run(){
        		Linkregister();
        	}
        }.start();
    }
    
	/*****************************************************************
	 * 向服务器发出注册请求
	 * 
	 ****************************************************************/
	private void Linkregister(){
		try {
            User user = m_appContext.registerVerify(m_strUserName, m_strUserPwd);
            user.setAccount(m_strUserName);
            user.setPwd(m_strUserPwd);
        	user.setRememberMe(true);
            
            Result res = user.getValidate();
            if( res.OK()){
            	m_appContext.saveLoginInfo(user);	//	保存登录信息	
            	//	成功
            	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.REGISTERMSG_SUCCESS, user));
            }else{
            	m_appContext.cleanLoginInfo();	//	清除登录信息
            	// 失败
            	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.REGISTERMSG_FAIL, res));
            }
        } catch (AppException e) {
        	e.printStackTrace();			
	    	// 异常
        	m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.REGISTERMSG_ABNORMAL, e));
        }		
	}
	
	private void showTipsWindows(View view) {
		UIHelper.showAgreementDialog(UserRegisterActivity.this,
				R.string.str_user_agreement_title, R.string.str_user_agreement,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
						}
						dialog.dismiss();
						
						if(null != m_chbRead)
						{
							m_chbRead.setChecked(true);
						}
					}
				});
//		if (m_PPWindow == null) {
//
//			m_PPWindow = new PromptPopupWindow(getApplicationContext(), true);
//			View.OnClickListener onclickListener = new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					switch (v.getId()) {
//					case R.id.prompt_title_cancel:
//						hideTipsWindows();
//						break;
//					case R.id.prompt_title_ok:
//						{
//							hideTipsWindows();
//							if(null != m_chbRead){
//								m_chbRead.setChecked(true);
//							}
//						}
//						break;
//					default:
//						break;
//					}
//				}
//			};
//			m_PPWindow.setListener(onclickListener);
//			m_PPWindow.initView(R.string.str_user_agreement_title,
//					R.string.str_user_agreement, false);
//		}
//		m_PPWindow.show(view);
	}
	
	private void hideTipsWindows() {
		if (m_PPWindow != null) {
			m_PPWindow.dismiss();
		}
	}
}

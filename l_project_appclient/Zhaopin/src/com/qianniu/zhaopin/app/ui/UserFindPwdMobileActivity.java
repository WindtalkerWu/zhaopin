package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.User;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class UserFindPwdMobileActivity extends BaseActivity{

	private Context m_Context;
	
	private View m_vFPMloading;
	private AnimationDrawable m_loadingAnimation;
	
	private ImageButton m_btnBack;					// 返回按钮
	
	private Button m_btnSubmit;						// 提交按钮
//	private Button m_btnVerify;						// 获取验证码按钮
	
	private TextView m_tvContact;
	
	private EditText m_etUserName;					// 用户名
//	private EditText m_etVerify;					// 验证码
//	private EditText m_etUserPwd;					// 用户密码
//	private EditText m_etUserPwdConfirm;			// 用户密码确认
	
	private String m_strUserName;
	private String m_strUserPwd;
	private String m_strVerify;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_findpwd_mobile);
		
		m_Context = this;
		
		init();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/*****************************************************************
	 * 初始化
	 * 
	 * 
	 ****************************************************************/
	private void init(){
		m_btnBack = (ImageButton)findViewById(R.id.fpm_mobile_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});	
		
		m_btnSubmit = (Button)findViewById(R.id.fpm_mobile_btn_submit);
		m_btnSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkUserInfo()){
					findPassword();
				}
			}
		});
		
//		m_btnVerify = (Button)findViewById(R.id.fpm_mobile_btn_verify);
//		m_btnVerify.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(checkUserName()){
//					getVerify();
//				}
//			}
//		});
		
		m_tvContact = (TextView) findViewById(R.id.fpm_mobile_contact);
		m_tvContact.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkCallingOrSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
					return ;
				}
				Uri uri = Uri.parse("tel:" + "021-60727869");
				Intent it = new Intent(Intent.ACTION_CALL, uri);
				// it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(it);	
			}
		});
		
		m_etUserName = (EditText)findViewById(R.id.fpm_mobile_username);
//		m_etVerify = (EditText)findViewById(R.id.fpm_mobile_verify);
//		m_etUserPwd = (EditText)findViewById(R.id.fpm_mobile_userpwd);
//		m_etUserPwdConfirm = (EditText)findViewById(R.id.fpm_mobile_userpwd_confirm);
		
		m_vFPMloading = findViewById(R.id.findpwd_mobile_loading);
	}
	
	/*****************************************************************
	 * 检查用户名是否为空
	 * 
	 * 
	 ****************************************************************/
	private boolean checkUserName(){
		// 检查用户名是否为空
		m_strUserName = m_etUserName.getText().toString();
		if(m_strUserName.isEmpty()){
			UIHelper.ToastMessage(this, getString(R.string.msg_findpwd_mobile_username_empty));

			return false;
		}
		
		return true;
	}
	
	/*****************************************************************
	 * 检查信息是否输入
	 * 
	 * 
	 ****************************************************************/
	private boolean checkUserInfo(){
		if(!checkUserName()){
			return false;
		}
		
		// 检查验证码是否为空
/*		m_strVerify = m_etVerify.getText().toString();
		if(m_strVerify.isEmpty()){
			UIHelper.ToastMessage(this, getString(R.string.msg_findpwd_mobile_Verify_empty));

			return false;
		}
		
		// 检查密码是否为空
		String strUserPwd = m_etUserPwd.getText().toString();
		if(strUserPwd.isEmpty()){
			UIHelper.ToastMessage(this, getString(R.string.msg_findpwd_mobile_usernamepwd_empty));
			
			return false;
		}
		
		// 检查第二次输入密码是否为空
		String strUserPwdC = m_etUserPwdConfirm.getText().toString();
		if(strUserPwdC.isEmpty()){
			UIHelper.ToastMessage(this, getString(R.string.msg_findpwd_mobile_usernamepwd_confirm_empty));
			
			return false;
		}
		
		// 检查两次输入的密码是否一致
		if(!strUserPwd.equals(strUserPwdC)){
			UIHelper.ToastMessage(this, getString(R.string.msg_findpwd_mobile_usernamepwd_notthesame));
			
			return false;
		}
		m_strUserPwd = strUserPwd;
*/		
		return true;
	}
	
	/*****************************************************************
	 * 获取验证码
	 * 
	 * 
	 ****************************************************************/
    private void getVerify() {
    	
    	m_vFPMloading.setVisibility(View.VISIBLE);
    	m_loadingAnimation = (AnimationDrawable)m_vFPMloading.getBackground();
    	m_loadingAnimation.start();
        
    	new Thread(){
        	public void run(){
        		LinkGetVerify();
        	}
        }.start();
    }
    
	/*****************************************************************
	 * 手机找回密码
	 * 
	 * 
	 ****************************************************************/
   private void findPassword() {
    	
    	m_vFPMloading.setVisibility(View.VISIBLE);
    	m_loadingAnimation = (AnimationDrawable)m_vFPMloading.getBackground();
    	m_loadingAnimation.start();
        
    	new Thread(){
        	public void run(){
        		LinkFindPassword();
        	}
        }.start();
    }
   
	/*****************************************************************
	 * 消息处理
	 * 
	 ****************************************************************/
    private Handler m_handler = new Handler() {
    	public void handleMessage(Message msg){
    		switch(msg.what){
    		case HeadhunterPublic.FINDPASSWORDMSG_MOBILE_ABNORMAL:
			case HeadhunterPublic.FINDPASSWORDMSG_MOBILE_FAIL:
				{
					m_vFPMloading.setVisibility(View.INVISIBLE);
					m_loadingAnimation.stop();

					UIHelper.ToastMessage(UserFindPwdMobileActivity.this, getString(R.string.msg_findpwd_mobile_fail) + msg.obj);		
				}
				break;
			case HeadhunterPublic.FINDPASSWORDMSG_MOBILE_SUCCESS:
				{
					m_vFPMloading.setVisibility(View.INVISIBLE);

					// 提示手机找回密码成功
					UIHelper.ToastMessage(UserFindPwdMobileActivity.this, R.string.msg_findpwd_mobile_success);
					setResult(HeadhunterPublic.RESULT_FINDPWD_MOBILE_SUCCESS_OK);
					
					finish();
				}
				break;
			case HeadhunterPublic.GETVERIFYMSG_SUCCESS:
				{
					// 提示获取验证码成功
					m_vFPMloading.setVisibility(View.INVISIBLE);
					
					UIHelper.ToastMessage(UserFindPwdMobileActivity.this,
							R.string.msg_get_Verify_success);
				}
				break;
			case HeadhunterPublic.GETVERIFYMSG_ABNORMAL:
			case HeadhunterPublic.GETVERIFYMSG_FAIL:
				{
					// 提示获取验证码失败
					m_vFPMloading.setVisibility(View.INVISIBLE);
					m_loadingAnimation.stop();

					UIHelper.ToastMessage(UserFindPwdMobileActivity.this, 
							getString(R.string.msg_get_Verify_fail) + msg.obj);
				}
				break;
			default:
				break;
			}	    		
    	}
    };
    
	/*****************************************************************
	 * 向服务器发出获取验证码请求
	 * 
	 ****************************************************************/
	private void LinkGetVerify(){
		Message msg =new Message();
		try {
			AppContext ac = (AppContext)getApplication(); 
			
            User user = ac.getVerify(m_strUserName);
            
            Result res = user.getValidate();
            if( res.OK()){
            	msg.what = HeadhunterPublic.GETVERIFYMSG_SUCCESS;		//	成功
            }else{
            	msg.what = HeadhunterPublic.GETVERIFYMSG_FAIL;			//	失败
            	msg.obj = res.getErrorMessage();
            }
        } catch (AppException e) {
        	e.printStackTrace();
	    	msg.what = HeadhunterPublic.GETVERIFYMSG_ABNORMAL;			// 异常
	    	msg.obj = e;
        }
		
		m_handler.sendMessage(msg); 		
	}
	
	/*****************************************************************
	 * 向服务器发出密码修改请求
	 * 
	 ****************************************************************/
	private void LinkFindPassword(){
		
		Message msg =new Message();
		try {
			AppContext ac = (AppContext)getApplication(); 
			
            User user = ac.findpwdMobileVerify(m_strUserName, m_strUserPwd, m_strVerify);
            
            Result res = user.getValidate();
            if( res.OK()){
            	msg.what = HeadhunterPublic.FINDPASSWORDMSG_MOBILE_SUCCESS;			//	成功
            }else{
            	msg.what = HeadhunterPublic.FINDPASSWORDMSG_MOBILE_FAIL;			//	失败
            	msg.obj = res.getErrorMessage();
            }
        } catch (AppException e) {
        	e.printStackTrace();
	    	msg.what = HeadhunterPublic.FINDPASSWORDMSG_MOBILE_ABNORMAL;			// 异常
	    	msg.obj = e;
        }
		
		m_handler.sendMessage(msg); 
	}
}

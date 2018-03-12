package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
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

public class UserFindPwdMailActivity extends BaseActivity{
	
	private Context m_Context;
	
	private View m_vFPMloading;
	private AnimationDrawable m_loadingAnimation;
	
	private ImageButton m_btnBack;					// 返回按钮
	
	private Button m_btnSubmit;						// 提交按钮
	
	private TextView m_tvContact;

	private EditText m_etUserName;					// 用户名
	private String m_strUserName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_findpwd_mail);
		
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
		m_btnBack = (ImageButton)findViewById(R.id.fpm_mail_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});	
		
		m_btnSubmit = (Button)findViewById(R.id.fpm_mail_btn_submit);
		m_btnSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkUserName()){
					findPassword();
				}
			}
		});
		
		m_tvContact = (TextView) findViewById(R.id.fpm_mail_contact);
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
		
		m_etUserName = (EditText)findViewById(R.id.fpm_mail_username);
		m_vFPMloading = findViewById(R.id.findpwd_mail_loading);
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
			UIHelper.ToastMessage(this, getString(R.string.msg_findpwd_mail_username_empty));

			return false;
		}
		
		return true;
	}
	
	/*****************************************************************
	 * 邮箱找回密码
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
			case HeadhunterPublic.FINDPASSWORDMSG_MAIL_ABNORMAL:
				{
					m_vFPMloading.setVisibility(View.INVISIBLE);
					m_loadingAnimation.stop();
					((AppException)msg.obj).makeToast(UserFindPwdMailActivity.this);
				}
				break;
			case HeadhunterPublic.FINDPASSWORDMSG_MAIL_FAIL:
				{
					m_vFPMloading.setVisibility(View.INVISIBLE);
					m_loadingAnimation.stop();

					// 提示邮箱找回密码失败
					UIHelper.ToastMessage(UserFindPwdMailActivity.this, getString(R.string.msg_findpwd_mail_fail) + msg.obj);		
				}
				break;
			case HeadhunterPublic.FINDPASSWORDMSG_MAIL_SUCCESS:
				{
					m_vFPMloading.setVisibility(View.INVISIBLE);

					// 提示邮箱找回密码成功
					UIHelper.ToastMessage(UserFindPwdMailActivity.this, R.string.msg_findpwd_mail_success);
					setResult(HeadhunterPublic.RESULT_FINDPWD_MAIL_SUCCESS_OK);
					
					finish();
				}
				break;
			default:
				break;
			}	    		
    	}
    };
    
	/*****************************************************************
	 * 向服务器发出密码修改请求
	 * 
	 ****************************************************************/
	private void LinkFindPassword(){
		
		Message msg =new Message();
		try {
			AppContext ac = (AppContext)getApplication(); 
			
            User user = ac.findpwdMailVerify(m_strUserName);
            
            Result res = user.getValidate();;
            if( res.OK()){
            	msg.what = HeadhunterPublic.FINDPASSWORDMSG_MAIL_SUCCESS;		//	成功
            }else{
            	msg.what = HeadhunterPublic.FINDPASSWORDMSG_MAIL_FAIL;			//	失败
            	msg.obj = res.getErrorMessage();
            }
        } catch (AppException e) {
        	e.printStackTrace();
	    	msg.what = HeadhunterPublic.FINDPASSWORDMSG_MAIL_ABNORMAL;			// 异常
	    	msg.obj = e;
        }
		
		m_handler.sendMessage(msg); 
	}
}

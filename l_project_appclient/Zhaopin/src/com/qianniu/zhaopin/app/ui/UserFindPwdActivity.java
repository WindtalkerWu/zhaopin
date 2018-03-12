package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class UserFindPwdActivity extends BaseActivity{
	
	private Context m_Context;
	
	private Button m_btnMail;						// 选择邮箱账号找回按钮
	private Button m_btnMobile;						// 选择手机账号找回按钮
	
	private ImageButton m_btnBack;					// 返回按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_findpwd);
		
		m_Context = this;
		
		init();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if( HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode){
			switch(resultCode){
			case HeadhunterPublic.RESULT_FINDPWD_MOBILE_SUCCESS_OK:
			case HeadhunterPublic.RESULT_FINDPWD_MAIL_SUCCESS_OK:
				{
					finish();
				}
				break;
			default:
				break;
			}
		}
	}

	/*****************************************************************
	 * 初始化
	 * 
	 * 
	 ****************************************************************/
	private void init(){
		m_btnBack = (ImageButton)findViewById(R.id.findpwd_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		m_btnMail = (Button)findViewById(R.id.fidnpwd_btn_mail);
		m_btnMail.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 进入通过邮箱找回密码界面
		        Intent intent = new Intent();
		        intent.setClass(UserFindPwdActivity.this, UserFindPwdMailActivity.class);
		        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
		});
		
		m_btnMobile = (Button)findViewById(R.id.fidnpwd_btn_moblie);
		m_btnMobile.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 进入通过手机账号找回密码界面
		        Intent intent = new Intent();
		        intent.setClass(UserFindPwdActivity.this, UserFindPwdMobileActivity.class);
		        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
		});
	}
	
}

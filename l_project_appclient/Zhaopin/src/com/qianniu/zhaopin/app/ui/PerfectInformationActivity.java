package com.qianniu.zhaopin.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.thp.UmShare;

/**
 * 完善资料界面
 * @author wuzy
 *
 */
public class PerfectInformationActivity extends BaseActivity {
	private Context m_Context;
	private AppContext m_appContext;

	private ImageButton m_btnBack;					// 返回按钮
	
	private Button m_btnSubmit;						// 提交按钮
	private Button m_btnSkip;						// 跳过按钮
	private Button m_btnAuth;						// 验证按钮
	
	private EditText m_etName;						// 姓名
	private EditText m_etPhone;						// 手机号
	private EditText m_etAuth;						// 验证码
	
	private RadioButton m_rbMale;					// 男
	private RadioButton m_rbFemale;					// 女
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_perfectinformation);

		m_Context = this;
		m_appContext = (AppContext) getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
		
		// 初始化控件
		initControl();
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
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 初始化文本控件
		initTextControl();
		
		// 初始化按钮控件
		initButtonControl();
		
		// 男
		m_rbMale = (RadioButton)findViewById(R.id.perfectinformation_rb_male);
		// 女
		m_rbMale = (RadioButton)findViewById(R.id.perfectinformation_rb_female);
	}
	
	/**
	 * 初始化文本控件
	 */
	private void initTextControl(){					
		// 姓名
		m_etName = (EditText)findViewById(R.id.perfectinformation_et_name);
		// 手机号
		m_etPhone = (EditText)findViewById(R.id.perfectinformation_et_phone);
		// 验证码
		m_etAuth = (EditText)findViewById(R.id.perfectinformation_et_auth);
	}
	
	/**
	 * 初始化按钮控件
	 */
	private void initButtonControl(){
		// 返回按钮
		m_btnBack = (ImageButton)findViewById(R.id.perfectinformation_btn_back);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 提交按钮
		m_btnSubmit = (Button)findViewById(R.id.perfectinformation_btn_submit);
		m_btnSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		
		// 跳过按钮
		m_btnSkip = (Button)findViewById(R.id.perfectinformation_btn_skip);
		m_btnSkip.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 验证按钮
		m_btnAuth = (Button)findViewById(R.id.perfectinformation_btn_auth);
		m_btnAuth.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});		
	}
}

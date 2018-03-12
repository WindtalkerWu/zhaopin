package com.qianniu.zhaopin.thp.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.ui.BaseActivity;

public class AlipayAccountBindingActivity extends BaseActivity{
	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageView m_btnBack;					// 返回按钮
	private Button m_btnBinding;					// 绑定按钮
	
	private EditText m_etAccount;					// 支付宝账号
	private EditText m_etQianniuPwd;				// 牵牛账号密码
	
	private String m_strAccout;
	private boolean m_bBindingSuc;
	
	public static final String ALIPAY_ACCOUNT = "alipay_account";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aliplayaccount_binding);
		
		m_Context = this;
		m_appContext = (AppContext) getApplication();

		initData();
		initControl();
	}

	/**
	 * 初始化数据
	 */
	private void initData(){
		m_bBindingSuc = false;
		
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_strAccout = bundle.getString(ALIPAY_ACCOUNT);
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 支付宝账号
		m_etAccount = (EditText)findViewById(R.id.aliplayaccount_binding_et_account);
		if(null != m_etAccount && null != m_strAccout
				&& !m_strAccout.isEmpty()){
			m_etAccount.setText(m_strAccout);
		}
		
		// 牵牛账号密码
		m_etQianniuPwd = (EditText)findViewById(R.id.aliplayaccount_binding_et_qianniupwd);

		// 返回按钮
		m_btnBack = (ImageView)findViewById(R.id.aliplayaccount_binding_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 提现按钮
		m_btnBinding = (Button)findViewById(R.id.aliplayaccount_binding_btn_binding);
		m_btnBinding.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkTextValue()){
					binding();
				}
			}
		});
	}
	
	/**
	 * 判断控件上输入的值
	 * @return
	 */
	private boolean checkTextValue(){
		if(null == m_etAccount){
			return false;
		}
		
		if(null == m_etQianniuPwd){
			return false;
		}
		
		// 获取输入的支付宝账号
		m_strAccout = m_etAccount.getText().toString();
		if(m_strAccout.isEmpty()){
			m_etAccount.setFocusable(true);
			UIHelper.ToastMessage(this, getString(
					R.string.msg_aliplayaccount_binding_account_isempty));
			return false;
		}
		
//		String strPwd = m_etQianniuPwd.getText().toString();
//		if(strPwd.isEmpty()){
//			m_etQianniuPwd.setFocusable(true);
//			UIHelper.ToastMessage(this, getString(
//					R.string.msg_aliplayaccount_binding_qianniupwd_isempty));
//			return false;
//		}
		
		return true;
	}
	
	/**
	 * 绑定
	 */
	private void binding(){
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			return;
		}
		
		showProgressDialog();
		
		new Thread() {
			public void run() {
				bindingAlipayAccount(m_strAccout);
			}
		}.start();
	}
	
	/**
	 * 绑定支付宝账号
	 * @param strAccout
	 */
	private void bindingAlipayAccount(String strAccout){
		try{
			
			Result res = m_appContext.bindingAlipayAccount(strAccout);

			if (res.OK()) {
				// 成功
				m_Handler.sendMessage(m_Handler.obtainMessage(
						HeadhunterPublic.MSG_BINDING_ALIPAACCOUNT_SUCCESS));	
			} else {
				// 失败
				m_Handler.sendMessage(m_Handler.obtainMessage(
							HeadhunterPublic.MSG_BINDING_ALIPAACCOUNT_FAIL, res.getErrorMessage()));
			}
		}catch (AppException e) {
			e.printStackTrace();
			// 异常
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_BINDING_ALIPAACCOUNT_ABNORMAL, e));
		}		
	}
	
	private Handler m_Handler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what){
			case HeadhunterPublic.MSG_BINDING_ALIPAACCOUNT_SUCCESS:
				{// 绑定成功
					dismissProgressDialog();
					
					m_bBindingSuc = true;
					
					UIHelper.ToastMessage(m_Context, getString(
							R.string.msg_aliplayaccount_binding_success));
					
					finishAlipayAccountBinding();
				}
				break;
			case HeadhunterPublic.MSG_BINDING_ALIPAACCOUNT_FAIL:
				{// 绑定失败
					dismissProgressDialog();
					
					UIHelper.ToastMessage(m_Context, getString(
							R.string.msg_aliplayaccount_binding_fail));
				}
				break;
			case HeadhunterPublic.MSG_BINDING_ALIPAACCOUNT_ABNORMAL:
				{// 绑定异常
					dismissProgressDialog();
					
					((AppException)msg.obj).makeToast(m_Context);
				}
				break;
			}
		}

	};
	
	/**
	 * 结束
	 */
	private void finishAlipayAccountBinding(){
		if(m_bBindingSuc){
			Bundle bundle = new Bundle();
			bundle.putString(ALIPAY_ACCOUNT, m_strAccout);
			
			setResult(HeadhunterPublic.RESULT_ALIPAYACCOUNT_CHANGE, 
					getIntent().putExtras(bundle));
		}
		
		finish();
	}
}

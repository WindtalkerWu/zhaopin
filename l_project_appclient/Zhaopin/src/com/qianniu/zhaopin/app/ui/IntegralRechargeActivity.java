package com.qianniu.zhaopin.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.thp.UmShare;

/**
 * 积分充值界面
 * @author wuzy
 *
 */
public class IntegralRechargeActivity extends BaseActivity {
	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageView m_btnBack;					// 返回按钮
	private RelativeLayout m_btnGoRecharge;			// 去充值
	
	private EditText m_etRechargeSum;				// 充值输入控件
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_integralrechargeactivity);

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
		// 充值输入控件
		m_etRechargeSum = (EditText)findViewById(R.id.integralrecharge_et_rechargesum);
		
		// 返回按钮
		m_btnBack = (ImageView)findViewById(R.id.integralrecharge_btn_back);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 去充值充值
		m_btnGoRecharge = (RelativeLayout)findViewById(R.id.integralrecharge_lp_gorecharge);
		m_btnGoRecharge.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}

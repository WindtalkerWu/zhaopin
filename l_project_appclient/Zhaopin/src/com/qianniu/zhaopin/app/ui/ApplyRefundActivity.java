package com.qianniu.zhaopin.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;

/**
 * 申请退款界面
 * @author wuzy
 *
 */
public class ApplyRefundActivity extends BaseActivity {
	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageButton m_btnBack;					// 返回按钮
	private RelativeLayout m_btnConfirm;			// 确定按钮
	
	private EditText m_edName;						// 退款人姓名
	
	private String m_strName;						// 退款人姓名
	
	// 传递退款人姓名
	public static final String APPLYREFUND_NAME = "applyrefund_name";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_applyrefund);
		
		m_Context = this;
		m_appContext = (AppContext) this.getApplication();
		
		initData();
		initControl();
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 退款人姓名
		m_edName = (EditText)findViewById(R.id.applyrefund_et_name);
		if(null != m_strName && !m_strName.isEmpty()){
			m_edName.setText(m_strName);
		}
		
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.applyrefund_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 确定按钮
		m_btnConfirm = (RelativeLayout)findViewById(R.id.applyrefund_lp_confirm);
		m_btnConfirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	

			}
		});
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_strName = bundle.getString(APPLYREFUND_NAME);
		}
	}
}

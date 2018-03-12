package com.qianniu.zhaopin.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.DeliveryAddressInfoData;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.thp.UmShare;

/**
 * 新增/编辑收货地址界面
 * @author wuzy
 *
 */
public class DeliveryAddressActivity extends BaseActivity {
	
	private Context m_Context;
	private AppContext m_appContext;

	private ImageView m_btnBack;					// 返回按钮
	private ImageView m_btnSubmit;					// 提交按钮
	private Button m_btnSave;						// 保存按钮
	private Button m_btnDel;						// 删除按钮
	private Button m_btnSetDefault;					// 设置为默认按钮
	
	private TextView m_tvTitle;						// 标题
	private EditText m_tvName;						// 收货人名称
	private EditText m_tvTel;						// 联系方式
	private EditText m_tvPostCode;					// 邮政编码
	private EditText m_tvAddress;					// 详细地址
	
	private int m_nType = 0;						// 界面类型
	
	private String m_strName;						// 收货人
	private String m_strTel;						// 联系方式
	private String m_strPostCode;					// 邮政编码
	private String m_strAddress;					// 详细地址
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deliveryaddress);

		m_Context = this;
		m_appContext = (AppContext) getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
		
		// 初始化数据
		ininData();
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
		initTextView();
		
		// 返回按钮
		m_btnBack = (ImageView)findViewById(R.id.deliveryaddress_btn_back);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 提交按钮
		m_btnSubmit = (ImageView)findViewById(R.id.deliveryaddress_btn_submit);
		m_btnSubmit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		
		// 保存按钮
		m_btnSave = (Button)findViewById(R.id.deliveryaddress_btn_save);
		m_btnSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		
		// 删除按钮
		m_btnDel = (Button)findViewById(R.id.deliveryaddress_btn_del);
		m_btnDel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		
		// 设置为默认按钮
		m_btnSetDefault = (Button)findViewById(R.id.deliveryaddress_btn_setdefault);
		m_btnSetDefault.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		
		switch(m_nType){
		case HeadhunterPublic.DELIVERYADDRESS_TYPE_EDIT:	// 编辑收货地址界面
			{
				if(null != m_tvTitle){
					m_tvTitle.setText(getString(
							R.string.str_deliveryaddress_edittitle));
				}
				
				// 显示删除收货地址按钮
				if(null != m_btnDel){
					m_btnDel.setVisibility(View.VISIBLE);
				}
				
				// 显示设置默认收货地址按钮
				if(null != m_btnSetDefault){
					m_btnSetDefault.setVisibility(View.VISIBLE);
				}
				
				// 显示提交按钮
				if(null != m_btnSubmit){
					m_btnSubmit.setVisibility(View.VISIBLE);
				}
			}
			break;
		case HeadhunterPublic.DELIVERYADDRESS_TYPE_NEW:		// 新增收货地址界面
		default:
			{
				if(null != m_btnSave){
					m_btnSave.setVisibility(View.VISIBLE);
				}
			}
			break;
		}
	}
	
	/**
	 * 初始化文本控件
	 */
	private void initTextView(){
		// 标题
		m_tvTitle = (TextView)findViewById(R.id.deliveryaddress_tv_title);
		
		// 收货人名称
		m_tvName = (EditText)findViewById(R.id.deliveryaddress_et_name);
		if(m_tvName != null && m_strName != null && !m_strName.isEmpty()){
			m_tvName.setText(m_strName);
		}
		
		// 联系方式
		m_tvTel = (EditText)findViewById(R.id.deliveryaddress_et_tel);
		if(m_tvTel != null && m_strTel != null && !m_strTel.isEmpty()){
			m_tvTel.setText(m_strTel);
		}
		
		// 邮政编码
		m_tvPostCode = (EditText)findViewById(R.id.adddeliveryaddress_et_postcode);
		if(m_tvPostCode != null && m_strPostCode!= null && !m_strPostCode.isEmpty()){
			m_tvPostCode.setText(m_strPostCode);
		}
		
		// 详细地址
		m_tvAddress = (EditText)findViewById(R.id.deliveryaddress_et_address);
		if(m_tvAddress != null && m_strAddress != null && !m_strAddress.isEmpty()){
			m_tvAddress.setText(m_strAddress);
		}
	}
	
	/**
     * 初始化数据
     */
    private void ininData(){
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			DeliveryAddressInfoData daid = (DeliveryAddressInfoData)bundle.getSerializable(
					HeadhunterPublic.DELIVERYADDRESS_DATATRANSFER_DATA);
			if(null != daid){
				// 收货人
				m_strName = daid.getStrName();
				// 联系方式
				m_strTel = daid.getStrTel();
				// 邮政编码
				m_strPostCode = daid.getStrPostCode();
				// 详细地址
				m_strAddress = daid.getStrAddress();					
			}
			
			m_nType = bundle.getInt(
					HeadhunterPublic.DELIVERYADDRESS_DATATRANSFER_TYPE);
		} 
    }
}

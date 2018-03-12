package com.qianniu.zhaopin.app.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.app.ui.integrationmall.IntegrationMallActivity;
import com.qianniu.zhaopin.thp.UmShare;

/**
 * 积分管理界面
 * @author wuzy
 *
 */
public class MyIntegralActivity extends BaseActivity{
	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageView m_btnBack;					// 返回按钮
	
	private TextView m_tvQianniuintegral;			// 积分规则文本框
	
	private ImageView m_imgComplete;
	private ImageView m_imgPhoto;					// 头像控件
	
	private TextView m_tvLevel;						// 等级
	private TextView m_tvIntegral;					// 总积分
	
	private RelativeLayout m_rlConversionrecord;	// 兑换记录
	private RelativeLayout m_rlAddress;				// 收货地址 
	private RelativeLayout m_rlRecharge;			// 我要充值
	private RelativeLayout m_rlGoMall;				// 去商城
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myintegralactivity);

		m_Context = this;
		m_appContext = (AppContext) getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
		
		// 初始化控件
		initControl();
		// 获取数据
		initData();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
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
	 * 初始化文本控件
	 */
	private void initTextView(){
		// 等级
		m_tvLevel = (TextView)findViewById(R.id.myintegral_tv_level);
		m_tvLevel.setText(String.format(getString(R.string.str_myintegral_level),
				"1"));
		
		// 总积分
		m_tvIntegral = (TextView)findViewById(R.id.myintegral_tv_integral);	
	}
	
	/**
	 * 初始化头像相关控件
	 */
	private void initHeadSculpture(){
		m_imgComplete = (ImageView)findViewById(R.id.myintegral_img_complete);
		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(
				this, 0,
				Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
				Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
				Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
				Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
		m_imgComplete.setImageBitmap(completebmp);

		m_imgPhoto = (ImageView) findViewById(R.id.myintegral_img_headphoto);
		Bitmap photobmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.person_photo_normal_big);
		photobmp = ImageUtils.createRoundHeadPhoto(this,
				photobmp);
		m_imgPhoto.setImageBitmap(photobmp);
		m_imgPhoto.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--我的账号--上传头像按钮
//				UmShare.UmStatistics(m_Context, "MyIntegral_UploadHeadButton");
//				
//				showSetPhotoDialog();
			}
		});
	}

	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 初始化文本控件
		initTextView();
		// 初始化头像相关控件
		initHeadSculpture();
		
		// 返回按钮
		m_btnBack = (ImageView)findViewById(R.id.myintegral_btn_back);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 积分规则
		m_tvQianniuintegral = (TextView)findViewById(R.id.myintegral_tv_qianniuintegral);
		m_tvQianniuintegral.setText(Html.fromHtml("<u>"+getString(R.string.str_qianniuintegral_title)+"</u>"));
		m_tvQianniuintegral.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showTipsWindows(v);
			}
		});
		
		// 兑换记录 
		m_rlConversionrecord = (RelativeLayout)findViewById(R.id.myintegral_rl_conversionrecord);
		m_rlConversionrecord.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startConversionRecordActivity();
			}
		});
		
		// 收货地址
		m_rlAddress = (RelativeLayout)findViewById(R.id.myintegral_rl_address);
		m_rlAddress.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startDeliveryAddressListActivity();
			}
		});
		
		// 我要充值 
		m_rlRecharge = (RelativeLayout)findViewById(R.id.myintegral_rl_recharge);
		m_rlRecharge.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 进入积分充值界面
				startIntegralRechargeActivity();
			}
		});
		
		// 去商城
		m_rlGoMall = (RelativeLayout)findViewById(R.id.myintegral_lp_gomall);
		m_rlGoMall.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 进入商城界面
				startIntegrationMallActivity();
			}
		});
	}
	
	/**
	 * 获取数据
	 */
	private void initData(){
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
//			m_Handler.sendMessage(m_Handler.obtainMessage(
//					HeadhunterPublic.MSG_MYACCOUN_GETTDATA_NONETWORKCONNECT));
			return;
		}
		
//		showProgressDialog();
//		
//		new Thread() {
//			public void run() {
//				getMyAccountInfo();
//			}
//		}.start();
	}
	
	/**
	 * 弹出积分规则对话框
	 * @param view
	 */
	private void showTipsWindows(View view) {
		UIHelper.showAgreementDialog(MyIntegralActivity.this,
				R.string.str_qianniuintegral_title, R.string.str_qianniuintegral_rule,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
						}
						dialog.dismiss();
					}
				});
	}
	
	/**
	 * 进入积分兑换记录界面
	 */
	private void startConversionRecordActivity(){
		Intent intent = new Intent(this, ConversionRecordActivity.class);
		startActivity(intent);
	}
	
	/**
	 * 进入收货地址列表界面
	 */
	private void startDeliveryAddressListActivity(){
		Intent intent = new Intent(this, DeliveryAddressListActivity.class);
		startActivity(intent);		
	}
	
	/**
	 * 进入积分充值界面
	 */
	private void startIntegralRechargeActivity(){
		Intent intent = new Intent(this, IntegralRechargeActivity.class);
		startActivity(intent);		
	}
	
	/**
	 * 进入商城界面
	 */
	private void startIntegrationMallActivity(){
		Intent intent = new Intent(this, IntegrationMallActivity.class);
		startActivity(intent);		
	}
}

package com.qianniu.zhaopin.app.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.R;

public class MapActivity extends BaseActivity {
	private Context m_Context;
	
	private ImageButton m_btnBack;			// 返回按钮
	
	private String m_strCompanyMapUrl;			// 公司地图url
	
	private WebView m_wvMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_map);
		
		m_Context = this;
		
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_strCompanyMapUrl = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYMAPURL);
		}
		
		init();
	}
	
	/**
	 * 初始化按钮
	 */
	private void initBtn(){
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.map_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
//		progressBar = (ProgressBar) findViewById(R.id.map_tv_head_progress);
		initProgressBar(R.id.map_tv_head_progress);
	}
	
	/**
	 * 初始化webview
	 */
	private void initwebView(){
		m_wvMap = (WebView)findViewById(R.id.map_webview);
		
		if(null == m_strCompanyMapUrl || m_strCompanyMapUrl.isEmpty()){
			m_strCompanyMapUrl = ConfigOptions.getCompanymapSite();
			m_wvMap.loadUrl(m_strCompanyMapUrl);			
		}else{
			m_wvMap.loadUrl(m_strCompanyMapUrl);
		}
		
		m_wvMap.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				showProgressBar();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				goneProgressBar();
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
//				return super.shouldOverrideUrlLoading(view, url);
				view.loadUrl(m_strCompanyMapUrl);                                       
                return true;
			}
		});
		
		// JavaScript使能(如果要加载的页面中有JS代码，则必须使能JS)
		WebSettings webSettings = m_wvMap.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setPluginState(WebSettings.PluginState.ON);
		m_wvMap.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//		
//		// 添加js交互接口类，并起别名 buttonlistner  
//		m_wvRewardInfo.addJavascriptInterface(new JavascriptInterface(this), "buttonlistner");  
//		m_wvRewardInfo.setWebViewClient(new MyWebViewClient());  
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		initBtn();
		initwebView();
	}
}

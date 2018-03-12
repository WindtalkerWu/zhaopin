package com.qianniu.zhaopin.app.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.service.ConnectionService;

public class MoreModuleActivity extends BaseActivity{


	private ImageView back;
	private WebView appRecommendContent;
	private String url = URLs.SYSTEM_BUILDER;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moremodule);
		initView();
	}

	private void initView() {
		initProgressBar(R.id.app_head_progress);
		back = (ImageView) findViewById(R.id.app_goback);
		appRecommendContent = (WebView) findViewById(R.id.app_webview);
//		progressBar = (ProgressBar) findViewById(R.id.app_recommend_head_progress);
		setListener();
		initWebView();
	}

	private void initWebView() {
		appRecommendContent.setVerticalScrollBarEnabled(true);
		appRecommendContent.getSettings().setJavaScriptEnabled(true);
		appRecommendContent.setWebViewClient(new WebViewClient() {
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
	
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		loadUrl();
	}

	private void loadUrl() {
		if(!UIHelper.isNetworkConnected((AppContext)getApplicationContext())){
			return;
		}
		appRecommendContent.loadUrl(url);
	}

	private void setListener() {
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!goBack()) {
					finish();
				}
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && goBack()) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private boolean goBack() {
		if (appRecommendContent.canGoBack()) {
			appRecommendContent.goBack();// 返回前一个页面
			return true;
		}
		return false;
	}

}

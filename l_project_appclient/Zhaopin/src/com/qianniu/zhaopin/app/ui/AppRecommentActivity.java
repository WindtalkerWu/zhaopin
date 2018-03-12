package com.qianniu.zhaopin.app.ui;


import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.service.ConnectionService;
import com.qianniu.zhaopin.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class AppRecommentActivity extends BaseActivity {

	private ImageView back;
	private WebView appRecommendContent;
	private String recommendUrl = URLs.HTTP + URLs.HOST + "/apprecommend/list";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_recommend);
		initView();
	}

	private void initView() {
		initProgressBar(R.id.app_recommend_head_progress);
		back = (ImageView) findViewById(R.id.app_recommend_goback);
		appRecommendContent = (WebView) findViewById(R.id.app_recommend_webview);
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
				if (url.lastIndexOf(".apk") != -1) {
					startDownloading(url);
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		loadUrl();
	}

	private void loadUrl() {
		if(!UIHelper.isNetworkConnected((AppContext)getApplicationContext())){
			return;
		}
		appRecommendContent.loadUrl(recommendUrl);
	}
	private void startDownloading(String url) {
		ConnectionService.startServiceDownload(AppRecommentActivity.this, url, null);
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

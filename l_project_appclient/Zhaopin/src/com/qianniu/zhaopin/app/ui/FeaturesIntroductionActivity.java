package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class FeaturesIntroductionActivity extends BaseActivity {

	private ImageView back;
	private WebView featuresIntroductionContent;
	private String featuresIntroductionUrl = URLs.HTTP + URLs.HOST + "/faq";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_features_introduction);
		initView();
	}
	private void initView() {
		initProgressBar(R.id.features_introduction_head_progress);
		back = (ImageView) findViewById(R.id.features_introduction_goback);
		featuresIntroductionContent = (WebView) findViewById(R.id.features_introduction_webview);
		setListener();
		initWebView();
	}
	private void initWebView() {
		featuresIntroductionContent.setVerticalScrollBarEnabled(true);
		featuresIntroductionContent.getSettings().setJavaScriptEnabled(true);
		featuresIntroductionContent.setWebViewClient(new WebViewClient(){
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
		featuresIntroductionContent.loadUrl(featuresIntroductionUrl);
	}
	private void setListener() {
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}

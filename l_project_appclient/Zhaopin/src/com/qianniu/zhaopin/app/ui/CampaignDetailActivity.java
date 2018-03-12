package com.qianniu.zhaopin.app.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.thp.UmShare;

public class CampaignDetailActivity extends BaseActivity{


	private Context m_Context;
	private AppContext appContext;// 全局Context
	private WebView mwebview;
	private ImageButton btn_back;
	private ImageButton btn_share;
	private TextView tv_title;
	private ProgressDialog mprogressDialog;
	private UmShare m_umShare;				// 友盟分享
	
	private String mtitle;
	private String mUrl;
	private String minfoid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_Context = this;
		appContext = (AppContext) this.getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	
		mtitle = getIntent().getStringExtra("info_title");
		mUrl = getIntent().getStringExtra("info_url");
		minfoid = getIntent().getStringExtra("info_id");

		setContentView(R.layout.activity_deatail_webview);
		
		m_umShare = new UmShare();
		
		mprogressDialog = new ProgressDialog(this);
		initProgressBar(R.id.foruminfo_detail_head_progress);
//		progress_head = (ProgressBar) findViewById(R.id.foruminfo_detail_head_progress);
		btn_back = (ImageButton) findViewById(R.id.foruminfo_detail_goback);
		btn_back.setOnClickListener(UIHelper.finish(this));
		btn_share = (ImageButton) findViewById(R.id.foruminfo_detail_share);
		btn_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计--号外详情--分享按钮
				UmShare.UmStatistics(m_Context, "ForumInfoDetail_ShareButton");
				// TODO Auto-generated method stub
				if(null != m_umShare){
					m_umShare.umOpenShare(CampaignDetailActivity.this);
				}
				//UIHelper.showShareDialog(CampaignDetailActivity.this, "我在牵牛网分享活动给您","我在牵牛网分享活动给您。 \n" +mUrl);
				UIHelper.showShareDialog(CampaignDetailActivity.this, "我在牵牛网分享活动给您",
						"我在牵牛网分享活动给您。 \n", mUrl);
			}
		});
		mwebview = (WebView) findViewById(R.id.foruminfo_detail_webview);

		
		tv_title = (TextView) findViewById(R.id.foruminfo_detail_title);
		if (mtitle != null)
			tv_title.setText(mtitle);
		initWebView();

	}

	public void initWebView() {

		final WebSettings settings = mwebview.getSettings();
		mwebview.getSettings().setUseWideViewPort(true);
		mwebview.getSettings().setJavaScriptEnabled(true);// 可用JS
		mwebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mwebview.getSettings().setPluginState(WebSettings.PluginState.ON);
		mwebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mwebview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(final WebView view,
					final String url) {
				// 载入网页
				loadurl(view, url);
				return true;
			}// 重写点击动作,用webview载入

			public void onPageFinished(WebView view, String url) {
				view.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");
			}
		});
		
		mwebview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
				if (progress == 100) {
					handler.sendEmptyMessage(1);// 如果全部载入,隐藏进度对话框
				}
				super.onProgressChanged(view, progress);
			}

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				// TODO Auto-generated method stub
				super.onShowCustomView(view, callback);
			}
		});
		if (mUrl != null)
			loadurl(mwebview, mUrl);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 0:
/*					mprogressDialog.setMessage(R.string.dialog_dataloadmsg);
					mprogressDialog.show();*/
					showProgressBar();
					break;
				case 1:
/*					mprogressDialog.dismiss();*/
					goneProgressBar();
					break;
				}
			}
			super.handleMessage(msg);
		}
	};

	public void loadurl(final WebView view, final String url) {

		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0);
				view.loadUrl(url);// 载入网页
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if ((keyCode == KeyEvent.KEYCODE_BACK) && mwebview.canGoBack()) {
			mwebview.goBack();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.onDestroy();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		m_umShare.UmActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 友盟统计
		UmShare.UmResume(m_Context);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 友盟统计
		UmShare.UmPause(m_Context);
	}

}

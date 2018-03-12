package com.qianniu.zhaopin.app.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

public class ResumePreviewActivity extends BaseActivity {

	private Context m_Context;
	private AppContext appContext;// 全局Context
	private WebView mwebview;
	private ImageButton btn_back;
	private ImageButton btn_share;
	private ImageButton btn_edit;
	private TextView tv_title;

	private String mtitle;
	private String mUrl;
	private String mresumeId;
	private boolean bedit = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_Context = this;
		appContext = (AppContext) this.getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		mUrl = bundle.getString("url");
		mresumeId = bundle.getString("resumeId");

		setContentView(R.layout.resume_preview);
		btn_back = (ImageButton) findViewById(R.id.resume_preview_goback);
		btn_back.setOnClickListener(mlistener);
		btn_share = (ImageButton) findViewById(R.id.resume_preview_share);
		btn_share.setOnClickListener(mlistener);
		btn_edit = (ImageButton) findViewById(R.id.resume_preview_edit);
		btn_edit.setOnClickListener(mlistener);
		mwebview = (WebView) findViewById(R.id.resume_detail_webview);

		initProgressBar(R.id.resume_detail_head_progress);
		// progress_head = (ProgressBar)
		// findViewById(R.id.resume_detail_head_progress);
		tv_title = (TextView) findViewById(R.id.resume_detail_title);
		tv_title.setText("简历预览");
		/*
		 * if (mtitle != null) tv_title.setText(mtitle);
		 */
		initWebView();

	}

	public void initWebView() {

		final WebSettings settings = mwebview.getSettings();
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
		if (mUrl != null) {
			String newurl = new String(mUrl);
			if (newurl.indexOf("?", 0) == -1) {
				newurl += "?platform=1";
			} else {
				newurl += "&platform=1";
			}

			newurl += "&at=" + appContext.getAccessToken();
			loadurl(mwebview, newurl);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 0:
					showProgressBar();
					break;
				case 1:
					goneProgressBar();
					break;
				}
			}
			super.handleMessage(msg);
		}
	};

	public void loadurl(final WebView view, final String url) {
		/*
		 * new Thread() { public void run() { handler.sendEmptyMessage(0);
		 * view.loadUrl(url);// 载入网页 } }.start();
		 */
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0);
				view.loadUrl(url);// 载入网页
			}
		});

	}

	OnClickListener mlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.resume_preview_goback: {
				if (bedit) {
					setResultAndFinish(RESULT_OK);
				} else {
					quit();
				}
			}
				break;
			case R.id.resume_preview_share: {
				// 友盟统计--简历预览--分享按钮
				UmShare.UmStatistics(m_Context, "RewardPreview_ShareButton");
				// UIHelper.showShareDialog(ResumePreviewActivity.this,
				// "我在牵牛网分享简历给您","我在牵牛网分享简历给您,如果您对我的简历感兴趣，可以随时打电话联系。 \n" +mUrl);

				Intent data = new Intent(Intent.ACTION_SENDTO);
				data.setData(Uri.parse("mailto:"));
				/*
				 * data.putExtra(Intent.EXTRA_EMAIL, new String[]{});
				 * data.putExtra(Intent.EXTRA_CC, new String[]{"111@qq.com"});
				 * data.putExtra(Intent.EXTRA_BCC, new String[]{"111@qq.com"});
				 */
				data.putExtra(Intent.EXTRA_SUBJECT, "我在牵牛网分享简历给您");
				data.putExtra(Intent.EXTRA_TEXT,
						"我在牵牛网分享简历给您,如果您对我的简历感兴趣，可以随时打电话联系。 \n" + mUrl);
				try {
					startActivity(data);
				} catch (ActivityNotFoundException  e) {
					// TODO: handle exception
					UIHelper.ToastMessage(m_Context, "没有邮箱应用，暂时无法分享！");
				}
				
			}
				break;
			case R.id.resume_preview_edit: {
				// 友盟统计--简历预览--简历编辑按钮
				UmShare.UmStatistics(m_Context, "RewardPreview_EditButton");

				if (mresumeId == null || mresumeId.length() == 0) {
					UIHelper.ToastMessage(m_Context, "当前简历无法编辑");
				} else {
					int resumeid = 0;
					resumeid = StringUtils.toInt(mresumeId, 0);
					UIHelper.showMyResumeEditActivityForResult(
							ResumePreviewActivity.this, resumeid);
				}

			}
				break;

			}
		}
	};

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
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {

		case ActivityRequestCode.RESULT_ACTIVITY_NEWRESUME: {
			bedit = true;
			mwebview.reload();
		}
			break;

		}
	}

	private void setResultAndFinish(int resultCode) {
		Intent intent = new Intent();
		intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_RESUMEID, mresumeId);
		setResult(resultCode, intent);
		((ResumePreviewActivity) m_Context).quit();
	}

	public void quit() {
		this.finish();
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

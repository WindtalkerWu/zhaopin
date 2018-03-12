package com.qianniu.zhaopin.thp.ui;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.tencent.weibo.sdk.android.api.util.BackGroudSeletor;
import com.tencent.weibo.sdk.android.api.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TencentWeiboAuthorize extends Activity{
	private ImageButton m_btnBack;		// 返回按钮
	
	WebView m_webView;
	
	String _url;
	String _fileName;
	public static int WEBVIEWSTATE_1 = 0;
	int webview_state = 0;
	String path;
	Dialog _dialog;
	public static final int ALERT_DOWNLOAD = 0;
	public static final int ALERT_FAV = 1;
	public static final int PROGRESS_H = 3;
	public static final int ALERT_NETWORK = 4;
	private ProgressDialog dialog;
	private LinearLayout layout = null;
	private String redirectUri = null;
	private String clientId = null;
	private boolean isShow = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tencentweiboauthorize);
		
		if (!Util.isNetworkAvailable(this)) {
			TencentWeiboAuthorize.this.showDialog(ALERT_NETWORK);
		} else {
			try {
				// Bundle bundle = getIntent().getExtras();
				clientId = Util.getConfig().getProperty("APP_KEY");// bundle.getString("APP_KEY");
				redirectUri = Util.getConfig().getProperty("REDIRECT_URI");// bundle.getString("REDIRECT_URI");
				if (clientId == null || "".equals(clientId)
						|| redirectUri == null || "".equals(redirectUri)) {
					Toast.makeText(TencentWeiboAuthorize.this, "请在配置文件中填写相应的信息",
							Toast.LENGTH_SHORT).show();
				}
				int state = (int) Math.random() * 1000 + 111;
				path = "https://open.t.qq.com/cgi-bin/oauth2/authorize?client_id="
						+ clientId
						+ "&response_type=token&redirect_uri="
						+ redirectUri + "&state=" + state;
				init();
			} catch (Exception e) {
				e.printStackTrace();
				setResult(RESULT_CANCELED);
				finish();
			}
		}
	}

	private void init(){
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setMessage("请稍后...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		dialog.show();
		
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.tencentweiboauthorize_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		//	
		m_webView = (WebView)findViewById(R.id.tencentweiboauthorize_webview);
		
		WebSettings webSettings = m_webView.getSettings();
		m_webView.setVerticalScrollBarEnabled(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(false);
		m_webView.loadUrl(path);
		m_webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				// if (dialog!=null&& !dialog.isShowing()) {
				// dialog.show();
				// }

			}

		});
		m_webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				// super.onPageFinished(view, url);
				if (url.indexOf("access_token") != -1 && !isShow) {
					jumpResultParser(url);
				}
				if (dialog != null && dialog.isShowing()) {
					dialog.cancel();
				}

			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.indexOf("access_token") != -1 && !isShow) {
					jumpResultParser(url);
				}
				return false;
			}
		});
	}

	/**
	 * 
	 * 获取授权后的返回地址，并对其进行解析
	 */
	public void jumpResultParser(String result) {

		String resultParam = result.split("#")[1];
		String params[] = resultParam.split("&");
		String accessToken = params[0].split("=")[1];
		String expiresIn = params[1].split("=")[1];
		String openid = params[2].split("=")[1];
		String openkey = params[3].split("=")[1];
		String refreshToken = params[4].split("=")[1];
		String state = params[5].split("=")[1];
		String name = params[6].split("=")[1];
		String nick = params[7].split("=")[1];
		Context context = this.getApplicationContext();
		if (accessToken != null && !"".equals(accessToken)) {
			Util.saveSharePersistent(context, "ACCESS_TOKEN", accessToken);
			Util.saveSharePersistent(context, "EXPIRES_IN", expiresIn);// accesstoken过期时间，以返回的时间的准，单位为秒，注意过期时提醒用户重新授权
			Util.saveSharePersistent(context, "OPEN_ID", openid);
			Util.saveSharePersistent(context, "OPEN_KEY", openkey);
			Util.saveSharePersistent(context, "REFRESH_TOKEN", refreshToken);
			Util.saveSharePersistent(context, "NAME", name);
			Util.saveSharePersistent(context, "NICK", nick);
			Util.saveSharePersistent(context, "CLIENT_ID", clientId);
			Util.saveSharePersistent(context, "AUTHORIZETIME",
					String.valueOf(System.currentTimeMillis() / 1000l));
			Toast.makeText(TencentWeiboAuthorize.this, "授权成功", Toast.LENGTH_SHORT).show();
			
			setResult(RESULT_OK);
			
			this.finish();
			isShow = true;
		}
	}

	Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 100:
				// Log.i("showDialog", "showDialog");
				TencentWeiboAuthorize.this.showDialog(ALERT_NETWORK);
				break;
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

		case PROGRESS_H:
			_dialog = new ProgressDialog(this);
			((ProgressDialog) _dialog).setMessage("加载中...");
			break;
		case ALERT_NETWORK:
			AlertDialog.Builder builder2 = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
			builder2.setTitle("网络连接异常，是否重新连接？");
			builder2.setPositiveButton("是",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (Util.isNetworkAvailable(TencentWeiboAuthorize.this)) {
								m_webView.loadUrl(path);
							} else {
								Message msg = Message.obtain();
								msg.what = 100;
								handle.sendMessage(msg);
							}
						}

					});
			builder2.setNegativeButton("否",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							TencentWeiboAuthorize.this.finish();
						}
					});
			_dialog = builder2.create();
			break;
		}
		return _dialog;
	}
}

package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.view.AdZoneView;
import com.qianniu.zhaopin.app.view.ExplainInfoMiddlePop;
import com.qianniu.zhaopin.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class testActivity extends BaseActivity {

	private static final String TAG = "TestActivity";
	private WebView mwebview;
	private Context mcontext;
	private ForeachThread mforeachThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		setContentView(R.layout.testlayout);
		Intent intent = getIntent();
		String action = intent.getAction();
		ViewGroup vg = (ViewGroup) findViewById(R.id.container);
/*		AdZoneView adzoneView = new AdZoneView(this, AdZoneView.ADZONE_ID_EXTRA);
		vg.addView(adzoneView);*/
		final Button btn = (Button) findViewById(R.id.changebtn);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
/*				if(mforeachThread != null){
					mforeachThread.setBexit(true);
				}*/

				//startlogin(testActivity.this);
				ExplainInfoMiddlePop pop = new ExplainInfoMiddlePop(mcontext);
				//pop.showAtLocation(btn,Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM ,0,10);
				pop.setExplainInfoText("默认简历afwefawfawefwefffwefwafweafw");
				pop.showAsDropDown(btn ,-75*3+13*3,10);
				pop.update();
			}
		});
		//startForeach();
		// getImgBmp(new RoundImg());
		/*
		 * setContentView(R.layout.foruminfodetail); mwebview = (WebView)
		 * findViewById(R.id.foruminfo_detail_webview); final WebSettings
		 * settings = mwebview.getSettings();
		 * mwebview.getSettings().setJavaScriptEnabled(true);// 可用JS
		 * mwebview.getSettings
		 * ().setJavaScriptCanOpenWindowsAutomatically(true);
		 * mwebview.getSettings().setPluginState(WebSettings.PluginState.ON);
		 * mwebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		 * mwebview.setWebViewClient(new WebViewClient() { public boolean
		 * shouldOverrideUrlLoading(final WebView view, final String url) {
		 * mwebview.loadUrl(url);// 载入网页 return true; }// 重写点击动作,用webview载入
		 * 
		 * public void onPageFinished(WebView view, String url) { view.loadUrl(
		 * "javascript:(function() { document.getElementsByTagName('video')[0].play(); })()"
		 * ); } }); mwebview.loadUrl("http://www.google.com");
		 */

	}

	/*
	 * private Bitmap getImgBmp(GraphicImgCreator creator){
	 * creator.creator(null); return null;
	 * 
	 * } public interface GraphicImgCreator{ public Bitmap creator(Bitmap bmp);
	 * } private class RoundImg implements GraphicImgCreator{
	 * 
	 * @Override public Bitmap creator(Bitmap bmp) { // TODO Auto-generated
	 * method stub MyLogger.i("RoundImg", "RoundImg is ok"); return null; }
	 * 
	 * }
	 */
	
	
	private void startForeach() {
		if (mforeachThread != null && mforeachThread.isAlive()) {
			synchronized (lock) {
				lock.notify();
			}
			
		} else {
			mforeachThread = new ForeachThread();
			mforeachThread.start();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case ActivityRequestCode.RESULT_ACTIVITY_LOGIN :{
			MyLogger.i(TAG, "RESULT_ACTIVITY_LOGIN##code =" +ActivityRequestCode.RESULT_ACTIVITY_LOGIN);
		}
			break;

		}
	}
	private void startlogin(){
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivityForResult(intent,ActivityRequestCode.RESULT_ACTIVITY_LOGIN );
	}
	private void startlogin(Activity activity){
		UIHelper.ToastMessage(activity, R.string.usermanager_unlogin);
		UIHelper.showLoginActivityForResult(activity);
	}
	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
			case 1: {
				this.sendEmptyMessageDelayed(5, 5 * 1000);
			}
				break;

			case 5: {
				startForeach();
			}
				break;
			case 6:{
				startlogin();

			}break;
			}

		}

	};
	private Object lock = new Object();
	
	class ForeachThread extends Thread {
		private boolean bexit = false;

		public final boolean isBexit() {
			return bexit;
		}

		public final void setBexit(boolean bexit) {
			this.bexit = bexit;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while (!bexit) {
				// Message msg = new Message();
				synchronized (lock){
				mhandler.sendEmptyMessage(1);
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					AppException.http(e);
				}
				}
			}
			mhandler.sendEmptyMessage(0);
		}

	}
}

package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppManager;
import com.qianniu.zhaopin.app.bean.VersionData;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

/**
 * 应用程序Activity的基�?
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-9-18
 */
public class BaseActivity extends Activity {
	public ThreadPoolController threadPool ;
	// 是否允许全屏
	private boolean allowFullScreen = true;

	// 是否允许�?��
	private boolean allowDestroy = true;

	private View view;
	
	private ProgressDialog progressDialog;

	protected AppContext mContext;
	protected Context mActivityContext;
//	protected ProgressBar progressBar;

	protected View progressBar;
	
	// ProgressDialog是否显示标识
	private boolean m_bShowPD = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		allowFullScreen = true;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏 
		// 添加Activity到堆�?
		AppManager.getAppManager().addActivity(this);
		mContext = (AppContext)getApplicationContext();
		mActivityContext = this;
	}

	@Override
	protected void onDestroy() {
		progressDialog = null;
		super.onDestroy();

		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().removeActivity(this);
	}

	public boolean isAllowFullScreen() {
		return allowFullScreen;
	}

	public void setFullscreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	}
	/**
	 * 设置是否可以全屏
	 * 
	 * @param allowFullScreen
	 */
	public void setAllowFullScreen(boolean allowFullScreen) {
		this.allowFullScreen = allowFullScreen;
	}

	public void setAllowDestroy(boolean allowDestroy) {
		this.allowDestroy = allowDestroy;
	}

	public void setAllowDestroy(boolean allowDestroy, View view) {
		this.allowDestroy = allowDestroy;
		this.view = view;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && view != null) {
			view.onKeyDown(keyCode, event);
			if (!allowDestroy) {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	public void initProgressBar(int resId) {
		ViewStub stub = (ViewStub)findViewById(resId);
		progressBar = stub.inflate();
		progressBar.setVisibility(View.GONE);
	}

	public void showProgressDialog() {
		if(m_bShowPD){
			return;
		}
		
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}
		progressDialog.setMessage("请稍候...");
		progressDialog.show();
		m_bShowPD = true;
	}
	public void showProgressDialog(int resId) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}
		progressDialog.setMessage(getResources().getString(resId));
		progressDialog.show();
	}
	public void showProgressDialog(String message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}
		progressDialog.setMessage(message);
		progressDialog.show();
	}
	public void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			m_bShowPD = false;
		}
	}
	public void showProgressBar() {
		if (progressBar != null && progressBar.getVisibility() != View.VISIBLE) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	public void hideProgressBar() {
		if (progressBar != null) {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
	public void goneProgressBar() {
		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}
	}

	public VersionData getCurrentVersion() {
		return ((AppContext)getApplicationContext()).getCurrentVersion();
	}
}

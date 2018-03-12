package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppManager;
import com.qianniu.zhaopin.app.common.ThreadPoolController;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class BaseFragmentActivity extends FragmentActivity{
	public ThreadPoolController threadPool;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏 
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		AppManager.getAppManager().finishActivity(this);
	}
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}

	
}

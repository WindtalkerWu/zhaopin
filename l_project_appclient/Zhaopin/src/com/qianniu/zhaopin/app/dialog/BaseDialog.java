package com.qianniu.zhaopin.app.dialog;


import com.qianniu.zhaopin.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseDialog extends Dialog {

	public BaseDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent); 
		getWindow().setWindowAnimations(R.style.Theme_Dialog); //���ô��ڵ�������  
	}

	protected BaseDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().setWindowAnimations(R.style.Theme_Dialog); //���ô��ڵ�������  
	}

	public BaseDialog(Context context, int theme) {
		super(context, theme);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().setWindowAnimations(R.style.Theme_Dialog); //���ô��ڵ�������  
	}

	public void setTitle(int id) {
	}

	public void setText(int id) {
	}

	public void setText(String s) {
	}

	public void setConfirmClickListener(DialogInterface.OnClickListener onClickListener) {
		
	}

	public void setCancelClickListener(DialogInterface.OnCancelListener onClickListener) {
	}

	public void setConfirmButtonText(int id) {
	}

	public void setCancelButtonText(int id) {
	}

/*	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}*/

}

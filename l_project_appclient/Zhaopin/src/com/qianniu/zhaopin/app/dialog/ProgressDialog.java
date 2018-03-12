package com.qianniu.zhaopin.app.dialog;

import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class ProgressDialog extends BaseDialog{
	private static final String TAG = "ProgressDialog";
	private ImageView loadingIv;
	private TextView titleTv;
	private TextView contentTv;
	private Context context;
	private String mtextmsg;
	
	public ProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setContentView(R.layout.dialog_process);
		this.context = context;
		Animation animation = AnimationUtils.loadAnimation(context,R.anim.dialog_rotate);
		contentTv = (TextView) this.findViewById(R.id.progress_dialog_content);
		loadingIv = (ImageView) this.findViewById(R.id.progress_dialog_loading);
		this.findViewById(R.id.progress_dialog_loading).startAnimation(animation);
		this.setCanceledOnTouchOutside(false);
		//this.setCancelable(false);
	}
	
	public void setMessage(String msg){
		mtextmsg = msg;
		if(mtextmsg != null)
			contentTv.setText(mtextmsg);
	}
	
	public void setMessage(int msgid){
		
		mtextmsg = context.getString(msgid);
		if(mtextmsg != null)
			contentTv.setText(mtextmsg);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
        AnimationDrawable animationDrawable = (AnimationDrawable) loadingIv.getDrawable();
        animationDrawable.stop();
		super.dismiss();
		
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
        AnimationDrawable animationDrawable = (AnimationDrawable) loadingIv.getDrawable();
        animationDrawable.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MyLogger.i(TAG, "progressdialog KEYCODE_BACK");
			this.dismiss();
		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}
	
}

package com.qianniu.zhaopin.app.view;

import com.qianniu.zhaopin.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ExplainInfoLeftPop extends PopupWindow implements OnTouchListener{

	private Context mContext;
	private LayoutInflater mInflater;
	private TextView info_tv;
	
	public ExplainInfoLeftPop(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		mContext = context;
		mInflater = LayoutInflater.from(context);
		View contentView = mInflater.inflate(R.layout.explaininfo_left_layout, null);
		this.setContentView(contentView);
		this.setFocusable(true);
		this.setTouchable(true);
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		this.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		this.setTouchInterceptor(this);
		setAnimationStyle(R.anim.dialog_rotate);
		
		info_tv = (TextView) contentView.findViewById(R.id.info_tv);
	}

	public ExplainInfoLeftPop(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	public void setExplainInfoText(String infoStr){
		info_tv.setText(infoStr);
	}

	public void setExplainInfoText(int infoStr_id){
		info_tv.setText(infoStr_id);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		//Log.i("test", "onTouch action = "+event.getAction());
		if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			this.dismiss();
			return true;
		}
		return false;
	}

		

	

}

package com.qianniu.zhaopin.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class VerticalScrollInterceptLineralayout extends LinearLayout{


	public VerticalScrollInterceptLineralayout(Context context,
			AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public VerticalScrollInterceptLineralayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	private float xDistance, yDistance, xLast, yLast;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// MyLogger.i(TAG, "onTouchEvent " + ev.getAction());
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:

			final float curX = ev.getX();
			final float curY = ev.getY();
			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;
			if (yDistance > xDistance) {
				this.getParent().requestDisallowInterceptTouchEvent(true);
			} else {
				this.getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			this.getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
}

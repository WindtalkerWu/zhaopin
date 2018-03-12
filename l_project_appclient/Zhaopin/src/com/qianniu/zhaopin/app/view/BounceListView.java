package com.qianniu.zhaopin.app.view;


import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class BounceListView extends ListView {

	private static final int MAX_Y_OVERSCROLL_DISTANCE = 100;

	private Context mContext;
	private int mMaxYOverscrollDistance;
	private boolean action_up;
	  private int delY;
	  private int preY;

	public BounceListView(Context context) {
		super(context);
		mContext = context;
		initBounceListView();
	}

	public BounceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initBounceListView();
	}

	public BounceListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initBounceListView();
	}

	private void initBounceListView() {
		// get the density of the screen and do some maths with it on the max
		// overscroll distance
		// variable so that you get similar behaviors no matter what the screen
		// size

		final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		final float density = metrics.density;

		mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
		//����listview������ʱ�򣬱���Ϊ͸��ɫ��
		this.setCacheColorHint(0x00000000);
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
			int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		// This is where the magic happens, we have replaced the incoming
		// maxOverScrollY with our own custom variable mMaxYOverscrollDistance;

		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
				mMaxYOverscrollDistance, isTouchEvent);
	}
//	protected void onOverScrolled (int scrollX, int scrollY, boolean clampedX, boolean clampedY)
//	{
//		this.scrollBy(0, delY / 2);
//		  if (action_up) {
//		      this.scrollTo(0, 0);
//		  }
//	}
//	@Override
//	public boolean onTouchEvent(MotionEvent ev) {
//		// TODO Auto-generated method stub
//		final float y = ev.getY();
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			action_up = false;
//			break;
//
//		case MotionEvent.ACTION_UP:
//
//			action_up = true;
//
//			break;
//
//		case MotionEvent.ACTION_MOVE:
//			  delY = (int) (preY - y);
//	            preY = (int)y;
//
//			break;
//		}
//
//
//		return super.onTouchEvent(ev);
//	}

	
	
}

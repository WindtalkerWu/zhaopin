package com.qianniu.zhaopin.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageButton;

public class ImageRadioButton extends ImageButton implements Checkable {

	private boolean mChecked = false;
	private boolean mCheackable = true;
	private boolean mBroadcasting;
	private OnCheckedChangeListener mOnCheckedChangeListener;

	public ImageRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		// 先不用xml设参数，后续添加
		/*
		 * TypedArray a = context.obtainStyledAttributes(attrs,
		 * com.android.internal.R.styleable.CompoundButton, defStyle, 0);
		 * boolean checked = a.getBoolean(
		 * com.android.internal.R.styleable.CompoundButton_checked, false);
		 */

		// setChecked(false);
		/*
		 * this.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub toggle(); } });
		 */
	}

	public ImageRadioButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

		// TODO Auto-generated constructor stub
	}

	public ImageRadioButton(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int actiong = event.getAction();
		switch (actiong) {
		case MotionEvent.ACTION_DOWN: {
			if (mCheackable) {
				toggle();
			}
		}
			break;
		}
		return true;
	}

	public final boolean isCheackable() {
		return mCheackable;
	}

	public final void setCheackable(boolean Cheackable) {
		this.mCheackable = Cheackable;
	}

	@Override
	public boolean isChecked() {
		// TODO Auto-generated method stub
		return mChecked;
	}

	@Override
	public void setChecked(boolean checked) {
		// TODO Auto-generated method stub
		if (mChecked != checked) {
			mChecked = checked;
			setPressed(checked);
			refreshDrawableState();

			// Avoid infinite recursions if setChecked() is called from a
			// listener
			if (mBroadcasting) {
				return;
			}

			mBroadcasting = true;
			if (mOnCheckedChangeListener != null) {
				mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
			}
			mBroadcasting = false;
		}
	}

	@Override
	public void toggle() {
		// TODO Auto-generated method stub
		setChecked(!mChecked);
	}

	@Override
	public boolean performClick() {
		/*
		 * XXX: These are tiny, need some surrounding 'expanded touch area',
		 * which will need to be implemented in Button if we only override
		 * performClick()
		 */

		/* When clicked, toggle the state */
		toggle();
		return super.performClick();
	}

	/**
	 * Interface definition for a callback to be invoked when the checked state
	 * of a compound button changed.
	 */
	public static interface OnCheckedChangeListener {
		/**
		 * Called when the checked state of a compound button has changed.
		 * 
		 * @param buttonView
		 *            The compound button view whose state has changed.
		 * @param isChecked
		 *            The new checked state of buttonView.
		 */
		void onCheckedChanged(View buttonView, boolean isChecked);
	}
}

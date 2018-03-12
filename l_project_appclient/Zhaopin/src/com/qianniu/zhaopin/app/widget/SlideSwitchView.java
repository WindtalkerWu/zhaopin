package com.qianniu.zhaopin.app.widget;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.MyLogger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SlideSwitchView extends View implements View.OnTouchListener {
	private boolean nowChoose = false;// 记录当前按钮是否打开,true为打开,flase为关闭

	private boolean isChecked;

	private boolean onSlip = false;// 记录用户是否在滑动的变量

	private float downX, nowX;// 按下时的x,当前的x

	private Rect btn_on, btn_off;// 打开和关闭状态下,游标的Rect .

	private boolean isChangListenerOn = false;

	private OnChangedListener changListener;

	private Bitmap bg_on, bg_off, slip_btn;

	public SlideSwitchView(Context context) {
		super(context);
		init();
	}

	public SlideSwitchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideSwitchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {// 初始化

		bg_on = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_on_bg);
		bg_off = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_off_bg);
		slip_btn = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_slip_bg);
		btn_on = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
		btn_off = new Rect(bg_off.getWidth() - slip_btn.getWidth(), 0,
				bg_off.getWidth(), slip_btn.getHeight());
		setOnTouchListener(this);// 设置监听器,也可以直接复写OnTouchEvent
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(bg_on.getWidth(), bg_on.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {// 绘图函数

		super.onDraw(canvas);

		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x;

		if (nowX < (bg_on.getWidth() / 2))// 滑动到前半段与后半段的背景不同,在此做判断
		{
			x = nowX - slip_btn.getWidth() / 2;
			canvas.drawBitmap(bg_off, matrix, paint);// 画出关闭时的背景
		} else {
			x = bg_on.getWidth() - slip_btn.getWidth() / 2;
			canvas.drawBitmap(bg_on, matrix, paint);// 画出打开时的背景
		}

		if (onSlip)// 是否是在滑动状态,
        {
			if (nowX >= bg_on.getWidth())// 是否划出指定范围,不能让游标跑到外头,必须做这个判断

				x = bg_on.getWidth() - slip_btn.getWidth() / 2;// 减去游标1/2的长度...

			else if (nowX < 0) {
				x = 0;
			} else {
				x = nowX - slip_btn.getWidth() / 2;
			}
		} else {// 非滑动状态

			if (nowChoose)// 根据现在的开关状态设置画游标的位置
			{
				x = btn_off.left;
				canvas.drawBitmap(bg_on, matrix, paint);// 初始状态为true时应该画出打开状态图片
			} else
				x = btn_on.left;
		}
		if (isChecked) {
			canvas.drawBitmap(bg_on, matrix, paint);
			x = btn_off.left;
			isChecked = !isChecked;
		}

		if (x < 0)// 对游标位置进行异常判断...
			x = 0;
		else if (x > bg_on.getWidth() - slip_btn.getWidth())
			x = bg_on.getWidth() - slip_btn.getWidth();
		canvas.drawBitmap(slip_btn, x, 0, paint);// 画出游标.

	}

	
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction())
		// 根据动作来执行代码

		{
		case MotionEvent.ACTION_MOVE:// 滑动
			nowX = event.getX();
			break;

		case MotionEvent.ACTION_DOWN:// 按下

			if (event.getX() > bg_on.getWidth()
					|| event.getY() > bg_on.getHeight())
				return false;
			onSlip = true;
			downX = event.getX();
			nowX = downX;
			break;

		case MotionEvent.ACTION_CANCEL: // 移到控件外部

			onSlip = false;
			boolean choose = nowChoose;
			if (nowX >= (bg_on.getWidth() / 2)) {
				nowX = bg_on.getWidth() - slip_btn.getWidth() / 2;
				nowChoose = true;
			} else {
				nowX = nowX - slip_btn.getWidth() / 2;
				nowChoose = false;
			}
			if (isChangListenerOn && (choose != nowChoose)) // 如果设置了监听器,就调用其方法..
				changListener.OnChanged(nowChoose);
			break;
		case MotionEvent.ACTION_UP:// 松开
			onSlip = false;
			boolean LastChoose = nowChoose;

			if (event.getX() >= (bg_on.getWidth() / 2)) {
				nowX = bg_on.getWidth() - slip_btn.getWidth() / 2;
				nowChoose = true;
			}

			else {
				nowX = nowX - slip_btn.getWidth() / 2;
				nowChoose = false;
			}

			if (isChangListenerOn && (LastChoose != nowChoose)) // 如果设置了监听器,就调用其方法..

				changListener.OnChanged(nowChoose);
			break;
		default:
		}
		invalidate();// 重画控件
		return true;
	}

	public void SetOnChangedListener(OnChangedListener l) {// 设置监听器,当状态修改的时候
		isChangListenerOn = true;
		changListener = l;
	}

	public interface OnChangedListener {
		abstract void OnChanged(boolean CheckState);
	}

	public void setCheck(boolean isChecked) {
		MyLogger.i(VIEW_LOG_TAG, "isChecked##" + isChecked);
		this.isChecked = isChecked;
		nowChoose = isChecked;
	}
	public boolean isChecked() {
		return this.isChecked;
	}
}
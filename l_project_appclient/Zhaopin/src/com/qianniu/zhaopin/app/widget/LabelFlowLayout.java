package com.qianniu.zhaopin.app.widget;

import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.ui.BaseActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class LabelFlowLayout extends ViewGroup
{
    private final static String TAG = "LabelFlowLayout";
    protected BaseActivity activity;

    private final static int VIEW_MARGIN = 16;
    protected int margin = -1;
    
    public LabelFlowLayout(Context context) {
        super(context);
        initViewMargin();
    }
    
    public LabelFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public LabelFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewMargin();
    }
    protected void initViewMargin() {
    	margin = CommonUtils.dip2px(getContext(), VIEW_MARGIN);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mCount = getChildCount();
        int mX = 0;
        int mY = 0;
        int mRow = 0;
        
        for (int index = 0; index < mCount; index++) {
            final View child = getChildAt(index);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            mX += width + margin;
            mY = mRow * (height + margin) + height + margin;
            if (mX > mWidth)
            {
                mX = width + margin;
                mRow++;
                mY = mRow * (height + margin) + height + margin;
            }
        }
        setMeasuredDimension(mWidth, mY);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        t = 0;
        int mX = l;
        int mY = t;
        int mRow = 0;
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            mX += width + margin;
            mY = mRow * (height + margin) + height + t + margin;
            if (mX > r) {
                mX = width + margin + l;
                mRow++;
                mY = mRow * (height + margin) + height + t + margin ;
            }
            child.layout(mX - width, mY - height, mX, mY);
        }
    }
}

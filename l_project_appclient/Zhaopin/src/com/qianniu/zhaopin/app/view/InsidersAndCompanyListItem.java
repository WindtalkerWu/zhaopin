package com.qianniu.zhaopin.app.view;

import com.qianniu.zhaopin.app.common.CommonUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;

public class InsidersAndCompanyListItem extends LinearLayout{
	public static final float MARGIN = 6.6f;

	public InsidersAndCompanyListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InsidersAndCompanyListItem(Context context) {
		super(context);
		setOrientation(LinearLayout.HORIZONTAL); //CommonUtils.dip2px(context, 175)
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
//		setPadding(CommonUtils.dip2px(context, 8), 0, 0, 0);
		setLayoutParams(params);
	}

	public void setView(View view) {
		LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, CommonUtils.dip2px(getContext(), MARGIN), 0);
		params.weight = 1;
		params.gravity = Gravity.CENTER_VERTICAL;
		addView(view, params);
	}
}

package com.qianniu.zhaopin.app.widget;

import com.qianniu.zhaopin.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class CompanyLabelFlowItem extends TextView{

	private CompanyLabelFlowLayout labelFlowLayout;

	public CompanyLabelFlowItem(Context context, CompanyLabelFlowLayout labelFlowLayout) {
		super(context);
		this.labelFlowLayout = labelFlowLayout;
		init();
	}

	public CompanyLabelFlowItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public CompanyLabelFlowItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@SuppressLint("ResourceAsColor")
	private void init() {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		setPadding(CommonUtils.dip2px(getContext(), 20), CommonUtils.dip2px(getContext(), 4),
//				CommonUtils.dip2px(getContext(), 20), CommonUtils.dip2px(getContext(), 4));
//		setTextColor(R.color.white);
		setTextColor(getContext().getResources().getColor(R.color.list_subTitle));
		setTextSize(12);
		setMaxEms(9);
		setSingleLine();
		setEllipsize(TruncateAt.END);
		setGravity(Gravity.CENTER);
		setLayoutParams(params);
	}
	
	@SuppressLint("ResourceAsColor")
	public void initData(String labelStr, int imageId) {
		setText(" " + labelStr + " ");
		setBackgroundResource(imageId);
	}
	
}

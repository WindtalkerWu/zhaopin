package com.qianniu.zhaopin.app.widget;


import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.CommonUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

public class CompanyLabelFlowLayout extends LabelFlowLayout {
	private static final String TAG = "CompanyLabelFlowLayout";
	private String[] labels;
	public CompanyLabelFlowLayout(Context context) {
        super(context);
    }
    
    public CompanyLabelFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public CompanyLabelFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void initViewMargin() {
    	margin = CommonUtils.dip2px(getContext(), 5);
    }
//    protected void initViewMargin() {
//    	margin = CommonUtils.dip2px(getContext(), VIEW_MARGIN);
//    }
    public void initData(String[] labels) {
    	this.labels = labels;
    	for (int i = 0; i < labels.length; i++) {
    		String label = labels[i];
    		if (!TextUtils.isEmpty(label)) {
    			CompanyLabelFlowItem labelFlowItem = new CompanyLabelFlowItem(getContext(), this);
    			labelFlowItem.initData(label, R.drawable.common_form_bg);
    			addView(labelFlowItem);
    		}
		}
    }
}

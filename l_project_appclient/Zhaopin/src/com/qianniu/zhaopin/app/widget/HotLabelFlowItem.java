package com.qianniu.zhaopin.app.widget;


import com.qianniu.zhaopin.app.bean.OneLevelData;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.view.Util;
import com.qianniu.zhaopin.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.view.MotionEvent;
import android.view.View;

public class HotLabelFlowItem extends Button implements View.OnClickListener, View.OnTouchListener{

	private boolean isSelected = false;
	private HotLabelFlowLayout labelFlowLayout;
	private OneLevelData oneLevelData;
	private int flag;

	public HotLabelFlowItem(Context context, HotLabelFlowLayout labelFlowLayout) {
		super(context);
		this.labelFlowLayout = labelFlowLayout;
		init();
	}

	public HotLabelFlowItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public HotLabelFlowItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@SuppressLint("ResourceAsColor")
	private void init() {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		setTextColor(R.color.white);
		setTextColor(getContext().getResources().getColor(R.color.white));
		setTextSize(16);
		setHeight(CommonUtils.dip2px(getContext(), 36));
		setPadding(CommonUtils.dip2px(getContext(), 20), CommonUtils.dip2px(getContext(), 2),
				CommonUtils.dip2px(getContext(), 20), CommonUtils.dip2px(getContext(), 2));
		setLayoutParams(params);
	}
	
	@SuppressLint("ResourceAsColor")
	public void initData(OneLevelData oneLevelData, int imageId, int flag) {
		this.oneLevelData = oneLevelData;
		this.flag = flag;
		if (oneLevelData != null) {
			setText(oneLevelData.getLabel());
		}
		initBg(imageId);
		setOnClickListener(this);
		setOnTouchListener(this);
	}
	private void initBg(int imageId) {
		setBackgroundResource(imageId);
		if (isSelected) {
			getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		}
	}

	@Override
	public void onClick(View v) {
		//doSomething
		if (TextUtils.isEmpty(getText().toString())) {// add button
//			startRewardSearchLabelActivity();
			labelFlowLayout.startRewardSearchLabelActivity();
		}

	}
//	private void startRewardSearchLabelActivity() {
//		Intent intent = new Intent();
//		 ComponentName comp = new ComponentName("com.matrixdigi.headhunter",  
//                 "com.matrixdigi.app.ui.RewardSearchLabelActivity");
//		 intent.setComponent(comp);
//		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		 intent.putExtra("selectLabels", (ArrayList<OneLevelData>)labelFlowLayout.getSelectedLabels());
//		 getContext().startActivity(intent);
//	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (flag == HotLabelFlowLayout.PARTLABEL) {
				getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
				isSelected = !isSelected;
			} else {
			}
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			if (flag == HotLabelFlowLayout.PARTLABEL) {
				getBackground().clearColorFilter();
				isSelected = !isSelected;
			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (flag == HotLabelFlowLayout.PARTLABEL) {
				labelFlowLayout.refreshLabel(oneLevelData);
				getBackground().clearColorFilter();
				isSelected = !isSelected;
			} else {
				if (TextUtils.isEmpty(getText().toString())) {// add button
					getBackground().clearColorFilter();
					setTextColor(Color.parseColor("#FFFFFF"));
					isSelected = !isSelected;
				}
				if (isSelected) {
					if (!TextUtils.isEmpty(getText().toString())) { //add button
						getBackground().clearColorFilter();
						setTextColor(Color.parseColor("#FFFFFF"));
						isSelected = !isSelected;
						labelFlowLayout.removeSelected(oneLevelData);
					} else {// add button
						getBackground().clearColorFilter();
						setTextColor(Color.parseColor("#FFFFFF"));
						isSelected = !isSelected;
					}
				} else {
					if (!TextUtils.isEmpty(getText().toString())) {
						if (labelFlowLayout.getSelectedCount() >= 5) {
							Util.toast(getContext(), "最多只能添加5个", true);
						} else {
							labelFlowLayout.addSelected(oneLevelData);
							getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
							setTextColor(Color.parseColor("#828282"));
							isSelected = !isSelected;
						}
					} else {// add button
						getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
						setTextColor(Color.parseColor("#828282"));
						isSelected = !isSelected;
					}
				}
			}
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (flag == HotLabelFlowLayout.PARTLABEL) {
//				getBackground().clearColorFilter();
//				isSelected = !isSelected;
			} else {
				if (TextUtils.isEmpty(getText().toString())) {// add button
					getBackground().clearColorFilter();
					setTextColor(Color.parseColor("#FFFFFF"));
					isSelected = !isSelected;
				}
			}
			invalidate();
		}
		return false;
	}
	
}

package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.R;

import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 推荐询问
 * @author wuzy
 *
 */
public class PromptPopupWindow extends PopupWindow{
	private View view;
	private ImageView ok;
	private ImageView cancel;
	
	private TextView contentTitle;
	private TextView content;
	private TextView contentCenter;
	
	public PromptPopupWindow(Context context, boolean isBig) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if (isBig) {
			view = inflater.inflate(R.layout.popup_window_prompt_big, null);
		} else {
			view = inflater.inflate(R.layout.popup_window_prompt, null);
		}
		ok = (ImageView) view.findViewById(R.id.prompt_title_ok);
		cancel = (ImageView) view.findViewById(R.id.prompt_title_cancel);
		contentTitle = (TextView) view.findViewById(R.id.prompt_content_title);
		content = (TextView) view.findViewById(R.id.prompt_content);
		contentCenter = (TextView) view.findViewById(R.id.prompt_content_center);
		this.setContentView(view);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);

//		this.setAnimationStyle(R.style.AnimBottom);
		
//		ColorDrawable dw = new ColorDrawable(0);
//		this.setBackgroundDrawable(dw);
	}
	public void setListener(OnClickListener onClickListener) {
		ok.setOnClickListener(onClickListener);
		cancel.setOnClickListener(onClickListener);
	}
	public void initView(String contentTitleStr, String contentStr) {
		contentTitle.setText(contentTitleStr);
		content.setText(contentStr);
	}
	public void initView(int contentTitleId, int contentId, boolean isContentCenter) {
		if (contentTitleId == -1) {
			contentTitle.setVisibility(View.GONE);
		} else {
			contentTitle.setText(contentTitleId);
		}
		if (isContentCenter) {
			contentCenter.setText(contentId);
			contentCenter.setVisibility(View.VISIBLE);
			content.setVisibility(View.GONE);
		} else {
			content.setText(contentId);
			content.setVisibility(View.VISIBLE);
			contentCenter.setVisibility(View.GONE);
			cancel.setVisibility(View.INVISIBLE);
		}
	}
	public void show(View view) {
		showAtLocation(view, Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0); 
	}
}

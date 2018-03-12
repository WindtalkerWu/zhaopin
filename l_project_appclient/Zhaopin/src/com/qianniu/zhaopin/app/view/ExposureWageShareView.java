package com.qianniu.zhaopin.app.view;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.thp.SinaWeiboShareActivity;
import com.qianniu.zhaopin.thp.WeChatShare;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class ExposureWageShareView extends LinearLayout {
	
	private Activity activity;
	
	private LayoutInflater inflater;
	private ImageView sinaShare;
	private ImageView timeLineShare;
	private ImageView wechatShare;

	public ExposureWageShareView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public ExposureWageShareView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public ExposureWageShareView(Context context) {
		super(context);
	}
	public void setActivty(Activity activity) {
		this.activity = activity;
	}
	private void initView() {
		inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.exposure_wage_share, this);
		
		int padddingTop = CommonUtils.dip2px(getContext(), 30);
		int padding = CommonUtils.dip2px(getContext(), 8);
		setPadding(padding, padddingTop, padding, padding);
		setOrientation(HORIZONTAL);
		
		sinaShare = (ImageView) findViewById(R.id.exposure_wage_share_sina_image);
		timeLineShare = (ImageView) findViewById(R.id.exposure_wage_share_timeline_image);
		wechatShare = (ImageView) findViewById(R.id.exposure_wage_share_wechat_image);
		
		setListener();
	}
	private void setListener() {
		sinaShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent i = new Intent(activity, SinaWeiboShareActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("content", "牵牛曝工资");
				i.putExtras(bundle);
				activity.startActivity(i);
			}
		});
		timeLineShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WeChatShare wcs = new WeChatShare(activity, activity.getIntent());
				wcs.ShareText("牵牛曝工资", false);
			}
		});
		wechatShare.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				WeChatShare wcs = new WeChatShare(activity, activity.getIntent());
				wcs.ShareText("牵牛曝工资", true);
			}
		});
	}

}

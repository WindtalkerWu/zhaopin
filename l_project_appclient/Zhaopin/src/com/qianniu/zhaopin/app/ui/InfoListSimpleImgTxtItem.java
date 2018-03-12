package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoListSimpleImgTxtItem extends LinearLayout {

	private Context mcontext;
	private TextView time_tv;
	private TextView title_tv;
	private TextView date_tv;
	private LinearLayout view;
	private ImageView topimg;
	private TextView topimg_tv;
	private TextView digest_tv;
	private LinearLayout detail;

	private InfoEntity minfoEntity;

	private BitmapManager bmpManager;
	private Bitmap defbmp;

	public InfoListSimpleImgTxtItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mcontext = context;
		this.bmpManager = new BitmapManager(null);
	}
	public InfoListSimpleImgTxtItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mcontext = context;
		this.bmpManager = new BitmapManager(null);
	}
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		time_tv = (TextView) findViewById(R.id.info_item_time_tv);
		title_tv = (TextView) findViewById(R.id.info_item_title_tv);
		date_tv = (TextView) findViewById(R.id.info_item_date_tv);
		view = (LinearLayout) findViewById(R.id.topSlot);
		view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (minfoEntity != null) {
					InfoEntity.actionJump(mcontext, minfoEntity);	
/*					UIHelper.showInfoDetial(mcontext, minfoEntity.getInfoId(),
							minfoEntity.getInfoTitle(),
							minfoEntity.getInfoUrl());*/
				}
			}
		});
		topimg_tv = (TextView) findViewById(R.id.info_item_title_in_image);
		digest_tv = (TextView) findViewById(R.id.info_item_content_tv);

		topimg = (ImageView) findViewById(R.id.info_item_photo_iv);
		detail = (LinearLayout) findViewById(R.id.info_item_detail);
//		this.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (minfoEntity != null) {
//					UIHelper.showInfoDetial(mcontext, minfoEntity.getInfoId(),
//							minfoEntity.getInfoTitle(),
//							minfoEntity.getInfoUrl());
//				}
//			}
//		});

	}

	public void setDefaultDisplayImg(Bitmap defbmp) {
		bmpManager.setDefaultBmp(defbmp);
		this.defbmp = defbmp;
	}

	public void bind(InfoEntity infoEntity, boolean isVisible) {
		minfoEntity = infoEntity;
		if (!isVisible) {
			this.setVisibility(View.GONE);
			return;
		} else {
			this.setVisibility(View.VISIBLE);
		}

		String imgURL = minfoEntity.getInfoImg();
		if (imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)) {
			// face.setImageResource(R.drawable.widget_dface);
			if (defbmp != null)
				topimg.setImageBitmap(defbmp);
		} else {
			bmpManager.loadBitmap(imgURL, topimg);
		}
		String receivetime = StringUtils.timeToString(minfoEntity.getReviveTimestamp());
		time_tv.setText(StringUtils.friendly_time(receivetime));

		title_tv.setText(minfoEntity.getInfoTitle());
		date_tv.setText(StringUtils.formatDateStringToYMD(minfoEntity
				.getInfoTimestamp()));
		digest_tv.setText(minfoEntity.getInfoDigest());

		topimg_tv.setText(minfoEntity.getInfoTitle());

	}

}

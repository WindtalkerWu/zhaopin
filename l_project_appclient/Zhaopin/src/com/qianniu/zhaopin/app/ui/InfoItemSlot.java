package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoItemSlot extends LinearLayout {

	private Context mcontext;
	private BitmapManager bmpManager;
	private Bitmap defbmp;
	private InfoEntity minfoEntity;

	private TextView summary_tv;
	private TextView title_tv;
	private ImageView cover_iv;
	

	public InfoItemSlot(Context context) {
		this(context,null);
	}
	public InfoItemSlot(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mcontext = context;
		this.bmpManager = new BitmapManager(null);
	}


	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		summary_tv = (TextView) findViewById(R.id.slot_summary);
		title_tv = (TextView) findViewById(R.id.slot_title);
		cover_iv = (ImageView) findViewById(R.id.slot_cover);
		this.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InfoEntity.actionJump(mcontext, minfoEntity);	
/*				if (minfoEntity != null) {
					UIHelper.showInfoDetial(mcontext, minfoEntity.getInfoId(),
							minfoEntity.getInfoTitle(),
							minfoEntity.getInfoUrl());
				}*/
			}
		});
	}

	public void setDefaultDisplayImg(Bitmap defbmp) {
		bmpManager.setDefaultBmp(defbmp);
		this.defbmp = defbmp;
	}

	public void bind(InfoEntity infoEntity, boolean isVisible) {
		minfoEntity = infoEntity;
		if (!isVisible) {
			this.setVisibility(View.INVISIBLE);
			return;
		} else {
			this.setVisibility(View.VISIBLE);
		}

		String imgURL = minfoEntity.getInfoImg();
		if (imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)) {
			// face.setImageResource(R.drawable.widget_dface);
			if (defbmp != null)
				cover_iv.setImageBitmap(defbmp);
		} else {
			bmpManager.loadBitmap(imgURL, cover_iv);
		}
		title_tv.setText(minfoEntity.getInfoTitle());
		summary_tv.setText(minfoEntity.getInfoDigest());

	}
}

package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.bean.ActionJumpEntity;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InfoListCommonItem extends RelativeLayout {

	private Context mcontext;
	
	public TextView title;
	public TextView digest;
	public TextView date;
	public ImageView face;
	private InfoEntity minfoEntity;

	private BitmapManager bmpManager;
	private Bitmap defbmp;

	public InfoListCommonItem(Context context) {
		this(context,null);
	}

	public InfoListCommonItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mcontext = context;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.qianniu_logobg));
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		// 获取控件对象
		title = (TextView) findViewById(R.id.info_listitem_title);
		digest = (TextView) findViewById(R.id.info_listitem_digest);
		date = (TextView) findViewById(R.id.info_listitem_date);
		face = (ImageView) findViewById(R.id.info_listitem_userface);
		this.setSoundEffectsEnabled(false);
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InfoEntity.actionJump(mcontext, minfoEntity);		
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
			this.setVisibility(View.GONE);
			return;
		} else {
			this.setVisibility(View.VISIBLE);
		}

		String faceURL = minfoEntity.getInfoImg();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)) {
			// face.setImageResource(R.drawable.widget_dface);
			if (defbmp != null)
				face.setImageBitmap(defbmp);
		} else {
			bmpManager.loadBitmap(faceURL, face);
		}
		// face.setOnClickListener(faceClickListener);
		// face.setTag(foruminfo);

		title.setText(minfoEntity.getInfoTitle());
		title.setTag(minfoEntity);// 设置隐藏参数(实体类)
		digest.setText(minfoEntity.getInfoDigest());
		date.setText(StringUtils.friendly_time(minfoEntity.getInfoTimestamp()));
		 

	}

}

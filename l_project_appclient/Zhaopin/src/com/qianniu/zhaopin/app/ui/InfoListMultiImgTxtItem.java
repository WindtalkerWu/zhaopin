package com.qianniu.zhaopin.app.ui;

import java.util.List;

import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoEntity;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoListMultiImgTxtItem extends LinearLayout {

	private Context mcontext;
	private BitmapManager bmpManager;
	private Bitmap defbmp;
	private ItemInfoEntity iteminfo;
	private List<InfoEntity> infolist;

	private TextView time_tv;
	private LinearLayout slotcontainer;
	private LinearLayout topSlotContainer;
	private ImageView topSlotimg;
	private TextView topSlotTitle;

	protected LayoutInflater mInflater;

	public InfoListMultiImgTxtItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mcontext = context;
		this.bmpManager = new BitmapManager(null);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public InfoListMultiImgTxtItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mcontext = context;
		this.bmpManager = new BitmapManager(null);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		time_tv = (TextView) findViewById(R.id.info_item_time_tv);
		slotcontainer = (LinearLayout) findViewById(R.id.info_item_Slotcotent);
		topSlotimg = (ImageView) findViewById(R.id.info_item_top_photo_iv);
		topSlotTitle = (TextView) findViewById(R.id.info_item_top_title_in_image);
		topSlotContainer = (LinearLayout) findViewById(R.id.info_item_multi_TopSlot);

		topSlotContainer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InfoEntity topinfo = (InfoEntity) topSlotContainer.getTag();
				InfoEntity.actionJump(mcontext, topinfo);	
/*				if (topinfo != null && topinfo.getInfoUrl() != null
						&& topinfo.getInfoUrl().length() != 0) {
					UIHelper.showInfoDetial(mcontext, topinfo.getInfoId(),
							topinfo.getInfoTitle(), topinfo.getInfoUrl());
				}*/
			}
		});

	}

	public void setDefaultDisplayImg(Bitmap defbmp) {
		bmpManager.setDefaultBmp(defbmp);
		this.defbmp = defbmp;
	}

	public void bind(ItemInfoEntity iteminfoEntity, boolean isVisible) {
		iteminfo = iteminfoEntity;
		if (iteminfo != null)
			infolist = iteminfo.getInfoEntitylist();
		if (!isVisible || infolist == null || infolist.size() == 0) {
			this.setVisibility(View.GONE);
			return;
		} else {
			this.setVisibility(View.VISIBLE);
		}
		InfoEntity topinfo = infolist.get(0);
		String imgURL = topinfo.getInfoImg();
		if (imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)) {
			// face.setImageResource(R.drawable.widget_dface);
			if (defbmp != null)
				topSlotimg.setImageBitmap(defbmp);
		} else {
			bmpManager.loadBitmap(imgURL, topSlotimg);
		}
		
		String receivetime = StringUtils.timeToString(topinfo.getReviveTimestamp());
		time_tv.setText(StringUtils.friendly_time(receivetime));
		String titleStr = topinfo.getInfoTitle();
		if (TextUtils.isEmpty(titleStr)) {
			topSlotTitle.setVisibility(View.GONE);
		} else {
			topSlotTitle.setText(topinfo.getInfoTitle());
		}
		topSlotContainer.setTag(topinfo);

		int size = infolist.size();
		slotcontainer.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
		for (int i = 1; i < size; i++) {
			if (i == size - 1) {

				InfoItemSlot bottomslot  = (InfoItemSlot) mInflater.inflate(R.layout.info_item_multi_bottomslot, null);
				bottomslot.bind(infolist.get(i), true);
				slotcontainer.addView(bottomslot);
			} else {
				InfoItemSlot midlot  = (InfoItemSlot) mInflater.inflate(R.layout.info_item_multi_middleslot, null);
				midlot.bind(infolist.get(i), true);
				slotcontainer.addView(midlot);
				View view = mInflater.inflate(R.layout.common_form_line, null);
				slotcontainer.addView(view, params);
			}
		}

	}
}

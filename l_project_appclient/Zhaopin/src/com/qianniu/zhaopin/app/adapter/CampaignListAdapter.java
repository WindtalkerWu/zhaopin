package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.bean.CampaignEntity;
import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoEntity;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CampaignListAdapter extends BaseAdapter {

	private Activity context;// 运行上下文
	private List<CampaignEntity> mlistData;
	private LayoutInflater minflater;
	private int itemViewResource;
	private BitmapManager bmpManager;
	private Bitmap defbmp;

	public CampaignListAdapter(Activity context, List<CampaignEntity> data) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
		this.minflater = LayoutInflater.from(context);

		this.mlistData = data;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.qianniu_bg_small));
		this.defbmp = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.qianniu_logobg);
	}

	public class CampaignItemViewHolder {
		public TextView time_tv;
		public TextView startdate_tv;
		public TextView enddate_tv;
		public TextView activestate_tv;
		public TextView maintitle_tv;
		public TextView subtitle_tv;
		// public TextView date_tv;
		public LinearLayout topslot_layout;
		public ImageView topimg;
		public TextView topimg_tv;
		public TextView digest_tv;
		public LinearLayout detail;

		public CampaignEntity mcampaignEntity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mlistData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mlistData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		CampaignItemViewHolder viewholder = null;
		if (convertView == null) {
			convertView = minflater
					.inflate(R.layout.campaign_item_layout, null);
			viewholder = new CampaignItemViewHolder();
			viewholder.time_tv = (TextView) convertView
					.findViewById(R.id.info_item_time_tv);
			viewholder.startdate_tv = (TextView) convertView
					.findViewById(R.id.startdate_tv);
			viewholder.enddate_tv = (TextView) convertView
					.findViewById(R.id.enddate_tv);
			viewholder.activestate_tv = (TextView) convertView
					.findViewById(R.id.active_state_tv);
			viewholder.maintitle_tv = (TextView) convertView
					.findViewById(R.id.maintitle_tv);
			viewholder.subtitle_tv = (TextView) convertView
					.findViewById(R.id.subtitle_tv);

			viewholder.topslot_layout = (LinearLayout) convertView
					.findViewById(R.id.topSlot);

			viewholder.topimg_tv = (TextView) convertView
					.findViewById(R.id.info_item_title_in_image);
			viewholder.digest_tv = (TextView) convertView
					.findViewById(R.id.info_item_content_tv);

			viewholder.topimg = (ImageView) convertView
					.findViewById(R.id.info_item_photo_iv);
			viewholder.detail = (LinearLayout) convertView
					.findViewById(R.id.info_item_detail);
			viewholder.mcampaignEntity = mlistData.get(position);
			convertView.setTag(viewholder);
		} else {
			viewholder = (CampaignItemViewHolder) convertView.getTag();
			viewholder.mcampaignEntity = mlistData.get(position);
			convertView.setTag(viewholder);
		}
		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CampaignItemViewHolder viewholder = (CampaignItemViewHolder) v
						.getTag();
				if (viewholder != null && viewholder.mcampaignEntity != null) {

					UIHelper.showCampaignDetial(context, String
							.valueOf(viewholder.mcampaignEntity
									.getCpagId()),
							viewholder.mcampaignEntity.getTitle(),
							viewholder.mcampaignEntity
									.getDetailUrl());
				}
			}
		});
		if (viewholder.mcampaignEntity != null) {
			String receivetime = StringUtils
					.timeToString(viewholder.mcampaignEntity
							.getReceiveTimestamp());
			viewholder.time_tv.setText(receivetime);
			String startdate = StringUtils
					.formatDateStringToYMD(viewholder.mcampaignEntity
							.getStartDate());
			viewholder.startdate_tv.setText(startdate);
			String enddate = StringUtils
					.formatDateStringToYMD(viewholder.mcampaignEntity
							.getEndtDate());
			viewholder.enddate_tv.setText(enddate);
			/*
			 * String activestr = viewholder.mcampaignEntity.get
			 * viewholder.activestate_tv.setText(activestr);
			 */
			String maintitle = viewholder.mcampaignEntity.getTitle();
			if (maintitle != null)
				viewholder.maintitle_tv.setText(maintitle);
			/*
			 * String subtitle = viewholder.mcampaignEntity.getDescription(); if
			 * (subtitle != null) viewholder.subtitle_tv.setText(subtitle);
			 */

			// viewholder.topimg_tv;
			String digeststr = viewholder.mcampaignEntity.getDescription();
			if (digeststr != null)
				viewholder.digest_tv.setText(digeststr);
			String imgurl = viewholder.mcampaignEntity.getPicUrl();
			if (imgurl.endsWith(".gif") || StringUtils.isEmpty(imgurl)) {
				// face.setImageResource(R.drawable.widget_dface);
				if (defbmp != null)
					viewholder.topimg.setImageBitmap(defbmp);
			} else {
				bmpManager.loadBitmap(imgurl, viewholder.topimg, defbmp);
			}

		}
		return convertView;
	}
}

package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.RewardInfoListAdapter.RewardListInfoViewHolder;
import com.qianniu.zhaopin.app.bean.ConversionInfoData;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 积分兑换记录适配器
 * @author wuzy
 *
 */
public class ConversionRecordListAdapter extends BaseAdapter {

	private Context m_Context;
	
	private List<ConversionInfoData> m_lsCID;		// 积分兑换记录数据
	
	private LayoutInflater m_infater = null;
	private ConversionInfoViewHolder m_holder;
	
	public ConversionRecordListAdapter(Context context,
			List<ConversionInfoData> list) {
		m_infater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		m_lsCID = list;
		m_Context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_lsCID.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_lsCID.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null || convertView.getTag() == null) {
			convertView = m_infater.inflate(
						R.layout.list_item_conversionrecord, null);

			m_holder = new ConversionInfoViewHolder(convertView);
			convertView.setTag(m_holder);
		} else {
			m_holder = (ConversionInfoViewHolder) convertView.getTag();
		}
		
		return convertView;
	}

	public static class ConversionInfoViewHolder {

		TextView tvConversionTitle; 	// 兑换商品title
		TextView tvIntegral; 			// 兑换商品所需积分
		TextView tvPrice; 				// 兑换商品的价钱
		TextView tvAttentionNum; 		// 关注数


		ImageView imgCommodity;  	// 兑换商品图片
		ImageView imgframe;  		// 兑换商品图片边框


		public ConversionInfoViewHolder(View view) {
			// 兑换商品title
			this.tvConversionTitle = (TextView) view
					.findViewById(R.id.listitem_conversionrecord_tv_title);
			// 兑换商品所需积分
			this.tvIntegral = (TextView) view
					.findViewById(R.id.listitem_conversionrecord_tv_integral);
			// 兑换商品的价钱
			this.tvPrice = (TextView) view
					.findViewById(R.id.listitem_conversionrecord_tv_price);
			// 关注数
			this.tvAttentionNum = (TextView) view
					.findViewById(R.id.listitem_conversionrecord_tv_attentionnum);


			// 兑换商品图片边框
			this.imgframe = (ImageView) view
					.findViewById(R.id.listitem_conversionrecord_img_frame);
			// 兑换商品图片
			this.imgCommodity = (ImageView) view
					.findViewById(R.id.listitem_conversionrecord_img_commodity);
//			this.llListItemRewadinfo = (LinearLayout) view
//					.findViewById(R.id.list_item_rewadinfo_rl_right);
//
//			
//			this.rlTop = (RelativeLayout) view
//					.findViewById(R.id.list_item_rewadinfo_rl_top);

		}
	}
}

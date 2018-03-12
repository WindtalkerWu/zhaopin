package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.widget.CompanyLabelFlowLayout;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RewardInfoWaterFallAdapter extends BaseAdapter{
	private Context m_Context;
	
	private LayoutInflater m_infater = null;
	// 悬赏数据列表
	private List<RewardData> m_lsRD;
	
	private BitmapManager m_bmpManager;
	
	public RewardInfoWaterFallAdapter(Context context, List<RewardData> lsRD){
		this.m_Context = context;
		this.m_lsRD = lsRD;
		this.m_bmpManager = new BitmapManager(null);
		
		this.m_infater = (LayoutInflater) m_Context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return  m_lsRD.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_lsRD.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		RewardInfoWaterFallViewHolder holder = null;
		
		final RewardData rd = m_lsRD.get(position);
	
		if (convertView == null || convertView.getTag() == null) {
			//
			convertView = m_infater.inflate(R.layout.list_item_rewardinfo_waterfall, null);

			holder = new RewardInfoWaterFallViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (RewardInfoWaterFallViewHolder) convertView.getTag();
		}

		// 
		setView(holder, rd);
		
		return convertView;
	}

	public static class RewardInfoWaterFallViewHolder {
		// Logo图片布局
		private RelativeLayout m_rlLogo;
		
		// 发布者类型图片
		private ImageView m_imgPublisherType;
		// logo图片
		private ImageView m_imgLogo;
		
		// 姓名/公司名称
		private TextView m_tvTitle;
		// 悬赏金额 
		private TextView m_tvRewardPrice;
		// 悬赏任务类型
		private TextView m_tvRewardType;
		// 职位名称
		private TextView m_tvPosition;
		// 剩余天数
		private TextView m_tvValidDate;
		// 关注人数 
		private TextView m_tvAttention;
		
		// 标签
		private CompanyLabelFlowLayout m_clfl;
		
		public RewardInfoWaterFallViewHolder(View view){

			// 发布者类型图片
			m_imgPublisherType = (ImageView) view.findViewById(R.id.list_item_rewadinfo_waterfall_img_publishertype);
			// logo图片
			m_imgLogo = (ImageView) view.findViewById(R.id.list_item_rewadinfo_waterfall_img_logo);
			
			// Logo图片布局
			m_rlLogo = (RelativeLayout) view.findViewById(R.id.list_item_rewadinfo_waterfall_rl_logo);
			
			// 姓名/公司名称
			m_tvTitle = (TextView) view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_title);
			// 悬赏金额 
			m_tvRewardPrice = (TextView) view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_rewardprice);
			// 悬赏任务类型
			m_tvRewardType = (TextView) view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_rewardtype);
			// 职位名称
			m_tvPosition = (TextView) view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_position);
			// 剩余天数
			m_tvValidDate = (TextView) view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_validdate);
			// 关注人数 
			m_tvAttention = (TextView) view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_attention);
			
			// 标签
			m_clfl = (CompanyLabelFlowLayout) view.findViewById(R.id.rewardsearch_label_flow_bg);
		}
	}
	
	private void setView(RewardInfoWaterFallViewHolder holder, RewardData rd){
		if(null == holder || null == rd){
			return;
		}
		
		// 悬赏任务类型
		if (null != rd.getTask_Type()) {
			int nType = Integer.valueOf(rd.getTask_Type());
			switch (nType) {
			case HeadhunterPublic.REWARD_TYPE_ENTRY: // 求职入职悬赏任务
				{
					// 设置发布者类型图片(性别图片)
					setGenderImg(rd.getGender(), holder.m_imgPublisherType);
					// 悬赏类型
					holder.m_tvRewardType.setTextColor(Color.rgb(43, 147, 252));
					holder.m_tvRewardType.setText(R.string.str_reward_type_entry);
					holder.m_tvRewardType.setVisibility(View.VISIBLE);
					
					holder.m_rlLogo.setVisibility(View.GONE);
				}
				break;
			case HeadhunterPublic.REWARD_TYPE_INTERVIEW:// 求职面试悬赏任务
				{
					// 设置发布者类型图片(性别图片)
					setGenderImg(rd.getGender(), holder.m_imgPublisherType);
					// 悬赏类型
					holder.m_tvRewardType.setTextColor(Color.rgb(116, 194, 86));
					holder.m_tvRewardType.setText(R.string.str_reward_type_interview);
					holder.m_tvRewardType.setVisibility(View.VISIBLE);
					
					holder.m_rlLogo.setVisibility(View.GONE);

				}
				break;
			case HeadhunterPublic.REWARD_TYPE_JOB: // 招聘悬赏任务
			default:
				{
					// 设置发布者类型图片
					holder.m_imgPublisherType.setImageResource(
							R.drawable.common_img_publisher_company);
					
					// 加载logo图片
					if(null != rd.getImg_url() && !rd.getImg_url().isEmpty()){
						holder.m_rlLogo.setVisibility(View.VISIBLE);
						loadImage(true, rd.getImg_url(), holder.m_imgLogo);
					}else{
						holder.m_rlLogo.setVisibility(View.VISIBLE);
					}
					
				}
				break;
			}
		}
		
		//  姓名/公司名称
		if(null != rd.getCompany_Name()){
			holder.m_tvTitle.setText(rd.getCompany_Name());
		}
		
		// 悬赏金额 
		if(null != rd.getTask_Bonus()){
			holder.m_tvRewardPrice.setText(m_Context
					.getString(R.string.str_reward_rmb) + rd.getTask_Bonus());
		}
		
		// 职位名称
		if(null != rd.getTask_Title()){
			holder.m_tvPosition.setText(rd.getTask_Title());
		}
		
		// 标签布局
		if(null != rd.getLabels() && rd.getLabels().length > 0){
			if (null != holder.m_clfl) {
				holder.m_clfl.removeAllViews();
				holder.m_clfl.initData(rd.getLabels());
			}
		}
		
		// 剩余天数
		if(null != rd.getDays_left()){
			String strTemp = String.format(m_Context.getString(R.string.str_reward_validdate),
					rd.getDays_left());
			holder.m_tvValidDate.setText(strTemp);
		}
		
		// 关注人数 
		if(null != rd.getJoin_count()){
			String strTemp = String.format(m_Context.getString(R.string.str_reward_attention),
					rd.getJoin_count());
			holder.m_tvAttention.setText(strTemp);
		}
	}
	
	/**
	 * 设置发布者类型图片(性别图片)
	 * @param strGender
	 */
	private void setGenderImg(String strGender, ImageView imageView){
		if(null == strGender || null == imageView){
			return;
		}
		
		if(strGender.isEmpty()){
			imageView.setImageResource(
					R.drawable.common_img_publisher_boy);
			
			return;
		}
		
		int nType = Integer.valueOf(strGender);
		switch (nType) {
		case HeadhunterPublic.REWARD_GENDER_BOY: // 男
			{
				imageView.setImageResource(
						R.drawable.common_img_publisher_boy);
			}
			break;
		case HeadhunterPublic.REWARD_GENDER_GIRL:// 女
			{
				imageView.setImageResource(
						R.drawable.common_img_publisher_girl);
			}
			break;
		default:
			{
				
			}
			break;
		}
	}

	/**
	 * 加载图片
	 * @param isLoadImage
	 */
	public void loadImage(boolean bLoadImage, String strUrl, ImageView imageView) {
		if (bLoadImage) {
			m_bmpManager.loadBitmap(strUrl, imageView);
		} else {
			imageView.setImageBitmap(null);
		}
	}
}

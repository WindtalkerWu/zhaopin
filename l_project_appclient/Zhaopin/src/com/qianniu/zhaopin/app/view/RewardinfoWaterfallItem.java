package com.qianniu.zhaopin.app.view;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.widget.CompanyLabelFlowLayout;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RewardinfoWaterfallItem extends RelativeLayout{

	private Context m_Context;
	
	private RewardData m_RewardData;
	
	private BitmapManager m_bmpManager;
	
	private LayoutInflater m_inflater;
	private View m_view;
	
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
	// 城市
	private TextView m_tvCity;
	
	// 标签
	private CompanyLabelFlowLayout m_clfl;
	
	// 公司Logo控件高度
	private int m_nLogoHeight;
	
//	public RewardinfoWaterfallItem(Context context, BitmapManager bmpManager, RewardData rd) {
//		super(context);
//		// TODO Auto-generated constructor stub
//		this.m_Context = context;
//		this.m_inflater = LayoutInflater.from(context);
//		this.m_bmpManager = bmpManager;
//		this.m_RewardData = rd;
//		this.m_nLogoHeight = 0;
//		
//		initView();
//		initData();
//	}
	
	public RewardinfoWaterfallItem(Context context, BitmapManager bmpManager, RewardData rd,
			int nLogoHeight) {
		super(context);
		// TODO Auto-generated constructor stub
		this.m_Context = context;
		this.m_inflater = LayoutInflater.from(context);
		this.m_bmpManager = bmpManager;
		this.m_RewardData = rd;
		this.m_nLogoHeight = nLogoHeight;
		
		initView();
		initData();
	}

	/**
	 * 
	 */
	private void initView() {
		// 设置背景
//		setBackgroundResource(R.drawable.common_list_item_selector);
		
		//
		m_view = m_inflater.inflate(R.layout.list_item_rewardinfo_waterfall, this);
		// 发布者类型图片
		m_imgPublisherType = (ImageView) m_view.findViewById(R.id.list_item_rewadinfo_waterfall_img_publishertype);
		// logo图片
		m_imgLogo = (ImageView) m_view.findViewById(R.id.list_item_rewadinfo_waterfall_img_logo);
		if(null != m_imgLogo){
			setImageHeight(m_nLogoHeight);
		}
		
		// Logo图片布局
		m_rlLogo = (RelativeLayout) m_view.findViewById(R.id.list_item_rewadinfo_waterfall_rl_logo);
		
		// 姓名/公司名称
		m_tvTitle = (TextView) m_view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_title);
		// 悬赏金额 
		m_tvRewardPrice = (TextView) m_view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_rewardprice);
		// 悬赏任务类型
		m_tvRewardType = (TextView) m_view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_rewardtype);
		// 职位名称
		m_tvPosition = (TextView) m_view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_position);
		// 剩余天数
		m_tvValidDate = (TextView) m_view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_validdate);
		// 关注人数 
		m_tvAttention = (TextView) m_view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_attention);
		// 城市
		m_tvCity = (TextView) m_view.findViewById(R.id.list_item_rewadinfo_waterfall_tv_city);
		
		// 标签
		m_clfl = (CompanyLabelFlowLayout) m_view.findViewById(R.id.rewardsearch_label_flow_bg);
	}
	
	/**
	 * 
	 */
	private void initData(){
		if(null != m_RewardData){
			m_view.setVisibility(View.VISIBLE);
			
			boolean bJobTyep = false;
			
			// 悬赏任务类型
			if (null != m_RewardData.getTask_Type()) {
				int nType = Integer.valueOf(m_RewardData.getTask_Type());
				switch (nType) {
				case HeadhunterPublic.REWARD_TYPE_ENTRY: // 求职入职悬赏任务
					{
						// 设置发布者类型图片(性别图片)
						setGenderImg(m_RewardData.getGender(), m_imgPublisherType);
						// 悬赏类型
						//m_tvRewardType.setTextColor(Color.rgb(43, 147, 252));
						m_tvRewardType.setTextColor(Color.rgb(40, 159, 243));
						m_tvRewardType.setText(R.string.str_reward_type_entry);
						m_tvRewardType.setVisibility(View.VISIBLE);
						
						m_rlLogo.setVisibility(View.GONE);
						m_clfl.setVisibility(View.GONE);
					}
					break;
				case HeadhunterPublic.REWARD_TYPE_INTERVIEW:// 求职面试悬赏任务
					{
						// 设置发布者类型图片(性别图片)
						setGenderImg(m_RewardData.getGender(), m_imgPublisherType);
						// 悬赏类型
						//m_tvRewardType.setTextColor(Color.rgb(116, 194, 86));
						m_tvRewardType.setTextColor(Color.rgb(0, 170, 161));
						m_tvRewardType.setText(R.string.str_reward_type_interview);
						m_tvRewardType.setVisibility(View.VISIBLE);
						
						m_rlLogo.setVisibility(View.GONE);
						m_clfl.setVisibility(View.GONE);
					}
					break;
				case HeadhunterPublic.REWARD_TYPE_JOB: // 招聘悬赏任务
				default:
					{
						// 设置发布者类型图片
						m_imgPublisherType.setImageResource(
								R.drawable.common_img_publisher_company);
						
						// 变更logo图片控件高度
//						if(0 != m_nLogoHeight){
//							setImgHeight(m_nLogoHeight);
//						}
						
						// 加载logo图片
						if(null != m_RewardData.getImg_url() && !m_RewardData.getImg_url().isEmpty()){
							m_rlLogo.setVisibility(View.VISIBLE);
							loadImage(true, m_RewardData.getImg_url(), m_imgLogo);
						}else{
							m_rlLogo.setVisibility(View.VISIBLE);
							m_imgLogo.setImageResource(
									R.drawable.common_img_companylogo);
						}
						
						// 悬赏类型
						m_tvRewardType.setVisibility(View.GONE);
						
						// 标签布局
						if(null != m_RewardData.getTask_Keyword() && m_RewardData.getTask_Keyword().length > 0){
							if (null != m_clfl) {
								m_clfl.removeAllViews();
								m_clfl.initData(m_RewardData.getTask_Keyword());
								m_clfl.setVisibility(View.VISIBLE);
							}
						}else{
							m_clfl.setVisibility(View.GONE);
						}
						
						bJobTyep = true;
						
					}
					break;
				}
			}
			
			//  姓名/公司名称
			if(null != m_RewardData.getCompany_Name()){
				m_tvTitle.setText(m_RewardData.getCompany_Name());
			}
			
			// 悬赏金额 
			if(null != m_RewardData.getTask_Bonus() && 
					!m_RewardData.getTask_Bonus().isEmpty() &&
					!m_RewardData.getTask_Bonus().equals("0") &&
					!m_RewardData.getTask_Bonus().equals("0.0") &&
					!m_RewardData.getTask_Bonus().equals("0.00")
					){
				
				// 字体加粗
				m_tvRewardPrice.getPaint().setFakeBoldText(true);

				if(!ConfigOptions.debug){
					double dBouns = Double.valueOf(m_RewardData.getTask_Bonus());
					int nBouns = (int)dBouns;
					String strBonus = String.valueOf(nBouns);
					m_tvRewardPrice.setText(m_Context
							.getString(R.string.str_reward_rmb) + strBonus);
				}else{
					m_tvRewardPrice.setText(m_Context
							.getString(R.string.str_reward_rmb) + m_RewardData.getTask_Bonus());
				}
				
				// 显示"推荐红包"文字
				if(bJobTyep){
					m_tvRewardType.setTextColor(m_Context.getResources().getColorStateList(R.color.list_subTitle));
					m_tvRewardType.setText(R.string.str_reward_type_job);
					m_tvRewardType.setVisibility(View.VISIBLE);
				}
			}else{
				m_tvRewardPrice.setText("");
			}
			
			// 职位名称
			if(null != m_RewardData.getTask_Title()){
				m_tvPosition.setText(m_RewardData.getTask_Title());
			}
			
			// 剩余天数
			if(null != m_RewardData.getDays_left()){
				String strTemp = String.format(m_Context.getString(R.string.str_reward_validdate),
						m_RewardData.getDays_left());
				m_tvValidDate.setText(strTemp);
			}else{
				String strTemp = String.format(m_Context.getString(R.string.str_reward_validdate),
						"0");
				m_tvValidDate.setText(strTemp);
			}
			
			// 关注人数 
			if(null != m_RewardData.getJoin_count() && !m_RewardData.getJoin_count().isEmpty()){
//				String strTemp = String.format(m_Context.getString(R.string.str_reward_attention),
//						UIHelper.getAttentionNum(m_RewardData.getJoin_count()));
//			    String strTemp = UIHelper.getAttentionNum(m_RewardData.getJoin_count());
				m_tvAttention.setText(m_RewardData.getJoin_count());
			}else{
				m_tvAttention.setText("0");
			}
			
			// 城市
			String strCity[] = m_RewardData.getTask_City();
 			if(null != strCity && strCity.length > 0){
 				String strTemp = UIHelper.getExpectCity(m_Context, strCity);
 				if(null != strTemp && !strTemp.isEmpty()){
 					if(strTemp.length() > 6){
 						strTemp = "[" + strTemp.substring(0, 5) + "...]";
 					}else{
 						strTemp = "[" + strTemp + "]";
 					}
 					m_tvCity.setText(strTemp);
 				}
 			}
		}else{
			m_view.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setListener(View.OnClickListener onClickListener) {
		m_view.setOnClickListener(onClickListener);
	}
	
	public void clearHeadImage() {
		m_imgLogo.setImageBitmap(null);
	}
	
	public void setClick(boolean isClickable) {
		setClickable(isClickable);
		m_view.setClickable(isClickable);
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
	
//	public void updateRewardinfo(RewardData rd, int nLogoHeight) {
//		this.m_RewardData = rd;
//		this.m_nLogoHeight = nLogoHeight;
//		initData();
//	}
	
//	public void updateRewardinfo(RewardData rd) {
//		this.m_RewardData = rd;
//		this.m_nLogoHeight = 0;
//		initData();
//	}
	
	public void updateRewardinfo(RewardData rd) {
		this.m_RewardData = rd;
		initData();
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
	
	public void setImgHeight(int nHeight){
		if(null != m_imgLogo){
			m_imgLogo.getLayoutParams().height = nHeight;
			// 加载logo图片
			if(null != m_RewardData.getImg_url() && !m_RewardData.getImg_url().isEmpty()){
				m_rlLogo.setVisibility(View.VISIBLE);
				loadImage(true, m_RewardData.getImg_url(), m_imgLogo);
			}else{
				m_rlLogo.setVisibility(View.VISIBLE);
				m_imgLogo.setImageResource(
						R.drawable.common_img_companylogo);
			}
		}
	}
	
	/**
	 * 设置图片控件的高度
	 * @param nWidth
	 */
	private void setImageHeight(int nHeight) {
		ViewGroup.LayoutParams params = m_imgLogo.getLayoutParams();
		params.height = nHeight;
		m_imgLogo.setLayoutParams(params);
	}
}

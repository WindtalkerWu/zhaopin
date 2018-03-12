package com.qianniu.zhaopin.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 悬赏任务列表适配器
 * 
 * @author wuzy
 * 
 */
public class RewardInfoListAdapter extends BaseAdapter {

	private List<RewardData> m_RewardLI;

	private LayoutInflater m_infater = null;
	private Context m_Context;

	private int m_nType;

	private RewardListInfoViewHolder m_holder;

	private int m_nHeight = 0;
	private int m_nCompanyCollectionHeight = 0;

	public final static int onePageCount = 10; // 每一页的item数

	private boolean m_bIsGetHeight = false; 		// 是否已经获取到控件的高度
	private boolean m_bIsCompanyCollection = false;
	private boolean m_bNotCompanyCollection = false;

	public RewardInfoListAdapter(Context context, int nType,
			List<RewardData> list) {
		m_infater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_RewardLI = list;
		m_Context = context;
		m_nType = nType;
		
		checkType();
	}

	/**
	 * @return
	 */
	public int getType(){
		return m_nType;
	}
	
	/**
	 * @param nType
	 */
	public void setType(int nType){
		m_nType = nType;
		checkType();
	}
	
	/**
	 * 
	 */
	private void checkType(){
		if(HeadhunterPublic.ACTIVITY_TYPE_MYCOLLECTION_COMPANY == m_nType){
			m_bIsCompanyCollection = true;
			m_bNotCompanyCollection = false;
			
			if(0 == m_nCompanyCollectionHeight){
				m_bIsGetHeight = false;
			}
		}else{
			m_bIsCompanyCollection = false;
			m_bNotCompanyCollection = true;
			
			if(0 == m_nHeight){
				m_bIsGetHeight = false;
			}
		}		
	}
	
	public void setM_RewardLI(List<RewardData> m_RewardLI) {
		this.m_RewardLI = m_RewardLI;
	}

	@Override
	public int getCount() {
		return m_RewardLI.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_RewardLI.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		m_holder = null;
		if (convertView == null || convertView.getTag() == null) {
			if (HeadhunterPublic.ACTIVITY_TYPE_MYRECORD == m_nType) {
				convertView = m_infater.inflate(
						R.layout.list_item_rewardinfo_myrecord, null);
			} else {
				convertView = m_infater.inflate(R.layout.list_item_rewardinfo,
						null);
			}

			m_holder = new RewardListInfoViewHolder(convertView);
			convertView.setTag(m_holder);
		} else {
			m_holder = (RewardListInfoViewHolder) convertView.getTag();
		}

		RewardData rlinfo = (RewardData) getItem(position);

		// 公司收藏
		if(HeadhunterPublic.ACTIVITY_TYPE_MYCOLLECTION_COMPANY == m_nType){
			// 发布时间
			m_holder.tvPublisherDate.setText(StringUtils.changeDateToWeek(rlinfo
					.getModified()));
			
			// 公司名称
			if(null != rlinfo.getTitle()){
				m_holder.tvRewadName.setText(rlinfo.getTitle());
			}
			
			// 公司地址
			if(null != rlinfo.getAddress()){
				m_holder.tvRewadKeyMsg.setText(rlinfo.getAddress());				
			}
			
			m_holder.rlTop.setVisibility(View.GONE);
			m_holder.ivline.setVisibility(View.GONE);
			
			if (m_bIsGetHeight) {
				String[] str = {"1"};
				Bitmap bmp = createCategoryBmp(m_nCompanyCollectionHeight,
						str);
				m_holder.ivTrade.setImageBitmap(bmp);
			} else {
				// 获取高度
				ViewTreeObserver vto = m_holder.rlMiddle
						.getViewTreeObserver();
				vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						// TODO Auto-generated method stub
						if (!m_bIsGetHeight && m_bIsCompanyCollection) {
							m_nCompanyCollectionHeight = m_holder.rlMiddle
									.getMeasuredHeight();
							
							m_bIsGetHeight = true;	
							notifyDataSetChanged();
						}
						return true;
					}
				});
			}
			
			return convertView;
		}
		
		m_holder.rlTop.setVisibility(View.VISIBLE);
		m_holder.ivline.setVisibility(View.VISIBLE);
		
		String task_bonus = rlinfo.getTask_Bonus();
		// 悬赏金额
		if(null!= task_bonus &&
			!TextUtils.isEmpty(task_bonus) && 
			!task_bonus.equals("0.00") &&
			!task_bonus.equals("0")){
			String bonus = (int)Double.parseDouble(task_bonus) + "";
			// 字体加粗
			TextPaint tp = m_holder.tvRewardPrice.getPaint();
			tp.setFakeBoldText(true);
			m_holder.tvRewardPrice.setText(m_Context
					.getString(R.string.str_reward_rmb) + bonus);		
		}else{
			m_holder.tvRewardPrice.setText("");
		}

		// 截止时间
		if(null != rlinfo.getPublisher_EndDate() &&
				!rlinfo.getPublisher_EndDate().isEmpty()){
			String strEndDate = m_Context
					.getString(R.string.str_reward_publisherdate_title)
					+ " "
					+ StringUtils.changeDateToMD(rlinfo.getPublisher_EndDate());
			m_holder.tvEndDate.setText(strEndDate);			
		}

		// 名称
		m_holder.tvRewadName.setText(rlinfo.getTask_Title());

		// 发布时间
		m_holder.tvPublisherDate.setText(StringUtils.changeDateToWeek(rlinfo
				.getPublisher_Date()));

		// 行业类型图片
		if (null != rlinfo.getTask_Category_Id()
				&& rlinfo.getTask_Category_Id().length > 0) {
			if (m_bIsGetHeight) {
				Bitmap bmp = createCategoryBmp(m_nHeight,
						rlinfo.getTask_Category_Id());
				m_holder.ivTrade.setImageBitmap(bmp);
			} else {
				ViewTreeObserver vto = m_holder.llListItemRewadinfo
						.getViewTreeObserver();
				vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						// TODO Auto-generated method stub
						if (!m_bIsGetHeight && m_bNotCompanyCollection) {
							m_nHeight = m_holder.llListItemRewadinfo
									.getMeasuredHeight();

							m_bIsGetHeight = true;
							notifyDataSetChanged();
						}
						return true;
					}
				});
			}
		}

		// 悬赏任务类型
		if (null != rlinfo.getTask_Type()) {
			int nType = Integer.valueOf(rlinfo.getTask_Type());
			switch (nType) {
			case HeadhunterPublic.REWARD_TYPE_ENTRY: // 求职入职悬赏任务
			{
				m_holder.tvRewadType.setTextColor(Color.rgb(43, 147, 252));
				m_holder.tvRewadType.setText(R.string.str_reward_type_entry);
				m_holder.tvRewadType.setVisibility(View.VISIBLE);

				// 类型图片
				m_holder.ivPublisherType
						.setImageResource(R.drawable.common_img_publisher_personal);

				// 期望城市(关键信息)
				m_holder.tvRewadKeyMsg.setText(UIHelper.getExpectCity(m_Context, rlinfo
						.getTask_City()));
			}
				break;
			case HeadhunterPublic.REWARD_TYPE_INTERVIEW:// 求职面试悬赏任务
			{
				m_holder.tvRewadType.setTextColor(Color.rgb(116, 194, 86));
				m_holder.tvRewadType
						.setText(R.string.str_reward_type_interview);
				m_holder.tvRewadType.setVisibility(View.VISIBLE);

				// 类型图片
				m_holder.ivPublisherType
						.setImageResource(R.drawable.common_img_publisher_personal);

				// 期望城市(关键信息)
				m_holder.tvRewadKeyMsg.setText(UIHelper.getExpectCity(m_Context, rlinfo
						.getTask_City()));
			}
				break;
			case HeadhunterPublic.REWARD_TYPE_JOB: // 招聘悬赏任务
			default: {
				m_holder.tvRewadType.setVisibility(View.GONE);

				// 类型图片
				m_holder.ivPublisherType
						.setImageResource(R.drawable.common_img_publisher_company);

				// 关键信息
				m_holder.tvRewadKeyMsg.setText(rlinfo.getCompany_Name());
			}
				break;
			}
		}

		switch (m_nType) {
		case HeadhunterPublic.ACTIVITY_TYPE_POST:
		case HeadhunterPublic.ACTIVITY_TYPE_REWARDLIST: 
		{
			// 是否已读
			if (rlinfo.getAction_5().equals(HeadhunterPublic.REWARD_READ_FLAG)) {
				m_holder.ivRead.setVisibility(View.INVISIBLE);
			} else {
				m_holder.ivRead.setVisibility(View.VISIBLE);
			}
		}
			break;
		case HeadhunterPublic.ACTIVITY_TYPE_MYRECORD: {
			// 是否已读
			m_holder.ivRead.setVisibility(View.INVISIBLE);

			// 审核状态
			if (null != rlinfo.getVerify_status()) {
				if (!rlinfo.getVerify_status().equals("1")) {
					m_holder.tvVerifystatus.setText(getVerifyStatusName(rlinfo
							.getVerify_status()));
				} else {
					m_holder.tvVerifystatus
							.setText(m_Context
									.getString(R.string.my_reward_list_reward_status_audit));
				}
			}

			// 简历名称
			String strTemp = rlinfo.getResume_name();
			if (null != strTemp) {
				if (!strTemp.isEmpty()) {
					switch (Integer.valueOf(rlinfo.getApply_type())) {
					case 1: {
						strTemp = m_Context
								.getString(R.string.str_myrecord_applytype_candidates)
								+ ": " + strTemp;
					}
						break;
					case 2: {
						strTemp = m_Context
								.getString(R.string.str_myrecord_applytype_recommend)
								+ ": " + strTemp;
					}
						break;
					default: {
						strTemp = UIHelper.getIdName(m_Context, rlinfo.getApply_type(),
								DBUtils.GLOBALDATA_TYPE_ACCEPTTASKMODE)
								+ ": "
								+ strTemp;
					}
						break;
					}
					m_holder.tvResumename.setText(strTemp);
				}
			}

			// 简历来源
			// m_holder.tvApplytype.setText(getIdName(rlinfo.getApply_type(),
			// DBUtils.GLOBALDATA_TYPE_ACCEPTTASKMODE));
		}
			break;
		default: {
			// 是否已读
			m_holder.ivRead.setVisibility(View.INVISIBLE);

			// 简历来源
			// m_holder.tvApplytype.setText(getIdName(rlinfo.getApply_type(),
			// DBUtils.GLOBALDATA_TYPE_ACCEPTTASKMODE));
		}
			break;
		}

		return convertView;
	}
	
	public static class RewardListInfoViewHolder {

		TextView tvRewardPrice; 	// 悬赏任务金额
		TextView tvEndDate; 		// 截止日期
		TextView tvPublisherDate; 	// 发布日期
		TextView tvRewadName; 		// 悬赏任务名称
		TextView tvRewadKeyMsg; 	// 悬赏任务关键字
		TextView tvRewadType; 		// 悬赏任务类型
		TextView tvVerifystatus; 	// 审核状态
		TextView tvResumename; 		// 简历名称
		TextView tvApplytype; 		// 简历来源

		ImageView ivPublisherType;  // 发布者类型图片
		ImageView ivTrade; 			// 行业类型
		ImageView ivRead; 			// 未读图片
		ImageView ivline; 			// 线

		LinearLayout llListItemRewadinfo;
		RelativeLayout rlTop; 
		RelativeLayout rlMiddle; 

		public RewardListInfoViewHolder(View view) {

			this.tvRewardPrice = (TextView) view
					.findViewById(R.id.list_item_rewadinfo_tv_rewardprice);
			this.tvEndDate = (TextView) view
					.findViewById(R.id.list_item_rewadinfo_tv_enddate);
			this.tvPublisherDate = (TextView) view
					.findViewById(R.id.list_item_rewadinfo_tv_publisherdate);
			this.tvRewadName = (TextView) view
					.findViewById(R.id.list_item_rewadinfo_tv_rewadname);
			this.tvRewadKeyMsg = (TextView) view
					.findViewById(R.id.list_item_rewadinfo_tv_rewadkeymsg);
			this.tvRewadType = (TextView) view
					.findViewById(R.id.list_item_rewadinfo_tv_rewardtype);
			this.tvVerifystatus = (TextView) view
					.findViewById(R.id.list_item_rewadinfo_tv_verifystatus);
			this.tvResumename = (TextView) view
					.findViewById(R.id.list_item_rewadinfo_tv_resumename);
			this.tvApplytype = (TextView) view
					.findViewById(R.id.list_item_rewadinfo_tv_applytype);

			this.ivTrade = (ImageView) view
					.findViewById(R.id.list_item_rewadinfo_img_trade);
			this.ivPublisherType = (ImageView) view
					.findViewById(R.id.list_item_rewadinfo_img_publishertype);

			this.llListItemRewadinfo = (LinearLayout) view
					.findViewById(R.id.list_item_rewadinfo_rl_right);
			this.ivRead = (ImageView) view
					.findViewById(R.id.list_item_rewadinfo_img_nored);
			
			this.rlTop = (RelativeLayout) view
					.findViewById(R.id.list_item_rewadinfo_rl_top);
			this.rlMiddle = (RelativeLayout) view
					.findViewById(R.id.list_item_rewadinfo_rl_middle);
			this.ivline = (ImageView) view
					.findViewById(R.id.list_item_rewadinfo_img_line);
		}
	}

	/**
	 * 新建行业颜色图片
	 * 
	 * @param nWidth
	 * @param nHeight
	 * @param nColor
	 * @return
	 */
	public Bitmap createCategoryColorBmp(int nWidth, int nHeight, int nColor) {
		Bitmap bmpRes = Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);

		Canvas canvasTemp = new Canvas(bmpRes);
		canvasTemp.drawColor(nColor);
		// canvasTemp.drawColor(Color.argb(0x55, 0xff, 0, 0));

		return bmpRes;
	}

	/**
	 * 图片拼接(垂直拼接)
	 * 
	 * @param bmpList
	 *            图片List
	 * @return 拼接好的新图片
	 */
	public Bitmap addVerticalBmp(List<Bitmap> bmpList) {
		int nWidth = 0;
		int nHeight = 0;

		for (Bitmap bmp : bmpList) {
			nHeight += bmp.getHeight();
			nWidth = Math.max(bmp.getWidth(), nWidth);
		}

		Bitmap bmpRes = Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bmpRes);

		int nW = 0;
		int nH = 0;
		for (Bitmap bmp : bmpList) {

			canvasTemp.drawBitmap(bmp, nW, nH, null);
			nH += bmp.getHeight();
		}

		return bmpRes;
	}

	/**
	 * 
	 * @param nHeight
	 * @param strList
	 * @return
	 */
	public Bitmap createCategoryBmp(int nHeight, String[] str) {

		int nW = 5;
		int nH = nHeight / str.length;

		List<Bitmap> bmpList = new ArrayList<Bitmap>();

		for (String strId : str) {
			Bitmap bmp = createCategoryColorBmp(nW, nH, getColor(strId));

			bmpList.add(bmp);
		}

		return addVerticalBmp(bmpList);
	}

	/**
	 * 从全局数据中获取状态的名称
	 * 
	 * @param strID
	 * @return
	 */
	private String getVerifyStatusName(String strId) {
		String strTemp = strId;
		AppContext ac = (AppContext) m_Context.getApplicationContext();

		try {
			return strTemp = ac.getVerifyStatusName(strId);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strTemp;
	}

	/**
	 * @param strId
	 * @return
	 */
	private int getColor(String strId) {
		// 紫色
		int nColor = Color.argb(127, 920, 40, 124);

		String strParentID = getParentId(strId);

		if (null != strParentID) {
			int nType = Integer.valueOf(strParentID);
			switch (nType) {
			case HeadhunterPublic.INDUSTRY_PARENTDATAID_TECHNOLOGY: {
				// 蓝色
				nColor = Color.argb(127, 43, 147, 252);
			}
				break;
			case HeadhunterPublic.INDUSTRY_PARENTDATAID_ADVERTISINGCOMPANY: {
				// 大红
				nColor = Color.argb(127, 193, 26, 17);
			}
				break;
			case HeadhunterPublic.INDUSTRY_PARENTDATAID_MEDIA: {
				// 绿色
				nColor = Color.argb(127, 116, 194, 86);
			}
				break;
			case HeadhunterPublic.INDUSTRY_PARENTDATAID_BRAND: {
				// 橘色
				nColor = Color.argb(127, 232, 117, 5);
			}
				break;
			case HeadhunterPublic.INDUSTRY_PARENTDATAID_OHTER:
			default: {
				// 紫色
				nColor = Color.argb(127, 920, 40, 124);
			}
				break;
			}
		}

		return nColor;
	}

	/**
	 * 获取父行ID
	 * 
	 * @param strId
	 * @return
	 */
	private String getParentId(String strId) {
		String strTemp = "";

		AppContext ac = (AppContext) m_Context.getApplicationContext();

		try {
			strTemp = ac
					.getParentId(strId, DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strTemp;
	}
}

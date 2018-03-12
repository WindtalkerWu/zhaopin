package com.qianniu.zhaopin.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.OneLevelData;
import com.qianniu.zhaopin.app.bean.RewardInfo;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 悬赏任务列表适配器
 * @author wuzy
 *
 */
public class MyRewardListAdapter extends BaseAdapter{
	
	private List<RewardInfo> rewardList;
	
	private LayoutInflater m_infater = null;
	private Context context;
	
	public MyRewardListAdapter(Context context, List<RewardInfo> list){
		this.context = context;
		m_infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rewardList = list;
	}

	public void setRewardList(List<RewardInfo> rewardList) {
		this.rewardList = rewardList;
	}
	@Override
	public int getCount() {
		return rewardList.size();
	}

	@Override
	public Object getItem(int position) {
		return rewardList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyRewardListViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = m_infater.inflate(R.layout.my_reward_item, null);
			holder = new MyRewardListViewHolder(convertView);
			convertView.setTag(holder);
		} 
		else{
			holder = (MyRewardListViewHolder) convertView.getTag() ;
		}

		RewardInfo rewardData = (RewardInfo)getItem(position);
		
		if (TextUtils.isEmpty(rewardData.getTask_id())) {
			convertView.setVisibility(View.INVISIBLE);
		} else {
			convertView.setVisibility(View.VISIBLE);
			String rewardBonusStr = context.getResources().getString(R.string.my_reward_list_reward_bonus);
			String rbStr = rewardData.getTask_bonus(); 
			String rewardBonusResult = String.format(rewardBonusStr, (int)Double.parseDouble(rbStr) + "");
			holder.rewardBonus.setText(rewardBonusResult);
			
			holder.rewardTitle.setText(rewardData.getTask_title());
			
			String[] categoryStr = rewardData.getTask_category_id();
			String post = "";
			int length = categoryStr.length;
			for (int i = 0; i < length; i++) {
				GlobalDataTable keywords = GlobalDataTable.getTypeDataById((AppContext)context,
						DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY, categoryStr[i]);
				if (keywords != null) {
					if (i == length -1) {
						post = post + keywords.getName();
					} else {
						post = post + keywords.getName() + ",";
					}
				}
			}
			holder.rewardPost.setText(post);

			
			String endDataPre = context.getResources().getString(R.string.my_reward_list_reward_enddata);
			String endData = rewardData.getPublisher_enddate();
			if(!TextUtils.isEmpty(endData) && !"null".equals(endData)){
				String endDataResult = String.format(endDataPre, endData);
				holder.rewardEnddate.setText(endDataResult);
			} else {
				holder.rewardEnddate.setText("");
			}
			
//			String status = rewardData.getTask_status();
////			String statusStr = "";
//			GlobalDataTable statusData = GlobalDataTable.getTypeDataById((AppContext)context, DBUtils.GLOBALDATA_TYPE_PUBLISHSTATUS, status);
//			holder.rewardEnddate.setVisibility(View.VISIBLE);
//			String statusStr = statusData.getName();
//			if(null != status){
//				switch(Integer.valueOf(status)){
//				case HeadhunterPublic.TASK_STATUS_PUBLISHING:
//					{
//						//正在进行中的状态
////						statusStr = context.getResources().getString(R.string.my_reward_list_reward_status_1);
//						holder.rewardStatus.setTextColor(context.getResources().getColor(R.color.resume_button_color));
//					}
//					break;
//				case HeadhunterPublic.TASK_STATUS_AUDIT:
//					{//审核
////						statusStr = context.getResources().getString(R.string.my_reward_list_reward_status_4);
//						holder.rewardStatus.setTextColor(context.getResources().getColor(R.color.resume_button_color));
//						holder.rewardEnddate.setVisibility(View.INVISIBLE);
//					}
//					break;
//				case HeadhunterPublic.TASK_STATUS_PAUSE:
//					{
//						//暂停
////						statusStr = context.getResources().getString(R.string.my_reward_list_reward_status_2);
//						holder.rewardStatus.setTextColor(context.getResources().getColor(R.color.resume_text_color));
//					}
//					break;
//				case HeadhunterPublic.TASK_STATUS_EXPIRED:
//					{
//						//过期
////						statusStr = context.getResources().getString(R.string.my_reward_list_reward_status_3);
//						holder.rewardStatus.setTextColor(context.getResources().getColor(R.color.red));
//					}
//					break;
//				case HeadhunterPublic.TASK_STATUS_ARCHIVE:
//					{
//						//归档  对用户来说都是过期
////						statusStr = context.getResources().getString(R.string.my_reward_list_reward_status_3);
//						holder.rewardStatus.setTextColor(context.getResources().getColor(R.color.red));
//					}
//					break;
//				}
//			}
			// 我的悬赏相关
			String strPayFlagCharacter = "";
			String strPayFlag = rewardData.getPay_flag();
			if(null != strPayFlag && !strPayFlag.isEmpty()){
				strPayFlagCharacter = UIHelper.getIdName(context, strPayFlag,
						DBUtils.GLOBALDATA_TYPE_PAYFLAG);
				
				// 字体颜色
				switch(Integer.valueOf(strPayFlag)){
				case HeadhunterPublic.PAY_FLAG_NOPAY:	// 未付款
					{
						holder.rewardStatus.setTextColor(context.getResources().
								getColor(R.color.red));
					}
					break;
				case HeadhunterPublic.PAY_FLAG_PAY:		// 已付款
					{
						holder.rewardStatus.setTextColor(context.getResources().
								getColor(R.color.resume_button_color));
					}
					break;
				case HeadhunterPublic.PAY_FLAG_REFUNDMENTINGING:	// 退款中....
					{
						holder.rewardStatus.setTextColor(context.getResources().
								getColor(R.color.red));
					}
					break;
				case HeadhunterPublic.PAY_FLAG_REFUNDMENTINGSUC:	// 退款成功
					{
						holder.rewardStatus.setTextColor(context.getResources().
								getColor(R.color.resume_button_color));
					}
					break;
				default:
					break;
				}
			}
			holder.rewardStatus.setText(strPayFlagCharacter);
		}
		
		return convertView;
	}
	
	
	public static class MyRewardListViewHolder{
		
		TextView rewardBonus;	
		TextView rewardTitle;	
		TextView rewardPost;	
		TextView rewardStatus;	
		TextView rewardEnddate;
		
		public MyRewardListViewHolder(View view){
			this.rewardBonus = (TextView)view.findViewById(R.id.my_reward_bonus);
			this.rewardTitle = (TextView)view.findViewById(R.id.my_reward_title);
			this.rewardPost = (TextView)view.findViewById(R.id.my_reward_post);
			this.rewardStatus = (TextView)view.findViewById(R.id.my_reward_status);
			this.rewardEnddate = (TextView)view.findViewById(R.id.my_reward_enddate);
		}
	}
	
	/**
	 * 新建行业颜色图片
	 * @param nWidth
	 * @param nHeight
	 * @param nColor
	 * @return
	 */
	public Bitmap createCategoryColorBmp(int nWidth, int nHeight, int nColor){
		Bitmap bmpRes = Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);
		
		Canvas canvasTemp = new Canvas(bmpRes);
		canvasTemp.drawColor(nColor);
		//canvasTemp.drawColor(Color.argb(0x55, 0xff, 0, 0));    

		return bmpRes;
	}
	
	/**
	 * 图片拼接(垂直拼接)
	 * @param bmpList 图片List
	 * @return 拼接好的新图片
	 */
	public Bitmap addVerticalBmp(List<Bitmap> bmpList){
		int nWidth = 0;
		int nHeight = 0;
		
		for(Bitmap bmp : bmpList){
			nHeight += bmp.getHeight();
			nWidth = Math.max(bmp.getWidth(), nWidth);
		}
		
		Bitmap bmpRes = Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);
		Canvas canvasTemp = new Canvas(bmpRes);
		
		int nW = 0;
		int nH = 0;
		for(Bitmap bmp : bmpList){
			
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
	public Bitmap createCategoryBmp(int nHeight, List<OneLevelData> lsOld){
		
		int nW = 5;
		int nH = nHeight/lsOld.size();
		
		List<Bitmap> bmpList = new ArrayList<Bitmap>();
		
		for(OneLevelData old : lsOld){
			String strID = old.getId();
			// 紫色
			int nColor = Color.argb(127, 920, 40, 124);
			if(null != strID){
				int nType = Integer.valueOf(strID);
				switch(nType){
				case HeadhunterPublic.INDUSTRY_PARENTDATAID_TECHNOLOGY:
					{
						// 蓝色
						nColor = Color.argb(127, 43, 147, 252);
					}
					break;
				case HeadhunterPublic.INDUSTRY_PARENTDATAID_ADVERTISINGCOMPANY:
					{
						// 大红
						nColor = Color.argb(127, 193, 26, 17);
					}
					break;
				case HeadhunterPublic.INDUSTRY_PARENTDATAID_MEDIA:
					{
						// 绿色
						nColor = Color.argb(127, 116, 194, 86);
					}
					break;
				case HeadhunterPublic.INDUSTRY_PARENTDATAID_BRAND:
					{
						// 橘色
						nColor = Color.argb(127, 232, 117, 5);
					}
					break;
				case HeadhunterPublic.INDUSTRY_PARENTDATAID_OHTER:
				default:
					{
						// 紫色
						nColor = Color.argb(127, 920, 40, 124);
					}
					break;
				}
			}
			
			Bitmap bmp = createCategoryColorBmp(nW, nH, nColor);
			
			bmpList.add(bmp);
		}
		
		return addVerticalBmp(bmpList);
	}
}

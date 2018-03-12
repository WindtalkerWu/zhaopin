package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.app.ui.BaseFragment;
import com.qianniu.zhaopin.app.ui.RewardInfoActivity;
import com.qianniu.zhaopin.app.view.InsidersAndCompanyListItem;
import com.qianniu.zhaopin.app.view.RewardinfoWaterfallItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;

public class RewardInfoWaterFallAdapter2 extends BaseAdapter{
	private BaseActivity m_activity;
	private BaseFragment m_fragment;
	private Context m_Context;
	
	/**************************************************************/
	// Activity类型
    private int m_nActivityType;
	private static final int ACTIVITY_TYPE_BASEACTIVITY = 0;		// BaseActivity
	private static final int ACTIVITY_TYPE_BASEFRAGMENT = 1;		// BaseFragment
	/**************************************************************/
	// 悬赏数据列表
	private List<RewardData> m_lsRD;
	
	private BitmapManager m_bmpManager;
	
	private int m_nChooseItem = -1;
	
	private boolean b_IsLoading = false;
	
	private boolean m_bIsGetWidth = false;
	private RewardInfoWaterFallViewHolder m_holder = null;
	private int m_nWidth = 0;
	
    public static int m_nLogoWidth = -1;	// logo图片的宽带
    
    private long lastClickTime = 0;
    private static final int CLICK_TIME = 500; 
	
	public RewardInfoWaterFallAdapter2(BaseActivity activity, List<RewardData> lsRD){
		this.m_activity = activity;
		this.m_Context = activity.getApplicationContext();
		this.m_lsRD = lsRD;
		this.m_bmpManager = new BitmapManager(null);
		this.m_nActivityType = ACTIVITY_TYPE_BASEACTIVITY;
		
		initLogoWidth();
	}
	
	public RewardInfoWaterFallAdapter2(BaseFragment fragment, List<RewardData> lsRD){
		this.m_fragment = fragment;
		this.m_Context = m_fragment.getActivity();
		this.m_lsRD = lsRD;
		this.m_bmpManager = new BitmapManager(null);
		this.m_nActivityType = ACTIVITY_TYPE_BASEFRAGMENT;
		
		initLogoWidth();
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
		m_holder = null;
		RewardinfoWaterfallItem rwItem = null;
		
		final RewardData rd = m_lsRD.get(position);
		
		if(null == convertView){
			rwItem = new RewardinfoWaterfallItem(m_Context, m_bmpManager, rd, (int)(m_nLogoWidth/2.25));
			convertView = rwItem;
			holder = new RewardInfoWaterFallViewHolder(rwItem);
			convertView.setTag(holder);
		} else {
			holder = (RewardInfoWaterFallViewHolder) convertView.getTag();
			rwItem = holder.rewardinfoWaterfallItem;
		}
		
//		if(!m_bIsGetWidth){
//			rwItem.updateRewardinfo(rd);
//			m_holder = holder;
//			// 获取view宽度
//			ViewTreeObserver vto = m_holder.rewardinfoWaterfallItem
//					.getViewTreeObserver();
//			vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//				@Override
//				public boolean onPreDraw() {
//					// TODO Auto-generated method stub
//					if (!m_bIsGetWidth) {
//						m_nWidth = m_holder.rewardinfoWaterfallItem
//								.getMeasuredWidth();
//						
//						m_bIsGetWidth = true;	
//						notifyDataSetChanged();
//					}
//					return true;
//				}
//			});	
//		}else{
////			rwItem.setImgHeight((int)(m_nWidth/2.25));
//			rwItem.updateRewardinfo(rd, (int)(m_nWidth/2.25));
//		}
		
		rwItem.updateRewardinfo(rd);
		final int nPosition = position;
		rwItem.setListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(isFastDoubleClick()){
					return;
				}
				
				m_nChooseItem = nPosition;
				
				// 数据传输
				Bundle bundle = new Bundle();
				bundle.putSerializable(HeadhunterPublic.REWARD_DATATRANSFER_REWARDDATA, rd);
				bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_ACTIYITYTYPE,
						HeadhunterPublic.ACTIVITY_TYPE_REWARDLIST);

				// 进入悬赏详细界面
				if(ACTIVITY_TYPE_BASEFRAGMENT == m_nActivityType){
					Intent intent = new Intent();
					intent.setClass(m_Context, RewardInfoActivity.class);
					intent.putExtras(bundle);
					m_fragment.startActivityForResult(intent,
							HeadhunterPublic.RESULT_ACTIVITY_CODE);					
				}else{
					Intent intent = new Intent();
					intent.setClass(m_Context, RewardInfoActivity.class);
					intent.putExtras(bundle);
					m_activity.startActivityForResult(intent,
							HeadhunterPublic.RESULT_ACTIVITY_CODE);						
				}

			}
		});

		return convertView;
	}

	public static class RewardInfoWaterFallViewHolder {
		RewardinfoWaterfallItem rewardinfoWaterfallItem;
		
		public RewardInfoWaterFallViewHolder(RewardinfoWaterfallItem view){
			rewardinfoWaterfallItem = view;
		}
	}
	
	/**
	 * @return
	 */
	public int getChooseItem() {
		return m_nChooseItem;
	}
	
    /**
     * 防止多次点击
     * @return
     */
    private boolean isFastDoubleClick() {   
        long time = System.currentTimeMillis();   
        long timeD = time - lastClickTime;   
        if ( 0 < timeD && timeD < CLICK_TIME) {
            return true;      
        }    
        
        lastClickTime = time;      
        return false;      
    } 
    
	/**
	 * 
	 */
	private void initLogoWidth() {	
		DisplayMetrics dm = null;   	
		switch(m_nActivityType){
		case ACTIVITY_TYPE_BASEFRAGMENT:
			{
				dm = m_fragment.getResources().getDisplayMetrics();
			}
			break;
		case ACTIVITY_TYPE_BASEACTIVITY:
		default:
			{
				dm = m_activity.getResources().getDisplayMetrics();
			}
			break;
		}

		if(null != dm){
			int screenWidth = dm.widthPixels;
			m_nLogoWidth = (screenWidth - CommonUtils.dip2px(m_Context, InsidersAndCompanyListItem.MARGIN) 
					* ( 2 + InsidersListAdapter.maxOneLine - 1)) / InsidersListAdapter.maxOneLine - 1;			
		}
	}
	
	public void upData(List<RewardData> lsRD){
		this.m_lsRD = lsRD;
	}
}

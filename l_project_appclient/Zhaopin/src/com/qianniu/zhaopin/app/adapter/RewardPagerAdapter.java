package com.qianniu.zhaopin.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.bean.RewardFilterCondition;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.ui.Fragment_rewardList;
import com.qianniu.zhaopin.app.ui.SubscriptionManageFragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class RewardPagerAdapter extends FragmentPagerAdapter implements
	ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener{
	
	private ViewPager m_viewPager;
	private TabHost m_tabHost;

	private Context m_Context;
	private FragmentManager m_fragmentManager;
	
	private int m_nRequestType;			// 请求的悬赏任务过滤类型
	private int m_nRequestDataType;		// 请求的数据类型(个人, 公司, 混合)
	private int m_nIndustryType;
	
	private RewardFilterCondition m_filters;
	
	private int m_nPosition; 	// 当前选中的
	private int m_nOldPosition; // 上次选中的
	
	private final ArrayList<TabRewardInfo> m_tabRewardInfo = new ArrayList<TabRewardInfo>();
	
	class TabRewardInfo {
		private String tag;
		private int type;
		private Bundle bundle;

		public TabRewardInfo(String strTag, int nType, Bundle args){
			tag = strTag;
			type = nType;
			bundle = args;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public Bundle getBundle() {
			return bundle;
		}

		public void setBundle(Bundle bundle) {
			this.bundle = bundle;
		}
	}
	
	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}
	
	public RewardPagerAdapter(Context context, FragmentManager fm,
			TabHost tabHost,
			ViewPager viewPage) {
		super(fm);
		// TODO Auto-generated constructor stub
		m_Context = context;
		m_fragmentManager = fm;
		
		m_viewPager = viewPage;
		m_viewPager.setAdapter(this);
		m_viewPager.setOnPageChangeListener(this);
		
		m_tabHost = tabHost;
		m_tabHost.setOnTabChangedListener(this);
		
		initData();
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		TabRewardInfo info = m_tabRewardInfo.get(position);
		
		Bundle bundle = info.getBundle();
		if(null != bundle){
			// 请求的数据类型(个人, 公司, 混合)
			bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_REQUESTDATATYPE,
					m_nRequestDataType);
			// 请求的悬赏任务过滤类型
			bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_REQUESTTYPE,
					m_nRequestType);
			
			info.setBundle(bundle);
		}
		
		return Fragment.instantiate(m_Context, Fragment_rewardList.class.getName(), bundle);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_tabRewardInfo.size();
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		m_nOldPosition = m_nPosition;
		m_nPosition = m_tabHost.getCurrentTab();
		m_viewPager.setCurrentItem(m_nPosition);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		for(int i = 0; i < m_tabRewardInfo.size(); i++){
			View view = m_tabHost.getTabWidget().getChildAt(i);
			if(null != view){
				TextView tv = (TextView)view.findViewById(R.id.reward_tv_tab);
				if(null != tv){
					if(position == i){
						tv.setTextColor(m_Context.getResources().
								getColor(R.color.resume_button_color));	
					}else{
						tv.setTextColor(Color.BLACK);	
					}				
				}
			}
		}
		
		m_nPosition = position;
		
		TabWidget widget = m_tabHost.getTabWidget();
		int oldFocusability = widget.getDescendantFocusability();
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		m_tabHost.setCurrentTab(position);
		widget.setDescendantFocusability(oldFocusability);
		
		// 更新数据
		Fragment_rewardList frmRewardList = (Fragment_rewardList)m_fragmentManager.
				findFragmentByTag("android:switcher:"+R.id.fragmentreward_viewpager+":" + String.valueOf(position));
		frmRewardList.upData();
	}
	
	/**
	 * 
	 */
	private void initData(){
		m_nRequestDataType = HeadhunterPublic.REWARD_REQUESTDATATYPE_COMPANY;
		m_nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_LIKE;
		// 行业
		m_nIndustryType = HeadhunterPublic.REWARD_INDUSTRYTYPE_ALL;
		
		m_nOldPosition = 0;
		m_nPosition = 0;
	}
	
	/**
	 * @param strTitle
	 * @return
	 */
	private View createTabView(String strTitle){
        View view = LayoutInflater.from(m_Context).inflate(R.layout.common_tab_reward, null);
        
        TextView tv = (TextView) view.findViewById(R.id.reward_tv_tab);
        tv.setText(strTitle);
        if(null != strTitle && strTitle.equals("全部")) {
        	tv.setTextColor(m_Context.getResources().
    				getColor(R.color.resume_button_color));
        }
        
        return view;
	}
	
	/**
	 * @param nType
	 * @return
	 */
	private int getPosition(int nType){
		int nPosition = 0;
		
		for(int i = 0; i < m_tabRewardInfo.size(); i++){
			TabRewardInfo info = m_tabRewardInfo.get(i);
			if(null != info){
				if(nType == info.getType()){
					return i;
				}
			}
		}
		
		return nPosition;
	}
	
	/**
	 * @param strTag
	 * @param strTitle
	 * @param nTyep
	 */
	public void addTab(String strTag, String strTitle, int nTyep){
		TabHost.TabSpec tabSpec = m_tabHost.newTabSpec(strTag).
				setIndicator(createTabView(strTitle));
		tabSpec.setContent(new DummyTabFactory(m_Context));
		
		TabRewardInfo info = new TabRewardInfo(strTag, nTyep, null);
		
		Bundle bundle = new Bundle();
		// 请求的悬赏任务过滤类型		
		bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_REQUESTTYPE,
				m_nRequestType);
		// 请求的数据类型(个人, 公司, 混合)
		bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_REQUESTDATATYPE,
				m_nRequestDataType);
		
		// 行业类型
		switch(nTyep){
			case HeadhunterPublic.REWARD_INDUSTRYTYPE_IT: // IT/互联网
				{
					m_nIndustryType = HeadhunterPublic.REWARD_INDUSTRYTYPE_IT;
				}
				break;
			case HeadhunterPublic.REWARD_INDUSTRYTYPE_GAMES:// 游戏
				{
					m_nIndustryType = HeadhunterPublic.REWARD_INDUSTRYTYPE_GAMES;			
				}
				break;
			case HeadhunterPublic.REWARD_INDUSTRYTYPE_ADVERTISEMENT:	// 广告传媒
				{
					m_nIndustryType = HeadhunterPublic.REWARD_INDUSTRYTYPE_ADVERTISEMENT;					
				}
				break;
			case HeadhunterPublic.REWARD_INDUSTRYTYPE_ALL:	// 全部
			default:
				{
					m_nIndustryType = HeadhunterPublic.REWARD_INDUSTRYTYPE_ALL;					
				}
				break;
		}
		bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_INDUSTRYTYPE,
				m_nIndustryType);
		
		info.setBundle(bundle);
		m_tabRewardInfo.add(info);

		m_tabHost.addTab(tabSpec);
		
		notifyDataSetChanged();
	}
	
	/**
	 * 更新请求的数据类型(个人, 公司, 混合)
	 */
	public void upRequestDataType(int nType){
		m_nRequestDataType = nType;
		
		// 更新数据
		upBundle();
	}

	/**
	 * 更新请求的悬赏任务过滤类型
	 * @param nType
	 */
	public void upRequestType(int nType){
		m_nRequestType = nType; 	
		
		// 更新数据
		upBundle();
	}
	
	/**
	 * 更新过滤条件
	 */
	public void upFilters(RewardFilterCondition rfc){	
		if(null == rfc){
			return;
		}
		
		m_filters = rfc;
		
		// 更新数据
		upBundle(m_nPosition);
		
		m_filters = null;
	}
	
	/**
	 * 更新指定Fragement界面的过滤条件
	 * @param nIndustryType
	 * @param rfc
	 */
	public void upFilters(int nIndustryType, RewardFilterCondition rfc){
		if(null == rfc){
			return;
		}
		
		m_filters = rfc;
		
		int nPosition = getPosition(nIndustryType);
		TabRewardInfo info = m_tabRewardInfo.get(nPosition);
		if(null != info){
			Bundle bundle = info.getBundle();
			if(null != bundle){
				// 请求的数据类型(个人, 公司, 混合)
				bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_REQUESTDATATYPE,
						m_nRequestDataType);
				// 请求的悬赏任务过滤类型
				bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_REQUESTTYPE, m_nRequestType);
				// 请求的过滤条件
				if(null != m_filters){
					bundle.putSerializable(HeadhunterPublic.REWARD_DATATRANSFER_FILTERS,
							m_filters);
				}
				
				info.setBundle(bundle);	
				
				Fragment_rewardList frmRewardList = (Fragment_rewardList)m_fragmentManager.
						findFragmentByTag("android:switcher:"+R.id.fragmentreward_viewpager+":" + String.valueOf(nPosition));
				if(null != frmRewardList){
					frmRewardList.upBundle(bundle);
				}
			}

		}
		
		m_tabHost.setCurrentTab(nIndustryType);
	}
	
	/**
	 * @param nPosition
	 */
	public void upBundle(int nPosition){
		if(null == m_tabRewardInfo){
			return;
		}
		
		Fragment_rewardList frmRewardList = (Fragment_rewardList)m_fragmentManager.
				findFragmentByTag("android:switcher:"+R.id.fragmentreward_viewpager+":" + String.valueOf(nPosition));
			
		if(null != frmRewardList){
			TabRewardInfo info = m_tabRewardInfo.get(nPosition);
			if(null != info){
				Bundle bundle = info.getBundle();
				// 请求的数据类型(个人, 公司, 混合)
				bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_REQUESTDATATYPE,
						m_nRequestDataType);
				// 请求的悬赏任务过滤类型
				bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_REQUESTTYPE, m_nRequestType);
				// 请求的过滤条件
				if(null != m_filters){
					bundle.putSerializable(HeadhunterPublic.REWARD_DATATRANSFER_FILTERS,
							m_filters);
				}
				
				info.setBundle(bundle);
				
				frmRewardList.upBundle(bundle);
				
				frmRewardList.upData();		
			}		
		}
	}
	
	/**
	 * 
	 */
	public void upBundle(){
		if(null == m_tabRewardInfo){
			return;
		}
		
		for(int i = 0; i < m_tabRewardInfo.size(); i++){
			Fragment_rewardList frmRewardList = (Fragment_rewardList)m_fragmentManager.
					findFragmentByTag("android:switcher:"+R.id.fragmentreward_viewpager+":" + String.valueOf(i));
			
			if(null != frmRewardList){
				TabRewardInfo info = m_tabRewardInfo.get(i);
				if(null != info){
					Bundle bundle = info.getBundle();
					// 请求的数据类型(个人, 公司, 混合)
					bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_REQUESTDATATYPE,
							m_nRequestDataType);
					// 请求的悬赏任务过滤类型
					bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_REQUESTTYPE, m_nRequestType);
					// 请求的过滤条件
					if(null != m_filters &&
							i == m_nPosition){
						bundle.putSerializable(HeadhunterPublic.REWARD_DATATRANSFER_FILTERS,
								m_filters);
					}
					
					info.setBundle(bundle);
					
					frmRewardList.upBundle(bundle);
					
					if(i == m_nPosition){
						frmRewardList.upData();
					}		
				}		
			}
		}
	}
	
	/**
	 * 获取当前Fragement界面类型(全部;IT/互联网;游戏;广告传媒)
	 * @return
	 */
	public int getIndustryType(){
		TabRewardInfo info = m_tabRewardInfo.get(m_nPosition);
		if(null == info){
			return -1;
		}
		
		Bundle bundle = info.getBundle();
		if(null == bundle){
			return -1;
		}
		
		return bundle.getInt(HeadhunterPublic.REWARD_DATATRANSFER_INDUSTRYTYPE);
	}
}

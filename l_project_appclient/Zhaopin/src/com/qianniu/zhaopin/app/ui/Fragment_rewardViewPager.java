package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.adapter.PullDownListAdapter;
import com.qianniu.zhaopin.app.adapter.RewardPagerAdapter;
import com.qianniu.zhaopin.app.bean.PullDownListInfo;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.bean.RewardFilterCondition;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class Fragment_rewardViewPager extends BaseFragment 
	implements OnClickListener
{
	private Context m_Context;
	private AppContext m_appContext; //
	private FragmentManager m_fragmentManager;
	
	private View m_viewReward;
	
	private ImageView m_imgBtnType;					// 类型切换按钮(公司悬赏, 个人悬赏)
	private ImageView m_imgBtnSearch;				// 搜索设置
	private ImageView m_imgBtnPublishreward;		// 发悬赏任务按钮
	
	// 标题栏
	private TextView m_tvSortName;
	
	/***************下拉菜单相关*****************************/
	private PullDownListAdapter m_PullDownLA;
	private List<PullDownListInfo> m_PDLI;
	private ListView m_PDLV;
	/****************************************************/
	private TabHost m_tabHost;
	private ViewPager m_ViewPager;
	
	private RewardPagerAdapter m_rewardPagerAdapter;
	/****************************************************/
	private int m_nRequestDataType;			// 请求的数据类型(个人, 公司, 混合)
	
	// 是否点击类型切换按钮(个人/公司)
//	private boolean m_bBtnTypeClick = false;
	
	/****************************************************/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode) {
			switch (resultCode) {
			case HeadhunterPublic.RESULT_REWARDSEARCH_OK:
				{
					// 获取悬赏任务搜索设置界面传递过来的数据
					Bundle bundle = data.getExtras();
					if (null != bundle) {
						RewardFilterCondition rfc = (RewardFilterCondition) bundle
								.getSerializable(HeadhunterPublic.REWARDSEARCH_DATATRANSFER_FILTERS);
	
						if(null != rfc){
							String strIndustry[] = rfc.getIndustry_fid();
							if(null != strIndustry){
								int nIndustryType;
								if(strIndustry.length > 1){
									nIndustryType = HeadhunterPublic.REWARD_INDUSTRYTYPE_ALL;
								}else{
									if(null != strIndustry[0] && !strIndustry[0].isEmpty()){
										nIndustryType = Integer.valueOf(strIndustry[0]);
										// 如果选中的行业不是(IT/互联网, 游戏, 广告传媒)时, 统一归到全部中
										if(HeadhunterPublic.REWARD_INDUSTRYTYPE_IT != nIndustryType &&
												HeadhunterPublic.REWARD_INDUSTRYTYPE_GAMES != nIndustryType &&
												HeadhunterPublic.REWARD_INDUSTRYTYPE_ADVERTISEMENT != nIndustryType){
											nIndustryType = HeadhunterPublic.REWARD_INDUSTRYTYPE_ALL;
										}										
									}else{
										m_rewardPagerAdapter.upFilters(rfc);
										return;
									}
								}
								
								if(null != m_rewardPagerAdapter){
									int nNowIndustryType = m_rewardPagerAdapter.getIndustryType();
									if(nIndustryType != nNowIndustryType){
										m_rewardPagerAdapter.upFilters(nIndustryType, rfc);
										return;
									}else{
										m_rewardPagerAdapter.upFilters(rfc);
										return;
									}
								}else{
									return;
								}
							}
	
							if(null != m_rewardPagerAdapter){
								m_rewardPagerAdapter.upFilters(rfc);
							}
						}
					}
				}
				break;
			case -1: // 登录成功
				{
					// 检查手机是否认证
//					checkMobileAuthStatus();
					showPublishRewardActivity();
				}
			default:
				break;
			}
		}else if(2 == requestCode){
			if(-1 == resultCode){
				// 进入悬赏发布界面
				showPublishRewardActivity();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		m_Context = this.getActivity();
		m_appContext = (AppContext) this.getActivity().getApplication();
		m_fragmentManager = this.getFragmentManager();

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		// 友盟统计
		UmShare.UmPause(m_Context);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 友盟统计
		UmShare.UmResume(m_Context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return super.onCreateView(inflater, container, savedInstanceState);
		if (m_viewReward != null) {
			ViewGroup p = (ViewGroup) m_viewReward.getParent();
			if (p != null) {
				p.removeAllViewsInLayout();
			}

		}else{
			m_viewReward = inflater.inflate(R.layout.fragment_rewardviewpager, container,
					false);
	
			// 初始化数据
			initData();
			// 初始化ViewPage
			initViewPage();
			// 初始化控件
			initControl();	
		}
		
		return m_viewReward;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int vid = v.getId();
		switch (vid) {
		case R.id.fragmentreward_tv_sortname: 
			{
				if (null != m_PDLV) {
					switch (m_PDLV.getVisibility()) {
					case View.VISIBLE: {
						m_tvSortName
								.setBackgroundResource(R.drawable.common_tv_bg_pulldown_normal);
						m_PDLV.setVisibility(View.GONE);
					}
						break;
					case View.GONE: {
						m_tvSortName
								.setBackgroundResource(R.drawable.common_tv_bg_pulldown_open);
						m_PDLV.setVisibility(View.VISIBLE);
					}
						break;
					default:
						break;
					}
				}
			}
			break;
		case R.id.fragmentreward_btn_search: 
			{
				// 友盟统计--任务--搜索按钮
				UmShare.UmStatistics(m_Context, "Reward_SearchButton");
				
				// 隐藏下拉菜单
				hidePullDownList();

				// 进入悬赏搜索条件设置界面
				Intent intent = new Intent();
				intent.setClass(m_Context, RewardSearchActivity.class);
				startActivityForResult(intent,
						HeadhunterPublic.RESULT_ACTIVITY_CODE);
	
			}
			break;
		case R.id.fragmentreward_btn_publishreward: 
			{
				// 友盟统计
				UmShare.UmStatistics(m_Context, "Reward_PublishrewardButton");
	
				// 隐藏下拉菜单
				hidePullDownList();

				// 判断是否登录
				if (checkISLogin()) {
					// 检查手机是否认证
//					checkMobileAuthStatus();
					showPublishRewardActivity();
				}
			}
			break;
		case R.id.fragmentreward_btn_type:
			{
				// 隐藏下拉菜单
				hidePullDownList();
				
//				m_bBtnTypeClick = true;
				
				// 切换数据类型(个人/公司)
				switch(m_nRequestDataType){
				case HeadhunterPublic.REWARD_REQUESTDATATYPE_COMPANY:		// 点击之前是公司
					{
						m_nRequestDataType = HeadhunterPublic.REWARD_REQUESTDATATYPE_PERSONAL;
						m_imgBtnType.setImageResource(
								R.drawable.common_button_company);
						
						// 友盟统计--任务--个人任务按钮
						UmShare.UmStatistics(m_Context, "Reward_PersonalButton");
					}
					break;
				case HeadhunterPublic.REWARD_REQUESTDATATYPE_PERSONAL:	// 点击之前是个人
				default:
					{
						m_nRequestDataType = HeadhunterPublic.REWARD_REQUESTDATATYPE_COMPANY;
						m_imgBtnType.setImageResource(
								R.drawable.common_button_personal);
						
						// 友盟统计--任务--公司任务按钮
						UmShare.UmStatistics(m_Context, "Reward_CompanyButton");
					}
					break;
				}
				
				// 更新数据
				if(null != m_rewardPagerAdapter){
					m_rewardPagerAdapter.upRequestDataType(m_nRequestDataType);
				}	
			}
			break;
		default:
			break;
		}	
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		// 默认获取公司悬赏任务数据
		m_nRequestDataType = HeadhunterPublic.REWARD_REQUESTDATATYPE_COMPANY;
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 类型切换按钮(公司悬赏, 个人悬赏)
		m_imgBtnType = (ImageView) m_viewReward
				.findViewById(R.id.fragmentreward_btn_type);
		m_imgBtnType.setOnClickListener(this);
		
		// 搜索设置按钮
		m_imgBtnSearch = (ImageView) m_viewReward
				.findViewById(R.id.fragmentreward_btn_search);
		m_imgBtnSearch.setOnClickListener(this);

		// 发悬赏任务按钮
		m_imgBtnPublishreward = (ImageView) m_viewReward
				.findViewById(R.id.fragmentreward_btn_publishreward);
		m_imgBtnPublishreward.setOnClickListener(this);
		
		// 标题栏
		m_tvSortName = (TextView) m_viewReward
				.findViewById(R.id.fragmentreward_tv_sortname);
		m_tvSortName.setOnClickListener(this);
		// 任务默认猜你喜欢排序
		m_tvSortName.setText(getText(R.string.str_reward_sort_youlike));
		// 初始化下拉菜单
		initPullDownListView();
	}
	
	/**
	 * 初始化ViewPage
	 */
	private void initViewPage(){
		m_tabHost = (TabHost)m_viewReward.findViewById(R.id.fragmentreward_tabhost); 
		m_tabHost.setup();
		
		m_ViewPager = (ViewPager)m_viewReward.findViewById(R.id.fragmentreward_viewpager);

		m_rewardPagerAdapter = new RewardPagerAdapter(m_Context, m_fragmentManager,
				m_tabHost, m_ViewPager);
		
		m_rewardPagerAdapter.addTab("all", "全部", HeadhunterPublic.REWARD_INDUSTRYTYPE_ALL);
		m_rewardPagerAdapter.addTab("it", "IT/互联网", HeadhunterPublic.REWARD_INDUSTRYTYPE_IT);
		m_rewardPagerAdapter.addTab("games", "游戏", HeadhunterPublic.REWARD_INDUSTRYTYPE_GAMES);
		m_rewardPagerAdapter.addTab("advertisement", "广告/传媒", HeadhunterPublic.REWARD_INDUSTRYTYPE_ADVERTISEMENT);
	}
	
	/**
	 * 初始化下拉菜单
	 */
	private void initPullDownListView(){
		// 弹出下拉ListView
		m_PDLV = (ListView) m_viewReward
				.findViewById(R.id.fragmentreward_lv_pulldown);
		m_PDLV.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int nItem,
					long arg3) {
				// TODO Auto-generated method stub
				PullDownListInfo pli = m_PDLI.get(nItem);
				if (!pli.getIsChoose()) {
					m_tvSortName.setText(pli.getText());
					//
					resetPullDownListView();
					pli.setIsChoose(true);
					m_PullDownLA.notifyDataSetChanged();
				}

				m_tvSortName
						.setBackgroundResource(R.drawable.common_tv_bg_pulldown_normal);
				m_PDLV.setVisibility(View.GONE);

				int nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_LIKE;
				switch (nItem) {
				case 1: 
					{
						nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_NEW;
						
						// 友盟统计--任务--排序--最新发布
						UmShare.UmStatistics(m_Context, "Reward_Requesttype_New");
					}
					break;
				case 2: {
//					nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_PERIPHERY;
//					m_filters = new RewardFilterCondition();
//					m_bIsSearch = false;
	//
//					// 友盟统计--任务--排序--周边职位
//					UmShare.UmStatistics(m_Context, "Reward_Requesttype_Reriphery");
//				}
//					break;
//				case 3: {
					nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_SORT;
//					m_tvSortName.setText(getText(R.string.str_reward_sort_reward));

					// 友盟统计--任务--排序--悬赏排名
					UmShare.UmStatistics(m_Context, "Reward_Requesttype_Sort");
				}
					break;
				case 0:
				default: 
					{
						nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_LIKE;
						
						// 友盟统计--任务--排序--猜你喜欢
						UmShare.UmStatistics(m_Context, "Reward_Requesttype_Like");
					}
					break;
				}
				
				if(null != m_rewardPagerAdapter){
					m_rewardPagerAdapter.upRequestType(nRequestType);
				}	
			}
			
		});
		
		// 初始化下拉菜单数据
		initPullDownData();
	}
	
	/**
	 * 初始化下拉菜单数据
	 */
	private void initPullDownData() {
		m_PDLI = new ArrayList<PullDownListInfo>();

		// 任务默认猜你喜欢排序
		PullDownListInfo pdl = new PullDownListInfo();
		pdl.setText(getText(R.string.str_reward_sort_youlike).toString());
		pdl.setIsChoose(true);
		m_PDLI.add(pdl);

		// 最新发布排序
		PullDownListInfo pdl2 = new PullDownListInfo();
		pdl2.setText(getText(R.string.str_reward_sort_new).toString());
		pdl2.setIsChoose(false);
		m_PDLI.add(pdl2);

//		 PullDownListInfo pdl3 = new PullDownListInfo();
//		 pdl3.setText(getText(R.string.str_reward_sort_peripheral_reward).toString());
//		 m_PDLI.add(pdl3);

		// 悬赏排序
		PullDownListInfo pdl4 = new PullDownListInfo();
		pdl4.setText(getText(R.string.str_reward_sort_reward).toString());
		pdl4.setIsChoose(false);
		m_PDLI.add(pdl4);

		m_PullDownLA = new PullDownListAdapter(m_appContext, m_PDLI);
		m_PDLV.setAdapter(m_PullDownLA);
	}
	
	/**
	 * 重置 下拉ListView
	 */
	private void resetPullDownListView() {
		if (null == m_PDLI) {
			return;
		}

		for (PullDownListInfo pdli : m_PDLI) {
			if (pdli.getIsChoose()) {
				pdli.setIsChoose(false);
			}
		}
	}
	
	/**
	 * 隐藏下拉菜单
	 */
	public void hidePullDownList(){
		if (null != m_PDLV) {
			m_PDLV.setVisibility(View.GONE);
		}
		
		if(null != m_tvSortName){
			m_tvSortName
				.setBackgroundResource(R.drawable.common_tv_bg_pulldown_normal);
		}
	}
	
	/**
	 * 变更类型切换按钮背景图
	 */
//	private void changeBtnTypeBG(){
//		if(m_bBtnTypeClick){
//			switch(m_nRequestDataType){
//			case HeadhunterPublic.REWARD_REQUESTDATATYPE_COMPANY:		// 公司
//			default:
//				{
//					m_imgBtnType.setImageResource(
//							R.drawable.common_button_personal);
//				}
//				break;
//			case HeadhunterPublic.REWARD_REQUESTDATATYPE_PERSONAL:	// 个人
//				{
//					m_imgBtnType.setImageResource(
//							R.drawable.common_button_company);
//				}
//				break;
//			}
//			
//			m_bBtnTypeClick = false;
//		}
//	}
	
	/**
	 * 进入手机认证界面
	 */
	private void showMobileAuthActivity() {
		Intent intent = new Intent(m_Context, MobileAuthActivity.class);
		startActivityForResult(intent, 2);
	}
	
	/**
	 * 进入悬赏发布界面
	 */
	private void showPublishRewardActivity(){
		// 进入发布悬赏界面
		Intent intent = new Intent();
		intent.setClass(m_Context, PublishRewardActivity.class);
		intent.putExtra(Fragment_reward.fromMainReward, true);
		getActivity().startActivityForResult(intent,
				HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 判断是否登录
	 * 
	 * @return
	 */
	private boolean checkISLogin() {
		if (!m_appContext.isLogin()) {
			// 进入行业选择界面
			Intent intent = new Intent();
			intent.setClass(m_Context, UserLoginActivity.class);
			startActivityForResult(intent,
					HeadhunterPublic.RESULT_ACTIVITY_CODE);

			return false;
		}

		return true;
	}
}

package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.MultiColumnListView.OnLoadMoreListener;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView.OnRefreshListener;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.PullDownListAdapter;
import com.qianniu.zhaopin.app.adapter.RewardInfoWaterFallAdapter;
import com.qianniu.zhaopin.app.adapter.RewardInfoWaterFallAdapter2;
import com.qianniu.zhaopin.app.bean.PullDownListInfo;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.bean.RewardFilterCondition;
import com.qianniu.zhaopin.app.bean.RewardListDataEntity;
import com.qianniu.zhaopin.app.bean.reqRewardList;
import com.qianniu.zhaopin.app.bean.reqRewardListBetwen;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.app.view.AdZoneView;
import com.qianniu.zhaopin.app.widget.PullToRefreshFooter;
import com.qianniu.zhaopin.thp.UmShare;

public class Fragment_rewardwaterfall extends BaseFragment
	implements OnClickListener
	{
	
	private Context m_Context;
	private AppContext m_appContext; //
	
	private View m_viewReward;
	
	private MultiColumnPullToRefreshListView m_lvReward; //
	private List<RewardData> m_listRewardInfo;
	private RewardInfoWaterFallAdapter2 m_rifAdapter;
	
	private ImageView m_imgBtnType;
	private ImageView m_imgBtnSearch;
	private ImageView m_imgBtnPublishreward;
	
	// 下拉菜单
	private PullDownListAdapter m_PullDownLA;
	private List<PullDownListInfo> m_PDLI;
	private ListView m_PDLV;
	
	// 标题栏
	private TextView m_tvSortName;
	
	// 广告栏
	private AdZoneView adzoneView;
	
	//
	private ProgressDialog m_progressDialog;
	/********************获取悬赏任务的状态*****************/
	private static final int LISTVIEW_STATUS_ONREFRESH = 0;
	private static final int LISTVIEW_STATUS_LOADMORE = 1;
	
	private int m_nListStatus;
	/********************获取悬赏任务请求参数*****************/
	private static int PAGE_COUNT = 30;			// 一页显示的悬赏任务Item;
	/********************获取悬赏任务请求参数*****************/
//	private static final int HANDLERTYPE_GETREWARD = 0;	// 获取悬赏任务数据
//	private static final int HANDLERTYPE_MOBILEAUTH = 1;  // 手机号验证
	/********************获取悬赏任务请求参数*****************/
	private String m_strTaskType;	// 悬赏类型
	private int m_nDirection; 		// 请求的数据类型 1=>请求新数据, 0=>请求旧数据
	private int m_nRequestType; 	// 请求的悬赏任务过滤类型
	private String m_strOffsetid; 	// 偏移ID
	// 过滤条件
	private RewardFilterCondition m_filters;
	
	private int m_nRequestDataType;			// 请求的数据类型(个人, 公司, 混合)
	private int m_nLastRequestDataType;		// 上次请求的数据类型(个人, 公司, 混合)
	private static final int REQUEST_DATA_TYPE_ALL = 0;
	private static final int REQUEST_DATA_TYPE_COMPANY = 1;
	private static final int REQUEST_DATA_TYPE_PERSONAL = 2;
	/*************************************/
	//
	private int m_nBottomposition;
	// 当前页面第一个的悬赏任务ID
	private String m_strFirstRewardID;
	// 当前页面最后一个的悬赏任务ID
	private String m_strLastRewardID;
	// 当前页面最尾一个的悬赏任务金额
	private String m_nLastBonus;
	
	// 是否搜索模式
	private boolean m_bIsSearch;
	// 选择的悬赏任务item
	//private int m_nChooseItem = 0;
	
	// 加载数据中
//	private boolean m_bDataLoading = false;
	
	// 是否点击类型切换按钮(个人/公司)
	private boolean m_bBtnTypeClick = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m_Context = this.getActivity();
		m_appContext = (AppContext) this.getActivity().getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (m_viewReward != null) {
			ViewGroup p = (ViewGroup) m_viewReward.getParent();
			if (p != null) {
				p.removeAllViewsInLayout();
			}

			if(adzoneView != null){
				adzoneView.refreshView();
			}

			// 猜你喜欢时不清空过滤条件
			if(HeadhunterPublic.REWARD_REQUESTTYPE_LIKE != m_nRequestType){
				m_filters = new RewardFilterCondition();	
			}
			
			m_nListStatus = LISTVIEW_STATUS_ONREFRESH;
			m_lvReward.setLoading();
			// 获取悬赏任务数据
			startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW, "", m_nRequestType);
			
			return m_viewReward;
		}
		
		m_viewReward = inflater.inflate(R.layout.fragment_reward_waterfall, container,
				false);
		
		// 设置
		m_imgBtnType = (ImageView) m_viewReward
				.findViewById(R.id.fragment_reward_btn_type);
		m_imgBtnType.setOnClickListener(this);
		
		// 搜索设置
		m_imgBtnSearch = (ImageView) m_viewReward
				.findViewById(R.id.fragment_reward_btn_search);
		m_imgBtnSearch.setOnClickListener(this);

		// 发布悬赏
		m_imgBtnPublishreward = (ImageView) m_viewReward
				.findViewById(R.id.fragment_reward_btn_publishreward);
		m_imgBtnPublishreward.setOnClickListener(this);
		
		// 悬赏任务数据表
		m_lvReward = (MultiColumnPullToRefreshListView) m_viewReward
				.findViewById(R.id.fragment_listview_reward);
		m_lvReward.setVisibility(View.VISIBLE);
		
		// 弹出下拉ListView
		m_PDLV = (ListView) m_viewReward
				.findViewById(R.id.fragment_reward_lv_pulldown);
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

				int nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_NEW;
				switch (nItem) {
				case 1: {
//					nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_LIKE;
//					m_filters = getSearchHistory();
	//
//					// 友盟统计--任务--排序--猜你喜欢
//					UmShare.UmStatistics(m_Context, "Reward_Requesttype_Like");
//				}
//					break;
//				case 2: {
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
					m_filters = new RewardFilterCondition();
					m_bIsSearch = false;

					// 友盟统计--任务--排序--悬赏排名
					UmShare.UmStatistics(m_Context, "Reward_Requesttype_Sort");
				}
					break;
				case 0:
				default: {
					nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_NEW;
					m_filters = new RewardFilterCondition();
					m_bIsSearch = false;

					// 友盟统计--任务--排序--最新发布
					UmShare.UmStatistics(m_Context, "Reward_Requesttype_New");
				}
					break;
				}

				m_nListStatus = LISTVIEW_STATUS_ONREFRESH;
				m_lvReward.setLoading();
				// 获取悬赏任务列表
				startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW, "",
						nRequestType);
			}
			
		});

		m_tvSortName = (TextView) m_viewReward
				.findViewById(R.id.fragment_reward_sortname);
		m_tvSortName.setOnClickListener(this);
		// 任务默认悬赏排序
		m_tvSortName.setText(getText(R.string.str_reward_sort_reward));
		// 初始化下拉菜单
		initPullDownListView();
		
		// 初始化全局数据
		m_nDirection = HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW;
		m_nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_SORT;
		m_strOffsetid = "";
		m_nLastBonus = "";
		m_filters = new RewardFilterCondition();
		m_bIsSearch = false;
		m_nRequestDataType = REQUEST_DATA_TYPE_COMPANY;	// 默认获取公司悬赏任务数据
		m_nLastRequestDataType = REQUEST_DATA_TYPE_COMPANY;
		
		// 初始化悬赏任务数据表
		initRewardListView(inflater);
		
		return m_viewReward;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int vid = v.getId();
		switch (vid) {
		case R.id.fragment_reward_sortname: {
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
		case R.id.fragment_reward_btn_search: {
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
		case R.id.fragment_reward_btn_publishreward: {
			// 友盟统计
			UmShare.UmStatistics(m_Context, "Reward_PublishrewardButton");

			// 隐藏下拉菜单
			hidePullDownList();

			// 判断是否登录
			if (checkISLogin()) {
				// 检查手机是否认证
//				checkMobileAuthStatus();
				showPublishRewardActivity();
			}
		}
			break;
		case R.id.fragment_reward_btn_type:
			{
				// 隐藏下拉菜单
				hidePullDownList();
				
				m_bBtnTypeClick = true;
				
				// 切换数据类型(个人/公司)
				switch(m_nRequestDataType){
				case REQUEST_DATA_TYPE_COMPANY:		// 点击之前是公司
					{
						m_nRequestDataType = REQUEST_DATA_TYPE_PERSONAL;
//						m_imgBtnType.setImageResource(
//								R.drawable.common_button_company);
						
						// 友盟统计--任务--个人任务按钮
						UmShare.UmStatistics(m_Context, "Reward_PersonalButton");
					}
					break;
				case REQUEST_DATA_TYPE_PERSONAL:	// 点击之前是个人
				default:
					{
						m_nRequestDataType = REQUEST_DATA_TYPE_COMPANY;
//						m_imgBtnType.setImageResource(
//								R.drawable.common_button_personal);
						
						// 友盟统计--任务--公司任务按钮
						UmShare.UmStatistics(m_Context, "Reward_CompanyButton");
					}
					break;
				}
				
				m_nListStatus = LISTVIEW_STATUS_ONREFRESH;
				m_lvReward.setLoading();
				
				// 获取悬赏任务列表
				startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW, "",
						m_nRequestType);
			}
			break;
		default:
			break;
		}	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode) {
			switch (resultCode) {
			case HeadhunterPublic.RESULT_REWARDINOF_OK: {
				// 获取悬赏任务详细界面传递过来的数据
				Bundle bundle = data.getExtras();
				if (null != bundle) {
					String strTaskId = bundle
							.getString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID);
					if(null == strTaskId){
						return;
					}
					
					if(strTaskId.isEmpty()){
						return;
					}

					// 获取收藏状态
					String strCollectionStatus = bundle
							.getString(HeadhunterPublic.REWARD_DATATRANSFER_COLLECTIONFLAG);
					// 获取应聘状态
					String strCandidateStatus = bundle
							.getString(HeadhunterPublic.REWARD_DATATRANSFER_CANDIDATEFLAG);
					// 获取是否已读状态
//					String strReadStatus = bundle
//							.getString(HeadhunterPublic.REWARD_DATATRANSFER_REWARDREAD);
					// 获取公司收藏状态
//					String strCompanyCollectionStatus = bundle
//							.getString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG);
					
					if(null == m_listRewardInfo){
						return;
					}

					int nChooseItem = m_rifAdapter.getChooseItem();
//					int nChooseItem = m_nChooseItem;
					if(nChooseItem < 0 || nChooseItem > m_listRewardInfo.size() - 1){
						return;
					}
					
					// 更新状态
					RewardData rld = m_listRewardInfo.get(nChooseItem);
					if (null != rld) {
						boolean bChange = false;
						// 更新应聘状态
						if(null != strCandidateStatus){
							if(!strCandidateStatus.isEmpty()){
								rld.setAction_1(strCandidateStatus);
								bChange = true;
							}
						}
						// 更新收藏状态
						if(null != strCollectionStatus){
							if(!strCollectionStatus.isEmpty()){
								rld.setAction_3(strCollectionStatus);
								bChange = true;
							}
						}
						
						// 更新是否已读状态
//						if(null != strReadStatus){
//							if(!strReadStatus.isEmpty()){
//								rld.setAction_5(strReadStatus);
//								// 保存已读的任务ID
//								saveReadRewardID(strTaskId);
//								if(null != m_rifAdapter){
//									m_rifAdapter.notifyDataSetChanged();
//								}
//							}
//						}
						
						// 更新公司收藏状态
//						if(null != strCompanyCollectionStatus &&
//								!strCompanyCollectionStatus.isEmpty()){
//							rld.setM27_status(strCompanyCollectionStatus);
//							bChange = true;
//						}
						
						if(bChange){
							// 把状态更新到缓存中
							upRewardData(rld);
						}
					}
				}
			}
				break;
			case HeadhunterPublic.RESULT_REWARDSEARCH_OK: {
				// 获取悬赏任务搜索设置界面传递过来的数据
				Bundle bundle = data.getExtras();
				if (null != bundle) {
					m_filters = (RewardFilterCondition) bundle
							.getSerializable(HeadhunterPublic.REWARDSEARCH_DATATRANSFER_FILTERS);

					m_bIsSearch = true;
					
					m_nListStatus = LISTVIEW_STATUS_ONREFRESH;
					m_lvReward.setLoading();
					
					// 获取悬赏任务列表
					startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW, "",
							m_nRequestType);
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
	
	/**
	 * 初始化下拉菜单
	 */
	private void initPullDownListView() {
		m_PDLI = new ArrayList<PullDownListInfo>();

		PullDownListInfo pdl = new PullDownListInfo();
		pdl.setText(getText(R.string.str_reward_sort_new).toString());
		pdl.setIsChoose(false);
		m_PDLI.add(pdl);

//		PullDownListInfo pdl2 = new PullDownListInfo();
//		pdl2.setText(getText(R.string.str_reward_sort_youlike).toString());
//		pdl2.setIsChoose(false);
//		m_PDLI.add(pdl2);

		// PullDownListInfo pdl3 = new PullDownListInfo();
		// pdl3.setText(getText(R.string.str_reward_sort_peripheral_reward).toString());
		// m_PDLI.add(pdl3);

		// 任务默认悬赏排序
		PullDownListInfo pdl4 = new PullDownListInfo();
		pdl4.setText(getText(R.string.str_reward_sort_reward).toString());
		pdl4.setIsChoose(true);
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
	 * @param inflater
	 */
	private void initRewardListView(LayoutInflater inflater) {
		ViewGroup headview = (ViewGroup) inflater.inflate(
				R.layout.banner_layout_waterfall, null);
		adzoneView = new AdZoneView(getActivity(),
				AdZoneView.ADZONE_ID_EXTRA,AdZoneView.SIZE_MATCHPARENT_SMALL,false,false);
		headview.addView(adzoneView);
		m_lvReward.addHeaderView(headview);
		
		// 初始化数据
		m_listRewardInfo = new ArrayList<RewardData>();
		// 
		m_rifAdapter = new RewardInfoWaterFallAdapter2(this, m_listRewardInfo);
//		m_rifAdapter = new RewardInfoWaterFallAdapter2(m_Context, m_listRewardInfo);
		// 
		m_lvReward.setAdapter(m_rifAdapter);
		// 监听点击事件
//		m_lvReward.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(PLA_AdapterView<?> parent, View view,
//					int nItem, long id) {
//				// TODO Auto-generated method stub
//				if (null != m_PDLV) {
//					m_PDLV.setVisibility(View.GONE);
//				}
//
//				// 点击头部无效
//				if (0 == nItem) {
//					return;
//				}
//
//				if (null == view){
//					return;
//				}
//
//				if (null == m_listRewardInfo) {
//					return;
//				}
//
//				int nChooseItem = nItem - m_lvReward.getHeaderViewsCount();
//				RewardData rld = m_listRewardInfo.get(nChooseItem);
//				if(null == rld){
//					return;
//				}
//				m_nChooseItem = nChooseItem;
//				
//				// 数据传输
//				Bundle bundle = new Bundle();
//				bundle.putSerializable(HeadhunterPublic.REWARD_DATATRANSFER_REWARDDATA, rld);
//				bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_ACTIYITYTYPE,
//						HeadhunterPublic.ACTIVITY_TYPE_REWARDLIST);
//
//				// 进入悬赏详细界面
//				Intent intent = new Intent();
//				intent.setClass(m_Context, RewardInfoActivity.class);
//				intent.putExtras(bundle);
//				startActivityForResult(intent,
//						HeadhunterPublic.RESULT_ACTIVITY_CODE);
//			}
//		});
		
		// 监听
		m_lvReward.setOnRefreshListener(new OnRefreshListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				m_nListStatus = LISTVIEW_STATUS_ONREFRESH;
				// 刷新广告栏
				if(adzoneView != null){
					adzoneView.refreshView();
				}
				// 获取悬赏任务数据
				startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW, "", m_nRequestType);
			}
			
		});
		// 监听加载更多
		m_lvReward.setOnLoadMoreListener(new  OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				// 限制数据量
				if(null != m_listRewardInfo &&
						m_listRewardInfo.size() > HeadhunterPublic.LOADDATA_MAX){
					if(null != m_lvReward){
						m_lvReward.onLoadMoreComplete(PullToRefreshFooter.STATE_LOADFULL);	
					}
					
					return;
				}
				
				m_nListStatus = LISTVIEW_STATUS_LOADMORE;
				// 加载更多数据
				startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_OLD,
						m_strLastRewardID, m_nRequestType);
			}
		});
		
		// 
		m_nListStatus = LISTVIEW_STATUS_ONREFRESH;
		m_lvReward.firstRefreshing();
		// 获取悬赏任务数据
		startGetData(m_nDirection, m_strOffsetid, m_nRequestType);
	}
	
	/**
	 * 获取悬赏任务数据
	 * 
	 * @param nDirection
	 *            请求的方式
	 * @param nOffsetid
	 *            偏移ID
	 * @param nRequestType
	 *            数据检索类型
	 */
	private void startGetData(int nDirection, String strOffsetid,
			int nRequestType) {
		m_nDirection = nDirection;
		m_strOffsetid = strOffsetid;
		m_nRequestType = nRequestType;

		// 判断网络是否连接
		if (!UIHelper.isNetworkConnected(m_appContext)) {
			m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(
					HeadhunterPublic.REWARDMSG_GETDATA_NONETWORKCONNECT));
			return;
		}
		
		new Thread() {
			public void run() {
				getRewardListData();
			}
		}.start();
	}

	private void getRewardListData() {
		try {
			RewardListDataEntity rlde = new RewardListDataEntity();
			// 悬赏排名时
			if (HeadhunterPublic.REWARD_REQUESTTYPE_SORT == m_nRequestType) {
				reqRewardListBetwen rrlb = new reqRewardListBetwen();
				
				// 设置请求参数
				rrlb.setDirection(m_nDirection);
				rrlb.setRequest_type(m_nRequestType);
				rrlb.setCount(PAGE_COUNT);
//				rrl.setCoordinates(m_strLongitude, m_strLatitude);		
				
				switch(m_nRequestDataType){
				case REQUEST_DATA_TYPE_COMPANY:		// 公司
					{
						String[] str = {"1"};
						rrlb.setTask_type(str);
					}
					break;
				case REQUEST_DATA_TYPE_PERSONAL:	// 个人
					{
						String[] str = {"2", "3"};
						rrlb.setTask_type(str);
					}
					break;
				}
				
				// 判断过滤条件是否为空
				if (null != m_filters) {
					rrlb.setFilters(m_filters);
				}
				
				// 更多
				if (HeadhunterPublic.REWARD_DIRECTIONTYPE_OLD == m_nDirection) {
					// 悬赏排名时，把最后一行的悬赏金额也传递给服务器
					rrlb.setOffsetid(m_nLastBonus);
					rrlb.setOffsetfield("task_bonus");
				}
				
				// 获取悬赏首页数据以前
				rlde = m_appContext.getRewardListDataEntityBetwen(rrlb);
			} else {
				reqRewardList rrl = new reqRewardList();
				
				// 设置请求参数
				rrl.setDirection(m_nDirection);
				rrl.setRequest_type(m_nRequestType);
				rrl.setCount(PAGE_COUNT);
//				rrl.setCoordinates(m_strLongitude, m_strLatitude);		
				
				switch(m_nRequestDataType){
				case REQUEST_DATA_TYPE_COMPANY:		// 公司
					{
						String[] str = {"1"};
						rrl.setTask_type(str);
					}
					break;
				case REQUEST_DATA_TYPE_PERSONAL:	// 个人
					{
						String[] str = {"2", "3"};
						rrl.setTask_type(str);
					}
					break;
				}
				
				// 判断过滤条件是否为空
				if (null != m_filters) {
					rrl.setFilters(m_filters);
				}
				
				// 更多
				if (HeadhunterPublic.REWARD_DIRECTIONTYPE_OLD == m_nDirection) {
					rrl.setOffsetfield("task_id");
					rrl.setOffsetid(m_strOffsetid);
				}
				
				// 获取悬赏首页数据以前
				rlde = m_appContext.getRewardListDataEntity(rrl);
			}

			Result res = rlde.getValidate();
			if (res.OK()) {
				// 成功
				m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(
						HeadhunterPublic.REWARDMSG_GETDATA_SUCCESS, rlde.getData()));
			} else {
				if (HeadhunterPublic.LINK_RESULT_DATA_TASKTABLEISNULL == res
						.getErrorCode()) {
					if (HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_NEW == m_nDirection) {
						// 没有数据
						m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(
								HeadhunterPublic.REWARDMSG_GETDATA_ISNULL));
					} else {
						// 没有更多的数据
						m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(
								HeadhunterPublic.REWARDMSG_GETDATA_NOMORE));
					}
				} else {
					// 失败
					m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(
							HeadhunterPublic.REWARDMSG_GETDATA_FAIL, res.getErrorMessage()));
				}
			}
		} catch (AppException e) {
			e.printStackTrace();
			// 异常
			m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(
					HeadhunterPublic.REWARDMSG_GETDATA_ABNORMAL, e));
		}
	}
	
	/**
	 * 消息处理
	 */
	private Handler m_RewardHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HeadhunterPublic.REWARDMSG_GETDATA_ABNORMAL: 
				{
					((AppException) msg.obj).makeToast(m_Context);
					
					// 变更状态
					changeListViewStatus(null);
					
					m_bBtnTypeClick = false;
				}
				break;
			case HeadhunterPublic.REWARDMSG_GETDATA_FAIL: 
				{
					UIHelper.ToastMessage(m_Context,
							getString(R.string.msg_reward_getdata_fail));
					
					// 变更状态
					changeListViewStatus(null);
					
					m_bBtnTypeClick = false;
				}
				break;
			case HeadhunterPublic.REWARDMSG_GETDATA_SUCCESS:
				{
					List<RewardData> lsRD = (List<RewardData>) msg.obj;
					// 融错处理
					if(null == lsRD){
						m_bBtnTypeClick = false;
						break;
					}else{
						if(lsRD.size() <= 0){
							m_bBtnTypeClick = false;
							break;
						}
					}
					
					if(m_nLastRequestDataType != m_nRequestDataType){
//						m_lvReward.MoveTop();
						m_nLastRequestDataType = m_nRequestDataType;
					}
					
					// 更新悬赏任务数据列表
					upRewardListData(lsRD, false);
					if (HeadhunterPublic.REWARD_DIRECTIONTYPE_OLD == m_nDirection) {
						m_lvReward.setSelection(m_nBottomposition);
					} else {
						m_lvReward.setSelection(0);
					}
					
					m_rifAdapter.notifyDataSetChanged();
					// 变更状态
					changeListViewStatus(lsRD);
					
					// 变更类型切换按钮背景图
					changeBtnTypeBG();
				}
				break;
			case HeadhunterPublic.REWARDMSG_GETDATA_ISNULL: 
				{// 没有数据
					if(m_bIsSearch){
						UIHelper.ToastMessage(m_Context,
								getString(R.string.msg_reward_getdata_isnullforsearch));
					}else{
						UIHelper.ToastMessage(m_Context,
								getString(R.string.msg_reward_getdata_isnull));
					}
	
					// 清空数据列表
					m_listRewardInfo.clear();
					m_rifAdapter.notifyDataSetChanged();
					
					// 变更状态
					changeListViewStatus(null);
					
					if(m_nLastRequestDataType != m_nRequestDataType){
						m_nLastRequestDataType = m_nRequestDataType;
					}
					
					if(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW == m_nDirection){
						// 变更类型切换按钮背景图
						changeBtnTypeBG();
					}
				}
				break;
			case HeadhunterPublic.REWARDMSG_GETDATA_NOMORE: 
				{// 没有更多的数据
					UIHelper.ToastMessage(m_Context,
							getString(R.string.msg_reward_getdata_nomore));
	
					// 变更状态
					changeListViewStatus(null);
				}
				break;
			case HeadhunterPublic.REWARDMSG_GETDATA_NONETWORKCONNECT:
				{
					if(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW == m_nDirection){
						List<RewardData> lsRD = getRewardData(m_nRequestType);
						// 融错处理
						if(null == lsRD || (lsRD.size() <= 0)){
							if(null != m_listRewardInfo){
								m_listRewardInfo.clear();
								m_rifAdapter.notifyDataSetChanged();
							}
							m_nBottomposition = 0;
							
							// 变更状态
							changeListViewStatus(lsRD);
							
							// 变更类型切换按钮背景图
							changeBtnTypeBG();
							
							break;
						}

						// 更新悬赏任务数据列表
						upRewardListData(lsRD, true);
						m_nBottomposition = 0;
						m_rifAdapter.notifyDataSetChanged();
						
						// 变更状态
						changeListViewStatus(lsRD);
						
						// 变更类型切换按钮背景图
						changeBtnTypeBG();
						
						if(m_nLastRequestDataType != m_nRequestDataType){
							m_nLastRequestDataType = m_nRequestDataType;
						}
					}else{
						m_lvReward.onLoadMoreComplete(PullToRefreshFooter.STATE_LOADFULL);
						m_bBtnTypeClick = false;
					}
				}
				break;
			case HeadhunterPublic.REWARDMSG_MOBILEAUTHESUCCESS:
				{
					dismissProgressDialog();
					Object obj = msg.obj;
					if (obj != null) {
						if (obj instanceof ReqUserInfo) {
							ReqUserInfo userInfo = (ReqUserInfo)obj;
							if (ReqUserInfo.PHONE_VERIFIED.equals(userInfo.getPhone_verified())) { //如果是认证过的 就存下来
								showPublishRewardActivity();
								DMSharedPreferencesUtil.putSharePre(m_appContext,
										DMSharedPreferencesUtil.DM_AUTH_INFO,
										m_appContext.getUserId(),
										ObjectUtils.getJsonStringFromObject(userInfo));
							} else {
								showMobileAuthDialog();
							}
						}
					}				
				}
				return;
			case HeadhunterPublic.REWARDMSG_MOBILEAUTHERROR:
				{
					dismissProgressDialog();
					Object obj = msg.obj;
					if (obj != null) {
						if (obj instanceof Result) {
							Result result = (Result) obj;
							result.handleErrcode(getActivity());
						} else if (obj instanceof AppException) {
							AppException exception = (AppException) obj;
							exception.makeToast(m_appContext);
						}
					}
				}
				return;
			default:
				break;
			}
		}
	};
	
	/**
	 * @param lsRLD
	 */
	private void upRewardListData(List<RewardData> lsRD, boolean bIsDBdata){
		boolean bFirst = false;

		if (null == m_listRewardInfo) {
			m_listRewardInfo = new ArrayList<RewardData>();
			m_nBottomposition = 0;
			
			if(!bIsDBdata){
				// 删除原来的缓存数据
				deleteAllRewardData(m_nRequestType);
			}
		} else {
			if (HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW == m_nDirection) {
				m_listRewardInfo.clear();
				m_nBottomposition = 0;
				
				if(!bIsDBdata){
					// 删除原来的缓存数据
					deleteAllRewardData(m_nRequestType);
				}
			} else {
				m_nBottomposition = m_listRewardInfo.size() - 1;
			}
		}

		for (RewardData rld : lsRD) {
			if (!bFirst) {
				// 获取第一个的悬赏任务ID
				m_strFirstRewardID = rld.getTask_Id();
				bFirst = true;
			}
			// 获取最后一行的悬赏任务ID
			m_strLastRewardID = rld.getTask_Id();
			// 获取最尾一行的悬赏金额
			m_nLastBonus = rld.getTask_Bonus();

			// 设置用户请求的数据类型
			rld.setUserrequest_type(String.valueOf(m_nRequestType));
			
			// 判断是否已读
//			if(checkRewardIsRead(rld.getTask_Id())){
//				rld.setAction_5(HeadhunterPublic.REWARD_READ_FLAG);
//			}
			
			// 设置请求的数据类型(个人:2, 公司:1, 混合:0)
			rld.setRequest_datatype(m_nRequestDataType);
			
			if(!bIsDBdata){
				if(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW == m_nDirection){
					// 保存任务数据到数据库中
					saveRewardData(rld);
				}
			}
			
			// 获取悬赏任务所有信息，并且添加到列表中
			m_listRewardInfo.add(rld);
		}
	}
	
	/**
	 * 获取数据库中的数据
	 * @param nRequestType
	 */
	private List<RewardData> getRewardData(int nRequestType){
		return RewardData.getRewardData(m_appContext, m_nRequestDataType, nRequestType, "", "", false);
	}
	
	/**
	 * 保存任务数据到数据库中
	 * @param rld
	 */
	private void saveRewardData(RewardData rd){
		RewardData.saveRewardData(m_appContext, rd);
	}
	
	/**
	 * 删除数据库中同一用户，同一请求类型的任务数据
	 * @param nRequestType
	 */
	private void deleteAllRewardData(int nRequestType){
		RewardData.deleteAllRewardData(m_appContext, m_nRequestDataType, nRequestType);
	}
	
	/**
	 * 更新数据库中的任务数据状态
	 * @param rld
	 */
	private void upRewardData(RewardData rld){
		RewardData.upRewardData(m_appContext, rld);
	}
	
	/**
	 * 
	 */
	private void showProgressDialog() {
		if (m_progressDialog == null) {
			m_progressDialog = new ProgressDialog(m_Context);
		}
		m_progressDialog.setMessage("请稍候...");
		m_progressDialog.show();
	}
	
	/**
	 * 
	 */
	private void dismissProgressDialog() {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}
	}
	
	/**
	 * 
	 */
	private void showMobileAuthDialog() {
		UIHelper.showCommonDialog(m_Context, 
				R.string.my_reward_list_auth_dialog_content,
				R.string.my_reward_list_auth_dialog_ok,
				R.string.my_reward_list_auth_dialog_cancel,
				new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					// 进入手机认证界面
					showMobileAuthActivity();
				}
				dialog.dismiss();
			}
		});
	}
	
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
	
	/**
	 * @param lsRD
	 */
	private void changeListViewStatus(List<RewardData> lsRD){
		switch(m_nListStatus){
		case LISTVIEW_STATUS_LOADMORE:
			{
				if(null == lsRD){
					m_lvReward.onLoadMoreComplete(PullToRefreshFooter.STATE_LOADFULL);
				}else{
					if(lsRD.size() < PAGE_COUNT){
						m_lvReward.onLoadMoreComplete(PullToRefreshFooter.STATE_LOADFULL);			
					}else{
						m_lvReward.onLoadMoreComplete(PullToRefreshFooter.STATE_NORMAL);
					}					
				}	
			}
			break;
		case LISTVIEW_STATUS_ONREFRESH:
			{
				if(null == lsRD){
					m_lvReward.onRefreshComplete(PullToRefreshFooter.STATE_LOADFULL);
				}else{
					if(lsRD.size() < PAGE_COUNT){
						m_lvReward.onRefreshComplete(PullToRefreshFooter.STATE_LOADFULL);			
					}else{
						m_lvReward.onRefreshComplete(PullToRefreshFooter.STATE_NORMAL);
					}
				}
			}
			break;
		}
	}
	
	/**
	 * 变更类型切换按钮背景图
	 */
	private void changeBtnTypeBG(){
		if(m_bBtnTypeClick){
			switch(m_nRequestDataType){
			case REQUEST_DATA_TYPE_COMPANY:		// 公司
			default:
				{
					m_imgBtnType.setImageResource(
							R.drawable.common_button_personal);
				}
				break;
			case REQUEST_DATA_TYPE_PERSONAL:	// 个人
				{
					m_imgBtnType.setImageResource(
							R.drawable.common_button_company);
				}
				break;
			}
			
			m_bBtnTypeClick = false;
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
}

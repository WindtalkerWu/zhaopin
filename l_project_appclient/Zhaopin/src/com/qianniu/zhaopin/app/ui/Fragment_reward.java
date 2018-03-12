package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.PullDownListAdapter;
import com.qianniu.zhaopin.app.adapter.RewardInfoListAdapter;
import com.qianniu.zhaopin.app.adapter.RewardInfoListAdapter.RewardListInfoViewHolder;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.PullDownListInfo;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.bean.RewardFilterCondition;
import com.qianniu.zhaopin.app.bean.RewardListDataEntity;
import com.qianniu.zhaopin.app.bean.reqRewardList;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.app.view.AdZoneView;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView.OnRefreshListener;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Fragment_reward extends BaseFragment implements OnClickListener,
		OnItemClickListener, OnScrollListener, OnRefreshListener {

	private static final String TAG = "Fragment_reward";
	private Context m_Context;
	private AppContext m_appContext; //

	private View m_viewReward;
//	private View m_viewLoad;
	FragmentTabHost m_TabHost;

	private ImageView m_imgBtnSet;
	private ImageView m_imgBtnSearch;
	private ImageView m_imgBtnPublishreward;

	private PullToRefreshListView m_lvReward; //
	private RewardInfoListAdapter m_RewardLA;
	private List<RewardData> m_listRewardInfo;
	private int m_nChooseItem;

	private View lvInfoType_footer;
	private TextView lvInfoType_foot_more;
	private ProgressBar lvInfoType_foot_progress;
	
	private ProgressDialog m_progressDialog;

	private TextView m_tvSortName;

	private PullDownListAdapter m_PullDownLA;
	private List<PullDownListInfo> m_PDLI;
	private ListView m_PDLV;
	private AdZoneView adzoneView;
	
	/********************获取悬赏任务请求参数*****************/
//	private String m_strTaskType;	// 悬赏类型
	private int m_nDirection; 		// 请求的数据类型 1=>请求新数据, 0=>请求旧数据
	private int m_nRequestType; 	// 请求的悬赏任务过滤类型
	private String m_strOffsetid; 	// 偏移ID
	// 过滤条件
	private RewardFilterCondition m_filters;
	
	private static final int VIEWTYPE_LIST = 0;			// 普通列表显示
	private static final int VIEWTYPE_WATERFALL = 1;	// 瀑布流显示
	/**************************************************/
	// 数据显示的界面类型
	private int m_nViewType;

	// 当前页面最上一行的悬赏任务ID
	private String m_strTopRewardID;
	// 当前页面最尾一行的悬赏任务ID
	private String m_strBottomRewardID;
	//
	private int m_nBottomposition;
	// 当前页面最尾一行的悬赏任务金额
	private String m_nBottomBonus;

	// 纬度
	private String m_strLatitude;
	// 经度
	private String m_strLongitude;

	private int m_nGetDataType;
	
	private boolean m_bIsSearch;			// 是否搜索模式
	private boolean m_isFirstRefreashing = false;
	
	public static String fromMainReward = "fromMainReward";

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
			
			//m_lvReward.firstRefreashing();
			m_isFirstRefreashing = true;
			m_nGetDataType = UIHelper.LISTVIEW_ACTION_INIT;
			
			// 获取悬赏任务数据
			startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW, "", m_nRequestType);
			
			return m_viewReward;
		}
		
		// 初始化全局数据
		m_nViewType = VIEWTYPE_LIST;
		m_nDirection = HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW;
		m_nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_SORT;
		m_strOffsetid = "";
		m_nBottomBonus = "";
		m_filters = new RewardFilterCondition();
		m_bIsSearch = false;
		
		m_viewReward = inflater.inflate(R.layout.fragment_reward, container,
				false);
//		m_viewLoad = (View) m_viewReward
//				.findViewById(R.id.commom_loading_layout);

		// 设置
		m_imgBtnSet = (ImageView) m_viewReward
				.findViewById(R.id.fragment_reward_btn_set);
		m_imgBtnSet.setOnClickListener(this);
		
		// 搜索设置
		m_imgBtnSearch = (ImageView) m_viewReward
				.findViewById(R.id.fragment_reward_btn_search);
		m_imgBtnSearch.setOnClickListener(this);

		// 发布悬赏
		m_imgBtnPublishreward = (ImageView) m_viewReward
				.findViewById(R.id.fragment_reward_btn_publishreward);
		m_imgBtnPublishreward.setOnClickListener(this);

		m_lvReward = (PullToRefreshListView) m_viewReward
				.findViewById(R.id.fragment_listview_reward);
		m_lvReward.setOnItemClickListener(this);
		m_lvReward.setOnScrollListener(this);
		m_lvReward.setOnRefreshListener(this);

		// 弹出下拉ListView
		m_PDLV = (ListView) m_viewReward
				.findViewById(R.id.fragment_reward_lv_pulldown);
		m_PDLV.setOnItemClickListener(this);

		m_tvSortName = (TextView) m_viewReward
				.findViewById(R.id.fragment_reward_sortname);
		m_tvSortName.setOnClickListener(this);
		// 任务默认悬赏排序
		m_tvSortName.setText(getText(R.string.str_reward_sort_reward));

		initPullDownListView();
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

			if (null != m_PDLV) {
				m_PDLV.setVisibility(View.GONE);
			}

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

			if (null != m_PDLV) {
				m_PDLV.setVisibility(View.GONE);
			}

			// 判断是否登录
			if (checkISLogin()) {
				// 检查手机是否认证
//				checkMobileAuthStatus();
				showPublishRewardActivity();
			}
		}
			break;
		case R.id.fragment_reward_btn_set:
			{
				// 设置界面
				UIHelper.showSetActivity(m_Context);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int nItem, long arg3) {
		// TODO Auto-generated method stub

		if ((ListView) arg0 == m_PDLV) {
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
//				nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_LIKE;
//				m_filters = getSearchHistory();
//
//				// 友盟统计--任务--排序--猜你喜欢
//				UmShare.UmStatistics(m_Context, "Reward_Requesttype_Like");
//			}
//				break;
//			case 2: {
//				nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_PERIPHERY;
//				m_filters = new RewardFilterCondition();
//				m_bIsSearch = false;
//
//				// 友盟统计--任务--排序--周边职位
//				UmShare.UmStatistics(m_Context, "Reward_Requesttype_Reriphery");
//			}
//				break;
//			case 3: {
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

//			m_lvReward.firstRefreashing();
			m_isFirstRefreashing = true;
			m_nGetDataType = UIHelper.LISTVIEW_ACTION_INIT;
			// 获取悬赏任务列表
			startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW, "",
					nRequestType);

		} else if ((PullToRefreshListView) arg0 == m_lvReward) {

			if (null != m_PDLV) {
				m_PDLV.setVisibility(View.GONE);
			}

			// 点击头部无效
			if (nItem <= m_lvReward.getHeaderViewsCount() - 1) {
				return;
			}

			if (view == null)
				return;

			RewardListInfoViewHolder rivh = (RewardListInfoViewHolder) view
					.getTag();
			if (null == rivh) {
				return;
			}

			if (null == m_listRewardInfo) {
				return;
			}

			// 获取选中的item
			int nChooseItem = nItem - m_lvReward.getHeaderViewsCount();
			if(nChooseItem < 0){
				return;
			}
			
			// 获取item对应数据
			RewardData rld = m_listRewardInfo.get(nChooseItem);
			if(null == rld){
				return;
			}
			m_nChooseItem = nChooseItem;

			// 数据传输
			Bundle bundle = new Bundle();
			bundle.putSerializable(HeadhunterPublic.REWARD_DATATRANSFER_REWARDDATA, rld);
			bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_ACTIYITYTYPE,
					HeadhunterPublic.ACTIVITY_TYPE_REWARDLIST);

			// 进入悬赏详细界面
			Intent intent = new Intent();
			intent.setClass(m_Context, RewardInfoActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent,
					HeadhunterPublic.RESULT_ACTIVITY_CODE);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		m_lvReward.onScroll(view, firstVisibleItem, visibleItemCount,
				totalItemCount);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		m_lvReward.onScrollStateChanged(view, scrollState);

		// 数据为空--不用继续下面代码了
		if (m_listRewardInfo.isEmpty())
			return;
		
		// 判断是否滚动到底部
		boolean scrollEnd = false;
		try {
			if (view.getPositionForView(lvInfoType_footer) == view
					.getLastVisiblePosition())
				scrollEnd = true;
		} catch (Exception e) {
			scrollEnd = false;
		}

		int lvDataState = StringUtils.toInt(m_lvReward.getTag());
		if(scrollEnd && UIHelper.LISTVIEW_DATA_MORE == lvDataState){
			// 限制数据量
			if(null != m_listRewardInfo &&
					m_listRewardInfo.size() > HeadhunterPublic.LOADDATA_MAX){
				m_lvReward.setTag(UIHelper.LISTVIEW_DATA_FULL);
				lvInfoType_foot_more.setText(R.string.load_full);
				return;
			}
			
			startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_OLD,
					m_strBottomRewardID, m_nRequestType);	
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		m_nGetDataType = UIHelper.LISTVIEW_ACTION_REFRESH;
		m_isFirstRefreashing = true;
		// 刷新广告栏
		if(adzoneView != null){
			adzoneView.refreshView();
		}
		
		// 获取悬赏任务数据
		//startGetData(m_nDirection, m_strOffsetid, m_nRequestType);
		startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW, "", m_nRequestType);
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
					String strReadStatus = bundle
							.getString(HeadhunterPublic.REWARD_DATATRANSFER_REWARDREAD);
					// 获取公司收藏状态
//					String strCompanyCollectionStatus = bundle
//							.getString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG);
					
					if(null == m_listRewardInfo){
						return;
					}
					
					// 更新状态
					RewardData rld = m_listRewardInfo.get(m_nChooseItem);
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
						if(null != strReadStatus){
							if(!strReadStatus.isEmpty()){
								rld.setAction_5(strReadStatus);
								// 保存已读的任务ID
								saveReadRewardID(strTaskId);
								if(null != m_RewardLA){
									m_RewardLA.notifyDataSetChanged();
								}
							}
						}
						
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
					
//					m_lvReward.firstRefreashing();
					m_isFirstRefreashing = true;
					m_nGetDataType = UIHelper.LISTVIEW_ACTION_INIT;
					
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
	 * 
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
	 * @param inflater
	 */
	private void initRewardListView(LayoutInflater inflater) {

		lvInfoType_footer = inflater.inflate(R.layout.listview_footer, null);
		lvInfoType_footer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 限制数据量
				if(null != m_listRewardInfo &&
						m_listRewardInfo.size() > HeadhunterPublic.LOADDATA_MAX){
					m_lvReward.setTag(UIHelper.LISTVIEW_DATA_FULL);
					lvInfoType_foot_more.setText(R.string.load_full);
					return;
				}
				
				int lvDataState = StringUtils.toInt(m_lvReward.getTag());
				if(UIHelper.LISTVIEW_DATA_MORE == lvDataState){
					startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_OLD,
							m_strBottomRewardID, m_nRequestType);	
				}
			}
		});

		lvInfoType_foot_progress = (ProgressBar) lvInfoType_footer
				.findViewById(R.id.listview_foot_progress);
		lvInfoType_foot_progress.setVisibility(View.GONE);
		lvInfoType_foot_more = (TextView) lvInfoType_footer
				.findViewById(R.id.listview_foot_more);

		m_listRewardInfo = new ArrayList<RewardData>();
		m_RewardLA = new RewardInfoListAdapter(m_appContext,
				HeadhunterPublic.ACTIVITY_TYPE_REWARDLIST, m_listRewardInfo);

/*		ViewGroup headview = (ViewGroup) inflater.inflate(
				R.layout.banner_layout, null);*/
		LinearLayout headview = new LinearLayout(getActivity());
		LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);	
		lp.setMargins(0, 0, 0, 0);
		headview.setLayoutParams(lp);
		headview.setOrientation(LinearLayout.VERTICAL);
		
		adzoneView = new AdZoneView(getActivity(),
				AdZoneView.ADZONE_ID_EXTRA,AdZoneView.SIZE_MATCHPARENT_SMALL,false,false);
		headview.addView(adzoneView);
		m_lvReward.addHeaderView(headview);
		m_lvReward.addFooterView(lvInfoType_footer);
		m_lvReward.setAdapter(m_RewardLA);

//		m_lvReward.firstRefreashing();
		m_isFirstRefreashing = true;
		// 
		m_nGetDataType = UIHelper.LISTVIEW_ACTION_INIT;
		// 获取悬赏任务数据
		startGetData(m_nDirection, m_strOffsetid, m_nRequestType);
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
	 * 获取经纬度
	 */
	private boolean getLocation() {
		LocationManager loctionManager;
		String contextService = Context.LOCATION_SERVICE;
		// 通过系统服务，取得LocationManager对象
		loctionManager = (LocationManager) this.getActivity().getSystemService(
				contextService);

		Criteria criteria = new Criteria();
		// 高精度
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 不要求海拔
		criteria.setAltitudeRequired(false);
		// 不要求方位
		criteria.setBearingRequired(false);
		// 允许产生开销
		criteria.setCostAllowed(true);
		// 低功耗
		// criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);// 消耗大的话，获取的频率高
		// 手机位置移动
		criteria.setSpeedRequired(true);

		// 从可用的位置提供器中，匹配以上标准的最佳提供器
		String provider = loctionManager.getBestProvider(criteria, true);

		MyLogger.i(TAG, "provider = " + provider);

		// 获得最后一次变化的位置
		Location location = loctionManager.getLastKnownLocation(provider);

		if (null != location) {
			// 获得纬度加班
			m_strLatitude = Double.toString(location.getLatitude());
			// 获得经度
			m_strLongitude = Double.toString(location.getLongitude());
		} else {
			return false;
		}

		MyLogger.i(TAG, "经度 = " + m_strLongitude + ",纬度 = " + m_strLatitude);
		return true;
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

		// 获取经纬度
//		if (HeadhunterPublic.REWARD_REQUESTTYPE_PERIPHERY == m_nRequestType) {
//			if (!getLocation()) {
//				return;
//			}
//		}

		// 判断网络是否连接
		if (!UIHelper.isNetworkConnected(m_appContext)) {
			m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(
					HeadhunterPublic.REWARDMSG_GETDATA_NONETWORKCONNECT));
			return;
		}

		m_lvReward.setTag(UIHelper.LISTVIEW_DATA_LOADING);
		if(!m_isFirstRefreashing){
			lvInfoType_foot_more.setText(R.string.load_ing);
			lvInfoType_foot_progress.setVisibility(View.VISIBLE);
		}else{
			m_lvReward.firstRefreshing();
			m_isFirstRefreashing = false;
		}
		m_lvReward.setLoadingState(true);

		new Thread() {
			public void run() {
				getRewardListData();
			}
		}.start();
	}

	private void getRewardListData() {
		try {
			// 设置请求参数
			reqRewardList rrl = new reqRewardList();
			rrl.setDirection(m_nDirection);
			rrl.setRequest_type(m_nRequestType);
			rrl.setCoordinates(m_strLongitude, m_strLatitude);
			
			// 判断显示界面是否是瀑布流
			if(VIEWTYPE_WATERFALL == m_nViewType){
//				rrl.setTask_type(m_strTaskType);
			}
			
			// 判断过滤条件是否为空
			if (null != m_filters) {
				rrl.setFilters(m_filters);
			}

			// 更多
			if (HeadhunterPublic.REWARD_DIRECTIONTYPE_OLD == m_nDirection) {
				// 悬赏排名时，把最后一行的悬赏金额也传递给服务器
				if (HeadhunterPublic.REWARD_REQUESTTYPE_SORT == m_nRequestType) {
					rrl.setOffsetid(m_nBottomBonus);
					rrl.setOffsetfield("task_bonus");
				} else {
					rrl.setOffsetfield("task_id");
					rrl.setOffsetid(m_strOffsetid);
				}
			}

			// 获取悬赏首页数据
			RewardListDataEntity rlde = m_appContext
					.getRewardListDataEntity(rrl);

			Result res = rlde.getValidate();
			// 
			m_lvReward.setLoadingState(false);
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
			m_lvReward.setLoadingState(false);
			// 异常
			m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(
					HeadhunterPublic.REWARDMSG_GETDATA_ABNORMAL, e));
		}
	}

	/**
	 * 更新悬赏任务列表数据
	 * 
	 * @param rld
	 */
	private void upRewardListData(List<RewardData> lsRLD, boolean bIsDBdata) {
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

		for (RewardData rld : lsRLD) {
			if (!bFirst) {
				// 获取第一行的悬赏任务ID
				m_strTopRewardID = rld.getTask_Id();
				bFirst = true;
			}
			// 获取最尾一行的悬赏任务ID
			m_strBottomRewardID = rld.getTask_Id();
			// 获取最尾一行的悬赏金额
			m_nBottomBonus = rld.getTask_Bonus();

			// 设置用户请求的数据类型
			rld.setUserrequest_type(String.valueOf(m_nRequestType));
			
			// 判断是否已读
			if(checkRewardIsRead(rld.getTask_Id())){
				rld.setAction_5(HeadhunterPublic.REWARD_READ_FLAG);
			}
			
			// 设置请求的数据类型(个人:2, 公司:1, 混合:0)
			rld.setRequest_datatype(0);

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
	 * 消息处理
	 */
	private Handler m_RewardHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HeadhunterPublic.REWARDMSG_GETDATA_ABNORMAL: 
				{
					m_lvReward.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvInfoType_foot_more.setText(R.string.load_more);
	
					((AppException) msg.obj).makeToast(m_Context);
				}
				break;
			case HeadhunterPublic.REWARDMSG_GETDATA_FAIL: 
				{
					m_lvReward.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvInfoType_foot_more.setText(R.string.load_more);
	
					UIHelper.ToastMessage(m_Context,
							getString(R.string.msg_reward_getdata_fail));
				}
				break;
			case HeadhunterPublic.REWARDMSG_GETDATA_SUCCESS:
				{
					List<RewardData> lsRLD = (List<RewardData>) msg.obj;
					// 融错处理
					if(null == lsRLD){
						break;
					}else{
						if(lsRLD.size() <= 0){
							break;
						}
					}
					
					// 更新悬赏任务数据列表
					upRewardListData(lsRLD, false);
					if (HeadhunterPublic.REWARD_DIRECTIONTYPE_OLD == m_nDirection) {
						m_lvReward.setSelection(m_nBottomposition);
					} else {
						m_lvReward.setSelection(0);
					}
					m_RewardLA.notifyDataSetChanged();
	
					if(m_listRewardInfo.size() < 10){
						m_lvReward.setTag(UIHelper.LISTVIEW_DATA_FULL);
						lvInfoType_foot_more.setText(R.string.load_full);				
					}else{
						m_lvReward.setTag(UIHelper.LISTVIEW_DATA_MORE);
						lvInfoType_foot_more.setText(R.string.load_more);
					}
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
					m_RewardLA.notifyDataSetChanged();
				}
				break;
			case HeadhunterPublic.REWARDMSG_GETDATA_NOMORE: 
				{// 没有更多的数据
					UIHelper.ToastMessage(m_Context,
							getString(R.string.msg_reward_getdata_nomore));
	
					m_lvReward.setTag(UIHelper.LISTVIEW_DATA_FULL);
					lvInfoType_foot_more.setText(R.string.load_full);
				}
				break;
			case HeadhunterPublic.REWARDMSG_GETDATA_NONETWORKCONNECT:
				{
					if(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW == m_nDirection){
						List<RewardData> lsRLD = getRewardData(m_nRequestType);
						// 融错处理
						if(null == lsRLD){
							if(null != m_listRewardInfo){
								m_listRewardInfo.clear();
								m_RewardLA.notifyDataSetChanged();
							}
							m_nBottomposition = 0;
							
							break;
						}else{
							if(lsRLD.size() <= 0){
								if(null != m_listRewardInfo){
									m_listRewardInfo.clear();
									m_RewardLA.notifyDataSetChanged();
								}
								m_nBottomposition = 0;
								
								break;
							}
						}
						
						// 更新悬赏任务数据列表
						upRewardListData(lsRLD, true);
						m_nBottomposition = 0;
						
						m_RewardLA.notifyDataSetChanged();
						
						m_lvReward.setTag(UIHelper.LISTVIEW_DATA_FULL);
						lvInfoType_foot_more.setText(R.string.load_full);						
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

			lvInfoType_foot_progress.setVisibility(ProgressBar.GONE);
			if (0 == m_RewardLA.getCount()) 
			{
				m_lvReward.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
				lvInfoType_foot_more.setText(R.string.load_empty);
			}

			switch (m_nGetDataType) {
			case UIHelper.LISTVIEW_ACTION_REFRESH: 
			case UIHelper.LISTVIEW_ACTION_INIT:
				{
					if(HeadhunterPublic.REWARD_DIRECTIONTYPE_OLD != m_nDirection){
						m_lvReward.onRefreshComplete();
						m_lvReward.setSelection(0);
					}
					m_nGetDataType = UIHelper.LISTVIEW_ACTION_INIT;
				}
				break;
			case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG: 
				{
					m_lvReward.onRefreshComplete();
					m_lvReward.setSelection(0);
					m_nGetDataType = UIHelper.LISTVIEW_ACTION_INIT;
				}
				break;
			default:
				break;
			}
		}
	};

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
	 * 获取最新一条搜索历史
	 */
	private RewardFilterCondition getSearchHistory() {

		RewardFilterCondition rfc = new RewardFilterCondition();

		try {
			rfc = m_appContext.getLastHistorySearch();
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rfc;
	}
	
	/**
	 * 进入悬赏发布界面
	 */
	private void showPublishRewardActivity(){
		// 进入发布悬赏界面
		Intent intent = new Intent();
		intent.setClass(m_Context, PublishRewardActivity.class);
		intent.putExtra(fromMainReward, true);
		getActivity().startActivityForResult(intent,
				HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 进入手机认证界面
	 */
	private void showMobileAuthActivity() {
		Intent intent = new Intent(m_Context, MobileAuthActivity.class);
		startActivityForResult(intent, 2);
	}
	
	/**
	 * 判断手机是否认证
	 */
	private void checkMobileAuthStatus() {
		String authInfo = DMSharedPreferencesUtil.getSharePreStr(m_Context,
				DMSharedPreferencesUtil.DM_AUTH_INFO,
				m_appContext.getUserId());

		if (!TextUtils.isEmpty(authInfo)) {
			ReqUserInfo userInfo = (ReqUserInfo) ObjectUtils.getObjectFromJsonString(authInfo, ReqUserInfo.class);
			if (ReqUserInfo.PHONE_VERIFIED.equals(userInfo.getPhone_verified())) {
				// 进入悬赏发布界面
				showPublishRewardActivity();
				return;
			}
		}

		showProgressDialog();
		new Thread() {
			
			public void run() {
				Result mobileAuthStatus = getMobileAuthStatus();
				if (mobileAuthStatus == null) {
					return;
				}

				if (mobileAuthStatus.OK()) {
					String resultStr = mobileAuthStatus.getJsonStr();
					ReqUserInfo userInfo = (ReqUserInfo) ObjectUtils
							.getObjectFromJsonString(resultStr,
									ReqUserInfo.class);
					m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(
							HeadhunterPublic.REWARDMSG_MOBILEAUTHESUCCESS,
							userInfo));
				} else {
					m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(HeadhunterPublic.REWARDMSG_MOBILEAUTHERROR,
							mobileAuthStatus));
				}
			}
		}.start();
	}
	
	// 获取手机认证状态
	private Result getMobileAuthStatus() {
		try {
			Result result = ApiClient
					.getUserInfo(m_appContext);
			return result;
		} catch (AppException e) {
			e.printStackTrace();
			m_RewardHandler.sendMessage(m_RewardHandler.obtainMessage(HeadhunterPublic.REWARDMSG_MOBILEAUTHERROR, e));
		}
		return null;
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
	 * 判断任务数据是否已读
	 * @param strRewardId
	 * @return
	 */
	private boolean checkRewardIsRead(String strRewardId){
		return RewardData.checkRewardIsRead(m_appContext, strRewardId);
	}
	
	/**
	 * 保存任务数据是否已读状态
	 * @param strRewardId
	 */
	private void saveReadRewardID(String strRewardId){
		RewardData.saveReadRewardID(m_appContext, strRewardId);
	}
	
	/**
	 * 获取数据库中的数据
	 * @param nRequestType
	 */
	private List<RewardData> getRewardData(int nRequestType){
		return RewardData.getRewardData(m_appContext, 0, nRequestType, "", "", false);
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
		RewardData.deleteAllRewardData(m_appContext, 0, nRequestType);
	}
	
	/**
	 * 更新数据库中的任务数据状态
	 * @param rld
	 */
	private void upRewardData(RewardData rld){
		RewardData.upRewardData(m_appContext, rld);
	}
}

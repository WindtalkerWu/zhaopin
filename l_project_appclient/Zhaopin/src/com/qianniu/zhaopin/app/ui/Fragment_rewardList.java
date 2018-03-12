package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.MultiColumnListView.OnLoadMoreListener;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView.OnRefreshListener;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.RewardInfoWaterFallAdapter2;
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
import com.qianniu.zhaopin.app.widget.PullToRefreshFooter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author wuzy
 *
 */
public class Fragment_rewardList extends BaseFragment{

	private Context m_Context;
	private AppContext m_appContext; //
	
	private View m_viewReward;
	
	//
	private ProgressDialog m_progressDialog;
	
	private MultiColumnPullToRefreshListView m_lvReward; //
	private List<RewardData> m_listRewardInfo;
	private RewardInfoWaterFallAdapter2 m_rifAdapter;
	
	/********************获取悬赏任务请求参数*****************/
	private String m_strTaskType;	// 悬赏类型
	private int m_nDirection; 		// 请求的数据类型 1=>请求新数据, 0=>请求旧数据
	private int m_nRequestType; 	// 请求的悬赏任务过滤类型
	private String m_strOffsetid; 	// 偏移ID
	// 过滤条件
	private RewardFilterCondition m_filters;
	private RewardFilterCondition m_filtersBak;
	
	private int m_nTabtype;					// 选项卡类型
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
	
	// 是否点击类型切换按钮(个人/公司)
	private boolean m_bBtnTypeClick = false;
	/********************获取悬赏任务的状态*****************/
	private static final int LISTVIEW_STATUS_ONREFRESH = 0;
	private static final int LISTVIEW_STATUS_LOADMORE = 1;
	
	private int m_nListStatus;
	/********************获取悬赏任务请求参数*****************/
	private static int PAGE_COUNT = 30;			// 一页显示的悬赏任务Item;
	/***************************************************/

	/**************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		m_Context = this.getActivity();
		m_appContext = (AppContext) this.getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(null != m_viewReward){
			ViewGroup p = (ViewGroup) m_viewReward.getParent();
			if (p != null) {
				p.removeAllViewsInLayout();
			}
			
			// 猜你喜欢时不清空过滤条件
//			if(HeadhunterPublic.REWARD_REQUESTTYPE_LIKE != m_nRequestType){
//				m_filters = new RewardFilterCondition();	
//			}
			
//			m_nListStatus = LISTVIEW_STATUS_ONREFRESH;
//			m_lvReward.setLoading();
//			// 获取悬赏任务数据
//			startGetData(HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW, "", m_nRequestType);
			
		}else{
			m_viewReward = inflater.inflate(R.layout.fragment_rewardlist, container,
					false);	
			
			// 悬赏任务数据表
			m_lvReward = (MultiColumnPullToRefreshListView) m_viewReward
					.findViewById(R.id.fragment_listview_reward);
			m_lvReward.setVisibility(View.VISIBLE);
			
			// 初始化全局数据
			initData();
			
			// 初始化悬赏任务数据表
			initRewardListView(inflater);
		}
		
		return m_viewReward;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode) {
			switch (resultCode) {
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
	
	/**
	 * @param inflater
	 */
	private void initRewardListView(LayoutInflater inflater) {
		// 初始化数据
		m_listRewardInfo = getRewardData(m_nRequestType);
		if(null == m_listRewardInfo){
			m_listRewardInfo = new ArrayList<RewardData>();
		}

		// 
		m_rifAdapter = new RewardInfoWaterFallAdapter2(this, m_listRewardInfo);
		// 
		m_lvReward.setAdapter(m_rifAdapter);

		// 监听
		m_lvReward.setOnRefreshListener(new OnRefreshListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				m_nListStatus = LISTVIEW_STATUS_ONREFRESH;

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
		
		if(HeadhunterPublic.REWARD_INDUSTRYTYPE_ALL == m_nTabtype ){
			m_nListStatus = LISTVIEW_STATUS_ONREFRESH;
			m_lvReward.firstRefreshing();
			// 获取悬赏任务数据
			startGetData(m_nDirection, m_strOffsetid, m_nRequestType);
		}
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
	
	/**
	 * 
	 */
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

			if(null != m_filtersBak){
				m_filters = m_filtersBak;
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
//					if (HeadhunterPublic.REWARD_DIRECTIONTYPE_OLD == m_nDirection) {
//						m_lvReward.setSelection(m_nBottomposition);
//					} else {
//						m_lvReward.setSelection(0);
//					}
//					m_rifAdapter.upData(m_listRewardInfo);
					m_rifAdapter.notifyDataSetChanged();
					// 变更状态
					changeListViewStatus(lsRD);
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
					// 读取历史数据
					getHistroyData();
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
//				m_listRewardInfo = lsRD;
				m_nBottomposition = 0;
				
				if(!bIsDBdata){
					// 删除原来的缓存数据
					deleteAllRewardData(m_nRequestType);
				}
			} else {
				m_nBottomposition = m_listRewardInfo.size() - 1;
//				m_listRewardInfo.addAll(lsRD);
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
		return RewardData.getRewardDataTabHost(m_appContext, m_nRequestDataType,
				nRequestType, m_nTabtype, "", "", false);
	}
	
	/**
	 * 保存任务数据到数据库中
	 * @param rld
	 */
	private void saveRewardData(RewardData rd){
		RewardData.saveRewardDataForTabHost(m_appContext, rd, m_nTabtype);
	}
	
	/**
	 * 删除数据库中同一用户，同一请求类型的任务数据, 同一行业类型(选项卡)
	 * @param nRequestType
	 */
	private void deleteAllRewardData(int nRequestType){
		RewardData.deleteAllRewardDataForTabHost(m_appContext, m_nRequestDataType,
				nRequestType, m_nTabtype);
	}
	
	/**
	 * 更新数据库中的任务数据状态
	 * @param rld
	 */
	private void upRewardData(RewardData rld){
		RewardData.upRewardDataForTabHost(m_appContext, rld, m_nTabtype);
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
	 * 初始化全局数据
	 */
	private void initData(){
		m_nDirection = HeadhunterPublic.REWARD_DIRECTIONTYPE_NEW;
//		m_nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_SORT;
		m_nRequestType = HeadhunterPublic.REWARD_REQUESTTYPE_LIKE;
		m_strOffsetid = "";
		m_nLastBonus = "";
		m_filters = new RewardFilterCondition();
		m_bIsSearch = false;
		m_nRequestDataType = REQUEST_DATA_TYPE_COMPANY;	// 默认获取公司悬赏任务数据
		m_nLastRequestDataType = REQUEST_DATA_TYPE_COMPANY;	
		// 默认全部行业
		m_nTabtype = HeadhunterPublic.REWARD_INDUSTRYTYPE_ALL; 
		
		// 获取传递过来的数据
		Bundle bundle = getArguments();
		// 获取全局数据
		upBundle(bundle);
	}
	
	/**
	 * 更新全局数据
	 */
	public void upBundle(Bundle bundle){
		if(null != bundle){
			m_nTabtype = bundle.getInt(HeadhunterPublic.REWARD_DATATRANSFER_INDUSTRYTYPE);
			switch(m_nTabtype){
			case HeadhunterPublic.REWARD_INDUSTRYTYPE_IT:
				{
					String[] str = {"1"};
					m_filters.setIndustry_fid(str);
				}
				break;
			case HeadhunterPublic.REWARD_INDUSTRYTYPE_GAMES:
				{
					String[] str = {"2"};
					m_filters.setIndustry_fid(str);	
				}
				break;
			case HeadhunterPublic.REWARD_INDUSTRYTYPE_ADVERTISEMENT:
				{
					String[] str = {"3"};
					m_filters.setIndustry_fid(str);
				}
				break;
			case HeadhunterPublic.REWARD_INDUSTRYTYPE_ALL:
			default:
				{
					;
				}
				break;
			}
			// 备份
			m_filtersBak = m_filters;

			// 请求的数据类型(个人, 公司, 混合)
			m_nRequestDataType = bundle.getInt(
					HeadhunterPublic.REWARD_DATATRANSFER_REQUESTDATATYPE);
			
			// 请求的悬赏任务过滤类型
			m_nRequestType = bundle.getInt(
					HeadhunterPublic.REWARD_DATATRANSFER_REQUESTTYPE);
			
			RewardFilterCondition rfc = (RewardFilterCondition) bundle
					.getSerializable(HeadhunterPublic.REWARD_DATATRANSFER_FILTERS);
			if(null != rfc){
				m_filters = rfc;
			}
		}
	}
	
	/**
	 * 更新数据
	 */
	public void upData(){
		m_nListStatus = LISTVIEW_STATUS_ONREFRESH;
		m_lvReward.setLoading();
		
		// 获取悬赏任务数据
		startGetData(m_nDirection, m_strOffsetid, m_nRequestType);
	}
	
	/**
	 * 读取历史数据
	 */
	private void getHistroyData(){
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
				
				return;
			}

			// 更新悬赏任务数据列表
			upRewardListData(lsRD, true);
			m_nBottomposition = 0;
			m_rifAdapter.notifyDataSetChanged();
			
			// 变更状态
			changeListViewStatus(lsRD);
			
			if(m_nLastRequestDataType != m_nRequestDataType){
				m_nLastRequestDataType = m_nRequestDataType;
			}
		}else{
			m_lvReward.onLoadMoreComplete(PullToRefreshFooter.STATE_LOADFULL);
			m_bBtnTypeClick = false;
		}
	}
}

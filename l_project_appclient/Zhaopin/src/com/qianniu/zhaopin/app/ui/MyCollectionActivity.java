
package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.PullDownListAdapter;
import com.qianniu.zhaopin.app.adapter.RewardInfoListAdapter;
import com.qianniu.zhaopin.app.adapter.RewardInfoListAdapter.RewardListInfoViewHolder;
import com.qianniu.zhaopin.app.bean.PullDownListInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.bean.RewardListDataEntity;
import com.qianniu.zhaopin.app.bean.reqRewardList;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView.OnRefreshListener;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 我的收藏
 * @author wuzy
 *
 */
public class MyCollectionActivity extends BaseActivity implements
    OnItemClickListener, OnScrollListener, OnRefreshListener{

	private Context m_Context;
	private AppContext m_appContext;
	
	private TextView m_tvTitle;					// 标题
	
	private ImageButton m_btnBack;				// 返回按钮
	
	private View lvInfoType_footer;
	private TextView lvInfoType_foot_more;
	private ProgressBar lvInfoType_foot_progress;
	
	// 下拉菜单列表相关
	private PullDownListAdapter m_PullDownLA;
	private List<PullDownListInfo> m_PDLI;
	private ListView m_PDLV;
	
	// 收藏的任务列表相关
	private PullToRefreshListView m_lvReward;		// 
	private RewardInfoListAdapter m_RewardLA;
	private List<RewardData> m_listRewardInfo;
	private int m_nChooseItem;
	
//	private View m_viewLoad;
	
	private int m_nActionType;				// 获取数据的动作类型
	
	private int m_nDirection;				//
	private String m_strOffsetid;			// 偏移ID
	
	private int m_nRequestType;				// 请求类型(公司/个人)
	private int m_nLastRequestType;			// 上次请求类型(公司/个人)
	
	// 当前页面最上一行的悬赏任务ID
	private String m_strTopRewardID;
	// 当前页面最尾一行的悬赏任务ID
	private String m_strBottomRewardID;
	// 
	private int m_nBottomposition;

	private boolean m_isFirstRefreashing = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycollection);
		
		m_Context = this;
		m_appContext = (AppContext) getApplication();
		
		initData();
		initControl();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if( HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode){
			switch(resultCode){
			case HeadhunterPublic.RESULT_COMPANYINOF_OK:
				{
					if(null == m_listRewardInfo){
						return;
					}
					// 获取Activity传递过来的数据
					Bundle bundle = data.getExtras();
					if(null != bundle){
						// 公司ID
						String strCompanyId = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYID);
						if(null == strCompanyId){
							return;
						}
						
						if(strCompanyId.isEmpty()){
							return;
						}
						
						// 获取收藏状态
						String strCollectionStatus = bundle.getString(
								HeadhunterPublic.REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG);
						
						if(null == strCollectionStatus){
							return;
						}
						
						if(strCollectionStatus.isEmpty()){
							return;
						}
						
						RewardData rld = m_listRewardInfo.get(m_nChooseItem);
						// 更新应聘状态
						if(null != rld){
							// 判断收藏状态是否被修改
							if(!strCollectionStatus.equals(rld.getM27_status())){
								m_listRewardInfo.remove(m_nChooseItem);
								if(null != m_RewardLA){
									m_RewardLA.notifyDataSetChanged();
								}
							}
						}
					}
				}
				break;
			case HeadhunterPublic.RESULT_REWARDINOF_OK:
				{
					if(null == m_listRewardInfo){
						return;
					}
					
					// 获取Activity传递过来的数据
					Bundle bundle = data.getExtras();
					if(null != bundle){
						// 任务ID
						String strTaskId = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID);
						if(null == strTaskId){
							return;
						}
						
						if(strTaskId.isEmpty()){
							return;
						}
						
						// 获取收藏状态
						String strCollectionStatus = bundle.getString(
								HeadhunterPublic.REWARD_DATATRANSFER_COLLECTIONFLAG);
						// 获取是否应聘状态
						String strCandidateStatus = bundle.getString(
								HeadhunterPublic.REWARD_DATATRANSFER_CANDIDATEFLAG);
						
						RewardData rld = m_listRewardInfo.get(m_nChooseItem);
						// 更新应聘状态
						if(null != rld){
							if(null != strCollectionStatus){
								// 判断收藏状态是否被修改
								if(!strCollectionStatus.equals(rld.getAction_3())){
									m_listRewardInfo.remove(m_nChooseItem);
									if(null != m_RewardLA){
										m_RewardLA.notifyDataSetChanged();
									}
								}
							}
							
							// 更新应聘状态
							if(null != strCandidateStatus){
								if(!strCandidateStatus.isEmpty()){
									rld.setAction_1(strCandidateStatus);
								}
							}
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		m_lvReward.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
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
			startGetData(HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_OLD, 
					m_strBottomRewardID, m_nRequestType);	
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int nItem, long arg3) {
		// TODO Auto-generated method stub
		if((ListView)arg0 == m_PDLV){
			PullDownListInfo pli = m_PDLI.get(nItem);
			if(!pli.getIsChoose()){
				m_tvTitle.setText(pli.getText());
				//
				resetPullDownListView();
				pli.setIsChoose(true);
				m_PullDownLA.notifyDataSetChanged();
			}
			
			m_tvTitle.setBackgroundResource(R.drawable.common_tv_bg_pulldown_normal);
			m_PDLV.setVisibility(View.GONE);
			
			int nRequestType = HeadhunterPublic.MYCOLLECTION_DATATYPE_REWARD;
			switch(nItem){
			case 1:
				{
					nRequestType = HeadhunterPublic.MYCOLLECTION_DATATYPE_COMPANY;
					m_RewardLA.setType(HeadhunterPublic.ACTIVITY_TYPE_MYCOLLECTION_COMPANY);
				}
				break;
			case 0:
			default:
				{
					nRequestType = HeadhunterPublic.MYCOLLECTION_DATATYPE_REWARD;
					m_RewardLA.setType(HeadhunterPublic.ACTIVITY_TYPE_MYCOLLECTION_TASK);
				}
				break;
			}
			
			// 获取收藏的任务列表
			startGetData(HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_NEW, "", nRequestType);
			
		}else if( (PullToRefreshListView)arg0 == m_lvReward){

			// 点击头部无效
			if (nItem <= m_lvReward.getHeaderViewsCount() - 1){
				return;
			}
			
			RewardListInfoViewHolder rivh = (RewardListInfoViewHolder) view.getTag();
			if(null == rivh){
				return;
			}
			
			if(null == m_listRewardInfo){
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

			switch(m_nRequestType){
			case HeadhunterPublic.MYCOLLECTION_DATATYPE_COMPANY:	// 公司收藏
				{
					// 进入公司详情界面
					showCompanyInfoActivity(rld);
				}
				break;
			case HeadhunterPublic.MYCOLLECTION_DATATYPE_REWARD:		// 任务收藏
			default:
				{
					// 进入悬赏详细界面
					showRewardInfoActivity(rld);
				}
				break;
			}
			

		}	
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		// 
		m_nActionType = UIHelper.LISTVIEW_ACTION_REFRESH;
		m_isFirstRefreashing = true;
		// 获取收藏任务数据
		startGetData(HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_NEW, "", m_nRequestType);
	}

	/**
	 * 初始化数据
	 */
	private void initData(){
		m_nDirection = HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_NEW;
		m_nRequestType = HeadhunterPublic.MYCOLLECTION_DATATYPE_REWARD;
		m_nLastRequestType = m_nRequestType;
		m_strOffsetid = "";
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
//		m_viewLoad = (View) findViewById(R.id.commom_loading_layout); 
		
		// 收藏的悬赏任务列表
		m_lvReward = (PullToRefreshListView)findViewById(R.id.mycollection_lv_reward);
		m_lvReward.setOnItemClickListener(this);
		m_lvReward.setOnScrollListener(this);
		m_lvReward.setOnRefreshListener(this);
		
		// 弹出下拉ListView
		m_PDLV = (ListView)findViewById(R.id.mycollection_lv_pulldown);
		m_PDLV.setOnItemClickListener(this);
		
		// 标题
		m_tvTitle = (TextView)findViewById(R.id.mycollection_tv_title);
		m_tvTitle.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != m_PDLV){
					switch(m_PDLV.getVisibility()){
					case View.VISIBLE:
						{
							m_tvTitle.setBackgroundResource(R.drawable.common_tv_bg_pulldown_normal);
							m_PDLV.setVisibility(View.GONE);
						}
						break;
					case View.GONE:
						{
							m_tvTitle.setBackgroundResource(R.drawable.common_tv_bg_pulldown_open);
							m_PDLV.setVisibility(View.VISIBLE);
						}
						break;
					default:
						break;
					}
				}
			}
		});
		
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.mycollection_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		initPullDownListView();
		initRewardListView();
	}

	/**
	 * 
	 */
	private void initPullDownListView(){
		m_PDLI = new ArrayList<PullDownListInfo>();
		
		PullDownListInfo pdl = new PullDownListInfo();
		pdl.setText(getText(R.string.str_mycollection_sort_reward).toString());
		pdl.setIsChoose(true);
		m_PDLI.add(pdl);
		
		PullDownListInfo pdl2 = new PullDownListInfo();
		pdl2.setText(getText(R.string.str_mycollection_sort_company).toString());
		m_PDLI.add(pdl2);
		
		m_PullDownLA  = new PullDownListAdapter(m_appContext, m_PDLI);
		m_PDLV.setAdapter(m_PullDownLA);
	}
	
	/**
	 * 初始化数据列表
	 */
	private void initRewardListView(){
		
		lvInfoType_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lvInfoType_footer.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int lvDataState = StringUtils.toInt(m_lvReward.getTag());
				if(UIHelper.LISTVIEW_DATA_MORE == lvDataState){
					startGetData(HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_OLD, 
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
				HeadhunterPublic.ACTIVITY_TYPE_MYCOLLECTION_TASK, m_listRewardInfo);
		
		m_lvReward.addFooterView(lvInfoType_footer);
		m_lvReward.setAdapter(m_RewardLA);
		
		m_lvReward.firstRefreshing();
		m_isFirstRefreashing = true;
		m_nActionType = UIHelper.LISTVIEW_ACTION_INIT;
		// 获取悬赏任务数据
		startGetData(HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_NEW, "", m_nRequestType);
	}
	
	/**
	 * 重置 下拉ListView
	 */
	private void resetPullDownListView(){
		if(null == m_PDLI){
			return;
		}

		for(PullDownListInfo pdli : m_PDLI){
			if(pdli.getIsChoose()){
				pdli.setIsChoose(false);
			}
		}
	}
	
	/**
	 * 获取收藏数据
	 * @param nRequestType
	 */
	private void startGetData(int nDirection, String strOffsetid, int nRequestType){
		m_nRequestType = nRequestType;
		m_nDirection = nDirection;
		m_strOffsetid = strOffsetid;

    	// 判断网络是否连接
		if(!UIHelper.isNetworkConnected(m_appContext)){
			m_CollectionHandler.sendMessage(m_CollectionHandler.obtainMessage(
					HeadhunterPublic.MYCOLLECTIONMSG_GETDATA_NONETWORKCONNECT));
			return;
		}
		
		m_lvReward.setTag(UIHelper.LISTVIEW_DATA_LOADING);
		if(!m_isFirstRefreashing){
			lvInfoType_foot_more.setText(R.string.load_ing);
			lvInfoType_foot_progress.setVisibility(View.VISIBLE);
		}else{
			m_isFirstRefreashing = false;
		}
		m_lvReward.setLoadingState(true);
		
    	new Thread(){
	    	public void run(){
	    		getTaskCollectionListData();
	    	}
    	}.start();
	}
	
	/**
	 * 获取收藏的任务列表
	 */
	private void getTaskCollectionListData(){
		try {
			// 设置请求参数
			reqRewardList rrl = new reqRewardList();
			rrl.setDirection(m_nDirection);
			rrl.setOffsetid(m_strOffsetid);
			
			// 获取收藏的任务数据
			RewardListDataEntity rlde = m_appContext.getCollectionList(rrl, m_nRequestType);

            Result res = rlde.getValidate();
			// 
			m_lvReward.setLoadingState(false);
            if( res.OK()){
            	// 成功
            	m_CollectionHandler.sendMessage(m_CollectionHandler.obtainMessage(
						HeadhunterPublic.MYCOLLECTIONMSG_GETCOLLECTION_SUCCESS, rlde.getData()));
            }else{
            	if(HeadhunterPublic.LINK_RESULT_DATA_TASKTABLEQUERYISNULL == res.getErrorCode() ||
            			HeadhunterPublic.LINK_RESULT_DATA_TASKACTION == res.getErrorCode()){
            		if(HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_NEW == m_nDirection){
            			// 没有数据
                    	m_CollectionHandler.sendMessage(m_CollectionHandler.obtainMessage(
        						HeadhunterPublic.MYCOLLECTIONMSG_GETCOLLECTION_ISNULL));
            		}else{// 没有更多数据
                    	m_CollectionHandler.sendMessage(m_CollectionHandler.obtainMessage(
        						HeadhunterPublic.MYCOLLECTIONMSG_GETCOLLECTION_NOMORE));
            		}
            	}
            	else{
            		// 失败
                	m_CollectionHandler.sendMessage(m_CollectionHandler.obtainMessage(
    						HeadhunterPublic.MYCOLLECTIONMSG_GETCOLLECTION_FAIL, res.getErrorMessage()));
            	}
            }
        } catch (AppException e) {
        	e.printStackTrace();
        	m_lvReward.setLoadingState(false);
        	// 异常
        	m_CollectionHandler.sendMessage(m_CollectionHandler.obtainMessage(
					HeadhunterPublic.MYCOLLECTIONMSG_GETCOLLECTION_ABNORMAL, e));
        }
	}
	
	/**
	 * 更新悬赏任务列表数据
	 * @param rld
	 */
	private void upRewardListData(List<RewardData> lsRLD){
		boolean bFirst = false;
		
		if(null == m_listRewardInfo){
			m_listRewardInfo = new ArrayList<RewardData>();
			m_nBottomposition = 0;
		}else{
			if(HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_NEW == m_nDirection){
				m_listRewardInfo.clear();
				m_nBottomposition = 0;
			}else{
				m_nBottomposition = m_listRewardInfo.size() - 1;
			}
		}
		
		for(RewardData rld : lsRLD){
			if(!bFirst){
				// 获取第一行的悬赏任务ID
				m_strTopRewardID = rld.getTask_Id();
				bFirst = true;
			}
			// 获取最尾一行的悬赏任务ID
			m_strBottomRewardID = rld.getTask_Id();

			// 获取悬赏任务所有信息，并且添加到列表中
			m_listRewardInfo.add(rld);
		}
	}
	
	/**
	 * 消息处理
	 */
	private Handler m_CollectionHandler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what){
			case HeadhunterPublic.MYCOLLECTIONMSG_GETCOLLECTION_ABNORMAL:
				{	
					m_lvReward.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvInfoType_foot_more.setText(R.string.load_more);
					
					((AppException)msg.obj).makeToast(m_Context);
					
					// 判断是否要清楚原来的数据
					checkIsClearData();
				}
				break;
			case HeadhunterPublic.MYCOLLECTIONMSG_GETCOLLECTION_FAIL:
				{
					m_lvReward.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvInfoType_foot_more.setText(R.string.load_more);

					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_mycollection_getcollection_fail));
					
					// 判断是否要清楚原来的数据
					checkIsClearData();
				}
				break;
			case HeadhunterPublic.MYCOLLECTIONMSG_GETCOLLECTION_SUCCESS:
				{
					// 更新悬赏任务数据列表
					upRewardListData((List<RewardData>)msg.obj);					
					if(HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_OLD == m_nDirection){
						m_lvReward.setSelection(m_nBottomposition);
					}else{
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
			case HeadhunterPublic.MYCOLLECTIONMSG_GETCOLLECTION_ISNULL:
				{// 没有数据
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_mycollection_getcollection_isnull));
					
					// 判断是否要清楚原来的数据
					checkIsClearData();
				}
				break;
			case HeadhunterPublic.MYCOLLECTIONMSG_GETCOLLECTION_NOMORE:
				{// 没有更多的数据
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_mycollection_getcollection_nomore));
					
					m_lvReward.setTag(UIHelper.LISTVIEW_DATA_FULL);
					lvInfoType_foot_more.setText(R.string.load_full);
				}
				break;
			case HeadhunterPublic.MYCOLLECTIONMSG_GETDATA_NONETWORKCONNECT:
				{
					// 判断是否要清楚原来的数据
					checkIsClearData();
				}
				break;
			default:
				break;
			}
			
			m_nLastRequestType = m_nRequestType;
			
			lvInfoType_foot_progress.setVisibility(ProgressBar.GONE);
			if (0 == m_RewardLA.getCount()) {
				m_lvReward.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
				lvInfoType_foot_more.setText(R.string.load_empty);
			}
			
			switch(m_nActionType){
			case UIHelper.LISTVIEW_ACTION_REFRESH: 
			case UIHelper.LISTVIEW_ACTION_INIT:
				{
					if(HeadhunterPublic.MYCOLLECTION_DIRECTIONTYPE_OLD != m_nDirection){
						m_lvReward.onRefreshComplete();
						m_lvReward.setSelection(0);
					}
					m_nActionType = UIHelper.LISTVIEW_ACTION_INIT;
				}
				break;
			case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
				{
					m_lvReward.onRefreshComplete();
					m_lvReward.setSelection(0);	
					m_nActionType = UIHelper.LISTVIEW_ACTION_INIT;
				}
				break;
			default:
				break;
			}
		}
	};
	

	
	/**
	 * 进入公司详情界面
	 * @param rld
	 */
	private void showCompanyInfoActivity(RewardData rld){

		// 数据传输
		Bundle bundle = new Bundle();
		bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYID, rld.getId());
		bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYURL, rld.getUrl());
//		bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG,
//				HeadhunterPublic.COMPANY_COLLECTION_FLAG);
		
		// 进入悬赏详细界面
        Intent intent = new Intent();
        intent.setClass(m_Context, CompanyInfoAcitivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 进入悬赏详细界面
	 * @param rld
	 */
	private void showRewardInfoActivity(RewardData rld){

		// 数据传输
		Bundle bundle = new Bundle();
		bundle.putSerializable(HeadhunterPublic.REWARD_DATATRANSFER_REWARDDATA, rld);
		bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_ACTIYITYTYPE,
				HeadhunterPublic.ACTIVITY_TYPE_MYCOLLECTION_TASK);
		
		// 进入悬赏详细界面
        Intent intent = new Intent();
        intent.setClass(m_Context, RewardInfoActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 判断是否要清楚原来的数据
	 */
	private void checkIsClearData(){
		if(m_nLastRequestType != m_nRequestType){
			m_listRewardInfo.clear();
			m_nBottomposition = 0;
			m_lvReward.setSelection(0);
			m_RewardLA.notifyDataSetChanged();

			m_lvReward.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
			lvInfoType_foot_more.setText(R.string.load_empty);
		}
	}
}

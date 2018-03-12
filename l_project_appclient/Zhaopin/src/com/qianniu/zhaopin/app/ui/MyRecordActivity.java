package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.RewardInfoListAdapter;
import com.qianniu.zhaopin.app.adapter.RewardInfoListAdapter.RewardListInfoViewHolder;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.bean.RewardListDataEntity;
import com.qianniu.zhaopin.app.bean.reqMyApply;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 我的记录
 * @author wuzy
 *
 */
public class MyRecordActivity extends BaseActivity implements
	OnItemClickListener, OnScrollListener, OnRefreshListener{

	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageButton m_btnBack;				// 返回按钮
	
	private View m_viewLoad;
	
	private View lvInfoType_footer;
	private TextView lvInfoType_foot_more;
	private ProgressBar lvInfoType_foot_progress;
	
	// 收藏的任务列表相关
	private PullToRefreshListView m_lvReward;		// 
	private RewardInfoListAdapter m_RewardLA;
	private List<RewardData> m_listRewardInfo;
	private int m_nChooseItem;
	
	private int m_nActionType;				// 获取数据的动作类型
	
	private int m_nDirection;				//
	private String m_strVerifyStatus;		// 审核状态
	private String m_strOffsetid;			// 偏移ID
	private String m_strTaskActionId;
	
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
		
		setContentView(R.layout.activity_myrecord);
		
		m_Context = this;
		m_appContext = (AppContext) getApplication();
		
		initData();
		initControl();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		// 
		m_nActionType = UIHelper.LISTVIEW_ACTION_REFRESH;
		m_isFirstRefreashing = true;
		// 获取记录数据
		startGetData(HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_NEW, "", m_strVerifyStatus);
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
			startGetData(HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_OLD,
					m_strBottomRewardID, m_strVerifyStatus);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int nItem, long arg3) {
		// TODO Auto-generated method stub
		if( (PullToRefreshListView)arg0 == m_lvReward){

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

			// 数据传输
			Bundle bundle = new Bundle();
			bundle.putSerializable(HeadhunterPublic.REWARD_DATATRANSFER_REWARDDATA, rld);
			bundle.putInt(HeadhunterPublic.REWARD_DATATRANSFER_ACTIYITYTYPE,
					HeadhunterPublic.ACTIVITY_TYPE_MYRECORD);
			
			// 进入悬赏详细界面
	        Intent intent = new Intent();
	        intent.setClass(m_Context, RewardInfoActivity.class);
	        intent.putExtras(bundle);
	        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
		}	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 初始化数据
	 */
	private void initData(){
		m_nDirection = HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_NEW;
		m_strOffsetid = "";
		m_strVerifyStatus = "";
		m_strTaskActionId = "";
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.myrecord_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		initRecordListView();
	}
	
	/**
	 * 初始化数据列表
	 */
	private void initRecordListView(){
		
		m_viewLoad = (View) findViewById(R.id.commom_loading_layout); 
		
		// 收藏的悬赏任务列表
		m_lvReward = (PullToRefreshListView)findViewById(R.id.myrecord_lv_record);
		m_lvReward.setOnItemClickListener(this);
		m_lvReward.setOnScrollListener(this);
		m_lvReward.setOnRefreshListener(this);
		
		lvInfoType_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lvInfoType_footer.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int lvDataState = StringUtils.toInt(m_lvReward.getTag());
				if(UIHelper.LISTVIEW_DATA_MORE == lvDataState){
					startGetData(HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_OLD,
							m_strBottomRewardID, m_strVerifyStatus);
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
				HeadhunterPublic.ACTIVITY_TYPE_MYRECORD, m_listRewardInfo);
		
		m_lvReward.addFooterView(lvInfoType_footer);
		m_lvReward.setAdapter(m_RewardLA);
		
		m_lvReward.firstRefreshing();
		m_isFirstRefreashing = true;
		m_nActionType = UIHelper.LISTVIEW_ACTION_INIT;
		// 获取悬赏任务数据
		startGetData(HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_NEW, "", "");
	}
	
	/**
	 * 获取我的记录数据
	 */
	private void startGetData(int nDirection, String strOffsetid, String strVerifyStatus){
		m_nDirection = nDirection;
		m_strOffsetid = strOffsetid;
		m_strVerifyStatus = strVerifyStatus;

    	// 判断网络是否连接
		if(!UIHelper.isNetworkConnected(m_appContext)){
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MYRECORDMSG_GETDATA_NONETWORKCONNECT));
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
        		getRecordListData();
        	}
        }.start();
	}
	
	private void getRecordListData(){
		try {
			// 设置请求参数
			reqMyApply rma = new reqMyApply();
//			rma.setDirection(m_nDirection);
//			rma.setOffsetfield("task_id");
//			rma.setOffsetid(m_strOffsetid);
			rma.setAction_direction(String.valueOf(m_nDirection));
			if(HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_OLD == m_nDirection){
				rma.setTask_action_id(m_strTaskActionId);
			}
			
			// 获取悬赏首页数据
			RewardListDataEntity rlde = m_appContext.getRecordList(rma);

            Result res = rlde.getValidate();
			// 
			m_lvReward.setLoadingState(false);
            if( res.OK()){
            	// 成功
            	m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MYRECORDMSG_GETRECORD_SUCCESS, rlde.getData()));
            }else{
            	if(HeadhunterPublic.LINK_RESULT_DATA_TASKTABLEQUERYISNULL == res.getErrorCode() ||
            			HeadhunterPublic.LINK_RESULT_DATA_TASKACTION == res.getErrorCode()){
            		if(HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_NEW == m_nDirection){
            			// 没有数据
                    	m_Handler.sendMessage(m_Handler.obtainMessage(
            					HeadhunterPublic.MYRECORDMSG_GETRECORD_ISNULL));
            		}else{
            			// 没有更多数据
                    	m_Handler.sendMessage(m_Handler.obtainMessage(
            					HeadhunterPublic.MYRECORDMSG_GETRECORD_NOMORE));
            		}
            	}else{
	            	// 失败
	            	m_Handler.sendMessage(m_Handler.obtainMessage(
	    					HeadhunterPublic.MYRECORDMSG_GETRECORD_FAIL, res.getErrorMessage()));
            	}
            }
        } catch (AppException e) {
        	e.printStackTrace();
        	m_lvReward.setLoadingState(false);
	    	// 异常
        	m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MYRECORDMSG_GETRECORD_ABNORMAL, e));
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
			if(HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_NEW == m_nDirection){
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
			//
			m_strTaskActionId = rld.getTask_action_id();
			
			// 获取悬赏任务所有信息，并且添加到列表中
			m_listRewardInfo.add(rld);
		}
	}
	
	/**
	 * 消息处理
	 */
	private Handler m_Handler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what){
			case HeadhunterPublic.MYRECORDMSG_GETRECORD_ABNORMAL:
				{
					m_lvReward.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvInfoType_foot_more.setText(R.string.load_more);
					
					((AppException)msg.obj).makeToast(m_Context);
				}
				break;
			case HeadhunterPublic.MYRECORDMSG_GETRECORD_FAIL:
				{
					m_lvReward.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvInfoType_foot_more.setText(R.string.load_more);
					
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_myrecord_getrecord_fail));		
				}
				break;
			case HeadhunterPublic.MYRECORDMSG_GETRECORD_SUCCESS:
				{
					// 更新任务记录数据列表
					upRewardListData((List<RewardData>)msg.obj);
					if (HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_OLD == m_nDirection) {
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
			case HeadhunterPublic.MYRECORDMSG_GETRECORD_ISNULL:
				{// 没有数据
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_myrecord_getrecord_isnull));	
				}
				break;
			case HeadhunterPublic.MYRECORDMSG_GETRECORD_NOMORE:
				{// 没有更多的数据
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_myrecord_getrecord_nomore));
					
					m_lvReward.setTag(UIHelper.LISTVIEW_DATA_FULL);
					lvInfoType_foot_more.setText(R.string.load_full);
				}
				break;
			case HeadhunterPublic.MYRECORDMSG_GETDATA_NONETWORKCONNECT:
				{
					;
				}
				break;
			default:
				break;
			}
			
			lvInfoType_foot_progress.setVisibility(ProgressBar.GONE);
			if (0 == m_RewardLA.getCount()) {
				m_lvReward.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
				lvInfoType_foot_more.setText(R.string.load_empty);
			}
			
			switch(m_nActionType){
			case UIHelper.LISTVIEW_ACTION_REFRESH:
			case UIHelper.LISTVIEW_ACTION_INIT:
				{
					if(HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_OLD != m_nDirection){
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
}

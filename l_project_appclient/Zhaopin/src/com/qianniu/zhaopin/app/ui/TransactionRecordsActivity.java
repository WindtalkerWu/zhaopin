package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.TransactionRecordsAdapter;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.TransactionRecordData;
import com.qianniu.zhaopin.app.bean.TransactionRecordListDataEntity;
import com.qianniu.zhaopin.app.bean.reqConsumelog;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView.OnRefreshListener;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 交易记录界面
 * @author wuzy
 *
 */
public class TransactionRecordsActivity extends BaseActivity implements
	OnScrollListener, OnRefreshListener{
	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageButton m_btnBack;				// 返回按钮
	
	// 交易记录列表相关
	private PullToRefreshListView m_lvRecords;		//
	private TransactionRecordsAdapter m_trAdapter;
	private List<TransactionRecordData> m_lsRecordInfo;
	
	private View m_viewLoad;
	private View lvInfoType_footer;
	private TextView lvInfoType_foot_more;
	private ProgressBar lvInfoType_foot_progress;
	
	private int m_nActionType;				// 获取数据的动作类型
	
	private boolean m_isFirstRefreashing = false;
	
	// 当前页面最上一行的悬赏任务ID
	private String m_strTopRecordID;
	// 当前页面最尾一行的悬赏任务ID
	private String m_strBottomRecordID;
	// 
	private int m_nBottomposition;
	/********************获取交易记录请求参数*****************/
	private int m_nDirection; 		// 请求的数据类型 1=>请求新数据, 0=>请求旧数据
	private String m_strOffsetid; 	// 偏移ID
	
	public static final int TRANSACTIONRECORD_DIRECTIONTYPE_OLD = 0;		// 请求旧数据
	public static final int TRANSACTIONRECORD_DIRECTIONTYPE_NEW = 1;		// 请求新数据
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_transactionrecords);
		
		m_Context = this;
		m_appContext = (AppContext) getApplication();
		
		initData();
		initControl();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		m_nActionType = UIHelper.LISTVIEW_ACTION_REFRESH;
		m_isFirstRefreashing = true;
		// 获取记录数据
		startGetData(TRANSACTIONRECORD_DIRECTIONTYPE_NEW, "");		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		m_lvRecords.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		m_lvRecords.onScrollStateChanged(view, scrollState);

		// 数据为空--不用继续下面代码了
		if (m_lsRecordInfo.isEmpty())
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

		int lvDataState = StringUtils.toInt(m_lvRecords.getTag());
		if(scrollEnd && UIHelper.LISTVIEW_DATA_MORE == lvDataState){
			startGetData(TRANSACTIONRECORD_DIRECTIONTYPE_OLD,
					m_strBottomRecordID);
		}	
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		m_nDirection = TRANSACTIONRECORD_DIRECTIONTYPE_NEW;
		m_strOffsetid = "";
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.transactionrecords_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		initRecordsListView();
	}
	
	/**
	 * 
	 */
	private void initRecordsListView(){
		m_viewLoad = (View) findViewById(R.id.commom_loading_layout); 
		
		// 交易记录列表
		m_lvRecords = (PullToRefreshListView)findViewById(R.id.transactionrecords_lv_records);
//		m_lvRecords.setOnItemClickListener(this);
		m_lvRecords.setOnScrollListener(this);
		m_lvRecords.setOnRefreshListener(this);
		
		// 
		lvInfoType_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lvInfoType_footer.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int lvDataState = StringUtils.toInt(m_lvRecords.getTag());
				if(UIHelper.LISTVIEW_DATA_MORE == lvDataState){
//					startGetData(HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_OLD,
//							m_strBottomRewardID, m_strVerifyStatus);
				}
			}
		});
		
		lvInfoType_foot_progress = (ProgressBar) lvInfoType_footer
				.findViewById(R.id.listview_foot_progress);
		lvInfoType_foot_progress.setVisibility(View.GONE);
		lvInfoType_foot_more = (TextView) lvInfoType_footer
				.findViewById(R.id.listview_foot_more);
		
		m_lsRecordInfo = new ArrayList<TransactionRecordData>();
		
		m_trAdapter = new TransactionRecordsAdapter(m_appContext, m_lsRecordInfo);
		m_lvRecords.addFooterView(lvInfoType_footer);
		m_lvRecords.setAdapter(m_trAdapter);
		
		m_lvRecords.firstRefreshing();
		m_isFirstRefreashing = true;
		m_nActionType = UIHelper.LISTVIEW_ACTION_INIT;
		// 获取悬赏任务数据
		startGetData(TRANSACTIONRECORD_DIRECTIONTYPE_NEW, "");
	}
	
	private void startGetData(int nDirection, String strOffsetid){
		m_nDirection = nDirection;
		m_strOffsetid = strOffsetid;
		
		// 判断网络是否连接
		if (!UIHelper.isNetworkConnected(m_appContext)) {
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_GETTRANSACTIONRECORD_NONETWORKCONNECT));
			return;
		}
		
		m_lvRecords.setTag(UIHelper.LISTVIEW_DATA_LOADING);
		if(!m_isFirstRefreashing){
			lvInfoType_foot_more.setText(R.string.load_ing);
			lvInfoType_foot_progress.setVisibility(View.VISIBLE);
		}else{
			m_isFirstRefreashing = false;
		}
		m_lvRecords.setLoadingState(true);
		
	   	new Thread(){
        	public void run(){
        		getRecordListData();
        	}
        }.start();
	}
	
	private void getRecordListData(){
		try {
			// 设置请求参数
			reqConsumelog rcl = new reqConsumelog();
			rcl.setDirection(m_nDirection);
			rcl.setOffsetid(m_strOffsetid);
			
			// 获取悬赏首页数据
			TransactionRecordListDataEntity trlde = m_appContext.getTransactionRecordList(rcl);

			// 
            m_lvRecords.setLoadingState(false);
            
            Result res = trlde.getValidate();
            if( res.OK()){
            	// 成功
            	m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_GETTRANSACTIONRECORD_SUCCESS, trlde.getData()));
            }else{
            	if(HeadhunterPublic.LINK_RESULT_DATA_TASKTABLEQUERYISNULL == res.getErrorCode() ||
            			HeadhunterPublic.LINK_RESULT_DATA_TASKACTION == res.getErrorCode()){
            		if(TRANSACTIONRECORD_DIRECTIONTYPE_NEW == m_nDirection){
            			// 没有数据
                    	m_Handler.sendMessage(m_Handler.obtainMessage(
            					HeadhunterPublic.MSG_GETTRANSACTIONRECORD_ISNULL));
            		}else{
            			// 没有更多数据
                    	m_Handler.sendMessage(m_Handler.obtainMessage(
            					HeadhunterPublic.MSG_GETTRANSACTIONRECORD_NOMORE));
            		}
            	}else{
	            	// 失败
	            	m_Handler.sendMessage(m_Handler.obtainMessage(
	    					HeadhunterPublic.MSG_GETTRANSACTIONRECORD_FAIL, res.getErrorMessage()));
            	}
            }
        } catch (AppException e) {
        	e.printStackTrace();
        	m_lvRecords.setLoadingState(false);
	    	// 异常
        	m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.MSG_GETTRANSACTIONRECORD_ABNORMAL, e));
        }
	}
	
	/**
	 * 更新悬赏任务列表数据
	 * @param rld
	 */
	private void upRecordListData(List<TransactionRecordData> lsTRD, boolean bIsDBdata){
		boolean bFirst = false;
		
		if(null == m_lsRecordInfo){
			m_lsRecordInfo = new ArrayList<TransactionRecordData>();
			m_nBottomposition = 0;
			
			if(!bIsDBdata){
				// 删除原来的缓存数据
				deleteAllTransactionRecordData();				
			}
		}else{
			if(TRANSACTIONRECORD_DIRECTIONTYPE_NEW == m_nDirection){
				m_lsRecordInfo.clear();
				m_nBottomposition = 0;

				if(!bIsDBdata){
					// 删除原来的缓存数据
					deleteAllTransactionRecordData();				
				}
			}else{
				m_nBottomposition = m_lsRecordInfo.size() - 1;
			}
		}
		
		for(TransactionRecordData trd : lsTRD){
			if(!bFirst){
				// 获取第一行的悬赏任务ID
				m_strTopRecordID = trd.getId();
				bFirst = true;
			}
			// 获取最尾一行的悬赏任务ID
			m_strBottomRecordID = trd.getId();
			
			if(TRANSACTIONRECORD_DIRECTIONTYPE_NEW == m_nDirection &&
					!bIsDBdata){
				// 保存交易记录数据到数据库中
				saveTransactionRecordData(trd);
			}
			
			// 明细字符串截取
			if(null != trd.getTitle() &&
					!trd.getTitle().isEmpty()){
				String strTemp = detailSubString(trd.getTitle());
				trd.setTitle(strTemp);
			}
			
			// 获取悬赏任务所有信息，并且添加到列表中
			m_lsRecordInfo.add(trd);
		}
	}
	
	/**
	 * 消息处理
	 */
	private Handler m_Handler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what){
			case HeadhunterPublic.MSG_GETTRANSACTIONRECORD_NONETWORKCONNECT:// 没有网络
				{
					if(TRANSACTIONRECORD_DIRECTIONTYPE_NEW == m_nDirection){
						// 从数据库中读取缓存数据
						List<TransactionRecordData> lsTRD = getTransactionRecordData();
						// 融错处理
						if(null == lsTRD){
							if(null != m_lsRecordInfo){
								m_lsRecordInfo.clear();
								m_trAdapter.notifyDataSetChanged();
							}
							m_nBottomposition = 0;
							
							break;
						}else{
							if(lsTRD.size() <= 0){
								if(null != m_lsRecordInfo){
									m_lsRecordInfo.clear();
									m_trAdapter.notifyDataSetChanged();
								}
								m_nBottomposition = 0;
								
								break;
							}
						}
						
						// 更新悬赏任务数据列表
						upRecordListData(lsTRD, true);
						m_nBottomposition = 0;
						
						m_trAdapter.notifyDataSetChanged();
						
						m_lvRecords.setTag(UIHelper.LISTVIEW_DATA_FULL);
						lvInfoType_foot_more.setText(R.string.load_full);						
					}
				}
				break;
			case HeadhunterPublic.MSG_GETTRANSACTIONRECORD_SUCCESS:// 获取交易记录成功
				{
					// 更新交易记录数据列表
					upRecordListData((List<TransactionRecordData>)msg.obj, false);
					
					if (TRANSACTIONRECORD_DIRECTIONTYPE_NEW != m_nDirection) {
						m_lvRecords.setSelection(m_nBottomposition);
					} else {
						m_lvRecords.setSelection(0);
					}
					m_trAdapter.notifyDataSetChanged();
						
					if(m_lsRecordInfo.size() < 10){
						m_lvRecords.setTag(UIHelper.LISTVIEW_DATA_FULL);
						lvInfoType_foot_more.setText(R.string.load_full);				
					}else if(m_lsRecordInfo.size() >= 10){
						m_lvRecords.setTag(UIHelper.LISTVIEW_DATA_MORE);
						lvInfoType_foot_more.setText(R.string.load_more);
					}
				}
				break;
			case HeadhunterPublic.MSG_GETTRANSACTIONRECORD_FAIL:// 获取交易记录失败
				{
					m_lvRecords.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvInfoType_foot_more.setText(R.string.load_more);
					
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_transactionrecord_getrecord_fail));
				}
				break;
			case HeadhunterPublic.MSG_GETTRANSACTIONRECORD_ABNORMAL:// 获取交易记录异常
				{
					m_lvRecords.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvInfoType_foot_more.setText(R.string.load_more);
					
					((AppException)msg.obj).makeToast(m_Context);
				}
				break;
			case HeadhunterPublic.MSG_GETTRANSACTIONRECORD_ISNULL:
				{// 没有数据
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_transactionrecord_getrecord_isnull));	
				}
				break;
			case HeadhunterPublic.MSG_GETTRANSACTIONRECORD_NOMORE:
				{// 没有更多的数据
					UIHelper.ToastMessage(m_Context, 
							getString(R.string.msg_transactionrecord_getrecord_nomore));
					
					m_lvRecords.setTag(UIHelper.LISTVIEW_DATA_FULL);
					lvInfoType_foot_more.setText(R.string.load_full);
				}
				break;
			default:
				break;
			}
			
			lvInfoType_foot_progress.setVisibility(ProgressBar.GONE);
			if (0 == m_trAdapter.getCount()) {
				m_lvRecords.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
				lvInfoType_foot_more.setText(R.string.load_empty);
			}
			
			switch(m_nActionType){
			case UIHelper.LISTVIEW_ACTION_REFRESH:
			case UIHelper.LISTVIEW_ACTION_INIT:
				{
					if(HeadhunterPublic.MYRECORDMSG_DIRECTIONTYPE_OLD != m_nDirection){
						m_lvRecords.onRefreshComplete();
						m_lvRecords.setSelection(0);
					}
					m_nActionType = UIHelper.LISTVIEW_ACTION_INIT;
				}
				break;
			case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
				{
					m_lvRecords.onRefreshComplete();
					m_lvRecords.setSelection(0);	
					m_nActionType = UIHelper.LISTVIEW_ACTION_INIT;
				}
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 获取数据库中的数据
	 * @param nRequestType
	 */
	private List<TransactionRecordData> getTransactionRecordData(){
		return TransactionRecordData.getTransactionRecordData(m_appContext, "");
	}
	
	/**
	 * 保存任务数据到数据库中
	 * @param rld
	 */
	private void saveTransactionRecordData(TransactionRecordData trd){
		TransactionRecordData.saveTransactionRecordData(m_appContext, trd);
	}
	
	/**
	 * 删除数据库中同一用户，同一请求类型的任务数据
	 * @param nRequestType
	 */
	private void deleteAllTransactionRecordData(){
		TransactionRecordData.deleteAllTransactionRecordData(m_appContext);
	}
	
	/**
	 * 明细字符串截取
	 * @param str
	 * @return
	 */
	private String detailSubString(String str){
		String strTemp = "";
		
		int m = str.indexOf("target='_blank'>");
		int n = str.indexOf("</a>");
		
		if(n  > m + 16){
			strTemp = str.substring(m + 16, n);
		}
		
		return strTemp;
	}
}

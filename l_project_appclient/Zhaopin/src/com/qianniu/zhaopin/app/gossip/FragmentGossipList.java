package com.qianniu.zhaopin.app.gossip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.alibaba.fastjson.JSONObject;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView;
import com.huewu.pla.lib.MultiColumnListView.OnLoadMoreListener;
import com.huewu.pla.lib.MultiColumnPullToRefreshListView.OnRefreshListener;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.CompanyListAdapter;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.GossipMsgEntity;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.RequestInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.app.ui.BaseFragment;
import com.qianniu.zhaopin.app.ui.CompanyListActivity;
import com.qianniu.zhaopin.app.ui.SubscriptionManageFragment;
import com.qianniu.zhaopin.app.widget.PullToRefreshFooter;

public class FragmentGossipList extends BaseFragment{
	private static final String TAG = "Fragment_GossipList";
	
	private Context m_Context;
	private AppContext m_appContext; 
	private Activity m_activity;
	private boolean isVisibleToUser = false;
	private boolean bFirstRefresh = false;
	private int mtype = 0;
	/************************************************************/
	private View m_Container;
	private MultiColumnPullToRefreshListView m_listView;
	private GossipMsgListAdapter m_listAdapt;
	/************************************************************/
	private List<GossipMsgEntity> m_listData;
	/************************************************************/
	private static final int HANDCODE_EXCEPTIONCODE = 20;
	private static final int HANDCODE_REFRESHSUCCESS = 21;
	private static final int HANDCODE_LOADMORSUCCESS = 22;
	/************************************************************/
	
	public static FragmentGossipList newInstance(int type) {
		FragmentGossipList fragment = new FragmentGossipList();
		fragment.mtype = type;
		return fragment;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		m_Context = this.getActivity();
		m_activity = this.getActivity();
		m_appContext = (AppContext) this.getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);

		// TODO Auto-generated method stub
		if(null != m_Container){
			ViewGroup p = (ViewGroup) m_Container.getParent();
			if (p != null) {
				p.removeAllViewsInLayout();
			}
			
		}else{
			m_Container = inflater.inflate(R.layout.fragment_gossip_list, container,
					false);	
			initView();
			initListView();
			initData();
		}
		
		return m_Container;
	
	}
	private void initView(){
		m_listView = (MultiColumnPullToRefreshListView) m_Container.findViewById(R.id.listview);
		m_listView.setVisibility(View.VISIBLE);
	}
	private void initListView(){
		m_listData =  new ArrayList<GossipMsgEntity>();
		m_listAdapt = new GossipMsgListAdapter(m_activity, m_listData);
		m_listView.setAdapter(m_listAdapt);
		m_listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (!UIHelper.isNetworkConnected(m_appContext)) {
					m_listView.onRefreshComplete();
					return;
				}
				doRefresh();
			}
		});
		m_listView.setOnLoadMoreListener(new  OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				if (!UIHelper.isNetworkConnected(m_appContext)) {
					m_listView.onLoadMoreComplete(PullToRefreshFooter.STATE_NORMAL);
					return;
				}
				doLoadMore();
			}
		});
		if (m_listData == null || m_listData.size() == 0) {
			m_listView.setFooterState(PullToRefreshFooter.STATE_NULL);
		} else if (m_listData.size() < GossipMsgListAdapter.onePageCount) {
			m_listView.setFooterState(PullToRefreshFooter.STATE_LOADFULL);
		} else {
			m_listView.setFooterState(PullToRefreshFooter.STATE_NORMAL);
		}
	}
	private void initData() {
		if(bFirstRefresh|| !isVisibleToUser)
			return;
		if (!UIHelper.isNetworkConnected(m_appContext)) {
			return;
		} 
		bFirstRefresh = true;
		m_listView.firstRefreshing();
		doRefresh();
	}
	private void doRefresh() {
		ApiClient.pool.execute(new Runnable() {
			@Override
			public void run() {
				Result result = refreshGossipMsgData();
				if (result != null) {
					mDatahandler.sendMessage(mDatahandler.obtainMessage(HANDCODE_REFRESHSUCCESS, result));
				}
			}
		});
	}
	private void doLoadMore() {
		ApiClient.pool.execute(new Runnable() {
			@Override
			public void run() {
				Result result = getMoreGossipMsgData();
				if (result != null) {
					mDatahandler.sendMessage(mDatahandler.obtainMessage(HANDCODE_LOADMORSUCCESS, result));
				}
			}
		});
	}
	//获取最新数据
	private Result refreshGossipMsgData() {
		RequestInfo requestInfo = new RequestInfo(RequestInfo.FLAG_DIRECTION_REFRESH,GossipMsgListAdapter.onePageCount);
		if(m_listData != null && m_listData.size() > 0){
			GossipMsgEntity msg = m_listData.get(0);
			requestInfo.setOffsetfield(msg.getOffsetfield());
			requestInfo.setOffsetid(msg.getOffsetid());
		}
		Result result = null;
		try {
			result = getGossipMsgDataByNet(m_appContext,requestInfo);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mDatahandler.sendMessage(mDatahandler.obtainMessage(HANDCODE_EXCEPTIONCODE, e));
		}
		return result;
	}
	//获取更多数据
	private Result getMoreGossipMsgData() {
		RequestInfo requestInfo = new RequestInfo(RequestInfo.FLAG_DIRECTION_MORE,GossipMsgListAdapter.onePageCount);
		if(m_listData != null && m_listData.size() > 0){
			GossipMsgEntity msg = m_listData.get(m_listData.size()-1);
			requestInfo.setOffsetfield(msg.getOffsetfield());
			requestInfo.setOffsetid(msg.getOffsetid());
		}

		Result result = null;
		try {
			result = getGossipMsgDataByNet(m_appContext,requestInfo);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mDatahandler.sendMessage(mDatahandler.obtainMessage(HANDCODE_EXCEPTIONCODE, e));
		}
		return result;
	}
	public static Result getGossipMsgDataByNet(AppContext appContext,
			RequestInfo requestInfo) throws AppException {

		String url = URLs.COMPANY_RECRUITMENT_HTTP;

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				ObjectUtils.getJsonStringFromObject(requestInfo));
		try {
			return Result.parse(ApiClient._post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}
	
	private void initGossipMsgSuccess(Result result) {
		if (result != null) {
			int state = PullToRefreshFooter.STATE_NORMAL;
			if (result.OK()) {
				String resultStr = result.getJsonStr();
				if (!TextUtils.isEmpty(resultStr)) {
					List<GossipMsgEntity> list = GossipMsgEntity.parse(resultStr);
					m_listData.addAll(0, list);
					if (m_listData.size() > m_listAdapt.totalCount) {
						for (int i = m_listData.size(); i >  m_listAdapt.totalCount; i--) {
							m_listData.remove(i);
						}
					}
					m_listAdapt.notifyDataSetChanged();
					if (m_listData.size() < CompanyListAdapter.onePageCount) {
						state = PullToRefreshFooter.STATE_LOADFULL;
					}
				} else {
					state = PullToRefreshFooter.STATE_NORMAL;
				}
			} else if (result.getErrorCode() == Result.CODE_DATA_EMPTY) {//表示没有数据，这样需要与服务器保持一致 
				state = PullToRefreshFooter.STATE_NULL;
				if (m_listData.size() > 0) {
										
					m_listAdapt.notifyDataSetChanged();
				}
				UIHelper.ToastMessage(m_Context, R.string.industry_insiders_no_data);
			} else {
				result.handleErrcode(m_activity);
			}
			m_listView.onRefreshComplete(state);
		}
	}
	private void moreGossipMsgSuccess(Result result) {
		if (result != null) {
			int state = PullToRefreshFooter.STATE_NORMAL;
			if (result.OK()) {
				String resultStr = result.getJsonStr();
				if (!TextUtils.isEmpty(resultStr)) {
					List<GossipMsgEntity> list = GossipMsgEntity.parse(resultStr);

					m_listData.addAll(list);
					if (m_listData.size() > m_listAdapt.totalCount) {
						for (int i = m_listData.size() - m_listAdapt.totalCount; i >= 0 ; i--) {
							m_listData.remove(i);
						}
					}
					m_listAdapt.notifyDataSetChanged();
					if (list.size() < CompanyListAdapter.onePageCount ) {
						state = PullToRefreshFooter.STATE_LOADFULL;
					}
				}
			} else if (result.getErrorCode() == Result.CODE_DATA_EMPTY) {
				state = PullToRefreshFooter.STATE_LOADFULL;
			} else {
				result.handleErrcode(m_activity);
			}
			m_listView.onLoadMoreComplete(state);
		}
	}
	private Handler mDatahandler = new Handler(){ 
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDCODE_REFRESHSUCCESS: {
				Object obj = msg.obj;
				if (obj != null) {
					if (obj instanceof Result) {
						Result result = (Result)obj;
						initGossipMsgSuccess(result);
					}
				}
				break;
			}
			case HANDCODE_LOADMORSUCCESS: {
				Object obj = msg.obj;
				if (obj != null) {
					if (obj instanceof Result) {
						Result result = (Result)obj;
						moreGossipMsgSuccess(result);
					}
				}
				break;
			}
			case HANDCODE_EXCEPTIONCODE: {
				if (m_listData.size() == 0) {
					m_listView.onRefreshComplete(PullToRefreshFooter.STATE_NULL);
				} else if (m_listData.size() > 0) {
					m_listView.onRefreshComplete(PullToRefreshFooter.STATE_NORMAL);
					m_listView.onLoadMoreComplete(PullToRefreshFooter.STATE_NORMAL);
				} else {
					m_listView.onRefreshComplete(PullToRefreshFooter.STATE_NULL);
					m_listView.onLoadMoreComplete(PullToRefreshFooter.STATE_NORMAL);
				}
				Object obj = msg.obj;
				if (obj != null) {
					if (obj instanceof AppException) {
						AppException exception = (AppException)obj;
						exception.makeToast(m_Context);
					}
				}
				break;
			}
			default:
				break;
			}
		}
	};
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		Log.i(TAG, "setUserVisibleHint  visibility = "+isVisibleToUser);
		isVisibleToUser = isVisibleToUser;
		initData();

	}
	public class GossipMsgListAdapter extends BaseAdapter{
		private static final String TAG = "GossipMsgListAdapter";
		private Context m_Context;
		private AppContext m_appContext; 
		private Activity m_activity;
		/************************************************************/
		private View m_Container;
		private MultiColumnPullToRefreshListView m_listView;
		/************************************************************/
		public final static int onePageCount = 24; //每一页的item数
		public final static int totalCount = onePageCount * 13; //加载的最大项
		private List<GossipMsgEntity> mDataList;
		/************************************************************/
		private BitmapManager m_bmpManager;
		/************************************************************/
		
		public GossipMsgListAdapter(Activity activity, List<GossipMsgEntity> dataList) {
			super();
			// TODO Auto-generated constructor stub
			m_activity = activity;
			m_Context = activity;
			m_appContext = (AppContext) m_activity.getApplication();
			m_bmpManager = new BitmapManager(null);
			mDataList = dataList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDataList != null ? mDataList.size() : 0;
			
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}

package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.CampaignListAdapter;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.CampaignEntity;
import com.qianniu.zhaopin.app.bean.CampaignEntityList;
import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoList;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.DsLog;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.widget.PullToRefreshFooter;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView.OnLoadMoreListener;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView.OnRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageButton;

/**
 * 活动列表activity
 * 
 * @author Administrator
 * 
 */
public class CampaignListActivity extends BaseActivity {
	public static final String TAG = "CampaignListActivity";
	private static final int PAGESIZE = 5;
	private static final int CACHE_MAXIMUM_NUMBER = 50;

	private static final int CODE_OK = 0;
	private static final int CODE_OTHER_ERR = 1;
	private static final int CODE_NET_EXCEPT = 2;
	private static final int CODE_NET_UNCONNECT = 3;

	private Context m_Context;
	private AppContext m_appContext;// 全局Context
	private PullToRefreshListView mlistview;
	private List<CampaignEntity> mlistData = new ArrayList<CampaignEntity>();
	private CampaignListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_Context = this;
		m_appContext = (AppContext) this.getApplicationContext();
		setContentView(R.layout.activity_campaignlist);
		ImageButton btn_back = (ImageButton) findViewById(R.id.titbar_lfet_ibtn);
		btn_back.setOnClickListener(UIHelper.finish(this));
		mlistview = (PullToRefreshListView) this
				.findViewById(R.id.pulllistview);
		mAdapter = new CampaignListAdapter(CampaignListActivity.this, mlistData);
		mlistview.setLoadMoreAdapter(mAdapter);
		mlistview.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				loadCampaignData(mlistData, mdataHandler,
						UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
		mlistview.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				loadCampaignData(mlistData, mdataHandler,
						UIHelper.LISTVIEW_ACTION_SCROLL);
			}
		});
		initLoadListData();
	}

	/**
	 * 初始化数据
	 */
	public void initLoadListData() {
		mlistData.clear();
		loadCampaignData(mlistData, mdataHandler, UIHelper.LISTVIEW_ACTION_INIT);
		mlistview.firstRefreshing();
	}

	/**
	 * 手动刷新数据
	 */
	public void refreshLoadListData() {

		loadCampaignData(mlistData, mdataHandler,
				UIHelper.LISTVIEW_ACTION_REFRESH);
		mlistview.firstRefreshing();
	}

	private void refreshPullListViewState(int action, int loadstatus) {
		switch (action) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH: {
			mlistview.onRefreshComplete(loadstatus);
		}
			break;

		case UIHelper.LISTVIEW_ACTION_SCROLL: {
			mlistview.completeLoadMore(loadstatus);
		}
			break;
		}
	}

	private void refreshPullListViewDataCache(int action,
			List<CampaignEntity> list) {
		switch (action) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH: {
			mlistData.addAll(0, list);
		}
			break;

		case UIHelper.LISTVIEW_ACTION_SCROLL: {
			mlistData.addAll(list);
		}
			break;
		}
		if (mlistData.size() > CACHE_MAXIMUM_NUMBER) {
			for (int i = CACHE_MAXIMUM_NUMBER; i < mlistData.size(); i++) {
				mlistData.remove(i);
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	private Handler mdataHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case CODE_OTHER_ERR:
			case CODE_NET_EXCEPT: {

				refreshPullListViewState(msg.arg1,
						PullToRefreshFooter.STATE_NORMAL);

				AppException e = (AppException) msg.obj;
				if (e != null) {
					e.makeToast(m_Context);
				}
			}
				break;

			case CODE_NET_UNCONNECT:
			case CODE_OK: {
				CampaignEntityList list = (CampaignEntityList) msg.obj;
				Result res = null;
				if (list != null) {
					res = list.getValidate();
				}
				if (list == null || res == null) {
					refreshPullListViewState(msg.arg1,
							PullToRefreshFooter.STATE_NORMAL);
				} else if (!res.OK()) {

					if (res.getErrorCode() == Result.CODE_NOTMORE ) {
						if (msg.arg1 == UIHelper.LISTVIEW_ACTION_SCROLL) {
							refreshPullListViewState(msg.arg1,
									PullToRefreshFooter.STATE_LOADFULL);
						} else {
							refreshPullListViewState(msg.arg1,
									PullToRefreshFooter.STATE_NORMAL);
						}
						refreshPullListViewDataCache(msg.arg1, list.getList());

					} else {
						boolean bflag = res
								.handleErrcode(CampaignListActivity.this);
						if (!bflag) {
							UIHelper.ToastMessage(m_Context,
									R.string.dialog_data_get_err);
						}
						refreshPullListViewState(msg.arg1,
								PullToRefreshFooter.STATE_NORMAL);
						refreshPullListViewDataCache(msg.arg1, list.getList());
					}
				} else {
					refreshPullListViewState(msg.arg1,
							PullToRefreshFooter.STATE_NORMAL);
					refreshPullListViewDataCache(msg.arg1, list.getList());
				}

			}
				break;
			}
		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

/*	private void loadCampaignData(final List<CampaignEntity> datalist,
			final Handler handler, final int action) {

		if (!m_appContext.isNetworkConnected()) {
			UIHelper.ToastMessage(m_Context,
					R.string.app_status_net_disconnected);
		}
		//逻辑：init ：从数据库读取最大的id，通过net增量获取page数量的数据；数据返回无最新数据或者返回数据数量不足page，则从数据库读取数据补足page；新数据存入数据库
		//refresh：从当前数据中取最大的Id，通过net增量获取page数量的数据，新数据存入数据库
		//scrollmore：从当前数据中取最小的id，通过数据库获取page数量的数据，返回数据不足page，则通过net增量获取比当前数据最小ID小的数据，补足page；
		Runnable r = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				long offsetid = 0;
				switch (action) {
				case UIHelper.LISTVIEW_ACTION_INIT: {

					// 从数据库获取最新（ID最大的数据）的id作为读取的offsetid
					CampaignEntityList listmax = CampaignEntityList
							.getCampaignDataFromDb(m_appContext, 0, true, 1, 0);
					if (listmax != null) {
						if (listmax.getList() != null
								&& listmax.getList().size() > 0) {
							CampaignEntity entity = listmax.getList().get(0);
							offsetid = entity.getCpagId();
							
						}
					}

				}
					break;
				case UIHelper.LISTVIEW_ACTION_REFRESH: {
					if (datalist != null) {
						offsetid = datalist.get(0).getCpagId();
					}
				}
					break;
				case UIHelper.LISTVIEW_ACTION_SCROLL: {
					if (datalist != null) {
						int size = datalist.size();
						if (size > 0)
							offsetid = datalist.get(size - 1).getCpagId();
					}
				}
					break;
				}
				String url = URLs.CAMPAIGN_LIST;
				if (action == UIHelper.LISTVIEW_ACTION_INIT
						|| action == UIHelper.LISTVIEW_ACTION_REFRESH) {

					CampaignEntityList list = null;
					Message msg = new Message();
					msg.what = CODE_OK;

					CampaignEntityList listnet = null;
					if (!m_appContext.isNetworkConnected()) {
						msg.what = CODE_NET_UNCONNECT;
					} else {
						try {

							listnet = CampaignEntityList
									.getCampaignDataFromNet(m_appContext, url,
											offsetid, CampaignEntity.NODE_ID,
											PAGESIZE, true);
							list = listnet;
						} catch (AppException e) {
							// TODO Auto-generated catch block
							DsLog.e(TAG, e.toString());

							msg.what = CODE_NET_EXCEPT;
							msg.obj = e;
						}
					}
					int datacount = 0;
					if (listnet != null && listnet.getValidate().OK()) {
						datacount = listnet.getList().size();
					}
					// LISTVIEW_ACTION_INIT :需要从数据库中补足数据
					if (datacount < PAGESIZE
							&& action == UIHelper.LISTVIEW_ACTION_INIT) {
						// 获取最小的id，然后从取比该id小的数据
						// 当返回数据位空或无数据返回的时候，当前offsetid的数据并未取到，故需要offsetid+1,以便能从数据库中取到offsetid对应的数据；
						if (list != null) {
							int size = list.getList().size();
							if (size > 0) {
								offsetid = list.getList().get(size - 1)
										.getCpagId();
							} else {
								offsetid = (offsetid > 0?offsetid += 1:offsetid);
							}
						} else {
							offsetid = (offsetid > 0?offsetid += 1:offsetid);
						}
						CampaignEntityList listdb = CampaignEntityList
								.getCampaignDataFromDb(m_appContext, offsetid,
										true, PAGESIZE - datacount, 0);
						if (list != null) {
							if (listdb != null) {
								listnet.getList().addAll(listdb.getList());
							}
						} else {
							list = listdb;
						}
					}
					if (msg.what != CODE_NET_EXCEPT) {
						msg.obj = list;
					}
					msg.arg1 = action;
					handler.sendMessage(msg);
				} else if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {

					CampaignEntityList list = null;
					Message msg = new Message();
					msg.what = CODE_OK;

					CampaignEntityList listdb = CampaignEntityList
							.getCampaignDataFromDb(m_appContext, offsetid,
									true, PAGESIZE, 0);

					int datacount = 0;
					if (listdb != null && listdb.getValidate().OK()) {
						datacount = listdb.getList().size();
						list = listdb;
						offsetid = listdb.getList().get(datacount - 1)
								.getCpagId();
					}
					if (datacount < PAGESIZE) {

						CampaignEntityList listnet = null;
						if (!m_appContext.isNetworkConnected()) {
							msg.what = CODE_NET_UNCONNECT;
						} else {
							try {

								listnet = CampaignEntityList
										.getCampaignDataFromNet(m_appContext,
												url, offsetid,
												CampaignEntity.NODE_ID,
												PAGESIZE - datacount, false);

								if (list != null) {
									if (listnet != null) {
										list.getList()
												.addAll(listnet.getList());
									}
								} else {
									list = listnet;
								}
							} catch (AppException e) {
								// TODO Auto-generated catch block
								DsLog.e(TAG, e.toString());

								msg.what = CODE_NET_EXCEPT;
								msg.obj = e;
							}
						}
					}

					if (msg.what != CODE_NET_EXCEPT) {
						msg.obj = list;
					}
					msg.arg1 = action;
					handler.sendMessage(msg);

				}
			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(r);
	}*/
	
	private void loadCampaignData(final List<CampaignEntity> datalist,
			final Handler handler, final int action) {

		if (!m_appContext.isNetworkConnected()) {
			UIHelper.ToastMessage(m_Context,
					R.string.app_status_net_disconnected);
		}
		//逻辑：init ：从网络获取最新数据，如果数据返回为空，从数据库加载；
		//refresh：从当前数据中取最大的Id，通过net增量获取page数量的数据，新数据存入数据库
		//scrollmore通过net增量获取比当前数据最小ID小的数据
		Runnable r = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				long offsetid = 0;
				switch (action) {
				case UIHelper.LISTVIEW_ACTION_INIT: {
					offsetid = 0;
				}
					break;
				case UIHelper.LISTVIEW_ACTION_REFRESH: {
					if (datalist != null && datalist.size() > 0) {
						offsetid = datalist.get(0).getCpagId();
					}
				}
					break;
				case UIHelper.LISTVIEW_ACTION_SCROLL: {
					if (datalist != null) {
						int size = datalist.size();
						if (size > 0)
							offsetid = datalist.get(size - 1).getCpagId();
					}
				}
					break;
				}
				String url = URLs.CAMPAIGN_LIST;
				if (action == UIHelper.LISTVIEW_ACTION_INIT
						|| action == UIHelper.LISTVIEW_ACTION_REFRESH) {

					CampaignEntityList list = null;
					Message msg = new Message();
					msg.what = CODE_OK;

					CampaignEntityList listnet = null;
					if (!m_appContext.isNetworkConnected()) {
						msg.what = CODE_NET_UNCONNECT;
					} else {
						try {

							listnet = CampaignEntityList
									.getCampaignDataFromNet(m_appContext, url,
											offsetid, CampaignEntity.NODE_ID,
											PAGESIZE, true);
							list = listnet;
						} catch (AppException e) {
							// TODO Auto-generated catch block
							DsLog.e(TAG, e.toString());

							msg.what = CODE_NET_EXCEPT;
							msg.obj = e;
						}
					}
					int datacount = 0;
					if (listnet != null && listnet.getValidate().OK()) {
						datacount = listnet.getList().size();
					}

					if (datacount == 0
							&& action == UIHelper.LISTVIEW_ACTION_INIT) {
	
						CampaignEntityList listdb = CampaignEntityList
								.getCampaignDataFromDb(m_appContext, offsetid,
										true, PAGESIZE - datacount, 0);
						list = listdb;
					}
					if (msg.what != CODE_NET_EXCEPT) {
						msg.obj = list;
					}
					msg.arg1 = action;
					handler.sendMessage(msg);
				} else if (action == UIHelper.LISTVIEW_ACTION_SCROLL) {

					CampaignEntityList list = null;
					Message msg = new Message();
					msg.what = CODE_OK;

					{

						CampaignEntityList listnet = null;
						if (!m_appContext.isNetworkConnected()) {
							msg.what = CODE_NET_UNCONNECT;
						} else {
							try {

								listnet = CampaignEntityList
										.getCampaignDataFromNet(m_appContext,
												url, offsetid,
												CampaignEntity.NODE_ID,
												PAGESIZE, false);
								list = listnet;
				
							} catch (AppException e) {
								// TODO Auto-generated catch block
								DsLog.e(TAG, e.toString());

								msg.what = CODE_NET_EXCEPT;
								msg.obj = e;
							}
						}
					}

					if (msg.what != CODE_NET_EXCEPT) {
						msg.obj = list;
					}
					msg.arg1 = action;
					handler.sendMessage(msg);

				}
			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(r);
	}

}

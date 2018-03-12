package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.ForumListAdapter;
import com.qianniu.zhaopin.app.adapter.ForumListAdapter.ForumListItemView;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.ForumTypeList;
import com.qianniu.zhaopin.app.bean.ItemInfoList;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.view.AdZoneView;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class Fragment_msg extends BaseFragment implements OnClickListener,
		OnEditorActionListener {
	private static final String TAG = "Fragment_msg";
	public static final int RESULT_LOADDATA_OK = 11;
	public static final int RESULT_LOADDATA_NET_OK = 12;
	public static final int RESULT_LOADDATA_DB_OK = 13;

	private static final int MSG_LOADDATA_APPEXCEPTION = 20;

	public static final int RESULT_LOADDATA_ERROR = 30;
	public static final int RESULT_EXCEPTION = 31;
	public static final int RESULT_EXCEPTION_NET = 32;
	public static final int RESULT_EXCEPTION_DB = 33;
	public static final int RESULT_DBERROR = 34;
	public static final int RESULT_NETWORK_UNCONNECT = 35;

	private Context m_Context;
	private View mContainer;
	private FragmentTabHost mTabHost;
	private PullToRefreshListView lvInfoTypes;
	private ForumListAdapter lvInfoTypeAdapter;
	private Handler lvInfoTypeHandler;
	private int lvInfoTypeSumData;
	private List<ForumType> lvInfoTypesData = new ArrayList<ForumType>();
	private View lvInfoType_footer;
	private TextView lvInfoType_foot_more;
	private ProgressBar lvInfoType_foot_progress;
	private View commonLoadView;
	private AdZoneView adzoneView ;
	
	private AppContext appContext;// 全局Context
	private int curInfoTypeCatalog = ForumTypeList.CATALOG_ASK;

	private ImageView m_btnSet; // 设置按钮
	private ImageView searchButton; // 搜索按钮
	private View searchLayout;
	private EditText searchEt;
	private ImageView searchCleanBt;

	private TextView mtitleTv;

	private ImageView mnewPoint;

	private int status = STATUS_NORMAL;

	private final static int STATUS_NORMAL = 0; // 普通列表状态
	private final static int STATUS_SEARCH = 1; // 搜索列表状态

	private MyLogger logger;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_Context = this.getActivity();
		appContext = (AppContext) this.getActivity().getApplication();

		logger = MyLogger.getLogger("Fragment_msg");
		logger.i("onCreate");
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		logger.i("onCreateView");
		if (mContainer != null) {
			ViewGroup p = (ViewGroup) mContainer.getParent();
			if (p != null) {
				p.removeAllViewsInLayout();
			}
			/*
			 * lvInfoTypes.onRefreshComplete(); lvInfoTypes.setSelection(0);
			 */
			if(adzoneView != null){
				adzoneView.refreshView();
			}
			initLoadListData();
			return mContainer;
		}
		mContainer = (View) inflater.inflate(R.layout.fragment_infotypeslist,
				container, false);// R.layout.fragment4,
		commonLoadView = (View) mContainer
				.findViewById(R.id.commom_loading_layout); // null);//
		m_btnSet = (ImageView) mContainer.findViewById(R.id.fragment_news_set);
		searchButton = (ImageView) mContainer
				.findViewById(R.id.fragment_news_search);
		searchLayout = mContainer
				.findViewById(R.id.fragment_news_search_layout);
		searchEt = (EditText) mContainer
				.findViewById(R.id.fragment_news_search_et);
		searchCleanBt = (ImageView) mContainer
				.findViewById(R.id.fragment_news_search_clean_iv);
		searchLayout.setVisibility(View.GONE);
		m_btnSet.setOnClickListener(this);
		searchButton.setOnClickListener(this);
		searchCleanBt.setOnClickListener(this);
		searchEt.setOnEditorActionListener(this);
		status = STATUS_NORMAL;
		mtitleTv = (TextView) mContainer.findViewById(R.id.fragment_news_title);
		mnewPoint = (ImageView) ((Activity) m_Context)
				.findViewById(R.id.main_tab_news_unread_iv);
		initInfoListView(inflater);
		return (View) mContainer;
	}

	/**
	 * 初始化板块列表
	 */
	private void initInfoListView(LayoutInflater inflater) {
		lvInfoTypeAdapter = new ForumListAdapter(this.getActivity(),
				lvInfoTypesData);
		lvInfoType_footer = inflater.inflate(R.layout.listview_footer, null);
		lvInfoType_foot_more = (TextView) lvInfoType_footer
				.findViewById(R.id.listview_foot_more);
		lvInfoType_foot_progress = (ProgressBar) lvInfoType_footer
				.findViewById(R.id.listview_foot_progress);
		lvInfoType_foot_progress.setVisibility(View.GONE);
		lvInfoType_footer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int lvDataState = StringUtils.toInt(lvInfoTypes.getTag());
				if (lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvInfoTypes.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvInfoType_foot_more.setText(R.string.load_ing);
					lvInfoType_foot_progress.setVisibility(View.VISIBLE);
					loadLvInfoTypesData(lvInfoTypeHandler,
							UIHelper.LISTVIEW_ACTION_REFRESH, false);
				}
			}
		});
		lvInfoTypes = (PullToRefreshListView) mContainer
				.findViewById(R.id.fragment_listview_news);
		ViewGroup headview = (ViewGroup) inflater.inflate(
				R.layout.banner_layout, null);
		adzoneView = new AdZoneView(getActivity(),
				AdZoneView.ADZONE_ID_INFO);

		headview.addView(adzoneView);
		lvInfoTypes.addHeaderView(headview);
		lvInfoTypes.addFooterView(lvInfoType_footer);
		lvInfoTypes.setAdapter(lvInfoTypeAdapter);
		// view 都初始化完毕，
		lvInfoTypeHandler = this.getLvHandler(lvInfoTypes, lvInfoTypeAdapter,
				lvInfoType_foot_more, lvInfoType_foot_progress,
				AppContext.PAGE_SIZE);

		lvInfoTypes
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// 点击头部、底部栏无效
						if (position == 0)
							return;
						if (view == null)
							return;
						ForumListItemView info = (ForumListItemView) view
								.getTag();
						if (info == null || info.infotype == null)
							return;
						int newcount = info.infotype.getNewCount();
						if (info.infotype.getInfoId() != null
								&& info.infotype.getNewCount() > 0) {
							info.infotype.setNewCount(0);
							//新方案不用更新数据库未读数据
							//updateUreadcountInDb(info.infotype.getInfoId(), 0);
							lvInfoTypeAdapter.notifyDataSetChanged();
							refreshUreadMsgCount();
						}

						UIHelper.showInfolist(view.getContext(),
								info.infotype.getInfoId(),
								info.infotype.getTitle(),
								info.infotype.getCutomType(), newcount);
					}
				});
		lvInfoTypes.setOnScrollListener(new AbsListView.OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvInfoTypes.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvInfoTypesData.isEmpty())
					return;

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lvInfoTypes.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});

		lvInfoTypes
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {

						lvInfoTypes.setTag(UIHelper.LISTVIEW_DATA_LOADING);
						//lvInfoType_foot_more.setText(R.string.load_ing);
						if (status == STATUS_NORMAL) {
							loadLvInfoTypesData(lvInfoTypeHandler,
									UIHelper.LISTVIEW_ACTION_REFRESH, false);
						} else {
							String str = searchEt.getText().toString();
							searchLvInfoTypesData(lvInfoTypeHandler, str,
									UIHelper.LISTVIEW_ACTION_REFRESH);
						}
					}
				});
		initLoadListData();
	}

	public void initLoadListData() {
		
		loadLvInfoTypesData(lvInfoTypeHandler, UIHelper.LISTVIEW_ACTION_INIT,
				true);
		lvInfoTypes.firstRefreshing();
		lvInfoTypes.setTag(UIHelper.LISTVIEW_DATA_LOADING);
/*		lvInfoType_foot_more.setText(R.string.load_ing);
		lvInfoType_foot_progress.setVisibility(View.VISIBLE);*/
		// commonLoadView.setVisibility(View.VISIBLE);
	}

	/**
	 * listview数据处理
	 * 
	 * @param what
	 *            数量
	 * @param obj
	 *            数据
	 * @param objtype
	 *            数据类型
	 * @param actiontype
	 *            操作类型
	 * @return notice 通知信息
	 */
	private void handleLvData(int what, Object obj, int objtype, int actiontype) {

		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
			// commonLoadView.setVisibility(View.GONE);
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newdata = 0;// 新加载数据-只有刷新动作才会使用到
			switch (objtype) {
			case UIHelper.LISTVIEW_DATATYPE_FORUMTYPES:
				ForumTypeList nlist = (ForumTypeList) obj;
				lvInfoTypesData.clear();// 先清除原有数据
				lvInfoTypesData.addAll(nlist.getInfoTypelist());

				break;
			}
		}

	}

	/**
	 * 获取listview的初始化Handler
	 * 
	 * @param lv
	 * @param adapter
	 * @return
	 */

	private Handler getLvHandler(final PullToRefreshListView lv,
			final BaseAdapter adapter, final TextView more,
			final ProgressBar progress, final int pageSize) {
		return new Handler() {
			private PullToRefreshListView mlistV = lv;
			private BaseAdapter mLvAdapt = adapter;
			private TextView mMore = more;
			private ProgressBar mprogress = progress;
			private int mMaxpageSize = pageSize;

			public void handleMessage(Message msg) {
				boolean brefresh = false;
				switch (msg.what) {
				case RESULT_LOADDATA_OK: {
					lvInfoTypes.setLoadingState(false);
					ForumTypeList nlist = (ForumTypeList) msg.obj;
					Result res = null;
					if (nlist != null) {
						res = nlist.getValidate();
					}
					if (nlist == null || res == null) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						mMore.setText(R.string.load_error);
					} else if (!res.OK()) {
						if (res.getErrorCode() == Result.CODE_NOTMORE) {
							lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
							adapter.notifyDataSetChanged();
							more.setText(R.string.load_full);
						} else {
							boolean bflag = res.handleErrcode(getActivity());
							if (!bflag) {
								UIHelper.ToastMessage(m_Context,
										R.string.dialog_data_get_err);
							}
							lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
							mMore.setText(R.string.load_error);
						}
					} else {

						handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
						mLvAdapt.notifyDataSetChanged();
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						mMore.setText(R.string.load_full);
					}
					/*
					 * handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
					 * mLvAdapt.notifyDataSetChanged();
					 * lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
					 * mMore.setText(R.string.load_full);
					 */
					refreshUreadMsgCount();
					brefresh = true;

				}
					break;
				case MSG_LOADDATA_APPEXCEPTION: {
					AppException e = (AppException) msg.obj;
					if (e != null) {
						e.makeToast(m_Context);
					}
				}
					break;
				case RESULT_LOADDATA_ERROR:
				case RESULT_EXCEPTION: {
					lvInfoTypes.setLoadingState(false);
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					mMore.setText(R.string.load_error);
					brefresh = true;
				}
					break;
				case RESULT_NETWORK_UNCONNECT: {
					UIHelper.ToastMessage(m_Context,
							R.string.network_not_connected);
				}
					break;
				}
				if (brefresh) {
					if (adapter.getCount() == 0) {
						lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
						mMore.setText(R.string.load_empty);

					}

					progress.setVisibility(ProgressBar.GONE);
					if (adapter.getCount() == 0) {
						lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
						mMore.setText(R.string.load_empty);

					}

					if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH||msg.arg1 == UIHelper.LISTVIEW_ACTION_INIT) {
						mlistV.onRefreshComplete();
						mlistV.setSelection(0);
					} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
						mlistV.onRefreshComplete();
						mlistV.setSelection(0);
					}
				}
				/*
				 * if (msg.what >= 0) { handleLvData(msg.what, msg.obj,
				 * msg.arg2, msg.arg1); mLvAdapt.notifyDataSetChanged();
				 * lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
				 * mMore.setText(R.string.load_full);
				 * 
				 * } else if (msg.what == -1) { // 有异常--显示加载出错 & 弹出错误消息
				 * lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
				 * mMore.setText(R.string.load_error);
				 * 
				 * ((AppException) msg.obj).makeToast(mcontext); }
				 * 
				 * if (adapter.getCount() == 0) {
				 * lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
				 * mMore.setText(R.string.load_empty);
				 * 
				 * }
				 * 
				 * progress.setVisibility(ProgressBar.GONE); if
				 * (adapter.getCount() == 0) {
				 * lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
				 * mMore.setText(R.string.load_empty);
				 * 
				 * } if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
				 * mlistV.onRefreshComplete(); mlistV.setSelection(0); } else if
				 * (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
				 * mlistV.onRefreshComplete(); mlistV.setSelection(0); }
				 */

			}
		};
	}

	/**
	 * 线程加载信息类型数据
	 * 
	 * @param catalog
	 *            分类
	 * @param pageIndex
	 *            当前页数
	 * @param handler
	 *            处理器
	 * @param action
	 *            动作标识
	 */
	private void loadLvInfoTypesData(final Handler handler, final int action,
			final boolean isFirst) {
		lvInfoTypes.setLoadingState(true);
		Thread t = new Thread() {
			public void run() {
				Message msg = new Message();
				boolean bexp = false;
				boolean isRefresh = false;
				ForumTypeList list = null;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;

				try {
					if (!appContext.isNetworkConnected()) {
						Message msgnet = new Message();
						msgnet.what = RESULT_NETWORK_UNCONNECT;
						handler.sendMessage(msgnet);
					} else {
						list = appContext.getInfoTypesListFromNet(isRefresh,
								isFirst, DBUtils.CATALOG_TYPE_INFO_CATALOG, 0);
					}
				} catch (AppException e) {
					msg = handler.obtainMessage();
					// TODO Auto-generated catch block
					msg.what = RESULT_EXCEPTION_NET;
					msg.obj = e;
					MyLogger.i(TAG, "loadLvInfoTypesData FromNet error = " + e);
					Message exception_msg = new Message();
					exception_msg.what = MSG_LOADDATA_APPEXCEPTION;
					exception_msg.obj = e;
					handler.sendMessage(exception_msg);
					bexp = true;
				}
				if (isFirst) {
					if (list == null || bexp) {
						try {
							list = appContext.getInfoTypesListFromDb(
									DBUtils.CATALOG_TYPE_INFO_CATALOG, 0,appContext.isLogin());
							bexp = false;
						} catch (AppException e) {
							msg = handler.obtainMessage();
							// TODO Auto-generated catch block
							msg.what = RESULT_EXCEPTION_DB;
							msg.obj = e;
							MyLogger.i(TAG,
									"loadLvInfoTypesData FromDb error = " + e);
							bexp = true;
						}
					}
				}
				if (bexp) {
					msg = handler.obtainMessage();
					msg.what = RESULT_EXCEPTION;
				} else if (list != null) {
					msg = handler.obtainMessage();
					msg.what = RESULT_LOADDATA_OK;
					msg.obj = list;
				} else {
					msg = handler.obtainMessage();
					msg.what = RESULT_LOADDATA_ERROR;
				}
				/*
				 * try { //消息模块，不需要栏目id； ForumTypeList list =
				 * appContext.getInfoTypesList(isRefresh,
				 * isFirst,DBUtils.CATALOG_TYPE_INFO_CATALOG,0); msg.what = 1;
				 * msg.obj = list; } catch (AppException e) {
				 * e.printStackTrace(); msg.what = -1; msg.obj = e; }
				 */
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_FORUMTYPES;
				handler.sendMessage(msg);
			}
		};
		
		if(threadPool == null){
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	/**
	 * 线程从数据库加载信息类型数据
	 * 
	 * @param catalog
	 *            分类
	 * @param pageIndex
	 *            当前页数
	 * @param handler
	 *            处理器
	 * @param action
	 *            动作标识
	 */
	private void loadLvInfoTypesDataFromdb(final Handler handler,
			final int action, final boolean isFirst) {
		lvInfoTypes.setLoadingState(true);
		Thread t = new Thread() {
			public void run() {
				Message msg = new Message();
				boolean bexp = false;
				boolean isRefresh = false;
				ForumTypeList list = null;
				
				try {
					list = appContext.getInfoTypesListFromDb(
							DBUtils.CATALOG_TYPE_INFO_CATALOG, 0,appContext.isLogin());
					bexp = false;
				} catch (AppException e) {
					msg = handler.obtainMessage();
					// TODO Auto-generated catch block
					msg.what = RESULT_EXCEPTION_DB;
					msg.obj = e;
					MyLogger.i(TAG,
							"loadLvInfoTypesData FromDb error = " + e);
					bexp = true;
				}

				if (bexp) {
					msg = handler.obtainMessage();
					msg.what = RESULT_EXCEPTION;
				} else if (list != null) {
					msg = handler.obtainMessage();
					msg.what = RESULT_LOADDATA_OK;
					msg.obj = list;
				} else {
					msg = handler.obtainMessage();
					msg.what = RESULT_LOADDATA_ERROR;
				}
				/*
				 * try { //消息模块，不需要栏目id； ForumTypeList list =
				 * appContext.getInfoTypesList(isRefresh,
				 * isFirst,DBUtils.CATALOG_TYPE_INFO_CATALOG,0); msg.what = 1;
				 * msg.obj = list; } catch (AppException e) {
				 * e.printStackTrace(); msg.what = -1; msg.obj = e; }
				 */
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_FORUMTYPES;
				handler.sendMessage(msg);
			}
		};
		
		if(threadPool == null){
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	private void searchLvInfoTypesData(final Handler handler,
			final String keyString, final int action) {

		Thread t = new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					String[] args = StringUtils.parseKeyWords(keyString);
					ForumTypeList list = appContext.serachInfoTypesListFromDb(
							DBUtils.CATALOG_TYPE_INFO_CATALOG, 0, args,appContext.isLogin());
					msg.what = RESULT_LOADDATA_OK;
					msg.obj = list;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = RESULT_EXCEPTION;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_FORUMTYPES;
				handler.sendMessage(msg);
			}
		};
		if(threadPool == null){
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int vid = v.getId();
		switch (vid) {
		case R.id.fragment_news_search:{
			// 友盟统计--消息--搜索按钮
/*			UmShare.UmStatistics(m_Context, "Msg_SearchButton");

			if (status == STATUS_NORMAL) {
				searchLayout.setVisibility(View.VISIBLE);
				status = STATUS_SEARCH;
			} else {
				searchLayout.setVisibility(View.GONE);
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getActivity()
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				status = STATUS_NORMAL;
				
				loadLvInfoTypesDataFromdb(lvInfoTypeHandler,
						UIHelper.LISTVIEW_ACTION_INIT, true);
			}*/

			UIHelper.showSubscriptionActivityBehindCheck(getActivity());
		}
			break;
		case R.id.fragment_news_search_clean_iv:
			searchEt.setText(null);
			break;
		case R.id.fragment_news_set: {
			// 设置界面
			UIHelper.showSetActivity(m_Context);
		}
			break;

		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		// logger.i("onEditorAction actionId =" + actionId);
		if (actionId == EditorInfo.IME_ACTION_DONE
				|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED
				|| actionId == EditorInfo.IME_ACTION_GO
				|| actionId == EditorInfo.IME_ACTION_SEARCH
				|| actionId == EditorInfo.IME_ACTION_SEND) {
			lvInfoTypes.setTag(UIHelper.LISTVIEW_DATA_LOADING);
			lvInfoType_foot_more.setText(R.string.load_ing);
			String str = searchEt.getText().toString();
			searchLvInfoTypesData(lvInfoTypeHandler, str,
					UIHelper.LISTVIEW_ACTION_REFRESH);
			InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getActivity()
					.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		return false;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		logger.i("onPause");
		// 友盟统计
		UmShare.UmPause(m_Context);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		logger.i("onResume");
		// 友盟统计
		UmShare.UmResume(m_Context);
	}

	/*
	 * @Override public void onActivityCreated(Bundle savedInstanceState) { //
	 * TODO Auto-generated method stub
	 * super.onActivityCreated(savedInstanceState);
	 * logger.i("onActivityCreated"); }
	 * 
	 * @Override public void onAttach(Activity activity) { // TODO
	 * Auto-generated method stub super.onAttach(activity);
	 * //logger.i("onAttach"); }
	 * 
	 * @Override public void onDestroy() { // TODO Auto-generated method stub
	 * super.onDestroy(); logger.i("onDestroy"); }
	 * 
	 * @Override public void onDestroyView() { // TODO Auto-generated method
	 * stub super.onDestroyView(); logger.i("onDestroyView"); }
	 * 
	 * @Override public void onStart() { // TODO Auto-generated method stub
	 * super.onStart(); logger.i("onStart"); }
	 * 
	 * @Override public void onStop() { // TODO Auto-generated method stub
	 * super.onStop(); logger.i("onStop"); }
	 */
	private void updateUreadcountInDb(final String catid, final int newcount) {
		Thread t =	new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				DBUtils db = DBUtils.getInstance(appContext);
				String sql = DBUtils.KEY_CAT_ID + " = " + catid;
				sql += " AND " + DBUtils.KEY_ROWDATA_TYPE + " = "
						+ DBUtils.CATALOG_TYPE_INFO_CATALOG;
				Cursor cursor = db
						.queryBindUser(true, DBUtils.catalogTableName,
								new String[] { "*" }, sql, null, null, null,
								null, null);
				if (cursor != null) {
					if (cursor.getCount() > 0) {
						ContentValues values = new ContentValues();
						values.put(DBUtils.KEY_UNREAD_COUNT,
								String.valueOf(newcount));
						boolean state = db.updateBindUser(DBUtils.catalogTableName,
								sql, values);
					}

					cursor.close();
				}
			}

		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != -1)
			return;
		switch (requestCode) {
		case ActivityRequestCode.RESULT_ACTIVITY_LOGIN: {
			initLoadListData();
		}
			break;
		}
	}

	private void refreshUreadMsgCount() {
		int newtotal = 0;
		for (int i = 0; i < lvInfoTypesData.size(); i++) {
			ForumType entity = lvInfoTypesData.get(i);
			newtotal += entity.getNewCount();
			String title = entity.getTitle();
			MyLogger.i(TAG, "new msg count = " + newtotal + ";title = " + title);
		}
		MyLogger.i(TAG, "new msg count total= " + newtotal);
		String title = getString(R.string.app_info_title_news);
		if (newtotal > 0)
			mtitleTv.setText(title + "(" + newtotal + ")");
		else
			mtitleTv.setText(title);

		if (newtotal > 0) {
			if (mnewPoint != null)
				mnewPoint.setVisibility(View.VISIBLE);
		} else {
			if (mnewPoint != null)
				mnewPoint.setVisibility(View.INVISIBLE);
		}
	}
}

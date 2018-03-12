package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.ForumInfoListAdapt;
import com.qianniu.zhaopin.app.adapter.ForumListAdapter;
import com.qianniu.zhaopin.app.adapter.ForumInfoListAdapt.ForumInfoListItemView;
import com.qianniu.zhaopin.app.adapter.ForumListAdapter.ForumListItemView;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.ForumTypeList;
import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoList;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.SubscriptionDataEntiy;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class InfoListActivity extends BaseActivity {
	private static final String TAG = "InfoListActivity";

	private static final int MSG_LOADDATA_APPEXCEPTION = 22;
	private static final int HANDCODE_SUMBIT_OK = 1;
	private static final int HANDCODE_SUMBIT_ERR = 2;
	/**
	 * 数据最多缓存的数量
	 */
	private static final int CACHE_MAXIMUM_NUMBER = 50;
	// 显示信息版块消息列表
	public static final int DISPLAY_TYPE_INFO = 1;
	// 显示号外版块消息列表
	public static final int DISPLAY_TYPE_EXTRA = 2;

	private Context mcontext;
	private int displayType = 1;
	private String mforumid;
	private String mtitle;
	private int customState;

	private ImageButton btn_back;
	private TextView tv_title;
	private ImageView tv_rightbt;
	private PullToRefreshListView lv_info;

	private ForumInfoListAdapt lvAdapter;
	private Handler lvHandler;
	private int lvSumData;
	private ArrayList<ItemInfoEntity> lvData = new ArrayList<ItemInfoEntity>();
	private View lv_footer;
	private TextView lv_foot_more;
	private ProgressBar lv_foot_progress;
	private AppContext appContext;// 全局Context
	private int newcount = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		appContext = (AppContext) this.getApplication();
		setContentView(R.layout.infolist_activity);
		Intent intent = getIntent();
		displayType = intent.getIntExtra("type", 1);
		mforumid = getIntent().getStringExtra("info_id");
		customState = intent.getIntExtra("custom_state", 0);
		newcount = intent.getIntExtra("newcount", -1);
		initView();

	}

	private Handler customhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == HANDCODE_SUMBIT_OK) {
				// if (customState == SubscriptionDataEntiy.SUBSTYPE_DEFAULT
				// ForumType.CUSTOM_TYPE_SELECTED
				// || customState == ForumType.CUSTOM_TYPE_DEFAULT)
				if (customState == SubscriptionDataEntiy.SUBSTYPE_DEFAULT
						|| customState == SubscriptionDataEntiy.SUBSTYPE_DONE) {
					customState = ForumType.CUSTOM_TYPE_UNSELECTED;
					// tv_rightbt.setText(R.string.app_catalog_customselect);
					tv_rightbt
							.setImageResource(R.drawable.common_button_subscription_add);

					UIHelper.ToastMessage(
							mcontext,
							getResources().getString(
									R.string.app_catalog_customcancel_success));
				} else if (customState == SubscriptionDataEntiy.SUBSTYPE_CANCEL)
				// (customState == ForumType.CUSTOM_TYPE_UNSELECTED)
				{
					customState = ForumType.CUSTOM_TYPE_SELECTED;
					// tv_rightbt.setText(R.string.app_catalog_customcancel);
					tv_rightbt
							.setImageResource(R.drawable.common_button_subscription_cancel);
					UIHelper.ToastMessage(
							mcontext,
							getResources().getString(
									R.string.app_catalog_customselect_success));
				}
				/*
				 * UIHelper.ToastMessage( mcontext, getResources().getString(
				 * R.string.app_catalog_customchange_success));
				 */
			} else {

				if (customState == ForumType.CUSTOM_TYPE_SELECTED
						|| customState == ForumType.CUSTOM_TYPE_DEFAULT) {

					UIHelper.ToastMessage(
							mcontext,
							getResources().getString(
									R.string.app_catalog_customcancel_fail));
				} else if (customState == ForumType.CUSTOM_TYPE_UNSELECTED) {

					UIHelper.ToastMessage(
							mcontext,
							getResources().getString(
									R.string.app_catalog_customselect_fail));
				}
				/*
				 * UIHelper.ToastMessage( mcontext, getResources().getString(
				 * R.string.app_catalog_customchange_fail));
				 */
			}
		}

	};
	private View.OnClickListener clicklistener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.infolist_rightbt:
				boolean blogin = appContext
						.isLoginAndToLogin(InfoListActivity.this);
				if (blogin) {
					// if (customState == ForumType.CUSTOM_TYPE_SELECTED
					// || customState == ForumType.CUSTOM_TYPE_DEFAULT) {
					// appContext.reportSingleCatalogManagerByThread(
					// appContext, customhandler, mforumid, false);
					//
					// } else {
					//
					// appContext.reportSingleCatalogManagerByThread(
					// appContext, customhandler, mforumid, true);
					// }
					if (customState == SubscriptionDataEntiy.SUBSTYPE_DONE) {
						reportSubscription(appContext, customhandler, mforumid, false);

					} else {
						reportSubscription(appContext, customhandler, mforumid, true);
					}

				}
				break;
			}
		}
	};

	private void reportSubscription(final AppContext appContext,
			final Handler handler, final String id, final boolean bSelected) {
		if (!UIHelper.isNetworkConnected((AppContext) mContext)) {
			return;
		}
		showProgressDialog();
		ThreadPoolController.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				Result result = null;
				try {
					String status;
					if (bSelected)
						status = "1";
					else
						status = "2";
					result = ApiClient.submitRSS(appContext, mforumid, status,
							URLs.RSS_TYPEPOST);
				} catch (AppException e) {
					result = new Result(Result.CODE_EXCEPTION, e.toString());
					handler.sendMessage(handler.obtainMessage(
							HANDCODE_SUMBIT_ERR, result));
				}
				if (result != null) {
					handler.sendMessage(handler.obtainMessage(
							HANDCODE_SUMBIT_OK, result));
				}
				dismissProgressDialog();
			}
		});
	}

	private void initView() {

		btn_back = (ImageButton) findViewById(R.id.infolist_goback);
		btn_back.setOnClickListener(UIHelper.finish(this));
		tv_title = (TextView) findViewById(R.id.infolist_title);
		tv_rightbt = (ImageView) findViewById(R.id.infolist_rightbt);

		if (customState == ForumType.CUSTOM_TYPE_DEFAULT) {
			// tv_rightbt.setText("");
			tv_rightbt.setVisibility(View.INVISIBLE);
		} else if (customState == ForumType.CUSTOM_TYPE_SELECTED
				|| customState == ForumType.CUSTOM_TYPE_DEFAULT) {
			// tv_rightbt.setText(R.string.app_catalog_customcancel);
			tv_rightbt.setVisibility(View.VISIBLE);
			tv_rightbt
					.setImageResource(R.drawable.common_button_subscription_cancel);
		} else {
			// tv_rightbt.setText(R.string.app_catalog_customselect);
			tv_rightbt.setVisibility(View.VISIBLE);
			tv_rightbt
					.setImageResource(R.drawable.common_button_subscription_add);
		}
		tv_rightbt.setOnClickListener(clicklistener);

		lv_info = (PullToRefreshListView) findViewById(R.id.infolist_listview);
		mtitle = getIntent().getStringExtra("info_title");
		tv_title.setText(mtitle);
		lvAdapter = new ForumInfoListAdapt(this, lvData);
		lv_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lv_foot_more = (TextView) lv_footer
				.findViewById(R.id.listview_foot_more);
		lv_foot_progress = (ProgressBar) lv_footer
				.findViewById(R.id.listview_foot_progress);
		lv_foot_progress.setVisibility(View.GONE);
		lv_footer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int lvDataState = StringUtils.toInt(lv_info.getTag());
				if (lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lv_info.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lv_foot_more.setText(R.string.load_ing);
					lv_foot_progress.setVisibility(View.VISIBLE);
					loadLvForumInfoData(mforumid, lvData, lvHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
		});
		lv_info.addFooterView(lv_footer);// 添加底部视图 必须在setAdapter前
		lv_info.setAdapter(lvAdapter);
		// view 都初始化完毕，
		lvHandler = this.getLvHandler(lv_info, lvAdapter, lv_foot_more,
				lv_foot_progress, AppContext.PAGE_SIZE);

		lv_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击头部、底部栏无效
				if (position == 0 || view == lv_footer)
					return;
				ForumInfoListItemView info = (ForumInfoListItemView) view
						.getTag();
				if (info == null || info.foruminfo == null)
					return;
				/*
				 * InfoEntity foruminfo = info.foruminfo;
				 * UIHelper.showInfoDetial(view.getContext(),
				 * foruminfo.getInfoId(), foruminfo.getInfoTitle(),
				 * foruminfo.getInfoUrl());
				 */

			}
		});
		lv_info.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lv_info.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (lvData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lv_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(lv_info.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lv_info.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lv_foot_more.setText(R.string.load_ing);
					lv_foot_progress.setVisibility(View.VISIBLE);
					loadLvForumInfoData(mforumid, lvData, lvHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lv_info.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
		lv_info.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				lv_info.setTag(UIHelper.LISTVIEW_DATA_LOADING);
				loadLvForumInfoData(mforumid, lvData, lvHandler,
						UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
		initLoadListData();
	}

	public void initLoadListData() {
		lvData.clear();
		loadLvForumInfoData(mforumid, lvData, lvHandler,
				UIHelper.LISTVIEW_ACTION_INIT);
		lv_info.firstRefreshing();
		lv_info.setTag(UIHelper.LISTVIEW_DATA_LOADING);
		/*
		 * lv_foot_more.setText(R.string.load_ing);
		 * lv_foot_progress.setVisibility(View.VISIBLE);
		 */
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
				if (msg.what == 1) {
					ItemInfoList nlist = (ItemInfoList) msg.obj;
					Result res = null;
					if (nlist != null) {
						res = nlist.getValidate();
					}
					if (nlist == null || res == null) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						mMore.setText(R.string.load_error);
					} else if (!res.OK()) {
						if (res.getErrorCode() == Result.CODE_NOTMORE) {
							if (msg.arg1 == UIHelper.LISTVIEW_ACTION_SCROLL) {
								lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
							} else {
								lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
							}
							lvData.addAll(0, nlist.getItemInfolist());
							adapter.notifyDataSetChanged();
							more.setText(R.string.load_full);
						} else {
							boolean bflag = res
									.handleErrcode(InfoListActivity.this);
							if (!bflag) {
								UIHelper.ToastMessage(mcontext,
										R.string.dialog_data_get_err);
							}
							lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
							MyLogger.i(TAG, res.getErrorMessage());
							mMore.setText(R.string.load_error);
						}
					} else {
						switch (msg.arg1) {
						case UIHelper.LISTVIEW_ACTION_INIT:
						case UIHelper.LISTVIEW_ACTION_REFRESH:
						case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG: {
							List<ItemInfoEntity> iteminfolist = nlist
									.getItemInfolist();
							if (lvData.size() > 0 && iteminfolist.size() > 10) {
								long receivetime = lvData.get(0)
										.getRecevietime();
								if (System.currentTimeMillis() - receivetime > 1
										* 24 * 60 * 60 * 1000) {
									lvData.clear();
								}

								/*
								 * List<InfoEntity> listinfo = lvData.get(0)
								 * .getInfoEntitylist(); if (listinfo != null &&
								 * listinfo.size() > 0) { String timestr =
								 * listinfo.get(0) .getReviveTimestamp(); if
								 * (timestr != null) { long receivetime =
								 * StringUtils .toLong(timestr); if
								 * (System.currentTimeMillis() - receivetime > 2
								 * * 24 * 60 60 * 1000) { lvData.clear(); } } }
								 */
							}

							lvData.addAll(0, nlist.getItemInfolist());
							if (lvData.size() > CACHE_MAXIMUM_NUMBER) {
								for (int i = CACHE_MAXIMUM_NUMBER; i < lvData
										.size(); i++) {
									lvData.remove(i);
								}
							}
							int sizelv = lvData.size();
							lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
							mLvAdapt.notifyDataSetChanged();
							more.setText(R.string.load_more);
						}
							break;
						case UIHelper.LISTVIEW_ACTION_SCROLL: {
							if (nlist.getItemInfolist().size() > 0) {
								lvData.addAll(nlist.getItemInfolist());
							}
							if (lvData.size() > CACHE_MAXIMUM_NUMBER) {
								for (int i = 0; i < (lvData.size() - CACHE_MAXIMUM_NUMBER); i++) {
									lvData.remove(i);
								}
							}
							lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
							adapter.notifyDataSetChanged();
							more.setText(R.string.load_more);
						}
							break;
						}
					}
					lv.setLoadingState(false);
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					mMore.setText(R.string.load_error);
					lv.setLoadingState(false);
					// ((AppException) msg.obj).makeToast(mcontext);
				} else if (msg.what == MSG_LOADDATA_APPEXCEPTION) {

					AppException e = (AppException) msg.obj;
					if (e != null) {
						e.makeToast(mcontext);
					}

				}
				if (adapter.getCount() == 0) {
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					mMore.setText(R.string.load_empty);

				}

				progress.setVisibility(ProgressBar.GONE);

				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH
						|| msg.arg1 == UIHelper.LISTVIEW_ACTION_INIT) {
					mlistV.onRefreshComplete();
					mlistV.setSelection(0);
				} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
					mlistV.onRefreshComplete();
					mlistV.setSelection(0);
				}

			}
		};
	}

	/**
	 * 线程加载数据
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
	private void loadLvForumInfoData(final String forumid,
			final List<ItemInfoEntity> datalist, final Handler handler,
			final int action) {
		if (!appContext.isNetworkConnected()) {
			UIHelper.ToastMessage(mcontext,
					R.string.app_status_net_disconnected);
		}
		lv_info.setLoadingState(true);
		Thread t = new Thread() {
			public void run() {
				ItemInfoList list = null;
				Message msg = new Message();
				boolean isRefresh = false;
				String offsetid = "";
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_INIT)
					isRefresh = true;
				if (datalist != null && datalist.size() > 0) {
					if (isRefresh)
						offsetid = ((ItemInfoEntity) datalist.get(0)).getFid();
					/* .getInfoEntitylist().get(0).getInfoId(); */

					else
						offsetid = ((ItemInfoEntity) datalist.get(datalist
								.size() - 1)).getFid();/*
														 * .getInfoEntitylist().get
														 * (0) .getInfoId();
														 */
				}
				String url = new String();
				int rowtype = DBUtils.INFO_TYPE_DEFAULT;
				switch (displayType) {
				case DISPLAY_TYPE_INFO:
					url = URLs.FORUMINFO_LIST + "/" + forumid;
					rowtype = DBUtils.INFO_TYPE_DEFAULT;
					break;
				case DISPLAY_TYPE_EXTRA:
					url = URLs.EXTRAINFO_LIST + "/" + forumid;
					rowtype = DBUtils.INFO_TYPE_DEFAULT;
					break;
				default:
					url = URLs.FORUMINFO_LIST + "/" + forumid;
					rowtype = DBUtils.INFO_TYPE_DEFAULT;
					break;
				}
				// 如果知道新数据为0，则从数据库读取数据；读取失败，再从网络获取
				if (newcount == 0 && action == UIHelper.LISTVIEW_ACTION_INIT) {

					try {
						list = appContext.getItemInfoListFromDb(appContext,
								forumid, rowtype);
					} catch (AppException e) {
						// TODO Auto-generated catch block
						msg.what = -1;
						msg.obj = e;
					}

					if (list == null) {
						try {

							list = appContext.getItemInfoListListFromNet(
									forumid, url, offsetid, isRefresh, rowtype);

						} catch (AppException e) {
							e.printStackTrace();
							msg.what = -1;
							msg.obj = e;
						}
					}

				} else {
					ItemInfoList olddblist = null;
					try {
						olddblist = appContext.getItemInfoListFromDb(
								appContext, forumid, rowtype);

					} catch (AppException e) {
						// TODO Auto-generated catch block
						msg.what = -1;
						msg.obj = e;
						Message exception_msg = new Message();
						exception_msg.what = MSG_LOADDATA_APPEXCEPTION;
						exception_msg.obj = e;
						handler.sendMessage(exception_msg);
					}
					// 从网络获取数据，是差量获取，如果没设置差量Id，则会从数据库获取最大的id；
					try {

						list = appContext.getItemInfoListListFromNet(forumid,
								url, offsetid, isRefresh, rowtype);

					} catch (AppException e) {
						e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
					}
					// 如果是初始化数据，由于是增量获取，得将数据库的数据追加到网络数据尾部
					if (action == UIHelper.LISTVIEW_ACTION_INIT) {
						{

							if (olddblist != null
									&& olddblist.getItemInfolist() != null) {
								if (list != null
										&& list.getItemInfolist() != null) {
									list.getItemInfolist().addAll(
											olddblist.getItemInfolist());
								} else {
									list = olddblist;
								}
							}

						}
					}

				}
				if (list != null) {
					msg.what = 1;
					msg.obj = list;
				} else {
					msg.what = -1;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_FORUMINFO;
				handler.sendMessage(msg);

			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case ActivityRequestCode.RESULT_ACTIVITY_LOGIN: {
			initLoadListData();
		}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.onDestroy();
		}
		return super.onKeyDown(keyCode, event);
	}

}

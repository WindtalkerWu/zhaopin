package com.qianniu.zhaopin.app.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.ExtraHomeListAdapt;
import com.qianniu.zhaopin.app.adapter.ForumListAdapter;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.ForumTypeList;
import com.qianniu.zhaopin.app.bean.ItemInfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoList;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Fragment_extra extends BaseFragment {
	private static final String TAG = "Fragment_extra";

	private static final int MSG_LOADDATA_APPEXCEPTION = 22;

	private static final int CACHE_MAXIMUM_NUMBER = 50;
	private Context m_Context;
	private AppContext appContext;// 全局Context
	private View mContainer;
	private View moreview;
	private ImageView m_btnSet;
	private PullToRefreshListView mlistview;
	private View mlistFooterView;
	private TextView mlistFootMoreTv;
	private ProgressBar mlistFootProgressBar;
	private ExtraHomeListAdapt mAdapter;
	private List<ItemInfoEntity> mlistData = new ArrayList<ItemInfoEntity>();
	private Handler lvDataHandler;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_Context = this.getActivity();
		appContext = (AppContext) this.getActivity().getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (mContainer != null) {
			ViewGroup p = (ViewGroup) mContainer.getParent();
			if (p != null) {
				p.removeAllViewsInLayout();
			}
			/*
			 * mlistview.onRefreshComplete(); mlistview.setSelection(0);
			 */
			if (mlistData == null || mlistData.size() == 0) {
				initLoadListData();
			} else {
				refreshLoadListData();
			}
			return mContainer;
		}
		mContainer = inflater.inflate(R.layout.fragment_extramain, container,
				false);
		moreview = mContainer.findViewById(R.id.fragment_extra_more);
		moreview.setOnClickListener(mclicklistener);

		ImageButton managerImgBtn = (ImageButton) mContainer
				.findViewById(R.id.fragment_extra_rightbt);
		managerImgBtn.setOnClickListener(mclicklistener);

		m_btnSet = (ImageView) mContainer.findViewById(R.id.fragment_extra_set);
		m_btnSet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 设置界面
				UIHelper.showSetActivity(m_Context);
			}

		});

		initListView(inflater);
		return mContainer;
	}

	OnClickListener mclicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.fragment_extra_more: {

				// TODO Auto-generated method stub
				// 友盟统计--号外--更多按钮
				UmShare.UmStatistics(m_Context, "Extra_MoreButton");

				if (!appContext.isNetworkConnected()) {
					UIHelper.ToastMessage(m_Context,
							R.string.network_not_connected);
				} else {
					Intent intent = new Intent(m_Context,
							ExtrasCatalogListActivity.class);

					m_Context.startActivity(intent);
				}

			}

				break;
			case R.id.fragment_extra_rightbt: {
				// 友盟统计--号外更多--订阅管理按钮
				UmShare.UmStatistics(m_Context, "ExtrasCatalogList_RighButton");
				UIHelper.showCustomManagerActivityBehindCheck(getActivity());
			}
				break;
			}
		}
	};

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

	private void initListView(LayoutInflater inflater) {
		mlistData.clear();
		mAdapter = new ExtraHomeListAdapt(this.getActivity(), mlistData);
		mlistFooterView = inflater.inflate(R.layout.listview_footer, null);
		mlistFootMoreTv = (TextView) mlistFooterView
				.findViewById(R.id.listview_foot_more);
		mlistFootProgressBar = (ProgressBar) mlistFooterView
				.findViewById(R.id.listview_foot_progress);
		mlistFootProgressBar.setVisibility(View.GONE);
		mlistFooterView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int lvDataState = StringUtils.toInt(mlistview.getTag());
				if (lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mlistview.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					mlistFootMoreTv.setText(R.string.load_ing);
					mlistFootProgressBar.setVisibility(View.VISIBLE);
					loadExtraHomeInfoData(mlistData, lvDataHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
		});
		mlistview = (PullToRefreshListView) mContainer
				.findViewById(R.id.fragment_extra_listview);
		mlistview.addFooterView(mlistFooterView);
		mlistview.setAdapter(mAdapter);
		lvDataHandler = this.getLvHandler(mlistview, mAdapter, mlistFootMoreTv,
				mlistFootProgressBar, AppContext.PAGE_SIZE);
		mlistview.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mlistview.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (mlistData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(mlistFooterView) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(mlistview.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mlistview.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					mlistFootMoreTv.setText(R.string.load_ing);
					mlistFootProgressBar.setVisibility(View.VISIBLE);
					loadExtraHomeInfoData(mlistData, lvDataHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mlistview.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});

		mlistview
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {

						mlistview.setTag(UIHelper.LISTVIEW_DATA_LOADING);
						// mlistFootMoreTv.setText(R.string.load_ing);
						loadExtraHomeInfoData(mlistData, lvDataHandler,
								UIHelper.LISTVIEW_ACTION_REFRESH);
					}
				});
		initLoadListData();
	}

	public void initLoadListData() {
		mlistData.clear();
		loadExtraHomeInfoData(mlistData, lvDataHandler,
				UIHelper.LISTVIEW_ACTION_INIT);
		mlistview.firstRefreshing();

		mlistview.setTag(UIHelper.LISTVIEW_DATA_LOADING);
		/*
		 * mlistFootMoreTv.setText(R.string.load_ing);
		 * mlistFootProgressBar.setVisibility(View.VISIBLE);
		 */

	}

	public void refreshLoadListData() {

		loadExtraHomeInfoData(mlistData, lvDataHandler,
				UIHelper.LISTVIEW_ACTION_REFRESH);
		mlistview.firstRefreshing();
		mlistview.setTag(UIHelper.LISTVIEW_DATA_LOADING);
		/*
		 * mlistFootProgressBar.setVisibility(View.VISIBLE);
		 * mlistFootMoreTv.setText(R.string.load_ing);
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
					mlistview.setLoadingState(false);
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

							mlistData.addAll(0, nlist.getItemInfolist());
							adapter.notifyDataSetChanged();
							more.setText(R.string.load_full);
						} else {
							boolean bflag = res.handleErrcode(getActivity());
							if (!bflag) {
								UIHelper.ToastMessage(m_Context,
										R.string.dialog_data_get_err);
							}
							lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
							MyLogger.i(TAG, res.getErrorMessage());
							mMore.setText(R.string.load_error);
						}
					} else {
						// listview数据处理
						switch (msg.arg1) {
						case UIHelper.LISTVIEW_ACTION_INIT:
						case UIHelper.LISTVIEW_ACTION_REFRESH:
						case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
							int newdata = 0;// 新加载数据-只有刷新动作才会使用到
							switch (msg.arg2) {
							case UIHelper.LISTVIEW_DATATYPE_FORUMINFO: {

								mlistData.addAll(0, nlist.getItemInfolist());
								if (mlistData.size() > CACHE_MAXIMUM_NUMBER) {
									for (int i = CACHE_MAXIMUM_NUMBER; i < mlistData
											.size(); i++) {
										mlistData.remove(i);
									}
								}
								lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
								mLvAdapt.notifyDataSetChanged();
								more.setText(R.string.load_more);
							}

								break;
							}
							break;
						case UIHelper.LISTVIEW_ACTION_SCROLL:
							switch (msg.arg2) {
							case UIHelper.LISTVIEW_DATATYPE_FORUMINFO: {
								if (nlist.getItemInfolist().size() > 0) {
									mlistData.addAll(nlist.getItemInfolist());
								}
								if (mlistData.size() > CACHE_MAXIMUM_NUMBER) {
									for (int i = 0; i < (mlistData.size() - CACHE_MAXIMUM_NUMBER); i++) {
										mlistData.remove(i);
									}
								}
								lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
								adapter.notifyDataSetChanged();
								more.setText(R.string.load_more);
							}

								break;
							}
							break;
						}
					}

				} else if (msg.what == -1) {
					mlistview.setLoadingState(false);
					// 有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					mMore.setText(R.string.load_error);
					// ((AppException) msg.obj).makeToast(mcontext);
				} else if (msg.what == MSG_LOADDATA_APPEXCEPTION) {

					AppException e = (AppException) msg.obj;
					if (e != null) {
						e.makeToast(m_Context);
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
	private void loadExtraHomeInfoData(final List<ItemInfoEntity> datalist,
			final Handler handler, final int action) {
		mlistview.setLoadingState(true);
		if (!appContext.isNetworkConnected()) {
			UIHelper.ToastMessage(m_Context,
					R.string.app_status_net_disconnected);
		}
		Thread t = new Thread() {
			public void run() {
				ItemInfoList list = null;
				Message msg = new Message();
				boolean isRefresh = false;
				String offsetid = "";
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_INIT)
					isRefresh = true;

				ItemInfoList olddblist = null;
				try {
					olddblist = appContext.getItemInfoListFromDb(appContext,
							null, DBUtils.INFO_TYPE_EXTRAHOME);
				} catch (AppException e) {
					// TODO Auto-generated catch block
					msg.what = -1;
					msg.obj = e;
				}

				try {
					// 消息模块，不需要栏目id；

					if (datalist != null && datalist.size() > 0) {
						if (isRefresh)
							offsetid = ((ItemInfoEntity) datalist.get(0))
									.getFid();
						/* .getInfoEntitylist().get(0).getInfoId(); */

						else
							offsetid = ((ItemInfoEntity) datalist.get(datalist
									.size() - 1)).getFid();/*
															 * getInfoEntitylist(
															 * ).get(0)
															 * .getInfoId();
															 */
					}

					String url = URLs.EXTRA_HOME;
					list = appContext.getItemInfoListListFromNet(null, url,
							offsetid, isRefresh, DBUtils.INFO_TYPE_EXTRAHOME);
				} catch (AppException e) {
					// e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
					Message exception_msg = new Message();
					exception_msg.what = MSG_LOADDATA_APPEXCEPTION;
					exception_msg.obj = e;
					handler.sendMessage(exception_msg);
				}

				if (action == UIHelper.LISTVIEW_ACTION_INIT) {
					if (olddblist != null
							&& olddblist.getItemInfolist() != null) {
						if (list != null && list.getItemInfolist() != null) {
							list.getItemInfolist().addAll(
									olddblist.getItemInfolist());
						} else {
							list = olddblist;
						}
					}

				}
				if (list != null) {
					msg.what = 1;
					msg.obj = list;
				} else {
					msg.what = -1;
				}
				/*
				 * try { // 消息模块，不需要栏目id；
				 * 
				 * if (datalist != null && datalist.size() > 0) { if (isRefresh)
				 * offsetid = ((ItemInfoEntity) datalist.get(0))
				 * .getInfoEntitylist().get(0).getInfoId();
				 * 
				 * else offsetid = ((ItemInfoEntity) datalist.get(datalist
				 * .size() - 1)).getInfoEntitylist().get(0) .getInfoId(); }
				 * 
				 * String url = URLs.EXTRA_HOME; ItemInfoList list =
				 * appContext.getItemInfoListList(null, url, offsetid,
				 * isRefresh, DBUtils.INFO_TYPE_EXTRAHOME);
				 * 
				 * msg.what = 1; msg.obj = list; } catch (AppException e) {
				 * //e.printStackTrace(); msg.what = -1; msg.obj = e; }
				 */
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_FORUMINFO;
				handler.sendMessage(msg);
			}
		};
		if(threadPool == null){
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
}

package com.qianniu.zhaopin.app.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.SubscriptionDataEntiy;
import com.qianniu.zhaopin.app.bean.SubscriptionDataEntiyList;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.BitmapManager;

import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.app.ui.CustomManagerActivity.ExtraCatListItemView;
import com.qianniu.zhaopin.app.view.BounceListView;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SubscriptionManageFragment extends BaseFragment {
	private String TAG = "SubscriptionManageFragment";
	public static final int TYPE_INFO = 1; // 资讯订阅,默认为资讯订阅
	public static final int TYPE_BOSS = 2;// 名人订阅
	public static final int TYPE_COMPANY = 3;// 名企订阅

	public static final int DISPLAYTYPE_SUBS = 1;// 订阅显示
	public static final int DISPLAYTYPE_SHOW = 2;// 更多显示

	private static final int HANDLERCODE_LOADDATAERR = 0;
	private static final int HANDLERCODE_LOADDATAOK = 1;
	private static final int HANDLERCODE_SUBMITERR = 2;
	private static final int HANDLERCODE_SUBMITOK = 3;
	private static final int HANDLERCODE_DATASUBMIT_START = 22;
	private static final int HANDLERCODE_DATASUBMIT_OVER = 23;

	private Context mContext;
	private AppContext mappContext;// 全局Context
	private Activity mActivity;
	private int mtype = TYPE_INFO;
	private int mdisplayType = DISPLAYTYPE_SUBS;

	private View mContainer;
	private BounceListView lv;
	private BaseAdapter lvAdapter;
	private List<SubscriptionDataEntiy> lvData = new ArrayList<SubscriptionDataEntiy>();

	public static SubscriptionManageFragment newSubsInstance(int type) {
		SubscriptionManageFragment fragment = new SubscriptionManageFragment();
		fragment.mtype = type;
		fragment.mdisplayType = DISPLAYTYPE_SUBS;
		return fragment;
	}

	public static SubscriptionManageFragment newDisplayInstance(
			int displayType, int type) {
		SubscriptionManageFragment fragment = new SubscriptionManageFragment();
		fragment.mtype = type;
		fragment.mdisplayType = displayType;
		return fragment;
	}

	public int getType() {
		return mtype;
	}

	public void setType(int type) {
		this.mtype = type;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
		mActivity = this.getActivity();
		mappContext = (AppContext) this.getActivity().getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		Log.i(TAG, "onCreateView mtype = " + mtype);
		if (mContainer != null) {
			ViewGroup p = (ViewGroup) mContainer.getParent();
			if (p != null) {
				p.removeAllViewsInLayout();
			}
			if (lvData.size() == 0) {
				initLoadListData();
			}
			return mContainer;
		}

		mContainer = (View) inflater.inflate(R.layout.fragment_subscription,
				container, false);

		lv = (BounceListView) mContainer.findViewById(R.id.content_lv);
		if (mdisplayType == DISPLAYTYPE_SUBS)
			lvAdapter = new SubAdapter(mActivity, lvData);
		else
			lvAdapter = new DisplayAdapter(mActivity, lvData);
		lv.setAdapter(lvAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mdisplayType == DISPLAYTYPE_SUBS) {
					SubViewHolder viewholder = (SubViewHolder) view.getTag();
					SubscriptionDataEntiy entity = viewholder.entity;
					ImageView checkview = viewholder.checkImgView;
					if (entity.getSubStatus() == SubscriptionDataEntiy.SUBSTYPE_DONE) {

						checkview.setVisibility(View.GONE);
						entity.setSubStatus(SubscriptionDataEntiy.SUBSTYPE_CANCEL);
					} else if (entity.getSubStatus() == SubscriptionDataEntiy.SUBSTYPE_CANCEL) {
						// checkview
						// .setImageResource(R.drawable.common_img_check_green);
						checkview.setVisibility(View.VISIBLE);
						entity.setSubStatus(SubscriptionDataEntiy.SUBSTYPE_DONE);
					}
				} else if (mdisplayType == DISPLAYTYPE_SHOW) {
					DisplayViewHolder viewholder = (DisplayViewHolder) view
							.getTag();
					SubscriptionDataEntiy entity = viewholder.entity;
					if (mtype == TYPE_INFO) {
						UIHelper.showExtraInfolist(view.getContext(),
								String.valueOf(entity.getCatId()),
								entity.getTitle(), entity.getSubStatus());
					}
				}

			}

		});

		initProgressBar(mContainer, R.id.stub_progressbar);
		initLoadListData();
		return mContainer;
	}

	/**
	 * 初始化数据
	 */
	public void initLoadListData() {
		lvData.clear();
		loadSubData(subsDataHandler, mtype);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	public class SubViewHolder { // 自定义控件集合
		public TextView title;
		public ImageView checkImgView;
		public SubscriptionDataEntiy entity;
	}

	/**
	 * 订阅列表Adapter类
	 */
	private class SubAdapter extends BaseAdapter {
		private Context context;// 运行上下文
		private List<SubscriptionDataEntiy> mdataList;// 数据集合
		private LayoutInflater listContainer;// 视图容器
		private int itemViewResource;// 自定义项视图源
		private BitmapManager bmpManager;

		/**
		 * 实例化Adapter
		 * 
		 * @param context
		 * @param data
		 * @param resource
		 */
		public SubAdapter(Context context, List<SubscriptionDataEntiy> data) {
			this.context = context;
			this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文

			this.mdataList = data;
			this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.qianniu_bg_small));
		}

		public int getCount() {
			return mdataList.size();
		}

		public Object getItem(int position) {
			return mdataList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// 自定义视图
			SubViewHolder viewholder = null;
//			Log.i(TAG, "SubAdapter getView position = " + position
//					+ ",getCount = " + mdataList.size() + ",mtype =" + mtype);
			if (convertView == null) {
				// 获取list_item布局文件的视图
				convertView = listContainer.inflate(
						R.layout.singlelinelistitem, null);

				viewholder = new SubViewHolder();
				// 获取控件对象
				viewholder.title = (TextView) convertView
						.findViewById(R.id.singlelinelistitem_title);
				viewholder.checkImgView = (ImageView) convertView
						.findViewById(R.id.singlelinelistitem_rightcheckedbtn);
				viewholder.entity = mdataList.get(position);

				// 设置控件集到convertView
				convertView.setTag(viewholder);
			} else {
				viewholder = (SubViewHolder) convertView.getTag();
				viewholder.entity = mdataList.get(position);
				convertView.setTag(viewholder);

			}
			viewholder.checkImgView.setVisibility(View.VISIBLE);

			// 设置文字和图片
			SubscriptionDataEntiy entity = viewholder.entity;
			viewholder.title.setText(entity.getTitle());
			if (entity.getSubStatus() == SubscriptionDataEntiy.SUBSTYPE_DEFAULT
					|| entity.getSubStatus() == SubscriptionDataEntiy.SUBSTYPE_DONE) {
				// viewholder.checkImgView
				// .setImageResource(R.drawable.common_img_check_green);
				viewholder.checkImgView.setVisibility(View.VISIBLE);
			} else {
				// viewholder.checkImgView
				// .setImageResource(R.drawable.common_img_check_gray);
				viewholder.checkImgView.setVisibility(View.GONE);
			}

			return convertView;
		}
	}

	private class DisplayViewHolder { // 自定义控件集合
		public TextView title;
		public ImageView face;
		public SubscriptionDataEntiy entity;
	}

	/**
	 * 消息版块列表Adapter类
	 */
	private class DisplayAdapter extends BaseAdapter {
		private Context context;// 运行上下文
		private List<SubscriptionDataEntiy> mdataList;// 数据集合
		private LayoutInflater listContainer;// 视图容器
		private int itemViewResource;// 自定义项视图源
		private BitmapManager bmpManager;

		/**
		 * 实例化Adapter
		 * 
		 * @param context
		 * @param data
		 * @param resource
		 */
		public DisplayAdapter(Activity context, List<SubscriptionDataEntiy> data) {
			this.context = context;
			this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文

			this.mdataList = data;
			this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.qianniu_logobg));
		}

		public int getCount() {
			return mdataList.size();
		}

		public Object getItem(int arg0) {
			return mdataList.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			MyLogger.d(TAG, "getView position = " + position);

			// 自定义视图
			DisplayViewHolder viewholder = null;

			if (convertView == null) {
				// 获取list_item布局文件的视图
				convertView = listContainer.inflate(
						R.layout.extras_ctl_listitem, null);

				viewholder = new DisplayViewHolder();
				// 获取控件对象
				viewholder.title = (TextView) convertView
						.findViewById(R.id.listitem_title_tv);
				viewholder.face = (ImageView) convertView
						.findViewById(R.id.listitem_userface_iv);
				viewholder.entity = mdataList.get(position);

				// 设置控件集到convertView
				convertView.setTag(viewholder);
			} else {
				viewholder = (DisplayViewHolder) convertView.getTag();
				viewholder.entity = mdataList.get(position);
				;
			}

			// 设置文字和图片
			SubscriptionDataEntiy entity = viewholder.entity;
			viewholder.title.setText(entity.getTitle());
			String faceURL = entity.getLogoUrl();

			if (faceURL.endsWith("portrait.gif")
					|| StringUtils.isEmpty(faceURL)) {
				viewholder.face.setImageResource(R.drawable.qianniu_logobg);
			} else {
				CommonRoundImgCreator creator = new CommonRoundImgCreator(
						mActivity,
						Constants.InfoModule.LIST_ITEM_FACEIMG_RADIUS, 0, 0);
				bmpManager.loadBitmap(faceURL, viewholder.face, null, creator);
			}

			return convertView;
		}
	}

	private Handler subsDataHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLERCODE_LOADDATAOK: {

				SubscriptionDataEntiyList nlist = (SubscriptionDataEntiyList) msg.obj;
//				Log.i(TAG, "HANDLERCODE_LOADDATAOK getCount = "
//						+ nlist.getEntitylist().size() + ",mtype =" + mtype);
				Result res = null;
				if (nlist != null) {
					res = nlist.getValidate();
				}
				if (nlist == null || res == null) {
					UIHelper.ToastMessage(mContext, R.string.load_error);

				} else if (!res.OK()) {
					if (res.getErrorCode() == Result.CODE_NOTMORE) {

						UIHelper.ToastMessage(mContext, R.string.load_full);
					} else {
						boolean bflag = res.handleErrcode(mActivity);
						if (!bflag) {
							UIHelper.ToastMessage(mContext,
									R.string.dialog_data_get_err);
						}

					}
				} else {
					lvData.clear();// 先清除原有数据
					lvData.addAll(nlist.getEntitylist());
					lvAdapter.notifyDataSetChanged();
//					Log.i(TAG, "HANDLERCODE_LOADDATAOK lvAdapter getCount = "
//							+ lvAdapter.getCount() + ",mtype =" + mtype);
				}

			}
				break;
			case HANDLERCODE_LOADDATAERR: {
				// 有异常--显示加载出错 & 弹出错误消息
				UIHelper.ToastMessage(mContext, R.string.dialog_data_get_err);
			}
				break;
			case HANDLERCODE_SUBMITOK: {
				Result result = (Result) msg.obj;
				if (result.OK()) {
					UIHelper.ToastMessage(mappContext,
							R.string.app_catalog_customselect_success);
					// ((CustomManagerActivity) mcontext).quit();
				} else {
					boolean bflag = result.handleErrcode(mActivity);
					if (!bflag)
						UIHelper.ToastMessage(mappContext,
								R.string.app_catalog_customselect_fail);
				}
			}
				break;

			case HANDLERCODE_DATASUBMIT_START: {
				showProgressBar();
			}
				break;
			case HANDLERCODE_DATASUBMIT_OVER: {
				hideProgressBar();
			}
				break;
			}
			;

		}

	};

	/**
	 * 获取订阅数据
	 * 
	 * @param handler
	 *            ：数据处理
	 * @param type
	 *            :当前订阅类型；
	 */
	private void loadSubData(final Handler handler, final int type) {

		Thread t = new Thread() {
			public void run() {
				Message msg = new Message();
				handler.sendEmptyMessage(HANDLERCODE_DATASUBMIT_START);
				try {
					String url = getRssListUrl(type);
					SubscriptionDataEntiyList list = getSubEntityListFromNet(
							mappContext, url);
					msg.what = HANDLERCODE_LOADDATAOK;
					msg.obj = list;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = HANDLERCODE_LOADDATAERR;
					msg.obj = e;
				}

				handler.sendMessage(msg);
				handler.sendEmptyMessage(HANDLERCODE_DATASUBMIT_OVER);
			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	private SubscriptionDataEntiyList getSubEntityListFromNet(
			AppContext appContext, String url) throws AppException {

		JSONObject obj = new JSONObject();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				obj.toString());
		try {
			return SubscriptionDataEntiyList.parse(appContext,
					ApiClient._post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static String getRssListUrl(int type) {
		String url = null;
		switch (type) {
		case TYPE_INFO: {
			url = URLs.RSS_SUBSMANAGER_LIST_BASE + "/" + URLs.RSS_TYPEPOST;
		}
			break;
		case TYPE_BOSS: {
			url = URLs.RSS_SUBSMANAGER_LIST_BASE + "/" + URLs.RSS_TYPEBOSS;
		}
			break;
		case TYPE_COMPANY: {
			url = URLs.RSS_SUBSMANAGER_LIST_BASE + "/" + URLs.RSS_TYPECOMPANY;
		}
			break;

		}
		return url;
	}

	public static String getRssSubmitUrl(int type) {
		String url = null;
		switch (type) {
		case TYPE_INFO: {
			url = URLs.RSS_SUBSMANAGER_SUBMIT_BASE + "/" + URLs.RSS_TYPEPOST;
		}
			break;
		case TYPE_BOSS: {
			url = URLs.RSS_SUBSMANAGER_SUBMIT_BASE + "/" + URLs.RSS_TYPEBOSS;
		}
			break;
		case TYPE_COMPANY: {
			url = URLs.RSS_SUBSMANAGER_SUBMIT_BASE + "/" + URLs.RSS_TYPECOMPANY;
		}
			break;

		}
		return url;
	}

	/**
	 * 全选或全不选（已经全部选择，则全部取消）
	 */
	public void selectedAll() {
		{
			if (lvData != null && lvData.size() > 0) {
				boolean selectedall = true;
				for (int i = 0; i < lvData.size(); i++) {
					SubscriptionDataEntiy entity = lvData.get(i);
					if (entity.getSubStatus() == SubscriptionDataEntiy.SUBSTYPE_CANCEL) {
						selectedall = false;
						break;
					}
				}
				if (selectedall) {
					for (int i = 0; i < lvData.size(); i++) {
						SubscriptionDataEntiy entity = lvData.get(i);
						if (entity.getSubStatus() != SubscriptionDataEntiy.SUBSTYPE_DEFAULT)
							entity.setSubStatus(SubscriptionDataEntiy.SUBSTYPE_CANCEL);
					}
				} else {

					for (int i = 0; i < lvData.size(); i++) {
						SubscriptionDataEntiy entity = lvData.get(i);
						if (entity.getSubStatus() != SubscriptionDataEntiy.SUBSTYPE_DEFAULT)
							entity.setSubStatus(SubscriptionDataEntiy.SUBSTYPE_DONE);
					}

				}
			}
			lvAdapter.notifyDataSetChanged();
		}
	}

	public List<SubscriptionDataEntiy> getSelectedData() {
		List<SubscriptionDataEntiy> list = new ArrayList<SubscriptionDataEntiy>();

		for (int i = 0; i < lvData.size(); i++) {
			SubscriptionDataEntiy entity = lvData.get(i);

			if (entity.getSubStatus() == SubscriptionDataEntiy.SUBSTYPE_DONE) {
				list.add(entity);
			}
		}

		return list;
	}

	public void reportSubscriptionByThread() {
		final List<SubscriptionDataEntiy> list = lvData;// getSelectedData();
		if (list.size() == 0) {
			UIHelper.ToastMessage(mappContext, R.string.dialog_submit_nodata);
			return;
		}
		final Handler handler = subsDataHandler;
		Thread t = new Thread() {
			public void run() {
				handler.sendEmptyMessage(HANDLERCODE_DATASUBMIT_START);
				Result result = reportSubscriptionData(mappContext, list, mtype);
				Message msg = new Message();
				msg.what = HANDLERCODE_SUBMITOK;
				msg.obj = result;
				handler.sendMessage(msg);
				handler.sendEmptyMessage(HANDLERCODE_DATASUBMIT_OVER);

			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	public static Result reportSubscriptionData(AppContext appContext,
			List<SubscriptionDataEntiy> list, int type) {

		String url = getRssSubmitUrl(type);
		Result result = null;

		//JSONObject data = new JSONObject();
		JSONArray array = new JSONArray();
		try {
						for (int i = 0; i < list.size(); i++) {
				JSONObject obj = new JSONObject();
				SubscriptionDataEntiy entity = list.get(i);
				obj.putOpt("cat_id", entity.getCatId());
				if (entity.getSubStatus() == SubscriptionDataEntiy.SUBSTYPE_DONE)
					obj.putOpt("status", SubscriptionDataEntiy.SUBSTYPE_DONE);
				else
					obj.putOpt("status", SubscriptionDataEntiy.SUBSTYPE_CANCEL);
				array.put(obj);
			}
			//data.putOpt("data", array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			result = new Result(-1, "json error");
			return result;
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				array.toString());
		try {
			result = ApiClient.http_post(appContext, url, params, null);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			result = new Result(-1, "AppException error");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = new Result(-1, "IOException error");
		} catch (Exception e) {
			result = new Result(-1, "Exception error");
		}
		return result;
	}

	// @Override
	// public void onActivityCreated(Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// super.onActivityCreated(savedInstanceState);
	// Log.i(TAG, "onActivityCreated mtype = "+mtype);
	// }
	//
	// @Override
	// public void onAttach(Activity activity) {
	// // TODO Auto-generated method stub
	// super.onAttach(activity);
	// Log.i(TAG, "onAttach mtype = "+mtype);
	// }
	//
	// @Override
	// public void onDestroy() {
	// // TODO Auto-generated method stub
	// super.onDestroy();
	// Log.i(TAG, "onDestroy mtype = "+mtype);
	// }
	//
	// @Override
	// public void onDestroyView() {
	// // TODO Auto-generated method stub
	// super.onDestroyView();
	// Log.i(TAG, "onDestroyView mtype = "+mtype);
	// }
	//
	// @Override
	// public void onDetach() {
	// // TODO Auto-generated method stub
	// super.onDetach();
	// Log.i(TAG, "onDetach mtype = "+mtype);
	// }
	//
	// @Override
	// public void onPause() {
	// // TODO Auto-generated method stub
	// super.onPause();
	// Log.i(TAG, "onPause mtype = "+mtype);
	// }
	//
	// @Override
	// public void onResume() {
	// // TODO Auto-generated method stub
	// super.onResume();
	// Log.i(TAG, "onResume mtype = "+mtype);
	// }
	//
	// @Override
	// public void onStart() {
	// // TODO Auto-generated method stub
	// super.onStart();
	// Log.i(TAG, "onStart mtype = "+mtype);
	// }
	//
	// @Override
	// public void onStop() {
	// // TODO Auto-generated method stub
	// super.onStop();
	// Log.i(TAG, "onCreateView mtype = "+mtype);
	// }

}

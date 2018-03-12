package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.ForumListAdapter.ForumListItemView;
import com.qianniu.zhaopin.app.bean.ColumnEntity;
import com.qianniu.zhaopin.app.bean.ColumnEntityList;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.ForumTypeList;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.view.BounceListView;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class ExtrasCatalogListActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {
	private static final String TAG = "ExtrasCatalogListActivity";
	private Context m_Context;
	private ExtraCatListAdapter lvAdapter;
	private List<ForumType> lvData = new ArrayList<ForumType>();
	private Map<Integer, ColumnEntity> columnMap = new HashMap<Integer, ColumnEntity>();
	private Map<Integer, List<ForumType>> catalogMap = new HashMap<Integer, List<ForumType>>();

	private ImageButton btn_back;
	private TextView tv_title;
	private ImageView tv_rightbt;

	private View commonLoadView;
	private RadioGroup columnGroup;
	private TextView commonStateTv;
	private BounceListView lv;
	private AppContext appContext;// 全局Context
	private LayoutInflater mInflater;

	public ExtrasCatalogListActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		m_Context = this;
		appContext = (AppContext) this.getApplication();
		mInflater = LayoutInflater.from(this);

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);

		setContentView(R.layout.activity_extrascataloglist);
		commonLoadView = (View) findViewById(R.id.commom_loading_layout);
		// commonProgressBar = (ProgressBar) commonLoadView
		// .findViewById(R.id.common_loading_progress);
		initProgressBar(R.id.common_loading_progress);
		commonStateTv = (TextView) commonLoadView
				.findViewById(R.id.common_loading_textview);

		btn_back = (ImageButton) findViewById(R.id.extracatlist_goback);
		btn_back.setOnClickListener(UIHelper.finish(this));
		tv_title = (TextView) findViewById(R.id.extracatlist_title);
		tv_rightbt = (ImageView) findViewById(R.id.extracatlist_rightbt);
		tv_rightbt.setOnClickListener(this);
		lv = (BounceListView) findViewById(R.id.extracatlist_listview);
		lvAdapter = new ExtraCatListAdapter(this, lvData);
		lv.setAdapter(lvAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ExtraCatListAdapter.ExtraCatListItemView info = (ExtraCatListAdapter.ExtraCatListItemView) view
						.getTag();
				if (info == null || info.infotype == null)
					return;

				UIHelper.showExtraInfolist(view.getContext(),
						info.infotype.getInfoId(), info.infotype.getTitle(),
						info.infotype.getCutomType());
			}

		});
		columnGroup = (RadioGroup) findViewById(R.id.column_group_rg);
		columnGroup.setOnCheckedChangeListener(this);
		initLoadColumnData();
	}

	public void initLoadColumnData() {
		loadLvColumnData(columnHandler);
		commonLoadView.setVisibility(View.VISIBLE);
		showProgressBar();
		commonStateTv.setText(R.string.load_ing);

	}

	private void handleLvData(int what, Object obj, int objtype, int columnId) {
		ForumTypeList nlist = (ForumTypeList) obj;

		lvData.clear();// 先清除原有数据
		lvData.addAll(nlist.getInfoTypelist());
		int size = catalogMap.size();
		catalogMap.put(columnId, nlist.getInfoTypelist());
		boolean b = catalogMap.containsKey(columnId);
		List<ForumType> l = catalogMap.get(columnId);
		int i = l.size();
		size = catalogMap.size();
	}

	private Handler lvInfoTypeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what >= 0) {
				// listview数据处理
				/*
				 * Notice notice = handleLvData(msg.what, msg.obj, msg.arg2,
				 * msg.arg1);
				 */
				ForumTypeList nlist = (ForumTypeList) msg.obj;
				if (nlist != null) {
					Result result = nlist.getValidate();
					if (result.OK()) {
						handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
						lvAdapter.notifyDataSetChanged();
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						commonLoadView.setVisibility(View.GONE);
					} else {

						boolean bflag = result
								.handleErrcode(ExtrasCatalogListActivity.this);
						if (!bflag) {
							UIHelper.ToastMessage(m_Context,
									R.string.dialog_data_get_err);
						}
						commonLoadView.setVisibility(View.GONE);
					}
				} else {

				}

			} else if (msg.what == -1) {
				// 有异常--显示加载出错 & 弹出错误消息
				lv.setTag(UIHelper.LISTVIEW_DATA_MORE);

				commonLoadView.setVisibility(View.GONE);
				UIHelper.ToastMessage(m_Context, R.string.dialog_data_get_err);
				// ((AppException) msg.obj).makeToast(mcontext);
			}
			if (lvAdapter.getCount() == 0) {
				lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
				commonLoadView.setVisibility(View.VISIBLE);

				commonStateTv.setText(R.string.load_empty);
			}

			goneProgressBar();

		}

	};
	private Handler columnHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what >= 0) {
				ColumnEntityList nlist = (ColumnEntityList) msg.obj;

				if (nlist != null) {
					Result result = nlist.getValidate();
					if (result.OK()) {
						columnGroup.removeAllViews();
						columnMap.clear();
						int size = nlist.getArraylist().size();
						for (int i = 0; i < size; i++) {
							ColumnEntity entity = nlist.getArraylist().get(i);
							boolean badd = addGroupView(entity, i);
							if (badd) {
								columnMap.put(i, entity);
							}
						}
						RadioButton rbtn = (RadioButton) columnGroup
								.getChildAt(0);
						rbtn.setChecked(true);

					} else {
						boolean bflag = result
								.handleErrcode(ExtrasCatalogListActivity.this);
						if (!bflag) {
							UIHelper.ToastMessage(m_Context, "获取数据失败");
						}
					}
				}

			} else if (msg.what == -1) {
				goneProgressBar();
				commonLoadView.setVisibility(View.VISIBLE);
				commonStateTv.setText(R.string.load_empty);
			}
			if (columnMap.size() == 0) {
				goneProgressBar();
				commonLoadView.setVisibility(View.VISIBLE);
				commonStateTv.setText(R.string.load_empty);
			}

		}

	};

	private boolean addGroupView(ColumnEntity entity, int rbt_id) {

		if (entity.getColumnTitle() != null
				&& entity.getColumnTitle().length() > 0) {
			RadioButton btn = (RadioButton) mInflater.inflate(
					R.layout.radio_button, null);
			btn.setText(entity.getColumnTitle());
			btn.setId(rbt_id);
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager WM = (WindowManager) m_Context
					.getSystemService("window");
			WM.getDefaultDisplay().getMetrics(dm);
			float density = dm.density;
			/*
			 * LinearLayout.LayoutParams lp = new
			 * LinearLayout.LayoutParams((int)(150*density),(int)(30*density));
			 */
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, (int) (30 * density));
			btn.setLayoutParams(lp);

			btn.setPadding((int) (2 * density), (int) (2 * density),
					(int) (5 * density), (int) (2 * density));

			columnGroup.addView(btn);
			return true;
		} else {
			return false;
		}

	}

	public void LoadListData(int columnId) {
		loadLvInfoTypesData(lvInfoTypeHandler, UIHelper.LISTVIEW_ACTION_INIT,
				columnId);
		commonLoadView.setVisibility(View.VISIBLE);
		showProgressBar();
		commonStateTv.setText(R.string.load_ing);
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
			final int columnid) {
		if (!appContext.isNetworkConnected()) {
			UIHelper.ToastMessage(m_Context, R.string.network_not_connected);
		}

		Thread t = new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					ForumTypeList list = appContext.getInfoTypesList(isRefresh,
							true, DBUtils.CATALOG_TYPE_EXTRA_CATALOG, columnid);
					if (list != null) {
						msg.what = 1;
						msg.obj = list;
					} else {
						msg.what = -1;
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = columnid;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_EXTRACATTYPES;

				handler.sendMessage(msg);
			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	/**
	 * 线程加载栏目数据
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
	private void loadLvColumnData(final Handler handler) {

		Thread t = new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;

				ColumnEntityList list = appContext.getColumnEntityList(
						DBUtils.CATALOG_TYPE_EXTRA_COLUMN, true);
				if (list != null) {
					msg.what = 1;
					msg.obj = list;
				} else {
					msg.what = -1;
					msg.obj = list;
				}
				msg.what = 1;
				msg.obj = list;

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
		switch (requestCode) {
		case ActivityRequestCode.RESULT_ACTIVITY_LOGIN: {
			MyLogger.i(TAG, "RESULT_ACTIVITY_LOGIN##code ="
					+ ActivityRequestCode.RESULT_ACTIVITY_LOGIN);
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
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 消息版块列表Adapter类
	 */
	private class ExtraCatListAdapter extends BaseAdapter {
		private Activity context;// 运行上下文
		private List<ForumType> listItems;// 数据集合
		private LayoutInflater listContainer;// 视图容器
		private int itemViewResource;// 自定义项视图源
		private BitmapManager bmpManager;

		private class ExtraCatListItemView { // 自定义控件集合
			public TextView title;
			public ImageView face;
			public ForumType infotype;
		}

		/**
		 * 实例化Adapter
		 * 
		 * @param context
		 * @param data
		 * @param resource
		 */
		public ExtraCatListAdapter(Activity context, List<ForumType> data) {
			this.context = context;
			this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文

			this.listItems = data;
			this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.qianniu_logobg));
		}

		public int getCount() {
			return listItems.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		/**
		 * ListView Item设置
		 */
		/*
		 * public View getView(int position, View convertView, ViewGroup parent)
		 * { MyLogger.d(TAG, "getView position = " + position);
		 * 
		 * // 自定义视图 ExtraCatListItemView listItemView = null;
		 * 
		 * if (convertView == null) { // 获取list_item布局文件的视图 convertView =
		 * listContainer.inflate( R.layout.singlelinelistitem, null);
		 * 
		 * listItemView = new ExtraCatListItemView(); // 获取控件对象
		 * listItemView.title = (TextView) convertView
		 * .findViewById(R.id.singlelinelistitem_title);
		 * 
		 * listItemView.infotype = listItems.get(position);
		 * 
		 * // 设置控件集到convertView convertView.setTag(listItemView); } else {
		 * listItemView = (ExtraCatListItemView) convertView.getTag();
		 * listItemView.infotype = listItems.get(position); ; }
		 * 
		 * // 设置文字和图片 ForumType infotype = listItemView.infotype;
		 * listItemView.title.setText(infotype.getTitle());
		 * listItemView.title.setTag(infotype);// 设置隐藏参数(实体类)
		 * 
		 * return convertView; }
		 */

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			MyLogger.d(TAG, "getView position = " + position);

			// 自定义视图
			ExtraCatListItemView listItemView = null;

			if (convertView == null) {
				// 获取list_item布局文件的视图
				convertView = listContainer.inflate(
						R.layout.extras_ctl_listitem, null);

				listItemView = new ExtraCatListItemView();
				// 获取控件对象
				listItemView.title = (TextView) convertView
						.findViewById(R.id.listitem_title_tv);
				listItemView.face = (ImageView) convertView
						.findViewById(R.id.listitem_userface_iv);
				listItemView.infotype = listItems.get(position);

				// 设置控件集到convertView
				convertView.setTag(listItemView);
			} else {
				listItemView = (ExtraCatListItemView) convertView.getTag();
				listItemView.infotype = listItems.get(position);
				;
			}

			// 设置文字和图片
			ForumType infotype = listItemView.infotype;
			listItemView.title.setText(infotype.getTitle());
			listItemView.title.setTag(infotype);// 设置隐藏参数(实体类)
			String faceURL = infotype.getInfoLogo_url();

			if (faceURL.endsWith("portrait.gif")
					|| StringUtils.isEmpty(faceURL)) {
				listItemView.face.setImageResource(R.drawable.widget_dface);
			} else {
				CommonRoundImgCreator creator = new CommonRoundImgCreator(
						context, Constants.InfoModule.LIST_ITEM_FACEIMG_RADIUS,
						0, 0);

				bmpManager
						.loadBitmap(faceURL, listItemView.face, null, creator);
			}

			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.extracatlist_rightbt:
			// 友盟统计--号外更多--订阅管理按钮
			UmShare.UmStatistics(m_Context, "ExtrasCatalogList_RighButton");
			UIHelper.showCustomManagerActivity(this);
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		ColumnEntity entity = columnMap.get(checkedId);
		int columid = Integer.parseInt(entity.getColumnId());
		if (catalogMap.containsKey(columid)
				&& catalogMap.get(columid).size() > 0) {
			lvData.clear();// 先清除原有数据
			List<ForumType> list = catalogMap.get(columid);
			lvData.addAll(list);
			lvAdapter.notifyDataSetChanged();
		} else {
			if (columid > 0) {
				LoadListData(Integer.parseInt(entity.getColumnId()));
			} else {

				Message msg = new Message();
				msg.what = -1;
				msg.obj = "invalid column";
				msg.arg1 = columid;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_EXTRACATTYPES;

				lvInfoTypeHandler.sendMessage(msg);
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 友盟统计
		UmShare.UmResume(m_Context);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 友盟统计
		UmShare.UmPause(m_Context);
	}
}

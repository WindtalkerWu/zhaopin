package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ActionBar.LayoutParams;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.ColumnEntity;
import com.qianniu.zhaopin.app.bean.ColumnEntityList;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.ForumTypeList;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.app.view.BounceListView;
import com.qianniu.zhaopin.R;

public class CustomManagerActivity extends BaseActivity implements
		OnClickListener, OnCheckedChangeListener {
	private static final String TAG = "CustomManagerActivity";

	private static final int HANDLERCODE_LOADDATAOK = 1;
	private static final int HANDLERCODE_LOADDATAERR = -1;
	private static final int HANDLERCODE_SUBMITOK = 2;
	private static final int HANDLERCODE_DATASUBMIT_START = 22;
	private static final int HANDLERCODE_DATASUBMIT_OVER = 23;
	private Context mcontext;
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

	private ProgressDialog mprogressDialog;


	public CustomManagerActivity() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		appContext = (AppContext) this.getApplication();
		mInflater = LayoutInflater.from(this);

		setContentView(R.layout.activity_extrascataloglist_manager);
		commonLoadView = (View) findViewById(R.id.commom_loading_layout);
		// commonProgressBar = (ProgressBar) commonLoadView
		// .findViewById(R.id.common_loading_progress);
		initProgressBar(R.id.common_loading_progress);
		commonStateTv = (TextView) commonLoadView
				.findViewById(R.id.common_loading_textview);

		btn_back = (ImageButton) findViewById(R.id.extracatlist_goback);
		btn_back.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.extracatlist_title);
		tv_title.setText(R.string.app_catalog_custommanager);
		tv_rightbt = (ImageView) findViewById(R.id.extracatlist_rightbt);
		// tv_rightbt.setText(R.string.frame_btn_save);
		tv_rightbt.setOnClickListener(this);
		ImageButton mallImgBtn = (ImageButton) findViewById(R.id.extracatlist_all);
		mallImgBtn.setOnClickListener(this);

		mprogressDialog = new ProgressDialog(this);
		// mprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		lv = (BounceListView) findViewById(R.id.extracatlist_listview);
		lvAdapter = new ExtraCatListAdapter(this, lvData);
		lv.setAdapter(lvAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ExtraCatListItemView listItemView = (ExtraCatListItemView) view
						.getTag();
				ForumType infotype = listItemView.infotype;
				ImageView checkview = listItemView.checkImgView;
				if (infotype.getCutomType() == ForumType.CUSTOM_TYPE_SELECTED) {
					// checkview
					// .setImageResource(R.drawable.common_img_check_gray);
					checkview.setVisibility(View.GONE);
					infotype.setCutomType(ForumType.CUSTOM_TYPE_UNSELECTED);
				} else if (infotype.getCutomType() == ForumType.CUSTOM_TYPE_UNSELECTED) {
					// checkview
					// .setImageResource(R.drawable.common_img_check_green);
					checkview.setVisibility(View.VISIBLE);
					infotype.setCutomType(ForumType.CUSTOM_TYPE_SELECTED);
				}
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

	}

	private Handler lvInfoTypeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ForumTypeList nlist = (ForumTypeList) msg.obj;
				Result res = null;
				if (nlist != null) {
					res = nlist.getValidate();
				}
				if (nlist == null || res == null) {
					UIHelper.ToastMessage(mcontext, R.string.load_error);

				} else if (!res.OK()) {
					if (res.getErrorCode() == Result.CODE_NOTMORE) {

						UIHelper.ToastMessage(mcontext, R.string.load_full);
					} else {
						boolean bflag = res
								.handleErrcode(CustomManagerActivity.this);
						if (!bflag) {
							UIHelper.ToastMessage(mcontext,
									R.string.dialog_data_get_err);
						}

					}
				} else {

					handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);
					lvAdapter.notifyDataSetChanged();
					commonLoadView.setVisibility(View.GONE);
				}
			} else if (msg.what == -1) {
				// 有异常--显示加载出错 & 弹出错误消息
				lv.setTag(UIHelper.LISTVIEW_DATA_MORE);

				commonLoadView.setVisibility(View.GONE);
				UIHelper.ToastMessage(mcontext, R.string.dialog_data_get_err);
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
				if (nlist != null && nlist.getArraylist().size() != 0) {
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
					RadioButton rbtn = (RadioButton) columnGroup.getChildAt(0);
					rbtn.setChecked(true);

				} else {

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
	private Handler submitHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDLERCODE_SUBMITOK: {
				Result result = (Result) msg.obj;
				if (result.OK()) {
					UIHelper.ToastMessage(mcontext,
							R.string.app_catalog_customselect_success);
					((CustomManagerActivity) mcontext).quit();
				} else {
					boolean bflag = result
							.handleErrcode(CustomManagerActivity.this);
					if (!bflag)
						UIHelper.ToastMessage(mcontext,
								R.string.app_catalog_customselect_fail);
				}
			}
				break;

			case HANDLERCODE_DATASUBMIT_START: {
				String dialogmsg = getResources().getString(
						R.string.dialog_datasubmitmsg);
				mprogressDialog.setMessage(dialogmsg);
				mprogressDialog.show();
			}
				break;
			case HANDLERCODE_DATASUBMIT_OVER: {
				mprogressDialog.dismiss();
			}
				break;
			}
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case ActivityRequestCode.RESULT_ACTIVITY_LOGIN: {
			initLoadColumnData();
		}
			break;
		}
	}

	private boolean addGroupView(ColumnEntity entity, int rbt_id) {

		if (entity.getColumnTitle() != null
				&& entity.getColumnTitle().length() > 0) {
			RadioButton btn = (RadioButton) mInflater.inflate(
					R.layout.radio_button, null);
			btn.setText(entity.getColumnTitle());
			btn.setId(rbt_id);
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager WM = (WindowManager) mcontext
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

		Thread t = new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					ForumTypeList list = appContext.getInfoTypesListFromNet(
							isRefresh, true,
							DBUtils.CATALOG_TYPE_CUSTOMMANAGER_CATALOG,
							columnid);
					msg.what = 1;
					msg.obj = list;
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
						DBUtils.CATALOG_TYPE_CUSTOMMANAGER_COLUMN, true);
				if (list != null) {
					msg.what = 1;
					msg.obj = list;
				} else {
					msg.what = -1;
					msg.obj = list;
				}

				handler.sendMessage(msg);
			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		boolean flag = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/*
			 * AppManager.getAppManager().finishActivity(
			 * CommonStaticItemList.class);
			 */
			this.finish();
		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}

	public List<ForumType> getSelectedCatalogId() {
		List<ForumType> list = new ArrayList<ForumType>();
		for (Entry<Integer, List<ForumType>> entry : catalogMap.entrySet()) {
			List<ForumType> forumslist = entry.getValue();
			for (int i = 0; i < forumslist.size(); i++) {
				ForumType data = (ForumType) forumslist.get(i);
				/*
				 * if (data.getCutomType() == ForumType.CUSTOM_TYPE_DEFAULT ||
				 * data.getCutomType() == ForumType.CUSTOM_TYPE_SELECTED) {
				 * list.add(data.getInfoId()); }
				 */
				list.add(data);
			}

		}
		return list;
	}

	private void reportSelectedCatalog(final List<ForumType> list,
			final Handler handler) {

		Thread t = new Thread() {
			public void run() {
				handler.sendEmptyMessage(HANDLERCODE_DATASUBMIT_START);
				Result result = ApiClient.reportMultiSelectedCatalog(
						appContext, list);
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

	public class ExtraCatListItemView { // 自定义控件集合
		public TextView title;
		public ImageView checkImgView;
		public ForumType infotype;
	}

	/**
	 * 消息版块列表Adapter类
	 */
	private class ExtraCatListAdapter extends BaseAdapter {
		private Context context;// 运行上下文
		private List<ForumType> listItems;// 数据集合
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
		public ExtraCatListAdapter(Context context, List<ForumType> data) {
			this.context = context;
			this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文

			this.listItems = data;
			this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.widget_dface_loading));
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
		public View getView(int position, View convertView, ViewGroup parent) {
			MyLogger.d("method", "getView position = " + position);

			// 自定义视图
			ExtraCatListItemView listItemView = null;

			if (convertView == null) {
				// 获取list_item布局文件的视图
				convertView = listContainer.inflate(
						R.layout.singlelinelistitem, null);

				listItemView = new ExtraCatListItemView();
				// 获取控件对象
				listItemView.title = (TextView) convertView
						.findViewById(R.id.singlelinelistitem_title);
				listItemView.checkImgView = (ImageView) convertView
						.findViewById(R.id.singlelinelistitem_rightcheckedbtn);
				listItemView.infotype = listItems.get(position);

				// 设置控件集到convertView
				convertView.setTag(listItemView);
			} else {
				listItemView = (ExtraCatListItemView) convertView.getTag();
				listItemView.infotype = listItems.get(position);
				convertView.setTag(listItemView);

			}
			listItemView.checkImgView.setVisibility(View.VISIBLE);

			// 设置文字和图片
			ForumType infotype = listItemView.infotype;
			listItemView.title.setText(infotype.getTitle());
			listItemView.title.setTag(infotype);// 设置隐藏参数(实体类)
			if (infotype.getCutomType() == ForumType.CUSTOM_TYPE_DEFAULT
					|| infotype.getCutomType() == ForumType.CUSTOM_TYPE_SELECTED) {
				// listItemView.checkImgView
				// .setImageResource(R.drawable.common_img_check_green);
				listItemView.checkImgView.setVisibility(View.VISIBLE);
			} else {
				// listItemView.checkImgView
				// .setImageResource(R.drawable.common_img_check_gray);
				listItemView.checkImgView.setVisibility(View.GONE);
			}
			/*
			 * listItemView.checkImgView.setTag(infotype);
			 * listItemView.checkImgView.setOnClickListener(new
			 * OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { // TODO Auto-generated
			 * method stub ImageView checkview = (ImageView) v; ForumType
			 * infotype = (ForumType) v.getTag(); if (infotype.getCutomType() ==
			 * ForumType.CUSTOM_TYPE_SELECTED) {
			 * checkview.setImageResource(R.drawable.common_img_check_gray);
			 * infotype.setCutomType(ForumType.CUSTOM_TYPE_UNSELECTED); } else
			 * if (infotype.getCutomType() == ForumType.CUSTOM_TYPE_UNSELECTED)
			 * { checkview.setImageResource(R.drawable.common_img_check_green);
			 * infotype.setCutomType(ForumType.CUSTOM_TYPE_SELECTED); } } });
			 */
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.extracatlist_goback:

			this.finish();
			break;
		case R.id.extracatlist_rightbt: {
			List<ForumType> selectedList = getSelectedCatalogId();
			if (selectedList.size() > 0) {
				reportSelectedCatalog(selectedList, submitHandler);
			} else {
				UIHelper.ToastMessage(mcontext, R.string.dialog_submit_nodata);
			}

		}
			break;
		case R.id.extracatlist_all: {
			if (lvData != null && lvData.size() > 0) {
				boolean selectedall = true;
				for (int i = 0; i < lvData.size(); i++) {
					ForumType infotype = lvData.get(i);
					if (infotype.getCutomType() == ForumType.CUSTOM_TYPE_UNSELECTED) {
						selectedall = false;
						break;
					}
				}
				if (selectedall) {
					for (int i = 0; i < lvData.size(); i++) {
						ForumType infotype = lvData.get(i);
						infotype.setCutomType(ForumType.CUSTOM_TYPE_UNSELECTED);
					}
				} else {

					for (int i = 0; i < lvData.size(); i++) {
						ForumType infotype = lvData.get(i);
						infotype.setCutomType(ForumType.CUSTOM_TYPE_SELECTED);
					}

				}
			}
			lvAdapter.notifyDataSetChanged();
		}
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		/*
		 * List<String> selectedList = getSelectedCatalogId();
		 * reportSelectedCatalog(selectedList);
		 */
		super.finish();

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

	public void quit() {
		this.finish();
	}
}

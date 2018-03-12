package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppManager;
import com.qianniu.zhaopin.app.bean.ColumnEntity;
import com.qianniu.zhaopin.app.bean.ColumnEntityList;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.view.BounceListView;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CommonStaticItemListActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "CommonStaticItemListActivity";
	public static final String KEY_DATATYPE = "key_type";
	public static final String KEY_PARENTID = "key_parentid";
	public static final String KEY_SELECTEDID = "key_selectedid";
	public static final String KEY_SELETEDTYPE = "key_selectedtype";

	public static final String RESULTKEY_ENTITY = "entitiy";

	private static final int COMMONLIST_REQUESTCODE = 500;
	public static final int COMMON_SINGLESELECT = 1;
	public static final int COMMON_MULTISELECT = 2;

	private static final int MAX_MULTISELECT = 5;

	private Context mcontext;
	private ListView lv;
	private AppContext appContext;// 全局Context
	private LayoutInflater mInflater;

	private ImageButton btn_back;
	private TextView tv_title;
	private CommonListAdapter lvAdapter;
	private List<GlobalDataTable> lvData = new ArrayList<GlobalDataTable>();

	private int datatype;
	private String mparentid = null;

	private List<String> mselectedIds = new ArrayList<String>();

	private int mselctedtype = COMMON_SINGLESELECT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		appContext = (AppContext) this.getApplication();
		mInflater = LayoutInflater.from(this);

		setContentView(R.layout.commonstaticlist_activity);
		Intent intent = this.getIntent();
		datatype = intent.getIntExtra(KEY_DATATYPE, -1);
		mparentid = intent.getStringExtra(KEY_PARENTID);
		mselctedtype = intent.getIntExtra(KEY_SELETEDTYPE, COMMON_SINGLESELECT);
		Bundle bundle = intent.getExtras();
		String[] selectedIds = bundle.getStringArray(KEY_SELECTEDID);
		mselectedIds.clear();
		if (selectedIds != null) {
			for (int i = 0; i < selectedIds.length; i++) {
				mselectedIds.add(selectedIds[i]);
			}
		}
		btn_back = (ImageButton) findViewById(R.id.commonlist_goback);
		btn_back.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.commonlist_title);
		switch (datatype) {
		case -1:
			break;
		case DBUtils.GLOBALDATA_TYPE_EDUCATION:
			tv_title.setText(R.string.select_educationdegree);
			break;
		case DBUtils.GLOBALDATA_TYPE_JOBSTATUS:
			tv_title.setText(R.string.select_jobstatus);
			break;
		case DBUtils.GLOBALDATA_TYPE_SALARY:
			tv_title.setText(R.string.select_salary);
			break;
		case DBUtils.GLOBALDATA_TYPE_ARRIVETIME:
			tv_title.setText(R.string.select_arrivetime);
			break;
		case DBUtils.GLOBALDATA_TYPE_LANGUAGE:
			tv_title.setText(R.string.select_language);
			break;
		case DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY:
			tv_title.setText(R.string.select_jobindustry);
			break;
		case DBUtils.GLOBALDATA_TYPE_SPECIALTY:
			tv_title.setText(R.string.select_spcialty);
			break;
		case DBUtils.GLOBALDATA_TYPE_LANGUAGEMASTERY:
			tv_title.setText(R.string.select_languagemastery);
			break;
		case DBUtils.GLOBALDATA_TYPE_LANGUAGLITERACY:
			tv_title.setText(R.string.select_languageliteracy);
			break;
		case DBUtils.GLOBALDATA_TYPE_LANGUAGSPEAKING:
			tv_title.setText(R.string.select_languagespeaking);
			break;
		case DBUtils.GLOBALDATA_TYPE_CITY:
			tv_title.setText(R.string.select_location);
			break;

		}

		lv = (ListView) findViewById(R.id.commonstaticlist_listView);
		lvAdapter = new CommonListAdapter(this, lvData);
		lv.setAdapter(lvAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CommonListAdapter.CommonListItemView info = (CommonListAdapter.CommonListItemView) view
						.getTag();

				if (info == null || info.itemdata == null)
					return;
				GlobalDataTable data = info.itemdata;
				if (mselctedtype == COMMON_SINGLESELECT) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable(RESULTKEY_ENTITY, data);
					intent.putExtras(bundle);
					intent.putExtra(KEY_SELETEDTYPE, mselctedtype);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					for (int i = 0; i < mselectedIds.size(); i++) {
					}
					if (info.selectIv.getVisibility() == View.VISIBLE) {
						info.selectIv.setVisibility(View.INVISIBLE);
						mselectedIds.remove(data.getID());
					} else {
						if (mselectedIds.size() > MAX_MULTISELECT) {
							UIHelper.ToastMessage(mcontext, "不能超过"
									+ MAX_MULTISELECT + "个");
						} else {
							mselectedIds.add(data.getID());
							info.selectIv.setVisibility(View.VISIBLE);
						}
					}

				}
				/*
				 * if (data.getHavingSubClass()) { Intent intent = new
				 * Intent(mcontext, CommonStaticItemListActivity.class);
				 * intent.putExtra(KEY_DATATYPE, datatype);
				 * intent.putExtra(KEY_PARENTID, data.getParentID());
				 * intent.putExtra(KEY_SELETEDTYPE, mselctedtype); Bundle bundle
				 * = new Bundle(); bundle.putStringArray(KEY_SELECTEDID,
				 * mselctedids); intent.putExtras(bundle);
				 * startActivityForResult(intent, COMMONLIST_REQUESTCODE); }
				 * else { Intent intent = new Intent(); Bundle bundle = new
				 * Bundle(); bundle.putSerializable("entitiy", data);
				 * intent.putExtras(bundle); setResult(RESULT_OK, intent);
				 * finish(); }
				 */
			}

		});
		loadData(mHandler);
	}

	private void loadData(final Handler handler) {

		Thread t = new Thread() {
			public void run() {
				Message msg = new Message();

				List<GlobalDataTable> list = GlobalDataTable.getTpyeData(
						appContext, datatype);

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

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what >= 0) {
				List<GlobalDataTable> nlist = (List<GlobalDataTable>) msg.obj;
				if (nlist != null && nlist.size() != 0) {
					lvData.clear();
					lvData.addAll(nlist);
					lvAdapter.notifyDataSetChanged();
				} else {

				}
			} else if (msg.what == -1) {

			}

		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.commonlist_goback: {
			/*
			 * AppManager.getAppManager().finishActivity(
			 * CommonStaticItemList.class);
			 */
			Intent intent = new Intent();

			intent.putExtra(KEY_SELETEDTYPE, mselctedtype);
			Bundle bundle = new Bundle();
			int selectedsize = mselectedIds.size();
			GlobalDataTable[] selctedData = new GlobalDataTable[selectedsize];

			for (int i = 0; i < selectedsize; i++) {
				for (GlobalDataTable item : lvData) {
					if (item.getID().equals(mselectedIds.get(i))) {
						selctedData[i] = item;
						break;
					}
				}
			}
			ArrayList list = new ArrayList();
			list.add(selctedData);
			bundle.putParcelableArrayList(KEY_SELECTEDID, list);

			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			this.finish();
		}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		setResult(RESULT_OK, data);
		this.finish();
	}

	/**
	 * Adapter类
	 */
	private class CommonListAdapter extends BaseAdapter {
		private Context context;// 运行上下文
		private List<GlobalDataTable> listItems;// 数据集合
		private LayoutInflater layoutinflater;
		private int itemViewResource;// 自定义项视图源

		// private List<String> mselectedArray = null;

		private class CommonListItemView { // 自定义控件集合
			public TextView title;
			public ImageView nextIv;
			public ImageView selectIv;
			public GlobalDataTable itemdata;
		}

		/**
		 * 实例化Adapter
		 * 
		 * @param context
		 * @param data
		 * @param resource
		 */
		public CommonListAdapter(Context context, List<GlobalDataTable> data) {
			this.context = context;
			this.layoutinflater = LayoutInflater.from(context); // 创建视图容器并设置上下文

			this.listItems = data;

		}

		public int getCount() {
			return listItems.size();
		}

		public Object getItem(int arg0) {
			return listItems.get(arg0);
		}

		public long getItemId(int arg0) {
			return 0;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			MyLogger.d(TAG, "getView position = " + position);

			// 自定义视图
			CommonListItemView listItemView = null;

			if (convertView == null) {
				// 获取list_item布局文件的视图
				convertView = layoutinflater.inflate(
						R.layout.singlelinelistitem, null);

				listItemView = new CommonListItemView();
				// 获取控件对象
				listItemView.title = (TextView) convertView
						.findViewById(R.id.singlelinelistitem_title);
				listItemView.nextIv = (ImageView) convertView
						.findViewById(R.id.singlelinelistitem_rightbtn);
				listItemView.selectIv = (ImageView) convertView
						.findViewById(R.id.singlelinelistitem_rightcheckedbtn);

				listItemView.itemdata = listItems.get(position);

				// 设置控件集到convertView
				convertView.setTag(listItemView);
			} else {
				listItemView = (CommonListItemView) convertView.getTag();
				listItemView.itemdata = listItems.get(position);

			}

			// 设置文字和图片
			GlobalDataTable itemdata = listItemView.itemdata;
			listItemView.title.setText(itemdata.getName());
			if (itemdata.getHavingSubClass()) {
				listItemView.nextIv.setVisibility(View.VISIBLE);
			} else {
				listItemView.nextIv.setVisibility(View.INVISIBLE);
			}
			listItemView.selectIv.setVisibility(View.INVISIBLE);
			for (int i = 0; i < mselectedIds.size(); i++) {
				if (itemdata.getID().equals(mselectedIds.get(i))) {
					String id = itemdata.getID();
					String id2 = mselectedIds.get(i);
					listItemView.selectIv.setVisibility(View.VISIBLE);
					break;
				}
			}

			listItemView.nextIv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

				}
			});

			return convertView;
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
		boolean flag = true;
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

}

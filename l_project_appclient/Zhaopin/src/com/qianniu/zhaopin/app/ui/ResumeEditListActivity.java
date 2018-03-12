package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.ResumeEducationExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumeJobExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeLanguageExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeProjectExpEntity;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

public class ResumeEditListActivity extends BaseActivity {

	private static final String TAG = "ResumeEditListActivity";

	private static final int REQUESETCODE_LANGUAGE = 301;
	private static final int REQUESETCODE_PROJECTEXP = 302;
	private static final int REQUESETCODE_WORKEXP = 303;
	private static final int REQUESETCODE_EDUCATIONEXP = 304;

	private Context mcontext;
	private AppContext mappcontext;
	private List<ResumeLanguageExpEntity> mlanguageExpEntityList;
	private List<ResumeProjectExpEntity> mprojectExpEntityList;
	private List<ResumeJobExpEntity> mjobExpEntityList;
	private List<ResumeEducationExpEntity> meducationExpEntityList;

	private int datatype = -1;
	private int resume_id;

	private List<ItemDataEntity> mlvData = new ArrayList<ItemDataEntity>();
	private List<ItemDataEntity> mlvCache = new ArrayList<ItemDataEntity>();
	private ArrayList bundlelist;

	private ListView mlistview;
	private ListAdapter lvAdapter;
	private TextView emptytv;
	private TextView titletv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		mappcontext = (AppContext) this.getApplication();
		
		// 友盟统计
		UmShare.UmsetDebugMode(mcontext);
	
		setContentView(R.layout.resume_language_list);
		titletv = (TextView) findViewById(R.id.resume_language_list_title);
		ImageButton back = (ImageButton) findViewById(R.id.resume_language_list_goback);
		back.setOnClickListener(mlistener);
		ImageView add = (ImageView) findViewById(R.id.resume_language_list_add);
		add.setOnClickListener(mlistener);
		emptytv = (TextView) findViewById(R.id.resume_language_list_empty);

		Intent intent = this.getIntent();
		datatype = intent.getIntExtra(
				ResumeEditHomeActivity.INTENT_KEY_DATATYPE, -1);

		Bundle bundle = intent.getExtras();
		resume_id = bundle.getInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID);
		bundlelist = bundle
				.getParcelableArrayList(ResumeEditHomeActivity.INTENT_KEY_PARCELABLEARRAY);
		if (datatype == -1 || bundlelist == null) {
			errorfinish();
		}
		switch (datatype) {
		case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
			titletv.setText(R.string.resume_title_languageexp);
			String str = getResources().getString(R.string.resume_title_languageexp);
			emptytv.setText(String.format("您暂时还没添加任何%s", str));
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
			titletv.setText(R.string.resume_title_educationexp);
			String str = getResources().getString(R.string.resume_title_educationexp);
			emptytv.setText(String.format("您暂时还没添加任何%s", str));
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
			titletv.setText(R.string.resume_title_projectexp);
			String str = getResources().getString(R.string.resume_title_projectexp);
			emptytv.setText(String.format("您暂时还没添加任何%s", str));
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
			titletv.setText(R.string.resume_title_workexp);
			String str = getResources().getString(R.string.resume_title_workexp);
			emptytv.setText(String.format("您暂时还没添加任何%s", str));
		}
			break;
		default:
			break;
		}
		mlistview = (ListView) findViewById(R.id.resume_language_list_list);
		lvAdapter = new ListAdapter(this, mlvData);
		mlistview.setAdapter(lvAdapter);
		mlistview.setOnItemClickListener(mlistitemlistener);
		initData();

	}

	OnItemClickListener mlistitemlistener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Intent intent = null;
			switch (datatype) {
			case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
				intent = new Intent(mcontext,
						ResumeEditLanguageItemActivity.class);
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
				intent = new Intent(mcontext,
						ResumeEditEducationItemActivity.class);
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
				intent = new Intent(mcontext,
						ResumeEditProjectItemActivity.class);
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
				intent = new Intent(mcontext,
						ResumeEditWorkExpItemActivity.class);
			}
				break;

			default:
				break;
			}
			if (intent == null)
				return;
			Bundle bundle = new Bundle();
			ListAdapter.ListItemView item = (ListAdapter.ListItemView) view
					.getTag();
			ItemDataEntity entity = item.itemdata;
			switch (datatype) {
			case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
				bundle.putSerializable(
						ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
						mlanguageExpEntityList.get(entity.getRowlocationid()));
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
				bundle.putSerializable(
						ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
						meducationExpEntityList.get(entity.getRowlocationid()));
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
				bundle.putSerializable(
						ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
						mprojectExpEntityList.get(entity.getRowlocationid()));
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
				bundle.putSerializable(
						ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
						mjobExpEntityList.get(entity.getRowlocationid()));
			}
				break;
			default:
				break;
			}

			// bundle.putSerializable("entitiy", new ResumeBaseinfoEntity());
			bundle.putInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
			intent.putExtras(bundle);
			intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_NEWFLAG, false);
			switch (datatype) {
			case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
				startActivityForResult(intent, REQUESETCODE_LANGUAGE);
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
				startActivityForResult(intent, REQUESETCODE_EDUCATIONEXP);
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
				startActivityForResult(intent, REQUESETCODE_PROJECTEXP);
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
				startActivityForResult(intent, REQUESETCODE_WORKEXP);
			}
				break;
			default:
				break;
			}

		}
	};

	private void setResultAndFinish(int resultCode) {

		// TODO Auto-generated method stub
		Intent intent = new Intent();

		Bundle bundle = new Bundle();
		ArrayList list = new ArrayList();

		switch (datatype) {
		case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
			list.add(mlanguageExpEntityList);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
			list.add(meducationExpEntityList);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
			list.add(mprojectExpEntityList);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
			list.add(mjobExpEntityList);
		}
			break;
		default:
			break;
		}
		bundle.putParcelableArrayList(
				ResumeEditHomeActivity.INTENT_KEY_PARCELABLEARRAY, list);

		intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
		intent.putExtras(bundle);
		setResult(resultCode, intent);
		((ResumeEditListActivity) mcontext).quit();
	}

	public void quit() {
		this.finish();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {

				mlvData.clear();
				mlvData.addAll(mlvCache);
				lvAdapter.notifyDataSetChanged();
				if (lvAdapter.getCount() > 0) {
					emptytv.setVisibility(View.INVISIBLE);
				} else {
					emptytv.setVisibility(View.VISIBLE);
				}
			} else if (msg.what == -1) {

			}

		}

	};

	private void initData() {
		new RefreshDataThread(false).start();
	}
	private void refreshData() {
		Thread t = new RefreshDataThread(true);
		if(threadPool == null){
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}
	private class RefreshDataThread extends Thread {
		private boolean isRefresh;

		public RefreshDataThread(boolean isRefresh) {
			super();
			// TODO Auto-generated constructor stub
			this.isRefresh = isRefresh;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			switch (datatype) {
			case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
				if (!isRefresh)
					mlanguageExpEntityList = (List<ResumeLanguageExpEntity>) bundlelist
							.get(0);
				List<GlobalDataTable> list = GlobalDataTable.getTpyeData(
						mappcontext, DBUtils.GLOBALDATA_TYPE_LANGUAGE);
				mlvCache.clear();
				for (int i = 0; i < mlanguageExpEntityList.size(); i++) {

					String languageid = mlanguageExpEntityList.get(i)
							.getLanguageid();
					String language = null;
					for (int j = 0; j < list.size(); j++) {
						if (list.get(j).getID().equals(languageid)) {
							language = list.get(j).getName();
							break;
						}
					}
					ItemDataEntity item = new ItemDataEntity();
					item.setTitle(language);
					item.setRowlocationid(i);
					mlvCache.add(item);
				}
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
				if (!isRefresh)
					meducationExpEntityList = (List<ResumeEducationExpEntity>) bundlelist
							.get(0);

				mlvCache.clear();
				ResumeEducationExpEntity.ComparatorEntity comparator = new ResumeEducationExpEntity.ComparatorEntity();
				Collections.sort(meducationExpEntityList, comparator);
				for (int i = 0; i < meducationExpEntityList.size(); i++) {

					String schoolname = meducationExpEntityList.get(i)
							.getSchoolname();
					String startdate = meducationExpEntityList.get(i)
							.getStartdate();

					startdate = formatDateString(startdate);
					String enddate = meducationExpEntityList.get(i)
							.getEnddate();
					enddate = formatDateString(enddate);
					ItemDataEntity item = new ItemDataEntity();
					item.setTitle(schoolname);
					String timeinfo = new String();
					if(startdate == null)
						timeinfo += "...";
					else
						timeinfo += startdate;
					timeinfo += "至";
					if(enddate == null)
						timeinfo += "...";
					else
						timeinfo += enddate;
					item.setSubtitle(timeinfo);
					item.setRowlocationid(i);
					mlvCache.add(item);
				}
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
				if (!isRefresh)
					mprojectExpEntityList = (List<ResumeProjectExpEntity>) bundlelist
							.get(0);

				mlvCache.clear();
				ResumeProjectExpEntity.ComparatorEntity comparator = new ResumeProjectExpEntity.ComparatorEntity();
				Collections.sort(mprojectExpEntityList, comparator);
				for (int i = 0; i < mprojectExpEntityList.size(); i++) {

					String projectname = mprojectExpEntityList.get(i)
							.getProjectname();
					String startdate = mprojectExpEntityList.get(i)
							.getStartdate();
					String enddate = mprojectExpEntityList.get(i).getEnddate();
					enddate = formatDateString(enddate);
					startdate = formatDateString(startdate);

					ItemDataEntity item = new ItemDataEntity();
					item.setTitle(projectname);
					String timeinfo = new String();
					if(startdate == null)
						timeinfo += "...";
					else
						timeinfo += startdate;
					timeinfo += "至";
					if(enddate == null)
						timeinfo += "...";
					else
						timeinfo += enddate;
					item.setSubtitle(timeinfo);
					item.setRowlocationid(i);
					mlvCache.add(item);
				}
			}
				break;
			case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
				if (!isRefresh)
					mjobExpEntityList = (List<ResumeJobExpEntity>) bundlelist
							.get(0);

				mlvCache.clear();
				ResumeJobExpEntity.ComparatorEntity comparator = new ResumeJobExpEntity.ComparatorEntity();
				Collections.sort(mjobExpEntityList, comparator);
				for (int i = 0; i < mjobExpEntityList.size(); i++) {

					String jobcompany = mjobExpEntityList.get(i).getCompany();
					String startdate = mjobExpEntityList.get(i).getStartdate();
					String enddate = mjobExpEntityList.get(i).getEnddate();
					enddate = formatDateString(enddate);
					startdate = formatDateString(startdate);
					ItemDataEntity item = new ItemDataEntity();
					item.setTitle(jobcompany);
					String timeinfo = new String();
					if(startdate == null)
						timeinfo += "...";
					else
						timeinfo += startdate;
					timeinfo += "至";
					if(enddate == null)
						timeinfo += "...";
					else
						timeinfo += enddate;
					item.setSubtitle(timeinfo);
					item.setRowlocationid(i);
					mlvCache.add(item);
				}
			}
				break;
			default: {
				errorfinish();
			}
				break;
			}

			mHandler.sendEmptyMessage(1);
		}

	}

	/**
	 * 将 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 转为 yyyy-MM-dd
	 * 
	 * @param datestr
	 */
	private String formatDateString(String datestr) {
		String formatdate = null;
		if (datestr != null && datestr.length() > 0) {
			Date date = StringUtils.toDate(datestr);
			if (date != null) {
				Calendar cal = CommonUtils.DateToCalendar(date);
				formatdate = CommonUtils.getYYMMDDString(cal);
			}
		}
		return formatdate;
	}

	private void errorfinish() {
		UIHelper.ToastMessage(mcontext, "数据错误");
		finish();
	}

	private class ItemDataEntity {
		private String title;
		private String subtitle;
		// item 数据对应在原list的位置
		private int rowlocationid;

		public final int getRowlocationid() {
			return rowlocationid;
		}

		public final void setRowlocationid(int rowlocationid) {
			this.rowlocationid = rowlocationid;
		}

		public final String getTitle() {
			return title;
		}

		public final void setTitle(String title) {
			this.title = title;
		}

		public final String getSubtitle() {
			return subtitle;
		}

		public final void setSubtitle(String subtitle) {
			this.subtitle = subtitle;
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
			setResultAndFinish(RESULT_OK);
			this.finish();
		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}

	OnClickListener mlistener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub

			int id = view.getId();
			switch (id) {
			case R.id.resume_language_list_goback: {
				setResultAndFinish(RESULT_OK);
			}
				break;

			case R.id.resume_language_list_add: {
				// TODO Auto-generated method stub
				Intent intent = null;
				switch (datatype) {
				case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
					// 友盟统计--简历语言技能--添加按钮
					UmShare.UmStatistics(mcontext, "ResumeEditList_Language_AddButton");
					
					intent = new Intent(mcontext,
							ResumeEditLanguageItemActivity.class);
				}
					break;
				case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
					// 友盟统计--简历教育经历--添加按钮
					UmShare.UmStatistics(mcontext, "ResumeEditList_Education_AddButton");
					
					intent = new Intent(mcontext,
							ResumeEditEducationItemActivity.class);
				}
					break;
				case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
					// 友盟统计--简历项目经历--添加按钮
					UmShare.UmStatistics(mcontext, "ResumeEditList_Projectexp_AddButton");
					
					intent = new Intent(mcontext,
							ResumeEditProjectItemActivity.class);
				}
					break;
				case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
					// 友盟统计--简历工作经验--添加按钮
					UmShare.UmStatistics(mcontext, "ResumeEditList_Workexperience_AddButton");
					
					intent = new Intent(mcontext,
							ResumeEditWorkExpItemActivity.class);
				}
					break;
				default:
					break;
				}
				if (intent == null)
					return;
				Bundle bundle = new Bundle();

				switch (datatype) {
				case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
					bundle.putSerializable(
							ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
							new ResumeLanguageExpEntity());
				}
					break;
				case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
					bundle.putSerializable(
							ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
							new ResumeEducationExpEntity());
				}
					break;
				case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
					bundle.putSerializable(
							ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
							new ResumeProjectExpEntity());
				}
					break;
				case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
					bundle.putSerializable(
							ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
							new ResumeJobExpEntity());
				}
					break;
				default:
					break;
				}

				// bundle.putSerializable("entitiy", new
				// ResumeBaseinfoEntity());
				bundle.putInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID,
						resume_id);
				intent.putExtras(bundle);
				intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_NEWFLAG, true);
				switch (datatype) {
				case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
					startActivityForResult(intent, REQUESETCODE_LANGUAGE);
				}
					break;
				case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
					startActivityForResult(intent, REQUESETCODE_EDUCATIONEXP);
				}
					break;
				case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
					startActivityForResult(intent, REQUESETCODE_PROJECTEXP);
				}
					break;
				case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
					startActivityForResult(intent, REQUESETCODE_WORKEXP);
				}
					break;
				default:
					break;
				}

			}
				break;
			}

		}

	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESETCODE_LANGUAGE: {
				resume_id = data.getIntExtra(
						ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
				Bundle bundle = data.getExtras();
				ResumeLanguageExpEntity entity = (ResumeLanguageExpEntity) bundle
						.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
				entity.setBcompleted(ResumeLanguageExpEntity.isCompletedForLanguageExp(entity));
				int size = mlanguageExpEntityList.size();
				int position = -1;
				for (int i = 0; i < size; i++) {
					if (mlanguageExpEntityList.get(i).getItemid()
							.equals(entity.getItemid())) {
						position = i;
						break;
					}
				}
				if (entity.getStatus() == ResumeEntity.RESUME_BLOCKSTATUS_DEL) {

					if (position != -1) {
						mlanguageExpEntityList.remove(position);
					}
				} else {
					if (position != -1) {
						mlanguageExpEntityList.remove(position);
					}
					mlanguageExpEntityList.add(entity);
				}
				refreshData();
			}
				break;
			case REQUESETCODE_EDUCATIONEXP: {
				resume_id = data.getIntExtra(
						ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
				Bundle bundle = data.getExtras();
				ResumeEducationExpEntity entity = (ResumeEducationExpEntity) bundle
						.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
				entity.setBcompleted(ResumeEducationExpEntity.isCompletedForEducationExp(entity));
				int size = meducationExpEntityList.size();
				int position = -1;
				for (int i = 0; i < size; i++) {
					if (meducationExpEntityList.get(i).getItemid()
							.equals(entity.getItemid())) {
						position = i;
						break;
					}
				}
				if (entity.getStatus() == ResumeEntity.RESUME_BLOCKSTATUS_DEL) {

					if (position != -1) {
						meducationExpEntityList.remove(position);
					}
				} else {
					if (position != -1) {
						meducationExpEntityList.remove(position);
					}
					meducationExpEntityList.add(entity);
				}
				refreshData();
			}
				break;
			case REQUESETCODE_PROJECTEXP: {
				resume_id = data.getIntExtra(
						ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
				Bundle bundle = data.getExtras();
				ResumeProjectExpEntity entity = (ResumeProjectExpEntity) bundle
						.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
				entity.setBcompleted(ResumeProjectExpEntity.isCompletedForProjectExp(entity));
				int size = mprojectExpEntityList.size();
				int position = -1;
				for (int i = 0; i < size; i++) {
					if (mprojectExpEntityList.get(i).getItemid()
							.equals(entity.getItemid())) {
						position = i;
						break;
					}
				}
				if (entity.getStatus() == ResumeEntity.RESUME_BLOCKSTATUS_DEL) {

					if (position != -1) {
						mprojectExpEntityList.remove(position);
					}
				} else {
					if (position != -1) {
						mprojectExpEntityList.remove(position);
					}
					mprojectExpEntityList.add(entity);
				}
				refreshData();
			}
				break;
			case REQUESETCODE_WORKEXP: {
				resume_id = data.getIntExtra(
						ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
				Bundle bundle = data.getExtras();
				ResumeJobExpEntity entity = (ResumeJobExpEntity) bundle
						.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
				entity.setBcompleted(ResumeJobExpEntity.isCompletedForJobExp(entity));
				int size = mjobExpEntityList.size();
				int position = -1;
				for (int i = 0; i < size; i++) {
					if (mjobExpEntityList.get(i).getItemid()
							.equals(entity.getItemid())) {
						position = i;
						break;
					}
				}
				if (entity.getStatus() == ResumeEntity.RESUME_BLOCKSTATUS_DEL) {

					if (position != -1) {
						mjobExpEntityList.remove(position);
					}
				} else {
					if (position != -1) {
						mjobExpEntityList.remove(position);
					}
					mjobExpEntityList.add(entity);
				}
				refreshData();
			}
				break;

			}
		}
	}

	/**
	 * Adapter类
	 */
	private class ListAdapter extends BaseAdapter {
		private Context context;// 运行上下文
		private List<ItemDataEntity> listItems;// 数据集合
		private LayoutInflater layoutinflater;
		private int itemViewResource;// 自定义项视图源

		// private List<String> mselectedArray = null;

		public class ListItemView { // 自定义控件集合
			public TextView title;
			public TextView subtitle;
			public ImageView pointIv;
			public ItemDataEntity itemdata;
		}

		/**
		 * 实例化Adapter
		 * 
		 * @param context
		 * @param data
		 * @param resource
		 */
		public ListAdapter(Context context, List<ItemDataEntity> data) {
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
			ListItemView listItemView = null;

			if (convertView == null) {
				// 获取list_item布局文件的视图
				convertView = layoutinflater.inflate(
						R.layout.resume_commonlistitem, null);

				listItemView = new ListItemView();
				// 获取控件对象
				listItemView.title = (TextView) convertView
						.findViewById(R.id.listitem_title);
				listItemView.subtitle = (TextView) convertView
						.findViewById(R.id.listitem_subtitle);
				listItemView.pointIv = (ImageView) convertView
						.findViewById(R.id.listitem_rightbtn);

				listItemView.itemdata = listItems.get(position);

				// 设置控件集到convertView
				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
				listItemView.itemdata = listItems.get(position);

			}

			// 设置文字和图片
			ItemDataEntity itemdata = listItemView.itemdata;
			if (itemdata.getTitle() != null)
				listItemView.title.setText(itemdata.getTitle());
			if (itemdata.getSubtitle() != null) {
				listItemView.subtitle.setText(itemdata.getSubtitle());
				listItemView.subtitle.setVisibility(View.VISIBLE);
			} else {
				listItemView.subtitle.setVisibility(View.GONE);
			}

			return convertView;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 友盟统计
		UmShare.UmResume(mcontext);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 友盟统计
		UmShare.UmPause(mcontext);
	}
}

package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.bean.CampaignEntityList;
import com.qianniu.zhaopin.app.bean.NoticeEntity;
import com.qianniu.zhaopin.app.bean.NoticeEntityList;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.constant.ActionConstants;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.service.ConnectionService;
import com.qianniu.zhaopin.app.view.ImageRadioButton;
import com.qianniu.zhaopin.app.view.ImageRadioButton.OnCheckedChangeListener;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends BaseFragmentActivity {
	public static final int HANDCODE_MSG_NEW = 1;

	private static final String KEY_CHECKED_TAB = "checkedTab";

	private Context m_Context;
	private AppContext mappContext;
	private ImageRadioButton fbNews;
	private ImageRadioButton fbReward;
	private ImageRadioButton fbForum;
	private ImageRadioButton fbactive;
	private ImageRadioButton[] mButtons;
	private ImageView newPoint_News;
	private ImageView newPoint_Reward;
	private ImageView newPoint_Extras;
	private ImageView newPoint_Active;

	String tabs[] = { "news", "jump", "forum", "active" };
	
	Class[] m_cls = new Class[4];
	private FragmentTabHost mTabHost;
	private ImageRadioButton mcheckedbtn = null;

	private boolean m_bOnCreateing;
	private int m_checkedfragment = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_bOnCreateing = true;
		m_Context = this;
		mappContext = (AppContext) this.getApplication();
		setContentView(R.layout.main_fragment_tabs);

		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
		
		initClass();
		
		initFragmentJumpMonitor(m_Context);
		initFragmentNewMonitor(m_Context);
		initNewPoint(m_Context, mhHandler);

		init();
		if (savedInstanceState != null) {
			m_checkedfragment = savedInstanceState.getInt(KEY_CHECKED_TAB);
			mButtons[m_checkedfragment].setChecked(true);
		} else {
			Intent intent = getIntent();
			String action = intent.getAction();
			setDefaultFragment();
		}
		

		ConnectionService.startService(m_Context);
		initDataManager();

		m_bOnCreateing = false;
	}

	private void initDataManager() {
		CampaignEntityList.deleteOldDataInDb(mappContext);
	}

	private void init() {
		mTabHost = (FragmentTabHost) this.findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.getTabWidget().setVisibility(View.GONE);
		for (int i = 0; i < tabs.length; i++) {
			// View tabView =
			// this.getLayoutInflater().inflate(R.layout.tab_indicator, null);
			mTabHost.addTab(mTabHost.newTabSpec(tabs[i]).setIndicator(tabs[i]),
					m_cls[i], null);
		}
		/*
		 * mTabHost.addTab(mTabHost.newTabSpec("news").setIndicator("news"),
		 * Fragment_infotype.class, null);
		 * mTabHost.addTab(mTabHost.newTabSpec("jump"
		 * ).setIndicator("jump"),Fragment3.class, null);
		 * mTabHost.addTab(mTabHost
		 * .newTabSpec("forum").setIndicator("forum"),Fragment4.class, null);
		 * mTabHost
		 * .addTab(mTabHost.newTabSpec("active").setIndicator("active"),
		 * Fragment5 .class, null);
		 */
		fbNews = (ImageRadioButton) findViewById(R.id.main_footbar_news);
		fbNews.setOnCheckedChangeListener(mCheckChangeListenter);
		fbReward = (ImageRadioButton) findViewById(R.id.main_footbar_reward);
		fbReward.setOnCheckedChangeListener(mCheckChangeListenter);
		fbForum = (ImageRadioButton) findViewById(R.id.main_footbar_extra);
		fbForum.setOnCheckedChangeListener(mCheckChangeListenter);
		fbactive = (ImageRadioButton) findViewById(R.id.main_footbar_my);
		fbactive.setOnCheckedChangeListener(mCheckChangeListenter);
		mButtons = new ImageRadioButton[4];
		mButtons[0] = fbNews;
		mButtons[1] = fbReward;
		mButtons[2] = fbForum;
		mButtons[3] = fbactive;
		newPoint_News = (ImageView) findViewById(R.id.main_tab_news_unread_iv);
		newPoint_Reward = (ImageView) findViewById(R.id.main_tab_active_jump_iv);
		newPoint_Extras = (ImageView) findViewById(R.id.main_tab_active_forum_iv);
		newPoint_Active = (ImageView) findViewById(R.id.main_tab_active_unread_iv);
		
		// if (!mappContext.isLogin()) {
		// UIHelper.showLoginActivity(mcontext);
		// }
		// UIHelper.showLoginActivityForResult(MainActivity.this);
	}

	OnCheckedChangeListener mCheckChangeListenter = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(View buttonView, boolean isChecked) {
			// TODO Auto-generated method stub

			ImageRadioButton btn = (ImageRadioButton) buttonView;
			int checkedId = buttonView.getId();
			if (!isChecked)
				return;
			if (isChecked) {
				btn.setCheackable(false);
				if (mcheckedbtn != null)
					mcheckedbtn.setCheackable(true);
				mcheckedbtn = btn;
			}
			for (int i = 0; i < mButtons.length; i++) {
				if (mButtons[i].getId() != checkedId)
					mButtons[i].setChecked(false);
				// mButtons[i].toggle();
			}
			switch (checkedId) {
			case R.id.main_footbar_news:
				// 友盟统计--消息主按钮
				UmShare.UmStatistics(m_Context, "Msg_MainButton");

				// mButtons[0].setChecked(isChecked);
				if (isChecked) {
					mTabHost.setCurrentTabByTag(tabs[0]);
					m_checkedfragment = 0;
				}

				break;
			case R.id.main_footbar_reward:
				// 友盟统计--任务主按钮
				UmShare.UmStatistics(m_Context, "Reward_MainButton");
				// mButtons[1].setChecked(isChecked);
				if (isChecked) {
					// 判断是否是第一次启动
					if (DMSharedPreferencesUtil.VALUE_FLG_FIRSTREWARD != DMSharedPreferencesUtil
							.getSharePreInt(m_Context,
									DMSharedPreferencesUtil.DM_HOTLABEL_DB,
									DMSharedPreferencesUtil.FIELD_FIRST_REWARD)) {
						// 加载引导界面
						Intent intent = new Intent(m_Context,
								GuideActivity.class);
						intent.putExtra(
								HeadhunterPublic.GUIDE_DATATRANSFER_TYPE,
								HeadhunterPublic.GUIDETYPE_REWARD);
						m_Context.startActivity(intent);

					}
					mTabHost.setCurrentTabByTag(tabs[1]);
					m_checkedfragment = 1;
				}
				break;
			case R.id.main_footbar_extra:
				// 友盟统计--号外主按钮
				UmShare.UmStatistics(m_Context, "Extra_MainButton");
				// mButtons[2].setChecked(isChecked);
				if (isChecked) {
					mTabHost.setCurrentTabByTag(tabs[2]);
					m_checkedfragment = 2;
				}
				break;
			case R.id.main_footbar_my:
				// 友盟统计--"我"主按钮
				UmShare.UmStatistics(m_Context, "My_MainButton");
				// mButtons[3].setChecked(isChecked);

				if (isChecked) {
					/*
					 * if (!mappContext.isLogin()) {
					 * UIHelper.showLoginActivity(m_Context); }
					 */

					// 判断是否是第一次启动
					if (DMSharedPreferencesUtil.VALUE_FLG_FIRSTMY != DMSharedPreferencesUtil
							.getSharePreInt(m_Context,
									DMSharedPreferencesUtil.DM_HOTLABEL_DB,
									DMSharedPreferencesUtil.FIELD_FIRST_MY)) {
						// 加载引导界面
						Intent intent = new Intent(m_Context,
								GuideActivity.class);
						intent.putExtra(
								HeadhunterPublic.GUIDE_DATATRANSFER_TYPE,
								HeadhunterPublic.GUIDETYPE_MY);
						m_Context.startActivity(intent);
					}

					mTabHost.setCurrentTabByTag(tabs[3]);
					m_checkedfragment = 3;
				}
				break;
			}

		}
	};

	private void setDefaultFragment() {
		mButtons[2].setChecked(true);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		m_Context.unregisterReceiver(mfragmentJumpReceiver);
		m_Context.unregisterReceiver(mfragmentNewReceiver);
		DBUtils db = DBUtils.getInstance(this);
		db.closeDb();
	}

	// 用于判断当前activity是否是活动的；
	private boolean bactive = false;

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		bactive = false;

		// 友盟
		UmShare.UmPause(m_Context);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bactive = true;

		// 友盟
		UmShare.UmResume(m_Context);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != -1)
			return;
		switch (requestCode) {
		case ActivityRequestCode.RESULT_ACTIVITY_LOGIN: {
			// setDefaultFragment();
		}
			break;
		}
	}

	private BroadcastReceiver mfragmentJumpReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(ActionConstants.ACTION_FRAGMENT_INFO)) {
				mButtons[0].setChecked(true);
			} else if (action.equals(ActionConstants.ACTION_FRAGMENT_REWARD)) {
				mButtons[1].setChecked(true);
			} else if (action.equals(ActionConstants.ACTION_FRAGMENT_EXTRA)) {
				mButtons[2].setChecked(true);
			} else if (action.equals(ActionConstants.ACTION_FRAGMENT_ACTIVE)) {
				mButtons[3].setChecked(true);
			}

		}
	};

	private void initFragmentJumpMonitor(Context context) {

		IntentFilter filter = new IntentFilter();
		filter.addAction(ActionConstants.ACTION_FRAGMENT_INFO);
		filter.addAction(ActionConstants.ACTION_FRAGMENT_EXTRA);
		filter.addAction(ActionConstants.ACTION_FRAGMENT_REWARD);
		filter.addCategory(ActionConstants.CATEGORY_FRAGMENT_DEFAULT);

		context.registerReceiver(mfragmentJumpReceiver, filter);

	}

	private BroadcastReceiver mfragmentNewReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(ActionConstants.ACTION_FRAGMENT_INFO_NEW)) {
				// newPoint_News.setVisibility(View.VISIBLE);
				setNewPointStateForNews(true);
			} else if (action.equals(ActionConstants.ACTION_FRAGMENT_EXTRA_NEW)) {
				newPoint_Extras.setVisibility(View.VISIBLE);
			}

		}
	};

	private void setNewPointStateForNews(boolean bnew) {
		if (bnew) {
			newPoint_News.setVisibility(View.VISIBLE);
			if (mcheckedbtn != null
					&& mcheckedbtn.getId() == R.id.main_footbar_news) {
				if (bactive) {
					Fragment fragment = MainActivity.this
							.getSupportFragmentManager().findFragmentByTag(
									tabs[0]);
					Fragment_msg fragmentmsg = (Fragment_msg) fragment;
					fragmentmsg.initLoadListData();
				}
			}
		}

	}

	private void initFragmentNewMonitor(Context context) {

		IntentFilter filter = new IntentFilter();
		filter.addAction(ActionConstants.ACTION_FRAGMENT_INFO_NEW);
		filter.addCategory(Intent.CATEGORY_DEFAULT);

		context.registerReceiver(mfragmentNewReceiver, filter);

	}

	private void initNewPoint(final Context context, final Handler handler) {
		Thread t = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				NoticeEntityList list = NoticeEntity.getNoticeEntityListFromDb(
						context, DBUtils.SERVICEDATA_TYPE_NEWATMESSAGE);
				if (list != null && list.getList() != null) {
					if (list.getList().size() > 0) {
						handler.sendEmptyMessage(HANDCODE_MSG_NEW);
						NoticeEntity.deleteTypeDataFromDb(context,
								DBUtils.SERVICEDATA_TYPE_NEWATMESSAGE);
					}
				}
			}

		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	private Handler mhHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDCODE_MSG_NEW: {
				newPoint_News.setVisibility(View.VISIBLE);
			}
				break;

			}
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mappContext.showQuitAlertDialog(MainActivity.this);
		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		m_checkedfragment = savedInstanceState.getInt(KEY_CHECKED_TAB);
		mButtons[m_checkedfragment].setChecked(true);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub

		outState.putInt(KEY_CHECKED_TAB, m_checkedfragment);
		super.onSaveInstanceState(outState);

	}
	
	/**
	 * 初始化Class
	 */
	private void initClass(){
		switch(ConfigOptions.getTaskViewMode()){
		case ConfigOptions.TASKVIEW_MODE_WATERFALL:			// 瀑布流模式
			{
				if(null == m_cls){
					m_cls = new Class[3];
				}
				
				m_cls[0] = Fragment_msg.class;
				//m_cls[1] = Fragment_rewardwaterfall.class;
				m_cls[1] = Fragment_rewardViewPager.class;
				m_cls[2] = Fragment_extraMain.class;
				//m_cls[3] = Fragment_my.class;
				m_cls[3] = Fragment_myNew.class;
			}
			break;
		case ConfigOptions.TASKVIEW_MODE_VERTICALLIST:		// 垂直List模式
		default:
			{
				if(null == m_cls){
					m_cls = new Class[3];
				}
				
				m_cls[0] = Fragment_msg.class;
				m_cls[1] = Fragment_reward.class;
				m_cls[2] = Fragment_extraMain.class;
				//m_cls[3] = Fragment_my.class;
				m_cls[3] = Fragment_myNew.class;
			}
			break;
		}
	}
}

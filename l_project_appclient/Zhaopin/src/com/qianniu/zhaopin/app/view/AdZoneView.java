package com.qianniu.zhaopin.app.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.ActionJumpEntity;
import com.qianniu.zhaopin.app.bean.AdZoneEntity;
import com.qianniu.zhaopin.app.bean.AdZoneList;
import com.qianniu.zhaopin.app.bean.ForumTypeList;
import com.qianniu.zhaopin.app.bean.ItemInfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoList;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.ActionConstants;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class AdZoneView extends FrameLayout {

	private static final String TAG = "AdZoneView";

	private static final int HANDCODE_DATA_FAIL = -1;
	private static final int HANDCODE_REFRESH = 0;
	private static final int HANDCODE_DATA_OK = 1;
	private static final int HANDCODE_VISBLE = 3;

	public static final int ADZONE_ID_NONE = -1;// 无
	public static final int ADZONE_ID_INFO = DBUtils.ADZONE_TYPE_INFO_CATALOG; // 消息栏宣传位
	public static final int ADZONE_ID_EXTRA = DBUtils.ADZONE_TYPE_REWARD_CATALOG; // 任务栏宣传位
	public static final int ADZONE_ID_LOGIN = DBUtils.ADZONE_TYPE_LOGIN; // 登录宣传位
	public static final int ADZONE_ID_MIX = DBUtils.ADZONE_TYPE_MIX; // 号外宣传位
	public static final int ADZONE_ID_NEWS = DBUtils.ADZONE_TYPE_NEWS; // 资讯宣传位
	public static final int ADZONE_ID_FAMOUS_MAN = DBUtils.ADZONE_TYPE_FAMOUS_MAN; // 号外宣传位
	public static final int ADZONE_ID_FAMOUS_COMPANY = DBUtils.ADZONE_TYPE_FAMOUS_COMPANY; // 资讯宣传位

	public static final AdSize SIZE_MATCHPARENT_SMALL = new AdSize(
			LayoutParams.MATCH_PARENT, 50);
	public static final AdSize SIZE_MATCHPARENT_MIDDLE = new AdSize(
			LayoutParams.MATCH_PARENT, 135);

	public static final String ADZONE_HTTP = "zones"; // 号外栏宣传位
	public static final String ADZONE_HTTP_INFOFILED = "message"; // 消息栏字段
	public static final String ADZONE_HTTP_REWARDFILED = "jobs"; // 任务栏字段
	public static final String ADZONE_HTTP_LOGINFILED = "login"; // 登录栏字段
	public static final String ADZONE_HTTP_MIXFILED = "extra"; // 号外栏字段
	public static final String ADZONE_HTTP_NEWSFILED = "extranews"; // 资讯栏字段
	public static final String ADZONE_HTTP_FAMOUS_MAN = "app/boss"; // 名人字段
	public static final String ADZONE_HTTP_FAMOUS_COMPANY = "app/company"; // 名企字段
	
	private static final int ADZONE_REFRESHTIME = 3;// S
	private static final int ADZONE_GETNEWAD_INVERTALTIME = /* 24 * 60 */2 * 60 * 1000;

	private Context mcontext;
	private AppContext appContext;// 全局Context
	private ThreadPoolController threadPool;

	private ViewPager adviewPager;
	private ViewGroup indicateContainer;
	private int madzoneId;

	private boolean hasfocus;
	private boolean bvisible;
	private ScrollTask mscrolltask = null;

	private BitmapManager bmpManager;
	private AdZoneList adzonelist;
	private MyAdapter madapter;
	private List<ViewGroup> viewContainer = new ArrayList<ViewGroup>(); // 滑动的图片集合
	private List<View> dots = new ArrayList<View>(); // 图片标题正文的那些点
	private int[] imageurl; // 图片ID
	private int currentItem = 0; // 当前图片的索引号
	private LayoutInflater minflater;
	private ScheduledExecutorService scheduledExecutorService;
	private boolean mbindicate = true;
	private boolean mbtitle = true;
	private AdSize madsize = null;

	private static final int ADZONE_TITLE_HEIGHT = 27;// DP
	private static final int ADZONE_DOTS_HEIGHT = 13;// DP

	public AdZoneView(Context context, int adzoneId) {
		this(context, null, adzoneId);
		// TODO Auto-generated constructor stub
	}

	public AdZoneView(Context context, int adzoneId, AdSize adsize) {
		this(context, null, adzoneId, adsize, true, true);
		// TODO Auto-generated constructor stub
	}

	public AdZoneView(Context context, int adzoneId, AdSize adsize,
			boolean btitle, boolean bindicate) {
		this(context, null, adzoneId, adsize, btitle, bindicate);
		// TODO Auto-generated constructor stub
	}

	public AdZoneView(Context context, AttributeSet attrs) {

		this(context, attrs, ADZONE_ID_NONE, null, true, true);
	}

	public AdZoneView(Context context, AttributeSet attrs, int adzoneId) {
		this(context, null, adzoneId, null, true, true);
	}

	public AdZoneView(Context context, AttributeSet attrs, int adzoneId,
			AdSize adsize, boolean btitle, boolean bindicate) {

		super(context, attrs);
		// 友盟统计
		UmShare.UmsetDebugMode(context);
		mcontext = context;
		initView(adzoneId, adsize, btitle, bindicate);
	}

	private void initView(int adzoneId, AdSize adsize, boolean btitle,
			boolean bindicate) {
		madsize = adsize == null ? SIZE_MATCHPARENT_MIDDLE : adsize;

		appContext = (AppContext) mcontext.getApplicationContext();
		madzoneId = adzoneId;
		mbindicate = bindicate;
		mbtitle = btitle;
		float density = this.getResources().getDisplayMetrics().density;

		int viewwidth = madsize.getWidth() < 0 ? (madsize.getWidth())
				: ((int) (density * madsize.getWidth()));
		int viewheight = madsize.getHeight() < 0 ? (madsize.getHeight())
				: ((int) (density * (madsize.getHeight()
						+ (mbtitle ? ADZONE_TITLE_HEIGHT : 0) + (mbindicate ? ADZONE_DOTS_HEIGHT
						: 0))));
		this.setLayoutParams(new LayoutParams(viewwidth, viewheight));
		minflater = (LayoutInflater) mcontext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentview = minflater.inflate(R.layout.adzoneview_layout, null);

		adviewPager = (ViewPager) contentview.findViewById(R.id.adzone_vp);
		madapter = new MyAdapter();

		adviewPager.setAdapter(madapter);
		adviewPager.setOnPageChangeListener(new MyPageChangeListener());
		bmpManager = new BitmapManager(null);
		indicateContainer = (ViewGroup) contentview
				.findViewById(R.id.adzone_dotscontainer);
		addView(contentview);
		loadData(madzoneId, handler);
	}

	/*
	 * mrefreshRunnable = new RefreshRunnable(adcustom.adParam);
	 * mcontrollerHandler.postDelayed(mrefreshRunnable, adcustom.refreshTime *
	 * 1000);
	 */
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

	}



	public int getMadzoneId() {
		return madzoneId;
	}

	public void setMadzoneId(int madzoneId) {
		this.madzoneId = madzoneId;
	}

	/**
	 * 换行切换任务
	 * 
	 * @author Administrator
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
/*			Log.e("AdZoneView", "madzoneId =" + madzoneId + ",adviewPager ="
					+ adviewPager + ",");
			Log.e("AdZoneView", "viewContainer.size() =" + viewContainer.size()
					+ ",currentItem = " + currentItem);*/
			if(adviewPager == null)
				return;
			synchronized (adviewPager) {
				if (viewContainer.size() <= 0)
					return;
				int newItem = (currentItem + 1) % viewContainer.size();
				Message msg = new Message();
				msg.what = HANDCODE_REFRESH;
				msg.obj = newItem;
				handler.sendMessage(msg); // 通过Handler切换图片
			}
		}

	}

	/*
	 * public void startAutoPlay() { scheduledExecutorService =
	 * Executors.newSingleThreadScheduledExecutor(); //
	 * 当Activity显示出来后，每两秒钟切换一次图片显示
	 * scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2,
	 * TimeUnit.SECONDS); }
	 * 
	 * public void stopAutoPlay() { if (scheduledExecutorService != null)
	 * scheduledExecutorService.shutdown(); }
	 */
	public void startAutoPlay() {
		if (mscrolltask != null) {
			handler.removeCallbacks(mscrolltask);
		}

		mscrolltask = new ScrollTask();
		long dealytime = ConfigOptions.getAdScrollUpdate();
		handler.postDelayed(mscrolltask, dealytime);
	}

	public String getUrlByZoneId(int zoneId) {
		String url = new String(URLs.HTTP + URLs.HOST);
		switch (zoneId) {
		case ADZONE_ID_INFO: {
			url += ("/" + ADZONE_HTTP + "/" + ADZONE_HTTP_INFOFILED);
		}
			break;
		case ADZONE_ID_EXTRA: {
			url += ("/" + ADZONE_HTTP + "/" + ADZONE_HTTP_REWARDFILED);
		}
			break;
		case ADZONE_ID_LOGIN: {
			url += ("/" + ADZONE_HTTP + "/" + ADZONE_HTTP_LOGINFILED);
		}
			break;
		case ADZONE_ID_MIX: {
			url += ("/" + ADZONE_HTTP + "/" + ADZONE_HTTP_MIXFILED);
		}
			break;
		case ADZONE_ID_NEWS: {
			url += ("/" + ADZONE_HTTP + "/" + ADZONE_HTTP_NEWSFILED);
		}
			break;
		case ADZONE_ID_FAMOUS_MAN: {
			url += ("/" + ADZONE_HTTP + "/" + ADZONE_HTTP_FAMOUS_MAN);
		}
			break;
		case ADZONE_ID_FAMOUS_COMPANY: {
			url += ("/" + ADZONE_HTTP + "/" + ADZONE_HTTP_FAMOUS_COMPANY);
		}
			break;
		default:
			url = null;
			break;
		}

		return url;
	}

	// 以前选中的位置

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {

		private int oldposition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {

			ImageView oldview = (ImageView) dots.get(oldposition);
			if (oldview != null) {
				oldview.setImageResource(R.drawable.page_indicator_unfocused);
			}
			ImageView newview = (ImageView) dots.get(position);
			if (newview != null) {
				newview.setImageResource(R.drawable.page_indicator_focused);
			}

			oldposition = position;
			currentItem = position;
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	// 切换当前显示的图片
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case HANDCODE_DATA_FAIL:
				adviewPager.setCurrentItem(currentItem);// 切换当前显示的图片
				break;

			case HANDCODE_REFRESH:
				adviewPager.setCurrentItem((Integer) msg.obj);// 切换当前显示的图片
				if (hasfocus) {
					startAutoPlay();
				}
				break;
			case HANDCODE_DATA_OK:
				adzonelist = (AdZoneList) msg.obj;
				if (adzonelist.getValidate().OK()
						&& adzonelist.getArraylist().size() > 0) {
					viewContainer.clear();
					dots.clear();
					indicateContainer.removeAllViews();

					for (int i = 0; i < adzonelist.getArraylist().size(); i++) {
						AdZoneEntity entity = adzonelist.getArraylist().get(i);
						ViewGroup itemView = (ViewGroup) minflater.inflate(
								R.layout.adzone_itemview, null);
						TextView titleview = (TextView) itemView
								.findViewById(R.id.adzone_item_title);
						/*
						 * ImageView imageView = (ImageView) itemView
						 * .findViewById(R.id.adzone_item_imgview);
						 */
						LinearLayout iv_container = (LinearLayout) itemView
								.findViewById(R.id.adzone_item_imgview_bg);
						ImageView imageView = new ImageView(mcontext);
						float density = mcontext.getResources()
								.getDisplayMetrics().density;

						int viewheight = madsize.getHeight() < 0 ? (madsize
								.getHeight()) : ((int) (density * (madsize
								.getHeight())));
						ViewGroup.LayoutParams lp_iv = new LayoutParams(
								ViewGroup.LayoutParams.MATCH_PARENT, viewheight);
						imageView
								.setBackgroundResource(R.drawable.qianniu_bg_small);
						imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
						// imageView.setAdjustViewBounds(true);
						iv_container.addView(imageView, lp_iv);

						/*
						 * imageView.setBackgroundColor(getResources().getColor(R
						 * .color.white));
						 * imageView.setScaleType(ScaleType.CENTER_CROP);
						 */

						bmpManager.loadBitmap(entity.getPic_url(), imageView);

						itemView.setTag(entity);
						imageView.setVisibility(View.VISIBLE);

						if (entity.getTitle() != null)
							titleview.setText(entity.getTitle());
						if (!mbtitle)
							titleview.setVisibility(View.GONE);

						itemView.setOnClickListener(clicklistener);
						viewContainer.add(itemView);

						ImageView dotview = new ImageView(mcontext);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
								ViewGroup.LayoutParams.WRAP_CONTENT,
								ViewGroup.LayoutParams.WRAP_CONTENT);
						lp.setMargins(3, 3, 3, 3);
						dotview.setLayoutParams(lp);
						dotview.setImageResource(R.drawable.page_indicator_unfocused);
						dots.add(dotview);
						indicateContainer.addView(dotview);

					}
					madapter.notifyDataSetChanged();
					if (!mbindicate) {
						indicateContainer.setVisibility(View.GONE);
					}
					// 初始化选中view
					setViewPagerCurrentItem(0);
					ImageView selectview = (ImageView) dots.get(0);
					if (selectview != null) {
						selectview
								.setImageResource(R.drawable.page_indicator_focused);
					}

					startAutoPlay();
				}
				if (viewContainer.size() == 0) {
					AdZoneView.this.setVisibility(GONE);
				} else {
					AdZoneView.this.setVisibility(VISIBLE);
				}
				break;
			case HANDCODE_VISBLE:
				if (hasfocus) {
					startAutoPlay();
				}
			}
			;

		}

	};

	private void setViewPagerCurrentItem(int position) {
		adviewPager.setCurrentItem(position);

		currentItem = position;
	}

	public OnClickListener clicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			// TODO Auto-generated method stub
			AdZoneEntity entity = (AdZoneEntity) v.getTag();
			if (entity == null)
				return;
			/*
			 * if(true){ Intent intenta = new Intent();
			 * intenta.setAction(ActionConstants.ACTION_FRAGMENT_INFO);
			 * intenta.addCategory(Intent.CATEGORY_DEFAULT);
			 * mcontext.startActivity(intenta);
			 * 
			 * Intent intentx = new Intent();
			 * intentx.setAction(ActionConstants.ACTION_FRAGMENT_EXTRA);
			 * intentx.addCategory(ActionConstants.CATEGORY_FRAGMENT_DEFAULT);
			 * mcontext.sendBroadcast(intentx);
			 * 
			 * return; }
			 */
			UmShare.UmStatistics(mcontext, "Login_PropagandaClick");
			switch (entity.getType()) {
			case ActionJumpEntity.ACTTYPE_NONE: {

			}
				break;
			case ActionJumpEntity.ACTTYPE_INNERJUMP: {
				ActionJumpEntity.performInnerActionJump(mcontext,
						entity.getActionId(), null);

			}
				break;
			case ActionJumpEntity.ACTTYPE_URLJUMP: {
				if (entity.getClick_url() != null)
					UIHelper.showInfoDetial(mcontext, "", entity.getTitle(),
							entity.getClick_url());
			}
				break;
			}

		}
	};

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return viewContainer.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(viewContainer.get(arg1));
			return viewContainer.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}

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
	private void loadData(final int adZoneid, final Handler handler) {

		Thread t = new Thread() {
			public void run() {
				Message msg = new Message();
				boolean bexcept = false;
				long latestTimeinDb = getLatestDataTimeInDb(mcontext, adZoneid);
				long nowtime = System.currentTimeMillis();
				long dealytime = ConfigOptions.getAdTimesUpdate();
				// Log.i("AdZoneView", "loadData adZoneid = "+
				// adZoneid+" nowtime = " + nowtime+",dealytime ="+dealytime
				// +",latestTimeinDb = "+latestTimeinDb);
				if (nowtime - latestTimeinDb < dealytime) {
					AdZoneList list = getAdZoneListFromDb(mcontext, adZoneid);
					msg.what = 1;
					msg.obj = list;
				} else {
					// 消息模块，不需要栏目id；
					AdZoneList list = null;
					try {
						list = getAdZoneListFromNet(appContext, adZoneid);
						if (list != null && list.getValidate().OK()) {
							deleteOverTimeAdInDb(appContext, adZoneid, nowtime);
						}

					} catch (AppException e) {
						// TODO Auto-generated catch block
						bexcept = true;
					}
					if (list == null || !list.getValidate().OK() || bexcept) {
						list = getAdZoneListFromDb(mcontext, adZoneid);
					}
					if (list.getValidate().OK()) {
						msg.what = HANDCODE_DATA_OK;
						msg.obj = list;
					} else {
						msg.what = HANDCODE_DATA_FAIL;
						msg.obj = list;
					}
				}
				handler.sendMessage(msg);

			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	private long getLatestDataTimeInDb(Context context, int zoneId) {
		long latesttime = 0;
		DBUtils db = DBUtils.getInstance(context);
		String sql = new String();
		sql = DBUtils.KEY_ZONE_ID + " = " + String.valueOf(zoneId);
		Cursor cursor = db.query(true, DBUtils.adZoneTableName, new String[] {
				DBUtils.KEY_ZONE_ID, DBUtils.KEY_TIMESTAMP }, sql, null, null,
				null, DBUtils.KEY_TIMESTAMP + " DESC", String.valueOf(1));
		if (cursor != null && !cursor.isAfterLast()) {
			cursor.moveToFirst();
			int row = cursor.getColumnIndex(DBUtils.KEY_TIMESTAMP);
			String timestamp = cursor.getString(row);
			latesttime = Long.parseLong(timestamp);

			cursor.close();
		}
		return latesttime;
	}

	private int deleteOverTimeAdInDb(Context context, int zoneId, long time) {
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = new String();
		sql = DBUtils.KEY_ZONE_ID + " = " + String.valueOf(zoneId);
		sql += " AND " + DBUtils.KEY_TIMESTAMP + " <= " + String.valueOf(time);
		int id = db.delete(DBUtils.adZoneTableName, sql, null);
		return id;
	}

	private AdZoneList getAdZoneListFromDb(Context context, int zoneId) {
		long latesttime = 0;
		DBUtils db = DBUtils.getInstance(context);
		AdZoneList list = new AdZoneList();
		Result result = null;
		String sql = new String();
		sql = DBUtils.KEY_ZONE_ID + " = " + String.valueOf(zoneId);
		Cursor cursor = db.query(true, DBUtils.adZoneTableName,
				new String[] { " * " }, sql, null, null, null,
				DBUtils.KEY_TIMESTAMP + " DESC", null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				AdZoneEntity entity = new AdZoneEntity();
				int row = cursor.getColumnIndex(DBUtils.KEY_AD_TYPE);
				if (row != -1) {
					int adtype = cursor.getInt(row);
					entity.setType(adtype);
				}
				row = cursor.getColumnIndex(DBUtils.KEY_AD_ID);
				if (row != -1) {
					String id = cursor.getString(row);
					entity.setAdId(id);
				}
				row = cursor.getColumnIndex(DBUtils.KEY_AD_ACTIONID);
				if (row != -1) {
					int actionid = cursor.getInt(row);
					entity.setActionId(actionid);
				}
				row = cursor.getColumnIndex(DBUtils.KEY_PIC_URL);
				if (row != -1) {
					String url = cursor.getString(row);
					entity.setPic_url(url);
				}
				row = cursor.getColumnIndex(DBUtils.KEY_CLICK_URL);
				if (row != -1) {
					String url = cursor.getString(row);
					entity.setClick_url(url);
				}
				row = cursor.getColumnIndex(DBUtils.KEY_AD_TITLE);
				if (row != -1) {
					String url = cursor.getString(row);
					entity.setTitle(url);
				}
				list.getArraylist().add(entity);
			}
			result = new Result(1, "success");
			list.setValidate(result);
			cursor.close();
		} else {
			result = new Result(-1, "failed");
			list.setValidate(result);
		}
		return list;
	}

	/**
	 * 版块类型列表
	 * 
	 * @param cat_type
	 *            :版块类型
	 * @param column_id
	 *            ：栏目id；在号外模块，用于区分不同的栏目
	 * @return
	 * @throws ApiException
	 */
	public AdZoneList getAdZoneListFromNet(AppContext appcontext, int zoneId)
			throws AppException {
		AdZoneList list = null;
		String key = "ForumTypeList";

		Result res = null;
		if (appcontext.isNetworkConnected()) {
			try {
				String url = getUrlByZoneId(zoneId);
				list = ApiClient.getAdZoneList(appcontext, url, zoneId);

			} catch (AppException e) {
				if (e.getType() == AppException.TYPE_HTTP_CODE) {
					res = new Result(-2, "网络异常," + e.getCode());
				} else {
					res = new Result(-1, " AppException " + e);
				}

			}
		} else {
			res = new Result(-1, this.getResources().getString(
					R.string.app_status_net_disconnected));
		}
		if (list == null) {
			list = new AdZoneList();
			list.setValidate(res);
		}
		return list;
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		// TODO Auto-generated method stub
		super.onVisibilityChanged(changedView, visibility);
		bvisible = (visibility == 0) ? true : false;
		Message msg = new Message();
		msg.what = HANDCODE_VISBLE;
		handler.sendMessage(msg);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);
		hasfocus = hasWindowFocus;
		Message msg = new Message();
		msg.what = HANDCODE_VISBLE;
		handler.sendMessage(msg);
	}

	private float xDistance, yDistance, xLast, yLast;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		// MyLogger.i(TAG, "onTouchEvent " + ev.getAction());
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:

			final float curX = ev.getX();
			final float curY = ev.getY();
			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;
			if (xDistance > yDistance) {
				// MyLogger.i(TAG,
				// "onTouchEvent ACTION_MOVE xDistance > yDistance");
				this.getParent().requestDisallowInterceptTouchEvent(true);
			} else {
				this.getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			this.getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	/**
	 * 手动刷新
	 */
	public void refreshView() {
		if (viewContainer.size() == 0)
			loadData(madzoneId, handler);
	}

	private static class AdSize {
		private int width;
		private int height;

		/**
		 * @param width
		 *            :dp
		 * @param height
		 *            :dp
		 */
		public AdSize(int width, int height) {
			super();
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}

}

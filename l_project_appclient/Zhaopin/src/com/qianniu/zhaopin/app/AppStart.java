package com.qianniu.zhaopin.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.AdZoneEntity;
import com.qianniu.zhaopin.app.bean.GlobalDataEntity;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.VersionData;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.service.ConnectionService;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.app.ui.MainActivity;
import com.qianniu.zhaopin.app.ui.ResumeEditJobTargetActivity;
import com.qianniu.zhaopin.app.ui.SplashActivity;
import com.qianniu.zhaopin.app.view.Util;
import com.qianniu.zhaopin.util.NumberUtils;
import com.qianniu.zhaopin.util.ShortCutUtils;
import com.qianniu.zhaopin.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界�? * @author liux (http://my.oschina.net/liux)
 * 
 * @version 1.0
 * @created 2012-3-21
 */
public class AppStart extends BaseActivity {

	private boolean b_animationend = false;
	private boolean b_globalend = false;
	private TextView loadingText;
	private DBUtils m_DBUtils;

	private View bg;
	private ImageView startImage;
	private ViewGroup mContainer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bg = View.inflate(this, R.layout.start, null);
		setContentView(bg);
		loadingText = (TextView) findViewById(R.id.loading_text);
		startImage = (ImageView) findViewById(R.id.start_image);

		mContainer = (ViewGroup) findViewById(R.id.container);
		if (ConfigOptions.apk_cmcc) {
			startImage.setBackgroundResource(R.drawable.cmccmall);
		} else {
			initStartImage();
		}
		// 创建快捷图标
		if (DMSharedPreferencesUtil.VALUE_FLG_OK != DMSharedPreferencesUtil
				.getSharePreInt(this, DMSharedPreferencesUtil.DM_HOTLABEL_DB,
						DMSharedPreferencesUtil.FIELD_FIRST_SHORTCUT)) {
		/*	ShortCutUtils.addShortCut(this, "牵牛招聘", R.drawable.ic_launcher,
					AppStart.class);*/
			showShortCutDialog();
			DMSharedPreferencesUtil.putSharePre(this,
					DMSharedPreferencesUtil.DM_HOTLABEL_DB,
					DMSharedPreferencesUtil.FIELD_FIRST_SHORTCUT,
					DMSharedPreferencesUtil.VALUE_FLG_OK);
		}

		// 渐变展示启动�?
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(3000);
		startImage.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				b_animationend = true;
				if (ConfigOptions.apk_cmcc) {
					if (!initStartImage()) {
						startImage
								.setBackgroundResource(R.drawable.start_background);
					}
				}
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});

		// 兼容低版本cookie�?.5版本以下，包�?.5.0,1.5.1�?
		AppContext appContext = (AppContext) getApplication();
		String cookie = appContext.getProperty("cookie");
		if (StringUtils.isEmpty(cookie)) {
			String cookie_name = appContext.getProperty("cookie_name");
			String cookie_value = appContext.getProperty("cookie_value");
			if (!StringUtils.isEmpty(cookie_name)
					&& !StringUtils.isEmpty(cookie_value)) {
				cookie = cookie_name + "=" + cookie_value;
				appContext.setProperty("cookie", cookie);
				appContext.removeProperty("cookie_domain", "cookie_name",
						"cookie_value", "cookie_version", "cookie_path");
			}
		}
		handler.postDelayed(runnable, TIME); // 每隔1s执行

		// enterApp();
		checkStartImage();
		if (appContext.checkIsOriginalApp()) {
			doCheckVersion();
		}else{
			m_handler.sendEmptyMessage(CHECKVERSIONEXCEPTION);
		}
	}

	// 初始化启动图片
	private boolean initStartImage() {
		String startImageName = DMSharedPreferencesUtil.getSharePreStr(
				getApplicationContext(), DMSharedPreferencesUtil.DM_APP_DB,
				DMSharedPreferencesUtil.startImageUri);
		if (!TextUtils.isEmpty(startImageName)) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			Bitmap bitmap = BitmapFactory.decodeFile(
					AppConfig.START_SAVE_IMAGE_ABSOLUTE_PATH + "/"
							+ startImageName, opt);
			if (bitmap != null) {
				startImage.setBackgroundDrawable(new BitmapDrawable(bitmap));
				return true;
			}
		}
		return false;
	}

	private void checkStartImage() {
		if (!Util.isWifi(getApplicationContext())) {
			return;
		}
		ApiClient.pool.execute(new Runnable() {

			@Override
			public void run() {
				Result result = null;
				try {
					result = ApiClient
							.checkStartImage((AppContext) getApplicationContext());
				} catch (AppException e) {
					e.printStackTrace();
				}
				if (result != null) {
					String imageNameOld = DMSharedPreferencesUtil
							.getSharePreStr(getApplicationContext(),
									DMSharedPreferencesUtil.DM_APP_DB,
									DMSharedPreferencesUtil.startImageUri);
					File file = new File(
							AppConfig.START_SAVE_IMAGE_ABSOLUTE_PATH + "/"
									+ imageNameOld);
					if (result.OK()) {
						String dataStr = result.getJsonStr();
						try {
							JSONArray josnArray = new JSONArray(dataStr);
							int size = josnArray.length();
							AdZoneEntity[] adZoneEntitys = new AdZoneEntity[size];
							for (int i = 0; i < size; i++) {
								JSONObject childjsonObj = (JSONObject) josnArray
										.getJSONObject(i);

								AdZoneEntity adZoneEntity = (AdZoneEntity) ObjectUtils
										.getObjectFromJsonString(
												childjsonObj.toString(),
												AdZoneEntity.class);
								adZoneEntitys[i] = adZoneEntity;
							}
							String imageUrl = "";
							imageUrl = adZoneEntitys[0].getPic_url();
							String imageName = imageUrl.substring(imageUrl
									.lastIndexOf("/") + 1);
							BitmapManager bitmapManager = new BitmapManager();
							if (!TextUtils.isEmpty(imageName)) {
								if (!imageName.equals(imageNameOld)) {
									// 记录最新的图片名称
									DMSharedPreferencesUtil
											.putSharePre(
													getApplicationContext(),
													DMSharedPreferencesUtil.DM_APP_DB,
													DMSharedPreferencesUtil.startImageUri,
													imageName);
									// 下载并显示新的启动图片
									Looper.prepare();
									bitmapManager.loadBitmap(imageUrl,
											startImage,
											AppConfig.SDCARD_IMAGE_START, null);
									// 删除之前的启动图片
									file.deleteOnExit();
								} else if (!file.exists()) {// 如果不是新图片，并且sdcard中不存在时，进行重新下载
									Looper.prepare();
									bitmapManager.loadBitmap(imageUrl,
											startImage,
											AppConfig.SDCARD_IMAGE_START, null);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (result.getErrorCode() == Result.CODE_DATA_EMPTY) { // 如果为空表示需要显示默认的图片，这样需要修改记录
						// 记录最新的图片名称
						DMSharedPreferencesUtil.putSharePre(
								getApplicationContext(),
								DMSharedPreferencesUtil.DM_APP_DB,
								DMSharedPreferencesUtil.startImageUri, "");
						// 删除之前的启动图片
						file.deleteOnExit();
					}
				}
			}
		});

	}

	private void enterApp() {
		loadingText.setVisibility(View.VISIBLE);
		m_DBUtils = DBUtils.getInstance(this);
		initDataBase();
		upGlobalData();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void startSplashActivity(int currendVersionCode) {
		Intent intent = new Intent(this, SplashActivity.class);
		startActivity(intent);
		DMSharedPreferencesUtil.putSharePre(AppStart.this,
				DMSharedPreferencesUtil.DM_APP_DB,
				DMSharedPreferencesUtil.splashVersion, currendVersionCode);
	}

	/**
	 * 跳转�?..
	 */
	private void redirectTo() {
		if (!b_globalend || !b_animationend) {
			return;
		}
		int splashVersion = DMSharedPreferencesUtil.getSharePreInt(
				AppStart.this, DMSharedPreferencesUtil.DM_APP_DB,
				DMSharedPreferencesUtil.splashVersion, -1);
		int currendVersionCode = getCurrentVersion().getVersionCode();
		if (splashVersion == -1) {
			startSplashActivity(currendVersionCode);
		} else if (splashVersion < currendVersionCode) {
			startSplashActivity(currendVersionCode);
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		/*
		 * Intent intent = new Intent(this, UserLoginActivity.class);
		 * startActivity(intent);
		 */
		/*
		 * Intent intent = new Intent(this, testActivity.class);
		 * startActivity(intent);
		 */
		/*
		 * Intent intent = new Intent("com.matrixdigi.action.test");
		 * startActivity(intent);
		 */
		finish();
	}

	private static final int CHECKVERSIONSUCCESS = 0;
	private static final int CHECKVERSIONEXCEPTION = 1;
	private Handler m_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECKVERSIONSUCCESS: {
				Object object = msg.obj;
				if (object instanceof Result) {
					Result result = (Result) object;
					checkVersionSuccess(result);
				}
				break;
			}
			case CHECKVERSIONEXCEPTION: {
				enterApp();
				break;
			}
			case HeadhunterPublic.GLOBALDATA_GETDATA_ABNORMAL:
			case HeadhunterPublic.GLOBALDATA_GETDATA_FAIL: {
				// UIHelper.ToastMessage(AppStart.this,
				// getString(R.string.msg_globaldata_getdata_fail));
				b_globalend = true;
				redirectTo();
			}
				break;
			case HeadhunterPublic.GLOBALDATA_GETVERSION_ABNORMAL:
			case HeadhunterPublic.GLOBALDATA_GETVERSION_FAIL: {
				// UIHelper.ToastMessage(AppStart.this,
				// getString(R.string.msg_globaldata_getversion_fail));
				b_globalend = true;
				redirectTo();
			}
				break;
			case HeadhunterPublic.GLOBALDATA_GETDATA_SAVESUCCESS:
			case HeadhunterPublic.GLOBALDATA_GETDATA_VERSIONIDENTICAL:
			case HeadhunterPublic.GLOBALDATA_GETDATA_NONETWORKCONNECT: {
				b_globalend = true;
				redirectTo();
			}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 获取全局数据版本号
	 */
	private void upGlobalData() {
		// 判断网络是否连接
		AppContext appContext = (AppContext) this.getApplication();
		if (!appContext.isNetworkConnected()) {
			m_handler
					.sendMessage(m_handler
							.obtainMessage(HeadhunterPublic.GLOBALDATA_GETDATA_NONETWORKCONNECT));
			return;
		}

		new Thread() {
			public void run() {
				GetGlobalDataVersion();
			}
		}.start();
	}

	/**
	 * 获取全局数据版本号
	 */
	private void GetGlobalDataVersion() {
		try {
			AppContext ac = (AppContext) getApplication();

			// 获取全局数据版本号
			GlobalDataEntity gde = ac
					.getGlobalDataEntity(HeadhunterPublic.GLOBALDATA_TYPE_GETVERSION);

			Result res = gde.getValidate();
			if (res.OK()) {
				if (!gde.getVersion().equals(ac.getGlobalDataVersion())) {
					GetGlobalData();
				} else {
					m_handler
							.sendMessage(m_handler
									.obtainMessage(HeadhunterPublic.GLOBALDATA_GETDATA_VERSIONIDENTICAL));
				}
			} else {
				// 失败
				m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.GLOBALDATA_GETVERSION_FAIL,
						res.getErrorMessage()));
			}
		} catch (AppException e) {
			e.printStackTrace();
			// 异常
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.GLOBALDATA_GETVERSION_ABNORMAL, e));
		}
	}

	/**
	 * 获取全局数据
	 */
	private void GetGlobalData() {
		try {
			AppContext ac = (AppContext) getApplication();

			// 获取全局数据
			GlobalDataEntity gde = ac
					.getGlobalDataEntity(HeadhunterPublic.GLOBALDATA_TYPE_GETDATA);

			Result res = gde.getValidate();
			if (res.OK()) {
				// 保存全局数据
				ac.savaGlobalData(gde.getData());
				// 保存全局数据版本号
				ac.saveGlobalDataVersion(gde.getVersion());

				m_handler
						.sendMessage(m_handler
								.obtainMessage(HeadhunterPublic.GLOBALDATA_GETDATA_SAVESUCCESS));
			} else {
				// 失败
				m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.GLOBALDATA_GETDATA_FAIL,
						res.getErrorMessage()));
			}
		} catch (AppException e) {
			e.printStackTrace();
			// 异常
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.GLOBALDATA_GETDATA_ABNORMAL, e));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			((AppContext) getApplicationContext())
					.showQuitAlertDialog(AppStart.this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化全局数据
	 */
	private void initDataBase() {
		// 判断数据库是否有全局数据
		if (DMSharedPreferencesUtil.VALUE_FLG_HAVINGGLOBALDB != DMSharedPreferencesUtil
				.getSharePreInt(this, DMSharedPreferencesUtil.DM_HOTLABEL_DB,
						DMSharedPreferencesUtil.FIELD_FLG_GLOBALDB)) {
			copyDataBase();
		} else {
			AppContext ac = (AppContext) getApplication();

			try {
				if (null != ac.getGlobalDataVersion()
						&& !ac.getGlobalDataVersion().isEmpty()) {
					return;
				}
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 备份数据库
			if (null != m_DBUtils) {
				try {
					m_DBUtils.bakDataBase();
					copyDataBase();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 从备份数据库中copy出全局数据
	 */
	private void copyDataBase() {
		String databaseFilename = DBUtils.DATABASE_PATH + DBUtils.DB_NAME_BAK;
		String strVersion = "";

		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();
		SQLiteDatabase bakDB = null;
		try {
			bakDB = SQLiteDatabase.openDatabase(databaseFilename, null,
					SQLiteDatabase.OPEN_READONLY);
			if (null == bakDB) {
				return;
			}

			// 获取全局数据
			Cursor c = bakDB.query(true, DBUtils.globaldataTableName,
					new String[] { "*" }, null, null, null, null, null, null);
			if (null != c) {
				if (c.getCount() > 0) {
					for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
						GlobalDataTable odt = new GlobalDataTable();

						odt.setType(c.getInt(c
								.getColumnIndex(DBUtils.KEY_GLOBALDATA_TYPE)));
						odt.setID(c.getString(c
								.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
						odt.setName(c.getString(c
								.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
						odt.setNamePinYin(c.getString(c
								.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
						// 判断是否有父数据，并且设置到list中
						String strHavingParent = c
								.getString(c
										.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGPARENT));
						odt.setHavingParent(strHavingParent.equals("1"));
						odt.setParentID(c.getString(c
								.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID)));
						// 判断是否有子数据，并且设置到list中
						String strHavingSub = c
								.getString(c
										.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS));
						odt.setHavingSubClass(strHavingSub.equals("1"));
						// 数据所属级数
						odt.setLevel(c.getInt(c
								.getColumnIndex(DBUtils.KEY_GLOBALDATA_LEVEL)));

						lsGDT.add(odt);
					}
				}

				c.close();
			}

			// 获取全局数据版本号
			Cursor cVersion = bakDB.query(true, DBUtils.versionTableName,
					new String[] { DBUtils.KEY_GLOBALDATA_VERSION }, null,
					null, null, null, null, null);
			if (null != cVersion) {
				if (cVersion.getCount() > 0) {
					cVersion.moveToFirst();
					strVersion = cVersion.getString(cVersion
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_VERSION));
				}

				cVersion.close();
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		if (null != bakDB) {
			bakDB.close();
		}

		if (lsGDT.size() > 0 && !strVersion.isEmpty()) {
			AppContext ac = (AppContext) getApplication();

			try {
				// 保存全局数据
				ac.initGlobalData(lsGDT);
				// 保存全局数据版本号
				ac.saveGlobalDataVersion(strVersion);

				// 设置数据库已经有全局数据
				DMSharedPreferencesUtil.putSharePre(this,
						DMSharedPreferencesUtil.DM_HOTLABEL_DB,
						DMSharedPreferencesUtil.FIELD_FLG_GLOBALDB,
						DMSharedPreferencesUtil.VALUE_FLG_HAVINGGLOBALDB);
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void doCheckVersion() {
		ApiClient.pool.execute(new Runnable() {

			@Override
			public void run() {
				Result result = checkVersion();
				if (result != null) {
					m_handler.sendMessage(m_handler.obtainMessage(
							CHECKVERSIONSUCCESS, result));
				}
			}
		});
	}

	private Result checkVersion() {
		try {
			Result result = ApiClient
					.checkVersion((AppContext) getApplicationContext());
			return result;
		} catch (AppException e) {
			e.printStackTrace();
			m_handler.sendEmptyMessage(CHECKVERSIONEXCEPTION);
		}
		return null;
	}

	private void checkVersionSuccess(Result result) {
		boolean isEnterApp = true;
		if (result != null) {
			if (result.OK()) {
				String jsonStr = result.getJsonStr();
				if (!TextUtils.isEmpty(jsonStr)) {
					final VersionData versionData = (VersionData) ObjectUtils
							.getObjectFromJsonString(jsonStr, VersionData.class);
					String newVersion = versionData.getVersion();
					String forceVersion = versionData.getForce_version();

					String currVersion = ((AppContext) getApplicationContext())
							.getCurrentVersion().getVersion();
					MyLogger.i("AppStart", "forceVersion##" + forceVersion
							+ "&&currVersion##" + currVersion);
					if (!TextUtils.isEmpty(versionData.getDownloadurl())
							&& NumberUtils.isHighVersion(currVersion,
									newVersion)) { // 有新版本，没有新版本不做处理

						if (!TextUtils.isEmpty(forceVersion)
								&& NumberUtils.isHighVersion(currVersion,
										forceVersion)) { // 判断是否是需要强制更新 当前版本高
															// 则不需要强制更新
							// 强制更新
							isEnterApp = false;
							showForceUpdateVersionDialog(versionData);
						} else {
							// 不强制更新
							String sharedVersion = DMSharedPreferencesUtil
									.getSharePreStr(
											(AppContext) getApplicationContext(),
											DMSharedPreferencesUtil.DM_APP_DB,
											DMSharedPreferencesUtil.newAppVersion);
							if (TextUtils.isEmpty(sharedVersion)
									|| !newVersion.equals(sharedVersion)) {
								isEnterApp = false;
								DMSharedPreferencesUtil.putSharePre(
										(AppContext) getApplicationContext(), // 提示一次就把newAppVersion的值修改掉
										DMSharedPreferencesUtil.DM_APP_DB,
										DMSharedPreferencesUtil.newAppVersion,
										newVersion);
								showUpateVersionDialog(versionData);
							}
						}
					}
				}
			}
		}
		if (isEnterApp) {
			enterApp();
		}
	}

	private void showForceUpdateVersionDialog(VersionData versionData) {
		loadingText.setVisibility(View.GONE);
		AlertDialog dialog = UIHelper.showUpdateVersionDialog(AppStart.this,
				versionData);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				loadingText.setVisibility(View.VISIBLE);
				finish();
			}
		});
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
	}

	private void showUpateVersionDialog(final VersionData versionData) {
		loadingText.setVisibility(View.GONE);
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					ConnectionService.startServiceDownload(AppStart.this,
							versionData.getDownloadurl(), null);
				}
				// enterApp();
				// loadingText.setVisibility(View.VISIBLE);
				dialog.dismiss();
			}
		};
		AlertDialog dialog = UIHelper.showCommonScrollDialog(AppStart.this, R.string.apk_update_title,
				versionData.getMemo(), R.string.sure_update, R.string.cancle_update,onClickListener);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				enterApp();
			}
		});
		// dialog.setCancelable(false);
		// dialog.setCanceledOnTouchOutside(false);
		// UIHelper.showUpdateVersionDialog(AppStart.this, versionData, false);
	}

	private int TIME = 300;
	private int i = 3;
	Handler handler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				handler.postDelayed(this, TIME);
				int tag = i % 4;
				switch (tag) {
				case 0:
					loadingText.setText("");
					break;
				case 1:
					loadingText.setText(".");
					break;
				case 2:
					loadingText.setText("..");
					break;
				case 3:
					loadingText.setText("...");
					break;
				default:
					break;
				}
				i++;
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	};
	private void showShortCutDialog() {
		if(ShortCutUtils.hasShortcut(mContext)){
			return ;
		}

		Dialog dialog = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.title_shortcut_creat)
				.setMessage(R.string.msg_shortcut_creat)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								ShortCutUtils.addShortCut(mContext, "牵牛招聘", R.drawable.ic_launcher,
										AppStart.class);
							}
						})
				.setNegativeButton(R.string.dialog_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();
		dialog.show();
	}
}
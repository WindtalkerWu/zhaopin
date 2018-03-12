package com.qianniu.zhaopin.app.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.AdZoneList;
import com.qianniu.zhaopin.app.bean.NoticeEntity;
import com.qianniu.zhaopin.app.bean.NoticeEntityList;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.ActionConstants;
import com.qianniu.zhaopin.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.WindowManager;

public class ConnectionService extends Service {

	public static final String KEY_NOTIFY_INDEX = "notificateindex";
	public static final String KEY_FILEPATH = "filepath";
	public static final String KEY_FILENAME = "filename";

	public static final String SHAREDKEY_POLL_SWITCH = "poll_interval";
	public static final String SHAREDKEY_POLL_INTERVALMILIS = "poll_interval";
	// 正常轮询时间间隔 15分钟
	private static final long POLL_INTERVALTIME_DEFAULT = 60 * 1000;// 15 *
																	// 60 *
																	// 1000;
	// 夜间轮询时间间隔 90分钟
	private static final long POLL_INTERVALTIME_SLEEPMODE = 3 * 60 * 1000;// 90
																			// *
																			// 60
																			// *
																			// 1000;

	private static final int HANDCODE_EVENT_POLLSUCCESS = 0;
	private static final int HANDCODE_EVENT_NOTLOGIN = 1;
	private static final int HANDCODE_EVENT_MSG_EMPTY = 2;

	private static final int HANDCODE_EVENT_DEFAULUTERR = 5;

	private static final int HANDCODE_EVENT_EXCEPTION = 10;

	private static final int HANDCODE_EVENT_STARTPOLL = 20;

	private static final int HANDCODE_NETSTATE_CONNECT = 31;
	private static final int HANDCODE_NETSTATE_DISCONNECT = 32;

	private AppContext mappcontext;
	private Context mcontext;
	private ThreadPoolController threadPool;

	private long mrequestNoticetime = 0;
	BroadcastReceiver cancelDlReceiver;

	// private boolean mbnetconnected = false;

	private BroadcastReceiver netStateReceiver;
	private static MyLogger logger = MyLogger.getLogger("ConnectionService");

	private long mdefaultintervaltime = POLL_INTERVALTIME_DEFAULT; // 默认5分钟，也可以从配置里读取更改
	private boolean bpoolopen = true;
	private ForeachThread mforeachThread;
	private Map<Integer, DownloadRunnable> downloadMap = new HashMap<Integer, DownloadRunnable>();

	public ConnectionService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mcontext = this;
		mappcontext = (AppContext) this.getApplicationContext();
		initPreferences();
		sendForeachDelay(10 * 1000/* 60 * 1000 */); // service 启动后，延时执行；
		initCancelDownloadMonitor(mcontext);
		initNetworkStateMonitor(mcontext);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (netStateReceiver != null) {
			mcontext.unregisterReceiver(netStateReceiver);
		}
		if (cancelDlReceiver != null) {
			mcontext.unregisterReceiver(cancelDlReceiver);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// logger.i("ConnectionService onStartCommand");
		String action = intent != null ? intent.getAction() : null;
		if (action != null
				&& action.equals(ActionConstants.ACTION_SERVICE_DOWNLOAD)) {

			String url = intent.getStringExtra("dl_url");
			String fileinfo = intent.getStringExtra("dl_info");
			if (url == null || url.length() == 0) {

			} else {
				String filename = null;
				if (fileinfo != null)
					filename = fileinfo;
				else
					filename = url.substring(url.lastIndexOf('/') + 1);
				;
				url = URLs.formatURL(url);
				download(url, filename);
			}

		}
		return START_STICKY;

	}

	public static boolean startServiceDownload(final Activity context,
			final String url, final String fileinfo) {
		if (context == null || url == null || url.length() == 0)
			return false;
		AppContext appcontext = (AppContext) context.getApplicationContext();
		if (appcontext.getNetworkType() == 0) {
			UIHelper.ToastMessage(context, R.string.app_status_net_disconnected);
			return false;
		}
		if (appcontext.getNetworkType() == 1) {
			Intent intent = new Intent(context, ConnectionService.class);
			intent.putExtra("dl_url", url);
			intent.putExtra("dl_info", fileinfo);
			intent.setAction(ActionConstants.ACTION_SERVICE_DOWNLOAD);
			context.startService(intent);
		} else {
			Dialog dialog = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(R.string.dialog_download_title)
					.setMessage(R.string.dialog_mobilenet_alertmsg)
					.setPositiveButton(R.string.dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									Intent intent = new Intent(context,
											ConnectionService.class);
									intent.putExtra("dl_url", url);
									intent.putExtra("dl_info", fileinfo);
									intent.setAction(ActionConstants.ACTION_SERVICE_DOWNLOAD);
									context.startService(intent);
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
		return true;

	}

	public static boolean startService(Context context) {
		if (context == null)
			return false;
		Intent intent = new Intent(context, ConnectionService.class);
		context.startService(intent);
		return true;

	}

	private void initPreferences() {

		SharedPreferences mPreferences = PreferenceManager
				.getDefaultSharedPreferences(mcontext);// getSharedPreferences(TAG,
														// MODE_PRIVATE);
		mdefaultintervaltime = mPreferences.getLong(
				SHAREDKEY_POLL_INTERVALMILIS, POLL_INTERVALTIME_DEFAULT);
		bpoolopen = mPreferences.getBoolean(SHAREDKEY_POLL_SWITCH, true);
	}

	/**
	 * 注册监听网络连接 netStateReceiver
	 */
	private void initNetworkStateMonitor(Context context) {

		netStateReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub

				ConnectivityManager manager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo gprs = manager
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				NetworkInfo wifi = manager
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

				if ((gprs != null && gprs.isConnected())
						|| (wifi != null && wifi.isConnected())) {

					logger.i("netStateReceiver network conncet  ");
					mforeachHandler.sendEmptyMessage(HANDCODE_NETSTATE_CONNECT);

				} else {
					logger.i("netStateReceiver network disconncet  ");
					mforeachHandler
							.sendEmptyMessage(HANDCODE_NETSTATE_DISCONNECT);

				}

			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		context.registerReceiver(netStateReceiver, filter);

	}

	private void download(String url, String filename) {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 0: {
					Bundle bundle = msg.getData();
					int notifyindex = bundle.getInt(KEY_NOTIFY_INDEX);
					String filepath = bundle.getString(KEY_FILEPATH);
					String filename = bundle.getString(KEY_FILENAME);
					
					File file = new File(filepath);
					Uri uri = Uri.fromFile(file);
					Intent installIntent = null;
					PendingIntent PendingIntent = null;
					if (filename != null
							&& filename.toLowerCase().endsWith(".apk")) {

						installIntent = new Intent();
						installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						installIntent.setAction("android.intent.action.VIEW");
						installIntent.setDataAndType(uri,
								"application/vnd.android.package-archive");
						PendingIntent = PendingIntent.getActivity(mcontext, 0,
								installIntent, 0);

					} else {
						// AdService.this.updatePendingIntent = null;
						Intent updateIntent = new Intent();
						PendingIntent = PendingIntent.getActivity(mcontext, 0,
								updateIntent, 0);
					}

					Notification updateNotification = new Notification(
							android.R.drawable.stat_sys_download_done, "成功下载"
									+ filename, System.currentTimeMillis());
					updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
					updateNotification.setLatestEventInfo(mcontext, "成功下载"
							+ filename, installIntent != null ? "点击安装" : "",
							PendingIntent);
					if (updateNotification != null) {

						NotificationServer ntfserver = NotificationServer
								.getInstance(mcontext);
						ntfserver.notify(notifyindex, updateNotification);
					}
					if (installIntent != null)
						startActivity(installIntent);

					recycleDownloadRunnableFromMap(notifyindex);
				}
					break;
				case 1: {
					Bundle bundle = msg.getData();
					int notifyindex = bundle.getInt(KEY_NOTIFY_INDEX);
					String filepath = bundle.getString(KEY_FILEPATH);
					DownloadRunnable dl_runnable = getDownloadRunnableFromMap(notifyindex);
					boolean bcancle = dl_runnable.getCanceldownloadFlag();
					if (bcancle) {
						NotificationServer ntfserver = NotificationServer
								.getInstance(mcontext);
						ntfserver.cancel(notifyindex);
					} else {
						Notification updateNotification = new Notification(
								android.R.drawable.stat_sys_download_done,
								"下载失败", System.currentTimeMillis());
						PendingIntent PendingIntent = null;
						Intent updateIntent = new Intent();
						PendingIntent = PendingIntent.getActivity(mcontext, 0,
								updateIntent, 0);
						updateNotification.flags = Notification.FLAG_AUTO_CANCEL;

						updateNotification.setLatestEventInfo(mcontext, "下载失败",
								null, PendingIntent);
						NotificationServer ntfserver = NotificationServer
								.getInstance(mcontext);
						ntfserver.notify(notifyindex, updateNotification);
					}

					if (filepath != null) {
						File file = new File(filepath);
						if (file != null) {
							file.delete();
						}

					}
					recycleDownloadRunnableFromMap(notifyindex);
				}
					break;
				}
			}

		};

		NotificationServer ntfserver = NotificationServer.getInstance(mcontext);
		int index = ntfserver
				.getNotificationIndex(NotificationServer.TYPE_DOWANLOAD);
		DownloadRunnable runnable = new DownloadRunnable(this, url, index,
				handler, filename);

		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(runnable);

		addDownloadRunnableToMap(index, runnable);
	}

	class DownloadRunnable implements Runnable {
		// Message message = AdService.this.updateHandler.obtainMessage();
		private Context mcontext;
		private String murl;
		private int mnotifyindex = 0;
		private String mfilename;
		private Handler mhandler;
		private boolean bcancel_download = false;
		private int download_percent = 0;

		DownloadRunnable(Context context, String url, int notifyindex,
				Handler handler, String filename) {
			mcontext = context;
			murl = url;
			mnotifyindex = notifyindex;
			mfilename = filename;
			mhandler = handler;
		}

		/**
		 * 取消下载
		 * 
		 * @param bcancel_download
		 */
		public final void canceldownload() {
			this.bcancel_download = true;
		}

		/**
		 * 获取取消下载标志,true:已取消，false:未取消
		 * 
		 * @param bcancel_download
		 */
		public final boolean getCanceldownloadFlag() {
			return this.bcancel_download;
		}

		/**
		 * 获取当前下载百分比
		 * 
		 * @return
		 */
		public final int getDownloadPercent() {
			return download_percent;
		}

		/**
		 * 获取当前下载的文件名
		 * 
		 * @return
		 */
		public final String getDownloadFilename() {
			return mfilename;
		}

		/**
		 * 下载文件，成功返回true，失败返回false
		 * 
		 * @param context
		 * @param downloadUrl
		 * @param saveFile
		 * @param notifyindex
		 * @param filename
		 * @return
		 * @throws Exception
		 */
		public boolean downloadFile(Context context, String downloadUrl,
				File saveFile, int notifyindex, String filename)
				throws Exception {
			int downloadCount = 0;
			int currentSize = 0;
			long totalSize = 0L;
			long updateTotalSize = 0;
			HttpURLConnection httpConnection = null;
			InputStream is = null;
			FileOutputStream fos = null;
			logger.i("downloadFile##" + downloadUrl);
			try {
				URL url = new URL(downloadUrl);
				httpConnection = (HttpURLConnection) url.openConnection();
				httpConnection.setRequestProperty("User-Agent", "matrixdigi");
				if (currentSize > 0) {
					httpConnection.setRequestProperty("RANGE", "bytes="
							+ currentSize + "-");
				}
				httpConnection
						.setRequestProperty("Accept-Encoding", "identity");
				httpConnection.setConnectTimeout(10000);
				httpConnection.setReadTimeout(20000);
				updateTotalSize = httpConnection.getContentLength();
				if (httpConnection.getResponseCode() != 200) {
					throw new Exception("fail!");
				}
				is = httpConnection.getInputStream();
				fos = new FileOutputStream(saveFile, false);
				byte[] buffer = new byte[4096];
				int readsize = 0;
				logger.i("downloadFile start notifyindex = " + notifyindex
						+ ", filename = " + filename + ",downloadUrl ="
						+ downloadUrl);
				do {
					fos.write(buffer, 0, readsize);
					totalSize += readsize;

					if ((updateTotalSize != -1)
							&& ((downloadCount == 0) || ((int) ((totalSize * 100L) / updateTotalSize) > downloadCount + 2))) {
						downloadCount = (int) ((totalSize * 100L) / updateTotalSize);
						download_percent = downloadCount;
						DecimalFormat fnum = new DecimalFormat("##0.00");

						Intent Intent = new Intent(
								ActionConstants.ACTION_SERVICE_DOWNLOAD_CANCEL);
						Intent.putExtra("index", notifyindex);
						PendingIntent updatePendingIntent = PendingIntent
								.getBroadcast(context, 0, Intent,
										PendingIntent.FLAG_UPDATE_CURRENT);
						/*
						 * PendingIntent updatePendingIntent = PendingIntent
						 * .getActivity(context, 0, updateIntent, 0);
						 */
						Notification updateNotification = new Notification(
								android.R.drawable.stat_sys_download, "正在下载"
										+ filename, System.currentTimeMillis());
						updateNotification.flags = Notification.FLAG_ONGOING_EVENT;
						updateNotification
								.setLatestEventInfo(
										mcontext,
										"正在下载" + filename,
										"下载进度:"
												+ downloadCount
												+ "%,"
												+ fnum.format(((float) totalSize / (1024 * 1024)))
												+ "MB/"
												+ fnum.format(((float) updateTotalSize / (1024 * 1024)))
												+ "MB", updatePendingIntent);

						/*
						 * mapNotifications .get(notifyindex);
						 */
						if (updateNotification != null) {

							NotificationServer ntfserver = NotificationServer
									.getInstance(mcontext);
							ntfserver.notify(notifyindex, updateNotification);
						}

					}
					if ((readsize = is.read(buffer)) <= 0)
						break;
				} while (this != null && bcancel_download == false);
				logger.i("downloadFile end notifyindex = " + notifyindex
						+ ",totalSize = " + totalSize + ", downloadCount = "
						+ downloadCount);
			} finally {
				if (httpConnection != null) {
					httpConnection.disconnect();
				}
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			}
			logger.i("downloadFile end updateTotalSize = " + updateTotalSize
					+ ",totalSize = " + totalSize + ", filename = " + filename
					+ ",downloadUrl =" + downloadUrl);
			if (totalSize == updateTotalSize)
				return true;
			else
				return false;
			// return totalSize;
		}

		public void run() {
			Message msg = new Message();
			File updateDir = null;
			File updateFile = null;
			msg.what = 0;
			try {
				if (!android.os.Environment.getExternalStorageState().equals( 
				android.os.Environment.MEDIA_MOUNTED)) {
					updateDir = mcontext.getFilesDir();
				} else {
					updateDir = new File(Environment.getExternalStorageDirectory(), "matrix/download/");
				}
				if (!updateDir.exists()) {

					boolean bmkdir = updateDir.mkdirs();

				} else {
					boolean bdir = updateDir.isDirectory();
					if (!bdir) {
						updateDir.delete();
						boolean bmkdir = updateDir.mkdirs();
					}

				}
				updateFile = new File(updateDir.getPath(), mfilename);

				if (updateFile.exists()) {
					updateFile.delete();
					boolean bnewfile = updateFile.createNewFile();
				}

				try {
					String[] args2 = { "chmod", "604", updateFile.getPath()};
					Runtime.getRuntime().exec(args2);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
//				 e.printStackTrace();
				logger.i("Exception = " + e);
				msg.what = 1;
				Bundle bundle = new Bundle();
				// bundle.putString("faild", "下载失败");
				bundle.putInt(KEY_NOTIFY_INDEX, mnotifyindex);
				if (updateFile != null)
					bundle.putString(KEY_FILEPATH, updateFile.getPath());
				msg.setData(bundle);
				mhandler.sendMessage(msg);
				return;
			}
			try {

				boolean bdownload = downloadFile(mcontext, murl, updateFile,
						mnotifyindex, mfilename);
				if (bdownload) {
					msg.what = 0;
					Bundle bundle = new Bundle();
					if (updateFile != null)
						bundle.putString(KEY_FILEPATH, updateFile.getPath());
					bundle.putString(KEY_FILENAME, mfilename);
					bundle.putInt(KEY_NOTIFY_INDEX, mnotifyindex);
					msg.setData(bundle);
					mhandler.sendMessage(msg);
				} else {
					msg.what = 1;
					Bundle bundle = new Bundle();
					if (updateFile != null)
						bundle.putString(KEY_FILEPATH, updateFile.getPath());
					bundle.putString(KEY_FILENAME, mfilename);
					bundle.putInt(KEY_NOTIFY_INDEX, mnotifyindex);
					msg.setData(bundle);
					mhandler.sendMessage(msg);

				}
			} catch (Exception e) {
//				 e.printStackTrace();
				msg.what = 1;
				Bundle bundle = new Bundle();
				bundle.putInt(KEY_NOTIFY_INDEX, mnotifyindex);
				if (updateFile != null)
					bundle.putString(KEY_FILEPATH, updateFile.getPath());
				msg.setData(bundle);

				mhandler.sendMessage(msg);
				return;
			}
		}
	}

	private Object lock = new Object();

	private void startForeach() {
		if (bpoolopen) {
			if (mforeachThread != null && mforeachThread.isAlive()) {
				synchronized (lock) {
					mforeachThread.setBexit(false);
					lock.notify();
				}

			} else {
				mforeachThread = new ForeachThread();
				mforeachThread.start();
			}
		}
	}

	private void stopForeach() {
		if (mforeachThread != null && mforeachThread.isAlive()) {

			synchronized (lock) {
				mforeachThread.setBexit(true);
				lock.notify();
			}
		}
	}

	private void sendForeachDelay(long dealymilis) {
		mforeachHandler.removeMessages(HANDCODE_EVENT_STARTPOLL);
		mforeachHandler.sendEmptyMessageDelayed(HANDCODE_EVENT_STARTPOLL,
				dealymilis);
	}

	private long getIntervalTime() {
		long interval_ms = ConfigOptions.getAppServiceDayTimes();
		boolean bback = NoticeEntity.isBackgroundForApplication(mappcontext);
		int nettype = mappcontext.getNetworkType();
		if (isSleepingTime()) {
			interval_ms = ConfigOptions.getAppServiceNightTimes();
		} else {

			if (bback) {
				interval_ms = ConfigOptions.getAppServiceDayTimes();
			} else {
				// wifi模式
				if (nettype == 1) {
					interval_ms = ConfigOptions.getAppServiceWifiTimes();
				} else {
					interval_ms = ConfigOptions.getAppServiceDayTimesFast();
				}
			}
		}
		return interval_ms;
	}

	private Handler mforeachHandler = new Handler() {
		public void handleMessage(Message msg) {
			long dealytime = mdefaultintervaltime;
			switch (msg.what) {
			case HANDCODE_EVENT_DEFAULUTERR:
			case HANDCODE_EVENT_EXCEPTION: {
				dealytime = getIntervalTime();
				sendForeachDelay(dealytime);
			}
				break;
			case HANDCODE_EVENT_POLLSUCCESS: {
				dealytime = getIntervalTime();
				sendForeachDelay(dealytime);
			}
				break;
			case HANDCODE_EVENT_NOTLOGIN: {
				dealytime = getIntervalTime();
				sendForeachDelay(dealytime);
			}
				break;
			case HANDCODE_NETSTATE_CONNECT: {
				dealytime = getIntervalTime();
				sendForeachDelay(dealytime);
			}
				break;
			case HANDCODE_NETSTATE_DISCONNECT: {
				stopForeach();
			}
				break;
			case HANDCODE_EVENT_STARTPOLL: {
				startForeach();
			}
				break;

			}

		}
	};

	/**
	 * 轮询通知信息
	 */
	class ForeachThread extends Thread {
		private boolean bexit = false;

		public final boolean isBexit() {
			return bexit;
		}

		public final void setBexit(boolean bexit) {
			this.bexit = bexit;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while (!bexit) {
				synchronized (lock) {

					Message msg = new Message();
					NoticeEntityList list = null;
					long offsettime = 0;
					if (mrequestNoticetime <= 0) {
						mrequestNoticetime = 0;
						mrequestNoticetime = DMSharedPreferencesUtil
								.getSharePreLong(
										mappcontext,
										DMSharedPreferencesUtil.DM_HOTLABEL_DB,
										DMSharedPreferencesUtil.FIELD_SERVICE_POLL_LASTSECOND,
										0);

					}
					if (mrequestNoticetime > 0) {
						offsettime = System.currentTimeMillis() / 1000
								- mrequestNoticetime;
					}
					mrequestNoticetime = System.currentTimeMillis() / 1000;
					DMSharedPreferencesUtil
							.putSharePreLong(
									mappcontext,
									DMSharedPreferencesUtil.DM_HOTLABEL_DB,
									DMSharedPreferencesUtil.FIELD_SERVICE_POLL_LASTSECOND,
									mrequestNoticetime);
					String url = URLs.SERVICE_POLL;
					try {
						/*
						 * boolean bback = NoticeEntity
						 * .isBackgroundForApplication(mappcontext);
						 */
						if (mappcontext.isNetworkConnected()) {
							list = getNoticeEntityListFromNet(mappcontext, url,
									offsettime);
						}
						if (list != null) {
							Result res = list.getValidate();
							if (res != null) {
								if (res.OK()) {
									msg.what = HANDCODE_EVENT_POLLSUCCESS;
									List<NoticeEntity> entitylist = list
											.getList();
									if (entitylist != null) {
										for (int i = 0; i < entitylist.size(); i++) {
											NoticeEntity entity = entitylist
													.get(i);
											NoticeEntity.handleNotice(
													mappcontext, entity);
										}
									}

								} else {
									switch (res.getErrorCode()) {
									case Result.CODE_NOTLOGIN_1000: // 未登录
									case Result.CODE_NOTLOGIN_3000: // 未登录
									case Result.CODE_NOTLOGIN_1203:
									case Result.CODE_TOKEN_INVALID:
									case Result.CODE_TOKEN_OVERTIME: {
										msg.what = HANDCODE_EVENT_NOTLOGIN;
									}
										break;
									default: {
										msg.what = HANDCODE_EVENT_DEFAULUTERR;
									}
										break;
									}
								}
							} else {
								msg.what = HANDCODE_EVENT_DEFAULUTERR;
							}

						} else {
							msg.what = HANDCODE_EVENT_DEFAULUTERR;
						}
					} catch (AppException e1) {
						// TODO Auto-generated catch block
						msg.what = HANDCODE_EVENT_EXCEPTION;
					}

					mforeachHandler.sendMessage(msg);
					try {

						lock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						AppException.http(e);
					}

				}
			}

		}

	}

	private NoticeEntityList getNoticeEntityListFromNet(AppContext appContext,
			String url, long offsettime) throws AppException {

		JSONObject obj = new JSONObject();
		try {
			obj.put("offsettime", offsettime);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			logger.e(e1);
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				obj.toString());
		try {
			return NoticeEntityList.parse(appContext,
					ApiClient._post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	private boolean isSleepingTime() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour > 0 && hour < 8) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 将下载的任务加入管理池；
	 * 
	 * @param index
	 * @param downloadRunnable
	 */
	private void addDownloadRunnableToMap(Integer index,
			DownloadRunnable downloadRunnable) {
		if (downloadMap == null)
			downloadMap = new HashMap<Integer, DownloadRunnable>();
		downloadMap.put(index, downloadRunnable);
	}

	/**
	 * 获取正在下载的任务实例
	 * 
	 * @param index
	 * @return
	 */
	private DownloadRunnable getDownloadRunnableFromMap(Integer index) {
		if (index < 0 || downloadMap == null)
			return null;

		return downloadMap.get(index);
	}

	/**
	 * 将下载任务从管理池移除
	 * 
	 * @param index
	 */
	private void recycleDownloadRunnableFromMap(int index) {
		if (index < 0 || downloadMap == null)
			return;
		downloadMap.remove(index);
	}

	private void showStopDownloadDialog(final int index) {
		final DownloadRunnable runnable = getDownloadRunnableFromMap(index);
		if (runnable == null)
			return;
		String filename = runnable.getDownloadFilename();
		int percent = runnable.getDownloadPercent();
		String msg = new String();
		msg += (filename != null) ? filename : "";
		msg += "当前已下载" + percent + "%";
		msg += ",是否取消？";
		Dialog dialog = MethodsCompat.getAlertDialogBuilder(mcontext,AlertDialog.THEME_HOLO_LIGHT)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("取消下载")
				.setMessage(msg)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								runnable.canceldownload();

							}
						})
				.setNegativeButton(R.string.dialog_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}

	/**
	 * 注册取消下载监听事件
	 */
	private void initCancelDownloadMonitor(Context context) {

		BroadcastReceiver cancelDlReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent != null && context != null) {

					int dl_index = intent.getIntExtra("index", -1);
					if (dl_index != -1) {
						showStopDownloadDialog(dl_index);
					}
				}

			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActionConstants.ACTION_SERVICE_DOWNLOAD_CANCEL);
		context.registerReceiver(cancelDlReceiver, filter);

	}
}

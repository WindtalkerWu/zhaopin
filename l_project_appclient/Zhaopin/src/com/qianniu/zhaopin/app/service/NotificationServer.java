package com.qianniu.zhaopin.app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.Gallery;

public class NotificationServer {
	/**
	 * notification type：下载
	 */
	public static final int TYPE_DOWANLOAD = 1;
	/**
	 * notification type：应用内部消息
	 */
	public static final int TYPE_INNERINFO = 2;
	/**
	 * notification type：推广消息 
	 */
	public static final int TYPE_SPREADINFO = 3;
	
	/**
	 * notification type：版本消息
	 */
	public static final int TYPE_VERSION = 4;

	// 下载通知Id
	private static final int INDEX_DOWANLOAD_DEFAULT = 0;
	private static final int INDEX_DOWANLOAD_MAX_INCREMENT = 49;
	// 应用内部消息通知id
	private static final int INDEX_INNERINFO_DEFAULT = 50;
	private static final int INDEX_INNERINFO_MAX_INCREMENT = 49;

	// 推广消息 通知id
	private static final int INDEX_SPREADINFO_DEFAULT = 100;
	private static final int INDEX_SPREADINFO_MAX_INCREMENT = 99;
	// 版本更新消息 通知id
	private static final int INDEX_VERSIONINFO_DEFAULT = 200;

	private static NotificationServer _nfserver = null;
	private NotificationManager _notificationManager = null;
	private Context mcontext;

	private int download_index = -1;
	private int innerinfo_index = -1;
	private int spread_index = -1;

	private NotificationServer(Context context) {
		// TODO Auto-generated constructor stub
		_notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mcontext = context;
	}

	public static NotificationServer getInstance(Context context) {
		if (_nfserver == null)
			_nfserver = new NotificationServer(context);

		return _nfserver;

	}

	private NotificationManager getNotificationManager() {
		if (_notificationManager == null)
			_notificationManager = (NotificationManager) mcontext
					.getSystemService(Context.NOTIFICATION_SERVICE);
		return _notificationManager;
	}

	public int getNotificationIndex(int type) {
		int indexid = -1;
		switch (type) {
		case TYPE_DOWANLOAD: {
			if (download_index == -1) {
				download_index = INDEX_DOWANLOAD_DEFAULT;
			} else {
				download_index++;
				if (download_index > INDEX_DOWANLOAD_DEFAULT
						+ INDEX_DOWANLOAD_MAX_INCREMENT)
					download_index = INDEX_DOWANLOAD_DEFAULT;
			}

			indexid = download_index;
		}
			break;

		case TYPE_INNERINFO: {
			if (innerinfo_index == -1) {
				innerinfo_index = INDEX_INNERINFO_DEFAULT;
			}

			indexid = innerinfo_index;
		}
			break;

		case TYPE_SPREADINFO: {
			if (spread_index == -1) {
				spread_index = INDEX_SPREADINFO_DEFAULT;
			} else {
				spread_index = INDEX_SPREADINFO_DEFAULT;
				/*
				spread_index++;
				if (spread_index > INDEX_SPREADINFO_DEFAULT
						+ INDEX_SPREADINFO_MAX_INCREMENT)
					spread_index = INDEX_SPREADINFO_DEFAULT;
			*/}

			indexid = spread_index;
		}
			break;
		case TYPE_VERSION: {
			if (spread_index == -1) {
				spread_index = INDEX_VERSIONINFO_DEFAULT;
			} else {
				spread_index = INDEX_VERSIONINFO_DEFAULT;
				}

			indexid = spread_index;
		}
			break;
		}
		return indexid;

	}
	
	public void notify(int id, Notification notification){
		_notificationManager.notify(id, notification);
	}
	public void notify(int id, Notification notification,boolean brecord,int type){
		_notificationManager.notify(id, notification);
	}
	
	public void cancel(int id){
		_notificationManager.cancel(id);
	}
	public void cancelAll(){
		_notificationManager.cancelAll();
	}
}

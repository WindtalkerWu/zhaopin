package com.qianniu.zhaopin.app.bean;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.constant.ActionConstants;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.service.NotificationServer;
import com.qianniu.zhaopin.R;

public class NoticeEntity extends Entity {

	private static final String TAG = "NoticeEntity";
	public static final String NODE_TYPE = "type";
	public static final String NODE_ACTION = "action";
	public static final String NODE_DATA = "data";

	public static final String NODE_NOTIFICATION = "notification";

	private Result validate;

	private String type;
	private NotificationDataEntity ntfDataEntity;
	private String jsondata;

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public final String getType() {
		return type;
	}

	public final void setType(String type) {
		this.type = type;
	}

	public final NotificationDataEntity getNtfDataEntity() {
		return ntfDataEntity;
	}

	public final void setNtfDataEntity(NotificationDataEntity ntfDataEntity) {
		this.ntfDataEntity = ntfDataEntity;
	}

	public final String getJsondata() {
		return jsondata;
	}

	public final void setJsondata(String jsondata) {
		this.jsondata = jsondata;
	}

	public static NoticeEntity parse(AppContext appContext, JSONObject jsonObj,
			boolean bsavetoDb) throws IOException, JSONException, AppException {

		NoticeEntity entity = new NoticeEntity();
		Result res = null;

		try {
			entity.type = jsonObj.getString(NODE_TYPE);

			if (jsonObj.has(NODE_DATA) && !jsonObj.isNull(NODE_DATA)) {

				entity.jsondata = jsonObj.getString(NODE_DATA);
			}
			if (jsonObj.has(NODE_ACTION) && !jsonObj.isNull(NODE_ACTION)) {

				JSONObject actobj = jsonObj.getJSONObject(NODE_ACTION);
				if (actobj.has(NODE_NOTIFICATION)
						&& !actobj.isNull(NODE_NOTIFICATION)) {
					JSONObject ntfobj = actobj.getJSONObject(NODE_NOTIFICATION);
					entity.ntfDataEntity = NotificationDataEntity.parse(
							appContext, ntfobj, false);
				}

			}

			res = new Result(1, "ok");
			entity.validate = res;
			if (appContext != null && bsavetoDb) {

			}

		} catch (Exception e) {

			throw AppException.json(e);
		}
		return entity;

	}

	private static void saveToDb(Context context, NoticeEntity entity) {
		if (entity == null || entity.getType() == null)
			return;

		DBUtils db = DBUtils.getInstance(context);

		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_SERVICEDATA_TYPE, entity.getType());
		if (entity.getJsondata() != null)
			values.put(DBUtils.KEY_SERVICEDATA_JSONDATA, entity.getJsondata());
		values.put(DBUtils.KEY_SERVICEDATA_TIMESTAMP,
				String.valueOf(System.currentTimeMillis()));
		values.put(DBUtils.KEY_SERVICEDATA_STATE_ID,
				DBUtils.SERVICEDATA_STATE_UNREAD);
		db.saveBindUser(DBUtils.serviceDataTableName, values);

	}

	public static NoticeEntityList getNoticeEntityListFromDb(Context context,
			String serviceType) {
		DBUtils db = DBUtils.getInstance(context);

		String sql = DBUtils.KEY_SERVICEDATA_TYPE + " = " + "\"" + serviceType
				+ "\"";
		Cursor cursor = db.queryWithOrderBindUser(DBUtils.serviceDataTableName,
				new String[] { "*" }, sql, null,
				DBUtils.KEY_SERVICEDATA_TIMESTAMP + " DESC");

		NoticeEntityList list = new NoticeEntityList();

		if (null != cursor) {
			if (cursor.getCount() > 0) {
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
						.moveToNext()) {
					int nameColumn = cursor
							.getColumnIndex(DBUtils.KEY_SERVICEDATA_JSONDATA);
					String json = cursor.getString(nameColumn);
					NoticeEntity entity = new NoticeEntity();
					entity.setJsondata(json);
					entity.setType(serviceType);
					list.getList().add(entity);
				}
			}

			cursor.close();
		}

		db.close();
		list.setValidate(new Result(1, null));
		return list;
	}

	public static int deleteTypeDataFromDb(Context context, String serviceType) {
		DBUtils db = DBUtils.getInstance(context);

		String sql = DBUtils.KEY_SERVICEDATA_TYPE + " = " + "\"" + serviceType
				+ "\"";
		int num = db.deleteBindUser(DBUtils.serviceDataTableName, sql, null);

		return num;
	}

	public static boolean handleNotice(Context context, NoticeEntity entity) {
		if (entity == null)
			return false;
		if (entity.getType().equals(DBUtils.SERVICEDATA_TYPE_NOTIFICATION)) {
			/* if (isBackgroundForApplication(context)) */{
				Intent intent = getNotificationActIntent(entity
						.getNtfDataEntity());
				sendNotification(context, intent, entity.getNtfDataEntity(),
						entity.getJsondata(),
						NotificationServer.TYPE_SPREADINFO, false);
			}

		} else if (entity.getType().equals(DBUtils.SERVICEDATA_TYPE_PERSONAL)) {
			if (isBackgroundForApplication(context)) {
				NotificationDataEntity ntfentity = entity.getNtfDataEntity();
				if (ntfentity != null) {
					ntfentity.setTitle(context.getResources().getString(
							R.string.app_name));
					ntfentity.setDescription(context.getResources().getString(
							R.string.notification_newmsg));
					Intent intent = getNotificationActIntent(ntfentity);
					sendNotification(context, intent, ntfentity,
							entity.getJsondata(),
							NotificationServer.TYPE_INNERINFO, false);
				}
				/*
				 * sendNotification(context, entity.getNtfDataEntity(),
				 * entity.getJsondata(), NotificationServer.TYPE_INNERINFO,
				 * false);
				 */
			}
		} else if (entity.getType().equals(
				DBUtils.SERVICEDATA_TYPE_NEWATMESSAGE)) {
			saveToDb(context, entity);
			sendBrocast(context, ActionConstants.BRODCAST_ID_FRAGMENTINFO_NEW,
					null);
		} else if (entity.getType().equals(DBUtils.SERVICEDATA_TYPE_NEWVERSION)) {
			AppContext appcontext = (AppContext) context
					.getApplicationContext();
			if (appcontext.checkIsOriginalApp()) {
				Intent intent = ActionConstants.getUpdateVersionStartIntent(
						context, entity.getJsondata());

				if (intent != null) {
					NotificationDataEntity ntfentity = new NotificationDataEntity();
					ntfentity.setTitle("牵牛招聘新版本通知");
					ntfentity.setDescription("请点击下载牵牛招聘最新版本！");
					sendNotification(context, intent, ntfentity,
							entity.getJsondata(),
							NotificationServer.TYPE_VERSION, false);
				}
			}
		}

		return false;
	}

	private static Intent getNotificationActIntent(NotificationDataEntity entity) {
		Intent intent = null;
		switch (entity.getActionId()) {
		case ActionConstants.ACTINNER_ID_FRAGMENTINFO: {
			intent = ActionConstants.getFragmentInfoStartIntent();
		}
			break;
		default: {
			intent = new Intent();
		}
			break;
		}
		return intent;
	}

	public static void sendNotification(Context context, Intent intent,
			NotificationDataEntity entity, String jsondata, int notifytype,
			boolean brecord) {
		if (entity == null)
			return;
		Bitmap bigIcon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
		NotificationServer ntfserver = NotificationServer.getInstance(context);
		int notifyindex = ntfserver.getNotificationIndex(notifytype);

		PendingIntent pendintent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		/*
		 * Notification updateNotification = new Notification(
		 * R.drawable.ic_stat_custom, entity.getTitle(),
		 * System.currentTimeMillis());
		 * updateNotification.setLatestEventInfo(context, entity.getTitle(),
		 * entity.getDescription(), pendintent);
		 */
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setContentTitle(entity.getTitle()).setTicker(entity.getTitle())
				.setContentText(entity.getDescription())
				.setSmallIcon(R.drawable.ic_stat_custom).setAutoCancel(true)
				.setLargeIcon(bigIcon);

		/* .setStyle(new Notification.BigPictureStyle().bigPicture(bigIcon)); */
		builder.setContentIntent(pendintent);
		Notification updateNotification = builder.build();
		updateNotification.flags = Notification.FLAG_AUTO_CANCEL;

		ntfserver.notify(notifyindex, updateNotification, brecord, notifytype);

	}

	public static void sendBrocast(Context context, int notifytype,
			String jsondata) {
		Intent intent = null;
		switch (notifytype) {
		case ActionConstants.BRODCAST_ID_FRAGMENTINFO_NEW: {
			intent = ActionConstants.getFragmentInfoBrocastNewMsgIntent();
		}
			break;
		}
		if (intent != null) {
			context.sendBroadcast(intent);
		}
	}

	public static boolean isBackgroundForApplication(Context context) {

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND
						|| appProcess.importance == RunningAppProcessInfo.IMPORTANCE_SERVICE) {
					MyLogger.i(TAG, String.format("Background App:",
							appProcess.processName));
					return true;
				} else {
					MyLogger.i(TAG, String.format("Foreground App:",
							appProcess.processName));
					return false;
				}
			}
		}
		return false;
	}
}

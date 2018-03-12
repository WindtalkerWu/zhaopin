package com.qianniu.zhaopin.app.common;

import com.qianniu.zhaopin.app.AppContext;

import android.content.Context;
import android.content.SharedPreferences;

public class DMSharedPreferencesUtil {
	public static String DM_HOTLABEL_DB = "hotlabel_db";
	public static String toHotLabel = "toHotLabel"; // 是否需要 显示 hot label页面 0--需要
													// 1--不需要

	public static String DM_APP_DB = "app_db";
	public static String splashVersion = "splashVersion"; // 引导界面版本
	public static String newAppVersion = "newAppVersion"; // 新应用程序提示版本号
	public static String taxRate = "taxRate"; // 个税税率
	public static String publishRewardPro = "publishRewardPro"; // 发布悬赏提示
	public static String taskShowType= "taskShowType"; // 任务列表展现模式 0 列表 1 瀑布流
	public static String startImageUri = "startImageUri"; // 启动图片记录
	
	public static String DM_AUTH_INFO = "auth_db";
	public static String phoneCache = "phoneCache"; //第一次验证，且未验证成功
	public static String emailCache = "emailCache"; //第一次验证，且未验证成功
	
	public static String DM_MYREWARD_INFO = "myreward_db";
	public static String myRewardInfo = "myRewardInfoCache";//未提交的

	// DB数据库是否有有全局数据标示 0: 没有/1: 有
	public static String FIELD_FLG_GLOBALDB = "field_flg_globaldb";
	public static int VALUE_FLG_NOTGLOBALDB = 0; // 无全局数据
	public static int VALUE_FLG_HAVINGGLOBALDB = 1; // 有全局数据

	public static String FIELD_GLOBALDATAV_VERSION = "field_globadata_version";
	
	// APP版本号
	public static String APP_VERSION = "app_version";
	// APP安装
	public static String APP_INSTALL = "app_install";

	// 是否第一次进入悬赏任务界面
	public static String FIELD_FIRST_REWARD = "field_first_reward";
	public static int VALUE_FLG_NOFIRSTREWARD = 0; // 第一次进入悬赏任务界面
	public static int VALUE_FLG_FIRSTREWARD = 1; // 非第一次进入悬赏任务界面

	// 是否第一次进入"我"界面
	public static String FIELD_FIRST_MY = "field_first_my";
	public static int VALUE_FLG_NOFIRSTMY = 0; // 第一次进入"我"界面
	public static int VALUE_FLG_FIRSTMY = 1; // 非第一次进入"我"界面
	
	// 是否第一次进入我的简历库界面
	public static String FIELD_FIRST_RESUMELIBRARY = "field_first_resumelibrary";
	public static int VALUE_FLG_NOFIRSTRESUMELIBRARY = 0; // 第一次进入我的简历库界面
	public static int VALUE_FLG_FIRSTRESUMELIBRARY = 1; // 非第一次进入我的简历库界面
	

	// 是否第一次创建快捷方式
	public static String FIELD_FIRST_SHORTCUT = "field_first_ShortCut";
	public static int VALUE_FLG_FALSE = 0; // 第一次
	public static int VALUE_FLG_OK = 1; // 非第一次
	
	// service 最后一次poll服务消息的时间戳（s）
	public static String FIELD_SERVICE_POLL_LASTSECOND = "field_service_poll_timesecond";




	// 取出whichSp中field字段对应的string类型的值
	public static String getSharePreStr(Context mContext, String whichSp,
			String field) {
		return getSharePreStr(mContext, whichSp, field, ""); //默认是空字串
	}

	public static String getSharePreStr(Context mContext, String whichSp,
			String field, String defaultVaule) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		String s = sp.getString(field, defaultVaule);
		return s;
	}
	public static String getByUserId(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		String userId = ((AppContext)mContext).getUserId();
		String s = sp.getString(userId + field, "");
		return s;
	}
	// 保存string类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, String whichSp,
			String field, String value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putString(field, value).commit();
	}
	public static void putByUserId(Context mContext, String whichSp,
			String field, String value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		String userId = ((AppContext)mContext).getUserId();
		sp.edit().putString(userId + field, value).commit();
	}
	// 取出whichSp中field字段对应的int类型的值
	public static int getSharePreInt(Context mContext, String whichSp,
			String field) {
		return getSharePreInt(mContext, whichSp, field, -1);
	}

	public static int getSharePreInt(Context mContext, String whichSp,
			String field, int defaultVaule) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		int i = sp.getInt(field, defaultVaule);// 如果该字段没对应值，则取出0
		return i;
	}

	// 保存int类型的value到whichSp中的field字段
	public static void putSharePre(Context mContext, String whichSp,
			String field, int value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putInt(field, value).commit();
	}

	public static void putSharePreBoolean(Context mContext, String whichSp,
			String field, boolean value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putBoolean(field, value).commit();
	}

	public static boolean getSharePreBoolean(Context mContext, String whichSp,
			String field) {
		return getSharePreBoolean(mContext, whichSp, field, false);
	}
	
	public static boolean getSharePreBoolean(Context mContext, String whichSp,
			String field, boolean defaultValue) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		boolean s = sp.getBoolean(field, defaultValue);// 如果该字段没对应值，则取出字符串0
		return s;
	}
	
	public static void putSharePreLong(Context mContext, String whichSp,
			String field, long value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putLong(field, value).commit();
	}
	public static long getSharePreLong(Context mContext, String whichSp,
			String field, long defaultVaule) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		long i = sp.getLong(field, defaultVaule);// 如果该字段没对应值，则取出0
		return i;
	}
}

package com.qianniu.zhaopin.app;

import java.io.IOException;
import java.util.Random;

import com.qianniu.zhaopin.app.common.MD5;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;


/**
 * 配置信息
 * 
 * @author dm
 * 
 */
public class ConfigOptions {

	public final static boolean debug = false; // 是否是debug模式
	public final static boolean logFlag = true; // 是否打开日志输出 开关
	public final static boolean logWriteToFile = false; // 是否将日志写入文件 开关
	public final static boolean apk_cmcc = false; // 是否中国移动发布包；

	private static String host;
	private static String hostIp;
	private static String imgHostIp;
	private static String webHomeHead;

	private static int httpTimeoutConnection; // http连接超时
	private static int httpTimeoutSocket; // http读取数据超时
	private static int httpRetryTimes; // http重复请求次数

	private static int appServiceDayTimes; // appservice请求间隔 (请求新官方、号外、个人、推送消息)
	private static int appServiceDayTimesFast;
	private static int appServiceNightTimes;
	private static int appServiceWifiTimes;

	private static int adTimesUpdate; // 广告位数据更新时间
	private static int adScrollUpdate; // 广告位自动滚动时间

	private static int umengUploadTimes; // 友盟统计上报间隔

	public static final int TASKVIEW_MODE_VERTICALLIST = 0; // 垂直List模式
	public static final int TASKVIEW_MODE_WATERFALL = 1; // 瀑布流模式
	private static int taskViewMode = TASKVIEW_MODE_WATERFALL; // 悬赏任务首页显示模式
	/* 开发环境内网 */
	private static final String DEVELOP_HOST_INNER = "192.168.1.41";
	private static final String DEVELOP_HOSTIP_INNER = DEVELOP_HOST_INNER
			+ ":8889";
	private static final String DEVELOP_IMGHOSTIP_INNER = DEVELOP_HOST_INNER;
	private static final String DEVELOP_WEBHOMEHEAD_INNER = "l_project_web";
	/* 开发环境外网 */
//	private static final String DEVELOP_HOST_OUTER = "222.66.4.242";
	private static final String DEVELOP_HOST_OUTER = "101.231.104.150";
	private static final String DEVELOP_HOSTIP_OUTER = DEVELOP_HOST_OUTER
			+ ":90";
	private static final String DEVELOP_IMGHOSTIP_OUTER = DEVELOP_HOSTIP_OUTER;
	private static final String DEVELOP_WEBHOMEHEAD_OUTER = "l_project_web";
	/* 预发布环境内网 */
	private static final String PREONLINE_HOST_INNER = "192.168.1.15";
	private static final String PREONLINE_HOSTIP_INNER = PREONLINE_HOST_INNER
			+ ":8889";
	private static final String PREONLINE_IMGHOSTIP_INNER = PREONLINE_HOST_INNER;
	private static final String PREONLINE_WEBHOMEHEAD_INNER = "l_project_web";
	/* 线上环境 */
	private static final String ONLINE_HOST = "www.1000new.com";
//	private static final String ONLINE_HOSTIP = ONLINE_HOST + ":8889";
	private static final String ONLINE_HOSTIP = "app.1000new.com";
	private static final String ONLINE_IMGHOSTIP = ONLINE_HOST;
	private static final String ONLINE_WEBHOMEHEAD = "";
	/* 张雷IP */
	private static final String ZHANGLEI_HOST = "10.30.100.95";
	
	/*官网地址*/
	private static final String WEB_SITE = "http://zhaopin.1000new.com/";
	private static final String COMPANYMAP_SITE = "http://zhaopin.1000new.com/";
	
	// 通讯录推荐显示控制开关
	// false 不显示通讯录推荐(此时最好在AndroidManifest中去掉READ_CONTACTS权限, 
	//		以免被360等工具判断为会读取用户通话记录)
	// true 显示通讯录推荐(必须要在AndroidManifest中添加权限
	//		uses-permission android:name="android.permission.READ_CONTACTS)
	public final static boolean RECOMMEND_CONTACTS = true; // 是否显示通讯录推荐

	static {
		if (debug) { // debug模式下
			host = DEVELOP_HOST_INNER;
			hostIp = DEVELOP_HOSTIP_INNER;
			imgHostIp = DEVELOP_IMGHOSTIP_INNER;
			webHomeHead = DEVELOP_WEBHOMEHEAD_INNER;
//			host = PREONLINE_HOST_INNER;
//			hostIp = PREONLINE_HOSTIP_INNER;
//			imgHostIp = PREONLINE_IMGHOSTIP_INNER;
//			webHomeHead = PREONLINE_WEBHOMEHEAD_INNER;

			appServiceDayTimes = 1 * 60 * 1000; // 1分钟
			appServiceDayTimesFast = 1 * 60 * 1000; // 1分钟
			appServiceNightTimes = 5 * 60 * 1000;// 5分钟
			appServiceWifiTimes = 1 * 60 * 1000;// 1分钟

			adTimesUpdate = 2 * 60 * 1000; // 2分钟
			adScrollUpdate = 2 * 60 * 1000; // 2分钟

			umengUploadTimes = 60 * 1000; // 1分钟

			httpTimeoutConnection = 10000;
			httpTimeoutSocket = 10000;
			httpRetryTimes = 2; // 2次

		} else { // release模式下

			host = ONLINE_HOST;

			hostIp = ONLINE_HOSTIP;
			imgHostIp = ONLINE_IMGHOSTIP;
			webHomeHead = ONLINE_WEBHOMEHEAD;

			appServiceDayTimes = 15 * 60 * 1000; // 15分钟
			appServiceDayTimesFast = 5 * 60 * 1000; // 5分钟
			appServiceNightTimes = 90 * 60 * 1000;// 90分钟
			appServiceWifiTimes = 1 * 60 * 1000;// 1分钟

			adTimesUpdate = 2 * 60 * 60 * 1000; // 2小时
			adScrollUpdate = 2 * 60 * 1000; // 2分钟

			umengUploadTimes = 60 * 1000; // 1分钟

			httpTimeoutConnection = 10000;
			httpTimeoutSocket = 10000;
			httpRetryTimes = 2; // 2次
		}
	}

	public static String getHost() {
		return host;
	}

	public static void setHost(String host) {
		ConfigOptions.host = host;
	}

	public static String getHostIp() {
		return hostIp;
	}

	public static String getImgHostIp() {
		return imgHostIp;
	}

	public static final String getWebHomeHead() {
		return webHomeHead;
	}

	public static final void setWebHomeHead(String webHomeHead) {
		ConfigOptions.webHomeHead = webHomeHead;
	}

	public static int getAppServiceDayTimes() {
		return appServiceDayTimes;
	}

	public static int getAppServiceNightTimes() {
		return appServiceNightTimes;
	}

	public static int getAppServiceWifiTimes() {
		return appServiceWifiTimes;
	}

	public static int getAdTimesUpdate() {
		return adTimesUpdate;
	}

	public static int getAdScrollUpdate() {
		return adScrollUpdate;
	}

	public static int getUmengUploadTimes() {
		return umengUploadTimes;
	}

	public static int getHttpTimeoutConnection() {
		return httpTimeoutConnection;
	}

	public static int getHttpTimeoutSocket() {
		return httpTimeoutSocket;
	}

	public static int getHttpRetryTimes() {
		return httpRetryTimes;
	}

	public static final int getAppServiceDayTimesFast() {
		return appServiceDayTimesFast;
	}

	public static final void setAppServiceDayTimesFast(
			int appServiceDayTimesFast) {
		ConfigOptions.appServiceDayTimesFast = appServiceDayTimesFast;
	}

	/**
	 * 获取悬赏任务首页显示模式
	 * 
	 * @return 悬赏任务首页显示模式
	 */
	public static int getTaskViewMode() {
		return taskViewMode;
	}

	/**
	 * 设置悬赏任务首页显示模式
	 * 
	 * @param taskViewMode
	 *            悬赏任务首页显示模式
	 */
	public static void setTaskViewMode(int taskViewMode) {
		ConfigOptions.taskViewMode = taskViewMode;
	}

	public static String getWebSite() {
		return WEB_SITE;
	}

	public static final String getCompanymapSite() {
		return COMPANYMAP_SITE;
	}
	
	public static String getDeviceMacID(Context context) {
		String str = null;
		try {
			WifiManager localWifiManager = (WifiManager) context
					.getSystemService("wifi");
			WifiInfo localWifiInfo = localWifiManager.getConnectionInfo();
			str = localWifiInfo.getMacAddress();
		} catch (Exception localException) {

		}
		if (str != null) {

			String mac = str.replaceAll(":", "");

			return mac;
		} else {
			//String name = MD5.MD5Encode("macaddr");
			String key = "macaddr";
			String strValue =getStringPreference(context, key, "null");
			if(strValue.compareTo("null")!=0)
				return strValue;

			String str1 = "m";
			Random random = new Random();

			for (int i = 0; i < 6; i++) {
				String stem = Integer.toHexString(random.nextInt(256));
				if (stem.length() < 2)
					str1 += "0";
				str1 += stem;

			}
			setStringPreference(context, key, str1);

			return str1;
		}
	}
	public static void setStringPreference(Context context, String key,
			String value) {
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString(key, value);
		editor.apply();
	}

	public static String getStringPreference(Context context, String key,
			String defValue) {
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(context);
		String vaulue = spf.getString(key, defValue);
		return vaulue;
	}
}

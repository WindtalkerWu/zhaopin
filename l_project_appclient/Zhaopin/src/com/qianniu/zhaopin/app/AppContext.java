package com.qianniu.zhaopin.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.CampaignEntityList;
import com.qianniu.zhaopin.app.bean.ColumnEntity;
import com.qianniu.zhaopin.app.bean.ColumnEntityList;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.ForumTypeList;
import com.qianniu.zhaopin.app.bean.GlobalData;
import com.qianniu.zhaopin.app.bean.GlobalDataEntity;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.ItemInfoEntity;
import com.qianniu.zhaopin.app.bean.ItemInfoList;
import com.qianniu.zhaopin.app.bean.MyAccountPersonalProfile;
import com.qianniu.zhaopin.app.bean.Notice;
import com.qianniu.zhaopin.app.bean.OneLevelData;
import com.qianniu.zhaopin.app.bean.OneLevelJobFunctionsData;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntityList;
import com.qianniu.zhaopin.app.bean.RewardFilterCondition;
import com.qianniu.zhaopin.app.bean.RewardListDataEntity;
import com.qianniu.zhaopin.app.bean.TaskStatusInfoEntity;
import com.qianniu.zhaopin.app.bean.TaxRateInfo;
import com.qianniu.zhaopin.app.bean.ThreeLevelJobFunctionsData;
import com.qianniu.zhaopin.app.bean.TransactionRecordListDataEntity;
import com.qianniu.zhaopin.app.bean.TwoLevelData;
import com.qianniu.zhaopin.app.bean.TwoLevelJobFunctionsData;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.bean.User;
import com.qianniu.zhaopin.app.bean.MyAccontInfoDataEntity;
import com.qianniu.zhaopin.app.bean.VersionData;
import com.qianniu.zhaopin.app.bean.reqConsumelog;
import com.qianniu.zhaopin.app.bean.reqMyApply;
import com.qianniu.zhaopin.app.bean.reqQuickRecommendData;
import com.qianniu.zhaopin.app.bean.reqRewardList;
import com.qianniu.zhaopin.app.bean.reqRewardListBetwen;
import com.qianniu.zhaopin.app.bean.reqTaskId;
import com.qianniu.zhaopin.app.common.CyptoUtils;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.FileUtils;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.R;
import com.tencent.tauth.Tencent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 全局应用程序类：用于保存和调用全�?��用配置及访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 *
 */
/**
 * @author Administrator
 * 
 */
public class AppContext extends Application {

	private static final String TAG = "AppContext";
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final int PAGE_SIZE = 20;// 默认分页大小
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

	private static AppContext appcontext = null;
	private boolean login = false; // 登录状�?
	private int loginUid = 0; // 登录用户的id
	private String access_token = null;
	private String user_id = null;
	private int nloginType = HeadhunterPublic.LOGINTYPE_DEFAULT;
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	private ThreadPoolController threadPool;
	private String saveImagePath;// 保存图片路径

	private Handler unLoginHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				UIHelper.ToastMessage(AppContext.this,
						getString(R.string.msg_login_error));
				// UIHelper.showLoginDialog(AppContext.this);
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		// 注册App异常崩溃处理�?
		// Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
		appcontext = this;
		init();
	}

	synchronized public static AppContext getAppContext() {
		return appcontext;
	}

	/**
	 * 初始�?
	 */
	private void init() {
		// 设置保存图片的路�?
		saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
		if (StringUtils.isEmpty(saveImagePath)) {
			setProperty(AppConfig.SAVE_IMAGE_PATH,
					AppConfig.DEFAULT_SAVE_IMAGE_ABSOLUTE_PATH);
			saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_ABSOLUTE_PATH;
		}
		// 网络连接判断
		if (!isNetworkConnected())
			UIHelper.ToastMessage(this, R.string.network_not_connected);
		// 初始化登录
		initLoginInfo();
		clearFileCache(appcontext);

	}

	/**
	 * �?��当前系统声音是否为正常模�?
	 * 
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	/**
	 * 应用程序是否发出提示�?
	 * 
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}

	/**
	 * �?��网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
	/**
	 * �?��网络是否可用
	 * 
	 * @return
	 */
	public static  boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网�? 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方�?
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 获取App安装包信�?
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 获取App唯一标识
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogin() {
		return login;
	}

	/**
	 * 获取用户登录类型
	 * 
	 * @return
	 */
	public int getloginType() {
		return nloginType;
	}

	/**
	 * 判断用户是否登录，未登录则跳转到登录界面
	 * 
	 * @param activity
	 * @return
	 */
	public boolean isLoginAndToLogin(Activity activity) {
		boolean blogin = isLogin();
		if (!blogin) {
			UIHelper.ToastMessage(activity, R.string.usermanager_unlogin);
			UIHelper.showLoginActivityForResult(activity);
		}
		return login;
	}
	public boolean isLoginAndToLogin(Context activity) {
		boolean blogin = isLogin();
		if (!blogin) {
			UIHelper.ToastMessage(activity, R.string.usermanager_unlogin);
			UIHelper.showLoginActivity(activity);
		}
		return login;
	}

	/**
	 * 获取登录用户id
	 * 
	 * @return
	 */
	public int getLoginUid() {
		return this.loginUid;
	}

	/**
	 * 获取登录用户access token
	 * 
	 * @return
	 */
	public String getAccessToken() {
		return this.access_token;
	}

	/**
	 * 获取登录用户user_id
	 * 
	 * @return
	 */
	public final String getUserId() {
		return user_id;
	}

	/**
	 * 用户注销
	 */
	public void Logout() {
		if (HeadhunterPublic.LOGINTYPE_QQ == nloginType) {
			Tencent tencent = Tencent.createInstance(
					UIHelper.getTHPAPPIDForNum(appcontext, HeadhunterPublic.THPKEY_QQ_APPID), 
					AppContext.this);
			if (null != tencent) {
				if (tencent.isSessionValid()) {
					tencent.logout(AppContext.this);
				}
			}
		}

		ApiClient.cleanCookie();
		cleanToken();
		this.cleanCookie();
	}

	/**
	 * 未登录或修改密码后的处理
	 */
	public Handler getUnLoginHandler() {
		return this.unLoginHandler;
	}

	/**
	 * 初始化用户登录信�?
	 */
	public void initLoginInfo() {
		User loginUser = getLoginInfo();
		if (loginUser != null && loginUser.getAccessToken() != null
				&& loginUser.isRememberMe()) {
			this.access_token = loginUser.getAccessToken();
			this.user_id = loginUser.getUserid();
			this.nloginType = Integer.valueOf(loginUser.getLoginType());
			this.login = true;
		} else {
			this.Logout();
		}
	}

	/**
	 * 用户登录验证
	 * 
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public User loginVerify(ReqUserInfo req) throws AppException {

		return ApiClient.login(this, req);
	}

	/**
	 * 用户注册
	 * 
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public User registerVerify(String strUserName, String strUserPwd)
			throws AppException {

		return ApiClient.register(this, strUserName, strUserPwd);
	}

	/**
	 * 找回用户密码(手机)
	 * 
	 * @param account
	 * @param pwd
	 * @param verify
	 *            验证码
	 * @return
	 * @throws AppException
	 */
	public User findpwdMobileVerify(String strUserName, String strUserPwd,
			String strVerify) throws AppException {

		return ApiClient.findpasswordmobile(this, strUserName, strUserPwd,
				strVerify);
	}

	/**
	 * 找回用户密码
	 * 
	 * @param strUserName
	 * @param nType
	 * @return
	 * @throws AppException
	 */
	public User findpassword(String strUserName, int nType) throws AppException {

		return ApiClient.findpassword(this, strUserName, nType);
	}

	/**
	 * 找回用户密码
	 * 
	 * @param account
	 * @param pwd
	 * @param verify
	 *            验证码
	 * @return
	 * @throws AppException
	 */
	public User findpwdMailVerify(String strUserName) throws AppException {

		return ApiClient.findpasswordmail(this, strUserName);
	}

	/**
	 * 保存全局数据版本号
	 * 
	 * @param strVersion
	 *            全局数据版本号
	 * @throws AppException
	 */
	public void saveGlobalDataVersion(String strVersion) throws AppException {
		GlobalDataEntity.saveGlobalDataVersion(this, strVersion);
	}

	/**
	 * 获取全局数据版本号
	 * 
	 * @return 全局数据版本号
	 * @throws AppException
	 */
	public String getGlobalDataVersion() throws AppException {
		return GlobalDataEntity.getGlobalDataVersion(this);
	}

	/**
	 * 从服务器获取全局数据
	 * 
	 * @param nType
	 *            请求类型
	 * @return
	 * @throws AppException
	 */
	public GlobalDataEntity getGlobalDataEntity(int nType) throws AppException {
		return ApiClient.getGlobalDataEntity(this, nType);
	}

	/**
	 * 判断是否要删除指定类型的全局数据
	 * 
	 * @param lsOLD
	 * @param nType
	 */
	public boolean checkDeleteGlobalData(List<OneLevelData> lsOLD, int nType) {
		if (null != lsOLD) {
			if (lsOLD.size() > 0) {
				GlobalDataTable.deleteGlobalData(this, nType);
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断是否要删除指定类型的全局数据
	 * 
	 * @param lsOLD
	 * @param nType
	 */
	public boolean checkDeleteTwoLevelData(List<TwoLevelData> lsTLD, int nType) {
		if (null != lsTLD) {
			if (lsTLD.size() > 0) {
				GlobalDataTable.deleteGlobalData(this, nType);
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断是否要删除指定类型的全局数据
	 * 
	 * @param lsTLJFD
	 * @param nType
	 * @return
	 */
	public boolean checkDeleteThreeLevelJobFunctionsData(
			List<ThreeLevelJobFunctionsData> lsTLJFD, int nType) {
		if (null != lsTLJFD) {
			if (lsTLJFD.size() > 0) {
				GlobalDataTable.deleteGlobalData(this, nType);
				return true;
			}
		}

		return false;
	}

	/**
	 * 保存全局数据
	 * 
	 * @param gd
	 *            全局数据
	 * @throws AppException
	 */
	public void savaGlobalData(GlobalData gd) throws AppException {

		List<GlobalDataTable> lsGD = new ArrayList<GlobalDataTable>();

		// 城市
		if (checkDeleteGlobalData(gd.getAllCity(), DBUtils.GLOBALDATA_TYPE_CITY)) {
			for (OneLevelData od : gd.getAllCity()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_CITY);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 学历
		if (checkDeleteGlobalData(gd.getEducation(),
				DBUtils.GLOBALDATA_TYPE_EDUCATION)) {
			for (OneLevelData od : gd.getEducation()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_EDUCATION);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 求职状态
		if (checkDeleteGlobalData(gd.getJobstatus(),
				DBUtils.GLOBALDATA_TYPE_JOBSTATUS)) {
			for (OneLevelData od : gd.getJobstatus()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_JOBSTATUS);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 年薪
		if (checkDeleteGlobalData(gd.getSalary(),
				DBUtils.GLOBALDATA_TYPE_SALARY)) {
			for (OneLevelData od : gd.getSalary()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_SALARY);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 到岗时间
		if (checkDeleteGlobalData(gd.getWork_Experience(),
				DBUtils.GLOBALDATA_TYPE_ARRIVETIME)) {
			for (OneLevelData od : gd.getWork_Experience()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_ARRIVETIME);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 语言技能
		if (checkDeleteGlobalData(gd.getLanguage(),
				DBUtils.GLOBALDATA_TYPE_LANGUAGE)) {
			for (OneLevelData od : gd.getLanguage()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_LANGUAGE);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 悬赏方式
		if (checkDeleteGlobalData(gd.getRewardway(),
				DBUtils.GLOBALDATA_TYPE_REWARDWAY)) {
			for (OneLevelData od : gd.getRewardway()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_REWARDWAY);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 悬赏周期
		if (checkDeleteGlobalData(gd.getRewardcycle(),
				DBUtils.GLOBALDATA_TYPE_REWARDCYCLE)) {
			for (OneLevelData od : gd.getRewardcycle()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_REWARDCYCLE);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 行业
		if (checkDeleteTwoLevelData(gd.getJob_Industry(),
				DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY)) {
			for (TwoLevelData td : gd.getJob_Industry()) {
				GlobalDataTable gdtOne = new GlobalDataTable();
				gdtOne.setType(DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY);
				gdtOne.setID(td.getId());
				gdtOne.setName(td.getLabel());
				gdtOne.setNamePinYin(td.getPinYin());
				gdtOne.setHavingParent(false);
				gdtOne.setParentID("");
				gdtOne.setHavingSubClass(true);

				lsGD.add(gdtOne);

				for (OneLevelData od : td.getOneLevel()) {
					GlobalDataTable gdt = new GlobalDataTable();
					gdt.setType(DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY);
					gdt.setID(od.getId());
					gdt.setName(od.getLabel());
					gdt.setNamePinYin(od.getPinYin());
					gdt.setHavingParent(true);
					gdt.setParentID(td.getId());
					gdt.setHavingSubClass(false);
					gdt.setLevel(2);

					lsGD.add(gdt);
				}
			}
		}

		// 专业
		if (checkDeleteGlobalData(gd.getSpecialty(),
				DBUtils.GLOBALDATA_TYPE_SPECIALTY)) {
			for (OneLevelData od : gd.getSpecialty()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_SPECIALTY);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 语言掌握程度
		if (checkDeleteGlobalData(gd.getLanguage_Mastery(),
				DBUtils.GLOBALDATA_TYPE_LANGUAGEMASTERY)) {
			for (OneLevelData od : gd.getLanguage_Mastery()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_LANGUAGEMASTERY);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 语言读写能力
		if (checkDeleteGlobalData(gd.getLanguage_Literacy(),
				DBUtils.GLOBALDATA_TYPE_LANGUAGLITERACY)) {
			for (OneLevelData od : gd.getLanguage_Literacy()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_LANGUAGLITERACY);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 语言听说能力
		if (checkDeleteGlobalData(gd.getLanguage_Speaking(),
				DBUtils.GLOBALDATA_TYPE_LANGUAGSPEAKING)) {
			for (OneLevelData od : gd.getLanguage_Speaking()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_LANGUAGSPEAKING);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 热点职位
		if (checkDeleteGlobalData(gd.getHot_Keyword(),
				DBUtils.GLOBALDATA_TYPE_HOTKEYWORD)) {
			for (OneLevelData od : gd.getHot_Keyword()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_HOTKEYWORD);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 热点城市
		if (checkDeleteGlobalData(gd.getLocation_Hot_City(),
				DBUtils.GLOBALDATA_TYPE_HOTCITY)) {
			for (OneLevelData od : gd.getLocation_Hot_City()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_HOTCITY);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 推荐理由
		if (checkDeleteGlobalData(gd.getM126(),
				DBUtils.GLOBALDATA_TYPE_RECOMMENDREASON)) {
			for (OneLevelData od : gd.getM126()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_RECOMMENDREASON);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 图片基础地址
		if (checkDeleteGlobalData(gd.getM128(),
				DBUtils.GLOBALDATA_TYPE_PICTUREBASEADDRESS)) {
			for (OneLevelData od : gd.getM128()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_PICTUREBASEADDRESS);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 图片提交地址
		if (checkDeleteGlobalData(gd.getM129(),
				DBUtils.GLOBALDATA_TYPE_PICTURESERVERADDRESS)) {
			for (OneLevelData od : gd.getM129()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_PICTURESERVERADDRESS);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 我的记录状态
		if (checkDeleteGlobalData(gd.getM125(),
				DBUtils.GLOBALDATA_TYPE_VERIFYSTATUS)) {
			for (OneLevelData od : gd.getM125()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_VERIFYSTATUS);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 悬赏任务发布状态
		if (checkDeleteGlobalData(gd.getM134(),
				DBUtils.GLOBALDATA_TYPE_PUBLISHSTATUS)) {
			for (OneLevelData od : gd.getM134()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_PUBLISHSTATUS);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 接受任务模式
		if (checkDeleteGlobalData(gd.getM137(),
				DBUtils.GLOBALDATA_TYPE_ACCEPTTASKMODE)) {
			for (OneLevelData od : gd.getM137()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_ACCEPTTASKMODE);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		// 职能分类
		if (checkDeleteThreeLevelJobFunctionsData(gd.getJob_functions(),
				DBUtils.GLOBALDATA_TYPE_JOBFUNCTION)) {
			for (ThreeLevelJobFunctionsData three : gd.getJob_functions()) {
				GlobalDataTable gdtOne = new GlobalDataTable();
				gdtOne.setType(DBUtils.GLOBALDATA_TYPE_JOBFUNCTION);
				gdtOne.setID(three.getId());
				gdtOne.setName(three.getLabel());
				gdtOne.setNamePinYin(three.getPinyin());
				gdtOne.setHavingParent(false);
				gdtOne.setParentID("");
				gdtOne.setHavingSubClass(true);

				lsGD.add(gdtOne);

				for (TwoLevelJobFunctionsData two : three.getChildren()) {
					GlobalDataTable gdtTwo = new GlobalDataTable();
					gdtTwo.setType(DBUtils.GLOBALDATA_TYPE_JOBFUNCTION);
					gdtTwo.setID(two.getId());
					gdtTwo.setName(two.getLabel());
					gdtTwo.setNamePinYin(two.getPinyin());
					gdtTwo.setHavingParent(true);
					gdtTwo.setParentID(three.getId());
					gdtTwo.setHavingSubClass(true);
					gdtTwo.setLevel(2);

					lsGD.add(gdtTwo);

					for (OneLevelJobFunctionsData one : two.getChildren()) {
						GlobalDataTable gdtThree = new GlobalDataTable();
						gdtThree.setType(DBUtils.GLOBALDATA_TYPE_JOBFUNCTION);
						gdtThree.setID(one.getId());
						gdtThree.setName(one.getLabel());
						gdtThree.setNamePinYin(one.getPinyin());
						gdtThree.setHavingParent(true);
						gdtThree.setParentID(two.getId());
						gdtThree.setHavingSubClass(false);
						gdtThree.setLevel(3);

						lsGD.add(gdtThree);
					}
				}
			}
		}

		// 付款标识
		if (checkDeleteGlobalData(gd.getM143(), DBUtils.GLOBALDATA_TYPE_PAYFLAG)) {
			for (OneLevelData od : gd.getM143()) {
				GlobalDataTable gdt = new GlobalDataTable();
				gdt.setType(DBUtils.GLOBALDATA_TYPE_PAYFLAG);
				gdt.setID(od.getId());
				gdt.setName(od.getLabel());
				gdt.setNamePinYin(od.getPinYin());
				gdt.setHavingParent(false);
				gdt.setParentID("");
				gdt.setHavingSubClass(false);

				lsGD.add(gdt);
			}
		}

		//
		GlobalDataTable.saveGlobalData(this, lsGD);
	}

	/**
	 * 初始化全局数据
	 * 
	 * @param gd
	 *            全局数据
	 * @throws AppException
	 */
	public void initGlobalData(List<GlobalDataTable> lsGDT) throws AppException {
		GlobalDataTable.saveGlobalData(this, lsGDT);
	}

	/**
	 * 获取悬赏首页数据
	 * 
	 * @param rrl
	 *            请求参数
	 * @return
	 * @throws AppException
	 */
	public RewardListDataEntity getRewardListDataEntity(reqRewardList rrl)
			throws AppException {
		return ApiClient.getRewardListDataEntity(this, rrl);
	}
	
	/**
	 * 获取悬赏首页数据(参数有betwen)
	 * 
	 * @param rrl
	 *            请求参数
	 * @return
	 * @throws AppException
	 */
	public RewardListDataEntity getRewardListDataEntityBetwen(reqRewardListBetwen rrlb)
			throws AppException {
		return ApiClient.getRewardListDataEntityBetwen(this, rrlb);
	}

	/**
	 * 获取同一类型数据
	 * 
	 * @param nType
	 *            类型
	 * @return
	 * @throws AppException
	 */
	public List<GlobalDataTable> getTpyeData(int nType) throws AppException {
		return GlobalDataTable.getTpyeData(this, nType);
	}

	/**
	 * 获取同一类型数据(不进行排序)
	 * 
	 * @param nType
	 *            类型
	 * @return
	 * @throws AppException
	 */
	public List<GlobalDataTable> getTpyeDataNoSort(int nType)
			throws AppException {
		return GlobalDataTable.queryGlobalData(this, nType);
		// return GlobalDataTable.getTpyeDataNoSort(this, nType);
	}

	/**
	 * 获取同一类型数据的父类(不进行排序)
	 * 
	 * @param nType
	 *            类型
	 * @return
	 * @throws AppException
	 */
	public List<GlobalDataTable> getParentClassDataNoSort(int nType)
			throws AppException {
		return GlobalDataTable.getSubClassDataNoSort(this, nType, null);
	}

	/**
	 * 获取子类数据
	 * 
	 * @param strParentId
	 *            父类ID
	 * @return
	 * @throws AppException
	 */
	public List<GlobalDataTable> getSubClassData(int nType, String strParentId)
			throws AppException {
		return GlobalDataTable.getSubClassData(this, nType, strParentId);
	}

	/**
	 * 获取指定类型指定父类的子类数据(不排序)
	 * 
	 * @param strParentId
	 *            父类ID
	 * @return
	 * @throws AppException
	 */
	public List<GlobalDataTable> getSubClassDataNoSort(int nType,
			String strParentId) throws AppException {
		return GlobalDataTable.getSubClassDataNoSort(this, nType, strParentId);
	}

	/**
	 * 获取指定类型数据(不排序)
	 * 
	 * @param nType
	 *            数据类型id
	 * @param strParentId
	 *            父类ID
	 * @param bHavingSubClass
	 *            是否有子类
	 * @return
	 * @throws AppException
	 */
	public List<GlobalDataTable> getSubClassDataNoSort(int nType,
			String strParentId, boolean bHavingSubClass) throws AppException {
		return GlobalDataTable.getSubClassDataNoSort(this, nType, strParentId,
				bHavingSubClass);
	}

	/**
	 * 获取指定类型，指定id的最低层数据(无子类)
	 * 
	 * @param nType
	 * @param rowid
	 * @return
	 * @throws AppException
	 */
	public GlobalDataTable getTypeBotoomDataById(int nType, String rowid)
			throws AppException {
		return GlobalDataTable.getTypeBotoomDataById(this, nType, rowid);
	}

	/**
	 * 发送收藏任务/取消收藏任务请求
	 * 
	 * @param strTaskId
	 * @param nCollectionStatus
	 * @return
	 * @throws AppException
	 */
	public Result applyCollection(String strTaskId, int nCollectionStatus)
			throws AppException {

		return ApiClient.applyCollection(this, strTaskId, nCollectionStatus);
	}

	/**
	 * 发送收藏公司/取消收藏公司请求
	 * 
	 * @param strTaskId
	 * @param nCollectionStatus
	 * @return
	 * @throws AppException
	 */
	public Result CompanyCollection(String strTaskId, int nCollectionStatus)
			throws AppException {

		return ApiClient.CompanyCollection(this, strTaskId, nCollectionStatus);
	}

	/**
	 * 获取我的收藏数据列表
	 * 
	 * @param rrl
	 * @return
	 * @throws AppException
	 */
	public RewardListDataEntity getCollectionList(reqRewardList rrl,
			int nRequestType) throws AppException {

		return ApiClient.getCollectionList(this, rrl, nRequestType);
	}

	/**
	 * 获取我的记录数据列表
	 * 
	 * @param rma
	 * @return
	 * @throws AppException
	 */
	public RewardListDataEntity getRecordList(reqMyApply rma)
			throws AppException {

		return ApiClient.getRecordList(this, rma);
	}

	/**
	 * 接受任务
	 * 
	 * @param strTaskId
	 * @param strApplyType
	 * @param strResumeId
	 * @return
	 * @throws AppException
	 */
	public Result applyReward(String strTaskId, String strApplyType,
			String strResumeId) throws AppException {

		return ApiClient
				.applyReward(this, strTaskId, strApplyType, strResumeId);
	}

	/**
	 * 快速推荐
	 * 
	 * @param reqQRD
	 * @return
	 * @throws AppException
	 */
	public Result quickRecommend(reqQuickRecommendData reqQRD)
			throws AppException {

		return ApiClient.quickRecommend(this, reqQRD);
	}

	/**
	 * 获取验证码
	 * 
	 * @param account
	 * @return
	 * @throws AppException
	 */
	public User getVerify(String strUserName) throws AppException {

		return ApiClient.getVerify(this, strUserName);
	}

	/**
	 * 保存登录信息
	 * 
	 * @param username
	 * @param pwd
	 */
	public void saveLoginInfo(final User user) {

		this.access_token = user.getAccessToken();
		this.user_id = user.getUserid();
		this.login = true;
		this.nloginType = Integer.valueOf(user.getLoginType());

		setProperties(new Properties() {
			{
				setProperty("user.access_token", user.getAccessToken());
				setProperty("user.user_id", user.getUserid());
				setProperty("user.name", user.getName());
				setProperty("user.account", user.getAccount());
				setProperty("user.login_type", user.getLoginType());
				setProperty("user.thirdpart_id", user.getThirdPartId());
				setProperty("user.thirdpart_token", user.getThirdPartToken());
				setProperty("user.pwd",
						CyptoUtils.encode("matrixdm", user.getPwd()));
				setProperty("user.isRememberMe",
						String.valueOf(user.isRememberMe()));// 是否记住我的信息

			}
		});
	}

	public void cleanToken() {
		this.loginUid = 0;
		this.login = false;
		this.access_token = null;
		this.user_id = null;
		this.nloginType = HeadhunterPublic.LOGINTYPE_DEFAULT;
		removeProperty("user.access_token", "user.thirdpart_token",
				"user.user_id", "user.login_type");
	}

	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		this.loginUid = 0;
		this.access_token = null;
		this.user_id = null;
		this.login = false;
		this.nloginType = HeadhunterPublic.LOGINTYPE_DEFAULT;
		removeProperty("user.access_token", "user.uid", "user.name",
				"user.face", "user.account", "user.login_type",
				"user.thirdpart_id", "user.thirdpart_token", "user.pwd",
				"user.location", "user.followers", "user.fans", "user.score",
				"user.isRememberMe", "user.user_id");
	}

	/**
	 * QQ登录时清除登录信息
	 * 
	 * @param tencent
	 * @param context
	 */
	public void cleanQQLoginInfo(Tencent tencent, Context context) {
		if (null != tencent) {
			tencent.logout(context);
		}

		this.loginUid = 0;
		this.access_token = null;
		this.user_id = null;
		this.login = false;
		this.nloginType = HeadhunterPublic.LOGINTYPE_DEFAULT;
		removeProperty("user.access_token", "user.uid", "user.name",
				"user.face", "user.account", "user.login_type",
				"user.thirdpart_id", "user.thirdpart_token", "user.pwd",
				"user.location", "user.followers", "user.fans", "user.score",
				"user.isRememberMe", "user.user_id");
	}

	/**
	 * 获取登录信息
	 * 
	 * @return
	 */
	public User getLoginInfo() {
		User lu = new User();

		lu.setAccessToken(getProperty("user.access_token"));
		lu.setName(getProperty("user.name"));
		lu.setAccount(getProperty("user.account"));
		lu.setLoginType(getProperty("user.login_type"));
		lu.setThirdPartId(getProperty("user.thirdpart_id"));
		lu.setThirdPartToken(getProperty("user.thirdpart_token"));
		lu.setPwd(CyptoUtils.decode("matrixdm", getProperty("user.pwd")));
		lu.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
		lu.setUserid(getProperty("user.user_id"));
		return lu;
	}

	/**
	 * 获取当前账户的用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		return getProperty("user.name");
	}

	public String getAccount() {
		return getProperty("user.account");
	}

	/**
	 * 保存用户头像
	 * 
	 * @param fileName
	 * @param bitmap
	 */
	public void saveUserFace(String fileName, Bitmap bitmap) {
		try {
			ImageUtils.saveImage(this, fileName, bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取用户头像
	 * 
	 * @param key
	 * @return
	 * @throws AppException
	 */
	public Bitmap getUserFace(String key) throws AppException {
		FileInputStream fis = null;
		try {
			fis = openFileInput(key);
			return BitmapFactory.decodeStream(fis);
		} catch (Exception e) {
			throw AppException.run(e);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 是否加载显示文章图片
	 * 
	 * @return
	 */
	public boolean isLoadImage() {
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		// 默认是加载的
		if (StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}

	/**
	 * 设置是否加载文章图片
	 * 
	 * @param b
	 */
	public void setConfigLoadimage(boolean b) {
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}

	/**
	 * 是否发出提示�?
	 * 
	 * @return
	 */
	public boolean isVoice() {
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		// 默认是开启提示声�?
		if (StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}

	/**
	 * 设置是否发出提示�?
	 * 
	 * @param b
	 */
	public void setConfigVoice(boolean b) {
		setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
	}

	/**
	 * 是否启动�?��更新
	 * 
	 * @return
	 */
	public boolean isCheckUp() {
		String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
		// 默认是开�?
		if (StringUtils.isEmpty(perf_checkup))
			return true;
		else
			return StringUtils.toBool(perf_checkup);
	}

	/**
	 * 设置启动�?��更新
	 * 
	 * @param b
	 */
	public void setConfigCheckUp(boolean b) {
		setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
	}

	/**
	 * 是否左右滑动
	 * 
	 * @return
	 */
	public boolean isScroll() {
		String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
		// 默认是关闭左右滑�?
		if (StringUtils.isEmpty(perf_scroll))
			return false;
		else
			return StringUtils.toBool(perf_scroll);
	}

	/**
	 * 设置是否左右滑动
	 * 
	 * @param b
	 */
	public void setConfigScroll(boolean b) {
		setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
	}

	/**
	 * 是否Https登录
	 * 
	 * @return
	 */
	public boolean isHttpsLogin() {
		String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
		// 默认是http
		if (StringUtils.isEmpty(perf_httpslogin))
			return false;
		else
			return StringUtils.toBool(perf_httpslogin);
	}

	/**
	 * 设置是是否Https登录
	 * 
	 * @param b
	 */
	public void setConfigHttpsLogin(boolean b) {
		setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
	}

	/**
	 * 清除保存的缓�?
	 */
	public void cleanCookie() {
		removeProperty(AppConfig.CONF_COOKIE);
	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {

		deleteDatabase("webview.db");
		deleteDatabase("webview.db-shm");
		deleteDatabase("webview.db-wal");
		deleteDatabase("webviewCache.db");
		deleteDatabase("webviewCache.db-shm");
		deleteDatabase("webviewCache.db-wal");
		// 清除数据缓存
		clearCacheFolder(getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(getCacheDir(), System.currentTimeMillis());
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),
					System.currentTimeMillis());
		}
		// 清除编辑器保存的临时内容
		Properties props = getProperties();
		for (Object key : props.keySet()) {
			String _key = key.toString();
			if (_key.startsWith("temp"))
				removeProperty(_key);
		}
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param numDays
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 将对象保存到内存缓存�?
	 * 
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}

	/**
	 * 从内存缓存中获取对象
	 * 
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key) {
		return memCacheRegion.get(key);
	}

	/**
	 * 保存磁盘缓存
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取磁盘缓存数据
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try {
			fis = openFileInput("cache_" + key + ".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	/**
	 * 获取内存中保存图片的路径
	 * 
	 * @return
	 */
	public String getSaveImagePath() {
		return saveImagePath;
	}

	/**
	 * 设置内存中保存图片的路径
	 * 
	 * @return
	 */
	public void setSaveImagePath(String saveImagePath) {
		this.saveImagePath = saveImagePath;
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
	public ForumTypeList getInfoTypesList(boolean isRefresh, boolean isFirst,
			int cat_type, int column_id) throws AppException {
		ForumTypeList list = null;
		String key = "ForumTypeList";
		String error = this.getResources().getString(
				R.string.app_status_net_disconnected);
		Result res = null;
		if (isNetworkConnected() && (isRefresh || isFirst)) {
			try {
				String url = null;
				if (cat_type == DBUtils.CATALOG_TYPE_INFO_CATALOG)
					url = URLs.FORUMTYPES_LIST;
				else if (cat_type == DBUtils.CATALOG_TYPE_EXTRA_CATALOG)
					url = URLs.EXTRA_LIST + "/" + column_id;
				else if (cat_type == DBUtils.CATALOG_TYPE_CUSTOMMANAGER_CATALOG) {

					url = URLs.EXTRA_RSSLIST + "/" + column_id;
				} else
					url = URLs.FORUMTYPES_LIST;

				list = ApiClient.getInfoTypesList(this, url, cat_type);

			} catch (AppException e) {
				res = new Result(-2, " AppException ");
			}
		} else {
			res = new Result(-1, this.getResources().getString(
					R.string.app_status_net_disconnected));
		}

		if (list == null)
			list = getInfoTypesListFromDb(cat_type, column_id);
		if (res != null && list != null) {
			list.setValidate(res);
		}
		return list;
	}

	public ForumTypeList getInfoTypesListFromNet(boolean isRefresh,
			boolean isFirst, int cat_type, int column_id) throws AppException {
		ForumTypeList list = new ForumTypeList();
		Result res = null;
		if (!isNetworkConnected()) {
			res = new Result(Result.CODE_NETWORK_UNCONNECT, "网络连接未打开");
			list.setValidate(res);
			return null;
		}
		if (isRefresh || isFirst) {
			try {
				String url = null;
				if (cat_type == DBUtils.CATALOG_TYPE_INFO_CATALOG)
					url = URLs.FORUMTYPES_LIST;
				else if (cat_type == DBUtils.CATALOG_TYPE_EXTRA_CATALOG)
					url = URLs.EXTRA_LIST + "/" + column_id;
				if (cat_type == DBUtils.CATALOG_TYPE_CUSTOMMANAGER_CATALOG) {

					url = URLs.EXTRA_RSSLIST + "/" + column_id;
				} else
					url = URLs.FORUMTYPES_LIST;

				list = ApiClient.getInfoTypesList(this, url, cat_type);

			} catch (AppException e) {
				throw e;
			}
			return list;
		}
		return null;
	}

	/**
	 * 从数据库读取版块列表
	 * 
	 * @param cat_type
	 *            如果没有，则填 0；
	 * @param column_id
	 *            如果没有，则填 0；
	 * @return
	 * @throws AppException
	 */
	public ForumTypeList getInfoTypesListFromDb(int cat_type, int column_id)
			throws AppException {

		DBUtils db = DBUtils.getInstance(this);
		String sql = new String();
		Result res = null;
		String rowCluase = new String(DBUtils.KEY_ROWDATA_TYPE);
		if (cat_type > 0) {
			rowCluase += (" = " + String.valueOf(cat_type));
		} else {

			rowCluase += (" IS NULL ");
		}
		sql += rowCluase;
		if (column_id > 0) {
			sql += (" AND " + DBUtils.KEY_COLUMN_ID + " = " + column_id);
		} else {
			sql += (" AND " + DBUtils.KEY_COLUMN_ID + " IS NULL ");
		}
		Cursor cursor = db.queryWithOrderBindUser(DBUtils.catalogTableName,
				new String[] { "*" }, sql, null, DBUtils.KEY_CAT_ID + " ASC");

		ForumTypeList infoTypeList = new ForumTypeList();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int nameColumn = cursor.getColumnIndex(DBUtils.KEY_JSON_CONTENT);
			String json = cursor.getString(nameColumn);
			if (json == null || json.length() == 0)
				continue;
			JSONObject childjsonObj;
			try {
				childjsonObj = new JSONObject(json);

				ForumType infotype = ForumType.parse(this, childjsonObj, false,
						0);
				int ureadcount = cursor.getInt(cursor
						.getColumnIndex(DBUtils.KEY_UNREAD_COUNT));
				infotype.setNewCount(ureadcount);
				if (infotype != null && infotype.getValidate().OK()) {
					infoTypeList.getInfoTypelist().add(infotype);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw AppException.io(e);
			}
		}
		cursor.close();
		db.close();
		res = new Result(Result.CODE_OK, "ok");
		infoTypeList.setValidate(res);
		return infoTypeList;

	}

	/**
	 * 从数据库读取版块列表
	 * 
	 * @param cat_type
	 *            如果没有，则填 0；
	 * @param column_id
	 *            如果没有，则填 0；
	 * @param islogin
	 *            :登录状态，(true:登录,false:未登录)
	 * @return
	 * @throws AppException
	 */

	public ForumTypeList getInfoTypesListFromDb(int cat_type, int column_id,
			boolean islogin) throws AppException {

		DBUtils db = DBUtils.getInstance(this);
		String sql = new String();
		Result res = null;
		String rowCluase = new String(DBUtils.KEY_ROWDATA_TYPE);
		if (cat_type > 0) {
			rowCluase += (" = " + String.valueOf(cat_type));
		} else {

			rowCluase += (" IS NULL ");
		}
		sql += rowCluase;
		if (column_id > 0) {
			sql += (" AND " + DBUtils.KEY_COLUMN_ID + " = " + column_id);
		} else {
			sql += (" AND " + DBUtils.KEY_COLUMN_ID + " IS NULL ");
		}
		if (islogin) {

			sql += (" AND (")
					+ (DBUtils.KEY_CUSTOM_TYPE + " = " + ForumType.CUSTOM_TYPE_DEFAULT)
					+ " OR "
					+ (DBUtils.KEY_CUSTOM_TYPE + " = " + ForumType.CUSTOM_TYPE_SELECTED)
					+ " )";
		} else {
			sql += (" AND " + DBUtils.KEY_PREVIEW_TYPE + " = " + ForumType.PREVIEW_TYPE_SYSTEM);
			sql += (" AND " + DBUtils.KEY_CUSTOM_TYPE + " = " + ForumType.CUSTOM_TYPE_DEFAULT);
		}
		Cursor cursor = db.queryWithOrderBindUser(DBUtils.catalogTableName,
				new String[] { "*" }, sql, null, DBUtils.KEY_CAT_ID + " ASC");

		ForumTypeList infoTypeList = new ForumTypeList();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int nameColumn = cursor.getColumnIndex(DBUtils.KEY_JSON_CONTENT);
			String json = cursor.getString(nameColumn);
			if (json == null || json.length() == 0)
				continue;
			JSONObject childjsonObj;
			try {
				childjsonObj = new JSONObject(json);

				ForumType infotype = ForumType.parse(this, childjsonObj, false,
						0);
				int ureadcount = cursor.getInt(cursor
						.getColumnIndex(DBUtils.KEY_UNREAD_COUNT));
				infotype.setNewCount(ureadcount);
				if (infotype != null && infotype.getValidate().OK()) {
					infoTypeList.getInfoTypelist().add(infotype);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw AppException.io(e);
			}
		}
		cursor.close();
		db.close();
		res = new Result(Result.CODE_OK, "ok");
		infoTypeList.setValidate(res);
		return infoTypeList;

	}

	/**
	 * 从数据库模糊搜索版块列表
	 * 
	 * @return ForumTypeList
	 * @throws AppException
	 */
	public ForumTypeList serachInfoTypesListFromDb(int cat_type, int column_id,
			String[] args, boolean islogin) throws AppException {

		DBUtils db = DBUtils.getInstance(this);
		String sql = new String();

		String rowCluase = new String(DBUtils.KEY_ROWDATA_TYPE);
		if (cat_type > 0) {
			rowCluase += (" = " + String.valueOf(cat_type));
		} else {

			rowCluase += (" IS NULL ");
		}
		sql += rowCluase;
		if (column_id > 0) {
			sql += (" AND " + DBUtils.KEY_COLUMN_ID + " = " + column_id);
		} else {
			sql += (" AND " + DBUtils.KEY_COLUMN_ID + " IS NULL ");
		}

		if (islogin) {

			sql += (" AND (")
					+ (DBUtils.KEY_CUSTOM_TYPE + " = " + ForumType.CUSTOM_TYPE_DEFAULT)
					+ " OR "
					+ (DBUtils.KEY_CUSTOM_TYPE + " = " + ForumType.CUSTOM_TYPE_SELECTED)
					+ " )";
		} else {
			sql += (" AND " + DBUtils.KEY_PREVIEW_TYPE + " = " + ForumType.PREVIEW_TYPE_SYSTEM);

			sql += (" AND " + DBUtils.KEY_CUSTOM_TYPE + " = " + ForumType.CUSTOM_TYPE_DEFAULT);
		}
		String keysql = DBUtils.assembleFuzzysearchString(
				DBUtils.KEY_JSON_CONTENT, args);
		if (keysql != null && keysql.length() > 0)
			sql += (" AND " + keysql);
		Cursor cursor = db.queryWithOrderBindUser(DBUtils.catalogTableName,
				new String[] { "*" }, sql, null, DBUtils.KEY_CAT_ID + " ASC");

		ForumTypeList infoTypeList = new ForumTypeList();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int nameColumn = cursor.getColumnIndex(DBUtils.KEY_JSON_CONTENT);
			String json = cursor.getString(nameColumn);
			if (json == null || json.length() == 0)
				continue;
			JSONObject childjsonObj;
			try {
				childjsonObj = new JSONObject(json);
				ForumType infotype = ForumType.parse(this, childjsonObj, false,
						0);
				int ureadcount = cursor.getInt(cursor
						.getColumnIndex(DBUtils.KEY_UNREAD_COUNT));
				infotype.setNewCount(ureadcount);
				if (infotype != null && infotype.getValidate().OK()) {
					infoTypeList.getInfoTypelist().add(infotype);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw AppException.run(e);

			}
		}
		cursor.close();
		db.close();
		Result res = new Result(1, "OK");
		infoTypeList.setValidate(res);
		return infoTypeList;

	}

	/**
	 * 版块消息列表
	 * 
	 * @param forumid
	 *            : 版块Id，如果为null，则在数据库中搜索全部
	 * @param url
	 *            ：从网络读取数据的URL，
	 * @param offsetId
	 *            ：当前从网络读取数据的开始ID
	 * @param isRefresh
	 *            ：是否刷新，刷新则从前读取，不是刷新，则从后读取
	 * @param rowtype
	 *            ： 数据保存类型
	 * @return
	 * @throws AppException
	 */
	public ItemInfoList getItemInfoListList(String forumid, String url,
			String offsetId, boolean isRefresh, int rowtype)
			throws AppException {
		ItemInfoList list = null;
		String key = "ForumInfoList";

		if (offsetId == null || offsetId.length() == 0) {
			list = getItemInfoListFromDb(this, forumid, rowtype);
			if (list != null && list.getItemInfolist().size() > 0) {
				offsetId = list.getItemInfolist().get(0).getInfoEntitylist()
						.get(0).getInfoId();
				if (list.getItemInfolist().size() > 20) {
					String catid = list.getItemInfolist().get(1)
							.getInfoEntitylist().get(0).getForumId();
					String msgid = list.getItemInfolist().get(1)
							.getInfoEntitylist().get(0).getInfoId();
					deleteItemInfoListFromDb(this, catid, msgid, 1, 0);
				}
			}
		}
		if (isNetworkConnected()) {
			try {
				ItemInfoList newlist = ApiClient.getForumInfoList(this, url,
						offsetId, PAGE_SIZE, isRefresh, rowtype);
				/*
				 * if(list != null && isRefresh){ Notice notice =
				 * list.getNotice(); list.setNotice(null);
				 * list.setCacheKey(key); saveObject(list, key);
				 * list.setNotice(notice); }
				 */
				if (list != null)
					newlist.getItemInfolist().addAll(list.getItemInfolist());
				list = newlist;
			} catch (AppException e) {
				/*
				 * if(list == null) throw e;
				 */
			}
		}/*
		 * else { list = getItemInfoListFromDb(this, forumid, rowtype); }
		 */
		if (list == null) {
			list = new ItemInfoList();
		}
		return list;
	}

	private static final int MAXCOUNT_INFOITEM = 30;

	// 如果offsetId不正确，则从数据库读取最大的ID作为offsetId，
	// 如果数据库最大的offsetId的时间是一天以前，则删除全部数据；如果不是，则保留数据库最新的30条数据，其它的删除；
	/**
	 * 从网络获取数据； //如果offsetId不正确，则从数据库读取最大的ID作为offsetId，
	 * //如果数据库最大的offsetId的时间是一天以前，则删除全部数据；如果不是，则保留数据库最新的30条数据，其它的删除；
	 * 
	 * @param forumid
	 *            ：cat_ID
	 * @param url
	 * @param offsetId
	 * @param isRefresh
	 * @param rowtype
	 * @return
	 * @throws AppException
	 */
	public ItemInfoList getItemInfoListListFromNet(String forumid, String url,
			String offsetId, boolean isRefresh, int rowtype)
			throws AppException {
		ItemInfoList list = null;
		String key = "ForumInfoList";
		MyLogger.i(TAG, "getItemInfoListListFromNet##" + url);

		if (offsetId == null || offsetId.length() == 0) {
			list = getItemInfoListFromDb(this, forumid, rowtype);
			if (list != null && list.getItemInfolist().size() > 0) {
				String timelast = list.getItemInfolist().get(0)
						.getInfoEntitylist().get(0).getReviveTimestamp();
				// 将ms转为标准时间格式
				timelast = StringUtils.timeToString(timelast);
				Date datelast = StringUtils.toDate(timelast);
				Date datenow = new Date(System.currentTimeMillis());

				if (datenow.getTime() - datelast.getTime() < 24 * 60 * 60 * 1000) {
					offsetId = list.getItemInfolist().get(0)
							.getInfoEntitylist().get(0).getInfoId();

					if (list.getItemInfolist().size() > MAXCOUNT_INFOITEM) {
						/*
						 * String catid = list.getItemInfolist().get(29)
						 * .getInfoEntitylist().get(0).getForumId();
						 */
						String msgid = list.getItemInfolist()
								.get(MAXCOUNT_INFOITEM - 1).getInfoEntitylist()
								.get(0).getInfoId();
						deleteItemInfoListFromDb(this, forumid, msgid, 1,
								rowtype);
					}
				} else {
					// deleteItemInfoListFromDb(this, forumid, null, 1,
					// rowtype);
				}
			}
		}

		if (isNetworkConnected()) {
			try {
				ItemInfoList newlist = ApiClient.getForumInfoList(this, url,
						offsetId, PAGE_SIZE, isRefresh, rowtype);

				list = newlist;
			} catch (AppException e) {

				throw e;
			}
		} else {
			list = null;
		}
		return list;
	}

	/**
	 * 从数据库读消息列表
	 * 
	 * @param appContext
	 * @param forumid
	 * @param rowtype
	 * @return ItemInfoList
	 * @throws AppException
	 */
	public ItemInfoList getItemInfoListFromDb(AppContext appContext,
			String catid, int rowtype) throws AppException {
		MyLogger.i(TAG, "getItemInfoListFromDb##" + catid + rowtype);
		DBUtils db = DBUtils.getInstance(this);
		String rowCluase = new String(DBUtils.KEY_ROWDATA_TYPE);
		if (rowtype >= 0) {

			rowCluase += (" = " + String.valueOf(rowtype));
		} else {

			rowCluase += (" IS NULL ");
		}
		String sql = new String();
		if (catid != null)
			sql += (DBUtils.KEY_CAT_ID + "=" + catid);
		if (sql != null && sql.length() > 0)
			sql += (" AND " + rowCluase);
		else
			sql += (rowCluase);

		Cursor cursor = db.queryWithOrderBindUser(DBUtils.infoTableName,
				new String[] { "*" }, sql, null, DBUtils.KEY_MSG_ID + " DESC");

		ItemInfoList list = new ItemInfoList();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int nameColumn = cursor.getColumnIndex(DBUtils.KEY_JSON_CONTENT);
			String json = cursor.getString(nameColumn);
			if (json == null || json.length() == 0)
				continue;
			int timecl = cursor.getColumnIndex(DBUtils.KEY_TIMESTAMP);
			long timemills = cursor.getLong(timecl);
			String timestr = null;
			if (timemills > 0) {
				timestr = StringUtils.timeToString(String.valueOf(timemills));
			}
			JSONObject childjsonObj;
			try {
				childjsonObj = new JSONObject(json);
				{

					ItemInfoEntity info = ItemInfoEntity.parse(appContext,
							childjsonObj, false, rowtype);

					if (info != null && info.getValidate().OK()) {
						List<InfoEntity> listinfo = info.getInfoEntitylist();
						if (listinfo != null && timestr != null) {
							for (int i = 0; i < listinfo.size(); i++) {
								listinfo.get(i).setReviveTimestamp(
										String.valueOf(timemills));
							}
						}
						info.setRecevietime(timemills);
						list.getItemInfolist().add(info);
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				continue;
				// throw AppException.network(e);
			}
		}
		cursor.close();
		db.close();
		Result res = new Result(1, "read in db success");
		list.setValidate(res);
		if (list.getItemInfolist().size() == 0)
			list = null;
		return list;

	}

	/**
	 * 从数据库删除消息列表
	 * 
	 * @return ForumTypeList
	 * @throws AppException
	 * @param cat_id
	 *            :KEY_CAT_ID;// msg_id: KEY_MSG_ID ;if null;删除 KEY_CAT_ID =
	 *            cat_id 的全部数据; // option: 0:删除单条 数据；KEY_CAT_ID = cat_id
	 *            ；KEY_MSG_ID = msg_id；// 1:删除比较老的数据；KEY_CAT_ID = cat_id
	 *            ；KEY_MSG_ID < msg_id；// 2:删除比较新的数据；KEY_CAT_ID = cat_id
	 *            ；KEY_MSG_ID > msg_id；//
	 * 
	 */
	public int deleteItemInfoListFromDb(AppContext appContext, String cat_id,
			String msg_id, int option, int rowtype) throws AppException {
		if (appContext == null)
			return 0;
		DBUtils db = DBUtils.getInstance(this);
		int id = 0;
		String catClause = null;
		String msgCluase = null;
		String rowCluase = null;
		if (cat_id != null) {
			catClause = new String(DBUtils.KEY_CAT_ID);
			catClause += ("=" + cat_id);
		}
		if (msg_id != null) {
			String param = msg_id;
			msgCluase = new String(DBUtils.KEY_MSG_ID);
			if (option <= 0)
				msgCluase += ("=" + param);
			else if (option == 1)
				msgCluase += ("<" + param);
			else if (option == 2)
				msgCluase += (">" + param);
			else
				msgCluase += ("=" + param);
		}
		if (rowtype > 0) {
			rowCluase = new String(DBUtils.KEY_ROWDATA_TYPE);
			rowCluase += (" = " + String.valueOf(rowtype));
		} else {
			rowCluase = new String(DBUtils.KEY_ROWDATA_TYPE);
			rowCluase += (" IS NULL ");
		}
		String whereClause = new String(rowCluase);
		if (catClause != null || msgCluase != null) {
			if (catClause != null)
				whereClause += (" AND " + catClause);
			if (msgCluase != null)
				whereClause += (" AND " + msgCluase);

		}
		id = db.deleteBindUser(DBUtils.infoTableName, whereClause, null);

		db.close();

		return id;
	}

	/**
	 * 从数据库模糊搜索版块列表
	 * 
	 * @return ForumTypeList
	 * @throws AppException
	 */
	public ItemInfoList serachItemInfoListFromDb(String forumid,
			String offsetId, boolean isRefresh, String[] args, int limitnum,
			int rowtype) throws AppException {

		DBUtils db = DBUtils.getInstance(this);
		String sql = new String();
		sql += ("( "
				+ DBUtils.assembleFuzzysearchString(DBUtils.KEY_JSON_CONTENT,
						args) + " )");
		if (offsetId != null && offsetId.length() > 0) {
			if (isRefresh)
				sql += (" AND " + "( " + DBUtils.KEY_MSG_ID + ">" + offsetId + " )");
			else
				sql += (" AND " + "( " + DBUtils.KEY_MSG_ID + "<" + offsetId + " )");
		}
		if (forumid != null && forumid.length() > 0) {
			sql += (" AND " + "( " + DBUtils.KEY_CAT_ID + "=" + forumid + " )");
		}
		String rowCluase = new String(DBUtils.KEY_ROWDATA_TYPE);
		if (rowtype > 0) {
			rowCluase += (" = " + String.valueOf(rowtype));
		} else {
			rowCluase += (" IS NULL ");
		}
		if (rowCluase != null) {
			sql += (" AND " + "( " + rowCluase + " )");
		}
		String limit = String.valueOf(limitnum);
		Cursor cursor = db.queryBindUser(true, DBUtils.infoTableName,
				new String[] { DBUtils.KEY_CAT_ID, DBUtils.KEY_JSON_CONTENT },
				sql, null, null, null, DBUtils.KEY_MSG_ID + " DESC", limit);

		ItemInfoList List = new ItemInfoList();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int nameColumn = cursor.getColumnIndex(DBUtils.KEY_JSON_CONTENT);
			String json = cursor.getString(nameColumn);
			if (json == null || json.length() == 0)
				continue;
			JSONObject childjsonObj;
			try {
				childjsonObj = new JSONObject(json);
				ItemInfoEntity info = ItemInfoEntity.parse(this, childjsonObj,
						false, rowtype);
				if (info != null && info.getValidate().OK()) {
					List.getItemInfolist().add(info);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// throw AppException.network(e);
			}
		}
		cursor.close();
		db.close();
		return List;

	}

	/**
	 * 栏目类型列表
	 * 
	 * @param cat_type
	 *            :版块类型
	 * @return
	 * @throws ApiException
	 */
	public ColumnEntityList getColumnEntityList(int type, boolean bsavetoDb) {
		ColumnEntityList list = null;
		String key = "ForumTypeList";

		Result res = null;
		if (isNetworkConnected()) {
			try {
				String url = null;

				url = URLs.EXTRA_COLUMN;

				list = ApiClient
						.getColumnEntityList(this, url, type, bsavetoDb);

			} catch (AppException e) {
				res = new Result(-2, " AppException ");
			}
		} else {
			res = new Result(-1, this.getResources().getString(
					R.string.app_status_net_disconnected));
		}

		if (list == null)
			list = getColumnEntityListFromDb(type);
		if (res != null) {
			list.setValidate(res);
		}
		return list;
	}

	/**
	 * 从数据库读取号外栏目列表
	 * 
	 * @return ForumTypeList
	 * @throws AppException
	 */
	public ColumnEntityList getColumnEntityListFromDb(int type) {

		DBUtils db = DBUtils.getInstance(this);
		String sql = new String();
		String rowCluase = new String(DBUtils.KEY_ROWDATA_TYPE);
		if (type > 0) {
			rowCluase += (" = " + String.valueOf(type));
		} else {

			rowCluase += (" IS NULL ");
		}
		sql += rowCluase;
		Cursor cursor = db.queryWithOrderBindUser(DBUtils.catalogTableName,
				new String[] { DBUtils.KEY_CAT_ID, DBUtils.KEY_JSON_CONTENT },
				sql, null, DBUtils.KEY_COLUMN_ID + " ASC");

		ColumnEntityList entitylist = new ColumnEntityList();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			int nameColumn = cursor.getColumnIndex(DBUtils.KEY_JSON_CONTENT);
			String json = cursor.getString(nameColumn);
			if (json == null || json.length() == 0)
				continue;
			JSONObject childjsonObj;
			try {
				childjsonObj = new JSONObject(json);
				ColumnEntity entity = ColumnEntity.parse(this, childjsonObj,
						DBUtils.CATALOG_TYPE_EXTRA_COLUMN, false);
				if (entity != null && entity.getValidate().OK()) {
					entitylist.getArraylist().add(entity);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// throw AppException.network(e);
			}
		}
		cursor.close();
		db.close();
		return entitylist;

	}

	public void reportSingleCatalogManagerByThread(final AppContext appContext,
			final Handler handler, final String catalog_id,
			final boolean bSelected) {

		Thread t = new Thread() {
			public void run() {
				int state = 0;
				if (!bSelected)
					state = 1;
				Result result = ApiClient.reportSigleSelectedStateCatalog(
						appContext, catalog_id, state);
				Message msg = new Message();
				if (result.OK()) {
					msg.what = 1;
					msg.obj = "sucess";
				} else if (result.getErrorCode() == -1) {
					msg.what = -1;
					msg.obj = "数据异常";
				} else {
					msg.what = -1;
					msg.obj = result.getErrorMessage();
				}
				if (handler != null)
					handler.sendMessage(msg);
			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	/**
	 * 从网络获取简历列表
	 * 
	 * @param appContext
	 * @param bsavetoDb
	 * @return
	 * @throws AppException
	 */
	public ResumeSimpleEntityList getResumeSimpleEntityListFromNet(
			AppContext appContext, boolean bsavetoDb) throws AppException {

		String Url = URLs.RESUME_LIST;

		JSONObject request = new JSONObject();
		try {
			request.putOpt("count", 100);

			int flag = 1;
			/*
			 * if (!isRefresh) flag = 0;
			 */
			request.putOpt("direction", flag);

			request.putOpt("offsetid", 0);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}
		String requestinfo = request.toString();

		Map<String, Object> params = getHttpPostParams(appContext, requestinfo);
		try {
			return ResumeSimpleEntityList.parse(appContext,
					ApiClient._post(appContext, Url, params, null), bsavetoDb);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}

	/**
	 * 从数据库读取简历列表
	 * 
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public ResumeSimpleEntityList getSimpleResumeListFromDb(
			AppContext appContext) throws AppException {
		Result res = null;
		DBUtils db = DBUtils.getInstance(appContext);

		/*
		 * String sql = new String(); sql = DBUtils.KEY_SIMPLERESUME_RESUME_ID +
		 * " = " + resumeid;
		 */

		Cursor cursor = db.queryWithOrderBindUser(
				DBUtils.simpleResumeTableName, new String[] { "*" }, null,
				null, DBUtils.KEY_SIMPLERESUME_MODIFY_TIME + " DESC");

		ResumeSimpleEntityList list = new ResumeSimpleEntityList();
		try {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				int jsonColumn = cursor
						.getColumnIndex(DBUtils.KEY_SIMPLERESUME_JSON_CONTENT);
				String json = cursor.getString(jsonColumn);
				if (json == null || json.length() == 0)
					continue;

				JSONObject childjsonObj;

				childjsonObj = new JSONObject(json);
				{

					ResumeSimpleEntity entity = ResumeSimpleEntity.parse(
							appContext, childjsonObj, false);
					int index = cursor
							.getColumnIndex(DBUtils.KEY_SIMPLERESUME_SELECTED_STATUS);
					int status = cursor.getInt(index);
					entity.setDefaultAuthenticat(status);
					if (entity != null && entity.getValidate().OK()) {
						list.getEntitylist().add(entity);
					}
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw AppException.io(e);
		} finally {
			cursor.close();
			db.close();
		}
		res = new Result(1, "ok");
		list.setValidate(res);
		return list;

	}

	/**
	 * 获取默认个人简历信息
	 * 
	 * @param appContext
	 * @param rsel
	 * @return
	 * @throws AppException
	 */
	public ResumeSimpleEntity getDefaultResumeInfo(ResumeSimpleEntityList rsel)
			throws AppException {
		ResumeSimpleEntity rse = new ResumeSimpleEntity();

		if (null != rsel) {
			if (null != rsel.getEntitylist()) {
				if (rsel.getEntitylist().size() > 0) {
					for (ResumeSimpleEntity resTemp : rsel.getEntitylist()) {
						rse = resTemp;
						if (HeadhunterPublic.RESUME_DEFAULTESUME == resTemp
								.getDefaultAuthenticat()) {
							break;
						}
					}
				}
			}
		}
		return rse;
	}

	/**
	 * 从数据库中获取默认个人简历信息
	 * 
	 * @param appContext
	 * @return ResumeSimpleEntity :返回简历数据entity，如果没有则为null；
	 * @throws AppException
	 */
	public ResumeSimpleEntity getDefaultResumeSimpleInfoFromDb(
			AppContext appContext) throws AppException {

		ResumeSimpleEntity rse = null;
		DBUtils db = DBUtils.getInstance(appContext);

		String sql = new String();
		sql = DBUtils.KEY_SIMPLERESUME_SELECTED_STATUS + " = "
				+ ResumeSimpleEntity.RESUME_DEFAULT_SELECTED;
		Cursor cursor = db.queryWithOrderBindUser(
				DBUtils.simpleResumeTableName, new String[] { "*" }, sql, null,
				null);

		try {

			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				int jsonColumn = cursor
						.getColumnIndex(DBUtils.KEY_SIMPLERESUME_JSON_CONTENT);
				String json = cursor.getString(jsonColumn);
				if (json != null && json.length() > 0) {
					JSONObject childjsonObj = new JSONObject(json);
					ResumeSimpleEntity entity = ResumeSimpleEntity.parse(
							appContext, childjsonObj, false);
					int index = cursor
							.getColumnIndex(DBUtils.KEY_SIMPLERESUME_SELECTED_STATUS);
					int status = cursor.getInt(index);
					entity.setDefaultAuthenticat(status);
					rse = entity;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw AppException.io(e);
		} finally {
			cursor.close();
			db.close();
		}
		return rse;
	}

	/**
	 * 通过简历id获取简历信息
	 * 
	 * @param resumeId
	 * @return
	 * @throws AppException
	 */
	public ResumeSimpleEntity getResumeInfoByIdFromBD(String resumeId)
			throws AppException {

		ResumeSimpleEntity rse = null;
		DBUtils db = DBUtils.getInstance(this);

		String sql = "select * from " + DBUtils.simpleResumeTableName
				+ " where " + DBUtils.KEY_RESUME_RESUME_ID + "=" + resumeId;
		Cursor cursor = db.rawQueryBindUser(sql, null);

		try {
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				int jsonColumn = cursor
						.getColumnIndex(DBUtils.KEY_SIMPLERESUME_JSON_CONTENT);
				String json = cursor.getString(jsonColumn);
				if (json != null && json.length() > 0) {
					JSONObject childjsonObj = new JSONObject(json);
					ResumeSimpleEntity entity = ResumeSimpleEntity.parse(this,
							childjsonObj, false);
					rse = entity;
				}
			}
		} catch (Exception e) {
			throw AppException.io(e);
		} finally {
			cursor.close();
			db.close();
		}
		return rse;
	}

	/**
	 * 删除简单简历数据库中指定Id的简历，
	 * 
	 * @param appContext
	 * @param resume_id
	 * @return
	 * @throws AppException
	 */
	public static int deleteSimpleResumeInDb(AppContext appContext,
			String resume_id) throws AppException {
		if (appContext == null)
			return 0;
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_SIMPLERESUME_RESUME_ID + " = " + resume_id;
		int id = db.deleteBindUser(DBUtils.simpleResumeTableName, sql, null);

		db.close();

		return id;
	}

	/**
	 * 更新简单简历数据库中简历的默认状态
	 * 
	 * @param appContext
	 * @param resume_id
	 * @param bdefault
	 * @return
	 * @throws AppException
	 */
	public static boolean updateDefaultSimpleResumeInDb(AppContext appContext,
			String resume_id, boolean bdefault) throws AppException {
		if (appContext == null)
			return false;
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_SIMPLERESUME_RESUME_ID + " = " + resume_id;
		ContentValues values = new ContentValues();
		if (bdefault)
			values.put(DBUtils.KEY_SIMPLERESUME_SELECTED_STATUS,
					ResumeSimpleEntity.RESUME_DEFAULT_SELECTED);
		else
			values.put(DBUtils.KEY_SIMPLERESUME_SELECTED_STATUS,
					ResumeSimpleEntity.RESUME_DEFAULT_UNSELECTED);
		boolean bact = db.updateBindUser(DBUtils.simpleResumeTableName, sql,
				values);

		db.close();

		return bact;
	}

	/**
	 * 
	 * @param appContext
	 * @param insidersAndCompanys
	 * @param type
	 *            行业圈内人 or 名企
	 */
	public static void refreashInsidersAndCompanyInDB(AppContext appContext,
			List<InsidersAndCompany> insidersAndCompanys, int type) {
		DBUtils db = DBUtils.getInstance(appContext);
		db.rawQuery("delete from " + DBUtils.INSIDERSANDCOMPANYTABLENAME
				+ " where type= " + type, null);
		// db.delete(DBUtils.INSIDERSANDCOMPANYTABLENAME, DBUtils.ICTYPE, new
		// String[]{type + ""});
		int size = insidersAndCompanys.size();
		for (int i = 0; i < size; i++) {
			InsidersAndCompany insidersAndCompany = insidersAndCompanys.get(i);
			ContentValues values = new ContentValues();
			values.put(DBUtils.ICID, insidersAndCompany.getId());
			values.put(DBUtils.ICNAME, insidersAndCompany.getName());
			values.put(DBUtils.ICTITLE, insidersAndCompany.getTitle());
			values.put(DBUtils.ICPICTURE, insidersAndCompany.getPicture());
			values.put(DBUtils.ICTASKCOUNT, insidersAndCompany.getTask_count());
			values.put(DBUtils.ICAUTHENTICATE,
					insidersAndCompany.getAuthenticate());
			values.put(DBUtils.ICMODIFIED, insidersAndCompany.getModified());
			values.put(DBUtils.ICATTENTION_COUNT,
					insidersAndCompany.getAttention_count());
			values.put(DBUtils.ICTAGS, ObjectUtils
					.getJsonStringFromObject(insidersAndCompany.getTags()));
			values.put(DBUtils.ICTYPE, type);
			values.put(DBUtils.ICMTIME, System.currentTimeMillis());

			db.save(DBUtils.INSIDERSANDCOMPANYTABLENAME, values);
		}
		db.close();
	}

	public static void clearInsidersAndCompanyInDB(AppContext appContext,
			int type) {
		DBUtils db = DBUtils.getInstance(appContext);
		db.rawQuery("delete from " + DBUtils.INSIDERSANDCOMPANYTABLENAME
				+ " where type= " + type, null);
		// db.delete(DBUtils.INSIDERSANDCOMPANYTABLENAME, DBUtils.ICTYPE, new
		// String[]{type + ""});
		db.close();
	}

	public static List<InsidersAndCompany> getInsidersAndCompanyInDB(
			AppContext appContext, int type) {
		List<InsidersAndCompany> insidersAndCompanys = new ArrayList<InsidersAndCompany>();
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = "select * from " + DBUtils.INSIDERSANDCOMPANYTABLENAME
				+ " where " + DBUtils.ICTYPE + " = " + type;
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		if (cursor != null && cursor.getCount() > 0) {
			do {
				String id = cursor.getString(cursor
						.getColumnIndex(DBUtils.ICID));
				String name = cursor.getString(cursor
						.getColumnIndex(DBUtils.ICNAME));
				String title = cursor.getString(cursor
						.getColumnIndex(DBUtils.ICTITLE));
				String picture = cursor.getString(cursor
						.getColumnIndex(DBUtils.ICPICTURE));
				String task_count = cursor.getString(cursor
						.getColumnIndex(DBUtils.ICTASKCOUNT));
				String authenticate = cursor.getString(cursor
						.getColumnIndex(DBUtils.ICAUTHENTICATE));
				String[] tags = ObjectUtils.getStringArrayFormJsonString(cursor
						.getString(cursor.getColumnIndex(DBUtils.ICTAGS)));
				String modified = cursor.getString(cursor
						.getColumnIndex(DBUtils.ICMODIFIED));
				String attention_count = cursor.getString(cursor
						.getColumnIndex(DBUtils.ICATTENTION_COUNT));

				InsidersAndCompany insidersAndCompany = new InsidersAndCompany();
				insidersAndCompany.setId(id);
				insidersAndCompany.setName(name);
				insidersAndCompany.setTitle(title);
				insidersAndCompany.setPicture(picture);
				insidersAndCompany.setTask_count(task_count);
				insidersAndCompany.setAuthenticate(authenticate);
				insidersAndCompany.setTags(tags);
				insidersAndCompany.setModified(modified);
				insidersAndCompany.setAttention_count(attention_count);
				insidersAndCompanys.add(insidersAndCompany);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return insidersAndCompanys;
	}

	/**
	 * 从全局数据中获取审核状态的名称
	 * 
	 * @param strId
	 * @return
	 * @throws AppException
	 */
	public String getVerifyStatusName(String strId) throws AppException {
		return GlobalDataTable.getVerifyStatusName(this, strId);
	}

	/**
	 * 从全局数据中获取指定id和类型的的指定信息
	 * 
	 * @param strId
	 * @param nType
	 * @param strColumnName
	 * @return
	 * @throws AppException
	 */
	public String getGlobalDataSpecifyInfo(String strId, int nType,
			String strColumnName) throws AppException {
		return GlobalDataTable.getGlobalDataSpecifyInfo(this, strId, nType,
				strColumnName);
	}

	/**
	 * 从全局数据中获取指定id和指定类型的父类信息
	 * 
	 * @param strId
	 * @param nType
	 * @param strColumnName
	 * @return
	 * @throws AppException
	 */
	public String getParentId(String strId, int nType) throws AppException {
		return GlobalDataTable.getParentId(this, strId, nType);
	}

	/**
	 * 从全局数据中获取指定id和指定类型的父类信息(可指定"指定id"是否有子类)
	 * 
	 * @param strId
	 * @param nType
	 * @param bHavingSubClass
	 * @return
	 * @throws AppException
	 */
	public String getParentId(String strId, int nType, boolean bHavingSubClass)
			throws AppException {
		return GlobalDataTable.getParentId(this, strId, nType, bHavingSubClass);
	}

	/**
	 * 保存搜索历史
	 * 
	 * @param rfc
	 * @return
	 * @throws AppException
	 */
	public boolean saveHistorySearch(RewardFilterCondition rfc)
			throws AppException {
		return RewardFilterCondition.saveHistorySearch(this, rfc);
	}

	/**
	 * 获取搜索历史
	 * 
	 * @return
	 * @throws AppException
	 */
	public List<RewardFilterCondition> getHistorySearch() throws AppException {
		return RewardFilterCondition.getHistorySearch(this);
	}

	/**
	 * 获取最新一条搜索历史
	 * 
	 * @return
	 * @throws AppException
	 */
	public RewardFilterCondition getLastHistorySearch() throws AppException {
		return RewardFilterCondition.getLastHistorySearch(this);
	}

	public VersionData getCurrentVersion() {
		PackageManager manager = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			VersionData versionData = new VersionData();
			versionData.setVersion(info.versionName);
			versionData.setVersionCode(info.versionCode);
			return versionData;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 退出应用程序 对话框
	@SuppressLint("NewApi")
	public void showQuitAlertDialog(Activity activity) {
		
		Dialog dialog = MethodsCompat.getAlertDialogBuilder(activity,AlertDialog.THEME_HOLO_LIGHT)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.dialog_appquit_title)
				.setMessage(R.string.dialog_appquit_msg)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								AppManager.getAppManager().finishAllActivity();
								System.exit(0);
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

	/**
	 * @param appcontext
	 *            ：AppContext实例
	 * @param postParams
	 *            :提交业务参数的josn字符串
	 * @return Map<String, Object> : 返回 ApiClient中_post方法中的参数 Map<String,
	 *         Object> params
	 */
	public static Map<String, Object> getHttpPostParams(AppContext appcontext,
			String postParams) {
		if (appcontext == null)
			return null;

		Map<String, Object> params = new HashMap<String, Object>();

		if (appcontext.isLogin()) {
			params.put("access_token", appcontext.getAccessToken());
		} else {
			params.put("access_token", "uuid_" + appcontext.getAppId());
		}
		DisplayMetrics dm = getPhoneDisplayMetrics(appcontext);
		if (dm != null) {
			JSONArray jsonarray = new JSONArray();
			jsonarray.put(dm.widthPixels);
			jsonarray.put(dm.heightPixels);
			try {
				jsonarray.put((double) (dm.density));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String sizestr = jsonarray.toString();
			params.put("size", sizestr);
		} else {
			params.put("size", "");
		}
		if (postParams == null)
			params.put("params", "");
		else
			params.put("params", postParams);
		return params;
	}
	
	public final static String formatUdidURL(AppContext appcontext,String path) {
		if(appcontext == null)
			return path;
		String udid = appcontext.getAppId();

		if (path.indexOf("?", 0) == -1) {
			path += ("?udid="+udid);
		} else {
			path += "&udid="+udid;
		}
	
		return path;
	}
	/**
	 * 用于获取设备屏幕参数
	 * 
	 * @param appcontext
	 *            : AppContext 实例
	 * @return DisplayMetrics : 返回设备屏幕参数(长，宽，密度)
	 */
	public static DisplayMetrics getPhoneDisplayMetrics(AppContext appcontext) {
		if (appcontext == null)
			return null;
		WindowManager WM = (WindowManager) appcontext
				.getSystemService("window");
		DisplayMetrics dm = new DisplayMetrics();
		WM.getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	// 获取社保公积金比例
	public static TaxRateInfo getTaxRateInfo(Context context) {
		String taxRateStr = DMSharedPreferencesUtil.getSharePreStr(context,
				DMSharedPreferencesUtil.DM_APP_DB,
				DMSharedPreferencesUtil.taxRate);
		TaxRateInfo taxRateInfo = new TaxRateInfo();
		if (!TextUtils.isEmpty(taxRateStr)) {
			taxRateInfo = (TaxRateInfo) ObjectUtils.getObjectFromJsonString(
					taxRateStr, TaxRateInfo.class);
		}
		return taxRateInfo;
	}

	/**
	 * 清除缓存的文件
	 * 
	 * @param context
	 */
	public void clearFileCache(Context context) {
		Thread thread = new Thread(new FileClearRunnable(context));

		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(thread);

	}

	private void deleteOldFile(File root) {
		if (root == null || !root.exists())
			return;
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			for (File file : files) {
				deleteOldFile(file);
			}
		} else if (!root.isHidden() && root.isFile()) {
			Date today = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			cal.add(Calendar.DATE, -7);
			Date overday = cal.getTime();
			Date fileDate = new Date(root.lastModified());
			if (fileDate.before(overday)) {
				//Log.i("deleteOldFile", "file name:"+root.getName());
				root.delete();
			}
		}
	}

	public class FileClearRunnable implements Runnable {

		private Context context;

		public FileClearRunnable(Context context) {
			super();
			// TODO Auto-generated constructor stub
			this.context = context;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			FileUtils fu = new FileUtils();
			File dir;

			dir = new File(AppConfig.DEFAULT_SAVE_IMAGE_ABSOLUTE_PATH);
			deleteOldFile(dir);

			dir = context.getFilesDir();
			deleteOldFile(dir);

		}
	}

	/**
	 * 获取我的账号相关信息
	 * 
	 * @return 我的账号相关信息
	 * @throws AppException
	 */
	public MyAccontInfoDataEntity getMyAccountInfoData() throws AppException {
		return ApiClient.getMyAccountInfoData(this);
	}

	/**
	 * 获取交易记录数据列表
	 * 
	 * @param rcl
	 * @return
	 * @throws AppException
	 */
	public TransactionRecordListDataEntity getTransactionRecordList(
			reqConsumelog rcl) throws AppException {

		return ApiClient.getTransactionRecordList(this, rcl);
	}

	/**
	 * 连接服务器检查安全密码是否正确
	 * 
	 * @param strSecurePwd
	 *            安全密码
	 * @return
	 * @throws AppException
	 */
	public Result checkSecurePwd(String strSecurePwd) throws AppException {

		return ApiClient.checkSecurePwd(this, strSecurePwd);
	}

	/**
	 * 绑定支付宝账号
	 * 
	 * @param strSecurePwd
	 * @return
	 * @throws AppException
	 */
	public Result bindingAlipayAccount(String strAccout) throws AppException {

		return ApiClient.bindingAlipayAccount(this, strAccout);
	}

	/**
	 * 获取支付订单号
	 * 
	 * @param strTaskId
	 * @return
	 * @throws AppException
	 */
	public Result getPaymentOrderNo(String strType, String strId)
			throws AppException {

		return ApiClient.getPaymentOrderNo(this, strType, strId);
	}

	/**
	 * 获取公司收藏状态
	 * 
	 * @param strId
	 * @return
	 * @throws AppException
	 */
	public Result getCompanyCollectionStatus(String strId) throws AppException {

		return ApiClient.getCompanyCollectionStatus(this, strId);
	}
	
	/**
	 * 获取任务收藏状态
	 * @param strId
	 * @return
	 * @throws AppException
	 */
	public TaskStatusInfoEntity getTaskCollectionStatus(reqTaskId taskid) throws AppException {

		return ApiClient.getTaskCollectionStatus(this, taskid);
	}

	/**
	 * 获取指定类型，指定级数，指定id的数据
	 * 
	 * @param nType
	 * @param nLevel
	 * @param strId
	 * @return
	 * @throws AppException
	 */
	public GlobalDataTable getGlobalDataTable(int nType, int nLevel,
			String strId) throws AppException {
		return GlobalDataTable.getGlobalDataTable(this, nType, nLevel, strId);
	}

	/**
	 * 提交(修改)个人资料
	 * 
	 * @param mapp
	 * @return
	 * @throws AppException
	 */
	public Result submitPersonalProfile(MyAccountPersonalProfile mapp)
			throws AppException {

		return ApiClient.submitPersonalProfile(this, mapp);
	}
	
	/**判断是否原版应用
	 * @return true：正版；false：盗版
	 */
	public boolean checkIsOriginalApp(){
		String packagename = this.getPackageName();
		if(packagename.equalsIgnoreCase("com.qianniu.zhaopin")){
			return true;
		}else{
			return false;
		}
	}
}

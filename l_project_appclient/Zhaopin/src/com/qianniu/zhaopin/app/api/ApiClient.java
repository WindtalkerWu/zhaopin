package com.qianniu.zhaopin.app.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.bean.AdZoneList;
import com.qianniu.zhaopin.app.bean.ColumnEntityList;
import com.qianniu.zhaopin.app.bean.CommentSubmitInfo;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.ForumTypeList;
import com.qianniu.zhaopin.app.bean.GlobalDataEntity;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.ItemInfoList;
import com.qianniu.zhaopin.app.bean.ModifyPwdInfo;
import com.qianniu.zhaopin.app.bean.MyAccountPersonalProfile;
import com.qianniu.zhaopin.app.bean.RSSInfo;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.RequestInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.RewardInfo;
import com.qianniu.zhaopin.app.bean.RewardListDataEntity;
import com.qianniu.zhaopin.app.bean.TaskStatusInfoEntity;
import com.qianniu.zhaopin.app.bean.TransactionRecordListDataEntity;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.bean.User;
import com.qianniu.zhaopin.app.bean.MyAccontInfoDataEntity;
import com.qianniu.zhaopin.app.bean.reqConsumelog;
import com.qianniu.zhaopin.app.bean.reqMyApply;
import com.qianniu.zhaopin.app.bean.reqQuickRecommendData;
import com.qianniu.zhaopin.app.bean.reqRewardList;
import com.qianniu.zhaopin.app.bean.reqRewardListBetwen;
import com.qianniu.zhaopin.app.bean.reqTaskId;
import com.qianniu.zhaopin.app.common.FileUtils;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ObjectUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * API客户端接口：用于访问网络数据
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {
	private static final String TAG = "ApiClient";

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";

	// private final static int TIMEOUT_CONNECTION = 10000;
	// private final static int TIMEOUT_SOCKET = 10000;
	// private final static int RETRY_TIME = 2;

	public static ExecutorService pool = Executors.newFixedThreadPool(5);

	private static String appCookie;
	private static String appUserAgent;

	public static void cleanCookie() {
		appCookie = "";
	}

	private static String getCookie(AppContext appContext) {
		if (appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}

	private static String getUserAgent(AppContext appContext) {
		if (appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("Android");
			/*
			 * ua.append('/' + appContext.getPackageInfo().versionName + '_' +
			 * appContext.getPackageInfo().versionCode);// App版本
			 */
			//ua.append("/Android");// 手机系统平台
			ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
			/*
			 * ua.append("/" + android.os.Build.MODEL); // 手机型号 ua.append("/" +
			 * appContext.getAppId());// 客户端唯一标识
			 */
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}

	private static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		// 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(ConfigOptions.getHttpTimeoutConnection());
		// 设置 读数据超时时间
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(ConfigOptions.getHttpTimeoutSocket());
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}

	private static GetMethod getHttpGet(String url, String cookie,
			String userAgent) {
	
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(
				ConfigOptions.getHttpTimeoutConnection());
		httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}

	private static PostMethod getHttpPost(String url, String cookie,
			String userAgent) {
		
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(ConfigOptions.getHttpTimeoutSocket());
		httpPost.setRequestHeader("Host", URLs.HOST);
		httpPost.setRequestHeader("Connection", "Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}

	private static String _MakeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			// 不做URLEncoder处理
			// url.append(URLEncoder.encode(String.valueOf(params.get(name)),
			// UTF_8));
		}

		return url.toString().replace("?&", "?");
	}

	/**
	 * get请求URL
	 * 
	 * @param url
	 * @throws AppException
	 */
	private static InputStream http_get(AppContext appContext, String url)
			throws AppException {
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);

		HttpClient httpClient = null;
		GetMethod httpGet = null;
		url = appContext.formatUdidURL(appContext, url);
		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, cookie, userAgent);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				responseBody = httpGet.getResponseBodyAsString();
				break;
			} catch (HttpException e) {
				time++;
				if (time < ConfigOptions.getHttpRetryTimes()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < ConfigOptions.getHttpRetryTimes()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < ConfigOptions.getHttpRetryTimes());

		responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
		if (responseBody.contains("result")
				&& responseBody.contains("errorCode")
				&& appContext.containsProperty("user.uid")) {
			try {
				Result res = Result.parse(new ByteArrayInputStream(responseBody
						.getBytes()));
				if (res.getErrorCode() == 0) {
					appContext.Logout();
					appContext.getUnLoginHandler().sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ByteArrayInputStream(responseBody.getBytes());
	}

	/**
	 * 公用post方法
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	public static InputStream _post(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException {
		MyLogger.getLogger(TAG).i("post_url==> " + url + ",params = " + params);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		url = appContext.formatUdidURL(appContext, url);
		HttpClient httpClient = null;
		PostMethod httpPost = null;

		// post表单参数处理
		int length = (params == null ? 0 : params.size())
				+ (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
		if (params != null)
			for (String name : params.keySet()) {
				parts[i++] = new StringPart(name, String.valueOf(params
						.get(name)), UTF_8);
			}
		if (files != null)
			for (String file : files.keySet()) {
				try {
					parts[i++] = new FilePart(file, files.get(file));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

		String responseBody = "";
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, null, userAgent);// getHttpPost(url,
				// cookie, userAgent);
				httpPost.setRequestEntity(new MultipartRequestEntity(parts,
						httpPost.getParams()));
				int statusCode = httpClient.executeMethod(httpPost);
				if (statusCode != HttpStatus.SC_OK) {
					MyLogger.e(TAG, "_post##statusCode = " + statusCode);
					throw AppException.http(statusCode);
				} else if (statusCode == HttpStatus.SC_OK) {
					Cookie[] cookies = httpClient.getState().getCookies();
					String tmpcookies = "";
					for (Cookie ck : cookies) {
						tmpcookies += ck.toString() + ";";
					}
					// 保存cookie
					if (appContext != null && tmpcookies != "") {
						appContext.setProperty("cookie", tmpcookies);
						appCookie = tmpcookies;
					}
				}
				responseBody = httpPost.getResponseBodyAsString();
				break;
			} catch (HttpException e) {
				time++;
				if (time < ConfigOptions.getHttpRetryTimes()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < ConfigOptions.getHttpRetryTimes()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} catch (Exception e) {
				time++;
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		} while (time < ConfigOptions.getHttpRetryTimes());

		responseBody = responseBody.replaceAll("\\p{Cntrl}", "");
		if (responseBody.contains("result")
				&& responseBody.contains("errorCode")
				&& appContext.containsProperty("user.uid")) {
			try {
				Result res = Result.parse(new ByteArrayInputStream(responseBody
						.getBytes()));
				if (res.getErrorCode() == 0) {
					appContext.Logout();
					appContext.getUnLoginHandler().sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ByteArrayInputStream(responseBody.getBytes());
	}

	public static String _postReturnStr(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException, IOException {
		InputStream inputStream = _post(appContext, url, params, files);
		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}
		return builder.toString();
	}

	private static String _getReturnStr(AppContext appContext, String url)
			throws AppException, IOException {
		InputStream inputStream = http_get(appContext, url);
		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}
		return builder.toString();
	}

	/**
	 * post请求URL
	 * 
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 * @throws IOException
	 * @throws
	 */
	public static Result http_post(AppContext appContext, String url,
			Map<String, Object> params, Map<String, File> files)
			throws AppException, IOException {
		return Result.parse(_post(appContext, url, params, files));
	}

	/**
	 * 登录， 自动处理cookie
	 * 
	 * @param url
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User login(AppContext appContext, ReqUserInfo req)
			throws AppException {
		/*
		 * params.put("username", username); params.put("pwd", pwd);
		 * params.put("keep_login", 1);
		 */
		JSONObject login = new JSONObject();
		try {
			login.putOpt("user_name", req.getUserName());
			login.putOpt("user_password", req.getUserPassword());

			login.putOpt("login_type", req.getLoginType());
			login.putOpt("thirdpart_id", req.getThirdPartId());
			login.putOpt("thirdpart_token", req.getThirdPartToken());
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String logininfo = login.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				logininfo);
		String loginurl = URLs.LOGIN_VALIDATE_HTTP;

		try {
			return User.parse(_post(appContext, loginurl, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 注册， 自动处理cookie
	 * 
	 * @param url
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User register(AppContext appContext, String strUserName,
			String strUserPwd) throws AppException {

		JSONObject login = new JSONObject();
		try {
			login.putOpt("user_name", strUserName);
			login.putOpt("user_password", strUserPwd);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String logininfo = login.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				logininfo);
		String registerurl = URLs.REGISTER_VALIDATE_HTTP;

		try {
			return User.parse(_post(appContext, registerurl, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取我的悬赏列表
	 * 
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static Result getMyReward(AppContext appContext) throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				null);
		String url = URLs.MY_REWARD_LIST_HTTP;

		try {
			Result result = Result.parse(_post(appContext, url, params, null));
			return result;
			// if (result.getErrorCode() == 1) {
			// return com.alibaba.fastjson.JSONArray
			// .parseArray(result.getJsonStr(), RewardListData.class);
			// return (MyRewardListInfo) ObjectUtils
			// .getObjectFromJsonString(result.getJsonStr(),
			// MyRewardListInfo.class);
			// }
			// String result = _postReturnStr(appContext, url, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
		// return null;
	}

	/**
	 * 改变我的悬赏状态
	 * 
	 * @param appContext
	 * @param taskId
	 * @param status
	 * @return
	 * @throws AppException
	 */
	public static boolean changeMyRewardStatus(AppContext appContext,
			String taskId, int status) throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}

		JSONObject josnObject = new JSONObject();
		try {
			josnObject.putOpt("task_id", taskId);
			josnObject.putOpt("status", status);
		} catch (org.json.JSONException e) {
			throw AppException.json(e);
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				josnObject.toString());
		String url = URLs.MY_REWARD_STATUS_LIST_HTTP;

		try {
			Result result = http_post(appContext, url, params, null);
			if (result.getErrorCode() == 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 提交悬赏
	 * 
	 * @param appContext
	 * @param rewardAddInfo
	 * @return
	 * @throws AppException
	 */
	public static Result saveReward(AppContext appContext,
			RewardInfo rewardAddInfo) throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}
		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				ObjectUtils.getJsonStringFromObject(rewardAddInfo));
		String url = URLs.SAVE_REWARD_HTTP;

		try {
			return http_post(appContext, url, params, null);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取悬赏更多信息
	 * 
	 * @param appContext
	 * @param id
	 * @return
	 * @throws AppException
	 */
	public static Result getMoreRewardInfo(AppContext appContext, String id)
			throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}

		String url = URLs.GET_DETIAL_REWARD_HTTP + "/" + id + "/" + "json";

		try {
			Result result = Result.parse(http_get(appContext, url));
			// return _getReturnStr(appContext, url);
			return result;
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 找回用户密码(手机)
	 * 
	 * @param url
	 * @param username
	 * @param pwd
	 * @param verify
	 *            验证码
	 * @return
	 * @throws AppException
	 */
	public static User findpasswordmobile(AppContext appContext,
			String strUserName, String strUserPwd, String strVerify)
			throws AppException {

		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("user_name", strUserName);
			pd.putOpt("type", 1);
			// pd.putOpt("user_password", strUserPwd);
			// pd.putOpt("", strVerify);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String pdinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				pdinfo);
		String registerurl = URLs.FIND_PASSWORD_MOBILE_HTTP;

		try {
			return User.parse(_post(appContext, registerurl, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 找回用户密码(邮箱)
	 * 
	 * @param url
	 * @param username
	 * @return
	 * @throws AppException
	 */
	public static User findpasswordmail(AppContext appContext,
			String strUserName) throws AppException {

		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("user_name", strUserName);
			pd.putOpt("type", 0);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String pdinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				pdinfo);
		String registerurl = URLs.FIND_PASSWORD_MAIL_HTTP;

		try {
			return User.findpassparse(_post(appContext, registerurl, params,
					null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 找回用户密码
	 * 
	 * @param url
	 * @param username
	 * @return
	 * @throws AppException
	 */
	public static User findpassword(AppContext appContext, String strUserName,
			int nType) throws AppException {

		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("user_name", strUserName);
			pd.putOpt("type", nType);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String pdinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				pdinfo);
		String registerurl = URLs.FIND_PASSWORD_HTTP;

		try {
			return User.findpassparse(_post(appContext, registerurl, params,
					null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取验证码
	 * 
	 * @param url
	 * @param username
	 * @return
	 * @throws AppException
	 */
	public static User getVerify(AppContext appContext, String strUserName)
			throws AppException {
		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("user_name", strUserName);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String pdinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				pdinfo);
		String registerurl = URLs.GET_VERIFY_HTTP;

		try {
			return User.parse(_post(appContext, registerurl, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		MyLogger.i(TAG, "getNetBitmap##" + url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		
		if (url == null)
			return null;
		int time = 0;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				InputStream inStream = httpGet.getResponseBodyAsStream();
			    final BitmapFactory.Options options = new BitmapFactory.Options();
			    // 当inJustDecodeBounds设为true时,不会加载图片仅获取图片尺寸信息
			    options.inJustDecodeBounds = true;
			    // 此时仅会将图片信息会保存至options对象内,decode方法不会返回bitmap对象
			    BitmapFactory.decodeStream(inStream, null, options);
			    inStream.close();
			    
				int BstatusCode = httpClient.executeMethod(httpGet);
				if (BstatusCode != HttpStatus.SC_OK) {
					throw AppException.http(BstatusCode);
				}
				InputStream BinStream = httpGet.getResponseBodyAsStream();
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inPreferredConfig = Bitmap.Config.RGB_565;
				opt.inPurgeable = true;
				opt.inInputShareable = true;
				opt.inJustDecodeBounds = false;
				opt.inSampleSize = ImageUtils.calculateInSampleSize(options, 480, 800);
/*				if (totalSize > 250 * 1024 * 1) {
					int zoomRate = (int) (totalSize / (250 * 1024));
					// zommRate缩放比，根据情况自行设定，如果为2则缩放为原来的1/2，如果为1不缩放
					if (zoomRate <= 0)
						zoomRate = 1;
					opt.inSampleSize = zoomRate;

				}*/
				try{
					Log.e(TAG,"url ="+url);
				bitmap = BitmapFactory.decodeStream(BinStream, null, opt );
				}catch(Exception e){
					Log.e(TAG,url+"/"+ e.toString());
					bitmap = null;
				}
				BinStream.close();
				break;
			} catch (HttpException e) {
				time++;
				if (time < ConfigOptions.getHttpRetryTimes()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < ConfigOptions.getHttpRetryTimes()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < ConfigOptions.getHttpRetryTimes());
		return bitmap;
	}
	/**
	 * 获取网络图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmapAndSave(Context context,String url) throws AppException {
		MyLogger.i(TAG, "getNetBitmap##" + url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		url = "http://www.1000new.com//img/tmpupload/2014/07/31/53d9f3b62649e.small.png";
		if (url == null)
			return null;
		int time = 0;
		boolean bsave = false;
		do {
			try {
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				InputStream inStream = httpGet.getResponseBodyAsStream();
				bsave =ImageUtils.saveImage(context, FileUtils.getFileName(url), inStream);
				inStream.close();
				break;
			} catch (HttpException e) {
				time++;
				if (time < ConfigOptions.getHttpRetryTimes()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if (time < ConfigOptions.getHttpRetryTimes()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < ConfigOptions.getHttpRetryTimes());
		return bitmap;
	}
	/**
	 * 获取版块类型列表
	 * 
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static ForumTypeList getInfoTypesList(AppContext appContext,
			String url, int cat_type) throws AppException {

		String infotypesurl = url;// URLs.FORUMTYPES_LIST;

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				null);
		try {
			return ForumTypeList.parse(appContext,
					_post(appContext, infotypesurl, params, null), cat_type);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取版块消息二级列表
	 * 
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */

	public static ItemInfoList getForumInfoList(AppContext appContext,
			String url, String offsetid, final int pageSize,
			final boolean isRefresh, final int rowtype) throws AppException {

		String Url = URLs.formatURL(url);

		JSONObject request = new JSONObject();

		try {
			request.putOpt("count", pageSize);

			int flag = 1;
			if (!isRefresh)
				flag = 0;
			request.putOpt("direction", flag);

			request.putOpt("offsetid", offsetid);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}
		String requestinfo = request.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				requestinfo);
		boolean bsave = false;
		if (isRefresh)
			bsave = true;
		try {
			return ItemInfoList.parse(appContext,
					_post(appContext, Url, params, null), bsave, rowtype);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取号外栏目列表
	 * 
	 * @param url
	 * @param catalog
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static ColumnEntityList getColumnEntityList(AppContext appContext,
			String url, int type, boolean bsavetoDb) throws AppException {

		String infotypesurl = url;// URLs.FORUMTYPES_LIST;

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				null);
		try {
			return ColumnEntityList.parse(appContext,
					_post(appContext, infotypesurl, params, null), type,
					bsavetoDb);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static Result reportMultiSelectedCatalog(AppContext appContext,
			List<ForumType> list) {
		String url = URLs.EXTRA_RSS;
		Result result = null;

		JSONObject data = new JSONObject();
		try {
			JSONArray array = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				JSONObject obj = new JSONObject();
				ForumType typedata = list.get(i);
				obj.putOpt("cat_id", typedata.getInfoId());
				if (typedata.getCutomType() == ForumType.CUSTOM_TYPE_DEFAULT
						|| typedata.getCutomType() == ForumType.CUSTOM_TYPE_SELECTED)
					obj.putOpt("type", 0);
				else
					obj.putOpt("type", 1);
				array.put(obj);
			}
			JSONObject value = new JSONObject();
			value.putOpt("all", array);
			data.putOpt("data", value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			result = new Result(-1, "json error");
			return result;
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				data.toString());
		try {
			result = http_post(appContext, url, params, null);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			result = new Result(-1, "AppException error");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = new Result(-1, "IOException error");
		} catch (Exception e) {
			result = new Result(-1, "Exception error");
		}
		return result;
	}

	public static Result reportSigleSelectedStateCatalog(AppContext appContext,
			String catalog_id, int state) {
		String url = URLs.EXTRA_RSS;
		Result result = null;

		JSONObject data = new JSONObject();
		try {

			JSONObject obj = new JSONObject();
			obj.putOpt("cat_id", catalog_id);
			obj.putOpt("type", state);

			JSONArray array = new JSONArray();
			array.put(obj);
			JSONObject value = new JSONObject();
			value.putOpt("one", array);
			data.putOpt("data", value);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			result = new Result(-1, "json error");
			return result;
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				data.toString());
		try {
			result = http_post(appContext, url, params, null);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			result = new Result(-1, "AppException error");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = new Result(-1, "IOException error");
		} catch (Exception e) {
			result = new Result(-1, "Exception error");
		}
		return result;
	}

	/**
	 * 获取宣传位数据
	 * 
	 * @param appContext
	 * @param url
	 * @param zoneId
	 * @return
	 * @throws AppException
	 */
	public static AdZoneList getAdZoneList(AppContext appContext, String url,
			int zoneId) throws AppException {

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				null);
		try {
			return AdZoneList.parse(appContext, http_get(appContext, url),
					zoneId, true);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 从服务器获取全局数据
	 * 
	 * @param appContext
	 * @param nType
	 * @return
	 * @throws AppException
	 */
	public static GlobalDataEntity getGlobalDataEntity(AppContext appContext,
			int nType) throws AppException {

		String url = URLs.GET_GLOBADATA_HTTP + "?dataflag=" + nType;

		try {
			return GlobalDataEntity.parse(appContext,
					http_get(appContext, url), nType);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取任务列表数据
	 * 
	 * @param appContext
	 * @param rrl
	 * @return
	 * @throws AppException
	 */
	public static RewardListDataEntity getRewardListDataEntity(
			AppContext appContext, reqRewardList rrl) throws AppException {

		String reqinfo = ObjectUtils.getJsonStringFromObject(rrl);

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.GET_REWARDLISTDATA_HTTP;

		try {
			return RewardListDataEntity.parse(_post(appContext, url, params,
					null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取任务列表数据(参数有betwen)
	 * 
	 * @param appContext
	 * @param rrl
	 * @return
	 * @throws AppException
	 */
	public static RewardListDataEntity getRewardListDataEntityBetwen(
			AppContext appContext, reqRewardListBetwen rrlb) throws AppException {

		String reqinfo = ObjectUtils.getJsonStringFromObject(rrlb);
		// 把"dmjt_"替换成"$"
		reqinfo = reqinfo.replaceAll("dmjt_", "\\$");			

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.GET_REWARDLISTDATA_HTTP;

		try {
			return RewardListDataEntity.parse(_post(appContext, url, params,
					null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 发送收藏任务/取消收藏任务请求
	 * 
	 * @param appContext
	 * @param strTaskId
	 *            悬赏任务id
	 * @param nCollectionStatus
	 *            收藏状态
	 * @return
	 * @throws AppException
	 */
	public static Result applyCollection(AppContext appContext,
			String strTaskId, int nCollectionStatus) throws AppException {

		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("task_id", strTaskId);
			pd.putOpt("status", nCollectionStatus);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String reqinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);

		String url = URLs.APPLY_TASKCOLLECTION_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 发送收藏公司/取消收藏公司请求
	 * 
	 * @param appContext
	 * @param strComplanyId
	 * @param nCollectionStatus
	 * @return
	 * @throws AppException
	 */
	public static Result CompanyCollection(AppContext appContext,
			String strCompanyId, int nCollectionStatus) throws AppException {
		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("id", strCompanyId);
			pd.putOpt("status", nCollectionStatus);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String reqinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);

		String url = URLs.APPLY_COMPANYCOLLECTION_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 向服务器发送接收任务请求
	 * 
	 * @param strTaskId
	 *            // 任务ID
	 * @param strApplyType
	 *            // 请求类型
	 * @param strResumeId
	 *            // 简历ID
	 * @return
	 * @throws AppException
	 */
	public static Result applyReward(AppContext appContext, String strTaskId,
			String strApplyType, String strResumeId) throws AppException {

		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("task_id", strTaskId);
			pd.putOpt("apply_type", strApplyType);
			pd.putOpt("resume_id", strResumeId);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String reqinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.APPLY_REWARDTASK_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 快速推荐
	 * 
	 * @param appContext
	 * @param reqQRD
	 * @return
	 * @throws AppException
	 */
	public static Result quickRecommend(AppContext appContext,
			reqQuickRecommendData reqQRD) throws AppException {

		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("task_id", reqQRD.getTask_Id());
			pd.putOpt("apply_type", reqQRD.getApply_Type());
			pd.putOpt("name", reqQRD.getName());
			pd.putOpt("mail", reqQRD.getMail());
			pd.putOpt("phone", reqQRD.getPhone());
			pd.putOpt("memo", reqQRD.getMemo());
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String reqinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.QUICKRECOMMENDED_REWARDTASK_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取收藏任务数据列表
	 * 
	 * @param appContext
	 * @param rrl
	 * @return
	 * @throws AppException
	 */
	public static RewardListDataEntity getCollectionList(AppContext appContext,
			reqRewardList rrl, int nRequestType) throws AppException {

		String reqinfo = ObjectUtils.getJsonStringFromObject(rrl);

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.GET_TASKCOLLECTIONLIST_HTTP;
		switch (nRequestType) {
		case HeadhunterPublic.MYCOLLECTION_DATATYPE_COMPANY: // 公司收藏
		{
			url = URLs.GET_COMPANYCOLLECTIONLIST_HTTP;
		}
			break;
		case HeadhunterPublic.MYCOLLECTION_DATATYPE_REWARD: // 任务收藏
		default: {
			url = URLs.GET_TASKCOLLECTIONLIST_HTTP;
		}
			break;
		}

		try {
			return RewardListDataEntity.parse(_post(appContext, url, params,
					null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取我的记录数据列表
	 * 
	 * @param appContext
	 * @param rma
	 * @return
	 * @throws AppException
	 */
	public static RewardListDataEntity getRecordList(AppContext appContext,
			reqMyApply rma) throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}

		String reqinfo = ObjectUtils.getJsonStringFromObject(rma);
		MyLogger.i(TAG, "getRecordList##request##" + reqinfo);

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.GET_RECORDLIST_HTTP;

		try {
			return RewardListDataEntity.parse(_post(appContext, url, params,
					null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 修改密码
	 * 
	 * @param appContext
	 * @param modifyPwdInfo
	 * @return
	 * @throws AppException
	 */
	public static Result modifyPwd(AppContext appContext,
			ModifyPwdInfo modifyPwdInfo) throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				ObjectUtils.getJsonStringFromObject(modifyPwdInfo));
		String url = URLs.CHANGE_PASSWORD_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 认证手机
	 * 
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static Result verifyMobile(AppContext appContext, String mobileNumber)
			throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.putOpt("phone", Long.parseLong(mobileNumber));
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
			throw AppException.json(e1);
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				jsonObject.toString());
		String url = URLs.VERIFY_MOBILE_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 验证手机验证码
	 * 
	 * @param appContext
	 * @param mobileNumber
	 * @return
	 * @throws AppException
	 */
	public static Result verifyMobileCode(AppContext appContext,
			String mobileCode) throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("verify_no", mobileCode);
		} catch (JSONException e1) {
			e1.printStackTrace();
			throw AppException.json(e1);
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				jsonObject.toString());
		String url = URLs.VERIFY_MOBILE_CODE_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 验证邮箱 发送邮件
	 * 
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static Result verifyEmail(AppContext appContext, String email)
			throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("email", email);
		} catch (JSONException e1) {
			e1.printStackTrace();
			throw AppException.json(e1);
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				jsonObject.toString());
		String url = URLs.VERIFY_EMAIL_CODE_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 验证邮箱 发送邮件
	 * 
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static Result submitFeedBack(AppContext appContext,
			String feedbackContent) throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("content", feedbackContent);
		} catch (JSONException e1) {
			e1.printStackTrace();
			throw AppException.json(e1);
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				jsonObject.toString());
		String url = URLs.FEEDBACK_SUBMIT_CODE_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 检查新版本
	 * 
	 * @param appContext
	 * @param modifyPwdInfo
	 * @return
	 * @throws AppException
	 */
	public static Result checkVersion(AppContext appContext)
			throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}
		String url = URLs.CHECK_VERSION_HTTP;

		try {
			return Result.parse(http_get(appContext, url));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	/**
	 * check启动图片
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static Result checkStartImage(AppContext appContext)
			throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}
		String url = URLs.APP_LOAD_IMAGE;

		try {
			return Result.parse(http_get(appContext, url));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static Result getUserInfo(AppContext appContext) throws AppException {
		if (!appContext.isNetworkConnected()) {
			throw AppException.network(new ConnectException());
		}
		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				null);
		String url = URLs.USER_INFO_HTTP;
		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 我模块获取用户信息
	 * 
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static Result getPersonalInfo(AppContext appContext)
			throws AppException {
		String url = URLs.PERSONAL_INFO_HTTP;
		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				null);
		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 行业圈内人
	 * 
	 * @param appContext
	 * @param requestInfo
	 * @return
	 * @throws AppException
	 */
	public static Result getIndustryInsiders(AppContext appContext,
			RequestInfo requestInfo) throws AppException {

		String url = URLs.INDUSTRY_INSIDERS_HTTP;

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				ObjectUtils.getJsonStringFromObject(requestInfo));
		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}

	/**
	 * 名企招聘
	 * 
	 * @param appContext
	 * @param requestInfo
	 * @return
	 * @throws AppException
	 */
	public static Result getCompanyRecruitment(AppContext appContext,
			RequestInfo requestInfo) throws AppException {

		String url = URLs.COMPANY_RECRUITMENT_HTTP;

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				ObjectUtils.getJsonStringFromObject(requestInfo));
		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}

	public static Result getIndustryInsiderDetail(AppContext appContext,
			String id, int type) throws AppException {

		String url = URLs.INSIDERS_DETAIL_HTTP + id;
		if (type == InsidersAndCompany.TYPECOMPANY) {
			url = URLs.COMPANY_DETAIL_HTTP + id;
		}
		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				null);
		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}

	/**
	 * 提交订阅
	 * 
	 * @param appContext
	 * @param cat_id
	 * @param status
	 *            1=>订阅, 2=>取消
	 * @param type
	 * @return
	 * @throws AppException
	 */
	public static Result submitRSSs(AppContext appContext, RSSInfo[] rssInfo,
			String type) throws AppException {

		String url = URLs.RSS_SUBSMANAGER_SUBMIT_BASE + "/" + URLs.RSS_TYPEPOST;
		if (URLs.RSS_TYPEBOSS.equals(type)) {
			url = URLs.RSS_SUBSMANAGER_SUBMIT_BASE + "/" + URLs.RSS_TYPEBOSS;
		} else if (URLs.RSS_TYPECOMPANY.equals(type)) {
			url = URLs.RSS_SUBSMANAGER_SUBMIT_BASE + "/" + URLs.RSS_TYPECOMPANY;
		}

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				ObjectUtils.getJsonStringFromObject(rssInfo));
		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}

	public static Result submitRSS(AppContext appContext, String cat_id,
			String status, String type) throws AppException {
		RSSInfo[] rssInfos = new RSSInfo[1];
		RSSInfo rssInfo = new RSSInfo();
		rssInfo.setCat_id(cat_id);
		rssInfo.setStatus(status);
		rssInfos[0] = rssInfo;
		return submitRSSs(appContext, rssInfos, type);
	}

	public static Result getCommentList(AppContext appContext, String id,
			int type, RequestInfo requestInfo) throws AppException {
		String typeStr = "boss";
		if (type == InsidersAndCompany.TYPECOMPANY) {
			typeStr = "company";
		}
		String url = URLs.COMMENT_LIST_HTTP + id + "/" + typeStr;

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				ObjectUtils.getJsonStringFromObject(requestInfo));
		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static Result submitComment(AppContext appContext,
			CommentSubmitInfo commentSubmit) throws AppException {

		String url = URLs.COMMENT_SUBMIT_HTTP;

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				ObjectUtils.getJsonStringFromObject(commentSubmit));
		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取职位列表 type:boss company
	 * 
	 * @param appContext
	 * @param commentSubmit
	 * @return
	 * @throws AppException
	 */
	public static Result getPostsByType(AppContext appContext, String id,
			RequestInfo requestInfo, int type) throws AppException {

		String url = URLs.COMPANY_LIST_HTTP + id;
		// if (type == InsidersAndCompany.TYPEINSIDERS) {
		// url = URLs.BOSS_LIST_HTTP + id;
		// }

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				ObjectUtils.getJsonStringFromObject(requestInfo));
		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	public static Result getPosts(AppContext appContext, String id,
			RequestInfo requestInfo) throws AppException {

		String url = URLs.COMPANY_LIST_HTTP + id;

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				ObjectUtils.getJsonStringFromObject(requestInfo));
		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取我的账号相关信息
	 * 
	 * @param appContext
	 * @return
	 * @throws AppException
	 */
	public static MyAccontInfoDataEntity getMyAccountInfoData(
			AppContext appContext) throws AppException {

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				null);
		String url = URLs.GET_MYACCOUNTINFODATA_HTTP;

		try {
			return MyAccontInfoDataEntity.parse(_post(appContext, url, params,
					null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取收藏任务数据列表
	 * 
	 * @param appContext
	 * @param rrl
	 * @return
	 * @throws AppException
	 */
	public static TransactionRecordListDataEntity getTransactionRecordList(
			AppContext appContext, reqConsumelog rcl) throws AppException {

		String reqinfo = ObjectUtils.getJsonStringFromObject(rcl);

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.GET_TRANSACTIONRECORDLIST_HTTP;

		try {
			return TransactionRecordListDataEntity.parse(_post(appContext, url,
					params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 连接服务器检查安全密码是否正确
	 * 
	 * @param appContext
	 * @param strSecurePwd
	 *            安全密码
	 * @return
	 * @throws AppException
	 */
	public static Result checkSecurePwd(AppContext appContext,
			String strSecurePwd) throws AppException {

		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("password", strSecurePwd);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String reqinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.CHECK_SECUREPASSWORD_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 绑定支付宝账号
	 * 
	 * @param appContext
	 * @param strAccout
	 * @return
	 * @throws AppException
	 */
	public static Result bindingAlipayAccount(AppContext appContext,
			String strAccout) throws AppException {

		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("alipay_acc", strAccout);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String reqinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.BINDING_ALIPAYACCOUNT_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取支付订单号
	 * 
	 * @param appContext
	 * @param strTaskId
	 * @return
	 * @throws AppException
	 */
	public static Result getPaymentOrderNo(AppContext appContext,
			String strType, String strId) throws AppException {

		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("restype", strType);
			pd.putOpt("resid", strId);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String reqinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.GET_PAYMENTORDERNO_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 获取公司收藏状态
	 * 
	 * @param appContext
	 * @param strId
	 * @return
	 * @throws AppException
	 */
	public static Result getCompanyCollectionStatus(AppContext appContext,
			String strId) throws AppException {

		JSONObject pd = new JSONObject();
		try {
			pd.putOpt("id", strId);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		String reqinfo = pd.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.GET_COMPANYCOLLECTIONSTATUS_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取任务收藏状态
	 * 
	 * @param appContext
	 * @param strId
	 * @return
	 * @throws AppException
	 */
	public static TaskStatusInfoEntity getTaskCollectionStatus(AppContext appContext,
			reqTaskId taskid) throws AppException {

		String reqinfo = ObjectUtils.getJsonStringFromObject(taskid);

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.GET_TASKCOLLECTIONSTATUS_HTTP;

		try {
			return TaskStatusInfoEntity.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	/**
	 * 提交(修改)个人资料
	 * 
	 * @param appContext
	 * @param mapp
	 * @return
	 * @throws AppException
	 */
	public static Result submitPersonalProfile(AppContext appContext,
			MyAccountPersonalProfile mapp) throws AppException {

		String reqinfo = ObjectUtils.getJsonStringFromObject(mapp);

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				reqinfo);
		String url = URLs.SUBMIT_PERSONALPROFILE_HTTP;

		try {
			return Result.parse(_post(appContext, url, params, null));
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}
}

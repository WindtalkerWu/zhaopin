package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.content.Context;

import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.common.StringUtils;

/**
 * 接口URL实体类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class URLs implements Serializable {

	// public final static String HOST = "10.30.100.89:8888";
	// private final static String HOSTIP = "192.168.1.41:8889";
	// private final static String HOSTIP = "222.66.4.242:8889";
	public final static String HOST = ConfigOptions.getHostIp();
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";

	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";

	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;
	public final static String URL_APK_HOST = HTTP + ConfigOptions.getHost()
			+ "/" + ConfigOptions.getWebHomeHead() + "/public";// apk下载
	// 图片服务器 外网host ip
	// private final static String URL_API_IMG_HOST = "http://222.66.4.242:90/";
	private final static String URL_API_IMG_HOST = HTTP
			+ ConfigOptions.getImgHostIp();
	/*********************************************************************************************************/
	// public final static String LOGIN_VALIDATE_HTTP = HTTP + HOST +
	// URL_SPLITTER + "user/login";
	public final static String LOGIN_VALIDATE_HTTP = HTTP + HOST + URL_SPLITTER
			+ "login";

	/*********************************************************************************************************/
	// public final static String LOGIN_VALIDATE_HTTP = HTTP + HOST +
	// URL_SPLITTER + "user/login";
	// 注册
	public final static String REGISTER_VALIDATE_HTTP = HTTP + HOST
			+ URL_SPLITTER + "register";

	// 手机密码找回
	public final static String FIND_PASSWORD_MOBILE_HTTP = HTTP + HOST
			+ URL_SPLITTER + "user/forgetpassword";
	// 邮箱密码找回
	public final static String FIND_PASSWORD_MAIL_HTTP = HTTP + HOST
			+ URL_SPLITTER + "user/forgetpassword";
	// 密码找回
	public final static String FIND_PASSWORD_HTTP = HTTP + HOST + URL_SPLITTER
			+ "user/forgetpassword";
	// 安全密码验证
	public final static String CHECK_SECUREPASSWORD_HTTP = HTTP + HOST
			+ URL_SPLITTER + "user/verifypassword";
	// 绑定支付宝账号
	public final static String BINDING_ALIPAYACCOUNT_HTTP = HTTP + HOST
			+ URL_SPLITTER + "user/changelalipayacc";
	// 获取支付订单号
	public final static String GET_PAYMENTORDERNO_HTTP = HTTP + HOST
			+ URL_SPLITTER + "payment/generate";

	// 获取我的账号信息相关
	public final static String GET_MYACCOUNTINFODATA_HTTP = HTTP + HOST
			+ URL_SPLITTER + "user/info";

	// 获取交易记录列表
	public final static String GET_TRANSACTIONRECORDLIST_HTTP = HTTP + HOST
			+ URL_SPLITTER + "user/consumelog";

	// 获取验证码
	public final static String GET_VERIFY_HTTP = HTTP + HOST + URL_SPLITTER
			+ "/";

	// 提交(修改)用户资料
	public final static String SUBMIT_PERSONALPROFILE_HTTP = HTTP + HOST
			+ URL_SPLITTER + "user/submit";
	/*********************************************************************************************************/
	// 获取全局数据
	public final static String GET_GLOBADATA_HTTP = HTTP + HOST + URL_SPLITTER
			+ "data/iinitialed";
	/*********************************************************************************************************/
	// 消息频道首页
	public final static String FORUMTYPES_LIST = URL_API_HOST + "message/home";
	// 获取 二级小报形式列表
	public final static String FORUMINFO_LIST = URL_API_HOST
			+ "message/advlist";// "message/list";
	/*********************************************************************************************************/
	// 悬赏相关
	// 悬赏首页
	public final static String GET_REWARDLISTDATA_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/main";
	// 接受招聘任务/推荐任务
	public final static String APPLY_REWARDTASK_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/apply";
	// 接受招聘任务/推荐任务
	public final static String QUICKRECOMMENDED_REWARDTASK_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/quickrecommended";
	// 获取公司收藏状态
	public final static String GET_COMPANYCOLLECTIONSTATUS_HTTP = HTTP + HOST
			+ URL_SPLITTER + "company/isfav";
	// 获取任务收藏状态
	public final static String GET_TASKCOLLECTIONSTATUS_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/applytype/get";
	// 发送收藏任务/取消收藏任务
	public final static String APPLY_TASKCOLLECTION_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/fav";
	// 发送收藏公司/取消收藏公司
	public final static String APPLY_COMPANYCOLLECTION_HTTP = HTTP + HOST
			+ URL_SPLITTER + "company/fav";
	// 获取我的任务收藏列表
	public final static String GET_TASKCOLLECTIONLIST_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/favlist";
	// 获取我的公司收藏列表
	public final static String GET_COMPANYCOLLECTIONLIST_HTTP = HTTP + HOST
			+ URL_SPLITTER + "company/favlist";
	// 获取我的记录列表
	public final static String GET_RECORDLIST_HTTP = HTTP + HOST + URL_SPLITTER
			+ "task/myapply";

	public final static String MY_REWARD_LIST_HTTP = HTTP + HOST + URL_SPLITTER
			+ "task/my";
	public final static String MY_REWARD_STATUS_LIST_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/setstatus";
	public final static String SAVE_REWARD_HTTP = HTTP + HOST + URL_SPLITTER
			+ "task/submit";
	public final static String GET_DETIAL_REWARD_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/info";
	// 修改密码
	public final static String CHANGE_PASSWORD_HTTP = HTTP + HOST
			+ URL_SPLITTER + "user/changpassword";
	// 检查app版本号
	public final static String CHECK_VERSION_HTTP = HTTP + HOST + URL_SPLITTER
			+ "system/andriod/version";
	// 认证手机
	public final static String VERIFY_MOBILE_HTTP = HTTP + HOST + URL_SPLITTER
			+ "system/userverify/phone";
	public final static String VERIFY_MOBILE_CODE_HTTP = HTTP + HOST
			+ URL_SPLITTER + "system/userverify/phone/submit";
	// 认证邮箱
	public final static String VERIFY_EMAIL_CODE_HTTP = HTTP + HOST
			+ URL_SPLITTER + "system/userverify/email";
	// 提交用户反馈
	public final static String FEEDBACK_SUBMIT_CODE_HTTP = HTTP + HOST
			+ URL_SPLITTER + "system/suggestion/submit";

	public final static String USER_INFO_HTTP = HTTP + HOST + URL_SPLITTER
			+ "user/info";

	// 个人信息首页
	public final static String PERSONAL_INFO_HTTP = HTTP + HOST + URL_SPLITTER
			+ "personal/main";
	
	//app启动界面图片
	public final static String APP_LOAD_IMAGE = HTTP + HOST + URL_SPLITTER
			+ "zones/apploading";

	/*********************************************************************************************************/
	// 号外模块 首页
	public final static String EXTRA_HOME = URL_API_HOST + "extra/home";
	// 号外模块 获取更多栏目
	public final static String EXTRA_LIST = URL_API_HOST + "extra/list";
	// 号外模块 获取 二级小报形式列表
	public final static String EXTRAINFO_LIST = URL_API_HOST + "extra/advlist";
	// 号外模块 号外栏目列表
	public final static String EXTRA_COLUMN = URL_API_HOST + "extra/column";
	// 订阅管理上报URL
	public final static String EXTRA_RSS = URL_API_HOST + "extra/rss";
	// 号外模块 订阅管理获取更多栏目
	public final static String EXTRA_RSSLIST = URL_API_HOST + "extra/rsslist";
	// 订阅管理模块下载和上传数据url：rss/submit/[:type] 订阅的列表, 标准接口, type为枚举(post=>号外,
	// boss=>名人, company=>名企)
	public final static String RSS_SUBSMANAGER_LIST_BASE = URL_API_HOST
			+ "rss/list";
	public final static String RSS_SUBSMANAGER_SUBMIT_BASE = URL_API_HOST
			+ "rss/submit";
	public final static String RSS_TYPEPOST = "post";
	public final static String RSS_TYPEBOSS = "boss";
	public final static String RSS_TYPECOMPANY = "company";

	/*********************************************************************************************************/
	// 简历列表数据获取
	public final static String RESUME_LIST = URL_API_HOST + "resume/list";
	// 简历详细数据获取URL
	public final static String RESUME_GET = URL_API_HOST + "resume/edit";
	// 简历详细数据上报URL
	public final static String RESUME_SUBMIT = URL_API_HOST + "resume/submit";

	// 简历头像数据上报URL /*外网上报地址和测试地址结构不同*/
	public final static String RESUME_PICTURE_SUBMIT = (ConfigOptions.debug ? (URL_API_IMG_HOST
			+ "/" + ConfigOptions.getWebHomeHead() + "/public/api/imgupload")
			: (URL_API_IMG_HOST + "/api/imgupload"));
	// 简历头像数据拼接基础URL /*外网上报地址和测试地址结构不同*/
	public final static String RESUME_PICTURE_BASESUBMIT = (ConfigOptions.debug ? (URL_API_IMG_HOST
			+ "/" + ConfigOptions.getWebHomeHead() + "/public")
			: (URL_API_IMG_HOST));
	// 简历状态上报URL
	public final static String RESUME_STATE = URL_API_HOST + "resume/setstatus";
	// 简历默认选择上报URL
	public final static String RESUME_SELECT = URL_API_HOST
			+ "resume/setselected";

	/*********************************************************************************************************/

	// 活动列表
	public final static String CAMPAIGN_LIST = URL_API_HOST + "campaign/list";

	// 后台service轮询url
	public final static String SERVICE_POLL = URL_API_HOST + "appservice";
	/*********************************************************************************************************/
	// 号外模块 行业圈内人
	public final static String INDUSTRY_INSIDERS_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/boss";
	public final static String INSIDERS_DETAIL_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/boss/info/";
	public final static String COMPANY_DETAIL_HTTP = HTTP + HOST + URL_SPLITTER
			+ "task/company/info/";
	public final static String COMPANY_RECRUITMENT_HTTP = HTTP + HOST
			+ URL_SPLITTER + "task/company";
	public final static String COMMENT_LIST_HTTP = HTTP + HOST + URL_SPLITTER
			+ "task/comment/list/"; // 评论列表
	public final static String COMMENT_SUBMIT_HTTP = HTTP + HOST + URL_SPLITTER
			+ "task/comment/submit"; // 评论
	// public final static String BOSS_LIST_HTTP = HTTP + HOST + URL_SPLITTER +
	// "task/boss/list/";
	public final static String COMPANY_LIST_HTTP = HTTP + HOST + URL_SPLITTER
			+ "task/company/list/";

	/*********************************************************************************************************/
	// 系统建设中url
	public final static String SYSTEM_BUILDER = HTTP + HOST + URL_SPLITTER
			+ "system/build";
	/*********************************************************************************************************/
	private final static String URL_HOST = "oschina.net";
	private final static String URL_WWW_HOST = "www." + URL_HOST;
	private final static String URL_MY_HOST = "my." + URL_HOST;

	public final static int URL_OBJ_TYPE_OTHER = 0x000;
	public final static int URL_OBJ_TYPE_NEWS = 0x001;
	public final static int URL_OBJ_TYPE_SOFTWARE = 0x002;
	public final static int URL_OBJ_TYPE_QUESTION = 0x003;
	public final static int URL_OBJ_TYPE_ZONE = 0x004;
	public final static int URL_OBJ_TYPE_BLOG = 0x005;
	public final static int URL_OBJ_TYPE_TWEET = 0x006;
	public final static int URL_OBJ_TYPE_QUESTION_TAG = 0x007;

	private int objId;
	private String objKey = "";
	private int objType;

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public String getObjKey() {
		return objKey;
	}

	public void setObjKey(String objKey) {
		this.objKey = objKey;
	}

	public int getObjType() {
		return objType;
	}

	public void setObjType(int objType) {
		this.objType = objType;
	}

	/**
	 * 转化URL为URLs实体
	 * 
	 * @param path
	 * @return 不能转化的链接返回null
	 */
	public final static URLs parseURL(String path) {
		if (StringUtils.isEmpty(path))
			return null;
		path = formatURL(path);
		URLs urls = null;
		String objId = "";
		try {
			URL url = new URL(path);
			// 站内链接
			if (url.getHost().contains(URL_HOST)) {
			}
		} catch (Exception e) {
			e.printStackTrace();
			urls = null;
		}
		return urls;
	}

	/**
	 * 解析url获得objId
	 * 
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjId(String path, String url_type) {
		String objId = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if (str.contains(URL_SPLITTER)) {
			tmp = str.split(URL_SPLITTER);
			objId = tmp[0];
		} else {
			objId = str;
		}
		return objId;
	}

	/**
	 * 解析url获得objKey
	 * 
	 * @param path
	 * @param url_type
	 * @return
	 */
	private final static String parseObjKey(String path, String url_type) {
		path = URLDecoder.decode(path);
		String objKey = "";
		int p = 0;
		String str = "";
		String[] tmp = null;
		p = path.indexOf(url_type) + url_type.length();
		str = path.substring(p);
		if (str.contains("?")) {
			tmp = str.split("?");
			objKey = tmp[0];
		} else {
			objKey = str;
		}
		return objKey;
	}

	/**
	 * 对URL进行格式处理
	 * 
	 * @param path
	 * @return
	 */
	public final static String formatURL(String path) {
		if (path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}

}

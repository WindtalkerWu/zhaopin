package com.qianniu.zhaopin.app.common;

public class HeadhunterPublic {
	/******************************APP*******************************************/
	public static final String APP_NAME = "牵牛招聘";
	public static final String PACKAGENAME = "com.qianniu.zhaopin";
	/**********************************新浪微博相关***************************************/
	// APP Key
//	public static final String SINAWEIBO_APP_KEY = "3594840959";
	public static final String SINAWEIBO_REDIRECT_URL = "http://open.weibo.com/apps/3594840959/privilege/oauth";
	
	// APP SECRET
//	public static final String SINAWEIBO_APP_SECRET = "4bd617b5a265b6bdfe2a73f587af97b6";
	
	// 新支持scope 支持传入多个scope权限，用逗号分隔
//	public static final String SINAWEIBO_SCOPE = "email,direct_messages_read,direct_messages_write," +  
//            "friendships_groups_read,friendships_groups_write,statuses_to_me_read," +  
//            "follow_app_official_microblog";
	
	public static final String SINAWEIBO_SCOPE = "all";
	
	// 微博回调
	public static final int SINAWEIBO_FOR_RESULT = 32973;
	
	public static final String SINAWEIBO_URL_OAUTH2_ACCESS_AUTHORIZE = "https://open.weibo.cn/oauth2/authorize";
	
	public static final String SINAWEIBO_URL = "https://api.weibo.com/2/account/get_uid.json";
	
	public static final String KEY_UID = "uid";
	public static final String KEY_TOKEN = "access_token";
	public static final String KEY_EXPIRES = "expires_in";
	public static final String KEY_REFRESHTOKEN = "refresh_token";
	
	/*********************************QQ登录相关*********************************/
	// APP ID
//	public static final String QQ_APP_ID = "100580163";
	
	// APP SECRET
//	public static final String QQ_APP_SECRET = "9cb67fd8047e74c23310c93a90c009ac";
	
	public static final String QQ_SCOPE = "all";
	
	public static final int QQ_FOR_RESULT = 5657;
	/********************************微信相关************************************/
	// APP ID
//	public static final String WECHAT_APPID = "wxad5b813061866c32";
	/********************************支付宝相关************************************/
	// 支付宝合作者身份id，以2088开头的16位纯数字
	public static final String ALIPAY_DEFAULT_PARTNER = "2088111870767671";

	// 收款支付宝账号
	public static final String ALIPAY_DEFAULT_SELLER = "seven_k@1000new.com.cn";

	// 商户(RSA)私钥
//	public static final String ALIPAY_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOcZxClddK9Y2iDFiomb8TMEkqLAn2wle+Iqma9Z6zkNIhJ3a6f/PVK+ifgzN/1mXB2fnrbaaDU8pf4nQvW7wo2kv3J9JifeD/uGoZ+wtBCz8MioLTxXew2e+Ac8YfGgQ62E6dITLiueXTjHsLELf5+5jgZdyhxduahGvV/42rwbAgMBAAECgYEAjAjw9J/MtPQCORl6eSglX+TFdhmCgOW1y7ZEqhyyjD+JBeeXNJVVc25B7EQdCU+dQdYXNnXh72BwtTDVQlIj8Mla0UzvPDKTvfgJSp0NdunVvrh25X61bc/6N3IkCKjP/lDZS089j1S4WAhTCvndAebexFRxm9b7N3e1j0UwcgECQQD/9hWc6VFjQQB5d23qxtn8T4iREPxDDDKFP1twNUITGGPdal1YpTcNsgniokmqIxCyLyrfpv95KL/2F1WLiSMDAkEA5yK4AQiWudQ8x85hYleHOYILzvo59VER7///eD7fU6mx0qpjWH4IC1vjLwqQ93xam3qiOlWMO0N1FfrHaw0rCQJBAMXej9CwUXmAG4TK/Y7cUFGLjlBOqCSQvlFhrCcvFLg8R584BZdsiQWuV5P6zyWWW+bTLbqdl6srBFKSsU/gIM8CQFnb9pt4QlkWuVaY1uWLz+e8tmiOgc/s58NdzG1sTBjTpW2Yjq0NuoToAWf9fKSq9PLzd3SkY7N6o1+bMsJcyTkCQEG+wkNYSlvD2OosCdit28DqIEQdCFop6gQZiF8Jr7MUM4BWn6i93U+oXiaZKulQDWSCqk5WIyWw3gywXbFEKo0=";
	public static final String ALIPAY_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMLZFllu5HMtgJ5xHxQGEVIppOQ5QSq4ewgnxOMjc6HM4hnHj1ig8S/IGIJZh3KSxE8iGlOtCacwj9s46d0KKQNtlqrx2m2CUQ0GFKg2Jz3C5uApJanoJbT7eVNfYFKaegV7EDRWWiWrJBD4FbqqtpQbtsQR489ccGBKHadJykufAgMBAAECgYBsQCNUiHFPNAZzADHd9AkHdq9vRAfabiecPkv6TSXezG8JF3+iI7gbbbWAJbFEjBmYRUAPIOFuC2LAznvPG+fwT/8gwaak8ggkZZD/Z/RSkfi5hjxzS4CYbwc1ohLvjomawkVgsnKz+d4U+opAEzWhBDCy/hrzCAkY7Vqz4T8LcQJBAPj2CdFl2LxTEVeqgE0xM74qEzj1GiGCjJOol4UuHrJ1FANUmkv+kwENwb3tBSqBycBIGg3IUO4RGvxI94344CsCQQDIW2HpjBa3XCPGOdwjChLq8EVxhMv2QJVKcTSbvhEUChvk0022d7VH/V8ZUMDhfVz7z+xTvca3ondyOd5SWJRdAkB7ilP65eg0I6BieUgrhfPgNK7PP8lUwZySlUGDJAy1j6V7tZrPhSdbiYEPhzNeEYOU9/J9zl5ilHhooqi9ebd7AkAWti0qMUQd8ubnQIXQvwKX+TaWAKyyNG33609AXFJJSEATN1S222kESRdvojTLGs/gh5hNgdS12kfZ/e9SvFHNAkAjSxdc/jcPY57TWE66q7ASFhvryGrQVh50dJb6WAX7p4G+hc2o/j8ISaXxqGWD0ZnZchS96IWMCgoQ6OLpGWdt";
	// 商户(RSA)公钥
//	public static final String ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDnGcQpXXSvWNogxYqJm/EzBJKiwJ9sJXviKpmvWes5DSISd2un/z1Svon4Mzf9Zlwdn5622mg1PKX+J0L1u8KNpL9yfSYn3g/7hqGfsLQQs/DIqC08V3sNnvgHPGHxoEOthOnSEy4rnl04x7CxC3+fuY4GXcocXbmoRr1f+Nq8GwIDAQAB";
	
	// 服务器异步通知页面路径
	public static final String ALIPAY_NOTIFYURL = "http://www.1000new.com/script/apppay/notify_url.php";
	
	/*************************************************************************/
	// 登录类型
	public static final int LOGINTYPE_DEFAULT = 1;		// 普通登录
	public static final int LOGINTYPE_QQ = 2;			// QQ登录
	public static final int LOGINTYPE_SINAWEIBO = 3;	// 新浪微博登录
	
	// 登录消息类型
	public static final int LOGINMSG_ABNORMAL = -1;		// 登录异常
	public static final int LOGINMSG_FAIL = 0;			// 登录失败
	public static final int LOGINMSG_SUCCESS = 1;		// 登录成功
	
	public static final int LOGINMSG_THP_SUCCESS = 101;	//
	public static final int LOGINMSG_THP_ERROR = 102;
	public static final int LOGINMSG_THP_EXCEPTION = 103;
	public static final int LOGINMSG_THP_CANCEL= 104;
	
	// 邮箱找回
	public static final int  FINDPWDPOPUPWINDOW_MSG_MOBILE = 4;
	// 手机号找回
	public static final int	 FINDPWDPOPUPWINDOW_MSG_MAIL = 5;
	
	public static final int FINDPASSWORDMSG_SUCCESS = 6;
	public static final int FINDPASSWORDMSG_FAIL = 7;
	public static final int FINDPASSWORDMSG_ABNORMAL = 8;
	public static final int FINDPASSWORDMSG_EMAILORMOBLILEISNULL = 9;
	public static final int FINDPASSWORDMSG_EMAILORMOBLILEISNOT = 10;		// 没有要找回的账号(邮箱或者手机)
	/*************************************************************************/
	// 登录消息类型
	public static final int REGISTERMSG_ABNORMAL = -1;		// 注册异常
	public static final int REGISTERMSG_FAIL = 0;			// 注册失败
	public static final int REGISTERMSG_SUCCESS = 1;		// 注册成功
	/*************************************************************************/
	// 手机找回密码消息类型
	public static final int FINDPASSWORDMSG_MOBILE_ABNORMAL = -1;		// 手机找回密码异常
	public static final int FINDPASSWORDMSG_MOBILE_FAIL = 0;			// 手机找回密码失败
	public static final int FINDPASSWORDMSG_MOBILE_SUCCESS = 1;			// 手机找回密码成功
	
	// 邮箱找回密码消息类型
	public static final int FINDPASSWORDMSG_MAIL_ABNORMAL = -1;		// 邮箱找回密码异常
	public static final int FINDPASSWORDMSG_MAIL_FAIL = 0;			// 邮箱找回密码失败
	public static final int FINDPASSWORDMSG_MAIL_SUCCESS = 1;		// 邮箱找回密码成功
	/*************************************************************************/
	// 获取验证码消息类型
	public static final int GETVERIFYMSG_ABNORMAL = 102;		// 获取验证码异常
	public static final int GETVERIFYMSG_FAIL = 101;			// 获取验证码失败
	public static final int GETVERIFYMSG_SUCCESS = 100;			// 获取验证码成功
	/*************************************************************************/
	// 获取全局数据
	public static final int GLOBALDATA_GETVERSION_SUCCESS = 1001;		// 获取全局数据版本号成功
	public static final int GLOBALDATA_GETVERSION_FAIL = 1002;			// 获取全局数据版本号失败
	public static final int GLOBALDATA_GETVERSION_ABNORMAL = 1003;		// 获取全局数据版本号异常
	
	public static final int GLOBALDATA_GETDATA_SUCCESS = 1004;			// 获取全局数据成功
	public static final int GLOBALDATA_GETDATA_FAIL = 1005;				// 获取全局数据失败
	public static final int GLOBALDATA_GETDATA_ABNORMAL = 1006;			// 获取全局数据异常
	
	public static final int GLOBALDATA_GETDATA_VERSIONIDENTICAL = 1007;	// 全局数据版本号相同
	public static final int GLOBALDATA_GETDATA_SAVESUCCESS = 1008;		// 全局数据更新保存成功
	
	public static final int GLOBALDATA_GETDATA_NONETWORKCONNECT = 1009;	// 无网络
	
	public static final int GLOBALDATA_TYPE_GETVERSION = 0;				// 获取全局数据版本
	public static final int GLOBALDATA_TYPE_GETDATA = 1; 				// 获取全局数据
	/*************************************************************************/
	// 悬赏任务首页相关
	// 请求的方式
	public static final int REWARD_DIRECTIONTYPE_OLD = 0;		// 请求旧数据
	public static final int REWARD_DIRECTIONTYPE_NEW = 1;		// 请求新数据
	
	// 数据检索类型
	public static final int REWARD_REQUESTTYPE_NEW = 1;			// 最新发布
	public static final int REWARD_REQUESTTYPE_LIKE = 2;		// 猜你喜欢
	public static final int REWARD_REQUESTTYPE_PERIPHERY = 3;	// 周边职位
	public static final int REWARD_REQUESTTYPE_SORT = 4; 		// 悬赏排名
	
	// 发布者类型
	public static final int REWARD_PUBLISHERTYPE_COMPANY = 1;		// 公司
	public static final int REWARD_PUBLISHERTYPE_HEADHUNTING = 2;	// 猎头
	public static final int REWARD_PUBLISHERTYPE_PERSONAL = 3;		// 个人
	
	// 
	public static final int REWARD_GENDER_BOY = 1;				// 男
	public static final int REWARD_GENDER_GIRL = 2;				// 女
	
	// 悬赏任务类型
	public static final int REWARD_TYPE_NO = 0;					// 无类型
	public static final int REWARD_TYPE_JOB = 1;				// 招聘
	public static final int REWARD_TYPE_INTERVIEW = 2;			// 求职面试任务
	public static final int REWARD_TYPE_ENTRY = 3;				// 求职入职任务
	
	// 是否接受悬赏任务表示
	public static final String REWARD_CANDIDATE_FLAG = "1";			// 已接受任务(应聘)
	public static final String REWARD_NOCANDIDATE_FLAG = "0";		// 没有接受任务(应聘)
	
	// 是否收藏标识
	public static final String REWARD_COLLECTION_FLAG = "1";	// 已收藏
	public static final String REWARD_NOCOLLECTION_FLAG = "0";	// 没有收藏
	
	// 是否已读标识
	public static final String REWARD_READ_FLAG = "1";		// 已读标识
	public static final String REWARD_NOREAD_FLAG = "0";	// 已读标识
	
	// 公司是否收藏标识
	public static final String COMPANY_COLLECTION_FLAG = "1";	// 已收藏
	public static final String COMPANY_NOCOLLECTION_FLAG = "0";	// 没有收藏

	//任务列表展现模式
	public static int TASKSHOWTYPE_LIST = 0; // 无全局数据
	public static int TASKSHOWTYPE_WATERFALL = 1; // 有全局数据
	
	// 获取悬赏任务数据列消息类型
	public static final int REWARDMSG_GETDATA_ABNORMAL = -1;	// 获取悬赏任务数据列异常
	public static final int REWARDMSG_GETDATA_FAIL = 0;			// 获取悬赏任务数据列失败
	public static final int REWARDMSG_GETDATA_SUCCESS = 1;		// 获取悬赏任务数据列成功
	public static final int REWARDMSG_GETDATA_ISNULL = 2;		// 获取悬赏任务数据列失败(没有数据)
	public static final int REWARDMSG_GETDATA_NOMORE = 3;		// 获取悬赏任务数据列失败(没有更多数据)
	public static final int REWARDMSG_GETDATA_NONETWORKCONNECT = 4;		// 无网络
	public static final int REWARDMSG_MOBILEAUTHESUCCESS = 5;
	public static final int REWARDMSG_MOBILEAUTHERROR = 6;
	
	// 数据传输
	public static final String REWARD_DATATRANSFER_TASKID = "task_id";				// 悬赏任务ID
	public static final String REWARD_DATATRANSFER_TASKTYPE = "task_type";			// 悬赏任务类型
	public static final String REWARD_DATATRANSFER_TASKTITLE = "task_title";		// 悬赏任务名称
	public static final String REWARD_DATATRANSFER_TASKURL = "task_url";			// 悬赏任务url
	public static final String REWARD_DATATRANSFER_CANDIDATEFLAG = "task_candidateflag";	// 是否接受标识
	public static final String REWARD_DATATRANSFER_COLLECTIONFLAG = "task_collectionflag";	// 是否收藏标识
	public static final String REWARD_DATATRANSFER_COMPANYNAME = "company_name";	// 公司名
	public static final String REWARD_DATATRANSFER_COMPANYURL = "company_url";		// 公司url
	public static final String REWARD_DATATRANSFER_COMPANYID = "company_id";		// 公司id
	public static final String REWARD_DATATRANSFER_COMPANYMAPURL = "company_mapurl";// 公司地图url
	public static final String REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG = "company_collectionflag";// 是否收藏标识
	
	public static final String REWARD_DATATRANSFER_REWARDDATA = "reward_data";		// 悬赏任务数据
	public static final String REWARD_DATATRANSFER_REWARDREAD = "reward_read";		// 是否已读悬赏任务
	public static final String REWARD_DATATRANSFER_ACTIYITYTYPE = "acitity_type";	// 界面类型
	
	// 请求的数据类型(个人, 公司, 混合)
	public static final int REWARD_REQUESTDATATYPE_ALL = 0;			// 全部
	public static final int REWARD_REQUESTDATATYPE_COMPANY = 1;		// 公司
	public static final int REWARD_REQUESTDATATYPE_PERSONAL = 2;	// 个人
	
	// 行业类型
	public static final int REWARD_INDUSTRYTYPE_ALL = 0;					// 全部
	public static final int REWARD_INDUSTRYTYPE_IT = 1;						// IT/互联网
	public static final int REWARD_INDUSTRYTYPE_GAMES = 2;					// 游戏
	public static final int REWARD_INDUSTRYTYPE_ADVERTISEMENT = 3;			// 广告传媒
	
	// 
	public static final String REWARD_DATATRANSFER_REQUESTTYPE = "request_type"; 		 // 请求的悬赏任务过滤类型
	public static final String REWARD_DATATRANSFER_REQUESTDATATYPE = "request_datatype"; // 请求的数据类型
	// 请求的行业类型
	public static final String REWARD_DATATRANSFER_INDUSTRYTYPE = "industry_type";
	// 请求的过滤条件
	public static final String REWARD_DATATRANSFER_FILTERS = "filters_data"; 
	
	/*************************************************************************/
	// 悬赏任务搜索
	// 传递的数据类型
	// 关键字
	public static final String REWARDSEARCH_DATATRANSFER_KEYWORD = "rewardsearch_keyword";
	// 城市
	public static final String REWARDSEARCH_DATATRANSFER_CITY = "rewardsearch_city";
	// 行业
	public static final String REWARDSEARCH_DATATRANSFER_INDUSTRY = "rewardsearch_industry";
	// 过滤条件
	public static final String REWARDSEARCH_DATATRANSFER_FILTERS = "rewardsearch_filters";
	
	/*************************************************************************/
	// 城市/地区选择
	// 城市数据类型
	public static final int CITYCHOOSE_DATATYPE_COMMON = 0;					// 普通
	public static final int CITYCHOOSE_DATATYPE_HOT = 1;					// 热点城市
	
	// 
	public static final int CITYCHOOSE_TYPE_SINGLE = 1;						// 单选
	public static final int CITYCHOOSE_TYPE_MULTIPLE = 0;					// 多选
	
	public static final int CITYCHOOSE_NUM_SINGLE = 1;						// 单选时的个数限定
	public static final int CITYCHOOSE_NUM_MULTIPLE = 5;					// 多选时的个数限定
	
	// 数据传输
	public static final String CITYCHOOSE_DATATRANSFER_TYPE = "citychoose_type";
	public static final String CITYCHOOSE_DATATRANSFER_DATA = "citychoose_data";
	public static final String CITYCHOOSE_DATATRANSFER_SELECTIDS = "citychoose_selectIds";
	public static final String CITYCHOOSE_DATATRANSFER_NUM = "citychoose_num";
//	public static final String CITYCHOOSE_DATATRANSFER_DATAID = "citychoose_dataid";	
//	public static final String CITYCHOOSE_DATATRANSFER_DATANAME = "citychoose_dataname";
	/*************************************************************************/
	// 行业选择
	// 
	public static final int INDUSTRYCHOOSE_TYPE_SINGLE = 1;						// 单选
	public static final int INDUSTRYCHOOSE_TYPE_MULTIPLE = 0;					// 多选
	
	// 数据传输
	public static final String INDUSTRYCHOOSE_DATATRANSFER_TYPE = "industrychoose_type";
	public static final String INDUSTRYCHOOSE_DATATRANSFER_DATA = "industrychoose_data";
	public static final String INDUSTRYCHOOSE_DATATRANSFER_SELECTEDS = "industrychoose_selecteds";
	public static final String INDUSTRYCHOOSE_DATATRANSFER_NUM = "industrychoose_num";
	// 选中的父行业数据
	public static final String INDUSTRYCHOOSE_DATATRANSFER_PARENTINDUSTRYCHOOSE = "industrychoose_parentindustry";	
	
	public static final int INDUSTRYCHOOSE_NUM_SINGLE = 1;						// 单选时的个数限定
	public static final int INDUSTRYCHOOSE_NUM_MULTIPLE = 5;					// 多选时的个数限定
	/*************************************************************************/
	// 行业最大类型的ID
	public static final int INDUSTRY_PARENTDATAID_TECHNOLOGY = 1;				// 技术
	public static final int INDUSTRY_PARENTDATAID_ADVERTISINGCOMPANY = 2;		// 广告公司
	public static final int INDUSTRY_PARENTDATAID_OHTER = 3;					// 其他
	public static final int INDUSTRY_PARENTDATAID_MEDIA = 4;					// 媒体
	public static final int INDUSTRY_PARENTDATAID_BRAND = 5;					// 品牌/广告
	
	/*************************************************************************/
	// 悬赏任务详细界面
	// 收藏请求
	public static final int REWARDINFO_STATUS_COLLECTION = 1;			// 收藏
	public static final int REWARDINFO_STATUS_CANCELCOLLECTION = 2;		// 取消收藏
	
	// 申请悬赏任务消息类型
	public static final int REWARDINFO_MSG_APPLYTASK_ABNORMAL = -1;		// 接受异常
	public static final int REWARDINFO_MSG_APPLYTASK_FAIL = 100;		// 接受失败
	public static final int REWARDINFO_MSG_APPLYTASK_SUCCESS = 101;		// 接受成功
	public static final int REWARDINFO_MSG_APPLYTASK_LOGIN = 102;		// token失效重新登录
	// 消息类型
	public static final int REWARDINFO_MSG_CANDIDATEOK = 103;			// 用户同意自荐
	public static final int REWARDINFO_MSG_CANDIDATECANCEL = 104;		// 用户取消自荐
	// 发送收藏/取消收藏请求后消息类型
	public static final int REWARDINFO_MSG_APPLYCOLLECTION_ABNORMAL = 105;	// 收藏请求异常
	public static final int REWARDINFO_MSG_APPLYCOLLECTION_FAIL = 106;		// 收藏请求失败
	public static final int REWARDINFO_MSG_APPLYCOLLECTION_SUCCESS = 107;	// 收藏请求成功
	public static final int REWARDINFO_MSG_APPLYCOLLECTION_LOGIN = 108;		// token失效重新登录
	// 获取简历list消息类型
	public static final int REWARDINFO_MSG_GETRESUMELIST_ABNORMAL = 109;	// 获取常用简历异常
	public static final int REWARDINFO_MSG_GETRESUMELIST_FAIL = 110;		// 获取常用简历失败
	public static final int REWARDINFO_MSG_GETRESUMELIST_SUCCESS = 111;		// 获取常用简历成功
	public static final int REWARDINFO_MSG_GETRESUMELIST_LOGIN = 112;		// token失效重新登录
	
	// 接受悬赏任务类型(传给服务器)
	public static final int REWARDINFO_APPLYTYPE_CANDIDATE = 1;			// 应聘
	public static final int REWARDINFO_APPLYTYPE_RECOMMEND = 2;			// 推荐
	public static final int REWARDINFO_APPLYTYPE_JOBRECOMMEND = 3;		// 接受求职任务(我要赚钱)
	public static final int REWARDINFO_APPLYTYPE_COLLECTION = 4;		// 收藏
	
	// 接受悬赏任务类型(传给服务器)
	public static final int APPLYTASK_SENDTYPE_CANDIDATE = 1;			// 应聘
	public static final int APPLYTASK_SENDTYPE_RECOMMEND = 2;			// 推荐
	public static final int APPLYTASK_SENDTYPE_JOBRECOMMEND = 1;		// 接受求职任务(我要赚钱)
	/*************************************************************************/
	// 公司详细界面
	// 收藏请求
	public static final int COMPANYINFO_STATUS_COLLECTION = 1;				// 收藏
	public static final int COMPANYINFO_STATUS_CANCELCOLLECTION = 2;		// 取消收藏
	/*************************************************************************/
	// 我要推荐
	public static final String JOBRECOMMEND_DATATRANSFER_REASON = "jobrecommend_reason";
	
	// 申请我要推荐后消息类型
	public static final int JOBRECOMMEND_MSG_QUICKRECOMMEND_LOGIN = 2;			// token失效重新登录
	public static final int JOBRECOMMEND_MSG_QUICKRECOMMEND_SUCCESS = 1;		// 我要推荐成功
	public static final int JOBRECOMMEND_MSG_QUICKRECOMMEND_FAIL = 0;			// 我要推荐失败
	public static final int JOBRECOMMEND_MSG_QUICKRECOMMEND_ABNORMAL = -1;		// 我要推荐异常
	/*************************************************************************/
	// 推荐理由
	
	// 数据传输
	public static final String RECOMMENDREASONCHOOSE_DATATRANSFER_DATA = "recommendreasonchoose_data";

	/*************************************************************************/
	// 推荐
	// 申请推荐请求后的消息类型
	public static final int REWARDRECOMMEND_MSG_APPLYTASK_ABNORMAL = -1;	// 接受异常
	public static final int REWARDRECOMMEND_MSG_APPLYTASK_FAIL = 0;			// 接受失败
	public static final int REWARDRECOMMEND_MSG_APPLYTASK_SUCCESS = 1;		// 接受成功
	public static final int REWARDRECOMMEND_MSG_QUICKRECOMMEND_LOGIN = 2;	// token失效重新登录
	/*************************************************************************/
	// 快速推荐	
	// 快速推荐后消息类型
	public static final int QUICKRECOMMEND_MSG_NONETWORKCONNECT = 3;		// 无网络
	public static final int QUICKRECOMMEND_MSG_QUICKRECOMMEND_LOGIN = 2;	// token失效重新登录
	public static final int QUICKRECOMMEND_MSG_RECOMMEND_SUCCESS = 1;		// 快速推荐成功
	public static final int QUICKRECOMMEND_MSG_RECOMMEND_FAIL = 0;			// 快速推荐失败
	public static final int QUICKRECOMMEND_MSG_RECOMMEND_ABNORMAL = -1;		// 快速推荐异常
	/*************************************************************************/
	// 简历
	// 是否为默认简历
	public static final int RESUME_DEFAULTESUME = 1;						// 默认简历
	/*************************************************************************/	
	// 我的收藏
	// 请求的方式
	public static final int MYCOLLECTION_DIRECTIONTYPE_OLD = 0;		// 请求旧数据
	public static final int MYCOLLECTION_DIRECTIONTYPE_NEW = 1;		// 请求新数据
	
	// 数据检索类型
	public static final int MYCOLLECTION_DATATYPE_REWARD = 1;			// 任务收藏
	public static final int MYCOLLECTION_DATATYPE_COMPANY = 2;			// 公司收藏
	
	// 获取悬赏任务收藏数据消息类型
	public static final int MYCOLLECTIONMSG_GETCOLLECTION_ABNORMAL = -1;	// 获取悬赏任务收藏数据异常
	public static final int MYCOLLECTIONMSG_GETCOLLECTION_FAIL = 0;			// 获取悬赏任务收藏数据失败
	public static final int MYCOLLECTIONMSG_GETCOLLECTION_SUCCESS = 1;		// 获取悬赏任务收藏数据成功
	public static final int MYCOLLECTIONMSG_GETCOLLECTION_ISNULL = 2;		// 获取悬赏任务收藏数据列失败(没有数据)
	public static final int MYCOLLECTIONMSG_GETCOLLECTION_NOMORE = 3;		// 获取悬赏任务收藏数据列失败(没有更多数据)
	public static final int MYCOLLECTIONMSG_GETDATA_NONETWORKCONNECT = 4;	// 无网络
	/*************************************************************************/	
	// 我的记录
	// 请求的方式
	public static final int MYRECORDMSG_DIRECTIONTYPE_OLD = 0;		// 请求旧数据
	public static final int MYRECORDMSG_DIRECTIONTYPE_NEW = 1;		// 请求新数据
	
	// 获取我的记录数据消息类型
	public static final int MYRECORDMSG_GETRECORD_ABNORMAL = -1;	// 获取我的记录数据异常
	public static final int MYRECORDMSG_GETRECORD_FAIL = 0;			// 获取我的记录数据失败
	public static final int MYRECORDMSG_GETRECORD_SUCCESS = 1;		// 获取我的记录数据成功
	public static final int MYRECORDMSG_GETRECORD_ISNULL = 2;		// 获取我的记录数据失败(没有数据)
	public static final int MYRECORDMSG_GETRECORD_NOMORE = 3;		// 获取我的记录数据失败(没有更多数据)
	public static final int MYRECORDMSG_GETDATA_NONETWORKCONNECT = 4;	// 无网络

	/*************************************************************************/	
	// 
	public static final String ONELEVELCHOOSE_DATATRANSFER_TITLE = "onelevelchoose_title";
	public static final String ONELEVELCHOOSE_DATATRANSFER_DATA = "onelevelchoose_data";
	public static final String ONELEVELCHOOSE_DATATRANSFER_SELECTED = "onelevelchoose_selected";
	public static final String ONELEVELCHOOSE_DATATRANSFER_BACKDATA = "onelevelchoose_backdata";
	/********************************职能选择***********************************/	
	public static final String JOBFUNCTIONCHOOSE_DATA = "jobfunctionschoose_data";
	/*************************************************************************/	
	// 网络连接返回code
//	public static final int LINK_RESULT_DATA_TASKTABLEISNULL = 1501;		// Task表为空
//	public static final int LINK_RESULT_DATA_TASKACTION = 1523;				// TaskAction表查询为空
//	public static final int LINK_RESULT_DATA_TASKTABLEQUERYISNULL = 1525;	// Task表查询为空
	public static final int LINK_RESULT_DATA_TASKTABLEISNULL = 1999;		// Task表为空
	public static final int LINK_RESULT_DATA_TASKACTION = 1999;				// TaskAction表查询为空
	public static final int LINK_RESULT_DATA_TASKTABLEQUERYISNULL = 1999;	// Task表查询为空
	
	public static final int LINK_RESULT_EMAILORMOBLILEISNOT = 1999;			// 没有要找回的账号(E-mil或电话)
	public static final int LINK_RESULT_EMAILORMOBLILEISNULL = 4003;		// E-mil或电话为空
	/*************************************************************************/	
	// 界面类型
	public static final int ACTIVITY_TYPE_REWARDLIST = 0;			// 悬赏任务列表界面
	public static final int ACTIVITY_TYPE_MYRECORD = 1;				// 我的记录列表界面
	public static final int ACTIVITY_TYPE_MYCOLLECTION_TASK = 2;	// 我的收藏列表任务界面
	public static final int ACTIVITY_TYPE_MYCOLLECTION_COMPANY = 3;	// 我的收藏列表公司界面
	public static final int ACTIVITY_TYPE_POST = 4;					// 圈内人， 名企 职位列表
	/*************************************************************************/	
	public static final int RESULT_REGISTER_SUCCESS_OK = 100;		// 注册成功返回值
	public static final int RESULT_FINDPWD_MOBILE_SUCCESS_OK = 101;	// 通过手机账号密码找回成功返回值
	public static final int RESULT_FINDPWD_MAIL_SUCCESS_OK = 102;	// 通过邮箱密码找回成功返回值
	public static final int RESULT_CHOOSECITY_OK = 103;				// 选择城市成功返回值
	public static final int RESULT_CHOOSECITY_CANCEL = 104;			// 在城市选择界面没有选城市返回值
	public static final int RESULT_INDUSTRYCITY_OK = 105;			// 选择行业成功返回值
	public static final int RESULT_INDUSTRYCITY_CANCEL = 106;		// 在城市行业界面没有选行业返回
	public static final int RESULT_RECOMMENDREASON_OK = 107;		// 选择推荐理由成功返回值
	public static final int RESULT_RECOMMENDREASON_CANCEL = 108;	// 没有选择推荐理由成功返回
	public static final int RESULT_REWARDINOF_OK = 109;				// 详细悬赏任务界面返回值
	public static final int RESULT_REWARDSEARCH_OK = 110;			// 悬赏任务搜索界面返回值
	
	public static final int RESULT_LABEL_OK = 109;				// 选择标签
	public static final int RESUME_SELECT_RESULT_OK = 110;  	//获取选择的简历ok
	
	public static final int REQUEST_SELECT_TIMES = 111;  		//选择周期请求
	public static final int REQUEST_SELECT_EXPECTATION = 112;  	//选择期望年薪请求
	public static final int REQUEST_SELECT_INDUSTRY = 113;  	//选择期望行业
	
	public static final int RESULT_JOBFUNCTIONSBOTTOM_OK = 114;		// 选择职能最低层数据成功返回值
	public static final int RESULT_JOBFUNCTIONSBOTTOM_CANCEL = 115;	// 没有选择职能最低层数据成功返回
	
	public static final int RESULT_JOBFUNCTIONS_OK = 116;			// 选择职能数据成功返回值
	public static final int RESULT_JOBFUNCTIONS_CANCEL = 117;		// 没有选择职能数据成功返回
	
	public static final int RESULT_COMPANYINOF_OK = 118;			// 公司详情界面返回值
	
	public static final int RESULT_ALIPAYACCOUNT_CHANGE = 119;		// alipay账号绑定返回值
	
	public static final int RESULT_MYREWARDDETAIL_CHANGE = 120;		// 个人悬赏详情返回值
	
	public static final int RESULT_QIANNIULOGIN_SUCCESS_OK = 121;			// 普通(牵牛用户)登录成功返回值
	
	public static final int RESULT_MYACCOUNT_CANCEL = 122;			// 完善资料(我的账号)按返回键返回
	public static final int RESULT_MYACCOUNT_SUBMITSUCCESS = 123;	// 完善资料(我的账号)中提交成功
	/*************************************************************************/
	public static final int RESULT_ACTIVITY_CODE = 0;		// 
	public static final int RESULT_ACTIVITY_TENCENTWEIBOLOGIN = 5001;
	public static final int RESULT_ACTIVITY_TENCENTWEIBOSHARE = 5002;
	public static final int RESULT_ACTIVITY_SINAWEIBOSHARE= 5003;
	
	public static final String RESUME_SELECT_RESULT = "resumeSelected";//获取选择的简历key
	
	/******************************任务状态*******************************************/

//	public static final int TASK_STATUS_PUBLISHING = 1; 	// 发布中
//	public static final int TASK_STATUS_AUDIT = 2;			// 审核中 即申请发布
//	public static final int TASK_STATUS_PAUSE = 3;			// 暂停
//	public static final int TASK_STATUS_EXPIRED = 4;		// 过期
//	public static final int TASK_STATUS_ARCHIVE = 5;		// 存档
	
	/******************************付款标识*******************************************/

	public static final int PAY_FLAG_NOPAY = 1; 				// 未付款
	public static final int PAY_FLAG_PAY = 2;					// 已付款
	public static final int PAY_FLAG_REFUNDMENTINGING = 3;		// 退款中....
	public static final int PAY_FLAG_REFUNDMENTINGSUC = 4;		// 退款成功
	
	/******************************验证密码返回值******************************************/
	public static final String SECUREPWD_PWD_TRUE = "1";	// 密码正确
	/******************************引导界面类型*******************************************/
	public static final int GUIDETYPE_REWARD = 0;
	public static final int GUIDETYPE_MY = 1;
	
	// 数据传输
	public static final String GUIDE_DATATRANSFER_TYPE = "guide_type";

	/******************************activity result*******************************************/
	public static final int RESULT_FINISH = 11;
	/******************************我的简历库*******************************************/
	public final static String RESUMELIST_CALLTYPE = "resumelist_calltype";
	public final static String RESUMELIST_RESUMESELECTID = "resumelist_resumeId";
	
	// 调用类型
	public final static String RESUMELIST_CALLTYPE_PUBLISHREWARD = "resumelist_calltype_publishreward";
	public final static String RESUMELIST_CALLTYPE_REWARDINFO = "resumelist_calltype_rewardinfo";
	
	/******************************分享*******************************************/
	public static final int SHARETYPE_SINAWEIBO = 0;			// 分享到新浪微博
	public static final int SHARETYPE_TENCENTWEIBO = 1;			// 分享到腾讯微博
	/******************************消息********************************************/
	public static final int MSG_GETLOCATION_SUCCESS = 3010;					// 定位成功
	public static final int MSG_GETLOCATION_FAIL = 3011;					// 定位失败
	
	public static final int MSG_BINDING_ALIPAACCOUNT_SUCCESS = 3012;		// 绑定支付宝成功
	public static final int MSG_BINDING_ALIPAACCOUNT_FAIL = 3013; 			// 绑定支付宝失败
	public static final int MSG_BINDING_ALIPAACCOUNT_ABNORMAL = 3014; 		// 绑定支付宝异常
	
	public static final int MSG_GETMYACCOUNTINFO_SUCCESS = 3015;			// 获取我的账号成功
	public static final int MSG_GETMYACCOUNTINFO_FAIL = 3016; 				// 获取我的账号失败
	public static final int MSG_GETMYACCOUNTINFO_ABNORMAL = 3017; 			// 获取我的账号异常
	
	public static final int MSG_SECUREPWD_SUCCESS = 3018;					// 判断安全密码正确
	public static final int MSG_SECUREPWD_FAIL = 3019; 						// 判断安全密码失败
	public static final int MSG_SECUREPWD_ABNORMAL = 3020; 					// 判断安全密码异常
	public static final int MSG_SECUREPWD_NONETWORKCONNECT = 3021;			// 判断安全密码时无网络
	public static final int MSG_SECUREPWD_PWDFALSE = 3022;					// 安全密码不正确
	
	public static final int MSG_PAY_SUCCESS = 3023;							// 支付成功
	public static final int MSG_PAY_FAIL = 3024;							// 支付失败
	public static final int MSG_PAY_ABNORAL = 3025;							// 支付异常
	
	public static final int MSG_GETRDEFAULTESUMEIMPLE_SUCCESS = 3026;		// 获取默认简历简单信息成功
	public static final int MSG_GETRDEFAULTESUMEIMPLE_FAIL  = 3027;			// 获取默认简历简单信息失败
	
	// 发送公司收藏/取消公司收藏请求后消息类型
	public static final int COMPANYINFO_MSG_APPLYCOLLECTION_ABNORMAL = 3028;	// 收藏请求异常
	public static final int COMPANYINFO_MSG_APPLYCOLLECTION_FAIL = 3029;		// 收藏请求失败
	public static final int COMPANYINFO_MSG_APPLYCOLLECTION_SUCCESS = 3030;		// 收藏请求成功
	public static final int COMPANYINFO_MSG_APPLYCOLLECTION_LOGIN = 3031;		// token失效重新登录
	
	// 获取公司收藏状态
	public static final int MSG_GETCOMPANYCOLLCETIONSTATU_ABNORMAL = 3032;		// 获取公司收藏异常
	public static final int MSG_GETCOMPANYCOLLCETIONSTATU_FAIL = 3033;			// 获取公司收藏失败
	public static final int MSG_GETCOMPANYCOLLCETIONSTATU_SUCCESS = 3034;		// 获取公司收藏成功
	
	// 获取我的账号信息相关消息
	public static final int MSG_MYACCOUN_GETTDATA_NONETWORKCONNECT = 3035;	// 无网络
	public static final int MSG_MYACCOUN_GETTDATA_SUCCESS = 3036;			// 获取我的账号信息成功
	public static final int MSG_MYACCOUN_GETTDATA_FAIL = 3037; 				// 获取我的账号信息失败
	public static final int MSG_MYACCOUN_GETTDATA_ABNORMAL = 3038; 			// 获取我的账号信息异常
	
	// 获取交易记录相关消息
	public static final int MSG_GETTRANSACTIONRECORD_NONETWORKCONNECT = 3039; 	// 无网络
	public static final int MSG_GETTRANSACTIONRECORD_SUCCESS = 3040;			// 获取交易记录成功
	public static final int MSG_GETTRANSACTIONRECORD_FAIL = 3041; 				// 获取交易记录失败
	public static final int MSG_GETTRANSACTIONRECORD_ABNORMAL = 3042; 			// 获取交易记录异常
	public static final int MSG_GETTRANSACTIONRECORD_ISNULL = 3043;				// 没有数据
	public static final int MSG_GETTRANSACTIONRECORD_NOMORE = 3044;				// 没有更多的数据
	
	// 获取支付订单号
	public static final int MSG_PAYMENTORDERNO_SUCCESS = 3045;				// 获取支付订单号成功
	public static final int MSG_PAYMENTORDERNO_FAIL = 3046; 				// 获取支付订单号失败
	public static final int MSG_PAYMENTORDERNO_ABNORMAL = 3047; 			// 获取支付订单号异常
	
	// 更新我的账号头像
	public static final int MSG_MYACCOUN_UPLOADHEADSCULPTUR_SUCCESS = 3048;		// 更新我的账号头像成功
	public static final int MSG_MYACCOUN_UPLOADHEADSCULPTUR_FAIL = 3049; 		// 更新我的账号头像失败
	public static final int MSG_MYACCOUN_UPLOADHEADSCULPTUR_ABNORMAL = 3050; 	// 更新我的账号头像异常
	
	// 提交个人资料
	public static final int MSG_SUBMITPERSONALPROFILE_SUCCESS = 3051;		// 提交个人资料成功
	public static final int MSG_SUBMITPERSONALPROFILE_FAIL = 3052; 			// 提交个人资料失败
	public static final int MSG_SUBMITPERSONALPROFILE_ABNORMAL = 3053; 		// 提交个人资料异常
	public static final int MSG_SUBMITPERSONALPROFILE_NONETWORKCONNECT = 3071; // 提交个人资料无网络
	
	// 获取新浪微博用户信息
	public static final int MSG_GETSINAUSERINFO_SUCCESS = 3054;				// 获取成功
	public static final int MSG_GETSINAUSERINFO_FAIL = 3055; 				// 获取失败
	public static final int MSG_GETSINAUSERINFO_ABNORMAL = 3056; 			// 获取异常
	public static final int MSG_GETSINAUSERINFO_NONETWORKCONNECT = 3057;	// 无网络
	
	// 获取QQ用户信息
	public static final int MSG_GETQQUSERINFO_SUCCESS = 3058;				// 获取成功
	public static final int MSG_GETQQUSERINFO_FAIL = 3059; 					// 获取失败
	public static final int MSG_GETQQUSERINFO_ABNORMAL = 3060; 				// 获取异常
	public static final int MSG_GETQQUSERINFO_NONETWORKCONNECT = 3061;		// 无网络
	
	// 获取验证码
	public static final int MSG_GETAUTHCODE_SUCCESS = 3062;					// 获取验证码成功
	public static final int MSG_GETAUTHCODE_FAIL = 3063;					// 获取验证码失败
	public static final int MSG_GETAUTHCODE_FAIL_1949 = 3064;				// 获取验证码失败(手机号被占用)
	public static final int MSG_GETAUTHCODE_ABNORMAL = 3065;				// 获取验证码异常
	public static final int MSG_GETAUTHCODE_NONETWORKCONNECT = 3066;		// 获取获取验证码时无网络
	
	// 绑定手机
	public static final int MSG_VERIFYPHONE_SUCCESS = 3067;					// 绑定手机成功
	public static final int MSG_VERIFYPHONE_FAIL = 3068;					// 绑定手机失败
	public static final int MSG_VERIFYPHONE_ABNORMAL = 3069;				// 绑定手机异常
	public static final int MSG_VERIFYPHONE_NONETWORKCONNECT = 3070;		// 绑定手机时无网络
	
	// 获取任务收藏状态
	public static final int MSG_GETTASKCOLLCETIONSTATU_ABNORMAL = 3071;			// 获取任务收藏异常
	public static final int MSG_GETTASKCOLLCETIONSTATU_FAIL = 3072;				// 获取任务收藏失败
	public static final int MSG_GETTASKCOLLCETIONSTATU_SUCCESS = 3073;			// 获取任务收藏成功
	public static final int MSG_GETTASKCOLLCETIONSTATU_NONETWORKCONNECT = 3074;   // 获取任务收藏无网络
	
	/******************************支付相关*******************************************/
	public static final double PAY_RATE = 0.15;								// 费率
	
	/******************************加载数据的最大量*******************************************/
	public static final int LOADDATA_MAX = 300;
	
	/*************************************************************************/	
	// 收货地址界面类型
	public static final int DELIVERYADDRESS_TYPE_NEW = 0;			// 新增收货地址界面
	public static final int DELIVERYADDRESS_TYPE_EDIT = 1;			// 编辑收货地址界面
	
	// 数据传输
	public static final String DELIVERYADDRESS_DATATRANSFER_DATA = "deliveryaddress_data";	// 收货地址相关数据
	public static final String DELIVERYADDRESS_DATATRANSFER_TYPE = "deliveryaddress_type";	// 界面类型
	
	/*************************************************************************/	
	// 我的账号界面类型
	public static final int MYACCOUNT_TYPE_DEFAULT = 0;			// 默认类型
	public static final int MYACCOUNT_TYPE_FIRST = 1;			// 完善资料
	
	// 我的账号是否完善
	public static final String MYACCOUNT_COMPLETEFLAG_TRUE = "1";	// 完善
	public static final String MYACCOUNT_COMPLETEFLAG_FALSE = "0";	// 不完善
	
	// 我的账号性别
	public static final String MYACCOUNT_GENDER_MALE = "1";		// 男
	public static final String MYACCOUNT_GENDER_FEMALE = "2";	// 女
	
	// 数据传输
	public static final String MYACCOUNT_DATATRANSFER_TYPE = "myaccount_type";
	public static final String MYACCOUNT_DATATRANSFER_USERINFO = "myaccount_userinfo";		// 用户信息
	
	/******************************第三方APPID*******************************************/	
	// 微信APPID key
	public static final String THPKEY_WECHAT_APPID = "THPID_ONE";				
	// QQ登录APPID key
	public static final String THPKEY_QQ_APPID = "THPID_TWO";
	// QQ登录 APPSECRET key
	public static final String THPKEY_QQ_APPSECRET = "THPSECRET_TWO";
	// 新浪微博APPID key
	public static final String THPKEY_SINAWEIBO_APPID = "THPID_THREE";
	// 新浪微博APP SECRET
	public static final String THPKEY_SINAWEIBO_APPSECRET = "THPSECRET_THREE";
}

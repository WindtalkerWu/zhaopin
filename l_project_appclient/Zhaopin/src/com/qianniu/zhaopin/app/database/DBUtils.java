package com.qianniu.zhaopin.app.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.MyLogger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.widget.Toast;

public class DBUtils {
	// jlftest
	private MyLogger logger = MyLogger.getLogger("dababase.DBUtils");

	private Context context;
	private AppContext appcontext;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	public static final String DB_NAME = "db_matrixdigi.db";
	public static final String DB_NAME_BAK = "db_matrixdigi_bak.db";
	public static final String DATABASE_PATH = "/data/data/com.qianniu.zhaopin/databases/";
	private int DB_VERSION = 4;
	/*********************************************************************************************/
	public static final String KEY_PUBLIC_USER_ID = "user_id";
	/*********************************************************************************************/
	// 首页消息列表(版块，栏目列表)
	public static final String catalogTableName = "tb_catalog";
	private static final String catalogSQL = "CREATE TABLE IF NOT EXISTS "
			+ catalogTableName
			+ " (_id integer primary key autoincrement, column_id integer, cat_id integer,"
			+ " json_content char, timeStamp integer ,"
			+ KEY_PUBLIC_USER_ID
			+ " text,"
			+ "rowdata_type integer,unread_count integer,customtype integer,purview_type integer);";
	// 二级消息列表(消息列表)
	public static final String infoTableName = "tb_info";
	private static final String infoSQL = "CREATE TABLE IF NOT EXISTS "
			+ infoTableName
			+ " (_id integer primary key autoincrement, cat_id integer, msg_id integer  ,"
			+ KEY_PUBLIC_USER_ID + " text,"
			+ " json_content text, timeStamp integer ,rowdata_type integer);";
	// 宣传位数据表
	public static final String adZoneTableName = "tb_adzone";
	private static final String adzoneSQL = "CREATE TABLE IF NOT EXISTS "
			+ adZoneTableName
			+ " (_id integer primary key autoincrement, ad_id integer, ad_type char,"
			+ " title text, pic_url text, click_url text, timeStamp integer,zone_id integer,action_id integer);";

	// 简历数据表
	public static final String resumeTableName = "tb_resume";
	private static final String resumeSQL = "CREATE TABLE IF NOT EXISTS "
			+ resumeTableName
			+ " (_id integer primary key autoincrement, resume_id integer,"
			+ KEY_PUBLIC_USER_ID
			+ " text,"
			+ "resume_time text,content_id integer,json_content text, completed_degree integer,"
			+ "timeStamp integer,rowdata_type integer,submit_state integer );";

	// 简历简单信息数据表
	public static final String simpleResumeTableName = "tb_simpleresume";
	private static final String simpleresumeSQL = "CREATE TABLE IF NOT EXISTS "
			+ simpleResumeTableName
			+ " (_id integer primary key autoincrement, resume_id integer,"
			+ "create_time text,modify_time text,json_content text, completed_degree integer,"
			+ "timeStamp integer,rowdata_type integer,submit_state integer ,selected_status integer ,"
			+ KEY_PUBLIC_USER_ID + " text," + " resume_name text );";

	// service接收数据表
	public static final String serviceDataTableName = "tb_servicedata";
	private static final String serviceDataSQL = "CREATE TABLE IF NOT EXISTS "
			+ serviceDataTableName
			+ " (_id integer primary key autoincrement, type text, json_content text,"
			+ KEY_PUBLIC_USER_ID + " text,"
			+ " timeStamp integer,state_id integer);";
	// notification 正在显示的数据表
	public static final String notificationTableName = "tb_notification";
	private static final String notificationSQL = "CREATE TABLE IF NOT EXISTS "
			+ notificationTableName
			+ " (_id integer primary key autoincrement, " + KEY_PUBLIC_USER_ID
			+ " text," + "index_id integer, type integer);";

	public static final String hotLabelTableName = "tb_hotlabel";
	private static final String hotLabelSQL = "CREATE TABLE IF NOT EXISTS "
			+ hotLabelTableName
			+ " (_id integer primary key autoincrement, label_id integer, label text, pinyin text, user_id text);";

	/*********************************************************************************************/
	/* catalogTableName 表的数据类型，键名：KEY_ROWDATA_TYPE */

	// 版块数据的保存类型
	// 默认类型
	public static final int CATALOG_TYPE_DEFAULT_CATALOG = 0;
	// 消息首页
	public static final int CATALOG_TYPE_INFO_CATALOG = 1;
	// 号外二级栏目
	public static final int CATALOG_TYPE_EXTRA_COLUMN = 2;
	// 号外二级版块
	public static final int CATALOG_TYPE_EXTRA_CATALOG = 3;
	// 号外订阅管理栏目
	public static final int CATALOG_TYPE_CUSTOMMANAGER_COLUMN = 4;
	// 号外订阅管理版块
	public static final int CATALOG_TYPE_CUSTOMMANAGER_CATALOG = 5;
	/*********************************************************************************************/
	/* infoTableName 表的数据类型，键名：KEY_ROWDATA_TYPE */
	// 默认类型
	public static final int INFO_TYPE_DEFAULT = 0;
	// 号外首页版块
	public static final int INFO_TYPE_EXTRAHOME = 1;

	/*********************************************************************************************/
	/* adZoneTableName 表的数据类型 ，键名：KEY_ZONE_ID */
	// 广告位 类型
	// 消息模块广告位
	public static final int ADZONE_TYPE_INFO_CATALOG = 1;
	// 牵牛模块广告位
	public static final int ADZONE_TYPE_REWARD_CATALOG = 2;
	// 登录广告位
	public static final int ADZONE_TYPE_LOGIN = 3;
	//号外广告位
	public static final int ADZONE_TYPE_MIX = 4;
	//资讯广告位
	public static final int ADZONE_TYPE_NEWS = 5;	
	//号外广告位
	public static final int ADZONE_TYPE_FAMOUS_MAN = 6;
	//资讯广告位
	public static final int ADZONE_TYPE_FAMOUS_COMPANY = 7;	
	
	/*********************************************************************************************/
	// 简历表数据
	// 数据库列名
	// 简历Id，每份简历只有一个唯一ID，新增从0开始负增长；需先取得最小的id，然后负增长
	public static final String KEY_RESUME_RESUME_ID = "resume_id";
	// 简历更新时间，
	public static final String KEY_RESUME_RESUME_TIME = "resume_time";
	// 数据段id，新增从0开始负增长；需先取得最小的id，然后负增长
	public static final String KEY_RESUME_RESUME_CONTENT_ID = "content_id";
	// 数据段json数据
	public static final String KEY_RESUME_JSON_CONTENT = "json_content";
	// 数据段数据是否完整, 0:不完整 ；1：完整
	public static final String KEY_RESUME_COMPLETED_DEGREE = "completed_degree";
	// 本地修改时间
	public static final String KEY_RESUME_TIMESTAMP = "timeStamp";
	// 数据段对应的数据类型
	public static final String KEY_RESUME_ROWDATA_TYPE = "rowdata_type";
	// 该段数据提交状态，0，未提交，1：已提交
	public static final String KEY_RESUME_SUBMIT_STATE = "submit_state";

	/* resumeTableName 表的行数据类型 ，键名：KEY_RESUME_ROWDATA_TYPE */
	// 完整简历行，只有简历id
	public static final int RESUME_TYPE_RESUME = 0;
	// 简历基本信息
	public static final int RESUME_TYPE_BASE = 1;
	// 简历工作经历
	public static final int RESUME_TYPE_JOBEXP = 2;
	// 简历项目经历
	public static final int RESUME_TYPE_PROJECTEXP = 3;
	// 简历教育经历
	public static final int RESUME_TYPE_EDUCATIONEXP = 4;
	// 简历语言技能
	public static final int RESUME_TYPE_LANGUAGEEXP = 5;
	// 快速简历
	public static final int RESUME_TYPE_QUICKITEM = 6;

	/* resumeTableName 表的行数据类 KEY_RESUME_SUBMIT_STATE 对应的状态 */
	// 未提交
	public static final int RESUME_STATE_UNSUBMITED = 0;
	// 已提交
	public static final int RESUME_STATE_SUBMITED = 1;

	/* resumeTableName 表的行数据类 KEY_RESUME_COMPLETED_DEGREE 对应的状态 */
	// 不完整
	public static final int RESUME_STATE_UNCOMPLETEED = 0;
	// 完整
	public static final int RESUME_STATE_COMPLETEED = 1;

	/*********************************************************************************************/
	// 简历简单信息数据表
	// 数据库列名
	// 简历Id，每份简历只有一个唯一ID，新增从0开始负增长；需先取得最小的id，然后负增长
	public static final String KEY_SIMPLERESUME_RESUME_ID = "resume_id";

	// 简历创建时间
	public static final String KEY_SIMPLERESUME_CREATE_TIME = "create_time";
	// 简历修改时间
	public static final String KEY_SIMPLERESUME_MODIFY_TIME = "modify_time";
	// 数据段json数据
	public static final String KEY_SIMPLERESUME_JSON_CONTENT = "json_content";
	// 数据段数据完成度
	public static final String KEY_SIMPLERESUME_COMPLETED_DEGREE = "completed_degree";
	// 本地修改时间
	public static final String KEY_SIMPLERESUME_TIMESTAMP = "timeStamp";
	// 数据段对应的数据类型,暂无用
	public static final String KEY_SIMPLERESUME_ROWDATA_TYPE = "rowdata_type";
	// 该段数据提交状态，0，未提交，1：已提交 ；暂无用,
	public static final String KEY_SIMPLERESUME_SUBMIT_STATE = "submit_state";
	// 是否为默认简历
	public static final String KEY_SIMPLERESUME_SELECTED_STATUS = "selected_status";
	// 简历名称
	public static final String KEY_SIMPLERESUME_RESUME_NAME = "resume_name";

	/*********************************************************************************************/
	// service数据表
	// 数据库列名
	// 数据type
	public static final String KEY_SERVICEDATA_TYPE = "type";
	// JSON数据体
	public static final String KEY_SERVICEDATA_JSONDATA = "json_content";
	// 简历修改时间
	public static final String KEY_SERVICEDATA_TIMESTAMP = "timeStamp";
	// 当前数据处理状态
	public static final String KEY_SERVICEDATA_STATE_ID = "state_id";

	/* service数据表的行数据类型 ，键名：KEY_SERVICEDATA_TYPE_ID */
	// 推送栏消息, 所有用户都能收到;
	public static final String SERVICEDATA_TYPE_NOTIFICATION = "notification";
	// 个人消息;
	public static final String SERVICEDATA_TYPE_PERSONAL = "personalmsg";
	// 消息板块新消息通知;
	public static final String SERVICEDATA_TYPE_NEWATMESSAGE = "newatmessage";
	// 版本更新通知;
	public static final String SERVICEDATA_TYPE_NEWVERSION = "newvesion";

	/* service数据表的行数据类型 ，键名：KEY_SERVICEDATA_STATE_ID */
	// 未读
	public static final int SERVICEDATA_STATE_UNREAD = 0;
	// 已读
	public static final int SERVICEDATA_STATE_READ = 1;

	/*********************************************************************************************/

	// 热门标签 数据库列名

	public static final String KEY_HOT_LABEL_ID = "label_id";

	public static final String KEY_HOT_LABEL_LABEL = "label";

	public static final String KEY_HOT_LABEL_PINYIN = "pinyin";

	public static final String KEY_HOT_LABEL_USER_ID = "user_id";

	/*********************************************************************************************/
	// catalog列表列号
	public static final int CATALOG_ID = 0;
	public static final int CATALOG_COLUMN_ID = 1;
	public static final int CATALOG_CAT_ID = 2;
	public static final int CATALOG_JSON_CONTENT = 3;
	public static final int CATALOG_TIMESTAMP = 4;
	public static final int CATALOG_DATA_TYPE = 5;
	// info列表列号
	public final static int INFO_ID = 0;
	public final static int INFO_CAT_ID = 1;
	public final static int INFO_MSG_ID = 2;
	public final static int INFO_JSON_CONTENT = 3;
	public final static int INFO_TIMESTAMP = 4;

	// 数据库列名
	// 专栏id
	public static final String KEY_COLUMN_ID = "column_id";
	// 版块id
	public static final String KEY_CAT_ID = "cat_id";
	// 消息id
	public static final String KEY_MSG_ID = "msg_id";
	// json数据
	public static final String KEY_JSON_CONTENT = "json_content";
	// 修改时间戳
	public static final String KEY_TIMESTAMP = "timeStamp";
	// 保存数据类型
	public static final String KEY_ROWDATA_TYPE = "rowdata_type";
	// 未读消息数量
	public static final String KEY_UNREAD_COUNT = "unread_count";
	// 订阅类型：0:default不可更改,1:已订阅,2:未订阅
	public static final String KEY_CUSTOM_TYPE = "customtype";
	// 消息类型：1=>官方消息,2=>个人消息(需要登陆),3=>号外资讯,4=>推送消息
	public static final String KEY_PREVIEW_TYPE = "purview_type";
	/*********************************************************************************************/
	// 宣传位数据表列名
	public static final String KEY_AD_ID = "ad_id";
	public static final String KEY_ZONE_ID = "zone_id";
	public static final String KEY_AD_TYPE = "ad_type";
	public static final String KEY_PIC_URL = "pic_url";
	public static final String KEY_CLICK_URL = "click_url";
	public static final String KEY_AD_TITLE = "title";
	public static final String KEY_AD_ACTIONID = "action_id";

	/*********************************************************************************************/
	// 全局版本号表
	public static final String KEY_GLOBALDATA_VERSION = "globaldata_version";

	public static final String versionTableName = "tb_version";
	public static final String versionSQL = "CREATE TABLE IF NOT EXISTS "
			+ versionTableName + " (_id integer primary key autoincrement, "
			+ KEY_GLOBALDATA_VERSION + " varchar);";

	// 全局数据表
	// 数据类型
	public static final String KEY_GLOBALDATA_TYPE = "globaldata_type";
	// 数据ID
	public static final String KEY_GLOBALDATA_ID = "globaldata_id";
	// 数据名
	public static final String KEY_GLOBALDATA_NAME = "globaldata_name";
	// 数据名拼音
	public static final String KEY_GLOBALDATA_NAMEPINYIN = "globaldata_namepinyin";
	// 是否有父类
	public static final String KEY_GLOBALDATA_HAVINGPARENT = "globaldata_havingparent";
	// 父类ID
	public static final String KEY_GLOBALDATA_PARENTID = "globaldata_parentid";
	// 是否有子类
	public static final String KEY_GLOBALDATA_HAVINGSUBCLASS = "globaldata_havingsubclass";
	// 数据属于第几级
	public static final String KEY_GLOBALDATA_LEVEL = "globaldata_level";

	public static final String globaldataTableName = "tb_globaldata";
	public static final String globaldataSQL = "CREATE TABLE IF NOT EXISTS "
			+ globaldataTableName + " (_id integer primary key autoincrement, "
			+ KEY_GLOBALDATA_TYPE + " integer, " + KEY_GLOBALDATA_ID
			+ " varchar, " + KEY_GLOBALDATA_NAME + " varchar, "
			+ KEY_GLOBALDATA_NAMEPINYIN + " varchar, "
			+ KEY_GLOBALDATA_HAVINGPARENT + " boolean, "
			+ KEY_GLOBALDATA_PARENTID + " varchar, "
			+ KEY_GLOBALDATA_HAVINGSUBCLASS + " boolean, "
			+ KEY_GLOBALDATA_LEVEL + " integer" + ");";

	// 城市
	public static final int GLOBALDATA_TYPE_CITY = 0;
	// 学历
	public static final int GLOBALDATA_TYPE_EDUCATION = 1;
	// 求职状态
	public static final int GLOBALDATA_TYPE_JOBSTATUS = 2;
	// 年薪
	public static final int GLOBALDATA_TYPE_SALARY = 3;
	// 到岗时间
	public static final int GLOBALDATA_TYPE_ARRIVETIME = 4;
	// 语言技能
	public static final int GLOBALDATA_TYPE_LANGUAGE = 5;
	// 悬赏方式
	public static final int GLOBALDATA_TYPE_REWARDWAY = 6;
	// 悬赏周期
	public static final int GLOBALDATA_TYPE_REWARDCYCLE = 7;
	// 行业
	public static final int GLOBALDATA_TYPE_JOBINDUSTRY = 8;
	// 专业
	public static final int GLOBALDATA_TYPE_SPECIALTY = 9;
	// 语言掌握程度
	public static final int GLOBALDATA_TYPE_LANGUAGEMASTERY = 10;
	// 语言读写能力
	public static final int GLOBALDATA_TYPE_LANGUAGLITERACY = 11;
	// 语言听说能力
	public static final int GLOBALDATA_TYPE_LANGUAGSPEAKING = 12;
	// 热点职位
	public static final int GLOBALDATA_TYPE_HOTKEYWORD = 13;
	// 热点城市
	public static final int GLOBALDATA_TYPE_HOTCITY = 14;
	// 推荐理由
	public static final int GLOBALDATA_TYPE_RECOMMENDREASON = 15;
	// 图片基础地址
	public static final int GLOBALDATA_TYPE_PICTUREBASEADDRESS = 16;
	// 图片提交地址
	public static final int GLOBALDATA_TYPE_PICTURESERVERADDRESS = 17;
	// 我的记录状态
	public static final int GLOBALDATA_TYPE_VERIFYSTATUS = 18;
	// 悬赏任务发布状态
	public static final int GLOBALDATA_TYPE_PUBLISHSTATUS = 19;
	// 接受任务模式
	public static final int GLOBALDATA_TYPE_ACCEPTTASKMODE = 20;
	// 职能分类
	public static final int GLOBALDATA_TYPE_JOBFUNCTION = 21;
	// 付款标识
	public static final int GLOBALDATA_TYPE_PAYFLAG = 22;

	/*********************************************************************************************/
	// 悬赏任务搜索历史
	// 用户id
	public static final String KEY_REWARDSEANRCHHISTORY_USERID = "rewardsearchhistory_userid";
	// 关键字
	public static final String KEY_REWARDSEANRCHHISTORY_KEWWORD = "rewardsearchhistory_keyword";
	// 城市
	public static final String KEY_REWARDSEANRCHHISTORY_CITY = "rewardsearchhistory_city";
	// 行业
	public static final String KEY_REWARDSEANRCHHISTORY_INDUSTRY = "rewardsearchhistory_industry";
	// 父行业
	public static final String KEY_REWARDSEANRCHHISTORY_PARENTINDUSTRY =
			"rewardsearchhistory_parentindustry";

	public static final String rewardsearchhistoryTableName = "tb_rewardsearchhistory";
	public static final String rewardsearchhistorySQL = "CREATE TABLE IF NOT EXISTS "
			+ rewardsearchhistoryTableName
			+ " (_id integer primary key autoincrement, "
			+ KEY_REWARDSEANRCHHISTORY_USERID
			+ " varchar, "
			+ KEY_REWARDSEANRCHHISTORY_KEWWORD
			+ " varchar, "
			+ KEY_REWARDSEANRCHHISTORY_CITY
			+ " varchar, "
			+ KEY_REWARDSEANRCHHISTORY_INDUSTRY + " varchar," 
			+ KEY_REWARDSEANRCHHISTORY_PARENTINDUSTRY + " varchar" 
			+");";
	/************* 悬赏任务表 **************************************************************************/
	// 用户ID
	public static final String KEY_REWARD_USERID = "reward_userid";
	// 用户选择要获取的悬赏任务类型
	public static final String KEY_REWARD_USERREQUEST_TYPE = "reward_userrequest_type";
	// 悬赏任务ID
	public static final String KEY_REWARD_TASKID = "reward_taskid";
	// 悬赏任务类型
	public static final String KEY_REWARD_TASKTYPE = "reward_tasktype";
	// 悬赏任务属于的行业
	public static final String KEY_REWARD_TASKCATEGORYID = "reward_taskcategoryid";
	// 悬赏任务名称
	public static final String KEY_REWARD_TASKTITLE = "reward_tasktitle";
	// 悬赏任务适用的城市
	public static final String KEY_REWARD_TASKCITY = "reward_taskcity";
	// 具体地址
	public static final String KEY_REWARD_TASKLOCATION = "reward_tasklocation";
	// 悬赏任务详情的url(html5)
	public static final String KEY_REWARD_TASKURL = "reward_taskurl";
	// 悬赏任务关键字
	public static final String KEY_REWARD_TASKKEYWORD = "reward_taskkeyword";
	// 悬赏任务奖金
	public static final String KEY_REWARD_TASKBONUS = "reward_taskbonus";
	// 公司名称
	public static final String KEY_REWARD_COMPANYNAME = "reward_companyname";
	// 公司url
	public static final String KEY_REWARD_COMPANYURL = "reward_companyurl";
	// 公司id
	public static final String KEY_REWARD_COMPANYID = "reward_companyid";
	// 公司图片url
	public static final String KEY_REWARD_COMPANIMGYURL = "reward_companyimgurl";
	// 公司收藏状态
	public static final String KEY_REWARD_COMPANYCOLLECTION = "reward_companycollection";
	// 悬赏任务状态
	public static final String KEY_REWARD_TASKSTATUS = "reward_taskstatus";
	// 发布者类型
	public static final String KEY_REWARD_PUBLISHERTYPE = "reward_publishertype";
	// 发布者名称
	public static final String KEY_REWARD_PUBLISHERNAME = "reward_publishername";
	// 发布时间
	public static final String KEY_REWARD_PUBLISHERDATE = "reward_publisherdate";
	// 发布截止时间
	public static final String KEY_REWARD_PUBLISHERENDDATE = "reward_publisherenddate";
	// 接受标识
	public static final String KEY_REWARD_ACTION1 = "reward_action1";
	// 收藏标识
	public static final String KEY_REWARD_ACTION3 = "reward_action3";
	// 已读标识
	public static final String KEY_REWARD_ACTION5 = "reward_action5";
	// 审核状态
	public static final String KEY_REWARD_VERIFYSTATUS = "reward_verifystatus";
	// 简历名称
	public static final String KEY_REWARD_RESUMENAME = "reward_resumename";
	// 简历来源
	public static final String KEY_REWARD_APPLYTYPE = "reward_apply_type";
	// 请求的数据类型(个人:2, 公司:1, 混合:0)
	public static final String KEY_REWARD_REQUESTDATATYPE = "reward_requestdatatype";
	// 剩余天数
	public static final String KEY_REWARD_VALIDDATE = "reward_validdate";
	// 关注数
	public static final String KEY_REWARD_CONCERNNUM = "reward_concernnum";
	// 行业类型(选项卡)
	public static final String KEY_REWARD_TABTYPE = "reward_tabtype";

	public static final String rewardTableName = "tb_reward";
	public static final String rewardSQL = "CREATE TABLE IF NOT EXISTS "
			+ rewardTableName + " (_id integer primary key autoincrement, "
			+ KEY_REWARD_USERID + " varchar, " + KEY_REWARD_USERREQUEST_TYPE
			+ " varchar, " + KEY_REWARD_TASKID + " varchar, "
			+ KEY_REWARD_TASKTYPE + " varchar, " + KEY_REWARD_TASKCATEGORYID
			+ " varchar, " + KEY_REWARD_TASKTITLE + " varchar, "
			+ KEY_REWARD_TASKCITY + " varchar, " + KEY_REWARD_TASKLOCATION
			+ " varchar, " + KEY_REWARD_TASKURL + " varchar, "
			+ KEY_REWARD_TASKKEYWORD + " varchar, " + KEY_REWARD_TASKBONUS
			+ " varchar, " + KEY_REWARD_COMPANYNAME + " varchar, "
			+ KEY_REWARD_COMPANYURL + " varchar, " + KEY_REWARD_COMPANYID
			+ " varchar, " + KEY_REWARD_COMPANIMGYURL + " varchar, "
			+ KEY_REWARD_COMPANYCOLLECTION + " varchar, "
			+ KEY_REWARD_TASKSTATUS + " varchar, " + KEY_REWARD_PUBLISHERTYPE
			+ " varchar, " + KEY_REWARD_PUBLISHERNAME + " varchar, "
			+ KEY_REWARD_PUBLISHERDATE + " varchar, "
			+ KEY_REWARD_PUBLISHERENDDATE + " varchar, " + KEY_REWARD_ACTION1
			+ " varchar, " + KEY_REWARD_ACTION3 + " varchar, "
			+ KEY_REWARD_ACTION5 + " varchar, " + KEY_REWARD_VERIFYSTATUS
			+ " varchar, " + KEY_REWARD_RESUMENAME + " varchar, "
			+ KEY_REWARD_APPLYTYPE + " varchar, " + KEY_REWARD_REQUESTDATATYPE
			+ " integer, " + KEY_REWARD_VALIDDATE + " varchar, "
			+ KEY_REWARD_CONCERNNUM + " varchar" + ");";
	
	// 悬赏任务(选项卡)数据列表
	public static final String rewardtabhostTableName = "tb_rewardtabhost";
	public static final String rewardtabhostSQL = "CREATE TABLE IF NOT EXISTS "
			+ rewardtabhostTableName + " (_id integer primary key autoincrement, "
			+ KEY_REWARD_USERID + " varchar, " + KEY_REWARD_USERREQUEST_TYPE
			+ " varchar, " + KEY_REWARD_TASKID + " varchar, "
			+ KEY_REWARD_TASKTYPE + " varchar, " + KEY_REWARD_TASKCATEGORYID
			+ " varchar, " + KEY_REWARD_TASKTITLE + " varchar, "
			+ KEY_REWARD_TASKCITY + " varchar, " + KEY_REWARD_TASKLOCATION
			+ " varchar, " + KEY_REWARD_TASKURL + " varchar, "
			+ KEY_REWARD_TASKKEYWORD + " varchar, " + KEY_REWARD_TASKBONUS
			+ " varchar, " + KEY_REWARD_COMPANYNAME + " varchar, "
			+ KEY_REWARD_COMPANYURL + " varchar, " + KEY_REWARD_COMPANYID
			+ " varchar, " + KEY_REWARD_COMPANIMGYURL + " varchar, "
			+ KEY_REWARD_COMPANYCOLLECTION + " varchar, "
			+ KEY_REWARD_TASKSTATUS + " varchar, " + KEY_REWARD_PUBLISHERTYPE
			+ " varchar, " + KEY_REWARD_PUBLISHERNAME + " varchar, "
			+ KEY_REWARD_PUBLISHERDATE + " varchar, "
			+ KEY_REWARD_PUBLISHERENDDATE + " varchar, " + KEY_REWARD_ACTION1
			+ " varchar, " + KEY_REWARD_ACTION3 + " varchar, "
			+ KEY_REWARD_ACTION5 + " varchar, " + KEY_REWARD_VERIFYSTATUS
			+ " varchar, " + KEY_REWARD_RESUMENAME + " varchar, "
			+ KEY_REWARD_APPLYTYPE + " varchar, " + KEY_REWARD_REQUESTDATATYPE
			+ " integer, " + KEY_REWARD_VALIDDATE + " varchar, "
			+ KEY_REWARD_CONCERNNUM + " varchar,"
			+ KEY_REWARD_TABTYPE + " integer"
			+ ");";
	/************* 悬赏任务已读表 **************************************************************************/
	// 已读悬赏任务ID
	public static final String KEY_REWARDREAD_TASKID = "rewardread_taskid";

	public static final String rewardreadTableName = "tb_rewardread";
	public static final String rewardreadSQL = "CREATE TABLE IF NOT EXISTS "
			+ rewardreadTableName + " (_id integer primary key autoincrement, "
			+ KEY_REWARDREAD_TASKID + " varchar);";
	/*************** 我的账号数据 ******************************************************************************/
	// 用户id
	public static final String KEY_MYACCOUNT_USERID = "myaccount_userid";
	// 用户名
	public static final String KEY_MYACCOUNT_USERNAME = "myaccount_username";
	// 姓名
	public static final String KEY_MYACCOUNT_DISPLAYNAME = "myaccount_displayname";
	// 用户email
	public static final String KEY_MYACCOUNT_EMAIL = "myaccount_email";
	// email验证标识
	public static final String KEY_MYACCOUNT_EMAILVERIFIED = "myaccount_emailverified";
	// 用户手机
	public static final String KEY_MYACCOUNT_PHONE = "myaccount_phone";
	// 用户手机验证标识
	public static final String KEY_MYACCOUNT_PHONEVERIFIED = "myaccount_phoneverified";
	// 性别
	public static final String KEY_MYACCOUNT_GENDER = "myaccount_gender";
	// 积分
	public static final String KEY_MYACCOUNT_CREDIT = "myaccount_credit";
	// 居住地
	public static final String KEY_MYACCOUNT_DOMICILE = "myaccount_domicile";
	// 行业
	public static final String KEY_MYACCOUNT_INDUSTRY = "myaccount_industry";
	// 出生日期
	public static final String KEY_MYACCOUNT_BIRTHDAY = "myaccount_birthday";
	// 参加工作日期
	public static final String KEY_MYACCOUNT_JOINWORKDATE = "myaccount_joinworkdate";
	// 学历ID
	public static final String KEY_MYACCOUNT_EDUCATIONID = "myaccount_educationid";
	// 自我描述
	public static final String KEY_MYACCOUNT_MEMO = "myaccount_memo";
	// 收藏的任务数
	public static final String KEY_MYACCOUNT_FAVCOUNT = "myaccount_favcount";
	// alipay的账号
	public static final String KEY_MYACCOUNT_ALIPAYACC = "myaccount_alipayacc";
	// 余额
	public static final String KEY_MYACCOUNT_BONUSSUM = "myaccount_bonussum";
	// 金钱-支出
	public static final String KEY_MYACCOUNT_BONUSSUMPAY = "myaccount_bonussumpay";
	// 金钱-收入
	public static final String KEY_MYACCOUNT_BONUSSUMGET = "myaccount_bonussumget";
	// 金钱交易记录条数
	public static final String KEY_MYACCOUNT_BONUSCOUNT = "myaccount_bonuscount";
	// 用户头像
	public static final String KEY_MYACCOUNT_AVATAR = "myaccount_avatar";
	// 个人资料完善标识
	public static final String KEY_MYACCOUNT_COMPLETEFLAG = "myaccount_completeflag";

	// 我的账号数据表名
	public static final String myaccountTableName = "tb_myaccount";
	public static final String myaccountSQL = "CREATE TABLE IF NOT EXISTS "
			+ myaccountTableName + " (_id integer primary key autoincrement, "
			+ KEY_MYACCOUNT_USERID + " varchar, " + KEY_MYACCOUNT_USERNAME
			+ " varchar, " + KEY_MYACCOUNT_DISPLAYNAME + " varchar, "
			+ KEY_MYACCOUNT_EMAIL + " varchar, " + KEY_MYACCOUNT_EMAILVERIFIED
			+ " varchar, " + KEY_MYACCOUNT_PHONE + " varchar, "
			+ KEY_MYACCOUNT_PHONEVERIFIED + " varchar, " + KEY_MYACCOUNT_GENDER
			+ " varchar, " + KEY_MYACCOUNT_CREDIT + " varchar, "
			+ KEY_MYACCOUNT_DOMICILE + " varchar, " + KEY_MYACCOUNT_INDUSTRY
			+ " varchar, " + KEY_MYACCOUNT_BIRTHDAY + " varchar, "
			+ KEY_MYACCOUNT_JOINWORKDATE + " varchar, "
			+ KEY_MYACCOUNT_EDUCATIONID + " varchar, " + KEY_MYACCOUNT_MEMO
			+ " varchar, " + KEY_MYACCOUNT_FAVCOUNT + " varchar, "
			+ KEY_MYACCOUNT_ALIPAYACC + " varchar, " + KEY_MYACCOUNT_BONUSSUM
			+ " varchar, " + KEY_MYACCOUNT_BONUSSUMPAY + " varchar, "
			+ KEY_MYACCOUNT_BONUSSUMGET + " varchar, "
			+ KEY_MYACCOUNT_BONUSCOUNT + " varchar, "
			+ KEY_MYACCOUNT_AVATAR+ " varchar, "
			+ KEY_MYACCOUNT_COMPLETEFLAG
			+ " varchar);";
	/************* 交易记录表 **************************************************************************/
	// 用户id
	public static final String KEY_TRANSATIONRECORD_USERID = "transactionrecord_userid";
	// 交易记录id
	public static final String KEY_TRANSATIONRECORD_RECORDID = "transactionrecord_recordid";
	// 消费金额
	public static final String KEY_TRANSATIONRECORD_MONEY = "transactionrecord_money";
	// 消费明细
	public static final String KEY_TRANSATIONRECORD_DTETAIL = "transactionrecord_dtetail";
	// 修改时间时间
	public static final String KEY_TRANSATIONRECORD_MODIFIEDTIME = "transactionrecord_modifiedtime";

	public static final String transactionrecordTableName = "tb_transactionrecord";
	public static final String transactionrecordSQL = "CREATE TABLE IF NOT EXISTS "
			+ transactionrecordTableName
			+ " (_id integer primary key autoincrement, "
			+ KEY_TRANSATIONRECORD_USERID
			+ " varchar, "
			+ KEY_TRANSATIONRECORD_RECORDID
			+ " varchar, "
			+ KEY_TRANSATIONRECORD_MONEY
			+ " varchar, "
			+ KEY_TRANSATIONRECORD_DTETAIL
			+ " varchar, "
			+ KEY_TRANSATIONRECORD_MODIFIEDTIME + " varchar);";
	/************ 活动数据表 *********************************************************************************/
	// 活动数据表列名
	public static final String KEY_CAMPAIGN_ID = "camp_id";
	public static final String KEY_CAMPAIGN_TYPE_ID = "type_id";
	public static final String KEY_CAMPAIGN_TITLE = "title";
	public static final String KEY_CAMPAIGN_DESCRIPTION = "description";
	public static final String KEY_CAMPAIGN_PICURL = "picUrl";
	public static final String KEY_CAMPAIGN_DEATAILURL = "detailUrl";
	public static final String KEY_CAMPAIGN_START_DATE = "startDate";
	public static final String KEY_CAMPAIGN_END_DATE = "endtDate";
	public static final String KEY_CAMPAIGN_MODIFY_DATE = "modifieDate";
	public static final String KEY_CAMPAIGN_JSONCONTENT = "json_content";
	public static final String KEY_CAMPAIGN_RECEIVE_TIMESTAMP = "receivetime";

	// 活动数据表
	public static final String campaignTableName = "tb_campaign";
	public static final String campaignSQL = "CREATE TABLE IF NOT EXISTS "
			+ campaignTableName
			+ " (_id integer primary key autoincrement, camp_id integer, type_id integer ,"
			+ "title text,description text,picUrl text,detailUrl text,startDate date,endtDate date,modifieDate date, "
			+ KEY_PUBLIC_USER_ID + " text,"
			+ " json_content text, receivetime integer);";
	/******* 号外首页dashboard数据 *******************************************************************************************/
	/* 号外首页导航数据，暂无用 */
	// dashboard数据表列名
	private long entityId;
	private String title;
	private String logoUrl;
	private int actionTypeId;
	private int actionId;
	private String modifieDate;
	private long receiveTimestamp;

	public static final String KEY_DASHBOARD_ID = "board_id";
	public static final String KEY_DASHBOARD_TITLE = "title";
	public static final String KEY_DASHBOARD_LOGOURL = "logoUrl";
	public static final String KEY_DASHBOARD_ACTIONTYPE_ID = "act_type";
	public static final String KEY_DASHBOARD_ACTION_ID = "action";
	public static final String KEY_DASHBOARD_MODIFY_DATE = "modifieDate";
	public static final String KEY_DASHBOARD_JSONCONTENT = "json_content";
	public static final String KEY_DASHBOARD_RECEIVE_TIMESTAMP = "receivetime";

	// dashboard数据表
	public static final String dashboardTableName = "tb_dashboard";
	public static final String dashboardSQL = "CREATE TABLE IF NOT EXISTS "
			+ dashboardTableName
			+ " (_id integer primary key autoincrement, board_id integer, "
			+ "title text,logoUrl text,act_type integer,action integer,modifieDate date,"
			+ KEY_PUBLIC_USER_ID + " text,"
			+ " json_content text, receivetime integer);";
	/**************************************************************************************************/
	// 行业圈内人 名企招聘 缓存表
	public static final String INSIDERSANDCOMPANYTABLENAME = "tb_insidersAndCompany";
	public static final String ICID = "id";
	public static final String ICNAME = "name";
	public static final String ICTITLE = "TITLE";
	public static final String ICPICTURE = "picture";
	public static final String ICTASKCOUNT = "taskcount";
	public static final String ICAUTHENTICATE = "authenticate";
	public static final String ICMODIFIED = "modified";
	public static final String ICATTENTION_COUNT = "attention_count";
	public static final String ICTAGS = "tags";
	public static final String ICTYPE = "type";
	public static final String ICMTIME = "mtime";// 更新时间

	public static final String INSIDERSANDCOMPANYSQL = "CREATE TABLE IF NOT EXISTS "
			+ INSIDERSANDCOMPANYTABLENAME
			+ " (_id integer primary key autoincrement, "
			+ ICID
			+ " text, "
			+ ICNAME
			+ " text, "
			+ ICTITLE
			+ " text, "
			+ ICTASKCOUNT
			+ " text, "
			+ ICPICTURE
			+ " text, "
			+ ICAUTHENTICATE
			+ " text, "
			+ ICMODIFIED
			+ " text, "
			+ ICATTENTION_COUNT
			+ " text, "
			+ ICTAGS
			+ " text, "
			+ ICTYPE
			+ " integer, "
			+ ICMTIME
			+ " text);";

	/**************************************************************************************************/
	private static DBUtils dbUtils = null;

	public synchronized static DBUtils getInstance(Context c) {
		if (dbUtils == null) {
			dbUtils = new DBUtils(c);
		}
		return dbUtils;
	}

	private DBUtils(Context context) {
		this.context = context;
		this.appcontext = (AppContext) context.getApplicationContext();
		Map<String, String> sqls = new HashMap<String, String>();
		sqls.put(catalogTableName, catalogSQL);
		sqls.put(infoTableName, infoSQL);
		sqls.put(adZoneTableName, adzoneSQL);
		sqls.put(resumeTableName, resumeSQL);
		sqls.put(simpleResumeTableName, simpleresumeSQL);
		sqls.put(hotLabelTableName, hotLabelSQL);
		sqls.put(serviceDataTableName, serviceDataSQL);
		sqls.put(campaignTableName, campaignSQL);
		// sqls.put(dashboardTableName, dashboardSQL);

		// 新建全局数据相关表
		sqls.put(versionTableName, versionSQL);
		sqls.put(globaldataTableName, globaldataSQL);

		// 新建悬赏任务搜索历史表
		sqls.put(rewardsearchhistoryTableName, rewardsearchhistorySQL);
		// 新建悬赏任务数据表
		sqls.put(rewardTableName, rewardSQL);
		// 新建悬赏任务(选项卡)数据表
		sqls.put(rewardtabhostTableName, rewardtabhostSQL);
		// 新建已读悬赏任务数据表
		sqls.put(rewardreadTableName, rewardreadSQL);
		// 新建我的账号数据表
		sqls.put(myaccountTableName, myaccountSQL);
		// 新建交易记录表
		sqls.put(transactionrecordTableName, transactionrecordSQL);

		// 行业圈内人 名企招聘列表
		sqls.put(INSIDERSANDCOMPANYTABLENAME, INSIDERSANDCOMPANYSQL);

		DBHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION, sqls);
		openDb();

		// 判断数据库是否有全局数据
		if (DMSharedPreferencesUtil.VALUE_FLG_HAVINGGLOBALDB != DMSharedPreferencesUtil
				.getSharePreInt(context,
						DMSharedPreferencesUtil.DM_HOTLABEL_DB,
						DMSharedPreferencesUtil.FIELD_FLG_GLOBALDB)) {
			// 备份数据库
			try {
				bakDataBase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public DBUtils openDb() throws SQLException {
		logger.i("DBUtils open");
		try {
			db = DBHelper.getWritableDatabase();
		} catch (Exception e) {
			logger.e("数据库错误！DBUtils open fail:" + e);
			Toast.makeText(context, "数据库错误！", Toast.LENGTH_LONG).show();
		}
		return this;
	}

	public boolean deleteDb() throws SQLException {

		return DBHelper.deleteDatabase();
	}

	public void closeDb() {

		synchronized (db) {
			if (db != null)
				db.close();
			db = null;
		}

	}

	public void close() {
		/* synchronized (DBHelper) */{
			/*
			 * closeDb(); DBHelper.close(); dbUtils = null;
			 */
		}

	}

	public long save(String tableName, ContentValues initialValues) {
		long i = -1;
		try {
			i = db.insert(tableName, null, initialValues);
		} catch (Throwable e) {

		}
		return i;
	}

	/**
	 * 保存数据到当前用户
	 * 
	 * @param tableName
	 * @param initialValues
	 * @return
	 */
	public long saveBindUser(String tableName, ContentValues initialValues) {
		long i = -1;
		try {
			if (appcontext.isLogin() && appcontext.getUserId() != null) {
				initialValues.put(KEY_PUBLIC_USER_ID, appcontext.getUserId());
			}

			i = db.insert(tableName, null, initialValues);
		} catch (Throwable e) {

		}
		return i;
	}

	public boolean update(String tableName, String whereClause,
			ContentValues args) {
		boolean flag = db.update(tableName, args, whereClause, null) > 0;
		return flag;
	}

	/**
	 * 更新当前用户的数据
	 * 
	 * @param tableName
	 * @param whereClause
	 * @param args
	 * @return
	 */
	public boolean updateBindUser(String tableName, String whereClause,
			ContentValues args) {
		String sql = new String();

		if (whereClause != null) {
			sql += whereClause;
			sql += " AND ";

		}
		if (appcontext.isLogin() && appcontext.getUserId() != null) {
			sql += (KEY_PUBLIC_USER_ID + " = " + appcontext.getUserId());
		} else {
			sql += (KEY_PUBLIC_USER_ID + " IS NULL");
		}

		boolean flag = db.update(tableName, args, sql, null) > 0;
		return flag;
	}

	public int delete(String tableName, String whereClause, String[] whereArgs) {
		int id = db.delete(tableName, whereClause, whereArgs);
		logger.i("Del whereClause id = " + id);
		return id;
	}

	public int clear(String tableName) {
		return delete(tableName, null, null);
	}

	/**
	 * 删除当前用户的数据
	 * 
	 * @param tableName
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int deleteBindUser(String tableName, String whereClause,
			String[] whereArgs) {
		String sql = new String();
		if (whereClause != null) {
			sql += whereClause;
			sql += " AND ";
		}
		if (appcontext.isLogin() && appcontext.getUserId() != null) {
			sql += (KEY_PUBLIC_USER_ID + " = " + appcontext.getUserId());
		} else {
			sql += (KEY_PUBLIC_USER_ID + " IS NULL");
		}

		int id = db.delete(tableName, sql, whereArgs);
		logger.i("Del whereClause id = " + id);
		return id;
	}

	public void beginTransaction() {
		if (null != db) {
			db.beginTransaction();
		}
	}

	public void setTransactionSuccessful() {
		if (null != db) {
			db.setTransactionSuccessful();
		}
	}

	public void endTransaction() {
		if (null != db) {
			db.endTransaction();
		}
	}

	public Cursor query(boolean distinct, String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) {
		Cursor mCursor = db.query(distinct, table, columns, selection,
				selectionArgs, groupBy, having, orderBy, limit);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	/**
	 * 搜索的当前用户的数据
	 * 
	 * @param distinct
	 * @param table
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return
	 */
	public Cursor queryBindUser(boolean distinct, String table,
			String[] columns, String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy, String limit) {
		String newselection = new String();
		if (selection != null) {
			newselection += selection;
			newselection += " AND ";
		}
		if (appcontext.isLogin() && appcontext.getUserId() != null) {
			newselection += (KEY_PUBLIC_USER_ID + " = " + appcontext
					.getUserId());
		} else {
			newselection += (KEY_PUBLIC_USER_ID + " IS NULL");
		}
		Cursor mCursor = db.query(distinct, table, columns, newselection,
				selectionArgs, groupBy, having, orderBy, limit);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	public Cursor query(String tableName, String[] columns, String querySql) {
		Cursor mCursor = db.query(true, tableName, columns, querySql, null,
				null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * 搜索的当前用户的数据
	 * 
	 * @param tableName
	 * @param columns
	 * @param querySql
	 * @return
	 */
	public Cursor queryBindUser(String tableName, String[] columns,
			String querySql) {
		String newselection = new String();
		if (querySql != null) {
			newselection += querySql;
			newselection += " AND ";

		}
		if (appcontext.isLogin() && appcontext.getUserId() != null) {
			newselection += (KEY_PUBLIC_USER_ID + " = " + appcontext
					.getUserId());
		} else {
			newselection += (KEY_PUBLIC_USER_ID + " IS NULL");
		}

		Cursor mCursor = db.query(true, tableName, columns, newselection, null,
				null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor queryWithOrder(String tableName, String[] columns,
			String querySql, String[] selectionArgs, String order) {
		Cursor mCursor = db.query(true, tableName, columns, querySql,
				selectionArgs, null, null, order, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	/**
	 * 搜索的当前用户的数据
	 * 
	 * @param tableName
	 * @param columns
	 * @param querySql
	 * @param selectionArgs
	 * @param order
	 * @return
	 */
	public Cursor queryWithOrderBindUser(String tableName, String[] columns,
			String querySql, String[] selectionArgs, String order) {
		String newselection = new String();
		if (querySql != null) {
			newselection += querySql;
			newselection += " AND ";
		}

		if (appcontext.isLogin() && appcontext.getUserId() != null) {
			newselection += (KEY_PUBLIC_USER_ID + " = " + appcontext
					.getUserId());
		} else {
			newselection += (KEY_PUBLIC_USER_ID + " IS NULL");
		}
		Cursor mCursor = db.query(true, tableName, columns, newselection,
				selectionArgs, null, null, order, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	/**
	 * @param strSql
	 * @param selectionArgs
	 * @return
	 */
	public Cursor rawQuery(String strSql, String[] selectionArgs) {
		Cursor c = db.rawQuery(strSql, selectionArgs);

		if (null != c) {
			c.moveToFirst();
		}

		return c;
	}

	/**
	 * 搜索的当前用户的数据
	 * 
	 * @param strSql
	 * @param selectionArgs
	 * @return
	 */
	public Cursor rawQueryBindUser(String strSql, String[] selectionArgs) {
		String sql = new String();

		if (strSql != null) {
			sql += strSql;
			if (strSql.toLowerCase().contains("where"))
				sql += " AND ";
			else
				sql += " where ";
		}
		if (appcontext.isLogin() && appcontext.getUserId() != null) {
			sql += (KEY_PUBLIC_USER_ID + " = " + appcontext.getUserId());
		} else {
			sql += (KEY_PUBLIC_USER_ID + " IS NULL");
		}

		Cursor c = db.rawQuery(sql, selectionArgs);

		if (null != c) {
			c.moveToFirst();
		}

		return c;
	}

	/**
	 * 数据升级类名数组； 每升一级，将该级对应的类名加入，没有则填 null；
	 */
	private String[] dbMigrations = { "com.qianniu.zhaopin.app.dbupdate.DbMigration_1",
			"com.qianniu.zhaopin.app.dbupdate.DbMigration_2", "com.qianniu.zhaopin.app.dbupdate.DbMigration_3"
				};

	private class DatabaseHelper extends SQLiteOpenHelper {

		private Context mcontext;
		private String mdbName;
		private Map<String, String> sqls;

		public DatabaseHelper(Context context, String dbName,
				CursorFactory factory, int version, Map<String, String> sqls) {
			super(context, dbName, null, version);
			mcontext = context;
			mdbName = dbName;
			this.sqls = sqls;
		}

		public void onCreate(SQLiteDatabase db) {
			logger.i("------database---------onCreate----------");
			if (sqls != null && sqls.size() > 0) {
				for (String table : sqls.values()) {
					db.execSQL(table);
					logger.i("onCreate sql-----" + table);
				}
			}
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			super.onOpen(db);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			logger.i("------database---------onUpgrade----------");
			logger.i("------database-----oldVersion------" + oldVersion);
			logger.i("------database-----newVersion------" + newVersion);
			if (newVersion > oldVersion) {
				if (dbMigrations.length == newVersion - 1) {
					for (int i = oldVersion; i < newVersion; i++) {
						int index = i - 1;
						if (dbMigrations[index] != null) {
							DbMigration dbm;
							try {
								dbm = (DbMigration) (Class
										.forName(dbMigrations[index]))
										.newInstance();
								if (dbm != null) {
									dbm.upgrade(db);
								}
							} catch (InstantiationException e) {
								// TODO Auto-generated catch block
								logger.i("database_onUpgrade_InstantiationException ="
										+ e);

							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								logger.i("database_onUpgrade_IllegalAccessException ="
										+ e);
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								logger.i("database_onUpgrade_ClassNotFoundExceptionn ="
										+ e);
							}

						}
					}
				}

				/*
				 * if (sqls != null && sqls.size() > 0) { for (String table :
				 * sqls.keySet()) { db.execSQL("DROP TABLE IF EXISTS  " +
				 * table); } } onCreate(db);
				 */
			}
		}

		public boolean deleteDatabase() {
			return mcontext.deleteDatabase(mdbName);
		}

		public void execSQL(SQLiteDatabase db, String sql) {
			logger.i("execSQL sql-----" + sql);
			db.execSQL(sql);

		}
	}

	public static String assembleFuzzysearchString(String columnName,
			String... args) {
		int count = args.length;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++) {
			if (i > 0)
				sb.append(" OR ");
			sb.append(columnName + " LIKE " + "\"" + "%");
			sb.append(args[i] + "%" + "\"");
		}
		return sb.toString();

	}

	/**
	 * 把备份数据复制到数据库中
	 * 
	 * @throws IOException
	 */
	public void bakDataBase() throws IOException {
		File dbfile = context.getDatabasePath(DBUtils.DB_NAME_BAK);
		if (!dbfile.exists()) {
			try {
				dbfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		FileOutputStream os = null;
		try {
			// 得到数据库文件的写入流
			os = new FileOutputStream(dbfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (null == os) {
			return;
		}

		// 得到数据库文件的数据流
		InputStream is = context.getAssets().open(DBUtils.DB_NAME);
		if (null == is) {
			return;
		}

		byte[] buffer = new byte[8192];
		int count = 0;
		try {
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
				os.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			is.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 设置数据库已经有全局数据
		// DMSharedPreferencesUtil.putSharePre(context,
		// DMSharedPreferencesUtil.DM_HOTLABEL_DB,
		// DMSharedPreferencesUtil.FIELD_FLG_GLOBALDB,
		// DMSharedPreferencesUtil.VALUE_FLG_HAVINGGLOBALDB);
	}
}

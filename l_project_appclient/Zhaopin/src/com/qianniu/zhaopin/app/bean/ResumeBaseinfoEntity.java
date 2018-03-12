package com.qianniu.zhaopin.app.bean;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.ui.JobRecommendActivity;

/*"status": "简历状态. 1=>正常, 2=>存档,即删除",
 "name": "姓名",
 "title": "头衔",
 "gender": "性别. 0=>未知, 1=>男, 2=>女",
 "phone": "电话",
 "mail": "邮箱",
 "picture": "头像",
 "birthday": "生日",
 "m100_5_id": "居住地",
 "m100_6_id": "最高学历",
 "m100_7_id": "求职状态",
 "tags": "个人关键字",
 "job_tags": "求职关键字",
 "memo": "个人简介",
 "m100_13_id": "期望行业",
 "expectioncity_id": "期望城市",
 "m100_8_id": "期望年薪",
 "selected_status": "是否为默认简历. 1=>默认",
 "m100_9_id": "到岗时间"*/

/**
 * 简历基本信息数据模型，包含个人基本信息和求职意向两部分数据
 * 
 * @author Administrator
 * 
 */
public class ResumeBaseinfoEntity extends Entity {
	private static final String TAG = "ResumeBaseinfoEntity";
	private String tag = "ResumeBaseinfoEntity";

	private static String NODE_STATUS = "status";
	private static String NODE_NAME = "name";
	private static String NODE_TITLE = "title";
	private static String NODE_POSITIONID = "jflv3_id";
	private static String NODE_GENDER = "gender";
	private static String NODE_PHONE = "phone";
	private static String NODE_MAIL = "mail";
	private static String NODE_PICTURE = "picture";
	private static String NODE_BIRTHDAY = "birthday";
	private static String NODE_PLACEID = "m104_id";
	private static String NODE_EDUCATION_DEGREE = "m105_id";
	private static String NODE_JOBGET_STATUS = "m106_id";
	private static String NODE_JOBGET_KEY = "job_tags";
	private static String NODE_SELFSTAG = "tags";
	private static String NODE_SELFMEMO = "memo";
	// private static String NODE_JOBGET_TRADE = "m112_id"; //行业1级
	private static String NODE_JOBGET_TRADE = "m113_id";// 行业2级
	private static String NODE_JOBGET_CITY = "expectioncity_id";
	private static String NODE_JOB_ANNUALSALARY = "m107_id";
	private static String NODE_JOB_ARRIVEDATE = "m108_id";
	private static String NODE_JOB_WORKSTRATDATE = "joinworkdate";
	private static String NODE_PERCENT = "percent";
	private static String NODE_SELECTED_STATUS = "selected_status";
	private static String NODE_URL = "url";

	private Result validate;

	private String resume_Id;
	private String resume_time;

	private int base_status = 1;
	private String name = "";
	private String persontitle = "";
	private String position_id = null;
	private int gender = 1;
	private String phone = "";
	private String mail = "";
	private String headphoto = "";
	private String placeId = "";
	private String birthday = "";
	private String educationdegreeId = "";
	private String jobgetstatusId = "";
	private String jobkey = "";
	private String selftag = "";
	private String selfmemo = "";
	private String jobtradeId = "";
	private String jobcityId = "";
	private ArrayList<String> jobtradeIdlist = new ArrayList<String>();
	private ArrayList<String> jobcityIdlist = new ArrayList<String>();
	private String jobsalaryId = "";
	private String arrivaldateId = "";
	private String workyear = "";
	private String url ="";

	private int percent = 0;
	private int defaultresume = 0;

	public final int getPercent() {
		return percent;
	}

	public final void setPercent(int percent) {
		this.percent = percent;
	}

	public final String getTag() {
		return tag;
	}

	public final void setTag(String tag) {
		this.tag = tag;
	}

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public final String getResume_Id() {
		return resume_Id;
	}

	public final void setResume_Id(String resume_Id) {
		this.resume_Id = resume_Id;
	}

	public final String getResume_time() {
		return resume_time;
	}

	public final void setResume_time(String resume_time) {
		this.resume_time = resume_time;
	}

	public final int getBase_status() {
		return base_status;
	}

	public final void setBase_status(int base_status) {
		this.base_status = base_status;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getPersontitle() {
		return persontitle;
	}

	public final void setPersontitle(String persontitle) {
		this.persontitle = persontitle;
	}

	
	public String getPosition_id() {
		return position_id;
	}

	public void setPosition_id(String position_id) {
		this.position_id = new String(position_id);
	}

	public final int getGender() {
		return gender;
	}

	public final void setGender(int gender) {
		this.gender = gender;
	}

	public final String getPhone() {
		return phone;
	}

	public final void setPhone(String phone) {
		this.phone = phone;
	}

	public final String getMail() {
		return mail;
	}

	public final void setMail(String mail) {
		this.mail = mail;
	}

	public final String getHeadphoto() {
		return headphoto;
	}

	public final void setHeadphoto(String headphoto) {
		this.headphoto = headphoto;
	}

	public final String getplaceId() {
		return placeId;
	}

	public final void setplaceId(String placeId) {
		this.placeId = placeId;
	}

	public final String getBirthday() {
		return birthday;
	}

	public final void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public final String geteducationdegreeId() {
		return educationdegreeId;
	}

	public final void seteducationdegreeId(String educationdegreeId) {
		this.educationdegreeId = educationdegreeId;
	}

	public final String getjobgetstatusId() {
		return jobgetstatusId;
	}

	public final void setjobgetstatusId(String jobgetstatusId) {
		this.jobgetstatusId = jobgetstatusId;
	}

	public final String getJobkey() {
		return jobkey;
	}

	public final void setJobkey(String jobkey) {
		this.jobkey = jobkey;
	}

	public final String getSelftag() {
		return selftag;
	}

	public final void setSelftag(String selftag) {
		this.selftag = selftag;
	}

	public final String getSelfmemo() {
		return selfmemo;
	}

	public final void setSelfmemo(String selfmemo) {
		this.selfmemo = selfmemo;
	}

	public final String getjobtradeId() {
		return jobtradeId;
	}

	public final void setjobtradeId(String jobtradeId) {
		this.jobtradeId = jobtradeId;
	}

	public final String getjobcityId() {
		return jobcityId;
	}

	public final void setjobcityId(String jobcityId) {
		this.jobcityId = jobcityId;
	}

	public final ArrayList<String> getJobtradeIdlist() {
		return jobtradeIdlist;
	}

	public final void setJobtradeIdlist(ArrayList<String> jobtradeIdlist) {
		this.jobtradeIdlist = jobtradeIdlist;
	}

	public final ArrayList<String> getJobcityIdlist() {
		return jobcityIdlist;
	}

	public final void setJobcityIdlist(ArrayList<String> jobcityIdlist) {
		this.jobcityIdlist = jobcityIdlist;
	}

	public final String getjobsalaryId() {
		return jobsalaryId;
	}

	public final void setjobsalaryId(String jobsalaryId) {
		this.jobsalaryId = jobsalaryId;
	}

	public final String getarrivaldateId() {
		return arrivaldateId;
	}

	public final void setarrivaldateId(String arrivaldateId) {
		this.arrivaldateId = arrivaldateId;
	}

	public final String getWorkyear() {
		return workyear;
	}

	public final void setWorkyear(String workyear) {
		this.workyear = workyear;
	}

	public final int getDefaultResume() {
		return defaultresume;
	}

	public final void setDefaultResume(int defaultresume) {
		this.defaultresume = defaultresume;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static ResumeBaseinfoEntity parse(AppContext appContext,
			JSONObject jsonObj, boolean bsavetoDb, String resume_id,
			String resume_time) throws IOException, JSONException, AppException {

		ResumeBaseinfoEntity entity = new ResumeBaseinfoEntity();
		Result res = null;

		try {
			entity.resume_Id = resume_id;
			entity.resume_time = resume_time;

			entity.base_status = jsonObj.getInt(NODE_STATUS);
			entity.name = jsonObj.getString(NODE_NAME);
			entity.persontitle = jsonObj.getString(NODE_TITLE);
			entity.position_id = jsonObj.getString(NODE_POSITIONID);
			entity.gender = jsonObj.getInt(NODE_GENDER);
			entity.phone = jsonObj.getString(NODE_PHONE);
			entity.mail = jsonObj.getString(NODE_MAIL);
			entity.headphoto = jsonObj.getString(NODE_PICTURE);
			entity.placeId = jsonObj.getString(NODE_PLACEID);
			entity.birthday = jsonObj.getString(NODE_BIRTHDAY);
			entity.educationdegreeId = jsonObj.getString(NODE_EDUCATION_DEGREE);
			entity.jobgetstatusId = jsonObj.getString(NODE_JOBGET_STATUS);
			entity.jobkey = jsonObj.getString(NODE_JOBGET_KEY);
			entity.selftag = jsonObj.getString(NODE_SELFSTAG);
			entity.selfmemo = jsonObj.getString(NODE_SELFMEMO);
			entity.url = jsonObj.getString(NODE_URL);
			/*
			 * entity.jobtradeId = jsonObj.getString(NODE_JOBGET_TRADE);
			 * entity.jobcityId = jsonObj.getString(NODE_JOBGET_CITY);
			 */
			if (jsonObj.has(NODE_JOBGET_TRADE)) {
				JSONArray jobtradeids = jsonObj.getJSONArray(NODE_JOBGET_TRADE);

				for (int i = 0; i < jobtradeids.length(); i++) {
					String tradeid = jobtradeids.getString(i);
					if (tradeid != null && tradeid.length() > 0)
						entity.jobtradeIdlist.add(tradeid);
				}
			}
			JSONArray jobcityids = jsonObj.getJSONArray(NODE_JOBGET_CITY);
			for (int i = 0; i < jobcityids.length(); i++) {
				String cityid = jobcityids.getString(i);
				if (cityid != null && cityid.length() > 0)
					entity.jobcityIdlist.add(cityid);
			}
			entity.jobsalaryId = jsonObj.getString(NODE_JOB_ANNUALSALARY);
			entity.arrivaldateId = jsonObj.getString(NODE_JOB_ARRIVEDATE);
			entity.workyear = jsonObj.getString(NODE_JOB_WORKSTRATDATE);
			entity.defaultresume = jsonObj.getInt(NODE_SELECTED_STATUS);
			entity.percent = jsonObj.getInt(NODE_PERCENT);

			res = new Result(1, "ok");
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				// 保存到数据库，如果
				deleteResumeBaseinfoEntityFromDb(appContext, entity.resume_Id);
				saveResumeBaseinfoEntityToDb(appContext, entity, true);
			}

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {

			entity.validate = res;

		}
		return entity;

	}

	public static JSONObject resumeBaseinfoEntityToJSONObject(
			ResumeBaseinfoEntity entity) {

		JSONObject obj = new JSONObject();

		try {
			obj.put(NODE_STATUS, entity.base_status);
			obj.put(NODE_NAME, entity.name);
			obj.put(NODE_TITLE, entity.persontitle);
			obj.put(NODE_POSITIONID,entity.position_id);
			obj.put(NODE_GENDER, entity.gender);
			obj.put(NODE_PHONE, entity.phone);
			obj.put(NODE_MAIL, entity.mail);
			obj.put(NODE_PICTURE, entity.headphoto);
			obj.put(NODE_BIRTHDAY, entity.birthday);
			obj.put(NODE_PLACEID, entity.placeId);
			obj.put(NODE_EDUCATION_DEGREE, entity.educationdegreeId);
			obj.put(NODE_JOBGET_STATUS, entity.jobgetstatusId);
			obj.put(NODE_JOBGET_KEY, entity.jobkey);
			obj.put(NODE_SELFSTAG, entity.selftag);
			obj.put(NODE_SELFMEMO, entity.selfmemo);
			JSONArray tradearray = new JSONArray();
			for (int i = 0; i < entity.jobtradeIdlist.size(); i++) {
				String industryidstr = entity.jobtradeIdlist.get(i);
				if (industryidstr != null && industryidstr.length() > 0)
					tradearray.put(industryidstr);
			}
			obj.put(NODE_JOBGET_TRADE, tradearray);
			JSONArray cityarray = new JSONArray();
			for (int i = 0; i < entity.jobcityIdlist.size(); i++) {
				String cityidstr = entity.jobcityIdlist.get(i);
				if (cityidstr != null && cityidstr.length() > 0)
					cityarray.put(cityidstr);
			}
			obj.put(NODE_JOBGET_CITY, cityarray);

			// obj.put(NODE_JOBGET_TRADE, entity.jobtradeId);
			// obj.put(NODE_JOBGET_CITY, entity.jobcityId);
			obj.put(NODE_JOB_ANNUALSALARY, entity.jobsalaryId);
			obj.put(NODE_JOB_ARRIVEDATE, entity.arrivaldateId);
			obj.put(NODE_JOB_WORKSTRATDATE, entity.workyear);
			obj.put(NODE_PERCENT, entity.percent);
			obj.put(NODE_SELECTED_STATUS, entity.defaultresume);

		} catch (JSONException e) {
			MyLogger.i(TAG, "resumeBaseinfoEntityToJSONObject " + e);
		}

		return obj;
	}

	public static boolean deleteResumeBaseinfoEntityFromDb(
			AppContext appContext, String resume_Id) {
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_Id;
		sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
				+ DBUtils.RESUME_TYPE_BASE;
		int id = db.deleteBindUser(DBUtils.resumeTableName, sql, null);
		if (id > 0)
			return true;
		else
			return false;
	}

	public static boolean saveResumeBaseinfoEntityToDb(AppContext appContext,
			ResumeBaseinfoEntity entity, boolean bsubmit) {

		String jsoncontent = resumeBaseinfoEntityToJSONObject(entity)
				.toString();

		DBUtils db = DBUtils.getInstance(appContext);

		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_RESUME_RESUME_ID, entity.resume_Id);
		values.put(DBUtils.KEY_RESUME_RESUME_TIME, entity.resume_time);
		values.put(DBUtils.KEY_RESUME_JSON_CONTENT, jsoncontent);
		values.put(DBUtils.KEY_RESUME_ROWDATA_TYPE, DBUtils.RESUME_TYPE_BASE);

		if (bsubmit)
			values.put(DBUtils.KEY_RESUME_SUBMIT_STATE,
					DBUtils.RESUME_STATE_SUBMITED);
		else
			values.put(DBUtils.KEY_RESUME_SUBMIT_STATE,
					DBUtils.RESUME_STATE_UNSUBMITED);

		values.put(DBUtils.KEY_RESUME_TIMESTAMP,
				String.valueOf(System.currentTimeMillis()));

		long id = db.saveBindUser(DBUtils.resumeTableName, values);

		if (id > 0)
			return true;
		else
			return false;
	}

	public static boolean isCompletedForSelfInfo(ResumeBaseinfoEntity entity) {
		boolean bcompleted = true;

		if (bcompleted && (entity.name == null || entity.name.length() == 0))
			bcompleted = false;
		if (bcompleted && (entity.gender < 1))
			bcompleted = false;
		if (bcompleted
				&& (entity.birthday == null || entity.birthday.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.workyear == null || entity.workyear.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.placeId == null || entity.placeId.length() == 0))
			bcompleted = false;
		if (bcompleted && (entity.phone == null || entity.phone.length() == 0))
			bcompleted = false;
		if (bcompleted && (entity.mail == null || entity.mail.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.educationdegreeId == null || entity.educationdegreeId
						.length() == 0))
			bcompleted = false;
		/*
		 * if (bcompleted && (entity.selftag == null || entity.selftag.length()
		 * == 0)) bcompleted = false;
		 */
		return bcompleted;

	}

	public static boolean isCompletedForJobIntension(ResumeBaseinfoEntity entity) {
		boolean bcompleted = true;

		if (bcompleted
				&& (entity.jobgetstatusId == null || entity.jobgetstatusId
						.length() == 0))
			bcompleted = false;

		/*
		 * if (bcompleted && (entity.jobtradeId == null ||
		 * entity.jobtradeId.length() == 0)) bcompleted = false;
		 * 
		 * if (bcompleted && (entity.jobcityId == null ||
		 * entity.jobcityId.length() == 0)) bcompleted = false;
		 */

		if (bcompleted
				&& (entity.jobtradeIdlist == null || entity.jobtradeIdlist
						.size() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.jobcityIdlist == null || entity.jobcityIdlist.size() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.jobsalaryId == null || entity.jobsalaryId.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.arrivaldateId == null || entity.arrivaldateId
						.length() == 0))
			bcompleted = false;
		/*
		 * if (bcompleted && (entity.jobkey == null || entity.jobkey.length() ==
		 * 0)) bcompleted = false;
		 */
		/*
		 * if (bcompleted && (entity.selfmemo == null ||
		 * entity.selfmemo.length() == 0)) bcompleted = false;
		 */
		return bcompleted;

	}

}

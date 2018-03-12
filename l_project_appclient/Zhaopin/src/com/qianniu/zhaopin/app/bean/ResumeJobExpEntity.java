package com.qianniu.zhaopin.app.bean;

import java.io.IOException;
import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.database.DBUtils;

/*{
 "id": "工作经历id, 为空时新增",
 "status": "简历状态. 1=>正常, 2=>存档,即删除",
 "company": "公司名称",
 "startdate": "开始时间",
 "enddate": "结束时间",
 "department": "部门名称",
 "title": "职位名称",
 "m100_13_id": "行业id",
 "memo": "工作描述"
 }*/

/**
 * 简历工作经历数据模型
 * 
 * @author Administrator
 * 
 */
public class ResumeJobExpEntity extends Entity {
	private static final String TAG = "ResumeJobExpEntity";

	private static String NODE_ID = "id";
	private static String NODE_STATUS = "status";
	private static String NODE_COMPANY = "company";
	private static String NODE_STARTDATE = "startdate";
	private static String NODE_ENDDATE = "enddate";
	private static String NODE_DEPARTMENT = "department";
	private static String NODE_JOBTITLE = "title";
	// private static String NODE_JOBINTRADE = "m112_id";//一级数据
	private static String NODE_JOBINTRADE = "m113_id";// 二级数据
	private static String NODE_JOBMEMO = "memo";
	private static String NODE_JOBFUNCID = "jobfunc_id";// 三级数据

	private Result validate;

	private String resume_Id;
	private String resume_time;
	private boolean bcompleted;

	private int status;
	private String itemid;
	private String company;
	private String startdate;
	private String enddate  = "2100-01-01";
	private String department;
	// 职位名称
	private String jobtitle;
	// 职位名称
	private String jobtitleId;
	private String jobtradeid;
	private String jobmemo;

	public final boolean isBcompleted() {
		return bcompleted;
	}

	public final void setBcompleted(boolean bcompleted) {
		this.bcompleted = bcompleted;
	}

	public final String getItemid() {
		return itemid;
	}

	public final void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public final String getItemId() {
		return this.itemid;
	}

	public final void setId(String itemid) {
		this.itemid = itemid;
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

	public final int getStatus() {
		return status;
	}

	public final void setStatus(int status) {
		this.status = status;
	}

	public final String getCompany() {
		return company;
	}

	public final void setCompany(String company) {
		this.company = company;
	}

	public final String getStartdate() {
		return startdate;
	}

	public final void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public final String getEnddate() {
		return enddate;
	}

	public final void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public final String getDepartment() {
		return department;
	}

	public final void setDepartment(String department) {
		this.department = department;
	}

	public final String getJobtitle() {
		return jobtitle;
	}

	public final void setJobtitle(String jobtitle) {
		this.jobtitle = jobtitle;
	}

	public final String getJobtradeid() {
		return jobtradeid;
	}

	public final void setJobtradeid(String jobtradeid) {
		this.jobtradeid = jobtradeid;
	}

	public final String getJobmemo() {
		return jobmemo;
	}

	public final void setJobmemo(String jobmemo) {
		this.jobmemo = jobmemo;
	}

	public String getJobtitleId() {
		return jobtitleId;
	}

	public void setJobtitleId(String jobtitleId) {
		this.jobtitleId = jobtitleId;
	}

	public static ResumeJobExpEntity parse(AppContext appContext,
			JSONObject jsonObj, boolean bsavetoDb, String resume_id,
			String resume_time) throws IOException, JSONException, AppException {

		ResumeJobExpEntity entity = new ResumeJobExpEntity();
		Result res = null;

		try {
			entity.resume_Id = resume_id;
			entity.resume_time = resume_time;

			entity.status = jsonObj.getInt(NODE_STATUS);
			entity.itemid = jsonObj.getString(NODE_ID);
			entity.company = jsonObj.getString(NODE_COMPANY);
			entity.startdate = jsonObj.getString(NODE_STARTDATE);
			entity.enddate = jsonObj.getString(NODE_ENDDATE);
			entity.department = jsonObj.getString(NODE_DEPARTMENT);
			if (jsonObj.has(NODE_JOBTITLE))
				entity.jobtitle = jsonObj.getString(NODE_JOBTITLE);
			if (jsonObj.has(NODE_JOBFUNCID))
				entity.jobtitleId = jsonObj.getString(NODE_JOBFUNCID);
			if (jsonObj.has(NODE_JOBINTRADE))
				entity.jobtradeid = jsonObj.getString(NODE_JOBINTRADE);
			entity.jobmemo = jsonObj.getString(NODE_JOBMEMO);

			res = new Result(1, "ok");
			entity.bcompleted = isCompletedForJobExp(entity);
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				// 保存到数据库，如果
				deleteResumeJobExpEntityFromDb(appContext, entity.resume_Id,
						entity.itemid);
				saveResumeJobExpEntityToDb(appContext, entity, true);
			}

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {

			entity.validate = res;

		}
		return entity;

	}

	public static JSONObject resumeJobExpEntityToJSONObject(
			ResumeJobExpEntity entity) {

		JSONObject obj = new JSONObject();

		try {
			obj.put(NODE_STATUS, entity.status);
			obj.put(NODE_ID, entity.itemid);
			obj.put(NODE_COMPANY, entity.company);
			obj.put(NODE_STARTDATE, entity.startdate);
			obj.put(NODE_ENDDATE, entity.enddate);
			obj.put(NODE_DEPARTMENT, entity.department);
			obj.put(NODE_JOBTITLE, entity.jobtitle);
			obj.put(NODE_JOBFUNCID, entity.jobtitleId);
			obj.put(NODE_JOBINTRADE, entity.jobtradeid);
			obj.put(NODE_JOBMEMO, entity.jobmemo);

		} catch (JSONException e) {
			MyLogger.e(TAG, "resumeBaseinfoEntityToJSONObject " + e);
		}

		return obj;
	}

	public static boolean deleteResumeJobExpEntityFromDb(AppContext appContext,
			String resume_Id, String itemid) {
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_Id;
		sql += " AND " + DBUtils.KEY_RESUME_RESUME_CONTENT_ID + " = " + itemid;
		sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
				+ DBUtils.RESUME_TYPE_JOBEXP;

		int id = db.deleteBindUser(DBUtils.resumeTableName, sql, null);
		if (id > 0)
			return true;
		else
			return false;
	}

	public static boolean saveResumeJobExpEntityToDb(AppContext appContext,
			ResumeJobExpEntity entity, boolean bsubmit) {

		String jsoncontent = resumeJobExpEntityToJSONObject(entity).toString();

		DBUtils db = DBUtils.getInstance(appContext);

		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_RESUME_RESUME_ID, entity.resume_Id);
		values.put(DBUtils.KEY_RESUME_RESUME_TIME, entity.resume_time);
		values.put(DBUtils.KEY_RESUME_JSON_CONTENT, jsoncontent);
		values.put(DBUtils.KEY_RESUME_ROWDATA_TYPE, DBUtils.RESUME_TYPE_JOBEXP);
		values.put(DBUtils.KEY_RESUME_RESUME_CONTENT_ID, entity.itemid);

		boolean bcompleted = entity.bcompleted;// isCompletedForJobExp(entity);
		if (bcompleted)
			values.put(DBUtils.KEY_RESUME_COMPLETED_DEGREE,
					DBUtils.RESUME_STATE_COMPLETEED);
		else
			values.put(DBUtils.KEY_RESUME_COMPLETED_DEGREE,
					DBUtils.RESUME_STATE_UNCOMPLETEED);

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

	public static boolean isCompletedForJobExp(ResumeJobExpEntity entity) {
		boolean bcompleted = true;

		if (bcompleted
				&& (entity.company == null || entity.company.length() == 0))
			bcompleted = false;

		if (bcompleted
				&& (entity.startdate == null || entity.startdate.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.enddate == null || entity.enddate.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.department == null || entity.department.length() == 0))
			bcompleted = false;
//		if (bcompleted
//				&& (entity.jobtitle == null || entity.jobtitle.length() == 0))
//			bcompleted = false;
		if (bcompleted
				&& (entity.jobtitleId == null || entity.jobtitleId.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.jobtradeid == null || entity.jobtradeid.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.jobmemo == null || entity.jobmemo.length() == 0))
			bcompleted = false;

		return bcompleted;

	}

	public static class ComparatorEntity implements Comparator {

		public int compare(Object arg0, Object arg1) {
			ResumeJobExpEntity entity0 = (ResumeJobExpEntity) arg0;
			ResumeJobExpEntity entity1 = (ResumeJobExpEntity) arg1;

			// 首先比较年龄，如果年龄相同，则比较名字

			long days;
			try {
				days = StringUtils.compareDateStr(entity0.getStartdate(),
						entity1.getStartdate());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				days = 0;
			}
			return (int) -days;
		}

	}
}

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
 "id": "项目经历id, 为空时新增",
 "status": "简历状态. 1=>正常, 2=>存档,即删除",
 "company": "公司名称",
 "name": "项目名称",
 "startdate": "开始时间",
 "enddate": "结束时间",
 "memo": "工作描述"
 }*/
/**简历项目经历数据模型
 * @author Administrator
 *
 */
public class ResumeProjectExpEntity extends Entity {
	private static final String TAG = "ResumeProjectExpEntity";
	private String tag = "ResumeProjectExpEntity";

	private static String NODE_ID = "id";
	private static String NODE_STATUS = "status";
	private static String NODE_COMPANY = "company";
	private static String NODE_PROJECTNAME = "name";
	private static String NODE_STARTDATE = "startdate";
	private static String NODE_ENDDATE = "enddate";
	private static String NODE_PROJECTMEMO = "memo";

	private Result validate;

	private String resume_Id;
	private String resume_time;
	private boolean bcompleted;
	
	private int status;
	private String itemid;
	private String company;
	private String startdate;
	private String enddate = "2100-01-01";
	private String projectname;
	private String projectmemo;

	public final boolean isBcompleted() {
		return bcompleted;
	}

	public final void setBcompleted(boolean bcompleted) {
		this.bcompleted = bcompleted;
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

	public final String getItemid() {
		return itemid;
	}

	public final void setItemid(String itemid) {
		this.itemid = itemid;
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

	public final String getProjectname() {
		return projectname;
	}

	public final void setProjectname(String projectname) {
		this.projectname = projectname;
	}

	public final String getProjectmemo() {
		return projectmemo;
	}

	public final void setProjectmemo(String projectmemo) {
		this.projectmemo = projectmemo;
	}

	public static ResumeProjectExpEntity parse(AppContext appContext,
			JSONObject jsonObj, boolean bsavetoDb, String resume_id,
			String resume_time) throws IOException, JSONException, AppException {

		ResumeProjectExpEntity entity = new ResumeProjectExpEntity();
		Result res = null;

		try {
			entity.resume_Id = resume_id;
			entity.resume_time = resume_time;

			entity.status = jsonObj.getInt(NODE_STATUS);
			entity.itemid = jsonObj.getString(NODE_ID);
			entity.company = jsonObj.getString(NODE_COMPANY);
			entity.startdate = jsonObj.getString(NODE_STARTDATE);
			entity.enddate = jsonObj.getString(NODE_ENDDATE);
			entity.projectname = jsonObj.getString(NODE_PROJECTNAME);
			entity.projectmemo = jsonObj.getString(NODE_PROJECTMEMO);

			res = new Result(1, "ok");
			entity.bcompleted = isCompletedForProjectExp(entity);
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				// 保存到数据库，如果
				deleteResumeProjectExpEntityFromDb(appContext, entity.resume_Id,
						entity.itemid);
				saveResumeProjectExpEntityToDb(appContext, entity, true);
			}

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {

			entity.validate = res;

		}
		return entity;

	}

	public static JSONObject resumeProjectExpEntityToJSONObject(
			ResumeProjectExpEntity entity) {

		JSONObject obj = new JSONObject();

		try {
			obj.put(NODE_STATUS, entity.status);
			obj.put(NODE_ID, entity.itemid);
			obj.put(NODE_COMPANY, entity.company);
			obj.put(NODE_STARTDATE, entity.startdate);
			obj.put(NODE_ENDDATE, entity.enddate);
			obj.put(NODE_PROJECTNAME, entity.projectname);
			obj.put(NODE_PROJECTMEMO, entity.projectmemo);

		} catch (JSONException e) {
			MyLogger.e(TAG, "resumeBaseinfoEntityToJSONObject "
					+ e);
		}

		return obj;
	}
	public static boolean deleteResumeProjectExpEntityFromDb(AppContext appContext,
			String resume_Id, String itemid) {
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_Id;
		sql += " AND " + DBUtils.KEY_RESUME_RESUME_CONTENT_ID + " = " + itemid;
		sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
				+ DBUtils.RESUME_TYPE_PROJECTEXP;

		int id = db.deleteBindUser(DBUtils.resumeTableName, sql, null);
		if (id > 0)
			return true;
		else
			return false;
	}
	
	public static boolean saveResumeProjectExpEntityToDb(AppContext appContext,
			ResumeProjectExpEntity entity, boolean bsubmit) {

		String jsoncontent = resumeProjectExpEntityToJSONObject(entity).toString();

		DBUtils db = DBUtils.getInstance(appContext);

		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_RESUME_RESUME_ID, entity.resume_Id);
		values.put(DBUtils.KEY_RESUME_RESUME_TIME, entity.resume_time);
		values.put(DBUtils.KEY_RESUME_JSON_CONTENT, jsoncontent);
		values.put(DBUtils.KEY_RESUME_ROWDATA_TYPE, DBUtils.RESUME_TYPE_PROJECTEXP);
		values.put(DBUtils.KEY_RESUME_RESUME_CONTENT_ID, entity.itemid);
		
		boolean bcompleted = entity.bcompleted;//isCompletedForProjectExp(entity);
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
	
	public static boolean isCompletedForProjectExp(ResumeProjectExpEntity entity) {
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
				&& (entity.projectname == null || entity.projectname.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.projectmemo == null || entity.projectmemo.length() == 0))
			bcompleted = false;

		return bcompleted;

	}
	
	public static class ComparatorEntity implements Comparator {

		public int compare(Object arg0, Object arg1) {
			ResumeProjectExpEntity entity0 = (ResumeProjectExpEntity) arg0;
			ResumeProjectExpEntity entity1 = (ResumeProjectExpEntity) arg1;

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

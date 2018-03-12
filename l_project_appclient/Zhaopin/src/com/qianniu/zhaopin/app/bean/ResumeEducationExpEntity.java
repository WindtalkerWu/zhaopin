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
    "id": "教育经历id, 为空时新增",
    "status": "简历状态. 1=>正常, 2=>存档,即删除",
    "name": "学校名称",
    "startdate": "开始时间",
    "enddate": "结束时间",
    "m100_6_id": "学历id",
    "m100_17_id": "专业id",
    "memo": "工作描述"
  }*/
/**简历教育经历数据模型
 * @author Administrator
 *
 */
public class ResumeEducationExpEntity extends Entity{
	private static final String TAG = "ResumeEducationExpEntity";
	private String tag = "ResumeEducationExpEntity";

	private static String NODE_ID = "id";
	private static String NODE_STATUS = "status";
	private static String NODE_SCHOOLNAME = "name";
	private static String NODE_STARTDATE = "startdate";
	private static String NODE_ENDDATE = "enddate";
	private static String NODE_DEGREEID = "m105_id";
	private static String NODE_MAJORID = "m116_id";
	
	private Result validate;

	private String resume_Id;
	private String resume_time;
	private boolean bcompleted;
	
	private int status;
	private String itemid;
	private String schoolname;
	private String startdate;
	private String enddate = "2100-01-01";
	private String degreeid;
	private String majorid;
	
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

	public final String getSchoolname() {
		return schoolname;
	}

	public final void setSchoolname(String schoolname) {
		this.schoolname = schoolname;
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

	public final String getDegreeid() {
		return degreeid;
	}

	public final void setDegreeid(String degreeid) {
		this.degreeid = degreeid;
	}

	public final String getMajorid() {
		return majorid;
	}

	public final void setMajorid(String majorid) {
		this.majorid = majorid;
	}

	public static ResumeEducationExpEntity parse(AppContext appContext,
			JSONObject jsonObj, boolean bsavetoDb, String resume_id,
			String resume_time) throws IOException, JSONException, AppException {

		ResumeEducationExpEntity entity = new ResumeEducationExpEntity();
		Result res = null;

		try {
			entity.resume_Id = resume_id;
			entity.resume_time = resume_time;

			entity.status = jsonObj.getInt(NODE_STATUS);
			entity.itemid = jsonObj.getString(NODE_ID);
			entity.schoolname = jsonObj.getString(NODE_SCHOOLNAME);
			entity.startdate = jsonObj.getString(NODE_STARTDATE);
			entity.enddate = jsonObj.getString(NODE_ENDDATE);
			entity.degreeid = jsonObj.getString(NODE_DEGREEID);
			entity.majorid = jsonObj.getString(NODE_MAJORID);

			res = new Result(1, "ok");
			entity.bcompleted = isCompletedForEducationExp(entity);
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				// 保存到数据库，如果
				deleteResumeEducationExpEntityFromDb(appContext, entity.resume_Id,
						entity.itemid);
				saveResumeEducationExpEntityToDb(appContext, entity, true);
			}

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {

			entity.validate = res;

		}
		return entity;

	}
	
	public static JSONObject resumeEducationExpEntityToJSONObject(
			ResumeEducationExpEntity entity) {

		JSONObject obj = new JSONObject();

		try {
			obj.put(NODE_STATUS, entity.status);
			obj.put(NODE_ID, entity.itemid);
			obj.put(NODE_SCHOOLNAME, entity.schoolname);
			obj.put(NODE_STARTDATE, entity.startdate);
			obj.put(NODE_ENDDATE, entity.enddate);
			obj.put(NODE_DEGREEID, entity.degreeid);
			obj.put(NODE_MAJORID, entity.majorid);

		} catch (JSONException e) {
			MyLogger.e(TAG, "resumeBaseinfoEntityToJSONObject "
					+ e);
		}

		return obj;
	}
	
	public static boolean deleteResumeEducationExpEntityFromDb(AppContext appContext,
			String resume_Id, String itemid) {
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_Id;
		sql += " AND " + DBUtils.KEY_RESUME_RESUME_CONTENT_ID + " = " + itemid;
		sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
				+ DBUtils.RESUME_TYPE_EDUCATIONEXP;

		int id = db.deleteBindUser(DBUtils.resumeTableName, sql, null);
		if (id > 0)
			return true;
		else
			return false;
	}
	
	public static boolean saveResumeEducationExpEntityToDb(AppContext appContext,
			ResumeEducationExpEntity entity, boolean bsubmit) {

		String jsoncontent = resumeEducationExpEntityToJSONObject(entity).toString();

		DBUtils db = DBUtils.getInstance(appContext);

		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_RESUME_RESUME_ID, entity.resume_Id);
		values.put(DBUtils.KEY_RESUME_RESUME_TIME, entity.resume_time);
		values.put(DBUtils.KEY_RESUME_JSON_CONTENT, jsoncontent);
		values.put(DBUtils.KEY_RESUME_ROWDATA_TYPE, DBUtils.RESUME_TYPE_EDUCATIONEXP);
		values.put(DBUtils.KEY_RESUME_RESUME_CONTENT_ID, entity.itemid);
		
		boolean bcompleted = entity.bcompleted;//isCompletedForEducationExp(entity);
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
	
	public static boolean isCompletedForEducationExp(ResumeEducationExpEntity entity) {
		boolean bcompleted = true;

		if (bcompleted
				&& (entity.schoolname == null || entity.schoolname.length() == 0))
			bcompleted = false;

		if (bcompleted
				&& (entity.startdate == null || entity.startdate.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.enddate == null || entity.enddate.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.degreeid == null || entity.degreeid.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.majorid == null || entity.majorid.length() == 0))
			bcompleted = false;

		return bcompleted;

	}
	
	public static class ComparatorEntity implements Comparator {

		public int compare(Object arg0, Object arg1) {
			ResumeEducationExpEntity entity0 = (ResumeEducationExpEntity) arg0;
			ResumeEducationExpEntity entity1 = (ResumeEducationExpEntity) arg1;

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

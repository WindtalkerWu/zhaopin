package com.qianniu.zhaopin.app.bean;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.database.DBUtils;

public class ResumeQuickItemEntity extends Entity {
	private static String NODE_CONTENT = "content";
	private static String NODE_STATUS = "status";

	private Result validate;

	private String resume_Id;
	private String resume_time;
	private boolean bcompleted;

	private String contentStr;
	private int status = 1;

	
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

	public final boolean isBcompleted() {
		return bcompleted;
	}

	public final void setBcompleted(boolean bcompleted) {
		this.bcompleted = bcompleted;
	}

	public final String getContentStr() {
		return contentStr;
	}

	public final void setContentStr(String contentStr) {
		this.contentStr = contentStr;
	}
	

	public final int getStatus() {
		return status;
	}

	public final void setStatus(int status) {
		this.status = status;
	}

	public static ResumeQuickItemEntity parse(AppContext appContext,
			JSONObject jsonObj, boolean bsavetoDb, String resume_id,
			String resume_time) throws IOException, JSONException, AppException {

		ResumeQuickItemEntity entity = new ResumeQuickItemEntity();
		Result res = null;

		try {
			entity.resume_Id = resume_id;
			entity.resume_time = resume_time;

			entity.contentStr = jsonObj.getString(NODE_CONTENT);
			//entity.status = jsonObj.getInt(NODE_STATUS);

			res = new Result(1, "ok");
			entity.bcompleted = isCompletedForQuickResume(entity);
			if (appContext != null && bsavetoDb) {
				//String jsonString = jsonObj.toString();
				// 保存到数据库，如果
/*				deleteResumeQuickItemEntityFromDb(appContext, entity.resume_Id);
				saveResumeQuickItemEntityToDb(appContext, entity, true);*/
			}

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {
			entity.validate = res;
		}
		return entity;

	}

	public static boolean deleteResumeQuickItemEntityFromDb(AppContext appContext,
			String resume_Id) {
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_Id;
		sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
				+ DBUtils.RESUME_TYPE_QUICKITEM;
		int id = db.deleteBindUser(DBUtils.resumeTableName, sql, null);
		if (id > 0)
			return true;
		else
			return false;
	}

	public static boolean saveResumeQuickItemEntityToDb(AppContext appContext,
			ResumeQuickItemEntity entity, boolean bsubmit) {

		String jsoncontent = resumeQuickItemEntityToJSONObject(entity)
				.toString();

		DBUtils db = DBUtils.getInstance(appContext);

		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_RESUME_RESUME_ID, entity.resume_Id);
		values.put(DBUtils.KEY_RESUME_RESUME_TIME, entity.resume_time);
		values.put(DBUtils.KEY_RESUME_JSON_CONTENT, jsoncontent);
		values.put(DBUtils.KEY_RESUME_ROWDATA_TYPE,
				DBUtils.RESUME_TYPE_QUICKITEM);

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

	public static JSONObject resumeQuickItemEntityToJSONObject(
			ResumeQuickItemEntity entity) {

		JSONObject obj = new JSONObject();

		try {
			obj.put(NODE_CONTENT, entity.contentStr);
			//obj.put(NODE_STATUS, entity.status);

		} catch (JSONException e) {

		}
		return obj;
	}

	public static boolean isCompletedForQuickResume(ResumeQuickItemEntity entity) {
		boolean bcompleted = true;
		if (entity == null) {
			bcompleted = false;
		} else {
			if (entity.contentStr == null || entity.contentStr.length() == 0) {
				bcompleted = false;
			}
		}
		return bcompleted;
	}

}

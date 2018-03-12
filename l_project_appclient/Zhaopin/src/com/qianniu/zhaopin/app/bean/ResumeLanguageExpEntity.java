package com.qianniu.zhaopin.app.bean;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.database.DBUtils;

/*"id": "语言程度id, 为空时新增",
"status": "简历状态. 1=>正常, 2=>存档,即删除",
"m100_10_id": "掌握语言id",
"m100_18_id": "掌握程度id",
"m100_19_id": "读写能力id",
"m100_20_id": "听说能力id"*/
/**简历语言技能数据模型
 * @author Administrator
 *
 */
public class ResumeLanguageExpEntity extends Entity{
	private static final String TAG = "ResumeLanguageExpEntity";
	private String tag = "ResumeLanguageExpEntity";

	private static String NODE_ID = "id";
	private static String NODE_STATUS = "status";
	private static String NODE_LANGUAGEID = "m109_id";
	private static String NODE_MASTERID = "m117_id";
	private static String NODE_READWRITEID = "m118_id";
	private static String NODE_LISTENSPEAKID = "m119_id";

	
	private Result validate;

	private String resume_Id;
	private String resume_time;
	private boolean bcompleted;
	
	private int status;
	private String itemid;
	private String languageid;
	private String masterid;
	private String readwriteid;
	private String listenspeakid;
	
	public final Result getValidate() {
		return validate;
	}


	public final boolean isBcompleted() {
		return bcompleted;
	}


	public final void setBcompleted(boolean bcompleted) {
		this.bcompleted = bcompleted;
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


	public final String getLanguageid() {
		return languageid;
	}


	public final void setLanguageid(String languageid) {
		this.languageid = languageid;
	}


	public final String getMasterid() {
		return masterid;
	}


	public final void setMasterid(String masterid) {
		this.masterid = masterid;
	}


	public final String getReadwriteid() {
		return readwriteid;
	}


	public final void setReadwriteid(String readwriteid) {
		this.readwriteid = readwriteid;
	}


	public final String getListenspeakid() {
		return listenspeakid;
	}


	public final void setListenspeakid(String listenspeakid) {
		this.listenspeakid = listenspeakid;
	}


	public static ResumeLanguageExpEntity parse(AppContext appContext,
			JSONObject jsonObj, boolean bsavetoDb, String resume_id,
			String resume_time) throws IOException, JSONException, AppException {

		ResumeLanguageExpEntity entity = new ResumeLanguageExpEntity();
		Result res = null;

		try {
			entity.resume_Id = resume_id;
			entity.resume_time = resume_time;

			entity.status = jsonObj.getInt(NODE_STATUS);
			entity.itemid = jsonObj.getString(NODE_ID);
			entity.languageid = jsonObj.getString(NODE_LANGUAGEID);
			entity.masterid = jsonObj.getString(NODE_MASTERID);
			entity.readwriteid = jsonObj.getString(NODE_READWRITEID);
			entity.listenspeakid = jsonObj.getString(NODE_LISTENSPEAKID);
	

			res = new Result(1, "ok");
			entity.bcompleted = isCompletedForLanguageExp(entity);
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				// 保存到数据库，如果
				deleteResumeLanguageExpEntityFromDb(appContext, entity.resume_Id,
						entity.itemid);
				saveResumeLanguageExpEntityToDb(appContext, entity, true);
			}

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {

			entity.validate = res;

		}
		return entity;

	}
	
	
	public static JSONObject resumeLanguageExpEntityToJSONObject(
			ResumeLanguageExpEntity entity) {

		JSONObject obj = new JSONObject();

		try {
			obj.put(NODE_STATUS, entity.status);
			obj.put(NODE_ID, entity.itemid);
			obj.put(NODE_LANGUAGEID, entity.languageid);
			obj.put(NODE_MASTERID, entity.masterid);
			obj.put(NODE_READWRITEID, entity.readwriteid);
			obj.put(NODE_LISTENSPEAKID, entity.listenspeakid);

		} catch (JSONException e) {
			MyLogger.e(TAG, "resumeBaseinfoEntityToJSONObject "
					+ e);
		}

		return obj;
	}
	
	
	public static boolean deleteResumeLanguageExpEntityFromDb(AppContext appContext,
			String resume_Id, String itemid) {
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_Id;
		sql += " AND " + DBUtils.KEY_RESUME_RESUME_CONTENT_ID + " = " + itemid;
		sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
				+ DBUtils.RESUME_TYPE_LANGUAGEEXP;

		int id = db.deleteBindUser(DBUtils.resumeTableName, sql, null);
		if (id > 0)
			return true;
		else
			return false;
	}
	
	
	public static boolean saveResumeLanguageExpEntityToDb(AppContext appContext,
			ResumeLanguageExpEntity entity, boolean bsubmit) {

		String jsoncontent = resumeLanguageExpEntityToJSONObject(entity).toString();

		DBUtils db = DBUtils.getInstance(appContext);

		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_RESUME_RESUME_ID, entity.resume_Id);
		values.put(DBUtils.KEY_RESUME_RESUME_TIME, entity.resume_time);
		values.put(DBUtils.KEY_RESUME_JSON_CONTENT, jsoncontent);
		values.put(DBUtils.KEY_RESUME_ROWDATA_TYPE, DBUtils.RESUME_TYPE_LANGUAGEEXP);
		values.put(DBUtils.KEY_RESUME_RESUME_CONTENT_ID, entity.itemid);
		
		boolean bcompleted = entity.bcompleted;//isCompletedForLanguageExp(entity);
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
	
	public static boolean isCompletedForLanguageExp(ResumeLanguageExpEntity entity) {
		boolean bcompleted = true;

		if (bcompleted
				&& (entity.languageid == null || entity.languageid.length() == 0))
			bcompleted = false;

		if (bcompleted
				&& (entity.masterid == null || entity.masterid.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.readwriteid == null || entity.readwriteid.length() == 0))
			bcompleted = false;
		if (bcompleted
				&& (entity.listenspeakid == null || entity.listenspeakid.length() == 0))
			bcompleted = false;


		return bcompleted;

	}
}

package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.database.DBUtils;

public class ResumeEntity extends Entity {
	public static final String NODE_RESUME_ID = "resume_id";
	public static final String NODE_RESUME_TIME = "modified";

	public static final String NODE_BASE = "base";
	public static final String NODE_JOBS = "jobs";
	public static final String NODE_PROJECTS = "projects";
	public static final String NODE_EDUCATIONS = "education";
	public static final String NODE_LANGUAGES = "language";
	public static final String NODE_QUICKITEM = "quick_resume";

	public static final int RESUME_BLOCK_UNKONW = -1;
	public static final int RESUME_BLOCK_BASE = 1;
	public static final int RESUME_BLOCK_JOBS = 2;
	public static final int RESUME_BLOCK_PROJECTS = 3;
	public static final int RESUME_BLOCK_EDUCATIONS = 4;
	public static final int RESUME_BLOCK_LANGUAGES = 5;
	public static final int RESUME_BLOCK_QUICKITEM = 6;

	// 简历各个模块的状态，1为正常，2为删除；
	public static final int RESUME_BLOCKSTATUS_NORMAL = 1;
	public static final int RESUME_BLOCKSTATUS_DEL = 2;

	private Result validate;

	private String resume_Id;
	private String resume_time;
	private ResumeBaseinfoEntity baseinfoEntity = new ResumeBaseinfoEntity();
	private List<ResumeJobExpEntity> mJobexpEntityList = new ArrayList<ResumeJobExpEntity>();
	private List<ResumeProjectExpEntity> mprojectExpEntityList = new ArrayList<ResumeProjectExpEntity>();
	private List<ResumeEducationExpEntity> meducationExpEntityList = new ArrayList<ResumeEducationExpEntity>();
	private List<ResumeLanguageExpEntity> mlanguageExpEntityList = new ArrayList<ResumeLanguageExpEntity>();
	private ResumeQuickItemEntity qucikEntity = new ResumeQuickItemEntity();

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

	public final ResumeBaseinfoEntity getBaseinfoEntity() {
		return baseinfoEntity;
	}

	public final void setBaseinfoEntity(ResumeBaseinfoEntity baseinfoEntity) {
		this.baseinfoEntity = baseinfoEntity;
	}

	public final List<ResumeJobExpEntity> getmJobexpEntityList() {
		return mJobexpEntityList;
	}

	public final void setmJobexpEntityList(
			List<ResumeJobExpEntity> mJobexpEntityList) {
		this.mJobexpEntityList = mJobexpEntityList;
	}

	public final List<ResumeProjectExpEntity> getMprojectExpEntityList() {
		return mprojectExpEntityList;
	}

	public final void setMprojectExpEntityList(
			List<ResumeProjectExpEntity> mprojectExpEntityList) {
		this.mprojectExpEntityList = mprojectExpEntityList;
	}

	public final List<ResumeEducationExpEntity> getMeducationExpEntityList() {
		return meducationExpEntityList;
	}

	public final void setMeducationExpEntityList(
			List<ResumeEducationExpEntity> meducationExpEntityList) {
		this.meducationExpEntityList = meducationExpEntityList;
	}

	public final List<ResumeLanguageExpEntity> getMlanguageExpEntityList() {
		return mlanguageExpEntityList;
	}

	public final void setMlanguageExpEntityList(
			List<ResumeLanguageExpEntity> mlanguageExpEntityList) {
		this.mlanguageExpEntityList = mlanguageExpEntityList;
	}

	public final ResumeQuickItemEntity getQucikEntity() {
		return qucikEntity;
	}

	public final void setQucikEntity(ResumeQuickItemEntity qucikEntity) {
		this.qucikEntity = qucikEntity;
	}

	public static ResumeEntity parse_all(AppContext appContext,
			InputStream inputStream, boolean bsavetoDb) throws IOException,
			AppException {

		ResumeEntity entity = new ResumeEntity();
		Result res = null;
		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}

		try {
			String content = builder.toString();
			content = content.replaceAll("null", "\"\"");
			JSONObject jsonObj = new JSONObject(content);
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");
			res = new Result(status, statusmsg);

			entity.validate = res;
			if (!res.OK())
				return entity;
			JSONObject rootobj = jsonObj.getJSONObject("data");

			entity.resume_Id = rootobj.getString(NODE_RESUME_ID);
			entity.resume_time = rootobj.getString(NODE_RESUME_TIME);
			if (bsavetoDb) {
				deleteResumeIdItemFromDb(appContext, entity.resume_Id);
				saveResumeIdItemToDb(appContext, entity.resume_Id,
						entity.resume_time, true);
			}
			JSONObject baseObj = rootobj.getJSONObject(NODE_BASE);
			entity.baseinfoEntity = ResumeBaseinfoEntity.parse(appContext,
					baseObj, bsavetoDb, entity.resume_Id, entity.resume_time);

			JSONArray jobObjs = rootobj.getJSONArray(NODE_JOBS);
			for (int i = 0; i < jobObjs.length(); i++) {
				JSONObject jobobj = (JSONObject) jobObjs.opt(i);
				ResumeJobExpEntity jobentity = ResumeJobExpEntity.parse(
						appContext, jobobj, bsavetoDb, entity.resume_Id,
						entity.resume_time);

				if (jobentity != null && jobentity.getValidate().OK()) {
					entity.mJobexpEntityList.add(jobentity);
				}
			}

			JSONArray projectObjs = rootobj.getJSONArray(NODE_PROJECTS);
			for (int i = 0; i < projectObjs.length(); i++) {
				JSONObject obj = (JSONObject) projectObjs.opt(i);
				ResumeProjectExpEntity projectentity = ResumeProjectExpEntity
						.parse(appContext, obj, bsavetoDb, entity.resume_Id,
								entity.resume_time);

				if (projectentity != null && projectentity.getValidate().OK()) {
					entity.mprojectExpEntityList.add(projectentity);
				}
			}

			JSONArray educationObjs = rootobj.getJSONArray(NODE_EDUCATIONS);
			int length = educationObjs.length();
			for (int i = 0; i < educationObjs.length(); i++) {
				JSONObject obj = (JSONObject) educationObjs.opt(i);
				ResumeEducationExpEntity eduentity = ResumeEducationExpEntity
						.parse(appContext, obj, bsavetoDb, entity.resume_Id,
								entity.resume_time);

				if (eduentity != null && eduentity.getValidate().OK()) {
					entity.meducationExpEntityList.add(eduentity);
				}
			}

			JSONArray languageObjs = rootobj.getJSONArray(NODE_LANGUAGES);
			for (int i = 0; i < languageObjs.length(); i++) {
				JSONObject obj = (JSONObject) languageObjs.opt(i);
				ResumeLanguageExpEntity lagentity = ResumeLanguageExpEntity
						.parse(appContext, obj, bsavetoDb, entity.resume_Id,
								entity.resume_time);

				if (lagentity != null && lagentity.getValidate().OK()) {
					entity.mlanguageExpEntityList.add(lagentity);
				}
			}
			if (rootobj.has(NODE_QUICKITEM)) {
				JSONObject quickitemObj = rootobj.getJSONObject(NODE_QUICKITEM);
				entity.qucikEntity = ResumeQuickItemEntity.parse(appContext,
						quickitemObj, bsavetoDb, entity.resume_Id,
						entity.resume_time);
			}

			res = new Result(status, statusmsg);

		} catch (JSONException e) {
			res = new Result(-1, "json error");
			throw AppException.json(e);

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.io(e);
		} finally {
			inputStream.close();
			entity.validate = res;

		}
		return entity;
	}

	/**
	 * 解析从网络上单独获取的job数据
	 * 
	 * @param appContext
	 * @param inputStream
	 * @param bsavetoDb
	 * @param resume_Id
	 * @param resume_time
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static List<ResumeJobExpEntity> parse_job(AppContext appContext,
			InputStream inputStream, boolean bsavetoDb, String resume_Id,
			String resume_time) throws IOException, AppException {
		List<ResumeJobExpEntity> list = new ArrayList<ResumeJobExpEntity>();

		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}

		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");

			JSONObject rootobj = jsonObj.getJSONObject("data");

			JSONArray jobObjs = rootobj.getJSONArray(NODE_JOBS);
			for (int i = 0; i < jobObjs.length(); i++) {
				JSONObject jobobj = (JSONObject) jobObjs.opt(i);
				ResumeJobExpEntity jobentity = ResumeJobExpEntity.parse(
						appContext, jobobj, bsavetoDb, resume_Id, resume_time);

				if (jobentity != null && jobentity.getValidate().OK()) {
					list.add(jobentity);
				}
			}

		} catch (JSONException e) {
			throw AppException.json(e);

		} catch (Exception e) {
			throw AppException.io(e);
		} finally {
			inputStream.close();
		}
		return list;
	}

	/**
	 * 解析单独从网络上获取的项目信息
	 * 
	 * @param appContext
	 * @param inputStream
	 * @param bsavetoDb
	 * @param resume_Id
	 * @param resume_time
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static List<ResumeProjectExpEntity> parse_project(
			AppContext appContext, InputStream inputStream, boolean bsavetoDb,
			String resume_Id, String resume_time) throws IOException,
			AppException {
		List<ResumeProjectExpEntity> list = new ArrayList<ResumeProjectExpEntity>();

		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}

		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");

			JSONObject rootobj = jsonObj.getJSONObject("data");

			JSONArray Objs = rootobj.getJSONArray(NODE_PROJECTS);
			for (int i = 0; i < Objs.length(); i++) {
				JSONObject jobobj = (JSONObject) Objs.opt(i);
				ResumeProjectExpEntity jobentity = ResumeProjectExpEntity
						.parse(appContext, jobobj, bsavetoDb, resume_Id,
								resume_time);

				if (jobentity != null && jobentity.getValidate().OK()) {
					list.add(jobentity);
				}
			}

		} catch (JSONException e) {
			throw AppException.json(e);

		} catch (Exception e) {
			throw AppException.io(e);
		} finally {
			inputStream.close();
		}
		return list;
	}

	/**
	 * 解析单独从网络上获取的教育信息
	 * 
	 * @param appContext
	 * @param inputStream
	 * @param bsavetoDb
	 * @param resume_Id
	 * @param resume_time
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static List<ResumeEducationExpEntity> parse_education(
			AppContext appContext, InputStream inputStream, boolean bsavetoDb,
			String resume_Id, String resume_time) throws IOException,
			AppException {
		List<ResumeEducationExpEntity> list = new ArrayList<ResumeEducationExpEntity>();

		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}

		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");

			JSONObject rootobj = jsonObj.getJSONObject("data");

			JSONArray Objs = rootobj.getJSONArray(NODE_PROJECTS);
			for (int i = 0; i < Objs.length(); i++) {
				JSONObject jobobj = (JSONObject) Objs.opt(i);
				ResumeEducationExpEntity jobentity = ResumeEducationExpEntity
						.parse(appContext, jobobj, bsavetoDb, resume_Id,
								resume_time);

				if (jobentity != null && jobentity.getValidate().OK()) {
					list.add(jobentity);
				}
			}

		} catch (JSONException e) {
			throw AppException.json(e);

		} catch (Exception e) {
			throw AppException.io(e);
		} finally {
			inputStream.close();
		}
		return list;
	}

	/**
	 * 解析单独从网络上获取的语言技能信息
	 * 
	 * @param appContext
	 * @param inputStream
	 * @param bsavetoDb
	 * @param resume_Id
	 * @param resume_time
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static List<ResumeLanguageExpEntity> parse_language(
			AppContext appContext, InputStream inputStream, boolean bsavetoDb,
			String resume_Id, String resume_time) throws IOException,
			AppException {
		List<ResumeLanguageExpEntity> list = new ArrayList<ResumeLanguageExpEntity>();

		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}

		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");

			JSONObject rootobj = jsonObj.getJSONObject("data");

			JSONArray Objs = rootobj.getJSONArray(NODE_PROJECTS);
			for (int i = 0; i < Objs.length(); i++) {
				JSONObject jobobj = (JSONObject) Objs.opt(i);
				ResumeLanguageExpEntity jobentity = ResumeLanguageExpEntity
						.parse(appContext, jobobj, bsavetoDb, resume_Id,
								resume_time);

				if (jobentity != null && jobentity.getValidate().OK()) {
					list.add(jobentity);
				}
			}

		} catch (JSONException e) {
			throw AppException.json(e);

		} catch (Exception e) {
			throw AppException.io(e);
		} finally {
			inputStream.close();
		}
		return list;
	}

	public static boolean deleteResumeIdItemFromDb(AppContext appContext,
			String resume_Id) {
		DBUtils db = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_Id;
		sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
				+ DBUtils.RESUME_TYPE_RESUME;

		int id = db.deleteBindUser(DBUtils.resumeTableName, sql, null);
		if (id > 0)
			return true;
		else
			return false;
	}

	public static boolean saveResumeIdItemToDb(AppContext appContext,
			String resume_Id, String resume_time, boolean bsubmit) {

		DBUtils db = DBUtils.getInstance(appContext);

		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_RESUME_RESUME_ID, resume_Id);
		values.put(DBUtils.KEY_RESUME_RESUME_TIME, resume_time);
		values.put(DBUtils.KEY_RESUME_ROWDATA_TYPE, DBUtils.RESUME_TYPE_RESUME);

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
}

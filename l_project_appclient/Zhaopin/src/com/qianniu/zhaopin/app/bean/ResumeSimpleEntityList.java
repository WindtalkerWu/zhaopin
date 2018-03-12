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

import android.database.Cursor;
import android.util.Log;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.database.DBUtils;

public class ResumeSimpleEntityList extends Entity {

	private Result validate;
	private int pagesize = 0;
	private List<ResumeSimpleEntity> entitylist = new ArrayList<ResumeSimpleEntity>();

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public final List<ResumeSimpleEntity> getEntitylist() {
		return entitylist;
	}

	public final void setEntitylist(List<ResumeSimpleEntity> entitylist) {
		this.entitylist = entitylist;
	}

	public static ResumeSimpleEntityList parse(AppContext appContext,
			InputStream inputStream, boolean bsavetoDb) throws IOException,
			AppException {

		ResumeSimpleEntityList entity = new ResumeSimpleEntityList();
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
			long updatetime = System.currentTimeMillis();
			if (res.OK()) {
				JSONArray jsonObjs = jsonObj.getJSONArray("data");
				for (int i = 0; i < jsonObjs.length(); i++) {
					JSONObject childjsonObj = (JSONObject) jsonObjs.opt(i);
					ResumeSimpleEntity dataEntity = ResumeSimpleEntity.parse(
							appContext, childjsonObj, true);
					if (dataEntity != null && dataEntity.getValidate().OK()) {
						entity.getEntitylist().add(dataEntity);
					}
				}
			}
			entity.validate = res;
			DBUtils db = DBUtils.getInstance(appContext);
			String sql = DBUtils.KEY_SIMPLERESUME_TIMESTAMP+ " < " + updatetime;
			int num = db.deleteBindUser(DBUtils.simpleResumeTableName, sql, null);
			//Log.i("resumesimplelist", "num =" + num);

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
}

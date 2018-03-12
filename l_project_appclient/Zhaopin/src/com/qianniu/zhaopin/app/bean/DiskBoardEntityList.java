package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.database.DBUtils;

public class DiskBoardEntityList extends Entity {
	private Result validate;
	private List<DiskBoardEntity> list = new ArrayList<DiskBoardEntity>();

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public final List<DiskBoardEntity> getList() {
		return list;
	}

	public final void setList(List<DiskBoardEntity> list) {
		this.list = list;
	}

	public static DiskBoardEntityList parse(AppContext appContext,
			InputStream inputStream, boolean bsavetoDb) throws IOException,
			AppException {

		DiskBoardEntityList list = new DiskBoardEntityList();
		Result res = null;
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
			JSONArray jsonObjs = jsonObj.getJSONArray("data");
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject childjsonObj = (JSONObject) jsonObjs.opt(i);
				DiskBoardEntity entity = DiskBoardEntity.parse(appContext,
						childjsonObj, bsavetoDb);
				if (entity != null && entity.getValidate().OK()) {
					list.getList().add(entity);
				}
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
			list.validate = res;

		}
		return list;
	}


	/**从网络获取数据
	 * @param appContext
	 * @param url
	 * @return
	 * @throws AppException
	 */
	public static DiskBoardEntityList getDiskBoardDataFromNet(
			AppContext appContext, String url) throws AppException {

		String Url = URLs.formatURL(url);

		JSONObject request = new JSONObject();

		String requestinfo = request.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				requestinfo);
		boolean bsave = true;

		try {
			return DiskBoardEntityList.parse(appContext,
					ApiClient._post(appContext, Url, params, null), bsave);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}
	public static DiskBoardEntityList getDiskBoardDataFromDb(
			AppContext appContext, long offsetid, boolean backward,
			int pagesize, int startoffset) {

		DBUtils db = DBUtils.getInstance(appContext);

		String sql = null;

		Cursor cursor = db.queryBindUser(true, DBUtils.dashboardTableName,
				new String[] { "*" }, sql, null, null, null,
				DBUtils.KEY_DASHBOARD_ID + " DESC", null);
		DiskBoardEntityList enitylist = parseDiskBoardEntityFromCursor(cursor);
		cursor.close();
		db.close();
		
		if (enitylist.getList().size() == 0)
			enitylist = null;
		else{
			Result r = new Result(1, "ok");
			enitylist.setValidate(r);
		}
		
		return enitylist;

	}
	/**
	 * 解析数据
	 * 
	 * @param cursor
	 * @return
	 */
	public static DiskBoardEntityList parseDiskBoardEntityFromCursor(
			Cursor cursor) {
		if (cursor == null)
			return null;
		DiskBoardEntityList enitylist = new DiskBoardEntityList();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			DiskBoardEntity entity = new DiskBoardEntity();

			int idColumn = cursor.getColumnIndex(DBUtils.KEY_DASHBOARD_ID);
			entity.setEntityId(cursor.getLong(idColumn));

			int titleColumn = cursor
					.getColumnIndex(DBUtils.KEY_DASHBOARD_TITLE);
			entity.setTitle(cursor.getString(titleColumn));

			int picurlColumn = cursor
					.getColumnIndex(DBUtils.KEY_DASHBOARD_LOGOURL);
			entity.setLogoUrl(cursor.getString(picurlColumn));

			int acttypeColumn = cursor
					.getColumnIndex(DBUtils.KEY_DASHBOARD_ACTIONTYPE_ID);
			entity.setActionTypeId(cursor.getInt(acttypeColumn));

			int actionColumn = cursor
					.getColumnIndex(DBUtils.KEY_DASHBOARD_ACTION_ID);
			entity.setActionId(cursor.getInt(actionColumn));

			int modifyColumn = cursor
					.getColumnIndex(DBUtils.KEY_DASHBOARD_MODIFY_DATE);
			entity.setModifieDate(cursor.getString(modifyColumn));

			int receivetimeColumn = cursor
					.getColumnIndex(DBUtils.KEY_DASHBOARD_RECEIVE_TIMESTAMP);
			entity.setReceiveTimestamp(cursor.getLong(receivetimeColumn));

			enitylist.getList().add(entity);
		}
		Result res = new Result(1, "read in db success");
		enitylist.setValidate(res);
		return enitylist;
	}
}

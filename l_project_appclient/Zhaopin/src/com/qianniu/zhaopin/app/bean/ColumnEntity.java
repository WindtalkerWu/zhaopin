package com.qianniu.zhaopin.app.bean;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.database.DBUtils;

/**
 * 订阅管理，栏目数据
 * 
 * @author Administrator
 * 
 */
public class ColumnEntity extends Entity {
	public final static String NODE_ID = "column_id";
	public final static String NODE_TITLE = "column_title";
	public final static String NODE_LOGO = "column_logo";
	public final static String NODE_TIMESTAMP = "cat_upd_timestamp";

	private String columnId;
	private String columnTitle;
	private String columnLogo_url;
	private String timeStamp;

	private Result validate;

	public final String getColumnId() {
		return columnId;
	}

	public final void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public final String getColumnTitle() {
		return columnTitle;
	}

	public final void setColumnTitle(String columnTitle) {
		this.columnTitle = columnTitle;
	}

	public final String getColumnLogo_url() {
		return columnLogo_url;
	}

	public final void setColumnLogo_url(String columnLogo_url) {
		this.columnLogo_url = columnLogo_url;
	}

	public final String getTimeStamp() {
		return timeStamp;
	}

	public final void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public ColumnEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static ColumnEntity parse(AppContext appContext, JSONObject jsonObj,
			int type, boolean bsavetoDb) throws IOException, JSONException,
			AppException {

		ColumnEntity entity = new ColumnEntity();
		Result res = null;

		try {
			entity.columnId = jsonObj.getString(NODE_ID);
			entity.columnTitle = jsonObj.getString(NODE_TITLE);
			entity.columnLogo_url = jsonObj.getString(NODE_LOGO);
			entity.timeStamp = jsonObj.getString(NODE_TIMESTAMP);

			res = new Result(1, "ok");
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				DBUtils db = DBUtils.getInstance(appContext);
				String sql = DBUtils.KEY_ROWDATA_TYPE + " = "
						+ String.valueOf(type) + " AND "
						+ DBUtils.KEY_COLUMN_ID + " = "
						+ String.valueOf(entity.columnId);
				db.deleteBindUser(DBUtils.catalogTableName, sql, null);
				ContentValues values = new ContentValues();
				values.put(DBUtils.KEY_COLUMN_ID, entity.columnId);
				values.put(DBUtils.KEY_JSON_CONTENT, jsonString);
				values.put(DBUtils.KEY_TIMESTAMP,
						String.valueOf(System.currentTimeMillis()));
				values.put(DBUtils.KEY_ROWDATA_TYPE, String.valueOf(type));
				db.saveBindUser(DBUtils.catalogTableName, values);
			}

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {

			entity.validate = res;

		}
		return entity;

	}
}

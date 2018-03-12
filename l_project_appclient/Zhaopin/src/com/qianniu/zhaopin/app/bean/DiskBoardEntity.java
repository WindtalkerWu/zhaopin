package com.qianniu.zhaopin.app.bean;

import java.io.IOException;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.database.DBUtils;

/*{
 "id": "ID",
 "title": "标题",
 "logo": "图片链接地址",
 "action_type": "app内跳转类型. m123",
 "action_id": "app内跳转地址. m131",
 "modified": "更新的时间戳"
 }*/
/**号外首页功能单元数据结构
 * @author Administrator
 *
 */
public class DiskBoardEntity extends Entity {
	public final static String NODE_ID = "id";
	private final static String NODE_TITLE = "title";
	private final static String NODE_LOGO_URL = "logo";
	private final static String NODE_ACTION_TYPE_ID = "action_type";
	private final static String NODE_ACTION_ID = "action_id";
	private final static String NODE_MODIFIED_DATE = "modified";

	private long entityId;
	private String title;
	private String logoUrl;
	private int actionTypeId;
	private int actionId;
	private String modifieDate;
	private long receiveTimestamp;

	private Result validate;

	
	
	public final long getEntityId() {
		return entityId;
	}



	public final void setEntityId(long entityId) {
		this.entityId = entityId;
	}



	public final String getTitle() {
		return title;
	}



	public final void setTitle(String title) {
		this.title = title;
	}



	public final String getLogoUrl() {
		return logoUrl;
	}



	public final void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}



	public final int getActionTypeId() {
		return actionTypeId;
	}



	public final void setActionTypeId(int actionTypeId) {
		this.actionTypeId = actionTypeId;
	}



	public final int getActionId() {
		return actionId;
	}



	public final void setActionId(int actionId) {
		this.actionId = actionId;
	}



	public final String getModifieDate() {
		return modifieDate;
	}



	public final void setModifieDate(String modifieDate) {
		this.modifieDate = modifieDate;
	}



	public final long getReceiveTimestamp() {
		return receiveTimestamp;
	}



	public final void setReceiveTimestamp(long receiveTimestamp) {
		this.receiveTimestamp = receiveTimestamp;
	}



	public final Result getValidate() {
		return validate;
	}



	public final void setValidate(Result validate) {
		this.validate = validate;
	}



	public static DiskBoardEntity parse(AppContext appContext,
			JSONObject jsonObj, boolean bsavetoDb) throws IOException,
			AppException {

		DiskBoardEntity entity = new DiskBoardEntity();
		Result res = null;

		try {
			entity.entityId = jsonObj.getLong(NODE_ID);
			entity.title = jsonObj.getString(NODE_TITLE);
			entity.logoUrl = jsonObj.getString(NODE_LOGO_URL);
			entity.actionTypeId = jsonObj.getInt(NODE_ACTION_TYPE_ID);
			entity.actionId = jsonObj.getInt(NODE_ACTION_ID);
			entity.modifieDate = jsonObj.getString(NODE_MODIFIED_DATE);
			entity.receiveTimestamp = System.currentTimeMillis();

			res = new Result(1, "ok");
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				DBUtils db = DBUtils.getInstance(appContext);
				String sql = DBUtils.KEY_DASHBOARD_ID + " = " + entity.entityId;
				Cursor cursor = db.queryBindUser(true,
						DBUtils.dashboardTableName, new String[] { "*" }, sql,
						null, null, null, DBUtils.KEY_CAMPAIGN_ID + " DESC",
						null);
				if (cursor.getCount() > 0) {
					db.deleteBindUser(DBUtils.dashboardTableName, sql, null);
				}
				cursor.close();

				ContentValues values = new ContentValues();
				values.put(DBUtils.KEY_DASHBOARD_ID, entity.entityId);
				values.put(DBUtils.KEY_DASHBOARD_TITLE, entity.title);
				values.put(DBUtils.KEY_DASHBOARD_LOGOURL, entity.logoUrl);
				values.put(DBUtils.KEY_DASHBOARD_ACTIONTYPE_ID,
						entity.actionTypeId);
				values.put(DBUtils.KEY_DASHBOARD_ACTION_ID, entity.actionId);
				values.put(DBUtils.KEY_DASHBOARD_MODIFY_DATE, entity.modifieDate);
				values.put(DBUtils.KEY_DASHBOARD_RECEIVE_TIMESTAMP,
						entity.receiveTimestamp);
				db.save(DBUtils.dashboardTableName, values);
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

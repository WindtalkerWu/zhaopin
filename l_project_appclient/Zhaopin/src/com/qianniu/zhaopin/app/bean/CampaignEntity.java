package com.qianniu.zhaopin.app.bean;

import java.io.IOException;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.database.DBUtils;

/* {
 "id": "id",
 "title": "活动名称",
 "picture": "活动图片",
 "type": "活动类型 m138",
 "description": "活动简介",
 "startDate": "开始日期",
 "endtDate": "结束日期",
 "h5_url": "活动详情H5",
 "modified": "更新的时间戳"
 }
 */
/**
 * 活动数据实体
 * 
 * @author Administrator
 * 
 */
public class CampaignEntity extends Entity {

	public final static String NODE_ID = "id";
	private final static String NODE_TITLE = "title";
	private final static String NODE_PIC_URL = "picture";
	private final static String NODE_TYPE_ID = "type";
	private final static String NODE_DESCRIPTION = "description";
	private final static String NODE_START_DATE = "startDate";
	private final static String NODE_END_DATE = "endtDate";
	private final static String NODE_DETAIL_URL = "h5_url";
	private final static String NODE_MODIFIED_DATE = "modified";

	private long cpag_id;
	private int cpag_type;
	private String title;
	private String description;
	private String picUrl;
	private String detailUrl;
	private String startDate;
	private String endtDate;
	private String modifieDate;
	private long receiveTimestamp;

	private Result validate;

	public final long getCpagId() {
		return cpag_id;
	}

	public final void setCpagId(long cpag_id) {
		this.cpag_id = cpag_id;
	}

	public final int getCpagType() {
		return cpag_type;
	}

	public final void setCpagType(int cpag_type) {
		this.cpag_type = cpag_type;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final String getPicUrl() {
		return picUrl;
	}

	public final void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public final String getDetailUrl() {
		return detailUrl;
	}

	public final void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public final String getStartDate() {
		return startDate;
	}

	public final void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public final String getEndtDate() {
		return endtDate;
	}

	public final void setEndtDate(String endtDate) {
		this.endtDate = endtDate;
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

	public static CampaignEntity parse(AppContext appContext,
			JSONObject jsonObj, boolean bsavetoDb) throws IOException,
			AppException {

		CampaignEntity entity = new CampaignEntity();
		Result res = null;

		try {
			entity.cpag_id = jsonObj.getLong(NODE_ID);
			entity.cpag_type = jsonObj.getInt(NODE_TYPE_ID);
			entity.title = jsonObj.getString(NODE_TITLE);
			entity.description = jsonObj.getString(NODE_DESCRIPTION);
			entity.picUrl = jsonObj.getString(NODE_PIC_URL);
			entity.detailUrl = jsonObj.getString(NODE_DETAIL_URL);
			entity.startDate = jsonObj.getString(NODE_START_DATE);
			entity.endtDate = jsonObj.getString(NODE_END_DATE);
			entity.modifieDate = jsonObj.getString(NODE_MODIFIED_DATE);
			entity.receiveTimestamp = System.currentTimeMillis();

			res = new Result(1, "ok");
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				DBUtils db = DBUtils.getInstance(appContext);
				String sql = DBUtils.KEY_CAMPAIGN_ID + " = " + entity.cpag_id;
				Cursor cursor = db.queryBindUser(true,
						DBUtils.campaignTableName, new String[] { "*" }, sql,
						null, null, null, DBUtils.KEY_CAMPAIGN_ID + " DESC",
						null);
				if (cursor.getCount() > 0) {
					db.deleteBindUser(DBUtils.campaignTableName, sql, null);
				}
				cursor.close();
				
				ContentValues values = new ContentValues();
				values.put(DBUtils.KEY_CAMPAIGN_ID, entity.cpag_id);
				values.put(DBUtils.KEY_CAMPAIGN_TYPE_ID, entity.cpag_type);
				values.put(DBUtils.KEY_CAMPAIGN_TITLE, entity.title);
				values.put(DBUtils.KEY_CAMPAIGN_DESCRIPTION, entity.description);
				values.put(DBUtils.KEY_CAMPAIGN_PICURL, entity.picUrl);
				values.put(DBUtils.KEY_CAMPAIGN_DEATAILURL, entity.detailUrl);
				values.put(DBUtils.KEY_CAMPAIGN_START_DATE, entity.startDate);
				values.put(DBUtils.KEY_CAMPAIGN_END_DATE, entity.endtDate);
				values.put(DBUtils.KEY_CAMPAIGN_MODIFY_DATE, entity.modifieDate);
				values.put(DBUtils.KEY_CAMPAIGN_RECEIVE_TIMESTAMP,
						entity.receiveTimestamp);
				db.saveBindUser(DBUtils.campaignTableName, values);
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

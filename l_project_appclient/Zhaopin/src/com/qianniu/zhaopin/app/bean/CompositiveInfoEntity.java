package com.qianniu.zhaopin.app.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.database.DBUtils;

/*[
 {
 "cat_id": "板块ID",
 "cat_logo": "板块图片",
 "cat_title": "板块标题",
 "cat_memo": "摘要",
 "post_content": [
 {
 "msg_id": "消息ID",
 "msg_title": "消息标题",
 "msg_img": "消息的缩略图",
 "msg_memo": "摘要",
 "msg_url": "该消息的url"
 }
 ]
 }
 ]*/
/**
 * 号外首页list数据原型
 * 注：此数据结构暂不使用
 * @author Administrator
 * 
 */
public class CompositiveInfoEntity extends Entity {

	public final static String NODE_CAT_ID = "cat_id";
	public final static String NODE_CAT_TITLE = "cat_title";
	public final static String NODE_CAT_LOGO = "cat_logo";
	public final static String NODE_CAT_DIGEST = "cat_memo";
	public final static String NODE_MSG_ID = "msg_id";
	public final static String NODE_MSG_TITLE = "msg_title";
	public final static String NODE_MSG_IMG = "msg_img";
	public final static String NODE_MSG_DIGEST = "msg_memo";
	public final static String NODE_MSG_URL = "msg_url";
	public final static String NODE_POSTCONTENT = "post_content";
	public final static String NODE_MSG_STAMPTIME = "msg_upd_timestamp";

	private String catId;
	private String catTitle;
	private String catLogo_url;
	private String catDigest;
	private String msgId;
	private String msgTitle;
	private String msgImg_url;
	private String msg_url;
	private String timeStamp;

	private Result validate;

	public static CompositiveInfoEntity parse(AppContext appContext,
			JSONObject jsonObj, boolean bsavetoDb, int row_type)
			throws IOException, JSONException, AppException {

		CompositiveInfoEntity entity = new CompositiveInfoEntity();
		ForumType infotype = new ForumType();
		Result res = null;

		try {
			entity.catId = jsonObj.getString(NODE_CAT_ID);
			entity.catTitle = jsonObj.getString(NODE_CAT_TITLE);
			entity.catLogo_url = jsonObj.getString(NODE_CAT_LOGO);
			entity.catDigest = jsonObj.getString(NODE_CAT_DIGEST);

			entity.msgId = jsonObj.getString(NODE_MSG_ID);
			entity.msgTitle = jsonObj.getString(NODE_MSG_TITLE);
			entity.msgImg_url = jsonObj.getString(NODE_MSG_IMG);
			entity.msg_url = jsonObj.getString(NODE_MSG_URL);
			entity.timeStamp = jsonObj.getString(NODE_MSG_STAMPTIME);

			res = new Result(1, "ok");
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				DBUtils db = DBUtils.getInstance(appContext);
				String sql = DBUtils.KEY_MSG_ID + " = " + entity.msgId
						+ " AND " + DBUtils.KEY_ROWDATA_TYPE + " = "
						+ String.valueOf(row_type);
				db.deleteBindUser(DBUtils.infoTableName, sql, null);

				ContentValues values = new ContentValues();
				values.put(DBUtils.KEY_CAT_ID, entity.catId);
				values.put(DBUtils.KEY_MSG_ID, entity.msgId);
				values.put(DBUtils.KEY_ROWDATA_TYPE, String.valueOf(row_type));
				values.put(DBUtils.KEY_JSON_CONTENT, jsonString);
				values.put(DBUtils.KEY_TIMESTAMP,
						String.valueOf(System.currentTimeMillis()));

				db.saveBindUser(DBUtils.infoTableName, values);
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

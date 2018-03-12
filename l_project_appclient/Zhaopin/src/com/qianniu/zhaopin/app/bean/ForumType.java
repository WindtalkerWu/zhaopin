package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.database.DBUtils;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Xml;

/**
 * 版块实体类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
/*
 * { "cat_id": "板块ID", "cat_logo": "板块图片", "cat_title": "板块标题", "cat_memo":
 * "摘要", "cat_post_count": "子模块新增的消息数", "cat_child_type": "子模块类型. 1=>列表,2=>小报",
 * "cat_upd_timestamp": "更新的时间戳", "subscription_type": "0,1,2(0:default不可更改,1:已订阅,2:未订阅)",
 *  "purview_type": "1=>官方消息,2=>个人消息(需要登陆),3=>号外资讯,4=>推送消息"}
 */
public class ForumType extends Entity {

	public final static String NODE_ID = "cat_id";
	public final static String NODE_TITLE = "cat_title";
	public final static String NODE_LOGO = "cat_logo";
	public final static String NODE_DIGEST = "cat_memo";
	public final static String NODE_COUNT_NEW = "cat_post_count";
	public final static String NODE_CHILD_DISPLAYTYPE = "cat_child_type";
	public final static String NODE_TIMESTAMP = "cat_upd_timestamp";
	public final static String NODE_COLUMNID = "column_id";
	public final static String NODE_CUSTOM_TYPE = "subscription_type";
	public final static String NODE_PREVIEW_TYPE = "purview_type";

	public final static int CUSTOM_TYPE_DEFAULT = 0;
	public final static int CUSTOM_TYPE_SELECTED = 1;
	public final static int CUSTOM_TYPE_UNSELECTED = 2;
	
	public final static int PREVIEW_TYPE_SYSTEM = 1;

	private String infoId;
	private String title;
	private String infoLogo_url;
	private String digest;
	private int newCount;
	private int childDisplayType;
	private String timeStamp;
	private String columnId;
	private int cutomType;
	private int previewType ;

	private Result validate;

	public ForumType() {

	}

	public final String getColumnId() {
		return columnId;
	}

	public final void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public final int getCutomType() {
		return cutomType;
	}

	public final void setCutomType(int cutomType) {
		this.cutomType = cutomType;
	}

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public final String getInfoId() {
		return infoId;
	}

	public final void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final String getInfoLogo_url() {
		return infoLogo_url;
	}

	public final void setInfoLogo_url(String infoLogo_url) {
		this.infoLogo_url = infoLogo_url;
	}

	public final String getDigest() {
		return digest;
	}

	public final void setDigest(String digest) {
		this.digest = digest;
	}

	public final int getNewCount() {
		return newCount;
	}

	public final void setNewCount(int newCount) {
		this.newCount = newCount;
	}

	public final int getChildDisplayType() {
		return childDisplayType;
	}

	public final void setChildDisplayType(int childDisplayType) {
		this.childDisplayType = childDisplayType;
	}

	public final String getTimeStamp() {
		return timeStamp;
	}

	public final void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	


	// 如果不需要保存到数据库，
	/**
	 * @param appContext
	 * @param jsonObj
	 * @param bsavetoDb
	 *            ：是否保存到数据库
	 * @param cat_type
	 *            ：版块类型，用于在保存到数据库的时候，保存版块信息
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 * @throws AppException
	 */
	public static ForumType parse(AppContext appContext, JSONObject jsonObj,
			boolean bsavetoDb, int cat_type) throws IOException, JSONException,
			AppException {

		ForumType infotype = new ForumType();
		Result res = null;

		try {
			infotype.infoId = jsonObj.getString(NODE_ID);
			infotype.title = jsonObj.getString(NODE_TITLE);
			infotype.infoLogo_url = jsonObj.getString(NODE_LOGO);
			infotype.digest = jsonObj.getString(NODE_DIGEST);
			infotype.newCount = jsonObj.getInt(NODE_COUNT_NEW);
			infotype.childDisplayType = jsonObj.getInt(NODE_CHILD_DISPLAYTYPE);
			infotype.timeStamp = jsonObj.getString(NODE_TIMESTAMP);
			if (!jsonObj.isNull(NODE_COLUMNID)) {
				infotype.columnId = jsonObj.getString(NODE_COLUMNID);
			}

			if (!jsonObj.isNull(NODE_CUSTOM_TYPE)) {
				infotype.cutomType = jsonObj.getInt(NODE_CUSTOM_TYPE);
			}
			if (!jsonObj.isNull(NODE_PREVIEW_TYPE)) {
				infotype.previewType = jsonObj.getInt(NODE_PREVIEW_TYPE);
			}
			
			int ureadcount = 0;
			boolean isexist = false;
			DBUtils db = DBUtils.getInstance(appContext);
			String sql = DBUtils.KEY_CAT_ID + " = " + infotype.infoId;
			sql += " AND " + DBUtils.KEY_ROWDATA_TYPE + " = " + cat_type;
			Cursor cursor = db.queryBindUser(true, DBUtils.catalogTableName,
					new String[] { "*" }, sql, null, null, null, null, null);
			if (cursor != null){
				if(cursor.getCount() > 0) {
					ureadcount = cursor.getInt(cursor
							.getColumnIndex(DBUtils.KEY_UNREAD_COUNT));
					isexist = true;
				}
				
				cursor.close();
			}
			
			//新方案，直接使用后端返回条数，不用累加
			//infotype.newCount += ureadcount;
			
			res = new Result(1, "ok");
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				if (isexist)
					db.deleteBindUser(DBUtils.catalogTableName, sql, null);

				ContentValues values = new ContentValues();
				values.put(DBUtils.KEY_CAT_ID, infotype.infoId);
				values.put(DBUtils.KEY_JSON_CONTENT, jsonString);
				values.put(DBUtils.KEY_TIMESTAMP,
						String.valueOf(System.currentTimeMillis()));
				values.put(DBUtils.KEY_ROWDATA_TYPE, String.valueOf(cat_type));
				values.put(DBUtils.KEY_UNREAD_COUNT, String.valueOf(infotype.newCount));
				values.put(DBUtils.KEY_CUSTOM_TYPE, String.valueOf(infotype.cutomType));
				values.put(DBUtils.KEY_PREVIEW_TYPE, String.valueOf(infotype.previewType));
				if (infotype.columnId != null && infotype.columnId.length() > 0) {
					values.put(DBUtils.KEY_COLUMN_ID,
							String.valueOf(infotype.columnId));
				}
				db.saveBindUser(DBUtils.catalogTableName, values);
			}

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {

			infotype.validate = res;

		}
		return infotype;

	}
}

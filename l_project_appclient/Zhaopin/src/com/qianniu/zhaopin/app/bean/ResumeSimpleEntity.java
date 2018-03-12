package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.database.DBUtils;

/*{
    "id": "1",
    "name": "姓名",
    "title": "头衔",
    "identity_status": "个人身份. 1=>已认证,2=>未认证",
    "phone_status": "手机. 1=>已认证,2=>未认证",
    "mail_status": "邮箱. 1=>已认证,2=>未认证",
    "picture": "头像",
    "created_time": "创建时间",
    "modified": "更新时间",
    "url": "简历URL. 请求的时候必须带token",
    "selected_status": "是否为默认简历. 1=>默认"
     "percent": "建立完整度"
  }*/
public class ResumeSimpleEntity extends Entity implements Serializable{
	public final static int RESUME_UNAUTHENTICATED  = 0;
	public final static int RESUME_AUTHENTICATED  = 1;
	
	public final static int RESUME_DEFAULT_UNSELECTED  = 0;
	public final static int RESUME_DEFAULT_SELECTED  = 1;
	
	public static final String NODE_ID = "id";
	public static final String NODE_NAME = "name";
	public static final String NODE_PERSONALTITLE = "title";
	public static final String NODE_PICTUREURL = "picture";
	public static final String NODE_CREAT_TIME= "created_time";
	public static final String NODE_MODIFI_TIME= "modified";
	public static final String NODE_RESUME_URL = "url";
	public static final String NODE_IDENTITY_STATUS = "identity_status";
	public static final String NODE_PHONE_STATUS= "phone_status";
	public static final String NODE_MAIL_STATUS= "mail_status";
	public static final String NODE_DEFAULT_SELECTED = "selected_status";
	public static final String NODE_COMPLETE_PERCENT= "percent";
	public static final String NODE_SELFMEMO= "memo";
	
	
	
	private String resumeId;
	private String createTime;
	private String modifyTime;
	private String name;
	private String personalTitle;
	private String headphotoUrl;
	private String resumeUrl;
	private int identityAuthenticat = RESUME_UNAUTHENTICATED;
	private int phoneAuthenticat = RESUME_UNAUTHENTICATED;
	private int mailAuthenticat = RESUME_UNAUTHENTICATED;
	private int defaultAuthenticat = RESUME_DEFAULT_UNSELECTED ;
	private int percent=0;
	private String selfmemo = "";
	
	private boolean bselected = false;
	
	
	public final boolean isSelected() {
		return bselected;
	}

	public final void setSelected(boolean bselected) {
		this.bselected = bselected;
	}

	private Result validate;
	
	public final String getResumeId() {
		return resumeId;
	}

	public final void setResumeId(String resumeId) {
		this.resumeId = resumeId;
	}

	public final String getCreateTime() {
		return createTime;
	}

	public final void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public final String getModifyTime() {
		return modifyTime;
	}

	public final void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	/**获取个人
	 * @return
	 */
	public final String getSelfmemo() {
		return selfmemo;
	}

/*	public final void setSelfmemo(String selfmemo) {
		this.selfmemo = selfmemo;
	}*/

	public final String getPersonalTitle() {
		return personalTitle;
	}

	public final void setPersonalTitle(String personalTitle) {
		this.personalTitle = personalTitle;
	}

	public final String getHeadphotoUrl() {
		return headphotoUrl;
	}

	public final void setHeadphotoUrl(String headphotoUrl) {
		this.headphotoUrl = headphotoUrl;
	}

	public final String getResumeUrl() {
		return resumeUrl;
	}

	public final void setResumeUrl(String resumeUrl) {
		this.resumeUrl = resumeUrl;
	}



	public final int getIdentityAuthenticat() {
		return identityAuthenticat;
	}

	public final void setIdentityAuthenticat(int identityAuthenticat) {
		this.identityAuthenticat = identityAuthenticat;
	}

	public final int getPhoneAuthenticat() {
		return phoneAuthenticat;
	}

	public final void setPhoneAuthenticat(int phoneAuthenticat) {
		this.phoneAuthenticat = phoneAuthenticat;
	}

	public final int getMailAuthenticat() {
		return mailAuthenticat;
	}

	public final void setMailAuthenticat(int mailAuthenticat) {
		this.mailAuthenticat = mailAuthenticat;
	}

	public final int getDefaultAuthenticat() {
		return defaultAuthenticat;
	}

	public final void setDefaultAuthenticat(int defaultAuthenticat) {
		this.defaultAuthenticat = defaultAuthenticat;
	}

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public final int getPercent() {
		return percent;
	}

	public final void setPercent(int percent) {
		this.percent = percent;
	}

	public static ResumeSimpleEntity parse(AppContext appContext, JSONObject jsonObj,
			boolean bsavetoDb) throws IOException, JSONException, AppException {

		ResumeSimpleEntity entity = new ResumeSimpleEntity();
		Result res = null;

		try {
			entity.resumeId = jsonObj.getString(NODE_ID);
			entity.name = jsonObj.getString(NODE_NAME);
			entity.createTime = jsonObj.getString(NODE_CREAT_TIME);
			entity.modifyTime = jsonObj.getString(NODE_MODIFI_TIME);
			entity.personalTitle = jsonObj.getString(NODE_PERSONALTITLE);
			entity.headphotoUrl = jsonObj.getString(NODE_PICTUREURL);
			entity.resumeUrl = jsonObj.getString(NODE_RESUME_URL);
			entity.identityAuthenticat = jsonObj.getInt(NODE_IDENTITY_STATUS);
			entity.phoneAuthenticat = jsonObj.getInt(NODE_PHONE_STATUS);
			entity.mailAuthenticat = jsonObj.getInt(NODE_MAIL_STATUS);
			entity.defaultAuthenticat = jsonObj.getInt(NODE_DEFAULT_SELECTED);
			entity.percent = jsonObj.getInt(NODE_COMPLETE_PERCENT);
			entity.selfmemo = jsonObj.getString(NODE_SELFMEMO);
			

			res = new Result(1, "ok");
			entity.validate = res;
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				MyLogger.getLogger("ResumeSimpleEntity").i(jsonString.toString());
				DBUtils db = DBUtils.getInstance(appContext);
				String sql = DBUtils.KEY_SIMPLERESUME_RESUME_ID+ " = " + entity.resumeId;
	
				Cursor cursor = db.queryBindUser(true, DBUtils.simpleResumeTableName, new String[] {"*"}, sql, null,
						null, null, null, null);
				if(cursor != null){
					if(cursor.getCount() > 0){
						db.deleteBindUser(DBUtils.simpleResumeTableName, sql, null);
					}
				
					cursor.close();
				}

				ContentValues values = new ContentValues();
				values.put(DBUtils.KEY_SIMPLERESUME_RESUME_ID, entity.resumeId);
				values.put(DBUtils.KEY_SIMPLERESUME_CREATE_TIME, entity.createTime);
				values.put(DBUtils.KEY_SIMPLERESUME_MODIFY_TIME, entity.modifyTime);
				values.put(DBUtils.KEY_SIMPLERESUME_JSON_CONTENT, jsonString);
				values.put(DBUtils.KEY_SIMPLERESUME_TIMESTAMP,
						String.valueOf(System.currentTimeMillis()));
				values.put(DBUtils.KEY_SIMPLERESUME_SELECTED_STATUS, entity.defaultAuthenticat);
				values.put(DBUtils.KEY_SIMPLERESUME_RESUME_NAME, entity.name);
				
				db.saveBindUser(DBUtils.simpleResumeTableName, values);
			}

		} catch (Exception e) {

			throw AppException.json(e);
		}
		return entity;

	}

}

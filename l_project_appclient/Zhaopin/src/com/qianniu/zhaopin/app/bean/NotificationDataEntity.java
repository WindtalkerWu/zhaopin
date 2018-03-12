package com.qianniu.zhaopin.app.bean;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;

public class NotificationDataEntity   extends Entity{
	public static final String NODE_TITLE = "title";
	public static final String NODE_DESCRIPTION= "description";
	public static final String NODE_ACTIONID = "action_id";
	
	private Result validate;
	
	private String title;
	private String description;
	private int actionId;
	
	
	public final Result getValidate() {
		return validate;
	}


	public final void setValidate(Result validate) {
		this.validate = validate;
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


	public final int getActionId() {
		return actionId;
	}


	public final void setActionId(int actionId) {
		this.actionId = actionId;
	}


	public static NotificationDataEntity parse(AppContext appContext, JSONObject jsonObj,
			boolean bsavetoDb) throws IOException, JSONException, AppException {

		NotificationDataEntity entity = new NotificationDataEntity();
		Result res = null;

		try {
			entity.title = jsonObj.getString(NODE_TITLE);
			entity.description = jsonObj.getString(NODE_DESCRIPTION);
			entity.actionId = jsonObj.getInt(NODE_ACTIONID);

			res = new Result(1, "ok");
			entity.validate = res;

		} catch (Exception e) {
			throw AppException.json(e);
		}
		return entity;

	}
	
}

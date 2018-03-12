package com.qianniu.zhaopin.app.bean;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;

/*{
    "cat_id": "板块ID",
    "cat_logo": "板块图片",
    "cat_title": "板块标题",
    "cat_memo": "摘要",
    "subscription_type": "0,1,2(0:default不可更改,1:已订阅,2:未订阅)"
  }*/
public class SubscriptionDataEntiy extends Entity {
	private final static String NODE_ID = "cat_id";
	private final static String NODE_LOGO = "cat_logo";
	private final static String NODE_TITLE = "cat_title";
	private final static String NODE_MEMO = "cat_memo";
	private final static String NODE_STATUS = "subscription_type";

	public final static int SUBSTYPE_DEFAULT = 0;//不可更改
	public final static int SUBSTYPE_DONE = 1;//已订阅
	public final static int SUBSTYPE_CANCEL = 2;//未订阅
	
	private int catId;
	private String logoUrl ;
	private String title ;
	private String memo ;
	private int subStatus ;
	private Result validate;
	
	
	
	public int getCatId() {
		return catId;
	}



	public void setCatId(int catId) {
		this.catId = catId;
	}



	public int getSubStatus() {
		return subStatus;
	}



	public void setSubStatus(int subStatus) {
		this.subStatus = subStatus;
	}



	public Result getValidate() {
		return validate;
	}



	public void setValidate(Result validate) {
		this.validate = validate;
	}



	public String getLogoUrl() {
		return logoUrl;
	}



	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getMemo() {
		return memo;
	}



	public void setMemo(String memo) {
		this.memo = memo;
	}



	public static SubscriptionDataEntiy parse(AppContext appContext, JSONObject jsonObj
			) throws IOException, JSONException,
			AppException {

		SubscriptionDataEntiy entity = new SubscriptionDataEntiy();
		Result res = null;

		try {
			entity.catId = jsonObj.getInt(NODE_ID);
			entity.logoUrl = jsonObj.getString(NODE_LOGO);
			entity.title = jsonObj.getString(NODE_TITLE);			
			entity.memo = jsonObj.getString(NODE_MEMO);
			entity.subStatus = jsonObj.getInt(NODE_STATUS);
			res = new Result(1, "ok");

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {

			entity.validate = res;

		}
		return entity;

	}
}
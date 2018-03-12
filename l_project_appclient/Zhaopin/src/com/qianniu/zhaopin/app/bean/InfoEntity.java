package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.UIHelper;

/*数据结构
 * {
 "cat_id": "板块ID",
 "cat_title": "板块标题",
 "cat_viewtype": "内容页打开方式. 1=>app内打开, 2=>web view方式打开",
 "msg_id": "消息ID",
 "msg_title": "消息标题",
 "msg_img": "消息的缩略图. 在小报方式打开时需要用到",
 "msg_memo": "摘要",
 "msg_url": "该消息的url",
 "msg_upd_timestamp": "更新的时间戳"
 }*/
/*{
 "cat_id": "板块ID",
 "msg_id": "消息ID",
 "msg_title": "消息标题",
 "msg_img": "消息的缩略图. ",
 "msg_author": "作者. ",
 "msg_content": "内容",
 "msg_memo": "摘要",
 "msg_url": "该消息的url",
 "msg_upd_timestamp": "更新的时间戳",
 "action_type_id": "操作类型 1=>h5链接, 2=>app内调整, 3=>纯文本. 默认1",
 "action_id": "同 zones/jobs: action_id. metadata 131",
 "action_data": "json. 默认空, 为action_type_id=2时的传递参数"
 }*/
/**
 * 消息二级列表实体类
 * 
 * @author Administrator
 * 
 */
public class InfoEntity extends Entity {
	private final static String NODE_CAT_ID = "cat_id";
	private final static String NODE_MSG_ID = "msg_id";
	private final static String NODE_MSG_TITLE = "msg_title";
	private final static String NODE_MSG_IMG = "msg_img";
	private final static String NODE_MSG_AUTHOR = "msg_author";
	private final static String NODE_MSG_CONTENT = "msg_content";
	private final static String NODE_MSG_MEMO = "msg_memo";
	private final static String NODE_MSG_URL = "msg_url";
	private final static String NODE_UPDATE_TIME = "msg_upd_timestamp";
	private final static String NODE_ACTION_TYPE_ID = "action_type_id";
	private final static String NODE_ACTION_ID = "action_id";
	private final static String NODE_ACTION_DATA = "action_data";

	private String forumId = "";
	private String infoId = "";
	private String infoTitle = "";
	private String infoImg = "";
	private String infoDigest = "";
	private String infoContent = "";
	private String infoUrl = "";
	private String infoTimestamp = "";
	private String infoAuthor = "";
	private String reviveTimestamp;
	private int action_type_id;
	private int action_id;
	private String action_data;

	private Result validate;

	public final String getInfoContent() {
		return infoContent;
	}

	public final void setInfoContent(String infoContent) {
		this.infoContent = infoContent;
	}

	public final String getReviveTimestamp() {
		return reviveTimestamp;
	}

	public final void setReviveTimestamp(String reviveTimestamp) {
		this.reviveTimestamp = reviveTimestamp;
	}

	public final String getInfoAuthor() {
		return infoAuthor;
	}

	public final void setInfoAuthor(String infoAuthor) {
		this.infoAuthor = infoAuthor;
	}

	public final String getForumId() {
		return forumId;
	}

	public final void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public final String getInfoId() {
		return infoId;
	}

	public final void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public final String getInfoTitle() {
		return infoTitle;
	}

	public final void setInfoTitle(String infoTitle) {
		this.infoTitle = infoTitle;
	}

	public final String getInfoImg() {
		return infoImg;
	}

	public final void setInfoImg(String infoImg) {
		this.infoImg = infoImg;
	}

	public final String getInfoDigest() {
		return infoDigest;
	}

	public final void setInfoDigest(String infoDigest) {
		this.infoDigest = infoDigest;
	}

	public final String getInfoUrl() {
		return infoUrl;
	}

	public final void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}

	public final String getInfoTimestamp() {
		return infoTimestamp;
	}

	public final void setInfoTimestamp(String infoTimestamp) {
		this.infoTimestamp = infoTimestamp;
	}

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public int getAction_type_id() {
		return action_type_id;
	}

	public void setAction_type_id(int action_type_id) {
		this.action_type_id = action_type_id;
	}

	public int getAction_id() {
		return action_id;
	}

	public void setAction_id(int action_id) {
		this.action_id = action_id;
	}

	public String getAction_data() {
		return action_data;
	}

	public void setAction_data(String action_data) {
		this.action_data = action_data;
	}

	public static InfoEntity parse(JSONObject jsonObj) throws IOException,
			JSONException, AppException {

		InfoEntity info = new InfoEntity();
		Result res = null;

		try {
			info.forumId = jsonObj.getString(NODE_CAT_ID);
			info.infoId = jsonObj.getString(NODE_MSG_ID);
			info.infoTitle = jsonObj.getString(NODE_MSG_TITLE);
			info.infoImg = jsonObj.getString(NODE_MSG_IMG);
			info.infoDigest = jsonObj.getString(NODE_MSG_MEMO);
			info.infoUrl = jsonObj.getString(NODE_MSG_URL);
			info.infoTimestamp = jsonObj.getString(NODE_UPDATE_TIME);
			info.infoAuthor = jsonObj.getString(NODE_MSG_AUTHOR);
			info.infoContent = jsonObj.getString(NODE_MSG_CONTENT);
			if (!jsonObj.isNull(NODE_ACTION_ID))
				info.action_id = jsonObj.getInt(NODE_ACTION_ID);
			if (!jsonObj.isNull(NODE_ACTION_TYPE_ID))
				info.action_type_id = jsonObj.getInt(NODE_ACTION_TYPE_ID);
			if (!jsonObj.isNull(NODE_ACTION_DATA))
				info.action_data = jsonObj.getString(NODE_ACTION_DATA);
			info.reviveTimestamp = String.valueOf(System.currentTimeMillis());
			res = new Result(1, "ok");

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.json(e);
		} finally {

			info.validate = res;

		}
		return info;

	}

	public static InfoEntity parse(InputStream inputStream) throws IOException,
			AppException {

		InfoEntity info = null;
		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}

		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			info = parse(jsonObj);

		} catch (JSONException e) {

			throw AppException.json(e);

		} catch (Exception e) {

			throw AppException.io(e);
		} finally {

		}
		return info;
	}

	public static void actionJump(Context context, InfoEntity entity) {
		if (entity == null || context == null)
			return;
		switch (entity.getAction_type_id()) {
		case ActionJumpEntity.ACTTYPE_NONE: {

		}
			break;
		case ActionJumpEntity.ACTTYPE_INNERJUMP: {
			ActionJumpEntity.performInnerActionJump(context,
					entity.getAction_id(), entity.getAction_data());

		}
			break;
		case ActionJumpEntity.ACTTYPE_DEFAULT:
		case ActionJumpEntity.ACTTYPE_URLJUMP: {
			if (entity.getInfoUrl() != null && !entity.getInfoUrl().isEmpty())
				UIHelper.showInfoDetial(context, entity.getInfoId(),
						entity.getInfoTitle(), entity.getInfoUrl());
		}
			break;
		default:
			break;
		}
	}

}

package com.qianniu.zhaopin.app.bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;

public class GossipMsgEntity extends Entity {
	private static final String TAG = "GossipMsgEntity";
	public static final String NODE_OFFSETID = "offsetid";
	public static final String NODE_OFFSETFIELD = "offsetfield";

	private String offsetid; 
	private String offsetfield; //偏移字段名称. 默认为 id. 后端通过此字段来决定排序
	
	public String getOffsetid() {
		return offsetid;
	}
	public void setOffsetid(String offsetid) {
		this.offsetid = offsetid;
	}
	public String getOffsetfield() {
		return offsetfield;
	}
	public void setOffsetfield(String offsetfield) {
		this.offsetfield = offsetfield;
	}
	public static GossipMsgEntity parse(JSONObject jsonObj) throws AppException {

		GossipMsgEntity entity = new GossipMsgEntity();
		Result res = null;

		try {

			if (jsonObj.has(NODE_OFFSETID) && !jsonObj.isNull(NODE_OFFSETID)) {

				entity.offsetid = jsonObj.getString(NODE_OFFSETID);
			}
			if (jsonObj.has(NODE_OFFSETFIELD) && !jsonObj.isNull(NODE_OFFSETFIELD)) {

				entity.offsetfield = jsonObj.getString(NODE_OFFSETFIELD);
			}

		} catch (Exception e) {

			throw AppException.json(e);
		}
		return entity;

	}
	public static List<GossipMsgEntity> parse(String jsonArrayStr){
		List<GossipMsgEntity> list = new ArrayList<GossipMsgEntity>();
		try {
			JSONArray jsonObjs = new JSONArray(jsonArrayStr);
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject childjsonObj = (JSONObject) jsonObjs.opt(i);
				GossipMsgEntity msg;
				try {
					msg = parse(childjsonObj);
					if (msg != null) {
						list.add(msg);
					}
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}

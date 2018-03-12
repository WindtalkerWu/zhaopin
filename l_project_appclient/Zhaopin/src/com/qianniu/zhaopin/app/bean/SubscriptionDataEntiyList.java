package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.MyLogger;

public class SubscriptionDataEntiyList {
	
	private Result validate;
	private List<SubscriptionDataEntiy> entitylist = new ArrayList<SubscriptionDataEntiy>();
	
	
	
	public Result getValidate() {
		return validate;
	}



	public void setValidate(Result validate) {
		this.validate = validate;
	}



	public List<SubscriptionDataEntiy> getEntitylist() {
		return entitylist;
	}



	public void setEntitylist(List<SubscriptionDataEntiy> entitylist) {
		this.entitylist = entitylist;
	}



	public static SubscriptionDataEntiyList parse(AppContext appContext,
			InputStream inputStream) throws IOException,
			AppException {

		SubscriptionDataEntiyList subDataList = new SubscriptionDataEntiyList();
		Result res = null;
		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}

		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");
			res = new Result(status, statusmsg);
			subDataList.validate = res;
			
			if (res.OK()) {

				JSONArray jsonObjs = jsonObj.getJSONArray("data");
				for (int i = 0; i < jsonObjs.length(); i++) {
					JSONObject childjsonObj = (JSONObject) jsonObjs.opt(i);
					SubscriptionDataEntiy entity = SubscriptionDataEntiy.parse(appContext,
							childjsonObj);
					if (entity != null && entity.getValidate().OK()) {
						subDataList.entitylist.add(entity);
					}
				}
			}

		} catch (JSONException e) {
			res = new Result(-1, "json error");
			throw AppException.json(e);

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.io(e);
		} finally {
			inputStream.close();
			subDataList.validate = res;

		}
		return subDataList;
	}
}

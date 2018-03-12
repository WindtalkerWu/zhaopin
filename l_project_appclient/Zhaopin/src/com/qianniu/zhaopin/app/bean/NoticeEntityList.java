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

import android.util.Log;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.MyLogger;

public class NoticeEntityList extends Entity{
	private final static String TAG = "NoticeEntityList";
	private Result validate;
	private List<NoticeEntity> list = new ArrayList<NoticeEntity>();
	
	
	
	public final Result getValidate() {
		return validate;
	}



	public final void setValidate(Result validate) {
		this.validate = validate;
	}



	public final List<NoticeEntity> getList() {
		return list;
	}



	public final void setList(List<NoticeEntity> list) {
		this.list = list;
	}



	public static NoticeEntityList parse(AppContext appContext,
			InputStream inputStream)
			throws IOException, AppException {

		NoticeEntityList list = new NoticeEntityList();
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
			MyLogger.i(TAG, "NoticeEntityList parase##" + builder.toString());
			res = new Result(status, statusmsg);
			if(res.OK()){
				JSONArray jsonObjs = jsonObj.getJSONArray("data");
				for (int i = 0; i < jsonObjs.length(); i++) {
					try{
						JSONObject childjsonObj = (JSONObject) jsonObjs.opt(i);
						NoticeEntity entity = NoticeEntity.parse(appContext, childjsonObj, false);
						if (entity != null && entity.getValidate().OK()) {
							list.getList().add(entity);
						}
					}catch (JSONException e) {

					}
				}
			}
			
		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.io(e);
		} finally {
			inputStream.close();
			list.validate = res;	
		}
		return list;
	}
}

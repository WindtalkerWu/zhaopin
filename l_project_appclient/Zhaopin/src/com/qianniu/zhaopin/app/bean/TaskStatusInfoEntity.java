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

import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.ObjectUtils;

public class TaskStatusInfoEntity {
	private Result validate;		// 是否成功
	private List<TaskStatusInfo> data;
	
	public TaskStatusInfoEntity(){
		this.data = new ArrayList<TaskStatusInfo>();
	}

	public Result getValidate() {
		return validate;
	}

	public void setValidate(Result validate) {
		this.validate = validate;
	}

	public List<TaskStatusInfo> getData() {
		return data;
	}

	public void setData(List<TaskStatusInfo> data) {
		this.data = data;
	}
	
	public static TaskStatusInfoEntity parse(InputStream inputStream)
			throws IOException, AppException{
		TaskStatusInfoEntity tsie = new TaskStatusInfoEntity();
		
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
			res = new Result(status,statusmsg);
			tsie.setValidate(res);
		
			if(res.OK()){
				JSONArray jsonObjs = jsonObj.getJSONArray("data");
				
				List<TaskStatusInfo> lsTSI = new ArrayList<TaskStatusInfo>();
					
				for (int i = 0; i < jsonObjs.length(); i++) {
					String strTemp = jsonObjs.getString(i);
					
					TaskStatusInfo tsi = (TaskStatusInfo)ObjectUtils.
							getObjectFromJsonString(strTemp, TaskStatusInfo.class);
					
					lsTSI.add(tsi);
				}
	
				tsie.setData(lsTSI);
			}
		} catch (JSONException e) {
			res = new Result(-1, "json error");
			throw AppException.json(e);
		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.io(e);
		} finally {
			inputStream.close();
			tsie.setValidate(res);
		}
		
		return tsie;
	}
}

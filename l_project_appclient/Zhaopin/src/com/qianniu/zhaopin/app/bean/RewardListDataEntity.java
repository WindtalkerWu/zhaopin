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
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ObjectUtils;

public class RewardListDataEntity {
	private static final String TAG = "RewardListDataEntity";
	
	private Result validate;								// 是否成功
	private List<RewardData> data;
	
	public RewardListDataEntity(){
		this.data = new ArrayList<RewardData>();
	}
	
	/**
	 * 获取是否成功标记
	 * @return 是否成功标记
	 */
	public Result getValidate(){
		return this.validate;
	}
	
	/**
	 * 设置是否成功标记
	 * @param res 是否成功标记
	 */
	public void setValidate(Result res){
		this.validate = res;
	}
	
	/**
	 * 获取悬赏任务数据
	 * @return 悬赏任务数据
	 */
	public List<RewardData> getData(){
		return this.data;
	}
	
	/**
	 * 设置悬赏任务数据
	 * @param rld 悬赏任务数据
	 */
	public void setData(List<RewardData> rld){
		this.data = rld;
	}

	/**
	 * @param appContext
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static RewardListDataEntity parse(InputStream inputStream)
			throws IOException, AppException{
		RewardListDataEntity rde = new RewardListDataEntity();
	  
		Result res = null;
		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
			inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
			.readLine()) {
			builder.append(s);
		}
		MyLogger.i(TAG, "parse###" + builder.toString());
		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");
			res = new Result(status,statusmsg);
			rde.setValidate(res);
		
			if(res.OK()){
				JSONArray jsonObjs = jsonObj.getJSONArray("data");
				
				List<RewardData> lrld = new ArrayList<RewardData>();
					
				for (int i = 0; i < jsonObjs.length(); i++) {
					String strTemp = jsonObjs.getString(i);
					
					RewardData rld = (RewardData)ObjectUtils.
							getObjectFromJsonString(strTemp, RewardData.class);
					
					lrld.add(rld);
				}
	
				rde.setData(lrld);
			}
		} catch (JSONException e) {
			res = new Result(-1, "json error");
			throw AppException.json(e);
		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.io(e);
		} finally {
			inputStream.close();
			rde.setValidate(res);
		}
		
	  return rde;
  }
}

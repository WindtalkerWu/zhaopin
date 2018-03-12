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

public class MyAccontInfoDataEntity {
	private static final String TAG = "UserInfoDataEntity";
	
	private Result validate;		// 是否成功
	private MyAccontInfoData data;		// 我的账号相关信息							
	
	public MyAccontInfoDataEntity(){
		this.data = new MyAccontInfoData();
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
	 * 获取我的账号相关信息
	 * @return 我的账号相关信息
	 */
	public MyAccontInfoData getData(){
		return this.data;
	}
	
	/**
	 * 设置我的账号相关信息
	 * @param uid 我的账号相关信息
	 */
	public void setData(MyAccontInfoData uid){
		this.data = uid;
	}
	
	/**
	 * @param appContext
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static MyAccontInfoDataEntity parse(InputStream inputStream)
			throws IOException, AppException{
		
		MyAccontInfoDataEntity uide = new MyAccontInfoDataEntity();
		
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
			uide.setValidate(res);

			if(res.OK()){
				String strData = jsonObj.getString("data");
				
				MyAccontInfoData uid = (MyAccontInfoData)ObjectUtils.
						getObjectFromJsonString(strData, MyAccontInfoData.class);
				
				uide.setData(uid);
			}
		} catch (JSONException e) {
			res = new Result(-1, "json error");
			throw AppException.json(e);

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.io(e);
		} finally {
			inputStream.close();
			uide.setValidate(res);
		}
		
		return uide;
	}
}

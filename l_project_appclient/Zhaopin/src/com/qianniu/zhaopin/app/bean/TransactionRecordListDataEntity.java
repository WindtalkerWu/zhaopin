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
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.ObjectUtils;

public class TransactionRecordListDataEntity {
	private static final String TAG = "TransactionRecordListDataEntity";
	
	private Result validate;					// 是否成功
	private List<TransactionRecordData> data;	// 交易记录数据					
	
	public TransactionRecordListDataEntity(){
		this.data = new ArrayList<TransactionRecordData>();
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
	 * 获取交易记录数据
	 * @return 交易记录数据
	 */
	public List<TransactionRecordData> getData(){
		return this.data;
	}
	
	/**
	 * 设置交易记录数据
	 * @param trd 交易记录数据
	 */
	public void setData(List<TransactionRecordData> trd){
		this.data = trd;
	}

	/**
	 * @param appContext
	 * @param inputStream
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static TransactionRecordListDataEntity parse(InputStream inputStream)
			throws IOException, AppException{
		TransactionRecordListDataEntity trlde = new TransactionRecordListDataEntity();
	  
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
			trlde.setValidate(res);
		
			if(res.OK()){
				JSONArray jsonObjs = jsonObj.getJSONArray("data");
				
				List<TransactionRecordData> lsTRD = new ArrayList<TransactionRecordData>();
					
				for (int i = 0; i < jsonObjs.length(); i++) {
					String strTemp = jsonObjs.getString(i);
					
					TransactionRecordData trd = (TransactionRecordData)ObjectUtils.
							getObjectFromJsonString(strTemp, TransactionRecordData.class);
					
					lsTRD.add(trd);
				}
	
				trlde.setData(lsTRD);
			}
		} catch (JSONException e) {
			res = new Result(-1, "json error");
			throw AppException.json(e);
		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.io(e);
		} finally {
			inputStream.close();
			trlde.setValidate(res);
		}
		
	  return trlde;
  }
}

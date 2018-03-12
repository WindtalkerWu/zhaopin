package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.database.DBUtils;

/**
 * 全局数据
 * @author wuzy
 *
 */
public class GlobalDataEntity extends Entity{
	private String strVersion;								// 数据版本号
	private GlobalData data;
	
	private Result validate;								// 是否成功
	
	public GlobalDataEntity(){
		this.data = new GlobalData();
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
	 * 获取数据版本号
	 * @return 数据版本号
	 */
	public String getVersion(){
		return this.strVersion;
	}
	
	/**
	 * 设置数据版本号
	 * @param str 数据版本号
	 */
	public void setVersion(String str){
		this.strVersion = str;
	}
	
	/**
	 * 获取全局数据
	 * @return 全局数据
	 */
	public GlobalData getData(){
		return this.data;
	}
	
	/**
	 * 设置全局数据
	 * @param gd 全局数据
	 */
	public void setData(GlobalData gd){
		this.data = gd;
	}
	
	/**
	 * @param appContext
	 * @param inputStream
	 * @param type
	 * @param bsavetoDb
	 * @return
	 * @throws IOException
	 * @throws AppException
	 */
	public static GlobalDataEntity parse(AppContext appContext, 
			InputStream inputStream, int nType)
			throws IOException, AppException{
		
		GlobalDataEntity gde = new GlobalDataEntity();
		
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
			gde.setValidate(res);
			
			JSONObject jsonDataObj = new JSONObject(jsonObj.getString("data"));
			// 获取全局数据版本号
			String strVer = jsonDataObj.getString("version");
			gde.setVersion(strVer);	
			
			// 获取全局数据
			if(HeadhunterPublic.GLOBALDATA_TYPE_GETDATA == nType){
				String strData = jsonDataObj.getString("data");
				GlobalData gd = (GlobalData)ObjectUtils.getObjectFromJsonString(strData, GlobalData.class);
				gde.setData(gd);
			}
		} catch (JSONException e) {
			res = new Result(-1, "json error");
			throw AppException.json(e);

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.io(e);
		} finally {
			inputStream.close();
			gde.setValidate(res);
		}
		
		return gde;
	}
	
	/**
	 * 保存全局数据版本号
	 * @param appContext
	 * @param strVersion	全局数据版本号
	 */
	public static void saveGlobalDataVersion(AppContext appContext, String strVersion){

		DBUtils dbu = DBUtils.getInstance(appContext);
//		String sql = DBUtils.KEY_GLOBALDATA_VERSION;
		dbu.delete(DBUtils.versionTableName, null, null);
		
		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_GLOBALDATA_VERSION, strVersion);
		dbu.save(DBUtils.versionTableName, values);
	}
	
	/**
	 * 获取全局数据版本号
	 * @param appContext
	 * @return
	 */
	public static String getGlobalDataVersion(AppContext appContext){
		String strRs = "";
		
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		Cursor c = dbu.query(DBUtils.versionTableName, new String[]{DBUtils.KEY_GLOBALDATA_VERSION}, null);
		if(null != c){
			if(c.getCount() > 0){
				c.moveToFirst();
				strRs = c.getString(c.getColumnIndex(DBUtils.KEY_GLOBALDATA_VERSION));	
			}
			
			c.close();
		}
			
		return strRs;
	}
}

package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.database.DBUtils;

/**
 * 悬赏任务查询过滤条件
 * @author wuzy
 *
 */
public class RewardFilterCondition implements Serializable {
	private String keyword;				// 搜索关键字, 以空格分割
	private String[] area_id;			// 地区id
	private String[] industry_id;		// 行业id
	private String[] industry_fid;		// 一级行业id
	
	public RewardFilterCondition(){
		this.keyword = "";
		this.area_id = new String[]{""};
		this.industry_id = new String[]{""};
		this.industry_fid = new String[]{""};
	}
	  
	/**
	* 获取搜索关键字
	* @return 搜索关键字
	*/
	public String getKeyword(){
		return this.keyword;
	}
	
	/**
	* 设置搜索关键字
	* @param str 搜索关键字
	*/
	public void setKeyword(String str){
		this.keyword = str;
	}
	  
	/**
	* 获取地区id
	* @return 地区id
	*/
	public String[] getArea_id(){
		return this.area_id;
	}
	
	/**
	* 设置地区id
	* @param str 地区id
	*/
	public void setArea_id(String[] str){
		this.area_id = str;
	}
	  
	/**
	* 获取行业id
	* @return 行业id
	*/
	public String[] getIndustry_id(){
		return this.industry_id;
	}
	
	/**
	* 设置行业id
	* @param lsStr 行业id
	*/
	public void setIndustry_id(String[] str){
		this.industry_id = str;
	}
	
	
	public String[] getIndustry_fid() {
		return industry_fid;
	}

	public void setIndustry_fid(String[] industry_fid) {
		this.industry_fid = industry_fid;
	}

	/**
	 * 获取历史数据
	 * @param appContext
	 * @return
	 */
	public static List<RewardFilterCondition> getHistorySearch (AppContext appContext){
		
		List<RewardFilterCondition> lsRFC = new ArrayList<RewardFilterCondition>();
		
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getAccessToken();
		} else {
			strUserId = appContext.getAppId();
		}
		
		String sql = DBUtils.KEY_REWARDSEANRCHHISTORY_USERID + " = \"" + strUserId + "\"";
		
		Cursor c = dbu.query(DBUtils.rewardsearchhistoryTableName,
				new String[] {"*"}, sql);
		
		if (null != c) {
			if(c.getCount() > 0){
				for (c.moveToLast(); !c.isBeforeFirst(); c.moveToPrevious()) {
					RewardFilterCondition rfc = new RewardFilterCondition();
					// 关键字
					rfc.setKeyword(c.getString(c
							.getColumnIndex(DBUtils.KEY_REWARDSEANRCHHISTORY_KEWWORD)));
					// 地区/城市
					String strCity = c.getString(c
							.getColumnIndex(DBUtils.KEY_REWARDSEANRCHHISTORY_CITY));
					if(null != strCity){
						if(!strCity.isEmpty()){
							if(!strCity.equals("null")){
								String[] strCityArray = ObjectUtils.getStringArrayFormJsonString(strCity);
								rfc.setArea_id(strCityArray);
							}
						}
					}

					// 行业
					String strIndustry = c.getString(c
							.getColumnIndex(DBUtils.KEY_REWARDSEANRCHHISTORY_INDUSTRY));
					if(null != strIndustry){
						if(!strIndustry.isEmpty()){
							if(!strIndustry.equals("null")){
								String[] strIndustryArray = ObjectUtils.getStringArrayFormJsonString(strIndustry);
								rfc.setIndustry_id(strIndustryArray);
							}
						}
					}
					
					// 父行业
					String strParentIndustry = c.getString(c
							.getColumnIndex(DBUtils.KEY_REWARDSEANRCHHISTORY_PARENTINDUSTRY));
					if(null != strParentIndustry){
						if(!strParentIndustry.isEmpty()){
							if(!strIndustry.equals("null")){
								String[] strParentIndustryArray = ObjectUtils.getStringArrayFormJsonString(strParentIndustry);
								rfc.setIndustry_fid(strParentIndustryArray);
							}
						}
					}
					
					lsRFC.add(rfc);
				}
			}
			
			c.close();
		}
		
		return lsRFC;	
	}
	
	/**
	 * 获取历史数据
	 * @param appContext
	 * @return
	 */
	public static RewardFilterCondition getLastHistorySearch (AppContext appContext){
		
		RewardFilterCondition rfc = new RewardFilterCondition();
		
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getAccessToken();
		} else {
			strUserId = appContext.getAppId();
		}
		
		String sql = DBUtils.KEY_REWARDSEANRCHHISTORY_USERID + " = \"" + strUserId + "\"";
		
		Cursor c = dbu.query(DBUtils.rewardsearchhistoryTableName, 
				new String[] {"*"}, sql);
		
		if (null != c) {
			if(c.getCount() > 0){
				c.moveToLast();
				// 关键字
				rfc.setKeyword(c.getString(c
						.getColumnIndex(DBUtils.KEY_REWARDSEANRCHHISTORY_KEWWORD)));
				// 地区/城市
				String strCity = c.getString(c
						.getColumnIndex(DBUtils.KEY_REWARDSEANRCHHISTORY_CITY));
				if(null != strCity){
					if(!strCity.isEmpty()){
						if(!strCity.equals("null")){
							String[] strCityArray = ObjectUtils.getStringArrayFormJsonString(strCity);
							rfc.setArea_id(strCityArray);
						}
					}
				}

				// 行业
				String strIndustry = c.getString(c
						.getColumnIndex(DBUtils.KEY_REWARDSEANRCHHISTORY_INDUSTRY));
				if(null != strIndustry){
					if(!strIndustry.isEmpty()){
						if(!strIndustry.equals("null")){
							String[] strIndustryArray = ObjectUtils.getStringArrayFormJsonString(strIndustry);
							rfc.setIndustry_id(strIndustryArray);
						}
					}
				}
				
				// 父行业
				String strParentIndustry = c.getString(c
						.getColumnIndex(DBUtils.KEY_REWARDSEANRCHHISTORY_PARENTINDUSTRY));
				if(null != strParentIndustry){
					if(!strParentIndustry.isEmpty()){
						if(!strIndustry.equals("null")){
							String[] strParentIndustryArray = ObjectUtils.getStringArrayFormJsonString(strParentIndustry);
							rfc.setIndustry_fid(strParentIndustryArray);
						}
					}
				}
			}
			
			c.close();
		}
		
		return rfc;	
	}
	
	/**
	 * 保存搜索数据
	 * @param appContext
	 * @param rfc
	 * @return
	 */
	public static boolean saveHistorySearch (AppContext appContext,
			RewardFilterCondition rfc){
		boolean bRet = false;
		
		// 获取现有的搜索历史
		List<RewardFilterCondition> lsRFC = getHistorySearch(appContext);

		boolean bDel = false;
		RewardFilterCondition rfcDelete = new RewardFilterCondition();
		if(lsRFC.size() >= 5){
			bDel = true;
		}

		// 判断表中是否有重复的数据
		for(int i = 0; i < lsRFC.size(); i++){
			RewardFilterCondition rfcTemp = lsRFC.get(i);
			if(rfc.getKeyword().equals(rfcTemp.getKeyword())){
				if(Arrays.equals(rfc.getArea_id(), rfcTemp.getArea_id())){
					if(Arrays.equals(rfc.getIndustry_id(), rfcTemp.getIndustry_id())){
						// 删除重复数据
						deleteHistorySearch(appContext, rfc);
						bDel = false;
						break;
					}
				}
			}
			
			if((lsRFC.size()- 1) == i && bDel){
				rfcDelete = rfcTemp;
			}
		}
		
		// 如果记录条数超期限， 删除第一条
		if(bDel){
			deleteHistorySearch(appContext, rfcDelete);
		}
		
		// 保存新数据
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strCity = ObjectUtils.getJsonStringFromObject(rfc.getArea_id());
		String strIndustry = ObjectUtils.getJsonStringFromObject(rfc.getIndustry_id());
		String strParentIndustry = ObjectUtils.getJsonStringFromObject(rfc.getIndustry_fid());
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getAccessToken();
		} else {
			strUserId = appContext.getAppId();
		}
		
		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_REWARDSEANRCHHISTORY_USERID, strUserId);
		// 关键字
		values.put(DBUtils.KEY_REWARDSEANRCHHISTORY_KEWWORD, rfc.getKeyword());
		// 地区/城市;
		values.put(DBUtils.KEY_REWARDSEANRCHHISTORY_CITY, strCity);
		// 行业
		values.put(DBUtils.KEY_REWARDSEANRCHHISTORY_INDUSTRY, strIndustry);
		// 父行业
		values.put(DBUtils.KEY_REWARDSEANRCHHISTORY_PARENTINDUSTRY, strParentIndustry);
		
		dbu.save(DBUtils.rewardsearchhistoryTableName, values);
		
		return bRet;
	}
	
	/**
	 * 删除搜索历史
	 * @param appContext
	 * @param rfcDelete
	 */
	public static void deleteHistorySearch (AppContext appContext, RewardFilterCondition rfcDelete){
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getAccessToken();
		} else {
			strUserId = appContext.getAppId();
		}
		
		String strDelCity = ObjectUtils.getJsonStringFromObject(rfcDelete.getArea_id());
		String strDelIndustry = ObjectUtils.getJsonStringFromObject(rfcDelete.getIndustry_id());
		String strDelParentIndustry = ObjectUtils.getJsonStringFromObject(rfcDelete.getIndustry_fid());
		
		String whereClause = DBUtils.KEY_REWARDSEANRCHHISTORY_KEWWORD + " = ? AND "
		+ DBUtils.KEY_REWARDSEANRCHHISTORY_CITY + " = ? AND "
		+ DBUtils.KEY_REWARDSEANRCHHISTORY_INDUSTRY + " = ? AND " 
		+ DBUtils.KEY_REWARDSEANRCHHISTORY_PARENTINDUSTRY + " = ? AND " 
		+ DBUtils.KEY_REWARDSEANRCHHISTORY_USERID + " = ?";	
		
		String[] whereArgs = {rfcDelete.getKeyword(), strDelCity, strDelIndustry, 
				strDelParentIndustry, strUserId};
		
		dbu.delete(DBUtils.rewardsearchhistoryTableName, whereClause, whereArgs);	
	}
}

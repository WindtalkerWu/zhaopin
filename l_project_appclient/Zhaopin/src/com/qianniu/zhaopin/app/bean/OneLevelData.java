package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;
import java.util.List;

import android.content.ContentValues;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.database.DBUtils;

/**
 * 一级数据结构
 * @author wzy
 *
 */
public class OneLevelData implements Serializable{
	private String id;			// 一级数据id
	private String label;		// 一级数据名称
	private String pinyin;		// 一级数据名称拼音
	private String userId;   
	
	public OneLevelData(){
		this.id = "";
		this.label = "";
		this.pinyin = "";
	}
	
	/**
	 * 获取一级数据id
	 * @return 一级数据id
	 */
	public String getId(){
		return this.id;
	}
	
	/**
	 * 设置一级数据id
	 * @param strId 一级数据id
	 */
	public void setId(String strId){
		this.id = strId;
	}
	
	/**
	 * 获取一级数据名称
	 * @return 一级数据名称
	 */
	public String getLabel(){
		return this.label;
	}
	
	/**
	 * 设置一级数据名称
	 * @param str 一级数据名称
	 */
	public void setLabel(String str){
		this.label = str;
	}
	
	/**
	 * 获取一级数据名称拼音
	 * @return 一级数据名称拼音
	 */
	public String getPinYin(){
		return this.pinyin;
	}
	
	/**
	 * 设置一级数据名称拼音
	 * @param str 一级数据名称拼音
	 */
	public void setPinYin(String str){
		this.pinyin = str;
	}
	
	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public static boolean saveToHotLabelTable(AppContext appContext, OneLevelData oneLevelData) {

		DBUtils db = DBUtils.getInstance(appContext);

		ContentValues values = new ContentValues();
		values.put(DBUtils.KEY_HOT_LABEL_ID, oneLevelData.getId());
		values.put(DBUtils.KEY_HOT_LABEL_LABEL, oneLevelData.getLabel());
		values.put(DBUtils.KEY_HOT_LABEL_PINYIN, oneLevelData.getPinYin());
		values.put(DBUtils.KEY_HOT_LABEL_USER_ID, appContext.getUserId());

		long id = db.save(DBUtils.hotLabelTableName, values);

		if (id > 0)
			return true;
		else
			return false;
	}
	public static void saveToHotLabelTable(AppContext appContext, List<OneLevelData> oneLevelDatas) {

		DBUtils db = DBUtils.getInstance(appContext);
		for (int i = 0; i < oneLevelDatas.size(); i++) {
			OneLevelData oneLevelData = oneLevelDatas.get(i);
			ContentValues values = new ContentValues();
			values.put(DBUtils.KEY_HOT_LABEL_ID, oneLevelData.getId());
			values.put(DBUtils.KEY_HOT_LABEL_LABEL, oneLevelData.getLabel());
			values.put(DBUtils.KEY_HOT_LABEL_PINYIN, oneLevelData.getPinYin());
			values.put(DBUtils.KEY_HOT_LABEL_USER_ID, appContext.getUserId()); //用户 id

			db.save(DBUtils.hotLabelTableName, values);
		}
		db.close();
	}
}

package com.qianniu.zhaopin.app.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 二级数据结构
 * @author wzy
 *
 */
public class TwoLevelData {
	private String id;						// 二级数据id
	private String label;					// 二级数据名称
	private String pinyin;					// 二级数据名称拼音
	private List<OneLevelData> onelevel;	// 一级数据列表
	
	public TwoLevelData(){
		this.id = "";
		this.label = "";
		this.pinyin = "";
		this.onelevel = new ArrayList<OneLevelData>();
	}
	
	/**
	 * 获取二级数据id
	 * @return 二级数据id
	 */
	public String getId(){
		return this.id;
	}
	
	/**
	 * 设置二级数据id
	 * @param strId 二级数据id
	 */
	public void setId(String strId){
		this.id = strId;
	}
	
	/**
	 * 获取二级数据名称
	 * @return 二级数据名称
	 */
	public String getLabel(){
		return this.label;
	}
	
	/**
	 * 设置二级数据名称
	 * @param str 二级数据名称
	 */
	public void setLabel(String str){
		this.label = str;
	}
	
	/**
	 * 获取二级数据名称拼音
	 * @return 一级数据名称拼音
	 */
	public String getPinYin(){
		return this.pinyin;
	}
	
	/**
	 * 设置二级数据名称拼音
	 * @param str 一级数据名称拼音
	 */
	public void setPinYin(String str){
		this.pinyin = str;
	}
	
	/**
	 * 获取一级数据列表
	 * @return 一级数据列表
	 */
	public List<OneLevelData> getOneLevel(){
		return this.onelevel;
	}
	
	/**
	 * 设置一级数据列表
	 * @param listOneLeve 一级数据列表
	 */
	public void setOneLevel(List<OneLevelData> listOneLeve){
		this.onelevel = listOneLeve;
	}
}

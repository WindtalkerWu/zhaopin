package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 行业数据结构
 * @author wuzy
 *
 */
public class TwoLevelChooseData implements Serializable {
	private String m_strID;							// 行业ID
	private String m_strName;						// 行业名称
	private String m_strNamePinYin;					// 行业名称拼音
	private boolean m_bHavingSub;					// 是否有子行业
	private List<OneLevelChooseData> m_listSub; 	// 子行业列表
	private boolean m_bHavingColor;					// 是否要颜色区别
	private boolean m_bSubExpand;					// 是否要展开
	
	public TwoLevelChooseData(){
		this.m_strID = "";
		this.m_strName = "";
		this.m_strNamePinYin = "";
		this.m_bHavingSub = false;
		this.m_listSub = new ArrayList<OneLevelChooseData>();
		this.m_bHavingColor = false;
		this.m_bSubExpand = false;
	}
	
	/**
	 * 获取行业ID
	 * @return 行业ID
	 */
	public String getID(){
		return this.m_strID;
	}
	
	/**
	 * 设置行业ID
	 * @param str 行业ID
	 */
	public void setID(String str){
		this.m_strID = str;
	}
	
	/**
	 * 获取行业名称
	 * @return 行业名称
	 */
	public String getName(){
		return this.m_strName;
	}
	
	/**
	 * 设置行业名称
	 * @param str 行业名称
	 */
	public void setName(String str){
		this.m_strName = str;
	}
	
	/**
	 * 获取行业名称拼音
	 * @return 行业名称拼音
	 */
	public String getNamePinYin(){
		return this.m_strNamePinYin;
	}
	
	/**
	 * 设置行业名称拼音
	 * @param str 行业名称拼音
	 */
	public void setNamePinYin(String str){
		this.m_strNamePinYin = str;
	}
	
	/**
	 * 获取是否有子行业
	 * @return 是否有子行业
	 */
	public boolean getHavingSub(){
		return this.m_bHavingSub;
	}
	
	/**
	 * 设置是否有子行业
	 * @param b 是否有子行业
	 */
	public void setHavingSub(boolean b){
		this.m_bHavingSub = b;
	}

	/**
	 * 获取子行业列表
	 * @return 子行业列表
	 */
	public List<OneLevelChooseData> getSubList(){
		return this.m_listSub;
	}
	
	/**
	 * 设置子行业列表
	 * @param lsICSD 子行业列表
	 */
	public void setSubList(List<OneLevelChooseData> lsICSD){
		this.m_listSub = lsICSD;
	}
	
	/**
	 * 获取是否要颜色区别
	 * @return 是否要颜色区别
	 */
	public boolean getHavingColor(){
		return this.m_bHavingColor;
	}
	
	/**
	 * 设置是否要颜色区别
	 * @param b 是否要颜色区别
	 */
	public void setHavingColor(boolean b){
		this.m_bHavingColor = b;
	}
	
	/**
	 * 获取是否要展开
	 * @return 是否要展开
	 */
	public boolean getSubExpand(){
		return this.m_bSubExpand;
	}
	
	/**
	 * 设置是否要展开
	 * @param b 是否要展开
	 */
	public void setSubExpand(boolean b){
		this.m_bSubExpand = b;
	}

}

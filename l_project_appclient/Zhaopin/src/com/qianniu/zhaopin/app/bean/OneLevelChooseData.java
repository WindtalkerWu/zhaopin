package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;

/**
 * 子行业数据结构
 * @author wuzy
 *
 */
public class OneLevelChooseData implements Serializable {
	private String m_strID;					// 子行业ID
	private String m_strName;				// 子行业名称
	private String m_strNamePinYin;			// 子行业名称拼音
	private String m_strParentID;			// 父行业ID
	private String m_strParentName;			// 父行业名称
	private boolean m_bIsChoose;			// 是否被选中
	
	public OneLevelChooseData(){
		this.m_strID = "";
		this.m_strName = "";
		this.m_strNamePinYin = "";
		this.m_strParentName = "";
		this.m_strParentID = "";
		this.m_bIsChoose = false;
	}
	
	/**
	 * 获取子行业ID
	 * @return 子行业ID
	 */
	public String getID(){
		return this.m_strID;
	}
	
	/**
	 * 设置子行业ID
	 * @param str 子行业ID
	 */
	public void setID(String str){
		this.m_strID = str;
	}
	
	/**
	 * 获取子行业名称
	 * @return 子行业名称
	 */
	public String getName(){
		return this.m_strName;
	}
	
	/**
	 * 设置子行业名称
	 * @param str 子行业名称
	 */
	public void setName(String str){
		this.m_strName = str;
	}
	
	/**
	 * 获取子行业名称拼音
	 * @return 子行业名称拼音
	 */
	public String getNamePinYin(){
		return this.m_strNamePinYin;
	}
	
	/**
	 * 设置子行业名称拼音
	 * @param str 子行业名称拼音
	 */
	public void setNamePinYin(String str){
		this.m_strNamePinYin = str;
	}
	
	/**
	 * 获取父行业ID
	 * @return 父行业ID
	 */
	public String getParentID(){
		return this.m_strParentID;
	}
	
	/**
	 * 设置父行业ID
	 * @param str 父行业ID
	 */
	public void setParentID(String str){
		this.m_strParentID = str;
	}
	
	/**
	 * 获取父行业名称
	 * @return 父行业名称
	 */
	public String getParentName(){
		return this.m_strParentName;
	}
	
	/**
	 * 设置父行业名称
	 * @param str 父行业名称
	 */
	public void setParentName(String str){
		this.m_strParentName = str;
	}
	
	/**
	 * 获取是否被选中
	 * @return 是否被选中
	 */
	public boolean getIsChoose(){
		return this.m_bIsChoose;
	}
	
	/**
	 * 设置是否被选中
	 * @param bIsChoose 是否被选中
	 */
	public void setIsChoose(boolean bIsChoose){
		this.m_bIsChoose = bIsChoose;
	}
}

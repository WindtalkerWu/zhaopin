package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;

import com.qianniu.zhaopin.app.common.HeadhunterPublic;

public class CityChooseData implements Serializable {
	private int m_nType;					// 数据类型
	private String m_strID;					// 城市ID
	private String m_strName;				// 城市名称
	private String m_strNamePinYin;			// 城市名称拼音
	private boolean m_bIsChoose;			// 是否被选中
	
	public CityChooseData(){
		this.m_bIsChoose = false;
		this.m_nType = HeadhunterPublic.CITYCHOOSE_DATATYPE_COMMON;
	}
	
	/**
	 * 获取数据类型
	 * @return 数据类型
	 */
	public int getType(){
		return this.m_nType;
	}
	
	/**
	 * 设置数据类型
	 * @param nType 数据类型
	 */
	public void setType(int nType){
		this.m_nType = nType;
	}
	
	/**
	 * 获取城市ID
	 * @return 城市ID
	 */
	public String getID(){
		return this.m_strID;
	}
	
	/**
	 * 设置城市ID
	 * @param str 城市ID
	 */
	public void setID(String str){
		this.m_strID = str;
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
	
	/**
	 * 获取城市名称
	 * @return 城市名称
	 */
	public String getName(){
		return this.m_strName;
	}
	
	/**
	 * 设置城市名称
	 * @param str 城市名称
	 */
	public void setName(String str){
		this.m_strName = str;
	}
	
	/**
	 * 获取城市名称拼音
	 * @return 城市名称拼音
	 */
	public String getNamePinYin(){
		return this.m_strNamePinYin;
	}
	
	/**
	 * 设置城市名称拼音
	 * @param str 城市名称拼音
	 */
	public void setNamePinYin(String str){
		this.m_strNamePinYin = str;
	}
}

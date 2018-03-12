package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ThreeLevelChooseData implements Serializable{
	private String m_strID;							// 行业ID
	private String m_strName;						// 行业名称
	private String m_strNamePinYin;					// 行业名称拼音
	private boolean m_bHavingSub;					// 是否有子行业
	private List<TwoLevelChooseData> m_listSub; 	// 子行业列表
	private boolean m_bHavingColor;					// 是否要颜色区别
	private boolean m_bSubExpand;					// 是否要展开
	
	public ThreeLevelChooseData(){
		this.m_strID = "";
		this.m_strName = "";
		this.m_strNamePinYin = "";
		this.m_bHavingSub = false;
		this.m_listSub = new ArrayList<TwoLevelChooseData>();
		this.m_bHavingColor = false;
		this.m_bSubExpand = false;
	}

	public String getID() {
		return m_strID;
	}

	public void setID(String strID) {
		this.m_strID = strID;
	}

	public String getName() {
		return m_strName;
	}

	public void setName(String strName) {
		this.m_strName = strName;
	}

	public String getNamePinYin() {
		return m_strNamePinYin;
	}

	public void setNamePinYin(String strNamePinYin) {
		this.m_strNamePinYin = strNamePinYin;
	}

	public boolean getbHavingSub() {
		return m_bHavingSub;
	}

	public void setHavingSub(boolean bHavingSub) {
		this.m_bHavingSub = bHavingSub;
	}

	public List<TwoLevelChooseData> getSubList() {
		return m_listSub;
	}

	public void setSubList(List<TwoLevelChooseData> lsTLCD) {
		this.m_listSub = lsTLCD;
	}

	public boolean getHavingColor() {
		return m_bHavingColor;
	}

	public void setHavingColor(boolean bHavingColor) {
		this.m_bHavingColor = bHavingColor;
	}

	public boolean getSubExpand() {
		return m_bSubExpand;
	}

	public void setSubExpand(boolean b) {
		this.m_bSubExpand = b;
	}
	
	
}

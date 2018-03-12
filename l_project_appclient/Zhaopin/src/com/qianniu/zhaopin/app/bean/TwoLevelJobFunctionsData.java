package com.qianniu.zhaopin.app.bean;

import java.util.ArrayList;
import java.util.List;

public class TwoLevelJobFunctionsData {
	private String id;									// 二级级数据id
	private String label;								// 二级数据名称
	private String pinyin;								// 二级数据名称拼音
	private List<OneLevelJobFunctionsData> children;	// 一级数据列表
	
	public TwoLevelJobFunctionsData(){
		this.id = "";
		this.label = "";
		this.pinyin = "";
		this.children = new ArrayList<OneLevelJobFunctionsData>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public List<OneLevelJobFunctionsData> getChildren() {
		return children;
	}

	public void setChildren(List<OneLevelJobFunctionsData> children) {
		this.children = children;
	}
	
	
}

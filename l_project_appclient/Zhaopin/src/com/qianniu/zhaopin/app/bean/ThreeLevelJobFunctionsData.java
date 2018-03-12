package com.qianniu.zhaopin.app.bean;

import java.util.ArrayList;
import java.util.List;

public class ThreeLevelJobFunctionsData extends Entity{
	private String id;									// 三级级数据id
	private String label;								// 三级数据名称
	private String pinyin;								// 三级数据名称拼音
	private List<TwoLevelJobFunctionsData> children;	// 二级数据列表
	
	public ThreeLevelJobFunctionsData(){
		this.id = "";
		this.label = "";
		this.pinyin = "";
		this.children = new ArrayList<TwoLevelJobFunctionsData>();
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

	public List<TwoLevelJobFunctionsData> getChildren() {
		return children;
	}

	public void setChildren(List<TwoLevelJobFunctionsData> children) {
		this.children = children;
	}
	
	
}

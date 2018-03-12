package com.qianniu.zhaopin.app.bean;

public class OneLevelJobFunctionsData {
	private String id;			// 一级数据id
	private String label;		// 一级数据名称
	private String pinyin;		// 一级数据名称拼音
	
	public OneLevelJobFunctionsData(){
		this.id = "";
		this.label = "";
		this.pinyin = "";
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
	
	
}

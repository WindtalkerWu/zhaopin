package com.qianniu.zhaopin.app.bean;

/**
 * 下拉菜单列表
 * @author wuzy
 *
 */
public class PullDownListInfo {
	private String strText;
	private boolean bIsChoose;
	
	public PullDownListInfo(){
		this.strText = "";
		this.bIsChoose = false;
	}
	
	/**
	 * 获取item上的字符串
	 * @return item上的字符串
	 */
	public String getText(){
		return this.strText;
	}
	
	/**
	 * 设置item上的字符串
	 * @param str 字符串
	 */
	public void setText(String str){
		this.strText = str;
	}
	
	/**
	 * 获取item是否被选中
	 * @return item是否被选中
	 */
	public boolean getIsChoose(){
		return this.bIsChoose;
	}
	
	/**
	 * 设置item是否被选中
	 * @param bChoose item是否被选中
	 */
	public void setIsChoose(boolean bChoose){
		this.bIsChoose = bChoose;
	}
}

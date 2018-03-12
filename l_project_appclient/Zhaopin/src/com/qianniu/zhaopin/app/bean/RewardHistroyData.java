package com.qianniu.zhaopin.app.bean;

public class RewardHistroyData {
	private String strName;			// 
	private boolean bNoImg;			// 是否显示图片
	
	public RewardHistroyData(){
		this.strName = "";
		this.bNoImg = false;
	}
	
	/**
	* 设置
	* @param str 悬赏
	*/
	public void setName(String str){
		this.strName = str;
	}
  
	/**
	* 获取
	* @return 悬赏
	*/
	public String getName(){
		return this.strName;
	}
	
	/**
	* 设置是否显示图片
	* @param b 是否显示图片
	*/
	public void setNoImg(boolean b){
		this.bNoImg = b;
	}
  
	/**
	* 获取是否显示图片
	* @return 是否显示图片
	*/
	public boolean getNoImg(){
		return this.bNoImg;
	}
}

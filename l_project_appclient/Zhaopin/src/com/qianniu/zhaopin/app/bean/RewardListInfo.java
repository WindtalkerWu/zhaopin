package com.qianniu.zhaopin.app.bean;


/**
 * 悬赏任务信息列表Adapter类
 * @author wuzy
 *
 */
public class RewardListInfo extends Entity{
	private RewardData rldData;				// 悬赏任务相关数据
	private boolean bIsRead;					// 已读
	
	public RewardListInfo(){
		this.rldData = new RewardData();
		this.bIsRead = false;
	}
	/**
	 * 获取悬赏任务相关数据
	 * @return 悬赏任务相关数据
	 */
	public RewardData getRewardListData(){
		return this.rldData;
	}

	/**
	 * 设置悬赏任务相关数据
	 * @param nId	悬赏任务相关数据
	 */
	public void setRewardListData(RewardData rld){
		this.rldData = rld;
	}
	
	/**
	 * 获取是否已读
	 * @return 是否已读
	 */
	public boolean getIsRead(){
		return this.bIsRead;
	}
	
	/**
	 * 设置是否已读
	 * @param bRead 是否已读
	 */
	public void setIsRead(boolean bRead){
		this.bIsRead = bRead;
	}
}

package com.qianniu.zhaopin.app.bean;

public class reqQuickRecommendData {
	private String task_id;		// 任务ID
	private String name;			// 姓名
	private String apply_type;	    // 2=>推荐, 4=>接受求职任务
	private String phone;			// 手机号
	private String mail;			// 邮箱
	private String memo;			// 推荐理由
	  
	public reqQuickRecommendData(){
		 this.task_id = "";
		 this.name = "";
		 this.apply_type = "";
		 this.phone = "";
		 this.mail = "";
		 this.memo = "";
	}
	  
	/**
	 * 获取任务ID
	 * 
	 * @return 任务ID
	 */
	public String getTask_Id() {
		return this.task_id;
	}

	/**
	 * 设置任务ID
	 * 
	 * @param str
	 *            任务ID
	 */
	public void setTask_Id(String str) {
		this.task_id = str;
	}
	  
	/**
	 * 获取姓名
	 * 
	 * @return 姓名
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 设置姓名
	 * 
	 * @param str
	 *            姓名
	 */
	public void setName(String str) {
		this.name = str;
	}
	  
	/**
	 * 获取快速推荐类型
	 * 
	 * @return 快速推荐类型
	 */
	public String getApply_Type() {
		return this.apply_type;
	}

	/**
	 * 设置快速推荐类型
	 * 
	 * @param str
	 *            快速推荐类型
	 */
	public void setApply_Type(String str) {
		this.apply_type = str;
	}
	  
	/**
	 * 获取手机号
	 * 
	 * @return 手机号
	 */
	public String getPhone() {
		return this.phone;
	}

	/**
	 * 设置手机号
	 * 
	 * @param str
	 *            手机号
	 */
	public void setPhone(String str) {
		this.phone = str;
	}
	  
	/**
	 * 获取邮箱
	 * 
	 * @return 邮箱
	 */
	public String getMail() {
		return this.mail;
	}

	/**
	 * 设置邮箱
	 * 
	 * @param str
	 *            邮箱
	 */
	public void setMail(String str) {
		this.mail = str;
	}
	   
	/**
	 * 获取推荐理由
	 * 
	 * @return 推荐理由
	 */
	public String getMemo() {
		return this.memo;
	}

	/**
	 * 设置推荐理由
	 * 
	 * @param str
	 *            推荐理由
	 */
	public void setMemo(String str) {
		this.memo = str;
	}
}

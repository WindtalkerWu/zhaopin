package com.qianniu.zhaopin.app.bean;

public class PersonalInfo {
	private int resume_id; //简历id
	private String name; // 姓名
	private String title; // 头衔
	private int identity_status; // 个人身份. 1=>已认证,2=>未认证
	private int phone_status; // 手机. 1=>已认证,2=>未认证
	private int mail_status; // 邮箱. 1=>已认证,2=>未认证
	private String picture; // 头像
	private int job_apply_nums; // 我的应聘数量
	private int job_recommend_nums; // 我的推荐数量
	private int job_reward_nums; // 我的悬赏数量
	private int job_favor_nums; // 我的收藏数量
	private float account; // 我的账户余额
	private int resume_num;//我的简历总数量
	private String display_name;	// 姓名/昵称
	
	public int getResume_id() {
		return resume_id;
	}
	public void setResume_id(int resume_id) {
		this.resume_id = resume_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getIdentity_status() {
		return identity_status;
	}
	public void setIdentity_status(int identity_status) {
		this.identity_status = identity_status;
	}
	
	/**
	 * @return :  1=>已认证, 2=>未认证 
	 */
	public int getPhone_status() {
		return phone_status;
	}
	public void setPhone_status(int phone_status) {
		this.phone_status = phone_status;
	}
	/**
	 * @return :  1=>已认证, 2=>未认证 
	 */
	public int getMail_status() {
		return mail_status;
	}
	public void setMail_status(int mail_status) {
		this.mail_status = mail_status;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public int getJob_apply_nums() {
		return job_apply_nums;
	}
	public void setJob_apply_nums(int job_apply_nums) {
		this.job_apply_nums = job_apply_nums;
	}
	public int getJob_recommend_nums() {
		return job_recommend_nums;
	}
	public void setJob_recommend_nums(int job_recommend_nums) {
		this.job_recommend_nums = job_recommend_nums;
	}
	public int getJob_reward_nums() {
		return job_reward_nums;
	}
	public void setJob_reward_nums(int job_reward_nums) {
		this.job_reward_nums = job_reward_nums;
	}
	public int getJob_favor_nums() {
		return job_favor_nums;
	}
	public void setJob_favor_nums(int job_favor_nums) {
		this.job_favor_nums = job_favor_nums;
	}
	public float getAccount() {
		return account;
	}
	public void setAccount(float account) {
		this.account = account;
	}
	public int getResume_num() {
		return resume_num;
	}
	public void setResume_num(int resume_num) {
		this.resume_num = resume_num;
	}
	
	/**
	 * 获取姓名/昵称
	 * @return 姓名/昵称
	 */
	public String getDisplay_name() {
		return display_name;
	}
	
	/**
	 * 设置姓名/昵称
	 * @param display_name 姓名/昵称
	 */
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	
	
}

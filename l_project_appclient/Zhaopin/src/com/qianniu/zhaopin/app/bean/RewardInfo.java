package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;
import java.util.List;

public class RewardInfo implements Serializable{
	
	private String task_id;
	private String task_type;
	private String task_title; //悬赏职位
	private String jflv3_id; //职能的三级id
	private String[] task_city; //悬赏城市 （期望地点 ）
	private String task_keyword;
	private String task_bonus;//悬赏金额
	private String task_lifecycle; //悬赏周期 id
	private String company_id;
	private String company_name;
	private String company_url;
	private String task_status; 
	private String publisher_type;
	private String publisher_name;
	private String publisher_date;
	private String publisher_enddate;
	private String pay_flag;						// 付款标识
	private int action_1;
	private int action_3;
	private int action_5;
	private String task_needs;
	private String task_location;
	private String task_expectation_id; //期望年薪id
	private String expectation_label;//期望年薪内容
	private String resume_id;//简历id
	private String task_memo; //我的推荐
	private String modified;
	private String task_url;
	private String[] task_category_id; // 悬赏任务属于的行业
	private String verify_status; // 审核状态
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public String getTask_type() {
		return task_type;
	}
	public void setTask_type(String task_type) {
		this.task_type = task_type;
	}
	public String getTask_title() {
		return task_title;
	}
	public void setTask_title(String task_title) {
		this.task_title = task_title;
	}
	public String getJflv3_id() {
		return jflv3_id;
	}
	public void setJflv3_id(String jflv3_id) {
		this.jflv3_id = jflv3_id;
	}
	public String[] getTask_city() {
		return task_city;
	}
	public void setTask_city(String[] task_city) {
		this.task_city = task_city;
	}
	public String getTask_keyword() {
		return task_keyword;
	}
	public void setTask_keyword(String task_keyword) {
		this.task_keyword = task_keyword;
	}
	public String getTask_bonus() {
		return task_bonus;
	}
	public void setTask_bonus(String task_bonus) {
		this.task_bonus = task_bonus;
	}
	
	public String getTask_lifecycle() {
		return task_lifecycle;
	}
	public void setTask_lifecycle(String task_lifecycle) {
		this.task_lifecycle = task_lifecycle;
	}
	public String getCompany_id() {
		return company_id;
	}
	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getCompany_url() {
		return company_url;
	}
	public void setCompany_url(String company_url) {
		this.company_url = company_url;
	}
	public String getTask_status() {
		return task_status;
	}
	public void setTask_status(String task_status) {
		this.task_status = task_status;
	}
	public String getPublisher_type() {
		return publisher_type;
	}
	public void setPublisher_type(String publisher_type) {
		this.publisher_type = publisher_type;
	}
	public String getPublisher_name() {
		return publisher_name;
	}
	public void setPublisher_name(String publisher_name) {
		this.publisher_name = publisher_name;
	}
	public String getPublisher_date() {
		return publisher_date;
	}
	public void setPublisher_date(String publisher_date) {
		this.publisher_date = publisher_date;
	}
	public String getPublisher_enddate() {
		return publisher_enddate;
	}
	public void setPublisher_enddate(String publisher_enddate) {
		this.publisher_enddate = publisher_enddate;
	}
	
	/**
	 * 获取付款标识
	 * @return 付款标识
	 */
	public String getPay_flag() {
		return pay_flag;
	}

	/**
	 * 设置付款标识
	 * @param pay_flag 付款标识
	 */
	public void setPay_flag(String pay_flag) {
		this.pay_flag = pay_flag;
	}

	public int getAction_1() {
		return action_1;
	}
	public void setAction_1(int action_1) {
		this.action_1 = action_1;
	}
	public int getAction_3() {
		return action_3;
	}
	public void setAction_3(int action_3) {
		this.action_3 = action_3;
	}
	public int getAction_5() {
		return action_5;
	}
	public void setAction_5(int action_5) {
		this.action_5 = action_5;
	}
	public String getTask_needs() {
		return task_needs;
	}
	public void setTask_needs(String task_needs) {
		this.task_needs = task_needs;
	}
	public String getTask_location() {
		return task_location;
	}
	public void setTask_location(String task_location) {
		this.task_location = task_location;
	}
	public String getTask_expectation_id() {
		return task_expectation_id;
	}
	public void setTask_expectation_id(String task_expectation_id) {
		this.task_expectation_id = task_expectation_id;
	}
	public String getExpectation_label() {
		return expectation_label;
	}
	public void setExpectation_label(String expectation_label) {
		this.expectation_label = expectation_label;
	}
	public String getResume_id() {
		return resume_id;
	}
	public void setResume_id(String resume_id) {
		this.resume_id = resume_id;
	}
	public String getTask_memo() {
		return task_memo;
	}
	public void setTask_memo(String task_memo) {
		this.task_memo = task_memo;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getTask_url() {
		return task_url;
	}
	public void setTask_url(String task_url) {
		this.task_url = task_url;
	}
	public String[] getTask_category_id() {
		return task_category_id;
	}
	public void setTask_category_id(String[] task_category_id) {
		this.task_category_id = task_category_id;
	}
	public String getVerify_status() {
		return verify_status;
	}
	public void setVerify_status(String verify_status) {
		this.verify_status = verify_status;
	}
}

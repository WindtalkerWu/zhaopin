package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;

public class InsidersAndCompany implements Serializable{
	public static final String auth = "1";
	public static final String noauth = "0";
	
	public static final int TYPEINSIDERS = 1; //1表示圈内人  2表示名企招聘
	public static final int TYPECOMPANY = 2; //1表示圈内人  2表示名企招聘
	
	public static final int RSS_NO = 0; //未订阅
	public static final int RSS_YES = 1; //已订阅
	public static final int RSS_UNAUTH = 3; //不可订阅，需要登录

	private String id;
	private String name;
	private String picture; //头像
	private String authenticate; //是否认证 0=>未认证, 1=>已认证.
	private String modified; //更新的时间戳
	private String attention_count; //关注度
	private String[] tags;
	

	private String task_count;
	private String title;
	private String company;
	private String description;
	private String weibo1;
	private String weibo2;
	private String weixin;
	private String created_time;
	private String status;
	private int rss_flag;
	
	private int type;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getAuthenticate() {
		return authenticate;
	}
	public void setAuthenticate(String authenticate) {
		this.authenticate = authenticate;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getAttention_count() {
		return attention_count;
	}
	public void setAttention_count(String attention_count) {
		this.attention_count = attention_count;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTask_count() {
		return task_count;
	}
	public void setTask_count(String task_count) {
		this.task_count = task_count;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getWeibo1() {
		return weibo1;
	}
	public void setWeibo1(String weibo1) {
		this.weibo1 = weibo1;
	}
	public String getWeibo2() {
		return weibo2;
	}
	public void setWeibo2(String weibo2) {
		this.weibo2 = weibo2;
	}
	public String getWeixin() {
		return weixin;
	}
	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}
	public String getCreated_time() {
		return created_time;
	}
	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public int getRss_flag() {
		return rss_flag;
	}
	public void setRss_flag(int rss_flag) {
		this.rss_flag = rss_flag;
	}
	
}

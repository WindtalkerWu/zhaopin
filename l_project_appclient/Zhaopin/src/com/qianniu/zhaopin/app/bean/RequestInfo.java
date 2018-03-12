package com.qianniu.zhaopin.app.bean;

public class RequestInfo {
	
	public static final int FLAG_DIRECTION_REFRESH = 1;
	public static final int FLAG_DIRECTION_MORE = 0;
	private int count; //单页返回的记录条数，默认为10
	private int direction ; //用户请求的方式. 1=>请求新数据(即刷新), 0=>请求旧数据(即加载更多). default:1
	private String offsetid; 
	private String offsetfield; //偏移字段名称. 默认为 id. 后端通过此字段来决定排序
	
	public RequestInfo() {
		this.count = 10;//默认是10
		this.direction = 1;
	}
	public RequestInfo(int directFlag) {
		this();
		this.direction = directFlag;
	}
	public RequestInfo(int directFlag, int reqCount) {
		this.count = reqCount;
		this.direction = directFlag;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public String getOffsetid() {
		return offsetid;
	}
	public void setOffsetid(String offsetid) {
		this.offsetid = offsetid;
	}
	public String getOffsetfield() {
		return offsetfield;
	}
	public void setOffsetfield(String offsetfield) {
		this.offsetfield = offsetfield;
	}
}

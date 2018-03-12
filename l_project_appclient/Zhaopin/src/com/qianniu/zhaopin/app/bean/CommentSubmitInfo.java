package com.qianniu.zhaopin.app.bean;

public class CommentSubmitInfo {
	private String type; // 1=>名人, 2=>名企
	private String id; //名人名企ID
	private String content; //评论内容

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}

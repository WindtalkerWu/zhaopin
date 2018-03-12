package com.qianniu.zhaopin.app.bean;

import android.text.TextUtils;


public class SeekWorthyCommentInfo extends CommentInfo{
	private int comment_count;

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}
	
	public boolean isEmpty() {
		if (TextUtils.isEmpty(id) && TextUtils.isEmpty(content) 
				&& TextUtils.isEmpty(modified) && TextUtils.isEmpty(user)) {
			return true;
		}
		return false;
	}
}

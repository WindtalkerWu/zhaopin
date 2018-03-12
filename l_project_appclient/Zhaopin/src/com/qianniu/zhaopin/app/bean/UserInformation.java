package com.qianniu.zhaopin.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.bean.Entity;

import android.util.Xml;

/**
 * 用户专页信息实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UserInformation extends Entity{
	
	private int pageSize;
	private User user = new User();


	public int getPageSize() {
		return pageSize;
	}
	public User getUser() {
		return user;
	}


	public static UserInformation parse(InputStream inputStream) throws IOException, AppException {
		return null;}
}

package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;

/*****************************************************************
 * 发送给后台的用户信息类
 * 
 ****************************************************************/
public class ReqUserInfo implements Serializable{
	
	private int login_type;				// 登陆类型. 1=>普通登陆, 2=>qq登陆, 3=>微博登陆
	
	private String thirdpart_id; 		// 第三方登陆的ID
	private String thirdpart_token; 	// 第三方登陆的TOKEN
	private String user_name; 			// 用户名
	private String user_password; 		// 用户密码
	private String display_name;		// 用户显示名称(第三方登录时为用户昵称)
	
	private String email;
	private String email_verified; // 1=>已认证, 2=>未认证 
	private String phone;
	private String phone_verified; // 1=>已认证, 2=>未认证 
	
	public static final String EMAIL_VERIFIED = "1";
	public static final String PHONE_VERIFIED = "1";
	
	public ReqUserInfo(){
		this.login_type = 1;
		this.thirdpart_id = "";
		this.thirdpart_token = "";
		this.user_name = "";
		this.user_password = "";
		this.display_name = "";
	}
	
	/*****************************************************************
	 * 获取登录类型
	 * 
	 ****************************************************************/
	public int getLoginType(){
		return this.login_type;
	}
	
	/*****************************************************************
	 * 设置登录类型
	 * 
	 ****************************************************************/
	public void setLoginType(int str){
		this.login_type = str;
	}
	
	/*****************************************************************
	 * 获取第三方登陆的ID
	 * 
	 ****************************************************************/
	public String getThirdPartId(){
		return this.thirdpart_id;
	}
	
	/*****************************************************************
	 * 设置第三方登陆的ID
	 * 
	 ****************************************************************/
	public void setThirdPartId(String str){
		this.thirdpart_id = str;
	}
	
	/*****************************************************************
	 * 获取第三方登陆的TOKEN
	 * 
	 ****************************************************************/
	public String getThirdPartToken(){
		return this.thirdpart_token;
	}
	
	/*****************************************************************
	 * 设置第三方登陆的TOKEN
	 * 
	 ****************************************************************/
	public void setThirdPartToken(String str){
		this.thirdpart_token = str;
	}
	
	/*****************************************************************
	 * 获取用户名
	 * 
	 ****************************************************************/
	public String getUserName(){
		return this.user_name;
	}
	
	/*****************************************************************
	 * 设置用户名
	 * 
	 ****************************************************************/
	public void setUserName(String str){
		this.user_name = str;
	}
	
	/*****************************************************************
	 * 获取用户名
	 * 
	 ****************************************************************/
	public String getUserPassword(){
		return this.user_password;
	}
	
	/*****************************************************************
	 * 设置登录类型
	 * 
	 ****************************************************************/
	public void setUserPassword(String str){
		this.user_password = str;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail_verified() {
		return email_verified;
	}

	public void setEmail_verified(String email_verified) {
		this.email_verified = email_verified;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone_verified() {
		return phone_verified;
	}

	public void setPhone_verified(String phone_verified) {
		this.phone_verified = phone_verified;
	}

	/**
	 * 获取用户显示名称(第三方登录时为用户昵称)
	 * @return 用户显示名称(第三方登录时为用户昵称)
	 */
	public String getDisplay_name() {
		return display_name;
	}

	/**
	 * 设置用户显示名称(第三方登录时为用户昵称)
	 * @param display_name 用户显示名称(第三方登录时为用户昵称)
	 */
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	
	
}

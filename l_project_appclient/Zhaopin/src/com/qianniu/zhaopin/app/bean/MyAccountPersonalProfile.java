package com.qianniu.zhaopin.app.bean;

/**
 * 个人资料(我的账号)
 * @author wuzy
 *
 */
public class MyAccountPersonalProfile {
	private String avatar;				// 用户头像url
	private String display_name;		// 姓名
	private String gender;				// 1=> 男, 2=> 女
	private String birthday;			// 生日
	private String m104_id;				// 居住地ID
	private String m105_id;				// 学历ID
	private String joinworkdate;		// 参加工作时间
	private String memo;				// 自我介绍
	
	public MyAccountPersonalProfile(){
		this.gender = "1";
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getM104_id() {
		return m104_id;
	}

	public void setM104_id(String m104_id) {
		this.m104_id = m104_id;
	}

	public String getM105_id() {
		return m105_id;
	}

	public void setM105_id(String m105_id) {
		this.m105_id = m105_id;
	}

	public String getJoinworkdate() {
		return joinworkdate;
	}

	public void setJoinworkdate(String joinworkdate) {
		this.joinworkdate = joinworkdate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}

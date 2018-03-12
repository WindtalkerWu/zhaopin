package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;

/**
 * 收货地址信息数据结构
 * @author wuzy
 *
 */
public class DeliveryAddressInfoData implements Serializable {
	private String strName;		// 收货人
	private String strTel;		// 联系方式
	private String strPostCode;	// 邮政编码
	private String strAddress;	// 详细地址
	
	public DeliveryAddressInfoData(){
		this.strName = "";
		this.strTel = "";
		this.strAddress = "";
		this.strPostCode = "";
	}

	public String getStrName() {
		return strName;
	}

	public void setStrName(String strName) {
		this.strName = strName;
	}

	public String getStrTel() {
		return strTel;
	}

	public void setStrTel(String strTel) {
		this.strTel = strTel;
	}

	public String getStrPostCode() {
		return strPostCode;
	}

	public void setStrPostCode(String strPostCode) {
		this.strPostCode = strPostCode;
	}

	public String getStrAddress() {
		return strAddress;
	}

	public void setStrAddress(String strAddress) {
		this.strAddress = strAddress;
	}
	
	
}

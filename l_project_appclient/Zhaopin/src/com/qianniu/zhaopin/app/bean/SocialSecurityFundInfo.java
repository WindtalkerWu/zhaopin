package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;

public class SocialSecurityFundInfo implements Serializable{
	private float pension;//养老
	private float medical;//医疗
	private float losejob;//失业
	private float fund;//公积金
	
	public SocialSecurityFundInfo() {
	}

	public float getPension() {
		return pension;
	}

	public void setPension(float pension) {
		this.pension = pension;
	}

	public float getMedical() {
		return medical;
	}

	public void setMedical(float medical) {
		this.medical = medical;
	}

	public float getLosejob() {
		return losejob;
	}

	public void setLosejob(float losejob) {
		this.losejob = losejob;
	}

	public float getFund() {
		return fund;
	}

	public void setFund(float fund) {
		this.fund = fund;
	}
	
}

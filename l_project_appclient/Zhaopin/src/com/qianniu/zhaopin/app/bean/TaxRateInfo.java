package com.qianniu.zhaopin.app.bean;

public class TaxRateInfo {
	private float pensionRate;//养老
	private float medicalRate;//医疗
	private float losejobRate;//失业
	private float fundRate;//公积金
	
	public TaxRateInfo() { //设置默认值
		this.pensionRate = 0.08f;
		this.medicalRate = 0.02f;
		this.losejobRate = 0.01f;
		this.fundRate = 0.07f;
	}

	public float getPensionRate() {
		return pensionRate;
	}

	public void setPensionRate(float pensionRate) {
		this.pensionRate = pensionRate;
	}

	public float getMedicalRate() {
		return medicalRate;
	}

	public void setMedicalRate(float medicalRate) {
		this.medicalRate = medicalRate;
	}

	public float getLosejobRate() {
		return losejobRate;
	}

	public void setLosejobRate(float losejobRate) {
		this.losejobRate = losejobRate;
	}

	public float getFundRate() {
		return fundRate;
	}

	public void setFundRate(float fundRate) {
		this.fundRate = fundRate;
	}
	
}

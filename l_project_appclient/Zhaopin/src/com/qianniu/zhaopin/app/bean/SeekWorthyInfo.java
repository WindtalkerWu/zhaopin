package com.qianniu.zhaopin.app.bean;

import java.util.List;

public class SeekWorthyInfo {
	
	private CompanyInfo recommend_company;
	private InsidersAndCompany boss;
	private List<CompanyInfo> company;
	private SeekWorthyCommentInfo comment;
	
	public CompanyInfo getRecommend_company() {
		return recommend_company;
	}
	public void setRecommend_company(CompanyInfo recommend_company) {
		this.recommend_company = recommend_company;
	}
	public InsidersAndCompany getBoss() {
		return boss;
	}
	public void setBoss(InsidersAndCompany boss) {
		this.boss = boss;
	}
	public List<CompanyInfo> getCompany() {
		return company;
	}
	public void setCompany(List<CompanyInfo> company) {
		this.company = company;
	}
	public SeekWorthyCommentInfo getComment() {
		return comment;
	}
	public void setComment(SeekWorthyCommentInfo comment) {
		this.comment = comment;
	}

}

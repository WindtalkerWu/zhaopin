package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;

public class VersionData implements Serializable{

	private String forceflag; //2 不用强制 1 强制
	private String version;
	private String force_version; //相对版本号，当client的版本号小于等于本值的时候，要求客户端强制升级
	private int versionCode;
	private String downloadurl;
	private String memo;
	private String modified; //更改时间
	
	public String getForceflag() {
		return forceflag;
	}
	public void setForceflag(String forceflag) {
		this.forceflag = forceflag;
	}
	public String getVersion() {
		return version;
	}
	public String getForce_version() {
		return force_version;
	}
	public void setForce_version(String force_version) {
		this.force_version = force_version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getDownloadurl() {
		return downloadurl;
	}
	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getModified() {
		return modified;
	}
	public void setModified(String modified) {
		this.modified = modified;
	}
}

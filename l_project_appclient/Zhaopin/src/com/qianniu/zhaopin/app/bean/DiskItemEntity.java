package com.qianniu.zhaopin.app.bean;

public class DiskItemEntity extends Entity {

	private int imgresId;
	private int titleresId;
	private int actionId;

	public DiskItemEntity(int imgresId,int titleresId,int actionId) {
		super();
		// TODO Auto-generated constructor stub
		this.imgresId = imgresId;
		this.titleresId = titleresId;
		this.actionId = actionId;
	}

	public final int getImgresId() {
		return imgresId;
	}

	public final void setImgresId(int imgresId) {
		this.imgresId = imgresId;
	}

	public final int getTitleresId() {
		return titleresId;
	}

	public final void setTitleresId(int titleresId) {
		this.titleresId = titleresId;
	}

	public final int getActionId() {
		return actionId;
	}

	public final void setActionId(int actionId) {
		this.actionId = actionId;
	}

}

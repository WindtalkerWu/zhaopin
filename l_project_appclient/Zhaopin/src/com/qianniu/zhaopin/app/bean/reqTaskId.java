package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;

public class reqTaskId implements Serializable {
	private String[] taskid;
	
	public reqTaskId(){
		
	}

	public String[] getTaskid() {
		return taskid;
	}

	public void setTaskid(String[] taskid) {
		this.taskid = taskid;
	}
	
}

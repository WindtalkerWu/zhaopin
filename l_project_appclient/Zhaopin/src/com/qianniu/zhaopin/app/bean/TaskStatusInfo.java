package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;

public class TaskStatusInfo implements Serializable {
    private String task_id; 	// 工作经历id, 为空时新增
    private String action_1;	// 接受标识. 1=>已接受任务
    private String action_3;	// 收藏标识. 1=>已收藏
    
    public TaskStatusInfo(){
    	this.action_1 = "0";
    	this.action_3 = "0";
    }

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public String getAction_1() {
		return action_1;
	}

	public void setAction_1(String action_1) {
		this.action_1 = action_1;
	}

	public String getAction_3() {
		return action_3;
	}

	public void setAction_3(String action_3) {
		this.action_3 = action_3;
	}
    
    
}

package com.qianniu.zhaopin.app.bean;

public class Between {
	private TaskBonus task_bonus;
//	private String task_bonus;
	
	public Between(){
		this.task_bonus = new TaskBonus();
	}

//	public String getTask_bonus() {
//		return task_bonus;
//	}
//
//	public void setTask_bonus(String task_bonus) {
//		this.task_bonus = task_bonus;
//	}

	public TaskBonus getTask_bonus() {
		return task_bonus;
	}

	public void setTask_bonus(TaskBonus task_bonus) {
		this.task_bonus = task_bonus;
	}
}

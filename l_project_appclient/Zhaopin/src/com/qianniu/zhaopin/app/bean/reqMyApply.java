package com.qianniu.zhaopin.app.bean;

public class reqMyApply {
	private int count;						// 单页返回的记录条数，默认为10"
	private int direction;					// 用户请求的方式. 1=>请求新数据, 0=>请求旧数据. default:1
	private String offsetid;				// 偏移ID. direction==1 => 大于该ID, direction==0 => 小于该ID. 默认为空
	private String offsetfield;				// 偏移字段名称
    private String verif_status;			// 审核状态  对应metadata.125
    private String task_action_id;			// 悬赏类型唯一ID
    private String action_direction;		// 用户请求的方式

	
	public reqMyApply(){
	  this.count = 10;
	  this.direction = 1;
	  this.offsetid = "";
	  this.offsetfield = "";
	  this.verif_status = "";
	  this.task_action_id = "";
	  this.action_direction = "1";
	}
	
	/**
	* 获取单页返回的记录条数
	* @return 单页返回的记录条数
	*/
	public int getCount(){
		return this.count;
	}
	
	/**
	* 设置单页返回的记录条数
	* @param str 单页返回的记录条数
	*/
	public void setCount(int n){
		this.count = n;
	}
	
	/**
	* 获取单页返回的记录条数
	* @return 单页返回的记录条数
	*/
	public int getDirection(){
		return this.direction;
	}
	
	/**
	* 设置单页返回的记录条数
	* @param n 单页返回的记录条数
	*/
	public void setDirection(int n){
		this.direction = n;
	}
	
	/**
	* 获取偏移ID
	* @return 偏移ID
	*/
	public String getOffsetid(){
		return this.offsetid;
	}
	
	/**
	* 设置偏移ID
	* @param str 偏移ID
	*/
	public void setOffsetid(String str){
		this.offsetid = str;
	}
	
	/**
	* 获取偏移字段名称
	* @return 偏移字段名称
	*/
	public String getOffsetfield(){
		return this.offsetfield;
	}
	
	/**
	* 设置偏移字段名称
	* @param str 偏移字段名称
	*/
	public void setOffsetfield(String str){
		this.offsetfield = str;
	}
	  
	/**
	* 获取审核状态
	* @return 审核状态
	*/
	public String getVerif_status(){
		return this.verif_status;
	}
	
	/**
	 * 设置审核状态
	 * @param str 审核状态id
	 */
	public void setVerif_status(String strId){
		this.verif_status = strId;
	}
	  
	/**
	* 获取悬赏类型唯一ID
	* @return 悬赏类型唯一ID
	*/
	public String getTask_action_id(){
		return this.task_action_id;
	}
	
	/**
	 * 设置悬赏类型唯一ID
	 * @param str 悬赏类型唯一ID
	 */
	public void setTask_action_id(String strId){
		this.task_action_id = strId;
	}
	  
	/**
	* 获取用户请求的方式
	* @return 用户请求的方式
	*/
	public String getAction_direction(){
		return this.action_direction;
	}
	
	/**
	 * 设置用户请求的方式
	 * @param str 用户请求的方式
	 */
	public void setAction_direction(String str){
		this.action_direction = str;
	}
}

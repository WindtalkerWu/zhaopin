package com.qianniu.zhaopin.app.bean;

/**
 * 请求获取悬赏任务列表信息时发送的数据格式(向服务器发送)
 * @author wuzy
 *
 */
public class reqRewardList {
	private int count;						// 单页返回的记录条数，默认为10"
	private int direction;					// 用户请求的方式. 1=>请求新数据, 0=>请求旧数据. default:1
	private String offsetid;				// 偏移ID. direction==1 => 大于该ID, direction==0 => 小于该ID. 默认为空
	private String offsetfield;				// 偏移字段名称
	private String[] task_type;
	private int request_type;				// 数据检索类型 1=>最新发布, 2=>猜你喜欢, 3=>周边职位, 4=>悬赏排名
    private String coordinates;				// 经纬度   格式: 经度,纬度  当request_type==3时带上
	private int coordinates_radius;			// 周边范围 选传. 经纬度半径. 单位:米"
	private RewardFilterCondition filters;	// 过滤条件
	
	public reqRewardList(){
	  this.count = 10;
	  this.direction = 1;
	  this.offsetid = "";
	  this.offsetfield = "";
	  this.request_type = 1;
	  this.coordinates_radius = 5000;
	  this.filters = new RewardFilterCondition();
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
	* 获取数据检索类型 
	* @return 数据检索类型 
	*/
	public int getRequest_type(){
		return this.request_type;
	}
	
	/**
	* 设置数据检索类型 
	* @param n 数据检索类型 
	*/
	public void setRequest_type(int n){
		this.request_type = n;
	}
	
	/**
	* 获取悬赏类型
	* @return 悬赏类型
	*/
	public String[] getTask_type(){
		return this.task_type;
	}
	
	/**
	* 设置悬赏类型
	* @param 悬赏类型 
	*/
	public void setTask_type(String[] str){
		this.task_type = str;
	}
	  
	/**
	* 获取经纬度
	* @return 经纬度
	*/
	public String getCoordinates(){
		return this.coordinates;
	}
	
	/**
	 * 设置经纬度
	 * @param strLongitude	经度
	 * @param strLatitude	纬度
	 */
	public void setCoordinates(String strLongitude, String strLatitude){
		this.coordinates = strLongitude + "," + strLatitude;
	}
	
	/**
	* 获取周边范围
	* @return 周边范围
	*/
	public int getCoordinates_radius(){
		return this.coordinates_radius;
	}
	
	/**
	* 设置周边范围
	* @param n 周边范围
	*/
	public void setCoordinates_radius(int n){
		this.coordinates_radius = n;
	}
	  
	/**
	* 获取过滤条件
	* @return 过滤条件
	*/
	public RewardFilterCondition getFilters(){
		return this.filters;
	}
	
	/**
	* 设置过滤条件
	* @param rfc 过滤条件
	*/
	public void setFilters(RewardFilterCondition rfc){
		this.filters = rfc;
	}
}

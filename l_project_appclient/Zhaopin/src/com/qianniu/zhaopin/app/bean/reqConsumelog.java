package com.qianniu.zhaopin.app.bean;

/**
 * 请求获取消费记录时向后台发送的请求参数结构
 * @author wuzy
 *
 */
public class reqConsumelog {
	private int count;						// 单页返回的记录条数，默认为10"
	private int direction;					// 用户请求的方式. 1=>请求新数据, 0=>请求旧数据. default:1
	private String offsetid;				// 偏移ID. direction==1 => 大于该ID, direction==0 => 小于该ID. 默认为空
	private String offsetfield;				// 偏移字段名称
	
	public reqConsumelog(){
		  this.count = 10;
		  this.direction = 1;
		  this.offsetid = "";
		  this.offsetfield = "";
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
}

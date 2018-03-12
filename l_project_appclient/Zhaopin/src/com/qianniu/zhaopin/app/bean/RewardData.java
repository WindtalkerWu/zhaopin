package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.database.DBUtils;

/**
 * 悬赏首页数据格式
 * @author wuzy
 *
 */
public class RewardData implements Serializable{
	  private String task_id;						// 悬赏任务ID
	  private String task_type;						// 悬赏任务类型  1=>招聘任务, 2=>求职面试任务, 3=>求职入职任务.  app端只提供2,3类型的任务提交
	  private String[] task_category_id;			// 悬赏任务属于的行业
	  private String task_title;					// 悬赏任务名称
	  private String[] task_city;					// 悬赏任务适用的城市
	  private String task_location;					// 具体地址
	  private String task_url;						// 悬赏任务详情的url(html5)
	  private String[] task_keyword;				// 悬赏任务关键字 
	  private String task_bonus;					// 悬赏任务奖金
	  private String company_name;					// 公司名称 
	  private String company_url;					// 公司url
	  private String company_id;					// 公司id
	  private String m27_status;					// 公司是否被收藏 1=>已收藏
	  private String task_status;					// 悬赏任务状态  1=>正常,2=>暂停, 3=>已存档
	  private String publisher_type;				// 发布者类型, 1=>公司, 2=>猎头, 3=>个人
	  private String publisher_name;				// 发布者名称
	  private String publisher_date;				// 发布时间
	  private String publisher_enddate;				// 发布截止时间
	  private String pay_flag;						// 付款标识
	  private String action_1;						// 接受标识
	  private String action_3;						// 收藏标识
	  private String action_5;						// 已读标识
	  private String verify_status;					// 审核状态
	  private String apply_type;					// 简历来源
	  private String resume_name;					// 简历名称
	  private String task_action_id;				// 
	  private String img_url;						// 图片url
	  private String join_count;					// 参与人数
	  private String[] labels;						// 标签
	  private String days_left;						// 剩余天数
	  private String gender;						// 性别
	  
	  private String user_id;						// 用户ID
	  private String userrequest_type;				// 用户选择要获取的悬赏任务类型
	  
	  private int request_datatype;					// 请求的数据类型(个人:2, 公司:1, 混合:0)
	  
	  /**********************************公司信息************************************/
	  private String id;							// 公司id
	  private String title;							// 公司名称
	  private String url;							// 公司URL
	  private String address;						// 公司地址
	  private String description;					// 公司描述
	  private String logo;							// 公司LOGO
	  private String modified;						// 修改时间(公司信息的最后一次被修改时间)
	  /***************************************************************************/
	  
	  public RewardData(){
		  this.task_location = "";
	  }
	  
	  /**
	  * 获取悬赏任务ID
	  * @return 悬赏任务ID
	  */
	  public String getTask_Id(){
		  return this.task_id;
	  }
	
	  /**
	  * 设置悬赏任务ID
	  * @param str 悬赏任务ID
	  */
	  public void setTask_Id(String str){
		  this.task_id = str;
	  }
	  
	  /**
	  * 获取悬赏任务类型
	  * @return 悬赏任务类型
	  */
	  public String getTask_Type(){
		  return this.task_type;
	  }
	
	  /**
	  * 设置悬赏任务类型
	  * @param str 悬赏任务类型
	  */
	  public void setTask_Type(String str){
		  this.task_type = str;
	  }
	  
	  /**
	  * 获取悬赏任务属于的行
	  * @return 悬赏任务属于的行
	  */
	  public String[] getTask_Category_Id(){
		  return this.task_category_id;
	  }
	
	  /**
	  * 设置悬赏任务属于的行
	  * @param str 悬赏任务属于的行
	  */
	  public void setTask_Category_Id(String[] str){
		  this.task_category_id = str;
	  }
	  
	  /**
	  * 获取悬赏任务名称
	  * @return 悬赏任务名称
	  */
	  public String getTask_Title(){
		  return this.task_title;
	  }
	
	  /**
	  * 设置悬赏任务名称
	  * @param str 悬赏任务名称
	  */
	  public void setTask_Title(String str){
		  this.task_title = str;
	  }
	  
	  /**
	  * 获取悬赏任务适用的城市
	  * @return 悬赏任务适用的城市
	  */
	  public String[] getTask_City(){
		  return this.task_city;
	  }
	
	  /**
	  * 设置悬赏任务适用的城市
	  * @param str 悬赏任务适用的城市
	  */
	  public void setTask_City(String[] str){
		  this.task_city = str;
	  }
	  
	  /**
	  * 获取具体地址
	  * @return 具体地址
	  */
	  public String getTask_location(){
		  return this.task_location;
	  }
	
	  /**
	  * 设置具体地址
	  * @param str 具体地址
	  */
	  public void setTask_location(String str){
		  this.task_location = str;
	  }
	  
	  /**
	  * 获取悬赏任务详情的url
	  * @return 悬赏任务详情的url
	  */
	  public String getTask_Url(){
		  return this.task_url;
	  }
	
	  /**
	  * 设置悬赏任务详情的url
	  * @param str 悬赏任务详情的url
	  */
	  public void setTask_Url(String str){
		  this.task_url = str;
	  }
	  
	  /**
	  * 获取悬赏任务关键字 
	  * @return 悬赏任务关键字 
	  */
	  public String[] getTask_Keyword(){
		  return this.task_keyword;
	  }
	
	  /**
	  * 设置悬赏任务关键字 
	  * @param str 悬赏任务关键字 
	  */
	  public void setTask_Keyword(String[] str){
		  this.task_keyword = str;
	  }
	  
	  /**
	  * 获取悬赏任务奖金
	  * @return 悬赏任务奖金
	  */
	  public String getTask_Bonus(){
		  return this.task_bonus;
	  }
	
	  /**
	  * 设置悬赏任务奖金
	  * @param str 悬赏任务奖金
	  */
	  public void setTask_Bonus(String str){
		  this.task_bonus = str;
	  }
	  
	  /**
	  * 获取公司名称 
	  * @return 公司名称 
	  */
	  public String getCompany_Name(){
		  return this.company_name;
	  }
	
	  /**
	  * 设置公司名称 
	  * @param str 公司名称 
	  */
	  public void setCompany_Name(String str){
		  this.company_name = str;
	  }
	  
	  /**
	  * 获取公司ID
	  * @return 公司ID
	  */
	  public String getCompany_Id(){
		  return this.company_id;
	  }
	
	  /**
	  * 设置公司ID
	  * @param str 公司ID
	  */
	  public void setCompany_Id(String str){
		  this.company_id = str;
	  }
	  
	  /**
	  * 获取公司url
	  * @return 公司url
	  */
	  public String getCompany_Url(){
		  return this.company_url;
	  }
	
	  
	  /**
	  * 获取公司是否被收藏标识
	  * @return
	  */
	 public String getM27_status() {
		  return m27_status;
	  }

	  /**
	   * 设置公司是否被收藏标识
	   * @param m27_status
	   */
	   public void setM27_status(String m27_status) {
		  this.m27_status = m27_status;
	  }

	/**
	  * 设置公司url
	  * @param str 公司url
	  */
	  public void setCompany_Url(String str){
		  this.company_url = str;
	  }
	  
	  /**
	  * 获取悬赏任务状态
	  * @return 悬赏任务状态
	  */
	  public String getTask_Status(){
		  return this.task_status;
	  }
	
	  /**
	  * 设置悬赏任务状态
	  * @param str 悬赏任务状态
	  */
	  public void setTask_Status(String str){
		  this.task_status = str;
	  }
	  
	  /**
	  * 获取发布者类型
	  * @return 发布者类型
	  */
	  public String getPublisher_Type(){
		  return this.publisher_type;
	  }
	
	  /**
	  * 设置发布者类型
	  * @param str 发布者类型
	  */
	  public void setPublisher_Type(String str){
		  this.publisher_type = str;
	  }
	  
	  /**
	  * 获取发布者名称
	  * @return 发布者名称
	  */
	  public String getPublisher_Name(){
		  return this.publisher_name;
	  }
	
	  /**
	  * 设置发布者名称
	  * @param str 发布者名称
	  */
	  public void setPublisher_Name(String str){
		  this.publisher_name = str;
	  }
	  
	  /**
	  * 获取发布时间
	  * @return 发布时间
	  */
	  public String getPublisher_Date(){
		  return this.publisher_date;
	  }
	
	  /**
	  * 设置发布时间
	  * @param str 发布时间
	  */
	  public void setPublisher_Date(String str){
		  this.publisher_date = str;
	  }
	  
	  /**
	  * 获取发布截止时间
	  * @return 发布截止时间
	  */
	  public String getPublisher_EndDate(){
		  return this.publisher_enddate;
	  }
	
	  /**
	  * 设置发布截止时间
	  * @param str 发布截止时间
	  */
	  public void setPublisher_EndDate(String str){
		  this.publisher_enddate = str;
	  }
	  
	  
	/**
	 * 获取付款标识
	 * @return 付款标识
	 */
	public String getPay_flag() {
		return pay_flag;
	}

	/**
	 * 设置付款标识
	 * @param pay_flag 付款标识
	 */
	public void setPay_flag(String pay_flag) {
		this.pay_flag = pay_flag;
	}

	/**
	  * 获取接受标识
	  * @return 接受标识
	  */
	  public String getAction_1(){
		  return this.action_1;
	  }
	
	  /**
	  * 设置接受标识
	  * @param str 接受标识
	  */
	  public void setAction_1(String str){
		  this.action_1 = str;
	  }
	  
	  /**
	  * 获取收藏标识
	  * @return 收藏标识
	  */
	  public String getAction_3(){
		  return this.action_3;
	  }
	
	  /**
	  * 设置收藏标识
	  * @param str 收藏标识
	  */
	  public void setAction_3(String str){
		  this.action_3 = str;
	  }
	  
	  /**
	  * 获取已读标识
	  * @return 已读标识
	  */
	  public String getAction_5(){
		  return this.action_5;
	  }
	
	  /**
	  * 设置已读标识
	  * @param str 已读标识
	  */
	  public void setAction_5(String str){
		  this.action_5 = str;
	  }
	  
	  /**
	  * 获取审核状态
	  * @return 审核状态
	  */
	  public String getVerify_status(){
		  return this.verify_status;
	  }
	
	  /**
	  * 设置审核状态
	  * @param str 审核状态
	  */
	  public void setVerify_status(String str){
		  this.verify_status = str;
	  }
	  
	  /**
	  * 获取简历来源
	  * @return 简历来源
	  */
	  public String getApply_type(){
		  return this.apply_type;
	  }
	
	  /**
	  * 设置简历来源
	  * @param str 简历来源
	  */
	  public void setApply_type(String str){
		  this.apply_type = str;
	  }
	  
	  /**
	  * 获取简历名称
	  * @return 简历名称
	  */
	  public String getResume_name(){
		  return this.resume_name;
	  }
	
	  /**
	  * 设置简历名称
	  * @param str 简历名称
	  */
	  public void setResume_name(String str){
		  this.resume_name = str;
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
	  * 获取图片url
	  * @return 图片url
	  */
	  public String getImg_url(){
		  return this.img_url;
	  }
	
	  /**
	  * 设置图片url
	  * @param str 图片url
	  */
	  public void setImg_url(String str){
		  this.img_url = str;
	  }
		
	  /**
	  * 获取参与人数
	  * @return 参与人数
	  */
	  public String getJoin_count(){
		  return this.join_count;
	  }
	
	  /**
	  * 设置参与人数
	  * @param str 参与人数
	  */
	  public void setJoin_count(String str){
		  this.join_count = str;
	  }
		
	  /**
	  * 获取标签
	  * @return 标签
	  */
	  public String[] getLabels(){
		  return this.labels;
	  }
	
	  /**
	  * 设置标签
	  * @param str 标签
	  */
	  public void setLabels(String[] str){
		  this.labels = str;
	  }
	  
	  /**
	  * 获取剩余天数
	  * @return 剩余天数
	  */
	  public String getDays_left(){
		  return this.days_left;
	  }
	
	  /**
	  * 设置剩余天数
	  * @param str 剩余天数
	  */
	  public void setDays_left(String str){
		  this.days_left = str;
	  }

	  /**
	  * 获取性别
	  * @return 性别
	  */
	  public String getGender(){
		  return this.gender;
	  }
	
	  /**
	  * 设置性别
	  * @param str 性别
	  */
	  public void setGender(String str){
		  this.gender = str;
	  }
		
	  /**
	  * 获取用户ID
	  * @return 用户ID
	  */
	  public String getUser_id(){
		  return this.user_id;
	  }
	
	  /**
	  * 设置用户ID
	  * @param str 用户ID
	  */
	  public void setUser_id(String str){
		  this.user_id = str;
	  }
	  
	  /**
	  * 获取用户选择要获取的悬赏任务类型
	  * @return 用户选择要获取的悬赏任务类型
	  */
	  public String getUserrequest_type(){
		  return this.userrequest_type;
	  }
	
	  /**
	  * 设置用户选择要获取的悬赏任务类型
	  * @param str 用户选择要获取的悬赏任务类型
	  */
	  public void setUserrequest_type(String str){
		  this.userrequest_type = str;
	  }
	  
	  
	  /**
	   * 获取请求的数据类型(个人:2, 公司:1, 混合:0) 
	  * @return 请求的数据类型(个人:2, 公司:1, 混合:0)
	  */
	  public int getRequest_datatype() {
		  return request_datatype;
	  }

	  /**
	   * 设置请求的数据类型(个人:2, 公司:1, 混合:0)
	   * @param request_datatype 请求的数据类型(个人:2, 公司:1, 混合:0)
	   */
	  public void setRequest_datatype(int request_datatype) {
		  this.request_datatype = request_datatype;
	  }

		/**
		 * 获取公司id
		 * @return 公司id
		 */
		public String getId() {
			return id;
		}
		
		/**
		 * 设置公司id
		 * @param id 公司id
		 */
		public void setId(String id) {
			this.id = id;
		}
		
		/**
		 * 获取公司名称
		 * @return 公司名称
		 */
		public String getTitle() {
			return title;
		}
		
		/**
		 * 设置公司名称
		 * @param title 公司名称
		 */
		public void setTitle(String title) {
			this.title = title;
		}
		
		/**
		 * 获取公司URL
		 * @return
		 */
		public String getUrl() {
			return url;
		}
		
		/**
		 * 设置公司URL
		 * @param url
		 */
		public void setUrl(String url) {
			this.url = url;
		}
		
		/**
		 * 获取公司地址
		 * @return 公司地址
		 */
		public String getAddress() {
			return address;
		}
		
		/**
		 * 设置公司地址
		 * @param address 公司地址
		 */
		public void setAddress(String address) {
			this.address = address;
		}
		
		/**
		 * 获取公司描述
		 * @return 公司描述
		 */
		public String getDescription() {
			return description;
		}
		
		/**
		 * 设置公司描述
		 * @param description 公司描述
		 */
		public void setDescription(String description) {
			this.description = description;
		}
		
		/**
		 * 获取公司LOGO
		 * @return 公司LOGO
		 */
		public String getLogo() {
			return logo;
		}
		
		/**
		 * 设置公司LOGO
		 * @param logo 公司LOGO
		 */
		public void setLogo(String logo) {
			this.logo = logo;
		}
		
		/**
		 * 获取公司信息修改时间
		 * @return 公司信息修改时间
		 */
		public String getModified() {
			return modified;
		}
		
		/**
		 * 设置公司信息修改时间
		 * @param modified 公司信息修改时间
		 */
		public void setModified(String modified) {
			this.modified = modified;
		}
		
	 /**
	 * 从数据库中获取指定请求类型的任务数据
	 * @param appContext
	 * @param nRequestType
	 * @param strRewardId
	 * @param strUserId
	 * @param bUseParameterId		true: 使用参数中传递过来的用户ID/false: 不使用参数中传递过来的用户ID
	 * @return
	 */
	public static List<RewardData> getRewardData (AppContext appContext, int nRequestDataType,
			  int nRequestType, String strRewardId, String strUserId,
			  boolean bUseParameterId){
			
			List<RewardData> lsRD = new ArrayList<RewardData>();
			
			DBUtils dbu = DBUtils.getInstance(appContext);
			
			if(!bUseParameterId){
				if (appContext.isLogin()) {
					strUserId = appContext.getUserId();
				} else {
					//strUserId = appContext.getAppId();
					strUserId = "";
				}
			}else{
				if(null == strUserId){
					return lsRD;
				}
			}
			
			String strnRequestDataType = String.valueOf(nRequestDataType);
			String strUserRequestType = String.valueOf(nRequestType);
			
			String sql = DBUtils.KEY_REWARD_USERID + " = \"" + strUserId + "\"";
			
			// 请求的数据类型(个人:2, 公司:1, 混合:0)
			if(null != strnRequestDataType){
				if(!strnRequestDataType.isEmpty()){
					sql = sql + " AND " + DBUtils.KEY_REWARD_REQUESTDATATYPE + " = \"" + strnRequestDataType + "\"";
				}
			}
			
			// 
			if(null != strUserRequestType){
				if(!strUserRequestType.isEmpty()){
					sql = sql + " AND " + DBUtils.KEY_REWARD_USERREQUEST_TYPE + " = \"" + strUserRequestType + "\"";
				}
			}
			
			// 
			if(null != strRewardId){
				if(!strRewardId.isEmpty()){
					sql = sql + " AND " + DBUtils.KEY_REWARD_TASKID + " = \"" + strUserRequestType + "\"";
				}
			}
			
			Cursor c = dbu.query(DBUtils.rewardTableName, new String[] {"*"}, sql);
			
			if (null != c) {
				if(c.getCount() > 0){
					for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
						RewardData rd = new RewardData();
						
						// 用户名
						rd.setUser_id(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_USERID)));
						// 用户选择要获取的悬赏任务类型
						rd.setUserrequest_type(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_USERREQUEST_TYPE)));
						// 悬赏任务ID
						rd.setTask_Id(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKID)));
						// 悬赏任务类型
						rd.setTask_Type(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKTYPE)));
						// 悬赏任务属于的行业
						String strCategoryId = c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKCATEGORYID));
						if(null != strCategoryId){
							if(!strCategoryId.isEmpty()){
								if(!strCategoryId.equals("null")){
									String[] strCategoryIdArray = ObjectUtils.getStringArrayFormJsonString(strCategoryId);
									rd.setTask_Category_Id(strCategoryIdArray);
								}
							}
						}
						// 悬赏任务名称
						rd.setTask_Title(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKTITLE)));
						// 悬赏任务适用的城市
						String strCity = c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKCITY));
						if(null != strCity){
							if(!strCity.isEmpty()){
								if(!strCity.equals("null")){
									String[] strCityArray = ObjectUtils.getStringArrayFormJsonString(strCity);
									rd.setTask_City(strCityArray);
								}
							}
						}
						// 具体地址
						rd.setTask_location(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKLOCATION)));
						// 悬赏任务详情的url(html5)
						rd.setTask_Url(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKURL)));
						// 悬赏任务关键字 
						String strKeyword = c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKKEYWORD));
						if(null != strKeyword){
							if(!strKeyword.isEmpty()){
								if(!strKeyword.equals("null")){
									String[] strKeywordArray = ObjectUtils.getStringArrayFormJsonString(strKeyword);
									rd.setTask_Keyword(strKeywordArray);
								}
							}
						}
						// 悬赏任务奖金
						rd.setTask_Bonus(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKBONUS)));
						// 公司名称 
						rd.setCompany_Name(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_COMPANYNAME)));
						// 公司url
						rd.setCompany_Url(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_COMPANYURL)));
						// 公司图片url
						rd.setImg_url(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_COMPANIMGYURL)));
						// 公司id
						rd.setCompany_Id(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_COMPANYID)));
						// 公司收藏状态
						rd.setM27_status(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_COMPANYCOLLECTION)));
						// 悬赏任务状态
						rd.setTask_Status(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKSTATUS)));
						// 发布者类型
						rd.setPublisher_Type(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_PUBLISHERTYPE)));
						// 发布者名称
						rd.setPublisher_Name(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_PUBLISHERNAME)));
						// 发布时间
						rd.setPublisher_Date(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_PUBLISHERDATE)));
						// 发布截止时间  
						rd.setPublisher_EndDate(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_PUBLISHERENDDATE)));
						// 接受标识
						rd.setAction_1(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_ACTION1)));
						// 收藏标识
						rd.setAction_3(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_ACTION3)));
						// 已读标识
						rd.setAction_5(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_ACTION5)));
						// 审核状态
						rd.setVerify_status(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_VERIFYSTATUS)));
						// 简历名称
						rd.setResume_name(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_RESUMENAME)));
						// 简历来源
						rd.setApply_type(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_APPLYTYPE)));
						// 请求的数据类型(个人:2, 公司:1, 混合:0)
						rd.setRequest_datatype(c.getInt(c
								.getColumnIndex(DBUtils.KEY_REWARD_REQUESTDATATYPE)));
						// 剩余天数
						rd.setDays_left(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_VALIDDATE)));
						// 关注数
						rd.setJoin_count(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_CONCERNNUM)));
						
						lsRD.add(rd);
					}
				}
				
				c.close();
			}
			
			return lsRD;	
		}
	  
	/**
	 * 保存搜索数据
	 * @param appContext
	 * @param rfc
	 * @return
	 */
	public static boolean saveRewardData (AppContext appContext,
			RewardData rd){
		boolean bRet = false;
		
//			// 获取数据库中是任务数据
//			List<RewardData> lsRD = getRewardData(appContext, rd.getUserrequest_type(), null);
//
//			boolean bDel = false;
//			RewardData rdDelete = new RewardData();
//			if(lsRD.size() >= 10){
//				bDel = true;
//			}
//
//			// 判断表中是否有重复的数据
//			for(int i = 0; i < lsRD.size(); i++){
//				RewardData rdTemp = lsRD.get(i);
//				// 判断悬赏任务ID是否相同
//				if(rd.getTask_Id().equals(rdTemp.getTask_Id())){
//					// 判断用户选择要获取的悬赏任务类型是否相同
//					if(rd.getUserrequest_type().equals(rdTemp.getUserrequest_type())){
//						// 删除重复数据
//						deleteRewardData(appContext, rd);
//						bDel = false;
//						break;
//					}
//				}
//				
//				if((lsRD.size()- 1) == i && bDel){
//					rdDelete = rdTemp;
//				}
//			}
//			
//			// 如果记录条数超期限， 删除第一条
//			if(bDel){
//				deleteRewardData(appContext, rdDelete);
//			}
		
		// 保存新数据
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getUserId();
		}
		
		ContentValues values = new ContentValues();
		// 用户ID
		values.put(DBUtils.KEY_REWARD_USERID, strUserId);
		// 用户选择要获取的悬赏任务类型
		values.put(DBUtils.KEY_REWARD_USERREQUEST_TYPE, rd.getUserrequest_type());
		// 悬赏任务ID
		values.put(DBUtils.KEY_REWARD_TASKID, rd.getTask_Id());
		// 悬赏任务类型
		values.put(DBUtils.KEY_REWARD_TASKTYPE, rd.getTask_Type());
		// 悬赏任务属于的行业
		String strCategoryId = ObjectUtils.getJsonStringFromObject(rd.getTask_Category_Id());
		values.put(DBUtils.KEY_REWARD_TASKCATEGORYID, strCategoryId);
		// 悬赏任务名称
		values.put(DBUtils.KEY_REWARD_TASKTITLE, rd.getTask_Title());
		// 悬赏任务适用的城市
		String strCity = ObjectUtils.getJsonStringFromObject(rd.getTask_City());
		values.put(DBUtils.KEY_REWARD_TASKCITY, strCity);
		// 具体地址
		values.put(DBUtils.KEY_REWARD_TASKLOCATION, rd.getTask_location());
		// 悬赏任务详情的url(html5)
		values.put(DBUtils.KEY_REWARD_TASKURL, rd.getTask_Url());
		// 悬赏任务关键字 
		String strKeyword = ObjectUtils.getJsonStringFromObject(rd.getTask_Keyword());
		values.put(DBUtils.KEY_REWARD_TASKKEYWORD, strKeyword);
		// 悬赏任务奖金
		values.put(DBUtils.KEY_REWARD_TASKBONUS, rd.getTask_Bonus());
		// 公司名称 
		values.put(DBUtils.KEY_REWARD_COMPANYNAME, rd.getCompany_Name());
		// 公司Id
		values.put(DBUtils.KEY_REWARD_COMPANYID, rd.getCompany_Id());
		// 公司url
		values.put(DBUtils.KEY_REWARD_COMPANYURL, rd.getCompany_Url());
		// 公司图片url
		values.put(DBUtils.KEY_REWARD_COMPANIMGYURL, rd.getImg_url());
		// 公司收藏
		values.put(DBUtils.KEY_REWARD_COMPANYCOLLECTION, rd.getM27_status());
		// 悬赏任务状态 
		values.put(DBUtils.KEY_REWARD_TASKSTATUS, rd.getTask_Status());
		// 发布者类型
		values.put(DBUtils.KEY_REWARD_PUBLISHERTYPE, rd.getPublisher_Type());
		// 发布者名称
		values.put(DBUtils.KEY_REWARD_PUBLISHERNAME, rd.getPublisher_Name());
		// 发布时间
		values.put(DBUtils.KEY_REWARD_PUBLISHERDATE, rd.getPublisher_Date());
		// 发布截止时间  
		values.put(DBUtils.KEY_REWARD_PUBLISHERENDDATE, rd.getPublisher_EndDate());
		// 接受标识
		values.put(DBUtils.KEY_REWARD_ACTION1, rd.getAction_1());
		// 收藏标识
		values.put(DBUtils.KEY_REWARD_ACTION3, rd.getAction_3());
		// 已读标识
		values.put(DBUtils.KEY_REWARD_ACTION5, rd.getAction_5());
		// 审核状态
		values.put(DBUtils.KEY_REWARD_VERIFYSTATUS, rd.getVerify_status());
		// 简历名称
		values.put(DBUtils.KEY_REWARD_RESUMENAME, rd.getResume_name());
		// 简历来源
		values.put(DBUtils.KEY_REWARD_APPLYTYPE, rd.getApply_type());
		// 请求的数据类型(个人:2, 公司:1, 混合:0)
		values.put(DBUtils.KEY_REWARD_REQUESTDATATYPE, rd.getRequest_datatype());
		// 剩余天数
		values.put(DBUtils.KEY_REWARD_VALIDDATE, rd.getDays_left());
		// 关注数
		values.put(DBUtils.KEY_REWARD_CONCERNNUM, rd.getJoin_count());
			
		long lnRet = dbu.save(DBUtils.rewardTableName, values);
		if(lnRet > 0){
			bRet = true;
		}else{
			bRet = false;
		}
				
		return bRet;
	}
		
	/**
	 * 删除任务数据
	 * @param appContext
	 * @param rfcDelete
	 */
	public static void deleteRewardData (AppContext appContext, RewardData rd){
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getUserId();
		} else {
//				strUserId = appContext.getAppId();
		}
		
		// 悬赏任务属于的行业
		String strCategoryId = ObjectUtils.getJsonStringFromObject(rd.getTask_Category_Id());
		// 悬赏任务适用的城市
		String strCity = ObjectUtils.getJsonStringFromObject(rd.getTask_City());
		// 悬赏任务关键字 
		String strKeyword = ObjectUtils.getJsonStringFromObject(rd.getTask_Keyword());
		
		String whereClause = DBUtils.KEY_REWARD_USERID + " = ? AND "
		+ DBUtils.KEY_REWARD_USERREQUEST_TYPE + " = ? AND "
		+ DBUtils.KEY_REWARD_TASKID + " = ? AND "
		+ DBUtils.KEY_REWARD_TASKTYPE + " = ? AND " 
		+ DBUtils.KEY_REWARD_TASKCATEGORYID + " = ? AND " 
		+ DBUtils.KEY_REWARD_TASKTITLE + " = ? AND "
		+ DBUtils.KEY_REWARD_TASKCITY + " = ? AND "
		+ DBUtils.KEY_REWARD_TASKLOCATION + " = ? AND "
		+ DBUtils.KEY_REWARD_TASKURL + " = ? AND "
		+ DBUtils.KEY_REWARD_TASKKEYWORD + " = ? AND "
		+ DBUtils.KEY_REWARD_TASKBONUS + " = ? AND "
		+ DBUtils.KEY_REWARD_COMPANYNAME + " = ? AND "
		+ DBUtils.KEY_REWARD_COMPANYURL + " = ? AND "
		+ DBUtils.KEY_REWARD_COMPANYID + " = ? AND "
		+ DBUtils.KEY_REWARD_COMPANIMGYURL + " = ? AND "
		+ DBUtils.KEY_REWARD_COMPANYCOLLECTION + " = ? AND "
		+ DBUtils.KEY_REWARD_TASKSTATUS + " = ? AND "
		+ DBUtils.KEY_REWARD_PUBLISHERTYPE + " = ? AND "
		+ DBUtils.KEY_REWARD_PUBLISHERNAME + " = ? AND "
		+ DBUtils.KEY_REWARD_PUBLISHERDATE + " = ? AND "
		+ DBUtils.KEY_REWARD_PUBLISHERENDDATE + " = ? AND "
		+ DBUtils.KEY_REWARD_ACTION1 + " = ? AND "
		+ DBUtils.KEY_REWARD_ACTION3 + " = ? AND "
		+ DBUtils.KEY_REWARD_ACTION5 + " = ? AND "
		+ DBUtils.KEY_REWARD_VERIFYSTATUS + " = ?"
		+ DBUtils.KEY_REWARD_RESUMENAME + " = ?"
		+ DBUtils.KEY_REWARD_APPLYTYPE + " = ?"
		+ DBUtils.KEY_REWARD_REQUESTDATATYPE + " = ?"
		+ DBUtils.KEY_REWARD_VALIDDATE + " = ?"
		+ DBUtils.KEY_REWARD_CONCERNNUM + " = ?"
		;
		
		String[] whereArgs = {strUserId, rd.getUserrequest_type(), rd.getTask_Id(), rd.getTask_Type(), strCategoryId, rd.getTask_Title(),
				strCity, rd.getTask_location(), rd.getTask_Url(), strKeyword, rd.getTask_Bonus(),
				rd.getCompany_Name(), rd.getCompany_Url(), rd.getCompany_Id(), rd.getImg_url(),
				rd.getM27_status(), rd.getTask_Status(), rd.getPublisher_Type(),rd.getPublisher_Name(),
				rd.getPublisher_Date(), rd.getPublisher_EndDate(), rd.getAction_1(), rd.getAction_3(), rd.getAction_5(),
				rd.getVerify_status(), rd.getResume_name(), rd.getApply_type(),
				String.valueOf(rd.getRequest_datatype()), rd.getDays_left(), rd.getJoin_count()
				};
		
		dbu.delete(DBUtils.rewardTableName, whereClause, whereArgs);
		
	} 
		
	/**
	 * 删除同一用户，同一请求类型的任务数据
	 * @param appContext
	 * @param rfcDelete
	 */
	public static void deleteAllRewardData (AppContext appContext, 
			int nRequestDataType, int nRequestType){
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getUserId();
		} else {
//				strUserId = appContext.getAppId();
		}
		
		String whereClause = DBUtils.KEY_REWARD_USERID + " = ? AND "
				+ DBUtils.KEY_REWARD_REQUESTDATATYPE + " = ? AND "
				+ DBUtils.KEY_REWARD_USERREQUEST_TYPE + " = ? ";	
		
		String[] whereArgs = {strUserId, String.valueOf(nRequestDataType), String.valueOf(nRequestType)};
		
		if(null == whereArgs 
				|| null == whereClause
				|| null == DBUtils.rewardTableName
				){
			return;
		}
		
		dbu.delete(DBUtils.rewardTableName, whereClause, whereArgs);
		
	}
		
	/**
	 * 更新数据
	 * @param appContext
	 * @param rld
	 */
	public static void upRewardData(AppContext appContext, RewardData rld){
		DBUtils dbu = DBUtils.getInstance(appContext);
				
		String strUserId = appContext.getUserId();
		if(null == strUserId){
			strUserId = "";
		}

		String whereClause = DBUtils.KEY_REWARD_USERID + " = " + strUserId + " AND "
				+ DBUtils.KEY_REWARD_TASKID + " = " + rld.getTask_Id() + " AND "
				+ DBUtils.KEY_REWARD_USERREQUEST_TYPE + " = " + rld.getUserrequest_type();	
		
		ContentValues cv = new ContentValues();
		// 是否接受任务
		cv.put(DBUtils.KEY_REWARD_ACTION1, rld.getAction_1());
		// 是否收藏
		cv.put(DBUtils.KEY_REWARD_ACTION3, rld.getAction_3());
		// 是否已读
		cv.put(DBUtils.KEY_REWARD_ACTION5, rld.getAction_5());
		// 公司收藏状态
		cv.put(DBUtils.KEY_REWARD_COMPANYCOLLECTION, rld.getM27_status());
		
		dbu.update(DBUtils.rewardTableName, whereClause, cv);
	}
		
	
	/**
	 * 检查任务是否已读
	 * @param appContext
	 * @param strRewardId
	 * @return
	 */
	public static boolean checkRewardIsRead (AppContext appContext,
			  String strRewardId){
		if(null == strRewardId){
			return false;
		}
		
		if(strRewardId.isEmpty()){
			return false;
		}
			
		DBUtils dbu = DBUtils.getInstance(appContext);
			
		String sql = DBUtils.KEY_REWARDREAD_TASKID + " = \"" + strRewardId + "\"";
		
		Cursor c = dbu.query(DBUtils.rewardreadTableName, new String[] {"*"}, sql);
		
		if (null == c){
			return false;
		}
		
		if(c.getCount() <= 0){
			c.close();
			return false;
		}
			
		c.close();
	
		return true;	
	}
	
	/**
	 * 保存已读任务ID
	 * @param appContext
	 * @param strRewardId
	 * @return
	 */
	public static boolean saveReadRewardID (AppContext appContext,
			String strRewardId){

		if(null == strRewardId){
			return false;
		}
		
		if(strRewardId.isEmpty()){
			return false;
		}
		
		// 保存新数据
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		ContentValues values = new ContentValues();
		// 悬赏任务ID
		values.put(DBUtils.KEY_REWARDREAD_TASKID, strRewardId);
			
		dbu.save(DBUtils.rewardreadTableName, values);
		
		return true;
	}
	
	/**
	 * 保存搜索数据(选项卡)
	 * @param appContext
	 * @param rfc
	 * @return
	 */
	public static boolean saveRewardDataForTabHost(AppContext appContext,
			RewardData rd, int nTabtype){
		boolean bRet = false;
		
		// 保存新数据
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getUserId();
		}
		
		ContentValues values = new ContentValues();
		// 用户ID
		values.put(DBUtils.KEY_REWARD_USERID, strUserId);
		// 用户选择要获取的悬赏任务类型
		values.put(DBUtils.KEY_REWARD_USERREQUEST_TYPE, rd.getUserrequest_type());
		// 悬赏任务ID
		values.put(DBUtils.KEY_REWARD_TASKID, rd.getTask_Id());
		// 悬赏任务类型
		values.put(DBUtils.KEY_REWARD_TASKTYPE, rd.getTask_Type());
		// 悬赏任务属于的行业
		String strCategoryId = ObjectUtils.getJsonStringFromObject(rd.getTask_Category_Id());
		values.put(DBUtils.KEY_REWARD_TASKCATEGORYID, strCategoryId);
		// 悬赏任务名称
		values.put(DBUtils.KEY_REWARD_TASKTITLE, rd.getTask_Title());
		// 悬赏任务适用的城市
		String strCity = ObjectUtils.getJsonStringFromObject(rd.getTask_City());
		values.put(DBUtils.KEY_REWARD_TASKCITY, strCity);
		// 具体地址
		values.put(DBUtils.KEY_REWARD_TASKLOCATION, rd.getTask_location());
		// 悬赏任务详情的url(html5)
		values.put(DBUtils.KEY_REWARD_TASKURL, rd.getTask_Url());
		// 悬赏任务关键字 
		String strKeyword = ObjectUtils.getJsonStringFromObject(rd.getTask_Keyword());
		values.put(DBUtils.KEY_REWARD_TASKKEYWORD, strKeyword);
		// 悬赏任务奖金
		values.put(DBUtils.KEY_REWARD_TASKBONUS, rd.getTask_Bonus());
		// 公司名称 
		values.put(DBUtils.KEY_REWARD_COMPANYNAME, rd.getCompany_Name());
		// 公司Id
		values.put(DBUtils.KEY_REWARD_COMPANYID, rd.getCompany_Id());
		// 公司url
		values.put(DBUtils.KEY_REWARD_COMPANYURL, rd.getCompany_Url());
		// 公司图片url
		values.put(DBUtils.KEY_REWARD_COMPANIMGYURL, rd.getImg_url());
		// 公司收藏
		values.put(DBUtils.KEY_REWARD_COMPANYCOLLECTION, rd.getM27_status());
		// 悬赏任务状态 
		values.put(DBUtils.KEY_REWARD_TASKSTATUS, rd.getTask_Status());
		// 发布者类型
		values.put(DBUtils.KEY_REWARD_PUBLISHERTYPE, rd.getPublisher_Type());
		// 发布者名称
		values.put(DBUtils.KEY_REWARD_PUBLISHERNAME, rd.getPublisher_Name());
		// 发布时间
		values.put(DBUtils.KEY_REWARD_PUBLISHERDATE, rd.getPublisher_Date());
		// 发布截止时间  
		values.put(DBUtils.KEY_REWARD_PUBLISHERENDDATE, rd.getPublisher_EndDate());
		// 接受标识
		values.put(DBUtils.KEY_REWARD_ACTION1, rd.getAction_1());
		// 收藏标识
		values.put(DBUtils.KEY_REWARD_ACTION3, rd.getAction_3());
		// 已读标识
		values.put(DBUtils.KEY_REWARD_ACTION5, rd.getAction_5());
		// 审核状态
		values.put(DBUtils.KEY_REWARD_VERIFYSTATUS, rd.getVerify_status());
		// 简历名称
		values.put(DBUtils.KEY_REWARD_RESUMENAME, rd.getResume_name());
		// 简历来源
		values.put(DBUtils.KEY_REWARD_APPLYTYPE, rd.getApply_type());
		// 请求的数据类型(个人:2, 公司:1, 混合:0)
		values.put(DBUtils.KEY_REWARD_REQUESTDATATYPE, rd.getRequest_datatype());
		// 剩余天数
		values.put(DBUtils.KEY_REWARD_VALIDDATE, rd.getDays_left());
		// 关注数
		values.put(DBUtils.KEY_REWARD_CONCERNNUM, rd.getJoin_count());
		// 行业类型(选项卡)
		values.put(DBUtils.KEY_REWARD_TABTYPE, nTabtype);
			
		long lnRet = dbu.save(DBUtils.rewardtabhostTableName, values);
		if(lnRet > 0){
			bRet = true;
		}else{
			bRet = false;
		}
				
		return bRet;
	}
	
	/**
	 * 删除同一用户，同一请求类型的任务数据
	 * @param appContext
	 * @param rfcDelete
	 */
	public static void deleteAllRewardDataForTabHost(AppContext appContext, 
			int nRequestDataType, int nRequestType, int nTabtype){
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getUserId();
		} else {
//				strUserId = appContext.getAppId();
		}
		
		String whereClause = DBUtils.KEY_REWARD_USERID + " = ? AND "
				+ DBUtils.KEY_REWARD_REQUESTDATATYPE + " = ? AND "
				+ DBUtils.KEY_REWARD_USERREQUEST_TYPE + " = ? AND "
				+ DBUtils.KEY_REWARD_TABTYPE + " = ?";	
		
		String[] whereArgs = {strUserId,
				String.valueOf(nRequestDataType),
				String.valueOf(nRequestType),
				String.valueOf(nTabtype)};
		
		if(null == whereArgs 
				|| null == whereClause
				|| null == DBUtils.rewardtabhostTableName
				){
			return;
		}
		
		dbu.delete(DBUtils.rewardtabhostTableName, whereClause, whereArgs);
		
	}
	
	/**
	 * 更新数据(选项卡)
	 * @param appContext
	 * @param rld
	 */
	public static void upRewardDataForTabHost(AppContext appContext, RewardData rld,
			int nIndustrytype){
		DBUtils dbu = DBUtils.getInstance(appContext);
				
		String strUserId = appContext.getUserId();
		if(null == strUserId){
			strUserId = "";
		}

		String whereClause = DBUtils.KEY_REWARD_USERID + " = " + strUserId + " AND "
				+ DBUtils.KEY_REWARD_TASKID + " = " + rld.getTask_Id() + " AND "
				+ DBUtils.KEY_REWARD_USERREQUEST_TYPE + " = " + rld.getUserrequest_type()
				+ DBUtils.KEY_REWARD_TABTYPE + " = " + nIndustrytype;	
		
		ContentValues cv = new ContentValues();
		// 是否接受任务
		cv.put(DBUtils.KEY_REWARD_ACTION1, rld.getAction_1());
		// 是否收藏
		cv.put(DBUtils.KEY_REWARD_ACTION3, rld.getAction_3());
		// 是否已读
		cv.put(DBUtils.KEY_REWARD_ACTION5, rld.getAction_5());
		// 公司收藏状态
		cv.put(DBUtils.KEY_REWARD_COMPANYCOLLECTION, rld.getM27_status());
		
		dbu.update(DBUtils.rewardtabhostTableName, whereClause, cv);
	}
	
	 /**
	 * 从数据库中获取指定请求类型的任务数据(选项卡)
	 * @param appContext
	 * @param nRequestType
	 * @param nIndustrytype
	 * @param strRewardId
	 * @param strUserId
	 * @param bUseParameterId		true: 使用参数中传递过来的用户ID/false: 不使用参数中传递过来的用户ID
	 * @return
	 */
	public static List<RewardData> getRewardDataTabHost (AppContext appContext, int nRequestDataType,
			  int nRequestType, int nTabtype,
			  String strRewardId, String strUserId,
			  boolean bUseParameterId){
			
			List<RewardData> lsRD = new ArrayList<RewardData>();
			
			DBUtils dbu = DBUtils.getInstance(appContext);
			
			if(!bUseParameterId){
				if (appContext.isLogin()) {
					strUserId = appContext.getUserId();
				} else {
					//strUserId = appContext.getAppId();
					strUserId = "";
				}
			}else{
				if(null == strUserId){
					return lsRD;
				}
			}
			
			String strnRequestDataType = String.valueOf(nRequestDataType);
			String strUserRequestType = String.valueOf(nRequestType);
			
			String sql = DBUtils.KEY_REWARD_USERID + " = \"" + strUserId + "\"";
			
			// 请求的数据类型(个人:2, 公司:1, 混合:0)
			if(null != strnRequestDataType){
				if(!strnRequestDataType.isEmpty()){
					sql = sql + " AND " + DBUtils.KEY_REWARD_REQUESTDATATYPE + " = \"" + strnRequestDataType + "\"";
				}
			}
			
			// 
			if(null != strUserRequestType){
				if(!strUserRequestType.isEmpty()){
					sql = sql + " AND " + DBUtils.KEY_REWARD_USERREQUEST_TYPE + " = \"" + strUserRequestType + "\"";
				}
			}
			
			// 选项卡类型
			String strTabtype = String.valueOf(nTabtype);
			if(null != strTabtype){
				if(!strTabtype.isEmpty()){
					sql = sql + " AND " + DBUtils.KEY_REWARD_TABTYPE +
							" = \"" + strTabtype + "\"";
				}
			}
			
			// 
			if(null != strRewardId){
				if(!strRewardId.isEmpty()){
					sql = sql + " AND " + DBUtils.KEY_REWARD_TASKID + " = \"" + strUserRequestType + "\"";
				}
			}
			
			Cursor c = dbu.query(DBUtils.rewardtabhostTableName, new String[] {"*"}, sql);
			
			if (null != c) {
				if(c.getCount() > 0){
					for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
						RewardData rd = new RewardData();
						
						// 用户名
						rd.setUser_id(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_USERID)));
						// 用户选择要获取的悬赏任务类型
						rd.setUserrequest_type(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_USERREQUEST_TYPE)));
						// 悬赏任务ID
						rd.setTask_Id(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKID)));
						// 悬赏任务类型
						rd.setTask_Type(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKTYPE)));
						// 悬赏任务属于的行业
						String strCategoryId = c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKCATEGORYID));
						if(null != strCategoryId){
							if(!strCategoryId.isEmpty()){
								if(!strCategoryId.equals("null")){
									String[] strCategoryIdArray = ObjectUtils.getStringArrayFormJsonString(strCategoryId);
									rd.setTask_Category_Id(strCategoryIdArray);
								}
							}
						}
						// 悬赏任务名称
						rd.setTask_Title(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKTITLE)));
						// 悬赏任务适用的城市
						String strCity = c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKCITY));
						if(null != strCity){
							if(!strCity.isEmpty()){
								if(!strCity.equals("null")){
									String[] strCityArray = ObjectUtils.getStringArrayFormJsonString(strCity);
									rd.setTask_City(strCityArray);
								}
							}
						}
						// 具体地址
						rd.setTask_location(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKLOCATION)));
						// 悬赏任务详情的url(html5)
						rd.setTask_Url(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKURL)));
						// 悬赏任务关键字 
						String strKeyword = c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKKEYWORD));
						if(null != strKeyword){
							if(!strKeyword.isEmpty()){
								if(!strKeyword.equals("null")){
									String[] strKeywordArray = ObjectUtils.getStringArrayFormJsonString(strKeyword);
									rd.setTask_Keyword(strKeywordArray);
								}
							}
						}
						// 悬赏任务奖金
						rd.setTask_Bonus(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKBONUS)));
						// 公司名称 
						rd.setCompany_Name(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_COMPANYNAME)));
						// 公司url
						rd.setCompany_Url(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_COMPANYURL)));
						// 公司图片url
						rd.setImg_url(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_COMPANIMGYURL)));
						// 公司id
						rd.setCompany_Id(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_COMPANYID)));
						// 公司收藏状态
						rd.setM27_status(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_COMPANYCOLLECTION)));
						// 悬赏任务状态
						rd.setTask_Status(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_TASKSTATUS)));
						// 发布者类型
						rd.setPublisher_Type(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_PUBLISHERTYPE)));
						// 发布者名称
						rd.setPublisher_Name(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_PUBLISHERNAME)));
						// 发布时间
						rd.setPublisher_Date(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_PUBLISHERDATE)));
						// 发布截止时间  
						rd.setPublisher_EndDate(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_PUBLISHERENDDATE)));
						// 接受标识
						rd.setAction_1(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_ACTION1)));
						// 收藏标识
						rd.setAction_3(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_ACTION3)));
						// 已读标识
						rd.setAction_5(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_ACTION5)));
						// 审核状态
						rd.setVerify_status(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_VERIFYSTATUS)));
						// 简历名称
						rd.setResume_name(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_RESUMENAME)));
						// 简历来源
						rd.setApply_type(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_APPLYTYPE)));
						// 请求的数据类型(个人:2, 公司:1, 混合:0)
						rd.setRequest_datatype(c.getInt(c
								.getColumnIndex(DBUtils.KEY_REWARD_REQUESTDATATYPE)));
						// 剩余天数
						rd.setDays_left(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_VALIDDATE)));
						// 关注数
						rd.setJoin_count(c.getString(c
								.getColumnIndex(DBUtils.KEY_REWARD_CONCERNNUM)));
						
						lsRD.add(rd);
					}
				}
				
				c.close();
			}
			
			return lsRD;	
		}
}

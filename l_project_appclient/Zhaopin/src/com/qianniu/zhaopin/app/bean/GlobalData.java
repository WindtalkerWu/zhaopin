package com.qianniu.zhaopin.app.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class GlobalData {
	private List<OneLevelData> location_hot_city;			// 热点城市
	private List<OneLevelData> hot_keyword;					// 热点职位
	private List<OneLevelData> education;					// 学历
	private List<OneLevelData> jobstatus;					// 求职状态
	private List<OneLevelData> salary;						// 年薪
	private List<OneLevelData> work_experience;				// 到岗时间
	private List<OneLevelData> language;					// 语言技能
	private List<OneLevelData> rewardway;					// 悬赏方式
	private List<OneLevelData> rewardcycle;					// 悬赏周期
	private List<TwoLevelData> job_industry;				// 行业
	private List<OneLevelData> specialty;					// 专业
	private List<OneLevelData> language_mastery;			// 语言掌握程度
	private List<OneLevelData> language_literacy;			// 语言读写能力
	private List<OneLevelData> language_speaking;			// 语言听说能力
	private List<OneLevelData> allcity;						// 全部城市
	private List<OneLevelData> m125;						// 我的记录状态
	private List<OneLevelData> m126;						// 推荐理由
	private List<OneLevelData> m128;						// 图片基础地址
	private List<OneLevelData> m129;						// 图片提交地址
	private List<OneLevelData> m134;						// 悬赏任务发布状态
	private List<OneLevelData> m137;						// 接受任务模式
	private List<ThreeLevelJobFunctionsData> job_functions;	// 职能分类
	private List<OneLevelData> m143;						// 付款标识
	
	public GlobalData(){
		this.location_hot_city = new ArrayList<OneLevelData>();
		this.hot_keyword = new ArrayList<OneLevelData>();
		this.education = new ArrayList<OneLevelData>();
		this.jobstatus = new ArrayList<OneLevelData>();
		this.salary = new ArrayList<OneLevelData>();
		this.work_experience = new ArrayList<OneLevelData>();
		this.language = new ArrayList<OneLevelData>();
		this.rewardway = new ArrayList<OneLevelData>();
		this.rewardcycle = new ArrayList<OneLevelData>();
		this.job_industry = new ArrayList<TwoLevelData>();
		this.specialty = new ArrayList<OneLevelData>();
		this.language_mastery = new ArrayList<OneLevelData>();
		this.language_literacy = new ArrayList<OneLevelData>();
		this.language_speaking = new ArrayList<OneLevelData>();
		this.allcity = new ArrayList<OneLevelData>();
		this.m125 = new ArrayList<OneLevelData>();
		this.m126 = new ArrayList<OneLevelData>();
		this.m128 = new ArrayList<OneLevelData>();
		this.m129 = new ArrayList<OneLevelData>();
		this.m134 = new ArrayList<OneLevelData>();
		this.m137 = new ArrayList<OneLevelData>();
		this.job_functions = new ArrayList<ThreeLevelJobFunctionsData>();
		this.m143 = new ArrayList<OneLevelData>();
	}

	/**
	 * 获取城市数据列表
	 * @return 城市数据列表
	 */
	public List<OneLevelData> getLocation_Hot_City(){
		return this.location_hot_city;
	}
	
	/**
	 * 设置城市数据列表
	 * @param listOneLeve 城市数据列表
	 */
	public void setLocation_Hot_City(List<OneLevelData> listOneLeve){
		this.location_hot_city = listOneLeve;
	}
	
	/**
	 * 获取热点职位数据列表
	 * @return 热点职位数据列表
	 */
	public List<OneLevelData> getHot_Keyword(){
		return this.hot_keyword;
	}
	
	/**
	 * 设置热点职位数据列表
	 * @param listOneLeve 热点职位数据列表
	 */
	public void setHot_Keyword(List<OneLevelData> listOneLeve){
		this.hot_keyword = listOneLeve;
	}
	
	/**
	 * 获取学历数据列表
	 * @return 学历数据列表
	 */
	public List<OneLevelData> getEducation(){
		return this.education;
	}
	
	/**
	 * 设置学历数据列表
	 * @param listOneLeve 学历数据列表
	 */
	public void setEducation(List<OneLevelData> listOneLeve){
		this.education = listOneLeve;
	}
	
	/**
	 * 获取求职状态列表
	 * @return 求职状态列表
	 */
	public List<OneLevelData> getJobstatus(){
		return this.jobstatus;
	}
	
	/**
	 * 设置求职状态列表
	 * @param listOneLeve 求职状态列表
	 */
	public void setJobstatus(List<OneLevelData> listOneLeve){
		this.jobstatus = listOneLeve;
	}
	
	/**
	 * 获取年薪列表
	 * @return 年薪列表
	 */
	public List<OneLevelData> getSalary(){
		return this.salary;
	}
	
	/**
	 * 设置年薪列表
	 * @param listOneLeve 年薪列表
	 */
	public void setSalary(List<OneLevelData> listOneLeve){
		this.salary = listOneLeve;
	}
	
	/**
	 * 获取到岗时间列表
	 * @return 到岗时间列表
	 */
	public List<OneLevelData> getWork_Experience(){
		return this.work_experience;
	}
	
	/**
	 * 设置到岗时间列表
	 * @param listOneLeve 到岗时间列表
	 */
	public void setWork_Experience(List<OneLevelData> listOneLeve){
		this.work_experience = listOneLeve;
	}
	
	/**
	 * 获取语言技能列表
	 * @return 语言技能列表
	 */
	public List<OneLevelData> getLanguage(){
		return this.language;
	}
	
	/**
	 * 设置语言技能列表
	 * @param listOneLeve 语言技能列表
	 */
	public void setLanguage(List<OneLevelData> listOneLeve){
		this.language = listOneLeve;
	}
	
	/**
	 * 获取悬赏方式列表
	 * @return 悬赏方式列表
	 */
	public List<OneLevelData> getRewardway(){
		return this.rewardway;
	}
	
	/**
	 * 设置悬赏方式列表
	 * @param listOneLeve 悬赏方式列表
	 */
	public void setRewardway(List<OneLevelData> listOneLeve){
		this.rewardway = listOneLeve;
	}
	
	/**
	 * 获取悬赏周期列表
	 * @return 悬赏周期列表
	 */
	public List<OneLevelData> getRewardcycle(){
		return this.rewardcycle;
	}
	
	/**
	 * 设置悬赏周期列表
	 * @param listOneLeve 悬赏周期列表
	 */
	public void setRewardcycle(List<OneLevelData> listOneLeve){
		this.rewardcycle = listOneLeve;
	}
	
	/**
	 * 获取行业数据列表
	 * @return 行业数据列表
	 */
	public List<TwoLevelData> getJob_Industry(){
		return this.job_industry;
	}
	
	/**
	 * 设置行业数据列表
	 * @param listTwoLeve 行业数据列表
	 */
	public void setJob_Industry(List<TwoLevelData> listTwoLeve){
		this.job_industry = listTwoLeve;
	}
	
	/**
	 * 获取专业数据列表
	 * @return 专业数据列表
	 */
	public List<OneLevelData> getSpecialty(){
		return this.specialty;
	}
	
	/**
	 * 设置专业数据列表
	 * @param listOneLeve 专业数据列表
	 */
	public void setSpecialty(List<OneLevelData> listOneLeve){
		this.specialty = listOneLeve;
	}
	
	/**
	 * 获取语言掌握程度数据列表
	 * @return 语言掌握程度数据列表
	 */
	public List<OneLevelData> getLanguage_Mastery(){
		return this.language_mastery;
	}
	
	/**
	 * 设置语言掌握程度数据列表
	 * @param listOneLeve 语言掌握程度数据列表
	 */
	public void setLanguage_Mastery(List<OneLevelData> listOneLeve){
		this.language_mastery = listOneLeve;
	}
	
	/**
	 * 获取语言读写能力数据列表
	 * @return 语言读写能力数据列表
	 */
	public List<OneLevelData> getLanguage_Literacy(){
		return this.language_literacy;
	}
	
	/**
	 * 设置语言读写能力数据列表
	 * @param listOneLeve 语言读写能力数据列表
	 */
	public void setLanguage_Literacy(List<OneLevelData> listOneLeve){
		this.language_literacy = listOneLeve;
	}
	
	/**
	 * 获取语言听说能力数据列表
	 * @return 语言听说能力数据列表
	 */
	public List<OneLevelData> getLanguage_Speaking(){
		return this.language_speaking;
	}
	
	/**
	 * 设置语言听说能力数据列表
	 * @param listOneLeve 语言听说能力数据列表
	 */
	public void setLanguage_Speaking(List<OneLevelData> listOneLeve){
		this.language_speaking = listOneLeve;
	}
	
	/**
	 * 获取全部城市数据列表
	 * @return 全部城市数据列表
	 */
	public List<OneLevelData> getAllCity(){
		return this.allcity;
	}
	
	/**
	 * 设置全部城市数据列表
	 * @param listOneLeve 全部城市数据列表
	 */
	public void setAllCity(List<OneLevelData> listOneLeve){
		this.allcity = listOneLeve;
	}
	
	/**
	 * 获取我的记录状态数据列表
	 * @return 推荐我的记录状态数据列表
	 */
	public List<OneLevelData> getM125(){
		return this.m125;
	}
	
	/**
	 * 设置我的记录状态数据列表
	 * @param listOneLeve 推荐我的记录状态数据列表
	 */
	public void setM125(List<OneLevelData> listOneLeve){
		this.m125 = listOneLeve;
	}
	
	/**
	 * 获取推荐理由数据列表
	 * @return 推荐理由数据列表
	 */
	public List<OneLevelData> getM126(){
		return this.m126;
	}
	
	/**
	 * 设置推荐理由数据列表
	 * @param listOneLeve 推荐理由数据列表
	 */
	public void setM126(List<OneLevelData> listOneLeve){
		this.m126 = listOneLeve;
	}
	
	/**
	 * 获取图片基础地址数据列表
	 * @return 图片基础地址数据列表
	 */
	public List<OneLevelData> getM128(){
		return this.m128;
	}
	
	/**
	 * 设置图片基础地址数据列表
	 * @param listOneLeve 图片基础地址数据列表
	 */
	public void setM128(List<OneLevelData> listOneLeve){
		this.m128 = listOneLeve;
	}
	
	/**
	 * 获取图片提交地址数据列表
	 * @return 图片提交地址数据列表
	 */
	public List<OneLevelData> getM129(){
		return this.m129;
	}
	
	/**
	 * 设置图片提交地址数据列表
	 * @param listOneLeve 图片提交地址数据列表
	 */
	public void setM129(List<OneLevelData> listOneLeve){
		this.m129 = listOneLeve;
	}
	
	/**
	 * 获取悬赏任务发布状态数据列表
	 * @return 悬赏任务发布状态数据列表
	 */
	public List<OneLevelData> getM134(){
		return this.m134;
	}
	
	/**
	 * 设置悬赏任务发布状态数据列表
	 * @param listOneLeve 悬赏任务发布状态数据列表
	 */
	public void setM134(List<OneLevelData> listOneLeve){
		this.m134 = listOneLeve;
	}
	
	/**
	 * 获取接受任务模式数据列表
	 * @return 接受任务模式数据列表
	 */
	public List<OneLevelData> getM137(){
		return this.m137;
	}
	
	/**
	 * 设置接受任务模式数据列表
	 * @param listOneLeve 接受任务模式数据列表
	 */
	public void setM137(List<OneLevelData> listOneLeve){
		this.m137 = listOneLeve;
	}
	
	/**
	 * 获取职能分类
	 * @return 职能分类
	 */
	public List<ThreeLevelJobFunctionsData> getJob_functions() {
		return job_functions;
	}

	/**
	 * 设置职能分类 
	 * @param job_functions 职能分类
	 */
	public void setJob_functions(List<ThreeLevelJobFunctionsData> job_functions) {
		this.job_functions = job_functions;
	}

	/**
	 * 获取付款标识
	 * @return 付款标识
	 */
	public List<OneLevelData> getM143() {
		return m143;
	}

	/**
	 * 设置付款标识
	 * @param m143 付款标识
	 */
	public void setM143(List<OneLevelData> m143) {
		this.m143 = m143;
	}

	
}

package com.qianniu.zhaopin.app.bean;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.database.DBUtils;

/**
 * 用户信息(我的账号)
 * @author wuzy
 *
 */
public class MyAccontInfoData implements Serializable{
	private String username;			// 用户名
	private String display_name;		// 姓名
	private String email;				// 用户email
	private String email_verified;		// email验证标识   1=>已认证, 2=>未认证
	private String phone;				// 用户手机
	private String phone_verified;		// phone验证标识   1=>已认证, 2=>未认证
	private String gender;				// "1=> 男, 2=> 女"
	private String credit;				// 积分
	private String m104_id;				// 居住地
	private String m112_id;				// 行业一级id
	private String avatar;				// 用户头像
	private String birthday;			// 出生日期
	private String joinworkdate;		// 参加工作日期
	private String m105_id;				// 学历ID
	private String memo;				// 自我描述
	private String favcount;			// 收藏的任务数
	private String alipay_acc;			// alipay的账号, 为空则未绑定
	private String bonussum;			// 余额
	private String bonussum_pay;		// 金钱-支出
	private String bonussum_get;		// 金钱-收入
	private String bonus_count;			// 金钱交易记录条数
	private String complete_flag;		// 个人资料完善标识. 1=>完善, 0=>不完善
	
	public MyAccontInfoData(){

	}

	/**
	 * 获取用户名
	 * @return 用户名
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 设置用户名
	 * @param str 用户名
	 */
	public void setUsername(String str) {
		this.username = str;
	}

	/**
	 * 获取姓名
	 * @return 姓名
	 */
	public String getDisplay_name() {
		return display_name;
	}

	/**
	 * 设置姓名
	 * @param display_name 姓名
	 */
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	/**
	 * 获取用户email
	 * @return 用户email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 设置用户email
	 * @param email 用户email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 获取email 验证标识
	 * @return email 验证标识
	 */
	public String getEmail_verified() {
		return email_verified;
	}

	/**
	 * 设置email 验证标识 
	 * @param email_verified email 验证标识
	 */
	public void setEmail_verified(String email_verified) {
		this.email_verified = email_verified;
	}

	/**
	 * 获取用户手机
	 * @return 用户手机
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 设置用户手机
	 * @param phone 用户手机
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 获取phone验证标识
	 * @return
	 */
	public String getPhone_verified() {
		return phone_verified;
	}

	/**
	 * 设置phone验证标识 
	 * @param phone_verified phone验证标识
	 */
	public void setPhone_verified(String phone_verified) {
		this.phone_verified = phone_verified;
	}

	/**
	 * 获取性别
	 * @return 性别
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * 设置性别 
	 * @param gender 性别
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	/**
	 * 获取积分
	 * @return 积分
	 */
	public String getCredit() {
		return credit;
	}

	/**
	 * 设置积分
	 * @param credit 积分
	 */
	public void setCredit(String credit) {
		this.credit = credit;
	}

	/**
	 * 获取居住地
	 * @return 居住地
	 */
	public String getM104_id() {
		return m104_id;
	}

	/**
	 * 设置居住地
	 * @param m104_id 居住地
	 */
	public void setM104_id(String m104_id) {
		this.m104_id = m104_id;
	}

	/**
	 * 获取行业一级id
	 * @return 行业一级id
	 */
	public String getM112_id() {
		return m112_id;
	}

	/**
	 * 设置行业一级id
	 * @param m112_id 行业一级id
	 */
	public void setM112_id(String m112_id) {
		this.m112_id = m112_id;
	}

	/**
	 * 获取个人头像Url
	 * @return 个人头像Url
	 */
	public String getAvatar() {
		return avatar;
	}

	/**
	 * 设置个人头像Url
	 * @param avatar 个人头像Url
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	/**
	 * 获取出生日期
	 * @return 出生日期
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * 设置出生日期
	 * @param birthday 出生日期
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * 获取参加工作日期
	 * @return 参加工作日期
	 */
	public String getJoinworkdate() {
		return joinworkdate;
	}

	/**
	 * 设置参加工作日期
	 * @param joinworkdate 参加工作日期
	 */
	public void setJoinworkdate(String joinworkdate) {
		this.joinworkdate = joinworkdate;
	}

	/**
	 * 获取学历ID
	 * @return 学历ID
	 */
	public String getM105_id() {
		return m105_id;
	}

	/**
	 * 设置学历ID
	 * @param m105_id 学历ID
	 */
	public void setM105_id(String m105_id) {
		this.m105_id = m105_id;
	}

	/**
	 * 获取自我描述
	 * @return 自我描述
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * 设置自我描述
	 * @param memo 自我描述
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	/**
	 * 获取收藏的任务数
	 * @return 收藏的任务数
	 */
	public String getFavcount() {
		return favcount;
	}

	/**
	 * 设置收藏的任务数
	 * @param favcount 收藏的任务数
	 */
	public void setFavcount(String favcount) {
		this.favcount = favcount;
	}

	/**
	 * 获取alipay的账号
	 * @return alipay的账号
	 */
	public String getAlipay_acc() {
		return alipay_acc;
	}

	/**
	 * 设置alipay的账号
	 * @param alipay_acc alipay的账号
	 */
	public void setAlipay_acc(String alipay_acc) {
		this.alipay_acc = alipay_acc;
	}

	/**
	 * 获取余额
	 * @return 余额
	 */
	public String getBonussum() {
		return bonussum;
	}

	/**
	 * 设置余额
	 * @param bonussum 余额
	 */
	public void setBonussum(String bonussum) {
		this.bonussum = bonussum;
	}

	/**
	 * 获取金钱(支出)
	 * @return 金钱(支出)
	 */
	public String getBonussum_pay() {
		return bonussum_pay;
	}

	/**
	 * 设置金钱(支出)
	 * @param bonussum_pay 金钱(支出)
	 */
	public void setBonussum_pay(String bonussum_pay) {
		this.bonussum_pay = bonussum_pay;
	}

	/**
	 * 获取金钱(收入)
	 * @return 金钱(收入)
	 */
	public String getBonussum_get() {
		return bonussum_get;
	}

	/**
	 * 设置金钱(收入)
	 * @param bonussum_get 金钱(收入)
	 */
	public void setBonussum_get(String bonussum_get) {
		this.bonussum_get = bonussum_get;
	}

	/**
	 * 获取金钱交易记录条数
	 * @return 金钱交易记录条数
	 */
	public String getBonus_count() {
		return bonus_count;
	}

	/**
	 * 设置金钱交易记录条数
	 * @param bonus_count 金钱交易记录条数
	 */
	public void setBonus_count(String bonus_count) {
		this.bonus_count = bonus_count;
	}

	
	/**
	 * 获取个人资料完善标识
	 * @return 个人资料完善标识 1=>完善, 0=>不完善
	 */
	public String getComplete_flag() {
		return complete_flag;
	}

	/**
	 * 设置个人资料完善标识
	 * @param complete_flag 个人资料完善标识 1=>完善, 0=>不完善
	 */
	public void setComplete_flag(String complete_flag) {
		this.complete_flag = complete_flag;
	}

	/**
	 * 从数据库中获取我的账号信息
	 * @param appContext
	 * @param strUserId
	 * @return
	 */
	public static MyAccontInfoData getMyAccontInfoData (AppContext appContext,
			String strUserId){
			
		DBUtils dbu = DBUtils.getInstance(appContext);
			
		if(null == strUserId || strUserId.isEmpty()){
			if (appContext.isLogin()) {
				strUserId = appContext.getUserId();
			} else {
				strUserId = "";
			}			
		}

		String sql = DBUtils.KEY_MYACCOUNT_USERID + " = \"" + strUserId + "\"";
			
		Cursor c = dbu.query(DBUtils.myaccountTableName, new String[] {"*"}, sql);
			
		if (null != c) {
			if(c.getCount() > 0){
				c.moveToFirst();
				
				MyAccontInfoData myacc = new MyAccontInfoData();
				// 用户名
				myacc.setUsername(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_USERNAME)));
				// 姓名
				myacc.setDisplay_name(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_DISPLAYNAME)));
				// 用户email
				myacc.setEmail(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_EMAIL)));
				// email验证标识
				myacc.setEmail_verified(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_EMAILVERIFIED)));
				// 用户手机
				myacc.setPhone(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_PHONE)));
				// 用户手机验证标识
				myacc.setPhone_verified(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_PHONEVERIFIED)));
				// 性别
				myacc.setGender(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_GENDER)));
				// 积分
				myacc.setCredit(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_CREDIT)));
				// 居住地
				myacc.setM104_id(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_DOMICILE)));
				// 行业
				myacc.setM112_id(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_INDUSTRY)));
				// 用户头像
				myacc.setAvatar(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_AVATAR)));
				// 出生日期
				myacc.setBirthday(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_BIRTHDAY)));
				// 参加工作日期
				myacc.setJoinworkdate(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_JOINWORKDATE)));
				// 学历ID
				myacc.setM105_id(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_EDUCATIONID)));
				// 自我描述
				myacc.setMemo(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_MEMO)));
				// 收藏的任务数
				myacc.setFavcount(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_FAVCOUNT)));
				// alipay的账号
				myacc.setAlipay_acc(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_ALIPAYACC)));
				// 余额
				myacc.setBonussum(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_BONUSSUM)));
				// 金钱-支出
				myacc.setBonussum_pay(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_BONUSSUMPAY)));
				// 金钱-收入
				myacc.setBonussum_get(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_BONUSSUMGET)));
				// 金钱交易记录条数
				myacc.setBonus_count(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_BONUSCOUNT)));
				// 个人资料完善标识
				myacc.setComplete_flag(c.getString(c
						.getColumnIndex(DBUtils.KEY_MYACCOUNT_COMPLETEFLAG)));
				
				c.close();
				return myacc;
			}
			
			c.close();
		}
		
		return null;
	}
	
	/**
	 * 把我的账号信息保存到数据库中
	 * @param appContext
	 * @param myacc
	 * @return
	 */
	public static boolean saveMyAccontInfoData (AppContext appContext,
			MyAccontInfoData myacc){
		if(null == myacc){
			return false;
		}
		
		boolean bRet = false;
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getUserId();
		}
		
		// 删除原来的数据
		deleteMyAccontInfoData(appContext);
		
		ContentValues values = new ContentValues();
		// 用户ID
		values.put(DBUtils.KEY_MYACCOUNT_USERID, strUserId);
		// 用户名
		values.put(DBUtils.KEY_MYACCOUNT_USERNAME, myacc.getUsername());
		// 姓名
		values.put(DBUtils.KEY_MYACCOUNT_DISPLAYNAME, myacc.getDisplay_name());
		// 用户email
		values.put(DBUtils.KEY_MYACCOUNT_EMAIL, myacc.getEmail());
		// email验证标识
		values.put(DBUtils.KEY_MYACCOUNT_EMAILVERIFIED, myacc.getEmail_verified());
		// 用户手机
		values.put(DBUtils.KEY_MYACCOUNT_PHONE, myacc.getPhone());
		// 用户手机验证标识
		values.put(DBUtils.KEY_MYACCOUNT_PHONEVERIFIED, myacc.getPhone_verified());
		// 性别
		values.put(DBUtils.KEY_MYACCOUNT_GENDER, myacc.getGender());
		// 积分
		values.put(DBUtils.KEY_MYACCOUNT_CREDIT, myacc.getCredit());
		// 居住地
		values.put(DBUtils.KEY_MYACCOUNT_DOMICILE, myacc.getM104_id());
		// 行业
		values.put(DBUtils.KEY_MYACCOUNT_INDUSTRY, myacc.getM112_id());
		// 用户头像
		values.put(DBUtils.KEY_MYACCOUNT_AVATAR, myacc.getAvatar());
		// 出生日期
		values.put(DBUtils.KEY_MYACCOUNT_BIRTHDAY, myacc.getBirthday());
		// 参加工作日期
		values.put(DBUtils.KEY_MYACCOUNT_JOINWORKDATE, myacc.getJoinworkdate());
		// 学历ID
		values.put(DBUtils.KEY_MYACCOUNT_EDUCATIONID, myacc.getM105_id());
		// 自我描述
		values.put(DBUtils.KEY_MYACCOUNT_MEMO, myacc.getMemo());
		// 收藏的任务数
		values.put(DBUtils.KEY_MYACCOUNT_FAVCOUNT, myacc.getFavcount());
		// alipay的账号
		values.put(DBUtils.KEY_MYACCOUNT_ALIPAYACC, myacc.getAlipay_acc());
		// 余额
		values.put(DBUtils.KEY_MYACCOUNT_BONUSSUM, myacc.getBonussum());
		// 金钱-支出
		values.put(DBUtils.KEY_MYACCOUNT_BONUSSUMPAY, myacc.getBonussum_pay());
		// 金钱-收入
		values.put(DBUtils.KEY_MYACCOUNT_BONUSSUMGET, myacc.getBonussum_get());
		// 金钱交易记录条数
		values.put(DBUtils.KEY_MYACCOUNT_BONUSCOUNT, myacc.getBonus_count());
		// 个人资料完善标识
		values.put(DBUtils.KEY_MYACCOUNT_COMPLETEFLAG, myacc.getComplete_flag());

		
		long lnRet = dbu.save(DBUtils.myaccountTableName, values);
		if(lnRet > 0){
			bRet = true;
		}else{
			bRet = false;
		}
		
		return bRet;
	}
	
	/**
	 * 删除当前账号的相关信息
	 * @param appContext
	 */
	public static void deleteMyAccontInfoData (AppContext appContext){
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getUserId();
		}
		
		String whereClause = DBUtils.KEY_MYACCOUNT_USERID + " = ? ";	
		
		String[] whereArgs = {strUserId};
		
		dbu.delete(DBUtils.myaccountTableName, whereClause, whereArgs);
		
	}
}

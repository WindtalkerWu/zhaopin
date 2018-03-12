package com.qianniu.zhaopin.app.bean;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.database.DBUtils;

/**
 * 交易记录id
 * @author wuzy
 *
 */
public class TransactionRecordData {
	private String title;		// 消费明细
	private String money;		// 消费金额
	private String modified;	// 时间
	private String id;			// 交易记录id
	
	public TransactionRecordData(){
		
	}

	/**
	 * 获取消费明细
	 * @return 消费明细
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置消费明细
	 * @param str 消费明细
	 */
	public void setTitle(String str) {
		this.title = str;
	}

	/**
	 * 获取消费金额
	 * @return 消费金额
	 */
	public String getMoney() {
		return money;
	}

	/**
	 * 设置消费金额
	 * @param money 消费金额
	 */
	public void setMoney(String money) {
		this.money = money;
	}

	/**
	 * 获取时间
	 * @return 时间
	 */
	public String getModified() {
		return modified;
	}

	/**
	 * 设置时间
	 * @param modified 时间
	 */
	public void setModified(String modified) {
		this.modified = modified;
	}

	/**
	 * 获取交易记录id
	 * @return 交易记录id
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置交易记录id
	 * @param id 交易记录id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 从数据库中读取交易记录
	 * @param appContext
	 * @param strUserId
	 * @return
	 */
	public static List<TransactionRecordData> getTransactionRecordData (AppContext appContext,
			  String strUserId){
			
		List<TransactionRecordData> lsTRD = new ArrayList<TransactionRecordData>();
			
		DBUtils dbu = DBUtils.getInstance(appContext);
			
		if(null == strUserId || strUserId.isEmpty()){
			if (appContext.isLogin()) {
				strUserId = appContext.getUserId();
			} else {
				strUserId = "";
			}			
		}
					
		String sql = DBUtils.KEY_TRANSATIONRECORD_USERID + " = \"" + strUserId + "\"";
			
		Cursor c = dbu.query(DBUtils.transactionrecordTableName, new String[] {"*"}, sql);
			
		if (null != c) {
			if(c.getCount() > 0){
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					TransactionRecordData trd = new TransactionRecordData();
					
					// 交易记录ID
					trd.setId(c.getString(c
							.getColumnIndex(DBUtils.KEY_TRANSATIONRECORD_RECORDID)));
					// 消费金额
					trd.setMoney(c.getString(c
							.getColumnIndex(DBUtils.KEY_TRANSATIONRECORD_MONEY)));
					// 消费明细
					trd.setTitle(c.getString(c
							.getColumnIndex(DBUtils.KEY_TRANSATIONRECORD_DTETAIL)));
					// 修改时间时间
					trd.setModified(c.getString(c
							.getColumnIndex(DBUtils.KEY_TRANSATIONRECORD_MODIFIEDTIME)));
					
					lsTRD.add(trd);
				}
			}
			
			c.close();
		}	
		
		return lsTRD;	
	}
	
	/**
	 * 保存交易记录到数据库中
	 * @param appContext
	 * @param trd
	 * @return
	 */
	public static boolean saveTransactionRecordData (AppContext appContext,
			TransactionRecordData trd){
		if(null == trd){
			return false;
		}
	
		boolean bRet = false;

		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getUserId();
		}
		
		ContentValues values = new ContentValues();
		// 用户ID
		values.put(DBUtils.KEY_TRANSATIONRECORD_USERID, strUserId);
		// 交易记录id
		values.put(DBUtils.KEY_TRANSATIONRECORD_RECORDID, trd.getId());
		// 消费金额
		values.put(DBUtils.KEY_TRANSATIONRECORD_MONEY, trd.getMoney());
		// 消费明细
		values.put(DBUtils.KEY_TRANSATIONRECORD_DTETAIL, trd.getTitle());
		// 修改时间时间
		values.put(DBUtils.KEY_TRANSATIONRECORD_MODIFIEDTIME, trd.getModified());
			
		long lnRet = dbu.save(DBUtils.transactionrecordTableName, values);
		if(lnRet > 0){
			bRet = true;
		}else{
			bRet = false;
		}
				
		return bRet;
	}
	
	/**
	 * 删除同一用户的所有交易记录
	 * @param appContext
	 */
	public static void deleteAllTransactionRecordData (AppContext appContext){
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String strUserId = "";
		if (appContext.isLogin()) {
			strUserId = appContext.getUserId();
		} 
		
		String whereClause = DBUtils.KEY_TRANSATIONRECORD_USERID + " = ? ";	
		
		String[] whereArgs = {strUserId};
		
		dbu.delete(DBUtils.transactionrecordTableName, whereClause, whereArgs);
	}
}

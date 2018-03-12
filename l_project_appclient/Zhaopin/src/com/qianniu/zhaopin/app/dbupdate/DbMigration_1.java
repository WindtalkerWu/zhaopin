package com.qianniu.zhaopin.app.dbupdate;

import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.database.DbMigration;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbMigration_1 implements DbMigration {
	private String tag = "DbMigration_1";

	@Override
	public void upgrade(SQLiteDatabase db) {
		// Log.i(tag, "upgrade");

		db.execSQL("ALTER TABLE  " + DBUtils.catalogTableName + " ADD "
				+ DBUtils.KEY_PUBLIC_USER_ID + " text");
		db.execSQL("ALTER TABLE  " + DBUtils.infoTableName + " ADD "
				+ DBUtils.KEY_PUBLIC_USER_ID + " text");
		db.execSQL("ALTER TABLE  " + DBUtils.resumeTableName + " ADD "
				+ DBUtils.KEY_PUBLIC_USER_ID + " text");
		db.execSQL("ALTER TABLE  " + DBUtils.simpleResumeTableName + " ADD "
				+ DBUtils.KEY_PUBLIC_USER_ID + " text");
		db.execSQL("ALTER TABLE  " + DBUtils.serviceDataTableName + " ADD "
				+ DBUtils.KEY_PUBLIC_USER_ID + " text");
//		db.execSQL("ALTER TABLE  " + DBUtils.notificationTableName + " ADD "
//				+ DBUtils.KEY_PUBLIC_USER_ID + " text");

		db.execSQL(DBUtils.campaignSQL);
		
		db.execSQL(DBUtils.INSIDERSANDCOMPANYSQL);//行业圈内人，名企招聘
		
		
		// 悬赏任务表添加用户选择要获取的悬赏任务类型
		db.execSQL("drop table if exists "+  DBUtils.rewardTableName);
		db.execSQL(DBUtils.rewardSQL);
//		db.execSQL("ALTER TABLE  " + DBUtils.rewardTableName + " ADD "
//				+ DBUtils.KEY_REWARD_USERREQUEST_TYPE + " varchar"
//				+ " ADD " + DBUtils.KEY_REWARD_COMPANYID + " varchar"
//				+ " ADD " + DBUtils.KEY_REWARD_COMPANIMGYURL + " varchar "
//				+ " ADD " + DBUtils.KEY_REWARD_COMPANYCOLLECTION + " varchar"
//				+ " ADD " + DBUtils.KEY_REWARD_REQUESTDATATYPE + " integer"
//				+ " ADD " + DBUtils.KEY_REWARD_VALIDDATE + " varchar"
//				+ " ADD " + DBUtils.KEY_REWARD_CONCERNNUM + " varchar"
//				);
		
		// 添加悬赏任务已读表
		db.execSQL(DBUtils.rewardreadSQL);
		
		// 添加我的账号数据表
		db.execSQL(DBUtils.myaccountSQL);
		
		// 添加交易记录表
		db.execSQL(DBUtils.transactionrecordSQL);
		
		// 全局数据表添加数据级数
//		db.execSQL("ALTER TABLE  " + DBUtils.globaldataTableName + " ADD "
//				+ DBUtils.KEY_GLOBALDATA_LEVEL + " integer"
//				);
		db.execSQL("drop table if exists "+  DBUtils.globaldataTableName);
		db.execSQL(DBUtils.globaldataSQL);
		
		//
		db.execSQL("drop table if exists "+  DBUtils.versionTableName);
		db.execSQL(DBUtils.versionSQL);
	}

	@Override
	public void downgrade(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// Log.i(tag, "downgrade");
	}

}

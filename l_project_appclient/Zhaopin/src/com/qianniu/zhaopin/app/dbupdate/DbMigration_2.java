package com.qianniu.zhaopin.app.dbupdate;

import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.database.DbMigration;

import android.database.sqlite.SQLiteDatabase;

public class DbMigration_2 implements DbMigration {
	private String tag = "DbMigration_2";

	@Override
	public void upgrade(SQLiteDatabase db) {
		// Log.i(tag, "upgrade");

//		db.execSQL("ALTER TABLE  " + DBUtils.catalogTableName + " ADD "
//				+ DBUtils.KEY_PUBLIC_USER_ID + " text");

		// 添加我的账号数据表
		db.execSQL("drop table if exists "+  DBUtils.myaccountTableName);
		db.execSQL(DBUtils.myaccountSQL);
		
		// 添加悬赏任务表(选项卡)数据表
		db.execSQL(DBUtils.rewardtabhostSQL);
		
		// 悬赏任务搜索历史添加字段"父行业"
		db.execSQL("drop table if exists "+  DBUtils.rewardsearchhistoryTableName);
		db.execSQL(DBUtils.rewardsearchhistorySQL);
	}

	@Override
	public void downgrade(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// Log.i(tag, "downgrade");
	}

}

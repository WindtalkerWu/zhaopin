package com.qianniu.zhaopin.app.dbupdate;

import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.database.DbMigration;

import android.database.sqlite.SQLiteDatabase;

public class DbMigration_3 implements DbMigration {
	private String tag = "DbMigration_3";

	@Override
	public void upgrade(SQLiteDatabase db) {
		
		db.execSQL("alter table " + DBUtils.INSIDERSANDCOMPANYTABLENAME + " add " + DBUtils.ICTITLE + " text default ''");
		db.execSQL("alter table " + DBUtils.INSIDERSANDCOMPANYTABLENAME + " add " + DBUtils.ICTASKCOUNT + " text default '0'");
	}

	@Override
	public void downgrade(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// Log.i(tag, "downgrade");
	}

}

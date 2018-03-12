package com.qianniu.zhaopin.app.database;

import android.database.sqlite.SQLiteDatabase;

/**数据库升级执行接口
 * @author Administrator
 *
 */
public interface  DbMigration {

	public  void upgrade(SQLiteDatabase db);
	public  void downgrade(SQLiteDatabase db);
}

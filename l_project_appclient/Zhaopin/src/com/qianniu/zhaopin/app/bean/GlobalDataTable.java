package com.qianniu.zhaopin.app.bean;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.database.DBUtils;

public class GlobalDataTable extends Entity {
	private int m_nType; 				// 数据类型
	private String m_strID; 			// 数据ID
	private String m_strName; 			// 数据名称
	private String m_strNamePinYin;		// 数据名称拼音
	private boolean m_bHavingParent; 	// 数据是否有父类
	private String m_strParentID; 		// 父类ID
	private boolean m_bHavingSubClass; 	// 数据是否有子类
	private int m_nLevel; 				// 数据属于第几级

	public GlobalDataTable() {
		this.m_bHavingParent = false;
		this.m_bHavingSubClass = false;
		this.m_nLevel = 1;
	}

	/**
	 * 获取数据类型
	 * 
	 * @return 数据类型
	 */
	public int getType() {
		return this.m_nType;
	}

	/**
	 * 设置数据类型
	 * 
	 * @param nType
	 *            数据类型
	 */
	public void setType(int nType) {
		this.m_nType = nType;
	}

	/**
	 * 获取数据ID
	 * 
	 * @return 数据ID
	 */
	public String getID() {
		return this.m_strID;
	}

	/**
	 * 设置数据ID
	 * 
	 * @param str
	 *            数据ID
	 */
	public void setID(String str) {
		this.m_strID = str;
	}

	/**
	 * 获取数据名称
	 * 
	 * @return 数据名称
	 */
	public String getName() {
		return this.m_strName;
	}

	/**
	 * 设置数据名称
	 * 
	 * @param str
	 *            数据名称
	 */
	public void setName(String str) {
		this.m_strName = str;
	}

	/**
	 * 获取数据名称拼音
	 * 
	 * @return 数据名称拼音
	 */
	public String getNamePinYin() {
		return this.m_strNamePinYin;
	}

	/**
	 * 设置数据名称拼音
	 * 
	 * @param str
	 *            数据名称拼音
	 */
	public void setNamePinYin(String str) {
		this.m_strNamePinYin = str;
	}

	/**
	 * 获取数据是否有父类
	 * 
	 * @return 数据是否有父类
	 */
	public boolean getHavingParent() {
		return this.m_bHavingParent;
	}

	/**
	 * 设置数据是否有父类
	 * 
	 * @param b
	 *            数据是否有父类
	 */
	public void setHavingParent(boolean b) {
		this.m_bHavingParent = b;
	}

	/**
	 * 获取父类ID
	 * 
	 * @return 父类ID
	 */
	public String getParentID() {
		return this.m_strParentID;
	}

	/**
	 * 设置父类ID
	 * 
	 * @param strID
	 *            父类ID
	 */
	public void setParentID(String strID) {
		this.m_strParentID = strID;
	}

	/**
	 * 获取数据是否有子类
	 * 
	 * @return 数据是否有子类
	 */
	public boolean getHavingSubClass() {
		return this.m_bHavingSubClass;
	}

	/**
	 * 设置数据是否有子类
	 * 
	 * @param str
	 *            数据是否有子类
	 */
	public void setHavingSubClass(boolean b) {
		this.m_bHavingSubClass = b;
	}
	
	
	/**
	 * 获取数据属于第几级
	 * @return 数据属于第几级
	 */
	public int getLevel() {
		return m_nLevel;
	}

	/**
	 * 设置数据属于第几级
	 * @param nLevel 数据属于第几级
	 */
	public void setLevel(int nLevel) {
		this.m_nLevel = nLevel;
	}

	/**
	 * @param appContext
	 * @param nType
	 */
	public static void deleteGlobalData(AppContext appContext, int nType){
		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String sql = DBUtils.KEY_GLOBALDATA_TYPE + " = " + String.valueOf(nType);

		dbu.delete(DBUtils.globaldataTableName, sql, null);
	}
	
	/**
	 * 保存全局数据
	 * 
	 * @param appContext
	 * @param lsGD
	 */
	public static void saveGlobalData(AppContext appContext,
			List<GlobalDataTable> lsGD) {

		DBUtils dbu = DBUtils.getInstance(appContext);

		// Begins a transaction
		dbu.beginTransaction();

		try {
			for (GlobalDataTable odt : lsGD) {
				ContentValues values = new ContentValues();
				values.put(DBUtils.KEY_GLOBALDATA_TYPE, odt.getType());
				values.put(DBUtils.KEY_GLOBALDATA_ID, odt.getID());
				values.put(DBUtils.KEY_GLOBALDATA_NAME, odt.getName());
				values.put(DBUtils.KEY_GLOBALDATA_NAMEPINYIN, odt.getNamePinYin());
				values.put(DBUtils.KEY_GLOBALDATA_HAVINGPARENT,
						odt.getHavingParent());
				
				String strParentID = odt.getParentID();
				if(null != strParentID){
					if(!strParentID.isEmpty()){
						values.put(DBUtils.KEY_GLOBALDATA_PARENTID, strParentID);	
					}
				}
				
				values.put(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS,
						odt.getHavingSubClass());
				// 数据所属级数
				values.put(DBUtils.KEY_GLOBALDATA_LEVEL, odt.getLevel());
				
				dbu.save(DBUtils.globaldataTableName, values);
			}
			
			// marks the current transaction as successful
			dbu.setTransactionSuccessful();
		} catch (Exception e) {
			// process it
			e.printStackTrace();
		} finally {
			// end a transaction
			dbu.endTransaction();
		}
	}
	
	/**
	 * 检查全局数据是否合法
	 * 
	 */
	public boolean CheckGlobalData(OneLevelData old){
		boolean bCheck = true;
		
		// 判断ID是否为空
		if(old.getId().isEmpty()){
			bCheck = false;
		}
		
		// 判断名称是否为空
		if(old.getLabel().isEmpty()){
			bCheck = false;
		}
		
		return bCheck;
	}

	/**获取指定类型数据
	 * @param appContext
	 * @param nType : 数据类型id
	 * @param strParentId 父类ID
	 * @return
	 */
	public static List<GlobalDataTable> getSubClassData(AppContext appContext,
			int nType, String strParentId) {
		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();

		DBUtils dbu = DBUtils.getInstance(appContext);

		String sql = DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType;
		if (strParentId != null)
			sql += " AND " + DBUtils.KEY_GLOBALDATA_PARENTID + " = "
					+ strParentId;
		else
			sql += " AND " + DBUtils.KEY_GLOBALDATA_PARENTID + " IS NULL ";

		Cursor c = dbu
				.queryWithOrder(DBUtils.globaldataTableName,
						new String[] { "*" }, sql, null,
						DBUtils.KEY_GLOBALDATA_ID + " ASC ");

		if (null != c) {
			if (c.getCount() > 0) {
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					GlobalDataTable odt = new GlobalDataTable();

					odt.setType(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_TYPE)));
					odt.setID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
					odt.setName(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
					odt.setNamePinYin(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
					// 判断是否有父数据，并且设置到list中
					String strHavingParent = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGPARENT));
					odt.setHavingParent(strHavingParent.equals("1"));
					odt.setParentID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID)));
					// 判断是否有子数据，并且设置到list中
					String strHavingSub = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS));
					odt.setHavingSubClass(strHavingSub.equals("1"));
					// 数据所属级数
					odt.setLevel(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_LEVEL)));

					lsGDT.add(odt);
				}
			}
			
			c.close();
		}

		return lsGDT;
	}

	/**
	 * 获取同一类型数据
	 * 
	 * @param appContext
	 * @param nType
	 *            类型
	 * @return
	 */
	public static List<GlobalDataTable> getTpyeData(AppContext appContext,
			int nType) {
		boolean bflag = false;
		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();

		DBUtils dbu = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType;

		Cursor c = dbu
				.queryWithOrder(DBUtils.globaldataTableName,
						new String[] { "*" }, sql, null,
						null);

		if (null != c) {
			if (c.getCount() > 0) {
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					GlobalDataTable odt = new GlobalDataTable();

					odt.setType(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_TYPE)));
					odt.setID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
					odt.setName(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
					odt.setNamePinYin(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
					// 判断是否有父数据，并且设置到list中
					String strHavingParent = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGPARENT));
					odt.setHavingParent(strHavingParent.equals("1"));
					odt.setParentID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID)));
					// 判断是否有子数据，并且设置到list中
					String strHavingSub = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS));
					odt.setHavingSubClass(strHavingSub.equals("1"));
					// 数据所属级数
					odt.setLevel(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_LEVEL)));

					lsGDT.add(odt);
				}
				bflag = true;
			}
			
			c.close();
		}

		if(bflag)
			return lsGDT;
		else 
			return null;
	}

	/**
	 * @param appContext
	 * @param nType
	 * @param rowid
	 * @return
	 */
	public static GlobalDataTable getTypeDataById(AppContext appContext,
			int nType, String rowid) {
		boolean bsuc = false;
		GlobalDataTable odt = new GlobalDataTable();
		DBUtils dbu = DBUtils.getInstance(appContext);

		String sql = DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType;
		if(rowid != null && rowid.length() > 0)
			sql += " AND " + DBUtils.KEY_GLOBALDATA_ID + " = " + rowid;
		
		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {"*"}, sql);

		if (null != c) {
			if (c.moveToFirst()&& !c.isAfterLast()) {				
				odt.setType(c.getInt(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_TYPE)));
				odt.setID(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
				odt.setName(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
				odt.setNamePinYin(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
				// 判断是否有父数据，并且设置到list中
				String strHavingParent = c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGPARENT));
				odt.setHavingParent(strHavingParent.equals("1"));
				odt.setParentID(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID)));
				// 判断是否有子数据，并且设置到list中
				String strHavingSub = c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS));
				odt.setHavingSubClass(strHavingSub.equals("1"));
				// 数据所属级数
				odt.setLevel(c.getInt(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_LEVEL)));
				bsuc = true;
			}

			c.close();
		}
		
		if (bsuc){
			return odt;
		}else{
			return null;
		}
	}

	/**
	 * 获取指定类型，指定id的最低层数据(无子类)
	 * @param appContext
	 * @param nType
	 * @param rowid
	 * @return
	 */
	public static GlobalDataTable getTypeBotoomDataById(AppContext appContext,
			int nType, String rowid) {
		boolean bsuc = false;
		GlobalDataTable odt = new GlobalDataTable();
		DBUtils dbu = DBUtils.getInstance(appContext);

		String sql = DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType + " AND " +
				DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS + " = 0";
		if(rowid != null && rowid.length() > 0)
			sql += " AND " + DBUtils.KEY_GLOBALDATA_ID + " = " + rowid;
		
		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {"*"}, sql);

		if (null != c) {
			if (c.moveToFirst()&& !c.isAfterLast()) {				
				odt.setType(c.getInt(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_TYPE)));
				odt.setID(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
				odt.setName(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
				odt.setNamePinYin(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
				// 判断是否有父数据，并且设置到list中
				String strHavingParent = c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGPARENT));
				odt.setHavingParent(strHavingParent.equals("1"));
				odt.setParentID(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID)));
				// 判断是否有子数据，并且设置到list中
				String strHavingSub = c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS));
				odt.setHavingSubClass(strHavingSub.equals("1"));
				// 数据所属级数
				odt.setLevel(c.getInt(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_LEVEL)));
				bsuc = true;
			}

			c.close();
		}
		
		if (bsuc){
			return odt;
		}else{
			return null;
		}
	}

	/**
	 * 获取同一类型数据(不进行排序)
	 * 
	 * @param appContext
	 * @param nType
	 *            类型
	 * @return
	 */
	public static List<GlobalDataTable> getTpyeDataNoSort(AppContext appContext,
			int nType) {
		boolean bflag = false;
		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();

		DBUtils dbu = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType;

		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {"*"}, sql);

		if (null != c) {
			if (c.getCount() > 0) {
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					GlobalDataTable odt = new GlobalDataTable();

					odt.setType(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_TYPE)));
					odt.setID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
					odt.setName(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
					odt.setNamePinYin(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
					// 判断是否有父数据，并且设置到list中
					String strHavingParent = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGPARENT));
					odt.setHavingParent(strHavingParent.equals("1"));
					odt.setParentID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID)));
					// 判断是否有子数据，并且设置到list中
					String strHavingSub = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS));
					odt.setHavingSubClass(strHavingSub.equals("1"));
					// 数据所属级数
					odt.setLevel(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_LEVEL)));

					lsGDT.add(odt);
				}
				bflag = true;
			}
			
			c.close();
		}

		if(bflag){
			return lsGDT;
		}else{
			return null;
		}
	}
	
	/**
	 * @param appContext
	 * @param nType
	 * @return
	 */
	public static List<GlobalDataTable> queryGlobalData(AppContext appContext,
			int nType) {
		boolean bflag = false;
		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();

		DBUtils dbu = DBUtils.getInstance(appContext);
		String sql = "select * from " + DBUtils.globaldataTableName 
				+ " where " + DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType ;
//		+ " order by " + DBUtils.KEY_GLOBALDATA_NAMEPINYIN
		Cursor c = dbu.rawQuery(sql, null);

		if (null != c) {
			if (c.getCount() > 0) {
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					GlobalDataTable odt = new GlobalDataTable();

					odt.setType(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_TYPE)));
					odt.setID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
					odt.setName(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
					odt.setNamePinYin(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
					// 判断是否有父数据，并且设置到list中
					String strHavingParent = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGPARENT));
					odt.setHavingParent(strHavingParent.equals("1"));
					odt.setParentID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID)));
					// 判断是否有子数据，并且设置到list中
					String strHavingSub = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS));
					odt.setHavingSubClass(strHavingSub.equals("1"));
					// 数据所属级数
					odt.setLevel(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_LEVEL)));

					lsGDT.add(odt);
				}
				bflag = true;
			}
			
			c.close();
		}

		if(bflag){
			return lsGDT;
		}else{
			return null;
		}
	}
	
	/**
	 * 获取指定类型数据(不排序)
	 * @param appContext
	 * @param nType : 数据类型id
	 * @param strParentId 父类ID
	 * @return
	 */
	public static List<GlobalDataTable> getSubClassDataNoSort(AppContext appContext,
			int nType, String strParentId) {
		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();

		DBUtils dbu = DBUtils.getInstance(appContext);

		String sql = DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType;
		if (strParentId != null)
			sql += " AND " + DBUtils.KEY_GLOBALDATA_PARENTID + " = "
					+ strParentId;
		else
			sql += " AND " + DBUtils.KEY_GLOBALDATA_PARENTID + " IS NULL ";

		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {"*"}, sql);
		
		if (null != c) {
			if (c.getCount() > 0) {
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					GlobalDataTable odt = new GlobalDataTable();

					odt.setType(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_TYPE)));
					odt.setID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
					odt.setName(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
					odt.setNamePinYin(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
					// 判断是否有父数据，并且设置到list中
					String strHavingParent = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGPARENT));
					odt.setHavingParent(strHavingParent.equals("1"));
					odt.setParentID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID)));
					// 判断是否有子数据，并且设置到list中
					String strHavingSub = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS));
					odt.setHavingSubClass(strHavingSub.equals("1"));
					// 数据所属级数
					odt.setLevel(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_LEVEL)));

					lsGDT.add(odt);
				}
			}
			
			c.close();
		}

		return lsGDT;
	}
	
	/**
	 * 获取指定类型数据(不排序)
	 * @param appContext
	 * @param nType				数据类型id
	 * @param strParentId		父类ID
	 * @param bHavingSubClass	是否有子类
	 * @return
	 */
	public static List<GlobalDataTable> getSubClassDataNoSort(AppContext appContext,
			int nType, String strParentId, boolean bHavingSubClass) {
		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();

		DBUtils dbu = DBUtils.getInstance(appContext);

		String sql = DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType;
		if (strParentId != null)
			sql += " AND " + DBUtils.KEY_GLOBALDATA_PARENTID + " = "
					+ strParentId;
		else
			sql += " AND " + DBUtils.KEY_GLOBALDATA_PARENTID + " IS NULL ";
		
		if(bHavingSubClass){
			sql += " AND " + DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS + " = 1";
		}else{
			sql += " AND " + DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS + " = 0";
		}

		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {"*"}, sql);
		
		if (null != c) {
			if (c.getCount() > 0) {
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					GlobalDataTable odt = new GlobalDataTable();

					odt.setType(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_TYPE)));
					odt.setID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
					odt.setName(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
					odt.setNamePinYin(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
					// 判断是否有父数据，并且设置到list中
					String strHavingParent = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGPARENT));
					odt.setHavingParent(strHavingParent.equals("1"));
					odt.setParentID(c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID)));
					// 判断是否有子数据，并且设置到list中
					String strHavingSub = c.getString(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS));
					odt.setHavingSubClass(strHavingSub.equals("1"));
					// 数据所属级数
					odt.setLevel(c.getInt(c
							.getColumnIndex(DBUtils.KEY_GLOBALDATA_LEVEL)));

					lsGDT.add(odt);
				}
			}
			
			c.close();
		}

		return lsGDT;
	}
	
	/**
	 * 从全局数据中获取审核状态的名称
	 * @param strID
	 * @return
	 */
	public static String getVerifyStatusName(AppContext appContext, String strId){
		String strTemp = strId;
		
		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();

		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String sql = DBUtils.KEY_GLOBALDATA_ID + " = " + strId + " AND " + 
				DBUtils.KEY_GLOBALDATA_TYPE + " = " + DBUtils.GLOBALDATA_TYPE_VERIFYSTATUS;
		
		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {"*"}, sql);
		
		if (null != c) {
			if(c.getCount() > 0){
				c.moveToFirst();
				strTemp = c.getString(c
								.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME));	
			}
			
			c.close();
		}
		
		return strTemp;
	}
	
	/**
	 * 从全局数据中获取指定id和类型的的名称
	 * @param appContext
	 * @param strId
	 * @param nType
	 * @return
	 */
//	public static String getGlobalDataName(AppContext appContext, String strId, int nType){
//		String strTemp = strId;
//		
//		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();
//
//		DBUtils dbu = DBUtils.getInstance(appContext);
//		
//		String sql = DBUtils.KEY_GLOBALDATA_ID + " = " + strId + " AND " + 
//				DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType;
//		
//		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {
//				DBUtils.KEY_GLOBALDATA_TYPE, 
//				DBUtils.KEY_GLOBALDATA_ID,
//				DBUtils.KEY_GLOBALDATA_NAME,
//				DBUtils.KEY_GLOBALDATA_NAMEPINYIN,
//				DBUtils.KEY_GLOBALDATA_HAVINGPARENT,
//				DBUtils.KEY_GLOBALDATA_PARENTID,
//				DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS }, sql);
//		
//		if (null != c) {
//			if(c.getCount() > 0){
//				c.moveToFirst();
//				strTemp = c.getString(c
//								.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME));	
//			}
//		}
//		
//		c.close();
//		
//		return strTemp;
//	}
	
	/**
	 * 从全局数据中获取指定id的数据的指定信息
	 * @param appContext
	 * @param strId
	 * @param nType
	 * @param columnName
	 * @return
	 */
	public static String getGlobalDataSpecifyInfo(AppContext appContext, String strId, int nType,
			String strColumnName){
		String strTemp = strId;
		
		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();

		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String sql = DBUtils.KEY_GLOBALDATA_ID + " = " + strId + " AND " + 
				DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType;
		
		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {"*"}, sql);
		
		if (null != c) {
			if(c.getCount() > 0){
				c.moveToFirst();
				strTemp = c.getString(c
								.getColumnIndex(strColumnName));	
			}
			
			c.close();
		}
		
		return strTemp;	
	}
	
	/**
	 * 从全局数据中获取指定id和指定类型的父类信息
	 * @param appContext
	 * @param strId
	 * @param nType
	 * @param strColumnName
	 * @return
	 */
	public static String getParentId(AppContext appContext, String strId, int nType){
		String strTemp = strId;
		
		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();

		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String sql = DBUtils.KEY_GLOBALDATA_ID + " = " + strId + " AND " + 
				DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType + " AND " + 
				DBUtils.KEY_GLOBALDATA_HAVINGPARENT + " = 1";
		
		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {"*"}, sql);
		
		if (null != c) {
			if(c.getCount() > 0){
				c.moveToFirst();
				strTemp = c.getString(c
								.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID));	
			}
			
			c.close();
		}
		
		return strTemp;	
	}
	
	/**
	 * 从全局数据中获取指定id和指定类型的父类信息(可指定"指定id"是否有子类)
	 * @param appContext
	 * @param strSubId
	 * @param nType
	 * @param bHavingSubClass
	 * @return
	 */
	public static String getParentId(AppContext appContext,
			String strId, int nType, boolean bHavingSubClass){
		String strTemp = "";
		
		if(null == strId || strId.isEmpty()){
			return strTemp;
		}
		
		List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();

		DBUtils dbu = DBUtils.getInstance(appContext);
		
		String sql = DBUtils.KEY_GLOBALDATA_ID + " = " + strId + " AND " + 
				DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType + " AND " + 
				DBUtils.KEY_GLOBALDATA_HAVINGPARENT + " = 1";
		
		if(bHavingSubClass){
			sql += " AND " + DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS + " = 1";
		}else{
			sql += " AND " + DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS + " = 0";
		}
		
		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {"*"}, sql);
		
		if (null != c) {
			if(c.getCount() > 0){
				c.moveToFirst();
				strTemp = c.getString(c
								.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID));	
			}
			
			c.close();
		}
		
		return strTemp;	
	}
	
	/**
	 * 获取指定类型，指定级数，指定id的数据
	 * @param appContext
	 * @param nType	指定类型
	 * @param nLevel 指定级数
	 * @param strId 指定id
	 * @return
	 */
	public static GlobalDataTable getGlobalDataTable(AppContext appContext,
			int nType, int nLevel, String strId) {
		if(null == strId || strId.isEmpty()){
			return null;
		}
		
		boolean bflag = false;
		GlobalDataTable odt = new GlobalDataTable();

		DBUtils dbu = DBUtils.getInstance(appContext);
		String sql = DBUtils.KEY_GLOBALDATA_ID + " = " + strId + " AND " + 
				DBUtils.KEY_GLOBALDATA_TYPE + " = " + nType + " AND " + 
				DBUtils.KEY_GLOBALDATA_LEVEL + " = " + nLevel;

		Cursor c = dbu.query(DBUtils.globaldataTableName, new String[] {"*"}, sql);

		if (null != c) {
			if (c.getCount() > 0 && c.moveToFirst() && !c.isAfterLast()){
					
				odt.setType(c.getInt(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_TYPE)));
				odt.setID(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_ID)));
				odt.setName(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAME)));
				odt.setNamePinYin(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_NAMEPINYIN)));
				// 判断是否有父数据，并且设置到list中
				String strHavingParent = c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGPARENT));
				odt.setHavingParent(strHavingParent.equals("1"));
				odt.setParentID(c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_PARENTID)));
				// 判断是否有子数据，并且设置到list中
				String strHavingSub = c.getString(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_HAVINGSUBCLASS));
				odt.setHavingSubClass(strHavingSub.equals("1"));
				// 数据所属级数
				odt.setLevel(c.getInt(c
						.getColumnIndex(DBUtils.KEY_GLOBALDATA_LEVEL)));
					
				bflag = true;
			}
			
			c.close();
		}

		if(bflag){
			return odt;
		}else{
			return null;
		}
	}
}

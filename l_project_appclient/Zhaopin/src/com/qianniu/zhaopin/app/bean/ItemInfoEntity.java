package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.database.DBUtils;

public class ItemInfoEntity extends Entity{

	private Result validate;
	private int pagesize = 0;
	private int view_type;
	private String fid;
	private long recevietime;
	private List<InfoEntity> infolist = new ArrayList<InfoEntity>();

	public List<InfoEntity> getInfoEntitylist() {
		return infolist;
	}
	public final Result getValidate() {
		return validate;
	}
	

	public final long getRecevietime() {
		return recevietime;
	}
	public final void setRecevietime(long recevietime) {
		this.recevietime = recevietime;
	}
	public final String getFid() {
		return fid;
	}
	public final void setFid(String fid) {
		this.fid = fid;
	}
	public final int getViewtype() {
		return view_type;
	}
	
	public final void setView_type(int view_type) {
		this.view_type = view_type;
	}
	public static ItemInfoEntity parse(AppContext appContext, JSONObject jsonObj,boolean bsavetoDb,int rowtype)
			throws IOException, AppException {

		ItemInfoEntity iteminfo = new ItemInfoEntity();
		Result res = null;

		try {			
			iteminfo.validate = res;
			iteminfo.view_type = jsonObj.getInt("view_type");
/*			try{
				iteminfo.fid = jsonObj.getString("fid");
			}catch (JSONException e) {
				
			}
			*/
			iteminfo.fid = jsonObj.getString("fid");
			JSONArray jsonObjs = jsonObj.getJSONArray("data");
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject childjsonObj = (JSONObject) jsonObjs.opt(i);
				InfoEntity info = InfoEntity.parse(childjsonObj);
				if (info != null && info.getValidate().OK()) {
					iteminfo.getInfoEntitylist().add(info);
				}
			}
			res = new Result(1, "success");
			if (appContext != null && bsavetoDb) {
				String jsonString = jsonObj.toString();
				DBUtils db = DBUtils.getInstance(appContext);

				String sql = DBUtils.KEY_CAT_ID + " = " + iteminfo.getInfoEntitylist().get(0).getForumId();
				sql += " AND " +DBUtils.KEY_MSG_ID + " = "  +iteminfo.getInfoEntitylist().get(0).getInfoId();
				sql += " AND " +DBUtils.KEY_ROWDATA_TYPE + " = " + String.valueOf(rowtype);
				Cursor cursor = db.queryBindUser(true, DBUtils.infoTableName, new String[] {
						DBUtils.KEY_CAT_ID, DBUtils.KEY_MSG_ID }, sql, null,
						null, null, null, null);
				if(cursor != null){
					if(cursor.getCount() > 0){
						db.deleteBindUser(DBUtils.infoTableName, sql, null);
					}
					
					cursor.close();
				}
				
				ContentValues values = new ContentValues();
				values.put(DBUtils.KEY_CAT_ID, iteminfo.getInfoEntitylist().get(0).getForumId());
				values.put(DBUtils.KEY_MSG_ID, iteminfo.getInfoEntitylist().get(0).getInfoId());
				values.put(DBUtils.KEY_JSON_CONTENT, jsonString);
				//如果类型大于0，则保存，否则为null
				if(rowtype >= 0)
					values.put(DBUtils.KEY_ROWDATA_TYPE, String.valueOf(rowtype));
				values.put(DBUtils.KEY_TIMESTAMP,
						String.valueOf(System.currentTimeMillis()));
				db.saveBindUser(DBUtils.infoTableName, values);
				db.close();
			}

		} catch (JSONException e) {
			res = new Result(-1, "json error");
			throw AppException.json(e);

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.io(e);
		} finally {
			
			iteminfo.validate = res;
		
		}
		return iteminfo;	

		
	}

	public static ItemInfoEntity parse(AppContext appContext,InputStream inputStream,boolean bsavetoDb,int rowtype)
			throws IOException, AppException {

		ItemInfoEntity iteminfo = null;
		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}

		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			iteminfo = parse(appContext,jsonObj,bsavetoDb,rowtype);
		
		} catch (JSONException e) {

			throw AppException.json(e);

		} catch (Exception e) {

			throw AppException.io(e);
		} finally {
					
		}
		return iteminfo;
	}
	
}

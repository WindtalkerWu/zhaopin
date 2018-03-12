package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.database.DBUtils;

public class CampaignEntityList extends Entity {
	public final static int MAXNUMBER_CAMPAIGNDATA_INDB = 100;
	private Result validate;
	private List<CampaignEntity> list = new ArrayList<CampaignEntity>();

	public CampaignEntityList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public final List<CampaignEntity> getList() {
		return list;
	}

	public final void setList(List<CampaignEntity> list) {
		this.list = list;
	}

	public static CampaignEntityList parse(AppContext appContext,
			InputStream inputStream, boolean bsavetoDb) throws IOException,
			AppException {

		CampaignEntityList list = new CampaignEntityList();
		Result res = null;
		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}

		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");
			res = new Result(status, statusmsg);
			list.validate = res;
			if (res.OK()) {
				JSONArray jsonObjs = jsonObj.getJSONArray("data");

				for (int i = 0; i < jsonObjs.length(); i++) {
					JSONObject childjsonObj = (JSONObject) jsonObjs.opt(i);
					CampaignEntity entity = CampaignEntity.parse(appContext,
							childjsonObj, bsavetoDb);
					if (entity != null && entity.getValidate().OK()) {
						list.getList().add(entity);
					}
				}
			}

		} catch (JSONException e) {
			res = new Result(-1, "json error");
			throw AppException.json(e);

		} catch (Exception e) {
			res = new Result(-1, "Exception error");
			throw AppException.io(e);
		} finally {
			inputStream.close();
			list.validate = res;

		}
		return list;
	}

	public static CampaignEntityList getCampaignDataFromNet(
			AppContext appContext, String url, long offsetid,
			String offsetfield, int pageSize, final boolean isRefresh)
			throws AppException {

		String Url = URLs.formatURL(url);

		JSONObject request = new JSONObject();

		try {
			request.putOpt("count", pageSize);
			int flag = 1;
			if (!isRefresh)
				flag = 0;

			request.putOpt("direction", flag);
			request.putOpt("offsetid", offsetid);
			request.putOpt("offsetfield", offsetfield);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}
		String requestinfo = request.toString();

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				requestinfo);
		/*
		 * boolean bsave = false; if (isRefresh) bsave = true;
		 */
		// 数据全部保存，
		boolean bsave = true;
		try {
			return CampaignEntityList.parse(appContext,
					ApiClient._post(appContext, Url, params, null), bsave);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}

	}

	public static int DIRECT_FORWARD = 0;
	public static int DIRECT_BACKWARD = 1;

	/**
	 * 从数据库读取数据列表,所有数据按msgid降序排列
	 * 
	 * @param appContext
	 * @param offsetid
	 *            : <= 0，搜索全部
	 * @param backward
	 *            : true，小于offsetid； false :大于 offsetid
	 * @param pagesize
	 *            : >0 ,做limit
	 * @param startoffset
	 *            : 开始搜索的行
	 * @return
	 * @throws AppException
	 */
	public static CampaignEntityList getCampaignDataFromDb(
			AppContext appContext, long offsetid, boolean backward,
			int pagesize, int startoffset) {

		DBUtils db = DBUtils.getInstance(appContext);

		String sql = null;
		if (offsetid > 0) {
			if (backward)
				sql = (DBUtils.KEY_CAMPAIGN_ID + " < " + offsetid);
			else
				sql = (DBUtils.KEY_CAMPAIGN_ID + " > " + offsetid);
		}
		String limitstr = null;
		if (startoffset < 0)
			startoffset = 0;
		if (pagesize > 0) {
			limitstr = startoffset + "," + pagesize;
		}
		Cursor cursor = db.queryBindUser(true, DBUtils.campaignTableName,
				new String[] { "*" }, sql, null, null, null,
				DBUtils.KEY_CAMPAIGN_ID + " DESC", limitstr);
		CampaignEntityList enitylist = parseCampaignEntityFromCursor(cursor);
		cursor.close();
		db.close();

		if (enitylist.getList().size() == 0)
			enitylist = null;
		else {
			Result r = new Result(1, "ok");
			enitylist.setValidate(r);
		}

		return enitylist;

	}

	/**
	 * 删除数据库中数据；
	 * 
	 * @param appContext
	 * @param offsetid
	 *            :null，删除全部
	 * @param backward
	 *            :true，小于offsetid； false :大于 offsetid
	 * @return :删除数量
	 * @throws AppException
	 */
	public static int deleteCampaignDataFromDb(AppContext appContext,
			String offsetid, boolean backward) {
		if (appContext == null)
			return 0;
		DBUtils db = DBUtils.getInstance(appContext);
		int id = 0;
		String whereClause = null;

		if (offsetid != null) {
			if (backward)
				whereClause = (DBUtils.KEY_CAMPAIGN_ID + " < " + offsetid);
			else
				whereClause = (DBUtils.KEY_CAMPAIGN_ID + " > " + offsetid);
		}

		id = db.deleteBindUser(DBUtils.campaignTableName, whereClause, null);

		db.close();

		return id;
	}

	/**
	 * 获取数据库中数据的条数
	 * 
	 * @param appContext
	 * @return 数量, -1: error ;
	 */
	public static int getCampaignEntityCountInDb(AppContext appContext) {
		if (appContext == null)
			return 0;
		DBUtils db = DBUtils.getInstance(appContext);
		int id = -1;
		String sql = new String();
		sql += ("select count(*) as nums from " + DBUtils.campaignTableName);
		Cursor cursor = db.rawQueryBindUser(sql, null);
		if (cursor != null) {
			cursor.moveToFirst();
			  //id = cursor.getInt(0);
			if (cursor.getCount() > 0) {
				int column = cursor.getColumnIndex("nums");
				id = cursor.getInt(column) ;
			}
			cursor.close();
		}
		return id;
	}

	/**
	 * 解析数据
	 * 
	 * @param cursor
	 * @return
	 */
	public static CampaignEntityList parseCampaignEntityFromCursor(Cursor cursor) {
		if (cursor == null)
			return null;
		CampaignEntityList enitylist = new CampaignEntityList();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			CampaignEntity campaignEntity = new CampaignEntity();
			int idColumn = cursor.getColumnIndex(DBUtils.KEY_CAMPAIGN_ID);
			campaignEntity.setCpagId(cursor.getLong(idColumn));

			int typeColumn = cursor
					.getColumnIndex(DBUtils.KEY_CAMPAIGN_TYPE_ID);
			campaignEntity.setCpagType(cursor.getInt(typeColumn));

			int titleColumn = cursor.getColumnIndex(DBUtils.KEY_CAMPAIGN_TITLE);
			campaignEntity.setTitle(cursor.getString(titleColumn));
			;

			int descriptColumn = cursor
					.getColumnIndex(DBUtils.KEY_CAMPAIGN_DESCRIPTION);
			campaignEntity.setDescription(cursor.getString(descriptColumn));

			int picurlColumn = cursor
					.getColumnIndex(DBUtils.KEY_CAMPAIGN_PICURL);
			campaignEntity.setPicUrl(cursor.getString(picurlColumn));
			;

			int detailurlColumn = cursor
					.getColumnIndex(DBUtils.KEY_CAMPAIGN_DEATAILURL);
			campaignEntity.setDetailUrl(cursor.getString(detailurlColumn));

			int startdateColumn = cursor
					.getColumnIndex(DBUtils.KEY_CAMPAIGN_START_DATE);
			campaignEntity.setStartDate(cursor.getString(startdateColumn));

			int enddateColumn = cursor
					.getColumnIndex(DBUtils.KEY_CAMPAIGN_END_DATE);
			campaignEntity.setEndtDate(cursor.getString(enddateColumn));

			int modifyColumn = cursor
					.getColumnIndex(DBUtils.KEY_CAMPAIGN_MODIFY_DATE);
			campaignEntity.setModifieDate(cursor.getString(modifyColumn));

			int receivetimeColumn = cursor
					.getColumnIndex(DBUtils.KEY_CAMPAIGN_RECEIVE_TIMESTAMP);
			campaignEntity.setReceiveTimestamp(cursor
					.getLong(receivetimeColumn));

			enitylist.getList().add(campaignEntity);
		}
		Result res = new Result(1, "read in db success");
		enitylist.setValidate(res);
		return enitylist;
	}

	public static void deleteOldDataInDb(final AppContext appcontext) {
		if (appcontext == null)
			return;
		Runnable r = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int count = getCampaignEntityCountInDb(appcontext);
				//Log.i("CampaignEntityList", "count = " + count);
				if (count > MAXNUMBER_CAMPAIGNDATA_INDB) {
					long offsetid = 0;
					// 从数据库获取最新（ID最大的数据）的id作为读取的offsetid
					CampaignEntityList listmax = CampaignEntityList
							.getCampaignDataFromDb(appcontext, 0, true, 1,
									MAXNUMBER_CAMPAIGNDATA_INDB);
					if (listmax != null) {
						if (listmax.getList() != null
								&& listmax.getList().size() > 0) {
							CampaignEntity entity = listmax.getList().get(0);
							offsetid = entity.getCpagId();

						}
					}
					//Log.i("CampaignEntityList", "offsetid = " + offsetid);
					if (offsetid != 0) {
						int i = deleteCampaignDataFromDb(appcontext,
								String.valueOf(offsetid), true);
						//Log.i("CampaignEntityList", "i = " + i);
					}
				}
			}
		};

		ThreadPoolController threadpool = ThreadPoolController.getInstance();
		threadpool.execute(r);

	}
}

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

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;

public class AdZoneList extends Entity {
	private Result validate;
	private List<AdZoneEntity> list = new ArrayList<AdZoneEntity>();

	public AdZoneList() {
		super();
		// TODO Auto-generated constructor stub
	}
	public final Result getValidate() {
		return validate;
	}


	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public final List<AdZoneEntity> getArraylist() {
		return list;
	}

	public static AdZoneList parse(AppContext appContext,
			InputStream inputStream, int zoneid, boolean bsavetoDb)
			throws IOException, AppException {

		AdZoneList list = new AdZoneList();
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
			JSONArray jsonObjs = jsonObj.getJSONArray("data");
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject childjsonObj = (JSONObject) jsonObjs.opt(i);
				AdZoneEntity entity = AdZoneEntity.parse(appContext,childjsonObj,zoneid,bsavetoDb);
				if (entity != null && entity.getValidate().OK()) {
					list.getArraylist().add(entity);
				}
			}
			res = new Result(status, statusmsg);

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
}

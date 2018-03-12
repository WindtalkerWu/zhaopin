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

public class ItemInfoList extends Entity {

	private Result validate;

	private int pagesize = 0;

	private List<ItemInfoEntity> iteminfolist = new ArrayList<ItemInfoEntity>();

	public List<ItemInfoEntity> getItemInfolist() {
		return iteminfolist;
	}

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public static ItemInfoList parse(AppContext appContext,
			InputStream inputStream, boolean bsavetoDb, int rowtype)
			throws IOException, AppException {

		ItemInfoList infoList = new ItemInfoList();
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
			infoList.validate = res;
			if (res.OK()) {
				JSONArray jsonObjs = jsonObj.getJSONArray("data");
				for (int i = 0; i < jsonObjs.length(); i++) {
					JSONObject childjsonObj = (JSONObject) jsonObjs.opt(i);
					ItemInfoEntity info = ItemInfoEntity.parse(appContext,
							childjsonObj, bsavetoDb, rowtype);
					if (info != null && info.getValidate().OK()) {
						infoList.getItemInfolist().add(info);
					}
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
			infoList.validate = res;

		}
		return infoList;
	}

}

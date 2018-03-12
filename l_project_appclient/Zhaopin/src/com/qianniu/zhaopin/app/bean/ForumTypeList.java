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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.MyLogger;

import android.util.Log;
import android.util.Xml;

/**
 * 版块列表实体类
 * 
 */
public class ForumTypeList extends Entity {
	private final static String TAG = "ForumTypeList";

	public final static int CATALOG_ASK = 1;

	private Result validate;
	private int pagesize = 0;
	private List<ForumType> infoTypelist = new ArrayList<ForumType>();

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public List<ForumType> getInfoTypelist() {
		return infoTypelist;
	}

	public static ForumTypeList parse(AppContext appContext,
			InputStream inputStream, int cat_type) throws IOException,
			AppException {

		ForumTypeList infoTypeList = new ForumTypeList();
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
			infoTypeList.validate = res;
			MyLogger.e(TAG, "Fragment_msg##ForumTypeList parse = " + builder.toString());
			if (res.OK()) {

				JSONArray jsonObjs = jsonObj.getJSONArray("data");
				for (int i = 0; i < jsonObjs.length(); i++) {
					JSONObject childjsonObj = (JSONObject) jsonObjs.opt(i);
					ForumType infotype = ForumType.parse(appContext,
							childjsonObj, true, cat_type);
					if (infotype != null && infotype.getValidate().OK()) {
						infoTypeList.getInfoTypelist().add(infotype);
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
			infoTypeList.validate = res;

		}
		return infoTypeList;
	}
}

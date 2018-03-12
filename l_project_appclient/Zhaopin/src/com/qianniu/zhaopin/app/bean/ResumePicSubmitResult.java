package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;

/*{
 "resume_id": "简历ID",
 "pic_url": "个人头像的在线地址"
 }*/
public class ResumePicSubmitResult {
	private Result validate;
	private String resume_Id;
	private String pic_url;

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public final String getResumeId() {
		return resume_Id;
	}

	public final void setResumeId(String resume_Id) {
		this.resume_Id = resume_Id;
	}

	public final String getPicurl() {
		return pic_url;
	}

	public final void setPicurl(String pic_url) {
		this.pic_url = pic_url;
	}

	public static ResumePicSubmitResult parse(AppContext appContext,
			InputStream inputStream) throws IOException, AppException {
		ResumePicSubmitResult entity = new ResumePicSubmitResult();
		Result res = null;

		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				inputStream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");
			
			res = new Result(status, statusmsg);

			entity.validate = res;
			if (res.OK()) {
/*				JSONObject rootobj = jsonObj.getJSONObject("data");
				entity.resume_Id = rootobj
						.getString(ResumeEntity.NODE_RESUME_ID);
				entity.pic_url = rootobj.getString("pic_url");*/
				entity.pic_url = jsonObj.getString("data");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			throw AppException.json(e);
		}

		return entity;

	}
}

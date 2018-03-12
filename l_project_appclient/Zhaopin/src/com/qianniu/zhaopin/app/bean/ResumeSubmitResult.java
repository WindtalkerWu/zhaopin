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
 "status": 1,
 "msg": "success",
 "data": {
 "resume_id": "1",
 "base": {
 "id": "1"
 },
 "jobs": {
 "id": "4"
 },
 "projects": {},
 "education": {},
 "language": {}
 }
 }*/

public class ResumeSubmitResult extends Entity {
	private Result validate;
	private String resume_Id;
	private int item_Id;
	private String url;

	public final int getItem_Id() {
		return item_Id;
	}

	public final void setItem_Id(int item_Id) {
		this.item_Id = item_Id;
	}

	public final Result getValidate() {
		return validate;
	}

	public final void setValidate(Result validate) {
		this.validate = validate;
	}

	public final String getResume_Id() {
		return resume_Id;
	}

	public final void setResume_Id(String resume_Id) {
		this.resume_Id = resume_Id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static ResumeSubmitResult parse(AppContext appContext,
			InputStream inputStream, int submitBlockId) throws IOException,
			AppException {
		ResumeSubmitResult entity = new ResumeSubmitResult();
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
				JSONObject rootobj = jsonObj.getJSONObject("data");
				entity.resume_Id = rootobj
						.getString(ResumeEntity.NODE_RESUME_ID);
				if(rootobj.has("url"))
					entity.url = rootobj.getString("url");
				switch (submitBlockId) {
				case ResumeEntity.RESUME_BLOCK_BASE:

					break;
				case ResumeEntity.RESUME_BLOCK_JOBS: {
					JSONObject itemobj = rootobj
							.getJSONObject(ResumeEntity.NODE_JOBS);
					entity.item_Id = itemobj.getInt("id");
				}
					break;
				case ResumeEntity.RESUME_BLOCK_PROJECTS:{
					JSONObject itemobj = rootobj
							.getJSONObject(ResumeEntity.NODE_PROJECTS);
					entity.item_Id = itemobj.getInt("id");
				}
					
					break;
				case ResumeEntity.RESUME_BLOCK_EDUCATIONS:
				{
					JSONObject itemobj = rootobj
							.getJSONObject(ResumeEntity.NODE_EDUCATIONS);
					entity.item_Id = itemobj.getInt("id");
				}

					break;
				case ResumeEntity.RESUME_BLOCK_LANGUAGES:
				{
					JSONObject itemobj = rootobj
							.getJSONObject(ResumeEntity.NODE_LANGUAGES);
					entity.item_Id = itemobj.getInt("id");
				}

					break;
				}

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block

			throw AppException.json(e);
		}

		return entity;

	}
}

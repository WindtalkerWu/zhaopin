package com.qianniu.zhaopin.app.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumePicSubmitResult;
import com.qianniu.zhaopin.app.bean.ResumeSubmitResult;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.database.DBUtils;

public class ResumeUtils {
	public static final int RESUME_SUBMIT_ERROR = -100;
	public static final int RESUME_SUBMIT_NETERROR = -102;
	public static final int RESUME_SUBMIT_DATAERROR = -103;
	public static final int RESUME_SUBMIT_EXCEPTIONERROR = -104;

	public static final int RESUME_SUBMITDEL_ERROR = -105;
	public static final int RESUME_SUBMITDEL_NETERROR = -106;
	public static final int RESUME_SUBMITDEL_DATAERROR = -107;
	public static final int RESUME_SUBMITDEL_EXCEPTIONERROR = -108;
	


	public static final int RESUME_SUBMIT_OK = 200;
	public static final int RESUME_SUBMITDEL_OK = 201;
	public static final int RESUME_SUBMITIMG_OK = 202;
	//简历删除汇报返回
	public static final int RESUME_DEL_OK = 203;
	public static final int RESUME_DEL_ERR = 204;
	//简历设置默认汇报返回
	public static final int RESUME_SETDEFAULT_OK = 205;
	public static final int RESUME_SETDEFAULT_ERR = 206;

	public static final int RESUME_GETGLOBALDATA = 10;
	public static final int RESUME_GETGLOBALDATA_INDUSTRAY = 11;
	public static final int RESUME_GETGLOBALDATA_CITY = 12;

	/**
	 * 线程提交简历数据(新增或更新),handler 返回数据
	 * 
	 * @param appcontext
	 * @param resume_id
	 * @param jsonstring
	 * @param resumeblock
	 * @param handler
	 */
	public static void submitData(final AppContext appcontext,
			final int resume_id, final String jsonstring,
			final int resumeblock, final Handler handler) {

		new Thread() {
			public void run() {
				Message msg = new Message();
				String resumeid = (resume_id > 0) ? String.valueOf(resume_id)
						: "0";
				String block = null;
				switch (resumeblock) {
				case ResumeEntity.RESUME_BLOCK_BASE:
					block = ResumeEntity.NODE_BASE;
					break;
				case ResumeEntity.RESUME_BLOCK_JOBS:
					block = ResumeEntity.NODE_JOBS;
					break;
				case ResumeEntity.RESUME_BLOCK_PROJECTS:
					block = ResumeEntity.NODE_PROJECTS;
					break;
				case ResumeEntity.RESUME_BLOCK_EDUCATIONS:
					block = ResumeEntity.NODE_EDUCATIONS;
					break;
				case ResumeEntity.RESUME_BLOCK_LANGUAGES:
					block = ResumeEntity.NODE_LANGUAGES;
					break;
				case ResumeEntity.RESUME_BLOCK_QUICKITEM:
					block = ResumeEntity.NODE_QUICKITEM;
					break;
				}
				if (block == null || jsonstring == null) {
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
					return;
				}
				if (!appcontext.isNetworkConnected()) {
					msg.what = RESUME_SUBMIT_NETERROR;
					handler.sendMessage(msg);
					return;
				}

				String url = URLs.RESUME_SUBMIT + "/" + resumeid + "/" + block;
				JSONObject obj = new JSONObject();
				try {
					if (resumeblock == ResumeEntity.RESUME_BLOCK_BASE || resumeblock == ResumeEntity.RESUME_BLOCK_QUICKITEM) {
						JSONObject dataobj = new JSONObject(jsonstring);
						obj.put(block, dataobj);

					} else {
						JSONArray array = new JSONArray();
						JSONObject dataobj = new JSONObject(jsonstring);
						array.put(dataobj);
						obj.put(block, array);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
					return;
				}


				Map<String, Object> params = AppContext.getHttpPostParams(appcontext,obj.toString());
				ResumeSubmitResult result = null;
				try {

					result = ResumeSubmitResult.parse(appcontext,
							ApiClient._post(appcontext, url, params, null),
							resumeblock);
				} catch (Exception e) {
					msg.what = RESUME_SUBMIT_EXCEPTIONERROR;
					msg.obj = e;
				}

				if (result != null) {
					msg.what = RESUME_SUBMIT_OK;
					msg.obj = result;
				} else {
					msg.what = RESUME_SUBMIT_ERROR;
					msg.obj = result;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}

	public static void deleteData(final AppContext appcontext,
			final int resume_id, final String jsonstring,
			final int resumeblock, final Handler handler) {

		new Thread() {
			public void run() {
				Message msg = new Message();
				String resumeid = (resume_id > 0) ? String.valueOf(resume_id)
						: "0";
				String block = null;
				switch (resumeblock) {
				case ResumeEntity.RESUME_BLOCK_BASE:
					block = ResumeEntity.NODE_BASE;
					break;
				case ResumeEntity.RESUME_BLOCK_JOBS:
					block = ResumeEntity.NODE_JOBS;
					break;
				case ResumeEntity.RESUME_BLOCK_PROJECTS:
					block = ResumeEntity.NODE_PROJECTS;
					break;
				case ResumeEntity.RESUME_BLOCK_EDUCATIONS:
					block = ResumeEntity.NODE_EDUCATIONS;
					break;
				case ResumeEntity.RESUME_BLOCK_LANGUAGES:
					block = ResumeEntity.NODE_LANGUAGES;
					break;
				case ResumeEntity.RESUME_BLOCK_QUICKITEM:
					block = ResumeEntity.NODE_QUICKITEM;
					break;
				}
				if (block == null || jsonstring == null) {
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
					return;
				}
				if (!appcontext.isNetworkConnected()) {
					msg.what = RESUME_SUBMIT_NETERROR;
					handler.sendMessage(msg);
					return;
				}

				String url = URLs.RESUME_SUBMIT + "/" + resumeid + "/" + "del";
				JSONObject obj = new JSONObject();
				try {
					if (resumeblock == ResumeEntity.RESUME_BLOCK_BASE) {
						JSONObject dataobj = new JSONObject(jsonstring);
						obj.put(block, dataobj);

					} else {
						JSONArray array = new JSONArray();
						JSONObject dataobj = new JSONObject(jsonstring);
						array.put(dataobj);
						obj.put(block, array);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					msg.what = RESUME_SUBMITDEL_DATAERROR;
					handler.sendMessage(msg);
					return;
				}


				Map<String, Object> params = AppContext.getHttpPostParams(appcontext,obj.toString());
				ResumeSubmitResult result = null;
				try {

					result = ResumeSubmitResult.parse(appcontext,
							ApiClient._post(appcontext, url, params, null),
							resumeblock);
				} catch (Exception e) {
					msg.what = RESUME_SUBMITDEL_EXCEPTIONERROR;
					msg.obj = e;
				}

				if (result != null) {
					msg.what = RESUME_SUBMITDEL_OK;
					msg.obj = result;
				} else {
					msg.what = RESUME_SUBMITDEL_ERROR;
					msg.obj = result;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}

	public static void submitHeadphoto(final AppContext appcontext,
			final int resume_id, final Bitmap photo, final Handler handler) {

		new Thread() {
			public void run() {
				Message msg = new Message();
				String resumeid = (resume_id > 0) ? String.valueOf(resume_id)
						: "0";
				if (photo == null) {
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
					return;
				}
				if (!appcontext.isNetworkConnected()) {
					msg.what = RESUME_SUBMIT_NETERROR;
					handler.sendMessage(msg);
					return;
				}
				String filename = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/"
						+ "headhphoto_"
						+ System.currentTimeMillis() + ".jpg";

				try {
					FileOutputStream fileOutputStream = new FileOutputStream(
							filename);
					int totalbytes = photo.getRowBytes()*photo.getHeight();
					byte[] bytes = ImageUtils.getBitmapByte(photo);
					int quality = 100;
					if(totalbytes > 50*1024){
						quality = (50*1024)/totalbytes;
					}
					photo.compress(Bitmap.CompressFormat.JPEG, quality,
							fileOutputStream);
					fileOutputStream.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
				}

				

				String url = URLs.RESUME_PICTURE_SUBMIT; // "http://222.66.4.242:90/l_project_web/public/api/imgupload";//
				// "http://10.30.100.24/work/apps/code/l_project_web/public/api/imgupload";//
/*				List<GlobalDataTable> list = GlobalDataTable.getTpyeData(
						appcontext,
						DBUtils.GLOBALDATA_TYPE_PICTURESERVERADDRESS);
				if (list != null && list.size() > 0) {
					GlobalDataTable data = list.get(0);
					url = data.getName();
				}*/
				if (url == null || url.length() == 0) {
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
					return;
				}
				JSONObject obj = new JSONObject();

				try {
					obj.put("resume_id", resume_id);
					//obj.put("binary_content", bytes);
					obj.put("pic_type", "JPEG");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
					return;
				}
				Map<String, Object> params = AppContext.getHttpPostParams(appcontext,obj.toString());
				
				Map<String, File> files = new HashMap<String, File>();
				File photofile = new File(filename);
				long filesize = photofile.length();
				files.put("uploadfile", photofile);

				ResumePicSubmitResult result = null;
				try {

					result = ResumePicSubmitResult.parse(appcontext,
							ApiClient._post(appcontext, url, params, files));
				} catch (Exception e) {
					msg.what = RESUME_SUBMIT_EXCEPTIONERROR;
					msg.obj = e;
				} finally {
					if (photofile.exists()) {
						photofile.delete();
					}
				}

				if (result != null) {
					msg.what = RESUME_SUBMITIMG_OK;
					msg.obj = result;
				} else {
					msg.what = RESUME_SUBMIT_ERROR;
					msg.obj = result;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}

	/**删除简历
	 * @param appcontext
	 * @param resume_id
	 * @param handler
	 */
	public static void deleteResume(final AppContext appcontext,
			final int resume_id, final Handler handler) {

		new Thread() {
			public void run() {
				Message msg = new Message();
				String resumeid = (resume_id > 0) ? String.valueOf(resume_id)
						: null;
				if(resumeid == null){					
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
					return;
				}
				if (!appcontext.isNetworkConnected()) {
					msg.what = RESUME_SUBMIT_NETERROR;
					handler.sendMessage(msg);
					return;
				}

				String url = URLs.RESUME_STATE;
				JSONObject obj = new JSONObject();
				try {
					obj.put("resume_id", resume_id);
					obj.put("status", 2);//2为存档，即从界面删除
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
					return;
				}

				Map<String, Object> params = AppContext.getHttpPostParams(appcontext,obj.toString());
				Result result = null;
				try {
					result = Result.parse(ApiClient._post(appcontext, url, params, null));

				} catch (Exception e) {
					msg.what = RESUME_SUBMIT_EXCEPTIONERROR;
					msg.obj = e;
				}

				if (result != null) {
					msg.what = RESUME_DEL_OK;
					msg.obj = result;
				} else {
					msg.what = RESUME_DEL_ERR;
					msg.obj = result;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}
	
	/**设置默认简历上报
	 * @param appcontext
	 * @param resume_id
	 * @param handler
	 */
	public static void setDefaultResume(final AppContext appcontext,
			final int resume_id, final Handler handler) {

		new Thread() {
			public void run() {
				Message msg = new Message();
				String resumeid = (resume_id > 0) ? String.valueOf(resume_id)
						: null;
				if(resumeid == null){					
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
					return;
				}
				if (!appcontext.isNetworkConnected()) {
					msg.what = RESUME_SUBMIT_NETERROR;
					handler.sendMessage(msg);
					return;
				}

				String url = URLs.RESUME_SELECT;
				JSONObject obj = new JSONObject();
				try {
					obj.put("resume_id", resume_id);					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					msg.what = RESUME_SUBMIT_DATAERROR;
					handler.sendMessage(msg);
					return;
				}



				Map<String, Object> params = AppContext.getHttpPostParams(appcontext,obj.toString());
				Result result = null;
				try {
					result = Result.parse(ApiClient._post(appcontext, url, params, null));

				} catch (Exception e) {
					msg.what = RESUME_SUBMIT_EXCEPTIONERROR;
					msg.obj = e;
				}

				if (result != null) {
					msg.what = RESUME_SETDEFAULT_OK;
					msg.obj = result;
				} else {
					msg.what = RESUME_SETDEFAULT_ERR;
					msg.obj = result;
				}
				handler.sendMessage(msg);
			}
		}.start();

	}
	
	public static ResumeSubmitResult submitDataNoThread(final AppContext appcontext,
			final int resume_id, final String jsonstring,
			final int resumeblock) {

		String resumeid = (resume_id > 0) ? String.valueOf(resume_id)
				: "0";
		String block = null;
		switch (resumeblock) {
		case ResumeEntity.RESUME_BLOCK_BASE:
			block = ResumeEntity.NODE_BASE;
			break;
		case ResumeEntity.RESUME_BLOCK_JOBS:
			block = ResumeEntity.NODE_JOBS;
			break;
		case ResumeEntity.RESUME_BLOCK_PROJECTS:
			block = ResumeEntity.NODE_PROJECTS;
			break;
		case ResumeEntity.RESUME_BLOCK_EDUCATIONS:
			block = ResumeEntity.NODE_EDUCATIONS;
			break;
		case ResumeEntity.RESUME_BLOCK_LANGUAGES:
			block = ResumeEntity.NODE_LANGUAGES;
			break;
		case ResumeEntity.RESUME_BLOCK_QUICKITEM:
			block = ResumeEntity.NODE_QUICKITEM;
			break;
		}
		if (block == null || jsonstring == null) {

			return null;
		}
		if (!appcontext.isNetworkConnected()) {

			return null;
		}

		String url = URLs.RESUME_SUBMIT + "/" + resumeid + "/" + "del";
		JSONObject obj = new JSONObject();
		try {
			if (resumeblock == ResumeEntity.RESUME_BLOCK_BASE) {
				JSONObject dataobj = new JSONObject(jsonstring);
				obj.put(block, dataobj);

			} else {
				JSONArray array = new JSONArray();
				JSONObject dataobj = new JSONObject(jsonstring);
				array.put(dataobj);
				obj.put(block, array);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block

			return null;
		}


		Map<String, Object> params = AppContext.getHttpPostParams(appcontext,obj.toString());
		ResumeSubmitResult result = null;
		try {

			result = ResumeSubmitResult.parse(appcontext,
					ApiClient._post(appcontext, url, params, null),
					resumeblock);
		} catch (Exception e) {

		}

		return result;
	}
}

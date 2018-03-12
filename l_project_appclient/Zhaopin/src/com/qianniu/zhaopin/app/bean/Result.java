package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Xml;

/**
 * 数据操作结果实体类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Result extends Base {

	private static final String TAG = "Result";
	public static final int CODE_OK = 1;

	public static final int CODE_NOTLOGIN_1000 = 1000; // 未登录
	public static final int CODE_UNSUBSCRIBED = 1002; // 未订阅

	public static final int CODE_SERVERESERVICE = 1200; // 服务器服务, 请联系管理员
	public static final int CODE_NOTLOGIN_1203 = 1203; // 未登录
	public static final int CODE_NOTMORE = 1999; // 无更新数据
	
	public static final int CODE_OLDPWD_1306 = 1306; // 修改密码，旧密码不一致
	public static final int CODE_TASKNULL_1501 = 1501; //task表为空

	public static final int CODE_TOKEN_INVALID = 2301; // 无效的token
	public static final int CODE_TOKEN_OVERTIME = 2302; // 过期的token

	public static final int CODE_NOTLOGIN_3000 = 3000; // 未登录
	public static final int CODE_DATA_NULL_1901 = 1901;//数据为null
			
	public static final int CODE_VERIFY_CODE_NOT_SAME = 1955; // 验证码不一致
	
	public static final int CODE_USERNAME_HASREGISTER = 4004; // 用户名已经被注册
	
//	public static final int CODE_INDUSTRYINSIDERS_EMPTY = 1581; // 行业圈内人数据为null
//	public static final int CODE_COMMENT_EMPTY = 1201; // 评论列表为null
	public static final int CODE_DATA_EMPTY = 1999; // 列表为null
	
	/*************************************************************************/
	//简历部分返回code
	public static final int CODE_RESUME_LIST_EMPTY = 1999; // 简历列表为空
	
	/*************************************************************************/

	// 普通state code 从 -10到-29
	public static final int CODE_NETWORK_UNCONNECT = -10; // 网络连接未打开

	// exception 从 -30到-40
	public static final int CODE_EXCEPTION = -30; // 异常

	private int errorCode;
	private String errorMessage;
	private String jsonStr;

	// private Comment comment;

	public boolean OK() {
		return errorCode == 1;
	}

	public Result(int errorCode, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public boolean handleErrcode(Activity activity) {
		boolean bflag = false;
		AppContext appcontext = (AppContext) activity.getApplicationContext();
		switch (errorCode) {
		case CODE_NETWORK_UNCONNECT:
			UIHelper.ToastMessage(activity, R.string.network_not_connected);
			break;
		case CODE_NOTLOGIN_1000: // 未登录
		case CODE_NOTLOGIN_3000: // 未登录
		case CODE_NOTLOGIN_1203: {
			UIHelper.ToastMessage(activity, R.string.usermanager_unlogin);
			appcontext.Logout();
			UIHelper.showLoginActivityForResult(activity);
			bflag = true;
		}
			break;

		case CODE_TOKEN_INVALID:
		case CODE_TOKEN_OVERTIME: {
			switch (errorCode) {
			case CODE_TOKEN_INVALID:
/*				UIHelper.ToastMessage(activity,
						R.string.usermanager_token_invalid);*/

				UIHelper.ToastMessage(activity,
						R.string.usermanager_unlogin);
				break;
			case CODE_TOKEN_OVERTIME:
/*				UIHelper.ToastMessage(activity,
						R.string.usermanager_token_overtime);*/
				UIHelper.ToastMessage(activity,
						R.string.usermanager_unlogin);
				break;
			}
			appcontext.Logout();
			UIHelper.showLoginActivityForResult(activity);
			bflag = true;
		}
			break;
		case CODE_OLDPWD_1306:
			UIHelper.ToastMessage(activity,
					R.string.usermanager_oldpwd_error);
			break;
		case CODE_TASKNULL_1501:
			UIHelper.ToastMessage(activity,
					R.string.usermanager_task_null);
			break;
		case CODE_DATA_NULL_1901:
			break;
		default: {
			UIHelper.ToastMessage(activity.getApplicationContext(), "请求失败");
			bflag = false;
		}
			break;
		}
		return bflag;

	}

	public String getJsonStr() {
		return jsonStr;
	}

	public void setJsonStr(String jsonStr) {
		this.jsonStr = jsonStr;
	}

	/**
	 * 解析调用结果
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Result parse(InputStream stream) throws IOException,
			AppException {
		Result res = null;
		StringBuilder builder = new StringBuilder();
		BufferedReader bufreader = new BufferedReader(new InputStreamReader(
				stream));
		for (String s = bufreader.readLine(); s != null; s = bufreader
				.readLine()) {
			builder.append(s);
		}
		MyLogger.i(TAG, "Result##" + builder.toString());
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");
			String jsonStr = jsonObj.getString("data");
			res = new Result(status, statusmsg);
			res.setJsonStr(jsonStr);
		} catch (JSONException e) {
			throw AppException.json(e);
		} finally {
			stream.close();
		}
		return res;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/*
	 * public Comment getComment() { return comment; } public void
	 * setComment(Comment comment) { this.comment = comment; }
	 */
	@Override
	public String toString() {
		return String.format("RESULT: CODE:%d,MSG:%s", errorCode, errorMessage);
	}

}

package com.qianniu.zhaopin.app.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.bean.Base;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;

/**
 * 登录用户实体类

 */
public class User extends Base {
/*	"user.access_token", "user.uid", "user.name",
	"user.face", "user.account", "user.login_type",
	"user.thirdpart_id", "user.thirdpart_token", "user.pwd",
	"user.location", "user.followers", "user.fans", "user.score",
	"user.isRememberMe","user.user_id"*/
	
	public static final String KEY_USER_ACCESS_TOKEN = "access_token";
	public static final String KEY_USER_ID= "user.user_id";
	public static final String KEY_USER_NAME = "user.name";
	public static final String KEY_USER_ACCOUNT = "user.account";
	public static final String KEY_USER_PASSWORD = "user.pwd";
	public static final String KEY_USER_REMEMBERME = "user.isRememberMe";
		
	private String name;
	private String account;
	private String pwd;
	private Result validate;
	private boolean isRememberMe;
	private String access_token ;
	private String user_id ; 
	
	private String login_type;			// 登录类型
	private String thirdpart_token;		// 第三方Token
	private String thirdpart_id;		// open id


	public User(){
		this.name = "";
		this.account = "";
		this.pwd = "";
		this.access_token = "";
		this.login_type = Integer.toString(HeadhunterPublic.LOGINTYPE_DEFAULT);
		this.thirdpart_token = "";
		this.thirdpart_id = "";
		this.user_id = ""; 
	}
	
	public final String getAccessToken() {
		return access_token;
	}
	
	public final void setAccessToken(String access_token) {
		this.access_token = access_token;
	}
	
	public final String getUserid() {
		return user_id;
	}

	public final void setUserid(String user_id) {
		this.user_id = user_id;
	}

	public boolean isRememberMe() {
		return isRememberMe;
	}
	
	public void setRememberMe(boolean isRememberMe) {
		this.isRememberMe = isRememberMe;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Result getValidate() {
		return validate;
	}
	
	public void setValidate(Result validate) {
		this.validate = validate;
	}
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getPwd() {
		return pwd;
	}
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/*****************************************************************
	 * 获取登录类型
	 * 
	 ****************************************************************/
	public String getLoginType(){
		return this.login_type;
	}
	
	/*****************************************************************
	 * 设置登录类型
	 * 
	 ****************************************************************/
	public void setLoginType(String str){
		this.login_type = str;
	}
	
	/*****************************************************************
	 * 获取第三方登陆的ID
	 * 
	 ****************************************************************/
	public String getThirdPartId(){
		return this.thirdpart_id;
	}
	
	/*****************************************************************
	 * 设置第三方登陆的ID
	 * 
	 ****************************************************************/
	public void setThirdPartId(String str){
		this.thirdpart_id = str;
	}
	
	/*****************************************************************
	 * 获取第三方登陆的TOKEN
	 * 
	 ****************************************************************/
	public String getThirdPartToken(){
		return this.thirdpart_token;
	}
	
	/*****************************************************************
	 * 设置第三方登陆的TOKEN
	 * 
	 ****************************************************************/
	public void setThirdPartToken(String str){
		this.thirdpart_token = str;
	}
	
	public static User parse(InputStream stream) throws IOException, AppException {
		User user = new User();
		Result res = null;
		StringBuilder builder = new StringBuilder(); 
		BufferedReader bufreader = new BufferedReader(new  InputStreamReader(stream));
        for (String s = bufreader.readLine(); s != null; s = bufreader 
                .readLine()) { 
            builder.append(s); 
        } 
        
		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");
			res = new Result(status,statusmsg);
			user.validate = res;
/*			
			String data = jsonObj.getString("data");
			if(null != data)
				user.access_token = new String(data);*/
			if(1 == status){
				JSONObject datajosn = jsonObj.getJSONObject("data");
				user.access_token = datajosn.getString("access_token");
				user.user_id = datajosn.getString("user_id");
			}
			
		} catch(JSONException e){
			res = new Result(-1,"json error");
		}catch (Exception e) {
			res = new Result(-1,"Exception error");
		} finally {
			stream.close();
			user.validate = res;
			return user;
		}
		
	}
	
	public static User findpassparse(InputStream stream) throws IOException, AppException {
		User user = new User();
		Result res = null;
		StringBuilder builder = new StringBuilder(); 
		BufferedReader bufreader = new BufferedReader(new  InputStreamReader(stream));
        for (String s = bufreader.readLine(); s != null; s = bufreader 
                .readLine()) { 
            builder.append(s); 
        } 
        
		try {
			JSONObject jsonObj = new JSONObject(builder.toString());
			int status = jsonObj.getInt("status");
			String statusmsg = jsonObj.getString("msg");
			res = new Result(status,statusmsg);
			user.validate = res;
/*			
			String data = jsonObj.getString("data");
			if(null != data)
				user.access_token = new String(data);*/
//			if(1 == status){
//				JSONObject datajosn = jsonObj.getJSONObject("data");
//				user.access_token = datajosn.getString("access_token");
//				user.user_id = datajosn.getString("user_id");
//			}
			
		} catch(JSONException e){
			res = new Result(-1,"json error");
		}catch (Exception e) {
			res = new Result(-1,"Exception error");
		} finally {
			stream.close();
			user.validate = res;
			return user;
		}
		
	}
}

package com.qianniu.zhaopin.thp.Listener;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.thp.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

public class SinaAuthListener implements WeiboAuthListener{
	private Context m_Context;
	private Handler m_Handler;
	private String m_srtCode;
	
	private Oauth2AccessToken oauth2AccessToken;

	public SinaAuthListener(Context context, Handler handler){
		this.m_Context = context;
		this.m_Handler = handler;
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		if(null != m_Handler){
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.LOGINMSG_THP_CANCEL));
		} 
	}

	@Override
	public void onComplete(Bundle values) {
		// TODO Auto-generated method stub
		m_srtCode = values.getString("code"); 
		
//		MyLogger.i("新浪微博登录", "code = " + m_srtCode);
		
		if(null != m_srtCode){
			//android 4.0以后访问HTTP必须异步处理  
	    	new Thread(){
	        	public void run(){
	        		getToken(m_srtCode);
	        	}
	        }.start();
		}else{
            String access_token = values.getString("access_token");  
            String expires_in = values.getString("expires_in"); 
            String strUID = values.getString("uid");
            String strUserName = values.getString("userName");
            
            if(null == strUID){
            	Toast.makeText(m_Context.getApplicationContext(),
            			"新浪微博登录失败", Toast.LENGTH_SHORT).show();
            	return;
            }
            
            if(strUID.isEmpty()){
            	Toast.makeText(m_Context.getApplicationContext(),
            			"新浪微博登录失败", Toast.LENGTH_SHORT).show();
            	return;
            }
            
            // 保存token
            saveToken(strUID, access_token, expires_in);
            
    		if(null != m_Handler){

    			ReqUserInfo req = new ReqUserInfo();
    			req.setLoginType(HeadhunterPublic.LOGINTYPE_SINAWEIBO);
    			req.setThirdPartToken(access_token);
    			req.setThirdPartId(strUID);
    			
    			if(null != strUserName && !strUserName.isEmpty()){
    				req.setDisplay_name(strUserName);
    			}
    			
    			m_Handler.sendMessage(m_Handler.obtainMessage(
    					HeadhunterPublic.LOGINMSG_THP_SUCCESS, req));
    		}else{
    			Toast.makeText(m_Context.getApplicationContext(), "新浪微博登录失败", Toast.LENGTH_SHORT).show();
    		}
		}
	}

	@Override
	public void onWeiboException(WeiboException arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(m_Context.getApplicationContext(), "新浪微博登录出现错误", Toast.LENGTH_SHORT).show();
		
		if(null != m_Handler){
			m_Handler.sendMessage(m_Handler.obtainMessage(
					HeadhunterPublic.LOGINMSG_THP_EXCEPTION, arg0));
		} 
	}
	
	private void getToken(String strCode){
        try {
        	//key secret 在基本信息里 回调页面 在高级信息里  
        	String url = "https://api.weibo.com/oauth2/access_token?client_id=" +
					UIHelper.getTHPAPPIDForNum(
							m_Context, HeadhunterPublic.THPKEY_SINAWEIBO_APPID)
        			+ "&client_secret=" +
					UIHelper.getTHPAPPID(
							m_Context, HeadhunterPublic.THPKEY_SINAWEIBO_APPSECRET)
        			+ "&grant_type=authorization_code&redirect_uri=" + 
        			HeadhunterPublic.SINAWEIBO_REDIRECT_URL + "&code=";
        	
            HttpPost post=new HttpPost(URI.create(url + strCode));  
            HttpClient httpClient=new DefaultHttpClient();  
            HttpResponse response= httpClient.execute(post); 
            
            if (response.getStatusLine().getStatusCode() == 200) {  
                  
                    String temp=EntityUtils.toString(response.getEntity()); 
                    
                    JSONObject o = new JSONObject(temp); 
                    
                    String access_token = o.getString("access_token");  
                    String expires_in = o.getString("expires_in"); 
                    String strUID = o.getString("uid");
                    String strUserName = o.getString("userName");
                    
                    // 保存token
                    saveToken(strUID, access_token, expires_in);
                    
            		if(null != m_Handler){

            			ReqUserInfo req = new ReqUserInfo();
            			req.setLoginType(HeadhunterPublic.LOGINTYPE_SINAWEIBO);
            			req.setThirdPartToken(access_token);
            			req.setThirdPartId(strUID);
            			
            			if(null != strUserName && !strUserName.isEmpty()){
            				req.setDisplay_name(strUserName);
            			}
            			
            			m_Handler.sendMessage(m_Handler.obtainMessage(
            					HeadhunterPublic.LOGINMSG_THP_SUCCESS, req));
            		}  
            }  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (JSONException e) {  
            e.printStackTrace();  
        }  
	}
	
	private void saveToken(String strUid, String strAccessToken, String strExpiresIn){
	    //这里按照 毫秒计算  
	    long time= Long.parseLong(strExpiresIn)*1000+ System.currentTimeMillis();  
	    if (oauth2AccessToken==null) {  
	    	oauth2AccessToken = new Oauth2AccessToken();  
	    } 
	    
	    oauth2AccessToken.setExpiresTime(time);  
	    oauth2AccessToken.setToken(strAccessToken); 
	    oauth2AccessToken.setUid(strUid);
          
	    AccessTokenKeeper.writeAccessToken(m_Context, oauth2AccessToken);
	}
}
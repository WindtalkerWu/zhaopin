package com.qianniu.zhaopin.thp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.qianniu.zhaopin.app.ConfigOptions;
import com.umeng.analytics.MobclickAgent;
/**
 * 友盟分享和统计
 * @author wuzy
 *
 */
public class UmShare {
//	private UMSocialService m_UMcontroller;
	
	public UmShare(){
//		this.m_UMcontroller = null;
//		
//		umInit();
	}
	
	/**
	 * 初始化
	 * @return
	 */
//	private UMSocialService umInit(){
//		
//		UMSocialService umController = UMServiceFactory.getUMSocialService("Android Test", RequestType.SOCIAL);
//		
//		// 设置新浪SSO handler
//		umController.getConfig().setSinaSsoHandler(new SinaSsoHandler());
//		
//		// 
//		umController.getConfig().setShareMail(false);
//		umController.getConfig().setShareSms(false);
//		
//		// 移除人人，QQ空间， 豆瓣平台
//		umController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.QZONE, SHARE_MEDIA.DOUBAN);
//		
//		return umController;
//	}
	
	/**
	 * 显示友盟分享界面
	 * @param con
	 */
//	public void umOpenShare(Context con){
	public void umOpenShare(Activity activity){
//		if(null == m_UMcontroller){
//			m_UMcontroller = umInit();
//		}
		
//		m_UMcontroller.openShare(
//				con, false);
//		m_UMcontroller.openShare(activity, false);
	}
	
	/**
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void UmActivityResult(int requestCode, int resultCode, Intent data){
//		if(null != m_UMcontroller){
			 /**
		     * 使用SSO必须添加，指定获取授权信息的回调页面，并传给SDK进行处理
		     */
//		    UMSsoHandler sinaSsoHandler = m_UMcontroller.getConfig().getSinaSsoHandler();
//		    if ( sinaSsoHandler != null && requestCode == UMSsoHandler.DEFAULT_AUTH_ACTIVITY_CODE) {
//		    if ( null != sinaSsoHandler) {
//		        sinaSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//		    }			
//		}
	}
	
	/**
	 * 友盟统计
	 * @param context
	 * @param eventId
	 */
	public static void UmStatistics(Context context, String eventId){
		MobclickAgent.onEvent(context, eventId);
	}
	
	public static void UmsetDebugMode(Context context){
//		MobclickAgent.updateOnlineConfig(context);
		MobclickAgent.setDebugMode(ConfigOptions.logFlag);
	}
	
	public static void UmResume(Context context){
		MobclickAgent.onResume(context);
	}
	
	public static void UmPause(Context context){
		MobclickAgent.onPause(context);
	}
	
	/**
	 * 集成测试用来获取设备信息
	 * @param context
	 * @return
	 */
	public static String getDeviceInfo(Context context) {
	    try{
	      org.json.JSONObject json = new org.json.JSONObject();
	      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
	          .getSystemService(Context.TELEPHONY_SERVICE);
	  
	      String device_id = tm.getDeviceId();
	      
	      android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	          
	      String mac = wifi.getConnectionInfo().getMacAddress();
	      json.put("mac", mac);
	      
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = mac;
	      }
	      
	      if( TextUtils.isEmpty(device_id) ){
	        device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
	      }
	      
	      json.put("device_id", device_id);
	      
	      return json.toString();
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	  return null;
	}
                  
}

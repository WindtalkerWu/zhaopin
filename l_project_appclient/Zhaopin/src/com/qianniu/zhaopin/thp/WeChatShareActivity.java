package com.qianniu.zhaopin.thp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class WeChatShareActivity extends Activity implements IWXAPIEventHandler{
	private IWXAPI  m_IWXAPI;
	private Context m_Context;
	
	private String m_strContent;
	private boolean m_bShareFriends;
//	public WeChatShare(Context context){
//		m_Context = context;
//		// 
//		m_IWXAPI = WXAPIFactory.createWXAPI(m_Context, HeadhunterPublic.WECHAT_APPID);
//		// 将牵牛的appid注册到微信
//		m_IWXAPI.registerApp(HeadhunterPublic.WECHAT_APPID);
//	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.m_Context = this;
		
		if(!initData()){
			finish();
		}
		
		String strAppID = UIHelper.getTHPAPPID(
				m_Context, HeadhunterPublic.THPKEY_WECHAT_APPID);
		
		// 
		m_IWXAPI = WXAPIFactory.createWXAPI(m_Context, strAppID);
		// 将牵牛的appid注册到微信
		m_IWXAPI.registerApp(strAppID);
		//
		m_IWXAPI.handleIntent(getIntent(), this);
		
		// 
		ShareText(m_strContent, m_bShareFriends);
	}

	@Override
	public void onReq(BaseReq req) {
		// TODO Auto-generated method stub
		switch(req.getType()){
		
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
		switch(resp.getType()){
		
		}
	}
	
	private boolean initData(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(null != bundle){
			m_strContent = bundle.getString("content");
			if(null == m_strContent){
				return false;
			}

			m_bShareFriends = bundle.getBoolean("sharefriends");

			return true;
		}
		
		return false;
	}
	
	/**
	 * 分享文本
	 * @param strMsg
	 * @param bShareFriends		true: 分享微信朋友圈/false: 分享微信朋友
	 */
	public void ShareText(String strContent, boolean bShareFriends){
        if(!m_IWXAPI.isWXAppInstalled()) {
            Toast.makeText(m_Context, "目前您的微信版本过低或者未安装微信，需要安装微信才能使用",
            		Toast.LENGTH_SHORT).show();
            finish();
        	return;
        }

		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = strContent;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = strContent;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		req.scene = bShareFriends ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		
		// 调用api接口发送数据到微信
		boolean bRet = m_IWXAPI.sendReq(req);
//		finish();
//	    if(bRet){
//	    	Toast.makeText(m_Context, "分享成功", 2000).show();
//	    	
//	    }else{
//	    	Toast.makeText(m_Context, "分享失败", 2000).show();
//	    	finish();
//	    }      
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}

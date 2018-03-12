package com.qianniu.zhaopin.thp.Listener;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

public class QQUiListener implements IUiListener{
	Context m_Context;
	
	Handler m_Handler;
	
	public QQUiListener(Context context, Handler handler){
		this.m_Context = context;
		this.m_Handler = handler;
	}
	
	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		Toast.makeText(m_Context.getApplicationContext(), "取消QQ登录", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onComplete(JSONObject values) {
		// TODO Auto-generated method stub
		
		try {
			String strToken = values.getString("access_token");
			String strOpenId = values.getString("openid");
			
			String strTemp = "openid: " + strOpenId + "\naccess_token: " + strToken;
			
//			strOpenId = "76EC9684D9DD12923A8BDAA88394FA08";
//			strToken = "B51AC021D8E6373E0BA45304237BCCBD";
			
//			MyLogger.i("QQ登录成功 ", strTemp);
			
			if(null != m_Handler){
				ReqUserInfo req = new ReqUserInfo();
				req.setLoginType(HeadhunterPublic.LOGINTYPE_QQ);
				req.setThirdPartToken(strToken);
				req.setThirdPartId(strOpenId);
				  
				m_Handler.sendMessage(m_Handler.obtainMessage(
						HeadhunterPublic.LOGINMSG_THP_SUCCESS, req));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onError(UiError e) {
		// TODO Auto-generated method stub
		
		String strTemp = "code:" + e.errorCode + ", msg:"
                + e.errorMessage + ", detail:" + e.errorDetail;
                
		Toast.makeText(m_Context.getApplicationContext(), "QQ登录出现错误", Toast.LENGTH_SHORT).show();
		
//		MyLogger.i("QQ登录出现错误 ", strTemp);
	}

}

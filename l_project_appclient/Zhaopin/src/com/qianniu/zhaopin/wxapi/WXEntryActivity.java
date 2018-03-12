package com.qianniu.zhaopin.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private IWXAPI  m_IWXAPI;
	private Context m_Context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_wxentry);
		
		this.m_Context = this;
		
		// 
		m_IWXAPI = WXAPIFactory.createWXAPI(m_Context, UIHelper.getTHPAPPID(
				m_Context, HeadhunterPublic.THPKEY_WECHAT_APPID));
		
		m_IWXAPI.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq req) {
		// TODO Auto-generated method stub
		switch(req.getType()){
		
		}
		
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
        int result = 0;
        switch (resp.errCode) {
        case BaseResp.ErrCode.ERR_OK: // 分享成功
	        {
	        	result = R.string.msg_share_success;
	        }
	        break;
        case BaseResp.ErrCode.ERR_USER_CANCEL:// 取消分享
	        {
	        	result = R.string.msg_share_cancel;
	        }
	        break;
        case BaseResp.ErrCode.ERR_AUTH_DENIED:  // 分享失败
        default:
	        {
	        	result = R.string.msg_share_fail;
	        }
            break;
        }
        
        UIHelper.ToastMessage(WXEntryActivity.this, 
        		result);
		
		finish();
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		
		setIntent(intent);
		m_IWXAPI.handleIntent(intent, this);
	}
}

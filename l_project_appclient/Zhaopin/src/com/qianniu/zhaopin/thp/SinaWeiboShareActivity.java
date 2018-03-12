package com.qianniu.zhaopin.thp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.thp.Listener.SinaAuthListener;
import com.qianniu.zhaopin.thp.ui.ThpShareActivity;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.sso.SsoHandler;


public class SinaWeiboShareActivity extends BaseActivity{
	private Context m_Context;
	private AppContext m_appContext; //
	
	private SsoHandler m_ssoSinaHandler; // sso认证功能
	private WeiboAuth m_SinaWA;
	
	public static Oauth2AccessToken m_AccessToken;
	
	private String m_strContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.m_Context = this;
		m_appContext = (AppContext) this.getApplication();
	
		if(!initData()){
			finish();
		}
		
		m_AccessToken = AccessTokenKeeper.readAccessToken(m_Context);
		publish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case HeadhunterPublic.SINAWEIBO_FOR_RESULT: // Sina微博
			{
				if (null != m_ssoSinaHandler) {
					m_ssoSinaHandler.authorizeCallBack(requestCode, resultCode,
							data);
				}
			}
			break;
		case HeadhunterPublic.RESULT_ACTIVITY_SINAWEIBOSHARE:
			{
				finish();
			}
			break;
		default:
			break;
		}
	}

	private boolean initData(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(null != bundle){
			m_strContent = bundle.getString("content");
			return true;
		}
		
		return false;
	}
	
	private void publish(){	
		if(m_AccessToken != null && m_AccessToken.isSessionValid()){
			Intent i = new Intent(m_Context, ThpShareActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("content", m_strContent);
			bundle.putInt("sharetype", HeadhunterPublic.SHARETYPE_SINAWEIBO);
			i.putExtras(bundle);
			
			startActivityForResult(i, HeadhunterPublic.RESULT_ACTIVITY_SINAWEIBOSHARE);
		}else{
			login();
		}
	}
	
	private void login(){
        m_SinaWA = new WeiboAuth(this,
				UIHelper.getTHPAPPIDForNum(
						m_appContext, HeadhunterPublic.THPKEY_SINAWEIBO_APPID),
				HeadhunterPublic.SINAWEIBO_REDIRECT_URL,
				HeadhunterPublic.SINAWEIBO_SCOPE);

        m_ssoSinaHandler = new SsoHandler(this, m_SinaWA);
		m_ssoSinaHandler.authorize(new SinaAuthListener(this, m_handler));
	}
	
	private Handler m_handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case HeadhunterPublic.LOGINMSG_THP_SUCCESS:
				{
					m_AccessToken = AccessTokenKeeper.readAccessToken(m_Context);
					publish();	
				}
				break;
			case HeadhunterPublic.LOGINMSG_THP_CANCEL:
			case HeadhunterPublic.LOGINMSG_THP_EXCEPTION:
				{
					finish();
				}
				break;
			}
		}
	};
	
}

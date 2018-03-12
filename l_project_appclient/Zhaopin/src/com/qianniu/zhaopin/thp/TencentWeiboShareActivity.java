package com.qianniu.zhaopin.thp;

import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.thp.ui.TencentWeiboAuthorize;
import com.qianniu.zhaopin.thp.ui.ThpShareActivity;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class TencentWeiboShareActivity extends BaseActivity{
	
	private Context m_Context;
	
	private String m_strAccessToken;	// 用户访问令牌
	
	private String m_strContent;
//	private String m_strVideoUrl;
//	private String m_strPicUrl;
	
//	private int m_nPublishType;			// 发布类型
	
//	private int PUBLISHTYPE_NOPARAMETER = 0;		// 完全手动输入
//	private int PUBLISHTYPE_PARAMETER = 1;			// 带参数
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.m_Context = this;
		
		if(!initData()){
			finish();
		}
		
		if(!checkIsLogin()){
			return;
		}
		
		publish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case HeadhunterPublic.RESULT_ACTIVITY_TENCENTWEIBOLOGIN:
			{
				if(RESULT_OK == resultCode){
					publish();
				}else{
					finish();
				}
			}
			break;
		case HeadhunterPublic.RESULT_ACTIVITY_TENCENTWEIBOSHARE:
			{
				finish();
			}
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
	
	/**
	 * 检查是否已经授权
	 * @return
	 */
	private boolean checkIsLogin(){
		m_strAccessToken = Util.getSharePersistent(m_Context,
				"ACCESS_TOKEN");
		if (m_strAccessToken == null || "".equals(m_strAccessToken)) {
			login();
			return false;
		} 
		
		return true;
	}
	
	/**
	 * 授权
	 * @return
	 */
	private void login(){
		long ln_Appid = Long.valueOf(Util.getConfig().getProperty("APP_KEY"));
		String strAppSecket = Util.getConfig().getProperty("APP_KEY_SEC");
		
		AuthHelper.register(m_Context, ln_Appid, strAppSecket, new OnAuthListener() {

			// 授权失败
			@Override
			public void onAuthFail(int result, String strErr) {
				// TODO Auto-generated method stub
				Toast.makeText(m_Context, "result : " + result, 1000)
				.show();
			}

			// 授权成功
			@Override
			public void onAuthPassed(String arg0, WeiboToken token) {
				// TODO Auto-generated method stub
				Util.saveSharePersistent(m_Context, "ACCESS_TOKEN", token.accessToken);
				Util.saveSharePersistent(m_Context, "EXPIRES_IN", String.valueOf(token.expiresIn));
				Util.saveSharePersistent(m_Context, "OPEN_ID", token.openID);
//				Util.saveSharePersistent(m_Context, "OPEN_KEY", token.omasKey);
				Util.saveSharePersistent(m_Context, "REFRESH_TOKEN", "");
//				Util.saveSharePersistent(m_Context, "NAME", name);
//				Util.saveSharePersistent(m_Context, "NICK", name);
				Util.saveSharePersistent(m_Context, "CLIENT_ID", Util.getConfig().getProperty("APP_KEY"));
				Util.saveSharePersistent(m_Context, "AUTHORIZETIME",
						String.valueOf(System.currentTimeMillis() / 1000l));
				
				publish();
			}

			// 当前设备没有安装腾讯微博客户端
			@Override
			public void onWeiBoNotInstalled() {
				// TODO Auto-generated method stub
				Intent i = new Intent(m_Context, TencentWeiboAuthorize.class);
				startActivityForResult(i, HeadhunterPublic.RESULT_ACTIVITY_TENCENTWEIBOLOGIN);
			}

			// 当前设备没安装指定版本的微博客户端
			@Override
			public void onWeiboVersionMisMatch() {
				// TODO Auto-generated method stub
				Intent i = new Intent(m_Context, TencentWeiboAuthorize.class);
				startActivityForResult(i, HeadhunterPublic.RESULT_ACTIVITY_TENCENTWEIBOLOGIN);
			}
		});
		
		AuthHelper.auth(m_Context, "");
	}
	
	/**
	 * 
	 */
	public void publish(){
		Intent i = new Intent(m_Context, ThpShareActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("content", m_strContent);
		bundle.putInt("sharetype", HeadhunterPublic.SHARETYPE_TENCENTWEIBO);
		i.putExtras(bundle);
			
		startActivityForResult(i, HeadhunterPublic.RESULT_ACTIVITY_TENCENTWEIBOSHARE);
	}
	
//	public void publish(String strContent, String strVideoUrl, String strPicUrl){
//		m_nPublishType = PUBLISHTYPE_PARAMETER;
//		
//		m_strContent = strContent;
//		m_strVideoUrl = strVideoUrl;
//		m_strPicUrl = strPicUrl;
//		
//		if(!checkIsLogin()){
//			return;
//		}
//		
//		/**
//		 * 跳转到一键转播组件
//		 * 可以传递一些参数
//		 * 如content为转播内容
//		 * video_url为转播视频URL
//		 * pic_url为转播图片URL
//		 */
//		Intent i = new Intent(m_Context, ReAddActivity.class);
//		Bundle bundle = new Bundle();
//		bundle.putString("content", "Make U happy");
//		bundle.putString("video_url", "http://www.tudou.com/programs/view/b-4VQLxwoX4/");
//		bundle.putString("pic_url", "http://t2.qpic.cn/mblogpic/9c7e34358608bb61a696/2000");
////		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		i.putExtras(bundle);
//		m_Activity.startActivity(i);
//	}
}

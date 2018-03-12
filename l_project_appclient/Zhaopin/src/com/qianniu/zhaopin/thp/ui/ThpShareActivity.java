package com.qianniu.zhaopin.thp.ui;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.thp.AccessTokenKeeper;
import com.qianniu.zhaopin.thp.HttpUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.tencent.weibo.sdk.android.api.WeiboAPI;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.model.AccountModel;
import com.tencent.weibo.sdk.android.model.BaseVO;
import com.tencent.weibo.sdk.android.model.ModelResult;
import com.tencent.weibo.sdk.android.network.HttpCallback;

public class ThpShareActivity  extends BaseActivity implements HttpCallback{
	private Context m_Context;
	private AppContext m_appContext;
	
	// 控件
	private ImageButton m_btnBack;		// 返回按钮
	private ImageButton m_btnSubmit;	// 提交按钮
	
	private TextView m_tvTitle;			// 标题控件
	
	private EditText m_etContent;		// 分享的内容控件
	
	// 
	private int m_nShareType;
	
	// 
	private String m_strTitle;			// 标题内容
	private String m_strContent;		// 分享内容
	private String m_strPic;
	
	private WeiboAPI m_weiboAPI;		// 腾讯微博相关API
	
	/********************新浪微博相关***********************************/
	private Oauth2AccessToken m_SinaAccessToken;
//	private SsoHandler m_ssoSinaHandler; // sso认证功能
//	private WeiboAuth m_SinaWA;
	/*******************************************************/
	
	private ProgressDialog m_dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thpshare);
		
		m_Context = getApplicationContext();
		m_appContext = (AppContext) getApplication();
		
		if(!initData()){
			this.finish();
			return;
		}
		
		initControl();
	}

	@Override
	public void onResult(Object object) {
		// TODO Auto-generated method stub
		if (m_dialog != null && m_dialog.isShowing()) {
			m_dialog.dismiss();
		}

		if (m_dialog != null) {
			ModelResult result = (ModelResult) object;
			if (result.isExpires()) {
				Toast.makeText(ThpShareActivity.this,
						result.getError_message(), Toast.LENGTH_SHORT)
						.show();
			} else {
				if (result.isSuccess()) {
					Toast.makeText(ThpShareActivity.this, "发送成功", 4000)
							.show();
					Log.d("发送成功", object.toString());
					finish();
				} else {
					Toast.makeText(ThpShareActivity.this,
							((ModelResult) object).getError_message(), 4000)
							.show();
				}
			}

		}		
	}
	
	/**
	 * 初始化数据
	 */
	private boolean initData(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(null != bundle){
			m_strContent = bundle.getString("content");
			m_nShareType = bundle.getInt("sharetype");
		}
		
		switch(m_nShareType){
		case HeadhunterPublic.SHARETYPE_SINAWEIBO:
			{
				m_SinaAccessToken = AccessTokenKeeper.readAccessToken(m_Context);
				if(null == m_SinaAccessToken || !m_SinaAccessToken.isSessionValid()){
					return false;
				}
				
				m_strTitle = getString(R.string.str_thpshare_sinaweibo);
			}
			break;
		case HeadhunterPublic.SHARETYPE_TENCENTWEIBO:
			{
				//
				String strAccessToken = Util.getSharePersistent(getApplicationContext(),
						"ACCESS_TOKEN");
				if (strAccessToken == null || "".equals(strAccessToken)) {
					Toast.makeText(ThpShareActivity.this, "请先授权", Toast.LENGTH_SHORT)
							.show();
					return false;
				}
				
				m_strTitle = getString(R.string.str_thpshare_tencentweibo);
				// 
				AccountModel account = new AccountModel(strAccessToken);
				m_weiboAPI = new WeiboAPI(account);
			}
			break;
		default:
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		m_dialog = new ProgressDialog(ThpShareActivity.this);
		m_dialog.setMessage("正在发送请稍后......");
		
		// 标题
		m_tvTitle = (TextView)findViewById(R.id.thpshare_tv_title);
		if(null != m_strTitle){
			m_tvTitle.setText(m_strTitle);
		}
		
		// 分享内容
		m_etContent = (EditText)findViewById(R.id.thpshare_et_content);
		if(null != m_strContent){
			m_etContent.setText(m_strContent);
		}
		
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.thpshare_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED);
				finish();
			}
			
		});
		
		// 提交
		m_btnSubmit = (ImageButton)findViewById(R.id.thpshare_btn_submit);
		m_btnSubmit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (m_dialog != null && !m_dialog.isShowing()) {
					m_dialog.show();
				}
				
				switch(m_nShareType){
				case HeadhunterPublic.SHARETYPE_SINAWEIBO:
					{
						sendSinaWeibo();
					}
					break;
				case HeadhunterPublic.SHARETYPE_TENCENTWEIBO:
					{
						sendTencentWeibo();
					}
					break;
				}
			}
			
		});
	}
	
	private void sendTencentWeibo(){
		double longitude = 0d;
		double latitude = 0d;
//		if (mLocation != null) {
//			longitude = mLocation.getLongitude();
//			latitude = mLocation.getLatitude();
//		}
//		if (!frameLayout_icon.isShown()) {
			m_weiboAPI.addWeibo(m_Context, m_strContent, "json", longitude,
					latitude, 0, 0, ThpShareActivity.this, null, BaseVO.TYPE_JSON);
//		} else if (frameLayout_icon.getVisibility() == View.VISIBLE) {
//			weiboAPI.addPic(context, content, "json", longitude,
//					latitude, mBitmap, 0, 0, this, null,
//					BaseVO.TYPE_JSON);
//		}		
	}
	
	private void sendSinaWeibo(){
		String url = "";
		WeiboParameters weiboParameters = new WeiboParameters();
		weiboParameters.add("status", m_strContent);
		weiboParameters.add("access_token",
				m_SinaAccessToken.getToken());
		if (null == m_strPic) {
			// 只发文字的URL
			url = "https://api.weibo.com/2/statuses/update.json";
		} else {
			url = "https://upload.api.weibo.com/2/statuses/upload.json";
		}
		
		sendSinaWeiboThread sThread = new sendSinaWeiboThread(url, m_handlerSend, 
				m_strContent, null, m_SinaAccessToken);
		Thread thread = new Thread(sThread);
		thread.start();
	}
	
	public static class sendSinaWeiboThread implements Runnable {

		private String url = "";
		private Handler handler;
		private String content;
		private File pic;
		private Oauth2AccessToken sinaAccessToken;

		public sendSinaWeiboThread(String u, Handler handler, String content,
				File pic, Oauth2AccessToken accessToken) {
			this.url = u;
			this.handler = handler;
			this.content = content;
			this.pic = pic;
			this.sinaAccessToken = accessToken;
		}

		@Override
		public void run() {
			HttpClient httpclient = HttpUtils.getNewHttpClient();
			HttpPost httppost = new HttpPost(url);
			if (pic == null) {
				List params = new ArrayList();
				params.add(new BasicNameValuePair("access_token",
						sinaAccessToken.getToken()));
				params.add(new BasicNameValuePair("status", content));
				try {

					// 添加请求参数到请求对象
					httppost.setEntity(new UrlEncodedFormEntity(params,
							HTTP.UTF_8));
					httppost.getParams().setBooleanParameter(
							CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
					httppost.addHeader("Content-Type",
							"application/x-www-form-urlencoded");

					// 发送请求并等待响应
					HttpResponse httpResponse = httpclient.execute(httppost);
					// 若状态码为200 ok
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						// 读返回数据
						String json = EntityUtils.toString(httpResponse
								.getEntity());

						// 成功
		            	handler.sendMessage(handler.obtainMessage(0x001));
					} else {
						handler.sendMessage(handler.obtainMessage(0x002));
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("access_token",
							sinaAccessToken.getToken());
					params.put("status", content);

					// 发图片
					String BOUNDARY = "--------------et567z"; // 数据分隔线
					String MULTIPART_FORM_DATA = "Multipart/form-data";

					URL newurl = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) newurl
							.openConnection();

					conn.setDoInput(true);// 允许输入
					conn.setDoOutput(true);// 允许输出
					conn.setUseCaches(false);// 不使用Cache
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Connection", "Keep-Alive");
					conn.setRequestProperty("Charset", "UTF-8");
					conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
							+ ";boundary=" + BOUNDARY);

					StringBuilder sb = new StringBuilder();

					// 上传的表单参数部分，格式请参考文章
					for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
						sb.append("--");
						sb.append(BOUNDARY);
						sb.append("\r\n");
						sb.append("Content-Disposition: form-data; name=\""
								+ entry.getKey() + "\"\r\n\r\n");
						sb.append(entry.getValue());
						sb.append("\r\n");
					}
					// System.out.println(sb.toString());
					 DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
					 outStream.write(sb.toString().getBytes());// 发送表单字段数据

					 ///头部 及其文本 内容结束
					 //把文件读成二进制数组
					 BufferedInputStream bis=new BufferedInputStream(new FileInputStream(pic));
					 byte[] content=new byte[bis.available()];
					 int temp_length=bis.read(content);
					 if (temp_length!=content.length) {
						content=null;
						return;
					}
						//上传的文件部分，格式请参考文章
						
						StringBuilder split = new StringBuilder();
						split.append("--");
						split.append(BOUNDARY);
						split.append("\r\n");
						split.append("Content-Disposition: form-data;name=\"pic\";filename=\"temp.jpg\"\r\n");
						split.append("Content-Type: image/jpg\r\n\r\n");
						
						outStream.write(split.toString().getBytes());
						outStream.write(content, 0, content.length);
						outStream.write("\r\n".getBytes());  

						byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();//数据结束标志
						outStream.write(end_data);
						outStream.flush();
						int result_code = conn.getResponseCode();
					 if (result_code==200) {
						handler.sendMessage(handler.obtainMessage(0x001));
					}
					 else {
						handler.sendMessage(handler.obtainMessage(0x002));
					}
				} catch (IOException e) {
					handler.sendMessage(handler.obtainMessage(0x002));
				}
			}

		}

	}
	
	private Handler m_handlerSend = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (m_dialog != null && m_dialog.isShowing()) {
				m_dialog.dismiss();
			}
			
			switch(msg.what){
			case 0x001:
				{
					Toast.makeText(ThpShareActivity.this, "微博发布成功！", 2000).show();
					setResult(RESULT_OK);
					finish();
				}
				break;
			case 0x002:
			default:
				{
					Toast.makeText(ThpShareActivity.this, "微博发布失败，请检查网络！", 2000).show();
				}
				break;
			}
		}
	};
}

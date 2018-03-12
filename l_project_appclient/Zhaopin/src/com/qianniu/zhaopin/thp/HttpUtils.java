package com.qianniu.zhaopin.thp;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.security.KeyStore;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HttpUtils {
	public static final int HTTP_SUCCESS = 0X001;
	public static final int HTTP_FAIL = 0X002;

	// 根据gson字符串 得到 实体
	public static Object getEntity(String json, Object object) {
		Gson gson = new Gson();
		object = gson.fromJson(json, object.getClass());
		return object;
	}

	// 根据json 得到 一个集合
	public static List getList(String json, Type listType) {
		// new TypeToken<List<Object>>(){}.getType()
		Gson gson = new Gson();
		List list = gson.fromJson(json, listType);
		return list;
	}

	// 处理出来结果是 JSON 对象
	public static class HttpJSONObjectThread implements Runnable {
		private String url;
		private Handler handler;

		public HttpJSONObjectThread(String url, Handler handler) {
			this.url = url;
			this.handler = handler;

		}

		@Override
		public void run() {
			Message message = new Message();
			try {
				HttpGet post = new HttpGet(URI.create(url));
				// HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = getNewHttpClient().execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {

					String temp = EntityUtils.toString(response.getEntity());

					message.what = HTTP_SUCCESS;
					Bundle bundle = new Bundle();
					bundle.putString("json", temp);
					message.setData(bundle);

				} else {

					message.what = HTTP_FAIL;
				}
				handler.sendMessage(message);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	// 处理出来结果是 JSON 对象
	public static class HttpPostJSONObjectThread implements Runnable {
		private String url;
		private Handler handler;

		public HttpPostJSONObjectThread(String url, Handler handler) {
			this.url = url;
			this.handler = handler;

		}

		@Override
		public void run() {
			Message message = new Message();
			try {
				HttpRequest request;
				HttpPost post = new HttpPost(URI.create(url));

				// HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = getNewHttpClient().execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {

					String temp = EntityUtils.toString(response.getEntity());

					message.what = HTTP_SUCCESS;
					Bundle bundle = new Bundle();
					bundle.putString("json", temp);
					message.setData(bundle);

				} else {
					int temp = response.getStatusLine().getStatusCode();
					message.what = HTTP_FAIL;
				}
				handler.sendMessage(message);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	// 老土的post
	public static class HttpPostThread implements Runnable {
		private String url = "";
		private Handler handler;
		private List params;

		public HttpPostThread(String u, Handler handler,List list) {
			this.url = u;
			this.handler = handler;
			params = list;
		}

		@Override
		public void run() {
			HttpClient httpclient = HttpUtils.getNewHttpClient();
			HttpPost httppost = new HttpPost(url);
			try {

				// 添加请求参数到请求对象
				httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				httppost.getParams().setBooleanParameter(
						CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
				httppost.addHeader("Content-Type",
						"application/x-www-form-urlencoded");
				// 发送请求并等待响应
				HttpResponse httpResponse = httpclient.execute(httppost);
				Message msg = new Message();
				// 若状态码为200 ok
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					// 读返回数据
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					Bundle bundle=new Bundle();
					bundle.putString("json", json);
					msg.setData(bundle);
					msg.what = 0x001;
				} else {
					msg.what = 0x002;
				}
				handler.sendMessage(msg);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	// 获取加了SSL的 httpclient
	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
}

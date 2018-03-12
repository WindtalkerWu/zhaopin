package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

public class CompanyInfoAcitivity extends BaseActivity{
	
	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageButton m_btnBack;			// 返回按钮
	private ImageButton m_btnCollection;	// 收藏按钮
	
	private String m_strCompanyUrl;			// 公司url
	private String m_strCompanyId;			// 公司id
	
	private String m_strCompanyCollection;	 // 公司收藏状态
	private String m_strOldCollectionStatus; // 公司收藏最初的状态
	private int m_nCollectionStatus;		 // 将要设置的收藏状态(发送给服务器的状态)
	
	private WebView m_wvCompanyInfo;
	
	private boolean m_bLoadpageError = false;
	
	private boolean m_bIsLogin = false; // 判断是否有登录操作
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_companyinfo);
		
		m_Context = this;
		m_appContext = (AppContext) getApplication();
		
		initData();
		init();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if( HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode){
			switch(resultCode){
			case RESULT_OK:		// 登录成功
				{
					m_bIsLogin = true;
					// 获取收藏状态
					getCollectionStatus();
				}
				break;
			default:
				break;
			}
		}
	}
	
	private void initData(){
		m_strCompanyCollection = HeadhunterPublic.COMPANY_NOCOLLECTION_FLAG;
		m_strOldCollectionStatus = HeadhunterPublic.COMPANY_NOCOLLECTION_FLAG;
		
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_strCompanyId = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYID);
			m_strCompanyUrl = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYURL);
//			m_strCompanyCollection = bundle.getString(HeadhunterPublic.
//					REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG);
			
//			m_strOldCollectionStatus = m_strCompanyCollection;
		}
		
//		if(m_strCompanyCollection.equals(HeadhunterPublic.COMPANY_NOCOLLECTION_FLAG)){
//			m_nCollectionStatus = HeadhunterPublic.COMPANYINFO_STATUS_COLLECTION;
//		}else{
//			m_nCollectionStatus = HeadhunterPublic.COMPANYINFO_STATUS_CANCELCOLLECTION;
//		}
	}
	
	/**
	 * 初始化按钮
	 */
	private void initBtn(){
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.companyinfo_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishCompanyInfo();
			}
		});
		initProgressBar(R.id.companyinfo_tv_head_progress);
//		progressBar = (ProgressBar) findViewById(R.id.companyinfo_tv_head_progress);
		// 收藏
		m_btnCollection = (ImageButton)findViewById(R.id.companyinfo_btn_collection);
		m_btnCollection.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkISLogin()){
					collection();
				}
			}
		});
	}
	
	/**
	 * 初始化webview
	 */
	private void initwebView(){
		m_wvCompanyInfo = (WebView)findViewById(R.id.rewardinfo_webview);
		
		if(null == m_strCompanyUrl || m_strCompanyUrl.isEmpty()){
			m_strCompanyUrl = ConfigOptions.getWebSite();
			m_wvCompanyInfo.loadUrl(m_strCompanyUrl);			
		}else{
			m_wvCompanyInfo.loadUrl(m_strCompanyUrl);
		}
		
//		m_wvCompanyInfo.setWebViewClient(new WebViewClient(){
//			@Override
//			public void onPageStarted(WebView view, String url, Bitmap favicon) {
//				showProgressBar();
//				super.onPageStarted(view, url, favicon);
//			}
//
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				goneProgressBar();
//				super.onPageFinished(view, url);
//			}
//
//			@Override
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				// TODO Auto-generated method stub
//				return super.shouldOverrideUrlLoading(view, url);
////				view.loadUrl(m_strCompanyUrl);             
////                return true;
//			}
//			
//           @Override  
//           public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {  
//               m_bLoadpageError = true;
//               super.onReceivedError(view, errorCode, description, failingUrl);  
//     
//           }  
//		});
		
		// JavaScript使能(如果要加载的页面中有JS代码，则必须使能JS)
		WebSettings webSettings = m_wvCompanyInfo.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setPluginState(WebSettings.PluginState.ON);
		m_wvCompanyInfo.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		
		// 添加js交互接口类，并起别名 buttonlistner  
		m_wvCompanyInfo.addJavascriptInterface(new JavascriptInterface(this), "buttonlistner");  
		m_wvCompanyInfo.setWebViewClient(new MyWebViewClient());  
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		initBtn();
		initwebView();
	}
	
    /**
     * js通信接口  
     *
     */
    public class JavascriptInterface {  
  
        private Context context;  
  
        public JavascriptInterface(Context context) {  
            this.context = context;  
        }  
  
//        public void openCompanyInfoAcitivity(String strUrl){  
//        	
//        	if(strUrl.isEmpty()){
//        		return;
//        	}
//  
//        	m_strCompanyUrl = strUrl;
//        	
//			// 数据传输
//			Bundle bundle = new Bundle();
//			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYURL, m_strCompanyUrl);
//			
//			// 进入公司详情界面
//	        Intent intent = new Intent();
//	        intent.setClass(m_Context, CompanyInfoAcitivity.class);
//	        intent.putExtras(bundle);
//	        startActivity(intent);
//        }
        
        @android.webkit.JavascriptInterface
        public void openCompanyMapAcitivity(String strUrl){  
        	
        	if(strUrl.isEmpty()){
        		return;
        	}
        	
			// 数据传输
			Bundle bundle = new Bundle();
			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYMAPURL, strUrl);
			
			// 进入地图界面
	        Intent intent = new Intent();
	        intent.setClass(m_Context, MapActivity.class);
	        intent.putExtras(bundle);
	        startActivity(intent);      	
        }
    } 
    
    /**
     * 监听
     *
     */
    private class MyWebViewClient extends WebViewClient{
    	   @Override  
           public boolean shouldOverrideUrlLoading(WebView view, String url) {  
     
               return super.shouldOverrideUrlLoading(view, url);  
           }  
     
           @Override  
           public void onPageFinished(WebView view, String url) {  
     
               view.getSettings().setJavaScriptEnabled(true); 
               goneProgressBar();  
               super.onPageFinished(view, url); 
               
               if(!m_bLoadpageError){
            	   if(m_appContext.isLogin()){
            		   // 获取公司收藏状态
                	   getCollectionStatus();          		   
            	   }else{
            		   m_btnCollection.setVisibility(View.VISIBLE); 
            	   }
               }
           }  
     
           @Override  
           public void onPageStarted(WebView view, String url, Bitmap favicon) {  
               view.getSettings().setJavaScriptEnabled(true); 
               showProgressBar();
               super.onPageStarted(view, url, favicon);  
           }  
     
           @Override  
           public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {  
        	   m_bLoadpageError = true;
               super.onReceivedError(view, errorCode, description, failingUrl);  
           }      	
    }
    
	/**
	 * 收藏/取消收藏
	 */
	private void collection(){
    	// 判断网络是否连接
		if(!UIHelper.isNetworkConnected(m_appContext)){
			return;
		}
		
		showProgressBar();
    	
    	new Thread(){
        	public void run(){
        		linkCollection();
        	}
        }.start();
	}
	
	/**
	 * 发送收藏/取消收藏请求
	 */
	private void linkCollection(){
		try {
			Result res = m_appContext.CompanyCollection(m_strCompanyId,
					m_nCollectionStatus);
            if( res.OK()){			
            	// 成功
            	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.COMPANYINFO_MSG_APPLYCOLLECTION_SUCCESS));
            }else{
            	if(Result.CODE_TOKEN_INVALID == res.getErrorCode() ||
            			Result.CODE_TOKEN_OVERTIME == res.getErrorCode()){
					// 重新登录
	            	m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.COMPANYINFO_MSG_APPLYCOLLECTION_LOGIN));
            	}else{
					// 失败
	            	m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.COMPANYINFO_MSG_APPLYCOLLECTION_FAIL, res.getErrorMessage()));
            	}
            }
		} catch (AppException e) {
        	e.printStackTrace();
			// 异常
        	m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.COMPANYINFO_MSG_APPLYCOLLECTION_ABNORMAL, e));
		}	
	}
	
	
	/**
	 * 
	 */
    private Handler m_handler = new Handler() {
    	public void handleMessage(Message msg){
    		switch(msg.what){
			case HeadhunterPublic.COMPANYINFO_MSG_APPLYCOLLECTION_SUCCESS:
				{// 收藏成功
					goneProgressBar();
					
					String strMsg = "";
					if(m_strCompanyCollection.equals(HeadhunterPublic.COMPANY_COLLECTION_FLAG)){
						m_btnCollection.setImageResource(R.drawable.common_button_nocollection);
						strMsg = getString(R.string.msg_setnocollection_success);
						m_strCompanyCollection = HeadhunterPublic.REWARD_NOCOLLECTION_FLAG;
						m_nCollectionStatus = HeadhunterPublic.REWARDINFO_STATUS_COLLECTION;
					}else{
						m_btnCollection.setImageResource(R.drawable.common_button_collection);
						strMsg = getString(R.string.msg_setiscollection_success);
						m_strCompanyCollection = HeadhunterPublic.REWARD_COLLECTION_FLAG;
						m_nCollectionStatus = HeadhunterPublic.REWARDINFO_STATUS_CANCELCOLLECTION;
					}
					
					UIHelper.ToastMessage(CompanyInfoAcitivity.this, strMsg);
				}
				break;
			case HeadhunterPublic.MSG_GETCOMPANYCOLLCETIONSTATU_ABNORMAL:// 获取收藏状态异常
			case HeadhunterPublic.COMPANYINFO_MSG_APPLYCOLLECTION_ABNORMAL:
				{
					goneProgressBar();
					
					((AppException)msg.obj).makeToast(CompanyInfoAcitivity.this);
				}
				break;
			case HeadhunterPublic.COMPANYINFO_MSG_APPLYCOLLECTION_FAIL:
				{// 收藏失败
					goneProgressBar();
					
					String strMsg = "";
					if(m_strCompanyCollection.equals(HeadhunterPublic.COMPANY_COLLECTION_FLAG)){
						strMsg = getString(R.string.msg_setnocollection_fail);
					}else{
						strMsg = getString(R.string.msg_setiscollection_fail);
					}
					
					UIHelper.ToastMessage(CompanyInfoAcitivity.this, strMsg);
				}
				break;
			case HeadhunterPublic.COMPANYINFO_MSG_APPLYCOLLECTION_LOGIN:
				{
					goneProgressBar();
					
					// 重新登录
					login();
				}
				break;
			case HeadhunterPublic.MSG_GETCOMPANYCOLLCETIONSTATU_SUCCESS:// 获取收藏状态成功
				{
					goneProgressBar();
					
					Result res = (Result)msg.obj;
					
					if(null == res.getJsonStr()){
						return;
					}
					
					// 收藏状态
					if(res.getJsonStr().equals(HeadhunterPublic.COMPANY_COLLECTION_FLAG)){
						m_strCompanyCollection = HeadhunterPublic.COMPANY_COLLECTION_FLAG;
						m_nCollectionStatus = HeadhunterPublic.COMPANYINFO_STATUS_CANCELCOLLECTION;
		       			m_btnCollection.setImageResource(R.drawable.common_button_collection);
					}else{
						m_strCompanyCollection = HeadhunterPublic.COMPANY_NOCOLLECTION_FLAG;
						m_nCollectionStatus = HeadhunterPublic.COMPANYINFO_STATUS_COLLECTION;
						m_btnCollection.setImageResource(R.drawable.common_button_nocollection);
					}
			
					m_strOldCollectionStatus = m_strCompanyCollection;
		       		
		           	m_btnCollection.setVisibility(View.VISIBLE); 
		           	
		           	if(m_bIsLogin){
		           		m_bIsLogin = false;
		           		collection();
		           	}
				}
				break;
			case HeadhunterPublic.MSG_GETCOMPANYCOLLCETIONSTATU_FAIL:// 获取收藏状态失败
				{
					goneProgressBar();
					
					UIHelper.ToastMessage(CompanyInfoAcitivity.this,
							getString(R.string.msg_getcompanycollectionstatu_fail));
				}
				break;
			default:
				break;
			}	    		
    	}
    };
	/**
	 * 如果未登录，弹出对话框
	 */
	private void showAlertDialog(){
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.string_alertdialog_kindlyreminder));
		dl.setMessage(getString(R.string.string_alertdialog_nologin));
		dl.setPositiveButton(getString(R.string.string_alertdialog_gologin),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
			    		// 登录
			    		login();
					}
			
			});
		dl.setNegativeButton(getString(R.string.string_alertdialog_stroll),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
			
			});
		dl.show();
	}
	
    /**
     * 登录
     */
    private void login(){
        Intent intent = new Intent();
        intent.setClass(m_Context, UserLoginActivity.class);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
    }
    
    /**
     * 判断是否登录
     * @return
     */
    private boolean checkISLogin(){
    	if(!m_appContext.isLogin()){
    		m_bIsLogin = false;
    		showAlertDialog();
    		return false;
    	}

    	return true;
    }
    
    /**
     * 结束
     */
    private void finishCompanyInfo(){
    	// 判断收藏状态是否改变
    	if(null != m_strOldCollectionStatus && null != m_strCompanyCollection){
        	if(!m_strOldCollectionStatus.equals(m_strCompanyCollection)){
    			Bundle bundle = new Bundle();
    			// 公司ID
    			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYID, m_strCompanyId);
    			// 新的收藏状态
    			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG, 
    					m_strCompanyCollection);
    			
    			setResult(HeadhunterPublic.RESULT_COMPANYINOF_OK, 
    					getIntent().putExtras(bundle));
        	}   		
    	}
    	
    	finish();
    }
    
    /**
     * 获取公司收藏状态
     */
    private void getCollectionStatus(){
    	// 判断网络是否连接
		if(!UIHelper.isNetworkConnected(m_appContext)){
			return;
		}
		
		showProgressBar();
    	
    	new Thread(){
        	public void run(){
        		linkGetCollectionStatus();
        	}
        }.start();
    }
    
    /**
     * 发送获取公司收藏状态请求
     */
    private void linkGetCollectionStatus(){
		try {
			Result res = m_appContext.getCompanyCollectionStatus(m_strCompanyId);
            if( res.OK()){			
            	// 成功
            	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.MSG_GETCOMPANYCOLLCETIONSTATU_SUCCESS, res));
            }else{
				// 失败
            	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.MSG_GETCOMPANYCOLLCETIONSTATU_FAIL, res.getErrorMessage()));
            }
		} catch (AppException e) {
        	e.printStackTrace();
			// 异常
        	m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_GETCOMPANYCOLLCETIONSTATU_ABNORMAL, e));
		}
    }
}

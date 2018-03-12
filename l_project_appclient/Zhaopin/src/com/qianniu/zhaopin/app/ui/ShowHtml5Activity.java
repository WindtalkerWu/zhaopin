package com.qianniu.zhaopin.app.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.common.UIHelper;

/**
 * 显示html5页面
 * @author wuzy
 *
 */
public class ShowHtml5Activity extends BaseActivity {
	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageButton m_btnBack;			// 返回按钮
	private ImageButton m_btnShare;			// 分享按钮
	
	private TextView m_tvTitle;				// 标题控件
	
	private WebView m_wvHtml5;
	
	private int m_nType;					// 界面类型
	private String m_strUrl;
	private String m_strTitel;				// 标题字符串
	
	// 加载html5页面是否成功标志
	private boolean m_bLoadpageError = false;
	// 传递过来的html5 url异常标志
	private boolean m_bUrlAbnormal = false;
	
	// 界面类型
	public static final int SHOWHTML5_TYPE_RESUME = 0;	// 简历预览
	
	// 数据传输
	public static final String SHOWHTML5_DATATRANSFER_TYPE = "showhtml5_type";		// 界面类型
	public static final String SHOWHTML5_DATATRANSFER_TITLE = "showhtml5_title";	// 标题
	public static final String SHOWHTML5_DATATRANSFER_URL = "showhtml5_url";		// 要显示的html5页面的url
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showhtml5);
		
		ininData();
		initControl();
	}
	
	/**
     * 初始化数据
     */
    private void ininData(){
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_nType = bundle.getInt(SHOWHTML5_DATATRANSFER_TYPE);
			m_strUrl = bundle.getString(SHOWHTML5_DATATRANSFER_URL);
			m_strTitel = bundle.getString(SHOWHTML5_DATATRANSFER_TITLE);
		}
    }
    
	/**
	 * 初始化控件
	 */
    private void initControl(){
		// 设置标题
		m_tvTitle = (TextView)findViewById(R.id.showhtml5_tv_title);
		if(null != m_strTitel && !m_strTitel.isEmpty()){
			m_tvTitle.setText(m_strTitel);
		}
		
		// 初始化按钮
		initBtn();
		// 初始化webView
    	initwebView();
    }
    
	/**
	 * 初始化按钮
	 */
	private void initBtn(){
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.showhtml5_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 分享
		m_btnShare = (ImageButton)findViewById(R.id.showhtml5_btn_share);
		m_btnShare.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(m_nType){
				case SHOWHTML5_TYPE_RESUME:
					{
						Intent data = new Intent(Intent.ACTION_SENDTO);
						data.setData(Uri.parse("mailto:"));

						data.putExtra(Intent.EXTRA_SUBJECT, "我在牵牛网分享简历给您");
						data.putExtra(Intent.EXTRA_TEXT,
								"我在牵牛网分享简历给您,如果您对我的简历感兴趣，可以随时打电话联系。 \n" + m_strUrl);
						try {
							startActivity(data);
						} catch (ActivityNotFoundException  e) {
							// TODO: handle exception
							UIHelper.ToastMessage(m_Context, "没有邮箱应用，暂时无法分享！");
						}
					}
					break;
				default:
					{
						return;
					}
				}
			}
		});		
	}
	
	/**
	 * 初始化webView
	 */
	@SuppressLint("JavascriptInterface")
	private void initwebView(){
		m_wvHtml5 = (WebView)findViewById(R.id.showhtml5_webview);
		
		// JavaScript使能(如果要加载的页面中有JS代码，则必须使能JS)
		WebSettings webSettings = m_wvHtml5.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setPluginState(WebSettings.PluginState.ON);
		m_wvHtml5.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		// 添加js交互接口类，并起别名 buttonlistner  
		m_wvHtml5.addJavascriptInterface(new JavascriptInterface(this), "buttonlistner");  
		m_wvHtml5.setWebViewClient(new MyWebViewClient());

		if(null == m_strUrl || m_strUrl.isEmpty()){
			m_strUrl = ConfigOptions.getWebSite();
			m_wvHtml5.loadUrl(m_strUrl);
			m_bUrlAbnormal = true;
		}else{
			m_wvHtml5.loadUrl(m_strUrl);
			m_bUrlAbnormal = false;
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
               // html加载完成之后，添加监听按钮的点击js函数  
 //              addButtonClickListner();
               
               if(!m_bLoadpageError && !m_bUrlAbnormal){
	           		m_btnShare.setVisibility(View.VISIBLE); 
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
     * js通信接口  
     *
     */
    public class JavascriptInterface {  
  
        private Context context;  
  
        public JavascriptInterface(Context context) {  
            this.context = context;  
        }  
  
//        @android.webkit.JavascriptInterface
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
//			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYID, m_strCompanyId);
//			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYURL, m_strCompanyUrl);
////			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG,
////					m_strCompanyCollection);
//			
//			// 进入公司详情界面
//	        Intent intent = new Intent();
//	        intent.setClass(m_Context, CompanyInfoAcitivity.class);
//	        intent.putExtras(bundle);
////	        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
//	        startActivity(intent);
//        }
        
//        @android.webkit.JavascriptInterface
//        public void openCompanyMapAcitivity(String strUrl){  
//        	
//        	if(strUrl.isEmpty()){
//        		return;
//        	}
//        	
//			// 数据传输
//			Bundle bundle = new Bundle();
//			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYMAPURL, strUrl);
//			
//			// 进入地图界面
//	        Intent intent = new Intent();
//	        intent.setClass(m_Context, MapActivity.class);
//	        intent.putExtras(bundle);
//	        startActivity(intent);      	
//        }
    } 
}

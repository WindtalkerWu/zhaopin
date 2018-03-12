package com.qianniu.zhaopin.app.ui;

import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntityList;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.bean.TaskStatusInfo;
import com.qianniu.zhaopin.app.bean.TaskStatusInfoEntity;
import com.qianniu.zhaopin.app.bean.reqTaskId;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;
import com.qianniu.zhaopin.thp.WeChatShare;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RewardInfoActivity extends BaseActivity{
	
	private Context m_Context;
	private AppContext m_appContext;
	
	private UmShare m_umShare;				// 友盟分享
	
	private ImageButton m_btnBack;			// 返回按钮
	private ImageButton m_btnCollection;	// 收藏按钮
	private ImageButton m_btnShare;			// 分享按钮
	private RelativeLayout m_btnCandidate;		// 应聘按钮
//	private RelativeLayout m_btnRecommend;		// 推荐按钮
	private RelativeLayout m_btnJobRecommend;	// 我要推荐按钮
	
	private TextView m_tvTitle;				// 标题
//	private TextView m_tvCandidate;			// 应聘
	
	private WebView m_wvRewardInfo;
	private View m_vRewardInfo;
	
	private RelativeLayout m_rlCandidate;
	private RelativeLayout m_rlJobRecommend;
	private RelativeLayout m_rlButton;
	
	private String m_strTaskId;			// 悬赏任务ID
	private int m_nTaskType;			// 悬赏任务类型
	private String m_strTaskTitle;		// 悬赏任务名称
	private String m_strTaskUrl;		// 悬赏任务url
	private String m_strCompanyUrl;		// 发布悬赏任务的公司url
	private String m_strCompanyName;	// 发布悬赏任务的公司名
	private String m_strCompanyId;		// 发布悬赏任务的公司ID
	private String m_strCompanyCollection;	// 发布悬赏任务的公司是否被收藏
	private String m_strPublisherUserId;// 发布悬赏任务的用户Id
	private String m_strRequestType;	// 
	private int m_nRequestDataType;		// 请求的数据类型(个人, 公司, 混合) 
	private String m_strBonus;			// 悬赏任务金额
	private boolean m_bRead = false;	// 悬赏任务是否已读
	
	private String m_strCollectionStatus;	// 收藏状态
	private int m_nCollectionStatus;		// 将要设置的收藏状态(发送给服务器的状态)
//	private String m_strCandidateStatus;	// 应聘状态
	
//	private String m_strOldCollectionStatus; // 记录最初的收藏状态
//	private String m_strOldCandidateStatus;  // 记录最初的应聘状态
	private String m_strOldCompanyCollectionStatus; // 公司最初的收藏状态
	
	private String m_strApplyType;
	private String m_strResumeId;
	
	private int m_nApplyType;			// 接受任务类型
	private int m_nBeforeActivity;		//
	
	private boolean m_bCandidatelLock;	// 应聘请求是否进行中
	
	// 加载html5页面是否成功标志
	private boolean m_bLoadpageError = false;
	// 传递过来的html5 url异常标志
	private boolean m_bUrlAbnormal = false;
	
	
	private String m_strShareUrl;		// 悬赏任务url(分享时的url)
	
	private boolean m_bIsLogin = false; // 判断是否有登录操作
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rewardinfo);
		m_vRewardInfo = (View) findViewById(R.id.rewardinfo_layout); 
		
		m_Context = this;
		m_appContext = (AppContext) getApplication();
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
		
		ininData();
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
					// 从未登陆数据中copy数据到登陆用户的数据中
					copyDBData();
				
					switch(m_nApplyType){
					case HeadhunterPublic.REWARDINFO_APPLYTYPE_CANDIDATE:
						{
							//showPromptAlertDialog();
							showRecommendChooseAlertDialog();
						}
						break;
					case HeadhunterPublic.REWARDINFO_APPLYTYPE_RECOMMEND:
						{
							// showRewardRecommend();
							showRecommendChooseAlertDialog();
						}
						break;
					case HeadhunterPublic.REWARDINFO_APPLYTYPE_JOBRECOMMEND:
						{
							String strUserId = m_appContext.getUserId();
							if(null != m_strPublisherUserId &&
							   null != strUserId){
								if(m_strPublisherUserId.equals(strUserId)){
									m_btnJobRecommend.setBackgroundResource(R.drawable.common_button_no);
									m_btnJobRecommend.setEnabled(false);
									return;
								}
							}

							showJobRecommend();							
						}
						break;
					case HeadhunterPublic.REWARDINFO_APPLYTYPE_COLLECTION:
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
				break;
//			case HeadhunterPublic.RESULT_COMPANYINOF_OK:
//				{
//					// 获取Activity传递过来的数据
//					Bundle bundle = data.getExtras();
//					if(null != bundle){
//						// 公司ID
//						String strCompanyId = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYID);
//						if(null == strCompanyId){
//							return;
//						}
//						
//						if(strCompanyId.isEmpty()){
//							return;
//						}
//						
//						// 获取收藏状态
//						String strCollectionStatus = bundle.getString(
//								HeadhunterPublic.REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG);
//						
//						if(null == strCollectionStatus){
//							return;
//						}
//						
//						if(strCollectionStatus.isEmpty()){
//							return;
//						}
//						
//						m_strCompanyCollection = strCollectionStatus;
//					}					
//				}
//				break;
			case HeadhunterPublic.RESUME_SELECT_RESULT_OK:
				{
					// 获取Activity传递过来的简历数据
					ResumeSimpleEntity rse = (ResumeSimpleEntity) data
								.getSerializableExtra(HeadhunterPublic.RESUME_SELECT_RESULT);
					if(null != rse){
						if(null != rse.getResumeId() && !rse.getResumeId().isEmpty()){
							m_strResumeId = rse.getResumeId();
							
							candidate();
						}
					}
				}
				break;
			default:
				break;
			}
		}
		
		m_umShare.UmActivityResult(requestCode, resultCode, data);
	}

	
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	finishRewardInfo();
    	
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 友盟统计
		UmShare.UmResume(m_Context);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 友盟统计
		UmShare.UmPause(m_Context);
	}

	/**
     * 初始化数据
     */
    private void ininData(){
		m_nApplyType = HeadhunterPublic.REWARDINFO_APPLYTYPE_CANDIDATE;
		m_strCollectionStatus = HeadhunterPublic.REWARD_NOCOLLECTION_FLAG;
//		m_strCandidateStatus = HeadhunterPublic.REWARD_NOCANDIDATE_FLAG;
		m_nBeforeActivity = -1;
		
		m_strTaskId = "";
		m_nTaskType = HeadhunterPublic.REWARD_TYPE_NO;
		m_strTaskTitle = "";
		m_strTaskUrl = "";
		m_strCompanyUrl = "";
		m_strCompanyName = "";
		m_strPublisherUserId = "";
		m_strRequestType = "";
		m_nRequestDataType = 0;
		
		m_bCandidatelLock = false;

		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			RewardData rld = (RewardData)bundle.getSerializable(HeadhunterPublic.REWARD_DATATRANSFER_REWARDDATA);
			if(null != rld){
				m_strTaskId = rld.getTask_Id();
				m_nTaskType = Integer.valueOf(rld.getTask_Type());
				m_strTaskTitle = rld.getTask_Title();
				// 
				m_strTaskUrl = rld.getTask_Url();
				m_strShareUrl = m_strTaskUrl;
				// url添加过滤
				if(null != m_strTaskUrl && !m_strTaskUrl.isEmpty()){
					m_strTaskUrl = m_strTaskUrl + "?platform=1";
				}
				
				m_strCompanyUrl = rld.getCompany_Url();
				m_strCompanyName = rld.getCompany_Name();
				m_strPublisherUserId = rld.getUser_id();
				m_strRequestType = rld.getUserrequest_type();
				m_strBonus = rld.getTask_Bonus();
				
				// 获取收藏状态
				//m_strCollectionStatus = rld.getAction_3();
				// 获取是否应聘状态
				//m_strCandidateStatus = rld.getAction_1();
				
				 // 保存最初的收藏状态
				//m_strOldCollectionStatus = m_strCollectionStatus;
				 // 保存最初的应聘状态
				//m_strOldCandidateStatus = m_strCandidateStatus;	
				
				// 是否已读
				String strRead = rld.getAction_5();
				if(null != strRead){
					if(strRead.equals(HeadhunterPublic.REWARD_READ_FLAG)){
						m_bRead = true;
					}				
				}
				
				// 公司是否被收藏
				m_strCompanyCollection = rld.getM27_status();
				m_strOldCompanyCollectionStatus = m_strCompanyCollection;
				// 公司ID
				if(null == rld.getId() || rld.getId().isEmpty()){
					m_strCompanyId = rld.getCompany_Id();
				}else{
					m_strCompanyId = rld.getId();
				}
				
				//
				m_nRequestDataType = rld.getRequest_datatype();
			}
			m_nBeforeActivity = bundle.getInt(
					HeadhunterPublic.REWARD_DATATRANSFER_ACTIYITYTYPE);
		} 
		
		if(m_strCollectionStatus.equals(HeadhunterPublic.REWARD_NOCANDIDATE_FLAG)){
			m_nCollectionStatus = HeadhunterPublic.REWARDINFO_STATUS_COLLECTION;
		}else{
			m_nCollectionStatus = HeadhunterPublic.REWARDINFO_STATUS_CANCELCOLLECTION;
		}
    }
    
	/**
	 * 初始化按钮
	 */
	private void initBtn(){
		initProgressBar(R.id.rewardinfo_lp_progress);
//		progressBar = (ProgressBar) findViewById(R.id.rewardinfo_lp_progress);
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.rewardinfo_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishRewardInfo();
			}
		});
		
		// 收藏
		m_btnCollection = (ImageButton)findViewById(R.id.rewardinfo_btn_collection);
		m_btnCollection.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(m_nTaskType){
				case HeadhunterPublic.REWARD_TYPE_INTERVIEW:
				case HeadhunterPublic.REWARD_TYPE_ENTRY:
					{
						// 友盟统计--任务悬赏详情--收藏按钮
						UmShare.UmStatistics(m_Context, "RewardInfo_Reward_Collection");
					}
					break;
				case HeadhunterPublic.REWARD_TYPE_JOB:
				default:
					{
						// 友盟统计--任务职位详情--收藏按钮
						UmShare.UmStatistics(m_Context, "RewardInfo_Job_Collection");
					}
					break;
				}
				
				m_nApplyType = HeadhunterPublic.REWARDINFO_APPLYTYPE_COLLECTION;
				if(checkISLogin()){
					collection();
				}
			}
		});
		
		// 分享
		m_btnShare = (ImageButton)findViewById(R.id.rewardinfo_btn_share);
		m_btnShare.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strTitle = "";
				String strMsg = "";
				switch(m_nTaskType){
				case HeadhunterPublic.REWARD_TYPE_INTERVIEW:
				case HeadhunterPublic.REWARD_TYPE_ENTRY:
					{
						// 友盟统计--任务悬赏详情--分享按钮
						UmShare.UmStatistics(m_Context, "RewardInfo_Reward_Share");
						if(null != m_strBonus && 
								!m_strBonus.isEmpty() &&
								!m_strBonus.equals("0") &&
								!m_strBonus.equals("0.0") &&
								!m_strBonus.equals("0.00")){
							strMsg = String.format(getString(R.string.str_sharerewardinfo_recommend),
									m_strBonus, m_strShareUrl);							
						}else{
							strMsg = String.format(getString(R.string.str_sharerewardinfo_recommend_zero),
									m_strShareUrl);	
						}

					}
					break;
				case HeadhunterPublic.REWARD_TYPE_JOB:
				default:
					{
						// 友盟统计--任务职位详情--分享按钮
						UmShare.UmStatistics(m_Context, "RewardInfo_Job_Share");
						if(null != m_strBonus && 
								!m_strBonus.isEmpty() &&
								!m_strBonus.equals("0") &&
								!m_strBonus.equals("0.0") &&
								!m_strBonus.equals("0.00")){
							strMsg = String.format(getString(R.string.str_sharerewardinfo_candidate),
									m_strCompanyName, m_strTaskTitle, m_strBonus, m_strShareUrl);						
						}else{
							strMsg = String.format(getString(R.string.str_sharerewardinfo_candidate_zero),
									m_strCompanyName, m_strTaskTitle, m_strShareUrl);
						}
					}
					break;
				}
				
				//UIHelper.showShareDialog(RewardInfoActivity.this, strTitle, strMsg);
				strTitle = String.format(m_strCompanyName + "\n" + m_strTaskTitle);
				UIHelper.showShareDialog(RewardInfoActivity.this, strTitle, strMsg, m_strShareUrl);
			}
		});
		
		// 应聘(投简历)
//		m_tvCandidate = (TextView)findViewById(R.id.rewardinfo_tv_candidate);
		m_btnCandidate = (RelativeLayout)findViewById(R.id.rewardinfo_lp_btncandidate);
		m_btnCandidate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--任务职位详情--应聘按钮
				UmShare.UmStatistics(m_Context, "RewardInfo_Job_Candidate");
				m_nApplyType = HeadhunterPublic.REWARDINFO_APPLYTYPE_CANDIDATE;
				if(checkISLogin()){
//					showAskPopupWindow(v);
//					showPromptAlertDialog();
					showRecommendChooseAlertDialog();
				}
			}
		});

		// 设置接受任务(应聘)按钮状态
//		if(m_strCandidateStatus.equals(HeadhunterPublic.REWARD_CANDIDATE_FLAG)){
//			m_btnCandidate.setBackgroundResource(R.drawable.common_button_no);
//			m_btnCandidate.setEnabled(false);
//			m_tvCandidate.setText(R.string.str_rewardinfo_candidateok);
//		}
		
		// 推荐
//		m_btnRecommend = (RelativeLayout)findViewById(R.id.rewardinfo_lp_btnrecommend );
//		m_btnRecommend.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				// 友盟统计--任务职位详情--推荐按钮
//				UmShare.UmStatistics(m_Context, "RewardInfo_Job_Recommend");
//				
//				m_nApplyType = HeadhunterPublic.REWARDINFO_APPLYTYPE_RECOMMEND;
//				if(checkISLogin()){
//					// showRewardRecommend();
//					showRecommendChooseAlertDialog();
//				}
//			}
//		});
		
		// 我要赚钱/我要推荐
		m_btnJobRecommend = (RelativeLayout)findViewById(R.id.rewardinfo_lp_btnjobrecommend);
		m_btnJobRecommend.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--任务悬赏详情--我要赚钱按钮
				UmShare.UmStatistics(m_Context, "RewardInfo_Reward_JobRecommend");
				
				m_nApplyType = HeadhunterPublic.REWARDINFO_APPLYTYPE_JOBRECOMMEND;
				if(checkISLogin()){
					showJobRecommend();
				}
			}
		});
		
		// 设置我要赚钱按钮状态
		if(HeadhunterPublic.REWARD_TYPE_INTERVIEW == m_nTaskType || 
				HeadhunterPublic.REWARD_TYPE_ENTRY == m_nTaskType){
			// 判断用户是否登录
			if(m_appContext.isLogin()){
				String strUserId = m_appContext.getUserId();
				if(null != m_strPublisherUserId &&
				   null != strUserId){
					if(m_strPublisherUserId.equals(strUserId)){
						m_btnJobRecommend.setBackgroundResource(R.drawable.common_button_no);
						m_btnJobRecommend.setEnabled(false);
					}
				}
			}			
		}
	}
	
	/**
	 * 初始化webview
	 */
	@SuppressLint("JavascriptInterface")
	private void initwebView(){
		m_wvRewardInfo = (WebView)findViewById(R.id.rewardinfo_webview);
		
		// JavaScript使能(如果要加载的页面中有JS代码，则必须使能JS)
		WebSettings webSettings = m_wvRewardInfo.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setPluginState(WebSettings.PluginState.ON);
		m_wvRewardInfo.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		// 添加js交互接口类，并起别名 buttonlistner  
		m_wvRewardInfo.addJavascriptInterface(new JavascriptInterface(this), "buttonlistner");  
		m_wvRewardInfo.setWebViewClient(new MyWebViewClient());

		if(null == m_strTaskUrl || m_strTaskUrl.isEmpty()){
			m_strTaskUrl = ConfigOptions.getWebSite();
			m_wvRewardInfo.loadUrl(m_strTaskUrl);
			m_bUrlAbnormal = true;
		}else{
			m_wvRewardInfo.loadUrl(m_strTaskUrl);
			m_bUrlAbnormal = false;
		}
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		initBtn();
		initwebView();
		
		// 设置标题
		m_tvTitle = (TextView)findViewById(R.id.rewardinfo_tv_title);
		switch(m_nTaskType){
		case HeadhunterPublic.REWARD_TYPE_INTERVIEW:
		case HeadhunterPublic.REWARD_TYPE_ENTRY:
			{
				m_tvTitle.setText(R.string.str_rewardinfo_title_personal);
			}
			break;
		case HeadhunterPublic.REWARD_TYPE_JOB:
		default:
			{
				m_tvTitle.setText(R.string.str_rewardinfo_title_job);
			}
			break;
		}
		
		m_rlCandidate = (RelativeLayout)findViewById(R.id.rewardinfo_rl_candidate);
		m_rlJobRecommend = (RelativeLayout)findViewById(R.id.rewardinfo_rl_jobrecommend);
		m_rlButton = (RelativeLayout)findViewById(R.id.rewardinfo_rl_button);

		m_umShare = new UmShare();
	}
	
	/**
	 * 设置控件状态
	 * @param bBtn true显示button/false不显示button
	 */
	private void setControl(boolean bBtn){
		switch(m_nTaskType){
		case HeadhunterPublic.REWARD_TYPE_INTERVIEW:
		case HeadhunterPublic.REWARD_TYPE_ENTRY:
			{
//				if(bBtn){
					m_rlJobRecommend.setVisibility(View.VISIBLE);
					m_rlCandidate.setVisibility(View.GONE);
//				}else{
//					m_rlButton.setVisibility(View.GONE);
//				}
			}
			break;
		case HeadhunterPublic.REWARD_TYPE_JOB:
		default:
			{
//				if(bBtn){
					m_rlCandidate.setVisibility(View.VISIBLE);
					m_rlJobRecommend.setVisibility(View.GONE);
//				}else{
//					m_rlButton.setVisibility(View.GONE);
//				}
			}
			break;
		}
		
//		if(!bBtn){
//			if(null != m_btnCollection){
//				m_btnCollection.setVisibility(View.GONE);
//			}
//			
//			if(null != m_btnShare){
//				m_btnShare.setVisibility(View.GONE);
//			}
//		}
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
  
        @android.webkit.JavascriptInterface
        public void openCompanyInfoAcitivity(String strUrl){  
        	
        	if(strUrl.isEmpty()){
        		return;
        	}
  
        	m_strCompanyUrl = strUrl;
        	
			// 数据传输
			Bundle bundle = new Bundle();
			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYID, m_strCompanyId);
			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYURL, m_strCompanyUrl);
//			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG,
//					m_strCompanyCollection);
			
			// 进入公司详情界面
	        Intent intent = new Intent();
	        intent.setClass(m_Context, CompanyInfoAcitivity.class);
	        intent.putExtras(bundle);
//	        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
	        startActivity(intent);
        }
        
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
               // html加载完成之后，添加监听按钮的点击js函数  
 //              addButtonClickListner();
               
               if(!m_bLoadpageError && 
            	   HeadhunterPublic.ACTIVITY_TYPE_MYRECORD !=  m_nBeforeActivity &&
            	   !m_bUrlAbnormal){
	           		setControl(true);
	           		m_rlButton.setVisibility(View.VISIBLE);
	           		m_btnShare.setVisibility(View.VISIBLE); 
	           	   
            	   if(m_appContext.isLogin()){
            		   // 获取任务收藏状态
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
     * 注入js函数监听  
     */
//    private void addButtonClickListner() {  
//    	
//    	if(null == m_wvRewardInfo){
//    		return;
//    	}
//    	
//    	MyLogger.i("测试", "addButtonClickListner");
//    	
//        //在按钮点击的时候调用本地java接口 
//    	m_wvRewardInfo.loadUrl("javascript:(function(){var obj=document.getElementById(\'CompanyInfo\');" 
//        + "obj.onclick=function()" +   
//        "{alert('you click me');window.buttonlistner.openCompanyInfoAcitivity();return false;}})()");  
//    } 
    
    /**
     * 判断是否登录
     * @return
     */
    private boolean checkISLogin(){
    	if(!m_appContext.isLogin()){
 //   		if(HeadhunterPublic.REWARDINFO_APPLYTYPE_JOBRECOMMEND == m_nApplyType){
    			showAlertDialog();
//    		}else{
//	    		// 登录
//	    		login();
//    		}
    		return false;
    	}

    	return true;
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
     * 获取个人默认简历id
     * @return
     */
    private boolean getResumeId(){
    
		ResumeSimpleEntityList rsel = new ResumeSimpleEntityList();
		// 从数据库中读取简历列表
		try {
			rsel = m_appContext.getSimpleResumeListFromDb(m_appContext);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<ResumeSimpleEntity> lsRSE = rsel.getEntitylist();
		if(lsRSE.size() > 0){
			return getDefaulResumeInfo(rsel);	
		}else{
			// 从服务器中获取
			GetResumeList();
		}
		
    	return false;
    }
    
    /**
     * 获取默认简历id
     * @param rsel
     * @return
     */
    private boolean getDefaulResumeInfo(ResumeSimpleEntityList rsel){
    	if(null == rsel){
    		return false;
    	}
    	
    	if(rsel.getEntitylist().size() <= 0){
//			UIHelper.ToastMessage(RewardInfoActivity.this, 
//					R.string.msg_rewardinfo_no_resume);
    		showPromptWindows(m_vRewardInfo);

    		return false;
    	}
 
		ResumeSimpleEntity res = new ResumeSimpleEntity();
		try {
			res = m_appContext.getDefaultResumeInfo(rsel);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(null != res){
			String strResumeId = res.getResumeId();
			if(null != strResumeId){
				if(!strResumeId.isEmpty()){
					m_strResumeId = res.getResumeId();
					return true;
				}
			}
			
//			UIHelper.ToastMessage(RewardInfoActivity.this, 
//					R.string.msg_rewardinfo_no_resume);
			showPromptWindows(m_vRewardInfo);
		}
		
    	return false;
    }
    
	/**
	 * 显示应聘询问界面
	 * @param v
	 */
//	private void showAskPopupWindow(View v) {
//		if (m_cppw == null) {
//			String strTitle = m_Context.getString(R.string.str_candidatesprompt_title);
//			String strContent = String.format(m_Context.getString(R.string.str_candidatesprompt_msg1), m_strCompanyName) +
//					String.format(m_Context.getString(R.string.str_candidatesprompt_msg2), m_strTaskTitle);
//			
//			m_cppw = new AskPopupWindow(m_Context, strTitle, strContent);
//			m_cppw.setListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					switch (v.getId()) {
//					case R.id.popupwindow_ask_img_ok:
//						{
//							hideAskPopupWindow();
//							// 确认要应聘
//							m_nApplyType = HeadhunterPublic.REWARDINFO_APPLYTYPE_CANDIDATE;
//							if(checkISLogin()){
//								if(getResumeId()){
//									candidate();
//								}
//							}
//						}
//						break;
//					case R.id.popupwindow_ask_img_cancel:
//						{
//							hideAskPopupWindow();
//						}
//						break;
//					default:
//						break;
//					}
//				}
//			});
//		}
//		
//		m_cppw.showAtLocation(v, Gravity.TOP, 0, 0);
//	}
	
	/**
	 * 关闭提示框
	 */
//	private void hideAskPopupWindow() {
//		if (m_cppw != null) {
//			m_cppw.dismiss();
//		}
//	}
	
    /**
     * 请求获取用户的简历list
     */
    private void linkGetResumeList(){
		try {
			ResumeSimpleEntityList rsel = m_appContext.
					getResumeSimpleEntityListFromNet(m_appContext, true);
			
			if (rsel != null) {
				// 成功
            	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.REWARDINFO_MSG_GETRESUMELIST_SUCCESS, rsel));
			} else {
				// 失败
            	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.REWARDINFO_MSG_GETRESUMELIST_FAIL));
			}
		} catch (AppException e) {
        	e.printStackTrace();

			// 异常
        	m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.REWARDINFO_MSG_GETRESUMELIST_ABNORMAL, e));
		}     	
    }
	
	/**
	 * 发送接受任务(应聘)请求
	 */
	private void linkCandidate(){
		try {
			Result res = m_appContext.applyReward(m_strTaskId, String.valueOf(HeadhunterPublic.APPLYTASK_SENDTYPE_CANDIDATE),
					m_strResumeId);
            if( res.OK()){
            	// 成功
            	m_handler.sendMessage(m_handler.obtainMessage(
        				HeadhunterPublic.REWARDINFO_MSG_APPLYTASK_SUCCESS));
            }else{
            	if(Result.CODE_TOKEN_INVALID == res.getErrorCode() ||
            			Result.CODE_TOKEN_OVERTIME == res.getErrorCode()){
					// 重新登录
                	m_handler.sendMessage(m_handler.obtainMessage(
            				HeadhunterPublic.REWARDINFO_MSG_APPLYTASK_LOGIN));
            	}else{
            		// 失败
                	m_handler.sendMessage(m_handler.obtainMessage(
            				HeadhunterPublic.REWARDINFO_MSG_APPLYTASK_FAIL, res.getErrorMessage()));
	            }          
            }
		} catch (AppException e) {
        	e.printStackTrace();
        	// 异常
        	m_handler.sendMessage(m_handler.obtainMessage(
    				HeadhunterPublic.REWARDINFO_MSG_APPLYTASK_ABNORMAL, e));
		}
	}
	
	/**
	 * 发送收藏/取消收藏请求
	 */
	private void linkCollection(){
		try {
			Result res = m_appContext.applyCollection(m_strTaskId,
					m_nCollectionStatus);
            if( res.OK()){			
            	// 成功
            	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.REWARDINFO_MSG_APPLYCOLLECTION_SUCCESS));
            }else{
            	if(Result.CODE_TOKEN_INVALID == res.getErrorCode() ||
            			Result.CODE_TOKEN_OVERTIME == res.getErrorCode()){
					// 重新登录
	            	m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.REWARDINFO_MSG_APPLYCOLLECTION_LOGIN));
            	}else{
					// 失败
	            	m_handler.sendMessage(m_handler.obtainMessage(
							HeadhunterPublic.REWARDINFO_MSG_APPLYCOLLECTION_FAIL, res.getErrorMessage()));
            	}
            }
		} catch (AppException e) {
        	e.printStackTrace();
			// 异常
        	m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.REWARDINFO_MSG_APPLYCOLLECTION_ABNORMAL, e));
		}	
	}
	
	/**
	 * 接受任务(应聘)
	 */
	private void candidate(){
    	// 判断网络是否连接
		if(!UIHelper.isNetworkConnected(m_appContext)){
			return;
		}

		showProgressDialog();
    	
    	m_bCandidatelLock = true;
    	
    	new Thread(){
        	public void run(){
        		linkCandidate();
        	}
        }.start();
	}

	/**
	 * 从服务器上获取用户的简历list
	 */
	private void GetResumeList(){
    	// 判断网络是否连接
		if(!UIHelper.isNetworkConnected(m_appContext)){
			return;
		}
		
    	showProgressBar();
    	new Thread(){
        	public void run(){
        		linkGetResumeList();
        	}
        }.start();		
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
	 * 
	 */
    private Handler m_handler = new Handler() {
    	public void handleMessage(Message msg){
    		switch(msg.what){
//			case HeadhunterPublic.REWARDINFO_MSG_APPLYTASK_SUCCESS:
//				{// 应聘成功
//					dismissProgressDialog();
//					
//					m_btnCandidate.setBackgroundResource(R.drawable.common_button_no);
//					m_btnCandidate.setEnabled(false);
//					m_tvCandidate.setText(R.string.str_rewardinfo_candidateok);
//
//					UIHelper.ToastMessage(RewardInfoActivity.this, 
//							R.string.msg_rewardinfo_candidate_success);
//					
//					// 设置应聘状态为成功
//					m_strCandidateStatus = HeadhunterPublic.REWARD_CANDIDATE_FLAG;
//					
//					m_bCandidatelLock = false;
//				}
//				break;
//			case HeadhunterPublic.REWARDINFO_MSG_APPLYTASK_ABNORMAL:
//				{
//					dismissProgressDialog();
//					((AppException)msg.obj).makeToast(RewardInfoActivity.this);
//				}
//				break;
//			case HeadhunterPublic.REWARDINFO_MSG_APPLYTASK_FAIL:
//				{// 应聘失败
//					dismissProgressDialog();
//					
//					m_bCandidatelLock = false;
//					
//					UIHelper.ToastMessage(RewardInfoActivity.this, 
//							R.string.msg_rewardinfo_candidate_fail);
//				}
//				break;
			case HeadhunterPublic.REWARDINFO_MSG_APPLYCOLLECTION_SUCCESS:
				{// 收藏成功
					goneProgressBar();
					
					String strMsg = "";
					if(m_strCollectionStatus.equals(HeadhunterPublic.REWARD_COLLECTION_FLAG)){
						m_btnCollection.setImageResource(R.drawable.common_button_nocollection);
						strMsg = getString(R.string.msg_setnocollection_success);
						m_strCollectionStatus = HeadhunterPublic.REWARD_NOCOLLECTION_FLAG;
						m_nCollectionStatus = HeadhunterPublic.REWARDINFO_STATUS_COLLECTION;
					}else{
						m_btnCollection.setImageResource(R.drawable.common_button_collection);
						strMsg = getString(R.string.msg_setiscollection_success);
						m_strCollectionStatus = HeadhunterPublic.REWARD_COLLECTION_FLAG;
						m_nCollectionStatus = HeadhunterPublic.REWARDINFO_STATUS_CANCELCOLLECTION;
					}
					
					UIHelper.ToastMessage(RewardInfoActivity.this, strMsg);
				}
				break;
			case HeadhunterPublic.REWARDINFO_MSG_APPLYCOLLECTION_ABNORMAL:
				{
					goneProgressBar();
					((AppException)msg.obj).makeToast(RewardInfoActivity.this);
				}
				break;
			case HeadhunterPublic.REWARDINFO_MSG_APPLYCOLLECTION_FAIL:
				{// 收藏失败
					goneProgressBar();
					
					String strMsg = "";
					if(m_strCollectionStatus.equals(HeadhunterPublic.REWARD_COLLECTION_FLAG)){
						strMsg = getString(R.string.msg_setnocollection_fail);
					}else{
						strMsg = getString(R.string.msg_setiscollection_fail);
					}
					
					UIHelper.ToastMessage(RewardInfoActivity.this, strMsg);
				}
				break;
			case HeadhunterPublic.REWARDINFO_MSG_GETRESUMELIST_SUCCESS:
				{// 获取用户的简历list成功
					goneProgressBar();
					
					ResumeSimpleEntityList rsel = (ResumeSimpleEntityList)msg.obj;

					if(getDefaulResumeInfo(rsel)){
						candidate();
					}
				}
				break;
			case HeadhunterPublic.REWARDINFO_MSG_GETRESUMELIST_ABNORMAL:
				{
					goneProgressBar();
					((AppException)msg.obj).makeToast(RewardInfoActivity.this);
				}
				break;
			case HeadhunterPublic.REWARDINFO_MSG_GETRESUMELIST_FAIL:
				{// 获取用户的简历list失败
					goneProgressBar();
					
					UIHelper.ToastMessage(RewardInfoActivity.this, 
							R.string.msg_rewardinfo_getresume_fail);
				}
				break;
			case  HeadhunterPublic.REWARDINFO_MSG_CANDIDATEOK:
				{// 确认要应聘
					m_nApplyType = HeadhunterPublic.REWARDINFO_APPLYTYPE_CANDIDATE;
					if(checkISLogin()){
						if(getResumeId()){
							candidate();
						}
					}
				}
				break;
			case HeadhunterPublic.REWARDINFO_MSG_APPLYTASK_LOGIN:
			case HeadhunterPublic.REWARDINFO_MSG_APPLYCOLLECTION_LOGIN:
				{
					goneProgressBar();
					
					// 重新登录
					login();
				}
				break;

			case HeadhunterPublic.MSG_GETTASKCOLLCETIONSTATU_SUCCESS:// 获取收藏状态成功
				{
					goneProgressBar();
					
					List<TaskStatusInfo> lsTSI = (List<TaskStatusInfo>)msg.obj;
					
					if(null == lsTSI || lsTSI.size() <= 0){
						return;
					}
					
					TaskStatusInfo tsi = lsTSI.get(0);
					if(null == tsi){
						return;
					}
					
					// 收藏状态
					if(tsi.getAction_3().equals(HeadhunterPublic.COMPANY_COLLECTION_FLAG)){
						m_strCompanyCollection = HeadhunterPublic.COMPANY_COLLECTION_FLAG;
						m_nCollectionStatus = HeadhunterPublic.COMPANYINFO_STATUS_CANCELCOLLECTION;
		       			m_btnCollection.setImageResource(R.drawable.common_button_collection);
					}else{
						m_strCompanyCollection = HeadhunterPublic.COMPANY_NOCOLLECTION_FLAG;
						m_nCollectionStatus = HeadhunterPublic.COMPANYINFO_STATUS_COLLECTION;
						m_btnCollection.setImageResource(R.drawable.common_button_nocollection);
					}
			
//					m_strOldCollectionStatus = m_strCompanyCollection;
		       		
		           	m_btnCollection.setVisibility(View.VISIBLE); 
		           	
		           	if(m_bIsLogin){
		           		m_bIsLogin = false;
		           		collection();
		           	}
				}
				break;
			case HeadhunterPublic.MSG_GETTASKCOLLCETIONSTATU_FAIL:// 获取收藏状态失败
				{
					goneProgressBar();
					
					UIHelper.ToastMessage(RewardInfoActivity.this,
							getString(R.string.msg_gettaskcollectionstatu_fail));
				}
				break;
			case HeadhunterPublic.MSG_GETTASKCOLLCETIONSTATU_ABNORMAL:	// 获取收藏状态异常
				{
					goneProgressBar();
					((AppException)msg.obj).makeToast(RewardInfoActivity.this);
				}
				break;
			default:
				break;
			}	    		
    	}
    };
    
    /**
     * 进入我要推荐界面
     */
    private void showJobRecommend(){
		// 数据传输
		Bundle bundle = new Bundle();
		bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID, m_strTaskId);
		
		// 进入我要赚钱界面
        Intent intent = new Intent();
        intent.setClass(m_Context, RewardRecommendActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
    }
    
    /**
     * 进入推荐界面
     */
    private void showRewardRecommend(){
		// 数据传输
		Bundle bundle = new Bundle();
		bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID, m_strTaskId);
		bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_TASKTITLE, m_strTaskTitle);
		bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYNAME, m_strCompanyName);
		

		// 进入推荐界面
        Intent intent = new Intent();
        intent.setClass(m_Context, JobRecommendActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
    }
    
    /**
     * 进入快速推荐界面
     */
    private void showQuickRecommend(){
		// 数据传输
		Bundle bundle = new Bundle();
		bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID, m_strTaskId);
		bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_TASKTITLE, m_strTaskTitle);
		bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYNAME, m_strCompanyName);
		

		// 进入快速推荐界面
        Intent intent = new Intent();
        intent.setClass(m_Context, QuickRecommendActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
    }
    
    /**
     * 结束
     */
    private void finishRewardInfo(){
    	boolean bSend = false;
    	boolean bSendRead = false;
//    	boolean bSendCollection = false;
//   	boolean bSendCandidate = false;
//    	boolean bSendCompanyCollection = false;
    	
    	// 判断收藏状态是否改变
//    	if(null != m_strOldCollectionStatus && null != m_strCollectionStatus){
//        	if(!m_strOldCollectionStatus.equals(m_strCollectionStatus)){
//        		bSend = true;
//        		bSendCollection = true;
//        	}   		
//    	}
    	
    	// 判断应聘状态是否改变
//    	if(null != m_strOldCandidateStatus && null != m_strCandidateStatus){
//        	if(!m_strOldCandidateStatus.equals(m_strCandidateStatus)){
//        		bSend = true;
//        		bSendCandidate = true;
//        	}    		
//    	}
    	
    	// 判断已读状态是否改变
    	if(!m_bLoadpageError){
    		if(!m_bRead){
    			bSend = true;
    			bSendRead = true;
    		}
    	}	
    	
    	// 判断公司收藏状态是否改变
//    	if(null != m_strOldCompanyCollectionStatus && null != m_strCompanyCollection){
//        	if(!m_strOldCompanyCollectionStatus.equals(m_strCompanyCollection)){
//        		bSend = true;
//        		bSendCompanyCollection = true;
//        	}   		
//    	}
    	
    	if(bSend){
    		// 把据传递回去	
			Bundle bundle = new Bundle();
			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID, m_strTaskId);
			
			// 应聘状态已经改变把最新的状态传递回去
//			if(bSendCandidate){
//				bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_CANDIDATEFLAG, m_strCandidateStatus);	
//			}
			
			// 收藏状态已经改变把最新的状态传递回去
//			if(bSendCollection){
//				bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COLLECTIONFLAG, m_strCollectionStatus);
//			}
			
			// 是否已读状态状态已经改变把最新的状态传递回去
			if(bSendRead){
				bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_REWARDREAD, HeadhunterPublic.REWARD_READ_FLAG);
			}
		
			// 公司收藏状态已经改变把最新的状态传递回去
//			if(bSendCompanyCollection){
//				bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYCOLLECTIONFLAG,
//						m_strCompanyCollection);
//			}
			
			setResult(HeadhunterPublic.RESULT_REWARDINOF_OK, 
					getIntent().putExtras(bundle));	
    	}
    	
    	finish();
    }
    
    /**
     * 显示提示框
     * @param view
     */
    private void showPromptWindows(View view) {
    	UIHelper.showCommonDialog(RewardInfoActivity.this, R.string.msg_rewardinfo_no_resume, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					showResumeList();
				}
				dialog.dismiss();
			}
		});
//		if (null == m_ppw) {
//			m_ppw = new PromptPopupWindow(getApplicationContext(), false);
//			m_ppw.setListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					switch (v.getId()) {
//					case R.id.prompt_title_ok:
//						hidePromptWindows();
//						showRewardList();
//						break;
//					case R.id.prompt_title_cancel:
//						hidePromptWindows();
//						break;
//					default:
//						break;
//					}
//				}
//			});
//			
//			m_ppw.initView(-1,
//					R.string.msg_rewardinfo_no_resume, true);
//		}
//		m_ppw.show(view);
	}
    
	/**
	 * 关闭提示框
	 */
//	private void hidePromptWindows() {
//		if (m_ppw != null) {
//			m_ppw.dismiss();
//		}
//	}
	
	/**
	 * 进入简历库
	 */
	private void showRewardList(){
        Intent intent = new Intent();
        intent.putExtra(HeadhunterPublic.RESUMELIST_CALLTYPE, HeadhunterPublic.RESUMELIST_CALLTYPE_REWARDINFO);
        intent.putExtra(HeadhunterPublic.REWARD_DATATRANSFER_TASKID, m_strTaskId);
        intent.putExtra(HeadhunterPublic.REWARD_DATATRANSFER_TASKTITLE, m_strTaskTitle);
        intent.putExtra(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYNAME, m_strCompanyName);
        
        intent.setClass(m_Context, ResumeListActivity.class);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 进入简历库(应聘)
	 */
	private void showCandidateRewardList(){
        Intent intent = new Intent();
        intent.putExtra(HeadhunterPublic.RESUMELIST_CALLTYPE, HeadhunterPublic.RESUMELIST_CALLTYPE_PUBLISHREWARD);
        intent.putExtra(HeadhunterPublic.REWARD_DATATRANSFER_TASKID, m_strTaskId);
        intent.putExtra(HeadhunterPublic.REWARD_DATATRANSFER_TASKTITLE, m_strTaskTitle);
        intent.putExtra(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYNAME, m_strCompanyName);
        
        intent.setClass(m_Context, ResumeListActivity.class);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);		
	}

	private void showResumeList(){
        Intent intent = new Intent();
        intent.setClass(m_Context, ResumeListActivity.class);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
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
	 * 应聘提示框
	 */
	private void showPromptAlertDialog(){
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.str_candidatesprompt_title));
		LayoutInflater inflater = (LayoutInflater) m_Context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View textView = (View) inflater.inflate(R.layout.alertdialog_prompt_twotext, null);
		TextView tvMsg = (TextView)textView.findViewById(R.id.alertdialog_prompt_twotext_tv_msg);
		
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        	tvMsg.setTextColor(m_Context.getResources().getColor(R.color.white));
        }
		
		// 消息内容
		String strMsg = String.format(m_Context.getString(R.string.str_candidatesprompt_msg),
				m_strCompanyName, m_strTaskTitle);
		tvMsg.setText(strMsg);
		
		dl.setView(textView);
		dl.setPositiveButton(getString(R.string.dialog_ok),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						// 应聘
//						if(m_bCandidatelLock){
//							candidate();
//						}else{
//							if(getResumeId()){
//								candidate();
//							}		
//						}
						// 进入简历库
						showCandidateRewardList();
					}
			
			});
		dl.setNegativeButton(getString(R.string.dialog_cancel),
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
	 * 提示框
	 */
	private void showRecommendChooseAlertDialog(){
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.string_alertdialog_recommendway));
	
		if(ConfigOptions.RECOMMEND_CONTACTS){
			String[] strItems = new String[4]; 
			strItems[0] = getString(R.string.string_alertdialog_contactsrecommend);
			strItems[1] = getString(R.string.string_alertdialog_resumerecommend);
			strItems[2] = getString(R.string.string_alertdialog_wechatfriend);
			strItems[3] = getString(R.string.string_alertdialog_wechatfriends);
			dl.setItems(strItems, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					
					switch(which){
					case 0:
						{
							// 进入快速推荐界面
							showQuickRecommend();
						}
						break;
					case 1:
						{
							// 进入推荐界面
							//showRewardRecommend();
							// 进入简历库
							showRewardList();
						}
						break;
					case 2:	// 分享微信好友
						{
							// 友盟统计--分享--微信朋友按钮
							UmShare.UmStatistics(m_Context, "Share_WeChatButton");

							//
							shareWechat(0);
						}
						break;
					case 3:	// 分享微信朋友圈
						{
							// 友盟统计--分享--微信朋友圈按钮
							UmShare.UmStatistics(m_Context, "Share_WeChatFriendCircleButton");
							
							//
							shareWechat(1);
						}
						break;
					}					
				}
				
			});
		}else{
			String[] strItems = new String[3]; 
			strItems[0] = getString(R.string.string_alertdialog_resumerecommend);
			strItems[1] = getString(R.string.string_alertdialog_wechatfriend);
			strItems[2] = getString(R.string.string_alertdialog_wechatfriends);
			dl.setItems(strItems, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					
					switch(which){
					case 0:
						{
							// 进入推荐界面
							//showRewardRecommend();
							// 进入简历库
							showRewardList();
						}
						break;
					case 1:	// 分享微信好友
						{
							// 友盟统计--分享--微信朋友按钮
							UmShare.UmStatistics(m_Context, "Share_WeChatButton");
	
							//
							shareWechat(0);
						}
						break;
					case 2:	// 分享微信朋友圈
						{
							// 友盟统计--分享--微信朋友圈按钮
							UmShare.UmStatistics(m_Context, "Share_WeChatFriendCircleButton");
							
							//
							shareWechat(1);
						}
						break;
					}					
				}
				
			});
		}
		
		dl.setNegativeButton(getString(R.string.string_alertdialog_back),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub						
						// 友盟统计--任务职位详情--(推荐方式)再想想按钮
						UmShare.UmStatistics(m_Context, "RewardInfo_ThinkAgainButton");
						
						dialog.dismiss();
					}
			
			});
		
		dl.show();
	}
	
	/**
	 * 
	 */
	private void copyDBData(){
		if(null == m_strRequestType){
			return;
		}
		
		int nRequestType = Integer.valueOf(m_strRequestType);
		List<RewardData> lsRD = RewardData.getRewardData(m_appContext,
				m_nRequestDataType, nRequestType, "", "", true);
		
		if(null != lsRD){
			if(lsRD.size() > 0){
				RewardData.deleteAllRewardData(m_appContext, m_nRequestDataType, 
						nRequestType);
				
				for(RewardData rd : lsRD){
					RewardData.saveRewardData(m_appContext, rd);
				}
			}
		}
	}
	
	/**
	 * 分享到微信好友/朋友圈
	 */
	private void shareWechat(int nWechatType){
		String strMsg = "";
		String strTitle = "";

		if(null != m_strBonus && 
				!m_strBonus.isEmpty() &&
				!m_strBonus.equals("0") &&
				!m_strBonus.equals("0.0") &&
				!m_strBonus.equals("0.00")){
			strMsg = String.format(getString(R.string.str_sharerewardinfo_candidate),
					m_strCompanyName, m_strTaskTitle, m_strBonus, m_strShareUrl);						
		}else{
			strMsg = String.format(getString(R.string.str_sharerewardinfo_candidate_zero),
					m_strCompanyName, m_strTaskTitle, m_strShareUrl);
		}
		
		strTitle = String.format(m_strCompanyName + "\n" + m_strTaskTitle);
		
		switch (nWechatType) {
		case 0: // 微信朋友
			{
				WeChatShare wcs = new WeChatShare(m_Context, getIntent());
				Bitmap bitmap = wcs.createQRCodeImage(m_strShareUrl);
				if(null != bitmap){
					wcs.ShareTextAndBmp(strTitle, m_strShareUrl, strMsg, bitmap, false);
				}else{
					wcs.ShareText(strMsg, false);
				}
			}
			break;
		case 1: // 微信朋友圈
			{
				WeChatShare wcs = new WeChatShare(m_Context, getIntent());
				
				Bitmap bitmap = wcs.createQRCodeImage(m_strShareUrl);
				if(null != bitmap){
					wcs.ShareTextAndBmp(strTitle, m_strShareUrl, strMsg, bitmap, true);
				}else{
					wcs.ShareText(strMsg, true);
				}
			}
			break;
		}
	}
	
    /**
     * 获取任务收藏状态
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
     * 发送获取任务收藏状态请求
     */
    private void linkGetCollectionStatus(){
		try {
			reqTaskId taskid = new reqTaskId();
			String[] strTaskId = new String[1];
			strTaskId[0] = m_strTaskId;
			taskid.setTaskid(strTaskId);
			
			TaskStatusInfoEntity tsie = m_appContext.getTaskCollectionStatus(taskid);
			Result res = tsie.getValidate();
			if( res.OK()){			
		    	// 成功
		    	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.MSG_GETTASKCOLLCETIONSTATU_SUCCESS, tsie.getData()));
			}else{
				// 失败
	        	m_handler.sendMessage(m_handler.obtainMessage(
						HeadhunterPublic.MSG_GETTASKCOLLCETIONSTATU_FAIL, res.getErrorMessage()));
			}
		} catch (AppException e) {
        	e.printStackTrace();
			// 异常
        	m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_GETTASKCOLLCETIONSTATU_ABNORMAL, e));
		}
    }
}

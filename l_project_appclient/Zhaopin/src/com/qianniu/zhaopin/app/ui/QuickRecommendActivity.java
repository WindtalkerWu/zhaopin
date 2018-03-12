package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.reqQuickRecommendData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.view.Util;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 快速推荐
 * @author wuzy
 *
 */
public class QuickRecommendActivity extends BaseActivity{
	private Context m_Context;
	
//	private AskPopupWindow m_askPW;					// 推荐提示框
	
	private ImageButton m_btnBack;					// 返回按钮
	private ImageButton m_btnContact;				// 通讯录
	private RelativeLayout m_btnConfirm;			// 确定
	
	private EditText m_etName;						// 名字
	private EditText m_etTel;						// 联系电话
	private EditText m_etMail;						// 邮箱
	private EditText m_etSubmit;					// 推荐理由
	private EditText m_etJobName;					// 职位名 
	
	private String m_strTaskId;						// 悬赏任务ID
	private String m_strTaskTitle;					// 悬赏任务Title(此处为职位名)
	private String m_strCompanyName;				// 公司名
	
	private String m_strName;
	private String m_strPhone;

//	private View m_vloading;
//	private AnimationDrawable m_loadingAnimation;
	
	private final int REQUEST_CONTACT = 1000;
	private final int REQUEST_SMS = 1001;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_quickrecommend);
		
		m_Context = this;
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	
		initData();
		initControl();
		
		startContact();
	}	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if( HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode){
			switch(resultCode){
			case RESULT_OK:		// 登录成功
				{
					Recommend();
				}
				break;
			default:
				break;
			}
		}else if(REQUEST_CONTACT == requestCode){
	        if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }    

                Uri result = data.getData();

                Cursor c = managedQuery(result, null, null, null, null);
                if(null != c){
                	c.moveToFirst();
                	String phoneNum = getContactPhone(this, c);
                	if(null != m_etTel && null != phoneNum){
                		m_etTel.setText(phoneNum);
                	}
                	
                	String userName = getContactName(c);
                	if(null != m_etName && null != userName){
                		m_etName.setText(userName);
                	}
                }
            }
		}
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
	private void initData(){
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_strTaskId = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID);
			m_strTaskTitle = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_TASKTITLE);
			m_strCompanyName = bundle.getString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYNAME);
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
//		m_vloading = findViewById(R.id.quickrecommend_loading);
//		m_loadingAnimation = (AnimationDrawable)m_vloading.getBackground();
		
		// 名字
		m_etName = (EditText)findViewById(R.id.quickrecommend_et_name);
		// 联系电话
		m_etTel = (EditText)findViewById(R.id.quickrecommend_et_tel);
		// 邮箱
		m_etMail = (EditText)findViewById(R.id.quickrecommend_et_mail);
		// 职位名
		m_etJobName = (EditText)findViewById(R.id.quickrecommend_et_job);
		m_etJobName.setText(m_strTaskTitle);
		m_etJobName.setEnabled(false);
		// 推荐理由
		m_etSubmit = (EditText)findViewById(R.id.quickrecommend_et_reason);
		
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.quickrecommend_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 通讯录
		m_btnContact = (ImageButton)findViewById(R.id.quickrecommend_btn_contact);
		m_btnContact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--快速推荐--通讯录
				UmShare.UmStatistics(m_Context, "QuickRecommend_Contact");
				startContact();
			}
			
		});
		
		// 确定
		m_btnConfirm = (RelativeLayout)findViewById(R.id.quickrecommend_lp_confirm);
		m_btnConfirm.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--快速推荐--确定
				UmShare.UmStatistics(m_Context, "QuickRecommend_Confirm");
				if(checkControlValue()){
					// showAskPopupWindow(v);
					showPromptAlertDialog();
				}
			}
		});
	}
	
	/**
	 * 检查控件
	 * @return
	 */
	private boolean checkControlValue(){

		String strTemp = null;
		
		// 检查名字是否为空
		m_strName = m_etName.getText().toString();
		if(m_strName.isEmpty()){
			UIHelper.ToastMessage(this, 
					getString(R.string.msg_quickrecommend_name_empty));	
			return false;
		}
		
		// 检查联系方法是否为空
		m_strPhone = m_etTel.getText().toString();
		if(m_strPhone.isEmpty()){
			UIHelper.ToastMessage(this, 
					getString(R.string.msg_quickrecommend_tel_empty));
			return false;
		}
		
		// 检查邮箱是否为空
//		strTemp = m_etMail.getText().toString();
//		if(strTemp.isEmpty()){
//			UIHelper.ToastMessage(this, 
//					getString(R.string.msg_quickrecommend_mail_empty));
//			return false;
//		}else{
//			if(!Util.checkEmail(strTemp)){
//				UIHelper.ToastMessage(this, 
//						getString(R.string.msg_quickrecommend_mail_illegal));
//				
//				return false;
//			}
//		}
		
		// 检查推荐理由是否为空
		strTemp = m_etSubmit.getText().toString();
		if(strTemp.isEmpty()){
			UIHelper.ToastMessage(this, 
					getString(R.string.msg_quickrecommend_reason_empty));
			return false;
		}
		
		return true;
	}
	
	/**
	 * 向服务器提交推荐请求
	 */
	private void linkRecommend(){
		try {
			AppContext ac = (AppContext)getApplication(); 
			
			reqQuickRecommendData reqQRD = new reqQuickRecommendData();
			reqQRD.setApply_Type(String.valueOf(HeadhunterPublic.APPLYTASK_SENDTYPE_RECOMMEND));
			reqQRD.setTask_Id(m_strTaskId);
			reqQRD.setPhone(m_etTel.getText().toString());
			reqQRD.setName(m_etName.getText().toString());
			reqQRD.setMail(m_etMail.getText().toString());
			reqQRD.setMemo(m_etSubmit.getText().toString());
			
			Result res = ac.quickRecommend(reqQRD);
            if( res.OK()){			
            	//	成功
    	    	m_handler.sendMessage(m_handler.obtainMessage(
    					HeadhunterPublic.QUICKRECOMMEND_MSG_RECOMMEND_SUCCESS));
            }else{
            	// 判断token是否失效
            	if(Result.CODE_TOKEN_INVALID == res.getErrorCode() ||
            			Result.CODE_TOKEN_OVERTIME == res.getErrorCode()){
					// 重新登录
	    	    	m_handler.sendMessage(m_handler.obtainMessage(
	    					HeadhunterPublic.QUICKRECOMMEND_MSG_QUICKRECOMMEND_LOGIN));
            	}else{
//	            	// 失败
	    	    	m_handler.sendMessage(m_handler.obtainMessage(
	    					HeadhunterPublic.QUICKRECOMMEND_MSG_RECOMMEND_FAIL, res.getErrorMessage()));
            	}
            }
		} catch (AppException e) {
        	e.printStackTrace();
			// 异常
	    	m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.QUICKRECOMMEND_MSG_RECOMMEND_ABNORMAL, e));
		}	
	}
	
	private void Recommend(){
    	showProgressDialog();
    	
		// 判断网络是否连接
		if (!UIHelper.isNetworkConnected((AppContext) this.getApplication())) {
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.QUICKRECOMMEND_MSG_NONETWORKCONNECT));
			return;
		}
    	
    	new Thread(){
        	public void run(){
        		linkRecommend();
        	}
        }.start();
	}
	
	/**
	 * 
	 */
    private Handler m_handler = new Handler() {
    	public void handleMessage(Message msg){
    		switch(msg.what){
			case HeadhunterPublic.QUICKRECOMMEND_MSG_RECOMMEND_SUCCESS:
				{
					dismissProgressDialog();
					
//					UIHelper.ToastMessage(QuickRecommendActivity.this, 
//							R.string.msg_quickrecommend_recommend_success);
//					
//					finish();
					showAlertDialog();
				}
				break;

			case HeadhunterPublic.QUICKRECOMMEND_MSG_RECOMMEND_ABNORMAL:
				{
					dismissProgressDialog();
					((AppException)msg.obj).makeToast(QuickRecommendActivity.this);
				}
				break;
			case HeadhunterPublic.QUICKRECOMMEND_MSG_RECOMMEND_FAIL:
				{
					dismissProgressDialog();
					
					UIHelper.ToastMessage(QuickRecommendActivity.this, 
							R.string.msg_quickrecommend_recommend_fail);
				}
				break;
			case HeadhunterPublic.QUICKRECOMMEND_MSG_QUICKRECOMMEND_LOGIN:
				{
					dismissProgressDialog();
					
					login();
				}
				break;
			case HeadhunterPublic.QUICKRECOMMEND_MSG_NONETWORKCONNECT:{
				;
			}
			default:
				break;
			}	    		
    	}
    };
    
    /**
     * 登录
     */
    private void login(){
        Intent intent = new Intent();
        intent.setClass(m_Context, UserLoginActivity.class);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
    }
    
    /**
     * 进入通讯录
     */
    private void startContact() {
    	Intent intent = new Intent();
    	intent.setAction(Intent.ACTION_PICK);
    	intent.setData(ContactsContract.Contacts.CONTENT_URI);
    	startActivityForResult(intent, REQUEST_CONTACT);
    }
    
    /**
     * 获取联系人电话号码
     * @param mContext
     * @param cursor
     * @return
     */
    private String getContactPhone(Context context,Cursor cursor) 
    { 
    	String strPhone = ""; 
    	
    	if(null == cursor){
    		return strPhone;
    	}
    	
    	try {
	            int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);   
	            int phoneNum = cursor.getInt(phoneColumn);  
	            if (phoneNum > 0) 
	            { 
	            	// 获得联系人的ID号 
	                int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID); 
	                String contactId = cursor.getString(idColumn); 
	                
	                // 获得联系人的电话号码的cursor; 
	                Cursor phones = context.getContentResolver().query( 
	                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
	                ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId,null, null); 
	               
	                if (phones.moveToFirst()) 
	                { 
	                        // 遍历所有的电话号码 
	                        for (;!phones.isAfterLast();phones.moveToNext()) 
	                        {                                             
	                            int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER); 
	                            int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE); 
	                            int phone_type = phones.getInt(typeindex); 
	                            String phoneNumber = phones.getString(index); 
	                            
	                            //当手机号码为空的或者为空字段 跳过当前循环  
	                            if (TextUtils.isEmpty(phoneNumber)){
	                            	continue; 
	                            }
	                                     
	                            switch (phone_type) {
	                            case 2:
	    	                        {
	    	                        	strPhone = phoneNumber;
	                                    break;	                        	
	    	                        }
	                            }  
	                        } 
	                        
	                        if (!phones.isClosed()) 
	                        { 
	                        	phones.close(); 
	                        } 
	                } 
		      }     		
    	}catch (Exception e){
    		e.printStackTrace();
    		UIHelper.ToastMessage(QuickRecommendActivity.this, 
					R.string.msg_quickrecommend_readcontact_fail);
    	}

        return strPhone; 
    }
    
    /**
     * 获取联系人名称
     * @param cursor
     * @return
     */
    private String getContactName(Cursor cursor){
    	String strName = "";
    	
    	if(null != cursor){
    		try{
    			strName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); 
    		}catch (Exception e){
        		e.printStackTrace();
        		
        		UIHelper.ToastMessage(QuickRecommendActivity.this, 
						R.string.msg_quickrecommend_readcontact_fail);
        	}
    	}
    	
    	return strName;
    }
    
    /**
     * 是否发送短信询问框
     */
    private void showAlertDialog(){
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.msg_quickrecommend_recommend_success));
		dl.setMessage(String.format(getString(R.string.msg_quickrecommend_sms), m_strName));
		dl.setPositiveButton(getString(R.string.sure),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						sendSMS();
					}
			
			});
		dl.setNegativeButton(getString(R.string.cancle),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						finish();
					}
			
			});
		dl.show();
    }
    
    /**
     * 发送短信
     */
    private void sendSMS(){
    	Uri uri = Uri.parse("smsto:"+m_strPhone);           
    	Intent it = new Intent(Intent.ACTION_SENDTO, uri);            
    	it.putExtra("sms_body", String.format(getString(R.string.msg_quickrecommend_sendsms), m_strCompanyName, m_strTaskTitle));            
    	startActivity(it);  
//    	startActivityForResult(it, REQUEST_SMS);
    }
    
	/**
	 * 显示快速推荐询问界面
	 * @param v
	 */
//	private void showAskPopupWindow(View v) {
//		if (m_askPW == null) {
//			String strTitle = m_Context.getString(R.string.str_quickrecommend_title);
//			String strContent = String.format(m_Context.getString(R.string.str_quickrecommend_ask_content), 
//					m_strName, m_strTaskTitle, m_strCompanyName);
//			
//			m_askPW = new AskPopupWindow(m_Context, strTitle, strContent);
//			m_askPW.setListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					switch (v.getId()) {
//					case R.id.popupwindow_ask_img_ok:
//						{
//							hideAskPopupWindow();
//							// 确认要快速推荐
//							Recommend();
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
//		m_askPW.showAtLocation(v, Gravity.TOP, 0, 0);
//	}
	
	/**
	 * 关闭提示框
	 */
//	private void hideAskPopupWindow() {
//		if (m_askPW != null) {
//			m_askPW.dismiss();
//		}
//	}
	
	/**
	 * 提示框
	 */
	private void showPromptAlertDialog(){
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.str_quickrecommend_title));
		LayoutInflater inflater = (LayoutInflater) m_Context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View textView = (View) inflater.inflate(R.layout.alertdialog_prompt_twotext, null);
		TextView tvMsg = (TextView)textView.findViewById(R.id.alertdialog_prompt_twotext_tv_msg);
		
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        	tvMsg.setTextColor(m_Context.getResources().getColor(R.color.white));
        }
		
		// 消息内容
		String strMsg = String.format(m_Context.getString(R.string.str_quickrecommend_ask_content), 
				m_strName, m_strCompanyName, m_strTaskTitle);
		tvMsg.setText(strMsg);
		
		dl.setView(textView);
		dl.setPositiveButton(getString(R.string.dialog_ok),
				new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						
						// 确认要快速推荐
						Recommend();
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
}

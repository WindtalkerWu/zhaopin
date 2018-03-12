package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.ReqUserInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.view.Util;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MailAuthActivity extends BaseActivity {
	private ImageButton back;
	
	private EditText mailNumber;
	private Button doAuth;
	private TextView authInfo;
	
	private Context m_Context;
	
	private ReqUserInfo userInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail_auth);

		m_Context = this;
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	
		initView();
		initUserInfo();
		setListener();
		init();
	}

	private void initUserInfo() {
		userInfo = new ReqUserInfo();
		userInfo.setUserName(((AppContext)getApplicationContext()).getAccount());
	}
	private void init() {
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			return;
		}
		new InitAsyncTask().execute();
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
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == ActivityRequestCode.RESULT_ACTIVITY_LOGIN) {
				init();
			}
		}
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.mail_auth_goback);
		mailNumber = (EditText) findViewById(R.id.mobile_mail_txt);
		authInfo = (TextView) findViewById(R.id.mobile_auth_old_email);
		doAuth = (Button) findViewById(R.id.mobile_mail_domail);
	}

	private void setListener() {
		doAuth.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计--邮箱认证--认证按钮
				UmShare.UmStatistics(m_Context, "MailAuth_DoAuthButton");
				
				String email = mailNumber.getText().toString();
				if (checkEmail(email)) {
					//如果是没有认证的状态，每次点击存到Preference
					DMSharedPreferencesUtil.putSharePre(getApplicationContext(),
							DMSharedPreferencesUtil.DM_AUTH_INFO,
							((AppContext)getApplicationContext()).getUserId() + DMSharedPreferencesUtil.emailCache, email);
					if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
						return;
					}
					showProgressDialog();
					new VerifyEmailTask().execute();
				}
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	private boolean checkEmail(String email) {
		if (TextUtils.isEmpty(email)) {
			 UIHelper.ToastMessage(getApplicationContext(), R.string.mail_auth_email_null);
			 return false;
		}
		if (email.equals(userInfo.getEmail())) {
	    	Toast.makeText(this, getResources().getString(R.string.mail_auth_email_equal),
	  	          Toast.LENGTH_SHORT).show();
	  	      return false;
	    } 
		if (!Util.checkEmail(email)) {
			UIHelper.ToastMessage(getApplicationContext(), R.string.mail_auth_email_error);
			return false;
		}
		return true;
	}
	private Result verifyEmail() {
		try {
			Result result = ApiClient.verifyEmail((AppContext)getApplicationContext(), mailNumber.getText().toString());
			return result;
		} catch (AppException e) {
			e.printStackTrace();
			final AppException ee = e;
			mailNumber.post(new Runnable() {
				@Override
				public void run() {
					ee.makeToast(getApplicationContext());
				}
			});
		}
		return null;
	}
	private Result initData() {
		try {
			Result result = ApiClient.getUserInfo((AppContext)getApplicationContext());
			return result;
		} catch (AppException e) {
			e.printStackTrace();
			final AppException ee = e;
			mailNumber.post(new Runnable() {
				@Override
				public void run() {
					ee.makeToast(getApplicationContext());
				}
			});
		}
		return null;
	}
	private class InitAsyncTask extends AsyncTask<Void, Void, Result> {

		@Override
		protected void onPreExecute() {
			showProgressDialog();
			super.onPreExecute();
		}
		@Override
		protected Result doInBackground(Void... params) {
			return initData();
		}
		@Override
		protected void onPostExecute(Result result) {
			super.onPostExecute(result);
			dismissProgressDialog();
			if (result != null) {
				if (result.OK()) {
					String resultStr = result.getJsonStr();
					userInfo = (ReqUserInfo) ObjectUtils
							.getObjectFromJsonString(resultStr, ReqUserInfo.class);
					String authFlag = userInfo.getEmail_verified();
					if (ReqUserInfo.EMAIL_VERIFIED.equals(authFlag)) {
						String email = userInfo.getEmail();
						authInfo.setVisibility(View.VISIBLE);
						authInfo.setText("已认证：" + email);
						 DMSharedPreferencesUtil.putSharePre(getApplicationContext(),
									DMSharedPreferencesUtil.DM_AUTH_INFO,
									((AppContext)getApplicationContext()).getUserId(),
									result.getJsonStr());
					} else {
						String email = DMSharedPreferencesUtil.getSharePreStr(getApplicationContext(),
								DMSharedPreferencesUtil.DM_AUTH_INFO,
								((AppContext)getApplicationContext()).getUserId() + DMSharedPreferencesUtil.emailCache);
						mailNumber.setText(email);
					}
				} else {
					result.handleErrcode(MailAuthActivity.this);
				}
			}
		}
	}
	private class VerifyEmailTask extends AsyncTask<Void, Void, Result> {

		@Override
		protected Result doInBackground(Void... params) {
			return verifyEmail();
		}
		@Override
	    protected void onPostExecute(Result result) {
	      super.onPostExecute(result);
	      dismissProgressDialog();
	      if (result != null) {
		      if (result.OK()) {
		    	  UIHelper.ToastMessage(getApplicationContext(), R.string.mail_auth_verify_success);
		    	  userInfo.setEmail_verified(ReqUserInfo.EMAIL_VERIFIED);
		    	  DMSharedPreferencesUtil.putSharePre(getApplicationContext(),
							DMSharedPreferencesUtil.DM_AUTH_INFO,
							((AppContext)getApplicationContext()).getUserId(),
							ObjectUtils.getJsonStringFromObject(userInfo));
		    	  finish();
		      } else if (result.getErrorCode() == 1949) {
		    	  UIHelper.ToastMessage(getApplicationContext(),
							R.string.mail_auth_email_verify_1949);
		      } else {
		    	  result.handleErrcode(MailAuthActivity.this);
//		    	  UIHelper.ToastMessage(getApplicationContext(), R.string.mail_auth_verify_fail);
		      }
	      }
	    }
	}
}

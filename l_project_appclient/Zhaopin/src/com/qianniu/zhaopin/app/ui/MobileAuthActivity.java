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

public class MobileAuthActivity extends BaseActivity {
	private ImageButton back;
	
	private EditText mobileNumber;
	private EditText code;
	private TextView authInfo;
	
	private Button getCode;
	private Button authMobile;
	
	private Context m_Context;
	
	private ReqUserInfo userInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mobile_auth);
		
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
		back = (ImageButton) findViewById(R.id.mobile_auth_goback);
		mobileNumber = (EditText) findViewById(R.id.mobile_auth_phone);
		code = (EditText) findViewById(R.id.mobile_auth_code);
		authInfo = (TextView) findViewById(R.id.mobile_auth_old_mobile);
		
		getCode = (Button) findViewById(R.id.mobile_auth_getcode_btn);
		authMobile = (Button) findViewById(R.id.mobile_auth_doauth);
	}
	private void setListener() {
		authMobile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计--手机认证--认证按钮
				UmShare.UmStatistics(m_Context, "MobileAuth_AuthMobileButton");
				
				String codeStr = code.getText().toString();
				if (checkCode(codeStr)) {
					if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
						return;
					}
					showProgressDialog();
					new VerifyMobileCodeTask().execute();
				}
			}
		});
		getCode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String mobileNumberStr = mobileNumber.getText().toString();
				if (checkNumber(mobileNumberStr)) {
					DMSharedPreferencesUtil.putSharePre(getApplicationContext(),
							DMSharedPreferencesUtil.DM_AUTH_INFO,
							((AppContext)getApplicationContext()).getUserId() + DMSharedPreferencesUtil.phoneCache,
							mobileNumberStr);
					if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
						return;
					}
					showProgressDialog();
					new VerifyMobileNumberTask().execute();
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
	private Result verifyMobileNumber() {
		try {
			Result result = ApiClient.verifyMobile((AppContext)getApplicationContext(),
					mobileNumber.getText().toString());
			return result;
		} catch (AppException e) {
			e.printStackTrace();
			final AppException ee = e;
			mobileNumber.post(new Runnable() {
				@Override
				public void run() {
					ee.makeToast(getApplicationContext());
				}
			});
		}
		return null;
	}
	private boolean checkNumber(String mobileNumber) {
	    if (TextUtils.isEmpty(mobileNumber)) {
	      Toast.makeText(this, getResources().getString(R.string.mobile_auth_mobile_null),
	          Toast.LENGTH_SHORT).show();
	      return false;
	    }
	    if (mobileNumber.equals(userInfo.getPhone())) {
	    	Toast.makeText(this, getResources().getString(R.string.mobile_auth_mobile_equal),
	  	          Toast.LENGTH_SHORT).show();
	  	      return false;
	    }
	    if (!Util.checkMobileFormate(mobileNumber)) {
	      Toast.makeText(this, getResources().getString(R.string.mobile_auth_mobile_error),
	          Toast.LENGTH_SHORT).show();
	      return false;
	    }
	    return true;
	  }
	private boolean checkCode(String code) {
	    if (TextUtils.isEmpty(code)) {
	      Toast.makeText(this, getResources().getString(R.string.mobile_auth_code_null),
	          Toast.LENGTH_SHORT).show();
	      return false;
	    }
	    return true;
	  }
	private Result initData() {
		try {
			Result result = ApiClient.getUserInfo((AppContext)getApplicationContext());
			return result;
		} catch (AppException e) {
			e.printStackTrace();
			final AppException ee = e;
			mobileNumber.post(new Runnable() {
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
					String authFlag = userInfo.getPhone_verified();
					if (ReqUserInfo.PHONE_VERIFIED.equals(authFlag)) { //如果认证则显示认证信息，并更新SharedPreferences数据
						String mobile = userInfo.getPhone();
						authInfo.setVisibility(View.VISIBLE);
						authInfo.setText("已认证：" + mobile);
						DMSharedPreferencesUtil.putSharePre(getApplicationContext(),
								DMSharedPreferencesUtil.DM_AUTH_INFO,
								((AppContext)getApplicationContext()).getUserId(),
								result.getJsonStr());
					} else {//如果没有认证，则从SharedPreferences中获取手机号码，并显示
						String phoneCache = DMSharedPreferencesUtil.getSharePreStr(getApplicationContext(),
								DMSharedPreferencesUtil.DM_AUTH_INFO,
								((AppContext)getApplicationContext()).getUserId() + DMSharedPreferencesUtil.phoneCache);
						mobileNumber.setText(phoneCache);
					}
				} else {
					result.handleErrcode(MobileAuthActivity.this);
				}
			}
		}
	}
	private class VerifyMobileNumberTask extends AsyncTask<Void, Void, Result> {

		@Override
		protected Result doInBackground(Void... params) {
			return verifyMobileNumber();
		}
		@Override
	    protected void onPostExecute(Result result) {
	      super.onPostExecute(result);
	      dismissProgressDialog();
	      if (result != null) {
		      if (result.OK()) {
		    	  UIHelper.ToastMessage(getApplicationContext(), R.string.mobile_auth_getcode_success);
		      } else if (result.getErrorCode() == 1949) {
		    	  UIHelper.ToastMessage(getApplicationContext(), R.string.mobile_auth_getcode_1949);
		      } else {
		    	  result.handleErrcode(MobileAuthActivity.this);
//		    	  UIHelper.ToastMessage(getApplicationContext(), R.string.mobile_auth_getcode_fail);
		      }  
	      }
	    }
	}
	private Result verifyMobileCode() {
		try {
			Result result = ApiClient.verifyMobileCode((AppContext)getApplicationContext(),
					code.getText().toString());
			return result;
		} catch (AppException e) {
			e.printStackTrace();
			final AppException ee = e;
			code.post(new Runnable() {
				@Override
				public void run() {
					ee.makeToast(getApplicationContext());
				}
			});
		}
		return null;
	}
	private class VerifyMobileCodeTask extends AsyncTask<Void, Void, Result> {

		@Override
		protected Result doInBackground(Void... params) {
			return verifyMobileCode();
		}
		@Override
	    protected void onPostExecute(Result result) {
	      super.onPostExecute(result);
	      dismissProgressDialog();
	      if (result != null) {
		      if (result.OK()) {
		    	  UIHelper.ToastMessage(getApplicationContext(), R.string.mobile_auth_verifycode_success);
		    	  //如果认证成功，则更新SharedPreferences数据
		    	  userInfo.setPhone_verified(ReqUserInfo.PHONE_VERIFIED);
		    	  DMSharedPreferencesUtil.putSharePre(getApplicationContext(),
							DMSharedPreferencesUtil.DM_AUTH_INFO,
							((AppContext)getApplicationContext()).getUserId(),
							ObjectUtils.getJsonStringFromObject(userInfo));
		    	  setResult(RESULT_OK);
		    	  finish();
		      } else if (result.getErrorCode() == Result.CODE_VERIFY_CODE_NOT_SAME) {
		    	  UIHelper.ToastMessage(getApplicationContext(), R.string.mobile_auth_verifycode_code_error);
		      } else {
		    	  result.handleErrcode(MobileAuthActivity.this);
		      }
	      }
	    }
	}
}

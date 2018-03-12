package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.ModifyPwdInfo;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ModifyPwdActivity extends BaseActivity {
	private ImageButton back;

	private EditText oldPwd;
	private EditText newPwd;
	private EditText newPwdAgain;
	private Button submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_modify);
		initView();
		setListener();
	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.password_modify_goback);
		oldPwd = (EditText) findViewById(R.id.password_modify_old_pwd);
		newPwd = (EditText) findViewById(R.id.password_modify_new_pwd_1);
		newPwdAgain = (EditText) findViewById(R.id.password_modify_new_pwd_2);
		submit = (Button) findViewById(R.id.password_modify_submit);
	}

	private void setListener() {
		submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				doSubmit();
			}
		});
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void doSubmit() {
		String oldPwdStr = oldPwd.getText().toString();
		String newPwdStr = newPwd.getText().toString();
		String newPwdAgainStr = newPwdAgain.getText().toString();
		if (check(oldPwdStr, newPwdStr, newPwdAgainStr)) {
			if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
				return;
			}
			showProgressDialog();
			new ModifyPwdTask().execute(oldPwdStr, newPwdStr);
		}
	}

	// 提交修改
	private Result doSubmit(String oldPwdStr, String newPwdStr) {
		ModifyPwdInfo modifyPwdInfo = new ModifyPwdInfo();
		modifyPwdInfo.setUser_name(((AppContext) getApplicationContext())
				.getUserName());
		modifyPwdInfo.setOld_password(oldPwdStr);
		modifyPwdInfo.setNew_password(newPwdStr);
		try {
			return ApiClient.modifyPwd(
					(AppContext) getApplicationContext(), modifyPwdInfo);
		} catch (AppException e) {
			e.printStackTrace();
			final AppException ee = e;
			submit.post(new Runnable() {
				@Override
				public void run() {
					ee.makeToast(getApplicationContext());
				}
			});
		}
		return null;
	}

	private boolean check(String oldPwdStr, String newPwdStr,
			String newPwdAgainStr) {
		if (TextUtils.isEmpty(oldPwdStr)) {
			UIHelper.ToastMessage(getApplicationContext(), getResources()
					.getString(R.string.password_modify_oldpsd_isnull));
			return false;
		}
		if (TextUtils.isEmpty(newPwdStr)) {
			UIHelper.ToastMessage(getApplicationContext(), getResources()
					.getString(R.string.password_modify_newpwd_isnull));
			return false;
		}
		if (TextUtils.isEmpty(newPwdAgainStr)) {
			UIHelper.ToastMessage(getApplicationContext(), getResources()
					.getString(R.string.password_modify_newpwd1_isnull));
			return false;
		}
		if (!newPwdStr.equals(newPwdAgainStr)) {
			UIHelper.ToastMessage(getApplicationContext(), getResources()
					.getString(R.string.password_modify_newpwd_error));
			return false;
		}
		return true;
	}

	private class ModifyPwdTask extends AsyncTask<String, Void, Result> {

		@Override
		protected Result doInBackground(String... params) {
			return doSubmit(params[0], params[1]);
		}

		@Override
		protected void onPostExecute(Result result) {
			super.onPostExecute(result);
			dismissProgressDialog();
			if (result != null) {
				if (result.OK()) {
					(((AppContext)getApplicationContext())).Logout();
					(((AppContext)getApplicationContext())).removeProperty("user.pwd");
					UIHelper.ToastMessage(getApplicationContext(),
							R.string.password_modify_success);
					setResult(RESULT_OK);
					finish();
				} else {
					result.handleErrcode(ModifyPwdActivity.this);
				}
			}
		}
	}
}

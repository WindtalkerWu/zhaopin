package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
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

public class FeedbackActivity extends BaseActivity {
	private ImageButton back;
	private EditText feedBackContent;
	private Button submit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initView();
	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.feedback_goback);
		feedBackContent = (EditText) findViewById(R.id.feedback_content);
		submit = (Button) findViewById(R.id.feedback_dosubmit);

		setListener();
	}
	private void setListener() {
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String feedBack = feedBackContent.getText().toString();
				if (!TextUtils.isEmpty(feedBack)) {
					if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
						return;
					}
					showProgressDialog();
					new FeedBackTask().execute();
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
	private Result doSubmit() {
		try {
			Result result = ApiClient.submitFeedBack((AppContext)getApplicationContext(),
					feedBackContent.getText().toString());
			return result;
		} catch (AppException e) {
			e.printStackTrace();
			final AppException ee = e;
			feedBackContent.post(new Runnable() {
				
				@Override
				public void run() {
					ee.makeToast(getApplicationContext());
				}
			});
		}
		return null;
	}
	private class FeedBackTask extends AsyncTask<Void, Void, Result> {

		@Override
		protected Result doInBackground(Void... params) {
			return doSubmit();
		}
		@Override
	    protected void onPostExecute(Result result) {
	      super.onPostExecute(result);
	      dismissProgressDialog();
	      if (result != null) {
		      if (result.OK()) {
		    	  UIHelper.ToastMessage(getApplicationContext(), R.string.feedback_submit_success);
		      } else {
		    	  result.handleErrcode(FeedbackActivity.this);
//		    	  UIHelper.ToastMessage(getApplicationContext(), R.string.feedback_submit_fail);
		      }
	      }
	    }
	}
}

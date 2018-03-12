package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.CommentSubmitInfo;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentActivity extends BaseActivity implements OnClickListener{
	private static final String TAG = "CommentActivity";
	private ImageView back;
	private ImageView share;
	private EditText content;
	private TextView contentLength;
	private Button submit;
	
	private InsidersAndCompany ic;
	private int type;
	
	private static final int SUBMITEXCEPTION = 1;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SUBMITEXCEPTION:
				Object obj = msg.obj;
				if (obj instanceof AppException) {
					AppException exception = (AppException)obj;
					exception.makeToast(mContext);
				}
				break;

			default:
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		// 友盟统计
		UmShare.UmsetDebugMode(this);
	
		initView();
		getIntentData();
	}
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			ic = (InsidersAndCompany) intent.getSerializableExtra("ic");
			type = ic.getType();
			MyLogger.i(TAG, "type##" + type);
		}
	}
	private void initView() {
		back = (ImageView) findViewById(R.id.comment_goback);
		share = (ImageView) findViewById(R.id.comment_share);
		content = (EditText) findViewById(R.id.comment_content);
		contentLength = (TextView) findViewById(R.id.comment_length);
		submit = (Button) findViewById(R.id.comment_dosubmit);
		
		back.setOnClickListener(this);
		share.setOnClickListener(this);
		submit.setOnClickListener(this);
		content.addTextChangedListener(new TextWatcher() {
			private CharSequence temp; 
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s; 
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
	            contentLength.setText(140 - temp.length() + "");
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.comment_goback:
			finish();
			break;
		case R.id.comment_share:
			break;
		case R.id.comment_dosubmit:
			// 友盟统计--评论墙--发布评论按钮
			UmShare.UmStatistics(this, "Comment_SubmitButton");
			
			doSubmit();
			break;

		default:
			break;
		}
	}
	private void doSubmit() {
		if (TextUtils.isEmpty(content.getText().toString())) {
			UIHelper.ToastMessage(mContext, R.string.comment_submit_null);
			return;
		}
		if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
			return;
		}
		showProgressDialog();
		new SubmitCommentTask().execute();
	}
	private class SubmitCommentTask extends AsyncTask<Void, Void, Result> {

		@Override
		protected Result doInBackground(Void... params) {
			return submitComment();
		}
		@Override
		protected void onPostExecute(Result result) {
			super.onPostExecute(result);
			dismissProgressDialog();
			if (result != null) {
				if (result.OK()) {
					setResult(RESULT_OK);
					finish();
				} else {
					result.handleErrcode(CommentActivity.this);
				}
			}
		}
		
	}
	private Result submitComment() {
		CommentSubmitInfo commentSubmit = new CommentSubmitInfo();
		commentSubmit.setId(ic.getId());
		commentSubmit.setType(type + "");
		commentSubmit.setContent(content.getText().toString());
		try {
			return ApiClient.submitComment(mContext, commentSubmit);
		} catch (AppException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(SUBMITEXCEPTION, e));
		}
		return null;
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// 友盟统计
		UmShare.UmPause(this);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
		// 友盟统计
		UmShare.UmResume(this);
	}
}

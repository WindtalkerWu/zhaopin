package com.qianniu.zhaopin.app.ui;

import org.json.JSONObject;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.ResumeBaseinfoEntity;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumeQuickItemEntity;
import com.qianniu.zhaopin.app.bean.ResumeSubmitResult;
import com.qianniu.zhaopin.app.common.ResumeUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ResumeEditQuickActivity extends BaseActivity {

	private static final int MSG_RESUMESUBMIT_START = 20;
	private static final int MSG_RESUMESUBMIT_OVER = 21;

	private Context mcontext;
	private AppContext mappcontext;

	private ResumeQuickItemEntity mquickEntity;
	private int resume_id;

	private EditText mcontentEt;
	private boolean bsubmitFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		mappcontext = (AppContext) this.getApplication();
		setContentView(R.layout.resume_quickedit_layout);
		if (savedInstanceState != null) {
			mquickEntity = (ResumeQuickItemEntity) savedInstanceState
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			if (mquickEntity != null)
				resume_id = StringUtils.toInt(mquickEntity.getResume_Id(), 0);
		} else {
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			mquickEntity = (ResumeQuickItemEntity) bundle.get("entitiy");
			resume_id = bundle.getInt("resumeid");

		}
		if (mquickEntity == null)
			mquickEntity = new ResumeQuickItemEntity();
		ImageButton back = (ImageButton) findViewById(R.id.resume_goback);
		back.setOnClickListener(mclicklistener);
		ImageButton msaveImgBtn = (ImageButton) findViewById(R.id.resume_edit_save);
		msaveImgBtn.setOnClickListener(mclicklistener);
		Button submitBtn = (Button) findViewById(R.id.resume_fast_submit);
		submitBtn.setOnClickListener(mclicklistener);
		Button delBtn = (Button) findViewById(R.id.resume_fast_del);
		delBtn.setOnClickListener(mclicklistener);

		mcontentEt = (EditText) findViewById(R.id.fastresume_edittv);
		displayUIByEntity(mquickEntity);
		
	}

	private void displayUIByEntity(ResumeQuickItemEntity entity) {
		if (entity != null) {
			if (entity.getContentStr() != null
					&& entity.getContentStr().length() > 0) {
				mcontentEt.setText(entity.getContentStr());
			}
		}
	}

	private OnClickListener mclicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.resume_goback: {
				quit();
			}
				break;
			case R.id.resume_fast_del: {
				// deleteQuickResumeFromNet();
			}
				break;
			case R.id.resume_fast_submit:
			case R.id.resume_edit_save: {
				submitQuickResumeToNet();
			}
				break;

			}
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			quit();

		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}

	/**
	 * activity 退出
	 */
	private void quit() {

		String contentstr = mcontentEt.getText().toString();
		if (contentstr != null && contentstr.length() > 0)
			UIHelper.showQuitAlertDialog(ResumeEditQuickActivity.this);
		else
			finish();

	}
	private void setResultAndFinish(int resultCode) {
		Intent intent = new Intent();
		intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
				mquickEntity);
		intent.putExtras(bundle);
		setResult(resultCode, intent);
		((ResumeEditQuickActivity) mcontext).finish();
	}
	private boolean getSubmitFlag() {
		return bsubmitFlag;
	}

	private void setSubmitFlag(boolean newFlag) {
		bsubmitFlag = newFlag;
	}

	private void readinfo() {

		String contentStr = mcontentEt.getText().toString();
		if (contentStr != null)
			mquickEntity.setContentStr(contentStr);

	}

	private boolean checkComplete() {

		boolean bcomplete = true;

		String contentstr = mcontentEt.getText().toString();
		if (bcomplete && contentstr != null && contentstr.length() > 0)
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请添加快速简历内容");
			mcontentEt.requestFocus();
			bcomplete = false;
			return bcomplete;
		}
		return bcomplete;
	}

	private void submitQuickResumeToNet() {
		if (!checkComplete()) {
			return;
		}
		if (getSubmitFlag()) {
			return;
		}
		mhandler.sendEmptyMessage(MSG_RESUMESUBMIT_START);
		readinfo();
		JSONObject obj = ResumeQuickItemEntity
				.resumeQuickItemEntityToJSONObject(mquickEntity);
		String str = obj.toString();
		int i = 0;
		ResumeUtils.submitData(mappcontext, resume_id, str,
				ResumeEntity.RESUME_BLOCK_QUICKITEM, mhandler);
	}

	private void deleteQuickResumeFromNet() {
		{
			if (getSubmitFlag()) {
				return;
			}
			readinfo();
			mquickEntity.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_DEL);
			JSONObject obj = ResumeQuickItemEntity
					.resumeQuickItemEntityToJSONObject(mquickEntity);
			String str = obj.toString();
			int i = 0;
			ResumeUtils.deleteData(mappcontext, resume_id, str,
					ResumeEntity.RESUME_BLOCK_LANGUAGES, mhandler);
			mhandler.sendEmptyMessage(MSG_RESUMESUBMIT_START);

		}
	}

	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_RESUMESUBMIT_START:
				setSubmitFlag(true);
				showProgressDialog(R.string.dialog_datasubmitmsg);

				break;
			case MSG_RESUMESUBMIT_OVER:
				setSubmitFlag(false);
				dismissProgressDialog();
				break;
			case ResumeUtils.RESUME_SUBMIT_OK:
				ResumeSubmitResult result = (ResumeSubmitResult) msg.obj;
				resume_id = StringUtils.toInt(result.getResume_Id(), resume_id);
				UIHelper.ToastMessage(mcontext, R.string.dialog_save_success);
				setSubmitFlag(false);
				mhandler.sendEmptyMessage(MSG_RESUMESUBMIT_OVER);
				setResultAndFinish(RESULT_OK);
				
				break;
			case ResumeUtils.RESUME_SUBMIT_ERROR:
			case ResumeUtils.RESUME_SUBMIT_NETERROR:
			case ResumeUtils.RESUME_SUBMIT_DATAERROR:
			case ResumeUtils.RESUME_SUBMIT_EXCEPTIONERROR:
				if (msg.what == ResumeUtils.RESUME_SUBMIT_NETERROR
						|| msg.what == ResumeUtils.RESUME_SUBMITDEL_NETERROR) {
					UIHelper.ToastMessage(mcontext,
							R.string.app_status_net_disconnected);
				} else {
					UIHelper.ToastMessage(mcontext, R.string.dialog_save_faile);
				}
				setSubmitFlag(false);
				mhandler.sendEmptyMessage(MSG_RESUMESUBMIT_OVER);
				break;
			}
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mquickEntity = (ResumeQuickItemEntity) savedInstanceState
				.getSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
		if (mquickEntity != null)
			resume_id = StringUtils.toInt(mquickEntity.getResume_Id(), 0);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		readinfo();
		outState.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
				mquickEntity);
		super.onSaveInstanceState(outState);

	}
}

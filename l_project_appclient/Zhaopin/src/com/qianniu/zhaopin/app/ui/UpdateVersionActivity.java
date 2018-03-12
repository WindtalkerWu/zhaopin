package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.bean.VersionData;
import com.qianniu.zhaopin.app.common.UIHelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UpdateVersionActivity extends BaseActivity {
	private VersionData versionData;
	private AlertDialog.Builder builder = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setTitle("哈哈哈");
		initContentView();
		getIntentData();
		showDialog();
	}
	private void initContentView() {
		View view = new View(mContext);
		setContentView(view);
		view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	private void getIntentData() {
		Intent intent = getIntent();
		versionData = (VersionData) intent.getSerializableExtra("versionData");
	}
	
	private void showDialog() {
		AlertDialog dialog = UIHelper.showUpdateVersionDialog(this, versionData);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				finish();
			}
		});
//		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which == DialogInterface.BUTTON_POSITIVE) {
//					ConnectionService.startServiceDownload(UpdateVersionActivity.this, versionData.getDownloadurl(), null);
//				}
//				dialog.dismiss();
//			}
//		};
//		AlertDialog dialog = UIHelper.showCommonAlertDialog(UpdateVersionActivity.this, R.string.apk_update_title, versionData.getMemo(),
//				R.string.sure_update, R.string.cancle_update, onClickListener);
//		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//			
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				finish();
//			}
//		});
	}

}

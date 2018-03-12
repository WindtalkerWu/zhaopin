package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

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
import android.widget.ImageButton;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.ResumeBaseinfoEntity;
import com.qianniu.zhaopin.app.bean.ResumeEducationExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumeJobExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeLanguageExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeSubmitResult;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.ResumeUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.R;

public class ResumeEditLanguageItemActivity extends BaseActivity {

	private static final int REQUESTCODE_LANGUAGE = 510;
	private static final int REQUESTCODE_LANGUAGEMASTER = 511;
	private static final int REQUESTCODE_LANGUAGLITERACY = 512;
	private static final int REQUESTCODE_LANGUAGSPEAKING = 513;

	private static final int HANDLERCODE_DATALOAD = 20;
	private static final int HANDLERCODE_DATALOADOVER = 21;

	private static final int HANDLERCODE_DATASUBMIT = 22;
	private static final int HANDLERCODE_DATALSUBMITOVER = 23;

	private Context mcontext;
	private AppContext mappcontext;


	private ProgressDialog mprogressDialog;

	private Button mlanguageBtn;
	private Button mmasterBtn;
	private Button mliteracyBtn;
	private Button mspeakingBtn;
	private Button msaveBtn;
	private Button mdeleteBtn;

	private ImageButton mbackBtn;

	private String languageid = null;
	private String masterid = null;
	private String literacyid = null;
	private String speakingid = null;

	private int resume_id;
	private boolean bnew = false;
	private ResumeLanguageExpEntity mEntity;

	private boolean bsubmitFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		mappcontext = (AppContext) this.getApplication();
		setContentView(R.layout.resume_language_item);
		if (savedInstanceState != null) {
			mEntity = (ResumeLanguageExpEntity) savedInstanceState
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			if (mEntity != null)
				resume_id = StringUtils.toInt(mEntity.getResume_Id(), 0);
		} else {
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			mEntity = (ResumeLanguageExpEntity) bundle
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			resume_id = bundle
					.getInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID);
			bnew = intent.getBooleanExtra(
					ResumeEditHomeActivity.INTENT_KEY_NEWFLAG, false);
		}
		if (mEntity == null)
			mEntity = new ResumeLanguageExpEntity();

		mlanguageBtn = (Button) findViewById(R.id.language_class);
		mlanguageBtn.setOnClickListener(mclicklistener);
		mmasterBtn = (Button) findViewById(R.id.master_degree);
		mmasterBtn.setOnClickListener(mclicklistener);
		mliteracyBtn = (Button) findViewById(R.id.read_write);
		mliteracyBtn.setOnClickListener(mclicklistener);
		mspeakingBtn = (Button) findViewById(R.id.listen_speak);
		mspeakingBtn.setOnClickListener(mclicklistener);
		msaveBtn = (Button) findViewById(R.id.resume_education_item_submit);
		msaveBtn.setOnClickListener(mclicklistener);
		mdeleteBtn = (Button) findViewById(R.id.resume_education_item_delete);
		mdeleteBtn.setOnClickListener(mclicklistener);

		mbackBtn = (ImageButton) findViewById(R.id.resume_language_item_goback);
		mbackBtn.setOnClickListener(mclicklistener);
		ImageButton msaveImgBtn = (ImageButton) findViewById(R.id.resume_edit_save);
		msaveImgBtn.setOnClickListener(mclicklistener);

		mprogressDialog = new ProgressDialog(this);
		// mprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		/* if (!bnew) */{
			displaySelfinfo();
		}

	}

	OnClickListener mclicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.language_class: {
				Intent intent = new Intent(mcontext,
						CommonStaticItemListActivity.class);
				intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
						DBUtils.GLOBALDATA_TYPE_LANGUAGE);
				intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				Bundle bundle = new Bundle();

				String[] selectedids = { languageid };
				bundle.putStringArray(
						CommonStaticItemListActivity.KEY_SELECTEDID,
						selectedids);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_LANGUAGE);
			}
				break;
			case R.id.master_degree: {
				Intent intent = new Intent(mcontext,
						CommonStaticItemListActivity.class);
				intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
						DBUtils.GLOBALDATA_TYPE_LANGUAGEMASTERY);
				intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				Bundle bundle = new Bundle();

				String[] selectedids = { masterid };
				bundle.putStringArray(
						CommonStaticItemListActivity.KEY_SELECTEDID,
						selectedids);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_LANGUAGEMASTER);
			}
				break;

			case R.id.read_write: {
				Intent intent = new Intent(mcontext,
						CommonStaticItemListActivity.class);
				intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
						DBUtils.GLOBALDATA_TYPE_LANGUAGLITERACY);
				intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				Bundle bundle = new Bundle();

				String[] selectedids = { literacyid };
				bundle.putStringArray(
						CommonStaticItemListActivity.KEY_SELECTEDID,
						selectedids);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_LANGUAGLITERACY);
			}
				break;

			case R.id.listen_speak: {
				Intent intent = new Intent(mcontext,
						CommonStaticItemListActivity.class);
				intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
						DBUtils.GLOBALDATA_TYPE_LANGUAGSPEAKING);
				intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				Bundle bundle = new Bundle();

				String[] selectedids = { speakingid };
				bundle.putStringArray(
						CommonStaticItemListActivity.KEY_SELECTEDID,
						selectedids);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_LANGUAGSPEAKING);
			}
				break;
			case R.id.resume_edit_save:
			case R.id.resume_education_item_submit: {
				if (!checkComplete()) {
					break;
				}
				if (getSubmitFlag()) {
					break;
				}
				readSelfinfo();
				mEntity.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_NORMAL);

				JSONObject obj = mEntity
						.resumeLanguageExpEntityToJSONObject(mEntity);
				String str = obj.toString();
				int i = 0;
				ResumeUtils.submitData(mappcontext, resume_id, str,
						ResumeEntity.RESUME_BLOCK_LANGUAGES, mhandler);
				mhandler.sendEmptyMessage(HANDLERCODE_DATASUBMIT);

			}
				break;
			case R.id.resume_education_item_delete: {
				if (getSubmitFlag()) {
					break;
				}
				readSelfinfo();
				mEntity.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_DEL);
				JSONObject obj = mEntity
						.resumeLanguageExpEntityToJSONObject(mEntity);
				String str = obj.toString();
				int i = 0;
				ResumeUtils.deleteData(mappcontext, resume_id, str,
						ResumeEntity.RESUME_BLOCK_LANGUAGES, mhandler);
				mhandler.sendEmptyMessage(HANDLERCODE_DATASUBMIT);

			}
				break;
			case R.id.resume_language_item_goback: {
				showQuitAlertDialog();
			}
				break;
			}
		}
	};

	private boolean getSubmitFlag() {
		return bsubmitFlag;
	}

	private void setSubmitFlag(boolean newFlag) {
		bsubmitFlag = newFlag;
	}

	private Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {

			case HANDLERCODE_DATALOAD: {
				String dialogmsg = getResources().getString(
						R.string.dialog_dataloadmsg);
				mprogressDialog.setMessage(dialogmsg);
				mprogressDialog.show();
			}
				break;
			case HANDLERCODE_DATALOADOVER: {
				mprogressDialog.dismiss();
			}
				break;
			case HANDLERCODE_DATASUBMIT: {
				setSubmitFlag(true);
				String dialogmsg = getResources().getString(
						R.string.dialog_datasubmitmsg);
				mprogressDialog.setMessage(dialogmsg);
				mprogressDialog.show();
			}
				break;
			case HANDLERCODE_DATALSUBMITOVER: {
				mprogressDialog.dismiss();
				setSubmitFlag(false);
			}
				break;
			case ResumeUtils.RESUME_GETGLOBALDATA: {
				GlobalDataTable table = (GlobalDataTable) msg.obj;
				switch (table.getType()) {
				case DBUtils.GLOBALDATA_TYPE_LANGUAGE:
					mlanguageBtn.setText(table.getName());
					break;
				case DBUtils.GLOBALDATA_TYPE_LANGUAGEMASTERY:
					mmasterBtn.setText(table.getName());
					break;
				case DBUtils.GLOBALDATA_TYPE_LANGUAGLITERACY:
					mliteracyBtn.setText(table.getName());
					break;
				case DBUtils.GLOBALDATA_TYPE_LANGUAGSPEAKING:
					mspeakingBtn.setText(table.getName());
					break;

				}

			}
				break;
			case ResumeUtils.RESUME_SUBMITDEL_OK: {
				mhandler.sendEmptyMessage(HANDLERCODE_DATALSUBMITOVER);
				ResumeSubmitResult result = (ResumeSubmitResult) msg.obj;
				resume_id = StringUtils.toInt(result.getResume_Id(), resume_id);
				int itemid = result.getItem_Id();
				if (itemid <= 0) {
					mEntity.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_NORMAL);
					UIHelper.ToastMessage(mcontext,
							R.string.dialog_submit_failed);
					return;
				}
				mEntity.setItemid(String.valueOf(itemid));
				UIHelper.ToastMessage(mcontext, R.string.dialog_delete_success);
				setSubmitFlag(false);
				setResultAndFinish(RESULT_OK);

			}
				break;
			case ResumeUtils.RESUME_SUBMIT_OK: {
				mhandler.sendEmptyMessage(HANDLERCODE_DATALSUBMITOVER);
				ResumeSubmitResult result = (ResumeSubmitResult) msg.obj;
				resume_id = StringUtils.toInt(result.getResume_Id(), resume_id);
				int itemid = result.getItem_Id();
				if (itemid <= 0) {
					mEntity.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_NORMAL);
					UIHelper.ToastMessage(mcontext,
							R.string.dialog_submit_failed);
					return;
				}
				mEntity.setItemid(String.valueOf(itemid));
				UIHelper.ToastMessage(mcontext, R.string.dialog_save_success);
				setSubmitFlag(false);
				setResultAndFinish(RESULT_OK);

			}
				break;
			case ResumeUtils.RESUME_SUBMITDEL_ERROR:
			case ResumeUtils.RESUME_SUBMITDEL_NETERROR:
			case ResumeUtils.RESUME_SUBMITDEL_DATAERROR:
			case ResumeUtils.RESUME_SUBMITDEL_EXCEPTIONERROR: {
				mEntity.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_NORMAL);
				mhandler.sendEmptyMessage(HANDLERCODE_DATALSUBMITOVER);
				if (msg.what == ResumeUtils.RESUME_SUBMIT_NETERROR
						|| msg.what == ResumeUtils.RESUME_SUBMITDEL_NETERROR) {
					UIHelper.ToastMessage(mcontext,
							R.string.app_status_net_disconnected);
				} else {
					UIHelper.ToastMessage(mcontext,
							R.string.dialog_delete_faile);
				}
				setSubmitFlag(false);
			}
				break;
			case ResumeUtils.RESUME_SUBMIT_ERROR:
			case ResumeUtils.RESUME_SUBMIT_NETERROR:
			case ResumeUtils.RESUME_SUBMIT_DATAERROR:
			case ResumeUtils.RESUME_SUBMIT_EXCEPTIONERROR:
				mhandler.sendEmptyMessage(HANDLERCODE_DATALSUBMITOVER);
				if (msg.what == ResumeUtils.RESUME_SUBMIT_NETERROR
						|| msg.what == ResumeUtils.RESUME_SUBMITDEL_NETERROR) {
					UIHelper.ToastMessage(mcontext,
							R.string.app_status_net_disconnected);
				} else {
					UIHelper.ToastMessage(mcontext, R.string.dialog_save_faile);
				}
				setSubmitFlag(false);
				break;
			}
		}

	};

	private void displaySelfinfo() {

		Thread t = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mhandler.sendEmptyMessage(HANDLERCODE_DATALOAD);
				languageid = mEntity.getLanguageid();
				if (languageid != null && languageid.length() > 0) {
					GlobalDataTable table = GlobalDataTable.getTypeDataById(
							mappcontext, DBUtils.GLOBALDATA_TYPE_LANGUAGE,
							languageid);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}
				}

				masterid = mEntity.getMasterid();
				if (masterid != null && masterid.length() > 0) {
					GlobalDataTable table = GlobalDataTable.getTypeDataById(
							mappcontext,
							DBUtils.GLOBALDATA_TYPE_LANGUAGEMASTERY, masterid);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}
				}
				literacyid = mEntity.getReadwriteid();
				if (literacyid != null && literacyid.length() > 0) {
					GlobalDataTable table = GlobalDataTable
							.getTypeDataById(mappcontext,
									DBUtils.GLOBALDATA_TYPE_LANGUAGLITERACY,
									literacyid);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}
				}
				speakingid = mEntity.getMasterid();
				if (speakingid != null && speakingid.length() > 0) {
					GlobalDataTable table = GlobalDataTable
							.getTypeDataById(mappcontext,
									DBUtils.GLOBALDATA_TYPE_LANGUAGSPEAKING,
									speakingid);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}
				}
				mhandler.sendEmptyMessage(HANDLERCODE_DATALOADOVER);
			}
		};
		if(threadPool == null){
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	private boolean checkComplete() {

		boolean bcomplete = true;
		if (bcomplete && languageid != null && languageid.length() > 0)
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请选择语言类别");
			bcomplete = false;
			return bcomplete;
		}

		return bcomplete;
	}

	private void readSelfinfo() {

		if (languageid != null)
			mEntity.setLanguageid(languageid);
		if (masterid != null)
			mEntity.setMasterid(masterid);
		if (literacyid != null)
			mEntity.setReadwriteid(literacyid);
		if (speakingid != null)
			mEntity.setListenspeakid(speakingid);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE_LANGUAGE: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						languageid = entity.getID();
						mlanguageBtn.setText(entity.getName());
					}

				}
			}
				break;
			case REQUESTCODE_LANGUAGEMASTER: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						masterid = entity.getID();
						mmasterBtn.setText(entity.getName());
					}

				}
			}
				break;
			case REQUESTCODE_LANGUAGLITERACY: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						literacyid = entity.getID();
						mliteracyBtn.setText(entity.getName());
					}

				}
			}
				break;
			case REQUESTCODE_LANGUAGSPEAKING: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						speakingid = entity.getID();
						mspeakingBtn.setText(entity.getName());
					}

				}
			}
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showQuitAlertDialog();

		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}

	private void showQuitAlertDialog() {
		Dialog dialog = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.dialog_quitedittitle)
				.setMessage(R.string.dialog_quiteditmsg)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								((ResumeEditLanguageItemActivity) mcontext)
										.quit();
							}
						})
				.setNegativeButton(R.string.dialog_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();
		dialog.show();
	}

	private void setResultAndFinish(int resultCode) {
		Intent intent = new Intent();
		intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
				mEntity);
		intent.putExtras(bundle);
		setResult(resultCode, intent);
		((ResumeEditLanguageItemActivity) mcontext).quit();
	}

	public void quit() {
		this.finish();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mEntity = (ResumeLanguageExpEntity) savedInstanceState
				.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
		if (mEntity != null)
			resume_id = StringUtils.toInt(mEntity.getResume_Id(), 0);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		readSelfinfo();// mbaseEntity
		outState.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
				mEntity);
		super.onSaveInstanceState(outState);

	}
}

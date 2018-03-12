package com.qianniu.zhaopin.app.ui;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.ResumeEducationExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumeJobExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeLanguageExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeProjectExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeSubmitResult;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.FastDoubleClick;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.ResumeUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.app.view.DateTextView;
import com.qianniu.zhaopin.app.view.EndDateTextView;
import com.qianniu.zhaopin.R;

public class ResumeEditEducationItemActivity extends BaseActivity {
	private static final int REQUESTCODE_MAJOR = 100;
	private static final int REQUESTCODE_DEGREE = 101;

	private static final int HANDLERCODE_DATALOAD = 20;
	private static final int HANDLERCODE_DATALOADOVER = 21;

	private static final int HANDLERCODE_DATASUBMIT = 22;
	private static final int HANDLERCODE_DATALSUBMITOVER = 23;
	private Context mcontext;
	private AppContext mappcontext;

	private DateTextView mstarttimeBtn;
	private EndDateTextView mendtimeBtn;
	private Button mmajorBtn;
	private Button mdegreeBtn;
	private Button msaveBtn;
	private Button mdeleteBtn;
	private ImageButton mbackBtn;
	private ImageButton msaveImgBtn;
	private EditText mschoolEt;

	private ProgressDialog mprogressDialog;

	private int currentDatePickerId = 0;

	private int resume_id;
	private boolean bnew = false;
	private ResumeEducationExpEntity mEntity;

	private String startdate = null;
	private String enddate = null;
	private String majorid = null;
	private String degreeid = null;

	private boolean bsubmitFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		mappcontext = (AppContext) this.getApplication();
		setContentView(R.layout.resume_education_item);
		if (savedInstanceState != null) {
			mEntity = (ResumeEducationExpEntity) savedInstanceState
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			if (mEntity != null)
				resume_id = StringUtils.toInt(mEntity.getResume_Id(), 0);
		} else {
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			mEntity = (ResumeEducationExpEntity) bundle
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			resume_id = bundle
					.getInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID);
			bnew = intent.getBooleanExtra(
					ResumeEditHomeActivity.INTENT_KEY_NEWFLAG, false);
		}
		if (mEntity == null)
			mEntity = new ResumeEducationExpEntity();

		mbackBtn = (ImageButton) findViewById(R.id.resume_education_item_goback);
		mbackBtn.setOnClickListener(mclicklistener);
		msaveImgBtn = (ImageButton) findViewById(R.id.resume_edit_save);
		msaveImgBtn.setOnClickListener(mclicklistener);
		msaveBtn = (Button) findViewById(R.id.resume_education_item_submit);
		msaveBtn.setOnClickListener(mclicklistener);
		mdeleteBtn = (Button) findViewById(R.id.resume_education_item_delete);
		mdeleteBtn.setOnClickListener(mclicklistener);

		mstarttimeBtn = (DateTextView) findViewById(R.id.start_time);
		mstarttimeBtn.setOnClickListener(mclicklistener);
		mendtimeBtn = (EndDateTextView) findViewById(R.id.end_time);
		mendtimeBtn.setOnClickListener(mclicklistener);
		mmajorBtn = (Button) findViewById(R.id.major);
		mmajorBtn.setOnClickListener(mclicklistener);
		mdegreeBtn = (Button) findViewById(R.id.degree);
		mdegreeBtn.setOnClickListener(mclicklistener);

		mschoolEt = (EditText) findViewById(R.id.school);

		mprogressDialog = new ProgressDialog(this);
		// mprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		/* if (!bnew) */{
			displaySelfinfo();
		}

	}

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
				setSubmitFlag(false);
				mprogressDialog.dismiss();
			}
				break;
			case ResumeUtils.RESUME_GETGLOBALDATA: {
				GlobalDataTable table = (GlobalDataTable) msg.obj;
				switch (table.getType()) {
				case DBUtils.GLOBALDATA_TYPE_SPECIALTY:
					mmajorBtn.setText(table.getName());
					break;
				case DBUtils.GLOBALDATA_TYPE_EDUCATION:
					mdegreeBtn.setText(table.getName());
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
				setResultAndFinish(RESULT_OK);
				setSubmitFlag(false);

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
				setResultAndFinish(RESULT_OK);
				setSubmitFlag(false);

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

		String school = mEntity.getSchoolname();
		if (school != null && school.length() > 0)
			mschoolEt.setText(school);
		startdate = mEntity.getStartdate();
		if (startdate != null && startdate.length() > 0) {
			mstarttimeBtn.setDate(startdate);

		}

		enddate = mEntity.getEnddate();
		if (enddate != null && enddate.length() > 0) {
			mendtimeBtn.setDate(enddate);
		}

		Thread t = new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mhandler.sendEmptyMessage(HANDLERCODE_DATALOAD);
				majorid = mEntity.getMajorid();
				if (majorid != null && majorid.length() > 0) {
					GlobalDataTable table = GlobalDataTable.getTypeDataById(
							mappcontext, DBUtils.GLOBALDATA_TYPE_SPECIALTY,
							majorid);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}
				}

				degreeid = mEntity.getDegreeid();
				if (degreeid != null && degreeid.length() > 0) {
					GlobalDataTable table = GlobalDataTable.getTypeDataById(
							mappcontext, DBUtils.GLOBALDATA_TYPE_EDUCATION,
							degreeid);
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
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	private boolean checkComplete() {

		boolean bcomplete = true;
		String school = mschoolEt.getText().toString();
		if (school != null && school.length() > 0)
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请输入学校名称");
			mschoolEt.requestFocus();
			bcomplete = false;
			return bcomplete;
		}
		return bcomplete;
	}

	private void readSelfinfo() {
		String school = mschoolEt.getText().toString();
		if (school != null)
			mEntity.setSchoolname(school);
		startdate = mstarttimeBtn.getDate();
		if (startdate != null)
			mEntity.setStartdate(startdate);

		enddate = mendtimeBtn.getDate();
		if (enddate != null)
			mEntity.setEnddate(enddate);

		if (degreeid != null)
			mEntity.setDegreeid(degreeid);
		if (majorid != null)
			mEntity.setMajorid(majorid);

	}

	private void showDatePickerDialog(Context context,
			OnDateSetListener callBack, int year, int month, int day, int vid) {
		DatePickerDialog dialog = new DatePickerDialog(context, callBack, year,
				month, day);
		dialog.show();
		currentDatePickerId = vid;
	}

	private OnDateSetListener mDateSetListener = new OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			updateCalendarDisplay(year, monthOfYear, dayOfMonth);
		}
	};

	private void updateCalendarDisplay(int year, int month, int day) {
		switch (currentDatePickerId) {
		case R.id.start_time: {

			boolean bexp = false;
			long days = 0;
			long days2 = 0;
			String end_date = mEntity.getEnddate();
			String start_date = StringUtils.formatToYYMMDD(year, month, day);

			try {
				days2 = StringUtils.compareDateStrWithToday(start_date);
				days = StringUtils.compareDateStr(end_date, start_date);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				bexp = true;
			}

			if (days2 > 0) {
				UIHelper.ToastMessage(mappcontext, "开始时间不可以超过今天！");
			} else if (days < 0) {
				UIHelper.ToastMessage(mappcontext, "开始时间要小于结束时间！");
			} else {
				mEntity.setStartdate(start_date);
				mstarttimeBtn.setDate(start_date);
			}
		}
			break;
		case R.id.end_time: {

			boolean bexp = false;
			long days = 0;
			long days2 = 0;
			String start_date = mEntity.getStartdate();
			String end_date = StringUtils.formatToYYMMDD(year, month, day);

			try {
				days2 = StringUtils.compareDateStrWithToday(end_date);
				days = StringUtils.compareDateStr(end_date, start_date);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				bexp = true;
			}

			if (days < 0) {
				UIHelper.ToastMessage(mappcontext, "结束时间要大于开始时间！");
			} else {
				mEntity.setEnddate(end_date);
				mendtimeBtn.setDate(end_date);
			}
		}
			break;
		}

	}

	OnClickListener mclicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (FastDoubleClick.isFastDoubleClick()) {
				return;
			}
			switch (id) {
			case R.id.start_time: {
				startdate = mstarttimeBtn.getDate();
				
				Calendar c = StringUtils.toCalendar(startdate);
				if (c == null) {
					c = Calendar.getInstance();
				}
				showDatePickerDialog(mcontext, mDateSetListener,
						c.get(Calendar.YEAR), c.get(Calendar.MONTH),
						c.get(Calendar.DAY_OF_MONTH), id);
			}
				break;
			case R.id.end_time: {
				enddate = mendtimeBtn.getDate();
				
				Calendar c = StringUtils.toCalendar(enddate);
				if (c == null|| c.get(Calendar.YEAR) == 2100) {
					c = Calendar.getInstance();
				}
				showDatePickerDialog(mcontext, mDateSetListener,
						c.get(Calendar.YEAR), c.get(Calendar.MONTH),
						c.get(Calendar.DAY_OF_MONTH), id);
			}
				break;
			case R.id.major: {
				Intent intent = new Intent(mcontext,
						CommonStaticItemListActivity.class);
				intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
						DBUtils.GLOBALDATA_TYPE_SPECIALTY);
				intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				Bundle bundle = new Bundle();

				String[] selectedids = { majorid };
				bundle.putStringArray(
						CommonStaticItemListActivity.KEY_SELECTEDID,
						selectedids);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_MAJOR);
			}
				break;
			case R.id.degree: {
				Intent intent = new Intent(mcontext,
						CommonStaticItemListActivity.class);
				intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
						DBUtils.GLOBALDATA_TYPE_EDUCATION);
				intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				Bundle bundle = new Bundle();

				String[] selectedids = { degreeid };
				bundle.putStringArray(
						CommonStaticItemListActivity.KEY_SELECTEDID,
						selectedids);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_DEGREE);
			}
				break;
			case R.id.resume_education_item_goback: {
				showQuitAlertDialog();
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
						.resumeEducationExpEntityToJSONObject(mEntity);

				String str = obj.toString();
				int i = 0;
				ResumeUtils.submitData(mappcontext, resume_id, str,
						ResumeEntity.RESUME_BLOCK_EDUCATIONS, mhandler);
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
						.resumeEducationExpEntityToJSONObject(mEntity);

				String str = obj.toString();
				int i = 0;
				ResumeUtils.deleteData(mappcontext, resume_id, str,
						ResumeEntity.RESUME_BLOCK_EDUCATIONS, mhandler);
				mhandler.sendEmptyMessage(HANDLERCODE_DATASUBMIT);

			}
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE_DEGREE: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						degreeid = entity.getID();
						mdegreeBtn.setText(entity.getName());
					}

				}
			}
				break;
			case REQUESTCODE_MAJOR: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						majorid = entity.getID();
						mmajorBtn.setText(entity.getName());
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
								((ResumeEditEducationItemActivity) mcontext)
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
		((ResumeEditEducationItemActivity) mcontext).quit();
	}

	public void quit() {
		this.finish();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mEntity = (ResumeEducationExpEntity) savedInstanceState
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

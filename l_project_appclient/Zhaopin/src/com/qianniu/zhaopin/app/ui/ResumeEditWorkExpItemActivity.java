package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.app.bean.ResumeBaseinfoEntity;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumeJobExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeLanguageExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeProjectExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeSubmitResult;
import com.qianniu.zhaopin.app.bean.ThreeLevelJobFunctionsData;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.FastDoubleClick;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
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

public class ResumeEditWorkExpItemActivity extends BaseActivity {

	private static final int REQUESTCODE_INDUSTRY = 510;

	private static final int HANDLERCODE_DATALOAD = 20;
	private static final int HANDLERCODE_DATALOADOVER = 21;

	private static final int HANDLERCODE_DATASUBMIT = 22;
	private static final int HANDLERCODE_DATALSUBMITOVER = 23;

	private Context mcontext;
	private AppContext mappcontext;

	private DateTextView mstarttimeBtn;
	private EndDateTextView mendtimeBtn;

	private Button mindustryBtn;
	private Button msaveBtn;
	private Button mdeleteBtn;
	private ImageButton mbackBtn;

	private EditText mcmpnameEt;
	private EditText mdepartmentEt;
	private EditText mpositionEt;
	private EditText mmemoEt;

	private Button positionBtn;

	private ProgressDialog mprogressDialog;

	private String industryid = null;
	private String positionid = null;

	private int resume_id;
	private boolean bnew = false;
	private ResumeJobExpEntity mEntity;
	private String startdate = null;
	private String enddate = null;

	private int currentDatePickerId = 0;

	private boolean bsubmitFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		mappcontext = (AppContext) this.getApplication();
		setContentView(R.layout.resume_workexperience_item);
		if (savedInstanceState != null) {
			mEntity = (ResumeJobExpEntity) savedInstanceState
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			if (mEntity != null)
				resume_id = StringUtils.toInt(mEntity.getResume_Id(), 0);
		} else {
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			mEntity = (ResumeJobExpEntity) bundle
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			resume_id = bundle
					.getInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID);
			bnew = intent.getBooleanExtra(
					ResumeEditHomeActivity.INTENT_KEY_NEWFLAG, false);
		}
		if (mEntity == null)
			mEntity = new ResumeJobExpEntity();

		mbackBtn = (ImageButton) findViewById(R.id.resume_workexp_item_goback);
		mbackBtn.setOnClickListener(mclicklistener);
		ImageButton msaveImgBtn = (ImageButton) findViewById(R.id.resume_edit_save);
		msaveImgBtn.setOnClickListener(mclicklistener);
		msaveBtn = (Button) findViewById(R.id.resume_workexp_item_submit);
		msaveBtn.setOnClickListener(mclicklistener);
		mdeleteBtn = (Button) findViewById(R.id.resume_workexp_item_delete);
		mdeleteBtn.setOnClickListener(mclicklistener);

		mindustryBtn = (Button) findViewById(R.id.industry);
		mindustryBtn.setOnClickListener(mclicklistener);

		mstarttimeBtn = (DateTextView) findViewById(R.id.start_time);
		mstarttimeBtn.setOnClickListener(mclicklistener);
		mendtimeBtn = (EndDateTextView) findViewById(R.id.end_time);
		mendtimeBtn.setOnClickListener(mclicklistener);

		mpositionEt = (EditText) findViewById(R.id.position);
		positionBtn = (Button) findViewById(R.id.position_btn);
		positionBtn.setOnClickListener(mclicklistener);

		mcmpnameEt = (EditText) findViewById(R.id.compname);
		mdepartmentEt = (EditText) findViewById(R.id.department);
		mmemoEt = (EditText) findViewById(R.id.jd);
		mmemoEt.addTextChangedListener(mEditTextWatcher);

		mprogressDialog = new ProgressDialog(this);
		// mprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		/* if (!bnew) */{
			displaySelfinfo();
		}
	}

	private static final int MAX_INPUT_LENGTH_500 = 500;
	TextWatcher mEditTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

			int length = s.toString().length();
			if (length > MAX_INPUT_LENGTH_500) {
				UIHelper.ToastMessage(mContext, "您输入内容过长！");
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
				setSubmitFlag(false);
				mprogressDialog.dismiss();
			}
				break;
			case ResumeUtils.RESUME_GETGLOBALDATA: {
				GlobalDataTable table = (GlobalDataTable) msg.obj;
				switch (table.getType()) {

				case DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY:
					mindustryBtn.setText(table.getName());
					break;
				case DBUtils.GLOBALDATA_TYPE_JOBFUNCTION: {
					positionBtn.setText(table.getName());
				}
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

		String cmpname = mEntity.getCompany();
		if (cmpname != null && cmpname.length() > 0)
			mcmpnameEt.setText(cmpname);
		String dptname = mEntity.getDepartment();
		if (dptname != null && dptname.length() > 0)
			mdepartmentEt.setText(dptname);
		String position = mEntity.getJobtitle();
		if (position != null && position.length() > 0)
			mpositionEt.setText(position);
		String memo = mEntity.getJobmemo();
		if (memo != null && memo.length() > 0)
			mmemoEt.setText(memo);
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
				industryid = mEntity.getJobtradeid();
				if (industryid != null) {
					//行业是2级数据
					GlobalDataTable table = GlobalDataTable.getGlobalDataTable(
							mappcontext, DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY,2,
							industryid);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}
				}
				positionid = mEntity.getJobtitleId();
				if (positionid != null) {
					//职位名称是3级数据
					GlobalDataTable table = GlobalDataTable.getGlobalDataTable(
							mappcontext, DBUtils.GLOBALDATA_TYPE_JOBFUNCTION,3,
							positionid);
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
		String cmpname = mcmpnameEt.getText().toString();
		if (cmpname != null && cmpname.length() > 0)
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请输入公司名称");
			mcmpnameEt.requestFocus();
			bcomplete = false;
			return bcomplete;
		}
		return bcomplete;
	}

	private void readSelfinfo() {

		String cmpname = mcmpnameEt.getText().toString();
		if (cmpname != null)
			mEntity.setCompany(cmpname);
		String dptname = mdepartmentEt.getText().toString();
		if (dptname != null)
			mEntity.setDepartment(dptname);
//		String position = mpositionEt.getText().toString();
//		if (position != null)
//			mEntity.setJobtitle(position);
		
		if (positionid != null)
			mEntity.setJobtitleId(positionid);
		
		String memo = mmemoEt.getText().toString();
		if (memo != null)
			mEntity.setJobmemo(memo);

		startdate = mstarttimeBtn.getDate();
		
		if (startdate != null)
			mEntity.setStartdate(startdate);

		enddate = mendtimeBtn.getDate();

		if (enddate != null)
			mEntity.setEnddate(enddate);

		if (industryid != null)
			mEntity.setJobtradeid(industryid);

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
			String end_date = mendtimeBtn.getDate();
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
				mstarttimeBtn.setDate(start_date);
			}

		}
			break;
		case R.id.end_time: {

			boolean bexp = false;
			long days = 0;
			long days2 = 0;
			String start_date = mstarttimeBtn.getDate();

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
				mendtimeBtn.setDate(end_date);
			}
		}
			break;
		}

	}

	OnClickListener mclicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (FastDoubleClick.isFastDoubleClick()) {
				return;
			}
			int id = v.getId();
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
				if (c == null || c.get(Calendar.YEAR) == 2100) {
					c = Calendar.getInstance();
				}
				showDatePickerDialog(mcontext, mDateSetListener,
						c.get(Calendar.YEAR), c.get(Calendar.MONTH),
						c.get(Calendar.DAY_OF_MONTH), id);
			}
				break;
			case R.id.position_btn: {
				Bundle bundle = new Bundle();
				bundle.putString(HeadhunterPublic.JOBFUNCTIONCHOOSE_DATA, positionid);

				Intent intent = new Intent();
				intent.setClass(mcontext, JobFunctionsChooseActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent,
						HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
				break;
			case R.id.industry: {
				Bundle bundle = new Bundle();

				bundle.putInt(
						HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_TYPE,
						HeadhunterPublic.INDUSTRYCHOOSE_TYPE_SINGLE);
				ArrayList<OneLevelChooseData> industrydatas = new ArrayList<OneLevelChooseData>();
				OneLevelChooseData industrydata = new OneLevelChooseData();
				industrydata.setID(industryid);
				industrydata.setIsChoose(true);
				industrydatas.add(industrydata);
				bundle.putSerializable(
						HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_DATA,
						industrydatas);

				Intent intent = new Intent();
				intent.setClass(mcontext, IndustryChooseActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_INDUSTRY);
				/*
				 * Intent intent = new Intent(mcontext,
				 * CommonStaticItemListActivity.class);
				 * intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
				 * DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY);
				 * intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
				 * CommonStaticItemListActivity.COMMON_SINGLESELECT); Bundle
				 * bundle = new Bundle();
				 * 
				 * String[] selectedids = { industryid }; bundle.putStringArray(
				 * CommonStaticItemListActivity.KEY_SELECTEDID, selectedids);
				 * intent.putExtras(bundle); startActivityForResult(intent,
				 * REQUESTCODE_INDUSTRY);
				 */
			}
				break;
			case R.id.resume_workexp_item_goback: {
				showQuitAlertDialog();
			}
				break;
			case R.id.resume_edit_save:
			case R.id.resume_workexp_item_submit: {
				if (!checkComplete()) {
					break;
				}
				if (getSubmitFlag()) {
					break;
				}
				readSelfinfo();
				mEntity.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_NORMAL);

				JSONObject obj = mEntity
						.resumeJobExpEntityToJSONObject(mEntity);

				String str = obj.toString();
				int i = 0;
				ResumeUtils.submitData(mappcontext, resume_id, str,
						ResumeEntity.RESUME_BLOCK_JOBS, mhandler);
				mhandler.sendEmptyMessage(HANDLERCODE_DATASUBMIT);

			}
				break;
			case R.id.resume_workexp_item_delete: {
				if (getSubmitFlag()) {
					break;
				}
				readSelfinfo();
				mEntity.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_DEL);
				JSONObject obj = mEntity
						.resumeJobExpEntityToJSONObject(mEntity);

				String str = obj.toString();
				int i = 0;
				ResumeUtils.deleteData(mappcontext, resume_id, str,
						ResumeEntity.RESUME_BLOCK_JOBS, mhandler);
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
			case REQUESTCODE_INDUSTRY: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						industryid = entity.getID();
						mindustryBtn.setText(entity.getName());
					}

				}
			}
				break;

			}
		}

		if (resultCode == HeadhunterPublic.RESULT_INDUSTRYCITY_OK) {
			switch (requestCode) {
			case REQUESTCODE_INDUSTRY: {

				ArrayList<OneLevelChooseData> industrydatas = (ArrayList<OneLevelChooseData>) data
						.getSerializableExtra(HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_DATA);
				if (null != industrydatas && industrydatas.size() > 0) {
					OneLevelChooseData industrydata = industrydatas.get(0);

					industryid = industrydata.getID();
					mindustryBtn.setText(industrydata.getName());
				}
			}
				break;
			}
		}
		if (HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode) {
			switch (resultCode) {

			case HeadhunterPublic.RESULT_JOBFUNCTIONS_OK: {
				OneLevelChooseData choosedata = null;

				choosedata = (OneLevelChooseData) data
						.getSerializableExtra(HeadhunterPublic.JOBFUNCTIONCHOOSE_DATA);
				if (choosedata != null) {
					String str = choosedata.getName();
					positionid = choosedata.getID();
					positionBtn.setText(choosedata.getName());
				}
			}
				break;
			default:
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
								((ResumeEditWorkExpItemActivity) mcontext)
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
		((ResumeEditWorkExpItemActivity) mcontext).quit();
	}

	public void quit() {
		this.finish();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mEntity = (ResumeJobExpEntity) savedInstanceState
				.getSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
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

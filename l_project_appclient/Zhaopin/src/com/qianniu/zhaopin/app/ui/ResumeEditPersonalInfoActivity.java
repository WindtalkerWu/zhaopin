package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.RadioButton;
import android.widget.TextView;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.bean.CityChooseData;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.ResumeBaseinfoEntity;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumeSubmitResult;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.FastDoubleClick;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.ResumeUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

public class ResumeEditPersonalInfoActivity extends BaseActivity {

	private static final int BOY = 1;
	private static final int GIRL = 2;

	private static final int REQUESTCODE_DOMICILE = 501;
	private static final int REQUESTCODE_EDUDEGREE = 502;

	private Context mcontext;
	private AppContext mappcontext;

	private EditText mnameEt;
	private RadioButton mboyRbtn;
	private RadioButton mgirlRbtn;
	private TextView mbirthBtn;
	private Button mworkyearBtn;
	private Button mcityBtn;
	private EditText mmobileEt;
	private EditText memailEt;
	private Button medulevelBtn;
	private EditText mlabelsEt;

	private Button msaveBtn;

	private int currentDatePickerId = 0;
	private ResumeBaseinfoEntity mbaseEntity;
	private int resume_id;

	private boolean bnew = false;

	private String birthdate = null;
	private String workyear = null;
	private String cityid = null;
	private String edudegreeid = null;

	private ProgressDialog mprogressDialog;
	private boolean bsubmitFlag = false;

	private static final int MSG_RESUMESUBMIT_START = 5;
	private static final int MSG_RESUMESUBMIT_OVER = 6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		mappcontext = (AppContext) this.getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(mcontext);

		setContentView(R.layout.resume_personal_info);
		if (savedInstanceState != null) {
			mbaseEntity = (ResumeBaseinfoEntity) savedInstanceState
					.getSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			if (mbaseEntity != null)
				resume_id = StringUtils.toInt(mbaseEntity.getResume_Id(), 0);
		} else {
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			mbaseEntity = (ResumeBaseinfoEntity) bundle.get("entitiy");
			resume_id = bundle.getInt("resumeid");
			bnew = intent.getBooleanExtra("bnew", false);
		}
		if (mbaseEntity == null)
			mbaseEntity = new ResumeBaseinfoEntity();

		mprogressDialog = new ProgressDialog(this);

		ImageButton back = (ImageButton) findViewById(R.id.resume_personal_goback);
		back.setOnClickListener(mclicklistener);
		ImageButton msaveImgBtn = (ImageButton) findViewById(R.id.resume_edit_save);
		msaveImgBtn.setOnClickListener(mclicklistener);

		mnameEt = (EditText) findViewById(R.id.name);
		mmobileEt = (EditText) findViewById(R.id.mobile);
		memailEt = (EditText) findViewById(R.id.email);
		mlabelsEt = (EditText) findViewById(R.id.labels);

		mboyRbtn = (RadioButton) findViewById(R.id.gender_boy);
		mgirlRbtn = (RadioButton) findViewById(R.id.gender_girl);

		mbirthBtn = (TextView) findViewById(R.id.birth);
		mbirthBtn.setOnClickListener(mclicklistener);
		mworkyearBtn = (Button) findViewById(R.id.workyear);
		mworkyearBtn.setOnClickListener(mclicklistener);
		mcityBtn = (Button) findViewById(R.id.city);
		mcityBtn.setOnClickListener(mclicklistener);
		medulevelBtn = (Button) findViewById(R.id.edulevel);
		medulevelBtn.setOnClickListener(mclicklistener);
		msaveBtn = (Button) findViewById(R.id.selfinfo_save);
		msaveBtn.setOnClickListener(mclicklistener);

		/* if (!bnew) */{
			displaySelfinfo();
		}

	}

	private void displaySelfinfo() {

		String name = mbaseEntity.getName();
		if (name != null && name.length() > 0)
			mnameEt.setText(name);

		int gender = mbaseEntity.getGender();
		if (gender == BOY) {
			mboyRbtn.setChecked(true);
		} else {
			mgirlRbtn.setChecked(true);
		}

		birthdate = mbaseEntity.getBirthday();
		if (birthdate != null && birthdate.length() > 0) {
			mbirthBtn.setText(StringUtils.formatDateStringToYMD(birthdate));
		}

		workyear = mbaseEntity.getWorkyear();
		if (workyear != null && workyear.length() > 0) {
			mworkyearBtn.setText(StringUtils.formatDateStringToYMD(workyear));

		}

		String phonenum = mbaseEntity.getPhone();
		if (phonenum != null && phonenum.length() > 0) {
			mmobileEt.setText(phonenum);
		}

		String email = mbaseEntity.getMail();
		if (email != null && email.length() > 0) {
			memailEt.setText(email);
		}

		String selftag = mbaseEntity.getSelftag();
		if (selftag != null && selftag.length() > 0) {
			mlabelsEt.setText(selftag);
		}

		Thread t = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				edudegreeid = mbaseEntity.geteducationdegreeId();
				if (edudegreeid != null && edudegreeid.length() > 0) {
					GlobalDataTable table = GlobalDataTable.getTypeDataById(
							mappcontext, DBUtils.GLOBALDATA_TYPE_EDUCATION,
							edudegreeid);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}
				}

				cityid = mbaseEntity.getplaceId();
				if (cityid != null && cityid.length() > 0) {
					GlobalDataTable table = GlobalDataTable.getTypeDataById(
							mappcontext, DBUtils.GLOBALDATA_TYPE_CITY, cityid);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}
				}

			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	private void readSelfinfo() {

		String name = mnameEt.getText().toString();
		if (name != null)
			mbaseEntity.setName(name);

		int gender = 1;
		if (mboyRbtn.isChecked()) {
			gender = BOY;
		} else {
			gender = GIRL;
		}
		mbaseEntity.setGender(gender);

		birthdate = mbirthBtn.getText().toString();
		birthdate = StringUtils.changeYMDToYYMMDD(birthdate);
		if (birthdate != null) {

			mbaseEntity.setBirthday(birthdate);
		}

		workyear = mworkyearBtn.getText().toString();
		workyear = StringUtils.changeYMDToYYMMDD(workyear);
		if (workyear != null) {

			mbaseEntity.setWorkyear(workyear);
		}

		if (cityid != null)
			mbaseEntity.setplaceId(cityid);

		String phonenum = mmobileEt.getText().toString();
		if (phonenum != null)
			mbaseEntity.setPhone(phonenum);

		String email = memailEt.getText().toString();
		if (email != null)
			mbaseEntity.setMail(email);

		if (edudegreeid != null)
			mbaseEntity.seteducationdegreeId(edudegreeid);

		String selftag = mlabelsEt.getText().toString();
		if (selftag != null)
			mbaseEntity.setSelftag(selftag);

	}

	private boolean checkCompleteSelfinfo() {

		boolean bcomplete = true;

		String name = mnameEt.getText().toString();
		if (bcomplete && name != null && name.length() > 0)
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请输入姓名");
			mnameEt.requestFocus();
			bcomplete = false;
			return bcomplete;
		}

		birthdate = mbirthBtn.getText().toString();
		birthdate = StringUtils.changeYMDToYYMMDD(birthdate);
		if (bcomplete && birthdate != null && birthdate.length() > 0) {

			bcomplete = true;
		} else {
			UIHelper.ToastMessage(mcontext, "请选择出生日期");
			bcomplete = false;
			return bcomplete;
		}

		workyear = mworkyearBtn.getText().toString();
		workyear = StringUtils.changeYMDToYYMMDD(workyear);
		if (bcomplete && workyear != null && workyear.length() > 0) {

			bcomplete = true;
		} else {
			UIHelper.ToastMessage(mcontext, "请选择工作年份");
			bcomplete = false;
			return bcomplete;
		}

		if (bcomplete && cityid != null && cityid.length() > 0)
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请选择所在地区");
			bcomplete = false;
			return bcomplete;
		}

		String phonenum = mmobileEt.getText().toString();
		if (bcomplete && phonenum != null
				&& StringUtils.isPhoneNumberValid(phonenum))
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请输入正确的电话号码");
			mmobileEt.requestFocus();
			bcomplete = false;
			return bcomplete;
		}

		String email = memailEt.getText().toString();
		if (bcomplete && email != null && StringUtils.isEmail(email))
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请输入正确的电子邮箱");
			memailEt.requestFocus();
			bcomplete = false;
			return bcomplete;
		}
		if (bcomplete && edudegreeid != null && edudegreeid.length() > 0)
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请选择学历");
			bcomplete = false;
			return bcomplete;
		}
		return bcomplete;

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
		case R.id.birth: {
			boolean bexp = false;
			long days = 0;
			long days2 = 0;
			String work_date = mworkyearBtn.getText().toString();
			if (work_date != null) {
				work_date = StringUtils.changeYMDToYYMMDD(work_date);
			}
			String birth_date = StringUtils.formatToYYMMDD(year, month, day);
			try {
				
				days2 = StringUtils.compareDateStrWithToday(birth_date);
				days = StringUtils.compareDateStr(birth_date,work_date);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				bexp = true;
			}
			if(days2 > 0){
				UIHelper.ToastMessage(mappcontext, "出生日期不可以超过今天！");
			}else if(days > 0){
				UIHelper.ToastMessage(mappcontext, "出生日期要小于工作日期！");
			}else{
				mbirthBtn.setText(StringUtils.formatToYMD(year, month, day));
			}

		}
			break;
		case R.id.workyear: {
			boolean bexp = false;
			long days = 0;
			long days2 = 0;
			String birth_date = mbirthBtn.getText().toString();
			if (birth_date != null) {
				birth_date = StringUtils.changeYMDToYYMMDD(birth_date);
			}
			String work_date = StringUtils.formatToYYMMDD(year, month, day);

			try {
				days2 = StringUtils.compareDateStrWithToday(work_date);
				days = StringUtils.compareDateStr(birth_date,work_date);
				

			} catch (Exception e) {
				// TODO Auto-generated catch block
				bexp = true;
			}
			if(days2 > 0){
				UIHelper.ToastMessage(mappcontext, "工作日期不可以超过今天！");
			}else if(days > 0){
				UIHelper.ToastMessage(mappcontext, "工作日期要大于出生日期！");
			}else{
				mworkyearBtn.setText(StringUtils.formatToYMD(year, month, day));
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
			case R.id.birth: {
				// final Calendar c = Calendar.getInstance();
				birthdate = mbirthBtn.getText().toString();
				birthdate = StringUtils.changeYMDToYYMMDD(birthdate);
				Calendar c = StringUtils.toCalendar(birthdate);
				if (c == null) {
					c = Calendar.getInstance();
				}
				showDatePickerDialog(mcontext, mDateSetListener,
						c.get(Calendar.YEAR), c.get(Calendar.MONTH),
						c.get(Calendar.DAY_OF_MONTH), id);
			}
				break;
			case R.id.workyear: {
				workyear = mworkyearBtn.getText().toString();
				workyear = StringUtils.changeYMDToYYMMDD(workyear);
				Calendar c = StringUtils.toCalendar(workyear);
				if (c == null) {
					c = Calendar.getInstance();
				}
				showDatePickerDialog(mcontext, mDateSetListener,
						c.get(Calendar.YEAR), c.get(Calendar.MONTH),
						c.get(Calendar.DAY_OF_MONTH), id);
			}
				break;

			case R.id.city: {
				// 数据传输
				Bundle bundle = new Bundle();
				// 设置为多选
				bundle.putInt(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_TYPE,
						HeadhunterPublic.CITYCHOOSE_TYPE_SINGLE);
				ArrayList<CityChooseData> citydatas = new ArrayList<CityChooseData>();
				CityChooseData citydata = new CityChooseData();
				citydata.setID(cityid);
				citydata.setIsChoose(true);
				citydatas.add(citydata);
				bundle.putSerializable(
						HeadhunterPublic.CITYCHOOSE_DATATRANSFER_DATA,
						citydatas);

				// 进入城市选择界面
				Intent intent = new Intent();
				intent.setClass(mcontext, CityChooseActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_DOMICILE);

				/*
				 * Intent intent = new Intent(mcontext,
				 * CommonStaticItemListActivity.class);
				 * intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
				 * DBUtils.GLOBALDATA_TYPE_CITY);
				 * intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
				 * CommonStaticItemListActivity.COMMON_SINGLESELECT); Bundle
				 * bundle = new Bundle();
				 * 
				 * String[] selectedids = { cityid }; bundle.putStringArray(
				 * CommonStaticItemListActivity.KEY_SELECTEDID, selectedids);
				 * intent.putExtras(bundle); startActivityForResult(intent,
				 * REQUESTCODE_DOMICILE);
				 */
			}
				break;

			case R.id.edulevel: {
				Intent intent = new Intent(mcontext,
						CommonStaticItemListActivity.class);
				intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
						DBUtils.GLOBALDATA_TYPE_EDUCATION);
				intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				Bundle bundle = new Bundle();

				String[] selectedids = { edudegreeid };
				bundle.putStringArray(
						CommonStaticItemListActivity.KEY_SELECTEDID,
						selectedids);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_EDUDEGREE);
			}
				break;
			case R.id.resume_edit_save:
			case R.id.selfinfo_save: {
				if (!checkCompleteSelfinfo()) {
					break;
				}
				// 友盟统计--简历个人资料--保存按钮
				UmShare.UmStatistics(mcontext,
						"ResumeEditPersonalInfo_SaveButton");

				if (getSubmitFlag()) {
					break;
				}
				mhandler.sendEmptyMessage(MSG_RESUMESUBMIT_START);

				readSelfinfo();

				JSONObject obj = mbaseEntity
						.resumeBaseinfoEntityToJSONObject(mbaseEntity);
				String str = obj.toString();
				int i = 0;
				ResumeUtils.submitData(mappcontext, resume_id, str,
						ResumeEntity.RESUME_BLOCK_BASE, mhandler);

			}
				break;
			case R.id.resume_personal_goback: {
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

			case MSG_RESUMESUBMIT_START:
				setSubmitFlag(true);
				mprogressDialog.setMessage(R.string.dialog_datasubmitmsg);
				mprogressDialog.show();
				break;
			case MSG_RESUMESUBMIT_OVER:
				setSubmitFlag(false);
				mprogressDialog.dismiss();
				break;

			case ResumeUtils.RESUME_GETGLOBALDATA: {
				GlobalDataTable table = (GlobalDataTable) msg.obj;
				switch (table.getType()) {
				case DBUtils.GLOBALDATA_TYPE_EDUCATION:
					medulevelBtn.setText(table.getName());

					break;
				case DBUtils.GLOBALDATA_TYPE_CITY:
					mcityBtn.setText(table.getName());
					break;

				}

			}
				break;
			case ResumeUtils.RESUME_SUBMIT_OK:
				ResumeSubmitResult result = (ResumeSubmitResult) msg.obj;
				resume_id = StringUtils.toInt(result.getResume_Id(), resume_id);
				UIHelper.ToastMessage(mcontext, R.string.dialog_save_success);
				setSubmitFlag(false);
				mhandler.sendEmptyMessage(MSG_RESUMESUBMIT_OVER);
				mbaseEntity.setUrl(result.getUrl());
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE_DOMICILE: {
				/*
				 * int type = data.getIntExtra(
				 * CommonStaticItemListActivity.KEY_SELETEDTYPE,
				 * CommonStaticItemListActivity.COMMON_SINGLESELECT); if (type
				 * == CommonStaticItemListActivity.COMMON_SINGLESELECT) { Bundle
				 * bundle = data.getExtras(); GlobalDataTable entity =
				 * (GlobalDataTable) bundle
				 * .getSerializable(CommonStaticItemListActivity
				 * .RESULTKEY_ENTITY); if (entity != null) { cityid =
				 * entity.getID(); mcityBtn.setText(entity.getName()); }
				 * 
				 * }
				 */
				ArrayList<CityChooseData> citydatas = (ArrayList<CityChooseData>) data
						.getSerializableExtra(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_DATA);
				if (null != citydatas && citydatas.size() > 0) {
					CityChooseData citydata = citydatas.get(0);
					cityid = citydata.getID();
					mcityBtn.setText(citydata.getName());
				}
			}
				break;
			case REQUESTCODE_EDUDEGREE: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						edudegreeid = entity.getID();
						medulevelBtn.setText(entity.getName());
					}

				}
			}
				break;
			}
		}

		if (resultCode == HeadhunterPublic.RESULT_CHOOSECITY_OK) {
			switch (requestCode) {
			case REQUESTCODE_DOMICILE: {
				/*
				 * int type = data.getIntExtra(
				 * CommonStaticItemListActivity.KEY_SELETEDTYPE,
				 * CommonStaticItemListActivity.COMMON_SINGLESELECT); if (type
				 * == CommonStaticItemListActivity.COMMON_SINGLESELECT) { Bundle
				 * bundle = data.getExtras(); GlobalDataTable entity =
				 * (GlobalDataTable) bundle
				 * .getSerializable(CommonStaticItemListActivity
				 * .RESULTKEY_ENTITY); if (entity != null) { cityid =
				 * entity.getID(); mcityBtn.setText(entity.getName()); }
				 * 
				 * }
				 */
				ArrayList<CityChooseData> citydatas = (ArrayList<CityChooseData>) data
						.getSerializableExtra(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_DATA);
				if (null != citydatas && citydatas.size() > 0) {
					CityChooseData citydata = citydatas.get(0);
					cityid = citydata.getID();
					mcityBtn.setText(citydata.getName());
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
								((ResumeEditPersonalInfoActivity) mcontext)
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
				mbaseEntity);
		intent.putExtras(bundle);
		setResult(resultCode, intent);
		((ResumeEditPersonalInfoActivity) mcontext).quit();
	}

	public void quit() {
		this.finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 友盟统计
		UmShare.UmResume(mcontext);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 友盟统计
		UmShare.UmPause(mcontext);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		mbaseEntity = (ResumeBaseinfoEntity) savedInstanceState
				.getSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
		if (mbaseEntity != null)
			resume_id = StringUtils.toInt(mbaseEntity.getResume_Id(), 0);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		readSelfinfo();// mbaseEntity
		outState.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
				mbaseEntity);
		super.onSaveInstanceState(outState);

	}

}

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.aps.s;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.CityChooseData;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.app.bean.ResumeBaseinfoEntity;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumeSubmitResult;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.ResumeUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

public class ResumeEditJobTargetActivity extends BaseActivity {
	private static final int REQUESTCODE_INDUSTRY = 503;
	private static final int REQUESTCODE_CITY = 504;
	private static final int REQUESTCODE_SALAYR = 505;
	private static final int REQUESTCODE_ARRIVEINTIME = 506;
	private static final int REQUESTCODE_JOBGETSTATUS = 507;

	private static final int RESUME_GETGLOBALDATA_SINGLE = 1;
	private static final int RESUME_GETGLOBALDATA_MULTI = 2;
	
	private static final int MAX_INPUT_LENGTH_500 = 500;

	private Context mcontext;
	private AppContext mappcontext;


	private ResumeBaseinfoEntity mbaseEntity;
	private int resume_id;

	private boolean bnew = false;

	private Button mgetSatusBtn;
	private Button mhopeIndustryBtn;
	private Button mhopeCityBtn;
	private Button mhopeSalaryBtn;
	private Button mhopeArrivedinBtn;
	private Button msaveBtn;
	private Button positionBtn;
	
	private EditText mhopeTagsEt;
	private EditText mhopeIntroduceEt;
	private EditText mprivatetitleEt;

	private String positionid = null;
	
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

		setContentView(R.layout.resume_jobtarget);
		if (savedInstanceState != null) {
			mbaseEntity = (ResumeBaseinfoEntity) savedInstanceState
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
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

		ImageButton back = (ImageButton) findViewById(R.id.resume_jobtarget_goback);
		back.setOnClickListener(mclicklistener);
		ImageButton msaveImgBtn = (ImageButton) findViewById(R.id.resume_edit_save);
		msaveImgBtn.setOnClickListener(mclicklistener);

		mgetSatusBtn = (Button) findViewById(R.id.job_status);
		mgetSatusBtn.setOnClickListener(mclicklistener);
		mhopeIndustryBtn = (Button) findViewById(R.id.hope_industry);
		mhopeIndustryBtn.setOnClickListener(mclicklistener);
		mhopeCityBtn = (Button) findViewById(R.id.hope_city);
		mhopeCityBtn.setOnClickListener(mclicklistener);
		mhopeSalaryBtn = (Button) findViewById(R.id.hope_salary);
		mhopeSalaryBtn.setOnClickListener(mclicklistener);
		mhopeArrivedinBtn = (Button) findViewById(R.id.hope_arrivedin);
		mhopeArrivedinBtn.setOnClickListener(mclicklistener);
		msaveBtn = (Button) findViewById(R.id.resume_jobtarget_submit);
		msaveBtn.setOnClickListener(mclicklistener);

		mhopeTagsEt = (EditText) findViewById(R.id.hope_position_tags);
		mhopeIntroduceEt = (EditText) findViewById(R.id.hope_introduction);
		mprivatetitleEt = (EditText) findViewById(R.id.privatetitle);
		
		positionBtn = (Button) findViewById(R.id.position_btn);
		positionBtn.setOnClickListener(mclicklistener);
		mhopeIntroduceEt.addTextChangedListener(mEditTextWatcher);
		/* if (!bnew) */{
			displaySelfinfo();
		}
	}
	TextWatcher mEditTextWatcher =new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			int length = s.length();
			if(length > MAX_INPUT_LENGTH_500){
				UIHelper.ToastMessage(mContext, "您输入内容过长！");
			}			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			

		}
	};
	private String jobSatusId = null;
	private String industryid = null;
	private String cityid = null;
	private String salaryid = null;
	private String arrivateinId = null;
	private ArrayList<String> jobtradeIdlist = new ArrayList<String>();
	private ArrayList<String> jobcityIdlist = new ArrayList<String>();

	private void displaySelfinfo() {

		Thread t = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				/*
				 * industryid = mbaseEntity.getjobtradeId(); if (industryid !=
				 * null && industryid.length() > 0) { GlobalDataTable table =
				 * GlobalDataTable.getTypeDataById( mappcontext,
				 * DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY, industryid); if (table
				 * != null) { Message msg = new Message(); msg.what =
				 * ResumeUtils.RESUME_GETGLOBALDATA; msg.obj = table;
				 * mhandler.sendMessage(msg); } }
				 */

				jobtradeIdlist = mbaseEntity.getJobtradeIdlist();
				if (jobtradeIdlist != null && jobtradeIdlist.size() > 0) {
					String industrys_str = new String();
					for (int i = 0; i < jobtradeIdlist.size(); i++) {
						GlobalDataTable table = GlobalDataTable
								.getGlobalDataTable(mappcontext,
										DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY,2,
										jobtradeIdlist.get(i));
						if (table != null) {
							if (industrys_str.length() > 0)
								industrys_str += ",";
							industrys_str += table.getName();
						}
					}

					if (industrys_str != null && industrys_str.length() > 0) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = industrys_str;
						msg.arg1 = RESUME_GETGLOBALDATA_MULTI;
						msg.arg2 = DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY;
						mhandler.sendMessage(msg);
					}
				}
				/*
				 * cityid = mbaseEntity.getjobcityId(); if (cityid != null &&
				 * cityid.length() > 0) { GlobalDataTable table =
				 * GlobalDataTable.getTypeDataById( mappcontext,
				 * DBUtils.GLOBALDATA_TYPE_CITY, cityid); if (table != null) {
				 * Message msg = new Message(); msg.what =
				 * ResumeUtils.RESUME_GETGLOBALDATA; msg.obj = table;
				 * mhandler.sendMessage(msg); } }
				 */
				jobcityIdlist = mbaseEntity.getJobcityIdlist();
				if (jobcityIdlist != null && jobcityIdlist.size() > 0) {
					String citys_str = new String();
					for (int i = 0; i < jobcityIdlist.size(); i++) {
						GlobalDataTable table = GlobalDataTable
								.getTypeDataById(mappcontext,
										DBUtils.GLOBALDATA_TYPE_CITY,
										jobcityIdlist.get(i));
						if (table != null) {
							if (citys_str.length() > 0)
								citys_str += ",";
							citys_str += table.getName();
						}
					}

					if (citys_str != null && citys_str.length() > 0) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = citys_str;
						msg.arg1 = RESUME_GETGLOBALDATA_MULTI;
						mhandler.sendMessage(msg);
					}
				}
				salaryid = mbaseEntity.getjobsalaryId();
				if (salaryid != null && salaryid.length() > 0) {
					GlobalDataTable table = GlobalDataTable.getTypeDataById(
							mappcontext, DBUtils.GLOBALDATA_TYPE_SALARY,
							salaryid);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}
				}

				arrivateinId = mbaseEntity.getarrivaldateId();
				if (arrivateinId != null && arrivateinId.length() > 0) {
					GlobalDataTable table = GlobalDataTable.getTypeDataById(
							mappcontext, DBUtils.GLOBALDATA_TYPE_ARRIVETIME,
							arrivateinId);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}
				}

				jobSatusId = mbaseEntity.getjobgetstatusId();
				if (jobSatusId != null && jobSatusId.length() > 0) {

					GlobalDataTable table = GlobalDataTable.getTypeDataById(
							mappcontext, DBUtils.GLOBALDATA_TYPE_JOBSTATUS,
							jobSatusId);
					if (table != null) {
						Message msg = new Message();
						msg.what = ResumeUtils.RESUME_GETGLOBALDATA;
						msg.obj = table;
						mhandler.sendMessage(msg);
					}

				}
				positionid = mbaseEntity.getPosition_id();
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

			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);

		String hopetags = mbaseEntity.getJobkey();
		if (hopetags != null && hopetags.length() > 0)
			mhopeTagsEt.setText(hopetags);
		String introduction = mbaseEntity.getSelfmemo();
		if (introduction != null && introduction.length() > 0)
			mhopeIntroduceEt.setText(introduction);
		String selftitle = mbaseEntity.getPersontitle();
		if (selftitle != null && selftitle.length() > 0) {
			mprivatetitleEt.setText(selftitle);
		}

	}

	private void readjobinfo() {

		String hopetags = mhopeTagsEt.getText().toString();
		if (hopetags != null)
			mbaseEntity.setJobkey(hopetags);

		String introduction = mhopeIntroduceEt.getText().toString();
		if (introduction != null)
			mbaseEntity.setSelfmemo(introduction);

		if (jobSatusId != null)
			mbaseEntity.setjobgetstatusId(jobSatusId);
		/*
		 * if (industryid != null) mbaseEntity.setjobtradeId(industryid);
		 */
		if(positionid != null)
			mbaseEntity.setPosition_id(positionid);
		if (jobtradeIdlist != null)
			mbaseEntity.setJobtradeIdlist(jobtradeIdlist);
		/*
		 * if (cityid != null) mbaseEntity.setjobcityId(cityid);
		 */
		if (jobcityIdlist != null)
			mbaseEntity.setJobcityIdlist(jobcityIdlist);
		if (salaryid != null)
			mbaseEntity.setjobsalaryId(salaryid);
		if (arrivateinId != null)
			mbaseEntity.setarrivaldateId(arrivateinId);
		String selftitle = mprivatetitleEt.getText().toString();
		if (selftitle != null)
			mbaseEntity.setPersontitle(selftitle);
	}

	private boolean checkComplete() {

		boolean bcomplete = true;
		if (bcomplete && jobSatusId != null && jobSatusId.length() > 0)
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请选择求职状态");
			bcomplete = false;
			return bcomplete;
		}

		//String selftitle = mprivatetitleEt.getText().toString();
		if (bcomplete && positionid != null && positionid.length() > 0 && !positionid.equals("0"))
			bcomplete = true;
		else {
			UIHelper.ToastMessage(mcontext, "请选择期望职位");
			mprivatetitleEt.requestFocus();
			bcomplete = false;
			return bcomplete;
		}

		if (jobtradeIdlist != null && jobtradeIdlist.size() > 0) {
			bcomplete = true;
		} else {
			UIHelper.ToastMessage(mcontext, "请选择期望行业");
			mprivatetitleEt.requestFocus();
			bcomplete = false;
			return bcomplete;
		}
		if (jobcityIdlist != null && jobcityIdlist.size() > 0) {
			bcomplete = true;
		} else {
			UIHelper.ToastMessage(mcontext, "请选择期望地区");
			mprivatetitleEt.requestFocus();
			bcomplete = false;
			return bcomplete;
		}

		return bcomplete;

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
				int datasize = msg.arg1;

				if (datasize == RESUME_GETGLOBALDATA_MULTI) {
					String info = (String) msg.obj;
					switch (msg.arg2) {
					case DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY:
						mhopeIndustryBtn.setText(info);
						break;
					case DBUtils.GLOBALDATA_TYPE_CITY:
						mhopeCityBtn.setText(info);
						break;

					}
				} else {
					GlobalDataTable table = (GlobalDataTable) msg.obj;
					switch (table.getType()) {
					case DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY:
						mhopeIndustryBtn.setText(table.getName());
						break;
					case DBUtils.GLOBALDATA_TYPE_CITY:
						mhopeCityBtn.setText(table.getName());
						break;
					case DBUtils.GLOBALDATA_TYPE_SALARY:
						mhopeSalaryBtn.setText(table.getName());
						break;
					case DBUtils.GLOBALDATA_TYPE_ARRIVETIME:
						mhopeArrivedinBtn.setText(table.getName());
						break;
					case DBUtils.GLOBALDATA_TYPE_JOBSTATUS:
						mgetSatusBtn.setText(table.getName());
						break;
					case DBUtils.GLOBALDATA_TYPE_JOBFUNCTION: {
						positionBtn.setText(table.getName());
					}
					}
				}

			}
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
	OnClickListener mclicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.hope_industry: {

				Bundle bundle = new Bundle();

				bundle.putInt(
						HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_TYPE,
						HeadhunterPublic.INDUSTRYCHOOSE_TYPE_MULTIPLE);
				bundle.putInt(HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_NUM,
						5);
				ArrayList<OneLevelChooseData> industrydatas = new ArrayList<OneLevelChooseData>();

				for (int i = 0; i < jobtradeIdlist.size(); i++) {
					OneLevelChooseData industrydata = new OneLevelChooseData();
					industrydata.setID(jobtradeIdlist.get(i));
					industrydata.setIsChoose(true);
					industrydatas.add(industrydata);
				}

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

				/*
				 * Intent intent = new Intent(); intent.setClass(mcontext,
				 * IndustryChooseActivity.class); startActivityForResult(intent,
				 * REQUESTCODE_INDUSTRY);
				 */
			}
				break;
			case R.id.hope_city: {
				// 数据传输
				Bundle bundle = new Bundle();
				// 设置为多选
				bundle.putInt(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_TYPE,
						HeadhunterPublic.CITYCHOOSE_TYPE_MULTIPLE);
				bundle.putInt(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_NUM, 5);
				ArrayList<CityChooseData> citydatas = new ArrayList<CityChooseData>();
				for (int i = 0; i < jobcityIdlist.size(); i++) {
					CityChooseData citydata = new CityChooseData();
					citydata.setID(jobcityIdlist.get(i));
					citydata.setIsChoose(true);
					citydatas.add(citydata);
				}

				bundle.putSerializable(
						HeadhunterPublic.CITYCHOOSE_DATATRANSFER_DATA,
						citydatas);

				// 进入城市选择界面
				Intent intent = new Intent();
				intent.setClass(mcontext, CityChooseActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_CITY);

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
				 * REQUESTCODE_CITY);
				 */

			}
				break;

			case R.id.hope_salary: {
				Intent intent = new Intent(mcontext,
						CommonStaticItemListActivity.class);
				intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
						DBUtils.GLOBALDATA_TYPE_SALARY);
				intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				Bundle bundle = new Bundle();

				String[] selectedids = { salaryid };
				bundle.putStringArray(
						CommonStaticItemListActivity.KEY_SELECTEDID,
						selectedids);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_SALAYR);
			}
				break;

			case R.id.hope_arrivedin: {
				Intent intent = new Intent(mcontext,
						CommonStaticItemListActivity.class);
				intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
						DBUtils.GLOBALDATA_TYPE_ARRIVETIME);
				intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				Bundle bundle = new Bundle();

				String[] selectedids = { arrivateinId };
				bundle.putStringArray(
						CommonStaticItemListActivity.KEY_SELECTEDID,
						selectedids);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_ARRIVEINTIME);
			}
				break;
			case R.id.job_status: {
				Intent intent = new Intent(mcontext,
						CommonStaticItemListActivity.class);
				intent.putExtra(CommonStaticItemListActivity.KEY_DATATYPE,
						DBUtils.GLOBALDATA_TYPE_JOBSTATUS);
				intent.putExtra(CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				Bundle bundle = new Bundle();

				String[] selectedids = { jobSatusId };
				bundle.putStringArray(
						CommonStaticItemListActivity.KEY_SELECTEDID,
						selectedids);
				intent.putExtras(bundle);
				startActivityForResult(intent, REQUESTCODE_JOBGETSTATUS);
			}
				break;
			case R.id.position_btn: {
				Bundle bundle = new Bundle();
				if(positionid != null && !positionid.equalsIgnoreCase("0"))
					bundle.putString(HeadhunterPublic.JOBFUNCTIONCHOOSE_DATA, positionid);

				Intent intent = new Intent();
				intent.setClass(mcontext, JobFunctionsChooseActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent,
						HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
			break;
			case R.id.resume_edit_save:
			case R.id.resume_jobtarget_submit: {
				if (!checkComplete()) {
					break;
				}
				// 友盟统计--简历求职意向--保存按钮
				UmShare.UmStatistics(mcontext,
						"ResumeEditJobTarget_SubmitButton");

				if (getSubmitFlag()) {
					break;
				}
				mhandler.sendEmptyMessage(MSG_RESUMESUBMIT_START);
				readjobinfo();
				JSONObject obj = mbaseEntity
						.resumeBaseinfoEntityToJSONObject(mbaseEntity);
				String str = obj.toString();
				int i = 0;
				ResumeUtils.submitData(mappcontext, resume_id, str,
						ResumeEntity.RESUME_BLOCK_BASE, mhandler);

			}
				break;
			case R.id.resume_jobtarget_goback: {
				showQuitAlertDialog();
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
						mhopeIndustryBtn.setText(entity.getName());
					}
				}
			}
				break;

			case REQUESTCODE_ARRIVEINTIME: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						arrivateinId = entity.getID();
						mhopeArrivedinBtn.setText(entity.getName());
					}
				}
			}
				break;

			case REQUESTCODE_JOBGETSTATUS: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						jobSatusId = entity.getID();
						mgetSatusBtn.setText(entity.getName());
					}
				}
			}
				break;
			case REQUESTCODE_SALAYR: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						salaryid = entity.getID();
						mhopeSalaryBtn.setText(entity.getName());
					}
				}
			}
				break;

			case REQUESTCODE_CITY: {
				int type = data.getIntExtra(
						CommonStaticItemListActivity.KEY_SELETEDTYPE,
						CommonStaticItemListActivity.COMMON_SINGLESELECT);
				if (type == CommonStaticItemListActivity.COMMON_SINGLESELECT) {
					Bundle bundle = data.getExtras();
					GlobalDataTable entity = (GlobalDataTable) bundle
							.getSerializable(CommonStaticItemListActivity.RESULTKEY_ENTITY);
					if (entity != null) {
						cityid = entity.getID();
						mhopeCityBtn.setText(entity.getName());
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
					ArrayList<String> newindustryIds = new ArrayList<String>();
					String industrystr = new String();
					for (int i = 0; i < industrydatas.size(); i++) {
						OneLevelChooseData industrydata = industrydatas.get(i);
						newindustryIds.add(industrydata.getID());
						if (industrystr.length() > 0)
							industrystr += ",";
						industrystr += industrydata.getName();
					}
					jobtradeIdlist = newindustryIds;
					mhopeIndustryBtn.setText(industrystr);

				}
				/*
				 * if (null != industrydatas && industrydatas.size() > 0) {
				 * OneLevelChooseData industrydata = industrydatas.get(0);
				 * 
				 * industryid = industrydata.getID();
				 * mhopeIndustryBtn.setText(industrydata.getName()); }
				 */
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
		if (resultCode == HeadhunterPublic.RESULT_CHOOSECITY_OK) {
			switch (requestCode) {
			case REQUESTCODE_CITY: {
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
					ArrayList<String> newcityIds = new ArrayList<String>();
					String citys_str = new String();
					for (int i = 0; i < citydatas.size(); i++) {
						CityChooseData citydata = citydatas.get(i);
						newcityIds.add(citydata.getID());
						if (citys_str.length() > 0)
							citys_str += ",";
						citys_str += citydata.getName();
					}
					jobcityIdlist = newcityIds;
					mhopeCityBtn.setText(citys_str);

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
								((ResumeEditJobTargetActivity) mcontext).quit();
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
		((ResumeEditJobTargetActivity) mcontext).quit();
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
				.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
		if (mbaseEntity != null)
			resume_id = StringUtils.toInt(mbaseEntity.getResume_Id(), 0);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		readjobinfo();// mbaseEntity
		outState.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
				mbaseEntity);
		super.onSaveInstanceState(outState);

	}
}

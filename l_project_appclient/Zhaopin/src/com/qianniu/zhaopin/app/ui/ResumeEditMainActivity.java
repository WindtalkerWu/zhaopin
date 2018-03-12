package com.qianniu.zhaopin.app.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.Entity;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.ResumeBaseinfoEntity;
import com.qianniu.zhaopin.app.bean.ResumeEducationExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumeJobExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeLanguageExpEntity;
import com.qianniu.zhaopin.app.bean.ResumePicSubmitResult;
import com.qianniu.zhaopin.app.bean.ResumeProjectExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeQuickItemEntity;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.bean.ResumeSubmitResult;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.ResumeUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.app.view.ResumeBriefTagItem.OnExecuteAct;
import com.qianniu.zhaopin.app.view.ResumeMultiTagBriefItem;
import com.qianniu.zhaopin.app.view.ResumeMultiTagBriefItem.OnMultiBirefItemAct;
import com.qianniu.zhaopin.app.view.ResumeSigleTagBriefItem;
import com.qianniu.zhaopin.app.view.ResumeSigleTagBriefItem.OnSingleBirefItemAct;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ResumeEditMainActivity extends BaseActivity {
	private static final int RESUME_RETURN_SELFINFO = 3001;
	private static final int RESUME_RETURN_JOBINTENSION = 3002;
	private static final int RESUME_RETURN_WORKEXP = 3003;
	private static final int RESUME_RETURN_PROJECTEXP = 3004;
	private static final int RESUME_RETURN_EDUCATION = 3005;
	private static final int RESUME_RETURN_LANGUAGE = 3006;
	private static final int RESUME_RETURN_DEATAIL = 3007;
	private static final int RESUME_RETURN_FASTCONTENT = 3008;

	public static final String INTENT_KEY_SERIALIZE = "entitiy";
	public static final String INTENT_KEY_RESUMEID = "resumeid";
	public static final String INTENT_KEY_PARCELABLEARRAY = "ParcelableArray";
	public static final String INTENT_KEY_DATATYPE = "datatype";
	public static final String INTENT_KEY_NEWFLAG = "bnew";
	public static final String INTENT_KEY_EDITFLAG = "bedit";

	public static final int INTENT_DATATYPE_WORKEXP = 1;
	public static final int INTENT_DATATYPE_PROJECTEXP = 2;
	public static final int INTENT_DATATYPE_EDUCATION = 3;
	public static final int INTENT_DATATYPE_LANGUAGE = 4;
	public static final int INTENT_DATATYPE_JOBINTENSION = 5;
	public static final int INTENT_DATATYPE_FASTCONTENT = 6;
	public static final int INTENT_DATATYPE_SELFINFO = 7;

	/** 通过图库获取图片 */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	/** 照相机拍照获取图片 */
	private static final int CAMERA_WITH_DATA = 3022;
	private static final int ICON_SIZE = 96;
	/**
	 * 照相机拍摄照片转化为该File对象
	 */
	private File mCurrentPhotoFile;
	private String mNetpicBaseUrl = URLs.RESUME_PICTURE_BASESUBMIT;
	/**
	 * 使用照相机拍摄照片作为头像时会使用到这个路径
	 */
	public static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/matrix/Camera");

	// handler 处理的事件类型

	private static final int MSG_LOADDATA_OK = 0;
	private static final int MSG_LOADDATA_ERROR = 21;
	private static final int MSG_LOADDATA_APPEXCEPTION = 22;

	private static final int MSG_DATALOAD_START = 1;
	private static final int MSG_DATALOAD_OVER = 2;

	private static final int MSG_PHOTOSUBMIT_START = 3;
	private static final int MSG_PHOTOSUBMIT_OVER = 4;

	private static final int MSG_RESUMESUBMIT_START = 5;
	private static final int MSG_RESUMESUBMIT_OVER = 6;

	// 完成度占比
	private static final int DEGREE_HEADPHOTO = 10;
	private static final int DEGREE_SELFINFO = 30;
	private static final int DEGREE_JOBINTENSION = 20;
	private static final int DEGREE_JOBEXP = 10;
	private static final int DEGREE_PROJECTEXP = 10;
	private static final int DEGREE_EDUCATIONXP = 10;
	private static final int DEGREE_LANGUAGEEXP = 10;

	private Context mcontext;
	private AppContext mappcontext;

	private int resume_id;
	private ProgressDialog mprogressDialog;
	private BitmapManager bmpManager;
	private ResumeEntity mresumeEntity;
	private int mcompletedegree = 0;

	private ResumeSigleTagBriefItem mFastContentItem;
	private ResumeSigleTagBriefItem mJobIntensionItem;
	private ResumeMultiTagBriefItem mWorkExperienceItem;
	private ResumeMultiTagBriefItem mProjectItem;
	private ResumeMultiTagBriefItem mEducationItem;
	private ResumeMultiTagBriefItem mLanguageItem;

	private ImageView completeImgview;
	private ImageView photoImgview;
	private ImageView defaultresumeIv;
	private TextView nameText;
	private TextView hopeJobText;
	private ViewGroup mSelfinfoLayout;

	private ImageButton mbackImgBtn;
	private ImageView msaveBtn;
	private ImageButton mpreviewBtn;

	private boolean bedit = false;
	private boolean bsubmitFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		mappcontext = (AppContext) this.getApplication();
		// 友盟统计
		UmShare.UmsetDebugMode(mcontext);
		bmpManager = new BitmapManager();

		if (savedInstanceState != null) {
			mresumeEntity = (ResumeEntity) savedInstanceState
					.get(ResumeEditMainActivity.INTENT_KEY_SERIALIZE);
			if (mresumeEntity != null)
				resume_id = StringUtils.toInt(mresumeEntity.getResume_Id(), 0);

		} else {
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			resume_id = getIntent().getIntExtra("resume_id", 0);

		}
		if (mresumeEntity == null)
			mresumeEntity = new ResumeEntity();
		if (resume_id != 0) {
			loadResumeData(mappcontext, resume_id, mhandler);
		}

		setContentView(R.layout.resume_edit_main_activity);
		mprogressDialog = new ProgressDialog(this);

		mSelfinfoLayout = (ViewGroup) findViewById(R.id.resume_selfinfo_layout);
		nameText = (TextView) findViewById(R.id.resume_name_tv);
		hopeJobText = (TextView) findViewById(R.id.resume_job_tv);
		completeImgview = (ImageView) findViewById(R.id.complete_imgview);
		photoImgview = (ImageView) findViewById(R.id.headphoto_imgview);
		defaultresumeIv = (ImageView) findViewById(R.id.default_imgview);

		mbackImgBtn = (ImageButton) findViewById(R.id.resume_edit_goback);
		msaveBtn = (ImageView) findViewById(R.id.resume_edit_save);
		mpreviewBtn = (ImageButton) findViewById(R.id.resume_preview);

		mFastContentItem = (ResumeSigleTagBriefItem) findViewById(R.id.resume_fastcontent_item);
		mJobIntensionItem = (ResumeSigleTagBriefItem) findViewById(R.id.resume_jobintension_item);
		mWorkExperienceItem = (ResumeMultiTagBriefItem) findViewById(R.id.resume_workexperience_item);
		mProjectItem = (ResumeMultiTagBriefItem) findViewById(R.id.resume_project_item);
		mEducationItem = (ResumeMultiTagBriefItem) findViewById(R.id.resume_education_item);
		mLanguageItem = (ResumeMultiTagBriefItem) findViewById(R.id.resume_language_item);

		initView();
	}

	private void initView() {

		mbackImgBtn.setOnClickListener(mlistener);
		msaveBtn.setOnClickListener(mlistener);
		mpreviewBtn.setOnClickListener(mlistener);

		// 头像
		Bitmap photobmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.person_photo_edit_big);
		photobmp = ImageUtils.createRoundHeadPhoto(this, photobmp);
		photoImgview.setImageBitmap(photobmp);
		photoImgview.setOnClickListener(mlistener);
		// 完成度
		displayCompleteDegree(mresumeEntity);

		mSelfinfoLayout.setOnClickListener(mlistener);

		Entity baseInfoentity = mresumeEntity.getBaseinfoEntity();
		List<Entity> BaseinfoList = new ArrayList<Entity>();
		BaseinfoList.add(baseInfoentity);

		mFastContentItem.initView(INTENT_DATATYPE_FASTCONTENT, null);
		mFastContentItem.setSymbol(R.drawable.resume_point_fastcontent);
		mFastContentItem.setPrompt(R.string.resume_guide_fastcontent);
		mFastContentItem.setSubTitleVisible(View.INVISIBLE);
		mFastContentItem.setOnAddClick(singleItemact);

		mJobIntensionItem.initView(INTENT_DATATYPE_JOBINTENSION, null);
		mJobIntensionItem.setSymbol(R.drawable.resume_point_jobintension);
		mJobIntensionItem.setPrompt(R.string.resume_guide_jobintension);
		mJobIntensionItem.setOnAddClick(singleItemact);

		mWorkExperienceItem.initView(INTENT_DATATYPE_WORKEXP,
				mresumeEntity.getmJobexpEntityList());
		mWorkExperienceItem.setSymbol(R.drawable.resume_point_workexp);
		mWorkExperienceItem.setPrompt(R.string.resume_guide_workexp);
		mWorkExperienceItem.setOnAddClick(multiItemAct);
		mWorkExperienceItem.setOnExecuteAct(tagExecuteAct);

		mProjectItem.initView(INTENT_DATATYPE_PROJECTEXP,
				mresumeEntity.getmJobexpEntityList());
		mProjectItem.setSymbol(R.drawable.resume_point_project);
		mProjectItem.setPrompt(R.string.resume_guide_project);
		mProjectItem.setOnAddClick(multiItemAct);
		mProjectItem.setOnExecuteAct(tagExecuteAct);

		mEducationItem.initView(INTENT_DATATYPE_EDUCATION,
				mresumeEntity.getMeducationExpEntityList());
		mEducationItem.setSymbol(R.drawable.resume_point_education);
		mEducationItem.setPrompt(R.string.resume_guide_education);
		mEducationItem.setOnAddClick(multiItemAct);
		mEducationItem.setOnExecuteAct(tagExecuteAct);

		mLanguageItem.initView(INTENT_DATATYPE_LANGUAGE,
				mresumeEntity.getMlanguageExpEntityList());
		mLanguageItem.setSymbol(R.drawable.resume_point_language);
		mLanguageItem.setPrompt(R.string.resume_guide_language);
		mLanguageItem.setOnAddClick(multiItemAct);
		mLanguageItem.setOnExecuteAct(tagExecuteAct);
	}

	private OnMultiBirefItemAct multiItemAct = new OnMultiBirefItemAct() {

		@Override
		public void OnAddClick(int type, Context context) {
			boolean bcompleted = ResumeBaseinfoEntity
					.isCompletedForSelfInfo(mresumeEntity.getBaseinfoEntity());
			if (!bcompleted) {
				UIHelper.ToastMessage(mcontext, "请先完善个人资料！");

			} else {
				startMultiInfoItemResumeEdit(null, type);
			}
		}
	};
	private OnSingleBirefItemAct singleItemact = new OnSingleBirefItemAct() {

		@Override
		public void OnAddClick(int type, Context context) {
			// TODO Auto-generated method stub
			boolean bcompleted = ResumeBaseinfoEntity
					.isCompletedForSelfInfo(mresumeEntity.getBaseinfoEntity());
			if (!bcompleted) {
				UIHelper.ToastMessage(mcontext, "请先完善个人资料！");

			} else {
				switch (type) {
				case ResumeEditMainActivity.INTENT_DATATYPE_FASTCONTENT: {
					startSingleInfoItemResumeEdit(
							mresumeEntity.getQucikEntity(), type);
				}
					break;
				case ResumeEditMainActivity.INTENT_DATATYPE_JOBINTENSION: {
					startSingleInfoItemResumeEdit(
							mresumeEntity.getBaseinfoEntity(), type);
				}
					break;

				default:
					break;
				}
			}

		}
	};

	private OnExecuteAct tagExecuteAct = new OnExecuteAct() {

		@Override
		public void editAction(Entity entity, int type) {
			// TODO Auto-generated method stub
			startMultiInfoItemResumeEdit(entity, type);
		}

		@Override
		public void delAction(Entity entity, int type, View view) {
			// TODO Auto-generated method stub
			if (entity != null) {
				TagInfo taginfo = new TagInfo(entity, type, view);
				showDelAlertDialog(taginfo);
			}
		}
	};

	private void showDelAlertDialog(TagInfo taginfo) {
		final TagInfo deltaginfo = taginfo;
		Dialog dialog = MethodsCompat
				.getAlertDialogBuilder(this, AlertDialog.THEME_HOLO_LIGHT)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.dialog_delete_title)
				.setMessage(R.string.dialog_delete_msg)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								new DelBlockItemAsyncTask().execute(deltaginfo);
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

	private void delItemViewInMultiInfoContainer(int type, View view) {

		if (view == null)
			return;
		switch (type) {

		case INTENT_DATATYPE_LANGUAGE: {
			mLanguageItem.removeBriefTag(view);

		}
			break;
		case INTENT_DATATYPE_EDUCATION: {
			mEducationItem.removeBriefTag(view);
		}
			break;
		case INTENT_DATATYPE_PROJECTEXP: {
			mProjectItem.removeBriefTag(view);
		}
			break;
		case INTENT_DATATYPE_WORKEXP: {
			mWorkExperienceItem.removeBriefTag(view);
		}
			break;
		default:
			break;
		}

	}

	private class TagInfo {

		public Entity mentity;
		public int mtype;
		public View mview;

		public TagInfo(Entity entity, int type, View view) {
			super();
			// TODO Auto-generated constructor stub
			this.mentity = entity;
			this.mtype = type;
			this.mview = view;
		}

	}

	private class DelBlockItemAsyncTask extends
			AsyncTask<TagInfo, Void, ResumeSubmitResult> {
		TagInfo taginfo = null;

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			mprogressDialog.show();
		}

		@Override
		protected ResumeSubmitResult doInBackground(TagInfo... params) {
			// TODO Auto-generated method stub

			taginfo = params[0];
			if (taginfo == null)
				return null;

			String jsonstring = null;
			int resumeblock = -1;
			setBlockInfoToDelState(taginfo.mentity, taginfo.mtype);
			int id = StringUtils.toInt(mresumeEntity.getResume_Id(), 0);
			switch (taginfo.mtype) {

			case INTENT_DATATYPE_LANGUAGE: {
				JSONObject obj = ResumeLanguageExpEntity
						.resumeLanguageExpEntityToJSONObject(((ResumeLanguageExpEntity) (taginfo.mentity)));
				jsonstring = obj.toString();
				resumeblock = ResumeEntity.RESUME_BLOCK_LANGUAGES;

			}
				break;
			case INTENT_DATATYPE_EDUCATION: {
				JSONObject obj = ResumeEducationExpEntity
						.resumeEducationExpEntityToJSONObject(((ResumeEducationExpEntity) (taginfo.mentity)));
				jsonstring = obj.toString();
				resumeblock = ResumeEntity.RESUME_BLOCK_EDUCATIONS;

			}
				break;
			case INTENT_DATATYPE_PROJECTEXP: {
				JSONObject obj = ResumeProjectExpEntity
						.resumeProjectExpEntityToJSONObject(((ResumeProjectExpEntity) (taginfo.mentity)));
				jsonstring = obj.toString();
				resumeblock = ResumeEntity.RESUME_BLOCK_PROJECTS;

			}
				break;
			case INTENT_DATATYPE_WORKEXP: {
				JSONObject obj = ResumeJobExpEntity
						.resumeJobExpEntityToJSONObject(((ResumeJobExpEntity) (taginfo.mentity)));
				jsonstring = obj.toString();
				resumeblock = ResumeEntity.RESUME_BLOCK_JOBS;

			}
				break;
			default:
				break;
			}
			ResumeSubmitResult result = ResumeUtils.submitDataNoThread(
					mappcontext, id, jsonstring, resumeblock);
			return result;
		}

		@Override
		protected void onPostExecute(ResumeSubmitResult result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null && result.getValidate() != null
					&& result.getValidate().OK()) {

				UIHelper.ToastMessage(mcontext, R.string.dialog_delete_success);
				if (taginfo != null)
					delItemViewInMultiInfoContainer(taginfo.mtype,
							taginfo.mview);

			} else {
				setBlockInfoToNormalState(taginfo.mentity, taginfo.mtype);
				UIHelper.ToastMessage(mcontext, R.string.dialog_submit_failed);
			}
			mprogressDialog.dismiss();
		}

	}

	private boolean setBlockInfoToDelState(Entity entity, int type) {

		if (entity == null)
			return false;
		switch (type) {

		case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
			((ResumeLanguageExpEntity) entity)
					.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_DEL);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
			((ResumeEducationExpEntity) entity)
					.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_DEL);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
			((ResumeProjectExpEntity) entity)
					.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_DEL);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
			((ResumeJobExpEntity) entity)
					.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_DEL);
		}
			break;
		default:
			break;
		}
		return true;
	}

	private boolean setBlockInfoToNormalState(Entity entity, int type) {

		if (entity == null)
			return false;
		switch (type) {

		case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
			((ResumeLanguageExpEntity) entity)
					.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_NORMAL);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
			((ResumeEducationExpEntity) entity)
					.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_NORMAL);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
			((ResumeProjectExpEntity) entity)
					.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_NORMAL);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
			((ResumeJobExpEntity) entity)
					.setStatus(ResumeEntity.RESUME_BLOCKSTATUS_NORMAL);
		}
			break;
		default:
			break;
		}
		return true;
	}

	private void startSingleInfoItemResumeEdit(Entity entity, int type) {
		Intent intent = null;
		switch (type) {
		case INTENT_DATATYPE_FASTCONTENT: {
			intent = new Intent(mcontext, ResumeEditQuickActivity.class);
		}
			break;
		case INTENT_DATATYPE_JOBINTENSION: {

			intent = new Intent(mcontext, ResumeEditJobTargetActivity.class);
		}
			break;

		default:
			break;
		}
		if (intent == null)
			return;
		Bundle bundle = new Bundle();
		bundle.putInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
		switch (type) {
		case INTENT_DATATYPE_FASTCONTENT: {
			if (entity == null) {
				entity = new ResumeQuickItemEntity();
			}
			bundle.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
					entity);

		}
			break;
		case INTENT_DATATYPE_JOBINTENSION: {
			if (entity == null) {
				entity = new ResumeBaseinfoEntity();
			}
			bundle.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
					entity);

		}
			break;

		default:
			break;
		}

		intent.putExtras(bundle);
		intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_NEWFLAG, true);
		switch (type) {
		case INTENT_DATATYPE_FASTCONTENT: {
			startActivityForResult(intent, RESUME_RETURN_FASTCONTENT);
		}
			break;
		case INTENT_DATATYPE_JOBINTENSION: {
			startActivityForResult(intent, RESUME_RETURN_JOBINTENSION);
		}
			break;

		default:
			break;
		}

	}

	private void startMultiInfoItemResumeEdit(Entity entity, int type) {
		Intent intent = null;
		switch (type) {
		case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
			intent = new Intent(mcontext, ResumeEditLanguageItemActivity.class);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {

			intent = new Intent(mcontext, ResumeEditEducationItemActivity.class);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
			intent = new Intent(mcontext, ResumeEditProjectItemActivity.class);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {

			intent = new Intent(mcontext, ResumeEditWorkExpItemActivity.class);
		}
			break;
		default:
			break;
		}
		if (intent == null)
			return;
		Bundle bundle = new Bundle();
		bundle.putInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
		switch (type) {
		case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
			if (entity == null) {
				entity = new ResumeLanguageExpEntity();
			}
			bundle.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
					entity);

		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
			if (entity == null) {
				entity = new ResumeEducationExpEntity();
			}
			bundle.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
					entity);

		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
			if (entity == null) {
				entity = new ResumeProjectExpEntity();
			}
			bundle.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
					entity);

		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
			if (entity == null) {
				entity = new ResumeJobExpEntity();
			}
			bundle.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
					entity);

		}
			break;
		default:
			break;
		}

		intent.putExtras(bundle);
		intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_NEWFLAG, true);
		switch (type) {
		case ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE: {
			startActivityForResult(intent, RESUME_RETURN_LANGUAGE);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION: {
			startActivityForResult(intent, RESUME_RETURN_EDUCATION);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP: {
			startActivityForResult(intent, RESUME_RETURN_PROJECTEXP);
		}
			break;
		case ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP: {
			startActivityForResult(intent, RESUME_RETURN_WORKEXP);
		}
			break;
		default:
			break;
		}

	}

	private void displayResumeEntity(ResumeEntity entity) {
		displayCompleteDegree(mresumeEntity);
		displayFastContet(mresumeEntity);
		displaySelfInfo(mresumeEntity);
		displayJobIntension(mresumeEntity);
		displayEducationExp(mresumeEntity);
		displayWorkExp(mresumeEntity);
		displayLanguage(mresumeEntity);
		displayProjectExp(mresumeEntity);
	}

	private void displayCompleteDegree(ResumeEntity entity) {
		if (entity == null)
			return;
		int percent = entity.getBaseinfoEntity().getPercent();
		/*
		 * Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(this,
		 * entity .getBaseinfoEntity().getPercent());
		 */
		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(this,
				percent, Constants.ResumeModule.COMPLETEBMP_SMALL_RADIUS,
				Constants.ResumeModule.COMPLETEBMP_SMALL_BODER_WIDTH,
				Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
				Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
		completeImgview.setImageBitmap(completebmp);
	}

	private void displaySelfInfo(ResumeEntity entity) {
		int completedegree = 0;
		ResumeBaseinfoEntity baseinfo = mresumeEntity.getBaseinfoEntity();
		if (baseinfo != null && resume_id > 0) {
			String name = baseinfo.getName();
			if (name != null && name.length() > 0) {
				nameText.setText(name);
			}

			String title = baseinfo.getPersontitle();
			if (title != null && title.length() > 0) {
				hopeJobText.setText(title);
			}

			String picurl = baseinfo.getHeadphoto();
			if (picurl != null && picurl.length() > 0) {
				setHeadPhotoToView(picurl, photoImgview,
						R.drawable.person_photo_edit_big);

			}
			int defaultstatus = ResumeSimpleEntity.RESUME_DEFAULT_UNSELECTED;
			if (defaultstatus == ResumeSimpleEntity.RESUME_DEFAULT_UNSELECTED)
				defaultresumeIv.setImageResource(R.drawable.default_normal);
			else
				defaultresumeIv.setImageResource(R.drawable.default_light);
		}
	}

	private int judgeResumeCompleteDegree(ResumeEntity entity) {

		int completedegree = 0;
		if (entity == null)
			return 0;

		ResumeBaseinfoEntity baseinfo = entity.getBaseinfoEntity();
		if (baseinfo != null) {

			boolean bcompleted = ResumeBaseinfoEntity
					.isCompletedForSelfInfo(baseinfo);
			if (bcompleted) {
				completedegree += DEGREE_SELFINFO;

			}

			bcompleted = ResumeBaseinfoEntity
					.isCompletedForJobIntension(baseinfo);
			if (bcompleted) {
				completedegree += DEGREE_JOBINTENSION;

			}
			String picurl = baseinfo.getHeadphoto();
			if (picurl != null && picurl.length() > 0) {
				completedegree += DEGREE_HEADPHOTO;
			}

		}

		List<ResumeJobExpEntity> jobexplist = entity.getmJobexpEntityList();
		if (jobexplist != null) {
			boolean bstate = false;
			for (int i = 0; i < jobexplist.size(); i++) {
				ResumeJobExpEntity expentity = jobexplist.get(i);
				if (expentity != null) {
					bstate = expentity.isBcompleted();
					if (bstate)
						break;
				}
			}
			if (bstate) {
				completedegree += DEGREE_JOBEXP;

			}
		}

		List<ResumeProjectExpEntity> projectexplist = entity
				.getMprojectExpEntityList();
		if (projectexplist != null) {
			boolean bstate = false;
			for (int i = 0; i < projectexplist.size(); i++) {
				ResumeProjectExpEntity expentity = projectexplist.get(i);
				if (expentity != null) {
					bstate = expentity.isBcompleted();
					if (bstate)
						break;
				}
			}
			if (bstate) {
				completedegree += DEGREE_PROJECTEXP;

			}
		}

		List<ResumeEducationExpEntity> eduexplist = entity
				.getMeducationExpEntityList();
		if (eduexplist != null) {
			boolean bstate = false;
			for (int i = 0; i < eduexplist.size(); i++) {
				ResumeEducationExpEntity expentity = eduexplist.get(i);
				if (expentity != null) {
					bstate = expentity.isBcompleted();
					if (bstate)
						break;
				}
			}
			if (bstate) {
				completedegree += DEGREE_EDUCATIONXP;

			}
		}

		List<ResumeLanguageExpEntity> languageexplist = entity
				.getMlanguageExpEntityList();
		if (languageexplist != null) {
			boolean bstate = false;
			for (int i = 0; i < languageexplist.size(); i++) {
				ResumeLanguageExpEntity expentity = languageexplist.get(i);
				if (expentity != null) {
					bstate = expentity.isBcompleted();
					if (bstate)
						break;
				}
			}
			if (bstate) {
				completedegree += DEGREE_LANGUAGEEXP;

			}
		}
		return completedegree;

	}

	private void displayFastContet(ResumeEntity entity) {
		Entity infoentity = mresumeEntity.getQucikEntity();
		List<Entity> list = new ArrayList<Entity>();
		list.add(infoentity);
		mFastContentItem.bind(list);
	}

	private void displayJobIntension(ResumeEntity entity) {
		Entity baseInfoentity = mresumeEntity.getBaseinfoEntity();
		if (baseInfoentity != null && resume_id > 0) {
			List<Entity> BaseinfoList = new ArrayList<Entity>();
			BaseinfoList.add(baseInfoentity);
			mJobIntensionItem.bind(BaseinfoList);
		}
		boolean bcompleted = ResumeBaseinfoEntity
				.isCompletedForJobIntension((ResumeBaseinfoEntity) baseInfoentity);
		mJobIntensionItem.setCompledStatus(bcompleted);

	}

	private void displayWorkExp(ResumeEntity entity) {
		mWorkExperienceItem.bind(mresumeEntity.getmJobexpEntityList());
		List<ResumeJobExpEntity> jobexplist = mresumeEntity
				.getmJobexpEntityList();
		if (jobexplist != null) {
			boolean bstate = false;
			for (int i = 0; i < jobexplist.size(); i++) {
				ResumeJobExpEntity expentity = jobexplist.get(i);
				if (expentity != null) {
					bstate = expentity.isBcompleted();
					if (bstate)
						break;
				}
			}
			mWorkExperienceItem.setCompledStatus(bstate);
		}

	}

	private void displayProjectExp(ResumeEntity entity) {
		mProjectItem.bind(mresumeEntity.getMprojectExpEntityList());
		List<ResumeProjectExpEntity> projectexplist = mresumeEntity
				.getMprojectExpEntityList();
		if (projectexplist != null) {
			boolean bstate = false;
			for (int i = 0; i < projectexplist.size(); i++) {
				ResumeProjectExpEntity expentity = projectexplist.get(i);
				if (expentity != null) {
					bstate = expentity.isBcompleted();
					if (bstate)
						break;
				}
			}
			mProjectItem.setCompledStatus(bstate);
		}
	}

	private void displayEducationExp(ResumeEntity entity) {
		mEducationItem.bind(mresumeEntity.getMeducationExpEntityList());
		List<ResumeEducationExpEntity> eduexplist = mresumeEntity
				.getMeducationExpEntityList();
		if (eduexplist != null) {
			boolean bstate = false;
			for (int i = 0; i < eduexplist.size(); i++) {
				ResumeEducationExpEntity expentity = eduexplist.get(i);
				if (expentity != null) {
					bstate = expentity.isBcompleted();
					if (bstate)
						break;
				}
			}
			mEducationItem.setCompledStatus(bstate);
		}
	}

	private void displayLanguage(ResumeEntity entity) {
		mLanguageItem.bind(mresumeEntity.getMlanguageExpEntityList());
		List<ResumeLanguageExpEntity> languageexplist = entity
				.getMlanguageExpEntityList();
		if (languageexplist != null) {
			boolean bstate = false;
			for (int i = 0; i < languageexplist.size(); i++) {
				ResumeLanguageExpEntity expentity = languageexplist.get(i);
				if (expentity != null) {
					bstate = expentity.isBcompleted();
					if (bstate)
						break;
				}
			}
			mLanguageItem.setCompledStatus(bstate);
		}
	}

	private void refreshSingleResumeItem(int type, ResumeEntity entity) {
		switch (type) {
		case ResumeEditMainActivity.INTENT_DATATYPE_LANGUAGE: {
			mLanguageItem.bind(mresumeEntity.getMlanguageExpEntityList());
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_EDUCATION: {
			mEducationItem.bind(mresumeEntity.getMeducationExpEntityList());
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_PROJECTEXP: {
			mProjectItem.bind(mresumeEntity.getMprojectExpEntityList());
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_WORKEXP: {
			mWorkExperienceItem.bind(mresumeEntity.getmJobexpEntityList());
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_FASTCONTENT: {

		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_JOBINTENSION: {
			Entity baseInfoentity = mresumeEntity.getBaseinfoEntity();
			List<Entity> BaseinfoList = new ArrayList<Entity>();
			BaseinfoList.add(baseInfoentity);
			mJobIntensionItem.bind(BaseinfoList);
		}
			break;
		case INTENT_DATATYPE_SELFINFO: {
		}
			break;
		default:
			break;
		}
		Entity baseInfoentity = mresumeEntity.getBaseinfoEntity();
		List<Entity> BaseinfoList = new ArrayList<Entity>();
		BaseinfoList.add(baseInfoentity);

		mJobIntensionItem.bind(BaseinfoList);
		mWorkExperienceItem.bind(mresumeEntity.getmJobexpEntityList());
		mProjectItem.bind(mresumeEntity.getMprojectExpEntityList());
		mEducationItem.bind(mresumeEntity.getMeducationExpEntityList());
		mLanguageItem.bind(mresumeEntity.getMlanguageExpEntityList());

	}

	private void loadResumeData(final AppContext appcontext,
			final int resume_id, final Handler handler) {
		if (!appcontext.isNetworkConnected()) {
			UIHelper.ToastMessage(this, R.string.app_status_net_disconnected);
		}
		handler.sendEmptyMessage(MSG_DATALOAD_START);
		Thread t = new Thread() {
			public void run() {
				String resume_id_s = String.valueOf(resume_id);
				ResumeEntity resumeEntity = null;
				boolean bnetConnect = appcontext.isNetworkConnected();
				boolean bnetDataException = false;

				Message msg = new Message();
				// bnetConnect = false;
				if (bnetConnect && resume_id > 0) {
					try {
						resumeEntity = getResumeEntityFromNet(appcontext,
								resume_id_s, true);
					} catch (AppException e) {
						// TODO Auto-generated catch block
						bnetDataException = true;
						Message exception_msg = new Message();
						exception_msg.what = MSG_LOADDATA_APPEXCEPTION;
						exception_msg.obj = e;
						handler.sendMessage(exception_msg);
					}
				}

				if (resumeEntity != null) {
					msg.what = MSG_LOADDATA_OK;
					msg.obj = resumeEntity;
				} else {
					msg.what = MSG_LOADDATA_ERROR;
					msg.obj = resumeEntity;
				}
				handler.sendEmptyMessage(MSG_DATALOAD_OVER);
				handler.sendMessage(msg);
			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);

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
			case MSG_PHOTOSUBMIT_START:
				// setSubmitFlag(true);
				mprogressDialog.setMessage("请稍候...");
				mprogressDialog.show();
				break;
			case MSG_PHOTOSUBMIT_OVER:
				// setSubmitFlag(false);
				mprogressDialog.dismiss();
				break;
			case MSG_DATALOAD_START:
				mprogressDialog.setMessage(R.string.dialog_dataloadmsg);
				mprogressDialog.show();
				break;
			case MSG_DATALOAD_OVER:
				mprogressDialog.dismiss();
				break;
			case MSG_LOADDATA_ERROR: {
				UIHelper.ToastMessage(mcontext, R.string.dialog_data_get_err);
				if (mresumeEntity == null) {
					quit();
				}
			}
				break;
			case MSG_LOADDATA_APPEXCEPTION: {
				AppException e = (AppException) msg.obj;
				if (e != null) {
					e.makeToast(mcontext);
				}
			}
				break;
			case MSG_LOADDATA_OK:
				ResumeEntity entity = (ResumeEntity) msg.obj;
				Result res = entity.getValidate();
				if (!res.OK()) {

					boolean bflag = res
							.handleErrcode(ResumeEditMainActivity.this);
					if (!bflag) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_data_get_err);
					}
					if (mresumeEntity == null) {
						quit();
					}
				} else {
					mresumeEntity = entity;
					displayResumeEntity(mresumeEntity);
				}
				break;
			case BitmapManager.BITMAPMANAGER_OBTAINIMG:
				Map<String, Object> map = (Map<String, Object>) msg.obj;
				Bitmap bmp = (Bitmap) map
						.get(BitmapManager.BITMAPMANAGER_KEY_BITMAP);
				String url = (String) map
						.get(BitmapManager.BITMAPMANAGER_KEY_URL);
				if (bmp != null) {
					bmp = ImageUtils.createRoundHeadPhoto(
							ResumeEditMainActivity.this, bmp);
				}
				if (bmp != null)
					photoImgview.setImageBitmap(bmp);

				break;
			case ResumeUtils.RESUME_SUBMITIMG_OK: {

				mhandler.sendEmptyMessage(MSG_PHOTOSUBMIT_OVER);
				ResumePicSubmitResult result = (ResumePicSubmitResult) msg.obj;
				resume_id = StringUtils.toInt(result.getResumeId(), resume_id);
				String picurl = result.getPicurl();
				if (mNetpicBaseUrl != null)
					picurl = mNetpicBaseUrl + picurl;

				mresumeEntity.getBaseinfoEntity().setHeadphoto(picurl);
				UIHelper.ToastMessage(mcontext, R.string.dialog_save_success);
				int degree = judgeResumeCompleteDegree(mresumeEntity);
				mresumeEntity.getBaseinfoEntity().setPercent(degree);
				displaySelfInfo(mresumeEntity);
			}
				break;
			case ResumeUtils.RESUME_SUBMIT_OK: {
				ResumeSubmitResult result = (ResumeSubmitResult) msg.obj;
				resume_id = StringUtils.toInt(result.getResume_Id(), resume_id);
				UIHelper.ToastMessage(mcontext, R.string.dialog_save_success);
				bedit = true;
				mhandler.sendEmptyMessage(MSG_RESUMESUBMIT_OVER);
				setResultAndFinish(RESULT_OK);
				setSubmitFlag(false);
			}
				break;

			case ResumeUtils.RESUME_SUBMIT_ERROR:
			case ResumeUtils.RESUME_SUBMIT_NETERROR:
			case ResumeUtils.RESUME_SUBMIT_DATAERROR:
			case ResumeUtils.RESUME_SUBMIT_EXCEPTIONERROR:
				mhandler.sendEmptyMessage(MSG_PHOTOSUBMIT_OVER);
				if (msg.what == ResumeUtils.RESUME_SUBMIT_NETERROR) {
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
	private Handler mphotohandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case ResumeUtils.RESUME_SUBMITIMG_OK: {

				mhandler.sendEmptyMessage(MSG_PHOTOSUBMIT_OVER);
				ResumePicSubmitResult result = (ResumePicSubmitResult) msg.obj;
				resume_id = StringUtils.toInt(result.getResumeId(), resume_id);
				String picurl = result.getPicurl();
				if (mNetpicBaseUrl != null)
					picurl = mNetpicBaseUrl + picurl;

				mresumeEntity.getBaseinfoEntity().setHeadphoto(picurl);
				UIHelper.ToastMessage(mcontext, R.string.dialog_save_success);
				if (picurl != null && picurl.length() > 0) {
					setHeadPhotoToView(picurl, photoImgview,
							R.drawable.person_photo_edit_big);

				}

			}
				break;
			case ResumeUtils.RESUME_SUBMIT_ERROR:
			case ResumeUtils.RESUME_SUBMIT_NETERROR:
			case ResumeUtils.RESUME_SUBMIT_DATAERROR:
			case ResumeUtils.RESUME_SUBMIT_EXCEPTIONERROR:
				mhandler.sendEmptyMessage(MSG_PHOTOSUBMIT_OVER);
				if (msg.what == ResumeUtils.RESUME_SUBMIT_NETERROR) {
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
	OnClickListener mlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.resume_selfinfo_layout: {
				Intent intent = new Intent(mcontext,
						ResumeEditPersonalInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("entitiy",
						mresumeEntity.getBaseinfoEntity());
				bundle.putInt("resumeid", resume_id);
				intent.putExtras(bundle);
				startActivityForResult(intent, RESUME_RETURN_SELFINFO);
			}
				break;
			case R.id.headphoto_imgview: {
				showSetPhotoDialog();
			}
				break;
			case R.id.resume_edit_save: {
				// 友盟统计--简历编辑--保存按钮
				UmShare.UmStatistics(mcontext, "ResumeEdit_SaveButton");
				boolean bcompleted = ResumeBaseinfoEntity
						.isCompletedForSelfInfo(mresumeEntity
								.getBaseinfoEntity());
				if (!bcompleted) {
					UIHelper.ToastMessage(mcontext, "请先完善个人资料！");
					break;
				}
				if (getSubmitFlag()) {
					break;
				}
				JSONObject obj = ResumeBaseinfoEntity
						.resumeBaseinfoEntityToJSONObject(mresumeEntity
								.getBaseinfoEntity());
				String str = obj.toString();
				int i = 0;
				mhandler.sendEmptyMessage(MSG_RESUMESUBMIT_START);
				ResumeUtils.submitData(mappcontext, resume_id, str,
						ResumeEntity.RESUME_BLOCK_BASE, mhandler);
			}
				break;
			case R.id.resume_preview: {

				if (mresumeEntity != null) {
					ResumeBaseinfoEntity baseEntity = mresumeEntity
							.getBaseinfoEntity();
					if (baseEntity != null && baseEntity.getUrl() != null) {
						String url = baseEntity.getUrl();
						int resumeid = StringUtils.toInt(
								mresumeEntity.getResume_Id(), 0);
						if (resumeid == 0 || url == null || url.length() == 0) {
							UIHelper.ToastMessage(mcontext, "当前简历无法预览");
						} else {
							UIHelper.showResumePreviewForResult(
									ResumeEditMainActivity.this, url,
									mresumeEntity.getResume_Id());
						}
					}

				} else {
					UIHelper.ToastMessage(mcontext, R.string.resume_list_empty);
				}

			}
				break;
			case R.id.resume_edit_goback: {
				if (bedit) {
					showQuitAlertDialog();
				} else {
					quit();
				}

			}
				break;
			default:
				break;
			}
		}
	};

	public static ResumeEntity getResumeEntityFromNet(AppContext appContext,
			String resume_id, boolean bsavetoDb) throws AppException {

		String Url = URLs.RESUME_GET + "/" + resume_id + "/all";

		Map<String, Object> params = AppContext.getHttpPostParams(appContext,
				null);
		try {

			return ResumeEntity.parse_all(appContext,
					ApiClient._post(appContext, Url, params, null), bsavetoDb);
		} catch (Exception e) {
			if (e instanceof AppException)
				throw (AppException) e;
			throw AppException.network(e);
		}
	}

	private boolean getSubmitFlag() {
		return bsubmitFlag;
	}

	private void setSubmitFlag(boolean newFlag) {
		bsubmitFlag = newFlag;
	}

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		boolean flag = true;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (bedit) {
				showQuitAlertDialog();
			} else {
				quit();
			}

		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Ignore failed requests
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case RESUME_RETURN_FASTCONTENT: {
			bedit = true;
			int id = data.getIntExtra(INTENT_KEY_RESUMEID, 0);
			Bundle bundle = data.getExtras();
			ResumeQuickItemEntity entity = (ResumeQuickItemEntity) bundle
					.get(INTENT_KEY_SERIALIZE);
			entity.setBcompleted(ResumeQuickItemEntity
					.isCompletedForQuickResume(entity));

			mresumeEntity.setQucikEntity(entity);
			displayFastContet(mresumeEntity);
		}
			break;

		case RESUME_RETURN_LANGUAGE: {
			bedit = true;
			int id = data.getIntExtra(
					ResumeEditHomeActivity.INTENT_KEY_RESUMEID, 0);
			Bundle bundle = data.getExtras();
			ResumeLanguageExpEntity entity = (ResumeLanguageExpEntity) bundle
					.get(INTENT_KEY_SERIALIZE);
			entity.setBcompleted(ResumeLanguageExpEntity
					.isCompletedForLanguageExp(entity));

			int size = mresumeEntity.getMlanguageExpEntityList().size();
			int position = -1;
			for (int i = 0; i < size; i++) {
				if (mresumeEntity.getMlanguageExpEntityList().get(i)
						.getItemid().equals(entity.getItemid())) {
					position = i;
					break;
				}
			}
			if (position != -1) {
				mresumeEntity.getMlanguageExpEntityList().remove(position);
			}
			mresumeEntity.getMlanguageExpEntityList().add(entity);
			displayLanguage(mresumeEntity);
		}
			break;
		case RESUME_RETURN_EDUCATION: {
			bedit = true;
			int id = data.getIntExtra(INTENT_KEY_RESUMEID, 0);
			Bundle bundle = data.getExtras();
			ResumeEducationExpEntity entity = (ResumeEducationExpEntity) bundle
					.get(INTENT_KEY_SERIALIZE);
			entity.setBcompleted(ResumeEducationExpEntity
					.isCompletedForEducationExp(entity));

			int size = mresumeEntity.getMeducationExpEntityList().size();
			int position = -1;
			for (int i = 0; i < size; i++) {
				if (mresumeEntity.getMeducationExpEntityList().get(i)
						.getItemid().equals(entity.getItemid())) {
					position = i;
					break;
				}
			}
			if (position != -1) {
				mresumeEntity.getMeducationExpEntityList().remove(position);
			}
			mresumeEntity.getMeducationExpEntityList().add(entity);
			displayEducationExp(mresumeEntity);
		}
			break;
		case RESUME_RETURN_WORKEXP: {
			bedit = true;
			int id = data.getIntExtra(INTENT_KEY_RESUMEID, 0);
			Bundle bundle = data.getExtras();
			ResumeJobExpEntity entity = (ResumeJobExpEntity) bundle
					.get(INTENT_KEY_SERIALIZE);
			entity.setBcompleted(ResumeJobExpEntity
					.isCompletedForJobExp(entity));

			int size = mresumeEntity.getmJobexpEntityList().size();
			int position = -1;
			for (int i = 0; i < size; i++) {
				if (mresumeEntity.getmJobexpEntityList().get(i).getItemid()
						.equals(entity.getItemid())) {
					position = i;
					break;
				}
			}
			if (position != -1) {
				mresumeEntity.getmJobexpEntityList().remove(position);
			}
			mresumeEntity.getmJobexpEntityList().add(entity);
			displayWorkExp(mresumeEntity);
		}
			break;
		case RESUME_RETURN_PROJECTEXP: {
			bedit = true;
			int id = data.getIntExtra(INTENT_KEY_RESUMEID, 0);
			Bundle bundle = data.getExtras();
			ResumeProjectExpEntity entity = (ResumeProjectExpEntity) bundle
					.get(INTENT_KEY_SERIALIZE);
			entity.setBcompleted(ResumeProjectExpEntity
					.isCompletedForProjectExp(entity));

			int size = mresumeEntity.getMprojectExpEntityList().size();
			int position = -1;
			for (int i = 0; i < size; i++) {
				if (mresumeEntity.getMprojectExpEntityList().get(i).getItemid()
						.equals(entity.getItemid())) {
					position = i;
					break;
				}
			}
			if (position != -1) {
				mresumeEntity.getMprojectExpEntityList().remove(position);
			}
			mresumeEntity.getMprojectExpEntityList().add(entity);
			displayProjectExp(mresumeEntity);
		}
			break;

		case RESUME_RETURN_SELFINFO: {
			bedit = true;
			Bundle bundle = data.getExtras();
			resume_id = data.getIntExtra(INTENT_KEY_RESUMEID, resume_id);

			ResumeBaseinfoEntity entity = (ResumeBaseinfoEntity) bundle
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			mresumeEntity.setBaseinfoEntity(entity);
			mresumeEntity.setResume_Id(String.valueOf(resume_id));
			displaySelfInfo(mresumeEntity);
		}
			break;
		case RESUME_RETURN_JOBINTENSION: {
			bedit = true;
			Bundle bundle = data.getExtras();
			resume_id = data.getIntExtra(
					ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);

			ResumeBaseinfoEntity entity = (ResumeBaseinfoEntity) bundle
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			mresumeEntity.setBaseinfoEntity(entity);
			displayJobIntension(mresumeEntity);
		}
			break;
		case PHOTO_PICKED_WITH_DATA: {

			final Bitmap photo = data.getParcelableExtra("data");
			ResumeUtils.submitHeadphoto(mappcontext, resume_id, photo,
					mphotohandler);
			mhandler.sendEmptyMessage(MSG_PHOTOSUBMIT_START);
			break;
		}

		case CAMERA_WITH_DATA: {
			doCropPhoto(mCurrentPhotoFile);
			break;
		}

		case ActivityRequestCode.RESULT_ACTIVITY_LOGIN: {
			loadResumeData(mappcontext, resume_id, mhandler);
		}
			break;

		}
		int degree = judgeResumeCompleteDegree(mresumeEntity);
		mresumeEntity.getBaseinfoEntity().setPercent(degree);
		displayCompleteDegree(mresumeEntity);
	}

	protected void doCropPhoto(File f) {
		try {
			// Add the image to the media store
			MediaScannerConnection.scanFile(this,
					new String[] { f.getAbsolutePath() },
					new String[] { null }, null);

			// Launch gallery to crop the photo
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (Exception e) {

		}
	}

	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", ICON_SIZE);
		intent.putExtra("outputY", ICON_SIZE);
		intent.putExtra("return-data", true);
		return intent;
	}

	private void setHeadPhotoToView(String url, ImageView imgv, int defaultbmpId) {

		CommonRoundImgCreator creator = new CommonRoundImgCreator(
				ResumeEditMainActivity.this,
				Constants.ResumeModule.HEADPHOTO_SMALL_RADIUS,
				Constants.ResumeModule.HEADPHOTO_SMALL_BODER_WIDTH,
				Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

		Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
				defaultbmpId);
		if (url != null) {
			bmpManager.loadBitmap(url, imgv, defaultbmp, creator);
		} else {
			imgv.setImageBitmap(defaultbmp);
		}

	}

	private void showSetPhotoDialog() {
		final AlertDialog imageDialog = MethodsCompat
				.getAlertDialogBuilder(this, AlertDialog.THEME_HOLO_LIGHT)
				.setTitle("上传头像")
				.setIcon(android.R.drawable.btn_star)
				.setItems(new String[] { "本地图片", "拍照" },
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								// 相册选图
								if (item == 0) {

									doPickPhotoFromGallery();
								}
								// 手机拍照
								else if (item == 1) {
									doTakePhoto();
								}
								// imageDialog.dismiss();
							}
						}).create();
		imageDialog.setCanceledOnTouchOutside(true);
		imageDialog.show();
	}

	protected void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			PHOTO_DIR.mkdirs();
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
		}
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	protected void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {

		}
	}

	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", ICON_SIZE);
		intent.putExtra("outputY", ICON_SIZE);
		intent.putExtra("return-data", true);
		return intent;
	}

	private void showQuitAlertDialog() {
		Dialog dialog = MethodsCompat
				.getAlertDialogBuilder(this, AlertDialog.THEME_HOLO_LIGHT)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.dialog_quitedittitle)
				.setMessage(R.string.dialog_quiteditmsg)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								setResultAndFinish(RESULT_OK);
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
		if (bedit) {
			Intent intent = new Intent();
			intent.putExtra(ResumeEditMainActivity.INTENT_KEY_RESUMEID,
					resume_id);

			setResult(resultCode, intent);
		}
		((ResumeEditMainActivity) mcontext).quit();

	}

	public void quit() {
		this.finish();
	}
}

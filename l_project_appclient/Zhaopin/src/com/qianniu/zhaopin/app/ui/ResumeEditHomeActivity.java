package com.qianniu.zhaopin.app.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.ColumnEntityList;
import com.qianniu.zhaopin.app.bean.ForumType;
import com.qianniu.zhaopin.app.bean.ForumTypeList;
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
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ResumeEditHomeActivity extends BaseActivity {

	private static final int RESUME_RETURN_SELFINFO = 3001;
	private static final int RESUME_RETURN_JOBINTENSION = 3002;
	private static final int RESUME_RETURN_WORKEXP = 3003;
	private static final int RESUME_RETURN_PROJECTEXP = 3004;
	private static final int RESUME_RETURN_EDUCATION = 3005;
	private static final int RESUME_RETURN_LANGUAGE = 3006;
	private static final int RESUME_RETURN_DEATAIL = 3007;

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

	/** 通过图库获取图片 */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	/** 照相机拍照获取图片 */
	private static final int CAMERA_WITH_DATA = 3022;
	private static final int ICON_SIZE = 96;
	/**
	 * 照相机拍摄照片转化为该File对象
	 */
	private File mCurrentPhotoFile;
	private String mNetpicBaseUrl = null;
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

	private View bg;
	private TextView basicText;
	private TextView hopeText;
	private TextView workText;
	private TextView projectText;
	private TextView educationText;
	private TextView languageText;

	private TextView changeTimeText;
	private TextView nameText;
	private TextView hopeJobText;
	private ImageView defaultresumeIv;
	private ImageView creditIv;

	private ImageButton mbackImgBtn;
	private ImageView msaveBtn;
	private ImageButton mpreviewBtn;

	private ImageView completeImgview;
	private ImageView photoImgview;
	private BitmapManager bitmanager;

	private int resume_id;
	private ProgressDialog mprogressDialog;
	private BitmapManager bmpManager;
	private ResumeEntity mresumeEntity;
	private int mcompletedegree = 0;

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

		resume_id = getIntent().getIntExtra("resume_id", 0);
		if (savedInstanceState != null) {
			mresumeEntity = (ResumeEntity) savedInstanceState
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			if (mresumeEntity != null)
				resume_id = StringUtils.toInt(mresumeEntity.getResume_Id(), 0);

		} else {
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			resume_id = getIntent().getIntExtra("resume_id", 0);

		}

		setContentView(R.layout.resume_edit_home);
		bg = findViewById(R.id.resume_edit_bg);
		mbackImgBtn = (ImageButton) findViewById(R.id.resume_edit_goback);
		mbackImgBtn.setOnClickListener(mlistener);
		msaveBtn = (ImageView) findViewById(R.id.resume_edit_save);
		msaveBtn.setOnClickListener(mlistener);
		mpreviewBtn = (ImageButton) findViewById(R.id.resume_preview);
		mpreviewBtn.setOnClickListener(mlistener);

		completeImgview = (ImageView) findViewById(R.id.complete_imgview);
		/*
		 * Bitmap completebmp = ImageUtils.creatArcsBitmap(75, 8, 0xFFDC5B57,
		 * 0xFF3A4147, -90, 270);
		 */
		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(this,
				mcompletedegree);
		completeImgview.setImageBitmap(completebmp);

		photoImgview = (ImageView) findViewById(R.id.headphoto_imgview);
		Bitmap photobmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.person_photo_edit_big);
		/*
		 * photobmp = Bitmap.createScaledBitmap(photobmp, 120, 120, true);
		 * photobmp = ImageUtils.toRoundBitmap(photobmp, 8, 0xFFA9A59E);
		 */
		photobmp = ImageUtils.createRoundHeadPhoto(this, photobmp);
		photoImgview.setImageBitmap(photobmp);
		photoImgview.setOnClickListener(mlistener);

		ViewGroup personalItem = (ViewGroup) findViewById(R.id.resume_personalinfo_item);
		personalItem.setOnClickListener(mlistener);
		ViewGroup educationItem = (ViewGroup) findViewById(R.id.resume_education_item);
		educationItem.setOnClickListener(mlistener);
		ViewGroup jobItem = (ViewGroup) findViewById(R.id.resume_jobintension_item);
		jobItem.setOnClickListener(mlistener);
		ViewGroup languageItem = (ViewGroup) findViewById(R.id.resume_language_item);
		languageItem.setOnClickListener(mlistener);
		ViewGroup projectItem = (ViewGroup) findViewById(R.id.resume_project_item);
		projectItem.setOnClickListener(mlistener);
		ViewGroup workItem = (ViewGroup) findViewById(R.id.resume_workexperience_item);
		workItem.setOnClickListener(mlistener);
		ViewGroup wholeItem = (ViewGroup) findViewById(R.id.resume_whole_item);
		wholeItem.setOnClickListener(mlistener);
		View fastbtn = (View) findViewById(R.id.resume_fastcontent_bt);
		fastbtn.setOnClickListener(mlistener);
		View completeBtn = findViewById(R.id.resume_completeresume_bt);
		completeBtn.setOnClickListener(mlistener);

		ViewGroup photoItem = (ViewGroup) findViewById(R.id.resume_photo_item);
		photoItem.setOnClickListener(mlistener);

		basicText = (TextView) findViewById(R.id.resume_basicText);
		hopeText = (TextView) findViewById(R.id.resume_hopeText);
		workText = (TextView) findViewById(R.id.resume_workText);
		projectText = (TextView) findViewById(R.id.resume_projectText);
		educationText = (TextView) findViewById(R.id.resume_educationText);
		languageText = (TextView) findViewById(R.id.resume_languageText);

		changeTimeText = (TextView) findViewById(R.id.resume_time_tv);
		nameText = (TextView) findViewById(R.id.resume_name_tv);
		hopeJobText = (TextView) findViewById(R.id.resume_job_tv);
		defaultresumeIv = (ImageView) findViewById(R.id.default_imgview);
		creditIv = (ImageView) findViewById(R.id.credit_imgview);

		mprogressDialog = new ProgressDialog(this);
		// mprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		getNetImgBaseUrl(mappcontext);
		if (resume_id != 0) {
			loadResumeData(mappcontext, resume_id, mhandler);
		} else {
			mresumeEntity = new ResumeEntity();
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

	OnClickListener mlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.resume_edit_goback: {
				if (bedit) {
					showQuitAlertDialog();
				} else {
					quit();
				}

			}
				break;
			case R.id.resume_preview:{

				if (mresumeEntity != null) {
					ResumeBaseinfoEntity baseEntity = mresumeEntity.getBaseinfoEntity();
					if(baseEntity != null && baseEntity.getUrl() != null){
						String url = baseEntity.getUrl();
						int resumeid = StringUtils.toInt(mresumeEntity.getResume_Id(), 0);
						if (resumeid == 0 || url == null || url.length() == 0) {
							UIHelper.ToastMessage(mcontext, "当前简历无法预览");
						} else {
							UIHelper.showResumePreviewForResult(ResumeEditHomeActivity.this, url,mresumeEntity.getResume_Id());
						}	
					}


				} else {
					UIHelper.ToastMessage(mcontext, R.string.resume_list_empty);


				}
			
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
			case R.id.resume_personalinfo_item: {
				// 友盟统计--简历编辑--个人资料按钮
				UmShare.UmStatistics(mcontext, "ResumeEdit_PersonalinfoButton");

				Intent intent = new Intent(mcontext,
						ResumeEditPersonalInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("entitiy",
						mresumeEntity.getBaseinfoEntity());
				// bundle.putSerializable("entitiy", new
				// ResumeBaseinfoEntity());
				bundle.putInt("resumeid", resume_id);
				intent.putExtras(bundle);

				startActivityForResult(intent, RESUME_RETURN_SELFINFO);
				// startActivity(intent);
			}
				break;
			case R.id.resume_education_item: {
				// 友盟统计--简历编辑--教育经历按钮
				UmShare.UmStatistics(mcontext, "ResumeEdit_EducationButton");

				Intent intent = new Intent(mcontext,
						ResumeEditListActivity.class);
				Bundle bundle = new Bundle();
				ArrayList list = new ArrayList();
				list.add(mresumeEntity.getMeducationExpEntityList());
				bundle.putParcelableArrayList(INTENT_KEY_PARCELABLEARRAY, list);
				bundle.putInt(INTENT_KEY_RESUMEID, resume_id);
				intent.putExtras(bundle);
				intent.putExtra(INTENT_KEY_DATATYPE, INTENT_DATATYPE_EDUCATION);
				startActivityForResult(intent, RESUME_RETURN_EDUCATION);
			}
				break;
			case R.id.resume_jobintension_item: {
				// 友盟统计--简历编辑--求职意向按钮
				UmShare.UmStatistics(mcontext, "ResumeEdit_JobintensionButton");

				Intent intent = new Intent(mcontext,
						ResumeEditJobTargetActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("entitiy",
						mresumeEntity.getBaseinfoEntity());
				// bundle.putSerializable("entitiy", new
				// ResumeBaseinfoEntity());
				bundle.putInt("resumeid", resume_id);
				intent.putExtras(bundle);
				intent.putExtra("bnew", false);
				startActivityForResult(intent, RESUME_RETURN_JOBINTENSION);
			}
				break;
			case R.id.resume_language_item: {
				// 友盟统计--简历编辑--语言技能按钮
				UmShare.UmStatistics(mcontext, "ResumeEdit_LanguageButton");

				Intent intent = new Intent(mcontext,
						ResumeEditListActivity.class);
				Bundle bundle = new Bundle();
				ArrayList list = new ArrayList();
				list.add(mresumeEntity.getMlanguageExpEntityList());
				bundle.putParcelableArrayList(INTENT_KEY_PARCELABLEARRAY, list);
				bundle.putInt(INTENT_KEY_RESUMEID, resume_id);
				intent.putExtras(bundle);
				intent.putExtra(INTENT_KEY_DATATYPE, INTENT_DATATYPE_LANGUAGE);
				startActivityForResult(intent, RESUME_RETURN_LANGUAGE);
			}
				break;
			case R.id.resume_project_item: {
				// 友盟统计--简历编辑--项目经历按钮
				UmShare.UmStatistics(mcontext, "ResumeEdit_ProjectButton");

				Intent intent = new Intent(mcontext,
						ResumeEditListActivity.class);
				Bundle bundle = new Bundle();
				ArrayList list = new ArrayList();
				list.add(mresumeEntity.getMprojectExpEntityList());
				bundle.putParcelableArrayList(INTENT_KEY_PARCELABLEARRAY, list);
				bundle.putInt(INTENT_KEY_RESUMEID, resume_id);
				intent.putExtras(bundle);
				intent.putExtra(INTENT_KEY_DATATYPE, INTENT_DATATYPE_PROJECTEXP);
				startActivityForResult(intent, RESUME_RETURN_PROJECTEXP);
			}
				break;
			case R.id.resume_workexperience_item: {
				// 友盟统计--简历编辑--工作经验按钮
				UmShare.UmStatistics(mcontext,
						"ResumeEdit_WorkexperienceButton");

				Intent intent = new Intent(mcontext,
						ResumeEditListActivity.class);
				Bundle bundle = new Bundle();
				ArrayList list = new ArrayList();
				list.add(mresumeEntity.getmJobexpEntityList());
				bundle.putParcelableArrayList(INTENT_KEY_PARCELABLEARRAY, list);
				bundle.putInt(INTENT_KEY_RESUMEID, resume_id);
				intent.putExtras(bundle);
				intent.putExtra(INTENT_KEY_DATATYPE, INTENT_DATATYPE_WORKEXP);
				startActivityForResult(intent, RESUME_RETURN_WORKEXP);
			}
				break;
			case R.id.headphoto_imgview: {
				// 友盟统计--简历编辑--头像设置按钮
				UmShare.UmStatistics(mcontext, "ResumeEdit_HeadphotoImgview");

				showSetPhotoDialog();
				// showPhotoSelectPopwindow();
			}
				break;

			case R.id.resume_whole_item: {
				boolean bcompleted = ResumeBaseinfoEntity
						.isCompletedForSelfInfo(mresumeEntity
								.getBaseinfoEntity());
				if (!bcompleted) {
					UIHelper.ToastMessage(mcontext, "请先完善个人资料！");
					break;
				}
				ViewGroup wholecontainer = (ViewGroup) findViewById(R.id.resume_whole_container);
				ImageView pointImView = (ImageView) v
						.findViewById(R.id.common_item_arrow2);
				if (wholecontainer.getVisibility() == View.GONE
						|| wholecontainer.getVisibility() == View.INVISIBLE) {
					pointImView.setImageResource(R.drawable.common_img_up);
					wholecontainer.setVisibility(View.VISIBLE);
					Animation pushin = AnimationUtils.loadAnimation(mcontext,
							R.anim.push_top_in);
					wholecontainer.startAnimation(pushin);
					pushin.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation arg0) {

							ScrollView mscrollview = (ScrollView) findViewById(R.id.scroolview_container);
							ViewGroup wholeItem = (ViewGroup) findViewById(R.id.resume_whole_item);
							ViewGroup wholecontainer = (ViewGroup) findViewById(R.id.resume_whole_container);
							int height = wholecontainer.getHeight();
							int height1 = wholeItem.getHeight();
							mscrollview.smoothScrollTo(0, height + height1);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationStart(Animation animation) {
						}

					});

				} else {
					wholecontainer.setVisibility(View.GONE);
					Animation pushout = AnimationUtils.loadAnimation(mcontext,
							R.anim.push_top_out);
					pointImView.setImageResource(R.drawable.common_img_down);
					wholecontainer.startAnimation(pushout);
				}
			}
				break;
			case R.id.resume_fastcontent_bt: {
				boolean bcompleted = ResumeBaseinfoEntity
						.isCompletedForSelfInfo(mresumeEntity
								.getBaseinfoEntity());
				if (!bcompleted) {
					UIHelper.ToastMessage(mcontext, "请先完善个人资料！");
					break;
				}
				Intent intent = new Intent(mcontext,
						ResumeEditQuickActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("entitiy",
						mresumeEntity.getQucikEntity());
				bundle.putInt("resumeid", resume_id);
				intent.putExtras(bundle);

				startActivity(intent);
			}
				break;
			case R.id.resume_completeresume_bt: {
				boolean bcompleted = ResumeBaseinfoEntity
						.isCompletedForSelfInfo(mresumeEntity
								.getBaseinfoEntity());
				if (!bcompleted) {
					UIHelper.ToastMessage(mcontext, "请先完善个人资料！");
					break;
				}
				Intent intent = new Intent(mcontext,
						ResumeEditDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("entitiy", mresumeEntity);
				bundle.putInt("resumeid", resume_id);
				intent.putExtras(bundle);
				startActivityForResult(intent, RESUME_RETURN_DEATAIL);

			}
				break;
			}
		}
	};

	private void showPhotoSelectPopwindow() {
		View contentView = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.popup_window_image_select, null);
		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setAnimationStyle(R.anim.dialog_rotate);
		popupWindow.setOutsideTouchable(false);

		ColorDrawable cw = new ColorDrawable(Color.parseColor("#00000000"));
		popupWindow.setBackgroundDrawable(cw);
		popupWindow.showAtLocation(findViewById(R.id.resume_edit_bg),
				Gravity.BOTTOM, 0, 0);

		Button localbtn = (Button) contentView.findViewById(R.id.local_picture);
		localbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doPickPhotoFromGallery();
				popupWindow.dismiss();
			}
		});
		Button takepohtobtn = (Button) contentView
				.findViewById(R.id.take_picture);
		takepohtobtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doTakePhoto();
				popupWindow.dismiss();
			}
		});

		Button cancelBtn = (Button) contentView.findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});

	}

	private void showSetPhotoDialog() {
		final AlertDialog imageDialog = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT)
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Ignore failed requests
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case RESUME_RETURN_DEATAIL: {
			boolean flag = data.getBooleanExtra(INTENT_KEY_EDITFLAG, false);
			Bundle bundle = data.getExtras();
			resume_id = data.getIntExtra(
					ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
			ResumeEntity entity = (ResumeEntity) bundle
					.getSerializable(INTENT_KEY_SERIALIZE);
			if (flag)
				mresumeEntity = entity;
			bedit |= flag;
		}
			break;
		case RESUME_RETURN_LANGUAGE: {
			bedit = true;
			Bundle bundle = data.getExtras();
			resume_id = data.getIntExtra(
					ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
			ArrayList bundlelist = bundle
					.getParcelableArrayList(ResumeEditHomeActivity.INTENT_KEY_PARCELABLEARRAY);
			if (bundlelist != null) {
				List<ResumeLanguageExpEntity> list = (List<ResumeLanguageExpEntity>) bundlelist
						.get(0);
				if (list != null) {
					mresumeEntity.setMlanguageExpEntityList(list);
				}
			}
		}
			break;
		case RESUME_RETURN_EDUCATION: {
			bedit = true;
			Bundle bundle = data.getExtras();
			resume_id = data.getIntExtra(
					ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
			ArrayList bundlelist = bundle
					.getParcelableArrayList(ResumeEditHomeActivity.INTENT_KEY_PARCELABLEARRAY);
			if (bundlelist != null) {
				List<ResumeEducationExpEntity> list = (List<ResumeEducationExpEntity>) bundlelist
						.get(0);
				if (list != null) {
					mresumeEntity.setMeducationExpEntityList(list);
				}
			}
		}
			break;
		case RESUME_RETURN_WORKEXP: {
			bedit = true;
			Bundle bundle = data.getExtras();
			resume_id = data.getIntExtra(
					ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
			ArrayList bundlelist = bundle
					.getParcelableArrayList(ResumeEditHomeActivity.INTENT_KEY_PARCELABLEARRAY);
			if (bundlelist != null) {
				List<ResumeJobExpEntity> list = (List<ResumeJobExpEntity>) bundlelist
						.get(0);
				if (list != null) {
					mresumeEntity.setmJobexpEntityList(list);
				}
			}
		}
			break;
		case RESUME_RETURN_PROJECTEXP: {
			bedit = true;
			Bundle bundle = data.getExtras();
			resume_id = data.getIntExtra(
					ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
			ArrayList bundlelist = bundle
					.getParcelableArrayList(ResumeEditHomeActivity.INTENT_KEY_PARCELABLEARRAY);
			if (bundlelist != null) {
				List<ResumeProjectExpEntity> list = (List<ResumeProjectExpEntity>) bundlelist
						.get(0);
				if (list != null) {
					mresumeEntity.setMprojectExpEntityList(list);
				}
			}
		}
			break;

		case RESUME_RETURN_SELFINFO: {
			bedit = true;
			Bundle bundle = data.getExtras();
			resume_id = data.getIntExtra(
					ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);

			ResumeBaseinfoEntity entity = (ResumeBaseinfoEntity) bundle
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			mresumeEntity.setBaseinfoEntity(entity);
			mresumeEntity.setResume_Id(String.valueOf(resume_id));
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

		}
			break;
		case PHOTO_PICKED_WITH_DATA: {

			final Bitmap photo = data.getParcelableExtra("data");
			// mImageViewPhoto.setImageBitmap(photo);
			/*
			 * Bitmap photobmp = ImageUtils.createRoundHeadPhoto(this, photo);
			 * photoImgview.setImageBitmap(photobmp);
			 */

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
		displayResumeEntity(mresumeEntity);
	}

	private void displayResumeEntity(ResumeEntity entity) {
		int completedegree = 0;
		if (entity == null)
			return;
		String time = entity.getResume_time();
		if (time != null) {
			time = StringUtils.formatDateStringToModifyYMD(time);
			if (time != null && time.length() > 0)
				changeTimeText.setText(time);
		}
		ResumeBaseinfoEntity baseinfo = entity.getBaseinfoEntity();
		if (baseinfo != null) {
			String name = baseinfo.getName();
			if (name != null && name.length() > 0) {
				nameText.setText(name);
			}

			String title = baseinfo.getPersontitle();
			if (title != null && title.length() > 0) {
				hopeJobText.setText(title);
			}

			boolean bcompleted = ResumeBaseinfoEntity
					.isCompletedForSelfInfo(baseinfo);
			if (bcompleted) {
				completedegree += DEGREE_SELFINFO;
				basicText.setText(R.string.resume_edit_comlete);
			} else {
				basicText.setText(R.string.resume_edit_uncomlete);
			}

			bcompleted = ResumeBaseinfoEntity
					.isCompletedForJobIntension(baseinfo);
			if (bcompleted) {
				completedegree += DEGREE_JOBINTENSION;
				hopeText.setText(R.string.resume_edit_comlete);
			} else {
				hopeText.setText(R.string.resume_edit_uncomlete);
			}
			String picurl = baseinfo.getHeadphoto();
			if (picurl != null && picurl.length() > 0) {
				setHeadPhotoToView(picurl, photoImgview,
						R.drawable.person_photo_edit_big);
				completedegree += DEGREE_HEADPHOTO;
			}

		}
		int defaultstatus = baseinfo.getDefaultResume();
		if (defaultstatus == ResumeSimpleEntity.RESUME_DEFAULT_UNSELECTED)
			defaultresumeIv.setImageResource(R.drawable.default_normal);
		else
			defaultresumeIv.setImageResource(R.drawable.default_light);

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
				workText.setText(R.string.resume_edit_comlete);
			} else {
				workText.setText(R.string.resume_edit_uncomlete);
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
				projectText.setText(R.string.resume_edit_comlete);
			} else {
				projectText.setText(R.string.resume_edit_uncomlete);
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
				educationText.setText(R.string.resume_edit_comlete);
			} else {
				educationText.setText(R.string.resume_edit_uncomlete);
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
				languageText.setText(R.string.resume_edit_comlete);
			} else {
				languageText.setText(R.string.resume_edit_uncomlete);
			}
		}

		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(this,
				completedegree);
		completeImgview.setImageBitmap(completebmp);

		mresumeEntity.getBaseinfoEntity().setPercent(completedegree);
		mcompletedegree = completedegree;
	}

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
				displayResumeEntity(mresumeEntity);

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
							.handleErrcode(ResumeEditHomeActivity.this);
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
							ResumeEditHomeActivity.this, bmp);
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
				displayResumeEntity(mresumeEntity);

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

				if (/* true || */!bnetConnect || bnetDataException
						|| resume_id < 0) {
					try {
						resumeEntity = getResumeEntityFromDb(appcontext,
								resume_id_s);
					} catch (AppException e) {
						// TODO Auto-generated catch block
						msg.what = MSG_LOADDATA_ERROR;

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

	public static ResumeEntity getResumeEntityFromDb(AppContext appContext,
			String resume_id) throws AppException {

		ResumeEntity resumeEntity = new ResumeEntity();
		Result res = new Result(1, "ok");
		;
		String sql = new String();
		DBUtils db = DBUtils.getInstance(appContext);
		String resume_time = null;
		// 简历id，更新时间
		{
			sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_id;
			sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
					+ DBUtils.RESUME_TYPE_RESUME;

			Cursor cursor = db.queryWithOrderBindUser(DBUtils.resumeTableName,
					new String[] { "*" }, sql, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					int Column = cursor
							.getColumnIndex(DBUtils.KEY_RESUME_RESUME_ID);
					int resumeid = cursor.getInt(Column);
					resumeEntity.setResume_Id(String.valueOf(resumeid));
					Column = cursor
							.getColumnIndex(DBUtils.KEY_RESUME_RESUME_TIME);
					String time = cursor.getString(Column);// getString(Column);
					resumeEntity.setResume_time(time);
					resume_time = time;
				}
				cursor.close();
				res = new Result(1, "ok");
			} else {
				return null;
			}
		}
		// 基本信息
		{
			sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_id;
			sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
					+ DBUtils.RESUME_TYPE_BASE;

			Cursor cursor = db.queryWithOrder(DBUtils.resumeTableName,
					new String[] { "*" }, sql, null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					int Column = cursor
							.getColumnIndex(DBUtils.KEY_RESUME_JSON_CONTENT);
					String jsoncontent = cursor.getString(Column);
					JSONObject obj;
					try {
						obj = new JSONObject(jsoncontent);
						ResumeBaseinfoEntity entity;

						entity = ResumeBaseinfoEntity
								.parse(appContext, obj, false, resume_id,
										resumeEntity.getResume_time());

						resumeEntity.setBaseinfoEntity(entity);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						res = new Result(-1, "json error");
						throw AppException.json(e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						res = new Result(-1, "Exception error");
						throw AppException.io(e);
					} catch (Exception e) {
						res = new Result(-1, "Exception error");
						throw AppException.io(e);
					} finally {
						cursor.close();
					}

				}
			}
		}
		// 工作经历
		{

			sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_id;
			sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
					+ DBUtils.RESUME_TYPE_JOBEXP;

			Cursor cursor = db.queryWithOrder(DBUtils.resumeTableName,
					new String[] { "*" }, sql, null,
					DBUtils.KEY_RESUME_RESUME_CONTENT_ID + " DESC");
			if (cursor != null) {
				try {
					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
							.moveToNext()) {

						int completeColumn = cursor
								.getColumnIndex(DBUtils.KEY_RESUME_COMPLETED_DEGREE);
						int complete = cursor.getInt(completeColumn);

						int Column = cursor
								.getColumnIndex(DBUtils.KEY_RESUME_JSON_CONTENT);
						String jsoncontent = cursor.getString(Column);
						JSONObject obj;
						obj = new JSONObject(jsoncontent);
						ResumeJobExpEntity jobentity;

						jobentity = ResumeJobExpEntity.parse(appContext, obj,
								false, resume_id, resume_time);

						if (jobentity != null && jobentity.getValidate().OK()) {
							jobentity
									.setBcompleted(complete > 0 ? true : false);
							resumeEntity.getmJobexpEntityList().add(jobentity);
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					res = new Result(-1, "json error");
					throw AppException.json(e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					res = new Result(-1, "Exception error");
					throw AppException.io(e);
				} finally {
					cursor.close();
				}

			}
		}
		// 教育经历
		{

			sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_id;
			sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
					+ DBUtils.RESUME_TYPE_EDUCATIONEXP;

			Cursor cursor = db.queryWithOrder(DBUtils.resumeTableName,
					new String[] { "*" }, sql, null,
					DBUtils.KEY_RESUME_RESUME_CONTENT_ID + " DESC");
			if (cursor != null) {
				try {
					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
							.moveToNext()) {
						int completeColumn = cursor
								.getColumnIndex(DBUtils.KEY_RESUME_COMPLETED_DEGREE);
						int complete = cursor.getInt(completeColumn);

						int Column = cursor
								.getColumnIndex(DBUtils.KEY_RESUME_JSON_CONTENT);
						String jsoncontent = cursor.getString(Column);
						JSONObject obj;

						obj = new JSONObject(jsoncontent);
						ResumeEducationExpEntity jobentity;

						jobentity = ResumeEducationExpEntity.parse(appContext,
								obj, false, resume_id, resume_time);

						if (jobentity != null && jobentity.getValidate().OK()) {
							jobentity
									.setBcompleted(complete > 0 ? true : false);
							resumeEntity.getMeducationExpEntityList().add(
									jobentity);
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					res = new Result(-1, "json error");
					throw AppException.json(e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					res = new Result(-1, "Exception error");
					throw AppException.io(e);
				} finally {
					cursor.close();
				}

			}
		}

		// 项目经历
		{

			sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_id;
			sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
					+ DBUtils.RESUME_TYPE_PROJECTEXP;

			Cursor cursor = db.queryWithOrder(DBUtils.resumeTableName,
					new String[] { "*" }, sql, null,
					DBUtils.KEY_RESUME_RESUME_CONTENT_ID + " DESC");
			if (cursor != null) {
				try {
					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
							.moveToNext()) {
						int completeColumn = cursor
								.getColumnIndex(DBUtils.KEY_RESUME_COMPLETED_DEGREE);
						int complete = cursor.getInt(completeColumn);

						int Column = cursor
								.getColumnIndex(DBUtils.KEY_RESUME_JSON_CONTENT);
						String jsoncontent = cursor.getString(Column);
						JSONObject obj;

						obj = new JSONObject(jsoncontent);
						ResumeProjectExpEntity jobentity;

						jobentity = ResumeProjectExpEntity.parse(appContext,
								obj, false, resume_id, resume_time);

						if (jobentity != null && jobentity.getValidate().OK()) {
							jobentity
									.setBcompleted(complete > 0 ? true : false);
							resumeEntity.getMprojectExpEntityList().add(
									jobentity);
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					res = new Result(-1, "json error");
					throw AppException.json(e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					res = new Result(-1, "Exception error");
					throw AppException.io(e);
				} finally {
					cursor.close();
				}

			}
		}

		// 语言技能
		{

			sql = DBUtils.KEY_RESUME_RESUME_ID + " = " + resume_id;
			sql += " AND " + DBUtils.KEY_RESUME_ROWDATA_TYPE + " = "
					+ DBUtils.RESUME_TYPE_LANGUAGEEXP;

			Cursor cursor = db.queryWithOrder(DBUtils.resumeTableName,
					new String[] { "*" }, sql, null,
					DBUtils.KEY_RESUME_RESUME_CONTENT_ID + " DESC");
			if (cursor != null) {
				try {
					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
							.moveToNext()) {
						int completeColumn = cursor
								.getColumnIndex(DBUtils.KEY_RESUME_COMPLETED_DEGREE);
						int complete = cursor.getInt(completeColumn);

						int Column = cursor
								.getColumnIndex(DBUtils.KEY_RESUME_JSON_CONTENT);
						String jsoncontent = cursor.getString(Column);
						JSONObject obj;

						obj = new JSONObject(jsoncontent);
						ResumeLanguageExpEntity jobentity;

						jobentity = ResumeLanguageExpEntity.parse(appContext,
								obj, false, resume_id, resume_time);

						if (jobentity != null && jobentity.getValidate().OK()) {
							jobentity
									.setBcompleted(complete > 0 ? true : false);
							resumeEntity.getMlanguageExpEntityList().add(
									jobentity);
						}

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					res = new Result(-1, "json error");
					throw AppException.json(e);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					res = new Result(-1, "Exception error");
					throw AppException.io(e);
				} finally {
					cursor.close();
				}

			}
		}
		resumeEntity.setValidate(res);
		return resumeEntity;

	}

	private void setHeadPhotoToView(String url, ImageView imgv, int defaultbmpId) {

		CommonRoundImgCreator creator = new CommonRoundImgCreator(
				ResumeEditHomeActivity.this,
				Constants.ResumeModule.HEADPHOTO_STAND_RADIUS,
				Constants.ResumeModule.HEADPHOTO_STAND_BODER_WIDTH,
				Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

		Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
				defaultbmpId);
		if (url != null) {
			bmpManager.loadBitmap(url, imgv, defaultbmp, creator);
		} else {
			imgv.setImageBitmap(defaultbmp);
		}

	}

	private void getNetImgBaseUrl(final AppContext appcontext) {
		Thread t = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				String baseurl = null;
				/*
				 * List<GlobalDataTable> list = GlobalDataTable.getTpyeData(
				 * appcontext, DBUtils.GLOBALDATA_TYPE_PICTUREBASEADDRESS); if
				 * (list != null && list.size() > 0) { GlobalDataTable data =
				 * list.get(0); baseurl = data.getName(); }
				 */
				baseurl = URLs.RESUME_PICTURE_BASESUBMIT;
				mNetpicBaseUrl = baseurl;
			}

		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);

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
								// ((ResumeEditHomeActivity) mcontext).quit();
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
			intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_RESUMEID,
					resume_id);

			setResult(resultCode, intent);
		}
		((ResumeEditHomeActivity) mcontext).quit();

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
		mresumeEntity = (ResumeEntity) savedInstanceState
				.getSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
		if (mresumeEntity != null)
			resume_id = StringUtils.toInt(mresumeEntity.getResume_Id(), 0);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub

		outState.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
				mresumeEntity);
		super.onSaveInstanceState(outState);

	}
}

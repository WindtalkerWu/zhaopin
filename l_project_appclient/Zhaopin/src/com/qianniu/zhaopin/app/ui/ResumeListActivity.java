package com.qianniu.zhaopin.app.ui;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.auth.BasicUserPrincipal;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.adapter.ResumeListAdapt;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntityList;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonRoundImgCreator;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.ResumeUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.ThreadPoolController;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.constant.Constants;
import com.qianniu.zhaopin.app.dialog.ProgressDialog;
import com.qianniu.zhaopin.app.view.ExplainInfoMiddlePop;
import com.qianniu.zhaopin.app.view.HorizontalListView;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResumeListActivity extends BaseActivity {

	// handler 处理的事件类型

	private static final int MSG_LOADDATA_OK = 0;
	private static final int MSG_LOADDATA_ERROR = 21;
	private static final int MSG_LOADDATA_APPEXCEPTION = 22;
	// 数据加载事件
	private static final int MSG_DATALOAD_START = 1;
	private static final int MSG_DATALOAD_OVER = 2;
	// 数据上报
	private static final int MSG_DATASUBMIT_START = 3;
	private static final int MSG_DATASUBMIT_OVER = 4;

	public static final int RESUMES_NUMBER_MAX = 10;

	private Context mcontext;
	private AppContext mappcontext;

	private TextView changeTimeText;
	private TextView nameText;
	private TextView hopeJobText;
	private ImageView defaultresumeIv;
	private ImageView creditIv;
	private ImageButton newBtn;
	private LinearLayout operateLayout;
	private LinearLayout operateLayout1;
	private Button delBtn;
	private Button defaultBtn;
	private Button editBtn;
	private Button previewBtn;
	private Button quickrecommendBtn;
	private Button recommendBtn;

	private View bigaddview;
	private Button selelctOk;

	private LinearLayout actbar_edit;
	private LinearLayout actbar_recommend;

	private ImageView completeImgview;
	private ImageView photoImgview;
	private BitmapManager bmpManager;
	private ProgressDialog mprogressDialog;

	private String resumeSelectedId;
	private ResumeListAdapt mAdapter;
	private List<ResumeSimpleEntity> mlistData = new ArrayList<ResumeSimpleEntity>();
	private HorizontalListView mhorizonlv;
	private int mSelectedPositon = -1;
	private ResumeSimpleEntity mentity;
	private ResumeSimpleEntity mdefaultentity;
	private int mdefaultindex = -1;

	private HorizontalScrollView mScrollView;
	private LinearLayout mviewcontainer;
	private LayoutInflater mlayoutInflater;
	private int mWidthPixels;

	private String from;
	private boolean bsubmitFlag = false;
	private boolean bfirstguide = false;

	private String m_strTaskId; // 悬赏任务ID
	private String m_strTaskTitle; // 悬赏任务Title(此处为职位名)
	// private String m_strResumeId; // 当前被选中的简历id
	private String m_strCompanyName; // 公司名
	private String m_strName; // 被推荐人姓名

	private boolean m_isRecommenMode = false;
	// 是否在推荐中
	private boolean m_bLockRecommend = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		mappcontext = (AppContext) this.getApplication();

		// 友盟统计
		UmShare.UmsetDebugMode(mcontext);

		setContentView(R.layout.resumelist_activity);
		bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				getResources(), R.drawable.widget_dface_loading));

		photoImgview = (ImageView) findViewById(R.id.headphoto_imgview);
		ImageButton back = (ImageButton) findViewById(R.id.resumelist_goback);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		completeImgview = (ImageView) findViewById(R.id.complete_imgview);
		Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(this, 0,
				Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
				Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
				Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
				Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
		completeImgview.setImageBitmap(completebmp);

		photoImgview = (ImageView) findViewById(R.id.headphoto_imgview);
		Bitmap photobmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.person_photo_normal_big);
		photobmp = ImageUtils.createRoundHeadPhoto(this, photobmp);
		photoImgview.setImageBitmap(photobmp);

		ViewGroup myresume = (ViewGroup) findViewById(R.id.resume_photo_item);
		myresume.setOnClickListener(mlistener);
		// 默认简历
		findViewById(R.id.default_imgview).setOnClickListener(mlistener);
		findViewById(R.id.credit_imgview).setOnClickListener(mlistener);

		newBtn = (ImageButton) findViewById(R.id.resumelist_newbutn);
		newBtn.setOnClickListener(mlistener);
		operateLayout = (LinearLayout) findViewById(R.id.resume_actbar_edit);
		operateLayout1 = (LinearLayout) findViewById(R.id.resume_actbar_recommend);
		delBtn = (Button) findViewById(R.id.resumelist_del_bt);
		delBtn.setOnClickListener(mlistener);
		defaultBtn = (Button) findViewById(R.id.resumelist_default_bt);
		defaultBtn.setOnClickListener(mlistener);
		editBtn = (Button) findViewById(R.id.resumelist_edit_bt);
		editBtn.setOnClickListener(mlistener);
		previewBtn = (Button) findViewById(R.id.resumelist_preview_bt);
		previewBtn.setOnClickListener(mlistener);
		selelctOk = (Button) findViewById(R.id.resumelist_ok);
		selelctOk.setOnClickListener(mlistener);
		quickrecommendBtn = (Button) findViewById(R.id.resumelist_quickrecommend_bt);
		quickrecommendBtn.setOnClickListener(mlistener);
		recommendBtn = (Button) findViewById(R.id.resumelist_recommend_bt);
		recommendBtn.setOnClickListener(mlistener);

		changeTimeText = (TextView) findViewById(R.id.resume_time_tv);
		nameText = (TextView) findViewById(R.id.resume_name_tv);
		nameText.setOnClickListener(mlistener);
		hopeJobText = (TextView) findViewById(R.id.resume_job_tv);
		defaultresumeIv = (ImageView) findViewById(R.id.default_imgview);
		creditIv = (ImageView) findViewById(R.id.credit_imgview);

		mprogressDialog = new ProgressDialog(this);
		// mprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mhorizonlv = (HorizontalListView) findViewById(R.id.resumelist_horizonlv);
		mAdapter = new ResumeListAdapt(ResumeListActivity.this, mlistData);
		mhorizonlv.setAdapter(mAdapter);
		mhorizonlv.setOnItemClickListener(mitemlistener);

		mWidthPixels = getResources().getDisplayMetrics().widthPixels;
		mScrollView = (HorizontalScrollView) findViewById(R.id.resumelist_horizonScrollView);
		mviewcontainer = (LinearLayout) findViewById(R.id.resumelistcontainer);
		mlayoutInflater = LayoutInflater.from(mcontext);

		bigaddview = initListItemAddView();
		mviewcontainer.addView(bigaddview);
		loadResumeData(mappcontext, lvDataHandler);
		getIntentData();
	}

	private boolean getSubmitFlag() {
		return bsubmitFlag;
	}

	private void setSubmitFlag(boolean newFlag) {
		bsubmitFlag = newFlag;
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			from = intent.getStringExtra(HeadhunterPublic.RESUMELIST_CALLTYPE);
			operateLayout.setVisibility(View.VISIBLE);
			operateLayout1.setVisibility(View.GONE);
		}
		if (HeadhunterPublic.RESUMELIST_CALLTYPE_PUBLISHREWARD.equals(from)) {
			resumeSelectedId = intent
					.getStringExtra(HeadhunterPublic.RESUMELIST_RESUMESELECTID);
			operateLayout.setVisibility(View.GONE);
			operateLayout1.setVisibility(View.VISIBLE);
			selelctOk.setVisibility(View.VISIBLE);

		} else if (HeadhunterPublic.RESUMELIST_CALLTYPE_REWARDINFO.equals(from)) {
			m_strTaskId = intent
					.getStringExtra(HeadhunterPublic.REWARD_DATATRANSFER_TASKID);
			m_strTaskTitle = intent
					.getStringExtra(HeadhunterPublic.REWARD_DATATRANSFER_TASKTITLE);
			m_strCompanyName = intent
					.getStringExtra(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYNAME);

			delBtn.setVisibility(View.GONE);
			defaultBtn.setVisibility(View.GONE);

			// 设置推荐按钮状态
			recommendBtn.setBackgroundResource(R.drawable.common_button_gray);
			recommendBtn.setEnabled(false);
			recommendBtn.setVisibility(View.VISIBLE);

			// 开关控制通讯录推荐是否显示
			if(ConfigOptions.RECOMMEND_CONTACTS){
				quickrecommendBtn.setVisibility(View.VISIBLE);
			}else{
				quickrecommendBtn.setVisibility(View.GONE);
			}

			m_isRecommenMode = true;

			TextView tvTitle = (TextView) findViewById(R.id.resumelist_title);
			if (null != tvTitle) {
				tvTitle.setText(getString(R.string.str_rewardrecommend_title));
			}

			operateLayout.setVisibility(View.GONE);
			operateLayout1.setVisibility(View.VISIBLE);
		}
	}

	private void displayResume(ResumeSimpleEntity entity) {
		mentity = entity;

		int completedegree = 0;
		if (entity != null) {
			String time = entity.getModifyTime();
			if (time != null) {
				time = StringUtils.formatDateStringToModifyYMD(time);
				if (time != null && time.length() > 0) {
					changeTimeText.setText(time);
				} else {
					changeTimeText.setText("");
				}
			}

			m_strName = entity.getName();
			if (m_strName != null && m_strName.length() > 0) {
				nameText.setText(m_strName);
			} else {
				nameText.setText("");
			}

			String job = entity.getPersonalTitle();
			if (job != null && job.length() > 0) {
				hopeJobText.setText(job);
			} else {
				hopeJobText.setText("");
			}
			completedegree = entity.getPercent();
			Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(this,
					completedegree,
					Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
					Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
					Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
					Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
			completeImgview.setImageBitmap(completebmp);

			CommonRoundImgCreator creator = new CommonRoundImgCreator(
					ResumeListActivity.this,
					Constants.ResumeModule.HEADPHOTO_STAND_RADIUS,
					Constants.ResumeModule.HEADPHOTO_STAND_BODER_WIDTH,
					Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

			Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.person_photo_normal_big);
			bmpManager.loadBitmap(entity.getHeadphotoUrl(), photoImgview,
					defaultbmp, creator);

			int defaultstatus = entity.getDefaultAuthenticat();
			if (defaultstatus == ResumeSimpleEntity.RESUME_DEFAULT_UNSELECTED)
				defaultresumeIv.setImageResource(R.drawable.default_normal);
			else
				defaultresumeIv.setImageResource(R.drawable.default_light);
		} else {
			// changeTimeText.setText(R.string.resume_modifytime);
			changeTimeText.setText("");
			nameText.setText(R.string.str_quickrecommend_name);
			hopeJobText.setText(R.string.resume_hopetitle);

			Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(this,
					completedegree,
					Constants.ResumeModule.COMPLETEBMP_STAND_RADIUS,
					Constants.ResumeModule.COMPLETEBMP_STAND_BODER_WIDTH,
					Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
					Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
			completeImgview.setImageBitmap(completebmp);

			CommonRoundImgCreator creator = new CommonRoundImgCreator(
					ResumeListActivity.this,
					Constants.ResumeModule.HEADPHOTO_STAND_RADIUS,
					Constants.ResumeModule.HEADPHOTO_STAND_BODER_WIDTH,
					Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

			Bitmap defaultbmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.person_photo_normal_big);
			Bitmap photobmp = creator.creator(defaultbmp);
			photoImgview.setImageBitmap(photobmp);

			defaultresumeIv.setImageResource(R.drawable.default_normal);
		}

	}

	private View initListItemAddView() {
		View addtView = mlayoutInflater.inflate(R.layout.resumelist_item, null);

		/*
		 * ResumeListItemViewHolder viewholder = new ResumeListItemViewHolder();
		 * // 获取控件对象 viewholder.name = (TextView) convertView
		 * .findViewById(R.id.resume_name_tv); viewholder.completeIv =
		 * (ImageView) convertView .findViewById(R.id.complete_imgview);
		 * 
		 * viewholder.bottomline = convertView .findViewById(R.id.bottomline);
		 */
		ImageView photoIv = (ImageView) addtView
				.findViewById(R.id.headphoto_imgview);
		CommonRoundImgCreator creator = new CommonRoundImgCreator(
				ResumeListActivity.this,
				Constants.ResumeModule.COMPLETEBMP_SMALL_RADIUS,
				Constants.ResumeModule.HEADPHOTO_SMALL_BODER_WIDTH,
				Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

		Bitmap defaultbmp = BitmapFactory.decodeResource(
				mcontext.getResources(), R.drawable.person_photo_add_big);

		defaultbmp = creator.creator(defaultbmp);
		photoIv.setImageBitmap(defaultbmp);
		View bottomline = addtView.findViewById(R.id.bottomline);
		bottomline.setVisibility(View.INVISIBLE);
		TextView name = (TextView) addtView.findViewById(R.id.resume_name_tv);
		name.setText("新建简历");
		addtView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计--我的简历库--创建(左下角)按钮
				UmShare.UmStatistics(mcontext, "Resumelist_NewResume_LeftButton");
				if (isNewResumeAbled()) {
					UIHelper.showMyResumeEditActivityForResult(
							ResumeListActivity.this, 0);
				} else {
					UIHelper.ToastMessage(mcontext,
							String.format("您最多可新建%d份简历", RESUMES_NUMBER_MAX));
				}

			}
		});
		return addtView;
	}

	private void setSelectedItem(int position, boolean bjump) {
		ResumeSimpleEntity entity = mlistData.get(position);
		entity.setSelected(true);
		if (mSelectedPositon >= 0) {
			ResumeSimpleEntity preentity = mlistData.get(mSelectedPositon);
			entity.setSelected(false);
		}
		mSelectedPositon = position;
		if (bjump)
			mhorizonlv.setSelection(position);

		mAdapter.notifyDataSetInvalidated();
		displayResume(entity);
	}

	private Handler mdeleteHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case ResumeUtils.RESUME_DEL_OK: {

				Result result = (Result) msg.obj;
				if (result == null) {
					if (msg.what == ResumeUtils.RESUME_DEL_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_delete_faile);
					} else if (msg.what == ResumeUtils.RESUME_SETDEFAULT_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_setdefault_faile);
					}

				} else if (result.OK()) {
					if (msg.what == ResumeUtils.RESUME_DEL_OK) {
						String resumeid = mentity.getResumeId();

						Thread t = new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									if (mappcontext != null && mentity != null)
										mappcontext.deleteSimpleResumeInDb(
												mappcontext,
												mentity.getResumeId());
								} catch (AppException e) {
									// TODO Auto-generated catch block

								}
							}
						});
						if (threadPool == null) {
							threadPool = ThreadPoolController.getInstance();
						}
						threadPool.execute(t);

						int index = -1;
						for (int i = 0; i < mlistData.size(); i++) {
							if (mlistData.get(i).getResumeId().equals(resumeid)) {
								index = i;
								break;
							}
						}

						if (mlistData != null && mlistData.size() > index) {
							mlistData.remove(index);

						}

						refreshResumlistDisplay(mlistData);
					} else if (msg.what == ResumeUtils.RESUME_SETDEFAULT_OK) {
						if (mdefaultentity != null) {
							mdefaultentity
									.setDefaultAuthenticat(ResumeSimpleEntity.RESUME_DEFAULT_UNSELECTED);
						}
						mentity.setDefaultAuthenticat(ResumeSimpleEntity.RESUME_DEFAULT_SELECTED);
						String newid = (mentity != null) ? mentity
								.getResumeId() : null;
						String oldid = (mdefaultentity != null) ? mdefaultentity
								.getResumeId() : null;
						updateResumeDataInDb(oldid, newid);
						mdefaultentity = mentity;
						refreshResumlistDisplay(mlistData);

					}
					if (msg.what == ResumeUtils.RESUME_DEL_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_delete_success);
					} else if (msg.what == ResumeUtils.RESUME_SETDEFAULT_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_setdefault_success);
					}

				} else {
					if (msg.what == ResumeUtils.RESUME_DEL_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_delete_faile);
					} else if (msg.what == ResumeUtils.RESUME_SETDEFAULT_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_setdefault_faile);
					}
					boolean bact = result
							.handleErrcode(ResumeListActivity.this);
				}
				setSubmitFlag(false);
				submitHandler.sendEmptyMessage(MSG_DATASUBMIT_OVER);
			}
				break;
			case ResumeUtils.RESUME_DEL_ERR:
			case ResumeUtils.RESUME_SUBMIT_ERROR:
			case ResumeUtils.RESUME_SUBMIT_NETERROR:
			case ResumeUtils.RESUME_SUBMIT_DATAERROR:
			case ResumeUtils.RESUME_SUBMIT_EXCEPTIONERROR:
				submitHandler.sendEmptyMessage(MSG_DATASUBMIT_OVER);
				setSubmitFlag(false);
				if (msg.what == ResumeUtils.RESUME_SUBMIT_NETERROR
						|| msg.what == ResumeUtils.RESUME_SUBMITDEL_NETERROR) {
					UIHelper.ToastMessage(mcontext,
							R.string.app_status_net_disconnected);
				} else {
					UIHelper.ToastMessage(mcontext,
							R.string.dialog_delete_faile);
				}
				// UIHelper.ToastMessage(mcontext,
				// R.string.dialog_submit_failed);
				break;
			}
		}

	};

	private Handler submitHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {

			case MSG_DATASUBMIT_START:
				setSubmitFlag(true);
				mprogressDialog.setMessage(R.string.dialog_datasubmitmsg);
				mprogressDialog.show();
				break;
			case MSG_DATASUBMIT_OVER:
				setSubmitFlag(false);
				mprogressDialog.dismiss();
				break;
			case ResumeUtils.RESUME_SETDEFAULT_OK: {

				Result result = (Result) msg.obj;
				if (result == null) {
					if (msg.what == ResumeUtils.RESUME_DEL_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_delete_faile);
					} else if (msg.what == ResumeUtils.RESUME_SETDEFAULT_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_setdefault_faile);
					}

				} else if (result.OK()) {
					if (msg.what == ResumeUtils.RESUME_DEL_OK) {
						String resumeid = mentity.getResumeId();

						Thread t = new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									if (mappcontext != null && mentity != null)
										mappcontext.deleteSimpleResumeInDb(
												mappcontext,
												mentity.getResumeId());
								} catch (AppException e) {
									// TODO Auto-generated catch block

								}
							}
						});
						if (threadPool == null) {
							threadPool = ThreadPoolController.getInstance();
						}
						threadPool.execute(t);

						int index = -1;
						for (int i = 0; i < mlistData.size(); i++) {
							if (mlistData.get(i).getResumeId().equals(resumeid)) {
								index = i;
								break;
							}
						}

						if (mlistData != null && mlistData.size() > index) {
							mlistData.remove(index);

						}

						refreshResumlistDisplay(mlistData);
					} else if (msg.what == ResumeUtils.RESUME_SETDEFAULT_OK) {
						if (mdefaultentity != null) {
							mdefaultentity
									.setDefaultAuthenticat(ResumeSimpleEntity.RESUME_DEFAULT_UNSELECTED);
						}
						mentity.setDefaultAuthenticat(ResumeSimpleEntity.RESUME_DEFAULT_SELECTED);
						String newid = (mentity != null) ? mentity
								.getResumeId() : null;
						String oldid = (mdefaultentity != null) ? mdefaultentity
								.getResumeId() : null;
						updateResumeDataInDb(oldid, newid);
						mdefaultentity = mentity;
						refreshResumlistDisplay(mlistData);

					}
					if (msg.what == ResumeUtils.RESUME_DEL_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_delete_success);
					} else if (msg.what == ResumeUtils.RESUME_SETDEFAULT_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_setdefault_success);
					}

				} else {
					if (msg.what == ResumeUtils.RESUME_DEL_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_delete_faile);
					} else if (msg.what == ResumeUtils.RESUME_SETDEFAULT_OK) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_setdefault_faile);
					}
					boolean bact = result
							.handleErrcode(ResumeListActivity.this);
				}
				setSubmitFlag(false);
				submitHandler.sendEmptyMessage(MSG_DATASUBMIT_OVER);
			}
				break;

			case ResumeUtils.RESUME_SETDEFAULT_ERR:
			case ResumeUtils.RESUME_SUBMIT_ERROR:
			case ResumeUtils.RESUME_SUBMIT_NETERROR:
			case ResumeUtils.RESUME_SUBMIT_DATAERROR:
			case ResumeUtils.RESUME_SUBMIT_EXCEPTIONERROR:
				submitHandler.sendEmptyMessage(MSG_DATASUBMIT_OVER);
				setSubmitFlag(false);
				if (msg.what == ResumeUtils.RESUME_SUBMIT_NETERROR
						|| msg.what == ResumeUtils.RESUME_SUBMITDEL_NETERROR) {
					UIHelper.ToastMessage(mcontext,
							R.string.app_status_net_disconnected);
				} else {
					UIHelper.ToastMessage(mcontext,
							R.string.dialog_setdefault_faile);
				}
				// UIHelper.ToastMessage(mcontext,
				// R.string.dialog_submit_failed);
				break;
			}

		}

	};
	private Handler lvDataHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {

			case MSG_DATALOAD_START:
				mprogressDialog.setMessage(R.string.dialog_dataloadmsg);
				mprogressDialog.show();
				break;
			case MSG_DATALOAD_OVER:

				mprogressDialog.dismiss();
				break;
			case MSG_LOADDATA_ERROR: {
				// boolean bflag = res
				// .handleErrcode(ProfessionalInfoActivity.this);
				// if (!bflag) {
				// UIHelper.ToastMessage(m_Context,
				// R.string.dialog_data_get_err);
				// }
				UIHelper.ToastMessage(mcontext, R.string.dialog_data_get_err);

			}
				break;
			case MSG_LOADDATA_APPEXCEPTION: {
				AppException e = (AppException) msg.obj;
				if (e != null) {
					e.makeToast(mcontext);
				}

			}
				break;
			case MSG_LOADDATA_OK: {
				ResumeSimpleEntityList entitylist = (ResumeSimpleEntityList) msg.obj;
				Result res = entitylist.getValidate();
				if (!res.OK()) {
					if (res.getErrorCode() == Result.CODE_RESUME_LIST_EMPTY) {
						UIHelper.ToastMessage(mcontext,
								R.string.resume_list_empty);
						//delBtn.setEnabled(false);
						//defaultBtn.setEnabled(false);
						break;
					}
					boolean bflag = res.handleErrcode(ResumeListActivity.this);
					if (!bflag) {
						UIHelper.ToastMessage(mcontext,
								R.string.dialog_data_get_err);
					}
				} else {
					if (entitylist.getEntitylist() != null
							&& entitylist.getEntitylist().size() > 0) {
						mlistData.clear();
						mlistData.addAll(0, entitylist.getEntitylist());
						ResumelistRefeshTask task = new ResumelistRefeshTask();
						task.execute(mlistData);

						if (m_isRecommenMode) {
							// 设置推荐按钮状态
							recommendBtn
									.setBackgroundResource(R.drawable.common_button_green);
							recommendBtn.setEnabled(true);
						}
					}

					// if (entitylist.getEntitylist() != null
					// && entitylist.getEntitylist().size() > 0) {
					// mlistData.clear();
					// mlistData.addAll(0, entitylist.getEntitylist());
					// mAdapter.notifyDataSetChanged();
					// }
					// int defaultlocation = -1;
					// if (mlistData.size() > 0) {
					// defaultlocation = 0;
					// }
					// for (int i = 0; i < mlistData.size(); i++) {
					// if (mlistData.get(i).getDefaultAuthenticat() ==
					// ResumeSimpleEntity.RESUME_DEFAULT_SELECTED) {
					// defaultlocation = i;
					// break;
					// }
					// }
					//
					/*
					 * ResumeSimpleEntity entity =
					 * mlistData.get(defaultlocation); displayResume(entity);
					 */
				}
			}
				break;

			}
		}

	};

	private void refreshResumlistDisplay(List<ResumeSimpleEntity> listdata) {
		ResumelistRefeshTask task = new ResumelistRefeshTask();
		task.execute(listdata);
	}

	private void loadResumeData(final AppContext appcontext,
			final Handler handler) {
		if (!appcontext.isNetworkConnected()) {
			UIHelper.ToastMessage(this, R.string.app_status_net_disconnected);
		}
		// handler.sendEmptyMessage(MSG_DATALOAD_START);
		Thread t = new Thread() {
			public void run() {
				handler.sendEmptyMessage(MSG_DATALOAD_START);
				boolean bnetConnect = appcontext.isNetworkConnected();
				boolean bnetDataException = false;
				ResumeSimpleEntityList entitylist = null;
				Message msg = new Message();
				// bnetConnect = false;
				if (bnetConnect) {
					try {
						entitylist = appcontext
								.getResumeSimpleEntityListFromNet(appcontext,
										true);
					} catch (AppException e) {
						// TODO Auto-generated catch block
						bnetDataException = true;

						Message exception_msg = new Message();
						exception_msg.what = MSG_LOADDATA_APPEXCEPTION;
						exception_msg.obj = e;
						handler.sendMessage(exception_msg);
					}
				}
				if (entitylist == null || bnetDataException) {
					try {
						entitylist = appcontext
								.getSimpleResumeListFromDb(mappcontext);
					} catch (AppException e) {
						// TODO Auto-generated catch block
						msg.obj = e;
					}
				}
				if (entitylist != null) {
					msg.what = MSG_LOADDATA_OK;
					msg.obj = entitylist;
				} else {
					msg.what = MSG_LOADDATA_ERROR;

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

	OnItemClickListener mitemlistener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub

			setSelectedItem(position, false);
		}
	};

	OnClickListener mlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.resumelist_del_bt: {
				
				/*
				 * // 友盟统计--我的简历库--删除简历按钮 UmShare.UmStatistics(mcontext,
				 * "Resumelist_DelButton"); if (getSubmitFlag()) { break; } if
				 * (mentity != null && mentity.getResumeId() != null) { int
				 * resumeid = StringUtils.toInt(mentity.getResumeId(), 0);
				 * submitHandler.sendEmptyMessage(MSG_DATASUBMIT_START);
				 * ResumeUtils.deleteResume(mappcontext, resumeid,
				 * mdeleteHandler); }
				 */
				if (mentity != null) {
					showDeleteAlertDialog();
				} else {
					UIHelper.ToastMessage(mcontext, R.string.resume_list_empty);
				}

			}
				break;
			case R.id.default_imgview: {
				ExplainInfoMiddlePop pop = new ExplainInfoMiddlePop(mcontext);
				pop.setExplainInfoText("默认简历");
				int btn_width = v.getWidth() / 2;
				DisplayMetrics dm = AppContext
						.getPhoneDisplayMetrics(mappcontext);
				int xoffset = (int) ((-50) * dm.density + btn_width);
				int yoffset = (int) (3 * dm.density);
				pop.showAsDropDown(v, xoffset, 0);
				pop.update();
			}
				break;
			case R.id.resumelist_edit_bt: {
				if (mentity != null) {
					String url = mentity.getResumeUrl();
					int resumeid = StringUtils.toInt(mentity.getResumeId(), 0);
					if (resumeid == 0 || url == null || url.length() == 0) {
						UIHelper.ToastMessage(mcontext, "当前简历无法编辑");
					} else {
						UIHelper.showMyResumeEditActivityForResult(
								ResumeListActivity.this, resumeid);
					}

				} else {
					UIHelper.ToastMessage(mcontext, R.string.resume_list_empty);
				}
			}
				break;
			case R.id.resumelist_preview_bt: {
				if (mentity != null) {
					String url = mentity.getResumeUrl();
					String resumeid = mentity.getResumeId();
					// int resumeid = StringUtils.toInt(mentity.getResumeId(),
					// 0);
					if (resumeid == null || url == null
							|| resumeid.length() == 0 || url.length() == 0) {
						UIHelper.ToastMessage(mcontext, "当前简历无法预览");
					} else {
						UIHelper.showResumePreviewForResult(
								ResumeListActivity.this, url, resumeid);
					}

				} else {
					UIHelper.ToastMessage(mcontext, R.string.resume_list_empty);
				}
			}
				break;
			case R.id.credit_imgview: {
				ExplainInfoMiddlePop pop = new ExplainInfoMiddlePop(mcontext);
				pop.setExplainInfoText("信用等级");
				int btn_width = v.getWidth() / 2;
				DisplayMetrics dm = AppContext
						.getPhoneDisplayMetrics(mappcontext);
				int xoffset = (int) ((-50) * dm.density + btn_width);
				int yoffset = (int) (3 * dm.density);
				pop.showAsDropDown(v, xoffset, 0);
				pop.update();
			}
				break;
			case R.id.resumelist_default_bt: {
				// 友盟统计--我的简历库--设置默认简历按钮
				UmShare.UmStatistics(mcontext, "Resumelist_DefaultButton");
				if (getSubmitFlag()) {
					break;
				}
				if (mentity != null && mentity.getResumeId() != null) {
					int resumeid = StringUtils.toInt(mentity.getResumeId(), 0);
					submitHandler.sendEmptyMessage(MSG_DATASUBMIT_START);
					ResumeUtils.setDefaultResume(mappcontext, resumeid,
							submitHandler);
				} else {
					UIHelper.ToastMessage(mcontext, R.string.resume_list_empty);
				}
			}
				break;
			case R.id.resumelist_newbutn: {
				// 友盟统计--我的简历库--创建(右上角)按钮
				UmShare.UmStatistics(mcontext, "Resumelist_NewResume_RightButton");
				if (isNewResumeAbled()) {
					UIHelper.showMyResumeEditActivityForResult(
							ResumeListActivity.this, 0);
				} else {
					UIHelper.ToastMessage(mcontext,
							String.format("您最多可新建%d份简历", RESUMES_NUMBER_MAX));
				}

			}
				break;
			case R.id.resume_photo_item: {
				// 友盟统计--我的简历库--简历预览按钮
				UmShare.UmStatistics(mcontext, "Resumelist_HeadPhotoImgview");
				/*
				 * int resumeid = 0; if (mentity != null) resumeid =
				 * StringUtils.toInt(mentity.getResumeId(), 0);
				 * UIHelper.showMyResumeEditActivity(mcontext, resmeid);
				 */
				if (mentity != null) {

					String url = mentity.getResumeUrl();
					String resumeid = mentity.getResumeId();
					// int resumeid = StringUtils.toInt(mentity.getResumeId(),
					// 0);
					if (resumeid == null || url == null
							|| resumeid.length() == 0 || url.length() == 0) {
						UIHelper.ToastMessage(mcontext, "当前简历无法预览");
					} else {
						UIHelper.showResumePreviewForResult(
								ResumeListActivity.this, url, resumeid);
					}

				} else {
					UIHelper.ToastMessage(mcontext, R.string.resume_list_empty);
					/*
					 * ExplainInfoMiddlePop pop = new
					 * ExplainInfoMiddlePop(mcontext);
					 * pop.setExplainInfoText(R.string.resume_list_empty); int
					 * btn_width = v.getWidth() / 2; DisplayMetrics dm =
					 * AppContext .getPhoneDisplayMetrics(mappcontext); int
					 * xoffset = (int) ((-50) * dm.density + btn_width); int
					 * yoffset = (int) (3 * dm.density); pop.showAsDropDown(v,
					 * xoffset, 0); pop.update();
					 */

				}

			}
				break;
			case R.id.resume_name_tv: {
				UIHelper.showResumePreview(mcontext, mentity);
			}
				break;
			case R.id.resumelist_ok: {
				if (HeadhunterPublic.RESUMELIST_CALLTYPE_PUBLISHREWARD
						.equals(from)) {
					if (mselectentity != null) {
						setResult(
								HeadhunterPublic.RESUME_SELECT_RESULT_OK,
								getIntent().putExtra(
										HeadhunterPublic.RESUME_SELECT_RESULT,
										mselectentity));
						finish();
					} else {
						UIHelper.ToastMessage(getApplicationContext(),
								R.string.resume_list_select_null);
					}
				}
			}
				break;
			case R.id.resumelist_quickrecommend_bt: {
				// 友盟统计--推荐简历--快速推荐按钮
				UmShare.UmStatistics(mcontext,
						"JobRecommend_QuickRecommendButton");
				// 数据传输
				Bundle bundle = new Bundle();
				bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_TASKID,
						m_strTaskId);
				bundle.putString(
						HeadhunterPublic.REWARD_DATATRANSFER_TASKTITLE,
						m_strTaskTitle);
				bundle.putString(
						HeadhunterPublic.REWARD_DATATRANSFER_COMPANYNAME,
						m_strCompanyName);

				// 进入快速推荐界面
				Intent intent = new Intent();
				intent.setClass(mcontext, QuickRecommendActivity.class);
				intent.putExtras(bundle);
				startActivityForResult(intent,
						HeadhunterPublic.RESULT_ACTIVITY_CODE);
			}
				break;
			case R.id.resumelist_recommend_bt: {
				// 友盟统计--推荐简历--推荐按钮
				UmShare.UmStatistics(mcontext, "JobRecommend_RecommendButton");

				showPromptAlertDialog();
			}
				break;

			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case ActivityRequestCode.RESULT_ACTIVITY_LOGIN: {
			loadResumeData(mappcontext, lvDataHandler);
		}
			break;
		case ActivityRequestCode.RESULT_ACTIVITY_NEWRESUME: {
			loadResumeData(mappcontext, lvDataHandler);
		}
			break;

		case ActivityRequestCode.RESULT_ACTIVITY_RESUMEPREVIEW: {
			loadResumeData(mappcontext, lvDataHandler);
		}
			break;

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/* loadResumeData(mappcontext, lvDataHandler); */

		// 友盟统计
		UmShare.UmResume(mcontext);

		if (bfirstguide) {
			loadResumeData(mappcontext, lvDataHandler);
			bfirstguide = false;
		}
		// 判断是否是第一次启动
		if (DMSharedPreferencesUtil.VALUE_FLG_FIRSTRESUMELIBRARY != DMSharedPreferencesUtil
				.getSharePreInt(mcontext,
						DMSharedPreferencesUtil.DM_HOTLABEL_DB,
						DMSharedPreferencesUtil.FIELD_FIRST_RESUMELIBRARY)) {
			// 加载引导界面
			Intent intent = new Intent(mcontext, ResumeNewGuideActivity.class);
			mcontext.startActivity(intent);
			bfirstguide = true;

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	private View mselectedView;
	private ResumeSimpleEntity mselectentity;

	private void setSelectedView(View view) {
		if (view == null) {
			displayResume(null);
			return;
		}
		ResumeListItemViewHolder newholder = (ResumeListItemViewHolder) view
				.getTag();

		if (mselectedView != null) {
			ResumeListItemViewHolder oldholder = (ResumeListItemViewHolder) mselectedView
					.getTag();
			oldholder.bottomline.setVisibility(View.INVISIBLE);
		}
		newholder.bottomline.setVisibility(View.VISIBLE);
		mselectedView = view;
		int left = view.getLeft();
		int width = view.getWidth();
		int x = (int) (view.getLeft() + view.getWidth() / 2.0 - mWidthPixels / 2.0);
		mScrollView.smoothScrollTo(x, 0);
		mselectentity = newholder.entity;
		displayResume(newholder.entity);
	}

	private OnClickListener listviewlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setSelectedView(v);
		}
	};

	public static class ResumeListItemViewHolder { // 自定义控件集合
		public TextView name;
		public ImageView photoIv;
		public ImageView completeIv;
		public View bottomline;
		public ResumeSimpleEntity entity;
		public int postion;
	}

	public class ResumelistRefeshTask extends
			AsyncTask<List<ResumeSimpleEntity>, Integer, List<View>> {

		private int defaultlocation = -1;

		public ResumelistRefeshTask() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onPostExecute(List<View> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			mviewcontainer.removeAllViews();
			mviewcontainer.addView(bigaddview);
			for (int i = 0; i < result.size(); i++) {
				ResumeListItemViewHolder viewholder = (ResumeListItemViewHolder) result
						.get(i).getTag();
				ResumeSimpleEntity entity = viewholder.entity;
				String name = entity.getName();
				if (name != null && name.length() > 0) {
					viewholder.name.setText(name);
				}

				int percent = entity.getPercent();

				Bitmap completebmp = ImageUtils.createCompleteDegreeBitmap(
						ResumeListActivity.this, percent,
						Constants.ResumeModule.COMPLETEBMP_SMALL_RADIUS,
						Constants.ResumeModule.COMPLETEBMP_SMALL_BODER_WIDTH,
						Constants.ResumeModule.COMPLETEBMP_STAND_ARCS_COLOR,
						Constants.ResumeModule.COMPLETEBMP_STAND_CIRCLE_COLOR);
				viewholder.completeIv.setImageBitmap(completebmp);

				CommonRoundImgCreator creator = new CommonRoundImgCreator(
						ResumeListActivity.this,
						Constants.ResumeModule.HEADPHOTO_SMALL_RADIUS,
						Constants.ResumeModule.HEADPHOTO_SMALL_BODER_WIDTH,
						Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);

				Bitmap defaultbmp = BitmapFactory.decodeResource(
						mcontext.getResources(),
						R.drawable.person_photo_normal_small);
				bmpManager.loadBitmap(entity.getHeadphotoUrl(),
						viewholder.photoIv, defaultbmp, creator);
				viewholder.bottomline.setVisibility(View.INVISIBLE);
				mviewcontainer.addView(result.get(i));
				if (entity.getResumeId().equals(resumeSelectedId)) {
					viewholder.bottomline.setVisibility(View.VISIBLE);
					defaultlocation = i;
				}
			}
			if (result.size() == 0) {
				//delBtn.setEnabled(false);
				//defaultBtn.setEnabled(false);
			} else {
				delBtn.setEnabled(true);
				defaultBtn.setEnabled(true);
			}
			if (defaultlocation != -1)
				setSelectedView(result.get(defaultlocation));
			else {
				setSelectedView(null);
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected List<View> doInBackground(List<ResumeSimpleEntity>... params) {
			// TODO Auto-generated method stub
			ArrayList<View> listview = new ArrayList<View>();
			List<ResumeSimpleEntity> listentity = params[0];
			if (listentity.size() > 0) {
				defaultlocation = 0;
			} else {
				defaultlocation = -1;
			}

			for (int i = 0; i < listentity.size(); i++) {
				View convertView = mlayoutInflater.inflate(
						R.layout.resumelist_item, null);
				ResumeSimpleEntity entity = listentity.get(i);
				if (entity.getDefaultAuthenticat() == ResumeSimpleEntity.RESUME_DEFAULT_SELECTED) {
					defaultlocation = i;
					mdefaultentity = entity;
					mdefaultindex = i;
				}
				ResumeListItemViewHolder viewholder = new ResumeListItemViewHolder();
				// 获取控件对象
				viewholder.name = (TextView) convertView
						.findViewById(R.id.resume_name_tv);
				viewholder.completeIv = (ImageView) convertView
						.findViewById(R.id.complete_imgview);
				viewholder.photoIv = (ImageView) convertView
						.findViewById(R.id.headphoto_imgview);
				viewholder.bottomline = convertView
						.findViewById(R.id.bottomline);
				viewholder.postion = i;
				viewholder.entity = entity;
				convertView.setTag(viewholder);
				convertView.setOnClickListener(listviewlistener);

				listview.add(convertView);

			}
			return listview;
		}

	}

	private void updateResumeDataInDb(final String oldDefId,
			final String newDefId) {

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (oldDefId != null && newDefId != null
						&& oldDefId.equalsIgnoreCase(newDefId))
					return;
				try {
					if (mappcontext != null && newDefId != null)
						mappcontext.updateDefaultSimpleResumeInDb(mappcontext,
								newDefId, true);
				} catch (AppException e) {
					// TODO Auto-generated catch block

				}
				try {
					if (mappcontext != null && oldDefId != null)
						mappcontext.updateDefaultSimpleResumeInDb(mappcontext,
								oldDefId, false);
				} catch (AppException e) {
					// TODO Auto-generated catch block

				}
			}
		});
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 友盟统计
		UmShare.UmPause(mcontext);
	}

	private void showDeleteAlertDialog() {
		Dialog dialog = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.resume_delresume)
				.setMessage(R.string.resume_delresume_alertmsg)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// ((ResumeEditHomeActivity) mcontext).quit();
								deleteResume();
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

	private void deleteResume() {

		// 友盟统计--我的简历库--删除简历按钮
		UmShare.UmStatistics(mcontext, "Resumelist_DelButton");
		if (getSubmitFlag()) {
			return;
		}
		if (mentity != null && mentity.getResumeId() != null) {
			int resumeid = StringUtils.toInt(mentity.getResumeId(), 0);
			submitHandler.sendEmptyMessage(MSG_DATASUBMIT_START);
			ResumeUtils.deleteResume(mappcontext, resumeid, mdeleteHandler);
		}

	}

	/**
	 * 应聘提示框
	 */
	private void showPromptAlertDialog() {
		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.str_quickrecommend_title));

		LayoutInflater inflater = (LayoutInflater) mcontext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View textView = (View) inflater.inflate(
				R.layout.alertdialog_prompt_twotext, null);
		TextView tvMsg = (TextView) textView
				.findViewById(R.id.alertdialog_prompt_twotext_tv_msg);
		
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        	tvMsg.setTextColor(mcontext.getResources().getColor(R.color.white));
        }

		// 消息内容
		String strMsg = String.format(
				mcontext.getString(R.string.str_quickrecommend_ask_content),
				m_strName, m_strCompanyName, m_strTaskTitle);
		tvMsg.setText(strMsg);

		dl.setView(textView);
		dl.setPositiveButton(getString(R.string.dialog_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

						// 确认要快速推荐
						Recommend();
					}

				});
		dl.setNegativeButton(getString(R.string.dialog_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}

				});
		dl.show();
	}

	/**
	 * 发送接受任务请求
	 */
	private void LinkRecommend() {
		try {
			AppContext ac = (AppContext) getApplication();

			String resumeid = mentity.getResumeId();
			Result res = ac.applyReward(m_strTaskId, String
					.valueOf(HeadhunterPublic.APPLYTASK_SENDTYPE_RECOMMEND),
					resumeid);
			if (res.OK()) {
				// 成功
				m_handler
						.sendMessage(m_handler
								.obtainMessage(HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_SUCCESS));
			} else {
				if (Result.CODE_TOKEN_INVALID == res.getErrorCode()
						|| Result.CODE_TOKEN_OVERTIME == res.getErrorCode()) {
					// 重新登录
					m_handler
							.sendMessage(m_handler
									.obtainMessage(HeadhunterPublic.REWARDRECOMMEND_MSG_QUICKRECOMMEND_LOGIN));
				} else {
					// 失败
					m_handler
							.sendMessage(m_handler
									.obtainMessage(
											HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_FAIL,
											res.getErrorMessage()));
				}
			}
		} catch (AppException e) {
			e.printStackTrace();
			// 异常
			m_handler
					.sendMessage(m_handler
							.obtainMessage(
									HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_ABNORMAL,
									e));
		}
	}

	private void Recommend() {
		showProgressDialog();

		m_bLockRecommend = true;

		Thread t = new Thread() {
			public void run() {
				LinkRecommend();
			}
		};
		if (threadPool == null) {
			threadPool = ThreadPoolController.getInstance();
		}
		threadPool.execute(t);
	}

	/**
	 * 
	 */
	private Handler m_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_SUCCESS: {
				dismissProgressDialog();

				UIHelper.ToastMessage(ResumeListActivity.this,
						R.string.msg_rewardrecommend_recommend_success);

				m_bLockRecommend = false;
			}
				break;
			case HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_ABNORMAL: {
				dismissProgressDialog();
				((AppException) msg.obj).makeToast(ResumeListActivity.this);
			}
				break;
			case HeadhunterPublic.REWARDRECOMMEND_MSG_APPLYTASK_FAIL: {
				dismissProgressDialog();

				UIHelper.ToastMessage(ResumeListActivity.this,
						R.string.msg_rewardrecommend_recommend_fail);

				m_bLockRecommend = false;
			}
				break;
			case HeadhunterPublic.REWARDRECOMMEND_MSG_QUICKRECOMMEND_LOGIN: {
				dismissProgressDialog();

				UIHelper.showLoginActivityForResult(ResumeListActivity.this);
			}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 判断是否可以新建简历
	 * 
	 * @return
	 */
	private boolean isNewResumeAbled() {
		boolean bable = true;
		if (mlistData != null) {
			if (mlistData.size() >= RESUMES_NUMBER_MAX) {
				bable = false;
			}
		}
		return bable;
	}
}

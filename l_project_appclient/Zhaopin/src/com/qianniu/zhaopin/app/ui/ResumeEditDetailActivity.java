package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.ResumeBaseinfoEntity;
import com.qianniu.zhaopin.app.bean.ResumeEducationExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumeJobExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeLanguageExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeProjectExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.common.ActivityRequestCode;
import com.qianniu.zhaopin.app.common.ImageUtils;
import com.qianniu.zhaopin.app.common.ResumeUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class ResumeEditDetailActivity extends BaseActivity {
	private static final int RESUME_RETURN_SELFINFO = 3001;
	private static final int RESUME_RETURN_JOBINTENSION = 3002;
	private static final int RESUME_RETURN_WORKEXP = 3003;
	private static final int RESUME_RETURN_PROJECTEXP = 3004;
	private static final int RESUME_RETURN_EDUCATION = 3005;
	private static final int RESUME_RETURN_LANGUAGE = 3006;

	private Context mcontext;
	private AppContext mappcontext;
	private ResumeEntity mresumeEntity;
	private int resume_id;

	private TextView hopeText;
	private TextView workText;
	private TextView projectText;
	private TextView educationText;
	private TextView languageText;
	
	private ImageButton mbackImgBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mcontext = this;
		mappcontext = (AppContext) this.getApplication();
		setContentView(R.layout.resume_edit_detail);
		if (savedInstanceState != null) {
			mresumeEntity = (ResumeEntity) savedInstanceState
					.get(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			if (mresumeEntity != null)
				resume_id = StringUtils.toInt(mresumeEntity.getResume_Id(), 0);
		} else {
			Intent intent = getIntent();
			Bundle bundle = intent.getExtras();
			mresumeEntity = (ResumeEntity) bundle.getSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE);
			resume_id = bundle.getInt("resumeid");

		}
		hopeText = (TextView) findViewById(R.id.resume_hopeText);
		workText = (TextView) findViewById(R.id.resume_workText);
		projectText = (TextView) findViewById(R.id.resume_projectText);
		educationText = (TextView) findViewById(R.id.resume_educationText);
		languageText = (TextView) findViewById(R.id.resume_languageText);

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
		
		mbackImgBtn = (ImageButton) findViewById(R.id.resume_edit_goback);
		mbackImgBtn.setOnClickListener(mlistener);
		displayResumeEntity(mresumeEntity);
	}

	private void displayResumeEntity(ResumeEntity entity) {
		int completedegree = 0;
		if (entity == null)
			return;

		ResumeBaseinfoEntity baseinfo = entity.getBaseinfoEntity();
		if (baseinfo != null) {

			boolean bcompleted = ResumeBaseinfoEntity
					.isCompletedForJobIntension(baseinfo);
			if (bcompleted) {
				hopeText.setText(R.string.resume_edit_comlete);
			} else {
				hopeText.setText(R.string.resume_edit_uncomlete);
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

				languageText.setText(R.string.resume_edit_comlete);
			} else {
				languageText.setText(R.string.resume_edit_uncomlete);
			}
		}

	}

	OnClickListener mlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.resume_edit_goback: {
				setResultAndFinish(RESULT_OK);
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
				bundle.putParcelableArrayList(
						ResumeEditHomeActivity.INTENT_KEY_PARCELABLEARRAY, list);
				bundle.putInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID,
						resume_id);
				intent.putExtras(bundle);
				intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_DATATYPE,
						ResumeEditHomeActivity.INTENT_DATATYPE_EDUCATION);
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
				bundle.putParcelableArrayList(
						ResumeEditHomeActivity.INTENT_KEY_PARCELABLEARRAY, list);
				bundle.putInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID,
						resume_id);
				intent.putExtras(bundle);
				intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_DATATYPE,
						ResumeEditHomeActivity.INTENT_DATATYPE_LANGUAGE);
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
				bundle.putParcelableArrayList(
						ResumeEditHomeActivity.INTENT_KEY_PARCELABLEARRAY, list);
				bundle.putInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID,
						resume_id);
				intent.putExtras(bundle);
				intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_DATATYPE,
						ResumeEditHomeActivity.INTENT_DATATYPE_PROJECTEXP);
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
				bundle.putParcelableArrayList(
						ResumeEditHomeActivity.INTENT_KEY_PARCELABLEARRAY, list);
				bundle.putInt(ResumeEditHomeActivity.INTENT_KEY_RESUMEID,
						resume_id);
				intent.putExtras(bundle);
				intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_DATATYPE,
						ResumeEditHomeActivity.INTENT_DATATYPE_WORKEXP);
				startActivityForResult(intent, RESUME_RETURN_WORKEXP);
			}
				break;

			}
		}
	};
	// 修改判断
	private boolean bedit = false;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Ignore failed requests
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
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

		}
		displayResumeEntity(mresumeEntity);
	}
	private void setResultAndFinish(int resultCode) {
		Intent intent = new Intent();
		intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_RESUMEID, resume_id);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ResumeEditHomeActivity.INTENT_KEY_SERIALIZE,
				mresumeEntity);
		intent.putExtra(ResumeEditHomeActivity.INTENT_KEY_EDITFLAG, bedit);
		intent.putExtras(bundle);
		setResult(resultCode, intent);
		this.finish();
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
			setResultAndFinish(RESULT_OK);
		} else {
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
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

package com.qianniu.zhaopin.app.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.Entity;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.ResumeEducationExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeEntity;
import com.qianniu.zhaopin.app.bean.ResumeJobExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeLanguageExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeProjectExpEntity;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.ui.ResumeEditMainActivity;
import com.qianniu.zhaopin.app.view.ResumeBriefTagItem.OnExecuteAct;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ResumeMultiTagBriefItem extends ResumeBriefInfoItemSlot {

	private int mtype = -1;
	private List<GlobalDataTable> mLanguageDatalist = null;
	private List<GlobalDataTable> mMasterDatalist = null;
	private OnExecuteAct mExecuteAct;
	private OnMultiBirefItemAct mBirefAct;
	

	public ResumeMultiTagBriefItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setTitle("项目经历");
		setSymbol(R.drawable.add_button);
	}

	public ResumeMultiTagBriefItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void initView(int type, List<? extends Entity> entityList) {
		mtype = type;
		bind(entityList);
		String titleStr = "标题";
		int resid = R.drawable.add_button;
		switch (mtype) {
		case ResumeEditMainActivity.INTENT_DATATYPE_LANGUAGE: {
			titleStr = getResources().getString(
					R.string.resume_title_languageexp);

			resid = R.drawable.add_button;
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_EDUCATION: {
			titleStr = getResources().getString(
					R.string.resume_title_educationexp);

			resid = R.drawable.add_button;
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_PROJECTEXP: {
			titleStr = getResources().getString(
					R.string.resume_title_projectexp);

			resid = R.drawable.add_button;
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_WORKEXP: {
			titleStr = getResources().getString(R.string.resume_title_workexp);

			resid = R.drawable.add_button;
		}
			break;
		default:
			break;
		}
		setTitle(titleStr);
		setSymbol(resid);
	}

	@Override
	protected View getBriefTag(Entity entity) {
		// TODO Auto-generated method stub
		int status = getEntitySatus(entity);
		if(status == ResumeEntity.RESUME_BLOCKSTATUS_DEL)
			return null;
		ResumeBriefTagItem tag = new ResumeBriefTagItem(mcontext);
		tag.bindEntityAndType(entity,mtype);
		tag.setOnExecuteAct(mExecuteAct);
		String title = getTagTitle(entity);
		tag.setTitle(title);
		String content = getTagContent(entity);
		tag.setContent(content);
		String itemid = getEntityItemId(entity);
		mTagMap.put(itemid, tag);
		return tag;
	}
	public void setOnExecuteAct(OnExecuteAct act){
		mExecuteAct = act;
	}
	

	private String getTagTitle(Entity entity) {
		String title = null;
		switch (mtype) {
		case ResumeEditMainActivity.INTENT_DATATYPE_LANGUAGE: {
			if (mLanguageDatalist == null)
				mLanguageDatalist = GlobalDataTable.getTpyeData(mappcontext,
						DBUtils.GLOBALDATA_TYPE_LANGUAGE);
			String languageid = ((ResumeLanguageExpEntity) entity)
					.getLanguageid();
			String language = null;
			for (int j = 0; j < mLanguageDatalist.size(); j++) {
				if (mLanguageDatalist.get(j).getID().equals(languageid)) {
					language = mLanguageDatalist.get(j).getName();
					break;
				}
			}
			title = language;
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_EDUCATION: {
			title = "学校：" + ((ResumeEducationExpEntity) entity).getSchoolname();
			;
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_PROJECTEXP: {
			title = "项目：" + ((ResumeProjectExpEntity) entity).getProjectname();
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_WORKEXP: {
			title = "公司：" + ((ResumeJobExpEntity) entity).getCompany();
		}
			break;
		default:
			break;
		}
		return title;
	}

	private String getTagContent(Entity entity) {
		String str = null;
		switch (mtype) {
		case ResumeEditMainActivity.INTENT_DATATYPE_LANGUAGE: {
			if (mMasterDatalist == null)
				mMasterDatalist = GlobalDataTable.getTpyeData(mappcontext,
						DBUtils.GLOBALDATA_TYPE_LANGUAGEMASTERY);
			String masterstr = null;
			String masterid = ((ResumeLanguageExpEntity) entity).getMasterid();
			for (int j = 0; j < mMasterDatalist.size(); j++) {
				if (mMasterDatalist.get(j).getID().equals(masterid)) {
					masterstr = mMasterDatalist.get(j).getName();
					break;
				}
			}

			str = masterstr;
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_EDUCATION: {
			str = getFormatTimeStr(
					((ResumeEducationExpEntity) entity).getStartdate(),
					((ResumeEducationExpEntity) entity).getEnddate());

		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_PROJECTEXP: {

			str = getFormatTimeStr(
					((ResumeProjectExpEntity) entity).getStartdate(),
					((ResumeProjectExpEntity) entity).getEnddate());
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_WORKEXP: {

			str = getFormatTimeStr(
					((ResumeJobExpEntity) entity).getStartdate(),
					((ResumeJobExpEntity) entity).getEnddate());
		}
			break;
		default:
			break;
		}
		return str;
	}
	private int getEntitySatus(Entity entity) {
		int  status =  ResumeEntity.RESUME_BLOCKSTATUS_NORMAL;
		switch (mtype) {
		case ResumeEditMainActivity.INTENT_DATATYPE_LANGUAGE: {
			status = ((ResumeLanguageExpEntity) entity).getStatus();
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_EDUCATION: {
			status = ((ResumeEducationExpEntity) entity).getStatus();
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_PROJECTEXP: {
			status = ((ResumeProjectExpEntity) entity).getStatus();
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_WORKEXP: {
			status = ((ResumeJobExpEntity) entity).getStatus();
		}
			break;
		default:
			break;
		}
		return status;
	}
	private String getEntityItemId(Entity entity) {
		String itmeid =  null;
		switch (mtype) {
		case ResumeEditMainActivity.INTENT_DATATYPE_LANGUAGE: {
			itmeid = ((ResumeLanguageExpEntity) entity).getItemid();
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_EDUCATION: {
			itmeid = ((ResumeEducationExpEntity) entity).getItemid();
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_PROJECTEXP: {
			itmeid = ((ResumeProjectExpEntity) entity).getItemid();
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_WORKEXP: {
			itmeid = ((ResumeJobExpEntity) entity).getItemid();
		}
			break;
		default:
			break;
		}
		return itmeid;
	}
	
	public void setOnAddClick(OnMultiBirefItemAct act){
		mBirefAct = act;
		setAddBtnClickListerner(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBirefAct.OnAddClick(mtype,mcontext);
			}
		});
	}
	public interface OnMultiBirefItemAct{
		public void OnAddClick(int type,Context context);
	};
}

package com.qianniu.zhaopin.app.view;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.bean.Entity;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.ResumeBaseinfoEntity;
import com.qianniu.zhaopin.app.bean.ResumeLanguageExpEntity;
import com.qianniu.zhaopin.app.bean.ResumeQuickItemEntity;
import com.qianniu.zhaopin.app.common.ResumeUtils;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.ui.ResumeEditMainActivity;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResumeSigleTagBriefItem extends ResumeBriefInfoItemSlot {
	private int mtype = -1;
	private OnSingleBirefItemAct mBirefAct;

	public ResumeSigleTagBriefItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ResumeSigleTagBriefItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void initView(int type, List<Entity> entityList) {
		mtype = type;
		bind(entityList);
		String titleStr = "标题";
		String btnStr = "编辑";
		int resid = R.drawable.add_button;
		switch (mtype) {
		case ResumeEditMainActivity.INTENT_DATATYPE_FASTCONTENT: {
			titleStr = getResources().getString(R.string.resume_fastresume);

			resid = R.drawable.add_button;
			btnStr = "编辑";
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_JOBINTENSION: {
			titleStr = getResources().getString(R.string.resume_jobtarget);

			resid = R.drawable.add_button;
			btnStr = "编辑";
		}
			break;

		default:
			break;
		}
		setTitle(titleStr);
		setSymbol(resid);
		setAddBtnText(btnStr);
	}

	@Override
	protected View getBriefTag(Entity entity) {
		// TODO Auto-generated method stub
		View tag = null;
		switch (mtype) {
		case ResumeEditMainActivity.INTENT_DATATYPE_FASTCONTENT: {
			if (entity != null) {
				ResumeQuickItemEntity infoentity= (ResumeQuickItemEntity) entity;
				LayoutInflater inflater = LayoutInflater.from(mcontext);
				tag =  inflater.inflate(
						R.layout.resume_tagbrief_quickcontent, null);
				((TextView)tag).setText(infoentity.getContentStr());

			}
			 
		}
			break;
		case ResumeEditMainActivity.INTENT_DATATYPE_JOBINTENSION: {
			if (entity != null) {
				ResumeBaseinfoEntity baseentity = (ResumeBaseinfoEntity) entity;
				LayoutInflater inflater = LayoutInflater.from(mcontext);
				tag =  inflater.inflate(
						R.layout.resume_briefinfo_jobintension, null);

				TextView tvJobstatus = (TextView) tag
						.findViewById(R.id.job_status);
				GlobalDataTable table = GlobalDataTable.getTypeDataById(
						mappcontext, DBUtils.GLOBALDATA_TYPE_JOBSTATUS,
						baseentity.getjobgetstatusId());
				if (table != null && table.getName() != null) {
					tvJobstatus.setText(table.getName());
				}
				
				TextView tvindustry = (TextView) tag
						.findViewById(R.id.hope_industry);
				displayMultiIdInTextView(baseentity.getJobtradeIdlist(), DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY, 2, tvindustry);

				TextView tvCity = (TextView) tag
						.findViewById(R.id.hope_city);
				displayMultiIdInTextView(baseentity.getJobcityIdlist(), DBUtils.GLOBALDATA_TYPE_CITY, 1, tvCity);
				
				TextView tvPosition= (TextView) tag
						.findViewById(R.id.position_btn);
				String positionid = baseentity.getPosition_id();
				if (positionid != null) {
					//职位名称是3级数据
					table = GlobalDataTable.getGlobalDataTable(
							mappcontext, DBUtils.GLOBALDATA_TYPE_JOBFUNCTION,3,
							positionid);
					if (table != null && table.getName() != null) {
						tvPosition.setText(table.getName());
					}
					
				}
				

	
			}
		}
			break;

		default:
			break;
		}
		return tag;
	}

	private void displayMultiIdInTextView(ArrayList<String> list,int ntype,int nlevel,TextView tv){
		
		if (list != null && list.size() > 0) {
			String str = new String();
			for (int i = 0; i < list.size(); i++) {
				GlobalDataTable table = GlobalDataTable
						.getGlobalDataTable(mappcontext,
								ntype,nlevel,
								list.get(i));
				if (table != null) {
					if (str.length() > 0)
						str += ",";
					str += table.getName();
				}
			}
			if(tv != null)
				tv.setText(str);

		}
	}
	
	public void setOnAddClick(OnSingleBirefItemAct act) {
		mBirefAct = act;
		setAddBtnClickListerner(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBirefAct.OnAddClick(mtype, mcontext);
			}
		});
	}

	public interface OnSingleBirefItemAct {
		public void OnAddClick(int type, Context context);
	};
}

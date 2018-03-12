package com.qianniu.zhaopin.app.view;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.bean.Entity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResumeBriefTagItem extends LinearLayout{
	
	private LayoutInflater minflater;
	private TextView mtv_title;
	private TextView mtv_content;
	private ImageView miv_delte;
	private ImageView miv_edit;
	private OnExecuteAct mExecuteAct;
	private Entity mentity;
	private int mtype;
	private ViewGroup mLayout;
	public ResumeBriefTagItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(lp);
		this.setOrientation(HORIZONTAL);
		minflater = LayoutInflater.from(context);
		minflater.inflate(R.layout.resume_brief_tag_item, this);
		mLayout = this; 
		mtv_title = (TextView) findViewById(R.id.tv_title);
		mtv_content = (TextView) findViewById(R.id.tv_content);
		miv_delte = (ImageView) findViewById(R.id.iv_delete);
		miv_edit = (ImageView) findViewById(R.id.iv_edit);
		
		miv_delte.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mExecuteAct != null){
					mExecuteAct.delAction(mentity,mtype, mLayout);
				}
			}
		});
		miv_edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mExecuteAct != null){
					mExecuteAct.editAction(mentity,mtype);
				}
			}
		});
	}

	public ResumeBriefTagItem(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

		
	}
	public void bindEntityAndType(Entity entity,int type){
		mentity = entity;
		mtype = type;
	}
	
	public void setTitle(String str){
		 mtv_title.setText(str);
	}
	
	public void setContent(String str){
		mtv_content.setText(str);
	}
	public void setOnExecuteAct(OnExecuteAct act){
		mExecuteAct = act;
	}
	
	public interface OnExecuteAct{
		public void delAction(Entity entity,int type,View view);
		public void editAction(Entity entity,int type);
	};
}

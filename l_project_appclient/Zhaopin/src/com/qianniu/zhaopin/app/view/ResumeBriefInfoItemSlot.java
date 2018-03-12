package com.qianniu.zhaopin.app.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.Entity;
import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.view.ResumeBriefTagItem.OnExecuteAct;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public abstract class ResumeBriefInfoItemSlot extends LinearLayout {
	protected Context mcontext;
	protected AppContext mappcontext;
	private LayoutInflater minflater;
	private ImageButton mibtn_shrink;
	private ImageView miv_symbol;
	private TextView mtv_title;
	private TextView mtv_subTitle;
	private Button mbtn_add;
	private TextView mtv_prompt;
	private ViewGroup mTagContainer;

	protected Map<String, View> mTagMap = new HashMap<String, View>();

	public ResumeBriefInfoItemSlot(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mcontext = context;
		mappcontext = (AppContext) context.getApplicationContext();
		minflater = LayoutInflater.from(context);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(lp);
		this.setOrientation(VERTICAL);

		minflater.inflate(R.layout.resume_edit_singleitem_container, this);

		mibtn_shrink = (ImageButton) findViewById(R.id.ibtn_shrink);
		miv_symbol = (ImageView) findViewById(R.id.ibtn_symbol);
		mtv_title = (TextView) findViewById(R.id.tv_title);
		mtv_subTitle = (TextView) findViewById(R.id.tv_completeDegree);
		mbtn_add = (Button) findViewById(R.id.btn_add);

		mtv_prompt = (TextView) findViewById(R.id.tv_prompt);
		mTagContainer = (ViewGroup) findViewById(R.id.resume_brief_tagContainer);
		mibtn_shrink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mTagContainer.getVisibility() == GONE) {
					mibtn_shrink.setImageResource(R.drawable.shrink_up);
					mTagContainer.setVisibility(VISIBLE);
					Animation pushin = AnimationUtils.loadAnimation(mcontext,
							R.anim.push_top_in);
					mTagContainer.startAnimation(pushin);
				} else {
					if(mTagContainer.getChildCount() == 0)
						return ;
					mibtn_shrink.setImageResource(R.drawable.shrink_down);
					mTagContainer.setVisibility(GONE);
				}
			}
		});
		refreshShrinkState();
	}
	private void refreshShrinkState(){

		// TODO Auto-generated method stub
		if (mTagContainer.getChildCount() >0 &&mTagContainer.getVisibility() == GONE) {
			mibtn_shrink.setImageResource(R.drawable.shrink_down);

		} else if(mTagContainer.getChildCount() >0 &&mTagContainer.getVisibility() == VISIBLE){
			mibtn_shrink.setImageResource(R.drawable.shrink_up);			
		}else{
			mibtn_shrink.setImageResource(R.drawable.shrink_up);
		}
	
	}
	public ResumeBriefInfoItemSlot(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();

	}

	public void bind(List<? extends Entity> entityList) {
		refreshShrinkState();
		if (entityList == null)
			return;
		mTagContainer.removeAllViews();
		mTagMap.clear();
		for (Entity entity : entityList) {
			View tag = getBriefTag(entity);
			if (tag != null) {
				mTagContainer.addView(tag);

			}
		}
		promptViewAutoVisble();
	}

	private void promptViewAutoVisble() {
		if (mTagContainer.getChildCount() > 0) {
			mtv_prompt.setVisibility(GONE);
		} else {
			mtv_prompt.setVisibility(VISIBLE);
		}
	}

	public void setTitle(String str) {
		if (str != null)
			mtv_title.setText(str);
	}

	public void setSubTitle(String str) {
		if (str != null)
			mtv_subTitle.setText(str);
	}
	public void setSubTitleVisible(int visibility) {
			mtv_subTitle.setVisibility(visibility);
	}

	public void setCompledStatus(boolean bcompleted){
		if(bcompleted){
			String str = "("+getResources().getString(R.string.resume_edit_comlete)+")";
			mtv_subTitle.setText(str);
		}else{
			String str = "("+getResources().getString(R.string.resume_edit_uncomlete)+")";
			mtv_subTitle.setText(str);
		}
	}
	public void setPrompt(String str) {
		if (str != null)
			mtv_prompt.setText(str);
	}

	public void setPrompt(int strid) {

		mtv_prompt.setText(strid);
	}

	public void setSymbol(int resId) {
		miv_symbol.setImageResource(resId);
	}

	public void setAddBtnText(String str) {
		if (str != null)
			mbtn_add.setText(str);
	}

	protected void setAddBtnClickListerner(OnClickListener listener) {
		mbtn_add.setOnClickListener(listener);
	}

	protected abstract View getBriefTag(Entity entity);

	public void removeBriefTag(View view) {
		if (view != null) {
			mTagContainer.removeView(view);
			promptViewAutoVisble();
		}
	}

	/**
	 * @param startdate
	 *            :yyyy-MM-dd HH:mm:ss
	 * @param enddate
	 *            :yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public String getFormatTimeStr(String startdate, String enddate) {

		startdate = StringUtils.formatDateStringToYM(startdate);
		String tonow = getResources()
				.getString(R.string.str_date_to_now_YYMMDD);
		if (enddate != null && enddate.startsWith(tonow))
			enddate = "至今";
		else
			enddate = StringUtils.formatDateStringToYM(enddate);
		String timeinfo = new String();
		if (startdate == null)
			timeinfo += "...";
		else
			timeinfo += startdate;
		timeinfo += "至";
		if (enddate == null)
			timeinfo += "...";
		else
			timeinfo += enddate;
		return timeinfo;
	}

}

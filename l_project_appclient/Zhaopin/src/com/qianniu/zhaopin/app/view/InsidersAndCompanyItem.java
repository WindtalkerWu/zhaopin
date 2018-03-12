package com.qianniu.zhaopin.app.view;


import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.adapter.InsidersListAdapter;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.widget.CompanyLabelFlowLayout;
import com.qianniu.zhaopin.util.QNUtil;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InsidersAndCompanyItem extends RelativeLayout{
	
	private AppContext mContext;
	private LayoutInflater inflater;
	private View layout;
	private ImageView headPortrait;
	private TextView name;
	private TextView title;
	
	private TextView companyPostCount;
//	private ImageView auth;
	private TextView jobCount;
	private TextView attentionCount;
	private CompanyLabelFlowLayout labelFlowLayout;
	
	private BitmapManager bmpManager;

	private InsidersAndCompany industryInsiderPeople;
	
	private int imageWidth;

	public InsidersAndCompanyItem(Context mContext, BitmapManager bmpManager, InsidersAndCompany industryInsiderPeople) {
		this(mContext, bmpManager, industryInsiderPeople, -1);
	}
	public InsidersAndCompanyItem(Context mContext, BitmapManager bmpManager, InsidersAndCompany industryInsiderPeople, int imageWidth) {
		super(mContext);
		this.mContext = (AppContext)mContext;
		this.industryInsiderPeople = industryInsiderPeople;
		this.inflater = LayoutInflater.from(mContext);
		this.bmpManager = bmpManager;
		this.imageWidth = imageWidth;

		initView();
		initData(true);
	}
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
		ViewGroup.LayoutParams params = headPortrait.getLayoutParams();
		if (industryInsiderPeople != null && industryInsiderPeople.getType() == InsidersAndCompany.TYPECOMPANY) {
			params.height = (int)(imageWidth / 2.25);
		} else {
			params.height = imageWidth;
		}
		headPortrait.setLayoutParams(params);
	}
//	public View getLayout() {
//		return layout;
//	}
	
	public void setIndustryInsiderPeople(InsidersAndCompany industryInsiderPeople, boolean isLoadMore) {
		this.industryInsiderPeople = industryInsiderPeople;
		initData(isLoadMore);
//		if (isLoadMore) {
//			initData(true);
//		} else {
//			initData(true);
//		}
	}
	private void initView() {
		if (industryInsiderPeople != null && industryInsiderPeople.getType() == InsidersAndCompany.TYPECOMPANY ) {
			MyLogger.i("initView", industryInsiderPeople.getType() + "");
			layout = inflater.inflate(R.layout.company_item, this);
			headPortrait = (ImageView) layout.findViewById(R.id.company_item_head_portrait);
			name = (TextView) layout.findViewById(R.id.company_item_name);
			
			companyPostCount = (TextView) layout.findViewById(R.id.company_item_post_count);
			
			labelFlowLayout = (CompanyLabelFlowLayout) layout.findViewById(R.id.company_item_labels);
			attentionCount = (TextView) layout.findViewById(R.id.company_item_attention_count);
		} else {
			setBackgroundResource(R.drawable.common_list_item_selector);
			setPadding(0, 0, 0, 0);
			layout = inflater.inflate(R.layout.insiders_item, this);
			headPortrait = (ImageView) layout.findViewById(R.id.insiders_item_head_portrait);
			
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth - CommonUtils.dip2px(getContext(), 0.7f),
//					imageWidth - CommonUtils.dip2px(getContext(), 0.7f));
//			params.gravity = Gravity.CENTER_HORIZONTAL;
//			headPortrait.setLayoutParams(params);

			title = (TextView) layout.findViewById(R.id.insiders_item_title);
			name = (TextView) layout.findViewById(R.id.insiders_item_name);
//			auth = (ImageView) layout.findViewById(R.id.industry_insiders_item_auth);
//			jobCount = (TextView) layout.findViewById(R.id.industry_insiders_item_job_count);
			attentionCount = (TextView) layout.findViewById(R.id.insiders_item_attention_count);
		}
		setImageWidth(imageWidth);
//		
	}
	private void initData(boolean isLoadImage) {
		if (industryInsiderPeople != null) {
			loadImage(isLoadImage);
			layout.setVisibility(View.VISIBLE);
			name.setText(industryInsiderPeople.getName());
			if (title != null) {
				title.setText(industryInsiderPeople.getTitle());
			}
			if (companyPostCount != null) {
				String postCountStr = getContext().getResources().getString(R.string.company_item_post_count_txt);
				String task_count = industryInsiderPeople.getTask_count();
				if ("null".equals(task_count)) {
					task_count = "0";
				}
				String postCountResult = String.format(postCountStr, task_count);
				SpannableStringBuilder result = QNUtil.formateStringColor(postCountResult, 0, 
						postCountResult.length() - 4, mContext.getResources().getColor(R.color.resume_button_color));
				companyPostCount.setText(result);
			}
			
//			String attentionCountStr = getContext().getResources().getString(R.string.industry_insiders_item_attention_count_txt);
//			String attentionCountResult = String.format(attentionCountStr, industryInsiderPeople.getAttention_count());
//			SpannableStringBuilder result = QNUtil.formateStringColor(attentionCountResult, 0, attentionCountResult.length() - 4, Color.RED);
//			attentionCount.setText(result);
			
			attentionCount.setText(industryInsiderPeople.getAttention_count());
			
			if (industryInsiderPeople.getType() == InsidersAndCompany.TYPECOMPANY) {
				if (labelFlowLayout != null) {
					labelFlowLayout.removeAllViews();
					labelFlowLayout.initData(industryInsiderPeople.getTags());
				}
			}
		} else {
			layout.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setListener(View.OnClickListener onClickListener) {
		layout.setOnClickListener(onClickListener);
	}
	public void clearHeadImage() {
		headPortrait.setImageBitmap(null);;
	}

	//加载图片
	public void loadImage(boolean isLoadImage) {
		//R.drawable.qianniu_bg_widget
		headPortrait.setImageBitmap(null);
		if (isLoadImage) {
			bmpManager.loadBitmap(industryInsiderPeople.getPicture(), headPortrait);
		}
//		else {
//			headPortrait.setImageBitmap(null);
//		}
	}

	public void setClick(boolean isClickable) {
		setClickable(isClickable);
		layout.setClickable(isClickable);
	}
}

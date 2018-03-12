package com.qianniu.zhaopin.app.view;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.CompanyListAdapter;
import com.qianniu.zhaopin.app.bean.CompanyInfo;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.util.QNUtil;
import com.sina.weibo.sdk.utils.UIUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SeekWorthyTopView extends LinearLayout{

	private ImageView headImage;
	private TextView name;
//	private ImageView auth;
	private TextView title;
	private TextView company;
	private TextView attention;
	private TextView description;
	
	private InsidersAndCompany boss;
	private CompanyInfo companyInfo;
	
	private BitmapManager bmpManager;
	private Activity activity;
	
	private int headWidth = -1;
	private static final float SCALE =  2.25f;
	
	public SeekWorthyTopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public SeekWorthyTopView(Context context) {
		super(context);
		initView();
	}
	public SeekWorthyTopView(Activity activity, CompanyInfo companyInfo) {
		super(activity.getApplicationContext());
		this.activity = activity;
		init();
		setCompanyInfo(companyInfo);
	}
	public SeekWorthyTopView(Activity activity, InsidersAndCompany boss) {
		super(activity.getApplicationContext());
		this.activity = activity;
		init();
		setBoss(boss);
	}
	private void init() {
		setOrientation(VERTICAL);
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
//		int maggin = CommonUtils.dip2px(getContext(), 8);
//		setPadding(maggin, maggin, maggin, maggin);
//		params.setMargins(maggin, maggin, maggin, maggin);
		setLayoutParams(params);
		initView();
	}
	private void initView() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seek_worthy_top, this);

		headImage = (ImageView) findViewById(R.id.seekworthy_top_image);
		name = (TextView) findViewById(R.id.seekworthy_top_name);
//		auth = (ImageView) findViewById(R.id.seekworthy_top_auth);
		title = (TextView) findViewById(R.id.seekworthy_top_title);
		company = (TextView) findViewById(R.id.seekworthy_top_company);
		attention = (TextView) findViewById(R.id.seekworthy_top_attention);
		description = (TextView) findViewById(R.id.seekworthy_top_description);
		
		this.bmpManager = new BitmapManager(null);
	}
	public void setBoss(InsidersAndCompany boss) {
		this.boss = boss;
		refresh();
	}
	public void setCompanyInfo(CompanyInfo companyInfo) {
		this.companyInfo = companyInfo;
		refresh();
	}
	
	private void refresh() {
		if (boss != null) {
			RelativeLayout imageLayout = (RelativeLayout) findViewById(R.id.seekworthy_top_image_layout);
			LayoutParams params = (LayoutParams) imageLayout.getLayoutParams();
			params.topMargin = CommonUtils.dip2px(getContext(), 3);
			imageLayout.setLayoutParams(params);
			
			bmpManager.loadMiddleRoundBitmap(activity, boss.getPicture(), headImage);
			name.setText(boss.getName());
//			if (InsidersAndCompany.auth.equals(boss.getAuthenticate())) {
//				
//			}
//			String attentionString = getResources().getString(R.string.seek_worthy_attention);
//			String attentionResult = String.format(attentionString, boss.getAttention_count());
//			SpannableStringBuilder result = QNUtil.formateStringColor(attentionResult, 0, attentionResult.length() - 4, Color.RED);
			attention.setText(boss.getAttention_count());
			String titleStr = QNUtil.handleCharacter(boss.getTitle());
			if (!TextUtils.isEmpty(titleStr)) {
				title.setText(titleStr);
				title.setVisibility(View.VISIBLE);
			}
			company.setText(QNUtil.handleCharacter(boss.getCompany()));
			description.setText(QNUtil.handleCharacter(boss.getDescription()));
		} else if (companyInfo != null) {
			LinearLayout imageRight = (LinearLayout) findViewById(R.id.seekworthy_top_image_right);
			RelativeLayout imageLeft = (RelativeLayout) findViewById(R.id.seekworthy_top_image_layout);
			imageLeft.setBackgroundResource(R.drawable.common_form_bg);
			
			LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams)imageLeft.getLayoutParams();
			params1.width = CompanyListAdapter.logoWidth - 20;
			params1.height = (int)(params1.width / SCALE);
			imageLeft.setLayoutParams(params1);
			imageLeft.setPadding(0, 0, 0, 0);

			LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams)imageRight.getLayoutParams();
			params2.weight = 1;
//			params2.leftMargin = 5;
//			params2.topMargin = 5;
			imageRight.setLayoutParams(params2);
			
			RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams)headImage.getLayoutParams();
			params3.width = LayoutParams.MATCH_PARENT;
			params3.height = LayoutParams.MATCH_PARENT;
			headImage.setLayoutParams(params3);
			headImage.setBackgroundResource(R.drawable.qianniu_bg_widget);
			
			bmpManager.loadBitmap(companyInfo.getLogo(), headImage, null); //, (int)headWidth, (int)(headWidth / SCALE)
			name.setText(companyInfo.getTitle());
//			if (InsidersAndCompany.auth.equals(boss.getAuthenticate())) {	
//			}
//			String attentionString = getResources().getString(R.string.seek_worthy_attention);
//			String attentionResult = String.format(attentionString, companyInfo.getAttention_count());
//			SpannableStringBuilder result = QNUtil.formateStringColor(attentionResult, 0, attentionResult.length() - 4, Color.RED);
			company.setVisibility(View.GONE);
			attention.setText(companyInfo.getAttention_count());
//			title.setText(QNUtil.handleCharacter(companyInfo.getAddress()));
			title.setVisibility(View.GONE);
			description.setText(QNUtil.handleCharacter(companyInfo.getDescription()));
		}
	}
	
}

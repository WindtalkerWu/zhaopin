package com.qianniu.zhaopin.app.view;

import com.qianniu.zhaopin.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExposureWageTableLayout extends LinearLayout{
	
	public static final int HOTCOMPANY = 1;//热门公司
	public static final int HOTPOST = 2;//热门职位
	private int type;
	private LayoutInflater inflater;

	public ExposureWageTableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ExposureWageTableLayout(Context context) {
		super(context);
	}
	private void init() {
		inflater = LayoutInflater.from(getContext());
		setOrientation(VERTICAL);
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	private void initView() {
		initTitle();
	}
	private void refreshView() {
		removeAllViews();
		addItem();
	}
	private void initTitle() {
		View title = inflater.inflate(R.layout.exposure_wage_table_title, this);
		TextView titleTv = (TextView) findViewById(R.id.exposure_wage_table_title_txt);
		TextView postTv = (TextView) findViewById(R.id.exposure_wage_table_title_post);
		postTv.setText(getContext().getResources().getString(R.string.exposure_wage_home_hot_company));
		switch (type) {
		case HOTCOMPANY:
			titleTv.setText(getContext().getResources().getString(R.string.exposure_wage_home_hot_company));
			break;
		case HOTPOST:
			titleTv.setText(getContext().getResources().getString(R.string.exposure_wage_home_hot_company));
			break;
		default:
			break;
		}
	}
	private void addItem() {
		switch (type) {
		case HOTCOMPANY:
			addHotCompanyItemView();//采用for循环来添加数据
			break;
		case HOTPOST:
			addHotPostItemView();//采用for循环来添加数据
			break;
		default:
			break;
		}
	}
	private void addHotCompanyItemView() {
		View item1 = inflater.inflate(R.layout.exposure_wage_table_item_1, this);
		TextView companyName = (TextView) findViewById(R.id.exposure_wage_table_item_1_content);
		TextView postCount = (TextView) findViewById(R.id.exposure_wage_table_item_1_count);
		item1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	private void addHotPostItemView() {
		LinearLayout item2 = (LinearLayout) inflater.inflate(R.layout.exposure_wage_table_item_2, this);
		addHotPostItemView(item2);
	}
	private void addHotPostItemView(LinearLayout layout) {
		TextView postName = (TextView) inflater.inflate(R.layout.exposure_wage_table_item_2_item, layout);
		postName.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}

}

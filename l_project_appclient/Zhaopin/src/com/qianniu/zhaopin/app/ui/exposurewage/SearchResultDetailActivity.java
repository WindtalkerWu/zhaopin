package com.qianniu.zhaopin.app.ui.exposurewage;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.CommonTabsAdapter;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.ui.BaseFragmentActivity;
import com.qianniu.zhaopin.app.ui.CityChooseActivity;
import com.qianniu.zhaopin.app.ui.SettingActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class SearchResultDetailActivity extends BaseFragmentActivity {

	private ImageButton back;
	private TextView title;
	
	private FragmentManager fragmentManager;
	
	private int type;
	public static final int SEARCH_COMPANY = 1;
	public static final int SEARCH_POST = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exposure_search_result_detail);

		type = getIntent().getIntExtra("searchType", SEARCH_COMPANY);
		
		initView();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initView() {
		fragmentManager = getSupportFragmentManager();
		
		back = (ImageButton) findViewById(R.id.search_result_detail_goback);
		title = (TextView) findViewById(R.id.search_result_detail_title);
		
		
		setListener();
		
		initFragment();
	}
	private void initFragment() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (type == SEARCH_COMPANY) {
			transaction.replace(R.id.search_result_detail_content, new SearchResultCompanyDetailFragment());
		} else if (type == SEARCH_POST) {
			transaction.replace(R.id.search_result_detail_content, new SearchResultPostFragment());
		}
		transaction.addToBackStack(null);
		transaction.commit();
	}

	private void setListener() {
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
	}
	
}

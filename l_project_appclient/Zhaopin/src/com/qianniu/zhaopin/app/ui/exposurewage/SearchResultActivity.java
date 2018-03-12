package com.qianniu.zhaopin.app.ui.exposurewage;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.CommonTabsAdapter;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.ui.BaseFragmentActivity;
import com.qianniu.zhaopin.app.ui.CityChooseActivity;
import com.qianniu.zhaopin.app.ui.SettingActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class SearchResultActivity extends BaseFragmentActivity {

	private ImageButton back;
	private TextView title;
	private Button selectCity;
	
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private CommonTabsAdapter mTabsAdapter;
	
	
	private static final int CITYREQUEST = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exposure_search_result);

		initView();
	} 
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == HeadhunterPublic.RESULT_CHOOSECITY_OK) {
			if (requestCode == CITYREQUEST) {
				
			}
		}
	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.exposure_search_result_goback);
		title = (TextView) findViewById(R.id.exposure_search_result_title);
		selectCity = (Button) findViewById(R.id.exposure_search_result_select_city);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager) findViewById(R.id.exposure_search_result_pager);

		initTabs();
		
		setListener();
		
		
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
		selectCity.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(SearchResultActivity.this, CityChooseActivity.class);
				startActivityForResult(intent, CITYREQUEST);
			}
		});
	}
	private void initTabs() {
		mTabsAdapter = new CommonTabsAdapter(this, mTabHost, mViewPager, new int[]{R.id.exposure_wage_title, R.id.exposure_wage_count});

		mTabsAdapter.addTab(mTabHost.newTabSpec("company")
				.setIndicator(createTabView(getResources().getString(
								R.string.exposure_search_result_title_1), "")),
								SearchResultCompanyFragment.class, null);
		mTabsAdapter.addTab(mTabHost.newTabSpec("post")
				.setIndicator(createTabView(getResources().getString(
								R.string.exposure_search_result_title_2), "")),
								SearchResultPostFragment.class, null);
		
		mTabHost.setCurrentTab(0);
		mTabsAdapter.updateTab();
	}

	private View createTabView(String titleStr, String countStr) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.exposure_wage_tab_indicator, null);
		TextView title = (TextView) view.findViewById(R.id.exposure_wage_title);
		TextView count = (TextView) view.findViewById(R.id.exposure_wage_count);
		count.setVisibility(View.GONE);
		title.setText(titleStr);
		return view;
	}
}

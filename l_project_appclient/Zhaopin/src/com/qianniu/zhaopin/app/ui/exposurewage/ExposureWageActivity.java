package com.qianniu.zhaopin.app.ui.exposurewage;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.CommonTabsAdapter;
import com.qianniu.zhaopin.app.ui.BaseFragmentActivity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class ExposureWageActivity extends BaseFragmentActivity {

	private ImageButton back;
	private ImageView doSubmit;

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private CommonTabsAdapter mTabsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exposure_wage);

		initView();
	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.exposure_wage_goback);
		doSubmit = (ImageView) findViewById(R.id.exposure_wage_dosubmit);
		
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager) findViewById(R.id.exposure_wage_pager);

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
	}
	private void initTabs() {
		mTabsAdapter = new CommonTabsAdapter(this, mTabHost, mViewPager, new int[]{R.id.exposure_wage_title, R.id.exposure_wage_count});

		mTabsAdapter.addTab(mTabHost.newTabSpec("exposure")
				.setIndicator(createTabView(getResources().getString(
								R.string.exposure_wage_tab_title_1),
								getResources().getString(
								R.string.exposure_wage_tab_title_count_1))),
								ExposureWageFragment.class, null);
		mTabsAdapter.addTab(mTabHost.newTabSpec("upload")
				.setIndicator(createTabView(getResources().getString(
								R.string.exposure_wage_tab_title_2),
								getResources().getString(
								R.string.exposure_wage_tab_title_count_2))),
								ExposurePayrollFragment.class, null);
		mTabHost.setCurrentTab(0);
		mTabsAdapter.updateTab();
	}

	private View createTabView(String titleStr, String countStr) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.exposure_wage_tab_indicator, null);
		
		TextView title = (TextView) view.findViewById(R.id.exposure_wage_title);
		TextView count = (TextView) view.findViewById(R.id.exposure_wage_count);
		title.setText(titleStr);
		count.setText(countStr);
		return view;
	}
}

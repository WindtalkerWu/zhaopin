package com.qianniu.zhaopin.app.ui.exposurewage;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.CommonTabsAdapter;
import com.qianniu.zhaopin.app.ui.BaseFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

public class SearchResultCompanyDetailFragment extends BaseFragment {

	private TabHost mTabHost;
	private ViewPager mViewPager;
	private CommonTabsAdapter mTabsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search_result_company_detail, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ViewPager) view.findViewById(R.id.search_result_company_detail_pager);

		initTabs();
		
		setListener();
	}

	private void setListener() {
	}
	private void initTabs() {
		mTabsAdapter = new CommonTabsAdapter(getActivity(), mTabHost, mViewPager, new int[]{R.id.exposure_wage_title, R.id.exposure_wage_count});

		mTabsAdapter.addTab(mTabHost.newTabSpec("post")
				.setIndicator(createTabView(getResources().getString(
								R.string.exposure_search_result_detail_title_1), "")),
								SearchResultDetailCompanyPostFragment.class, null);
		mTabsAdapter.addTab(mTabHost.newTabSpec("comment")
				.setIndicator(createTabView(getResources().getString(
								R.string.exposure_search_result_detail_title_2), "")),
								SearchResultDetailCommentFragment.class, null);
		
		mTabHost.setCurrentTab(0);
		mTabsAdapter.updateTab();
	}

	private View createTabView(String titleStr, String countStr) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.exposure_wage_tab_indicator, null);
		TextView title = (TextView) view.findViewById(R.id.exposure_wage_title);
		TextView count = (TextView) view.findViewById(R.id.exposure_wage_count);
		title.setText(titleStr);
		count.setVisibility(View.GONE);
		return view;
	}
}

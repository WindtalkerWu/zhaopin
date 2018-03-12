package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.FastDoubleClick;
import com.qianniu.zhaopin.indicator.TitlePageIndicator;

public class SubscriptionActivity extends BaseFragmentActivity implements
		OnClickListener {
	public static final String SUBTYPE = "subtype";
	private static final String[] TITLE = new String[] { "号外", "名人", "名企" };
	private static final int[] TYPE = new int[] {
			SubscriptionManageFragment.TYPE_INFO,
			SubscriptionManageFragment.TYPE_BOSS,
			SubscriptionManageFragment.TYPE_COMPANY };

	private ViewPager pager;
	private FragmentPagerAdapter adapter;
	private ArrayList<Fragment> fragmentsList = new ArrayList<Fragment>();
	private int defaultType = SubscriptionManageFragment.TYPE_INFO;
	private String TAG = "SubscriptionActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent data = getIntent();
		if (data != null) {
			defaultType = data.getIntExtra(SUBTYPE,
					SubscriptionManageFragment.TYPE_INFO);
		}
		setContentView(R.layout.subsfragment_layout);
		ImageButton btn_back = (ImageButton) findViewById(R.id.goback_ibtn);
		btn_back.setOnClickListener(this);

		ImageView btn_save = (ImageView) findViewById(R.id.save_iv);
		btn_save.setOnClickListener(this);

		ImageButton btn_all = (ImageButton) findViewById(R.id.all_ibtn);
		btn_all.setOnClickListener(this);

		adapter = new MyAdapter(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				// Log.i(TAG, "onPageSelected position :"+position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub
				// Log.i(TAG,
				// "onPageScrolled position :"+position+",positionOffset:"+positionOffset+",positionOffsetPixels:"+positionOffsetPixels);
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				// Log.i(TAG, "onPageScrollStateChanged state :"+state);
			}
		});

		indicator.setCurrentItem(defaultType - 1);
		indicator.notifyDataSetChanged();
	}

	class MyAdapter extends FragmentPagerAdapter {
		private HashMap<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();
		private ArrayList<Fragment> fragmentsList;

		public MyAdapter(FragmentManager fm) {
			super(fm);
			// fragmentsList = list;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = fragmentMap.get(position);
			if (fragment == null) {
				fragment = SubscriptionManageFragment
						.newSubsInstance(TYPE[position]);
				fragmentMap.put(position, fragment);
			}
			return fragment;

		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLE[position];
		}

		@Override
		public int getCount() {
			return TITLE.length;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.goback_ibtn: {
			this.finish();
		}
			break;
		case R.id.save_iv: {
			if (!FastDoubleClick.isFastDoubleClick()) {
				SubscriptionManageFragment fragment = (SubscriptionManageFragment) adapter
						.getItem(pager.getCurrentItem());
				fragment.reportSubscriptionByThread();
			}
		}
			break;
		case R.id.all_ibtn: {
			int i = pager.getCurrentItem();
			SubscriptionManageFragment fragment = (SubscriptionManageFragment) adapter
					.getItem(pager.getCurrentItem());
			fragment.selectedAll();
		}
			break;
		}
	}
}

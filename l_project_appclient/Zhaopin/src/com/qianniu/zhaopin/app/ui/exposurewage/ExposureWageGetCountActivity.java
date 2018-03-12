package com.qianniu.zhaopin.app.ui.exposurewage;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.ScrollingTabsAdapter;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.ui.BaseFragmentActivity;
import com.qianniu.zhaopin.app.widget.ScrollableTabView;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class ExposureWageGetCountActivity extends BaseFragmentActivity {

	private ImageButton back;
	private TextView searchResult;
	private ImageView doSubmit;
	private ScrollableTabView scrollableTabs;
	private ViewPager viewPager;
	
	private ExposureWagePagerAdapter mPagerAdapter;
	private ScrollingTabsAdapter mScrollingTabsAdapter;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_exposure_wage_getcount);
		
		initView();
		
		setListener();

		initViewPager();
		initScrollableTab();
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.exposure_wage_getcount_goback);
		searchResult = (TextView) findViewById(R.id.exposure_wage_getcount_searchresult);
		doSubmit = (ImageView) findViewById(R.id.exposure_wage_getcount_dosubmit);
		scrollableTabs = (ScrollableTabView) findViewById(R.id.exposure_wage_getcount_scrollingTabs);
		viewPager = (ViewPager) findViewById(R.id.exposure_wage_getcount_viewPager);
		
		scrollableTabs.setActivity(this);
	}
	private void setListener() {
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		doSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	private void initViewPager() {
		mPagerAdapter = new ExposureWagePagerAdapter(getSupportFragmentManager());
		mPagerAdapter.addFragment(new ExposureWageShareFragment());
		mPagerAdapter.addFragment(new ExposureWageFragment());
		mPagerAdapter.addFragment(new ExposurePayrollFragment());
		mPagerAdapter.addFragment(new ExposureInviteFriendsFragment());
		viewPager.setOffscreenPageLimit(4);
		viewPager.setAdapter(mPagerAdapter);
		viewPager.setCurrentItem(0);
	}
	private void initScrollableTab() {
		scrollableTabs.setTabIds(new int[]{R.id.scrolling_tab_title, R.id.scrolling_tab_count});
		
		mScrollingTabsAdapter = new ScrollingTabsAdapter(this);
		scrollableTabs.setAdapter(mScrollingTabsAdapter);
		scrollableTabs.setViewPager(viewPager);
	}
	public void showDoSubmit(int i) {
		doSubmit.setVisibility(View.GONE);
		switch (i) {
		case 1:
			doSubmit.setVisibility(View.VISIBLE);
			doSubmit.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				}
			});
			break;
		case 2:
			doSubmit.setVisibility(View.VISIBLE);
			doSubmit.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				}
			});
			break;

		default:
			break;
		}
	}
	
	static class ExposureWagePagerAdapter extends FragmentPagerAdapter {
	    private final List<Fragment> fragments = new ArrayList<Fragment>();
	    
	    public ExposureWagePagerAdapter(FragmentManager manager) {
	        super(manager);
	    }

	    public void addFragment(Fragment fragment) {
	    	fragments.add(fragment);
	    	notifyDataSetChanged();
	    }
	    @Override
	    public int getCount() {
	        return fragments.size();
	    }


	    @Override
	    public Fragment getItem(int position) {
	    	MyLogger.i("getItem", "position==" + position);
			return fragments.get(position);
	    }
	    @Override  
	    public void destroyItem(ViewGroup container, int position, Object object) {
	        super.destroyItem(container, position, object);
	    }  
	    

	}
}

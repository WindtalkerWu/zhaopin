package com.qianniu.zhaopin.app.ui.integrationmall;

import java.util.ArrayList;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.CommonTabsAdapter;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.ui.BaseFragmentActivity;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

/**
 * 积分商城主入口
 * @author dm
 *
 */
public class IntegrationMallActivity extends BaseFragmentActivity {

	private TabHost mTabHost;
	private ViewPager mViewPager;
    private CommonTabsAdapter mTabsAdapter;
    
    private ImageButton back;
    private ImageView integrationManager;
    private ImageView search;
    private TextView adPosition; //放广告

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_integration_mall);
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager)findViewById(R.id.integration_mall_pager);

        mTabsAdapter = new CommonTabsAdapter(this, mTabHost, mViewPager);

        mTabsAdapter.addTab(mTabHost.newTabSpec("simple")
        		.setIndicator(createTabView(getResources().getString(R.string.integration_mall_use_integration))),
                IntegrationUseFragment.class, null);
        mTabsAdapter.addTab(mTabHost.newTabSpec("contacts")
        		.setIndicator(createTabView(getResources().getString(R.string.integration_mall_get_integration))),
        		IntegrationGetFragment.class, null);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        initView();
    }

    private void initView() {
    	back = (ImageButton) findViewById(R.id.integration_mall_goback);
    	integrationManager = (ImageView) findViewById(R.id.integration_mall_integrationmanager);
    	search = (ImageView) findViewById(R.id.integration_mall_title_search);
    	adPosition = (TextView) findViewById(R.id.integration_mall_tv);

    	back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
    	search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UIHelper.startIntegrationSearch(IntegrationMallActivity.this);
			}
		});
    	integrationManager.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				UIHelper.startIntegrationManager(IntegrationMallActivity.this);
			}
		});
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
    private View createTabView(String text) {
        View view = LayoutInflater.from(this).inflate(R.layout.common_tab_indicator, null);
        TextView tv = (TextView) view.findViewById(R.id.tv_tab);
        tv.setText(text);
        return view;
    }

    
}

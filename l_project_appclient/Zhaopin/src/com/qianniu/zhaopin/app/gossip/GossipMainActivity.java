package com.qianniu.zhaopin.app.gossip;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.RewardPagerAdapter;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.app.ui.BaseFragmentActivity;

public class GossipMainActivity extends BaseFragmentActivity {
	public static final int GOSSIP_TYPE_LATEST = 0;
	public static final int GOSSIP_TYPE_HOTEST = 1;
	/*****************************************************/
	private ImageView mBackIv;
	/*****************************************************/
	private FragmentManager m_fragmentManager;
	private TabHost m_tabHost;
	private ViewPager m_ViewPager;
	private GossipPagerAdapter mPagerAdapter;

	/*****************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gossipmain);
		m_fragmentManager = this.getSupportFragmentManager();
		m_tabHost = (TabHost) findViewById(R.id.tabhost);
		m_ViewPager = (ViewPager) findViewById(R.id.viewpager);
		initView();
		initViewPage();
	}

	private void initView() {
		mBackIv = (ImageView) findViewById(R.id.back);
		mBackIv.setOnClickListener(mClickListener);
	}

	private void initViewPage() {

		m_tabHost.setup();

		mPagerAdapter = new GossipPagerAdapter(this, m_fragmentManager,
				m_tabHost, m_ViewPager);

		mPagerAdapter.addTab("latest", "最新发布", GOSSIP_TYPE_LATEST);
		mPagerAdapter.addTab("hotset", "八卦穿越", GOSSIP_TYPE_HOTEST);

	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back: {
				finish();
			}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	static class GossipTabContent implements TabHost.TabContentFactory {
		private final Context mContext;

		public GossipTabContent(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	public class GossipPagerAdapter extends FragmentPagerAdapter implements
			ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {
		private String Tag = "GossipPagerAdapter";
		private Context m_Context;
		private FragmentManager m_fragmentManager;
		/*****************************************************/
		private ViewPager m_viewPager;
		private TabHost m_tabHost;
		private List<TabInfo> mtabList = new ArrayList<TabInfo>();
		/*****************************************************/
		private int m_nPosition; // 当前选中的
		private int m_nOldPosition; // 上次选中的

		public GossipPagerAdapter(Activity activity, FragmentManager fm,
				TabHost tabHost, ViewPager viewPage) {
			super(fm);
			// TODO Auto-generated constructor stub
			m_Context = activity;
			m_fragmentManager = fm;

			m_tabHost = tabHost;
			m_tabHost.setOnTabChangedListener(this);

			m_viewPager = viewPage;
			m_viewPager.setAdapter(this);
			m_viewPager.setOnPageChangeListener(this);
			initData();
		}

		private void initData() {
			m_nOldPosition = 0;
			m_nPosition = 0;
			mtabList.clear();
		}

		public void addTab(String tag, String title, int type) {
			TabHost.TabSpec tabspec = m_tabHost.newTabSpec(tag).setIndicator(
					createTabView(title));
			tabspec.setContent(new GossipTabContent(m_Context));
			TabInfo tabinfo = new TabInfo(tag, type);
			mtabList.add(tabinfo);
			m_tabHost.addTab(tabspec);
			notifyDataSetChanged();
		}

		private View createTabView(String strTitle) {
			View view = LayoutInflater.from(m_Context).inflate(
					R.layout.tab_gossip_msg, null);
			TextView tv = (TextView) view.findViewById(R.id.tv_tab);
			tv.setText(strTitle);
			return view;
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			TabInfo tabinfo = mtabList.get(position);
			FragmentGossipList fragment = FragmentGossipList
					.newInstance(tabinfo.getMtype());
			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mtabList.size();
		}

		@Override
		public void onTabChanged(String tabId) {
			// TODO Auto-generated method stub
			Log.i(Tag, "onTabChanged tag = " + tabId);
			m_nOldPosition = m_nPosition;
			m_nPosition = m_tabHost.getCurrentTab();
			int nowid = m_viewPager.getCurrentItem();
			if (nowid != m_nPosition)
				m_viewPager.setCurrentItem(m_nPosition);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub
			// Log.i(Tag, "onPageScrollStateChanged state = "+state);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// TODO Auto-generated method stub
			// Log.i(Tag, "onPageScrolled position = "+position);
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			Log.i(Tag, "onPageSelected position = " + position);
			m_tabHost.setCurrentTab(position);
		}

		class TabInfo {
			private String mtag;
			private int mtype;

			public TabInfo(String tag, int type) {
				mtag = tag;
				mtype = type;

			}

			public String getMtag() {
				return mtag;
			}

			public void setMtag(String mtag) {
				this.mtag = mtag;
			}

			public int getMtype() {
				return mtype;
			}

			public void setMtype(int mtype) {
				this.mtype = mtype;
			}

		}
	}
}

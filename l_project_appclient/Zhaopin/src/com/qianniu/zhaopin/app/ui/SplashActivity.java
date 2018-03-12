package com.qianniu.zhaopin.app.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.thp.UmShare;
import com.qianniu.zhaopin.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class SplashActivity extends BaseActivity implements OnPageChangeListener,OnTouchListener {
	
	private Context m_Context;
	
	private ViewPager splashViewPager;
	private List<View> splashViews;
	private SplashViewPagerAdapter splashViewPagerAdapter;
	private int[] imageResId = { R.drawable.splash_page1, R.drawable.splash_page2, R.drawable.splash_page3};
//	private int[] imageResId = { R.drawable.splash_page };
	
	private int lastX = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		m_Context = this;
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
		
		initView();
	}

	private void initView() {
		splashViewPager = (ViewPager) findViewById(R.id.splash_viewpager);
		
		initViewPager();
	}
	private void initViewPager() {
		LayoutInflater inflater = LayoutInflater.from(this);
		splashViews = new ArrayList<View>();
		int size = imageResId.length;
		for (int i = 0; i < size; i++) {
			View view = inflater.inflate(R.layout.splash_viewpager_item, null);
			view.setBackgroundDrawable(readBitMap(mContext, imageResId[i]));
//			view.setBackgroundResource(imageResId[i]);
			if (i == size - 1) {
				Button button = (Button) view.findViewById(R.id.splash_button);
				button.setVisibility(View.VISIBLE);
				button.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						enterMain();
					}
				});
			}
			
			splashViews.add(view);
		}
		splashViewPagerAdapter = new SplashViewPagerAdapter(splashViews, this);
		splashViewPager.setAdapter(splashViewPagerAdapter);
		splashViewPager.setOnPageChangeListener(this);
//		splashViewPager.setOnTouchListener(this);
	}
	public Drawable readBitMap(Context context, int resId){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		//获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return new BitmapDrawable(BitmapFactory.decodeStream(is,null,opt));
	}

	private void enterMain() {
		 Intent intent = new Intent(this, MainActivity.class);
	     startActivity(intent);
//	     overridePendingTransition(0, android.R.anim.slide_in_left); 
	     // 友盟统计
	     UmCountInstall();
		 finish();
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int)event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if((lastX - event.getX()) >100 && (splashViewPager.getCurrentItem() == splashViews.size() -1)){
				enterMain();
			}
			break;
		default:
			break;
		}
		return false;
	}
	private class SplashViewPagerAdapter extends PagerAdapter {
		 private List<View> views;
		 private Activity activity;

		 public SplashViewPagerAdapter(List<View> views, Activity activity) {
		        this.views = views;
		        this.activity = activity;
		    }
		@Override
		public int getCount() {
			return views.size();
		}
		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(views.get(position), 0); 
			return views.get(position);
		}
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return (arg0 == arg1);
		}
		@Override
		public void destroyItem(View container, int position, Object object) {
			 ((ViewPager) container).removeView(views.get(position));
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		// 友盟
		UmShare.UmPause(m_Context);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 友盟
		UmShare.UmResume(m_Context);
	}

	/**
	* 友盟统计---App安装
	*/
	private void UmCountInstall(){
		if(!DMSharedPreferencesUtil.getSharePreBoolean(m_Context,
			DMSharedPreferencesUtil.DM_HOTLABEL_DB,
			DMSharedPreferencesUtil.APP_INSTALL)){
			// 
			DMSharedPreferencesUtil.putSharePreBoolean(m_Context,
				 DMSharedPreferencesUtil.DM_HOTLABEL_DB,
				 DMSharedPreferencesUtil.APP_INSTALL,
				 true);
		
			// 友盟统计--App安装
			UmShare.UmStatistics(m_Context, "App_Install");
		}
		else{
			UmCountUpdate();
		}
	}
	
	/*
	 * 	友盟统计---版本更新
	*/
	private void UmCountUpdate(){
		String strVersion = ((AppContext) getApplicationContext())
				.getCurrentVersion().getVersion();
		
		String strOldVersion = DMSharedPreferencesUtil.getSharePreStr(m_Context,
				DMSharedPreferencesUtil.DM_HOTLABEL_DB,
				DMSharedPreferencesUtil.APP_VERSION);
		
		if(!strVersion.equals(strOldVersion)){
			DMSharedPreferencesUtil.putSharePre(m_Context,
				 DMSharedPreferencesUtil.DM_HOTLABEL_DB,
				 DMSharedPreferencesUtil.APP_VERSION,
				 strVersion);
			
			UmShare.UmStatistics(m_Context, "App_Update");
		}
	}
}

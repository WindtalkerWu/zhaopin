/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.qianniu.zhaopin.app.widget;

import com.qianniu.zhaopin.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PullToRefreshFooter extends RelativeLayout {
	public final static int STATE_NORMAL = 0; //正常状态，还有更多需要加载
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2; //正在加载中
	public final static int STATE_LOADFULL = 3;//数据全部加载完
	public final static int STATE_NULL = 4;//没有数据，初始状态
	private Context mContext;

	private View mContentView;
	private View mProgressBar;
	private TextView mHintView;
	
	private int state;
	
	public PullToRefreshFooter(Context context) {
		super(context);
		initView(context);
		setVisibility(View.GONE);
	}
	
	public PullToRefreshFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	
	public void setState(int state) {
		this.state = state;
//		mHintView.setVisibility(View.INVISIBLE);
//		mProgressBar.setVisibility(View.INVISIBLE);
//		mHintView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.GONE);
		mHintView.setVisibility(View.VISIBLE);
		setVisibility(View.VISIBLE);
		if (state == STATE_READY) {
			mHintView.setText(R.string.load_ready);
		} else if (state == STATE_LOADING) {
			mProgressBar.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.load_ing);
		} else if (state == STATE_LOADFULL){
			mHintView.setText(R.string.load_full);
		} else if (state == STATE_NULL) {
			setVisibility(View.GONE);
		} else {
			mHintView.setText(R.string.load_more);
		}
	}
	public int getState() {
		return state;
	}
	public void setBottomMargin(int height) {
		if (height < 0) return ;
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mContentView.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}
	
	public int getBottomMargin() {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mContentView.getLayoutParams();
		return lp.bottomMargin;
	}
	
	
	/**
	 * normal status
	 */
	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}
	
	
	/**
	 * loading status 
	 */
	public void loading() {
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mContentView.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}
	
	/**
	 * show footer
	 */
	public void show() {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mContentView.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}
	
	private void initView(Context context) {
		mContext = context;
		RelativeLayout moreView = (RelativeLayout)LayoutInflater.from(mContext).inflate(R.layout.listview_footer, null);
		addView(moreView);
		moreView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		mContentView = moreView.findViewById(R.id.listview_foot_content);
		mProgressBar = moreView.findViewById(R.id.listview_foot_progress);
		mHintView = (TextView)moreView.findViewById(R.id.listview_foot_more);
	}
	
	
}

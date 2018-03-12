package com.qianniu.zhaopin.app.widget;

import com.qianniu.zhaopin.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 下拉刷新控件
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class PullToRefreshListView extends ListView implements OnScrollListener {  
	   
    private final static String TAG = "PullToRefreshListView";  
    
	private static final int MAX_Y_OVERSCROLL_DISTANCE = 100;
	private Context mContext;
	private int mMaxYOverscrollDistance;
	
    // 下拉刷新标志   
    private final static int PULL_To_REFRESH = 0; 
    // 松开刷新标志   
    private final static int RELEASE_To_REFRESH = 1; 
    // 正在刷新标志   
    public final static int REFRESHING = 2;  
    // 刷新完成标志   
    private final static int DONE = 3;  
  
    private LayoutInflater inflater;  
  
    private LinearLayout headView;  
    private TextView tipsTextview;  
    private TextView lastUpdatedTextView;  
    private ImageView arrowImageView;  
    private ProgressBar progressBar;
    // 用来设置箭头图标动画效果   
    private RotateAnimation animation;  
    private RotateAnimation reverseAnimation;  
  
    // 用于保证startY的值在一个完整的touch事件中只被记录一次   
    private boolean isRecored;  
  
    private int headContentWidth;  
    private int headContentHeight;  
    private int headContentOriginalTopPadding;
  
    private int startY;  
    private int firstItemIndex;
    private int currentScrollState;
  
    private int state;  
  
    private boolean isBack;  
  
    public OnRefreshListener refreshListener;
    public OnLoadMoreListener loadMoreListener;
    
    // -- footer view
 	private PullToRefreshFooter mFooterView;
 	private boolean mEnablePullLoad;
 	private boolean mPullLoading = false;
 	private boolean mIsFooterReady = false;
 	
 	private float mFirstY = -1; // save event y
    
    private boolean bloading = false;
    public PullToRefreshListView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context);  
    }  
    
    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        init(context);  
    }  
  
    public int getState() {
		return state;
	}
    private void init(Context context) {   
    	mContext = context;
		final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		final float density = metrics.density;

		mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    	//设置滑动效果
        animation = new RotateAnimation(0, -180,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);  
        animation.setInterpolator(new LinearInterpolator());  
        animation.setDuration(200);  
        animation.setFillAfter(true);  
  
        reverseAnimation = new RotateAnimation(-180, 0,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);  
        reverseAnimation.setInterpolator(new LinearInterpolator());  
        reverseAnimation.setDuration(200);  
        reverseAnimation.setFillAfter(true);  
        
        inflater = LayoutInflater.from(context);  
        headView = (LinearLayout) inflater.inflate(R.layout.pull_to_refresh_head, null);  
  
        arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);  
//        arrowImageView.setMinimumWidth(50);
//        arrowImageView.setMinimumHeight(50);
        progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
        tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);  
        lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);  
        
        headContentOriginalTopPadding = headView.getPaddingTop();  
        
        measureView(headView);  
        headContentHeight = headView.getMeasuredHeight();  
        headContentWidth = headView.getMeasuredWidth(); 
        
        headView.setPadding(headView.getPaddingLeft(), -1 * headContentHeight, headView.getPaddingRight(), headView.getPaddingBottom());  
        headView.invalidate();
        
        addHeaderView(headView);  
        
     // init footer view
     	mFooterView = new PullToRefreshFooter(context);

        setOnScrollListener(this); 
    }
    @Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}

    //需要有加载更多功能时候的设置adapter的方式
    public void setLoadMoreAdapter(ListAdapter adapter) {
    	setAdapter(adapter);
    	addFooterView();
    }
    public void addFooterView() {
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			mFooterView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//不在下拉刷新状态，以及没有全部加载,且不在加载更多的状态下进行点击刷新
					if (state != REFRESHING && !mPullLoading) { //
						startLoadMore();
					}
				}
			});
			addFooterView(mFooterView);
		}
    }
    /**
	 * stop load more, reset footer view.
	 */
//	public void stopLoadMore() {
//		if (mPullLoading == true) {
//			mPullLoading = false;
//			mFooterView.setState(XListViewFooter.STATE_NORMAL);
//		}
//	}
	public void completeLoadMore(int status) {
		if (mPullLoading == true) {
			state = PULL_To_REFRESH;
			mPullLoading = false;
			mFooterView.setState(status);
		}
	}
	public void setFooterState(int status) {
		mFooterView.setState(status);
	}
	public int getFooterState() {
		return mFooterView.getState();
	}
	private void startLoadMore() {
		//设置了加载更多的监听器，以及加载更多状态不是数据加载完毕，和为空数据的时候，都可以进行上滑刷新
		if (loadMoreListener != null && mFooterView.getState() != PullToRefreshFooter.STATE_LOADFULL 
				&& mFooterView.getState() != PullToRefreshFooter.STATE_NULL) {
			mPullLoading = true;
			state = REFRESHING;
			mFooterView.setState(PullToRefreshFooter.STATE_LOADING);
			loadMoreListener.onLoadMore();
		}
		
	}

    public void settingOnScroll(AbsListView view, int firstVisiableItem, int visibleItemCount,  int totalItemCount) {
        firstItemIndex = firstVisiableItem;
//        if (firstVisiableItem + visibleItemCount >= totalItemCount){//currentScrollState == SCROLL_STATE_TOUCH_SCROLL
//        	if (state != REFRESHING
//        			&& ! mPullLoading) {
//    		startLoadMore();
//        	}
//    	}
    }
    public void settingOnScrollStateChanged(AbsListView view, int scrollState) {
    	currentScrollState = scrollState;
    	if(scrollState == SCROLL_STATE_IDLE && getCount() > 0){
            //判断滚动到底部   
            if(view.getLastVisiblePosition() == (view.getCount()-1)){  
            	if (state != REFRESHING && ! mPullLoading) {
            		startLoadMore();
            	}
            }
    	}
    }
    @Override
    public void onScroll(AbsListView view, int firstVisiableItem, int visibleItemCount,  int totalItemCount) {
    	settingOnScroll(view, firstVisiableItem, visibleItemCount, totalItemCount);
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    	settingOnScrollStateChanged(view, scrollState);
    } 
    
    public final boolean getLoadingState() {
		return bloading;
	}

	public final void setLoadingState(boolean bloading) {
		//Log.i(TAG, "setLoadingState bloading =" + bloading);
		this.bloading = bloading;
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (mFirstY == -1) {
			mFirstY = event.getRawY();
		}
        switch (event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
            if (firstItemIndex == 0 && !isRecored) {  
                startY = (int) event.getY();  
                isRecored = true;
            }  
            break;  
        
        case MotionEvent.ACTION_CANCEL://失去焦点&取消动作
        case MotionEvent.ACTION_UP:  
  
            if (state != REFRESHING) {  
                if (state == DONE) {  //当前-抬起-ACTION_UP：DONE什么都不做
                }  
                else if (state == PULL_To_REFRESH) {  //当前-抬起-ACTION_UP：PULL_To_REFRESH-->DONE-由下拉刷新状态到刷新完成状态
                    state = DONE;  
                    changeHeaderViewByState();
                }  
                else if (state == RELEASE_To_REFRESH) { //当前-抬起-ACTION_UP：RELEASE_To_REFRESH-->REFRESHING-由松开刷新状态，到刷新完成状态  
                    state = REFRESHING;  
                    changeHeaderViewByState();
                    onRefresh();
                }
                final float deltaY = event.getRawY() - mFirstY;
            	if ((deltaY < 0) && !mPullLoading && getLastVisiblePosition() == getCount() - 1) {
            		startLoadMore();
            	}
            	mFirstY = -1; // reset
            }  
  
            isRecored = false;
            isBack = false;
  
            break;  
  
        case MotionEvent.ACTION_MOVE:  
            int tempY = (int) event.getY();
            if (!isRecored && firstItemIndex == 0) {
                isRecored = true;
                startY = tempY;
            }
            //如果正在加载，则不能进行刷新操作 
            if(bloading){
            	break;
            }
            Log.i(TAG,"tempY =" + tempY +";startY = "+startY);
            if (state != REFRESHING && isRecored) {  
                // 可以松开刷新了   
                if (state == RELEASE_To_REFRESH) {  
                    // 往上推，推到屏幕足够掩盖head的程度，但还没有全部掩盖   
                    if ((tempY - startY < headContentHeight + 20)  
                            && (tempY - startY) > 0) {  
                        state = PULL_To_REFRESH;  
                        changeHeaderViewByState();
                    } else if (tempY - startY <= 0) {   // 一下子推到顶   
                        state = DONE;  
                        changeHeaderViewByState();
                    } else {  // 往下拉，或者还没有上推到屏幕顶部掩盖head  
                        // 不用进行特别的操作，只用更新paddingTop的值就行了  
                    }  
                } else if (state == PULL_To_REFRESH) { // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态     
                    // 下拉到可以进入RELEASE_TO_REFRESH的状态  
                    if (tempY - startY >= headContentHeight+20) {  
                        state = RELEASE_To_REFRESH;  
                        isBack = true;  
                        changeHeaderViewByState();
                    }  
                    // 上推到顶了   
                    else if (tempY - startY <= 0) {  
                        state = DONE;  
                        changeHeaderViewByState();
                    }  
                } else if (state == DONE) { // done状态下   
                    if (tempY - startY > 0) {  
                        state = PULL_To_REFRESH;  
                        changeHeaderViewByState();
                    }  
                }  
                
                // 更新headView的size   
                if (state == PULL_To_REFRESH) { 
                	int topPadding = (int)((-1 * headContentHeight + (tempY - startY)));

                	headView.setPadding(headView.getPaddingLeft(), topPadding, headView.getPaddingRight(), headView.getPaddingBottom());   
                    headView.invalidate();
                }  
  
                // 更新headView的paddingTop   
                if (state == RELEASE_To_REFRESH) {  
                	int topPadding = (int)((tempY - startY - headContentHeight));
                	if(topPadding > mMaxYOverscrollDistance)
                		topPadding = mMaxYOverscrollDistance;

                	headView.setPadding(headView.getPaddingLeft(), topPadding, headView.getPaddingRight(), headView.getPaddingBottom());    
                    headView.invalidate();
                }  
            }  
            break;
            default:
			break;
        }

		if (state == RELEASE_To_REFRESH) {
			return true;
		}
        return super.onTouchEvent(event);  
    }  

    // 当状态改变时候，调用该方法，以更新界面   
    private void changeHeaderViewByState() {  
        switch (state) {  
        case RELEASE_To_REFRESH:  
        	
            arrowImageView.setVisibility(View.VISIBLE);  
            progressBar.setVisibility(View.GONE);  
            tipsTextview.setVisibility(View.VISIBLE);  
            lastUpdatedTextView.setVisibility(View.VISIBLE);  
  
            arrowImageView.clearAnimation();  
            arrowImageView.startAnimation(animation);  
  
            tipsTextview.setText(R.string.pull_to_refresh_release_label);
            break;  
        case PULL_To_REFRESH:
        	
        	progressBar.setVisibility(View.GONE);  
            tipsTextview.setVisibility(View.VISIBLE);  
            lastUpdatedTextView.setVisibility(View.VISIBLE);  
            arrowImageView.clearAnimation();  
            arrowImageView.setVisibility(View.VISIBLE);  
            if (isBack) {  
                isBack = false;  
                arrowImageView.clearAnimation();  
                arrowImageView.startAnimation(reverseAnimation);  
            } 
            tipsTextview.setText(R.string.pull_to_refresh_pull_label);
            break;  
  
        case REFRESHING:
        	headView.setPadding(headView.getPaddingLeft(), headContentOriginalTopPadding, headView.getPaddingRight(), headView.getPaddingBottom());   
            headView.invalidate();  
  
            progressBar.setVisibility(View.VISIBLE);  
            arrowImageView.clearAnimation();  
            arrowImageView.setVisibility(View.GONE);  
            tipsTextview.setText(R.string.pull_to_refresh_refreshing_label);  
            lastUpdatedTextView.setVisibility(View.GONE);
            break;  
        case DONE:
        	headView.setPadding(headView.getPaddingLeft(), -1 * headContentHeight, headView.getPaddingRight(), headView.getPaddingBottom());  

        	progressBar.setVisibility(View.GONE);  
            arrowImageView.clearAnimation();  
            // 此处更换图标   
            arrowImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);  
  
            tipsTextview.setText(R.string.pull_to_refresh_pull_label);  
            lastUpdatedTextView.setVisibility(View.VISIBLE);
            headView.invalidate();  
            break;  
        }  
    }  
  
    //点击刷新
    public void clickRefresh() {
    	setSelection(0);
    	state = REFRESHING;  
        changeHeaderViewByState();  
        onRefresh(); 
    }
    
    public void setOnRefreshListener(OnRefreshListener refreshListener) {  
        this.refreshListener = refreshListener; 
    }
    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {  
        this.loadMoreListener = loadMoreListener; 
    } 
  
    public interface OnRefreshListener {  
        public void onRefresh();
        
    }
    public interface OnLoadMoreListener {
        public void onLoadMore();
    }
  
    public void onRefreshComplete(String update) {  
        lastUpdatedTextView.setText(update);  
        onRefreshComplete();
    } 
    
    public void firstRefreshing() {
        state = REFRESHING;
        headView.setPadding(0, 10, 0, 0);
        progressBar.setVisibility(View.VISIBLE);
        arrowImageView.clearAnimation();
        arrowImageView.setVisibility(View.GONE);
        tipsTextview.setText(R.string.pull_to_refresh_refreshing_label);
        lastUpdatedTextView.setVisibility(View.GONE);
    }
    /**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		state = PULL_To_REFRESH;
		changeHeaderViewByState();
	}
    public void onRefreshComplete() {  
        state = DONE;  
        changeHeaderViewByState();
		setSelection(0);
    }
    public void onRefreshComplete(int loadMoreStatus) {  
    	onRefreshComplete();
		mFooterView.setState(loadMoreStatus);
    }  
  
    private void onRefresh() {  
        if (refreshListener != null) {  
            refreshListener.onRefresh();  
        }  
    }  
  
    // 计算headView的width及height值  
    private void measureView(View child) {  
        ViewGroup.LayoutParams p = child.getLayoutParams();  
        if (p == null) {  
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,  
                    ViewGroup.LayoutParams.WRAP_CONTENT);  
        }  
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);  
        int lpHeight = p.height;  
        int childHeightSpec;  
        if (lpHeight > 0) {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,  
                    MeasureSpec.EXACTLY);  
        } else {  
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,  
                    MeasureSpec.UNSPECIFIED);  
        }  
        child.measure(childWidthSpec, childHeightSpec);  
    }  
/*	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
			int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		// This is where the magic happens, we have replaced the incoming
		// maxOverScrollY with our own custom variable mMaxYOverscrollDistance;

		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
				mMaxYOverscrollDistance, isTouchEvent);
	}  
    */
}
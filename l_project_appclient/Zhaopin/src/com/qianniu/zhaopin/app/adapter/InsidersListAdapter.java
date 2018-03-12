package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.app.ui.InsidersListActivity;
import com.qianniu.zhaopin.app.ui.SeekWorthyActivity;
import com.qianniu.zhaopin.app.view.InsidersAndCompanyItem;
import com.qianniu.zhaopin.app.view.InsidersAndCompanyListItem;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class InsidersListAdapter extends BaseAdapter{
	private static final String TAG = "InsidersAndCompanyAdapter";
	private Context mContext;
	private BaseActivity activity;
	private List<InsidersAndCompany> insidersAndCompanys;
	private int type;

	private int selectIndex = -1;
	
	public final static int maxOneLine = 2;
	public final static int onePageCount = 24; //每一页的item数
	public final static int totalCount = onePageCount * 13; //加载的最大项
	
	private BitmapManager bmpManager;
	private boolean isLoading = false;
	
	private int headWidth = -1;
	private boolean isHeightFresh = false;

	public InsidersListAdapter() {
	}
	public InsidersListAdapter(BaseActivity activity, List<InsidersAndCompany> insidersAndCompany) {
		this.activity = activity;
		this.mContext = activity.getApplicationContext();
		this.insidersAndCompanys = insidersAndCompany;
		this.bmpManager = new BitmapManager(null);
		initHeadWidth();
	}
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
	public int getSelectIndex() {
		return selectIndex;
	}
	public void setIndustryInsiderPeoples(
			List<InsidersAndCompany> industryInsiderPeoples) {
		this.insidersAndCompanys = industryInsiderPeoples;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	public int getCount() {
		int count = insidersAndCompanys.size() / maxOneLine;
		int surplus = insidersAndCompanys.size() % maxOneLine;
		if (surplus > 0) {
			count = count + 1;
		}
		return  count;
	}

	@Override
	public Object getItem(int position) {
		return insidersAndCompanys.get(position);
	}

	@Override
	public long getItemId(int position) {
		int count = position / maxOneLine;
		int surplus = position % maxOneLine;
		if (surplus > 0) {
			count = count + 1;
		}
		return count;
	}

	private void initHeadWidth() {
		DisplayMetrics displayMetrics = (((AppContext) mContext).getPhoneDisplayMetrics((AppContext) mContext));
		int screenWidth = displayMetrics.widthPixels;
		headWidth = (screenWidth - CommonUtils.dip2px(mContext, InsidersAndCompanyListItem.MARGIN) 
				* ( 2 + InsidersListAdapter.maxOneLine - 1)) / InsidersListAdapter.maxOneLine - 1;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		InsidersAndCompanyViewHolder holder = null;
		if (convertView == null) {
			MyLogger.i(TAG, "convertView == null");
			InsidersAndCompanyListItem listItemView = new InsidersAndCompanyListItem(mContext);
			convertView = listItemView;
			holder = new InsidersAndCompanyViewHolder(listItemView);
			convertView.setTag(holder);
			
			int start = position * maxOneLine;
			for (int i = 0; i < maxOneLine; i++) {
				final int current = start + i;
				if (current < insidersAndCompanys.size()) {
					final InsidersAndCompany insidersAndCompany = insidersAndCompanys.get(current);
					insidersAndCompany.setType(type);
					InsidersAndCompanyItem industryInsidersItem =
							new InsidersAndCompanyItem(mContext, bmpManager, insidersAndCompany, headWidth);
					holder.insidersAndCompanyListItem.setView(industryInsidersItem);
					industryInsidersItem.setListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(activity, SeekWorthyActivity.class);
							intent.putExtra("ic", insidersAndCompany);
							activity.startActivity(intent);
							selectIndex = current;
							System.out.println(selectIndex);
						}
					});
//					if (headWidth == -1) {
//						final InsidersAndCompanyItem item = industryInsidersItem;
//						ViewTreeObserver vto = item.getViewTreeObserver();
//						vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//							@Override
//							public boolean onPreDraw() {
//								headWidth = item.getMeasuredWidth();
//								item.setImageWidth(headWidth);
//								return true;
//							}
//						});
//					} else {
//						industryInsidersItem.setImageWidth(headWidth);
//						if (!isHeightFresh) {
//							notifyDataSetChanged();
//							isHeightFresh = true;
//						}
//					}
				} else {
					InsidersAndCompanyItem industryInsidersItem = 
							new InsidersAndCompanyItem(mContext, bmpManager, null , headWidth);
					holder.insidersAndCompanyListItem.setView(industryInsidersItem);
					industryInsidersItem.setListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
						}
					});
				}
			}
		} else { //不为null时候 刷新数据
			MyLogger.i(TAG, "convertView != null");
			holder = (InsidersAndCompanyViewHolder) convertView.getTag();
			
			int start = position * maxOneLine;
			for (int i = 0; i < maxOneLine; i++) {
				final int current = start + i;
				InsidersAndCompanyItem industryInsidersItem = holder.getItem(i);
//				industryInsidersItem.clearHeadImage();
				if (current < insidersAndCompanys.size()) {
					final InsidersAndCompany insidersAndCompany = insidersAndCompanys.get(current);
					insidersAndCompany.setType(type);
					industryInsidersItem.setIndustryInsiderPeople(insidersAndCompany, isLoading);
					industryInsidersItem.setListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(activity, SeekWorthyActivity.class);
							intent.putExtra("ic", insidersAndCompany);
							activity.startActivity(intent);
							selectIndex = current;
						}
					});
				} else {
					industryInsidersItem.setIndustryInsiderPeople(null, false);
				}
			}
		}
		
		return convertView;
	}
	public void loadImage(int postion, View view) {
//		if (view == null) {
//			return;
//		}
//		MyLogger.i(TAG, "###loadImage###");
//		InsidersAndCompanyListItem listItem = (InsidersAndCompanyListItem)view;
//		for (int i = 0; i < maxOneLine; i++) {
//			int index = postion * maxOneLine + i;
//			View childView = listItem.getChildAt(i);
//			ImageView imageView = (ImageView) childView.findViewById(R.id.industry_insiders_item_head_portrait);
//			loadImage(index, imageView);
//		}
		if (view instanceof InsidersAndCompanyListItem) {
			MyLogger.i(TAG, "###loadImage###" + postion);
			InsidersAndCompanyListItem listItem = (InsidersAndCompanyListItem)view;
			for (int i = 0; i < maxOneLine; i++) {
				int index = (postion - 1) * maxOneLine + i;
				if (index < insidersAndCompanys.size()) {
					View childView = listItem.getChildAt(i);
					ImageView imageView = (ImageView) childView.findViewById(R.id.insiders_item_head_portrait);
					loadImage(index, imageView);
				}
			}
		}
	}

	//加载图片
	public void loadImage(int index, ImageView imageView) {
//		MyLogger.i(TAG, "###imageView##" + imageView);
		bmpManager.loadBitmap(insidersAndCompanys.get(index).getPicture(), imageView); //, null, (int)headWidth, (int)headWidth
//			bmpManager.loadBitmap("http://222.66.4.242:90/l_project_web/public//img/tmpupload/2013/12/30/52c0d6939f15f.small.jpg", imageView);
	}

	public static class InsidersAndCompanyViewHolder{
		InsidersAndCompanyListItem insidersAndCompanyListItem;
		
		public InsidersAndCompanyViewHolder(InsidersAndCompanyListItem view){
			insidersAndCompanyListItem = view;
		}
		public InsidersAndCompanyItem getItem(int index) {
			return (InsidersAndCompanyItem)insidersAndCompanyListItem.getChildAt(index);
		}
		
	}

}

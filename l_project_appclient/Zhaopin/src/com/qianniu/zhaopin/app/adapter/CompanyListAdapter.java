package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.CommonUtils;
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

public class CompanyListAdapter extends BaseAdapter{
	private static final String TAG = "CompanyListAdapter";
	private Context mContext;
	private BaseActivity activity;
	private List<InsidersAndCompany> insidersAndCompanys;
	private int type;
	
	private int selectIndex;

	public final static int onePageCount = 24; //每一页的item数
	public final static int totalCount = onePageCount * 13; //加载的最大项

	private BitmapManager bmpManager;
	
	public static int logoWidth = -1;
	private boolean isHeightFresh = false;
	
	private static final float SCALE =  2.25f;

	public CompanyListAdapter() {
	}
	public CompanyListAdapter(BaseActivity activity, List<InsidersAndCompany> insidersAndCompany) {
		this.activity = activity;
		this.mContext = activity.getApplicationContext();
		this.insidersAndCompanys = insidersAndCompany;
		this.bmpManager = new BitmapManager(null);
		initHeadWidth();
	}
	public void setIndustryInsiderPeoples(
			List<InsidersAndCompany> industryInsiderPeoples) {
		this.insidersAndCompanys = industryInsiderPeoples;
	}
	public int getSelectIndex() {
		return selectIndex;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	public int getCount() {
		return insidersAndCompanys != null ? insidersAndCompanys.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return insidersAndCompanys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	private void initHeadWidth() {
		DisplayMetrics displayMetrics = (((AppContext) mContext).getPhoneDisplayMetrics((AppContext) mContext));
		int screenWidth = displayMetrics.widthPixels;
		logoWidth = (screenWidth - CommonUtils.dip2px(mContext, InsidersAndCompanyListItem.MARGIN) 
				* ( 2 + InsidersListAdapter.maxOneLine - 1)) / InsidersListAdapter.maxOneLine - 1;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CompanyItemViewHolder holder = null;
		InsidersAndCompanyItem companyListItem = null;
		final InsidersAndCompany insidersAndCompany = insidersAndCompanys.get(position);
		insidersAndCompany.setType(type);
		if (convertView == null) {
			companyListItem = new InsidersAndCompanyItem(mContext, bmpManager, insidersAndCompany, logoWidth);
			convertView = companyListItem;
			holder = new CompanyItemViewHolder(companyListItem);
			convertView.setTag(holder);
//			if (logoWidth == -1) {
//				final InsidersAndCompanyItem item = companyListItem;
//				ViewTreeObserver vto = item.getViewTreeObserver();
//				vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//					@Override
//					public boolean onPreDraw() {
//						logoWidth = item.getMeasuredWidth();
//						item.setImageWidth(logoWidth);
//						return true;
//					}
//				});
//			} else {
//				companyListItem.setImageWidth(logoWidth);
//				if (!isHeightFresh) {
//					notifyDataSetChanged();
//					isHeightFresh = true;
//				}
//			}
		} else {
			holder = (CompanyItemViewHolder) convertView.getTag();
			companyListItem = holder.insidersAndCompanyListItem;
		}

		companyListItem.setIndustryInsiderPeople(insidersAndCompany, true);
		final int select = position;
		companyListItem.setListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectIndex = select;
				Intent intent = new Intent(activity, SeekWorthyActivity.class);
				intent.putExtra("ic", insidersAndCompany);
				activity.startActivity(intent);
			}
		});
		
		return convertView;
	}
	public void loadImage(int postion, View view) {
	}

	//加载图片
	public void loadImage(int index, ImageView imageView) {
//		MyLogger.i(TAG, "###imageView##" + imageView);
		bmpManager.loadBitmap(insidersAndCompanys.get(index).getPicture(), imageView); //, null, (int)logoWidth, (int)(logoWidth / SCALE)
//			bmpManager.loadBitmap("http://222.66.4.242:90/l_project_web/public//img/tmpupload/2013/12/30/52c0d6939f15f.small.jpg", imageView);
	}

	public static class CompanyItemViewHolder{
		InsidersAndCompanyItem insidersAndCompanyListItem;
		
		public CompanyItemViewHolder(InsidersAndCompanyItem view){
			insidersAndCompanyListItem = view;
		}
	}

}

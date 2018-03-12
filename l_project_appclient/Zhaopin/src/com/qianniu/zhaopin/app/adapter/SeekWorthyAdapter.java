package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.bean.CompanyInfo;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.util.QNUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SeekWorthyAdapter extends BaseAdapter {

	private static final String TAG = "SeekWorthyAdapter";
	private Context mContext;
	private Activity activity;
	private List<CompanyInfo> companys;
	private BitmapManager bmpManager;
	private LayoutInflater mLayoutInflater;
	
	public SeekWorthyAdapter(Activity activity, List<CompanyInfo> companys) {
		this.mContext = activity.getApplicationContext();
		this.activity = activity;
		this.companys = companys;
		this.bmpManager = new BitmapManager(null);
		mLayoutInflater = LayoutInflater.from(mContext);
	}
	
	public void setCompanys(List<CompanyInfo> companys) {
		this.companys = companys;
	}
	
	@Override
	public int getCount() {
		return companys.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SeekWorthyViewHolder holder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.seek_worthy_item, null);
			holder = new SeekWorthyViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (SeekWorthyViewHolder) convertView.getTag();
		}
		CompanyInfo companyInfo = companys.get(position);
//		holder.loadImage(companyInfo.getLogo());
		
		holder.companyName.setText(companyInfo.getTitle());

		String titleStr = mContext.getResources().getString(R.string.seek_worthy_title);
		String titleResult = String.format(titleStr, companyInfo.getTasks_num() + "");
		SpannableStringBuilder result = QNUtil.formateStringColor(titleResult, 2, titleResult.length() - 2, Color.RED);
		holder.recruitment.setText(result);
		
		String addressStr = mContext.getResources().getString(R.string.seek_worthy_address);
		String addressResult = String.format(addressStr, companyInfo.getAddress());
		holder.address.setText(addressResult);
		
//		String postStr = mContext.getResources().getString(R.string.seek_worthy_post);
//		String postResult = String.format(postStr, companyInfo.getId());
//		holder.post.setText(postResult);
		
		holder.time.setText(companyInfo.getModified());
		return convertView;
	}
	public class SeekWorthyViewHolder{
		private ImageView image;
		private TextView companyName;
		private TextView recruitment;
		private TextView address;
//		private TextView post;
		private TextView time;
		public SeekWorthyViewHolder(View view){
//			image = (ImageView) view.findViewById(R.id.seekworthy_item_image);
			companyName = (TextView) view.findViewById(R.id.seekworthy_item_company_name);
			recruitment = (TextView) view.findViewById(R.id.seekworthy_item_recruitment);
			address = (TextView) view.findViewById(R.id.seekworthy_item_address);
//			post = (TextView) view.findViewById(R.id.seekworthy_item_post);
			time = (TextView) view.findViewById(R.id.seekworthy_item_time);
		}
//		private void loadImage(String url) {
//			MyLogger.i(TAG, "url##" + url);
//			bmpManager.loadMiddleRoundBitmap(activity, url, image);
//		}
		
	}
}

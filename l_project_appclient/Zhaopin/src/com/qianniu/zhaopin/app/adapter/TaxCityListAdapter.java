package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.adapter.CityChooseListAdater.CityChooseViewHolder;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaxCityListAdapter extends BaseAdapter{

	private Context mContext;
    private LayoutInflater mInfater = null;
    private String[] cities;
    
    private int selectIndex;
    
    public TaxCityListAdapter(Context context, String[] cities){
    	this.mContext = context;
    	this.mInfater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	this.cities = cities;
    	this.selectIndex = -1;
    }

    public int getSelectIndex() {
		return selectIndex;
	}
    public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}
	@Override
	public int getCount() {
		return cities.length;
	}

	@Override
	public Object getItem(int position) {
		return cities[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TaxCityViewHolder viewHolder = null;
		if(convertView == null){
			convertView = mInfater.inflate(R.layout.list_item_onelevelchoose, null);
			viewHolder = new TaxCityViewHolder(convertView);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (TaxCityViewHolder)convertView.getTag();
		}
		
		String contentStr = (String) getItem(position);
		viewHolder.content.setText(contentStr);
		if (selectIndex == position ) {
			viewHolder.selectFlag.setVisibility(View.VISIBLE);
		} else {
			viewHolder.selectFlag.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

	static class TaxCityViewHolder{
		TextView content;				//	推荐理由
		ImageView selectFlag;
		
		public TaxCityViewHolder(View view){

			this.content = (TextView)view.findViewById(R.id.list_item_recommendreason_text);
			this.selectFlag = (ImageView) view.findViewById(R.id.list_item_recommendreason_image);
		}
	}
}

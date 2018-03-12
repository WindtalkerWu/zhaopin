package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class IntegrationGoodsListAdapter extends BaseAdapter {
	
	private Context context;
	private int type;
	private List data;

	public IntegrationGoodsListAdapter(Context context , List data, int type) {
		this.context = context;
		this.data = data;
		this.type = type;
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}

package com.qianniu.zhaopin.app.ui.exposurewage;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.bean.CommentInfo;
import com.qianniu.zhaopin.app.common.MyLogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchResultCompanyFragment extends ListFragment {
	private LayoutInflater mInflater;
	
	private ResultCompanyAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mInflater = inflater;
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLogger.i(getTag(), "SearchResultCompanyFragment##onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		setEmptyText("没有搜索到结果");
		
//		adapter = new ResultCompanyAdapter();
//		setListAdapter(adapter);
		setListShown(true);
		//test
		getView().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SearchResultDetailActivity.class);
				intent.putExtra("searchType", SearchResultDetailActivity.SEARCH_COMPANY);
				startActivity(intent);
			}
		});
	}
	
	class ResultCompanyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ResultCompanyViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.exposure_wage_table_item_1, null);
				holder = new ResultCompanyViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ResultCompanyViewHolder)convertView.getTag();
			}
			
			return convertView;
		}
		
	}
	class ResultCompanyViewHolder {
		private TextView content;
		private TextView count;
		public ResultCompanyViewHolder(View view) {
			content = (TextView) view.findViewById(R.id.exposure_wage_table_item_1_content);
			count = (TextView) view.findViewById(R.id.exposure_wage_table_item_1_count);
		}
	}

}

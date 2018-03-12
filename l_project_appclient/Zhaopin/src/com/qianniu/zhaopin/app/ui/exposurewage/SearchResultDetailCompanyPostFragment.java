package com.qianniu.zhaopin.app.ui.exposurewage;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.MyLogger;

public class SearchResultDetailCompanyPostFragment extends ListFragment {
	
	private LayoutInflater mInflater;
	
	private ResultPostAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mInflater = inflater;
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MyLogger.i(getTag(), "SearchResultCompanyPostFragment##onActivityCreated");
		setEmptyText("没有搜索到结果");
//		adapter = new ResultPostAdapter();
//		setListAdapter(adapter);
		setListShown(true);
		
	}

	class ResultPostAdapter extends BaseAdapter {

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
			ResultPostViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.exposure_search_result_post_item, null);
				holder = new ResultPostViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ResultPostViewHolder)convertView.getTag();
			}
			
			return convertView;
		}
		
	}
	class ResultPostViewHolder {
		private TextView postName;
		private TextView salary;
		private TextView exposureCount;
		public ResultPostViewHolder(View view) {
			postName = (TextView) view.findViewById(R.id.exposure_search_result_item_content);
			salary = (TextView) view.findViewById(R.id.exposure_search_result_item_salary);
			exposureCount = (TextView) view.findViewById(R.id.exposure_search_result_item_company);
		}
	}

}

package com.qianniu.zhaopin.app.ui.exposurewage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.ui.BaseFragment;

public class SearchResultDetailCommentFragment extends BaseFragment {

	private FragmentManager fragmentManager;

	private Button doComment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.fragment_exposure_search_result_comment, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		fragmentManager = getActivity().getSupportFragmentManager();

		doComment = (Button) view
				.findViewById(R.id.search_result_comment_comment);

		initFragment();
		setListener();
	}

	private void initFragment() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.search_result_comment_content,
				new SearchResultDetailCommentList());
		transaction.addToBackStack(null);
		transaction.commit();
	}

	private void setListener() {
		doComment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						ExposureCommentActivity.class);
				startActivity(intent);
			}
		});
	}
}

class SearchResultDetailCommentList extends ListFragment {
	private LayoutInflater mInflater;

	private ResultCommentAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mInflater = inflater;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MyLogger.i(getTag(),
				"SearchResultDetailCommentFragment##onActivityCreated");
		setEmptyText("没有搜索到结果");

		// adapter = new ResultPostAdapter();
		// setListAdapter(adapter);
		setListShown(true);

	}

	class ResultCommentAdapter extends BaseAdapter {

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
			ResultCommentViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.exposure_search_result_comment_item, null);
				holder = new ResultCommentViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ResultCommentViewHolder) convertView.getTag();
			}

			return convertView;
		}

	}

	class ResultCommentViewHolder {
		private TextView content;
		private TextView username;
		private TextView time;

		public ResultCommentViewHolder(View view) {
			content = (TextView) view
					.findViewById(R.id.exposure_search_result_comment_item_content);
			username = (TextView) view
					.findViewById(R.id.exposure_search_result_comment_item_username);
			time = (TextView) view
					.findViewById(R.id.exposure_search_result_comment_item_time);
		}
	}
}

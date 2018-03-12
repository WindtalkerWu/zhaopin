package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.HistroyListAdapter.HistoryViewHolder;
import com.qianniu.zhaopin.app.bean.CommentInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {
	private Context mContext;
	private List<CommentInfo> commentInfos;
	private LayoutInflater mInflater;
	
	public final static int onePageCount = 10; //每一页的item数

	public CommentAdapter(Context mContext, List<CommentInfo> commentInfos) {
		this.mContext = mContext;
		this.commentInfos = commentInfos;
		this.mInflater = LayoutInflater.from(mContext);
	}
	public void setCommentInfos(List<CommentInfo> commentInfos) {
		this.commentInfos = commentInfos;
	}
	@Override
	public int getCount() {
		return commentInfos.size();
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
		CommentViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.comment_list_item, null);
			holder = new CommentViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (CommentViewHolder)convertView.getTag();
		}
		CommentInfo commentInfo = commentInfos.get(position);
		holder.content.setText(commentInfo.getContent());
		holder.time.setText(commentInfo.getModified());
		String userStr = mContext.getResources().getString(R.string.comment_list_item_user);
		holder.user.setText(String.format(userStr, commentInfo.getUser()));
		return convertView;
	}
	
	private class CommentViewHolder {
		private TextView content;
		private TextView time;
		private TextView user;
		public CommentViewHolder(View view) {
			content = (TextView) view.findViewById(R.id.comment_list_item_content);
			time = (TextView) view.findViewById(R.id.comment_list_item_time);
			user = (TextView) view.findViewById(R.id.comment_list_item_user);
		}
	}

}

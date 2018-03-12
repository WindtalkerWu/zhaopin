package com.qianniu.zhaopin.app.view;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.bean.CommentInfo;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.SeekWorthyCommentInfo;
import com.qianniu.zhaopin.app.common.CommonUtils;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.ui.SeekWorthyActivity;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SeekWorthyBottomView extends LinearLayout{
	private TextView count;
	private LinearLayout itemBg;
	private TextView content;
	private TextView time;
	private TextView user;
	
	private Activity activity;
	
	private SeekWorthyCommentInfo seekWorthyCommentInfo;
	
	public SeekWorthyBottomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public SeekWorthyBottomView(Context context) {
		super(context);
		initView();
	}
	public SeekWorthyBottomView(Activity activity, SeekWorthyCommentInfo seekWorthyCommentInfo) {
		super(activity.getApplicationContext());
		initView();
		this.activity = activity;
		setSeekWorthyCommentInfo(seekWorthyCommentInfo);
	}
	public void setSeekWorthyCommentInfo(
			SeekWorthyCommentInfo seekWorthyCommentInfo) {
		this.seekWorthyCommentInfo = seekWorthyCommentInfo;
		refresh();
	}
	private void initView() {
		setOrientation(LinearLayout.VERTICAL);
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seek_worthy_bottom, this);

        count = (TextView) findViewById(R.id.seek_worthy_bottom_count);
        itemBg = (LinearLayout) findViewById(R.id.comment_list_item_bg);
        content = (TextView) findViewById(R.id.comment_list_item_content);
		time = (TextView) findViewById(R.id.comment_list_item_time);
		user = (TextView) findViewById(R.id.comment_list_item_user);

		itemBg.setBackgroundResource(R.drawable.common_list_item_selector);
		int padding = CommonUtils.dip2px(getContext(), 10);
		itemBg.setPadding(padding, padding, padding, padding);
	}
	private void refresh() {
		if (seekWorthyCommentInfo != null && !seekWorthyCommentInfo.isEmpty()) {
			setVisibility(View.VISIBLE);
			String countStr = getResources().getString(R.string.seek_worthy_bottom_count);
			String countResult = String.format(countStr, seekWorthyCommentInfo.getComment_count() + "");
			count.setText(countResult);
			content.setText(seekWorthyCommentInfo.getContent());
			time.setText(seekWorthyCommentInfo.getModified());
			
			String userStr = getResources().getString(R.string.comment_list_item_user);
			String userResult = String.format(userStr, seekWorthyCommentInfo.getUser() + "");
			user.setText(userResult);
		} else {
			setVisibility(View.GONE);
		}
	}
}

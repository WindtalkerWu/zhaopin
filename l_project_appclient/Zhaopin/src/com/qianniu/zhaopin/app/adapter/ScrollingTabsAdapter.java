package com.qianniu.zhaopin.app.adapter;

import com.qianniu.zhaopin.R;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class ScrollingTabsAdapter implements IScrollingTabsAdapter {

    private final FragmentActivity activity;
    private LayoutInflater inflater;
    private static final String[] TITLES = {"分享", "曝工资", "传工资单", "邀请好友"};
    private static final String[] COUNTS = {"+10", "+20", "+45", "+50"};
    public ScrollingTabsAdapter(FragmentActivity act) {
        activity = act;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public View getView(int position) {
        final RelativeLayout tab = (RelativeLayout)inflater.inflate(R.layout.scrolling_tabs, null);
        TextView title = (TextView) tab.findViewById(R.id.scrolling_tab_title);
        TextView count = (TextView) tab.findViewById(R.id.scrolling_tab_count);
        if ( position < TITLES.length) {
        	title.setText(TITLES[position]);
        	count.setText(COUNTS[position]);
        }
        return tab;
    }
}

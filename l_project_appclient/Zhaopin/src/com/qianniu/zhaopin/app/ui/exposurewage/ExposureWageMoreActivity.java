package com.qianniu.zhaopin.app.ui.exposurewage;

import java.util.zip.Inflater;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.ui.BaseActivity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ExposureWageMoreActivity extends BaseActivity implements OnClickListener{

	private ImageButton back;
	private TextView title;
	private ListView list;
	
	private ListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exposure_wage_more);
		
		initView();
		setListener();
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.exposure_wage_more_goback);
		title = (TextView) findViewById(R.id.exposure_wage_more_title);
		
		list = (ListView) findViewById(R.id.exposure_wage_more_list);
	}
	private void setListener() {
		back.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.exposure_wage_more_goback:
			finish();
			break;

		default:
			break;
		}
	}
	private void initAdapter() {
		adapter = new ListAdapter() {
			
			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				
			}
			
			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasStableIds() {
				return false;
			}
			
			@Override
			public int getViewTypeCount() {
				return 0;
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ListViewHolder holder = null;
				if (convertView == null) {
					convertView = LayoutInflater.from(mContext).inflate(R.layout.exposure_wage_table_item_1, null);
					holder = new ListViewHolder(convertView);
					convertView.setTag(holder);
				} else {
					holder = (ListViewHolder)convertView.getTag();
				}
				
				
				return convertView;
			}
			
			@Override
			public int getItemViewType(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				return 0;
			}
			
			@Override
			public boolean isEnabled(int position) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean areAllItemsEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}
	class ListViewHolder{
		TextView tv1;				//	推荐理由
		TextView tv2;
		
		public ListViewHolder(View view){
			this.tv1 = (TextView)view.findViewById(R.id.exposure_wage_table_item_1_content);
			this.tv2 = (TextView)view.findViewById(R.id.exposure_wage_table_item_1_count);
		}
	}
}

package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.TaxCityListAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class TaxCityListActivity extends BaseActivity implements OnClickListener{
	
//	public static String selectCityKey = "selectCity";
//	public static String selectUpperLimitKey = "selectUpperLimit";

	private ImageView back;
	private ListView cityList;
	
	private TaxCityListAdapter cityListAdapter;
	
	private int[] cityUpperLimit; //社保上限基数
	private String[] cities;
	private int selectId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tax_city_list);

		getIntentData();
		initView();
		initData();
	}
	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			selectId = intent.getIntExtra(TaxActivity.selectCityIdKey, -1);
		}
	}
	private void initView() {
		back = (ImageView) findViewById(R.id.tax_city_select_goback);
		cityList = (ListView) findViewById(R.id.tax_city_select_list);
	
		back.setOnClickListener(this);
		cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				cityListAdapter.setSelectIndex(arg2);
				cityListAdapter.notifyDataSetChanged();
				Intent intent = new Intent();
				intent.putExtra(TaxActivity.selectCityIdKey, arg2);
				setResult(RESULT_OK, intent);
				finish();
			}
			
		});
	}
	private void initData() {
		cityUpperLimit = getResources().getIntArray(R.array.tax_city_social_security_upper_limit);
		cities =  getResources().getStringArray(R.array.tax_city);
		
		cityListAdapter = new TaxCityListAdapter(mContext, cities);
		cityListAdapter.setSelectIndex(selectId);

		cityList.setAdapter(cityListAdapter);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tax_city_select_goback:
			finish();
			break;

		default:
			break;
		}
	}
}

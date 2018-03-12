package com.qianniu.zhaopin.app.ui.integrationmall;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.ui.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.app.Activity;

public class IntegrationSearchActivity extends BaseActivity {

	private ImageButton back;
	private EditText searchKey;
	private ImageButton doSearch;
	
	private ListView resultList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_integration_search);
		
		initView();
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.integration_search_title_goback);
		searchKey = (EditText) findViewById(R.id.integration_search_key);
		doSearch = (ImageButton) findViewById(R.id.integration_search_title_search);
		resultList = (ListView) findViewById(R.id.integration_search_content);
		
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		doSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}

}

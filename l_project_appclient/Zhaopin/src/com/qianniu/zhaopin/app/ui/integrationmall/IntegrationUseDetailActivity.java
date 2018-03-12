package com.qianniu.zhaopin.app.ui.integrationmall;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.R.layout;
import com.qianniu.zhaopin.app.common.UIHelper;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class IntegrationUseDetailActivity extends Activity {

	private ImageButton back;
	private ImageView collection;
	private ImageView share;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_integration_use_detail);
		
		initView();
	}

	private void initView () {
		back = (ImageButton) findViewById(R.id.integration_use_detail_goback);
		collection = (ImageView) findViewById(R.id.integration_use_detail_title_collection);
		share = (ImageView) findViewById(R.id.integration_use_detail_title_share);
		
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		collection.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				UIHelper.showShareDialog(context, title, msg);
			}
		});
	}
}

package com.qianniu.zhaopin.app.ui.integrationmall;

import com.qianniu.zhaopin.R;

import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class IntegrationGoodsListActivity extends Activity {
	private ImageButton back;
	private TextView title;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_integration_goods_list);
		
		initView();
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.integration_goods_list_title_goback);
		title = (TextView) findViewById(R.id.integration_goods_list_title);
		list = (ListView) findViewById(R.id.integration_goods_list);
	}

}

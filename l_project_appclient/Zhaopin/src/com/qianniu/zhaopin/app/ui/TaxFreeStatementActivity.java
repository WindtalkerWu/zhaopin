package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.R;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class TaxFreeStatementActivity extends BaseActivity {
	private ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tax_free_statement);
		
		initView();
	}

	private void initView() {
		back = (ImageView) findViewById(R.id.tax_free_statement_goback);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}

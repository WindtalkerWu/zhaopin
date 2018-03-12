package com.qianniu.zhaopin.app.ui.integrationmall;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.R.layout;
import com.qianniu.zhaopin.app.common.UIHelper;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IntegrationOrderActivity extends Activity {

	private ImageButton back;
	private RelativeLayout addressBg;
	private ImageView goodsCountAdd;
	private ImageView goodsCountMinus;
	private TextView goodsCount;
	private TextView integrationCount;//积分
	private Button doSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_integration_order);
		
		initView();
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.integration_order_title_goback);
		addressBg = (RelativeLayout) findViewById(R.id.integration_order_address_bg);
		goodsCountAdd = (ImageView) findViewById(R.id.integration_order_goodscount_add);
		goodsCountMinus = (ImageView) findViewById(R.id.integration_order_goodscount_minus);
		goodsCount = (TextView) findViewById(R.id.integration_order_goods_count);
		integrationCount = (TextView) findViewById(R.id.integration_order_integration_count);
		doSubmit = (Button) findViewById(R.id.integration_order_doSubmit);
		
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		addressBg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UIHelper.startDeliveryAddressList(IntegrationOrderActivity.this);
			}
		});
		goodsCountAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editGoodsCount(1);
			}
		});
		goodsCountMinus.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editGoodsCount(-1);
			}
		});
	}
	private void editGoodsCount(int value) {
		int count = Integer.parseInt(goodsCount.getText().toString());
		if (value < 0 && count <= 0) {
			return;
		}
		count = count + value;
		goodsCount.setText(count + "");
	}

}

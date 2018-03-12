package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.SocialSecurityFundInfo;
import com.qianniu.zhaopin.app.bean.TaxRateInfo;
import com.qianniu.zhaopin.util.NumberUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TaxDetailActivity extends BaseActivity implements OnClickListener{

	private ImageView goBack;
	private EditText pension;
	private TextView pensionRate;
	private EditText medical;
	private TextView medicalRate;
	private EditText losejob;
	private TextView losejobRate;
	private EditText fund;
	private TextView fundRate;
	private TextView total;
	
	private SocialSecurityFundInfo socialSecurityFundInfo;
	private TaxRateInfo taxRateInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tax_detail);
		initView();
		initData();
	}

	private void initData() {
		String totalStr = getResources().getString(R.string.tax_detail_total);
		String resultTotal = String.format(totalStr, 0 + "");
		Intent intent = getIntent();
		if (intent != null) {
			socialSecurityFundInfo = (SocialSecurityFundInfo) intent
					.getSerializableExtra(TaxActivity.socialSecurityFundInfoKey);
			if (socialSecurityFundInfo != null) {
				float pensionf = socialSecurityFundInfo.getPension();
				float medicalf = socialSecurityFundInfo.getMedical();
				float losejobf = socialSecurityFundInfo.getLosejob();
				float fundf = socialSecurityFundInfo.getFund();
				pension.setText(pensionf + "");
				medical.setText(medicalf + "");
				losejob.setText(losejobf + "");
				fund.setText(fundf + "");

				resultTotal = String.format(totalStr, NumberUtils.scaleDecimal2(pensionf + medicalf + losejobf + fundf) + "");
			}
		}
		total.setText(resultTotal);

		taxRateInfo = AppContext.getTaxRateInfo(mContext);
		
		pensionRate.setText(taxRateInfo.getPensionRate() * 100 + "%");
		medicalRate.setText(taxRateInfo.getMedicalRate() * 100 + "%");
		losejobRate.setText(taxRateInfo.getLosejobRate() * 100 + "%");
		fundRate.setText(taxRateInfo.getFundRate() * 100 + "%");
		
	}
	private void initView() {
		goBack = (ImageView) findViewById(R.id.tax_detail_goback);
		pension = (EditText) findViewById(R.id.tax_detail_pension);
		pensionRate = (TextView) findViewById(R.id.tax_detail_pension_rate);
		medical = (EditText) findViewById(R.id.tax_detail_medical);
		medicalRate = (TextView) findViewById(R.id.tax_detail_medical_rate);
		losejob = (EditText) findViewById(R.id.tax_detail_losejob);
		losejobRate = (TextView) findViewById(R.id.tax_detail_losejob_rate);
		fund = (EditText) findViewById(R.id.tax_detail_fund);
		fundRate = (TextView) findViewById(R.id.tax_detail_fund_rate);
		total = (TextView) findViewById(R.id.tax_detail_total);
		
		goBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tax_detail_goback://选择城市
			finish();
			break;
		
		default:
			break;
		}
		
	}

}

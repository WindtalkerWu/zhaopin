package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.TaxRateInfo;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.UIHelper;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;

public class TaxRateSettingActivity extends BaseActivity implements OnClickListener,OnFocusChangeListener{

	private ImageView goBack;
	private ImageView save;
	private EditText pension; //养老
	private EditText medical;//医疗
	private EditText losejob;//失业
	private EditText fund;//公积金
	
	private TaxRateInfo taxRateInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tax_setting);
		initView();
		initTaxRateView();
	}
	private void initTaxRateView() {
		taxRateInfo = AppContext.getTaxRateInfo(mContext);
		pension.setText(taxRateInfo.getPensionRate() * 100 + "");
		medical.setText(taxRateInfo.getMedicalRate() * 100 + "");
		losejob.setText(taxRateInfo.getLosejobRate() * 100 + "");
		fund.setText(taxRateInfo.getFundRate() * 100 + "");
	}
	private void initView() {
		goBack = (ImageView) findViewById(R.id.tax_setting_goback);
		save = (ImageView) findViewById(R.id.tax_setting_save);
		pension = (EditText) findViewById(R.id.tax_setting_pension);
		medical = (EditText) findViewById(R.id.tax_setting_medical);
		losejob = (EditText) findViewById(R.id.tax_setting_losejob);
		fund = (EditText) findViewById(R.id.tax_setting_fund);
		
		goBack.setOnClickListener(this);
		save.setOnClickListener(this);
		pension.setOnFocusChangeListener(this);
		medical.setOnFocusChangeListener(this);
		losejob.setOnFocusChangeListener(this);
		fund.setOnFocusChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tax_setting_goback://选择城市
			showBackDialog();
			break;
		case R.id.tax_setting_save:
			saveRate();
			break;
		default:
			break;
		}
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.tax_setting_pension:
		case R.id.tax_setting_medical:
		case R.id.tax_setting_losejob:
		case R.id.tax_setting_fund:
			if (hasFocus) {
				EditText et = (EditText)v;
				et.setSelection(et.getText().length());
			}
			break;
		default:
			break;
		}
	}
	private void showBackDialog() {
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					finish();
				}
				dialog.dismiss();
			}
		};
		UIHelper.showCommonDialog(this, R.string.tax_free_dialog_tips, onClickListener);
	}
	private void saveRate() {
		if (!checkNull()) {
			taxRateInfo.setPensionRate(Float.parseFloat(pension.getText().toString()) / 100);
			taxRateInfo.setMedicalRate(Float.parseFloat(medical.getText().toString()) / 100);
			taxRateInfo.setLosejobRate(Float.parseFloat(losejob.getText().toString()) / 100);
			taxRateInfo.setFundRate(Float.parseFloat(fund.getText().toString()) / 100);
			DMSharedPreferencesUtil.putSharePre(mContext, 
					DMSharedPreferencesUtil.DM_APP_DB, DMSharedPreferencesUtil.taxRate,
					ObjectUtils.getJsonStringFromObject(taxRateInfo));

			setResult(RESULT_OK);
			finish();
		}
	}
	private boolean checkNull() {
		if (TextUtils.isEmpty(pension.getText().toString())) {
			UIHelper.ToastMessage(mContext, R.string.tax_setting_pension_null);
			return true;
		}
		if (TextUtils.isEmpty(medical.getText().toString())) {
			UIHelper.ToastMessage(mContext, R.string.tax_setting_medical_null);
			return true;
		}
		if (TextUtils.isEmpty(losejob.getText().toString())) {
			UIHelper.ToastMessage(mContext, R.string.tax_setting_losejob_null);
			return true;
		}
		if (TextUtils.isEmpty(fund.getText().toString())) {
			UIHelper.ToastMessage(mContext, R.string.tax_setting_fund_null);
			return true;
		}
		return false;
	}
}

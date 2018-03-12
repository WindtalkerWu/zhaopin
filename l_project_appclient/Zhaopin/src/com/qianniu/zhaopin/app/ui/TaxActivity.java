package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.SocialSecurityFundInfo;
import com.qianniu.zhaopin.app.bean.TaxRateInfo;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.util.NumberUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TaxActivity extends BaseActivity implements OnClickListener{

	public static String selectCityIdKey = "selectCityId";
	public static String socialSecurityFundInfoKey = "socialSecurityFundInfo";

	private static int requestSelectCity = 0;
	private static int requestSettingRate = 1;

	private float defalutThreshold = 3500f;//默认起证额

	private ImageView goBack;
	private Button rateSetting;
	private RelativeLayout selectCityBg;
	private EditText selectCityEt;
	private ImageView selectCityImage;
	private EditText taxThreshold;//起征额
	private EditText beforeTax;//税前收入
	private EditText upperLimit;
	private RelativeLayout socialSecurityBg;
	private TextView socialSecurityTv;
	private TextView socialSecurity;//社保公积金
	private TextView freeStatement;
    private Button calculate;
    private Button reset;
    private TextView afterTax;//税后收入
    private TextView myTax;//应缴个税
    
    private TaxRateInfo taxRateInfo;
    private int[] cityUpperLimit; //社保上限基数
	private String[] cities;
	private int selectCityId;
	
	private SocialSecurityFundInfo socialSecurityFundInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tax);
		initView();
		initCitiesData();
	}

	private void initCitiesData() {
		cityUpperLimit = getResources().getIntArray(R.array.tax_city_social_security_upper_limit);
		cities =  getResources().getStringArray(R.array.tax_city);
		selectCityId = 0;
		
		selectCityEt.setText(cities[selectCityId]);
		taxThreshold.setText(defalutThreshold + "");
		upperLimit.setText(cityUpperLimit[selectCityId] + "");
	}
	private void initTaxRateInfo() {
		taxRateInfo = AppContext.getTaxRateInfo(mContext);
	}
	private void initView() {
		goBack = (ImageView) findViewById(R.id.tax_goback);
		rateSetting = (Button) findViewById(R.id.tax_rate_setting);
		selectCityBg = (RelativeLayout) findViewById(R.id.tax_select_city_item);
		selectCityEt = (EditText) findViewById(R.id.tax_city);
		selectCityImage = (ImageView) findViewById(R.id.tax_city_image);
		taxThreshold = (EditText) findViewById(R.id.tax_threshold);
		beforeTax = (EditText) findViewById(R.id.tax_before_tax);
		upperLimit = (EditText) findViewById(R.id.tax_upper_limit);
		socialSecurityBg = (RelativeLayout) findViewById(R.id.tax_social_security_item);
		socialSecurityTv = (TextView) findViewById(R.id.tax_social_security_tv);
		socialSecurity = (TextView) findViewById(R.id.tax_social_security);
		freeStatement = (TextView) findViewById(R.id.tax_free_statement);
		calculate = (Button) findViewById(R.id.tax_calculate);
		reset = (Button) findViewById(R.id.tax_reset);
//		taxDetail = (RelativeLayout) findViewById(R.id.tax_detail_bg);
		afterTax = (TextView) findViewById(R.id.tax_after_tax);
		myTax = (TextView) findViewById(R.id.tax_my_tax);
		
		goBack.setOnClickListener(this);
		rateSetting.setOnClickListener(this);
		selectCityBg.setOnClickListener(this);
		selectCityEt.setOnClickListener(this);
		selectCityImage.setOnClickListener(this);
		calculate.setOnClickListener(this);
		reset.setOnClickListener(this);
		socialSecurityBg.setOnClickListener(this);
//		taxDetail.setOnClickListener(this);
		
		initFreeStatement();
		initSocialSecurityFund();
	}
	
	private void initFreeStatement() {
		String initFreeStatementStr = getResources().getString(R.string.tax_free_statement);
		SpannableString statementTips = new SpannableString(initFreeStatementStr);
		statementTips.setSpan(new ClickableSpan() {
	            @Override
	            public void onClick(View widget) {
	            	startTaxFreeStatementActivity();
	            }
	        }, 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		freeStatement.setText(statementTips);
		freeStatement.setMovementMethod(LinkMovementMethod.getInstance());
	}
	private void initSocialSecurityFund() {
		String initSocialSecurityFundStr = getResources().getString(R.string.tax_item_social_security);
		SpannableString tips = new SpannableString(initSocialSecurityFundStr);
		tips.setSpan(new ClickableSpan() {
	            @Override
	            public void onClick(View widget) {
	            	startTaxDetailActivity();
	            }
	        }, 6, tips.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		socialSecurityTv.setText(tips);
		socialSecurityTv.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == requestSelectCity) {
				selectCityId = data.getIntExtra(selectCityIdKey, 0);
				selectCityEt.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						selectCityEt.setText(cities[selectCityId]);
						taxThreshold.setText(defalutThreshold + "");
						upperLimit.setText(cityUpperLimit[selectCityId] + "");
						calculateAndRefreash(false);
					}
				}, 150);
			}
			if (requestCode == requestSettingRate) {
				selectCityEt.postDelayed(new Runnable() {

					@Override
					public void run() {
						calculateAndRefreash(false);
					}
					
				}, 300);
			}
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tax_goback://选择城市
			finish();
			break;
		case R.id.tax_rate_setting:
			startTaxRateSettingActivity();
			break;
		case R.id.tax_select_city_item:
		case R.id.tax_city_image://选择城市
		case R.id.tax_city://选择城市
			startTaxCitySelectActivity();
			break;
		case R.id.tax_calculate://计算
			calculateAndRefreash(true);
			break;
		case R.id.tax_reset://重置
			reset();
			break;
		case R.id.tax_social_security_item://详细
			startTaxDetailActivity();
			break;
		default:
			break;
		}
	}
	private void reset() {
		selectCityId = 0;
		socialSecurityFundInfo = null;

		selectCityEt.setText(cities[selectCityId]);
		taxThreshold.setText(defalutThreshold + "");
		upperLimit.setText(cityUpperLimit[selectCityId] + "");
		socialSecurity.setText("");
		beforeTax.setText("");
		afterTax.setText("");
		myTax.setText("");;
	}
	private void startTaxDetailActivity() {
		Intent intent = new Intent(TaxActivity.this, TaxDetailActivity.class);
		if (socialSecurityFundInfo != null) {
			intent.putExtra(socialSecurityFundInfoKey, socialSecurityFundInfo);
		}
		startActivity(intent);
	}
	private void startTaxRateSettingActivity() {
		Intent intent = new Intent(TaxActivity.this, TaxRateSettingActivity.class);
		startActivityForResult(intent, requestSettingRate);
	}
	private void startTaxCitySelectActivity() {
		Intent intent = new Intent(TaxActivity.this, TaxCityListActivity.class);
		intent.putExtra(selectCityIdKey, selectCityId);
		startActivityForResult(intent, requestSelectCity);
	}
	private void startTaxFreeStatementActivity() {
		Intent intent = new Intent(TaxActivity.this, TaxFreeStatementActivity.class);
		startActivity(intent);
	}
	private void calculateAndRefreash(boolean isToast) {
		if (!checkNull(isToast)) {
			initTaxRateInfo();
			String beforeTaxStr = beforeTax.getText().toString();
			float beforeTax = Float.parseFloat(beforeTaxStr);//薪资
			String thresholdStr = taxThreshold.getText().toString();
			float threshold = Float.parseFloat(thresholdStr);//起征额
			
			float socialSecurityFund = calculateSocialSecurityFund(beforeTax);
			float tax = calculate(beforeTax, socialSecurityFund, threshold);

			socialSecurity.setText(socialSecurityFund + "");
			afterTax.setText(NumberUtils.scaleDecimal2(beforeTax - socialSecurityFund - tax) + "");
			myTax.setText(tax + "");
		}
	}
	private boolean checkNull(boolean isToast) {
		String taxThresholdStr = taxThreshold.getText().toString();
		if (TextUtils.isEmpty(taxThresholdStr)) {
			if (isToast) {
				UIHelper.ToastMessage(mContext, R.string.tax_check_threshold_null);
			}
			return true;
		}
		String beforeTaxStr = beforeTax.getText().toString();
		if (TextUtils.isEmpty(beforeTaxStr)) {
			if (isToast) {
				UIHelper.ToastMessage(mContext, R.string.tax_check_before_tax_null);
			}
			return true;
		}
		return false;
	}
	//计算社保公积金
	/**
	 * @param beforeTax
	 * @return
	 */
	private float calculateSocialSecurityFund(float beforeTax) {
		float newBeforeTax = beforeTax;
		float upperLimitf = Float.parseFloat(upperLimit.getText().toString());
		if (beforeTax > upperLimitf) {
			newBeforeTax = upperLimitf;
		}
		
		if (socialSecurityFundInfo == null) {
			socialSecurityFundInfo = new SocialSecurityFundInfo();
		}
		float pension = NumberUtils.scaleDecimal2(taxRateInfo.getPensionRate() * newBeforeTax);
		float medical = NumberUtils.scaleDecimal2(taxRateInfo.getMedicalRate() * newBeforeTax);
		float losejob = NumberUtils.scaleDecimal2(taxRateInfo.getLosejobRate() * newBeforeTax);
		float fund = NumberUtils.scaleDecimal2(taxRateInfo.getFundRate() * newBeforeTax);
		
		socialSecurityFundInfo.setPension(pension);
		socialSecurityFundInfo.setMedical(medical);
		socialSecurityFundInfo.setLosejob(losejob);
		socialSecurityFundInfo.setFund(fund);
		
		return NumberUtils.scaleDecimal2(pension + medical + losejob + fund);
	}
	//计算个税
	private float calculate(float beforeTax, float socialSecurityFund, float threshold) {
		float tax = 0; //个人税率
		
		float taxBase = (beforeTax -socialSecurityFund - threshold);
		if (taxBase > 80000) {
			tax = NumberUtils.scaleDecimal2(taxBase * 0.45f - 13505f);
		} else if (taxBase > 55000) {
			tax = NumberUtils.scaleDecimal2(taxBase * 0.35f - 5505f);
		} else if (taxBase > 35000) {
			tax = NumberUtils.scaleDecimal2(taxBase * 0.30f - 2755f);
		} else if (taxBase > 9000) {
			tax = NumberUtils.scaleDecimal2(taxBase * 0.25f - 1005f);
		} else if (taxBase > 4500) {
			tax = NumberUtils.scaleDecimal2(taxBase * 0.20f - 555f);
		} else if (taxBase > 1500) {
			tax = NumberUtils.scaleDecimal2(taxBase * 0.10f - 105f);
		} else if (taxBase > 0){
			tax = NumberUtils.scaleDecimal2(taxBase * 0.03f);
		}
		return tax;
	}
}

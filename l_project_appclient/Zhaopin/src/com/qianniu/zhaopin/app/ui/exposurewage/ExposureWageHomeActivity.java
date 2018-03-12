package com.qianniu.zhaopin.app.ui.exposurewage;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.app.view.AdZoneView;
import com.qianniu.zhaopin.app.view.ExposureWageTableLayout;
import com.qianniu.zhaopin.util.QNUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Advanceable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ExposureWageHomeActivity extends BaseActivity {

	private ImageButton back;
	private LinearLayout adViewBg;
	private AdZoneView adView;
	private EditText searchKey;
	private ImageView searchClean;
	private TextView searchCount;
	private TextView searchCountDay;
	private TextView getSearchCount;
	private ExposureWageTableLayout hotCompany;
	private ExposureWageTableLayout hotPost;
	private Button sunWage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exposure_wage_home);
		initView();
	}
	@Override
	protected void onResume() {
		super.onResume();

		initSearchCount(3 + "");
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.exposure_wage_home_goback);
		
		adViewBg = (LinearLayout) findViewById(R.id.exposure_wage_home_adzoneview);
		adView = new AdZoneView(ExposureWageHomeActivity.this, AdZoneView.ADZONE_ID_INFO);
		adViewBg.addView(adView);
		
		searchKey = (EditText) findViewById(R.id.exposure_wage_home_search_key);
		searchClean = (ImageView) findViewById(R.id.exposure_wage_home_cleansearch);
		searchCount = (TextView) findViewById(R.id.exposure_wage_home_search_count);
		searchCountDay = (TextView) findViewById(R.id.exposure_wage_home_search_count_day);
		getSearchCount = (TextView) findViewById(R.id.exposure_wage_home_get_search_count);
		sunWage = (Button) findViewById(R.id.exposure_wage_home_sun_wage);
		
		setListener();
	}
	private void setListener() {
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		sunWage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ExposureWageHomeActivity.this, ExposureWageActivity.class);
            	startActivity(intent);
			}
		});
		searchClean.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				searchKey.setText("");
			}
		});
		searchKey.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId ==EditorInfo.IME_ACTION_SEARCH){
					//do something
					Intent intent = new Intent(ExposureWageHomeActivity.this, SearchResultActivity.class);
					startActivity(intent);
					
					// 先隐藏键盘
					((InputMethodManager) searchKey.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(ExposureWageHomeActivity.this.getCurrentFocus()
					.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
					return true;
				}
				return false;
			}
		}); 
	}

	private void initSearchCount(String count) {
		String initSearchCountStr = getResources().getString(R.string.exposure_wage_home_search_count);
		String searchCountStr = String.format(initSearchCountStr, count);
		SpannableStringBuilder searchCountSpanStr = QNUtil.formateStringColor(searchCountStr, 2, count.length() + 2, Color.RED);
		searchCount.setText(searchCountSpanStr);
		
		String initSearchCountDayStr = getResources().getString(R.string.exposure_wage_home_search_count_day);
		String searchCountDayStr = String.format(initSearchCountDayStr, count);
		SpannableStringBuilder searchCountDaySpanStr = QNUtil.formateStringColor(searchCountDayStr, 
				searchCountDayStr.length() - 2, searchCountDayStr.length() - 1, mContext.getResources().getColor(R.color.resume_button_color));
		searchCountDay.setText(searchCountDaySpanStr);
		
		SpannableStringBuilder getSearchCountSpanStr = new SpannableStringBuilder(
				getResources().getString(R.string.exposure_wage_home_get_search_count));
		getSearchCountSpanStr.setSpan(new ClickableSpan() {
	            @Override
	            public void onClick(View widget) {
	            	//do something
	            	Intent intent = new Intent(ExposureWageHomeActivity.this, ExposureWageGetCountActivity.class);
	            	startActivity(intent);
	            }
	        }, 1, getSearchCountSpanStr.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSearchCount.setText(getSearchCountSpanStr);
		getSearchCount.setMovementMethod(LinkMovementMethod.getInstance());
	}
}

package com.qianniu.zhaopin.app.ui.exposurewage;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.ui.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ExposureWageFragment extends BaseFragment {
	private TextView cityTv;
	private EditText companyNameEt;
	private EditText postNameEt;
	private EditText salaryEt;
	private Button doSubmit;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MyLogger.i(getTag(), "onCreateView");
		View view = inflater.inflate(R.layout.fragment_exposure_wage, null);
		
		initView(view);
		
		return view;
	}
	private void initView(View view) {
		cityTv = (TextView) view.findViewById(R.id.exposure_wage_city);
		companyNameEt = (EditText) view.findViewById(R.id.exposure_wage_company);
		postNameEt = (EditText) view.findViewById(R.id.exposure_wage_post);
		salaryEt = (EditText) view.findViewById(R.id.exposure_wage_salary);
		doSubmit = (Button) view.findViewById(R.id.exposure_wage_dosubmit);
	}

}

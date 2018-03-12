package com.qianniu.zhaopin.app.ui.exposurewage;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.ui.BaseFragment;
import com.qianniu.zhaopin.app.view.ExposureWageShareView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExposureWageShareFragment extends BaseFragment {
	private ExposureWageShareView shareView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		MyLogger.i(getTag(), "onCreate");
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MyLogger.i(getTag(), "onCreateView");
		shareView = (ExposureWageShareView) inflater.inflate(R.layout.fragment_exposure_wage_share, null);
		shareView.setActivty(getActivity());
		return shareView;
	}
	@Override
	public void onDestroy() {
		MyLogger.i(getTag(), "onDestroy");
		super.onDestroy();
	}

}

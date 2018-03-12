package com.qianniu.zhaopin.app.ui.exposurewage;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.ui.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExposureInviteFriendsFragment extends BaseFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		MyLogger.i(getTag(), "onCreateView");
		View view = inflater.inflate(R.layout.fragment_exposure_invitefriends, null);
		
		initView(view);
		
		return view;
	}
	private void initView(View view) {
	}

}

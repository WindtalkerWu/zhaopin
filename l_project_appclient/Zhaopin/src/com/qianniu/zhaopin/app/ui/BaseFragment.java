package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.common.ThreadPoolController;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewStub;

public class BaseFragment extends Fragment {
	public ThreadPoolController threadPool ;
	private int index;
	protected View progressBar;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	public void initProgressBar(View container,int resId) {
		ViewStub stub = (ViewStub)container.findViewById(resId);
		progressBar = stub.inflate();
		progressBar.setVisibility(View.GONE);
	}
	public void showProgressBar() {
		if (progressBar != null && progressBar.getVisibility() != View.VISIBLE) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	public void hideProgressBar() {
		if (progressBar != null) {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
	public void goneProgressBar() {
		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}
	}
}

package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.R;
import com.umeng.common.net.t;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MoreProfessionalInfoActivity extends BaseFragmentActivity
		implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actitvity_moreinfo);
		ImageButton btn_back = (ImageButton) findViewById(R.id.goback_ibtn);
		btn_back.setOnClickListener(this);
		SubscriptionManageFragment fragment = SubscriptionManageFragment
				.newDisplayInstance(
						SubscriptionManageFragment.DISPLAYTYPE_SHOW,
						SubscriptionManageFragment.TYPE_INFO);
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.goback_ibtn: {
			this.finish();
		}
			break;

		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}

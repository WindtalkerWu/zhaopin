package com.qianniu.zhaopin.app.ui;


import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.thp.UmShare;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ResumeNewGuideActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resume_newguide_layout);
		findViewById(R.id.skip_btn).setOnClickListener(mclicklistener);
		findViewById(R.id.newresume_btn).setOnClickListener(mclicklistener);
		DMSharedPreferencesUtil.putSharePre(this, 
				DMSharedPreferencesUtil.DM_HOTLABEL_DB,
				DMSharedPreferencesUtil.FIELD_FIRST_RESUMELIBRARY,
				DMSharedPreferencesUtil.VALUE_FLG_FIRSTRESUMELIBRARY);
	}

	private OnClickListener mclicklistener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.skip_btn :{
				ResumeNewGuideActivity.this.finish();
			}
			break;
			case R.id.newresume_btn :{
				// 友盟统计--我的简历库--创建按钮
				UmShare.UmStatistics(ResumeNewGuideActivity.this, "Resumelist_NewButton");

				UIHelper.showMyResumeEditActivityForResult(
						ResumeNewGuideActivity.this, 0);
				ResumeNewGuideActivity.this.finish();
			}
			break;			
			}
		}
	};
}

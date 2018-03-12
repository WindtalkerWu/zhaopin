package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppStart;
import com.qianniu.zhaopin.app.bean.CityChooseData;
import com.qianniu.zhaopin.app.common.DMSharedPreferencesUtil;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class GuideActivity extends BaseActivity {
	
	private LinearLayout m_llGuide;
	
	private Context m_context;
	private AppContext mappContext;
	
	private int m_nGuideType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		
		m_context = this;
		mappContext = (AppContext) this.getApplication();
		
		initData();
		init();
	}
	
	private void initData(){
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_nGuideType = bundle.getInt(HeadhunterPublic.GUIDE_DATATRANSFER_TYPE);
		}else{
			m_nGuideType = HeadhunterPublic.GUIDETYPE_REWARD;
		}
	}
	
	private void init(){
		m_llGuide = (LinearLayout)findViewById(R.id.guide_layout);
		m_llGuide.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(m_nGuideType){
				case HeadhunterPublic.GUIDETYPE_REWARD:
					{
						DMSharedPreferencesUtil.putSharePre(m_context, 
								DMSharedPreferencesUtil.DM_HOTLABEL_DB,
								DMSharedPreferencesUtil.FIELD_FIRST_REWARD,
								DMSharedPreferencesUtil.VALUE_FLG_FIRSTREWARD);
//						startHotLabelActivity();
					}
					break;
				case HeadhunterPublic.GUIDETYPE_MY:
					{
						DMSharedPreferencesUtil.putSharePre(m_context, 
								DMSharedPreferencesUtil.DM_HOTLABEL_DB,
								DMSharedPreferencesUtil.FIELD_FIRST_MY,
								DMSharedPreferencesUtil.VALUE_FLG_FIRSTMY);
					}
					break;
				default:
					break;
				}
				
				finish();
			}
			
		});
		
		switch(m_nGuideType){
		case HeadhunterPublic.GUIDETYPE_REWARD:
			{
				
			}
			break;
		case HeadhunterPublic.GUIDETYPE_MY:
			{
				m_llGuide.setBackgroundResource(R.drawable.common_bg_guide_my);
			}
			break;
		default:
			break;
		}
	}
	private void startHotLabelActivity() {
		int toHotLabel = DMSharedPreferencesUtil.getSharePreInt(GuideActivity.this, DMSharedPreferencesUtil.DM_HOTLABEL_DB,
				DMSharedPreferencesUtil.toHotLabel);
		if (toHotLabel == -1) {
			Intent intent = new Intent(GuideActivity.this, HotLabelActivity.class);
			startActivity(intent);
			DMSharedPreferencesUtil.putSharePre(GuideActivity.this, DMSharedPreferencesUtil.DM_HOTLABEL_DB,
				DMSharedPreferencesUtil.toHotLabel, 1);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
}

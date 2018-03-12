package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;

import com.qianniu.zhaopin.app.bean.OneLevelData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.widget.HotLabelFlowItem;
import com.qianniu.zhaopin.app.widget.HotLabelFlowLayout;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class RewardSearchLabelActivity extends BaseActivity {

	private ImageButton back;
	private HotLabelFlowLayout labelFlowLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reward_search_label);
		initView();
	}
	private void initView() {
		back = (ImageButton) findViewById(R.id.reward_search_label_goback);
		labelFlowLayout = (HotLabelFlowLayout) findViewById(R.id.reward_search_label_flow_bg);
		labelFlowLayout.setSelectedLabels((ArrayList<OneLevelData>)getIntent().getSerializableExtra("selectLabels"));
		labelFlowLayout.initData(this, HotLabelFlowLayout.ALLLABEL, true);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	@Override
	public void finish() {
		Intent intent = new Intent(); 
        intent.putExtra("selectLabel", (ArrayList<OneLevelData>)labelFlowLayout.getSelectedLabels());
        setResult(HeadhunterPublic.RESULT_LABEL_OK, intent);
		super.finish();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

}

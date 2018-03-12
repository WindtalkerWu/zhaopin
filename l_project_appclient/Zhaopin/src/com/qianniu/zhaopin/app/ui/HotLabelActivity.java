package com.qianniu.zhaopin.app.ui;

import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.bean.OneLevelData;
import com.qianniu.zhaopin.app.view.Util;
import com.qianniu.zhaopin.app.widget.HotLabelFlowLayout;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HotLabelActivity extends BaseActivity {
	private Context m_Context;

	private TextView tipsMore;
	private HotLabelFlowLayout labelFlowLayout;
	private Button labelSave;
	private Button labelSkip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hot_label);
		m_Context = this;
		
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
		
		initView();
	}
	private void initView() {
		tipsMore = (TextView) findViewById(R.id.hot_label_tips_more);
		labelFlowLayout = (HotLabelFlowLayout) findViewById(R.id.hot_label_flow_bg);
		labelSave = (Button) findViewById(R.id.hot_label_save);
		labelSkip = (Button) findViewById(R.id.hot_label_skip);

		labelFlowLayout.initData(this, HotLabelFlowLayout.ALLLABEL, true);
		changeLabelsCount(0);
		labelSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (labelFlowLayout.getSelectedLabels() == null || labelFlowLayout.getSelectedLabels().size() <= 0) {
					Util.toast(getApplicationContext(), "请先选择标签", true);
					return;
				}
				OneLevelData.saveToHotLabelTable((AppContext)getApplicationContext(), labelFlowLayout.getSelectedLabels());
				finish();
			}
		});
		labelSkip.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 友盟统计
				UmShare.UmStatistics(m_Context, "Reward_HotLabel_Skip");
				
				finish();
			}
		});
	}
	public void changeLabels(List<OneLevelData> selectedLabels) {
		int selectCount = selectedLabels.size();
		changeLabelsCount(selectCount);
	}
	private void changeLabelsCount(int selectCount) {
		String tipsMoreStr = getResources().getString(R.string.hot_label_tips_more);    
		String tipsMoreResult = String.format(tipsMoreStr, selectCount, HotLabelFlowLayout.MAXSELECTEDSIZE - selectCount);
		tipsMore.setText(tipsMoreResult);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		UmShare.UmPause(m_Context);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		UmShare.UmResume(m_Context);
	}
	
	
}

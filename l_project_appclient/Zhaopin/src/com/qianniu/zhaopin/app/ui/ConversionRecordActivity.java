package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.adapter.ConversionRecordListAdapter;
import com.qianniu.zhaopin.app.adapter.RewardInfoListAdapter;
import com.qianniu.zhaopin.app.bean.ConversionInfoData;
import com.qianniu.zhaopin.app.bean.RewardData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView.OnRefreshListener;

/**
 * 积分兑换记录
 * @author wuzy
 *
 */
public class ConversionRecordActivity extends BaseActivity implements
	OnItemClickListener, OnScrollListener, OnRefreshListener{
	
	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageButton m_btnBack;				// 返回按钮

	// 积分兑换记录列表相关
	private PullToRefreshListView m_lvCR;		// 
	private ConversionRecordListAdapter m_crLA;	// 积分兑换记录适配器
	private List<ConversionInfoData> m_lsCID;	// 积分信息数据
	
	private View m_vFoot;
	private TextView m_tvFoot;
	private ProgressBar m_progressFoot;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_conversionrecord);
		
		m_Context = this;
		m_appContext = (AppContext) getApplication();
		
//		initData();
		// 初始化控件
		initControl();
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 返回按钮
		m_btnBack = (ImageButton)findViewById(R.id.conversionrecord_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 积分记录列表
		initRecordListView();
	}
	
	/**
	 * 初始化积分记录列表
	 */
	private void initRecordListView(){
		
//		m_viewLoad = (View) findViewById(R.id.commom_loading_layout); 
		
		// 收藏的悬赏任务列表
		m_lvCR = (PullToRefreshListView)findViewById(R.id.conversionrecord_lv_record);
		m_lvCR.setOnItemClickListener(this);
		m_lvCR.setOnScrollListener(this);
		m_lvCR.setOnRefreshListener(this);
		
		m_vFoot = getLayoutInflater().inflate(R.layout.listview_footer, null);
		m_vFoot.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		
		m_progressFoot = (ProgressBar) m_vFoot
				.findViewById(R.id.listview_foot_progress);
		m_progressFoot.setVisibility(View.GONE);
		m_tvFoot = (TextView) m_vFoot
				.findViewById(R.id.listview_foot_more);
		
		m_lsCID = new ArrayList<ConversionInfoData>();
		// test
		ConversionInfoData m1 = new ConversionInfoData();
		m_lsCID.add(m1);
		ConversionInfoData m2 = new ConversionInfoData();
		m_lsCID.add(m2);
		 
		m_crLA = new ConversionRecordListAdapter(m_appContext, m_lsCID);
		
		m_lvCR.addFooterView(m_vFoot);
		m_lvCR.setAdapter(m_crLA);
		
//		m_lvCR.firstRefreshing();
//		m_isFirstRefreashing = true;
//		m_nActionType = UIHelper.LISTVIEW_ACTION_INIT;
		// 获取兑换记录数据
	}
}

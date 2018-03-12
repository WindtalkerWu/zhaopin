package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.adapter.OneLevelChooseListAdapter;
import com.qianniu.zhaopin.app.bean.CityChooseData;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class OneLevelChooseActivity extends BaseActivity
	implements OnItemClickListener {
	private Context m_Context;
	
	private ImageButton m_btnBack;				// 返回按钮
	private TextView m_tvTitle;

	private ListView m_listOneLevel;				// 列表
	private List<OneLevelChooseData> m_listRCD;		// 列表数据
	
	private OneLevelChooseListAdapter m_RRCLAdapter;
	private String selectId;
	
	private String m_strTitle;			// 标题
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_onelevelchoose);

		m_Context = this;
		
		initData();
		initControl();
		initList();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int nItem, long arg3) {
		// TODO Auto-generated method stub
		if((ListView)arg0 == m_listOneLevel){
			OneLevelChooseData rcd = m_listRCD.get(nItem);
			rcd.setIsChoose(true);
			m_RRCLAdapter.setSelectId(rcd.getID());
			m_RRCLAdapter.notifyDataSetChanged();
				
			setResult(HeadhunterPublic.RESULT_RECOMMENDREASON_OK, 
					getIntent().putExtra(HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_BACKDATA, rcd));
			
			finish();
		}
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		m_strTitle = "";
		
		if(null == m_listRCD){
			m_listRCD = new ArrayList<OneLevelChooseData>();
		}
		
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			// 获取标题
			m_strTitle = bundle.getString(HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_TITLE);
			// 获取一级数据
			ArrayList<OneLevelChooseData> serializable = (ArrayList<OneLevelChooseData>)bundle.getSerializable(
					HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_DATA);
			selectId = bundle.getString(
					HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_SELECTED);
			m_listRCD = serializable;
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.onelevelchoose_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		m_tvTitle = (TextView)findViewById(R.id.onelevelchoose_tv_title);
		m_tvTitle.setText(m_strTitle);
	}
	
	/**
	 * 初始化理由列表
	 */
	private void initList(){
		m_listOneLevel = (ListView)findViewById(R.id.onelevelchoose_lv_reason);
		m_listOneLevel.setOnItemClickListener(this);
		
		if(null != m_listRCD){
			m_RRCLAdapter = new OneLevelChooseListAdapter(m_Context, m_listRCD);
			m_RRCLAdapter.setSelectId(selectId);
			m_listOneLevel.setAdapter(m_RRCLAdapter);		
		}
	}
}

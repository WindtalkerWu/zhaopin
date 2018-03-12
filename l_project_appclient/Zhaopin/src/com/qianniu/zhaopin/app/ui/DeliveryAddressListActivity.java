package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.adapter.DeliveryAddressListAdapter;
import com.qianniu.zhaopin.app.bean.DeliveryAddressInfoData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView;
import com.qianniu.zhaopin.app.widget.PullToRefreshListView.OnRefreshListener;

/**
 * 收货地址列表
 * @author wuzy
 *
 */
public class DeliveryAddressListActivity extends BaseActivity implements
	OnItemClickListener, OnScrollListener, OnRefreshListener{

	private Context m_Context;
	private AppContext m_appContext;
	
	private ImageView m_btnBack;				// 返回按钮
	private ImageView m_btnAdd;				// 添加收货地址按钮
	
	
	// 收货地址列表相关
	private PullToRefreshListView m_lvCR;			// 
	private DeliveryAddressListAdapter m_daLA;		// 收货地址适配器
	private List<DeliveryAddressInfoData> m_lsDAFD;	// 收货地址信息数据
	
	private View m_vFoot;
	private TextView m_tvFoot;
	private ProgressBar m_progressFoot;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_deliveryaddresslist);
		
		m_Context = this;
		m_appContext = (AppContext) getApplication();
		
//		initData();
		// 初始化控件
		initControl();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int nItem, long arg3) {
		// TODO Auto-generated method stub
		// 点击头部无效
		if (nItem <= m_lvCR.getHeaderViewsCount() - 1) {
			return;
		}

		if (view == null)
			return;
		
		// 获取选中的item
		int nChooseItem = nItem - m_lvCR.getHeaderViewsCount();
		if(nChooseItem < 0){
			return;
		}
		
		DeliveryAddressInfoData daid = m_lsDAFD.get(nChooseItem);
		if(null == daid){
			return;
		}
		
		// 进入编辑收货地址界面
		startEditDeliveryAddressActivity(daid);	
	}

	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 返回按钮
		m_btnBack = (ImageView)findViewById(R.id.deliveryaddresslist_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 添加按钮
		m_btnAdd = (ImageView)findViewById(R.id.deliveryaddresslist_btn_add);
		m_btnAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAddDeliveryAddressActivity();
			}
		});
		// 积分记录列表
		initAddressListView();
	}
	
	/**
	 * 初始化地址列表
	 */
	private void initAddressListView(){
//		m_viewLoad = (View) findViewById(R.id.commom_loading_layout); 
		
		// 收藏的悬赏任务列表
		m_lvCR = (PullToRefreshListView)findViewById(R.id.deliveryaddresslist_lv_address);
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
		
		m_lsDAFD = new ArrayList<DeliveryAddressInfoData>();
		// test
		DeliveryAddressInfoData m1 = new DeliveryAddressInfoData();
		m1.setStrAddress("华盛顿特区宾夕法尼亚大道1600号");
		m1.setStrName("贝拉克·侯赛因·奥巴马");
		m1.setStrPostCode("20500");
		m1.setStrTel("202-456-1111");
		m_lsDAFD.add(m1);

		DeliveryAddressInfoData m2 = new DeliveryAddressInfoData();
		m2.setStrName("弗拉基米尔·弗拉基米罗维奇·普京");
		m2.setStrAddress("莫斯科克里姆林宫");
		m2.setStrPostCode("20500");
		m_lsDAFD.add(m2);
		
		DeliveryAddressInfoData m3 = new DeliveryAddressInfoData();
		m3.setStrName("桑拿省长");
		m3.setStrAddress("西长安街174号中南海新华门");
		m_lsDAFD.add(m3);
		 
		m_daLA = new DeliveryAddressListAdapter(m_appContext, m_lsDAFD);
		
		m_lvCR.addFooterView(m_vFoot);
		m_lvCR.setAdapter(m_daLA);
	}
	
	/**
	 * 进入新增收货地址界面
	 */
	private void startAddDeliveryAddressActivity(){
		// 数据传输
		Bundle bundle = new Bundle();
//		bundle.putSerializable(HeadhunterPublic.REWARD_DATATRANSFER_REWARDDATA, rld);
		bundle.putInt(HeadhunterPublic.DELIVERYADDRESS_DATATRANSFER_TYPE,
				HeadhunterPublic.DELIVERYADDRESS_TYPE_NEW);

		// 进入悬赏详细界面
		Intent intent = new Intent();
		intent.setClass(m_Context, DeliveryAddressActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent,
				HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 进入编辑收货地址界面
	 */
	private void startEditDeliveryAddressActivity(
			DeliveryAddressInfoData daid){
		// 数据传输
		Bundle bundle = new Bundle();
		bundle.putSerializable(HeadhunterPublic.DELIVERYADDRESS_DATATRANSFER_DATA, daid);
		bundle.putInt(HeadhunterPublic.DELIVERYADDRESS_DATATRANSFER_TYPE,
				HeadhunterPublic.DELIVERYADDRESS_TYPE_EDIT);

		// 进入悬赏详细界面
		Intent intent = new Intent();
		intent.setClass(m_Context, DeliveryAddressActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent,
				HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
}

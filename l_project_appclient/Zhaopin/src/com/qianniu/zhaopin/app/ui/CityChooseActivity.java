package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.CityChooseListAdater;
import com.qianniu.zhaopin.app.adapter.SideBar;
import com.qianniu.zhaopin.app.bean.CityChooseData;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.thp.AMapLocationForQN;
import com.qianniu.zhaopin.R;

/**
 * 城市选择
 * @author wuzy
 *
 */
public class CityChooseActivity extends BaseActivity implements
	OnItemClickListener{

	private Context m_Context;
	private Activity m_Activity;
	
	private ImageButton m_btnBack;			// 返回按钮
	private ImageButton m_btnSave;			// 确定保存按钮
	
	private EditText m_etSearch;			// 搜索输入栏
	private ImageView m_ivSearchClean;		// 清楚搜索输入栏
	
	private SideBar m_SideBar;				// 拼音选择Bar
	private TextView m_tvDialogText;		// 被选择的拼音TextView
	
	private WindowManager m_WindowManager;
	
	private ListView m_listCity;					// 城市列表
	private CityChooseListAdater m_cclAdater;		// 城市列表列表适配器
	private List<CityChooseData> m_listCCD;			// 城市数据List
	private RelativeLayout m_rlCity;				// 城市列表布局
	
//	private ListView m_listCitySearch;				// 城市搜索列表
//	private CityChooseListAdater m_cclSearchAdater;	// 城市搜索列表列表适配器
//	private List<CityChooseData> m_listSearchCCD;	// 城市数据List
	private RelativeLayout m_rlCitySearch;			// 城市搜索列表布局
	
	private ArrayList<CityChooseData> m_alCCDOld;	// 传递过来的已选中数据
	
	private TextView m_tvCityName;					// 定位当前城市
	private TextView m_tvGetCityMode;				// 定位当前城市的类型
	
	private int m_nType;		// 类型
	private int m_nNum;			// 可选择的个数
	private int m_nChooseNum;	// 已经选中的个数
	
	private int m_nMode;
	
	private String m_strLastId; // 单选时，上次选中的ID
	
	private boolean m_bIsLocationing;				// 是否正在进行定位标示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_citychoose);
		
		m_Context = this;
		m_Activity = this;
		m_nChooseNum = 0;
			
		init();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int nItem, long arg3) {
		// TODO Auto-generated method stub	
		CityChooseData ccd = m_listCCD.get(nItem);
		
		// 如果是单选的话
		if(HeadhunterPublic.CITYCHOOSE_TYPE_SINGLE == m_nType){
			if(null != m_strLastId){
				if(!m_strLastId.equals(ccd.getID())){
					setDataChoose(m_strLastId, false);
					m_nChooseNum--;
				}
			}
		}
			
		if(ccd.getIsChoose()){
			setDataChoose(ccd.getID(), false);
			m_nChooseNum--;
		}else{
			if(m_nNum <= m_nChooseNum){
				UIHelper.ToastMessage(CityChooseActivity.this,
						String.format(getString(R.string.msg_choosecity_exceedsthese), m_nNum));

				return;
			}
			
			setDataChoose(ccd.getID(), true);
			m_strLastId = ccd.getID();
			m_nChooseNum++;
		}
		
		m_cclAdater.notifyDataSetChanged();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void init(){
		// 初始化数据
		initData();
		
		initBtn();
		// 初始化城市列表相关
		initListView();
		
		initCity();
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_nType = bundle.getInt(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_TYPE);
			
			// 获取选中的城市数据取的时候
			m_alCCDOld = (ArrayList<CityChooseData>)bundle.getSerializable(
					HeadhunterPublic.CITYCHOOSE_DATATRANSFER_DATA);
			
			m_nNum = bundle.getInt(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_NUM);
		}else{
			// 默认为多选
			m_nType = HeadhunterPublic.CITYCHOOSE_TYPE_MULTIPLE;
		}
		
		// 设定可选城市个数
		if(HeadhunterPublic.CITYCHOOSE_TYPE_MULTIPLE == m_nType){
			if(m_nNum <= 1){
				m_nNum = HeadhunterPublic.CITYCHOOSE_NUM_MULTIPLE;
			}
		}else{
			m_nNum = HeadhunterPublic.CITYCHOOSE_NUM_SINGLE;
		}
	}
	
	/**
	 * 初始化城市列表相关
	 */
	private void initListView(){
		
		m_rlCity = (RelativeLayout)findViewById(R.id.citychoose_rl_city);
		m_listCity = (ListView)findViewById(R.id.citychoose_lv_city);
		m_SideBar = (SideBar)findViewById(R.id.citychoose_sideBar);
		
		m_rlCitySearch = (RelativeLayout)findViewById(R.id.citychoose_rl_citysearch);
//		m_listCitySearch = (ListView)findViewById(R.id.citychoose_lv_citysearch);
		m_rlCitySearch.setVisibility(View.GONE);
		
		// 获取热点城市数据
		addCityDataToList(DBUtils.GLOBALDATA_TYPE_HOTCITY);
		// 获取普通城市数据
		addCityDataToList(DBUtils.GLOBALDATA_TYPE_CITY);
		
		m_cclAdater = new CityChooseListAdater(m_Context, m_listCCD);
		m_listCity.setAdapter(m_cclAdater);
		m_listCity.setOnItemClickListener(this);
		m_SideBar.setListView(m_listCity); 
		
		m_tvDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
        m_tvDialogText.setVisibility(View.INVISIBLE);

        m_WindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        m_WindowManager.addView(m_tvDialogText, lp);
        m_SideBar.setTextView(m_tvDialogText);
	}
	
	/**
	 * 初始化按钮
	 */
	private void initBtn(){
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.citychoose_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 确定保存
		m_btnSave = (ImageButton)findViewById(R.id.citychoose_btn_save);
		m_btnSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishChooseCity();
			}
		});
		
		// 搜索输入栏
		m_etSearch = (EditText)findViewById(R.id.citychoose_et_search);
		m_etSearch.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		// 清楚搜索输入栏
		m_ivSearchClean = (ImageView)findViewById(R.id.citychoose_iv_clean);
		m_ivSearchClean.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchClean();
			}
		});		
	}

	/**
	 * 
	 */
	private void initCity(){
		// 定位当前城市
		m_tvCityName = (TextView)findViewById(R.id.citychoose_tv_cityname);	
		m_tvCityName.setText(getString(R.string.str_locationing));
		// 定位当前城市的类型
		m_tvGetCityMode = (TextView)findViewById(R.id.citychoose_tv_getcitymode);
		
		getLocation();
	}
	
	/**
	 * 从全局数据中取出城市数据，并且添加到list中
	 * @param nType 数据类型
	 */
	private void addCityDataToList(int nType){
		
		if(null == m_listCCD){
			m_listCCD = new ArrayList<CityChooseData>();
		}
		
		int nCityType = HeadhunterPublic.CITYCHOOSE_DATATYPE_COMMON;
		if(DBUtils.GLOBALDATA_TYPE_HOTCITY == nType){
			nCityType = HeadhunterPublic.CITYCHOOSE_DATATYPE_HOT;
		}
		
		try {
			List<GlobalDataTable> lvGDT = getCityData(nType);
			
			for(GlobalDataTable gdt : lvGDT){
				CityChooseData ccd = new CityChooseData();
				
				ccd.setID(gdt.getID());
				ccd.setName(gdt.getName());
				ccd.setNamePinYin(gdt.getNamePinYin());
				ccd.setType(nCityType);
				
				// 判断之前数据是否被选中
				if(null != m_alCCDOld){
					if(HeadhunterPublic.CITYCHOOSE_TYPE_SINGLE == m_nType){
						CityChooseData ccdOld = m_alCCDOld.get(m_alCCDOld.size() -1);
						if(null != ccdOld){
							if(ccd.getID().equals(ccdOld.getID())){
								ccd.setIsChoose(true);
								m_strLastId = ccd.getID();
								m_nChooseNum = 1;
							}
						}
					}else{
						for(CityChooseData ccdOld : m_alCCDOld){
							if(ccd.getID().equals(ccdOld.getID())){
								ccd.setIsChoose(true);
								break;
							}
						}
						
						m_nChooseNum = m_alCCDOld.size();
					}
				}
				
				m_listCCD.add(ccd);
			}
		} catch (AppException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * 从全局数据中取出城市数据
	 * @param nType
	 * @return
	 * @throws AppException
	 */
	private List<GlobalDataTable> getCityData(int nType) throws AppException {
		 List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();
		 
		 AppContext ac = (AppContext)getApplication(); 
		 lsGDT = ac.getTpyeDataNoSort(nType);
		 
		 return lsGDT;
	}
	
	/**
	 * 获取选中的城市
	 * @return
	 */
	private List<CityChooseData> getChooseCityData(){
		List<CityChooseData> lsCCD = new ArrayList<CityChooseData>();
		
		for(CityChooseData ccd : m_listCCD){
			if(ccd.getIsChoose()){
				boolean bIsExist = false;
				for(CityChooseData ccdTemp : lsCCD){
					if(ccd.getID().equals(ccdTemp.getID())){
						bIsExist = true;
						break;
					}
				}

				if(!bIsExist){
					lsCCD.add(ccd);
				}
			}
		}
		
		return lsCCD;
	}
	
	/**
	 * 结束
	 */
	private void finishChooseCity(){
		
		List<CityChooseData> lsCCD = getChooseCityData();
		
		if(m_nNum < lsCCD.size()){
			UIHelper.ToastMessage(this, String.format(getString(R.string.msg_choosecity_exceedsthese), m_nNum));
			
			return;
		}else if(0 == lsCCD.size()){
			
			// 弹出对话框
			AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
			dl.setTitle(getString(R.string.str_citychoose_title));
			dl.setMessage(getString(R.string.msg_choosecity_isempty));
			dl.setPositiveButton(getString(R.string.sure),
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							setResult(HeadhunterPublic.RESULT_CHOOSECITY_CANCEL);
							dialog.dismiss();
							m_WindowManager.removeView(m_tvDialogText);
							finish();
						}
				
				});
			dl.setNegativeButton(getString(R.string.cancle),
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
				
				});
			dl.show();	
		}else{
			ArrayList<CityChooseData> alsCCD = new ArrayList<CityChooseData>();
			for(CityChooseData ccd : lsCCD){
				alsCCD.add(ccd);
			}

			// 把选中的数据传递回去			
			setResult(HeadhunterPublic.RESULT_CHOOSECITY_OK, 
					getIntent().putExtra(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_DATA, alsCCD));
			
			m_WindowManager.removeView(m_tvDialogText);
			finish();			
		}
	}
	
	/**
	 * 选择/取消选择城市
	 * @param strID
	 * @param bIsChoose
	 */
	private void setDataChoose(String strID, boolean bIsChoose){
		if(null == strID){
			return;
		}
		
		if(strID.isEmpty()){
			return;
		}
		
		for(CityChooseData ccd : m_listCCD){
			if(ccd.getID().equals(strID)){
				ccd.setIsChoose(bIsChoose);
			}
		}
	}
	
	private void searchClean(){

		m_etSearch.setText("");
		
		m_rlCity.setVisibility(View.VISIBLE);
		m_rlCitySearch.setVisibility(View.GONE);
	}
	
//	private void searchCityData(){
//		String strSearch = m_etSearch.getText().toString();
//		
//		m_rlCity.setVisibility(View.GONE);
//		m_rlCitySearch.setVisibility(View.GONE);
//	}
	
	/**
	 * 消息处理HeadhunterPublic
	 */
	private Handler m_Handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HeadhunterPublic.MSG_GETLOCATION_SUCCESS: 
				{
					m_bIsLocationing = false;
					String[] strRet = (String[])msg.obj;
					if(null != strRet && (strRet.length == 4)){
						String strCityName = strRet[0];
						if(null != strCityName && !strCityName.isEmpty()){
							if(null != m_tvCityName){
								m_tvCityName.setText(strCityName);
							}
							
							m_tvGetCityMode.setVisibility(View.VISIBLE);
//							if(null != strRet[3] && null != m_tvGetCityMode){
//								m_tvGetCityMode.setText(strRet[3]);
//								m_tvGetCityMode.setVisibility(View.VISIBLE);
//							}
						}else{
							m_tvCityName.setText(getString(R.string.str_location_fail));
						}
					}
				}
				break;
			case HeadhunterPublic.MSG_GETLOCATION_FAIL: 
				{
					m_bIsLocationing = false;
					if(null != m_tvCityName){
						m_tvCityName.setText(getString(R.string.str_location_fail));
					}
				}
				break;
			default:
				break;
			}
		}
	};
	
	private AMapLocationForQN m_AMapLocation;
	
	/**
	 * 定位
	 */
	private void getLocation(){
		m_AMapLocation = new AMapLocationForQN(m_Activity, m_Handler);
		if(null != m_AMapLocation){
			m_AMapLocation.startLocation();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(null != m_AMapLocation){
			m_bIsLocationing = m_AMapLocation.getLocationStatus();
			if(m_bIsLocationing){
				// 停止定位
				m_AMapLocation.stopLocation();				
			}

		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(m_bIsLocationing){
			getLocation();
		}
	}
	
}

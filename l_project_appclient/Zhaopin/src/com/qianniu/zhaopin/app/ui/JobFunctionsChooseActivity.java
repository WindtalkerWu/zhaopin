package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.TwoLevelChooseListAdapter;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.app.bean.ThreeLevelChooseData;
import com.qianniu.zhaopin.app.bean.TwoLevelChooseData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.database.DBUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ExpandableListView.OnChildClickListener;

public class JobFunctionsChooseActivity extends BaseActivity {

	private Context m_Context;
	
	private ImageButton m_btnBack;			// 返回按钮
	
	private ExpandableListView m_listJF;				// 职能分类列表
	private TwoLevelChooseListAdapter m_lsaJF;			// 职能分类列表适配器
	
	private List<TwoLevelChooseData> m_lsTCD;			// 职能分类数据List
	
//	private ThreeLevelChooseData m_ThreeChooseData = null;	// 传递过来的已选中数据
//	private TwoLevelChooseData m_TwoChooseData = null;
	private OneLevelChooseData m_OneChooseData = null;
	
	private String m_strBottomChooseId;					// 职能分类最低层被选中ID
	private String m_strThreeChooseId;
	private String m_strTwoChooseId;

	private boolean m_bSubExpand; 		// 是否展开
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jobfunctionchoose);
		
		m_Context = this;
		
		initData();
		initControl();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if( HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode){
			switch(resultCode){
			case HeadhunterPublic.RESULT_JOBFUNCTIONSBOTTOM_OK:
				{
					// 获取选中的推荐理由数据
					m_OneChooseData  =  (OneLevelChooseData)data.getSerializableExtra(
							HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_BACKDATA);
					
					finishChooseJobFunctions();
				}
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			// 获取被选择的数据
//			m_ThreeChooseData = (ThreeLevelChooseData)bundle.getSerializable(
//					HeadhunterPublic.JOBFUNCTIONCHOOSE_DATA);
			
			m_strBottomChooseId = bundle.getString(
					HeadhunterPublic.JOBFUNCTIONCHOOSE_DATA);
			if(null!= m_strBottomChooseId && !m_strBottomChooseId.isEmpty()){
				m_OneChooseData = getBottomChooseData(m_strBottomChooseId);
				m_strTwoChooseId = getTwoCHooseDataID(m_OneChooseData);
				m_strThreeChooseId = getThreeCHooseDataID(m_strTwoChooseId);
			}
			
		}
	}
	
	/**
	 * 初始化控件
	 */
	private void initControl(){
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.jobfunctionchoose_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 初始化列表
		InitExpandableListView();
	}
	
	/**
	 * 初始化列表
	 */
	private void InitExpandableListView(){
		m_listJF=  (ExpandableListView)findViewById(R.id.jobfunctionchoose_elv_industrychoose);

		if(getJobFunctionsData()){
			m_lsaJF = new TwoLevelChooseListAdapter(m_Context, m_lsTCD,
					TwoLevelChooseListAdapter.TYPE_JOBFUNCTION);
//			m_lsaJF.setSelectIds(selecteIds);
			m_listJF.setAdapter(m_lsaJF);
			
			for(int i = 0; i < m_listJF.getCount(); i++){
				if(i < m_lsTCD.size()){
					if(m_lsTCD.get(i).getSubExpand()){
						m_listJF.expandGroup(i);
					}
				}
			}
		}
		
		m_listJF.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				if(null != m_lsTCD){
					TwoLevelChooseData tlcd = m_lsTCD.get(groupPosition);
					if(null == tlcd){
						return false;
					}
					
					OneLevelChooseData olcd = tlcd.getSubList().get(childPosition);
					if(null == olcd){
						return false;
					}
					
//					if(null == m_ThreeChooseData){
//						m_ThreeChooseData = new ThreeLevelChooseData();
//					}
//					m_ThreeChooseData.setHavingColor(tlcd.getHavingColor());
//					m_ThreeChooseData.setID(tlcd.getID());
//					m_ThreeChooseData.setHavingSub(true);
//					m_ThreeChooseData.setName(tlcd.getName());
//					m_ThreeChooseData.setNamePinYin(tlcd.getNamePinYin());
//					m_ThreeChooseData.setSubExpand(false);
//					// 
//					if(null == m_TwoChooseData){
//						m_TwoChooseData = new TwoLevelChooseData();
//					}
//					m_TwoChooseData.setID(olcd.getID());
//					m_TwoChooseData.setName(olcd.getName());
//					m_TwoChooseData.setNamePinYin(olcd.getNamePinYin());
//					m_TwoChooseData.setHavingSub(true);
//					m_TwoChooseData.setHavingColor(false);
//					m_TwoChooseData.setSubExpand(false);
					
					jobFunctionsChooseForBottom(olcd.getID());
				}

				return true;
			}
			
		});		
	}
	
	/**
	 * 从全局数据中获取职能分类数据
	 */
	private boolean getJobFunctionsData(){
		boolean bRet = false;
		
		 List<GlobalDataTable> lsParentGDT = new ArrayList<GlobalDataTable>();
		 
		 AppContext ac = (AppContext)getApplication(); 
		 try {
			lsParentGDT = ac.getParentClassDataNoSort(DBUtils.GLOBALDATA_TYPE_JOBFUNCTION);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		if(null != lsParentGDT){
			if(null == m_lsTCD){
				m_lsTCD = new ArrayList<TwoLevelChooseData>();
			}
			
			for(GlobalDataTable gdt : lsParentGDT){
				TwoLevelChooseData icd = new TwoLevelChooseData();
				icd.setID(gdt.getID());
				icd.setName(gdt.getName());
				icd.setHavingSub(gdt.getHavingSubClass());
				icd.setHavingColor(true);
				
				setSubExpand(false);
				// 判断之前数据是否被选中
//				if(null != m_ThreeChooseData){
//					if(icd.getID().equals(m_ThreeChooseData.getID())){
//						setSubExpand(true);
//					}
//				}
				if(null != m_strThreeChooseId && !m_strThreeChooseId.isEmpty()){
					if(icd.getID().equals(m_strThreeChooseId)){
						setSubExpand(true);
					}
				}
				
				if(icd.getHavingSub()){
					icd.setSubList(getTwoLevelData(icd.getID(), icd.getName()));
				}
				
				icd.setSubExpand(getSubExpand());
				
				m_lsTCD.add(icd);
			}
			
			if(m_lsTCD.size() > 0){
				bRet = true;
			}
		}
		
		return bRet;
	}
	
	/**
	 * 获取子行业
	 * @param strParentID
	 * @param strParentName
	 * @return
	 */
	private List<OneLevelChooseData> getTwoLevelData(String strParentID, String strParentName){
		List<OneLevelChooseData> lsICSD = new ArrayList<OneLevelChooseData>();
		
		AppContext ac = (AppContext)getApplication(); 
		try {
			List<GlobalDataTable> lsGDT = ac.getSubClassDataNoSort(
					DBUtils.GLOBALDATA_TYPE_JOBFUNCTION, strParentID, true);
			
//			TwoLevelChooseData two = null;
			// 判断之前数据是否被选中
//			if(null != m_ThreeChooseData){
//				if(null != m_ThreeChooseData.getSubList()){
//					List<TwoLevelChooseData> lsTwo = m_ThreeChooseData.getSubList();
//					if(null != lsTwo && lsTwo.size() > 0){
//						two = lsTwo.get(0);
//						if(null != two){
//							if(null != two.getSubList()){
//								List<OneLevelChooseData> lsOne = two.getSubList();
//								if(null != lsOne && lsOne.size() > 0){
//									OneLevelChooseData one = lsOne.get(0);
//									if(null != one){
//										m_strBottomChooseId = one.getID();
//									}
//								}
//							}
//						}
//					}
//				}
//			}
			
			for(GlobalDataTable gdt : lsGDT){
				OneLevelChooseData icsd = new OneLevelChooseData();
				icsd.setID(gdt.getID());
				icsd.setName(gdt.getName());
				icsd.setParentID(gdt.getParentID());
				icsd.setParentName(strParentName);
				
//				if(null != two){
//					if(icsd.getID().equals(two.getID())){
//						icsd.setIsChoose(true);
//					}
//				}
				if(null != m_strTwoChooseId && !m_strTwoChooseId.isEmpty()){
					if(icsd.getID().equals(m_strTwoChooseId)){
						icsd.setIsChoose(true);
					}
				}

				lsICSD.add(icsd);
			}
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lsICSD;
	}
	
	/**
	 * 获取是否要展开
	 * @return 是否要展开
	 */
	private boolean getSubExpand(){
		return this.m_bSubExpand;
	}
	
	/**
	 * 设置是否要展开
	 * @param b 是否要展开
	 */
	private void setSubExpand(boolean b){
		this.m_bSubExpand = b;
	}
	
	private void jobFunctionsChooseForBottom(String strTwoLevelId){
		// 数据传输
		Bundle bundle = new Bundle();
		
		bundle.putString(HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_TITLE, 
				getString(R.string.str_jobfunctionchoose_title));
		bundle.putSerializable(HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_DATA,
				getBottomLevelData(strTwoLevelId));
		bundle.putString(HeadhunterPublic.ONELEVELCHOOSE_DATATRANSFER_SELECTED, m_strBottomChooseId);
		
        Intent intent = new Intent();
        intent.setClass(m_Context, JobFunctionsChooseForBottomActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 获取最低层数据
	 * @param strTwoLevelId
	 * @return
	 */
	private ArrayList<OneLevelChooseData> getBottomLevelData(String strTwoLevelId){
		ArrayList<OneLevelChooseData> alsOLCD = new ArrayList<OneLevelChooseData>();
		
		try {
			List<GlobalDataTable> lvGDT = getJobFunctionsBottomData(strTwoLevelId);
			
			for(GlobalDataTable gdt : lvGDT){
				OneLevelChooseData olcd = new OneLevelChooseData();
				
				olcd.setID(gdt.getID());
				olcd.setName(gdt.getName());
				olcd.setNamePinYin(gdt.getNamePinYin());
				olcd.setParentID(gdt.getParentID());
				
				alsOLCD.add(olcd);
			}
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return alsOLCD;
	}
	
	/**
	 * 从全局数据中取出职能分类最低层数据
	 * @param strTwoLevelId
	 * @return
	 * @throws AppException
	 */
	private List<GlobalDataTable> getJobFunctionsBottomData(String strTwoLevelId) throws AppException {
		 List<GlobalDataTable> lsGDT = new ArrayList<GlobalDataTable>();
		 
		 AppContext ac = (AppContext)getApplication(); 
		 lsGDT = ac.getSubClassDataNoSort(DBUtils.GLOBALDATA_TYPE_JOBFUNCTION, strTwoLevelId, false);
		 
		 return lsGDT;
	}
	
	/**
	 * 根据id获取最低层数据的信息
	 * @param strBottomId
	 * @return
	 * @throws AppException
	 */
	private GlobalDataTable getJobFunctionsBottomDataInfo(String strBottomId) throws AppException {
		 GlobalDataTable gdt = new GlobalDataTable();
		 
		 AppContext ac = (AppContext)getApplication(); 
		 gdt = ac.getTypeBotoomDataById(DBUtils.GLOBALDATA_TYPE_JOBFUNCTION, strBottomId);
		 
		 return gdt;
	}
	
	/**
	 * @param strBottomId
	 * @return
	 * @throws AppException
	 */
	private String getJobFunctionsTopDataId(String strId) throws AppException {
		 String strTmep = "";
		 
		 AppContext ac = (AppContext)getApplication(); 
		 strTmep = ac.getParentId(strId, DBUtils.GLOBALDATA_TYPE_JOBFUNCTION, true);
		 
		 return strTmep;
	}
	
	/**
	 * 结束选择
	 */
	private void finishChooseJobFunctions(){
//		if(null == m_strBottomChooseId ||
//				m_strBottomChooseId.isEmpty()){
//			return;
//		}
		
		if(null == m_OneChooseData){
			return;
		}
		
//		if(null == m_TwoChooseData){
//			return;
//		}
//		
//		if(null == m_ThreeChooseData){
//			return;
//		}
//		
//		ArrayList<OneLevelChooseData> alOLCD = new ArrayList<OneLevelChooseData>();
//		alOLCD.add(m_OneChooseData);
//		m_TwoChooseData.setSubList(alOLCD);
//		
//		ArrayList<TwoLevelChooseData> alTLCD = new ArrayList<TwoLevelChooseData>();
//		alTLCD.add(m_TwoChooseData);
//		m_ThreeChooseData.setSubList(alTLCD);
//		m_ThreeChooseData.setSubExpand(true);
//
//		setResult(HeadhunterPublic.RESULT_JOBFUNCTIONS_OK, 
//				getIntent().putExtra(HeadhunterPublic.JOBFUNCTIONCHOOSE_DATA, m_ThreeChooseData));
		
		setResult(HeadhunterPublic.RESULT_JOBFUNCTIONS_OK, 
				getIntent().putExtra(HeadhunterPublic.JOBFUNCTIONCHOOSE_DATA, m_OneChooseData));
		
		finish();	
	}
	
	/**
	 * 获取被选中的职能的最低层数据
	 * @param strBottomChooseDataID
	 * @return
	 */
	private OneLevelChooseData getBottomChooseData(String strBottomChooseDataID){
		OneLevelChooseData olcd = new OneLevelChooseData();
		
		if(null != strBottomChooseDataID && !strBottomChooseDataID.isEmpty()){
			try {
				GlobalDataTable odt = getJobFunctionsBottomDataInfo(strBottomChooseDataID);
				if(null != odt){
					olcd.setID(odt.getID());
					olcd.setName(odt.getName());
					olcd.setNamePinYin(odt.getNamePinYin());
					olcd.setParentID(odt.getParentID());
					olcd.setIsChoose(true);
				}
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		return olcd;
	}
	
	/**
	 * 获取被选中的职能第二层数据id
	 * @param strBottomChooseData
	 */
	private String getTwoCHooseDataID(OneLevelChooseData strBottomChooseData){
		String strTwoChooseDataId = "";
		
		if(null != strBottomChooseData){
			strTwoChooseDataId = strBottomChooseData.getParentID();
		}
		
		return strTwoChooseDataId;
	}
	
	/**
	 * 获取被选择的职能第三层数据id
	 * @param strTwoChooseDataId
	 * @return
	 */
	private String getThreeCHooseDataID(String strTwoChooseDataId){
		String strThreeChooseDataId = "";
		
		if(null != strTwoChooseDataId){
			try {
				strThreeChooseDataId = getJobFunctionsTopDataId(strTwoChooseDataId);
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return strThreeChooseDataId;
	}
}

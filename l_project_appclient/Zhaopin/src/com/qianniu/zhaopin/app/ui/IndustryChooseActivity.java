package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.TwoLevelChooseListAdapter;
import com.qianniu.zhaopin.app.bean.GlobalDataTable;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.app.bean.TwoLevelChooseData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;

public class IndustryChooseActivity extends BaseActivity {

	private Context m_Context;
	
	private ImageButton m_btnBack;			// 返回按钮
	private ImageButton m_btnSave;			// 确定保存按钮
	
	private ExpandableListView m_listIndustry;			// 行业列表
	private TwoLevelChooseListAdapter m_lsaIndustry;	// 行业列表适配器
	
	private List<TwoLevelChooseData> m_lsICD;			// 行业数据List
	
	private ArrayList<OneLevelChooseData> m_alsICSD;	// 传递过来的已选中数据
	
	private int m_nType;		// 类型
	private int m_nNum;			// 可选择的个数
	private int m_nChooseNum;
	
	private String m_strLastParentId;
	private String m_strLastId;
	
	private static final int MSG_INDUSTRYCHOOSE_EXCEEDSET = 1;
	
	private boolean m_bSubExpand; 		// 是否展开
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_industrychoose);
		
		m_Context = this;
		m_nChooseNum = 0;
		
		init();
	}

	/**
	 *初始化 
	 */
	private void init(){
		initData();
		initBtn();
		InitExpandableListView();
	}
	
	/**
	 * 初始化数据
	 */
	private void initData(){
		// 获取Activity传递过来的数据
		Bundle bundle = getIntent().getExtras();
		if(null != bundle){
			m_nType = bundle.getInt(HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_TYPE);
			
			// 获取选中的行业数据取的时候
			m_alsICSD = (ArrayList<OneLevelChooseData>)bundle.getSerializable(
					HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_DATA);
			
			m_nNum = bundle.getInt(HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_NUM);
		}else{
			// 默认为多选
			m_nType = HeadhunterPublic.INDUSTRYCHOOSE_TYPE_MULTIPLE;
		}
		
		// 设定可选行业个数
		if(HeadhunterPublic.INDUSTRYCHOOSE_TYPE_MULTIPLE == m_nType){
			if(m_nNum <= 1){
				m_nNum = HeadhunterPublic.INDUSTRYCHOOSE_NUM_MULTIPLE;
			}
		}else{
			m_nNum = HeadhunterPublic.INDUSTRYCHOOSE_NUM_SINGLE;
		}
	}
	
	/**
	 * 初始化按钮
	 */
	private void initBtn(){
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.industrychoose_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		// 确定保存
		m_btnSave = (ImageButton)findViewById(R.id.industrychoose_btn_save);
		m_btnSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishChooseIndustry();
			}
		});
	}
	
	/**
	 * 初始化列表
	 */
	private void InitExpandableListView(){
		m_listIndustry =  (ExpandableListView)findViewById(R.id.industrychoose_elv_industrychoose);

		if(getIndustryData()){
			m_lsaIndustry = new TwoLevelChooseListAdapter(m_Context, m_lsICD, 
					TwoLevelChooseListAdapter.TYPE_INDUSTRY);
//			m_lsaIndustry.setSelectIds(selecteIds);
			m_listIndustry.setAdapter(m_lsaIndustry);
			
			for(int i = 0; i < m_listIndustry.getCount(); i++){
				if(i < m_lsICD.size()){
					if(m_lsICD.get(i).getSubExpand()){
						m_listIndustry.expandGroup(i);
					}
				}
			}
		}
		
		m_listIndustry.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				if(null != m_lsICD){
					TwoLevelChooseData tlcd = m_lsICD.get(groupPosition);
					if(null == tlcd){
						return false;
					}
					
					OneLevelChooseData olcd = tlcd.getSubList().get(childPosition);
					if(null == olcd){
						return false;
					}
					
					// 单选
					if(HeadhunterPublic.INDUSTRYCHOOSE_TYPE_SINGLE == m_nType){
						if(null != m_strLastId && null != m_strLastParentId){
							if(!m_strLastId.equals(olcd.getID()) ||
								!m_strLastParentId.equals(tlcd.getID())){
									setDataChoose(m_strLastParentId, m_strLastId, false);
									m_nChooseNum--;
							}
						}
					}

					if(olcd.getIsChoose()){
						olcd.setIsChoose(false);
						m_nChooseNum--;
					}else{
						if(m_nNum <= m_nChooseNum){
							UIHelper.ToastMessage(IndustryChooseActivity.this,
									String.format(getString(R.string.msg_chooseindustry_exceedsthese), m_nNum));

							return false;
						}
						
						olcd.setIsChoose(true);
						m_nChooseNum++;

						if(HeadhunterPublic.INDUSTRYCHOOSE_TYPE_SINGLE == m_nType){
							m_strLastParentId = tlcd.getID();
							m_strLastId = olcd.getID();
						}
					}
					
					if(null != m_lsaIndustry){
						m_lsaIndustry.notifyDataSetChanged();
					}
				}

				return true;
			}
			
		});
	}
	
	/**
	 * 从全局数据中获取行业数据
	 */
	private boolean getIndustryData(){
		boolean bRet = false;
		
		 List<GlobalDataTable> lsParentGDT = new ArrayList<GlobalDataTable>();
		 
		 AppContext ac = (AppContext)getApplication(); 
		 try {
			lsParentGDT = ac.getParentClassDataNoSort(DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		if(null != lsParentGDT){
			if(null == m_lsICD){
				m_lsICD = new ArrayList<TwoLevelChooseData>();
			}
			
			for(GlobalDataTable gdt : lsParentGDT){
				TwoLevelChooseData icd = new TwoLevelChooseData();
				icd.setID(gdt.getID());
				icd.setName(gdt.getName());
				icd.setHavingSub(gdt.getHavingSubClass());
				icd.setHavingColor(true);
				
				setSubExpand(false);
				
				if(icd.getHavingSub()){
					icd.setSubList(getIndustrySubData(icd.getID(), icd.getName()));
				}
				
				icd.setSubExpand(getSubExpand());
				
				m_lsICD.add(icd);
			}
			
			if(m_lsICD.size() > 0){
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
	private List<OneLevelChooseData> getIndustrySubData(String strParentID, String strParentName){
		List<OneLevelChooseData> lsICSD = new ArrayList<OneLevelChooseData>();
		
		AppContext ac = (AppContext)getApplication(); 
		try {
			List<GlobalDataTable> lsGDT = ac.getSubClassDataNoSort(DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY, strParentID);
			
			for(GlobalDataTable gdt : lsGDT){
				OneLevelChooseData icsd = new OneLevelChooseData();
				icsd.setID(gdt.getID());
				icsd.setName(gdt.getName());
				icsd.setParentID(gdt.getParentID());
				icsd.setParentName(strParentName);
				
				// 判断之前数据是否被选中
				if(null != m_alsICSD){
					// 单选
					if(HeadhunterPublic.INDUSTRYCHOOSE_TYPE_SINGLE == m_nType){
						OneLevelChooseData ocdOld = m_alsICSD.get(m_alsICSD.size()-1);
						if(null != ocdOld){
							if(icsd.getID().equals(ocdOld.getID())){
								icsd.setIsChoose(true);
								m_strLastParentId = icsd.getParentID();
								m_strLastId = icsd.getID();
								m_nChooseNum = 1;
								setSubExpand(true);
							}
						}
					}else{
						for(OneLevelChooseData ocdOld : m_alsICSD){
							if(icsd.getID().equals(ocdOld.getID())){
								icsd.setIsChoose(true);
								m_nChooseNum ++;
								setSubExpand(true);
								break;
							}
						}
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
	 * 结束选择
	 */
	private void finishChooseIndustry(){
		List<OneLevelChooseData> lsICSD = getChooseIndustryData();
		
		if(m_nNum < lsICSD.size()){
			UIHelper.ToastMessage(this, String.format(getString(R.string.msg_chooseindustry_exceedsthese), m_nNum));
			
			return;
		}else if(0 == lsICSD.size()){
			
			// 弹出对话框
			AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
			dl.setTitle(getString(R.string.str_industrychoose_title));
			dl.setMessage(getString(R.string.msg_chooseindustry_isempty));
			dl.setPositiveButton(getString(R.string.sure),
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							setResult(HeadhunterPublic.RESULT_INDUSTRYCITY_CANCEL);
							dialog.dismiss();
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
			// 传递选择的行业数据
			ArrayList<OneLevelChooseData> alsICB = new ArrayList<OneLevelChooseData>();
			for(OneLevelChooseData icsd :  lsICSD){
				OneLevelChooseData idb = new OneLevelChooseData();
				idb.setID(icsd.getID());
				idb.setName(icsd.getName());
				idb.setParentID(icsd.getParentID());
				idb.setParentName(icsd.getParentName());
				alsICB.add(idb);
			}
			
			Intent intent = getIntent();
			// 传递选中的子行业
			intent.putExtra(HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_DATA, alsICB);

			// 获取选中的父行业
			String[] strParentData = getChooseIndustryParentData();
			// 传递选中的父行业
			intent.putExtra(HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_PARENTINDUSTRYCHOOSE, strParentData);
			
			setResult(HeadhunterPublic.RESULT_INDUSTRYCITY_OK, intent);
			
			finish();			
		}
	}
	
	/**
	 * 获取已经选择的数据
	 * @return
	 */
	private List<OneLevelChooseData> getChooseIndustryData(){
		List<OneLevelChooseData> lsICSD = new ArrayList<OneLevelChooseData>();
		
		for(TwoLevelChooseData icd : m_lsICD){
			for(OneLevelChooseData icsd : icd.getSubList()){
				if(icsd.getIsChoose()){
					lsICSD.add(icsd);
				}
			}
		}
		
		return lsICSD;
	}
	
	/**
	 * 获取已经选择的父行业id
	 * @return
	 */
	private String[] getChooseIndustryParentData(){
		String[] strTemp = new String[m_nNum];
		int m = 0;
		
		for(TwoLevelChooseData icd : m_lsICD){
			for(OneLevelChooseData icsd : icd.getSubList()){
				if(icsd.getIsChoose()){
					strTemp[m] = icd.getID();
					m++;
					break;
				}
			}
		}
		
		String[] strParentData = null;
		
		if(m>0){
			strParentData = new String[m];
			for(int n = 0; n < m; n++){
				strParentData[n] = strTemp[n];
			}
		}
		
		return strParentData;
	}
	
	/**
	 * 选中行业/取消选中行业
	 * @param strParentId
	 * @param strID
	 * @param bIsChoose
	 */
	private void setDataChoose(String strParentID, String strID, boolean bIsChoose){
		if(null == strParentID){
			return;
		}
		
		if(strParentID.isEmpty()){
			return;
		}
		
		if(null == strID){
			return;
		}
		
		if(strID.isEmpty()){
			return;
		}
		
		if(null == m_lsICD){
			return;
		}

		for(TwoLevelChooseData tlcd : m_lsICD){
			if(tlcd.getID().equals(strParentID)){
				for(OneLevelChooseData olcd: tlcd.getSubList()){
					if(olcd.getID().equals(strID)){
						olcd.setIsChoose(bIsChoose);
						return;
					}
				}
			}
		}
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
}

package com.qianniu.zhaopin.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.adapter.HistroyListAdapter;
import com.qianniu.zhaopin.app.bean.CityChooseData;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.app.bean.OneLevelData;
import com.qianniu.zhaopin.app.bean.RewardFilterCondition;
import com.qianniu.zhaopin.app.bean.RewardHistroyData;
import com.qianniu.zhaopin.app.bean.TwoLevelChooseData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.widget.HotLabelFlowLayout;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.UmShare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 悬赏任务搜索界面
 * @author wuzy
 *
 */
public class RewardSearchActivity extends BaseActivity implements
	OnItemClickListener {
	private Context m_Context;
	
	private RelativeLayout m_rlArea;		// 地区
	private RelativeLayout m_rlIndustry;	// 行业
	private RelativeLayout m_rlConfirm;		// 确定
	private RelativeLayout m_rlHistory;		// 历史搜索
	
	private TextView m_tvAreaChoose;		// 已选中的城市
	private TextView m_tvIndustryChoose;    // 已选中的行业
	
	private ImageView m_imgHistory;
	private ImageView m_imgClean;			// 
	
	private ImageButton m_btnBack;			// 返回按钮
	private EditText m_search_et;
	
	private HotLabelFlowLayout labelFlowLayout;
	
	private ListView m_listHistory;					// 历史列表
	private HistroyListAdapter m_adapterHistory;	// 历史列表适配器
	private List<RewardHistroyData> m_lsRHD;		// 历史数据List
	
	private ArrayList<CityChooseData> m_alsCCD;				// 选中的地区数据
	private ArrayList<OneLevelChooseData> m_alsICSD;		// 选中的行业数据
	private String[]  m_strParentIndustry;					// 选中的父行业数据
	
	private List<RewardFilterCondition> m_lsRFC;
	
	private boolean m_bNoHistroy;					// 是否有搜索历史标志 true: 没有/false: 有

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rewardsearch);
		
		m_Context = this;
		m_bNoHistroy = true;
		
		init();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int nItem, long arg3) {
		// TODO Auto-generated method stub
		if(null != m_lsRFC && !m_bNoHistroy){
			// 友盟统计
			UmShare.UmStatistics(m_Context, "RewardSearch_History");
			
			RewardFilterCondition rfc = m_lsRFC.get(nItem);
			
			// 把搜索历史保存到数据库中
			SaveSearchHistory(rfc);
			
			// 过滤条件数据传输			
			setResult(HeadhunterPublic.RESULT_REWARDSEARCH_OK, 
					getIntent().putExtra(HeadhunterPublic.REWARDSEARCH_DATATRANSFER_FILTERS, rfc));
			
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(HeadhunterPublic.RESULT_ACTIVITY_CODE == requestCode){
			switch(resultCode){
			case HeadhunterPublic.RESULT_CHOOSECITY_OK:
				{
					// 获取选中的城市数据取的时候
					m_alsCCD = (ArrayList<CityChooseData>)data.getSerializableExtra(
							HeadhunterPublic.CITYCHOOSE_DATATRANSFER_DATA);
					
					if(null != m_alsCCD){
						if(m_alsCCD.size() > 0){
							if(null != m_tvAreaChoose){
								String strTemp = "";
								boolean bFirst = true;
								for(CityChooseData ccd : m_alsCCD){
									if(bFirst){
										strTemp = ccd.getName();
										bFirst = false;
									}else{
										strTemp = strTemp + ", " + ccd.getName();
									}
								}
								m_tvAreaChoose.setText(strTemp);
							}
						}
					}
				}
				break;
			case HeadhunterPublic.RESULT_INDUSTRYCITY_OK:
				{
					// 获取选中的子行业数据
					m_alsICSD  =  (ArrayList<OneLevelChooseData>)data.getSerializableExtra(
							HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_DATA);
					if(null != m_alsICSD){
						if(m_alsICSD.size() > 0){
							if(null != m_tvIndustryChoose){
								String strTemp = "";
								boolean bFirst = true;
								for(OneLevelChooseData ics : m_alsICSD){
									if(bFirst){
										strTemp = ics.getName();
										bFirst = false;
									}else{
										strTemp = strTemp + ", " + ics.getName();
									}
								}
								m_tvIndustryChoose.setText(strTemp);
							}
						}
					}
					
					// 获取选中的 父行业数据
					m_strParentIndustry = data.getStringArrayExtra(
							HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_PARENTINDUSTRYCHOOSE);
				}
				break;
			case HeadhunterPublic.RESULT_LABEL_OK:
				ArrayList<OneLevelData> labels = (ArrayList<OneLevelData>)data.getSerializableExtra("selectLabel");
				labelFlowLayout.setSelectedLabels(labels);
				labelFlowLayout.resetLabels();
				labelFlowLayout.invalidate();
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 友盟统计
		UmShare.UmResume(m_Context);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 友盟统计
		UmShare.UmPause(m_Context);
	}

	private void init(){
		initLabelFlowLayout();
		intControl();
		InitListView();
	
		// 友盟统计
		UmShare.UmsetDebugMode(m_Context);
	}
	
	private void initLabelFlowLayout() {
		labelFlowLayout = (HotLabelFlowLayout) findViewById(R.id.rewardsearch_label_flow_bg);
		labelFlowLayout.initData(this, HotLabelFlowLayout.PARTLABEL, true);
	}
	
	public void changeLabel(OneLevelData oneLevelData) {
		String oldLabels = m_search_et.getText().toString();
		if (TextUtils.isEmpty(oldLabels)) {
			m_search_et.setText(oneLevelData.getLabel());
		} else {
			m_search_et.setText(oldLabels + " " + oneLevelData.getLabel());
		}
		m_search_et.setSelection(m_search_et.getText().length());
	}
	
	public void changeLabels(List<OneLevelData> oneLevelDatas) {
		m_search_et.setText("");
		if (oneLevelDatas != null && oneLevelDatas.size() > 0) {
			for (int i = 0; i < oneLevelDatas.size(); i++) {
				changeLabel(oneLevelDatas.get(i));
			}
		}
	}
	
	public void startRewardSearchLabelActivity() {
		Intent intent = new Intent(RewardSearchActivity.this, RewardSearchLabelActivity.class);
		intent.putExtra("selectLabels", (ArrayList<OneLevelData>)labelFlowLayout.getSelectedLabels());
		startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 初始化控件
	 */
	private void intControl(){
		// 返回
		m_btnBack = (ImageButton)findViewById(R.id.rewardsearch_btn_goback);
		m_btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// 地区选择
		m_rlArea = (RelativeLayout) findViewById(R.id.rewardsearch_rl_area);
		m_rlArea.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计
				UmShare.UmStatistics(m_Context, "RewardSearch_Area");
				chooseCity();
			}
			
		});
		
		// 地区选择
		m_rlIndustry = (RelativeLayout) findViewById(R.id.rewardsearch_rl_industry);
		m_rlIndustry.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 友盟统计
				UmShare.UmStatistics(m_Context, "RewardSearch_Industry");
				chooseIndustry();
			}
			
		});
		
		// 确定
		m_rlConfirm = (RelativeLayout) findViewById(R.id.rewardsearch_lp_btnconfirm);
		m_rlConfirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finishRewardSearch();
			}
			
		});
		
		//
		m_imgHistory = (ImageView)findViewById(R.id.rewardsearch_imgv_historyicon);
		// 历史搜索
		m_rlHistory = (RelativeLayout) findViewById(R.id.rewardsearch_rl_history);
		m_rlHistory.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != m_listHistory){
					switch(m_listHistory.getVisibility()){
					case View.VISIBLE:
						{
							m_listHistory.setVisibility(View.GONE);							
							m_imgHistory.setImageResource(R.drawable.common_img_down);
						}
						break;
					case View.GONE:
						{
							m_listHistory.setVisibility(View.VISIBLE);;
							m_imgHistory.setImageResource(R.drawable.common_img_up);
						}
						break;
					default:
						break;
					}
				}
			}
		});
		
		// 
		m_search_et = (EditText) findViewById(R.id.rewardsearch_et_search);
		
		//
		m_imgClean = (ImageView)findViewById(R.id.rewardsearch_iv_clean);
		m_imgClean.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(null != m_search_et){
					m_search_et.setText("");
				}
				labelFlowLayout.clearSelectedLables();
				labelFlowLayout.resetLabels();
				labelFlowLayout.invalidate();
			}
		});
		
		// 已选中的城市
		m_tvAreaChoose = (TextView) findViewById(R.id.rewardsearch_tv_areachoose);	
		// 已选中的行业
		m_tvIndustryChoose = (TextView) findViewById(R.id.rewardsearch_tv_industrychoose);    
	}
	
	/**
	 * 初始化列表
	 */
	private void InitListView(){
		m_listHistory =  (ListView)findViewById(R.id.rewardsearch_lv_histroy); 
		m_listHistory.setOnItemClickListener(this);
		
		if(getSearchHistory()){
			m_adapterHistory = new HistroyListAdapter(m_Context, m_lsRHD);
			m_listHistory.setAdapter(m_adapterHistory);
		}
	}

	/**
	 * 进入城市选择界面
	 */
	private void chooseCity(){
		// 数据传输
		Bundle bundle = new Bundle();
		// 设置为多选
		bundle.putInt(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_TYPE,
				HeadhunterPublic.CITYCHOOSE_TYPE_MULTIPLE);
		
		// 已经选中的城市
		if(null != m_alsCCD){
			bundle.putSerializable(HeadhunterPublic.CITYCHOOSE_DATATRANSFER_DATA,
					m_alsCCD);
		}
		
		// 进入城市选择界面
        Intent intent = new Intent();
        intent.setClass(m_Context, CityChooseActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	/**
	 * 进入行业选择界面
	 */
	private void chooseIndustry(){
		// 数据传输
		Bundle bundle = new Bundle();
		// 设置为多选
		bundle.putInt(HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_TYPE,
				HeadhunterPublic.INDUSTRYCHOOSE_TYPE_MULTIPLE);
		
		// 已经选中的城市
		if(null != m_alsICSD){
			bundle.putSerializable(HeadhunterPublic.INDUSTRYCHOOSE_DATATRANSFER_DATA,
					m_alsICSD);
		}

		// 进入行业选择界面
        Intent intent = new Intent();
        intent.setClass(m_Context, IndustryChooseActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, HeadhunterPublic.RESULT_ACTIVITY_CODE);
	}
	
	private void finishRewardSearch(){
		RewardFilterCondition filters = new RewardFilterCondition();
		
		// 关键字
		String str = m_search_et.getText().toString();
		int nEmpty = 0;
		if(null == str){
			nEmpty++;
		}else if(str.isEmpty()){
			nEmpty++;
		}else{
			filters.setKeyword(str);
		}
		
		// 城市/地区
		if(null == m_alsCCD){
			nEmpty++;
		}else if(m_alsCCD.size() <=0 ){
			nEmpty++;
		}else{
			String[] strCity = new String[m_alsCCD.size()];
			int i = 0;
			for(CityChooseData ccd : m_alsCCD){
				strCity[i] = ccd.getID();
				i++;
			}
			filters.setArea_id(strCity);
		}
		
		// 子行业
		if(null == m_alsICSD){
			nEmpty++;
		}else if(m_alsICSD.size() <=0 ){
			nEmpty++;
		}else{
			String[] strIndustry = new String[m_alsICSD.size()];
			int i = 0;
			for(OneLevelChooseData icsd : m_alsICSD){
				strIndustry[i] = icsd.getID();
				i++;
			}
			filters.setIndustry_id(strIndustry);
			
			// 父行业
			if(null != m_strParentIndustry && m_strParentIndustry.length > 0){
				filters.setIndustry_fid(m_strParentIndustry);
			}
		}
		
		if(3 == nEmpty){
			UIHelper.ToastMessage(m_Context, 
					getString(R.string.msg_rewardsearch_nochoose));	
			return;
		}
		
		// 把搜索历史保存到数据库中
		SaveSearchHistory(filters);
		
		// 过滤条件数据传输			
		setResult(HeadhunterPublic.RESULT_REWARDSEARCH_OK, 
				getIntent().putExtra(HeadhunterPublic.REWARDSEARCH_DATATRANSFER_FILTERS, filters));
		
		finish();
	}
	
	/**
	 * 把搜索历史保存到数据库中
	 * @param filters
	 */
	private void SaveSearchHistory(RewardFilterCondition filters){
		AppContext ac = (AppContext) m_Context.getApplicationContext();
		
		try {
			ac.saveHistorySearch(filters);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 *获取搜索历史 
	 */
	private boolean getSearchHistory(){
		boolean bRet = false;
		
		if(null == m_lsRFC){
			m_lsRFC = new ArrayList<RewardFilterCondition>();
		}
		 
		 AppContext ac = (AppContext)getApplication(); 
		 try {
			 m_lsRFC = ac.getHistorySearch();
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		if(null == m_lsRHD){
			m_lsRHD = new ArrayList<RewardHistroyData>();
		}
		 
		TwoLevelChooseData tcd = new TwoLevelChooseData();
		tcd.setName(getString(R.string.str_rewardsearch_historysearch));
		
		if(null != m_lsRFC){
			for(RewardFilterCondition rfc : m_lsRFC){
				RewardHistroyData rhd = new RewardHistroyData();
				// 关键字
				String strTemp = rfc.getKeyword();
				// 城市
				if(null != rfc.getArea_id()){
					for(String str : rfc.getArea_id()){
						if(!str.isEmpty()){
							if(strTemp.isEmpty()){
								strTemp += getIdName(str, DBUtils.GLOBALDATA_TYPE_CITY);
							}else{
								strTemp += "," + getIdName(str, DBUtils.GLOBALDATA_TYPE_CITY);
							}
						}
					}
				}
				
				// 行业
				if(null != rfc.getIndustry_id()){
					for(String str : rfc.getIndustry_id()){
						if(!str.isEmpty()){
							if(strTemp.isEmpty()){
								strTemp += getIdName(str, DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY);
							}else{
								strTemp += "," + getIdName(str, DBUtils.GLOBALDATA_TYPE_JOBINDUSTRY);	
							}
						}
					}					
				}
				
				rhd.setName(strTemp);
				m_lsRHD.add(rhd);
			}
		}
		
		if(0 == m_lsRHD.size()){
			RewardHistroyData rhd = new RewardHistroyData();
			rhd.setName(getString(R.string.str_rewardsearch_nohistory));
			rhd.setNoImg(true);
			m_lsRHD.add(rhd);
			
			m_bNoHistroy = true;
		}else{
			m_bNoHistroy = false;
		}
		
		bRet = true;
		
		return bRet;
	}
	
	/**
	 * 根据id和类型， 从全局数据中获取名称
	 * @param strId
	 * @param nType
	 * @return
	 */
	private String getIdName(String strId, int nType){
		String strTemp = strId;
		
		if(strId.isEmpty()){
			return strTemp;
		}
		
		AppContext ac = (AppContext)getApplication(); 
		
		try {
			strTemp = ac.getGlobalDataSpecifyInfo(strId, nType, DBUtils.KEY_GLOBALDATA_NAME);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return strTemp;
	}
}

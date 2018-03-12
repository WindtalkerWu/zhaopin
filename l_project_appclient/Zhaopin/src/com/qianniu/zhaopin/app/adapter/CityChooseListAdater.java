package com.qianniu.zhaopin.app.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.qianniu.zhaopin.app.bean.CityChooseData;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * @author wuzy
 *
 */
public class CityChooseListAdater extends BaseAdapter implements SectionIndexer{
	
    private List<CityChooseData> m_listCCD;
    public static HashMap<Integer, Boolean> isSelected;
    
    private Context context; 
    private LayoutInflater m_infater = null;
    
    private int m_nChooseNum;
    
    public CityChooseListAdater(Context _context, List<CityChooseData> list) {
    	
    	this.m_listCCD = list; 
    	 
        context = _context;
        m_nChooseNum = 0;
        initSelected();
        
        m_infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		CityChooseListAdater.isSelected = isSelected;
	}

	private void initSelected() {
        isSelected = new HashMap<Integer, Boolean>();
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_listCCD.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_listCCD.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		CityChooseViewHolder viewHolder = null;
		if(convertView == null){
			convertView = m_infater.inflate(R.layout.list_item_citychoose, null);
			viewHolder = new CityChooseViewHolder(convertView);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (CityChooseViewHolder)convertView.getTag();
		}
			
		final CityChooseData gdt = m_listCCD.get(position); 
		
		String fistStr = "";
		if(HeadhunterPublic.CITYCHOOSE_DATATYPE_HOT == gdt.getType()){
			if (position == 0) {
				viewHolder.tvTitle.setVisibility(View.VISIBLE);
	            viewHolder.tvTitle.setText(R.string.str_citychoose_hotcity);
			}else{
				viewHolder.tvTitle.setVisibility(View.GONE);
			}
		}else{
			fistStr = gdt.getNamePinYin().substring(0, 1);
			
	        if (position == 0) {   
	            viewHolder.tvTitle.setVisibility(View.VISIBLE);
	            viewHolder.tvTitle.setText(fistStr);
	        } else {
	        	String preLabel = m_listCCD.get(position - 1).getNamePinYin();
	            String preFirstStr = preLabel.substring(0, 1); 

	            if (fistStr.equals(preFirstStr)) {  
	            	viewHolder.tvTitle.setVisibility(View.GONE);
	            } else {  
	            	viewHolder.tvTitle.setVisibility(View.VISIBLE);
					viewHolder.tvTitle.setText(fistStr); 
	            }  
	        } 			
		}
	
		// 城市名称
		viewHolder.tvCityName.setText(gdt.getName());
		
		// 显示城市是否选中
		if (gdt.getIsChoose()) {
//			viewHolder.imgCheckBox.setImageResource(R.drawable.citychoose_img_list_checkbox_select);
			viewHolder.imgCheckBox.setVisibility(View.VISIBLE);
		} else {
//			viewHolder.imgCheckBox.setImageResource(R.drawable.citychoose_img_list_checkbox_default);
			viewHolder.imgCheckBox.setVisibility(View.INVISIBLE);
		}

		return convertView;  
	}
	@Override
	public int getPositionForSection(int section) {
		// TODO Auto-generated method stub
       for (int i = 0; i < m_listCCD.size(); i++) {
    	   if(HeadhunterPublic.CITYCHOOSE_DATATYPE_COMMON == m_listCCD.get(i).getType()){
               String l = m_listCCD.get(i).getNamePinYin().substring(0, 1); 
               char firstChar = l.toUpperCase().charAt(0); 
               if (firstChar == section) {
                   return i;  
               } 
    	   } 
        } 
       
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取已经选中的城市数
	 * @return 已经选中的城市数
	 */
	public int getChooseNum(){
		return m_nChooseNum;
	}
	
	static class CityChooseViewHolder{
		TextView tvTitle;				//	目录
		TextView tvCityName;			//	城市名称
		
		ImageView imgCheckBox;
		
		RelativeLayout rlCityName;
		
		public CityChooseViewHolder(View view){

			this.tvTitle = (TextView)view.findViewById(R.id.list_item_citychoose_title);
			this.tvCityName = (TextView)view.findViewById(R.id.list_item_citychoose_cityname);
			
			this.imgCheckBox = (ImageView) view.findViewById(R.id.list_item_citychoose_checkBox);
			
			this.rlCityName = (RelativeLayout) view.findViewById(R.id.list_item_citychoose_lp_cityname);
		}
	}
}

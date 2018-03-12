package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.adapter.CityChooseListAdater.CityChooseViewHolder;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OneLevelChooseListAdapter extends BaseAdapter{

    private Context m_Context; 
    private LayoutInflater m_infater = null;
    
    private List<OneLevelChooseData> m_listRCD;	// 理由列表数据
    private String selectId; 
    
    public OneLevelChooseListAdapter(Context context, 
    		List<OneLevelChooseData> listRCD){
    	this.m_Context = context;
    	m_listRCD = listRCD;
    	m_infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
	public String getSelectId() {
		return selectId;
	}

	public void setSelectId(String selectId) {
		this.selectId = selectId;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_listRCD.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_listRCD.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		RecommendReasonChooseViewHolder viewHolder = null;
		if(convertView == null){
			convertView = m_infater.inflate(R.layout.list_item_onelevelchoose, null);
			viewHolder = new RecommendReasonChooseViewHolder(convertView);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (RecommendReasonChooseViewHolder)convertView.getTag();
		}
		
		OneLevelChooseData listRCD = m_listRCD.get(position);
		viewHolder.tvReason.setText(listRCD.getName());
		if(listRCD.getID().equals(selectId)){
			listRCD.setIsChoose(true);
//			Resources resource = (Resources) m_Context.getResources();  
//			ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.resume_button_color);  
//			if (csl != null) {  
//				viewHolder.tvReason.setTextColor(csl);  
//			}
			viewHolder.selectFlag.setVisibility(View.VISIBLE);
		} else {
			viewHolder.selectFlag.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

	static class RecommendReasonChooseViewHolder{
		TextView tvReason;				//	推荐理由
		ImageView selectFlag;
		
		public RecommendReasonChooseViewHolder(View view){

			this.tvReason = (TextView)view.findViewById(R.id.list_item_recommendreason_text);
			this.selectFlag = (ImageView) view.findViewById(R.id.list_item_recommendreason_image);
		}
	}
}

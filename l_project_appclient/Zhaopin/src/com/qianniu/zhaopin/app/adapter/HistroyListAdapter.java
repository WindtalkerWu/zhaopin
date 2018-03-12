package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.adapter.OneLevelChooseListAdapter.RecommendReasonChooseViewHolder;
import com.qianniu.zhaopin.app.bean.OneLevelChooseData;
import com.qianniu.zhaopin.app.bean.RewardHistroyData;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author wuzy
 *
 */
public class HistroyListAdapter extends BaseAdapter{

    private Context m_Context; 
    private LayoutInflater m_infater = null;
    
    private List<RewardHistroyData> m_liRHD;	// 历史列表数据
    
    public HistroyListAdapter(Context context, 
    		List<RewardHistroyData> lsRHD){
    	this.m_Context = context;
    	m_liRHD = lsRHD;
    	m_infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_liRHD.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_liRHD.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HistoryViewHolder viewHolder = null;
		if(convertView == null){
			convertView = m_infater.inflate(R.layout.list_item_history, null);
			viewHolder = new HistoryViewHolder(convertView);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (HistoryViewHolder)convertView.getTag();
		}
		
		RewardHistroyData rhd = m_liRHD.get(position);

		viewHolder.tvHistory.setText(rhd.getName());
		
		if(rhd.getNoImg()){
			viewHolder.imgNext.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	static class HistoryViewHolder{
		TextView tvHistory;				//	推荐理由
		ImageView imgNext;
		
		public HistoryViewHolder(View view){

			this.tvHistory = (TextView)view.findViewById(R.id.list_item_history_text);
			
			this.imgNext = (ImageView)view.findViewById(R.id.list_item_history_img_next);
			
		}
	}
}

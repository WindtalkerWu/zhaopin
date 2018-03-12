package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.app.bean.PullDownListInfo;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PullDownListAdapter extends BaseAdapter{

	private List<PullDownListInfo> m_PDLI;
	
	private LayoutInflater m_infater = null;
	private Context m_Context;
	
	public PullDownListAdapter(Context context, List<PullDownListInfo> list){
		m_infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_PDLI = list;
		m_Context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_PDLI.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_PDLI.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		PullDownViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = m_infater.inflate(R.layout.list_item_pulldown, null);
			holder = new PullDownViewHolder(convertView);
			convertView.setTag(holder);
		} 
		else{
			convertView = convertView ;
			holder = (PullDownViewHolder) convertView.getTag() ;
		}

		PullDownListInfo pdlinfo = (PullDownListInfo)getItem(position);
		
		//
		holder.tvText.setText(pdlinfo.getText());
		
		if(pdlinfo.getIsChoose()){
			holder.tvText.setBackgroundResource(R.drawable.common_button_pulldown_choose);
			holder.tvText.setTextColor(m_Context.getResources().getColor(R.color.black));
		}else{
			holder.tvText.setBackgroundResource(R.drawable.common_button_pulldown_nochoose);
			holder.tvText.setTextColor(m_Context.getResources().getColor(R.color.white));
		}
		
		return convertView;
	}

	static class PullDownViewHolder{
		TextView tvText;			// 
		
		public PullDownViewHolder(View view){
			this.tvText = (TextView)view.findViewById(R.id.list_item_pulldown_text);
		}
	}
}

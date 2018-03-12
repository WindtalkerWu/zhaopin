package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.bean.TransactionRecordData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 交易记录适配器
 * @author wuzy
 *
 */
public class TransactionRecordsAdapter extends BaseAdapter {
	private LayoutInflater m_infater;
	private Context m_Context;
	
	private List<TransactionRecordData> m_lsRecordInfo;
	
	public TransactionRecordsAdapter(Context context, List<TransactionRecordData> lsRecordInfo){
		m_infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		m_Context = context;
		m_lsRecordInfo = lsRecordInfo;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_lsRecordInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_lsRecordInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TransactionRecordsListViewHolder holder = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = m_infater.inflate(R.layout.list_item_transactionrecord, null);
			holder = new TransactionRecordsListViewHolder(convertView);
			convertView.setTag(holder);
		} 
		else{
			holder = (TransactionRecordsListViewHolder) convertView.getTag() ;
		}
		
		TransactionRecordData trd = m_lsRecordInfo.get(position);
		
		String strMoney = "";
		if(null != trd.getMoney() && !trd.getMoney().isEmpty()){
			// 收支类型
			if(Double.valueOf(trd.getMoney()) >= 0){
				holder.tvType.setText(m_Context.getString(R.string.str_transactionrecords_income));
				holder.tvSum.setTextColor(m_Context.getResources().getColor(R.color.green));
//				strMoney = m_Context.getString(R.string.str_transactionrecords_incomesign)
//						+ m_Context.getString(R.string.str_reward_rmb)
				strMoney = m_Context.getString(R.string.str_reward_rmb)
						+ m_Context.getString(R.string.str_transactionrecords_incomesign)
						+ trd.getMoney();
			}else{
				holder.tvType.setText(m_Context.getString(R.string.str_transactionrecords_pay));
				holder.tvSum.setTextColor(m_Context.getResources().getColor(R.color.red));
//				strMoney = m_Context.getString(R.string.str_transactionrecords_paysign)
				strMoney = m_Context.getString(R.string.str_reward_rmb)
						+ trd.getMoney();
			}
			
			// 交易金额
			holder.tvSum.setText(strMoney);
		}
		
		// 交易时间
		if(null != trd.getModified() && !trd.getModified().isEmpty()){
			holder.tvDate.setText(trd.getModified());
		}
		
		// 交易明细
		if(null != trd.getTitle() && !trd.getTitle().isEmpty()){
			String strDtetail = m_Context.getString(R.string.str_transactionrecords_dtetail)
					+ trd.getTitle();
			holder.tvDtetail.setText(strDtetail);
		}
		
		return convertView;
	}

	public static class TransactionRecordsListViewHolder{
		
		TextView tvType;	
		TextView tvSum;	
		TextView tvDate;	
		TextView tvDtetail;	
		
		public TransactionRecordsListViewHolder(View view){
			this.tvType = (TextView)view.findViewById(R.id.list_item_transactionrecord_tv_type);
			this.tvSum = (TextView)view.findViewById(R.id.list_item_transactionrecord_tv_sum);
			this.tvDate = (TextView)view.findViewById(R.id.list_item_transactionrecord_tv_date);
			this.tvDtetail = (TextView)view.findViewById(R.id.list_item_transactionrecord_tv_dtetail);
		}
	}
}

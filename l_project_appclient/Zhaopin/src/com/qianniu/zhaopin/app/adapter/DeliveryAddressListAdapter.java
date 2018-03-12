package com.qianniu.zhaopin.app.adapter;

import java.util.List;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.adapter.ConversionRecordListAdapter.ConversionInfoViewHolder;
import com.qianniu.zhaopin.app.bean.DeliveryAddressInfoData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 收货地址列表适配器
 * @author wuzy
 *
 */
public class DeliveryAddressListAdapter  extends BaseAdapter{
	private Context m_Context;
	
	private LayoutInflater m_infater = null;
	
	private List<DeliveryAddressInfoData> m_lsDAFD;
	
//	private DeliveryAddressInfoViewHolder m_holder;
	
	public DeliveryAddressListAdapter(Context context,
			List<DeliveryAddressInfoData> list) {
		m_infater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		m_lsDAFD = list;
		m_Context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return m_lsDAFD.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return m_lsDAFD.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DeliveryAddressInfoViewHolder holder;
		if (convertView == null || convertView.getTag() == null) {
			convertView = m_infater.inflate(
						R.layout.list_item_deliveryaddress, null);

			holder = new DeliveryAddressInfoViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (DeliveryAddressInfoViewHolder) convertView.getTag();
		}
		
		DeliveryAddressInfoData daid = (DeliveryAddressInfoData)getItem(position);
		if(null != daid){
			// 设置收货人姓名
			if(null != daid.getStrName()){
				holder.tvName.setText(daid.getStrName());
			}
			
			// 设置联系方式
			if(null != daid.getStrTel()){
				holder.tvTel.setText(daid.getStrTel());
			}
			
			// 设置详细地址
			if(null != daid.getStrAddress()){
				holder.tvAddress.setText(daid.getStrAddress());
			}
		}
		
		return convertView;
	}
	
	public static class DeliveryAddressInfoViewHolder {

		TextView tvName; 		// 收货人姓名
		TextView tvTel; 		// 联系方式
		TextView tvAddress; 	// 详细地址

		public DeliveryAddressInfoViewHolder(View view) {
			// 收货人姓名
			this.tvName = (TextView) view
					.findViewById(R.id.listitem_deliveryaddress_tv_name);
			// 联系方式
			this.tvTel = (TextView) view
					.findViewById(R.id.listitem_deliveryaddress_tv_tel);
			// 详细地址
			this.tvAddress = (TextView) view
					.findViewById(R.id.listitem_deliveryaddress_tv_address);

		}
	}
}

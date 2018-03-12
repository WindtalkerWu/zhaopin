package com.qianniu.zhaopin.thp;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;

public class AMapLocationForQN implements
	AMapLocationListener, Runnable{
	
	private LocationManagerProxy m_aMapLocManager = null;
	
	private AMapLocation m_aMapLocation;	// 用于判断定位超时
	
	private Handler m_handler = new Handler();
	private Handler m_RetHandler;
	
	private boolean m_bIsLocationing;

	public AMapLocationForQN(Activity activity, Handler handler){
		this.m_RetHandler = handler;
		
		m_aMapLocManager = LocationManagerProxy.getInstance(activity);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (null == m_aMapLocation && null != m_aMapLocManager) {
			// 销毁掉定位
			stopLocation();
			
			// 定位失败
			if(null != m_RetHandler){
				m_RetHandler.sendMessage(m_RetHandler.obtainMessage(
						HeadhunterPublic.MSG_GETLOCATION_FAIL));				
			}
			
			m_bIsLocationing = false;
		}
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub
		if (location != null) {
			// 判断超时机制
			this.m_aMapLocation = location;
			
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String cityCode = "";
			String desc = "";
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
//			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
//					+ "\n精    度    :" + location.getAccuracy() + "米"
//					+ "\n定位方式:" + location.getProvider() + "\n定位时间:"
//					+ AMapUtil.convertToTime(location.getTime()) + "\n城市编码:"
//					+ cityCode + "\n位置描述:" + desc + "\n省:"
//					+ location.getProvince() + "\n市:" + location.getCity()
//					+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location
//					.getAdCode());
			
			String[] strRet = new String[4];
			// 城市名
			strRet[0] = location.getCity();
			// 
			strRet[1] = String.valueOf(geoLng);
			// 城市名
			strRet[2] = String.valueOf(geoLat);
			// 
			strRet[3] = location.getProvider();
			
			// 成功
			if(null != m_RetHandler){
				m_RetHandler.sendMessage(m_RetHandler.obtainMessage(
						HeadhunterPublic.MSG_GETLOCATION_SUCCESS, strRet));				
			}
			
			m_bIsLocationing = false;
		}		
	}
	
	/**
	 * 销毁定位
	 */
	public void stopLocation() {
		if (m_aMapLocManager != null) {
			m_aMapLocManager.removeUpdates(this);
			m_aMapLocManager.destory();
		}
		m_aMapLocManager = null;
	}

	public boolean getLocationStatus(){
		return m_bIsLocationing;
	}
	
	public void startLocation(){
		if(null == m_aMapLocManager){
			return;
		}

		/*
		 * m_aMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		m_aMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 5000, 10, this);
		m_bIsLocationing = true;
		
		// 设置超过30秒还没有定位到就停止定位
		m_handler.postDelayed(this, 30000);
	}
}

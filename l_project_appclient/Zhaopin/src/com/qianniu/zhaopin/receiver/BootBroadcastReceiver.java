package com.qianniu.zhaopin.receiver;

import com.qianniu.zhaopin.app.service.ConnectionService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent service_intent = new Intent(context, ConnectionService.class);
		context.startService(service_intent);
		//Log.i("BootBroadcastReceiver", "start ConnectionService");
	}

}

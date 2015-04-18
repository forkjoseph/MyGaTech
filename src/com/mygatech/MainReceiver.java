package com.mygatech;

import java.util.Calendar;
import java.util.Locale;

import com.mygatech.tsquare.TsquareService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;

public class MainReceiver extends BroadcastReceiver {
	final public static String ONE_TIME = "onetime";

	@SuppressWarnings("unused")
	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager powerManager = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = powerManager.isScreenOn();
		ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = false;
		boolean isWiFi = false;
		if (activeNetwork != null) {
			isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
			isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
		}
		
		//April Fool's day :)!
//		if(aprilFool())
//			context.startService(new Intent(context, AFDService.class));
//		if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) ==  0 && (isConnected && isWiFi)) {
//			Log.d("check", "Time chaged");
//			context.startService(new Intent(context, TsquareService.class));
//
//		}
//		if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK) ==  0 || !isScreenOn || ( isConnected && isWiFi))
//			context.startService(new Intent(context, TsquareMenuService.class));
	}
	
	public boolean aprilFool(){
		Calendar cal = Calendar.getInstance(Locale.US);
		int Month = cal.get(Calendar.MONTH);
		int Day = cal.get(Calendar.DAY_OF_MONTH);
		int Year = cal.get(Calendar.YEAR);
		int Hour = cal.get(Calendar.HOUR_OF_DAY);
		int Min = cal.get(Calendar.MINUTE);
		if(Month == Calendar.APRIL && Day == 1 && Year == 2014 && Hour == 13 && Min == 00 )
			return true;
		return false;
	}

}

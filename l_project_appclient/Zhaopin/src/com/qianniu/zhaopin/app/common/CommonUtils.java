package com.qianniu.zhaopin.app.common;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;

public class CommonUtils {
	public static Calendar DateToCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	
	public static String getYYMMDDString(Calendar cal){
		String stryear = String.format("%04d", cal.get(Calendar.YEAR));
		String strmonth = String.format("%02d", cal.get(Calendar.MONTH)+1);
		String strday = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
		StringBuilder strbuilder= new StringBuilder().append(stryear).append("-").append(strmonth).append("-")
				.append(strday).append(" ");
		return strbuilder.toString();
	}
	
	public static String getChineseYYMMDDString(Calendar cal){
		String stryear = String.format("%04d", cal.get(Calendar.YEAR));
		String strmonth = String.format("%02d", cal.get(Calendar.MONTH)+1);
		String strday = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
		StringBuilder strbuilder= new StringBuilder().append(stryear).append("年").append(strmonth).append("月")
				.append(strday).append("日");
		return strbuilder.toString();
	}
	public static String getChineseYMString(Calendar cal){
		String stryear = String.format("%04d", cal.get(Calendar.YEAR));
		String strmonth = String.format("%02d", cal.get(Calendar.MONTH)+1);

		StringBuilder strbuilder= new StringBuilder().append(stryear).append("年").append(strmonth).append("月")
				;
		return strbuilder.toString();
	}
	public static int dip2px(Context context, float dpValue) { 
		final float scale = context.getResources().getDisplayMetrics().density ; 
		return (int) (dpValue * scale + 0.5f) ;
	}
	public static int px2dip(Context context, float pxValue) {
		  final float scale = context.getResources().getDisplayMetrics().density ; 
		return (int) (pxValue / scale + 0.5f) ; 
	}
}

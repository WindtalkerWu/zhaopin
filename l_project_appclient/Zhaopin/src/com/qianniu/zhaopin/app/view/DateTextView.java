package com.qianniu.zhaopin.app.view;

import com.qianniu.zhaopin.app.common.StringUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class DateTextView extends TextView{

	public DateTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DateTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
	}


	/**
	 * @param date:YYYY-MM-DD HH-MM-SS
	 */
	public void setDate(CharSequence date) {
		// TODO Auto-generated method stub
		if(date != null){
			String YMDdate = StringUtils.formatDateStringToYMD(date.toString());
			setText(YMDdate);
		}
	}

	/**
	 * @return :YYYY-MM-DD HH-MM-SS
	 */
	public String getDate(){
		String date = getText().toString();		
		date = StringUtils.changeYMDToYYMMDD(date);
		return date;
	}
	
}

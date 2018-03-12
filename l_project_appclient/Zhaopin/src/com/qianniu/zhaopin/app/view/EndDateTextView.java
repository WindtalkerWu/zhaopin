package com.qianniu.zhaopin.app.view;

import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.app.common.StringUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class EndDateTextView extends DateTextView {

	public EndDateTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public EndDateTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

	}

	/**
	 * 如果日期为2100-01-01则显示为“至今”
	 *  如果日期是今天或未来的时间 则显示为“至今”
	 * @param date
	 *            :YYYY-MM-DD HH-MM-SS
	 */
	public void setDate(CharSequence date) {
		// TODO Auto-generated method stub
		if (date != null) {
			int days = -1;
			try {
				days = (int) StringUtils.compareDateStrWithToday(date
						.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String tonow = getResources().getString(
					R.string.str_date_to_now_YYMMDD);
			if (date.toString().startsWith(tonow)) {
				setText(R.string.str_date_to_now);
			} else if (days >= 0) {
				setText(R.string.str_date_to_now);
			} else {
				String YMDdate = StringUtils.formatDateStringToYMD(date
						.toString());
				setText(YMDdate);
			}
		}
	}

	/**
	 * 如果显示为“至今”则返回2100-01-01
	 * 
	 * @return :YYYY-MM-DD HH-MM-SS
	 */
	public String getDate() {
		String date = getText().toString();

		String tonow = getResources().getString(R.string.str_date_to_now);
		if (date.equals(tonow)) {
			date = getResources().getString(R.string.str_date_to_now_YYMMDD);
		} else {
			date = StringUtils.changeYMDToYYMMDD(date);
		}
		return date;
	}
}

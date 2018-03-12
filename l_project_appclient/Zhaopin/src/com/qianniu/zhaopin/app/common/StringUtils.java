package com.qianniu.zhaopin.app.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qianniu.zhaopin.app.AppException;

import android.util.Log;

/**
 * 字符串操作工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
	// private final static SimpleDateFormat dateFormater = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// private final static SimpleDateFormat dateFormater2 = new
	// SimpleDateFormat("yyyy-MM-dd");

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			if (sdate != null) {
				if (sdate.length() > 10)
					return dateFormater.get().parse(sdate);
				else
					return dateFormater2.get().parse(sdate);
			} else
				return null;

		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 将字符串转为Calendar类型
	 * 
	 * @param datestr
	 *            : YYYY-MM-DD HH-MM-SS
	 * @return
	 */
	public static Calendar toCalendar(String datestr) {
		String formatdate = null;
		Calendar cal = null;
		if (datestr != null && datestr.length() > 0) {
			Date date = StringUtils.toDate(datestr);
			if (date != null) {
				cal = CommonUtils.DateToCalendar(date);
			}
		}
		return cal;
	}

	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @return
	 */
	public static String friendly_time(String sdate) {
		Date time_param = toDate(sdate);
		if (time_param == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal_now = Calendar.getInstance();
		Calendar cal_param = CommonUtils.DateToCalendar(time_param);

		long day_param = cal_param.get(Calendar.DAY_OF_YEAR); // time.getTime()
																// / 86400000;
		long day_now = cal_now.get(Calendar.DAY_OF_YEAR);// cal.getTimeInMillis()
															// / 86400000;
		int days = 0;// (int) (ct - lt);
		if (cal_param.getTimeInMillis() <= cal_now.getTimeInMillis()) {
			if (day_param <= day_now) {
				days = (int) (day_now - day_param);
			} else {
				days = (int) (day_now + 365 - day_param);
			}
		} else {
			if (day_param >= day_now) {
				days = (int) (day_now - day_param);
			} else {
				days = (int) (day_now - 365 + day_param);
			}
		}

		if (days < 0) {
			ftime = CommonUtils.getChineseYYMMDDString(cal_param);
		} else if (days == 0) {
			String hour = String.format("%02d",
					cal_param.get(Calendar.HOUR_OF_DAY));
			String seconds = String.format("%02d",
					cal_param.get(Calendar.MINUTE));
			ftime = hour + ":" + seconds;
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 6) {

			ftime = changeCalendarToWeek(cal_param);
		} else if (days > 6) {

			ftime = CommonUtils.getChineseYYMMDDString(cal_param);
		}
		return ftime;
	}

	/**
	 * 判断给定字符串时间是否为今日
	 * 
	 * @param sdate
	 * @return boolean
	 */
	public static boolean isToday(String sdate) {
		boolean b = false;
		Date time = toDate(sdate);
		Date today = new Date();
		if (time != null) {
			String nowDate = dateFormater2.get().format(today);
			String timeDate = dateFormater2.get().format(time);
			if (nowDate.equals(timeDate)) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 将ms时间转为标准yyyy-MM-dd HH:mm:ss时间
	 * 
	 * @param millisecond
	 * @return
	 */
	public static String timeToString(Long millisecond) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = new Timestamp(millisecond);
		str = sdf.format(ts);
		return str;
	}

	/**
	 * 将ms时间转为标准yyyy-MM-dd HH:mm:ss时间
	 * 
	 * @param millisecond
	 * @return
	 */
	public static String timeToString(String millisecond) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Long time = toLong(millisecond);
		Timestamp ts = new Timestamp(time);
		str = sdf.format(ts);
		return str;
	}

	/**
	 * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
	 * 
	 * @param input
	 * @return boolean
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	/*
	 * public static boolean isEmail(String email) { if (email == null ||
	 * email.trim().length() == 0) return false; return
	 * emailer.matcher(email).matches(); }
	 */

	/**
	 * 判断是不是一个电话号码
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		/*
		 * 可接受的电话格式有：
		 */
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		/*
		 * 可接受的电话格式有：
		 */
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);

		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}

	/**
	 * 判断手机格式是否正确
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
	}

	/**
	 * 判断email格式是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	/**
	 * 判断是否全是数字
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static long toLong(String obj) {
		try {
			return Long.parseLong(obj);
		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBool(String b) {
		try {
			return Boolean.parseBoolean(b);
		} catch (Exception e) {
		}
		return false;
	}

	public static String[] parseKeyWords(String str) {
		if (str == null)
			return null;
		String[] strs = str.split(" ");
		return strs;
	}

	/**
	 * 将 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 转为 yyyy-MM-dd
	 * 
	 * @param datestr
	 */
	public static String formatDateString(String datestr) {
		String formatdate = null;
		if (datestr != null && datestr.length() > 0) {
			Date date = StringUtils.toDate(datestr);
			if (date != null) {
				Calendar cal = CommonUtils.DateToCalendar(date);
				formatdate = CommonUtils.getYYMMDDString(cal);
			}
		}
		return formatdate;
	}

	/**
	 * 将 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 转为 yyyy年MM月dd日
	 * 
	 * @param datestr
	 * @return
	 */
	public static String formatDateStringToYMD(String datestr) {
		String formatdate = null;
		if (datestr != null && datestr.length() > 0) {
			Date date = StringUtils.toDate(datestr);
			if (date != null) {
				Calendar cal = CommonUtils.DateToCalendar(date);
				formatdate = CommonUtils.getChineseYYMMDDString(cal);
			}
		}
		return formatdate;
	}
	/**
	 * 将 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 转为 yyyy年MM月dd日
	 * 
	 * @param datestr
	 * @return
	 */
	public static String formatDateStringToYM(String datestr) {
		String formatdate = null;
		if (datestr != null && datestr.length() > 0) {
			Date date = StringUtils.toDate(datestr);
			if (date != null) {
				Calendar cal = CommonUtils.DateToCalendar(date);
				formatdate = CommonUtils.getChineseYMString(cal);
			}
		}
		return formatdate;
	}

	public static String formatToYYMMDD(int year, int month, int day) {

		String stryear = String.format("%04d", year);
		String strmonth = String.format("%02d", month + 1);
		String strday = String.format("%02d", day);
		StringBuilder strb = new StringBuilder().append(stryear).append("-")
				.append(strmonth).append("-").append(strday);
		return strb.toString();
	}

	public static String formatToYMD(int year, int month, int day) {
		String stryear = String.format("%04d", year);
		String strmonth = String.format("%02d", month + 1);
		String strday = String.format("%02d", day);
		StringBuilder strb = new StringBuilder().append(stryear).append("年")
				.append(strmonth).append("月").append(strday).append("日");
		return strb.toString();
	}

	/**特别显示时间，如果时间大于等于今天，将日期改为2100-01-01
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String formatToYYMMDDForEndDay(int year, int month, int day) {
		String stryear = String.format("%04d", year);
		String strmonth = String.format("%02d", month + 1);
		String strday = String.format("%02d", day);
		Calendar cld = Calendar.getInstance();
		
		StringBuilder strb = new StringBuilder().append(stryear).append("-")
				.append(strmonth).append("-").append(strday);
		try {
			long days = compareDateStrWithToday(strday);
			if(days >= 0){
				strb = new StringBuilder().append("2100").append("-")
						.append("01").append("-").append("01");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strb.toString();

	}
	
	/**
	 * 
	 * @param sdate
	 * @return
	 */
	public static String changeDateToWeek(String sdate) {
		if(null == sdate || sdate.isEmpty()){
			return "";
		}
		
		// 截取年月日
		int nEnd = sdate.indexOf(" ");
		sdate = sdate.substring(0, nEnd);
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		// String curDate = dateFormater2.get().format(cal.getTime());
		// String paramDate = dateFormater2.get().format(time);
		// if (curDate.equals(paramDate)) {
		// String hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY));
		// String seconds = String.format("%02d", cal.get(Calendar.MINUTE));
		// ftime = hour + ":" + seconds;
		// return ftime;
		// }

		Calendar calDate = Calendar.getInstance();
		calDate.setTime(time);
		long days = (cal.getTimeInMillis() - calDate.getTimeInMillis()) / 86400000;
		if (days == 0) {
			ftime = "今天";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 6) {
			cal.setTime(time);
			int n = cal.get(Calendar.DAY_OF_WEEK);
			switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case 1: {
				ftime = "星期天";
			}
				break;
			case 2: {
				ftime = "星期一";
			}
				break;
			case 3: {
				ftime = "星期二";
			}
				break;
			case 4: {
				ftime = "星期三";
			}
				break;
			case 5: {
				ftime = "星期四";
			}
				break;
			case 6: {
				ftime = "星期五";
			}
				break;
			case 7: {
				ftime = "星期六";
			}
				break;
			}
		} else if (days > 6) {
			ftime = changeDateToYMD(sdate);
		}
		return ftime;
	}

	/**
	 * 把日期转换成年月日格式(如: 2013年10月1日)
	 * 
	 * @param sdate
	 * @return
	 */
	public static String changeDateToYMD(String sdate) {
		String strDate = "";
		if (null != sdate) {
			if (!sdate.isEmpty()) {
				int nFirst = sdate.indexOf("-");
				int nLast = sdate.lastIndexOf("-");
				strDate = sdate.substring(0, nFirst) + "年"
						+ sdate.substring(nFirst + 1, nLast) + "月"
						+ sdate.substring(nLast + 1, sdate.length()) + "日";
			}
		}
		return strDate;
	}

	/**
	 * 把日期转换成月日格式(如: 10月1日)
	 * 
	 * @param sdate
	 * @return
	 */
	public static String changeDateToMD(String sdate) {
		String strDate = "";
		if (null != sdate) {
			if (!sdate.isEmpty()) {
				// 截取年月日
				int nEnd = sdate.indexOf(" ");
				sdate = sdate.substring(0, nEnd);
				
				int nFirst = sdate.indexOf("-");
				int nLast = sdate.lastIndexOf("-");
				strDate = sdate.substring(nFirst + 1, nLast) + "月"
						+ sdate.substring(nLast + 1, sdate.length()) + "日";
			}
		}
		return strDate;
	}

	/**
	 * 将 yyyy年MM月dd日 转为 yyyy-MM-dd
	 * 
	 * @param datestr
	 *            :日期输入字串
	 * @return
	 */
	public static String changeYMDToYYMMDD(String datestr) {
		if (datestr == null)
			return null;
		String str = new String(datestr);
		str = str.replace("年", "-");
		str = str.replace("月", "-");
		str = str.replace("日", "");
		return str;

	}

	/**
	 * 将 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 转为 更新时间：yyyy年MM月dd日
	 * 
	 * 
	 * @param datestr
	 * @return
	 */
	public static String formatDateStringToModifyYMD(String datestr) {
		String str = formatDateStringToYMD(datestr);
		return "更新时间:" + str;
	}

	public static String changeCalendarToWeek(Calendar cal) {
		String ftime = null;
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case 1: {
			ftime = "星期天";
		}
			break;
		case 2: {
			ftime = "星期一";
		}
			break;
		case 3: {
			ftime = "星期二";
		}
			break;
		case 4: {
			ftime = "星期三";
		}
			break;
		case 5: {
			ftime = "星期四";
		}
			break;
		case 6: {
			ftime = "星期五";
		}
			break;
		case 7: {
			ftime = "星期六";
		}
			break;
		}
		return ftime;
	}
	
	/**比较两个日期的大小；
	 * @param ADate: yyyy-MM-dd HH:mm:ss 字符串
	 * @param BDate: yyyy-MM-dd HH:mm:ss 字符串
	 * @return:0-相等；>0 - ADate大于BDate的天数； <0 - ADate小于BDate的天数
	 * @throws Exception : 
	 */
	public static long compareDateStr(String ADate,String BDate )throws Exception{
		if(ADate == null || BDate == null ){
			if(ADate == null && BDate == null)
				throw new Exception("ADate and BDate are null point");
			else
				throw new Exception(ADate == null?("ADate"):"BDate"+"is null point");
		}
		Date adate = toDate(ADate);
		Date bdate = toDate(BDate);
		if (adate == null) {
			throw new Exception("ADate can't be convert to date");
		}
		if (bdate == null) {
			throw new Exception("BDate can't be convert to date");
		}
	
		Calendar acal = Calendar.getInstance();
		acal.setTime(adate);
		Calendar bcal = Calendar.getInstance();
		bcal.setTime(bdate);
		
		long days = (acal.getTimeInMillis() - bcal.getTimeInMillis()) / (24*60*60*1000);
		return days;		
	} 
	/**和当前日期比较大小
	 * @param ADate: yyyy-MM-dd HH:mm:ss 字符串
	 * @return
	 * @throws Exception
	 */
	public static long compareDateStrWithToday(String ADate)throws Exception{
		if(ADate == null  ){

				throw new Exception("ADate is null point");

		}
		Date adate = toDate(ADate);

	
		Calendar acal = Calendar.getInstance();
		acal.setTime(adate);
		Calendar bcal = Calendar.getInstance();

		
		long days = (acal.getTimeInMillis() - bcal.getTimeInMillis()) / (24*60*60*1000);
		return days;		
	} 
	
	
}

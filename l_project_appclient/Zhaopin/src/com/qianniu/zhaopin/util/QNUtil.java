package com.qianniu.zhaopin.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

public class QNUtil {
	public static SpannableStringBuilder formateStringColor(String source , int start, int end, int color) {
		SpannableStringBuilder style = new SpannableStringBuilder(source);
		style.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return style;
	}
	public static String removeHtmlTag(String resource) {
		return resource.replaceAll("<[^>]+>","");
	}
	public static String handleCharacter(String resource) {
		if (TextUtils.isEmpty(resource)) {
			return "";
		}
		return resource.replace("&nbsp;", "").replace(" ", "").replaceAll("<[^>]+>","");
	}
}

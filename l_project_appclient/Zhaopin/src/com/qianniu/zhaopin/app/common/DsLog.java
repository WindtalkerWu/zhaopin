package com.qianniu.zhaopin.app.common;

import android.util.Log;

public class DsLog {
	private static MyLogger logger = MyLogger.getLogger("DsLog");

	public static void v(Object owner, String content) {
		// DsLog.v(owner.toString(), content);
		logger.v("[" + owner.toString() + "] " + logFormat(content));
	}

	public static void d(Object owner, String content) {
		// DsLog.d(owner.toString(), content);
		logger.d("[" + owner.toString() + "] " + logFormat(content));
	}

	public static void i(Object owner, String content) {
		// DsLog.i(owner.toString(), content);
		logger.i("[" + owner.toString() + "] " + logFormat(content));
	}

	public static void w(Object owner, String content) {
		// DsLog.w(owner.toString(), content);
		logger.w("[" + owner.toString() + "] " + logFormat(content));
	}

	public static void e(Object owner, String content) {
		// DsLog.e2(owner.toString(), content);
		logger.e("[" + owner.toString() + "] " + logFormat(content));
	}

	private static String prettyArray(String[] array) {
		if (array.length == 0) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder("[");
		int len = array.length - 1;
		for (int i = 0; i < len; i++) {
			sb.append(array[i]);
			sb.append(", ");
		}
		sb.append(array[len]);
		sb.append("]");

		return sb.toString();
	}

	private static String logFormat(String format, Object... args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof String[]) {
				args[i] = prettyArray((String[]) args[i]);
			}
		}
		String s = String.format(format, args);
		s = "[" + Thread.currentThread().getId() + "] " + s;
		return s;
	}

	public static void v(String owner, String format, Object... args) {
		logger.v("[" + owner.toString() + "] " + logFormat(format, args));
	}

	public static void d(String owner, String format, Object... args) {
		logger.d("[" + owner.toString() + "] " + logFormat(format, args));
	}

	public static void i(String owner, String format, Object... args) {
		logger.i("[" + owner.toString() + "] " + logFormat(format, args));
	}

	public static void w(String owner, String format, Object... args) {
		logger.w("[" + owner.toString() + "] " + logFormat(format, args));
	}

	public static void e(String owner, String format, Object... args) {
		logger.e("[" + owner.toString() + "] " + logFormat(format, args));
	}

	public static void e2(String owner, String format, Object... args) {
		logger.w("[" + owner.toString() + "] " + logFormat(format, args));
	}

	public static boolean isLoggable(String module, int level) {
		return true;
	}

	public static void v(String content) {
		logger.v(logFormat(content));
	}

	public static void d(String content) {
		logger.d(logFormat(content));
	}

	public static void i(String content) {
		logger.i(logFormat(content));
	}

	public static void w(String content) {
		logger.w(logFormat(content));
	}

	public static void e(String content) {
		logger.e(logFormat(content));
	}

	// for tmp log
	public static void t(Object owner, String content) {
		v(owner, content);
	}

	public static void t(String owner, String format, Object... args) {
		v(owner, format, args);
	}

	public static void t(String content) {
		v(content);
	}

}

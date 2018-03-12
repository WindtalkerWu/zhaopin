package com.qianniu.zhaopin.app.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.qianniu.zhaopin.app.ConfigOptions;

import android.os.Environment;
import android.util.Log;



public class MyLogger {
	public final static String tag = "[matrixdigi]";
	private final static String logStorePath = Environment
			.getExternalStorageDirectory() + "/matrixdigi/logs";
	private final static String logFileNamePrefix = "matrix_";
	private static String logFile = null;
	private final static int logLevel = Log.VERBOSE;
	private static Hashtable<String, MyLogger> sLoggerTable = null;
	private String mClassName;

	public static MyLogger getLogger(String className) {
		if (sLoggerTable == null) {
			sLoggerTable = new Hashtable<String, MyLogger>();

/*			File dir = new File(logStorePath);
			if (dir.exists()) {
				if (dir.isDirectory()) {
					File[] files = new File(logStorePath).listFiles();
					if (files != null && files.length > 0) {
						Date today = new Date();
						Calendar cal = Calendar.getInstance();
						cal.setTime(today);
						cal.add(Calendar.DATE, -1);
						Date yestoday = cal.getTime();
						for (File file : files) {
							if (!file.isHidden() && file.isFile()) {
								if (file.getName().contains(logFileNamePrefix)
										&& file.getName().length() >= 15) {
									Date fileDate = Utils.stringToDate(file
											.getName().substring(7, 15));
									if (fileDate.before(yestoday)) {
										file.delete();
									}
								}
							}
						}
					}
				}
			}*/

			Date today = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			logFile = String.format(logStorePath + "/" + logFileNamePrefix
					+ "%s.log.txt", formatter.format(today));
		}
		MyLogger classLogger = (MyLogger) sLoggerTable.get(className);
		if (classLogger == null) {
			classLogger = new MyLogger(className);
			sLoggerTable.put(className, classLogger);
		}
		return classLogger;
	}
	private MyLogger(String name) {
		mClassName = name;
	}
	public static void i(String tag, String msg) {
		if (ConfigOptions.logFlag) {
			Log.i(tag, msg);
		}
	}
	public static void d(String tag, String msg) {
		if (ConfigOptions.logFlag) {
			Log.d(tag, msg);
		}
	}
	public static void e(String tag, String msg) {
		if (ConfigOptions.logFlag) {
			Log.e(tag, msg);
		}
	}
	public static void v(String tag, String msg) {
		if (ConfigOptions.logFlag) {
			Log.v(tag, msg);
		}
	}
	private String getFunctionName() {
/*		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.getFileName().equals("DsLog.java")) {
				return null;
			}
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}
			return "[ " + Thread.currentThread().getName() + ": "
					+ st.getFileName() + ":" + st.getLineNumber() + " ]";
		}*/
		return mClassName;
	}

	public void i(Object str) {
		if (ConfigOptions.logFlag) {
			if (logLevel <= Log.INFO) {
				String name = getFunctionName();
				if (name != null) {
					Log.i(tag, name + " - " + str);
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(name + " - " + str);
					}
				} else {
					Log.i(tag, str.toString());
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(str.toString());
					}
				}
			}
		}

	}
	public void i(String str) {
		if (ConfigOptions.logFlag) {
			if (logLevel <= Log.INFO) {
				String name = getFunctionName();
				if (name != null) {
					Log.i(tag, name + " - " + str);
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(name + " - " + str);
					}
				} else {
					Log.i(tag, str);
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(str.toString());
					}
				}
			}
		}

	}

	public void v(Object str) {
		if (ConfigOptions.logFlag) {
			if (logLevel <= Log.VERBOSE) {
				String name = getFunctionName();
				if (name != null) {
					Log.v(tag, name + " - " + str);
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(name + " - " + str);
					}
				} else {
					Log.v(tag, str.toString());
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(str.toString());
					}
				}
			}
		}
	}

	public void w(Object str) {
		if (ConfigOptions.logFlag) {
			if (logLevel <= Log.WARN) {
				String name = getFunctionName();
				if (name != null) {
					Log.w(tag, name + " - " + str);
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(name + " - " + str);
					}
				} else {
					Log.w(tag, str.toString());
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(str.toString());
					}
				}
			}
		}
	}

	public void e(Object str) {
		if (ConfigOptions.logFlag) {
			if (logLevel <= Log.ERROR) {
				String name = getFunctionName();
				if (name != null) {
					Log.e(tag, name + " - " + str);
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(name + " - " + str);
					}
				} else {
					Log.e(tag, str.toString());
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(str.toString());
					}
				}
			}
		}
	}

	public void e(Exception ex) {
		if (ConfigOptions.logFlag) {
			if (logLevel <= Log.ERROR) {
				Log.e(tag, "error", ex);
				if (ConfigOptions.logWriteToFile) {
					writeLogToFile(ex);
				}
			}
		}
	}

	public void e(String log, Throwable tr) {
		if (ConfigOptions.logFlag) {
			String line = getFunctionName();
			Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}"
					+ "[" + mClassName + line + ":] " + log + "\n", tr);
			if (ConfigOptions.logWriteToFile) {
				writeLogToFile("{Thread:" + Thread.currentThread().getName()
						+ "}" + "[" + mClassName + line + ":] " + log + "\n"
						+ Log.getStackTraceString(tr));
			}
		}
	}

	public void d(Object str) {
		if (ConfigOptions.logFlag) {
			if (logLevel <= Log.DEBUG) {
				String name = getFunctionName();
				if (name != null) {
					Log.d(tag, name + " - " + str);
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(name + " - " + str);
					}
				} else {
					Log.d(tag, str.toString());
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(str.toString());
					}
				}
			}
		}
	}
	public void d(String str) {
		if (ConfigOptions.logFlag) {
			if (logLevel <= Log.DEBUG) {
				String name = getFunctionName();
				if (name != null) {
					Log.d(tag, name + " - " + str);
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(name + " - " + str);
					}
				} else {
					Log.d(tag, str.toString());
					if (ConfigOptions.logWriteToFile) {
						writeLogToFile(str.toString());
					}
				}
			}
		}
	}

	private void writeLogToFile(Object str) {
		// �����sdCard��û��logĿ¼������дlog
		File dir = new File(logStorePath);
		if (!dir.exists()) {
			return;
		}
		FileOutputStream fos = null;
		PrintWriter pw = null;
		try {
			File fil = new File(logFile);
			if (!fil.exists()) {
				try {
					fil.createNewFile();
				} catch (Exception e) {
//					e.printStackTrace();
					// Log.d(tag, "û��SDCard!");
					return;
				}
			}
			fos = new FileOutputStream(fil, true);
			pw = new PrintWriter(fos);
			if (pw != null) {
				pw.print(str + "\r\n");
				pw.flush();
			}
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (pw != null) {
					pw.close();
				}
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
	}
}

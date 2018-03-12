package com.qianniu.zhaopin.app.common;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.AppManager;
import com.qianniu.zhaopin.app.bean.InsidersAndCompany;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.bean.VersionData;
import com.qianniu.zhaopin.app.database.DBUtils;
import com.qianniu.zhaopin.app.gossip.GossipMainActivity;
import com.qianniu.zhaopin.app.service.ConnectionService;
import com.qianniu.zhaopin.app.ui.AppRecommentActivity;
import com.qianniu.zhaopin.app.ui.BaseActivity;
import com.qianniu.zhaopin.app.ui.CampaignDetailActivity;
import com.qianniu.zhaopin.app.ui.CampaignListActivity;
import com.qianniu.zhaopin.app.ui.CommentListActivity;
import com.qianniu.zhaopin.app.ui.CompanyListActivity;
import com.qianniu.zhaopin.app.ui.CustomManagerActivity;
import com.qianniu.zhaopin.app.ui.DeliveryAddressListActivity;
import com.qianniu.zhaopin.app.ui.ForumInfoDetailActivity;
import com.qianniu.zhaopin.app.ui.InfoListActivity;
import com.qianniu.zhaopin.app.ui.InsidersListActivity;
import com.qianniu.zhaopin.app.ui.MoreModuleActivity;
import com.qianniu.zhaopin.app.ui.MyIntegralActivity;
import com.qianniu.zhaopin.app.ui.ProfessionalInfoActivity;
import com.qianniu.zhaopin.app.ui.ResumeEditHomeActivity;
import com.qianniu.zhaopin.app.ui.ResumeEditMainActivity;
import com.qianniu.zhaopin.app.ui.ResumeListActivity;
import com.qianniu.zhaopin.app.ui.ResumePreviewActivity;
import com.qianniu.zhaopin.app.ui.SeekWorthyActivity;
import com.qianniu.zhaopin.app.ui.SettingActivity;
import com.qianniu.zhaopin.app.ui.SubscriptionActivity;
import com.qianniu.zhaopin.app.ui.TaxActivity;
import com.qianniu.zhaopin.app.ui.UserLoginActivity;
import com.qianniu.zhaopin.app.ui.UserRegisterActivity;
import com.qianniu.zhaopin.app.ui.exposurewage.ExposureWageHomeActivity;
import com.qianniu.zhaopin.app.ui.integrationmall.IntegrationSearchActivity;
import com.qianniu.zhaopin.app.view.ExplainInfoLeftPop;
import com.qianniu.zhaopin.app.view.ExplainInfoRightPop;
import com.qianniu.zhaopin.app.widget.ScreenShotView;
import com.qianniu.zhaopin.app.widget.ScreenShotView.OnScreenShotListener;
import com.qianniu.zhaopin.thp.SinaWeiboShareActivity;
import com.qianniu.zhaopin.thp.TencentWeiboShareActivity;
import com.qianniu.zhaopin.thp.UmShare;
import com.qianniu.zhaopin.thp.WeChatShare;
import com.qianniu.zhaopin.wxapi.WXEntryActivity;
import com.qianniu.zhaopin.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
/**
 * @author Administrator
 * 
 */
public class UIHelper {
	/**
	 * listview 显示界面当前动作的类型
	 */
	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;
	/**
	 * listview 显示界面当前数据显示的状态
	 */
	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;

	/**
	 * listview 显示界面的类型
	 */
	public final static int LISTVIEW_DATATYPE_FORUMTYPES = 0x01;
	public final static int LISTVIEW_DATATYPE_FORUMINFO = 0x02;
	public final static int LISTVIEW_DATATYPE_EXTRACATTYPES = 0x03;

	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;

	/** 表情图片匹配 */
	private static Pattern facePattern = Pattern
			.compile("\\[{1}([0-9]\\d*)\\]{1}");

	/** 全局web样式 */
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
			+ "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
			+ "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} "
			+ "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

	/** label flow 背景 */
	public static int[] labelFlowImages = { R.drawable.blue_button,
			R.drawable.orange_button, R.drawable.green_button,
			R.drawable.blue_violet_button, R.drawable.pink_button };

	/**
	 * 发送App异常崩溃报告
	 * 
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont,
			final String crashReport) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(cont,AlertDialog.THEME_HOLO_LIGHT);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 发送异常报告
						Intent i = new Intent(Intent.ACTION_SEND);
						// i.setType("text/plain"); //模拟器
						i.setType("message/rfc822"); // 真机
						i.putExtra(Intent.EXTRA_EMAIL,
								new String[] { "jxsmallmouse@163.com" });
						i.putExtra(Intent.EXTRA_SUBJECT,
								"开源中国Android客户端 - 错误报告");
						i.putExtra(Intent.EXTRA_TEXT, crashReport);
						cont.startActivity(Intent.createChooser(i, "发送错误报告"));
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.setNegativeButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 退出
						AppManager.getAppManager().AppExit(cont);
					}
				});
		builder.show();
	}

	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}

	/**
	 * 点击返回监听事件
	 * 
	 * @param activity
	 * @return
	 */
	public static View.OnClickListener finish(final Activity activity) {
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		};
	}

	/**
	 * activity 退出选择提示框
	 * 
	 * @param activity
	 */
	public static void showQuitAlertDialog(final Activity activity) {
		Dialog dialog = MethodsCompat.getAlertDialogBuilder(activity,AlertDialog.THEME_HOLO_LIGHT)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(R.string.dialog_quitedittitle)
				.setMessage(R.string.dialog_quiteditmsg)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								activity.finish();
							}
						})
				.setNegativeButton(R.string.dialog_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();
		dialog.show();
	}

	/**
	 * url跳转
	 * 
	 * @param context
	 * @param url
	 */
	public static void showUrlRedirect(Context context, String url) {
		URLs urls = URLs.parseURL(url);
		if (urls != null) {
			showLinkRedirect(context, urls.getObjType(), urls.getObjId(),
					urls.getObjKey());
		} else {
			openBrowser(context, url);
		}
	}

	public static void showLinkRedirect(Context context, int objType,
			int objId, String objKey) {
		switch (objType) {
		/*
		 * case URLs.URL_OBJ_TYPE_NEWS: showNewsDetail(context, objId); break;
		 * case URLs.URL_OBJ_TYPE_QUESTION: showQuestionDetail(context, objId);
		 * break; case URLs.URL_OBJ_TYPE_QUESTION_TAG:
		 * showQuestionListByTag(context, objKey); break; case
		 * URLs.URL_OBJ_TYPE_SOFTWARE: showSoftwareDetail(context, objKey);
		 * break; case URLs.URL_OBJ_TYPE_ZONE: showUserCenter(context, objId,
		 * objKey); break; case URLs.URL_OBJ_TYPE_TWEET:
		 * showTweetDetail(context, objId); break; case URLs.URL_OBJ_TYPE_BLOG:
		 * showBlogDetail(context, objId); break;
		 */
		case URLs.URL_OBJ_TYPE_OTHER:
			openBrowser(context, objKey);
			break;
		}
	}

	/**
	 * 打开浏览器
	 * 
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			ToastMessage(context, "无法浏览此网页", 500);
		}
	}

	/**
	 * 获取webviewClient对象
	 * 
	 * @return
	 */
	public static WebViewClient getWebViewClient() {
		return new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				showUrlRedirect(view.getContext(), url);
				return true;
			}
		};
	}

	/**
	 * 获取TextWatcher对象
	 * 
	 * @param context
	 * @param tmlKey
	 * @return
	 */
	public static TextWatcher getTextWatcher(final Activity context,
			final String temlKey) {
		return new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 保存当前EditText正在编辑的内容
				((AppContext) context.getApplication()).setProperty(temlKey,
						s.toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		};
	}

	/**
	 * 清除文字
	 * 
	 * @param cont
	 * @param editer
	 */
	public static void showClearWordsDialog(final Context cont,
			final EditText editer, final TextView numwords) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(cont,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(R.string.clearwords);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 清除文字
						editer.setText("");
						numwords.setText("160");
					}
				});
		builder.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.show();
	}

	/**
	 * 显示对话框
	 * 
	 * @param context
	 * @param title
	 * @param onClickListener
	 */
	public static AlertDialog.Builder showCommonDialog(final Context context,
			int resId, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("提示");
		builder.setMessage(resId);
		builder.setPositiveButton(R.string.sure, onClickListener);
		builder.setNegativeButton(R.string.cancle, onClickListener);
		builder.setCancelable(false);
		builder.show();
		return builder;
	}
	public static AlertDialog.Builder showCommonDialog(final Context context,
			 int titleId, int msgId, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(titleId);
		builder.setMessage(msgId);
		builder.setPositiveButton(R.string.sure, onClickListener);
		builder.setNegativeButton(R.string.cancle, onClickListener);
		builder.setCancelable(false);
		//builder.show();
		return builder;
	}
	public static AlertDialog.Builder showCommonDialog(final Context context,
			View view, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("提示");
		builder.setView(view);
		builder.setPositiveButton(R.string.sure, onClickListener);
		builder.setNegativeButton(R.string.cancle, onClickListener);
		builder.setCancelable(false);
		builder.show();
		return builder;
	}

	public static AlertDialog.Builder showCommonDialog(final Context context,
			int title, String message, int resPositiveButtonId,
			int resNegativeButtonId,
			DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(resPositiveButtonId, onClickListener);
		builder.setNegativeButton(resNegativeButtonId, onClickListener);
		builder.show();
		return builder;
	}

	public static AlertDialog.Builder showCommonDialog(final Context context,
			int resId, int resPositiveButtonId, int resNegativeButtonId,
			DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("提示");
		builder.setMessage(resId);
		builder.setPositiveButton(resPositiveButtonId, onClickListener);
		builder.setNegativeButton(resNegativeButtonId, onClickListener);
		builder.show();
		return builder;
	}

	public static AlertDialog.Builder showCommonDialog(final Context context,
			View view, int resPositiveButtonId, int resNegativeButtonId,
			DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("提示");
		builder.setView(view);
		builder.setPositiveButton(resPositiveButtonId, onClickListener);
		builder.setNegativeButton(resNegativeButtonId, onClickListener);
		builder.show();
		return builder;
	}

	public static AlertDialog.Builder showAgreementDialog(
			final Context context, int titleId, int contentId,
			DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(titleId);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.agreement_dialog, null);
		builder.setView(view);
		TextView content = (TextView) view
				.findViewById(R.id.agreement_dialog_content);
		content.setText(contentId);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			content.setTextColor(context.getResources().getColorStateList(R.color.dialog_content_color));
		}
		builder.setPositiveButton(R.string.sure, onClickListener);
		
		return builder;
	}
	public static AlertDialog showCommonScrollDialog(
			final Context context, int titleId, String contentStr, int resPositiveButtonId,
			int resNegativeButtonId, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(titleId);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.common_scroll_dialog, null);
		builder.setView(view);
		TextView content = (TextView) view
				.findViewById(R.id.common_scroll_content);
		content.setText(contentStr);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			content.setTextColor(context.getResources().getColorStateList(R.color.dialog_content_color));
		}
		builder.setPositiveButton(resPositiveButtonId, onClickListener);
		builder.setNegativeButton(resNegativeButtonId, onClickListener);
		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}
	public static AlertDialog showCommonAlertDialog(final Context context,
			int title, String message, int resPositiveButtonId,
			int resNegativeButtonId,
			DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(resPositiveButtonId, onClickListener);
		builder.setNegativeButton(resNegativeButtonId, onClickListener);
		AlertDialog dialog = builder.create();
		dialog.show();
		return dialog;
	}
	/**
	 * 版本更新
	 * @param activity
	 * @param versionData
	 */
	public static AlertDialog showUpdateVersionDialog(final Activity activity,
			final VersionData versionData) {
		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					ConnectionService.startServiceDownload(activity,
							versionData.getDownloadurl(), null);
				}
//				if (activity instanceof UpdateVersionActivity || isFinish) {
//					activity.finish();
//				}
				dialog.dismiss();
			}
		};
		AlertDialog dialog = showCommonScrollDialog(activity, R.string.apk_update_title,
				versionData.getMemo(), R.string.sure_update,
				R.string.cancle_update, onClickListener);
		return dialog;
	}
	/**
	 * 版本强制更新
	 * @param activity
	 * @param versionData
	 */
//	public static void showForceUpdateVersionDialog(final Activity activity,
//			final VersionData versionData) {
//		AlertDialog.Builder builder = null;
//		DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which == DialogInterface.BUTTON_POSITIVE) {
//					ConnectionService.startServiceDownload(activity,
//							versionData.getDownloadurl(), null);
//				}
//				activity.finish();
//				dialog.dismiss();
//			}
//		};
//		showCommonDialog(activity, R.string.apk_update_title,
//				versionData.getMemo(), R.string.sure_update,
//				R.string.cancle_update, onClickListener);
//	}

	public static void startUpdateVersionActivity(Context context,
			VersionData versionData) {
		Intent intent = new Intent("qn.update.version");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("versionData", versionData);
		context.startActivity(intent);
	}

	public static boolean checkIsLogin(AppContext mContext, Activity from,
			int requestCode) {
		if (mContext.isLogin()) {
			return true;
		} else {
			startLoginActivity(from, requestCode);
			return false;
		}
	}

	public static void startLoginActivity(Activity from, int requestCode) {
		Intent intent = new Intent(from, UserLoginActivity.class);
		from.startActivityForResult(intent, requestCode);
	}

	/**
	 * 显示登录界面activity
	 * 
	 * @param context
	 */
	public static void showLoginActivity(Context context) {
		Intent intent = new Intent(context, UserLoginActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示登录界面activity,返回RequestCode：ActivityRequestCode.RESULT_ACTIVITY_LOGIN
	 * 
	 * @param context
	 */
	public static void showLoginActivityForResult(Activity activity) {
		Intent intent = new Intent(activity, UserLoginActivity.class);
		activity.startActivityForResult(intent,
				ActivityRequestCode.RESULT_ACTIVITY_LOGIN);
	}


	/**
	 * 显示注册界面
	 * 
	 * @param context
	 */
	public static void showRegisterActivity(Context context) {
		Intent intent = new Intent(context, UserRegisterActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示信息二级list
	 * 
	 * @param context
	 * 
	 */
	public static void showInfolist(Context context, String infoId,
			String title, int customState) {
		Intent intent = new Intent(context, InfoListActivity.class);
		intent.putExtra("info_id", infoId);
		intent.putExtra("info_title", title);
		intent.putExtra("type", InfoListActivity.DISPLAY_TYPE_INFO);
		intent.putExtra("custom_state", customState);
		context.startActivity(intent);
	}

	public static void showInfolist(Context context, String infoId,
			String title, int customState, int newcount) {
		Intent intent = new Intent(context, InfoListActivity.class);
		intent.putExtra("info_id", infoId);
		intent.putExtra("info_title", title);
		intent.putExtra("type", InfoListActivity.DISPLAY_TYPE_INFO);
		intent.putExtra("custom_state", customState);
		intent.putExtra("newcount", newcount);
		context.startActivity(intent);
	}

	/**
	 * 显示号外信息二级list
	 * 
	 * @param context
	 * 
	 */
	public static void showExtraInfolist(Context context, String infoId,
			String title, int customState) {
		AppContext appcontext = (AppContext) context.getApplicationContext();
		if (!appcontext.isNetworkConnected()) {
			UIHelper.ToastMessage(context, R.string.app_status_net_disconnected);
			return;
		}
		Intent intent = new Intent(context, InfoListActivity.class);
		intent.putExtra("info_id", infoId);
		intent.putExtra("info_title", title);
		intent.putExtra("type", InfoListActivity.DISPLAY_TYPE_EXTRA);
		intent.putExtra("custom_state", customState);
		context.startActivity(intent);
	}

	/**
	 * 显示信息详情
	 * 
	 * @param context
	 * @param newsId
	 */
	public static void showInfoDetial(Context context, String infoId,
			String title, String infoUrl) {

		Intent intent = new Intent(context, ForumInfoDetailActivity.class);
		intent.putExtra("info_id", infoId);
		intent.putExtra("info_title", title);
		intent.putExtra("info_url", infoUrl);
		context.startActivity(intent);
	}

	/**
	 * 跳转到订阅管理
	 * 
	 * @param context
	 */
	public static void showCustomManagerActivity(Context context) {
		Intent intent = new Intent(context, CustomManagerActivity.class);
		context.startActivity(intent);
	}

	public static void showCustomManagerActivityBehindCheck(Activity activity) {
		AppContext mappcontext = (AppContext) activity.getApplication();
		if (mappcontext.isLoginAndToLogin(activity)) {
			Intent intent = new Intent(activity, CustomManagerActivity.class);
			activity.startActivity(intent);
		}
	}

	public static void showMyResumeEditActivity(Context context, int resume_id) {
		Intent intent = new Intent(context, ResumeEditHomeActivity.class);
		intent.putExtra("resume_id", resume_id);
		context.startActivity(intent);
	}

	/**
	 * 启动简历编辑，带返回参数
	 * 
	 * @param context
	 * @param resume_id
	 */
	public static void showMyResumeEditActivityForResult(Activity context,
			int resume_id) {
		Intent intent = new Intent(context, ResumeEditMainActivity.class);
		//Intent intent = new Intent(context, ResumeEditHomeActivity.class);
		intent.putExtra("resume_id", resume_id);
		context.startActivityForResult(intent,
				ActivityRequestCode.RESULT_ACTIVITY_NEWRESUME);
	}

	/**
	 * 启动简历库界面
	 * 
	 * @param context
	 */
	public static void showResumeListActivity(Context context) {
		Intent intent = new Intent(context, ResumeListActivity.class);
		context.startActivity(intent);
	}

	public static void showResumeListActivityBehindCheck(Activity activity) {
		AppContext mappcontext = (AppContext) activity.getApplication();
		if (mappcontext.isLoginAndToLogin(activity)) {
			Intent intent = new Intent(activity, ResumeListActivity.class);
			activity.startActivity(intent);
		}
	}
	public static void showResumeListActivityBehindCheck(Context activity) {
		AppContext mappcontext = (AppContext) activity.getApplicationContext();
		if (mappcontext.isLoginAndToLogin(activity)) {
			Intent intent = new Intent(activity, ResumeListActivity.class);
			activity.startActivity(intent);
		}
	}


	/**
	 * 启动简历预览界面
	 * 
	 * @param context
	 * @param entity
	 */
	public static void showResumePreview(Context context,
			ResumeSimpleEntity entity) {

		Intent intent = new Intent(context, ResumePreviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("entity", entity);
		intent.putExtras(bundle);

		context.startActivity(intent);
	}

	public static void showResumePreviewForResult(Activity context,
			String url,String resumeid) {

		Intent intent = new Intent(context, ResumePreviewActivity.class);
		Bundle bundle = new Bundle();
		//bundle.putSerializable("entity", entity);
		bundle.putString("url", url);
		bundle.putString("resumeId", resumeid);
		intent.putExtras(bundle);
		context.startActivityForResult(intent,
				ActivityRequestCode.RESULT_ACTIVITY_RESUMEPREVIEW);

	}

	public static void showPopTips(Context context, View v, String content) {
		ExplainInfoLeftPop pop = new ExplainInfoLeftPop(context);
		pop.setExplainInfoText(content);
		int btn_width = v.getWidth() / 2;
		DisplayMetrics dm = AppContext
				.getPhoneDisplayMetrics((AppContext) (context
						.getApplicationContext()));
		pop.showAsDropDown(v, 0, CommonUtils.dip2px(context, -2));
		pop.update();
	}
	public static void showRightPopTips(Context context, View v, String content) {
		ExplainInfoRightPop pop = new ExplainInfoRightPop(context);
		pop.setExplainInfoText(content);
		int btn_width = v.getWidth() / 2;
		DisplayMetrics dm = AppContext
				.getPhoneDisplayMetrics((AppContext) (context
						.getApplicationContext()));
		pop.showAsDropDown(v, 0, CommonUtils.dip2px(context, -6));
		pop.update();
	}
	public static void sendEmail(Activity activity, String emailAddress, String title, String content) {
		try {
			Intent data = new Intent(Intent.ACTION_SENDTO);
//			Intent data = new Intent(Intent.ACTION_SEND);
//			String[] tos = { emailAddress };
//			data.putExtra(Intent.EXTRA_EMAIL, tos); 
//			data.putExtra(Intent.EXTRA_TEXT, content);  
//			data.putExtra(Intent.EXTRA_SUBJECT, title);
//	        data.setType("image/*");  
//	        data.setType("message/rfc882"); 
//	        Intent.createChooser(data, "请选择");
	        data.setData(Uri.parse("mailto:" + emailAddress));
	        data.putExtra(Intent.EXTRA_SUBJECT, title);  
	        data.putExtra(Intent.EXTRA_TEXT, content); 
	        activity.startActivity(data);
		} catch(Exception e) {
			UIHelper.ToastMessage(activity, "抱歉，没有找到发送邮件应用!");
		}
	}

	/**
	 * 判断是否有网络
	 * 
	 * @param appContext
	 * @param cont
	 */
	public static boolean isNetworkConnected(AppContext appContext) {
		if (!appContext.isNetworkConnected()) {
			UIHelper.ToastMessage(appContext, R.string.network_not_connected);

			return false;
		}
		return true;
	}

	/**
	 * 显示设置界面activity
	 * 
	 * @param context
	 */
	public static void showSetActivity(Context context) {
		Intent intent = new Intent(context, SettingActivity.class);
		// Intent intent = new Intent(context, IndustryInsidersActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 分享到'新浪微博'或'腾讯微博'的对话框
	 * 
	 * @param context
	 *            当前Activity
	 * @param title
	 *            分享的标题
	 * @param msg
	 *            分享的内容
	 */
	public static void showShareDialog(final Activity context,
			final String title, final String msg) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle(context.getString(R.string.share));
		String strPackageName = context.getPackageName();
		String strAPPName = context.getString(R.string.app_name);
		if(null != strPackageName && 
				strPackageName.equals(HeadhunterPublic.PACKAGENAME) &&
				null != strAPPName &&
				strAPPName.equals(HeadhunterPublic.APP_NAME)){
			builder.setItems(R.array.app_share_items,
					new DialogInterface.OnClickListener() {
	
						public void onClick(DialogInterface arg0, int arg1) {
							switch (arg1) {
							case 0:// 新浪微博
								{
									// 友盟统计--分享--新浪微博按钮
									UmShare.UmStatistics(context, "Share_SinaWeiboButton");
									
									Intent i = new Intent(context,
											SinaWeiboShareActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("content", msg);
									i.putExtras(bundle);
									context.startActivity(i);
								}
								break;
							case 1:// 腾讯微博
								{
									// 友盟统计--分享--腾讯微博按钮
									UmShare.UmStatistics(context, "Share_TencentWeiboButton");
									
									Intent i = new Intent(context,
											TencentWeiboShareActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("content", msg);
									i.putExtras(bundle);
									context.startActivity(i);
								}
								break;
							case 2: // 微信朋友
								{
									// 友盟统计--分享--微信朋友按钮
									UmShare.UmStatistics(context, "Share_WeChatButton");
									
		//							Intent i = new Intent(context,
		//									WeChatShareActivity.class);
		//							Bundle bundle = new Bundle();
		//							bundle.putString("content", msg);
		//							i.putExtras(bundle);
		//							context.startActivity(i);
									WeChatShare wcs = new WeChatShare(context, context.getIntent());
									wcs.ShareText(msg, false);
								}
								break;
							case 3: // 微信朋友圈
								{
									// 友盟统计--分享--微信朋友圈按钮
									UmShare.UmStatistics(context, "Share_WeChatFriendCircleButton");
		
									WeChatShare wcs = new WeChatShare(context, context.getIntent());
									wcs.ShareText(msg, true);
								}
								break;
							case 4:// 更多
								{
									// 友盟统计--分享--更多按钮
									UmShare.UmStatistics(context, "Share_MoreButton");
	
									showShareMore(context, title, msg);
									break;
								}
							}
						}
					});
		}else{
			builder.setItems(R.array.app_share_items_two,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							switch (arg1) {
							case 0:// 新浪微博
								{
									// 友盟统计--分享--新浪微博按钮
									UmShare.UmStatistics(context, "Share_SinaWeiboButton");
									
									Intent i = new Intent(context,
											SinaWeiboShareActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("content", msg);
									i.putExtras(bundle);
									context.startActivity(i);
								}
								break;
							case 1: // 微信朋友
								{
									// 友盟统计--分享--微信朋友按钮
									UmShare.UmStatistics(context, "Share_WeChatButton");
									
									WeChatShare wcs = new WeChatShare(context, context.getIntent());
									wcs.ShareText(msg, false);
								}
								break;
							case 2: // 微信朋友圈
								{
									// 友盟统计--分享--微信朋友圈按钮
									UmShare.UmStatistics(context, "Share_WeChatFriendCircleButton");
		
									WeChatShare wcs = new WeChatShare(context, context.getIntent());
									wcs.ShareText(msg, true);
								}
								break;
							case 3:// 更多
								{
									// 友盟统计--分享--更多按钮
									UmShare.UmStatistics(context, "Share_MoreButton");

									showShareMore(context, title, msg);
									break;
								}
							}
						}
					});
		}
		builder.create().show();
	}
	
	/**
	 * 分享到'新浪微博'或'腾讯微博'的对话框(微信加二维码图片)
	 * 
	 * @param context
	 *            当前Activity
	 * @param title
	 *            分享的标题
	 * @param msg
	 *            分享的内容
	 */
	public static void showShareDialog(final Activity context,
			final String title, final String msg, final String url) {
		AlertDialog.Builder builder = MethodsCompat.getAlertDialogBuilder(context,AlertDialog.THEME_HOLO_LIGHT);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setTitle(context.getString(R.string.share));
		
		String strPackageName = context.getPackageName();
		String strAPPName = context.getString(R.string.app_name);
		if(null != strPackageName && 
				strPackageName.equals(HeadhunterPublic.PACKAGENAME) &&
				null != strAPPName &&
				strAPPName.equals(HeadhunterPublic.APP_NAME)){
			builder.setItems(R.array.app_share_items,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							switch (arg1) {
							case 0:// 新浪微博
								{
									// 友盟统计--分享--新浪微博按钮
									UmShare.UmStatistics(context, "Share_SinaWeiboButton");
									
									Intent i = new Intent(context,
											SinaWeiboShareActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("content", msg);
									i.putExtras(bundle);
									context.startActivity(i);
								}
								break;
							case 1:// 腾讯微博
								{
									// 友盟统计--分享--腾讯微博按钮
									UmShare.UmStatistics(context, "Share_TencentWeiboButton");
									
									Intent i = new Intent(context,
											TencentWeiboShareActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("content", msg);
									i.putExtras(bundle);
									context.startActivity(i);
								}
								break;
							case 2: // 微信朋友
								{
									// 友盟统计--分享--微信朋友按钮
									UmShare.UmStatistics(context, "Share_WeChatButton");
									
									WeChatShare wcs = new WeChatShare(context, context.getIntent());
									//Bitmap bitmap = wcs.createQRCodeImage(url);
									Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
											R.drawable.share_logo);
									if(null != bitmap){
										wcs.ShareTextAndBmp(title, url, msg, bitmap, false);
									}else{
										wcs.ShareText(msg, false);
									}
								}
								break;
							case 3: // 微信朋友圈
								{
									// 友盟统计--分享--微信朋友圈按钮
									UmShare.UmStatistics(context, "Share_WeChatFriendCircleButton");
		
									WeChatShare wcs = new WeChatShare(context, context.getIntent());
									
									//Bitmap bitmap = wcs.createQRCodeImage(url);
									Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
											R.drawable.share_logo);
									if(null != bitmap){
										wcs.ShareTextAndBmp(title, url, msg, bitmap, true);
									}else{
										wcs.ShareText(msg, true);
									}
								}
								break;
							case 4:// 更多
								{
									// 友盟统计--分享--更多按钮
									UmShare.UmStatistics(context, "Share_MoreButton");

									showShareMore(context, title, msg);
									break;
								}
							}
						}
					});
		}else{
			builder.setItems(R.array.app_share_items_two,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							switch (arg1) {
							case 0:// 新浪微博
								{
									// 友盟统计--分享--新浪微博按钮
									UmShare.UmStatistics(context, "Share_SinaWeiboButton");
									
									Intent i = new Intent(context,
											SinaWeiboShareActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString("content", msg);
									i.putExtras(bundle);
									context.startActivity(i);
								}
								break;
							case 1: // 微信朋友
								{
									// 友盟统计--分享--微信朋友按钮
									UmShare.UmStatistics(context, "Share_WeChatButton");
									
									WeChatShare wcs = new WeChatShare(context, context.getIntent());
									Bitmap bitmap = wcs.createQRCodeImage(url);
									if(null != bitmap){
										wcs.ShareTextAndBmp(title, url, msg, bitmap, false);
									}else{
										wcs.ShareText(msg, false);
									}
								}
								break;
							case 2: // 微信朋友圈
								{
									// 友盟统计--分享--微信朋友圈按钮
									UmShare.UmStatistics(context, "Share_WeChatFriendCircleButton");
		
									WeChatShare wcs = new WeChatShare(context, context.getIntent());
									
									Bitmap bitmap = wcs.createQRCodeImage(url);
									if(null != bitmap){
										wcs.ShareTextAndBmp(title, url, msg, bitmap, true);
									}else{
										wcs.ShareText(msg, true);
									}
								}
								break;
							case 3:// 更多
								{
									// 友盟统计--分享--更多按钮
									UmShare.UmStatistics(context, "Share_MoreButton");

									showShareMore(context, title, msg);
									break;
								}
							}
						}
					});
		}
		builder.create().show();
	}

	/**
	 * 调用系统安装了的应用分享
	 * 
	 * @param context
	 * @param title
	 * @param url
	 */
	public static void showShareMore(Activity context, final String title,
			final String msg) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		/*
		 * Resources r= context.getResources(); Uri uri =
		 * Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
		 * r.getResourcePackageName(R.drawable.ic_launcher) + "/" +
		 * r.getResourceTypeName(R.drawable.ic_launcher) + "/" +
		 * r.getResourceEntryName(R.drawable.ic_launcher));
		 * 
		 * File file = new File("/storage/sdcard0/matrix/image/mayun2.jpg"); uri
		 * = Uri.fromFile(file); intent.putExtra(Intent.EXTRA_STREAM,uri);
		 * intent.setType("image/*");
		 */

		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, msg);

		context.startActivity(Intent.createChooser(intent, "选择分享"));
	}

	/**
	 * 添加截屏功能
	 */
	@SuppressLint("NewApi")
	public static void addScreenShot(Activity context,
			OnScreenShotListener mScreenShotListener) {
		BaseActivity cxt = null;
		if (context instanceof BaseActivity) {
			cxt = (BaseActivity) context;
			cxt.setAllowFullScreen(false);
			ScreenShotView screenShot = new ScreenShotView(cxt,
					mScreenShotListener);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			context.getWindow().addContentView(screenShot, lp);
		}
	}

	/**
	 * 
	 * @param context
	 */
	public static void startInsidersOrCompanyActivity(Activity context,
			int which) {
		Intent intent = null;
		if (which == InsidersAndCompany.TYPEINSIDERS) {
			intent = new Intent(context, InsidersListActivity.class);
		} else {
			intent = new Intent(context, CompanyListActivity.class);
		}
		intent.putExtra("type", which);
		context.startActivity(intent);
	}
	/**
	 * 
	 * @param context
	 */
	public static void startInsidersOrCompanyActivity(Context context,
			int which) {
		Intent intent = null;
		if (which == InsidersAndCompany.TYPEINSIDERS) {
			intent = new Intent(context, InsidersListActivity.class);
		} else {
			intent = new Intent(context, CompanyListActivity.class);
		}
		intent.putExtra("type", which);
		context.startActivity(intent);
	}
	/**
	 * 启动活动activity
	 * 
	 * @param context
	 */
	public static void startCampaignListActivity(Context context) {
		Intent intent = new Intent(context, CampaignListActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 显示活动详情
	 * 
	 * @param context
	 * @param newsId
	 */
	public static void showCampaignDetial(Activity context, String infoId,
			String title, String infoUrl) {

		Intent intent = new Intent(context, CampaignDetailActivity.class);
		intent.putExtra("info_id", infoId);
		intent.putExtra("info_title", title);
		intent.putExtra("info_url", infoUrl);
		context.startActivity(intent);
	}

	/**
	 * 税率计算调转
	 * 
	 * @param context
	 */
	public static void startTaxActivity(Context context) {
		Intent intent = new Intent(context, TaxActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 启动牵牛分享activity
	 * 
	 * @param context
	 */
	public static void startAppRecommentActivity(Context context) {
		Intent intent = new Intent(context, AppRecommentActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 启动行业资讯activity
	 * 
	 * @param context
	 */
	public static void startProfessionalInfoActivity(Context context) {
		Intent intent = new Intent(context, ProfessionalInfoActivity.class);
		context.startActivity(intent);
	}

	/**启动订阅管理activity
	 * @param activity
	 */
	public static void showSubscriptionActivityBehindCheck(Activity activity) {
		AppContext appcontext = (AppContext) activity.getApplication();
		if (appcontext.isLoginAndToLogin(activity)) {
			Intent intent = new Intent(activity ,SubscriptionActivity.class);
			activity.startActivity(intent);
		}
	}
	public static void showSubscriptionActivityBehindCheck(Context activity) {
		AppContext appcontext = (AppContext) activity.getApplicationContext();
		if (appcontext.isLoginAndToLogin(activity)) {
			Intent intent = new Intent(activity ,SubscriptionActivity.class);
			activity.startActivity(intent);
		}
	}
	/**晒工资主界面
	 * @param activity
	 */
	public static void startExposureWageActivity(Context activity){
		Intent intent = new Intent(activity, ExposureWageHomeActivity.class);
		activity.startActivity(intent);
	}
	/**
	 * 启动更多模块activity
	 * 
	 * @param context
	 */
	public static void startMoreModuleActivity(Activity context) {
		Intent intent = new Intent(context, MoreModuleActivity.class);
		context.startActivity(intent);
	}
	
	public static void startExposureWageActivity(Activity context) {
		Intent intent = new Intent(context, ExposureWageHomeActivity.class);
		context.startActivity(intent);
	}
	
	public static void startGossipMainctivity(Activity context) {
		Intent intent = new Intent(context, GossipMainActivity.class);
		context.startActivity(intent);
	}
	/**
	 * 拼接支付宝支付参数
	 * 
	 * @param strOutTradeNo
	 *            订单号
	 * @param strSubject
	 *            标题
	 * @param strBody
	 *            描述
	 * @param strPrice
	 *            借钱
	 * @return
	 */
	public static String getNewOrderInfo(String strOutTradeNo,
			String strSubject, String strBody, String strPrice) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(HeadhunterPublic.ALIPAY_DEFAULT_PARTNER);
		sb.append("\"&out_trade_no=\"");
		sb.append(strOutTradeNo);
		sb.append("\"&subject=\"");
		sb.append(strSubject);
		sb.append("\"&body=\"");
		sb.append(strBody);
		sb.append("\"&total_fee=\"");
		sb.append(strPrice);
		sb.append("\"&notify_url=\"");
		// 服务器异步通知页面路径(网址需要做URL编码)
		sb.append(URLEncoder.encode(HeadhunterPublic.ALIPAY_NOTIFYURL));
		sb.append("\"&service=\"mobile.securitypay.pay");
		sb.append("\"&_input_charset=\"UTF-8");
		sb.append("\"&return_url=\"");
		sb.append(URLEncoder.encode("http://m.alipay.com"));
		sb.append("\"&payment_type=\"1");
		sb.append("\"&seller_id=\"");
		sb.append(HeadhunterPublic.ALIPAY_DEFAULT_SELLER);

		// 如果show_url值为空，可不传
		// sb.append("\"&show_url=\"");
		sb.append("\"&it_b_pay=\"1m");
		sb.append("\"");

		return new String(sb);
	}

	/**
	 * 获取支付宝支付Sing类型
	 * 
	 * @return
	 */
	public static String getSignType() {
		return "sign_type=\"RSA\"";
	}

	/**
	 * 获取支付宝支付返回值状态
	 * 
	 * @param strResult
	 * @return
	 */
	public static String getAlipayResultStatus(String strResult) {
		String strTemp = "";

		if (null == strResult || strResult.isEmpty()) {
			return strTemp;
		}

		int m = strResult.indexOf("{");
		if (0 >= m - 1) {
			return strTemp;
		}

		int n = strResult.indexOf("}");
		if (n <= m - 1) {
			return strTemp;
		}

		strTemp = strResult.substring(m + 1, n);

		return strTemp;
	}

	/**
	 * 获取
	 * 
	 * @return
	 */
	public static String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String key = format.format(date);

		java.util.Random r = new java.util.Random();
		key += r.nextInt();
		key = key.substring(0, 15);

		return key;
	}
	
	/**
	 * 获取支付订单号
	 */
	public static void getPaymentOrderNo(AppContext appContext, Handler handler,
			String strTpye, String strId){
		try{
			
			Result res = appContext.getPaymentOrderNo(strTpye, strId);

			if (res.OK()) {
				// 成功
				handler.sendMessage(handler.obtainMessage(
						HeadhunterPublic.MSG_PAYMENTORDERNO_SUCCESS, res));	
			} else {
				// 失败
				handler.sendMessage(handler.obtainMessage(
							HeadhunterPublic.MSG_PAYMENTORDERNO_FAIL, res.getErrorMessage()));
			}
		}catch (AppException e) {
			e.printStackTrace();
			// 异常
			handler.sendMessage(handler.obtainMessage(
					HeadhunterPublic.MSG_PAYMENTORDERNO_ABNORMAL, e));
		}	
	}
	
	/**
	 * 根据id和类型， 从全局数据中获取名称
	 * 
	 * @param strId
	 * @param nType
	 * @return
	 */
	public static String getIdName(Context context, String strId, int nType) {
		String strTemp = "";
		if (null == strId) {
			return strTemp;
		}

		if (strId.isEmpty()) {
			return strTemp;
		}

		strTemp = strId;

		AppContext ac = (AppContext) context.getApplicationContext();

		try {
			strTemp = ac.getGlobalDataSpecifyInfo(strId, nType,
					DBUtils.KEY_GLOBALDATA_NAME);
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strTemp;
	}
	
	/**
	 * 获取期望城市字符串
	 * 
	 * @param strCity
	 * @return
	 */
	public static String getExpectCity(Context context, String[] strCity) {
		String strTemp = "";
		if (null != strCity) {
			for (int i = 0; i < strCity.length; i++) {
				if (0 == i) {
					strTemp = getIdName(context, strCity[i],
							DBUtils.GLOBALDATA_TYPE_CITY);

				} else {
					strTemp += ","
							+ getIdName(context, strCity[i],
									DBUtils.GLOBALDATA_TYPE_CITY);
				}
			}
		}

		return strTemp;
	}
	
	/**
	 * 跳转评论墙
	 * @param activity
	 * @param type
	 * @param id
	 */
	public static void startCommentWall(Activity activity, int type, String id) {
		InsidersAndCompany ic = new InsidersAndCompany();
		ic.setType(type);
		ic.setId(id);
		startCommentWall(activity, ic);
	}
	public static void startCommentWall(Activity activity, InsidersAndCompany ic) {
		Intent intent = new Intent(activity, CommentListActivity.class);
		intent.putExtra("ic", ic);
		activity.startActivity(intent);
	}
	public static void startIntegrationSearch(Activity activity) {
		Intent intent = new Intent(activity, IntegrationSearchActivity.class);
		activity.startActivity(intent);
	}
	public static void startIntegrationManager(Activity activity) {
		Intent intent = new Intent(activity, MyIntegralActivity.class);
		activity.startActivity(intent);
	}
	public static void startDeliveryAddressList(Activity activity) {
		Intent intent = new Intent(activity, DeliveryAddressListActivity.class);
		activity.startActivity(intent);
	}
	
	/**
	 * 获取关注数的显示字符串
	 * @param str
	 * @return
	 */
	public static String getAttentionNumq(String str){
		String strTemp = "";
		
		if(null != str && !str.isEmpty()){
			int n = Integer.valueOf(str);
			if(n > 0 && n <15){
				strTemp = "1+";
			}else if(n >= 15 && n < 50){
				strTemp = "15+";
			}else if(n >= 50 && n < 100){
				strTemp = "50+";
			}else if(n >= 100 && n < 250){
				strTemp = "100+";
			}else if(n >= 250 && n < 500){
				strTemp = "250+";
			}else if(n >= 500 && n < 1000){
				strTemp = "500+";
			}else if(n >= 1000 && n < 3000){
				strTemp = "1k+";
			}else if(n >= 3000 && n < 10000){
				strTemp = "3k+";
			}else if(n >= 10000){
				strTemp = "10k+";
			}else{
				strTemp = str;
			}
		}
		
		return strTemp;
	}
	
	/**
	 * 获取第三方ID
	 * @param strKey
	 * @return
	 */
	public static String getTHPAPPID(Context context, String strKey){
		String strTemp = "";
		
		if(null == context || null == strKey || strKey.isEmpty()){
			return strTemp;
		}
		
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager()  
			        .getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
			strTemp = appInfo.metaData.getString(strKey); 
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		 
		return strTemp;
	}
	
	/**
	 * 获取第三方ID
	 * @param strKey
	 * @return
	 */
	public static String getTHPAPPID(AppContext appcontext, String strKey){
		String strTemp = "";
		
		if(null == appcontext || null == strKey || strKey.isEmpty()){
			return strTemp;
		}
		
		ApplicationInfo appInfo;
		try {
			appInfo = appcontext.getPackageManager()  
			        .getApplicationInfo(appcontext.getPackageName(),PackageManager.GET_META_DATA);
			strTemp = appInfo.metaData.getString(strKey); 
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		 
		return strTemp;
	}
	
	/**
	 * 获取第三方APPID(ID有可能是纯数字)
	 * @param appcontext
	 * @param strKey
	 * @return
	 */
	public static String getTHPAPPIDForNum(AppContext appcontext, String strKey){
		String strTemp = "";
		
		if(null == appcontext || null == strKey || strKey.isEmpty()){
			return strTemp;
		}
		
		ApplicationInfo appInfo;
		try {
			appInfo = appcontext.getPackageManager()  
			        .getApplicationInfo(appcontext.getPackageName(),PackageManager.GET_META_DATA);
			strTemp = appInfo.metaData.getString(strKey);
			if(null != strTemp && !strTemp.isEmpty()){
				strTemp = strTemp.substring(1);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		 
		return strTemp;
	}
	
	/**
	 * 获取第三方APPID(ID有可能是纯数字)
	 * @param appcontext
	 * @param strKey
	 * @return
	 */
	public static String getTHPAPPIDForNum(Context context, String strKey){
		String strTemp = "";
		
		if(null == context || null == strKey || strKey.isEmpty()){
			return strTemp;
		}
		
		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager()  
			        .getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
			strTemp = appInfo.metaData.getString(strKey);
			if(null != strTemp && !strTemp.isEmpty()){
				strTemp = strTemp.substring(1);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		 
		return strTemp;
	}
}

package com.qianniu.zhaopin.app.ui;

import java.net.URLEncoder;

import com.alipay.android.app.sdk.AliPay;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.ConfigOptions;
import com.qianniu.zhaopin.app.bean.Result;
import com.qianniu.zhaopin.app.bean.RewardInfo;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.MethodsCompat;
import com.qianniu.zhaopin.app.common.MyLogger;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;
import com.qianniu.zhaopin.thp.Rsa;
import com.qianniu.zhaopin.thp.UmShare;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

public class MyRewardDetailActivity extends BaseActivity {

	private ImageView back;
	private WebView webView;
	// private Button pause;
	// private TextView pauseText;
	// private Button edit;
	private Button m_btnPay;
	private Button m_btnApplyRefund;

	private RewardInfo rewardInfo;
	private int m_nPayStatus; // 付款状态
	private int m_nOldPayStatus; // 初始付款状态

	private String m_strTaskId; // 悬赏任务ID
	private String m_strTotal; // 要支付的总金额
	private String m_strPrice; // 悬赏的金额
	private String m_strPaymentOrderNo; // 支付订单号
	String m_strSubject;
	String m_strBody;

	// private PromptPopupWindow tipsWindows;

	private int editRequest = 1;
	public static String rewardTaskId = "rewardTaskId";
	// private final static int CHANGESTATUSSUCESS = 1;
	// private final static int CHANGESTATUEFAIL = 12;

	private Handler m_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// case CHANGESTATUSSUCESS:
			// dismissProgressDialog();
			// if (HeadhunterPublic.TASK_STATUS_PUBLISHING == m_nstatus) {
			// rewardInfo.setTask_status(String.valueOf(HeadhunterPublic.TASK_STATUS_PAUSE));
			// UIHelper.ToastMessage(getApplicationContext(),
			// R.string.publish_reward_pause_success);
			// status = HeadhunterPublic.TASK_STATUS_PAUSE;
			// pause.setText(getResources().getString(R.string.my_reward_list_reward_status_pause));
			// } else if (HeadhunterPublic.TASK_STATUS_PAUSE == m_nstatus) {
			// rewardInfo.setTask_status(String.valueOf(HeadhunterPublic.TASK_STATUS_AUDIT));
			// UIHelper.ToastMessage(getApplicationContext(),
			// R.string.publish_reward_pause_publish_success);
			// status = HeadhunterPublic.TASK_STATUS_AUDIT;
			// pause.setText(getResources().getString(R.string.my_reward_list_reward_status_audit));
			// }
			// refreshStatus();
			// setResult(RESULT_OK);
			// break;
			// case CHANGESTATUEFAIL:
			// dismissProgressDialog();
			// UIHelper.ToastMessage(MyRewardDetailActivity.this,
			// R.string.my_reward_detail_error);
			// break;
			case HeadhunterPublic.MSG_PAY_SUCCESS: {
				UIHelper.ToastMessage(MyRewardDetailActivity.this,
						R.string.msg_pay_success);

				// 改变控件状态
				m_btnPay.setClickable(false);
				m_btnApplyRefund.setClickable(true);
				m_btnPay.setBackgroundResource(R.drawable.common_button_no);

				m_nPayStatus = HeadhunterPublic.PAY_FLAG_PAY;
			}
				break;
			case HeadhunterPublic.MSG_PAY_FAIL: {
				UIHelper.ToastMessage(MyRewardDetailActivity.this,
						R.string.msg_pay_fail);
			}
				break;
			case HeadhunterPublic.MSG_PAY_ABNORAL: {
				UIHelper.ToastMessage(MyRewardDetailActivity.this,
						R.string.msg_pay_abnormal);
			}
				break;
			case HeadhunterPublic.MSG_PAYMENTORDERNO_SUCCESS: // 获取支付订单号成功
			{
				dismissProgressDialog();

				Result result = (Result) msg.obj;
				m_strPaymentOrderNo = result.getJsonStr();

				if (null != m_strPaymentOrderNo
						&& !m_strPaymentOrderNo.isEmpty()) {
					// 付款
					pay();
				} else {
					UIHelper.ToastMessage(mContext,
							R.string.msg_myrewarddetail_getpaymentorderno_fail);
				}
			}
				break;
			case HeadhunterPublic.MSG_PAYMENTORDERNO_FAIL: // 获取支付订单号失败
			case HeadhunterPublic.MSG_PAYMENTORDERNO_ABNORMAL: // 获取支付订单号异常
			{
				dismissProgressDialog();
				UIHelper.ToastMessage(mContext,
						R.string.msg_myrewarddetail_getpaymentorderno_fail);
			}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_reward_detail);

		// 友盟统计
		UmShare.UmsetDebugMode(mContext);

		initView();
		setListener();
		getIntentData();
	}

	private void initView() {
		initProgressBar(R.id.my_reward_detail_head_progress);
		back = (ImageView) findViewById(R.id.my_reward_detail_goback);
		webView = (WebView) findViewById(R.id.my_reward_detail_webview);
		// pause = (Button) findViewById(R.id.my_reward_detail_pause);
		// pauseText = (TextView)
		// findViewById(R.id.my_reward_detail_pause_text);
		// edit = (Button) findViewById(R.id.my_reward_detail_edit);
		// 支付赏金按钮
		m_btnPay = (Button) findViewById(R.id.my_reward_detail_pay);
		m_btnPay.setClickable(false);
		// 申请退款按钮
		m_btnApplyRefund = (Button) findViewById(R.id.my_reward_detail_applyrefund);
		m_btnApplyRefund.setClickable(false);

		initWebView();
	}

	private void getIntentData() {
		Intent intent = getIntent();
		rewardInfo = (RewardInfo) intent
				.getSerializableExtra(MyRewardListActivity.myReward);

		if (null == rewardInfo) {
			return;
		}

		// m_strOutTradeNo = UIHelper.getOutTradeNo();
		m_strTaskId = rewardInfo.getTask_id();
		m_strPrice = rewardInfo.getTask_bonus();

		// 默认悬赏方式为面试
		String strTemp = getString(R.string.publish_reward_type_interview);
		if (null != rewardInfo.getTask_type()) {
			switch (Integer.valueOf(rewardInfo.getTask_type())) {
			case HeadhunterPublic.REWARD_TYPE_ENTRY: // 求职入职悬赏任务
			{
				strTemp = getString(R.string.publish_reward_type_entry);
			}
				break;
			case HeadhunterPublic.REWARD_TYPE_INTERVIEW:// 求职面试悬赏任务
			{
				strTemp = getString(R.string.publish_reward_type_interview);
			}
				break;
			default:
				break;
			}
		}
		m_strSubject = getString(R.string.app_name) + "-" + strTemp;
		m_strBody = getString(R.string.app_name) + "-" + strTemp;

		refresh();
	}

	private void refreshStatus() {
		if (null == rewardInfo) {
			return;
		}

		if (null == rewardInfo.getPay_flag()) {
			return;
		}
		m_nPayStatus = Integer.valueOf(rewardInfo.getPay_flag());
		m_nOldPayStatus = m_nPayStatus;

		switch (m_nPayStatus) {
		// case HeadhunterPublic.TASK_STATUS_PUBLISHING:
		// {//正在进行中的状态
		// pause.setBackgroundResource(R.drawable.common_button_red);
		// pause.setText(R.string.my_reward_detail_pause);
		// pause.setClickable(true);
		// edit.setBackgroundResource(R.drawable.common_button_green);
		// edit.setClickable(true);
		// }
		// break;
		// case HeadhunterPublic.TASK_STATUS_AUDIT:
		// {//审核中
		// pause.setText(R.string.my_reward_detail_pause);
		// pause.setBackgroundResource(R.drawable.common_button_no);
		// pause.setClickable(false);
		// edit.setBackgroundResource(R.drawable.common_button_green);
		// edit.setClickable(true);
		// }
		// break;
		// case HeadhunterPublic.TASK_STATUS_PAUSE:
		// {//暂停
		// pause.setText(R.string.my_reward_list_reward_status_todo);
		// pause.setBackgroundResource(R.drawable.common_button_red);
		// pause.setClickable(true);
		// edit.setBackgroundResource(R.drawable.common_button_green);
		// edit.setClickable(true);
		// }
		// break;
		// case HeadhunterPublic.TASK_STATUS_EXPIRED:
		// case HeadhunterPublic.TASK_STATUS_ARCHIVE:
		// {//过期 归档 归档对用户来说是过期
		// pause.setText(R.string.my_reward_detail_pause);
		// pause.setBackgroundResource(R.drawable.common_button_no);
		// pause.setClickable(false);
		// edit.setBackgroundResource(R.drawable.common_button_no);
		// edit.setClickable(false);
		// }
		// case HeadhunterPublic.TASK_STATUS_NOPAY:
		// {// 未付款
		// pause.setText(R.string.my_reward_detail_pay);
		// pause.setBackgroundResource(R.drawable.common_button_red);
		// pause.setClickable(true);
		// edit.setBackgroundResource(R.drawable.common_button_green);
		// edit.setClickable(true);
		// }
		// break;
		case HeadhunterPublic.PAY_FLAG_NOPAY: {// 未付款
			m_btnPay.setClickable(true);
			m_btnApplyRefund.setClickable(false);
			m_btnApplyRefund.setBackgroundResource(R.drawable.common_button_no);
		}
			break;
		case HeadhunterPublic.PAY_FLAG_PAY: {// 已付款
			m_btnPay.setClickable(false);
			m_btnApplyRefund.setClickable(true);
			m_btnPay.setBackgroundResource(R.drawable.common_button_no);
		}
			break;
		case HeadhunterPublic.PAY_FLAG_REFUNDMENTINGING: {// 退款中...
			m_btnPay.setClickable(false);
			m_btnApplyRefund.setClickable(false);
			m_btnPay.setBackgroundResource(R.drawable.common_button_no);
			m_btnApplyRefund.setBackgroundResource(R.drawable.common_button_no);
		}
			break;
		case HeadhunterPublic.PAY_FLAG_REFUNDMENTINGSUC: {// 退款成功
			m_btnPay.setClickable(false);
			m_btnApplyRefund.setClickable(false);
			m_btnPay.setBackgroundResource(R.drawable.common_button_no);
			m_btnApplyRefund.setBackgroundResource(R.drawable.common_button_no);
		}
			break;
		}
	}

	private void refresh() {
		refreshStatus();
		if (!UIHelper.isNetworkConnected((AppContext) getApplicationContext())) {
			return;
		}
		String strTaskUrl = rewardInfo.getTask_url();
		// url添加过滤
		if (null != strTaskUrl && !strTaskUrl.isEmpty()) {
			strTaskUrl = strTaskUrl + "?platform=1";
			webView.loadUrl(strTaskUrl);
		} else {
			webView.loadUrl(ConfigOptions.getWebSite());
		}

	}

	@SuppressLint("JavascriptInterface")
	private void initWebView() {
		webView.setVerticalScrollBarEnabled(true);
		webView.setWebViewClient(new MyWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JavascriptInterface(this),
				"buttonlistner");
	}

	private void setListener() {
		// edit.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // 友盟统计--个人悬赏详情--编辑按钮
		// UmShare.UmStatistics(m_Context, "MyRewardDetail_EditButton");
		//
		// if (HeadhunterPublic.TASK_STATUS_PAUSE == m_nstatus ||
		// HeadhunterPublic.TASK_STATUS_AUDIT == m_nstatus) { //只有在暂停的状态下才能修改
		// Intent intent = new Intent(MyRewardDetailActivity.this,
		// PublishRewardActivity.class);
		// intent.putExtra(rewardTaskId, rewardInfo.getTask_id());
		// startActivityForResult(intent, editRequest);
		// } else {
		// UIHelper.ToastMessage(MyRewardDetailActivity.this,
		// R.string.my_reward_edit_nopause);
		// }
		// // Intent intent = new Intent(MyRewardDetailActivity.this,
		// PublishRewardActivity.class);
		// // intent.putExtra(rewardData, rewardListData);
		// // startActivity(intent);
		// }
		// });
		// pause.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if(HeadhunterPublic.TASK_STATUS_NOPAY == m_nstatus){
		// // 友盟统计--个人悬赏详情--支付赏金按钮
		// UmShare.UmStatistics(m_Context, "MyRewardDetail_PayButton");
		//
		// showPayAlertDialog();
		// }else{
		// // 友盟统计--个人悬赏详情--暂停发布按钮
		// UmShare.UmStatistics(m_Context, "MyRewardDetail_PauseButton");
		//
		// if (HeadhunterPublic.TASK_STATUS_PAUSE != m_nstatus) { //暂停
		// showTipsWindows(pause);
		// } else {
		// changeStatus(HeadhunterPublic.TASK_STATUS_AUDIT);
		// }
		// }
		// }
		// });
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finishMyRewardDetailActivity();
			}
		});
		// 支付赏金
		m_btnPay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 友盟统计--个人悬赏详情--支付赏金按钮
				UmShare.UmStatistics(mContext, "MyRewardDetail_PayButton");

				showPayAlertDialog();
			}
		});
		//
		m_btnApplyRefund.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 友盟统计--个人悬赏详情--申请退款按钮
				UmShare.UmStatistics(mContext,
						"MyRewardDetail_ApplyRefundButton");
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// if (resultCode == RESULT_OK) {
		// if (requestCode == editRequest) { //修改成功后的状态一定是审核状态
		// m_nstatus = HeadhunterPublic.TASK_STATUS_AUDIT;
		// rewardInfo.setTask_status(String.valueOf(m_nstatus));
		// refresh();
		// setResult(RESULT_OK);
		// }
		// }
	}

	/**
	 * js通信接口
	 * 
	 */
	public class JavascriptInterface {

		private Context context;

		public JavascriptInterface(Context context) {
			this.context = context;
		}

		public void openCompanyInfoAcitivity(String strUrl) {

			if (strUrl.isEmpty()) {
				return;
			}

			// 数据传输
			Bundle bundle = new Bundle();
			bundle.putString(HeadhunterPublic.REWARD_DATATRANSFER_COMPANYURL,
					rewardInfo.getCompany_url());

			// 进入公司详情界面
			Intent intent = new Intent();
			intent.setClass(MyRewardDetailActivity.this,
					CompanyInfoAcitivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	/**
	 * 监听
	 * 
	 */
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			// html加载完成之后，添加监听按钮的点击js函数
			// addButtonClickListner();
			goneProgressBar();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {

			super.onPageStarted(view, url, favicon);
			showProgressBar();
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			MyLogger.i("onReceivedError", "errorCode##" + errorCode + "&description##"
					+ description + "&failingUrl##" + failingUrl);
			super.onReceivedError(view, errorCode, description, failingUrl);

		}
	}

	// private void changeStatus(final int status) {
	// if (!UIHelper.isNetworkConnected((AppContext)getApplicationContext())) {
	// return;
	// }
	// showProgressDialog();
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// boolean isSuccess = ApiClient.changeMyRewardStatus(
	// (AppContext)getApplicationContext(), rewardInfo.getTask_id(), status);
	// if (isSuccess) {
	// m_handler.sendEmptyMessage(CHANGESTATUSSUCESS);
	// } else {
	// m_handler.sendEmptyMessage(CHANGESTATUEFAIL);
	// }
	// } catch (AppException e) {
	// e.printStackTrace();
	// final AppException ee = e;
	// pause.post(new Runnable() {
	// @Override
	// public void run() {
	// ee.makeToast(getApplicationContext());
	// }
	// });
	// }
	// }
	// }).start();
	// }
	// private void showTipsWindows(View view) {
	// UIHelper.showCommonDialog(MyRewardDetailActivity.this,
	// R.string.publish_reward_pause_tips, new DialogInterface.OnClickListener()
	// {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// if (which == DialogInterface.BUTTON_POSITIVE) {
	// changeStatus(HeadhunterPublic.TASK_STATUS_PAUSE);
	// }
	// dialog.dismiss();
	// }
	// });
	// if (tipsWindows == null) {
	// tipsWindows = new PromptPopupWindow(getApplicationContext(), false);
	// tipsWindows.setListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.prompt_title_ok:
	// changeStatus(Integer.parseInt(HeadhunterPublic.TASK_STATUS_PAUSE));
	// hideTipsWindows();
	// break;
	// case R.id.prompt_title_cancel:
	// hideTipsWindows();
	// break;
	// default:
	// break;
	// }
	// }
	// });
	// tipsWindows.initView(-1,
	// R.string.publish_reward_pause_tips, true);
	// }
	// tipsWindows.show(webView);
	// }
	// private void hideTipsWindows() {
	// if (tipsWindows != null) {
	// tipsWindows.dismiss();
	// }
	// }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// 友盟统计
		UmShare.UmResume(mContext);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// 友盟统计
		UmShare.UmPause(mContext);
	}

	/**
	 * 付款
	 */
	private void pay() {
		try {
			// 判断订单号
			// if(null == m_strOutTradeNo || m_strOutTradeNo.isEmpty()){
			// // 支付失败
			// // UIHelper.ToastMessage(MyRewardDetailActivity.this,
			// R.string.msg_pay_fail);
			// m_handler.sendMessage(m_handler.obtainMessage(
			// HeadhunterPublic.MSG_PAY_FAIL));
			//
			// return;
			// }

			// 测试用
			if (ConfigOptions.debug) {
				m_strTotal = "0.01";
			}

			String info = UIHelper.getNewOrderInfo(m_strPaymentOrderNo,
					m_strSubject, m_strBody, m_strTotal);
			String sign = Rsa.sign(info, HeadhunterPublic.ALIPAY_PRIVATE);
			sign = URLEncoder.encode(sign);
			info += "&sign=\"" + sign + "\"&" + UIHelper.getSignType();

			final String orderInfo = info;
			new Thread() {
				public void run() {
					AliPay alipay = new AliPay(MyRewardDetailActivity.this,
							m_handler);

					// 设置为沙箱模式，不设置默认为线上环境
					// alipay.setSandBox(true);

					//
					String result = alipay.pay(orderInfo);
					// 获取支付返回状态
					String resultStatus = UIHelper
							.getAlipayResultStatus(result);

					if (null != resultStatus && resultStatus.equals("9000")) {
						// 支付成功
						m_handler
								.sendMessage(m_handler
										.obtainMessage(HeadhunterPublic.MSG_PAY_SUCCESS));
					} else {
						// 支付失败
						m_handler.sendMessage(m_handler
								.obtainMessage(HeadhunterPublic.MSG_PAY_FAIL));
					}
				}
			}.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			m_handler.sendMessage(m_handler.obtainMessage(
					HeadhunterPublic.MSG_PAY_ABNORAL, ex));
		}
	}

	/**
	 * 付款询问对话框
	 */
	private void showPayAlertDialog() {
		// 判断悬赏任务id
		if (null == m_strTaskId) {
			UIHelper.ToastMessage(mContext,
					R.string.msg_myrewarddetail_taskidisnull);
			return;
		}

		//
		if (m_strTaskId.isEmpty()) {
			UIHelper.ToastMessage(mContext,
					R.string.msg_myrewarddetail_taskidisempty);
			return;
		}

		// 判断金额
		if (null == m_strPrice) {
			UIHelper.ToastMessage(mContext,
					R.string.msg_myrewarddetail_taskbonusisnull);
			return;
		}

		if (m_strPrice.isEmpty()) {
			UIHelper.ToastMessage(mContext,
					R.string.msg_myrewarddetail_taskbonusempty);
		}

		// 获取金额
		double nPrice = Double.valueOf(m_strPrice);
		// 手续费
		double nFee = nPrice * HeadhunterPublic.PAY_RATE;
		String strFee = String.valueOf(nFee);
		// 总计
		double nTotal = nPrice + nFee;
		m_strTotal = String.valueOf(nTotal);

		// 提示信息
		String strPayMessage = String.format(
				getString(R.string.str_play_prompt), m_strPrice, strFee,
				m_strTotal);

		// 弹出对话框
		AlertDialog.Builder dl = MethodsCompat.getAlertDialogBuilder(this,AlertDialog.THEME_HOLO_LIGHT);
		dl.setTitle(getString(R.string.publish_reward_payprompt_title));
		dl.setMessage(strPayMessage);
		dl.setPositiveButton(getString(R.string.publish_reward_pay),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();

						// 获取支付单号
						runGetPaymentOrderNo();
					}

				});
		dl.setNegativeButton(getString(R.string.publish_reward_nopay),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}

				});
		dl.show();
	}

	/**
	 * 获取支付订单号
	 */
	private void runGetPaymentOrderNo() {

		if (!UIHelper.isNetworkConnected((AppContext) getApplicationContext())) {
			return;
		}

		showProgressDialog();

		new Thread() {
			public void run() {
				UIHelper.getPaymentOrderNo(mContext, m_handler, "task",
						m_strTaskId);
			}
		}.start();
	}

	/**
	 * 结束
	 */
	private void finishMyRewardDetailActivity() {
		if (m_nOldPayStatus != m_nPayStatus) {
			Bundle bundle = new Bundle();

			bundle.putString(MyRewardListActivity.PAY_FLAG,
					String.valueOf(m_nPayStatus));

			setResult(HeadhunterPublic.RESULT_MYREWARDDETAIL_CHANGE,
					getIntent().putExtras(bundle));
		}

		finish();
	}
}

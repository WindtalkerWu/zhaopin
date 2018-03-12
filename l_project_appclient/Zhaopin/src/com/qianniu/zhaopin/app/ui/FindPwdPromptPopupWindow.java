package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.app.view.Util;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FindPwdPromptPopupWindow extends PopupWindow {

	private Handler m_handler;
	private Context m_Context;
	private LayoutInflater m_layoutInflater;

	private View m_VCPPW;

	private RelativeLayout m_btnMobile;
	private RelativeLayout m_btnMail;

	private EditText m_etMethod;

	private ImageView m_imgClean;

	private String m_strMethod;

	public FindPwdPromptPopupWindow(Context context, Handler handler) {
		super(context);
		this.m_handler = handler;
		this.m_Context = context;
		this.m_layoutInflater = LayoutInflater.from(m_Context);
		this.m_strMethod = "";
		initPopView();
	}

	private void initPopView() {
		m_VCPPW = (View) m_layoutInflater.inflate(R.layout.popupwindow_findpwd,
				null);
		m_VCPPW.getBackground().setAlpha(10);

		this.setContentView(m_VCPPW);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// this.setAnimationStyle(R.style.popshow);
		setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		initControl();
	}

	private void initControl() {

		m_etMethod = (EditText) m_VCPPW
				.findViewById(R.id.popupwindow_findpwd_et_method);

		// 清除输入
		m_imgClean = (ImageView) m_VCPPW
				.findViewById(R.id.popupwindow_findpwd_iv_methodclean);
		m_imgClean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (null != m_etMethod) {
					m_etMethod.setText("");
				}
			}

		});

		// 手机号找回
		m_btnMobile = (RelativeLayout) m_VCPPW
				.findViewById(R.id.popupwindow_findpwd_rl_mobile);
		m_btnMobile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				m_strMethod = m_etMethod.getText().toString();
				if (checkNull(m_strMethod) && checkMobileFormate(m_strMethod)) {
					Message msg = new Message();
					msg.what = HeadhunterPublic.FINDPWDPOPUPWINDOW_MSG_MOBILE;
					msg.obj = m_strMethod;

					m_handler.sendMessage(msg);
					dismiss();
				}
			}
		});
		// 邮箱找回
		m_btnMail = (RelativeLayout) m_VCPPW
				.findViewById(R.id.popupwindow_findpwd_rl_mail);
		m_btnMail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				 m_strMethod = m_etMethod.getText().toString();
				if (checkNull(m_strMethod) && checkEmailFormate(m_strMethod)) {
					Message msg = new Message();
					msg.what = HeadhunterPublic.FINDPWDPOPUPWINDOW_MSG_MAIL;
					msg.obj = m_strMethod;

					m_handler.sendMessage(msg);
					dismiss();
				}
			}
		});
	}

	private boolean checkNull(String content) {
		if (TextUtils.isEmpty(content)) {
			UIHelper.ToastMessage(m_Context,
					R.string.msg_findpwd_method_isempty);
			return false;
		}
		return true;
	}

	private boolean checkMobileFormate(String content) {
		if (!Util.checkMobileFormate(m_strMethod)) {
			UIHelper.ToastMessage(m_Context, R.string.msg_register_username_formate_error);
			return false;
		}
		return true;
	}

	private boolean checkEmailFormate(String content) {
		if (!Util.checkEmail(content)) {
			UIHelper.ToastMessage(m_Context, R.string.msg_register_username_formate_error);
			return false;
		}
		return true;
	}
}

package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 询问提示框
 * @author wuzy
 *
 */
public class AskPopupWindow extends PopupWindow{
	
	private Context m_Context;
	private LayoutInflater m_layoutInflater;
	
	private View m_VCPPW;
	
	private ImageView m_btnOK;
	private ImageView m_btnCancel;

	private TextView m_tvContent;
	private TextView m_title;
	private TextView m_tvPrompt;

	private String m_strTitle; 
	private String m_strContent;
	
	public AskPopupWindow(Context context, String strTitle, String strContent
			){
		super(context);
		
		this.m_strTitle = strTitle;
		this.m_strContent = strContent;
		this.m_Context = context;
		this.m_layoutInflater = LayoutInflater.from(m_Context);
		
		initPopView();
	}
	
	private void initPopView(){	
		 m_VCPPW = (View)m_layoutInflater.inflate(R.layout.popup_window_ask, null);
		 m_VCPPW.getBackground().setAlpha(10);

		this.setContentView(m_VCPPW);
        this.setWidth(LayoutParams.FILL_PARENT);  
        this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
//		this.setAnimationStyle(R.style.popshow);
		
		initControl();
	}
	
	/**
	 * 
	 */
	private void initControl(){
		
		m_title =  (TextView) m_VCPPW.findViewById(R.id.popupwindow_ask_tv_title);
		m_title.setText(m_strTitle);
		
		m_tvContent = (TextView)m_VCPPW.findViewById(R.id.popupwindow_ask_tv_content);
		m_tvContent.setText(m_strContent);
		
		m_tvPrompt = (TextView)m_VCPPW.findViewById(R.id.popupwindow_ask_tv_prompt);
	
		m_btnOK = (ImageView)m_VCPPW.findViewById(R.id.popupwindow_ask_img_ok);
		m_btnCancel = (ImageView)m_VCPPW.findViewById(R.id.popupwindow_ask_img_cancel);
	}
	
	/**
	 * @param onClickListener
	 */
	public void setListener(OnClickListener onClickListener) {
		if(null != m_btnOK){
			m_btnOK.setOnClickListener(onClickListener);	
		}
		
		if(null != m_btnCancel){
			m_btnCancel.setOnClickListener(onClickListener);	
		}
	}
}

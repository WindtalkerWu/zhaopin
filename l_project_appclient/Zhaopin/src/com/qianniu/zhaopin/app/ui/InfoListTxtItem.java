package com.qianniu.zhaopin.app.ui;

import com.qianniu.zhaopin.app.bean.InfoEntity;
import com.qianniu.zhaopin.app.bean.ResumeSimpleEntity;
import com.qianniu.zhaopin.app.common.BitmapManager;
import com.qianniu.zhaopin.app.common.ObjectUtils;
import com.qianniu.zhaopin.app.common.StringUtils;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.qianniu.zhaopin.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class InfoListTxtItem extends LinearLayout{
	private Context mcontext;
	private TextView contenttv;
	private TextView time_tv;
	private ImageView userimg;
	
	private InfoEntity minfoEntity;
	
	private BitmapManager bmpManager;
	private Bitmap defbmp;
	
	private String m_strUrl = "";
	
	public InfoListTxtItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mcontext = context;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.qianniu_logobg));
	}

	public InfoListTxtItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mcontext = context;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(
				context.getResources(), R.drawable.qianniu_logobg));
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		contenttv = (TextView) findViewById(R.id.info_listitem_content);
		time_tv = (TextView) findViewById(R.id.info_listitem_time_tv);
		userimg = (ImageView) findViewById(R.id.info_listitem_userface);
		defbmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.qianniu_logobg);
		this.setSoundEffectsEnabled(false);
		this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InfoEntity.actionJump(mcontext, minfoEntity);		
			}
		});
	}
	public void bind(InfoEntity infoEntity, boolean isVisible) {
		minfoEntity = infoEntity;
		if (!isVisible) {
			this.setVisibility(View.GONE);
			return;
		} else {
			this.setVisibility(View.VISIBLE);
		}

		String strTemp = minfoEntity.getInfoContent();
		if(null != strTemp && !strTemp.isEmpty()){
			int m = strTemp.indexOf("<a href=\"");
			int n = strTemp.indexOf("\">");
			
			if(-1 != m && -1 !=n && m < n){
				m_strUrl = strTemp.substring(m + 9, n);
				
				String strMsg = strTemp.substring(0, m);
				if(null != strMsg && !strMsg.isEmpty()){
					
					int i = strTemp.indexOf("</a>");
					if(-1 != i && n < i){
						String strWarn = strTemp.substring(n + 2, i);
						int nLenght = strMsg.length();
						strMsg = strMsg + " " +strWarn;
						
						SpannableString tips = new SpannableString(strMsg);
						tips.setSpan(new ClickableSpan() {
					            @Override
					            public void onClick(View widget) {
					            	showResumePreview(m_strUrl);
					            }
					        }, nLenght + 1, tips.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						
						contenttv.setText(tips);
						contenttv.setMovementMethod(LinkMovementMethod.getInstance());
					}else{
						contenttv.setText(strMsg);
					}
				}
			}else{
				contenttv.setText(minfoEntity.getInfoContent());
			}
		}

		String receivetime = StringUtils.timeToString(minfoEntity.getReviveTimestamp());
		time_tv.setText(StringUtils.friendly_time(receivetime));
		
		String imgURL = minfoEntity.getInfoImg();
		if (imgURL.endsWith("portrait.gif") || StringUtils.isEmpty(imgURL)) {
			// face.setImageResource(R.drawable.widget_dface);
			if (defbmp != null)
				userimg.setImageBitmap(defbmp);
		} else {
			bmpManager.loadBitmap(imgURL, userimg,defbmp);
		}


	}
	
	/**
	 * @param strUrl
	 */
	private void showResumePreview(String strUrl){
		if(null == strUrl || strUrl.isEmpty()){
			return;
		}
	
		if(null == mcontext){
			return;
		}
		
		Intent intent = new Intent(mcontext, ShowHtml5Activity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(ShowHtml5Activity.SHOWHTML5_DATATRANSFER_TYPE,
				ShowHtml5Activity.SHOWHTML5_TYPE_RESUME);
		bundle.putString(ShowHtml5Activity.SHOWHTML5_DATATRANSFER_URL,
				strUrl);
		bundle.putString(ShowHtml5Activity.SHOWHTML5_DATATRANSFER_TITLE,
				"简历预览");
		intent.putExtras(bundle);

		mcontext.startActivity(intent);
	}
}

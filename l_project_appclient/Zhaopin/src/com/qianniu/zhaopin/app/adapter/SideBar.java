package com.qianniu.zhaopin.app.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SideBar extends View{

	private char[] l;  
	
	private ListView list; 
	
	private SectionIndexer m_sectionIndexter = null; 
	
//	private int m_nItemHeight = 28;
	private int m_nItemHeight = -1;
	
	private TextView mDialogText;
	
    public SideBar(Context context) {  
        super(context);  
        init();  
    }  
 
    public SideBar(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init();  
    }  
    
	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(); 
	}

	 private void init() {  
	        l = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',  
	                'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };  
	        setBackgroundColor(0x44FFFFFF);
	 } 
	 
	 public void setListView(ListView _list) {  
        list = _list;  
        m_sectionIndexter = (SectionIndexer) _list.getAdapter();  
	} 

    public void setTextView(TextView mDialogText) {  
    	this.mDialogText = mDialogText;  
    } 
	    
	public boolean onTouchEvent(MotionEvent event) {  
        super.onTouchEvent(event);  
        int i = (int) event.getY();  
        int idx = i / m_nItemHeight;  
        if (idx >= l.length) {  
            idx = l.length - 1;  
        } else if (idx < 0) {  
            idx = 0;  
        }  

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {  
        	mDialogText.setVisibility(View.VISIBLE);
        	mDialogText.setText(""+l[idx]);
            if (m_sectionIndexter == null) {  
            	m_sectionIndexter = (SectionIndexer) list.getAdapter();  
            }  
            int position = m_sectionIndexter.getPositionForSection(l[idx]);  
            if (position == -1) {  
                return true;  
            } 
            list.setSelection(position);  
        }else{
        	mDialogText.setVisibility(View.INVISIBLE);
        } 
 
        return true; 
	}  
	
	protected void onDraw(Canvas canvas) {  
        Paint paint = new Paint();
        if (m_nItemHeight == -1) {
            m_nItemHeight = getMeasuredHeight() / 26;
        }
        paint.setColor(0xff333333);
//        paint.setColor(0xff595c61);  
//        paint.setTextSize(24);
        paint.setTextSize(m_nItemHeight);
        paint.setTypeface(Typeface.MONOSPACE);
//        paint.setTypeface(Typeface.SANS_SERIF);
        paint.setTextAlign(Paint.Align.CENTER);
        float widthCenter = getMeasuredWidth() / 2;  
        for (int i = 0; i < l.length; i++) {  
            canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);  
        }
        super.onDraw(canvas);  
	}  
}

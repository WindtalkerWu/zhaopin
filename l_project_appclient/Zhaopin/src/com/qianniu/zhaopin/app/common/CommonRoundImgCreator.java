package com.qianniu.zhaopin.app.common;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

public class CommonRoundImgCreator implements GraphicImgCreator {

	private Activity mcontext;
	private int mbmpradius;
	private int mborderWidth;
	private int mbordercolor;

	
	/**
	 * @param context:activity contenxt
	 * @param bmpradius:图片半径,单位：dp
	 * @param borderWidth:边框宽度，没有请设置为0
	 * @param bordercolor：边框颜色
	 */
public CommonRoundImgCreator(Activity context,int bmpradius,int borderWidth,int bordercolor) {
		super();
		mcontext =context;
		mbmpradius = bmpradius;
		mborderWidth = (borderWidth >= 0)?borderWidth:0;
		mbordercolor = bordercolor;
	}




	@Override
	public Bitmap creator(Bitmap bmp) {
		if(bmp == null)
			return null;
		DisplayMetrics dm = new DisplayMetrics();
		mcontext.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int radiuspx = (int) (mbmpradius*dm.density);
		double wh = bmp.getWidth() / bmp.getHeight();
		if (wh == 0) {
			wh = 1;
		}
		Bitmap photobmp = Bitmap.createScaledBitmap(bmp, (int) (radiuspx * 2 * wh), radiuspx * 2, true);
		photobmp = ImageUtils.toRoundBitmap(photobmp, mborderWidth, mbordercolor);
		return photobmp;
	}
	
}

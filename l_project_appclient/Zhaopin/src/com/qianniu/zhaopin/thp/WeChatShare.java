package com.qianniu.zhaopin.thp;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.qianniu.zhaopin.app.common.HeadhunterPublic;
import com.qianniu.zhaopin.app.common.UIHelper;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WeChatShare {

	private IWXAPI  m_IWXAPI;
	private Context m_Context;
	
	private static int m_nWidth = 150;
	private static int m_nHeight = 150;
	
	public WeChatShare(Context context, Intent intent){
		m_Context = context;
		
		String strAppID = UIHelper.getTHPAPPID(
				m_Context, HeadhunterPublic.THPKEY_WECHAT_APPID);
		// 
		m_IWXAPI = WXAPIFactory.createWXAPI(m_Context, strAppID);
		// 将牵牛的appid注册到微信
		m_IWXAPI.registerApp(strAppID);
	}
	
	/**
	 * 分享文本
	 * @param strMsg
	 * @param bShareFriends		true: 分享微信朋友圈/false: 分享微信朋友
	 */
	public void ShareText(String strContent, boolean bShareFriends){
        if(!m_IWXAPI.isWXAppInstalled()) {
            Toast.makeText(m_Context, "目前您的微信版本过低或者未安装微信，需要安装微信才能使用",
            		Toast.LENGTH_SHORT).show();
        	return;
        }

		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = strContent;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = strContent;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		req.scene = bShareFriends ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		
		// 调用api接口发送数据到微信
		m_IWXAPI.sendReq(req);
	}
	
	public void ShareTextAndBmp(String strTitle, String strUrl, String strContent, 
			Bitmap bmp, boolean bShareFriends){
        if(!m_IWXAPI.isWXAppInstalled()) {
            Toast.makeText(m_Context, "目前您的微信版本过低或者未安装微信，需要安装微信才能使用",
            		Toast.LENGTH_SHORT).show();
        	return;
        }

        WXWebpageObject localWXWebpageObject = new WXWebpageObject(); 
        localWXWebpageObject.webpageUrl = strUrl;
        
	     WXMediaMessage localWXMediaMessage = new WXMediaMessage(localWXWebpageObject); 
	     localWXMediaMessage.title = strTitle;
	     localWXMediaMessage.description = strContent;
	     localWXMediaMessage.thumbData = bmpToByteArray(bmp, true);

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = System.currentTimeMillis() + "";
		req.message = localWXMediaMessage;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		req.scene = bShareFriends ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
		
		// 调用api接口发送数据到微信
		m_IWXAPI.sendReq(req);
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
   
   public Bitmap createImage(String strText, int nWidth, int nHeight) {
       try {
           // 需要引入core包
           QRCodeWriter writer = new QRCodeWriter();

           if (null == strText || strText.isEmpty() || strText.length() < 1) {
               return null;
           }

           // 把输入的文本转为二维码
           BitMatrix martix = writer.encode(strText, BarcodeFormat.QR_CODE,
        		   nWidth, nHeight);

           System.out.println("w:" + martix.getWidth() + "h:"
                   + martix.getHeight());

           Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
           hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
           BitMatrix bitMatrix = new QRCodeWriter().encode(strText,
                   BarcodeFormat.QR_CODE, nWidth, nHeight, hints);
           int[] pixels = new int[nWidth * nHeight];
           for (int y = 0; y < nHeight; y++) {
               for (int x = 0; x < nWidth; x++) {
                   if (bitMatrix.get(x, y)) {
                       pixels[y * nWidth + x] = 0xff000000;
                   } else {
                       pixels[y * nWidth + x] = 0xffffffff;
                   }

               }
           }

           Bitmap bitmap = Bitmap.createBitmap(nWidth, nHeight,
                   Bitmap.Config.ARGB_8888);

           bitmap.setPixels(pixels, 0, nWidth, 0, 0, nWidth, nHeight);
           
           return bitmap;

       } catch (WriterException e) {
           e.printStackTrace();
       }
       
       return null;
   }
   
   /**
    * 生成二维码图片
    * @param strText
 	* @return
 	*/
   public Bitmap createQRCodeImage(String strText) {
       try {
           if (null == strText || strText.isEmpty() || strText.length() < 1) {
               return null;
           }

           // 需要引入core包
           QRCodeWriter writer = new QRCodeWriter();

           // 把输入的文本转为二维码
           BitMatrix martix = writer.encode(strText, BarcodeFormat.QR_CODE,
        		   m_nWidth, m_nHeight);

//           System.out.println("w:" + martix.getWidth() + "h:"
//                   + martix.getHeight());

           Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
           hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
           BitMatrix bitMatrix = new QRCodeWriter().encode(strText,
                   BarcodeFormat.QR_CODE, m_nWidth, m_nHeight, hints);
           int[] pixels = new int[m_nWidth * m_nHeight];
           for (int y = 0; y < m_nHeight; y++) {
               for (int x = 0; x < m_nWidth; x++) {
                   if (bitMatrix.get(x, y)) {
                       pixels[y * m_nWidth + x] = 0xff000000;
                   } else {
                       pixels[y * m_nWidth + x] = 0xffffffff;
                   }

               }
           }

           Bitmap bitmap = Bitmap.createBitmap(m_nWidth, m_nHeight,
                   Bitmap.Config.ARGB_8888);

           bitmap.setPixels(pixels, 0, m_nWidth, 0, 0, m_nWidth, m_nHeight);
           
           return bitmap;

       } catch (WriterException e) {
           e.printStackTrace();
       }
       
       return null;
   }
   
   /**
    * 把图片转化成byte[]
    * @param bm
 	* @return
 	*/
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}

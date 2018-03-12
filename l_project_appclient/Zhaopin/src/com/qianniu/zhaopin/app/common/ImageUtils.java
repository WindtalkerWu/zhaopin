package com.qianniu.zhaopin.app.common;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.qianniu.zhaopin.app.AppConfig;
import com.qianniu.zhaopin.app.constant.Constants;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

/**
 * 图片操作工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ImageUtils {

	public static ExecutorService pool = Executors.newCachedThreadPool();

	public final static String SDCARD_MNT = "/mnt/sdcard";
	public final static String SDCARD = "/sdcard";

	/** 请求相册 */
	public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
	/** 请求相机 */
	public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
	/** 请求裁剪 */
	public static final int REQUEST_CODE_GETIMAGE_BYCROP = 2;

	// 简历头像的标准长宽 ，120 dp
	private static final int HEADPHOTO_STAND_WIDTH = 120;
	private static final int HEADPHOTO_STAND_HEIGHT = 120;
	// 简历头像边框宽度 ,8 dp;
	private static final int HEADPHOTO_BODER_WIDTH = 7;
	// 简历头像边框颜色 ,;
	private static final int HEADPHOTO_BODER_COLOR = 0xFFA9A59E;

	// 简历完成度图片 圆环半径 ，75 dp；
	private static final int COMPLETEBMP_RADIUS = 63;
	// 简历完成度图片 圆环底色
	private static final int COMPLETEBMP_CIRCLE_COLOR = 0xFF3A4147;
	// 简历完成度图片 圆环颜色 ；
	private static final int COMPLETEBMP_ARCS_COLOR = 0xFFDC5B57;
	// 简历完成度圆环宽度 ,8 dp;
	private static final int COMPLETEBMP_BODER_WIDTH = 7;

	/**
	 * 写图片文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @throws IOException
	 */
	public static void saveImage(Context context, String fileName, Bitmap bitmap)
			throws IOException {
		saveImage(context, AppConfig.SDCARD_IMAGE_ROOT, fileName, bitmap);
	}
	public static boolean saveImage(Context context, String fileName,
			InputStream is)throws IOException{
		return saveImage(context, AppConfig.SDCARD_IMAGE_ROOT, fileName, is);
	}
	public static boolean saveImage(Context context, String dir,String fileName,
			InputStream is) throws IOException {

		if (is == null || fileName == null || context == null)
			return false;
		boolean bsdsave = FileUtils.writeFile(is, dir,
				fileName);
		if (!bsdsave) {
			FileOutputStream fos = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			byte[] buffer = new byte[4096];
			int readsize = 0;
			do {
				fos.write(buffer, 0, readsize);

				if ((readsize = is.read(buffer)) <= 0)
					break;
			} while (true);
			fos.flush();
			fos.close();
			return true;
		}

		return false;

	}

	public static void saveImage(Context context, String dir, String fileName,
			Bitmap bitmap) throws IOException {
		saveImage(context, dir, fileName, bitmap, 100);
	}

	/**
	 * @param context
	 * @param dir
	 *            文件缓存路径
	 * @param fileName
	 * @param bitmap
	 * @param quality
	 * @throws IOException
	 */
	public static void saveImage(Context context, String dir, String fileName,
			Bitmap bitmap, int quality) throws IOException {
		if (bitmap == null || fileName == null || context == null)
			return;
		boolean bsdsave = FileUtils.writeFile(bitmap, quality, dir, fileName);
		if (!bsdsave) {
			FileOutputStream fos = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			if (fileName.endsWith(".png"))
				bitmap.compress(CompressFormat.PNG, quality, fos);
			else
				bitmap.compress(CompressFormat.JPEG, quality, fos);
			fos.flush();
			fos.close();
		}
	}

	public static void saveImage(Context context, String fileName, int maxsize,
			Bitmap bitmap) throws IOException {
		if (bitmap == null || fileName == null || context == null)
			return;
		Bitmap newbmp = compressImage(bitmap, maxsize);
		FileOutputStream fos = context.openFileOutput(fileName,
				Context.MODE_PRIVATE);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		newbmp.compress(CompressFormat.JPEG, 100, stream);
		byte[] bytes = stream.toByteArray();
		fos.write(bytes);
		fos.close();
		;
	}

	/**
	 * @param image
	 *            ：bitmap
	 * @param size
	 *            :图片最后的大小
	 * @return
	 */
	private static Bitmap compressImage(Bitmap image, int size) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于50kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩比options=50，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
			// String path = Environment.getExternalStorageDirectory()
			// .getAbsolutePath() + "/compress.jpg";
			// FileOutputStream fileOutputStream = new FileOutputStream(new
			// File(
			// path));
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 50,
			// fileOutputStream);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 写图片文件到SD卡
	 * 
	 * @throws IOException
	 */
	public static void saveImageToSD(Context ctx, String filePath,
			Bitmap bitmap, int quality) throws IOException {
		if (bitmap != null) {
			File file = new File(filePath.substring(0,
					filePath.lastIndexOf(File.separator)));
			if (!file.exists()) {
				file.mkdirs();
			}
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(filePath));
			bitmap.compress(CompressFormat.JPEG, quality, bos);
			bos.flush();
			bos.close();
			if (ctx != null) {
				scanPhoto(ctx, filePath);
			}
		}
	}

	/**
	 * 让Gallery上能马上看到该图片
	 */
	private static void scanPhoto(Context ctx, String imgFileName) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File file = new File(imgFileName);
		Uri contentUri = Uri.fromFile(file);
		mediaScanIntent.setData(contentUri);
		ctx.sendBroadcast(mediaScanIntent);
	}

	/**
	 * 获取bitmap
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String fileName) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = context.openFileInput(fileName);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 获取bitmap
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmapByPath(String filePath) {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// true,只是读图片大小，不申请bitmap内存
		opts.inJustDecodeBounds = true;
		bitmap = getBitmapByPath(filePath, opts);
		int size = (opts.outWidth * opts.outHeight);
		if (size > 250 * 1024 * 1) {
			int zoomRate = size / (250 * 1024);
			// zommRate缩放比，根据情况自行设定，如果为2则缩放为原来的1/2，如果为1不缩放
			if (zoomRate <= 0)
				zoomRate = 1;
			opts.inSampleSize = zoomRate;
			// Log.v("AsyncImageLoader", "图片过大，被缩放 1/" + zoomRate);
		}
		opts.inJustDecodeBounds = false;
		bitmap = getBitmapByPath(filePath, opts);

		return bitmap;

	}

	public static Bitmap getBitmapByPath(String filePath,
			BitmapFactory.Options opts) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			
			fis = new FileInputStream(file);
			if (opts != null) {
				opts.inPurgeable = true;
				opts.inInputShareable = true;
			}

			bitmap = BitmapFactory.decodeStream(fis, null, opts);
			file.setLastModified(System.currentTimeMillis());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 获取bitmap
	 * 
	 * @param file
	 * @return
	 */
	public static Bitmap getBitmapByFile(File file) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 使用当前时间戳拼接一个唯一的文件名
	 * 
	 * @param format
	 * @return
	 */
	public static String getTempFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
		String fileName = format.format(new Timestamp(System
				.currentTimeMillis()));
		return fileName;
	}

	/**
	 * 获取照相机使用的目录
	 * 
	 * @return
	 */
	public static String getCamerPath() {
		return Environment.getExternalStorageDirectory() + File.separator
				+ "FounderNews" + File.separator;
	}

	/**
	 * 判断当前Url是否标准的content://样式，如果不是，则返回绝对路径
	 * 
	 * @param uri
	 * @return
	 */
	public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
		String filePath = null;

		String mUriString = mUri.toString();
		mUriString = Uri.decode(mUriString);

		String pre1 = "file://" + SDCARD + File.separator;
		String pre2 = "file://" + SDCARD_MNT + File.separator;

		if (mUriString.startsWith(pre1)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre1.length());
		} else if (mUriString.startsWith(pre2)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre2.length());
		}
		return filePath;
	}

	/**
	 * 通过uri获取文件的绝对路径
	 * 
	 * @param uri
	 * @return
	 */
	public static String getAbsoluteImagePath(Activity context, Uri uri) {
		String imagePath = "";
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(uri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)

		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				imagePath = cursor.getString(column_index);
			}

			cursor.close();
		}

		return imagePath;
	}

	/**
	 * 获取图片缩略图 只有Android2.1以上版本支持
	 * 
	 * @param imgName
	 * @param kind
	 *            MediaStore.Images.Thumbnails.MICRO_KIND
	 * @return
	 */
	public static Bitmap loadImgThumbnail(Activity context, String imgName,
			int kind) {
		Bitmap bitmap = null;

		String[] proj = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME };

		Cursor cursor = context.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
				MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'",
				null, null);

		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			ContentResolver crThumb = context.getContentResolver();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			bitmap = MethodsCompat.getThumbnail(crThumb, cursor.getInt(0),
					kind, options);
		}
		return bitmap;
	}

	public static Bitmap loadImgThumbnail(String filePath, int w, int h) {
		Bitmap bitmap = getBitmapByPath(filePath);
		return zoomBitmap(bitmap, w, h);
	}

	/**
	 * 获取SD卡中最新图片路径
	 * 
	 * @return
	 */
	public static String getLatestImage(Activity context) {
		String latestImage = null;
		String[] items = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA };
		Cursor cursor = context.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items, null,
				null, MediaStore.Images.Media._ID + " desc");

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
						.moveToNext()) {
					latestImage = cursor.getString(1);
					break;
				}
			}

			cursor.close();
		}

		return latestImage;
	}

	/**
	 * 计算缩放图片的宽高
	 * 
	 * @param img_size
	 * @param square_size
	 * @return
	 */
	public static int[] scaleImageSize(int[] img_size, int square_size) {
		if (img_size[0] <= square_size && img_size[1] <= square_size)
			return img_size;
		double ratio = square_size
				/ (double) Math.max(img_size[0], img_size[1]);
		return new int[] { (int) (img_size[0] * ratio),
				(int) (img_size[1] * ratio) };
	}

	/**
	 * 创建缩略图
	 * 
	 * @param context
	 * @param largeImagePath
	 *            原始大图路径
	 * @param thumbfilePath
	 *            输出缩略图路径
	 * @param square_size
	 *            输出图片宽度
	 * @param quality
	 *            输出图片质量
	 * @throws IOException
	 */
	public static void createImageThumbnail(Context context,
			String largeImagePath, String thumbfilePath, int square_size,
			int quality) throws IOException {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		// 原始图片bitmap
		Bitmap cur_bitmap = getBitmapByPath(largeImagePath, opts);

		if (cur_bitmap == null)
			return;

		// 原始图片的高宽
		int[] cur_img_size = new int[] { cur_bitmap.getWidth(),
				cur_bitmap.getHeight() };
		// 计算原始图片缩放后的宽高
		int[] new_img_size = scaleImageSize(cur_img_size, square_size);
		// 生成缩放后的bitmap
		Bitmap thb_bitmap = zoomBitmap(cur_bitmap, new_img_size[0],
				new_img_size[1]);
		// 生成缩放后的图片文件
		saveImageToSD(null, thumbfilePath, thb_bitmap, quality);
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidht = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			try{
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
					true);
			}catch(IllegalArgumentException e){
				newbmp = null;
			}
		}
		return newbmp;
	}
	public static Bitmap scaleBitmap( Bitmap bitmap,int dstW,int dstH){

		int bmpwidth = bitmap.getWidth();
		int bmpheight = bitmap.getHeight();
		int newbmpwidth = 0;
		int newbmpheight = 0;
		Bitmap newbitmap = null;
		if(dstW == 0 || dstH == 0)
			return bitmap;
		if(bmpwidth > dstW || bmpheight > dstH ){
			if(dstW*bmpheight > bmpwidth*dstH ){
				newbmpheight = dstH ;
				newbmpwidth = (int)((bmpwidth*newbmpheight)/bmpheight);
			}else if(dstW*bmpheight  < bmpwidth*dstH ){
				newbmpwidth = dstW;
				newbmpheight = (int)((bmpheight*newbmpwidth)/bmpwidth);
			}
		}
		if(newbmpwidth != 0 || newbmpheight != 0){
			newbitmap = zoomBitmap(bitmap, newbmpwidth, newbmpheight);
		}
		if(newbitmap != null)
			return newbitmap;
		else
			return bitmap;
		
	}
	
	public static Bitmap createNewBitmap(Bitmap bitmap) {
		// 获取这个图片的宽和高
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 定义预转换成的图片的宽度和高度
		int newWidth = 200;
		int newHeight = 200;
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		// 旋转图片 动作
		// matrix.postRotate(45);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return resizedBitmap;
	}

	/**
	 * (缩放)重绘图片
	 * 
	 * @param context
	 *            Activity
	 * @param bitmap
	 * @return
	 */
	public static Bitmap reDrawBitMap(Activity context, Bitmap bitmap) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int rHeight = dm.heightPixels;
		int rWidth = dm.widthPixels;
		// float rHeight=dm.heightPixels/dm.density+0.5f;
		// float rWidth=dm.widthPixels/dm.density+0.5f;
		// int height=bitmap.getScaledHeight(dm);
		// int width = bitmap.getScaledWidth(dm);
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		float zoomScale;
		/** 方式1 **/
		// if(rWidth/rHeight>width/height){//以高为准
		// zoomScale=((float) rHeight) / height;
		// }else{
		// //if(rWidth/rHeight<width/height)//以宽为准
		// zoomScale=((float) rWidth) / width;
		// }
		/** 方式2 **/
		// if(width*1.5 >= height) {//以宽为准
		// if(width >= rWidth)
		// zoomScale = ((float) rWidth) / width;
		// else
		// zoomScale = 1.0f;
		// }else {//以高为准
		// if(height >= rHeight)
		// zoomScale = ((float) rHeight) / height;
		// else
		// zoomScale = 1.0f;
		// }
		/** 方式3 **/
		if (width >= rWidth)
			zoomScale = ((float) rWidth) / width;
		else
			zoomScale = 1.0f;
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(zoomScale, zoomScale);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 将Drawable转化为Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	/**
	 * 获得圆角图片的方法
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            一般设成14
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 获得带倒影的图片方法
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 将bitmap转化为drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	/**
	 * 获取图片类型
	 * 
	 * @param file
	 * @return
	 */
	public static String getImageType(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			String type = getImageType(in);
			return type;
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 获取图片的类型信息
	 * 
	 * @param in
	 * @return
	 * @see #getImageType(byte[])
	 */
	public static String getImageType(InputStream in) {
		if (in == null) {
			return null;
		}
		try {
			byte[] bytes = new byte[8];
			in.read(bytes);
			return getImageType(bytes);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 获取图片的类型信息
	 * 
	 * @param bytes
	 *            2~8 byte at beginning of the image file
	 * @return image mimetype or null if the file is not image
	 */
	public static String getImageType(byte[] bytes) {
		if (isJPEG(bytes)) {
			return "image/jpeg";
		}
		if (isGIF(bytes)) {
			return "image/gif";
		}
		if (isPNG(bytes)) {
			return "image/png";
		}
		if (isBMP(bytes)) {
			return "application/x-bmp";
		}
		return null;
	}

	private static boolean isJPEG(byte[] b) {
		if (b.length < 2) {
			return false;
		}
		return (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
	}

	private static boolean isGIF(byte[] b) {
		if (b.length < 6) {
			return false;
		}
		return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
				&& (b[4] == '7' || b[4] == '9') && b[5] == 'a';
	}

	private static boolean isPNG(byte[] b) {
		if (b.length < 8) {
			return false;
		}
		return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78
				&& b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10
				&& b[6] == (byte) 26 && b[7] == (byte) 10);
	}

	private static boolean isBMP(byte[] b) {
		if (b.length < 2) {
			return false;
		}
		return (b[0] == 0x42) && (b[1] == 0x4d);
	}

	/**
	 * 生成一个圆环底图和指定角度的圆环
	 * 
	 * @param radius
	 * @param borderWidth
	 * @param bordercolor
	 * @param circlecolor
	 * @param startAngle
	 * @param sweepAngle
	 * @return
	 */
	public static Bitmap creatArcsBitmap(int radius, int borderWidth,
			int bordercolor, int circlecolor, int startAngle, int sweepAngle) {
		Bitmap output = Bitmap.createBitmap(radius * 2, radius * 2,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Rect rect = new Rect((int) 0, (int) 0, (int) radius * 2,
				(int) radius * 2);

		final Paint paintframe = new Paint();
		paintframe.setStyle(Paint.Style.STROKE);
		paintframe.setAntiAlias(true);
		paintframe.setStrokeWidth(borderWidth);
		paintframe.setColor(circlecolor);
		int bw = (borderWidth / 2) * 2;

		int r = radius - borderWidth / 2;
		canvas.drawCircle(radius, radius, r, paintframe);

		drawArcs(canvas, rect, bw, bordercolor, startAngle, sweepAngle);
		return output;
	}

	/**
	 * 在canvas 上画圆环；
	 * 
	 * @param canvas
	 *            ：
	 * @param rect
	 *            ：画圆环区域
	 * @param borderWidth
	 * @param bordercolor
	 * @param startAngle
	 * @param sweepAngle
	 */
	public static void drawArcs(Canvas canvas, Rect rect, int borderWidth,
			int bordercolor, int startAngle, int sweepAngle) {

		float bw = (borderWidth / 2) * 2;
		int offsetbw = (borderWidth / 2);
		final Rect arcr = new Rect((int) rect.left + offsetbw, (int) rect.top
				+ offsetbw, (int) rect.right - offsetbw, (int) rect.bottom
				- offsetbw);
		final RectF arcrF = new RectF(arcr);
		final Paint paintframe = new Paint();
		paintframe.setStyle(Paint.Style.STROKE);
		paintframe.setAntiAlias(true);
		paintframe.setStrokeWidth(bw);
		paintframe.setColor(bordercolor);
		canvas.drawArc(arcrF, startAngle, sweepAngle, false, paintframe);
	}

	/**
	 * 将bitmap图片转为圆形图片，并可以加圆形边框
	 * 
	 * @param bitmap
	 *            : 传入Bitmap对象
	 * @param borderWidth
	 *            :边框宽度
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap, int borderWidth,
			int bordercolor) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int roundPx;
		int left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			int clip = (height - width) / 2;
			top = clip;
			bottom = top + 2 * roundPx;
			left = 0;
			right = 2 * roundPx;
			height = width = 2 * roundPx;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			int clip = (width - height) / 2;
			left = clip;
			right = left + 2 * roundPx;
			top = 0;
			bottom = 2 * roundPx;

			width = height = 2 * roundPx;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);

		final Paint paintframe = new Paint();
		paintframe.setStyle(Paint.Style.STROKE);
		paintframe.setAntiAlias(true);
		paintframe.setStrokeWidth(borderWidth);
		paintframe.setColor(bordercolor);
		float r = (float) roundPx - ((float) borderWidth) / 2;
		canvas.drawCircle(roundPx, roundPx, r, paintframe);

		return output;
	}

	/**
	 * 用于生成简历头像
	 * 
	 * @param srcbmp
	 *            :头像源图片
	 * @return
	 */
	public static Bitmap createRoundHeadPhoto(Activity context, Bitmap srcbmp) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = (int) (Constants.ResumeModule.HEADPHOTO_STAND_RADIUS * 2 * dm.density);
		int height = (int) (Constants.ResumeModule.HEADPHOTO_STAND_RADIUS * 2 * dm.density);
		int boderwidth = (int) (Constants.ResumeModule.HEADPHOTO_STAND_BODER_WIDTH * dm.density);
		Bitmap photobmp = Bitmap
				.createScaledBitmap(srcbmp, width, height, true);
		photobmp = ImageUtils.toRoundBitmap(photobmp, boderwidth,
				Constants.ResumeModule.HEADPHOTO_STAND_BODER_COLOR);
		return photobmp;
	}

	/**
	 * 用于生成简历完成度图片
	 * 
	 * @param degree
	 *            :0-100
	 * @return
	 */
	public static Bitmap createCompleteDegreeBitmap(Activity context, int degree) {
		int startAngle = -90;
		int sweepAngle = (360 * degree) / 100;
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int radius = (int) (COMPLETEBMP_RADIUS * dm.density);
		int boderwidth = (int) (COMPLETEBMP_BODER_WIDTH * dm.density);

		Bitmap completebmp = ImageUtils.creatArcsBitmap(radius, boderwidth,
				COMPLETEBMP_ARCS_COLOR, COMPLETEBMP_CIRCLE_COLOR, startAngle,
				sweepAngle);
		return completebmp;

	}

	/**
	 * 用于生成简历完成度图片
	 * 
	 * @param context
	 *            ：activity 实例
	 * @param degree
	 *            ：完成度
	 * @param radius
	 *            ：外圆半径，单位 dp
	 * @param boderwidth
	 *            : 边框宽度，单位 dp
	 * @param bordercolor
	 *            ：边框颜色
	 * @param circlecolor
	 *            ：边框底色
	 * @return
	 */
	public static Bitmap createCompleteDegreeBitmap(Activity context,
			int degree, int radius, int boderwidth, int bordercolor,
			int circlecolor) {
		int startAngle = -90;
		int sweepAngle = (360 * degree) / 100;
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int radiuspx = (int) (radius * dm.density);
		int boderwidthpx = (int) (boderwidth * dm.density);

		Bitmap completebmp = ImageUtils.creatArcsBitmap(radiuspx, boderwidthpx,
				bordercolor, circlecolor, startAngle, sweepAngle);
		return completebmp;

	}

	/**
	 * bitmap转byte
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] getBitmapByte(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		try {
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	/**
	 * byte 转 bitmap
	 * 
	 * @param temp
	 * @return
	 */
	public static Bitmap getBitmapFromByte(byte[] temp) {
		if (temp != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		} else {
			return null;
		}
	}

	/**
	 * 把图片转换成圆形
	 * @param activity
	 * @param bitmap
	 * @return
	 */
	public static Bitmap filletRoundBitmap(Activity activity, Bitmap bitmap){
		if (null == bitmap) {
			return null;
		}
		
		// 剪切缩放图片
		Bitmap bmpScaled = scaledBitmap(activity, bitmap, 100);
		
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int nWidth = (int) (Constants.ResumeModule.HEADPHOTO_STAND_RADIUS * 2 * dm.density);
		int nHeight = (int) (Constants.ResumeModule.HEADPHOTO_STAND_RADIUS * 2 * dm.density);
		
		Bitmap bmpOutPut = Bitmap
				.createScaledBitmap(bmpScaled, nWidth, nHeight, true);
		
//		Canvas canvas = new Canvas(bmpScaled);
//		final Paint paint = new Paint();
//		final RectF rectF = new RectF(new Rect(0, 0, nWidth, nHeight));
//		paint.setAntiAlias(true);
//		paint.setColor(nColor);
//		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		
		return bmpOutPut;
	}
	
	/**
	 * 把图片转换成圆角，并且加圆角边框
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @param nColor
	 * @return
	 */
	public static Bitmap filletFrameBitmap(Activity activity, Bitmap bitmap,
			float roundPx, int nColor) {
		if (null == bitmap) {
			return null;
		}

		// 剪切缩放图片
		Bitmap bmpScaled = scaledBitmap(activity, bitmap, 100);
		// 
		int nWidth = bmpScaled.getWidth();
		int nHeight = bmpScaled.getHeight();

		// 把图片转换成圆角图片
		Bitmap bmpfillet = filletBitmap(activity, bmpScaled, roundPx);
		int nfilletWidth = bmpfillet.getWidth();
		int nfilletHeight = bmpfillet.getHeight();

		// 画圆角背景
		Bitmap bmpBk = filletFrameBkBitmap(nWidth, nHeight, roundPx, nColor);

		//
		Bitmap bmpOutPut = Bitmap.createBitmap(nWidth, nHeight,
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bmpOutPut);
		canvas.drawBitmap(bmpBk, 0, 0, null);
		canvas.drawBitmap(bmpfillet, (nWidth - nfilletWidth)/2, (nHeight - nfilletHeight)/2, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();

		return bmpOutPut;
	}

	/**
	 * 把图片转换成圆角图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap filletBitmap(Activity activity, Bitmap bitmap,
			float roundPx) {
		if (null == bitmap) {
			return null;
		}

		// 缩小图片
		Bitmap bmpNew = reduceBitmap(bitmap, roundPx);

		// 获得图片的宽高
		int nNewWidth = bmpNew.getWidth();
		int nNewHeight = bmpNew.getHeight();

		Bitmap bmpOutPut = Bitmap.createBitmap(nNewWidth, nNewHeight,
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bmpOutPut);

		final Paint paint = new Paint();

		final Rect rect = new Rect(0, 0, nNewWidth, nNewHeight);

		final RectF rectF = new RectF(new Rect(0, 0, nNewWidth, nNewHeight));

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);

		paint.setColor(Color.RED);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		final Rect src = new Rect(0, 0, nNewWidth, nNewHeight);

		canvas.drawBitmap(bmpNew, src, rect, paint);

		return bmpOutPut;
	}

	/**
	 * 缩小图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap reduceBitmap(Bitmap bitmap, float floatSize) {
		if (null == bitmap) {
			return null;
		}

		// 获得图片的宽高
		int nWidth = bitmap.getWidth();
		int nHeight = bitmap.getHeight();

		// 设置想要的大小
		int nNewWidth = (int) (nWidth - floatSize);
		int nNewHeight = (int) (nHeight - floatSize);

		// 计算缩放比例
		float scaleWidth = ((float) nNewWidth) / nWidth;
		float scaleHeight = ((float) nNewHeight) / nHeight;

		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		// 得到新的图片
		// Bitmap bmpOutPut = Bitmap.createBitmap(bitmap, 0, 0, nNewWidth,
		// nNewHeight, matrix,
		// true);
		Bitmap bmpOutPut = Bitmap.createBitmap(nNewWidth, nNewHeight,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bmpOutPut);
		canvas.drawBitmap(bitmap, matrix, null);

		return bmpOutPut;
	}

	public static Bitmap scaledBitmap(Activity activity, Bitmap bitmap,
			int nWidth) {
		if (null == bitmap) {
			return null;
		}

		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int radiuspx = (int) (nWidth * dm.density);

		double wh = bitmap.getWidth() / bitmap.getHeight();
		if (wh == 0) {
			wh = 1;
		}

		Bitmap bmpOutPut = Bitmap.createScaledBitmap(bitmap,
				(int) (radiuspx * wh), radiuspx, true);

		return bmpOutPut;
	}

	/**
	 * 画圆角背景
	 * 
	 * @param nWidth
	 * @param nHeight
	 * @param roundPx
	 * @param nColor
	 * @return
	 */
	public static Bitmap filletFrameBkBitmap(int nWidth, int nHeight,
			float roundPx, int nColor) {
		Bitmap bmpBk = Bitmap.createBitmap(nWidth, nHeight, Config.ARGB_8888);

		Canvas canvas = new Canvas(bmpBk);

		final Paint paint = new Paint();

		final RectF rectF = new RectF(new Rect(0, 0, nWidth, nHeight));

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);

		paint.setColor(nColor);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		return bmpBk;
	}

	/**
	 * 计算压缩比例值
	 * 
	 * @param options
	 *            解析图片的配置信息
	 * @param reqWidth
	 *            所需图片压缩尺寸最小宽度
	 * @param reqHeight
	 *            所需图片压缩尺寸最小高度
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 保存图片原宽高值
		final int height = options.outHeight;
		final int width = options.outWidth;
		// 初始化压缩比例为1
		int inSampleSize = 1;

		// 当图片宽高值任何一个大于所需压缩图片宽高值时,进入循环计算系统
		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// 压缩比例值每次循环两倍增加,
			// 直到原图宽高值的一半除以压缩值后都~大于所需宽高值为止
			while ((halfHeight / inSampleSize) >= reqHeight
					&& (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}
}

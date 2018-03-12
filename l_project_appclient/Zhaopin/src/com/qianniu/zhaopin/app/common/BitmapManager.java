package com.qianniu.zhaopin.app.common;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.qianniu.zhaopin.app.AppConfig;
import com.qianniu.zhaopin.app.AppContext;
import com.qianniu.zhaopin.app.AppException;
import com.qianniu.zhaopin.app.api.ApiClient;
import com.qianniu.zhaopin.app.bean.URLs;
import com.qianniu.zhaopin.app.constant.Constants;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Matrix;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 异步线程加载图片工具类 使用说明： BitmapManager bmpManager; bmpManager = new
 * BitmapManager(BitmapFactory.decodeResource(context.getResources(),
 * R.drawable.loading)); bmpManager.loadBitmap(imageURL, imageView);
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-6-25
 */
public class BitmapManager {
	private static final String TAG = "BitmapManager";

	public final static int BITMAPMANAGER_DEFAULTIMG = 1000;
	public final static int BITMAPMANAGER_OBTAINIMG = 1001;

	public final static String BITMAPMANAGER_KEY_BITMAP = "bitmap";
	public final static String BITMAPMANAGER_KEY_URL = "url";

	private static HashMap<String, SoftReference<Bitmap>> cache;
	private static ExecutorService pool;
	private static Map<ImageView, String> imageViews;
	private Bitmap defaultBmp;

	static {
		cache = new HashMap<String, SoftReference<Bitmap>>();
		pool = Executors.newFixedThreadPool(6); // 固定线程池
		imageViews = Collections
				.synchronizedMap(new WeakHashMap<ImageView, String>());
		pool.shutdown();
	}

	public BitmapManager() {
	}

	public BitmapManager(Bitmap def) {
		this.defaultBmp = def;
	}
	public static void shutDownAllLoad(){
		pool.shutdownNow();
		
	}

	/**
	 * 设置默认图片
	 * 
	 * @param bmp
	 */
	public void setDefaultBmp(Bitmap bmp) {
		defaultBmp = bmp;
	}

	/**
	 * 加载图片
	 * 
	 * @param url
	 * @param imageView
	 */
	public void loadBitmap(String url, Context context, Handler handler) {
		if (url == null) {
			int i = 0;
			i = 1;
		}
		loadBitmap(url, context, handler, this.defaultBmp, 0, 0);
	}

	/**
	 * 加载图片
	 * 
	 * @param url
	 * @param imageView
	 */
	public void loadBitmap(String url, ImageView imageView) {
		if (url == null) {
			int i = 0;
			i = 1;
		}
		loadBitmap(url, imageView, this.defaultBmp, 0, 0);
	}

	public void loadBitmap(String url, ImageView imageView, String dir,
			Bitmap defaultBmp) {
		if (url == null) {
			int i = 0;
			i = 1;
		}
		loadBitmap(url, imageView, dir, defaultBmp, 0, 0);
	}

	/**
	 * 加载图片-可设置加载失败后显示的默认图片
	 * 
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp) {
		loadBitmap(url, imageView, defaultBmp, 0, 0);
	}

	/**
	 * 加载图片-可设置加载失败后显示的默认图片,图片先通过GraphicImgCreator改变后，栽加载入ImageView
	 * 
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 * @param creator
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp,
			GraphicImgCreator creator) {
		loadBitmap(url, imageView, defaultBmp, 0, 0, creator);
	}

	/**
	 * 加载圆形的图片
	 * 
	 * @param activity
	 * @param url
	 * @param imageView
	 */
	public void loadMiddleRoundBitmap(Activity activity, String url,
			ImageView imageView) {
		CommonRoundImgCreator creator = new CommonRoundImgCreator(activity,
				Constants.InfoModule.LIST_ITEM_MIDDLE_RADIUS, 0, 0);
		loadBitmap(url, imageView, defaultBmp, 0, 0, creator);
	}

	/**
	 * 加载图片-可指定显示图片的高宽
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp,
			int width, int height) {
		loadBitmap(url, imageView, defaultBmp, width, height, null);
	}

	public void loadBitmap(String url, ImageView imageView, String dir,
			Bitmap defaultBmp, int width, int height) {
		loadBitmap(url, imageView, dir, defaultBmp, width, height, null);
	}

	/*
	 * public void loadBitmap(String url, ImageView imageView, String dir,
	 * Bitmap defaultBmp, int width, int height, GraphicImgCreator creator) {
	 * imageViews.put(imageView, url); Bitmap bitmap = getBitmapFromCache(url);
	 * 
	 * bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true); if
	 * (bitmap != null) { // 显示缓存图片 if (creator != null) bitmap =
	 * creator.creator(bitmap); imageView.setImageBitmap(bitmap); } else { if
	 * (url == null || url.length() == 0) { Bitmap bmp = null; if (creator !=
	 * null && defaultBmp != null) bmp = creator.creator(defaultBmp); if (bmp !=
	 * null) imageView.setImageBitmap(bmp); return; }
	 *//** old */
	/*
	 * // 加载SD卡中的图片缓存 String filename = FileUtils.getFileName(url); String
	 * filepath = imageView.getContext().getFilesDir() + File.separator +
	 * filename; File file = new File(filepath); if (file.exists()) { //
	 * 显示SD卡中的图片缓存 Bitmap bmp = ImageUtils.getBitmap(imageView.getContext(),
	 * filename); if (creator != null) bmp = creator.creator(bmp);
	 * imageView.setImageBitmap(bmp); }
	 * 
	 * // 加载SD卡中的图片缓存
	 * 
	 * // String filename = FileUtils.getFileName(url); // bitmap =
	 * getBitmapFromFile(imageView.getContext(), filename); // // if (bitmap !=
	 * null) { // // 显示SD卡中的图片缓存 // if (creator != null) // bitmap =
	 * creator.creator(bitmap); // imageView.setImageBitmap(bitmap); //
	 * cache.put(url, new SoftReference<Bitmap>(bitmap)); //并将图片缓存到内存中 // } else
	 * { // // 线程加载网络图片 // Bitmap bmp = defaultBmp; // if(bmp == null) // bmp =
	 * this.defaultBmp; // if (creator != null && bmp != null) // bmp =
	 * creator.creator(bmp); // // if(bmp!=null) //
	 * imageView.setImageBitmap(bmp); // queueJob(url, null, imageView, null,
	 * width, height, creator); // }
	 *//** new */
	/*
	 * // 内存中没有从sd卡或网络获取 Bitmap bmp = defaultBmp; if(bmp == null) bmp =
	 * this.defaultBmp; if (creator != null && bmp != null) bmp =
	 * creator.creator(bmp);
	 * 
	 * if(bmp!=null) imageView.setImageBitmap(bmp); queueJob(url, null,
	 * imageView, dir, null, width, height, creator); } }
	 */

	public void loadBitmap(String url, ImageView imageView, String dir,
			Bitmap defaultBmp, int width, int height, GraphicImgCreator creator) {
		int reqW = width;
		int reqH = height;
		Bitmap bmp = null;

		if (imageView == null) {
			Log.i(TAG, "loadBitmap imageView = null");
			return;
		}

		Log.i(TAG, "loadBitmap imageView =" + imageView.getId() + ",url = "
				+ url);
		imageViews.put(imageView, url);
		/*
		 * if(reqW == 0 || reqH == 0){ reqW = imageView.getWidth(); reqH =
		 * imageView.getHeight(); }
		 */
		Bitmap bitmap = getBitmapFromCache(url);
		Log.i(TAG, "loadBitmap getBitmapFromCache imageView =" + imageView.getId() + ",url = "
				+ url+",bitmap = "+bitmap);

		if (bitmap != null) {
			bmp = bitmap;
		} else if (defaultBmp != null) {
			bmp = defaultBmp;

		}
		if (bmp != null) {
			bmp = ImageUtils.scaleBitmap(bmp, reqW, reqH);// createScaledBitmap(defaultBmp,
															// reqW,reqH);
			if (creator != null) {
				bmp = creator.creator(bmp);
			}
			if (bmp != null)
				imageView.setImageBitmap(bmp);
		}
		if (bitmap == null) {
			queueJob(url, null, imageView, dir, null, reqW, reqH, creator);
		}

	}

	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp,
			int width, int height, GraphicImgCreator creator) {
		loadBitmap(url, imageView, AppConfig.DEFAULT_SAVE_IMAGE_ABSOLUTE_PATH, defaultBmp,
				width, height, creator);
	}

	/**
	 * 加载图片-可指定显示图片的高宽
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void loadBitmap(String url, Context context, Handler handler,
			Bitmap defaultBmp, int width, int height) {

		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap != null) {
			// 显示缓存图片
			sendBitmapByHandler(bitmap, url, handler, BITMAPMANAGER_OBTAINIMG);
		} else {
			// 加载SD卡中的图片缓存

			/** old */
			// String filename = FileUtils.getFileName(url);
			// bitmap = getBitmapFromFile(context, filename);
			//
			// if (bitmap != null) {
			// sendBitmapByHandler(bitmap, url, handler,
			// BITMAPMANAGER_OBTAINIMG);
			// } else {
			// // 线程加载网络图片
			// sendBitmapByHandler(bitmap, url, handler,
			// BITMAPMANAGER_DEFAULTIMG);
			// queueJob(url, context, null, handler, width, height, null);
			// }
			/** new */
			queueJob(url, context, null, handler, width, height, null);
		}
	}

	private void sendBitmapByHandler(Bitmap bitmap, String url,
			Handler handler, int state) {
		Message message = new Message();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(BITMAPMANAGER_KEY_BITMAP, bitmap);
		map.put(BITMAPMANAGER_KEY_URL, url);
		message.what = state;
		message.obj = map;
		handler.sendMessage(message);
	}

	/**
	 * 从缓存中获取图片
	 * 
	 * @param url
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		if (cache.containsKey(url)) {
			MyLogger.i(TAG, "getBitmapFromCache##" + url);
			bitmap = cache.get(url).get();
		}
		return bitmap;
	}

	/**
	 * 从SD卡和内部存储中获取图片；
	 * 
	 * @param context
	 * @param filename
	 * @return
	 */
	public static Bitmap getBitmapFromFile(Context context, String filename) {
		return getBitmapFromFile(context,
				AppConfig.DEFAULT_SAVE_IMAGE_ABSOLUTE_PATH, filename);
	}

	public static Bitmap getBitmapFromFile(Context context, String dir,
			String filename) {
		Bitmap bitmap = null;
		// 加载SD卡中的图片缓存
		if (filename == null)
			return null;
		String sdfilepath = new String(dir);
		if (!sdfilepath.endsWith("/"))
			sdfilepath += "/";
		sdfilepath += filename;
		sdfilepath = FileUtils.checkAbsoluteFilePath(sdfilepath);
		bitmap = ImageUtils.getBitmapByPath(sdfilepath);
		if (bitmap == null) {
			String filepath = context.getFilesDir() + File.separator + filename;
			File file = new File(filepath);
			if (file.exists()) {

				bitmap = ImageUtils.getBitmap(context, filename);

				file.setLastModified(System.currentTimeMillis());
			}
		}

		return bitmap;
	}

	/**
	 * 从网络中加载图片 默认缓存目录 AppConfig.SDCARD_IMAGE_ROOT
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void queueJob(final String url, final Context context,
			final ImageView imageView, final Handler handler, final int width,
			final int height, final GraphicImgCreator creator) {
		queueJob(url, context, imageView, AppConfig.SDCARD_IMAGE_ROOT, handler,
				width, height, creator);
	}

	public void queueJob(final String url, final Context context,
			final ImageView imageView, final String dir, final Handler handler,
			final int width, final int height, final GraphicImgCreator creator) {
		/* Create handler in UI thread. */
		final Handler mhandler = new Handler() {
			Handler sendhandler = handler;

			public void handleMessage(Message msg) {
				if (sendhandler == null) {
					int i = 0;
				}
				String tag = imageViews.get(imageView);

				if (!tag.equals(url)) {
//					Log.e("jlf", "queueJob handleMessage imageView ="
//							+ imageView.getId() + ",url = " + url + ",tag = "
//							+ tag);
				}
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						if (imageView != null) {
							Bitmap bmp = (Bitmap) msg.obj;
							if (creator != null)
								bmp = creator.creator(bmp);
							// if (bmp != null)
							imageView.setImageBitmap(bmp);
						}
						if (handler != null) {
							sendBitmapByHandler((Bitmap) msg.obj, url, handler,
									BITMAPMANAGER_OBTAINIMG);
						}
					}
				} else {
					if (sendhandler != null && msg.obj != null) {
						sendBitmapByHandler((Bitmap) msg.obj, url, handler,
								BITMAPMANAGER_OBTAINIMG);
					}
				}
			}
		};
		if (url == null || url.length() == 0)
			return;
		if(pool.isShutdown()){
			pool = Executors.newFixedThreadPool(6);

		}
		pool.execute(new Runnable() {
			public void run() {

				Bitmap bitmap = getBitmapFromCache(url);

				if (bitmap == null) {
					String filename = FileUtils.getFileName(url);

					bitmap = getBitmapFromFile(imageView.getContext(), dir,
							filename);
					if (bitmap != null)
						cache.put(url, new SoftReference<Bitmap>(bitmap)); // 并将图片缓存到内存中
				}
				if (bitmap == null) {

					// if(AppContext.isNetworkConnected(context))
					bitmap = downloadBitmap(url, width, height);

					if (bitmap != null) {

						cache.put(url, new SoftReference<Bitmap>(bitmap)); // 并将图片缓存到内存中
						saveImage(context, imageView, dir, url, bitmap);// 将图片缓存到文件中

					}
				}
				if (bitmap != null) {
					bitmap = ImageUtils.scaleBitmap(bitmap, width, height);// createScaledBitmap(bitmap,
																			// width,
																			// height);
				}

				Message message = Message.obtain();
				message.obj = bitmap;
				mhandler.sendMessage(message);
			}
		});
	}

	private void saveImage(final Context context, final ImageView imageView,
			final String dir, final String url, final Bitmap bitmap) {

		ImageUtils.pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					// 向SD卡中写入图片缓存
					Context mcontext = null;
					if (context != null)
						mcontext = context;
					else if (imageView != null)
						mcontext = imageView.getContext();
					if (mcontext != null) {
						/*
						 * ImageUtils.saveImage(imageView.getContext(),
						 * FileUtils.getFileName(url), 200 * 1024, (Bitmap)
						 * msg.obj);
						 */
						ImageUtils.saveImage(imageView.getContext(), dir,
								FileUtils.getFileName(url), bitmap);
					}
				} catch (IOException e) {
					// //e.printStackTrace();
				}
			}
		});

	}

	/**
	 * 下载图片-可指定显示图片的高宽
	 * 
	 * @param url
	 * @param width
	 * @param height
	 */
	private Bitmap downloadBitmap(String url, int width, int height) {
		Bitmap bitmap = null;
		try {
			if (url == null)
				return null;
			// http加载图片
			String imgurl = URLs.formatURL(url);

			bitmap = ApiClient.getNetBitmap(imgurl);
			if (width > 0 && height > 0 && bitmap != null) {

				// 指定显示图片的高宽
				bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			}
			// 放入缓存
			if (bitmap != null)
				cache.put(url, new SoftReference<Bitmap>(bitmap));
			MyLogger.i(TAG, "图片放入缓存##" + url);
		} catch (AppException e) {
			// e.printStackTrace();
		}
		return bitmap;
	}

	// Bitmap转换成Drawable
	public Drawable bitmap2Drawable(Bitmap bitmap) {
		@SuppressWarnings("deprecation")
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		Drawable d = (Drawable) bd;
		return d;
	}

	public Bitmap shrinkBitmapToDisplay(Bitmap bitmap, int displayWidth,
			int displayHeight) {

		int bmpwidth = bitmap.getWidth();
		int bmpheight = bitmap.getHeight();
		int newbmpwidth = 0;
		int newbmpheight = 0;
		Bitmap newbitmap = null;
		if (bmpwidth > displayWidth || bmpheight > displayHeight) {
			if (displayWidth * bmpheight > bmpwidth * displayHeight) {
				newbmpheight = displayHeight - 6;
				newbmpwidth = (int) ((bmpwidth * newbmpheight) / bmpheight);
			} else if (displayWidth * bmpheight < bmpwidth * displayHeight) {
				newbmpwidth = displayWidth - 6;
				newbmpheight = (int) ((bmpheight * newbmpwidth) / bmpwidth);
			}
		}
		if (newbmpwidth != 0 || newbmpheight != 0) {
			newbitmap = zoomBitmap(bitmap, newbmpwidth, newbmpheight);
		}
		if (newbitmap != null)
			return newbitmap;
		else
			return bitmap;

	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	/**
	 * 加载图片, 然后添加圆角和圆角边框
	 * 
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 * @param floatSize
	 * @param nColor
	 */
	public void loadFilletFrameBitmap(Activity activity, String url,
			ImageView imageView, Bitmap defaultBmp, float floatSize, int nColor) {

		imageViews.put(imageView, url);

		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap != null) {
			// 显示缓存图片
			imageView.setImageBitmap(ImageUtils.filletFrameBitmap(activity,
					bitmap, floatSize, nColor));
		} else {
			if (url == null || url.length() == 0) {
				imageView.setImageBitmap(ImageUtils.filletFrameBitmap(activity,
						defaultBmp, floatSize, nColor));
				return;
			}

			// 内存中没有从sd卡或网络获取
			// Bitmap bmp = defaultBmp;
			// if(null == bmp){
			// bmp = this.defaultBmp;
			// }
			//
			// if(null != bmp){
			// imageView.setImageBitmap(ImageUtils.filletFrameBitmap(
			// activity, bmp, floatSize, nColor));
			// }

			// 从网络中加载图片
			queueJobFilletFrameBitmap(activity, url, null, imageView,
					AppConfig.SDCARD_IMAGE_ROOT, floatSize, nColor);
		}
	}

	/**
	 * @param activity
	 * @param url
	 * @param context
	 * @param imageView
	 * @param dir
	 * @param floatSize
	 * @param nColor
	 */
	public void queueJobFilletFrameBitmap(final Activity activity,
			final String url, final Context context, final ImageView imageView,
			final String dir, final float floatSize, final int nColor) {
		/* Create handler in UI thread. */
		final Handler mhandler = new Handler() {

			public void handleMessage(Message msg) {

				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						if (imageView != null) {
							Bitmap bmp = (Bitmap) msg.obj;
							imageView.setImageBitmap(ImageUtils
									.filletFrameBitmap(activity, bmp,
											floatSize, nColor));
						}
					}
				}
			}
		};

		pool.execute(new Runnable() {
			public void run() {
				String filename = FileUtils.getFileName(url);
				Bitmap bitmap = getBitmapFromFile(imageView.getContext(), dir,
						filename);

				if (bitmap == null) {
					bitmap = downloadBitmap(url, 0, 0);
					saveImage(context, imageView, dir, url, bitmap);// 将图片缓存到文件中
				}
				cache.put(url, new SoftReference<Bitmap>(bitmap)); // 并将图片缓存到内存中
				Message message = Message.obtain();
				message.obj = bitmap;
				mhandler.sendMessage(message);
				if (bitmap != null) {
					// 显示SD卡中的图片缓存
					// if (creator != null)
					// bitmap = creator.creator(bitmap);
					cache.put(url, new SoftReference<Bitmap>(bitmap)); // 并将图片缓存到内存中
				}
			}
		});
	}
	
	/**
	 * 加载图片, 然后添加圆角和圆角边框
	 * 
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 * @param floatSize
	 * @param nColor
	 */
	public void loadFilletRoundBitmap(Activity activity, String url,
			ImageView imageView, Bitmap defaultBmp) {

		imageViews.put(imageView, url);

		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap != null) {
			bitmap = ImageUtils.filletRoundBitmap(activity,
					bitmap);
			
			imageView.setImageBitmap(bitmap);
			
			// 显示缓存图片
//			imageView.setImageBitmap(ImageUtils.filletRoundBitmap(activity,
//					bitmap));
			
			saveImage(activity, imageView, AppConfig.SDCARD_IMAGE_ROOT, "wzy0001.jpg", bitmap);
		} else {
			if (url == null || url.length() == 0) {
				imageView.setImageBitmap(ImageUtils.filletRoundBitmap(activity,
						defaultBmp));
				return;
			}

			// 内存中没有从sd卡或网络获取
			// Bitmap bmp = defaultBmp;
			// if(null == bmp){
			// bmp = this.defaultBmp;
			// }
			//
			// if(null != bmp){
			// imageView.setImageBitmap(ImageUtils.filletFrameBitmap(
			// activity, bmp, floatSize, nColor));
			// }

			// 从网络中加载图片
			queueJobFilletRoundBitmap(activity, url, null, imageView,
					AppConfig.SDCARD_IMAGE_ROOT);
		}
	}

	/**
	 * @param activity
	 * @param url
	 * @param context
	 * @param imageView
	 * @param dir
	 */
	public void queueJobFilletRoundBitmap(final Activity activity,
			final String url, final Context context, final ImageView imageView,
			final String dir) {
		/* Create handler in UI thread. */
		final Handler mhandler = new Handler() {

			public void handleMessage(Message msg) {

				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						if (imageView != null) {
							Bitmap bmp = (Bitmap) msg.obj;
//							imageView.setImageBitmap(ImageUtils
//									.filletRoundBitmap(activity, bmp));
							bmp = ImageUtils
									.filletRoundBitmap(activity, bmp);
							imageView.setImageBitmap(bmp);
							
							saveImage(context, imageView, AppConfig.SDCARD_IMAGE_ROOT, "wzy0002.jpg", bmp);
						}
					}
				}
			}
		};

		pool.execute(new Runnable() {
			public void run() {
				String filename = FileUtils.getFileName(url);
				Bitmap bitmap = getBitmapFromFile(imageView.getContext(), dir,
						filename);

				if (bitmap == null) {
					bitmap = downloadBitmap(url, 0, 0);
					saveImage(context, imageView, dir, url, bitmap);// 将图片缓存到文件中
				}
				cache.put(url, new SoftReference<Bitmap>(bitmap)); // 并将图片缓存到内存中
				Message message = Message.obtain();
				message.obj = bitmap;
				mhandler.sendMessage(message);
				if (bitmap != null) {
					// 显示SD卡中的图片缓存
					// if (creator != null)
					// bitmap = creator.creator(bitmap);
					cache.put(url, new SoftReference<Bitmap>(bitmap)); // 并将图片缓存到内存中
				}
			}
		});
	}
	
	public static Bitmap createScaledBitmap(Bitmap src, int dstWidth,
			int dstHeight) {
		Bitmap dst = null;

		try {
			if (dstWidth == 0 || dstHeight == 0) {
				return src;
			}
			dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);
		} catch (IllegalArgumentException e) {
			dst = src;
		}
		return dst;
	}

}
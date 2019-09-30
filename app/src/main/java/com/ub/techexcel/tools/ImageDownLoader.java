package com.ub.techexcel.tools;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageDownLoader {
	/**
	 * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	/**
	 * 操作文件相关类对象的引用
	 */
	private FileUtils fileUtils;
	/**
	 * 下载Image的线程池 ExecutorService 建立多线程的步骤： 线程池
	 */
	private ExecutorService mImageThreadPool = null;

	/**
	 * 设置此程序缓存图片的空间大小
	 * 
	 * @param context
	 */
	public ImageDownLoader(Context context) {
		// 获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int mCacheSize = maxMemory / 8;
		// 给LruCache分配1/8 4M
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {
			// 必须重写此方法，来测量Bitmap的大小
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// 在每次存入缓存的时候调用
				return value.getRowBytes() * value.getHeight();
				// return value.getByteCount();
			}
		};
		fileUtils = new FileUtils(context);
	}

	/**
	 * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
	 * 
	 * @return
	 */
	public ExecutorService getThreadPool() {
		if (mImageThreadPool == null) {
			synchronized (ExecutorService.class) {
				if (mImageThreadPool == null) {
					// 获得当前系统CPU的数目
					int cpuNums = Runtime.getRuntime().availableProcessors();// 任意时间点
																				// 最多只有固定数目的活动线程存在
					// 此时如果有新的线程要建立，只能放在另外的队列中等待，直到当前的线程中某个线程终止直接被移出池子
					// 为了下载图片更加的流畅，我们用了2个线程来下载图片
					mImageThreadPool = Executors.newFixedThreadPool(cpuNums);
					// mImageThreadPool = Executors.newCachedThreadPool();
					// //缓存型池子 生存周期很短的异步型任务 60秒
				}
			}
		}
		return mImageThreadPool;

	}

	/**
	 * 添加Bitmap到内存缓存
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null && bitmap != null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从内存缓存中获取一个Bitmap
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 先从内存缓存中获取Bitmap,如果没有就从SD卡或者手机缓存中获取，SD卡或者手机缓存 没有就去下载
	 * 
	 * @param url
	 * @param listener
	 * @return
	 */
	public  Bitmap downloadImage(final String url,
			final onImageLoaderListener listener, final ImageView imageView) {
		// 替换Url中非字母和非数字的字符，这里比较重要，因为我们用Url作为文件名，比如我们的Url
		// 是Http://xiongfeng/abc.jpg;用这个作为图片名称，系统会认为xiongfeng为一个目录，
		// 我们没有创建此目录保存文件就会保存
		final String subUrl = url.replaceAll("[^\\w]", "");
		// 判断内存中是否有该图片(没有再从sdcard中获取)
		Bitmap bitmap = showCacheBitmap(subUrl);
		if (bitmap != null) {
			return bitmap;
		} else {
			// sdcard和内存中都无该图片
			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					listener.onImageLoader((Bitmap) msg.obj, url);
				}
			};
			// 调用线程池操作
			// mImageThreadPool.execute(new runnable);
			mImageThreadPool = getThreadPool();
			mImageThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					// 从网络获取image
					Bitmap bitmap = getBitmapFormUrl(url, imageView);
					Message msg = handler.obtainMessage();
					msg.obj = bitmap;
					handler.sendMessage(msg);
					try {
						// 保存在SD卡或者手机目录
						fileUtils.savaBitmap(subUrl, bitmap);
					} catch (IOException e) {
						e.printStackTrace();
					}
					// 将Bitmap 加入内存缓存
					addBitmapToMemoryCache(subUrl, bitmap);
				}
			});
		}
		return null;
	}

	/**
	 * 压缩图片
	 * 
	 * @param bitmap
	 *            源图片
	 * @param width
	 *            想要的宽度
	 * @param height
	 *            想要的高度
	 * @param isAdjust
	 *            是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩
	 * @return Bitmap
	 */
	public Bitmap reduce(Bitmap bitmap, int width, int height, boolean isAdjust) {
		// 如果想要的宽度和高度都比源图片小，就不压缩了，直接返回原图
		if (bitmap.getWidth() < width && bitmap.getHeight() < height) {
			return bitmap;
		}
		// 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor,
		// int scale, int roundingMode);
		// scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃
		float sx = new BigDecimal(width).divide(
				new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN)
				.floatValue();
		float sy = new BigDecimal(height).divide(
				new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN)
				.floatValue();
		if (isAdjust) {// 如果想自动调整比例，不至于图片会拉伸
			sx = (sx < sy ? sx : sy);
			sy = sx;// 哪个比例小一点，就用哪个比例
		}
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	/**
	 * 获取Bitmap, 内存中没有就去手机或者sd卡中获取，这一步在getView中会调用，比较关键的一步
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap showCacheBitmap(String url) {
		if (getBitmapFromMemCache(url) != null) {
			return getBitmapFromMemCache(url);
		} else if (fileUtils.isFileExists(url)
				&& fileUtils.getFileSize(url) != 0) {
			// 从SD卡获取手机里面获取Bitmap
			Bitmap bitmap = fileUtils.getBitmap(url);
			// 将Bitmap 加入内存缓存
			addBitmapToMemoryCache(url, bitmap);
			return bitmap;
		}
		return null;
	}

	/**
	 * 从Url中获取Bitmap
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFormUrl(String url, ImageView imageView) {
		Bitmap bitmap = null;
		HttpURLConnection con = null;
		try {
			URL mImageUrl = new URL(url);
			con = (HttpURLConnection) mImageUrl.openConnection();
			con.setDoInput(true);
			con.connect();

			InputStream stream = con.getInputStream();
			bitmap = BitmapFactory.decodeStream(stream);

			// 压缩图片
			ImageSize imageSize = getImageViewWidth(imageView);
			int reqWidth = imageSize.width;
			int reqHeight = imageSize.height;
			bitmap = reduce(bitmap, reqWidth, reqHeight, false);

			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return bitmap;
	}

	/**
	 * ImageView 的宽和高
	 * 
	 * @author wang
	 */
	private class ImageSize {
		int width;
		int height;
	}

	/**
	 * 取消正在下载的任务
	 */
	public synchronized void cancelTask() {
		if (mImageThreadPool != null) {
			mImageThreadPool.shutdownNow();
			mImageThreadPool = null;
		}
	}

	/**
	 * 异步下载图片的回调接口
	 * 
	 * @author len
	 * 
	 */
	public interface onImageLoaderListener {
		void onImageLoader(Bitmap bitmap, String url);
	}

	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewWidth(ImageView imageView) {
		ImageSize imageSize = new ImageSize();
		// 获得imageview的属性
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();

		// get the width of imageview
		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getWidth(); // Get actual image width
		if (width <= 0)
			width = params.width; // Get layout width parameter
		if (width <= 0)
			// 反射获得ImageView设置的最大宽度和高度
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
																	// maxWidth
																	// parameter
		if (width <= 0)
			width = displayMetrics.widthPixels;
		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getHeight(); // Get actual image height
		if (height <= 0)
			height = params.height; // Get layout height parameter
		if (height <= 0)
			height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
																		// maxHeight
																		// parameter
		if (height <= 0)
			height = displayMetrics.heightPixels;
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;

	}

	/**
	 * 反射获得ImageView设置的最大宽度和高度
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
		}
		return value;
	}

}

package com.shengshi.ufun.photoselect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.shengshi.ufun.app.UFunApplication;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Title:        NativeImageLoader       </p>
 * <p>Description:
 * 本地图片加载器,采用异步解析本地图片,使用单例模式,线程池加载图片
 * </p>
 * <p>@author:       liaodl                 </p>
 * <p>Copyright: Copyright (c) 2014         </p>
 * <p>Company:      @小鱼网                  </p>
 * <p>Create Time:     2014年7月23日                                 </p>
 * <p>@author:                              </p>
 * <p>Update Time:     2014年8月7日                                      </p>
 * <p>Updater:                              </p>
 * <p>Update Comments:
 * <p>修改图片加载调度算法，优先加载可视化区域的图片
 */
public class NativeImageLoader {
    /**
     * 内存Lru缓存
     */
    private LruCache<String, Bitmap> mMemoryCache;

    private static NativeImageLoader mInstance = new NativeImageLoader();

    /**
     * 创建一个固定线程数的线程池
     */
    private ThreadPoolExecutor mThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

    /**
     * 图片请求列表，用于调度
     */
    private ArrayList<ImageRequest> mImagesList = new ArrayList<ImageRequest>();

    /**
     * 处于正在请求状态的请求列表
     */
    private ArrayList<ImageRequest> mOnLoadingList = new ArrayList<ImageRequest>();

    private NativeImageLoader() {
        //获取应用程序的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        //用最大内存的1/4来存储图片
        final int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            //获取每张图片的大小
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;// 4.0以上可以用 return bitmap.getByteCount();
            }
        };
    }

    /**
     * 通过此方法来获取NativeImageLoader的实例
     *
     * @return
     */
    public static NativeImageLoader getInstance() {
        return mInstance;
    }


    /**
     * 加载本地图片，对图片不进行裁剪
     *
     * @param path
     * @param mCallBack
     * @return
     */
    public Bitmap loadNativeImage(final String path, final NativeImageCallBack mCallBack) {
        return this.loadNativeImage(path, null, mCallBack);
    }

    /**
     * 此方法来加载本地图片，这里的mPoint是用来封装ImageView的宽和高，我们会根据ImageView控件的大小来裁剪Bitmap
     * 如果你不想裁剪图片，调用loadNativeImage(final String path, final NativeImageCallBack mCallBack)来加载
     *
     * @param path
     * @param mPoint
     * @param mCallBack
     * @return
     */
    public Bitmap loadNativeImage(final String path, final Point point, final NativeImageCallBack callBack) {
//		//先获取内存中的Bitmap
//		Bitmap bitmap = getBitmapFromMemCache(path);
//		
//		final Handler mHander = new Handler(){
//
//			@Override
//			public void handleMessage(Message msg) {
//				super.handleMessage(msg);
//				mCallBack.onImageLoader((Bitmap)msg.obj, path);
//			}
//			
//		};
//		
//		//若该Bitmap不在内存缓存中，则启用线程去加载本地的图片，并将Bitmap加入到mMemoryCache中
//		if(bitmap == null){
//			mImageThreadPool.execute(new Runnable() {
//				
//				@Override
//				public void run() {
//					//先获取图片的缩略图
//					Bitmap mBitmap = decodeThumbBitmapForFile(path, mPoint == null ? 0: mPoint.x, mPoint == null ? 0: mPoint.y);
//					Message msg = mHander.obtainMessage();
//					msg.obj = mBitmap;
//					mHander.sendMessage(msg);
//					
//					//将图片加入到内存缓存
//					addBitmapToMemoryCache(path, mBitmap);
//				}
//			});
//		}
//		return bitmap;

        // 先获取内存中的Bitmap
        Bitmap bitmap = getBitmapFromMemCache(path);

        // 若该Bitmap不在内存缓存中，则将其加入到调度任务列表中
        if (bitmap == null) {
            addImageRequest(new ImageRequest(path, point, callBack));
        }
        return bitmap;

    }

    /**
     * 添加图片请求任务
     */
    private void addImageRequest(ImageRequest item) {
        if (null == item || TextUtils.isEmpty(item.getPath())) {
            return;
        }
        mImagesList.remove(item);
        mImagesList.add(item);
        dispatch();
    }

    /**
     * 删除图片请求任务
     *
     * @param path
     */
    private void removeImageRequest(ImageRequest item) {
        if (item == null || TextUtils.isEmpty(item.getPath())) {
            return;
        }
        mImagesList.remove(item);
        if (mImagesList.size() > 0) {
            dispatch();
        }
    }

    /**
     * 任务调度
     */
    private void dispatch() {
        // 开始调度
        // 如果当前线程池已满 ,不再处理请求任务
        if (mThreadPool.getActiveCount() >= mThreadPool.getCorePoolSize()) {
            return;
        }
        // 空闲线程的数量
        int spareThreads = mThreadPool.getCorePoolSize() - mThreadPool.getActiveCount();
        // 如果请求列表中数量小于空闲的线程数，顺序处理请求
        if (mImagesList.size() < spareThreads) {
            for (ImageRequest item : mImagesList) {
                execute(item);
            }
        } else { // 否则从后面(优先级高)开始处理请求
            for (int i = mImagesList.size() - 1; i >= mImagesList.size() - spareThreads; i--) {
                execute(mImagesList.get(i));
            }
        }
    }

    /**
     * 执行加载图片任务
     *
     * @param request
     */
    private void execute(final ImageRequest request) {
        // 如果该图片正在请求，忽略之。
        if (mOnLoadingList.contains(request)) {
            return;
        }

        final ImageHandler handler = new ImageHandler(this, request);
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                mOnLoadingList.add(request);
                Point size = request.getSize();
                if (size == null || size.x == 0 || size.y == 0) {
                    size = getDeviceSize(UFunApplication.getApplication());
                }
                // 先获取图片的缩略图
                Bitmap mBitmap = decodeThumbBitmapForFile(request.getPath(), size.x, size.y, true);
                if (mBitmap != null) {
                    Message msg = handler.obtainMessage();
                    msg.obj = mBitmap;
                    handler.sendMessage(msg);
                    // 将图片加入到内存缓存
                    addBitmapToMemoryCache(request.getPath(), mBitmap);
                }
                mOnLoadingList.remove(request);
            }
        });
    }

    /**
     * 往内存缓存中添加Bitmap
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            synchronized (mMemoryCache) {
                mMemoryCache.put(key, bitmap);
            }
        }
    }

    /**
     * 根据key来获取内存中的图片
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        Bitmap bitmap = null;
        // 先从硬引用缓存中获取
        synchronized (mMemoryCache) {
            bitmap = mMemoryCache.get(key);
            if (bitmap != null) {
                // 找到该Bitmap之后，将其移到LinkedHashMap的最前面，保证它在LRU算法中将被最后删除。
                mMemoryCache.remove(key);
                mMemoryCache.put(key, bitmap);
                return bitmap;
            }
        }
        return null;
    }

    /**
     * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
     *
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @param isHighQuality 是否质量高
     * @return
     */
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight,
                                            boolean isHighQuality) {
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 设置缩放比例
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);
        if (!isHighQuality) {
            // PS:为了减少内存，可以将缩放比例调的更大一些，这样就不会导致系统频繁GC的情况了
            options.inSampleSize += options.inSampleSize / 2 + 2;
        }
        // 图片质量
        options.inPreferredConfig = Config.RGB_565;

        // 设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;

        options.inPurgeable = true;
        options.inInputShareable = true;
        // 获取资源图片
        try {
            return BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    /**
     * <p/>
     * 该方法来自google的图片缓存Demo
     * <p/>
     * 请查看：http://developer.android.com/training/displaying-bitmaps/index.html
     * <p/>
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options}
     * object when decoding bitmaps using the decode* methods from
     * {@link BitmapFactory}. This implementation calculates the closest
     * inSampleSize that will result in the final decoded bitmap having a width
     * and height equal to or larger than the requested width and height. This
     * implementation does not ensure a power of 2 is returned for inSampleSize
     * which can be faster when decoding but results in a larger bitmap which
     * isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run
     *                  through a decode* method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private int computeScale(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value,
            // this will guarantee a final image
            // with both dimensions larger than or equal to the requested
            // height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static class ImageHandler extends Handler {
        private final WeakReference<NativeImageLoader> mActivity;

        private final WeakReference<ImageRequest> mRequest;

        public ImageHandler(NativeImageLoader activity, ImageRequest request) {
            mActivity = new WeakReference<NativeImageLoader>(activity);
            mRequest = new WeakReference<ImageRequest>(request);
        }

        @Override
        public void handleMessage(Message msg) {
            ImageRequest request = mRequest.get();
            if (request != null) {
                NativeImageLoader activity = mActivity.get();
                if (activity != null) {
                    activity.removeImageRequest(request);
                }
                request.getCallBack().onImageLoader((Bitmap) msg.obj, request.getPath());
            }
        }
    }

    /**
     * 获取屏幕的宽高
     *
     * @return
     */
    public Point getDeviceSize(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        Point size = new Point();
        size.x = dm.widthPixels;
        size.y = dm.heightPixels;
        return size;
    }

    /**
     * 加载本地图片的回调接口
     *
     * @author xiaanming
     */
    public interface NativeImageCallBack {
        /**
         * 当子线程加载完了本地的图片，将Bitmap和图片路径回调在此方法中
         *
         * @param bitmap
         * @param path
         */
        public void onImageLoader(Bitmap bitmap, String path);
    }
}

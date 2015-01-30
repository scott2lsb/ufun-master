package com.shengshi.base.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * <p>Title:      图片加载base
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-29
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public abstract class BaseImageLoader {

    public Context mContext;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    /**
     * 设置正在加载默认图片
     *
     * @return
     */
    public abstract int getDefaultImageOnLoading();

    /**
     * 设置加载失败默认图片
     *
     * @return
     */
    public abstract int getDefaultImageOnLoadFail();

    /**
     * 格式化图片地址
     *
     * @return
     */
    public abstract String patternUrl(String url);

    public BaseImageLoader(Context context) {
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        imageLoader = ImageLoader.getInstance();
        //显示图片的配置
        options = getDefaultDisplayOptions();
    }

    public DisplayImageOptions getDefaultDisplayOptions() {
        DisplayImageOptions options = getDefaultDisplayOptionsBuilder().build();
        return options;
    }

    public DisplayImageOptions.Builder getDefaultDisplayOptionsBuilder() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                .showImageOnLoading(getDefaultImageOnLoading())
                .showImageOnFail(getDefaultImageOnLoadFail()).cacheInMemory(true).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565);
        return builder;
    }

    public DisplayImageOptions getUserDisplayOptions() {
        DisplayImageOptions options = getUserDisplayOptionsBuilder().build();
        return options;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * 获取用户自定义的构造器
     *
     * @return
     */
    public abstract DisplayImageOptions.Builder getUserDisplayOptionsBuilder();

    /**
     * 图片加载
     *
     * @param imageUrl  图片来源，支持如下：
     *                  <p>1.图片来源于 sdcard
     *                  <p>String imagePath = "/mnt/sdcard/image.png";
     *                  <p>String imageUrl = Scheme.FILE.wrap(imagePath);
     *                  <p>2.图片来源于 Content provider
     *                  <p>String contentprividerUrl = "content://media/external/audio/albumart/13";
     *                  <p>3.图片来源于 assets
     *                  <p>String assetsUrl = Scheme.ASSETS.wrap("image.png");
     *                  <p>4.图片来源于 drawable
     *                  <p>String drawableUrl = Scheme.DRAWABLE.wrap("R.drawable.image");
     * @param imageView 图片控件
     */
    public void displayImage(String imageUrl, ImageView imageView) {
        displayImage(imageUrl, imageView, false);
    }

    /**
     * 是否带有默认动画(第一次fade in)的图片加载
     *
     * @param imageUrl             图片来源，支持如下：
     *                             <p>1.图片来源于 sdcard
     *                             <p>String imagePath = "/mnt/sdcard/image.png";
     *                             <p>String imageUrl = Scheme.FILE.wrap(imagePath);
     *                             <p>2.图片来源于 Content provider
     *                             <p>String contentprividerUrl = "content://media/external/audio/albumart/13";
     *                             <p>3.图片来源于 assets
     *                             <p>String assetsUrl = Scheme.ASSETS.wrap("image.png");
     *                             <p>4.图片来源于 drawable
     *                             <p>String drawableUrl = Scheme.DRAWABLE.wrap("R.drawable.image");
     * @param imageView            图片控件
     * @param showDefaultAnimation 是否带有默认动画(第一次fade in)
     */
    public void displayImage(String imageUrl, ImageView imageView, boolean showDefaultAnimation) {
        options = getUserDisplayOptions();
        if (!showDefaultAnimation) {
            imageLoader.displayImage(imageUrl, imageView, options);
        } else {
            ImageLoadingListener animateFirstListener = AnimateFirstDisplayListener.getListener();
            imageLoader.displayImage(imageUrl, imageView, options, animateFirstListener);
        }
    }

    public void displayImage(String imageUrl, ImageView imageView, ImageLoadingListener animationListener) {
        options = getUserDisplayOptions();
        imageLoader.displayImage(imageUrl, imageView, options, animationListener);
    }

    /**
     * 图片加载
     *
     * @param imageUrl  图片来源，支持如下：
     *                  <p>1.图片来源于 sdcard
     *                  <p>String imagePath = "/mnt/sdcard/image.png";
     *                  <p>String imageUrl = Scheme.FILE.wrap(imagePath);
     *                  <p>2.图片来源于 Content provider
     *                  <p>String contentprividerUrl = "content://media/external/audio/albumart/13";
     *                  <p>3.图片来源于 assets
     *                  <p>String assetsUrl = Scheme.ASSETS.wrap("image.png");
     *                  <p>4.图片来源于 drawable
     *                  <p>String drawableUrl = Scheme.DRAWABLE.wrap("R.drawable.image");
     * @param imageView 图片控件
     * @param options   显示选项
     */
    public void displayImage(String imageUrl, ImageView imageView, DisplayImageOptions options) {
        imageLoader.displayImage(imageUrl, imageView, options);
    }

    /**
     * 带有加载进度监听的图片加载
     *
     * @param imageUrl
     * @param imageView
     * @param progressListener
     */
    public void displayImage(String imageUrl, ImageView imageView,
                             ImageLoadingProgressListener progressListener) {
        options = getUserDisplayOptions();
        imageLoader.displayImage(imageUrl, imageView, options, new SimpleImageLoadingListener(),
                progressListener);
    }

    /**
     * 取消任务下载显示
     */
    public void cancleDisplay(ImageView imageView) {
        if (imageLoader != null) {
            imageLoader.cancelDisplayTask(imageView);
        }
    }

    /**
     * 清除内存缓存 和 sd卡缓存
     */
    public void clearCache() {
        clearMemeryCache();
        clearDiskCache();
    }

    /**
     * 清除内存缓存
     */
    public void clearMemeryCache() {
        if (imageLoader != null) {
            imageLoader.clearMemoryCache();
        }
    }

    /**
     * 清除sd卡缓存
     */
    public void clearDiskCache() {
        if (imageLoader != null) {
            imageLoader.clearDiskCache();
        }
    }

}

package com.shengshi.ufun.utils;

import android.content.Context;
import android.text.TextUtils;

import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.shengshi.base.tools.FileUtils;
import com.shengshi.base.tools.Log;
import com.shengshi.ufun.bean.BannerEntity;
import com.shengshi.ufun.config.UFunKey;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Title:文件操作
 * <p>Description:
 * <p>@author:  dengbw
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-23
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */

public class UfunFile {

    /**
     * 获取图片缓存目录
     *
     * @param context
     * @return
     */
    public static File getCacheDirectory(Context context) {
        File dir = StorageUtils.getCacheDirectory(context);
        return dir;
    }

    /**
     * 删除图片
     *
     * @param imgUrl
     * @return
     */
    public static void delete(String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)) {
            File file = DiskCacheUtils
                    .findInCache(imgUrl, com.nostra13.universalimageloader.core.ImageLoader
                            .getInstance().getDiskCache());
        /*	Log.i("---->"+com.nostra13.universalimageloader.core.ImageLoader
							.getInstance().getDiskCache()+"====="+file.getPath()+"==="+imgUrl);*/
            if (file != null && file.isFile() && file.exists()) {
                Log.i("找到缓存图片文件");
                file.delete();
            } else {
                Log.i("error-找不到缓存图片文件");
            }
        }
    }

    /**
     * 图片md5
     *
     * @param imgUrl
     * @return
     */
    public static String md5(String imgUrl) {
        File file = DiskCacheUtils.findInCache(imgUrl,
                com.nostra13.universalimageloader.core.ImageLoader.getInstance().getDiskCache());
        if (file != null && !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 保存导航图片
     *
     * @param context
     * @param user
     */
    public static void saveBanner(final Context context, final BannerEntity banner) {
        if (banner != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    File accountFile = context.getFileStreamPath(UFunKey.KEY_BANNER);
                    if (accountFile != null && accountFile.exists()) {
                        accountFile.delete();
                    }
                    FileUtils.write(context, UFunKey.KEY_BANNER, banner);
                }
            });
        }
    }

    /**
     * 获取导航图片
     *
     * @param context
     * @return
     */
    public static BannerEntity getBanner(Context context) {
        BannerEntity banner = null;
        try {
            Object obj = FileUtils.read(context, UFunKey.KEY_BANNER);
            if (obj != null) {
                banner = (BannerEntity) obj;
            }
            if (banner == null) {
                return new BannerEntity();
            }
        } catch (Exception e) {
            Log.e(e.getMessage());
            return new BannerEntity();
        }
        return banner;
    }

}

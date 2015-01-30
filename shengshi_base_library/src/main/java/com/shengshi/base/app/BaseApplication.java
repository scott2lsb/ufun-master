package com.shengshi.base.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.CrashHandler;

/**
 * <p>Title:     BaseApplication
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-8
 * <p>Update Time:  2014-11-7
 * <p>Updater:      liaodl
 * <p>Update Comments:
 * 1.解决百度地图定位sdk时会重新运行Application的onCreate方法，导致onCreate()多次运行问题
 */
public abstract class BaseApplication extends Application {

    public Context mContext;
    /**
     * 应用被强杀标志
     */
    public static int forceKillFlag = -1;//应用被强杀标志

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        if (!getCurProcessName(mContext).equals(AppHelper.getPackageName(mContext))) {
            return;
        }
        CrashHandler.getInstance().register(mContext);// 注册crashHandler
        SDKInitializer.initialize(mContext);// 在使用 百度地图SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        initImageLoader(mContext);
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}

package com.shengshi.ufun.app;

import android.content.Context;
import android.os.Environment;
import android.widget.TextView;

import com.androidquery.util.AQUtility;
import com.baidu.location.BDLocation;
import com.shengshi.base.app.BaseApplication;
import com.shengshi.base.map.LocationUtil;
import com.shengshi.base.map.LocationUtil.OnRequestLocationListener;
import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.Log;
import com.shengshi.rebate.app.RebateApplication;
import com.shengshi.ufun.utils.UFunTool;
import com.shengshi.ufun.utils.location.LocationResultMgr;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

/**
 * <p>Title:    LifeCircleApplication
 * <p>Description:
 * <p>@author:  dengbw
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-26
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class UFunApplication extends BaseApplication {

    private static UFunApplication application;
    private File imgCacheDir;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        setCacheDir();
        initRebateApplication(getApplicationContext());
        RebateApplication.initRebateApplication(mContext);
    }

    public static UFunApplication getApplication() {
        return application;
    }

    public static void initRebateApplication(Context context) {
        initRebateApplication(context, 0, null);
    }

    public static void initRebateApplication(Context context, int tp, TextView view) {
        if (!getCurProcessName(context).equals(AppHelper.getPackageName(context))) {
            Log.i("不是当前进程，不初始化LifeCircleApplication -- 避免重复初始化，多线程 和 百度地图bug 会引起此问题");
            return;
        }
        //全局调试模式开关
        boolean globalDebugMode = UFunTool.isDebug(context);
        Log.setDebugMode(globalDebugMode);
        //打开umeng调试模式后，数据实时发送
        MobclickAgent.setDebugMode(globalDebugMode);
        startLoction(context, tp, view);
        Log.i("LifeCircleApplication init successful");
    }

    public static void startLoction(final Context context, final int tp, final TextView view) {
        LocationUtil locationUtil = LocationUtil.getInstance(context);
        locationUtil.setRequestLocationListener(new OnRequestLocationListener() {
            @Override
            public void onStart() {
                Log.i("同城圈开始定位，定位结果将异步回调通知,且缓存到LocationResultMgr类里.");
            }

            @Override
            public void onSuccess(BDLocation location) {
                LocationResultMgr.getInstance(context).cache(location);
                if (tp == 1) {
                    view.setText(LocationResultMgr.getInstance(context).getCityName());
                }
                Log.i("同城圈定位成功，纬度:" + location.getLatitude() + ",经度:" + location.getLongitude());
                Log.i("同城圈定位成功，详细地址:" + LocationResultMgr.getInstance(context).getAddress());
            }
        });
        locationUtil.startLoction();
    }

    public void setCacheDir() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File ext = Environment.getExternalStorageDirectory();
            File cacheDir = new File(ext, "fishCache" + File.separator + "image");
            imgCacheDir = cacheDir;
            AQUtility.setCacheDir(cacheDir);
        } else {
            imgCacheDir = null;
        }
    }

    public File getImgCacheDir() {
        return imgCacheDir;
    }

}

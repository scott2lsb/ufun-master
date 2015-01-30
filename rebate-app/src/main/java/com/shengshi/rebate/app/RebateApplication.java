package com.shengshi.rebate.app;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.shengshi.base.app.BaseApplication;
import com.shengshi.base.map.LocationUtil;
import com.shengshi.base.map.LocationUtil.OnRequestLocationListener;
import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.Log;
import com.shengshi.rebate.location.LocationResultMgr;
import com.shengshi.rebate.utils.RebateTool;
import com.umeng.analytics.MobclickAgent;

/**
 * <p>Title:    RebateApplication
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-9
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateApplication extends BaseApplication {

    private static RebateApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initRebateApplication(getApplicationContext());
    }

    public static RebateApplication getApplication() {
        return application;
    }

    public static void initRebateApplication(Context context) {
        if (!getCurProcessName(context).equals(AppHelper.getPackageName(context))) {
            Log.i("不是当前进程，不初始化RebateApplication -- 避免重复初始化，多线程 和 百度地图bug 会引起此问题");
            return;
        }
        //全局调试模式开关
        boolean globalDebugMode = RebateTool.isDebug(context);
        Log.setDebugMode(globalDebugMode);
        //打开umeng调试模式后，数据实时发送
        MobclickAgent.setDebugMode(globalDebugMode);
        startLoction(context);
        Log.i("RebateApplication init successful");
    }

    public static void startLoction(final Context context) {
        LocationUtil locationUtil = LocationUtil.getInstance(context);
        locationUtil.setRequestLocationListener(new OnRequestLocationListener() {
            @Override
            public void onStart() {
                Log.i("同城卡开始定位，定位结果将异步回调通知,且缓存到LocationResultMgr类里.");
            }

            @Override
            public void onSuccess(BDLocation location) {
                LocationResultMgr.getInstance(context).cache(location);
                Log.i("同城卡定位成功，纬度:" + location.getLatitude() + ",经度:" + location.getLongitude());
                Log.i("同城卡定位成功，详细地址:" + LocationResultMgr.getInstance(context).getAddress());
            }
        });
        locationUtil.startLoction();
    }

}

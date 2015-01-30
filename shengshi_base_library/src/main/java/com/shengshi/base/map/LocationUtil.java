package com.shengshi.base.map;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.SparseArray;

import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.NetUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * <p>Title:      百度定位
 * <p>Description:   功能包括
 * <p>1.使用高精度定位模式，会同时使用网络定位(包括Wi-Fi和基站定位)和GPS定位，优先返回最高精度的定位结果；
 * <p>2.没有网络情况下，使用离线定位模式
 * <p>3.使用者实现OnRequestLocationListener监听，回调定位结果
 * <p>4.计算传入的点位置 与当前(定位)的位置的距离
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-7
 * <p>@author:      liaodl
 * <p>Update Time:  2014-11-21
 * <p>Updater:      liaodl
 * <p>Update Comments: 支持设置多个监听，规避多个应用监听谁覆盖谁 问题
 */
public class LocationUtil {
    private static LocationUtil instance;
    private static Object lock = new Object();
    private Context mContext;
    public LocationClient mLocationClient;
    public BDLocationListener locationCallback = new BaiduLocationListener();
    public BDLocation mBDLocation;
    private SparseArray<OnRequestLocationListener> listenerMap;
    private int key = 0;

    private LocationUtil(Context context) {
        this.mContext = context;
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(context.getApplicationContext());
        }
        mLocationClient.registerLocationListener(locationCallback); // 注册监听函数
        listenerMap = new SparseArray<OnRequestLocationListener>();
    }

    public static LocationUtil getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = new LocationUtil(context);
            }
            return instance;
        }
    }

    /**
     * 发起定位服务
     */
    public void startLoction() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(mContext.getApplicationContext());
        }
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        if (mLocationClient != null) {
            mLocationClient.setLocOption(getLocationOption());
        }
        for (int index = 0; index < key; index++) {
            OnRequestLocationListener listener = listenerMap.get(index);
            if (listener != null) {
                listener.onStart();
            }
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            if (NetUtil.isNetConnectionAvailable(mContext)) {
                mLocationClient.requestLocation();
            } else {
                mLocationClient.requestOfflineLocation();
            }
        }
    }

    /**
     * 定位回调监听
     */
    public class BaiduLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                Log.i("定位失败");
                return;
            }
            int locType = location.getLocType();
            Log.i("定位locType:" + locType);
            // 不是GPS定位,不是网络定位，不是缓存（已由离线定位代替），不是离线定位，则表示定位失败
            if (locType != BDLocation.TypeGpsLocation && locType != BDLocation.TypeNetWorkLocation
                    && locType != BDLocation.TypeCacheLocation
                    && locType != BDLocation.TypeOffLineLocation) {
                stopLocation();
                Log.i("定位失败");
                return;
            }
            mBDLocation = location;
            Log.i("定位成功");
            for (int index = 0; index < key; index++) {
                OnRequestLocationListener listener = listenerMap.get(index);
                if (listener != null) {
                    listener.onSuccess(location);
                }
            }
            stopLocation();
        }

    }

    /**
     * 设置定位参数
     */
    private LocationClientOption getLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setTimeOut(15000);
        option.setOpenGps(true);
        option.setLocationMode(LocationMode.Hight_Accuracy);
        option.setCoorType(BDGeofence.COORD_TYPE_BD09LL);//返回的定位结果是百度经纬度-bd09ll，默认值gcj02
        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
        option.setProdName(AppHelper.getPackageName(mContext));//设置产品线名称
        return option;
    }

    /**
     * 回调定位结果
     */
    public interface OnRequestLocationListener {
        void onStart();

        void onSuccess(BDLocation location);
    }

    public void setRequestLocationListener(OnRequestLocationListener listener) {
        listenerMap.put(key++, listener);
    }

    /**
     * 结束定位服务
     */
    public void stopLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
    }

    /**
     * 打开定位设置界面
     *
     * @param context
     */
    public static void startLocaitionSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 计算传入的点位置 与当前(定位)的位置的距离
     *
     * @param latitudeE6
     * @param longitudeE6
     * @return
     */
    public String getDistance(int latitudeE6, int longitudeE6) {
        String result = "";
        if (mBDLocation == null || latitudeE6 < 0 || longitudeE6 < 0) {
            return "距离未知";
        }
        LatLng shopPoint = new LatLng(latitudeE6, longitudeE6);
        LatLng localPoint = new LatLng((int) (mBDLocation.getLatitude() * 1E6),
                (int) (mBDLocation.getLongitude() * 1E6));
        double distance = DistanceUtil.getDistance(shopPoint, localPoint);
        DecimalFormat format = new DecimalFormat("#0.00");
        format.setRoundingMode(RoundingMode.HALF_UP);
        if (distance < 1000) {
            result = format.format(distance) + "m";
        }
        if (distance >= 1000) {
            result = format.format(distance / 1000) + "km";
        }
        return result;
    }

}

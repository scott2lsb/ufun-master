package com.shengshi.rebate.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.shengshi.base.data.DataManager;
import com.shengshi.base.tools.FileUtils;
import com.shengshi.base.tools.Log;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Title:      定位数据内存缓存
 * <p>Description:
 * 注意：	常用的都缓存在内存里，不要每次都有io操作
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-20
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class LocationResultMgr extends DataManager implements Serializable {

    private static final long serialVersionUID = 4503877705588665231L;

    public static LocationResultMgr instance;
    public static Object lock = new Object();
    public Context mContext;

    private BDLocation bdLocation;
    public LocationResult locationInfo;

    private LocationResultMgr(Context context) {
        mContext = context;
        locationInfo = new LocationResult();
    }

    public static LocationResultMgr getInstance(Context context) {
        synchronized (lock) {
            if (instance == null) {
                instance = new LocationResultMgr(context);
            }
        }
        return instance;
    }

    /**
     * 百度最原始实体，拿到此类，用户自己获取想要的信息
     *
     * @return
     */
    public BDLocation getBDLocation() {
        return bdLocation;
    }

    /**
     * 获取百度定位结果,封装到LocationResult
     *
     * @return
     */
    public LocationResult getLocationResult() {
        if (locationInfo == null) {
            locationInfo = getLocationResultFromFile(mContext);
        }
        if (locationInfo == null) {
            locationInfo = new LocationResult();
        }
        return locationInfo;
    }

    public void cache(BDLocation location) {
        if (location == null) {
            return;
        }
        this.bdLocation = location;
        if (locationInfo == null) {
            locationInfo = new LocationResult();
        }
        locationInfo.latitude = bdLocation.getLatitude();
        locationInfo.longitude = bdLocation.getLongitude();
        locationInfo.locationCityCode = bdLocation.getCityCode();
        locationInfo.locationCityName = bdLocation.getCity();
        locationInfo.address = bdLocation.getProvince() + bdLocation.getCity()
                + bdLocation.getStreet() + bdLocation.getStreetNumber();
        saveLocationResult(mContext, locationInfo);
    }

    public double getLatitude() {
        return locationInfo.latitude;
    }

    public double getLongitude() {
        return locationInfo.longitude;
    }

    public String getCityCode() {
        return locationInfo.locationCityCode;
    }

    public String getCityName() {
        return locationInfo.locationCityName;
    }

    public String getAddress() {
        return locationInfo.address;
    }

    /**
     * 保存百度定位结果
     *
     * @param location
     */
    private void saveLocationResult(final Context context, final LocationResult result) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable() {

            @Override
            public void run() {
                saveLocationResultSync(context, result);
            }
        });
    }

    private static final String LOCATION_PREFERENCE = "location_info";

    private void saveLocationResultSync(Context context, LocationResult result) {
        if (context != null && result != null) {
            FileUtils.write(context, LOCATION_PREFERENCE, result);
        }
    }

    private LocationResult getLocationResultFromFile(Context context) {
        LocationResult location = null;
        try {
            if (location == null) {
                Object obj = FileUtils.read(context, LOCATION_PREFERENCE);
                if (obj != null) {
                    location = (LocationResult) obj;
                }
            }
            if (location == null) {
                return new LocationResult();
            }
        } catch (Exception e) {
            Log.e(e.getMessage());
            return new LocationResult();
        }
        return location;
    }

}

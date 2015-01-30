package com.shengshi.rebate.ui.home;

import android.text.TextUtils;

import com.shengshi.base.data.DataManager;
import com.shengshi.rebate.bean.UserInfoEntity;

/**
 * <p>Title:      用户数据内存缓存
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
public class UserMgr extends DataManager {

    public static UserMgr instance;
    public static Object lock = new Object();

    private String cityCode;
    private String cityName;
    private UserInfoEntity userEntity;

    private UserMgr() {
    }

    public static UserMgr getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new UserMgr();
            }
        }
        return instance;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void refreshCityCode(String cityCode) {
        if (TextUtils.isEmpty(cityCode)) {
            cityCode = "350200";
        }
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void refreshCityName(String cityName) {
        if (TextUtils.isEmpty(cityName)) {
            cityCode = "厦门";
        }
        this.cityName = cityName;
    }

    public UserInfoEntity getUserEntity() {
        return userEntity;
    }

    public void refreshUserEntity(UserInfoEntity userEntity) {
        if (userEntity == null) {
            removeUserEntity();
        } else {
            this.userEntity = userEntity;
        }
    }

    public void removeUserEntity() {
        this.userEntity = new UserInfoEntity();
    }

}

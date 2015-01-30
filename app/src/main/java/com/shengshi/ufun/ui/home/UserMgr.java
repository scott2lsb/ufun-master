package com.shengshi.ufun.ui.home;

import android.text.TextUtils;

import com.shengshi.base.data.DataManager;
import com.shengshi.ufun.bean.UserInfoEntity;

/**
 * <p>Title:      用户数据内存缓存
 * <p>Description:
 * 注意：	常用的都缓存在内存里，不要每次都有io操作
 * <p>@author:  dengbw
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-26
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class UserMgr extends DataManager {

    public static UserMgr instance;
    public static Object lock = new Object();

    private String cityId;
    private String cityName;
    private String mobile;
    private UserInfoEntity userInfoEntity;

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

    public String getCityId() {
        return cityId;
    }

    public void refreshCityId(String cityId) {
        if (TextUtils.isEmpty(cityId)) {
            cityId = "350200";
        }
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void refreshCityName(String cityName) {
        if (TextUtils.isEmpty(cityName)) {
            cityName = "厦门";
        }
        this.cityName = cityName;
    }

    public String getMobile() {
        return mobile;
    }

    public void refreshMobile(String mobile) {
        this.mobile = mobile;
    }

    public UserInfoEntity getUserInfoEntity() {
        return userInfoEntity;
    }

    public void refreshUserInfoEntity(UserInfoEntity userInfoEntity) {
        if (userInfoEntity == null) {
            removeUserInfoEntity();
        } else {
            this.userInfoEntity = userInfoEntity;
        }
    }

    public void removeUserInfoEntity() {
        this.userInfoEntity = new UserInfoEntity();
    }

}

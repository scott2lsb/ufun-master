package com.shengshi.ufun.utils;

import android.content.Context;
import android.text.TextUtils;

import com.shengshi.base.tools.SharedPreferencesUtil;
import com.shengshi.ufun.R;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.ui.home.UserMgr;

/**
 * <p>Title:       一些常用工具类
 * <p>Description: 跟业务比较相关且常用，包括：
 * <p>1.设置全局debug模式
 * <p>@author:  dengbw
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-26
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class UFunTool {

    private static final String REBATE_PREFERENT_NAME = "ufun_preferent";

    private UFunTool() {
    }

    public static boolean isDebug(Context context) {
        String compare = context.getString(R.string.is_debug);
        if ("true".equalsIgnoreCase(compare)) {
            return true;
        }
        return false;
    }

    /**
     * @param context
     * @param cityId
     */
    public static void saveCityId(Context context, String cityId) {
        UserMgr.getInstance().refreshCityId(cityId);
        SharedPreferencesUtil.setValue(context, UFunKey.KEY_CITY_CODE, cityId,
                REBATE_PREFERENT_NAME);
    }

    public static String getCityId(Context context) {
        String cityId = "";
        cityId = UserMgr.getInstance().getCityId();
        if (TextUtils.isEmpty(cityId)) {
            cityId = SharedPreferencesUtil.getValue(context, UFunKey.KEY_CITY_CODE,
                    REBATE_PREFERENT_NAME);
        }
        return cityId;
    }

    public static void saveCityName(Context context, String cityName) {
        UserMgr.getInstance().refreshCityName(cityName);
        SharedPreferencesUtil.setValue(context, UFunKey.KEY_CITY_NAME, cityName,
                REBATE_PREFERENT_NAME);
    }

    public static String getCityName(Context context) {
        String cityName = "";
        cityName = UserMgr.getInstance().getCityName();
        if (TextUtils.isEmpty(cityName)) {
            cityName = SharedPreferencesUtil.getValue(context, UFunKey.KEY_CITY_NAME,
                    REBATE_PREFERENT_NAME);
        }
        return cityName;
    }

    /**
     * 保存用户手机号
     *
     * @param context
     * @param mobile
     */
    public static void saveMobile(Context context, String mobile) {
        SharedPreferencesUtil.setValue(context, UFunKey.KEY_MOBILE, mobile + "",
                REBATE_PREFERENT_NAME);
    }

    /**
     * 获取用户手机号
     *
     * @param context
     * @return
     */
    public static String getMobile(Context context) {
        String mobile = UserMgr.getInstance().getMobile();
        if (TextUtils.isEmpty(mobile)) {
            mobile = SharedPreferencesUtil.getValue(context, UFunKey.KEY_MOBILE,
                    REBATE_PREFERENT_NAME);
        }
        return mobile;
    }

    /**
     * 保存同城圈App是不是宿主app标志
     *
     * @param context
     * @param userId
     */
    public static void saveSubFlag(Context context, boolean isSub) {
        SharedPreferencesUtil.setBoolean(context, UFunKey.KEY_IS_SUB_FLAG, isSub,
                REBATE_PREFERENT_NAME);
    }

    /**
     * 获取同城圈App是不是宿主app标志
     *
     * @param context
     * @return
     */
    public static boolean getSubFlag(Context context) {
        return SharedPreferencesUtil.getBoolean(context, UFunKey.KEY_IS_SUB_FLAG,
                REBATE_PREFERENT_NAME, false);
    }

}

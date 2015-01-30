package com.shengshi.rebate.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.AppManager;
import com.shengshi.base.tools.SharedPreferencesUtil;
import com.shengshi.base.tools.ToastUtils;
import com.shengshi.rebate.R;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.ui.home.UserMgr;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * <p>Title:       返利卡一些常用工具类
 * <p>Description: 跟返利卡业务比较相关且常用，包括：
 * <p>1.设置全局debug模式
 * <p>2.保存和获取格式如"3502"的cityId，小鱼映射
 * <p>3.保存和获取用户id
 * <p>4.保存和获取返利卡App是不是宿主app标志
 * <p>5.隐藏键盘
 * <p>6.跳到拨号面板，拨打电话
 * <p>7.是否安装支付宝客户端
 * <p>8.格式化金钱格式，只保留两位小数
 * <p>9.退出应用，清除一些参数
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-18
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateTool {

    private static final String REBATE_PREFERENT_NAME = "rebate_preferent";

    private RebateTool() {
    }

    public static boolean isDebug(Context context) {
        String compare = context.getString(R.string.is_debug);
        if ("true".equalsIgnoreCase(compare)) {
            return true;
        }
        return false;
    }

    public static void saveCityCode(Context context, String cityCode) {
        UserMgr.getInstance().refreshCityCode(cityCode);
        SharedPreferencesUtil.setValue(context, RebateKey.KEY_CITY_CODE, cityCode,
                REBATE_PREFERENT_NAME);
    }

    public static String getCityCode(Context context) {
        String cityCode = "350200";
        cityCode = UserMgr.getInstance().getCityCode();
        if (TextUtils.isEmpty(cityCode)) {
            cityCode = SharedPreferencesUtil.getValue(context, RebateKey.KEY_CITY_CODE,
                    REBATE_PREFERENT_NAME);
        }
        return cityCode;
    }

    public static void saveCityName(Context context, String cityName) {
        SharedPreferencesUtil.setValue(context, RebateKey.KEY_CITY_NAME, cityName,
                REBATE_PREFERENT_NAME);
    }

    public static String getCityName(Context context) {
        String cityName = "厦门";
        cityName = UserMgr.getInstance().getCityName();
        if (TextUtils.isEmpty(cityName)) {
            cityName = SharedPreferencesUtil.getValue(context, RebateKey.KEY_CITY_NAME,
                    REBATE_PREFERENT_NAME);
        }
        return cityName;
    }

    /**
     * 保存有范用户id
     *
     * @param context
     * @param userId
     */
    public static void saveUFunUserId(Context context, int userId) {
        SharedPreferencesUtil.setValue(context, RebateKey.KEY_UFUN_USER_ID, userId + "",
                REBATE_PREFERENT_NAME);
    }

    /**
     * 获取有范用户id
     *
     * @param context
     * @return
     */
    public static String getUFunUserId(Context context) {
        return SharedPreferencesUtil.getValue(context, RebateKey.KEY_UFUN_USER_ID,
                REBATE_PREFERENT_NAME);
    }

    /**
     * 保存返利卡App是不是宿主app标志
     *
     * @param context
     * @param userId
     */
    public static void saveSubFlag(Context context, boolean isSub) {
        SharedPreferencesUtil.setBoolean(context, RebateKey.KEY_IS_SUB_FLAG, isSub,
                REBATE_PREFERENT_NAME);
    }

    /**
     * 获取返利卡App是不是宿主app标志
     *
     * @param context
     * @return
     */
    public static boolean getSubFlag(Context context) {
        return SharedPreferencesUtil.getBoolean(context, RebateKey.KEY_IS_SUB_FLAG,
                REBATE_PREFERENT_NAME, false);
    }

    // 隐藏键盘
    public static void hideKeybord(Context context, View searchEdit) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
    }

    // 显示键盘
    public static void showKeybord(View searchEdit) {
        InputMethodManager imm = (InputMethodManager) searchEdit.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    // 打电话 -- 跳到拨号面板
    public static void doTel(Activity activity, String phone) {
        if (TextUtils.isEmpty(phone.trim())) {
            ToastUtils.showToast(activity, "电话号码无效", Toast.LENGTH_SHORT);
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);//系统默认的action，用来打开默认的电话界面
        intent.setData(Uri.parse("tel:" + phone.trim()));
        activity.startActivity(intent);
    }

    /**
     * 是否安装支付宝客户端
     *
     * @return
     */
    public static boolean isInstalledAlipay(Context context) {
        return AppHelper.isAppInstalled(context, "com.alipay.android.app")
                || AppHelper.isAppInstalled(context, "com.eg.android.AlipayGphone");
    }

    /**
     * 格式化金钱
     *
     * @param value
     * @return
     */
    public static String formatDouble(double value) {
        DecimalFormat format = new DecimalFormat("#0.00");
        format.setRoundingMode(RoundingMode.HALF_UP);
        String result = format.format(value);
        return result;
    }

    /**
     * 格式化金钱
     *
     * @param value
     * @return
     */
    public static String formatDouble(String value) {
        return formatDouble(Double.parseDouble(value));
    }

    /**
     * 退出应用 要处理的一系列动作
     *
     * @param context
     */
    public static void exitApp(Context context) {
        boolean isSub = getSubFlag(context);
        if (!isSub) {
            AppManager.getAppManager().finishAllActivity();
        }
        RebateTool.saveSubFlag(context, false);
        AccountUtil.removeAccountInfo(context);
        BaseEncryptInfo.getInstance(context).clear();
    }

}

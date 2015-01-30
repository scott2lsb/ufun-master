package com.shengshi.base.tools;

import android.content.Context;

/**
 * <p>Title:     DensityUtil
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-8
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class DensityUtil {

    /**
     * 获取屏幕密度比例
     *
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取文字密度比例
     *
     * @param context
     * @return
     */
    public static float getScaledDensity(Context context) {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    /**
     * 转换dip为px
     *
     * @param context
     * @param dip     值
     * @return
     */
    public static int dip2px(Context context, double dip) {
        float scale = getScreenDensity(context);
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 转换px为dip
     *
     * @param context
     * @param px      像素值
     * @return
     */
    public static int px2dip(Context context, int px) {
        float scale = getScreenDensity(context);
        return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
    }

    /**
     * 将PX转SP
     *
     * @param context
     * @param pxValue 像素值
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = getScaledDensity(context);
        return (int) (pxValue / fontScale + 0.5f);
    }
}

package com.shengshi.base.tools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * <p>Title:      读取AndroidManifest.xml工具类
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
public class ManifestUtil {

    /**
     * 根据字段获取metadata值
     *
     * @param context
     * @param Key
     * @return
     */
    public static String readMetaData(Context context, String Key) {
        ApplicationInfo info;
        String value = "";
        try {
            info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            value = info.metaData.getString(Key);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获得友盟渠道
     *
     * @param context
     * @return
     */
    public static String readUMChannel(Context context) {
        ApplicationInfo info;
        String value = "";
        try {
            info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            value = info.metaData.getString("UMENG_CHANNEL");
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }
}

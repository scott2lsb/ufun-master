package com.shengshi.base.tools;

import android.os.Environment;

import java.io.File;

/**
 * <p>Title:      SD卡工具类
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-10
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class SdCardTool {

    private SdCardTool() {
    }

    /**
     * sdcard is exists
     * call this method before working on the sdcard
     */
    public static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * sdcard root directory
     */
    public static String getSdcardDir() {
        return Environment.getExternalStorageDirectory().toString();
    }

    /**
     * delete all files on the path
     *
     * @param path
     */
    public static void deleteAll(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    f.delete();
                }
            } else {
                file.delete();
            }
        }
    }

}

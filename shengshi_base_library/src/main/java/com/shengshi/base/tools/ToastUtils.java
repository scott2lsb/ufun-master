package com.shengshi.base.tools;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * <p>Title:       Toast工具类
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-24
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class ToastUtils {

    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    private ToastUtils() {
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * toast显示
     *
     * @param activity
     * @param content
     */
    public static void showToast(final Activity activity, final String content, final int duration) {
//		if(isFastDoubleClick()){
//			return;
//		}
        if (TextUtils.isEmpty(content)) {
            return;
        }
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mHandler.removeCallbacks(r);
                if (mToast != null)
                    mToast.setText(content);
                else
                    mToast = Toast.makeText(activity, content, duration);

                mHandler.postDelayed(r, 800);
                mToast.show();
            }
        });
    }

    /**
     * toast显示
     *
     * @param activity
     * @param resId    字符串id
     */
    public static void showToast(final Activity activity, final int resId, final int duration) {
        if (resId <= 0) {
            return;
        }
        showToast(activity, activity.getResources().getString(resId), duration);
    }

}

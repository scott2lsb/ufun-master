package com.shengshi.rebate.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.Toast;

import com.shengshi.base.tools.ToastUtils;
import com.shengshi.rebate.R;

public class AppMgrUtils {

    public static void launchAPP(Activity activity, String packageName, String cls) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, cls));
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            ToastUtils.showToast(activity, activity.getString(R.string.rebate_launchapp_error), Toast.LENGTH_SHORT);
        }
    }

    public static void launchAPP(Activity activity, String packageName, String cls, int requestCode) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, cls));
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            ToastUtils.showToast(activity, activity.getString(R.string.rebate_launchapp_error), Toast.LENGTH_SHORT);
        }
    }

}

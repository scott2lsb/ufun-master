package com.shengshi.ufun.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.shengshi.base.tools.AppManager;
import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.CustomProgressDialog;
import com.shengshi.ufun.R;
import com.shengshi.ufun.ui.LoginActivity;
import com.shengshi.ufun.ui.MainActivity;
import com.shengshi.ufun.utils.AccountUtil;
import com.shengshi.ufun.utils.UFunTool;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 *
 * @author dengbw
 * @version 1.0
 * @created 2012-5-5
 */
public class UIHelper {

    /**
     * 退出程序
     *
     * @param cont
     */
    public static void Exit(Context cont) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
//		builder.setIcon(R.drawable.oldfish);
        builder.setTitle(R.string.app_menu_surelogout);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();//退出
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 显示/隐藏键盘
     */
    public static void hideSoftInputMode(final EditText showSoftEditText, final Context ctx,
                                         boolean isHide) {
        final InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (showSoftEditText != null) {
            if (isHide) {
                imm.hideSoftInputFromWindow(showSoftEditText.getWindowToken(), 0);
            } else {
                showSoftEditText.setFocusable(true);
                showSoftEditText.setFocusableInTouchMode(true);
                showSoftEditText.requestFocus();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        imm.showSoftInput(showSoftEditText, InputMethodManager.SHOW_FORCED);
                    }
                }, 500);
            }
        }
    }

    public static void hideSoftInputMode(IBinder windowToken, Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // 从资源中获取Bitmap
    public static Bitmap getBitmapId(Context ctx, int resId) {
        try {
            Resources res = ctx.getResources();
            return BitmapFactory.decodeResource(res, resId);
        } catch (OutOfMemoryError e) {
            Log.e(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void ToastMessage(Context cont, String msg) {
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, int msg) {
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, String msg, int time) {
        Toast.makeText(cont, msg, time).show();
    }

    /**
     * 点击返回监听事件
     *
     * @param activity
     * @return
     */
    public static View.OnClickListener finish(final Activity activity) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                activity.finish();
            }
        };
    }

//	/**
//	 * 显示登录页面
//	 * 
//	 * @param activity
//	 */
//	public static void showLoginDialog(Context cont, int jump) {
//		Intent intent = new Intent(cont, LoginDialog.class);
//		intent.putExtra("LOGINTYPE", jump);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		cont.startActivity(intent);
//	}

    /**
     * 设置隐藏模态进度条
     */
    public static CustomProgressDialog customProgressDialog(Activity activity, String message) {
        CustomProgressDialog progressDialog = CustomProgressDialog.getDialog(activity);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    /**
     * 检查是否登录
     *
     * @param Context context
     * @return true登录
     */
    public static Boolean checkLogin(Context context) {
        if (!TextUtils.isEmpty(UFunTool.getMobile(context))) {
            return true;
        }
        return false;
    }

    /**
     * 跳转登录页面
     *
     * @param act
     * @param tp
     */
    public static void login(Activity act, int tp) {
        Intent intent = new Intent(act, LoginActivity.class);
        act.startActivityForResult(intent, tp);
    }

    /**
     * 检查错误信息  1001重新登录
     *
     * @param errCode    错误码
     * @param errMessage 错误提示
     * @param act
     */
    public static Boolean checkErrCode(int errCode, String errMessage, Activity act) {
        if (errCode != 0) {
            UIHelper.ToastMessage(act, errMessage);
            if (errCode == 1001) {
                AccountUtil.removeAccountInfo(act);
                login(act, 1001);
            }
            return true;
        }
        return false;
    }

    /**
     * 设置首页底部点击刷新
     */
    public static void setMainRefresh() {
        MainActivity.refresh0 = true;
        MainActivity.refresh1 = true;
        MainActivity.refresh2 = true;
        MainActivity.refresh3 = true;
    }

}

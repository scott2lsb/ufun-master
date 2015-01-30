package com.shengshi.base.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengshi.base.res.R;

import java.util.HashMap;

/**
 * <p>Title:     自定义加载框
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
public class CustomProgressDialog extends Dialog {

    private static CustomProgressDialog loadingDialog = null;

    private static HashMap<Activity, CustomProgressDialog> dialogMap = new HashMap<Activity, CustomProgressDialog>();

    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static CustomProgressDialog getDialog(Activity actvitiy) {
        loadingDialog = dialogMap.get(actvitiy);
        if (loadingDialog == null) {
            loadingDialog = new CustomProgressDialog(actvitiy, R.style.loading_dialog);
            loadingDialog.setContentView(R.layout.loading_custom_layout);
            loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            dialogMap.put(actvitiy, loadingDialog);
        }
        return loadingDialog;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (loadingDialog == null) {
            return;
        }
        ImageView imageView = (ImageView) loadingDialog.findViewById(R.id.loading_img);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    /**
     * 设置提示信息
     *
     * @param strMessage
     * @return
     */
    public CustomProgressDialog setMessage(String strMessage) {
        if (loadingDialog != null) {
            TextView tvMsg = (TextView) loadingDialog.findViewById(R.id.loading_text);
            if (tvMsg != null) {
                tvMsg.setText(strMessage);
            }
        }
        return loadingDialog;
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            loadingDialog = null;
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            loadingDialog = null;
        } catch (Exception e) {
            loadingDialog = null;
        }
    }

    @Override
    public void cancel() {
        try {
            super.cancel();
            loadingDialog = null;
        } catch (Exception e) {
            loadingDialog = null;
        }
    }
}

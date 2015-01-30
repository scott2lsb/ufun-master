package com.shengshi.rebate.utils;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;

import com.shengshi.base.tools.AppHelper;
import com.shengshi.rebate.R;

public class PopupWindowUtil {

    private PopupWindowUtil() {
    }

    /**
     * 创建PopWindow
     *
     * @param context
     * @param contentView
     * @param anchor
     * @return
     */
    public static PopupWindow createPopupWindow(Activity context, View contentView, View anchor) {
        int screenWidth = AppHelper.getScreenWidth(context);
        int screenHeight = AppHelper.getScreenHeight(context);
//		final PopupWindow mPopupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        final PopupWindow mPopupWindow = new PopupWindow(contentView, screenWidth, screenHeight);
//		mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x80000000));
        mPopupWindow.setAnimationStyle(R.style.popwinAnimation);
        mPopupWindow.setFocusable(true);// 获取焦点
        mPopupWindow.setTouchable(true); // 设置popupwindow可点击
        mPopupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
        mPopupWindow.setTouchInterceptor(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mPopupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        contentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mPopupWindow.dismiss();
                return true;
            }
        });
        mPopupWindow.showAsDropDown(anchor, 0, 0);
        return mPopupWindow;
    }

}

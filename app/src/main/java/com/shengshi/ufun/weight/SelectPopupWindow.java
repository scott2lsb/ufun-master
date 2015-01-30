package com.shengshi.ufun.weight;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shengshi.ufun.R;

public class SelectPopupWindow extends PopupWindow {
    private Button btn_cancel;
    private View mMenuView;

    public SelectPopupWindow(Activity context, OnClickListener itemsOnClick,
                             int tp, String text) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater
                .inflate(R.layout.selectpopupwindow, null);
        btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
        if (tp == 1) {
            mMenuView.findViewById(R.id.show1).setVisibility(
                    View.VISIBLE);
            mMenuView.findViewById(R.id.btn_take_photo)
                    .setOnClickListener(itemsOnClick);
            mMenuView.findViewById(R.id.btn_pick_photo)
                    .setOnClickListener(itemsOnClick);
        } else if (tp == 2) {
            mMenuView.findViewById(R.id.show2).setVisibility(
                    View.VISIBLE);
            Button btn_reply_favorite = (Button) mMenuView
                    .findViewById(R.id.btn_reply_favorite);
            btn_reply_favorite.setText(text);
            mMenuView.findViewById(R.id.btn_reply_all)
                    .setOnClickListener(itemsOnClick);
            mMenuView.findViewById(R.id.btn_reply_new)
                    .setOnClickListener(itemsOnClick);
            mMenuView.findViewById(R.id.btn_reply_landlord)
                    .setOnClickListener(itemsOnClick);
            mMenuView.findViewById(R.id.btn_reply_favorite)
                    .setOnClickListener(itemsOnClick);
        } else if (tp == 3) {
            mMenuView.findViewById(R.id.show3).setVisibility(
                    View.VISIBLE);
            TextView text_determine = (TextView) mMenuView
                    .findViewById(R.id.text_determine);
            text_determine.setText(text);
            mMenuView.findViewById(R.id.btn_determine)
                    .setOnClickListener(itemsOnClick);
        } else if (tp == 4) {
            mMenuView.findViewById(R.id.show1).setVisibility(
                    View.VISIBLE);
            Button btn_marry_share = (Button) mMenuView
                    .findViewById(R.id.btn_take_photo);
            btn_marry_share.setOnClickListener(itemsOnClick);
            Button btn_marry_favorite = (Button) mMenuView
                    .findViewById(R.id.btn_pick_photo);
            btn_marry_favorite.setOnClickListener(itemsOnClick);
            btn_marry_share.setText("分享相册");
            btn_marry_favorite.setText(text);
        }
        if (tp != 2) {
            // 取消按钮
            btn_cancel.setVisibility(View.VISIBLE);
            btn_cancel.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    // 销毁弹出框
                    dismiss();
                }
            });
        }
        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_layout)
                        .getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}

package com.shengshi.rebate.ui.home.popup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioButton;
import android.widget.TextView;

import com.shengshi.base.tools.AppHelper;
import com.shengshi.rebate.R;

import java.util.ArrayList;

/**
 * <p>Title:        菜单控件头部，封装了下拉动画，动态生成头部按钮个数
 * <p>Description:  参考https://github.com/yueyueniao2012/ExpandTabView封装，
 * 比原版 扩展PopView,只保留一个PopView,更灵活，适应性强
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-12
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class ExpandTabView extends LinearLayout implements OnDismissListener {
    private RadioButton mSelectedButton;
    private ArrayList<String> mTextArray = new ArrayList<String>();
    private ArrayList<LinearLayout> mViewArray = new ArrayList<LinearLayout>();
    private ArrayList<View> mSelectTab = new ArrayList<View>();
    private Context mContext;
    private int displayWidth;
    private int displayHeight;
    private PopupWindow popupWindow;
    private int mSelectPosition;

    public ExpandTabView(Context context) {
        super(context);
        init(context);
    }

    public ExpandTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        displayWidth = AppHelper.getScreenWidth(context);
        displayHeight = AppHelper.getScreenHeight(context);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    /**
     * 根据选择的位置设置tabitem显示的值
     */
    public void setTitle(String valueText, int position) {
        if (position < mSelectTab.size()) {
            DrawableCenterRadioButton categoryButton = findRadioButton(mSelectTab.get(position));
            if (categoryButton != null) {
                categoryButton.setText(valueText);
            }
        }
    }

    private DrawableCenterRadioButton findRadioButton(View view) {
        if (view == null) {
            return null;
        }
        DrawableCenterRadioButton categoryButton = (DrawableCenterRadioButton) view.findViewById(R.id.rebate_filter_category_tv);
        return categoryButton;
    }

    /**
     * 根据选择的位置获取tabitem显示的值
     */
    public String getTitle(int position) {
        if (position < mSelectTab.size()) {
            DrawableCenterRadioButton categoryButton = findRadioButton(mSelectTab.get(position));
            if (categoryButton != null && categoryButton.getText() != null) {
                return categoryButton.getText().toString();
            }
        }
        return "";
    }

    /**
     * 设置tabitem的个数和初始值
     */
    public void setValue(ArrayList<String> textArray, ArrayList<View> popViewArray) {
        if (mContext == null) {
            return;
        }
        removeAllViews();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mTextArray = textArray;
        LinearLayout container;
        mViewArray.clear();
        mSelectTab.clear();
        for (int i = 0; i < popViewArray.size(); i++) {
            container = new LinearLayout(mContext);
            int maxHeight = (int) (displayHeight * 0.7);
            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT, maxHeight, 1.0f);
//			params.leftMargin = 10;
//			params.rightMargin = 10;
//			params.topMargin = 10;
//			params.bottomMargin = 10;
            container.addView(popViewArray.get(i), params);
            mViewArray.add(container);
            View categoryView = inflater.inflate(R.layout.rebate_popupwindow_expand_tab, this, false);

            addView(categoryView);

            View line = new TextView(mContext);
            line.setBackgroundColor(mContext.getResources().getColor(R.color.line_color));
            if (i < popViewArray.size() - 1) {
                LayoutParams lp = new LayoutParams(2, LayoutParams.MATCH_PARENT);
                addView(line, lp);
            }
            mSelectTab.add(categoryView);
            DrawableCenterRadioButton categoryButton = findRadioButton(categoryView);
            //设置位置
            categoryButton.setTag(i);
            categoryButton.setText(mTextArray.get(i));

//			initBottomIndicatorLine(i, categoryView);

            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPressBack();
                }
            });

            container.setBackgroundColor(mContext.getResources().getColor(R.color.popup_main_background));
            categoryButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioButton radioButton = (RadioButton) view;
                    mSelectedButton = radioButton;
                    mSelectPosition = (Integer) mSelectedButton.getTag();
                    refreshUI();
                    startAnimation();
                    if (mOnButtonClickListener != null && radioButton.isChecked()) {
                        mOnButtonClickListener.onClick(view, mSelectPosition);
                    }
                }
            });
        }
    }

    /**
     * 初始化底部指示器闲
     *
     * @param index
     * @param categoryView
     */
    public void initBottomIndicatorLine(int index, View categoryView) {
        ImageView indicatorLine = findIndicator(categoryView);
        if (index == 0) {
            indicatorLine.setVisibility(View.VISIBLE);
        } else {
            indicatorLine.setVisibility(View.GONE);
        }
    }

    /**
     * 重置UI
     */
    public void resetUI() {
        for (int index = 0; index < mViewArray.size(); index++) {
            DrawableCenterRadioButton categoryButton = findRadioButton(mSelectTab.get(index));
            Drawable right = getContext().getResources().getDrawable(
                    R.drawable.rebate_dropdown_icon);
            right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
            categoryButton.setCompoundDrawables(null, null, right, null);
            ImageView indicatorLine = findIndicator(mSelectTab.get(index));
            indicatorLine.setVisibility(View.GONE);
        }
    }

    /**
     * 状态改变，更新UI
     */
    private void refreshUI() {
        Drawable right = null;
        for (int index = 0; index < mViewArray.size(); index++) {
            DrawableCenterRadioButton categoryButton = findRadioButton(mSelectTab.get(index));
            ImageView indicatorLine = findIndicator(mSelectTab.get(index));
            if (categoryButton != null && index == mSelectPosition) {
                right = getContext().getResources().getDrawable(R.drawable.rebate_dropup_icon);
                indicatorLine.setVisibility(View.VISIBLE);
            } else {
                right = getContext().getResources().getDrawable(R.drawable.rebate_dropdown_icon);
                indicatorLine.setVisibility(View.GONE);
            }
            right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
            categoryButton.setCompoundDrawables(null, null, right, null);
        }
    }

    private ImageView findIndicator(View view) {
        if (view == null) {
            return null;
        }
        ImageView indicatorLine = (ImageView) view.findViewById(R.id.rebate_filter_bottom_indicator);
        return indicatorLine;
    }

    private void startAnimation() {
        if (popupWindow == null) {
            popupWindow = new PopupWindow(mViewArray.get(mSelectPosition), displayWidth, displayHeight);
            popupWindow.setAnimationStyle(R.style.popwinAnimation);
            popupWindow.setFocusable(false);
            popupWindow.setOutsideTouchable(true);
        }
        if (mSelectedButton.isChecked()) {
            if (!popupWindow.isShowing()) {
                showPopup(mSelectPosition);
            } else {
                popupWindow.setOnDismissListener(this);
                popupWindow.dismiss();
                hideView();
            }
        } else {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                hideView();
            }
        }
    }

    private void showPopup(int position) {
        View tView = mViewArray.get(mSelectPosition).getChildAt(0);
        if (tView instanceof ViewBaseAction) {
            ViewBaseAction f = (ViewBaseAction) tView;
            f.show();
        }
        if (popupWindow.getContentView() != mViewArray.get(position)) {
            popupWindow.setContentView(mViewArray.get(position));
        }
        popupWindow.showAsDropDown(this, 0, 1);
    }

    /**
     * 如果菜单成展开状态，则让菜单收回去
     */
    public boolean onPressBack() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            hideView();
            if (mSelectedButton != null) {
                mSelectedButton.setChecked(false);
            }
            resetUI();//UI复原
            return true;
        } else {
            return false;
        }
    }

    private void hideView() {
        View tView = mViewArray.get(mSelectPosition).getChildAt(0);
        if (tView instanceof ViewBaseAction) {
            ViewBaseAction f = (ViewBaseAction) tView;
            f.hide();
        }
    }

    @Override
    public void onDismiss() {
        showPopup(mSelectPosition);
        popupWindow.setOnDismissListener(null);
    }

    private OnButtonClickListener mOnButtonClickListener;

    /**
     * 设置tabitem的点击监听事件
     */
    public void setOnButtonClickListener(OnButtonClickListener listener) {
        mOnButtonClickListener = listener;
    }

    /**
     * 自定义tabitem点击回调接口
     */
    public interface OnButtonClickListener {
        public void onClick(View view, int selectPosition);
    }

}

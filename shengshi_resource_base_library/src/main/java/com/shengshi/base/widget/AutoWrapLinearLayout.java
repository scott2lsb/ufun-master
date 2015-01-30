package com.shengshi.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * <p>Title:     实现自动换行
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014年6月18日
 * <p>Update Time:  2014-11-4
 * <p>Updater:    liaodl
 * <p>Update Comments: 修复设置paddingTop，控件onMesure高度问题
 */
public class AutoWrapLinearLayout extends LinearLayout {

    int mLeft, mRight, mTop, mBottom;
    Hashtable<View, Position> map = new Hashtable<View, Position>();
    boolean centerHorizontal = false;

    /**
     * 每个view上下的间距
     */
    private final int dividerLine = 5;
    /**
     * 每个view左右的间距
     */
    private final int dividerCol = 8;

    public AutoWrapLinearLayout(Context context) {
        super(context);
    }

    public AutoWrapLinearLayout(Context context, int horizontalSpacing, int verticalSpacing) {
        super(context);
    }

    public AutoWrapLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        int w_g = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android",
                "gravity", 0);
        //TypedArray ta=context.obtainStyledAttributes(attrs, R)
        centerHorizontal = w_g == Gravity.CENTER_HORIZONTAL;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mCount = getChildCount();
        List<View> w_arr = new ArrayList<View>();
        mLeft = 0;
        mRight = 0;
        mTop = 0;
        mBottom = 0;

        int j = 0;
        int childw = 0;
        for (int i = 0; i < mCount; i++) {
            final View child = getChildAt(i);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            // 此处增加onlayout中的换行判断，用于计算所需的高度
            childw = child.getMeasuredWidth();
            int childh = child.getMeasuredHeight();
            mRight += childw; //将每次子控件宽度进行统计叠加，如果大于设定的宽度则需要换行，高度即Top坐标也需重新设置

            Position position = new Position();
            mLeft = getPosition(i - j, i);
            mRight = mLeft + child.getMeasuredWidth();
            if (mRight >= mWidth) {
                j = i;
                mLeft = getPaddingLeft();
                mRight = mLeft + child.getMeasuredWidth();
                mTop += childh + dividerLine;
                //PS：如果发现高度还是有问题就得自己再细调了
                if (centerHorizontal) {
                    centerThese(w_arr, mWidth);
                    w_arr.clear();
                }
            }
            mBottom = mTop + child.getMeasuredHeight() + getPaddingTop();
            position.left = mLeft;
            position.top = mTop + getPaddingTop();
            position.right = mRight;
            position.bottom = mBottom;
            map.put(child, position);
            if (centerHorizontal) {
                w_arr.add(child);
            }
        }
        centerThese(w_arr, mWidth);
        setMeasuredDimension(mWidth, mBottom + getPaddingBottom());
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            Position pos = (Position) map.get(child);
            if (pos != null) {
                child.layout(pos.left, pos.top, pos.right, pos.bottom);
            } else {
                Log.i("AutoWrapLinearLayout", "onLayout error");
            }
        }
    }

    private class Position {
        int left, top, right, bottom;
    }

    public int getPosition(int IndexInRow, int childIndex) {
        if (IndexInRow > 0) {
            return getPosition(IndexInRow - 1, childIndex - 1)
                    + getChildAt(childIndex - 1).getMeasuredWidth() + 8;
        }
        return getPaddingLeft();
    }

    /**
     * 重新把这些元素居中排列
     *
     * @param items
     * @param width
     * @param itemWidth
     */
    protected void centerThese(List<View> items, int width) {
        int w_twidth = 0;
        for (View v : items) {
            w_twidth += v.getMeasuredWidth();
        }
        int w_left = Math.round((width - w_twidth) / 2);
        int w_fleft = w_left;
        for (View v : items) {
            Position p = map.get(v);
            int w_t1 = w_fleft;
            int w_t2 = w_t1 - p.left;
            p.left = w_t1;
            p.right = p.right + w_t2;
            w_fleft = p.left + v.getMeasuredWidth() + dividerCol;
            map.put(v, p);
        }
    }

}
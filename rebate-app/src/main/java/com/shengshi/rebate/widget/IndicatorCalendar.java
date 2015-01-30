package com.shengshi.rebate.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.shengshi.rebate.R;

import java.util.Calendar;

/**
 * <p>Title:        所有五折信息日历选择控件
 * <p>Description:
 * <p>@author:         liaodl
 * <p>Copyright:   Copyright (c) 2014
 * <p>Company:       @小鱼网
 * <p>Create Time: 2014-11-24
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class IndicatorCalendar extends LinearLayout {

    Calendar mCalendar;
    private View calendarView;

    public IndicatorCalendar(Context context) {
        super(context);
        initClock(context);
    }

    public IndicatorCalendar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        addView(initView(context));
    }

    private View initView(Context context) {
        if (calendarView == null) {
            calendarView = LayoutInflater.from(context).inflate(
                    R.layout.rebate_widget_indicator_calendar, null);
        }
        return calendarView;
    }

    public void setText(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            setVisibility(View.GONE);
            return;
        }
    }

}

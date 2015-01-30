package com.shengshi.rebate.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengshi.base.tools.DateUtil;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.rebate.R;

import java.util.Calendar;
import java.util.Date;

/**
 * <p>Title:        小日历控件
 * <p>Description:
 * <p>@author:         liaodl
 * <p>Copyright:   Copyright (c) 2014
 * <p>Company:       @小鱼网
 * <p>Create Time: 2014-8-20
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateCalendar extends LinearLayout {

    Calendar mCalendar;
    private View calendarView;
    private TextView weekView;
    private TextView dayView;

    public RebateCalendar(Context context) {
        super(context);
        initClock(context);
    }

    public RebateCalendar(Context context, AttributeSet attrs) {
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
                    R.layout.rebate_widget_little_calendar, null);
        }
        weekView = (TextView) calendarView.findViewById(R.id.week);
        dayView = (TextView) calendarView.findViewById(R.id.day);
        return calendarView;
    }

    /**
     * 传入格式如"2014-10-31 00:00:00" 日期字符串，
     * 自动填写星期几  和 日期数
     *
     * @param dateStr
     */
    public void setText(String dateStr) {
//		dateStr = "2014-10-31 00:00:00";
        if (TextUtils.isEmpty(dateStr)) {
            setVisibility(View.GONE);
            return;
        }
        try {
            Date date = TimeUitls.getDateByString(dateStr, "yyyy-MM-dd HH:mm:ss");
            mCalendar.setTime(date);

            String week = getWeek(mCalendar.get(Calendar.DAY_OF_WEEK));
            weekView.setText(week);

            String dayValue = "";
            String curDay = DateUtil.getCurrentDay();
            int serverDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            if (serverDay <= 9) {
                dayValue = "0" + serverDay;
            } else {
                dayValue = "" + serverDay;
            }
            if (curDay.equalsIgnoreCase(serverDay + "")) {
                dayValue = "今天";
            }
            dayView.setText(dayValue);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
            setVisibility(View.GONE);
            invalidate();
        }
    }

    public void setDay(String dayStr) {
        if (!TextUtils.isEmpty(dayStr)) {
            dayView.setText(dayStr);
        }
    }

    public void setWeek(String weekStr) {
        if (!TextUtils.isEmpty(weekStr)) {
            weekView.setText(weekStr);
        }
    }

    private String getWeek(int value) {
        String result = "";
        switch (value) {
            case 1:
                result = "星期日";
                break;
            case 2:
                result = "星期一";
                break;
            case 3:
                result = "星期二";
                break;
            case 4:
                result = "星期三";
                break;
            case 5:
                result = "星期四";
                break;
            case 6:
                result = "星期五";
                break;
            case 7:
                result = "星期六";
                break;
            default:
                break;
        }
        return result;
    }

}

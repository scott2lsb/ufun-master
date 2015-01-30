package com.shengshi.base.tools;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>Title:      日期时间工具类                                </p>
 * <p>Description:  包括以下功能，其他可参考{@link com.shengshi.base.tools.DateUtil}工具类
 * <p>1. 获取当前时间
 * <p>2. 比较两个时间点的时间差，返回分钟
 * <p>3. 格式化日期
 * <p>4. 取得一天后的当前时间
 * <p>5. 友好显示时间
 * <p>6. 获取天数
 * <p>7. 获取小时数
 * <p>8. 获取分钟数
 * <p>9. 获取秒数
 * </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company:        @小鱼网           </p>
 * <p>Create Time: 2013-4-12           </p>
 * <p>@author:                         </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TimeUitls {

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new Date());
        return date;
    }

    /**
     * 获取当前时间
     *
     * @param formats
     * @return
     */
    public static String getCurrentTime(String formats) {
        Date today = new Date();
        if (TextUtils.isEmpty(formats)) {
            formats = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dateFormater = new SimpleDateFormat(formats);
        String nowDate = dateFormater.format(today);
        return nowDate;
    }

    /**
     * 比较时间差，如：相差5分钟
     *
     * @param time1 当前时间
     * @param time2 要比较的时间
     * @return
     */
    public static long compareTime(String time1, String time2) {
        long minutes = 0L;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(time1);
            Date d2 = df.parse(time2);
            long diff = d1.getTime() - d2.getTime();
            minutes = diff / (60 * 1000);
        } catch (Exception e) {
        }
        return minutes;
    }

    /**
     * 用法：Date d = getDate("2013-01-18 16:16:43.0", "yyyy-MM-dd HH:mm:ss");
     *
     * @param dateStr
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date getDateByString(String dateStr, String pattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.parse(dateStr);
    }

    /**
     * 用法: formatDate(d, "yyyy-MM-dd HH:mm:ss");
     *
     * @param currentDate
     * @param pattern
     * @return
     */
    public static String formatDate(Date currentDate, String pattern) {
        if (currentDate == null || pattern == null) {
            throw new NullPointerException("The arguments are null !");
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(currentDate);
    }

    /**
     * 取得一天后的时间
     *
     * @return
     */
    public static String getTomorrowTime() {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
        Calendar Cal = Calendar.getInstance();
        Cal.setTime(new Date());
        Cal.add(java.util.Calendar.HOUR_OF_DAY, 24);
        return formatter.format(Cal.getTime());
    }

    /**
     * 以友好的方式显示时间
     * 论坛搜索用到
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate, int tp) {
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormater2 = new SimpleDateFormat("yy-MM-dd");
        Date time = null;
        String ftime = "";
        try {
            if (tp == 1) {
                String tmp = dateFormater.format(new Date(toInt(sdate) * 1000L));
                time = getDateByString(tmp, "yyyy-MM-dd HH:mm:ss");
            } else {
                time = getDateByString(sdate, "yyyy-MM-dd HH:mm:ss");
            }
            if (time == null) {
                return "Unknown";
            }
            Calendar cal = Calendar.getInstance();
            // 判断是否是同一天
            String curDate = dateFormater2.format(cal.getTime());
            String paramDate = dateFormater2.format(time);
            if (curDate.equals(paramDate)) {
                int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
                if (hour == 0)
                    ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
                else
                    ftime = hour + "小时前";
                return ftime;
            }
            long lt = time.getTime() / 86400000;
            long ct = cal.getTimeInMillis() / 86400000;
            int days = (int) (ct - lt);
            if (days == 0) {
                int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
                if (hour == 0)
                    ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
                else
                    ftime = hour + "小时前";
            } else if (days == 1) {
                ftime = "昨天";
            } else if (days == 2) {
                ftime = "前天";
            } else if (days > 2 && days <= 10) {
                ftime = days + "天前";
            } else if (days > 10) {
                ftime = dateFormater2.format(time);
            }
        } catch (ParseException e) {
            Log.e(e.getMessage(), e);
        }
        return ftime;
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    private static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    private static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 获取天数
     *
     * @param timeDistance
     * @return
     */
    public static int getDays(long timeDistance) {
        return (int) (timeDistance / (24 * 60 * 60 * 1000));
    }

    /**
     * 获取小时数
     *
     * @param timeDistance
     * @return
     */
    public static int getHour(long timeDistance) {
        int hour = (int) (timeDistance % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        return hour;
    }

    /**
     * 获取分钟数
     *
     * @param timeDistance
     * @return
     */
    public static int getMinute(long timeDistance) {
        int minute = 0;
        minute = (int) (timeDistance % (24 * 60 * 60 * 1000)) % (60 * 60 * 1000) / (60 * 1000);
        return minute;
    }

    /**
     * 获取秒数
     *
     * @param timeDistance
     * @return
     */
    public static int getMills(long timeDistance) {
        int mills = (int) (timeDistance % (24 * 60 * 60 * 1000)) % (60 * 60 * 1000) % (60 * 1000) / 1000;
        return mills;
    }

}

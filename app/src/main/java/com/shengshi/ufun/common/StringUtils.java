package com.shengshi.ufun.common;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 *
 * @author dengbw
 * @version 1.0
 * @created 2014-12-30
 */
public class StringUtils {
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate, int tp) {
        Date time = null;
        if (tp == 1) {
            time = toDate(formatDate(sdate));
        } else {
            time = toDate(sdate);
        }
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
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
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.format(today);
            String timeDate = dateFormater2.format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 整型转字符
     */
    public static String toString(int s) {
        return String.valueOf(s);
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
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
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 检测String是否全英文
     *
     * @param str
     * @return true是，否则是false
     */
    public static boolean checkNameEnglish(String str) {
        Pattern pa = Pattern.compile("^[A-Za-z]+$");
        Matcher matcher = pa.matcher(str);
        return matcher.find();
    }

    /**
     * 检测String是否全是中文
     *
     * @param str
     * @return true是，否则是false
     */
    public static boolean checkNameChese(String str) {
        Pattern pa = Pattern.compile("^[\u4e00-\u9fa5]*$");
        Matcher matcher = pa.matcher(str);
        return matcher.find();
    }

    /**
     * 检测String长度(中文算两个)
     *
     * @param str
     * @return
     */
    public static int getWordCount(String str) {
        // 将字符串中所有的非标准字符（双字节字符）替换成两个标准字符（**，或其他的也可以）
        str = str.replaceAll("[^\\x00-\\xff]", "**");
        int length = str.length();
        return length;
    }

    /**
     * 检测手机格式判断
     *
     * @param str
     * @return
     */
    public static boolean checkCellphone(String str) {
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 去掉html标签
     *
     * @param inputString
     * @return
     */
    public static String HtmlTitle(String inputString) {
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        try {
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
            textStr = htmlStr;
        } catch (Exception e) {
        }
        return textStr;
    }

    /*
     * 正常显示 TextView
     */
    public static void setViewText(TextView v, Object text) {
        if (v == null)
            return;
        if (TextUtils.isEmpty((CharSequence) text)) {
            v.setText("");
        } else {
            v.setText((CharSequence) text);
        }
    }

    /**
     * 字节数据转换为对象
     *
     * @param bytes: 数据
     * @return: 对象
     */
    public static Object bytesToObject(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        Object obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);
            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 对象转换为字节数组(对象必须实现序列化接口)
     *
     * @param obj : 对象
     * @return: 数据
     */
    public static byte[] objectToBytes(Object obj) {
        if (obj == null) {
            return null;
        }
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            oo.flush();
            bytes = bo.toByteArray();
            bo.close();
            oo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (bytes);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕宽度高度
     *
     * @param context
     * @param tp      0宽度，1高度
     * @return int
     */
    public static int getDisplayMetrics(Context context, int tp) {
        int pixels = 0;
        if (tp == 0) {
            pixels = context.getResources().getDisplayMetrics().widthPixels;
        } else if (tp == 1) {
            pixels = context.getResources().getDisplayMetrics().heightPixels;
        }
        return pixels;
    }

    public static String TimeStampDate(String sdate, String formats) {
        Date time = toDate(formatDate(sdate));
        if (time == null) {
            return "Unknown";
        }
        if (TextUtils.isEmpty(formats)) {
            formats = "yyyy-MM-dd HH:mm:ss";
        }
        String ftime = "";
        SimpleDateFormat dateFormater = new SimpleDateFormat(formats);
        ftime = dateFormater.format(time);
        return ftime;
    }

    /**
     * 将时间戳格式转为日期
     */
    public static String formatDate(String sdate) {
        return dateFormater.format(new Date(toInt(sdate) * 1000L));
    }

    /**
     * 获取当前时间
     *
     * @param formats
     * @return String
     */
    public static String getToday(String formats) {
        Date today = new Date();
        if (TextUtils.isEmpty(formats)) {
            formats = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dateFormater = new SimpleDateFormat(formats);
        String nowDate = dateFormater.format(today);
        return nowDate;
    }

    /**
     * 获取当前时间戳
     *
     * @return long
     */
    public static long getTime() {
        long currentTimeMillis = System.currentTimeMillis() / 1000L;
        return currentTimeMillis;
    }

    /**
     * 论坛帖子楼数转化成 沙发，板凳，地板的形式
     *
     * @param int : num 帖子楼数
     * @return: String
     */
    public static String threadsNumToStr(int num) {
        String str = "";
        switch (num) {
            case 0: {
                str = "楼主";
                break;
            }
            case 1: {
                str = "沙发";
                break;
            }
            case 2: {
                str = "板凳";
                break;
            }
            case 3: {
                str = "地板";
                break;
            }
            default: {
                str = Integer.toString(num) + "楼";
                break;
            }
        }
        return str;
    }

    /**
     * 将字符串中的\n替换成<br>
     *
     * @param needle
     * @param str
     * @param haystack
     * @return
     */
    public static String replace(String needle, String str, String haystack) {
        if (haystack == null) {
            return null;
        }
        int i = 0;
        if ((i = haystack.indexOf(needle, i)) >= 0) {
            char[] line = haystack.toCharArray(); // 把字串类转成字符数组
            char[] newString = str.toCharArray();
            int needleLength = needle.length();
            StringBuffer buf = new StringBuffer(line.length);
            buf.append(line, 0, i).append(newString);
            i += needleLength;
            int j = i;
            while ((i = haystack.indexOf(needle, i)) > 0) {
                buf.append(line, j, i - j).append(newString);
                i += needleLength;
                j = i;
            }
            buf.append(line, j, line.length - j);
            return buf.toString();
        }
        return haystack;
    }

    /**
     * 基本功能：过滤所有以"<"开头以">"结尾的标签
     *
     * @param str
     * @return String
     */
    public static String filterHtml(String str) {
        String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签
        Pattern pattern = Pattern.compile(regxpForHtml);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static boolean isValidate(List<?> list) {
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    public static boolean isValidate(Map<?, ?> map) {
        if (map != null && map.size() > 0) {
            return true;
        }
        return false;
    }

}
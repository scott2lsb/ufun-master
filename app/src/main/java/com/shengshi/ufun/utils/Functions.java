package com.shengshi.ufun.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.shengshi.base.tools.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共函数类
 *
 * @author ppl
 */
public class Functions {
    public static final Uri APN_URI = Uri.parse("content://telephony/carriers");
    public static final Uri CURRENT_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat dateFormater2 = new SimpleDateFormat("yy-MM-dd");
    private final static SimpleDateFormat dateFormater3 = new SimpleDateFormat("HH:mm");

    /**
     * 将时间戳格式转为日期
     *
     * @param sdate
     * @return
     */
    public static String formatDate(String sdate) {
        return dateFormater.format(new Date(toInt(sdate) * 1000L));
    }

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
            ftime = "昨天 " + dateFormater3.format(time);
        } else if (days == 2) {
            ftime = "前天 " + dateFormater3.format(time);
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.format(time);
        }
        return ftime;
    }

    /**
     * 时间格式化 格式如下: 昨天 前天 3-7天前 M月d日 HH:MM
     *
     * @param date : 时间
     * @return: 格式化后的字符串
     * <p/>
     * public static String timeIntervalString1(Date date) { if (date
     * == null) { return ""; } String dateString = ""; long oldTime =
     * date.getTime(); long currentTime = (new Date()).getTime(); long
     * intervalTime = (currentTime - oldTime) / 1000; if (intervalTime
     * / (24 * 3600) > 7) { SimpleDateFormat sdf = new
     * SimpleDateFormat("M月d日 HH:mm"); dateString = sdf.format(date); }
     * else if (intervalTime / (24 * 3600) >= 3) { dateString =
     * String.format("%d天前", intervalTime / (24 * 3600)); } else if
     * (intervalTime / (24 * 3600) == 2) { dateString = "前天"; } else if
     * (intervalTime / (24 * 3600) == 1) { dateString = "昨天"; } else if
     * (intervalTime >= 3600) { dateString = String.format("%d小时前",
     * intervalTime / 3600); } else if (intervalTime >= 60) {
     * dateString = String.format("%d分钟前", intervalTime / 60); } else {
     * dateString = "1分钟内"; }
     * <p/>
     * return dateString; }
     */
    public static Date TimeStamp2Date(String timestampString, String formats) {
        if (TextUtils.isEmpty(formats)) {
            formats = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat format = new SimpleDateFormat(formats);
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String strDate = format.format(new java.util.Date(timestamp));
        Date date = null;
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String TimeStamp3Date(String sdate, String formats) {
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
     * drawable转换为bitmap
     *
     * @param drawable : drawable
     * @return: bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        try {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            Bitmap bitmap = bd.getBitmap();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
     * 字节数据转换为对象
     *
     * @param bytes : 数据
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
     * 格式验证
     *
     * @param text   : 需要匹配的字符串
     * @param format : 格式字符串
     * @return: 是否匹配
     */
    public static boolean formatValidation(String text, String format) {
        if (text == null || format == null) {
            return false;
        }
        Pattern pat = Pattern.compile(format);
        Matcher matcher = pat.matcher(text);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context : 上下文
     * @param dpValue : dp
     * @return: px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context : 上文件
     * @param pxValue : px
     * @return: dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 检测是否有google地图
     *
     * @return: 是否成功
     */
    public static boolean checkGoogleMap() {
        try {
            Class.forName("com.google.android.maps.MapActivity");
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 判断wifi网络是否链接
     *
     * @param context : 上下文
     * @return: wifi网络是否可用
     */
    public static boolean isWiFiAvailable(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = (wifiInfo == null ? 0 : wifiInfo.getIpAddress());
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            return true;
        } else {
            return false;
        }
    }

//    /**
//     * 判断3G网络是否链接
//     * 
//     * @param context
//     *            : 上下文
//     * @return: 3G网络是否可用
//     */
//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivity = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivity != null) {
//            NetworkInfo info = connectivity.getActiveNetworkInfo();
//            if (info != null) {
//                if (info.isAvailable()) {
//                    if (info.getExtraInfo() != null
//                            && info.getExtraInfo().equals("cmnet")) {
//                        return true;
//                    }
//                    else if (info.getExtraInfo() != null
//                            && info.getExtraInfo().equals("cmwap")) {
//                        HttpClient client = new HttpClient();
//                        client.setProxyHost("10.0.0.172");
//                        client.setProxyPort(80);
//                        return true;
//                    }
//                    else {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }

    /**
     * 获取图片，设长宽
     *
     * @param filename
     * @param width
     * @param height
     */
    public static Bitmap decodeBitmapFile(String filename, int width, int height) {
        if (null != filename) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filename, opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength, width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(filename, opts);
            } catch (OutOfMemoryError e) {
                Log.e("内存溢出!");
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 压缩图片
     *
     * @param filename
     * @return
     */
    public static Bitmap decodeBitmapFile(String filename) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filename, options); // 此时返回bm为空
        /*
		 * // 获取这个图片的宽和高 int width = options.outWidth; int height =
		 * options.outHeight; // 定义预转换成的图片的宽度和高度 int newWidth = 680; int
		 * newHeight = 780; // 计算缩放率，新尺寸除原始尺寸 float scaleWidth = ((float)
		 * newWidth) / width; float scaleHeight = ((float) newHeight) / height;
		 */
        // 缩放比
        int be = (int) (options.outWidth / (float) 420);
        if (be <= 0)
            be = 1;
        options.inSampleSize = be;
        options.inJustDecodeBounds = false;
        // 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(filename, options);
        return bitmap;
    }

    /**
     * 从给定的Bitmap，并判断是否自动旋转方向
     */
    @SuppressLint("NewApi")
    public static Bitmap rotateBitmapFile(Bitmap bm, String imgpath) {
        if (imgpath != null) {
            int digree = 0;
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imgpath);
            } catch (IOException e) {
                e.printStackTrace();
                exif = null;
            }
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }
            if (digree != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(digree);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            }
        }
        return bm;
    }

    /**
     * 旋转图片
     *
     * @param bitmap
     * @return public static Bitmap rotateBitmapFile(Bitmap bitmap) { //
     * 创建操作图片用的matrix对象 Matrix matrix = new Matrix(); // 旋转图片 动作
     * matrix.postRotate(90); // 创建新的图片 Bitmap resizedBitmap =
     * Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
     * bitmap.getHeight(), matrix, true); return resizedBitmap; }
     */
    // 图片缩放
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
                                        int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    // 图片缩放
    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
                                                int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h
                / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static String getAbsoluteImagePath(Context context, Uri uri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, // Which
                // columns
                // to
                // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static void playSound(Context context, int resourceId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, resourceId);
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.release();
                }
            });
            mediaPlayer.start();
        }
    }

    public static void vibration(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400, 100, 400}; // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1); // 重复上面的pattern 如果只想震动一次，index设为-1
    }

    public static void savePic(Bitmap bitmap, String picName) {
        File f = new File("/mnt/sdcard/" + picName + ".jpg");
        try {
            FileOutputStream fe = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fe);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

//    /**
//     * MD5加密
//     * 
//     * @param str
//     * @return
//     */
//    public static String md5(String str)
//            throws NoSuchAlgorithmException,
//            UnsupportedEncodingException {
//        // 确定计算方法
//        MessageDigest md5 = MessageDigest.getInstance("MD5");
//        BASE64Encoder base64en = new BASE64Encoder();
//        md5.reset();
//        md5.update(str.getBytes("UTF-8"));
//        byte[] byteArray = md5.digest();
//        StringBuffer md5StrBuff = new StringBuffer();
//        for (int i = 0; i < byteArray.length; i++) {
//            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
//                md5StrBuff.append("0").append(
//                        Integer.toHexString(0xFF & byteArray[i]));
//            else
//                md5StrBuff.append(Integer
//                        .toHexString(0xFF & byteArray[i]));
//        }
//        // 加密后的字符串
//        return md5StrBuff.toString();
//    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap
                .createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    // public static String getTimedifference(int create_time){
    // //获取系统当前的时间-- time
    // Timestamp time = new Timestamp(System.currentTimeMillis());
    // long t= time.getTime();
    //
    // ( )/1000/60/60/24;
    //
    // return null;
    //
    // }
    public static String remainDateToString(int create_time) {
        java.util.Date startDate = null;
        java.util.Date endDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDateStr = sdf.format(new Date(create_time * 1000L));
        String endDateStr = sdf.format(new Date());
        Calendar calS = Calendar.getInstance();
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        try {
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        calS.setTime(startDate);
        int startY = calS.get(Calendar.YEAR);
        int startM = calS.get(Calendar.MONTH);
        int startD = calS.get(Calendar.DATE);
        int startDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);
        calS.setTime(endDate);
        int endY = calS.get(Calendar.YEAR);
        int endM = calS.get(Calendar.MONTH);
        // 处理2011-01-10到2011-01-10，认为服务为一天
        int endD = calS.get(Calendar.DATE) + 1;
        int endDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);
        StringBuilder sBuilder = new StringBuilder();
        if (endDate.compareTo(startDate) < 0) {
            return sBuilder.append("过期").toString();
        }
        int lday = endD - startD;
        if (lday < 0) {
            endM = endM - 1;
            lday = startDayOfMonth + lday;
        }
        // 处理天数问题，如：2011-01-01 到 2013-12-31 2年11个月31天 实际上就是3年
        if (lday == endDayOfMonth) {
            endM = endM + 1;
            lday = 0;
        }
        int mos = (endY - startY) * 12 + (endM - startM);
        int lyear = mos / 12;
        int lmonth = mos % 12;
        if (lyear > 0) {
            sBuilder.append(lyear + "年");
        }
        if (lmonth > 0) {
            sBuilder.append(lmonth + "个月");
        }
        if (lday > 0) {
            sBuilder.append(lday + "天");
        }
        return sBuilder.toString();
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

    /**
     * 去掉thml标签
     *
     * @param inputString
     * @return
     */
    public static String HtmlText(String inputString) {
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        try {
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            // }
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            // }
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
			/* 空格 —— */
            // p_html = Pattern.compile("\\ ", Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = htmlStr.replaceAll(" ", " ");
            textStr = htmlStr;
        } catch (Exception e) {
        }
        return textStr;
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

//	/**
//	 * 获取时间差
//	 * 
//	 * @param end
//	 * @param applicationCtx
//	 * @return
//	 */
//	public static long getTimedifference(LifeCircleApplication applicationCtx) {
//		String begin = applicationCtx.getUserConfig("begin_time", "0");
//		if (!begin.equals("0")) {
//			Long begin_time = Long.parseLong(begin);
//			Timestamp time = new Timestamp(System.currentTimeMillis());
//			long t = time.getTime();
//			long between = (t - begin_time) / 1000;// 除以1000是为了转换成秒
//			long hour = between % (24 * 3600) / 3600;
//			return hour;
//		}
//		return 0;
//	}

}

package com.shengshi.base.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.shengshi.base.R;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * <p>Title:    网络工具类
 * <p>Description:   包括
 * <p> 1. 判断网络是否连接正常 并且 是否toast提示
 * <p> 2. 判断网络类型是否为手机网络
 * <p> 3. 判断网络类型是否为wifi网络
 * <p> 4. 判断MOBILE网络是否可用
 * <p> 5. 判断wifi网络是否可用
 * <p> 6. 获取网络连接的类型(网络制式)
 * <p> 7. 获取Ip地址
 * <p> 8. 端口号是否被占用
 * <p> 9. 获取手机IP地址
 * <p> 10. 获取手机Mac地址
 * <p> 11. 判断网络是否为漫游
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-8
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class NetUtil {

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return true可用 false 不可用
     */
    public static boolean isNetConnectionAvailable(Context context) {
        return isNetConnectionAvailable(context, false);
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return true可用 false 不可用
     */
    public static boolean isNetConnectionAvailable(Context context, boolean showToast) {
        boolean conn = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                conn = true;
            } else {
                conn = false;
            }
        } catch (Exception e) {
            conn = false;
        }
        if (showToast && !conn) {
            Toast.makeText(context, R.string.network_not_connected, Toast.LENGTH_SHORT).show();
        }
        return conn;
    }

    /**
     * 为了兼容旧版小鱼方法名，以后使用请直接调用isNetConnectionAvailable()方法
     *
     * @param context
     * @return
     */
    public static boolean checkNet(Context context) {
        return isNetConnectionAvailable(context);
    }

    /**
     * 为了兼容旧版小鱼方法名，以后使用请直接调用isNetConnectionAvailable()方法
     *
     * @param context
     * @return
     */
    public static boolean checkNet(Context context, boolean showToast) {
        return isNetConnectionAvailable(context, showToast);
    }

    /**
     * 判断MOBILE网络是否可用
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static boolean isMobileDataEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileDataEnable = false;
        isMobileDataEnable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        return isMobileDataEnable;
    }


    /**
     * 判断wifi网络是否可用
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static boolean isWifiDataEnable(Context context) throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiDataEnable = false;
        isWifiDataEnable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        return isWifiDataEnable;
    }

    /**
     * 网络类型是否为手机网络
     *
     * @param context
     * @return
     */
    public static boolean isMobileNet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * 网络类型是否为wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 获取网络制式
     *
     * @param context
     * @return
     */
    public static String getAccessType(Context context) {
        String access = "";
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMgr.getActiveNetworkInfo();
        if (info != null) {
            int netType = info.getType();
            if (netType == ConnectivityManager.TYPE_WIFI) {
                access = NetType.WIFI.name();
            } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                access = NetType.MOBILE.name();
            }
        }
        return access;
    }

    /**
     * 获取Ip地址
     *
     * @return
     */
    public static String getHostIpAddress(Context context) {
        return AppHelper.getLocalIpAddress(context);
    }

    public static enum NetType {
        MOBILE, WIFI
    }

    public static interface NetChangedListener {
        void onNetConnected(NetType type);
    }

    /**
     * 端口号是否被占用
     *
     * @param port
     * @return
     */
    public static boolean isLocalPortInUse(int port) {
        boolean isUsed = false;
        try {
            isUsed = isPortInUse("127.0.0.1", port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return isUsed;
    }

    /**
     * @param host 主机
     * @param port 端口
     * @return true: already in use, false: not.
     * @throws UnknownHostException
     * @brief 检查主机端口是否被占用
     */
    private static boolean isPortInUse(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress theAddress = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(theAddress, port);
            socket.close();
            flag = true;
        } catch (IOException e) {
        }
        return flag;
    }

    /**
     * 判断网络是否为漫游
     */
    public static boolean isNetworkRoaming(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w("couldn't get connectivity manager");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null && tm.isNetworkRoaming()) {
                    Log.d("network is roaming");
                    return true;
                } else {
                    Log.d("network is not roaming");
                }
            } else {
                Log.d("not using mobile network");
            }
        }
        return false;
    }

}

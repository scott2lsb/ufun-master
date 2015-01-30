package com.shengshi.rebate.api;

import android.content.Context;

import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.Log;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.UserInfoEntity;
import com.shengshi.rebate.utils.AccountUtil;
import com.shengshi.rebate.utils.RebateTool;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class BaseEncryptInfo implements Serializable {

    private static BaseEncryptInfo encryptInfo;

    private static final long serialVersionUID = 728936894809750337L;
    private static final char last2byte = (char) Integer.parseInt("00000011", 2);
    private static final char last4byte = (char) Integer.parseInt("00001111", 2);
    private static final char last6byte = (char) Integer.parseInt("00111111", 2);
    private static final char lead6byte = (char) Integer.parseInt("11111100", 2);
    private static final char lead4byte = (char) Integer.parseInt("11110000", 2);
    private static final char lead2byte = (char) Integer.parseInt("11000000", 2);
    private static final char[] encodeTable = new char[]{'<', 'A', 'a', '0', 'B', 'b', '1', 'C',
            'c', '2', 'D', 'd', '3', 'E', 'e', '4', 'F', 'f', '5', 'G', 'g', '6', 'H', 'h', '7',
            'I', 'i', '8', 'J', 'j', '9', 'K', 'k', 'L', 'l', 'M', 'm', 'N', 'n', 'O', 'o', 'P',
            'p', 'Q', 'q', 'R', 'r', 'S', 's', 'T', 't', 'U', 'u', 'V', 'v', 'W', 'w', 'X', 'x',
            'Y', 'y', 'Z', 'z', '>'};

    private JSONObject params = null;
    private String callback = "";//类名.函数名
    private String token = "";
    private int uid = 0;//有范用户id，未登录为0
    private String username = "";//用户名 (可能没有)
    private String cityid = "350200";//城市Code，格式是350200
    private int timestamp = 0;//时间戳 (每次请求的timestamp必需都不一样)
    private String device = "0";//设备ID ios为1 android为0
    private String mobile = "";//同城圈手机号
    private String appversion = "";//返利卡版本号

    private UserInfoEntity mUserEntity;

    private BaseEncryptInfo(Context context) {
        params = new JSONObject();
        cityid = RebateTool.getCityCode(context);
        mUserEntity = AccountUtil.readAccountInfo(context);
        if (mUserEntity != null && mUserEntity.data != null) {
            token = mUserEntity.data.token;
            uid = mUserEntity.data.uid;
            username = mUserEntity.data.username;
            mobile = mUserEntity.data.mobile;
        }
        timestamp = (int) (System.currentTimeMillis() / 1000);
        device = AppHelper.getOSTypeNew();
        boolean isSub = RebateTool.getSubFlag(context);
        if (isSub) {
            appversion = context.getString(R.integer.rebate_version_code);
        } else {
            appversion = AppHelper.getVersionCode(context) + "";
        }
    }

    public static BaseEncryptInfo getInstance(Context context) {
        if (encryptInfo == null) {
            encryptInfo = new BaseEncryptInfo(context);
        }
        encryptInfo.timestamp = (int) (System.currentTimeMillis() / 1000);
        refreshUserInfo(context);
        return encryptInfo;
    }

    private static void refreshUserInfo(Context context) {
        UserInfoEntity user = AccountUtil.readAccountInfo(context);
        if (user != null && user.data != null) {
            encryptInfo.token = user.data.token;
            encryptInfo.uid = user.data.uid;
            encryptInfo.username = user.data.username;
            encryptInfo.mobile = user.data.mobile;
        }
    }

    /**
     * 清空参数
     */
    public void clear() {
        encryptInfo = null;
        params = null;
    }

    /**
     * 重置参数<br/>
     * 因采用单例模式，每个新请求时，如果想删除之前请求的不必要的参数，在添加参数调用putParam()方法之前，请调用下此方法
     */
    public void resetParams() {
        params = new JSONObject();
    }

    public void setParams(JSONObject paramjson) {
        try {
            if (params.length() > 0) {
                JSONArray keys = paramjson.names();
                for (int i = 0; i < keys.length(); i++) {
                    if (!params.isNull(keys.getString(i))) {
                        params.remove(keys.getString(i));
                    }
                    params.put(keys.getString(i), paramjson.get(keys.getString(i)));
                }
            } else {
                this.params = null;
                this.params = paramjson;
            }
        } catch (JSONException e) {
            Log.e(e.getMessage(), e);
        }
    }

    public void putParam(String key, Object value) {
        try {
            params.put(key, value);
        } catch (JSONException e) {
            Log.e(e.getMessage(), e);
        }
    }

    public String encryptCodeByPost() {
        JSONObject key = new JSONObject();
        try {
            key.put("callback", callback);
            key.put("params", params);
            key.put("cityid", cityid);
            key.put("token", token);
            key.put("uid", uid);
            key.put("username", username);
            key.put("device", device);
            key.put("timestamp", timestamp);
            key.put("appversion", appversion);
            String encryptCode = encode(key.toString().getBytes());
            return encryptCode;
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return "";
    }

    /**
     * get请求时还要URLEncoder
     *
     * @return
     */
    public String encryptCode() {
        try {
            return URLEncoder.encode(encryptCodeByPost(), HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            Log.e(e.getMessage(), e);
        }
        return "";
    }

    private String encode(byte[] from) {
        StringBuffer to = new StringBuffer((int) (from.length * 1.34) + 3);
        int num = 0;
        char currentByte = 0;
        for (int i = 0; i < from.length; i++) {
            num = num % 8;
            while (num < 8) {
                switch (num) {
                    case 0:
                        currentByte = (char) (from[i] & lead6byte);
                        currentByte = (char) (currentByte >>> 2);
                        break;
                    case 2:
                        currentByte = (char) (from[i] & last6byte);
                        break;
                    case 4:
                        currentByte = (char) (from[i] & last4byte);
                        currentByte = (char) (currentByte << 2);
                        if ((i + 1) < from.length) {
                            currentByte |= (from[i + 1] & lead2byte) >>> 6;
                        }
                        break;
                    case 6:
                        currentByte = (char) (from[i] & last2byte);
                        currentByte = (char) (currentByte << 4);
                        if ((i + 1) < from.length) {
                            currentByte |= (from[i + 1] & lead4byte) >>> 4;
                        }
                        break;
                }
                to.append(encodeTable[currentByte]);
                num += 6;
            }
        }
        StringBuffer rt = new StringBuffer((int) (from.length * 1.34) + 3);
        int len = to.length();
        int pos = ((int) (Math.random() * 1000)) % len;
        char add = to.charAt(pos);
        int step = (add % len) % 5 + 4;
        to.append(add);
        for (int i = 0; i <= len; i += step) {
            int m = 0;
            if (i + step > len) {
                while (m < step && i + m <= len && to.charAt(i + m) > 0) {
                    rt.append(to.charAt(i + m));
                    m++;
                }
            } else {
                while (m < step && i + step - m - 1 <= len && to.charAt(i + step - m - 1) > 0) {
                    rt.append(to.charAt(i + step - m - 1));
                    m++;
                }
            }
        }
        return rt.toString();
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public JSONObject getParams() {
        return params;
    }

    /**
     * 返回参数，格式为Map<String, Object>
     *
     * @return
     */
    public Map<String, Object> getParamsForMap() {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        try {
            if (params.length() > 0) {
                JSONArray keys = params.names();
                for (int i = 0; i < keys.length(); i++) {
                    String key = keys.getString(i);
                    if (!params.isNull(key)) {
                        paramsMap.put(key, params.get(key));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(e.getMessage(), e);
        }
        return paramsMap;
    }

    @Override
    public String toString() {
        String httpParams = "";
        try {
            if (params.length() > 0) {
                JSONArray keys = params.names();
                for (int i = 0; i < keys.length(); i++) {
                    String key = keys.getString(i);
                    if (!params.isNull(key)) {
                        httpParams += key + "=" + params.get(key) + ",";
                    }
                }
                httpParams = httpParams.substring(0, httpParams.length() - 1);
            }
        } catch (JSONException e) {
            Log.e(e.getMessage());
        }
        return "加密参数列表：BaseEncryptInfo[cityid = " + cityid + ",uid = " + uid + ",username = "
                + username + ",token = " + token + ",callback = " + callback + ",params = ['"
                + httpParams + "'] ,timestamp = " + timestamp + ",device = " + device
                + ",appversion = " + appversion + ",mobile = " + mobile + "]";
    }

}

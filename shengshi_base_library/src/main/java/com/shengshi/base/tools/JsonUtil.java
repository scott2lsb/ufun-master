package com.shengshi.base.tools;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * <p>Title:           JSON工具类                                            </p>
 * <p>Description:                          </p>
 * <p>@author:       liaodl                 </p>
 * <p>Copyright: Copyright (c) 2014         </p>
 * <p>Company:    @小鱼网                     </p>
 * <p>Create Time:     2014年7月2日                              </p>
 * <p>@author:                              </p>
 * <p>Update Time:                          </p>
 * <p>Updater:                              </p>
 * <p>Update Comments:                      </p>
 */
public class JsonUtil {

    /**
     * 对象转Json字符串
     *
     * @param entity 需要转换的对象实体
     * @return
     */
    public static <T> String toJson(T t) {
        if (t == null) {
            return "";
        }
        try {
            Gson gson = new Gson();
            return gson.toJson(t);
        } catch (Exception e) {
            Log.e("toJson 异常。", e);
            return "";
        }
    }

    /**
     * Json字符串转对象
     *
     * @param <T>
     * @param json Json字符串
     * @return
     */
    public static <T> T toObject(String json, Class<T> clazz) {
        if (clazz == null || TextUtils.isEmpty(json)) {
            return null;
        }

        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            Log.e("JSON 转换异常！", e);
            try {
                return clazz.newInstance();
            } catch (IllegalAccessException e1) {
                Log.e("toObject IllegalAccessException 实例化异常", e1);
            } catch (InstantiationException e1) {
                Log.e("toObject IllegalAccessException 实例化异常", e1);
            }
        }
        return null;
    }

    /**
     * Json字符串转对象
     *
     * @param json
     * @param type
     * @return
     */
    public static <T> T toObject(String json, Type type) {
        if (type == null || TextUtils.isEmpty(json)) {
            return null;
        }

        try {
            Gson gson = new Gson();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e("JSON 转换异常！", e);
        }
        return null;
    }

    /**
     * 转换字符串为Json对象
     *
     * @param json json字符串
     * @return
     */
    public static JSONObject parseJSON(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSONException :" + e);
        }
        return new JSONObject();
    }

}

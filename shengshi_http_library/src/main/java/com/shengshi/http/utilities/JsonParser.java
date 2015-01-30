package com.shengshi.http.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shengshi.http.net.AppException;

import java.lang.reflect.Type;

/**
 * <p>Title:    JsonParser
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-9-30
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class JsonParser {
    private static Gson gson = new GsonBuilder().serializeNulls().create();//支持value为null

    public static <T> T deserializeFromJson(String json, Class<T> clz) throws AppException {
        return gson.fromJson(json, clz);
    }

    public static <T> T deserializeFromJson(String json, Type type) throws AppException {
        return gson.fromJson(json, type);
    }

    public static String serializeToJson(Object object) throws AppException {
        return gson.toJson(object);
    }
}

package com.shengshi.http.net.callback;

import com.shengshi.http.net.AppException;
import com.shengshi.http.net.AppException.ExceptionStatus;
import com.shengshi.http.utilities.JsonParser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p>Title:     JsonCallback
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
public abstract class JsonCallback<T> extends AbstractCallback<T> {
//	private Class<T> mReturnClass;
//	private Type mReturnType;

    @Override
    public T bindData(String json) throws AppException {
        try {
            Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            return JsonParser.deserializeFromJson(json, type);
        } catch (Exception e) {
            throw new AppException(ExceptionStatus.ParseJsonException, e);
        }
//		if (mReturnClass != null) {
//			return JsonParser.deserializeFromJson(json, mReturnClass);
//		}else if (mReturnType != null) {
//			return JsonParser.deserializeFromJson(json, mReturnType);
//		}
//		return null;
    }

//	public JsonCallback<T> setReturnClass(Class<T> clz){
//		this.mReturnClass = clz;
//		return this;
//	}
//	
//	public JsonCallback<T> setReturnType(Type type){
//		this.mReturnType = type;
//		return this;
//	}
}

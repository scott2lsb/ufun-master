package com.shengshi.http.net.callback;

import com.google.gson.stream.JsonReader;
import com.shengshi.http.entities.BaseEntity;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.AppException.ExceptionStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p>Title:      JsonObjectCallback
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
public abstract class JsonObjectCallback<T extends BaseEntity> extends AbstractCallback<T> {

    @Override
    public T bindData(String json) throws AppException {
        if (path == null) {
            throw new AppException(ExceptionStatus.ParameterException,
                    "you should set path when you use JsonReaderCallback");
        }

        try {
            Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            FileReader in = new FileReader(new File(json));
            JsonReader reader = new JsonReader(in);
            T t = ((Class<T>) type).newInstance();
            t.readFromJson(reader);
            reader.close();
            return t;
        } catch (FileNotFoundException e) {
            throw new AppException(ExceptionStatus.ParseJsonException, e);
        } catch (InstantiationException e) {
            throw new AppException(ExceptionStatus.ParseJsonException, e);
        } catch (IllegalAccessException e) {
            throw new AppException(ExceptionStatus.ParseJsonException, e);
        } catch (IOException e) {
            throw new AppException(ExceptionStatus.ParseJsonException, e);
        }

    }
}

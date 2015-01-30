package com.shengshi.http.net.callback;

import com.shengshi.http.net.AppException;
import com.shengshi.http.net.IRequestListener;

import org.apache.http.HttpResponse;

import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * <p>Title:     ICallback
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
public interface ICallback<T> {
    void onSuccess(T result);

    void onFailure(AppException exception);

    T handle(HttpResponse response, IRequestListener listener) throws AppException;

    T handle(HttpResponse response) throws AppException;

    void cancel(boolean force);

    int retryCount();

    T bindData(String content) throws AppException;

    T preRequest();

    T postRequest(T t);

    boolean isForceCancelled();

    /**
     * 上传图片、文件等 流处理
     *
     * @param out
     * @return
     * @throws AppException
     */
    boolean onCustomOutput(OutputStream out) throws AppException;

    T handle(HttpURLConnection connection, IRequestListener listener) throws AppException;

    T handle(HttpURLConnection connection) throws AppException;

}

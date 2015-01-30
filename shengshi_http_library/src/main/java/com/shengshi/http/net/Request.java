package com.shengshi.http.net;

import android.os.AsyncTask;

import com.shengshi.http.net.callback.ICallback;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.http.utilities.Trace;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>Title:    Request
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
public class Request {
    public enum RequestMethod {
        GET, POST, DELETE, PUT
    }

    public enum RequestTool {
        HTTPCLIENT, HTTPURLCONNECTION
    }

    public RequestMethod method;
    public String url;
    public HttpEntity entity;
    public Map<String, String> headers;
    public ICallback callback;
    public IRequestListener listener;
    private RequestTask task;
    public RequestTool tool;
    public List<NameValuePair> urlParameters;
    public String postContent = "";

    /**
     * 压缩标志，已改为根据header自动判断,设置无效
     */
    @Deprecated
    public boolean isZip = false;

    public Request(String url) {
        this(url, RequestMethod.GET, RequestTool.HTTPCLIENT);
    }

    public Request(String url, RequestMethod method) {
        this(url, method, RequestTool.HTTPCLIENT);
    }

    public Request(String url, RequestTool tool) {
        this(url, RequestMethod.GET, tool);
    }

    public Request(String url, RequestMethod method, RequestTool tool) {
        this.url = url;
        this.method = method;
        this.tool = tool;
    }

    public void setCallback(ICallback callback) {
        this.callback = callback;
    }

    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(key, value);
    }

    public void addHeader(Map<String, String> headerMaps) {
        if (!CheckUtil.isValidate(headerMaps))
            return;
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        this.headers.putAll(headerMaps);
    }

    public void setRequestListener(IRequestListener listener) {
        this.listener = listener;
    }

    public void addParameter(String key, String value) {
        if (urlParameters == null) {
            urlParameters = new ArrayList<NameValuePair>();
        }
        urlParameters.add(new BasicNameValuePair(key, value));
    }

    public void addParameter(Map<String, Object> maps) {
        if (!CheckUtil.isValidate(maps))
            return;
//		Set<String> keys = maps.keySet();
//		Iterator<String> it = keys.iterator();
//		while (it.hasNext()) {
//			String key = it.next();
//			addParameter(key, maps.get(key));
//		}
        for (Entry<String, Object> entry : maps.entrySet()) {
            addParameter(entry.getKey(), entry.getValue() + "");
            Trace.i("请求参数: " + entry.getKey() + " = " + entry.getValue());
        }
    }

    public void addPostContent(String key, String value) {
        if (postContent.length() == 0) {
            this.postContent += key + "=" + value;
        } else {
            this.postContent += "&" + key + "=" + value;
        }
        Trace.i("请求参数: " + this.postContent);
    }

    public void execute() {
        task = new RequestTask(this);
        task.execute();
//		task.executeOnExecutor(exec, params);
    }

    public void cancel(boolean force) {
        if (force && task != null) {
            task.cancel(true);
        }
        if (callback != null) {
            callback.cancel(force);
        }
    }

    public boolean isRunningTask() {
        return task.getStatus() == AsyncTask.Status.RUNNING;
    }

    public void setdebug(boolean isDebug) {
        Trace.setDebugMode(isDebug);
    }
}

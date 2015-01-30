package com.shengshi.http.net;

import com.shengshi.http.net.AppException.ExceptionStatus;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.http.utilities.Trace;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>Title:     HttpUrlConnectionUtil
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
public class HttpUrlConnectionUtil {
    private static final int CONNECT_TIME = 30 * 1000;
    private static final int READ_TIME = 30 * 1000;//5 * 1000

    public static HttpURLConnection execute(Request request) throws AppException {
        switch (request.method) {
            case GET:
            case DELETE:
                return get(request);
            case POST:
            case PUT:
                return post(request);
            default:
                throw new AppException(ExceptionStatus.ParameterException, "the request method "
                        + request.method.name() + " can't be supported");
        }
    }

    private static HttpURLConnection post(Request request) throws AppException {
        OutputStream out = null;
        boolean isClosed = false;
        try {
            URL url = new URL(request.url);
            Trace.i("post请求的url是:" + request.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request.method.name());
            connection.setConnectTimeout(CONNECT_TIME);
            connection.setReadTimeout(READ_TIME);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            //FIXME 优化大文件
            //可以有效防止手机因为内存不足崩溃，此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
//			connection.setChunkedStreamingMode(0);
            setHeader(connection, request.headers);
            out = connection.getOutputStream();
            if (CheckUtil.isValidate(request.urlParameters)) {
                out.write(getParams(request.urlParameters).getBytes());
            }
            if (CheckUtil.isValidate(request.postContent)) {
                out.write(request.postContent.getBytes());
            }
            if (request.callback != null) {
                isClosed = request.callback.onCustomOutput(out);
            }

            return connection;
        } catch (MalformedURLException e) {
            throw new AppException(ExceptionStatus.ServerException, e.getMessage());
        } catch (IOException e) {
            throw new AppException(ExceptionStatus.ServerException, e.getMessage());
        } finally {
            if (out != null && !isClosed) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static HttpURLConnection get(Request request) throws AppException {
        try {
            request.url = getUrl(request.url, request.urlParameters);
            Trace.i("get请求的url是:" + request.url);
            URL url = new URL(request.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request.method.name());
            connection.setConnectTimeout(CONNECT_TIME);
            connection.setReadTimeout(READ_TIME);
            setHeader(connection, request.headers);
            return connection;
        } catch (MalformedURLException e) {
            throw new AppException(ExceptionStatus.ServerException, e.getMessage());
        } catch (IOException e) {
            throw new AppException(ExceptionStatus.ServerException, e.getMessage());
        }
    }

    private static void setHeader(HttpURLConnection connection, Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            for (Entry<String, String> entry : headers.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    private static String getUrl(String url, List<NameValuePair> urlParameters) {
        if (!CheckUtil.isValidate(url) || !CheckUtil.isValidate(urlParameters)) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        for (NameValuePair parame : urlParameters) {
            String key = parame.getName();
            String value = parame.getValue();
            if (url.contains(key)) {//防止重复添加
                continue;
            }
            if (!url.contains("?")) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            sb.append(key).append("=").append(value);
        }
        return url + sb.toString();
    }

    private static String getParams(List<NameValuePair> lists) {
        String params = "";
        NameValuePair pair = null;
        for (int j = 0; j < lists.size(); j++) {
            pair = lists.get(j);
            params += pair.getName() + "=" + pair.getValue();
            if (j != lists.size() - 1) {
                params += "&";
            }
        }
        return params;
    }

}

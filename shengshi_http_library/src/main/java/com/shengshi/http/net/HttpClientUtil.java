package com.shengshi.http.net;

import com.shengshi.http.net.AppException.ExceptionStatus;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.http.utilities.Trace;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <p>Title:    HttpClientUtil
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
public class HttpClientUtil {
    private static HttpClient client;
    private static final long CONNECTION_POOL_TIMEOUT = 30 * 1000;//5 * 1000
    private static final int CONNECTION_TIMEOUT = 30 * 1000;//10 * 1000
    private static final int SOCKET_TIMEOUT = 30 * 1000;//10 * 1000
    private static final int MAX_TOTAL_CONNECTIONS = 400;
    private static final int MAX_ROUTE_CONNECTIONS = 20;

    public static HttpResponse execute(Request request) throws AppException {
        switch (request.method) {
            case GET:
                return get(request);
            case POST:
                return post(request);
            case DELETE:

                break;
            case PUT:

                break;
            default:
                throw new AppException(ExceptionStatus.ParameterException, "the request method "
                        + request.method.name() + " can't be supported");
        }
        return null;
    }

    @SuppressWarnings("unused")
    private static HttpResponse get(Request request) throws AppException {
        HttpGet get = new HttpGet();
        try {
            HttpClient client = getHttpClient();
            request.url = getUrl(request.url, request.urlParameters);
            Trace.i("get请求的url是:" + request.url);
            /* get = new HttpGet(request.url); */
            get.setURI(new URI(request.url));
            setHeader(get, request.headers);
            return client.execute(get);
        } catch (ConnectTimeoutException e) {
            get.abort();
            if (e != null) {
                Trace.e(e.getMessage(), e);
                throw new AppException(ExceptionStatus.TimeoutException, e.getMessage());
            } else {
                throw new AppException(ExceptionStatus.TimeoutException, "ConnectTimeoutException");
            }
        } catch (ClientProtocolException e) {
            get.abort();
            if (e != null) {
                Trace.e(e.getMessage(), e);
                throw new AppException(ExceptionStatus.ServerException, e.getMessage());
            } else {
                throw new AppException(ExceptionStatus.ServerException, "ClientProtocolException");
            }
        } catch (IOException e) {
            get.abort();
            if (e != null) {
                Trace.e(e.getMessage(), e);
                throw new AppException(ExceptionStatus.ServerException, e.getMessage());
            } else {
                throw new AppException(ExceptionStatus.ServerException, "IOException");
            }
        } catch (URISyntaxException e) {
            get.abort();
            if (e != null) {
                Trace.e(e.getMessage(), e);
                throw new AppException(ExceptionStatus.URISyntaxException, e.getMessage());
            } else {
                throw new AppException(ExceptionStatus.URISyntaxException, "URISyntaxException");
            }
        } catch (Exception e) {
            get.abort();
            if (e != null) {
                Trace.e(e.getMessage(), e);
                throw new AppException(ExceptionStatus.IllegalStateException, e.getMessage());
            } else {
                throw new AppException(ExceptionStatus.IllegalStateException, "未知异常");
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

    private static HttpResponse post(Request request) throws AppException {
        try {
            HttpClient client = getHttpClient();
            HttpPost post = new HttpPost(request.url);
            Trace.i("post请求的url是:" + request.url);
            setHeader(post, request.headers);
            if (CheckUtil.isValidate(request.urlParameters)) {
                post.setEntity(new UrlEncodedFormEntity(request.urlParameters, HTTP.UTF_8));
            } else if (CheckUtil.isValidate(request.postContent)) {
                post.setEntity(new StringEntity(request.postContent));
            } else {
                if (request.entity == null) {
                    throw new IllegalStateException(
                            "you should set post content when use POST to request");
                }
                post.setEntity(request.entity);
            }
            return client.execute(post);
        } catch (ConnectTimeoutException e) {
            Trace.e(e.getMessage(), e);
            throw new AppException(ExceptionStatus.TimeoutException, e.getMessage());
        } catch (ClientProtocolException e) {
            Trace.e(e.getMessage(), e);
            throw new AppException(ExceptionStatus.ServerException, e.getMessage());
        } catch (IOException e) {
            Trace.e(e.getMessage(), e);
            throw new AppException(ExceptionStatus.ServerException, e.getMessage());
        }
    }

    public static synchronized HttpClient getHttpClient() {
        if (null == client) {
            HttpParams params = new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, "UTF-8");
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams
                    .setUserAgent(
                            params,
                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
                                    + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            // 3种超时设置
			/*
			 * 定义了从ConnectionManager管理的连接池中取出连接的超时时间
			 * 会抛ConnectionPoolTimeoutException
			 */
            ConnManagerParams.setTimeout(params, CONNECTION_POOL_TIMEOUT);
			/*
			 * 定义了通过网络与服务器建立连接的超时时间。
			 * HttpClient通过一个异步线程去创建与服务器的Socket连接，这就是该Socket连接的超时时间
			 * 会抛ConnectionTimeoutException
			 */
            HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
			/*
			 * 定义了Socket读数据的超时时间，即从服务器获取响应数据需要等待的时间 会抛SocketTimeoutException
			 */
            HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);

            // 设置最大连接数
            ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);
            // 设置每个路由最大连接数
            ConnPerRouteBean connPerRoute = new ConnPerRouteBean(MAX_ROUTE_CONNECTIONS);
            ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
            client = new DefaultHttpClient(conMgr, params);
        }

        return client;
    }

    private static void setHeader(HttpUriRequest mHttpUriRequest, Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            for (Entry<String, String> entry : headers.entrySet()) {
                mHttpUriRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

}

package com.shengshi.http.net.callback;

import com.shengshi.http.net.AppException;
import com.shengshi.http.net.AppException.ExceptionStatus;
import com.shengshi.http.net.IRequestListener;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.http.utilities.Trace;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

/**
 * <p>Title:     AbstractCallback
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-9-28
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public abstract class AbstractCallback<T> implements ICallback<T> {
    protected String path;
    private boolean isGzip;
    private boolean isCancelled;
    private boolean isForceCancelled;

    public void checkIfIsCancelled() throws AppException {
        if (isCancelled) {
            throw new AppException(ExceptionStatus.CancelException,
                    "the request has been cancelled");
        }
    }

    @Override
    public T preRequest() {
        return null;
    }

    @Override
    public T postRequest(T t) {
        return t;
    }

    @Override
    public int retryCount() {//默认超时不重试
        return 0;
    }

    @Override
    public T handle(HttpResponse response) throws AppException {
        return handle(response, null);
    }

    @Override
    public T handle(HttpURLConnection connection) throws AppException {
        return handle(connection, null);
    }

    @Override
    public T handle(HttpURLConnection connection, IRequestListener listener)
            throws AppException {
        try {
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            for (Entry<String, List<String>> map : headerFields.entrySet()) {
                for (String header : map.getValue()) {
                    Trace.i(map.getKey() + " : " + header);
                }
            }
            String encoding = connection.getContentEncoding();
            if (encoding != null && encoding.contains("gzip")) {//首先判断服务器返回的数据是否支持gzip压缩，
                isGzip = true; //如果支持则应该使用GZIPInputStream解压，否则会出现乱码无效数据
            } else {
                isGzip = false;
            }
            int statusCode = connection.getResponseCode();
            switch (statusCode) {
                case HttpStatus.SC_OK:
                    if (CheckUtil.isValidate(path)) {
                        FileOutputStream fos = new FileOutputStream(path);
                        InputStream is = connection.getInputStream();
                        byte[] buffer = new byte[2048];
                        int len = -1;
                        long contentLength = connection.getContentLength();
                        long curPos = 0;
                        while ((len = is.read(buffer)) != -1) {
                            checkIfIsCancelled();
                            if (listener != null) {
                                curPos += len;
                                listener.onProgressUpdate((int) (curPos / 1024),
                                        (int) (contentLength / 1024));
                            }
                            fos.write(buffer, 0, len);
                        }
                        is.close();
                        fos.flush();
                        fos.close();
                        return bindData(path);
                    } else {
                        ByteArrayOutputStream out;
                        InputStream is;
                        GZIPInputStream gis;
                        if (isGzip) {
                            out = new ByteArrayOutputStream();
                            is = connection.getInputStream();
                            gis = new GZIPInputStream(is);
                            byte[] buffer = new byte[2048];
                            int len = -1;
                            long contentLength = connection.getContentLength();
                            long curPos = 0;
                            while ((len = gis.read(buffer)) != -1) {
                                checkIfIsCancelled();
                                if (listener != null) {
                                    curPos += len;
                                    listener.onProgressUpdate((int) (curPos / 1024),
                                            (int) (contentLength / 1024));
                                }
                                out.write(buffer, 0, len);
                            }
                        } else {
                            out = new ByteArrayOutputStream();
                            is = connection.getInputStream();
                            byte[] buffer = new byte[2048];
                            int len = -1;
                            long contentLength = connection.getContentLength();
                            long curPos = 0;
                            while ((len = is.read(buffer)) != -1) {
                                checkIfIsCancelled();
                                if (listener != null) {
                                    curPos += len;
                                    listener.onProgressUpdate((int) (curPos / 1024),
                                            (int) (contentLength / 1024));
                                }
                                out.write(buffer, 0, len);
                            }
                        }
                        is.close();
                        out.flush();
                        out.close();
                        String returnStr = new String(out.toByteArray(), HTTP.UTF_8);
                        Trace.i("http返回:" + returnStr);
                        return bindData(returnStr);
                    }
                default:
                    break;
            }
            return null;
        } catch (IOException e) {
            throw new AppException(ExceptionStatus.IOException, e);
        }
    }

    @Override
    public T handle(HttpResponse response, IRequestListener listener)
            throws AppException {
//		file, json, xml, string, image
        try {
            checkIfIsCancelled();
            HttpEntity entity = response.getEntity();
            Header headers[] = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                Trace.i(headers[i].getName() + ": " + headers[i].getValue());
            }
            Header encoding = entity.getContentEncoding();
            if (encoding != null) {
                for (HeaderElement element : encoding.getElements()) {
                    if (element.getName().equalsIgnoreCase("gzip")) {
                        isGzip = true;//如果判断到浏览器头有gzip,强制采用压缩处理方法，不理会用户设置的isZip值
                    } else {
                        isGzip = false;
                    }
                }
            } else {
                isGzip = false;
            }
            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    if (CheckUtil.isValidate(path)) {
                        FileOutputStream fos = new FileOutputStream(path);
                        InputStream is = entity.getContent();
                        byte[] buffer = new byte[2048];
                        int len = -1;
                        long contentLength = entity.getContentLength();
                        long curPos = 0;
                        while ((len = is.read(buffer)) != -1) {
                            checkIfIsCancelled();
                            if (listener != null) {
                                curPos += len;
                                listener.onProgressUpdate((int) (curPos / 1024),
                                        (int) (contentLength / 1024));
                            }
                            fos.write(buffer, 0, len);
                        }
                        is.close();
                        fos.flush();
                        fos.close();
                        return bindData(path);
                    } else {
                        String returnStr = "";
                        if (!isGzip) {
                            returnStr = EntityUtils.toString(entity, "UTF-8");
                        } else {//以下是解压缩的过程
                            GZIPInputStream gis = new GZIPInputStream(entity.getContent());
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            byte buf[] = new byte[1024];
                            int len;
                            while ((len = gis.read(buf)) != -1) {
                                outputStream.write(buf, 0, len);
                            }
                            outputStream.close();
                            returnStr = new String(outputStream.toByteArray(), HTTP.UTF_8);
                        }
                        Trace.i("http返回:" + returnStr);
                        return bindData(returnStr);
                    }
                default:
                    break;
            }
            return null;
        } catch (FileNotFoundException e) {
            throw new AppException(ExceptionStatus.FileNotFoundException, e.getMessage());
        } catch (IllegalStateException e) {
            throw new AppException(ExceptionStatus.IllegalStateException, e.getMessage());
        } catch (ParseException e) {
            throw new AppException(ExceptionStatus.ParseException, e.getMessage());
        } catch (IOException e) {
            throw new AppException(ExceptionStatus.IOException, e.getMessage());
        }
    }

    public AbstractCallback<T> cache(String path) {
        this.path = path;
        return this;
    }

    public void cancel(boolean force) {
        this.isForceCancelled = force;
        isCancelled = true;
    }

    @Override
    public boolean isForceCancelled() {
        return isForceCancelled;
    }

    @Override
    public boolean onCustomOutput(OutputStream out) throws AppException {
        return false;
    }

}

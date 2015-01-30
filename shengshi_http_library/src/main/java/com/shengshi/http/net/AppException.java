package com.shengshi.http.net;

/**
 * <p>Title:       AppException
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
public class AppException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public enum ExceptionStatus {
        FileNotFoundException, IllegalStateException, ParseException, IOException, CancelException, ServerException, ParameterException, TimeoutException, ParseJsonException, UnsupportedEncodingException, OutOfMemoryError, URISyntaxException
    }

    private ExceptionStatus status;

    public AppException(ExceptionStatus status, String detailMessage) {
        super(detailMessage);
        this.status = status;
    }

    public AppException(ExceptionStatus status, Throwable throwable) {
        super(throwable);
        this.status = status;
    }

    public AppException(ExceptionStatus status, String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        this.status = status;
    }

    public ExceptionStatus getStatus() {
        return status;
    }

}

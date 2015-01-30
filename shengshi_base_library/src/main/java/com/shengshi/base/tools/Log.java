package com.shengshi.base.tools;

/**
 * <p>Title:             日志工具                                               </p>
 * <p>Description:
 * <strong>LOG_LEVEL正试为android.util.Log.ERROR，测试为android.util.Log.DEBUG</strong>
 * </p>
 * <p>@author:       liaodl                 </p>
 * <p>Copyright: Copyright (c) 2014         </p>
 * <p>Company:    @小鱼网                     </p>
 * <p>Create Time:     2014年6月30日                              </p>
 * <p>@author:                              </p>
 * <p>Update Time:                          </p>
 * <p>Updater:                              </p>
 * <p>Update Comments:                      </p>
 */
public class Log {

    protected static final String TAG = "Shengshi_Log";

    /**
     * if the message level less than LOG_LEVEL, no output.
     * <p/>
     * LOG_LEVEL values please see:
     * <p> android.util.Log.VERBOSE </p>
     * <p> android.util.Log.DEBUG </p>
     * <p> android.util.Log.INFO </p>
     * <p> android.util.Log.WARN </p>
     * <p> android.util.Log.ERROR </p>
     * <p/>
     * please change LOG_LEVEL to android.util.Log.INFO when Project release
     */
    protected static int LOG_LEVEL = android.util.Log.ERROR;

    private Log() {
    }

    /**
     * 设置调试模式
     *
     * @param isDebug
     */
    public static void setDebugMode(boolean isDebug) {
        if (isDebug) {
            LOG_LEVEL = android.util.Log.DEBUG;
        } else {
            LOG_LEVEL = android.util.Log.WARN + android.util.Log.ERROR;
        }
    }

    /**
     * Send a VERBOSE log message.
     *
     * @param msg The message you would like logged.
     */
    public static void v(String msg) {
        if (LOG_LEVEL <= android.util.Log.VERBOSE)
            android.util.Log.v(TAG, buildMessage(msg));
    }

    /**
     * Send a VERBOSE log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void v(String msg, Throwable thr) {
        if (LOG_LEVEL <= android.util.Log.VERBOSE)
            android.util.Log.v(TAG, buildMessage(msg), thr);
    }

    /**
     * Send a DEBUG log message.
     *
     * @param msg
     */
    public static void d(String msg) {
        if (LOG_LEVEL <= android.util.Log.DEBUG)
            android.util.Log.d(TAG, buildMessage(msg));
    }

    /**
     * Send a DEBUG log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String msg, Throwable thr) {
        if (LOG_LEVEL <= android.util.Log.DEBUG)
            android.util.Log.d(TAG, buildMessage(msg), thr);
    }

    /**
     * Send an INFO log message.
     *
     * @param msg The message you would like logged.
     */
    public static void i(String msg) {
        if (LOG_LEVEL <= android.util.Log.INFO)
            android.util.Log.i(TAG, buildMessage(msg));
    }

    /**
     * Send a INFO log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void i(String msg, Throwable thr) {
        if (LOG_LEVEL <= android.util.Log.INFO)
            android.util.Log.i(TAG, buildMessage(msg), thr);
    }

    /**
     * Send a WARN log message
     *
     * @param msg The message you would like logged.
     */
    public static void w(String msg) {
        if (LOG_LEVEL <= android.util.Log.WARN)
            android.util.Log.w(TAG, buildMessage(msg));
    }

    /**
     * Send a WARN log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void w(String msg, Throwable thr) {
        if (LOG_LEVEL <= android.util.Log.WARN)
            android.util.Log.w(TAG, buildMessage(msg), thr);
    }

    /**
     * Send an empty WARN log message and log the exception.
     *
     * @param thr An exception to log
     */
    public static void w(Throwable thr) {
        if (LOG_LEVEL <= android.util.Log.WARN)
            android.util.Log.w(TAG, buildMessage(""), thr);
    }

    /**
     * Send an ERROR log message.
     *
     * @param msg The message you would like logged.
     */
    public static void e(String msg) {
        if (LOG_LEVEL <= android.util.Log.ERROR)
            android.util.Log.e(TAG, buildMessage(msg));
    }

    /**
     * Send an ERROR log message and log the exception.
     *
     * @param msg The message you would like logged.
     * @param thr An exception to log
     */
    public static void e(String msg, Throwable thr) {
        if (LOG_LEVEL <= android.util.Log.ERROR)
            android.util.Log.e(TAG, buildMessage(msg), thr);
    }

    /**
     * Building Message
     *
     * @param msg The message you would like logged.
     * @return Message String
     */
    protected static String buildMessage(String msg) {
        StackTraceElement caller = new Throwable().fillInStackTrace().getStackTrace()[2];
        return new StringBuilder().append(caller.getClassName()).append(".")
                .append(caller.getMethodName()).append("(): ").append(msg).toString();
    }
}

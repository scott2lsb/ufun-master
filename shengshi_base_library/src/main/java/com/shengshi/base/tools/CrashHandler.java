package com.shengshi.base.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <p>Title:          全局异常捕获                                       </p>
 * <p>Description:
 * 全局异常捕获输出到SD卡 /shengshi/log 文件夹下，方便离线查看和定位问题
 * 如：Shengshi-2014-06-20_14-57-52-1403247472940.log 文件
 * </p>
 * <p>@author:       liaodl                 </p>
 * <p>Copyright: Copyright (c) 2014         </p>
 * <p>Company:    @小鱼网                     </p>
 * <p>Create Time: 2014年6月20日                  </p>
 * <p>@author:                              </p>
 * <p>Update Time:                          </p>
 * <p>Updater:                              </p>
 * <p>Update Comments:                      </p>
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "Shengshi_CrashHandler";
    public static final String ACTION = "com.shengshi.common.CrashHandler";

    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;
    /**
     * 程序的Context对象
     */
    private Context mContext;
    //系统默认的UncaughtException处理类   
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //用来存储设备信息和异常信息  
    private Map<String, String> infos = new HashMap<String, String>();
    //用于格式化日期,作为日志文件名的一部分  
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
    //日志位置
    private String logPath = null;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
        logPath = Environment.getExternalStorageDirectory() + "/shengshi/" + "log/";
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    /**
     * 注册异常处理
     *
     * @param context
     */
    public void register(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该GlobalExceptionHanlder为程序的默认处理器  
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理  
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "uncaughtException : ", e);
            }
            systemExit();
        }
    }

    /**
     * 退出程序
     */
    private void systemExit() {
        Intent intent = new Intent();
        intent.setAction(CrashHandler.ACTION);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 检查SD卡是否可用
     *
     * @return
     */
    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }

//        使用Toast来显示异常信息  
//        new Thread() {  
//            @Override  
//            public void run() {  
//                Looper.prepare();  
//                Toast.makeText(mContext, R.string.global_exception_tips, Toast.LENGTH_LONG).show();  
//                Looper.loop();  
//            }  
//        }.start();

        //收集设备参数信息   
        collectDeviceInfo(mContext);
        //保存日志文件   
        saveCrashInfo2File(ex);

        //分类处理异常
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        Log.e(TAG, result);//此处如果改成Log.d()；有些手机异常会打印不出来
        FileOutputStream fos = null;
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "Shengshi-" + time + "-" + timestamp + ".log";
            if (checkSDCard()) {
                File dir = new File(logPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                fos = new FileOutputStream(logPath + fileName);
                fos.write(sb.toString().getBytes());
                //UMeng 异常统计
                MobclickAgent.reportError(mContext, sb.toString());
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "an error occured while close file...", e);
            }
        }
        return null;
    }

    /**
     * 删除所有的日志文件
     */
    public void clearAllLogs(final Context ctx) {
        if (checkSDCard()) {
            File file = new File(logPath);
            if (file != null && file.exists()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
        }
    }

    /**
     * 异步删除所有日志文件
     *
     * @param ctx
     */
    public void asynClearAllLogs(final Context ctx) {
        new Thread() {
            public void run() {
                clearAllLogs(ctx);
            }
        }.start();
    }

//	/** 
//	* 保存错误信息到文件中 
//	* @param ex 
//	* @return 
//	*/
//	private String getCrashLog(Throwable ex) {
//		Writer info = new StringWriter();
//		PrintWriter printWriter = new PrintWriter(info);
//		ex.printStackTrace(printWriter);
//		Throwable cause = ex.getCause();
//		while (cause != null) {
//			cause.printStackTrace(printWriter);
//			cause = cause.getCause();
//		}
//		String result = info.toString();
//		printWriter.close();
//		StringBuilder builder = new StringBuilder();
//		builder.append("EXEPTION").append(ex.getLocalizedMessage()).append("STACK_TRACE")
//				.append(result);
//		return builder.toString();
//	}
//
//	private void sendEmail(String content) {
//		Intent data = new Intent(Intent.ACTION_SENDTO);
//		data.setData(Uri.parse("mailto:chenyh@shengshi.com.cn"));
//		data.putExtra(Intent.EXTRA_SUBJECT, "程序不小心出了一些问题，感谢反馈");
//		data.putExtra(Intent.EXTRA_TEXT, content);
//		data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		this.mContext.startActivity(data);
//	}

}

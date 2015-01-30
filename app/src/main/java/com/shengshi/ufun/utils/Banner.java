package com.shengshi.ufun.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Banner {
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private Context mContext;
    private Object lock = new Object();
    private boolean mAllowLoad = true;
    private String filename = "fish_banner.jpg";
    private String tmpfile = "tmp_file.jpg";

    public Banner(Context context) {
        mContext = context;
    }

    public Drawable loadDrawable(int cityid, final String url) {
        if (cityid == 0) {
            cityid = 63;
        }
        filename = cityid + "_" + filename;
        final String imgurl = url + "/mbanner.html?cityid=" + cityid;
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (!mAllowLoad) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                String md5 = "";
                if (file_exist(filename)) {
                    md5 = md5(filename);
                }
                download(imgurl, md5);
            }
        });
        if (file_exist(filename)) {
            return Drawable.createFromPath(mContext.getFilesDir().getAbsolutePath() + "/"
                    + filename);
        }
        return null;
    }

    private boolean file_exist(String path) {
        File dir = mContext.getFilesDir();
        String fp = dir.getAbsolutePath() + "/" + path;
        File file = new File(fp);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private String md5(String sfile) {
        File file = new File(mContext.getFilesDir().getAbsoluteFile() + "/" + sfile);
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static String md5(String pic, File mfile) {
        String imgPath = mfile.getAbsolutePath() + "/" + pic;
        File file = new File(imgPath);
        if (file != null && !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    private void download(String url, String md5) {
        if (md5 != null) {
            url = url + "&md5=" + md5;
        }
        int state = 0;
        InputStream is = null;
        OutputStream os = null;
        String ret = null;
        ByteArrayOutputStream ots = new ByteArrayOutputStream();
        try {
            is = new URL(url).openStream();
            os = mContext.openFileOutput(tmpfile, Context.MODE_PRIVATE);
            byte[] buf = new byte[4096];
            int len;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
                ots.write(buf, 0, len);
                state = state + len;
            }
            ret = new String(ots.toByteArray(), "ISO-8859-1");
            is.close();
            ots.close();
            ots = null;
            buf = null;
            is = null;
            os.close();
            os = null;
        } catch (Exception e) {
            // e.printStackTrace();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            state = 0;
        }
        if (state == 0) {
            mContext.deleteFile(tmpfile);
        } else {
            if (file_exist(filename)) {
                mContext.deleteFile(filename);
            }
            if (ret.endsWith("delete")) {
                mContext.deleteFile(tmpfile);
            } else {
                File file = new File(mContext.getFilesDir().getAbsoluteFile() + "/" + tmpfile);
                file.renameTo(new File(mContext.getFilesDir().getAbsoluteFile() + "/" + filename));
            }
        }
    }

}

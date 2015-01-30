package com.shengshi.ufun.photoselect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.shengshi.base.tools.SdCardTool;
import com.shengshi.base.tools.SharedPreferencesUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageTools {

    /**
     * 保存图片到相册
     *
     * @param photoBitmap
     * @param photoName
     * @return
     */
    public static String savePhotoToSDCard(Bitmap photoBitmap, String photoName) {
        String path = Environment.getExternalStorageDirectory() + "/DCIM/Camera";
        return savePhotoToSDCard(photoBitmap, path, photoName);
    }

    /**
     * Save image to the SD card
     *
     * @param photoBitmap
     * @param path        如 本地相册：Environment.getExternalStorageDirectory() + "/DCIM/Camera"
     * @param photoName   传入图片地址Url，判断是否重复保存
     * @return
     */
    public static String savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName) {
        String filePath = "";
        if (SdCardTool.isMounted()) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            photoName = photoName.substring(photoName.lastIndexOf("/") + 1);
            File photoFile = new File(path, photoName);
            if (photoFile.exists()) {
                return photoFile.getPath();
            }
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                        filePath = photoFile.toString();
                        photoBitmap.recycle();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                photoBitmap.recycle();
                e.printStackTrace();
            } catch (IOException e) {
                photoBitmap.recycle();
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                    photoBitmap.recycle();
                } catch (IOException e) {
                    e.printStackTrace();
                    photoBitmap.recycle();
                }
            }
        }
        return filePath;
    }

    /**
     * 扫描、刷新相册
     */
    public static void scanPhotos(String filePath, Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 保存是否压缩标志
     *
     * @param isCompress
     */
    public static void saveCompressFlag(Context context, boolean isCompress) {
        SharedPreferencesUtil.setBoolean(context, "choice_pics_iscompress", isCompress);
    }

    /**
     * 获取压缩标志
     *
     * @param context
     * @return
     */
    public static boolean getCompressFlag(Context context) {
        return SharedPreferencesUtil.getBoolean(context, "choice_pics_iscompress");
    }

}

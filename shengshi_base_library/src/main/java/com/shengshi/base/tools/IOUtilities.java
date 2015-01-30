package com.shengshi.base.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOUtilities {

    public static final int IO_BUFFER_SIZE = 4 * 1024;

    /**
     * Copy the content of the input stream into the output stream, using a
     * temporary byte array buffer whose size is defined by
     * {@link #IO_BUFFER_SIZE}.
     *
     * @param in  The input stream to copy from.
     * @param out The output stream to copy to.
     * @throws java.io.IOException If any error occurs during the copy.
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    public static byte[] readStreamToMemory(InputStream in) {
        if (in == null) {
            return null;
        }
        ByteArrayOutputStream out = null;
        byte[] result = null;
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            out = new ByteArrayOutputStream();
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            result = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    // public static String copyAttachmentToPackage(String path, String
    // attachmentType) {
    // try {
    // String packagePath =
    // ImageLoader.getInstance().getDiscCache().get(path).getAbsolutePath();
    // if (Utils.isImage(attachmentType)) {
    // Bitmap bitmap = BitmapUtil.loadBitmap(path, 480, 640, true);
    // if (bitmap == null) {
    // return path;
    // } else {
    // BitmapUtil.compressBitmap(packagePath, bitmap, 50, 2*1024*1024l);
    // }
    // }else {
    // // copy to
    // if (packagePath.equals(path)) {
    // return path;
    // }
    // copyFile(path, packagePath);
    // }
    // return packagePath;
    // } catch (IOException e) {
    // e.printStackTrace();
    // Trace.e(e.toString());
    // return path;
    // }
    // }

    public static void copyFile(String from, String dest) throws IOException {
        FileInputStream fis = new FileInputStream(new File(from));
        FileOutputStream fos = new FileOutputStream(new File(dest));
        copy(fis, fos);
    }

}

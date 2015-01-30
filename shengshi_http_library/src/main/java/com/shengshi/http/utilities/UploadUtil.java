package com.shengshi.http.utilities;

import com.shengshi.http.net.AppException;
import com.shengshi.http.net.AppException.ExceptionStatus;
import com.shengshi.http.net.FileEntity;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * <p>Title:     UploadUtil
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
public class UploadUtil {

    public static final String lineEnd = "\r\n";
    public static final String twoHyphens = "--";
    public static final String boundary = "*****";
    private static final int IO_BUFFER_SIZE = 1024 * 4;

    /**
     * @param out
     * @param filePath
     * @throws AppException
     */
    public static void upload(OutputStream out, String filePath) throws AppException {
        String BOUNDARY = "7d4a6d158c9"; // 数据分隔线
        DataOutputStream outStream = new DataOutputStream(out);
        try {
            outStream.writeBytes("--" + BOUNDARY + "\r\n");
            outStream
                    .writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                            + filePath.substring(filePath.lastIndexOf("/") + 1) + "\"" + "\r\n");
            outStream.writeBytes("\r\n");
            byte[] buffer = new byte[1024];
            FileInputStream fis = new FileInputStream(filePath);
            while (fis.read(buffer, 0, 1024) != -1) {
                outStream.write(buffer, 0, buffer.length);
            }
            fis.close();
            outStream.write("\r\n".getBytes());
            byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
            outStream.write(end_data);
            outStream.flush();
        } catch (FileNotFoundException e) {
            throw new AppException(ExceptionStatus.IOException, e);
        } catch (IOException e) {
            throw new AppException(ExceptionStatus.IOException, e);
        }
    }

    /**
     * @param out
     * @param postContent
     * @param entities
     */
    public static void upload(OutputStream out, String postContent, ArrayList<FileEntity> entities) {
        String BOUNDARY = "7d4a6d158c9"; // 数据分隔线
        String PREFIX = "--", LINEND = "\r\n";
        String CHARSET = "UTF-8";
        DataOutputStream outStream = new DataOutputStream(out);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINEND);
            sb.append("Content-Disposition: form-data; name=\"" + "data" + "\"" + LINEND);
            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
            sb.append(LINEND);
//			post content
            sb.append(postContent);
            sb.append(LINEND);
            outStream.write(sb.toString().getBytes());
            int i = 0;
            for (FileEntity entity : entities) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"file" + (i++)
                        + "\"; filename=\"" + entity.getFileName() + "\"" + LINEND);
                sb1.append("Content-Type: " + entity.getFileType() + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());

                InputStream is = new FileInputStream(entity.getFilePath());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 小鱼平台协议 上传多张图片
     *
     * @param dos
     * @param params
     * @throws AppException
     */
    public static void upload(DataOutputStream dos, Map<String, Object> params) throws AppException {
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                writeObject(dos, entry.getKey(), entry.getValue());
            }
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            dos.flush();
        } catch (IOException e) {
            throw new AppException(ExceptionStatus.IOException, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void writeObject(DataOutputStream dos, String name, Object obj) throws AppException {

        if (obj == null)
            return;

        try {
            if (obj instanceof Map<?, ?>) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) obj).entrySet()) {
                    writeObject(dos, name, entry.getKey(), entry.getValue());
//					writeObject(dos, name, entry.getValue());//递归
                }
            } else if (obj instanceof File) {
                File file = (File) obj;
                writeData(dos, name, file.getName(), new FileInputStream(file));
            } else if (obj instanceof byte[]) {
                writeData(dos, name, name, new ByteArrayInputStream((byte[]) obj));
            } else if (obj instanceof InputStream) {
                writeData(dos, name, name, (InputStream) obj);
            } else {
                writeField(dos, name, obj.toString());
            }
        } catch (FileNotFoundException e) {
            throw new AppException(ExceptionStatus.FileNotFoundException, e);
        }

    }


    private static void writeObject(DataOutputStream dos, String name, String filename, Object obj) throws AppException {

        if (obj == null)
            return;

        try {
            if (obj instanceof File) {
                File file = (File) obj;
                writeData(dos, name, file.getName(), new FileInputStream(file));
            } else if (obj instanceof byte[]) {
                writeData(dos, name, filename, new ByteArrayInputStream((byte[]) obj));
            } else if (obj instanceof InputStream) {
                writeData(dos, name, filename, (InputStream) obj);
            } else {
                writeField(dos, name, obj.toString());
            }
        } catch (FileNotFoundException e) {
            throw new AppException(ExceptionStatus.FileNotFoundException, e);
        }
    }


    private static void writeData_bak(DataOutputStream dos, String name, String filename, InputStream in)
            throws AppException {

        try {
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\";"
                    + " filename=\"" + filename + "\"" + lineEnd);

            //added to specify type
            dos.writeBytes("Content-Type: application/octet-stream");
            dos.writeBytes(lineEnd);
            dos.writeBytes("Content-Transfer-Encoding: binary");
            dos.writeBytes(lineEnd);

            dos.writeBytes(lineEnd);

//			byte[] buffer = new byte[IO_BUFFER_SIZE];
//			int len;
//			while ((len = in.read(buffer)) != -1) {
//				dos.write(buffer, 0, len);//FIXME 超大文件，会报outOfMemoryError
//			}

            int bytesAvailable = in.available();
            int bufferSize = Math.min(bytesAvailable, IO_BUFFER_SIZE);
            byte[] buffer = new byte[bufferSize];

            // Read file
            int bytesRead = in.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = in.available();
                bufferSize = Math.min(bytesAvailable, IO_BUFFER_SIZE);
                bytesRead = in.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
        } catch (OutOfMemoryError e) {
            throw new AppException(ExceptionStatus.OutOfMemoryError, e);
        } catch (IOException e) {
            throw new AppException(ExceptionStatus.IOException, e);
        }

    }

    protected static void writeData(DataOutputStream dos, String name, String filename, InputStream in)
            throws AppException {

        try {
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\";"
                    + " filename=\"" + filename + "\"" + lineEnd);

            //added to specify type
            dos.writeBytes("Content-Type: application/octet-stream");
            dos.writeBytes(lineEnd);
            dos.writeBytes("Content-Transfer-Encoding: binary");
            dos.writeBytes(lineEnd);

            dos.writeBytes(lineEnd);

            byte[] buffer = new byte[IO_BUFFER_SIZE];
            int len;
            while ((len = in.read(buffer)) != -1) {
                dos.write(buffer, 0, len);//FIXME 超大文件，会报outOfMemoryError
            }

            dos.writeBytes(lineEnd);
        } catch (IOException e) {
            throw new AppException(ExceptionStatus.IOException, e);
        }

    }

    private static void writeField(DataOutputStream dos, String name, String value)
            throws AppException {
        try {
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"");
            dos.writeBytes(lineEnd);
            dos.writeBytes(lineEnd);

            byte[] data = value.getBytes("UTF-8");
            dos.write(data);

            dos.writeBytes(lineEnd);
        } catch (Exception e) {
            throw new AppException(ExceptionStatus.IOException, e);
        }
    }

}

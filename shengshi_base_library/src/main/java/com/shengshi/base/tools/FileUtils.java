package com.shengshi.base.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

/**
 * <p>Title:       File工具类
 * <p>Description:
 * 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
 * <p>包括
 * <p>1、序列化对象到硬盘
 * <p>2、读取硬盘序列化对象
 * <p>3、删除一个文件，或者一个目录下的所有文件
 * <p>4、清除过期的图片
 * <p>5、获取一个在.shengshi文件夹下的文件对象
 * <p>6、将一个文件复制到另外一个文件中
 * <p>7、获取文件目录大小
 * <p>8、删除文件
 * <p>9、把文件转为byte[]
 * <p>10、保存字符串
 * <p>11、保存图片到指定路径
 * <p/>
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-8
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public final class FileUtils {
    public static final String ROOT_DIR_NAME = "/.shengshi";
    public static final String FOLDER_NAME_DRAFTS = "drafts";
    public static final long EXPIRE = 648000000;

    /**
     * 缓存失效时间
     */
    private static final int CACHE_TIME = 60 * 60000;

    /**
     * 读取assets里的文件，转成String
     */
    public static String readAssetsFile(final Context ctx, String fileName) {
        String str = null;
        try {
            InputStream inputStream = ctx.getAssets().open(fileName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte buf[] = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
            str = outputStream.toString();
        } catch (IOException e) {
        }
        return str;
    }

    /**
     * 删除一个文件，或者一个目录下的所有文件
     *
     * @param file 文件或者目录
     */
    public static boolean deleteDir(File file) {
        try {
            if (file == null || !file.exists()) {
                return true;
            }
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                if (files.length > 0) {
                    for (File child : files) {
                        deleteDir(child);
                    }
                }
                if (file.listFiles().length == 0) {
                    file.delete();
                }
            } else if (file.isFile()) {
                return file.delete();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

//	/**
//	 * 清除过期的图片
//	 */
//	public static void clearExpiredImage() {
//		try {
//			File dir = getDir(null);
//			if (dir != null && dir.exists() && dir.isDirectory()) {
//				String[] files = dir.list();
//				for (String fileName : files) {
//					File file = new File(dir, fileName);
//					if (file != null && file.exists() && file.isFile()) {
//						long modified = file.lastModified();
//						long distance = System.currentTimeMillis() - modified;
//						if (distance > EXPIRE) {
//							file.delete();
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

    /**
     * 获取一个在.shengshi文件夹下的文件对象
     *
     * @param fileName 文件路径以及名字 可以携带目录，如"filedir1/filedir2/a.apk" 但是，注意，字符串中的"/"仅代表
     *                 {@link File#separator} <br/>
     *                 如果没有带目录，默认为缓存图片目录下
     * @return
     */
    public static File getFile(String fileName) {
        String dir = null;
        int index = fileName.lastIndexOf(File.separator);
        if (index != -1) {
            dir = fileName.substring(0, index);
            fileName = fileName.substring(index + 1, fileName.length());
        }
        if (dir != null && TextUtils.isEmpty(dir.trim())) {
            dir = null;
        }
        File dirFile = getDir(dir);
        File file = new File(dirFile, "." + fileName);
        return file;
    }

    /**
     * 获取一个在.shengshi文件夹下的文件对象
     *
     * @param dir      文件夹路径，可以为多级 如:"filedir1/filedir2".如果为null，默认为缓存图片目录下
     * @param fileName 文件名字
     * @return
     */
    public static File getFile(String dir, String fileName) {
        File dirFile = getDir(dir);
        File file = new File(dirFile, "." + fileName);
        return file;
    }

    /**
     * @param dir 目录的名称 如果dir为空，则返回 缓存文件路径下的图片文件路径
     * @return sdcard/.shengshi/ 目录
     */
    public static File getDir(String dir) {
        StringBuffer path = null;
        File file = null;
        if (SdCardTool.isMounted()) {
            path = new StringBuffer(Environment.getExternalStorageDirectory().getAbsolutePath());
            path.append(ROOT_DIR_NAME);
            if (dir == null) {
                dir = "images";
            }
            path.append(File.separatorChar);
            path.append(".").append(dir);
            file = new File(path.toString());
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        return file;
    }

//	/**
//	 * 草稿箱目录的子目录
//	 * 
//	 * @param dirName
//	 *            ,数据库的id
//	 * @param fileName
//	 *            ,文件名
//	 * @return
//	 */
//	public static File getDraftFile(final String fileName) {
//		// 草稿箱的文件
//		File file = new File(getDir(FOLDER_NAME_DRAFTS), "." + fileName);
//		return file;
//	}

//	public static File getDownloadDir() {
//		StringBuffer path = null;
//		File file = null;
//		if (SdCardTool.isMounted()) {
//			path = new StringBuffer(Environment.getExternalStorageDirectory().getAbsolutePath());
//			path.append("/Download");
//			path.append(File.separatorChar);
//			path.append("fish");
//			file = new File(path.toString());
//			if (!file.exists()) {
//				file.mkdirs();
//			}
//		}
//		return file;
//	}

//	/*
//	 * 得到图片文件夹
//	 */
//	public static File getPicturesDir() {
//		StringBuffer path = null;
//		File file = null;
//		if (SdCardTool.isMounted()) {
//			path = new StringBuffer(Environment.getExternalStorageDirectory().getAbsolutePath());
//			path.append("/Pictures");
//			path.append(File.separatorChar);
//			path.append("fish");
//			file = new File(path.toString());
//			if (!file.exists()) {
//				file.mkdirs();
//			}
//		}
//		return file;
//	}

//	/**
//	 * /** 检查一个目录下有没有包含这个key的文件
//	 * 
//	 * @param file
//	 * @param key
//	 * @param ignoreCase
//	 * @return
//	 */
//	public static boolean dirContainsFileNameKey(File file, String key, boolean ignoreCase) {
//		if (ignoreCase) {
//			key = key.toLowerCase(Locale.getDefault());
//		}
//		if (file != null && file.exists() && file.isDirectory()) {
//			File[] files = file.listFiles();
//			int i = 0;
//			while (files != null && files.length > i) {
//				File temp = files[i];
//				if (temp == null || !temp.exists()) {
//					continue;
//				}
//				String tempName = null;
//				tempName = temp.getName();
//				if (ignoreCase) {
//					tempName = tempName.toLowerCase(Locale.getDefault());
//				}
//				if (tempName.contains(key)) {
//					return true;
//				}
//				if (temp.isDirectory()) {
//					if (dirContainsFileNameKey(temp, key, ignoreCase)) {
//						return true;
//					}
//				}
//				i++;
//			}
//		}
//		return false;
//	}

//	/**
//	 * 检查一个目录下
//	 * 
//	 * @param file
//	 * @param key
//	 * @param ignoreCase
//	 * @return
//	 */
//	public static List<File> dirContainsList(File file, String key, boolean ignoreCase) {
//		List<File> result = new ArrayList<File>();
//		if (ignoreCase) {
//			key = key.toLowerCase(Locale.getDefault());
//		}
//		if (file != null && file.exists() && file.isDirectory()) {
//			File[] files = file.listFiles();
//			int i = 0;
//			while (files != null && files.length > i) {
//				File temp = files[i];
//				if (temp == null || !temp.exists()) {
//					continue;
//				}
//				String tempName = null;
//				tempName = temp.getName();
//				if (ignoreCase) {
//					tempName = tempName.toLowerCase(Locale.getDefault());
//				}
//				if (tempName.contains(key)) {
//					result.add(temp);
//				}
//				if (temp.isDirectory()) {
//					result.addAll(dirContainsList(temp, key, ignoreCase));
//				}
//				i++;
//			}
//		}
//		return result;
//	}

//	/**
//	 * 把一个uri地址中的.和：切换成_生成对应的文件名字
//	 * 
//	 * @param uri
//	 * @return 返回/后的名字，如果异常，则返回Null
//	 */
//	public static String getLongFileNameFromUri(String uri) {
//		if (uri == null || "".equals(uri)) {
//			return null;
//		}
//		String fileName = uri.replaceAll("/", "_");
//		fileName = fileName.replaceAll(" +", "");
//		fileName = fileName.replaceAll(":", "_");
//		fileName = fileName.replaceAll("\\.", "_");
//		fileName = fileName.replaceAll("\\?", "_");
//		fileName = fileName.replaceAll("_+", "_");
//		return fileName;
//	}


//	// 取得文件大小
//	public static long getFileSizes(File f) {
//		long s = 0;
//		if (f.exists()) {
//			FileInputStream fis = null;
//			try {
//				fis = new FileInputStream(f);
//				s = fis.available();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} else {
//			Log.i("文件不存在");
//		}
//		return s;
//	}

//	/**
//	 * 获取一个文件，或者一个目录下的所有文件
//	 * @param file 文件或者目录
//	 */
//	public static String getFileLength() {
//		String formetFileSize = "0KB";
//		if (SdCardTool.isMounted()) {
//			File file = getDir(null);
//			long fileSize = 0;
//			if (file.exists()) {
//				fileSize = getFileSize(file);
//				formetFileSize = FormetFileSize(fileSize);
//			}
//			if (formetFileSize.equals("0.00B")) {
//				formetFileSize = "0KB";
//			}
//			return formetFileSize;
//		} else {
//			return formetFileSize;
//		}
//	}

//	// 递归 取得文件夹大小
//	public static long getFileSize(File f) {
//		if (!SdCardTool.isMounted()) {
//			return 0;
//		}
//		long size = 0;
//		File flist[] = f.listFiles();
//		for (int i = 0; i < flist.length; i++) {
//			if (flist[i].isDirectory()) {
//				size = size + getFileSize(flist[i]);
//			} else {
//				size = size + flist[i].length();
//			}
//		}
//		return size;
//	}

//	// 递归求取目录文件个数
//	public static long getlist(File f) {
//		long size = 0;
//		File flist[] = f.listFiles();
//		size = flist.length;
//		for (int i = 0; i < flist.length; i++) {
//			if (flist[i].isDirectory()) {
//				size = size + getlist(flist[i]);
//				size--;
//			}
//		}
//		return size;
//	}

    /**
     * Serialize the object
     */
    public static boolean saveObject(Serializable ser, String fileName, Context context) {
        return write(context, fileName, ser);
    }

    /**
     * Serialize the object
     */
    public static boolean write(Context context, String fileName, Object obj) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            out.writeObject(obj);
            out.flush();
            return true;
        } catch (FileNotFoundException e) {
            Log.w("FileNotFoundException error, message:" + e.getMessage(), e);
        } catch (IOException e) {
            Log.e("IOException error, message:" + e.getMessage(), e);
        } finally {
            StreamUtil.closeSilently(out);
        }
        return false;
    }

    /**
     * Serialize the object
     */
    public static boolean write(Context context, String fileName, Serializable ser) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            out.writeObject(ser);
            out.flush();
            return true;
        } catch (FileNotFoundException e) {
            Log.w("FileNotFoundException error, message:" + e.getMessage(), e);
        } catch (IOException e) {
            Log.e("IOException error, message:" + e.getMessage(), e);
        } finally {
            StreamUtil.closeSilently(out);
        }
        return false;
    }

    /**
     * Deserialize the Object
     */
    public static Serializable readObject(String fileName, Context context) {
        if (!isExistDataCache(fileName, context))
            return null;
        return (Serializable) read(context, fileName);
    }

    /**
     * Deserialize the Object
     */
    public static Object read(Context context, String fileName) {
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(context.openFileInput(fileName));
            return input.readObject();
        } catch (StreamCorruptedException e) {
            Log.e("error message:" + e.getMessage(), e);
        } catch (FileNotFoundException e) {
            Log.w("error message:" + e.getMessage(), e);
        } catch (IOException e) {
            Log.e("error message:" + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            Log.e("error message:" + e.getMessage(), e);
        } catch (Exception e) {
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = context.getFileStreamPath(fileName);
                data.delete();
            }
        } finally {
            StreamUtil.closeSilently(input);
        }

        return null;
    }

    /**
     * 判断缓存是否存在
     *
     * @param cachefile
     * @return
     */
    private static boolean isExistDataCache(String cachefile, Context context) {
        boolean exist = false;
        File data = context.getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    /**
     * 删除对象
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static void delObject(String file, Context context) {
        if (isExistDataCache(file, context)) {
            File data = context.getFileStreamPath(file);
            data.delete();
        }
    }

    /**
     * 判断缓存数据是否可读
     *
     * @param cachefile
     * @return
     */
    @SuppressWarnings("unused")
    private static boolean isReadDataCache(String cachefile, Context context) {
        return readObject(cachefile, context) != null;
    }

    /**
     * 判断缓存是否失效
     *
     * @param cachefile cachefiletime
     * @return
     */
    public static boolean isCacheDataFailure(String cachefile, int cachefiletime, Context context) {
        boolean failure = false;
        cachefiletime = cachefiletime * 60000;
        File data = context.getFileStreamPath(cachefile);
        if (data.exists() && (System.currentTimeMillis() - data.lastModified()) > cachefiletime)
            failure = true;
        else if (!data.exists())
            failure = true;
        return failure;
    }

    /**
     * 判断缓存是否失效
     *
     * @param cachefile
     * @return
     */
    public static boolean isCacheDataFailure(String cachefile, Context context) {
        boolean failure = false;
        File data = context.getFileStreamPath(cachefile);
        if (data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
            failure = true;
        else if (!data.exists())
            failure = true;
        return failure;
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 保存字符串
     *
     * @param str
     * @param logPath
     * @param fileName
     * @return
     */
    public static String saveString(String str, String logPath, String fileName) {
        FileOutputStream fos = null;
        try {
            if (SdCardTool.isMounted()) {
                File dir = new File(logPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                fos = new FileOutputStream(logPath + fileName);
                fos.write(str.toString().getBytes());
            }
            return fileName;
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        } finally {
            StreamUtil.closeSilently(fos);
        }
        return null;
    }

    /**
     * delete cache file
     */
    public static boolean deleteFile(Context context, String fileName) {
        return context.deleteFile(fileName);
    }

    /**
     * 把文件转为byte[]
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        // 获取文件大小
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // 文件太大，无法读取
            is.close();
            throw new IOException("File is to large " + file.getName());
        }
        // 创建一个数据来保存文件数据
        byte[] bytes = new byte[(int) length];
        // 读取数据到byte数组中
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }
        // 确保所有数据均被读取
        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /**
     * 将一个文件复制到另外一个文件中
     *
     * @param src 源文件
     * @param tar 目标文件，即最开始的空文件
     * @return
     */
    public static boolean copyFile(File src, File tar) {
        boolean result = false;
        if (src.exists() && src.isFile() && tar != null) {
            FileInputStream read = null;
            FileOutputStream out = null;
            if (tar.exists()) {
                deleteDir(tar);// 如果存在则删除
            }
            try {
                read = new FileInputStream(src);
                out = new FileOutputStream(tar);
                byte[] bts = new byte[1024];
                int lenth = 0;
                while ((lenth = read.read(bts)) != -1) {
                    out.write(bts, 0, lenth);
                }
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                StreamUtil.closeSilently(read);
                StreamUtil.closeSilently(out);
            }
        }
        return result;
    }

    /**
     * copy file
     */
    public static File copyFile(File file, String dir, String fileName) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return writeInputStream(in, dir, fileName);
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException error message:" + e.getMessage(), e);
            StreamUtil.closeSilently(in);
        }
        return null;
    }

    public static File writeInputStream(final InputStream in, final String dir, final String fileName) {
        if (in == null)
            return null;
        String absolutePath = dir;
        File f = new File(absolutePath);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                Log.e("mkdirs error:" + absolutePath);
            }
        }
        File mf = new File(absolutePath + "/" + fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mf);
            byte bt[] = new byte[512];
            int n = -1;
            while (true) {
                n = in.read(bt);
                if (n <= 0)
                    break;
                out.write(bt, 0, n);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException error message:" + e.getMessage(), e);
        } catch (IOException e) {
            Log.e("IOException error desc:" + e.getMessage(), e);
        } finally {
            absolutePath = null;
            StreamUtil.closeSilently(out);
            StreamUtil.closeSilently(in);
        }
        return mf;
    }

    /**
     * save bitmap to the sdcard
     * dir "/mnt/sdcard/temp/"
     * fileName "20111020163433.jpg"
     */
    public static File saveBitmap(final Bitmap bitmap, String dir, String fileName) {
        if (bitmap == null)
            return null;

        String absolutePath = dir;
        Log.d("absolutePath " + absolutePath);

        File f = new File(absolutePath);
        if (!f.exists()) {
            if (!f.mkdirs()) {
                Log.e("mkdirs error:" + absolutePath);
            }
        }

        File mf = new File(absolutePath + "/" + fileName);
        OutputStream outputStream = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
        byte[] jpegData = out.toByteArray();

        try {
            outputStream = new FileOutputStream(mf);
            outputStream.write(jpegData);
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException error message:" + e.getMessage(), e);
        } catch (IOException e) {
            Log.e("IOException error desc:" + e.getMessage(), e);
        } finally {
            StreamUtil.closeSilently(outputStream);
        }
        return mf;
    }
}

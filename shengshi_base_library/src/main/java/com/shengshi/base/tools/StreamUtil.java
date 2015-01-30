package com.shengshi.base.tools;

import java.io.Closeable;

public final class StreamUtil {

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            Log.e("关闭流异常", t);
        }
    }
}

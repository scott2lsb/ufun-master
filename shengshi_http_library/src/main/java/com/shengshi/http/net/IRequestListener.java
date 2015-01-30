package com.shengshi.http.net;

/**
 * <p>Title:      IRequestListener
 * <p>Description:   下载进度监听
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-9-30
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public interface IRequestListener {
    void onProgressUpdate(int curPos, int contentLength);
}

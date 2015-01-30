package com.shengshi.http.net.callback;

/**
 * <p>Title:     FileCallback
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
public abstract class FileCallback extends AbstractCallback<String> {

    @Override
    public String bindData(String content) {
        return content;
    }
}

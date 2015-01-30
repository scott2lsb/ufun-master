package com.shengshi.http.net;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:   RequestManager
 * <p>Description:    用于绑定Activity,  Activity退出,请求后续动作取消
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-9-30
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RequestManager {
    private static RequestManager mInstance;
    private HashMap<String, ArrayList<Request>> mRequestCache = null;

    private RequestManager() {
        mRequestCache = new HashMap<String, ArrayList<Request>>();
    }

    public static RequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new RequestManager();
        }
        return mInstance;
    }

    public void cancel(String key) {
        ArrayList<Request> requests = mRequestCache.get(key);
        if (requests != null && requests.size() > 0) {
            for (Request request : requests) {
                request.cancel(true);
            }
        }
    }

    public void cancelAll() {
    }

    public void execute(String key, Request request) {
        ArrayList<Request> requests = mRequestCache.get(key);
        if (requests == null) {
            requests = new ArrayList<Request>();
            mRequestCache.put(key, requests);
        }
        requests.add(request);
        request.execute();
    }

}

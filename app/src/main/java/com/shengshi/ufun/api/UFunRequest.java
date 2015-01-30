package com.shengshi.ufun.api;

import android.content.Context;

import com.shengshi.base.tools.Log;
import com.shengshi.http.net.Request;
import com.shengshi.ufun.utils.UFunTool;

/**
 * <p>Title:        封装所有的http请求
 * <p>Description:
 * <p>@author:  dengbw
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-26
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class UFunRequest extends Request {

    private Context mContext;

    public UFunRequest(Context context, String url) {
        super(url);
        init(context);
    }

    public UFunRequest(Context context, String url, RequestMethod method) {
        super(url, method);
        init(context);
    }

    public UFunRequest(Context context, String url, RequestTool tool) {
        super(url, tool);
        init(context);
    }

    public UFunRequest(Context context, String url, RequestMethod method, RequestTool tool) {
        super(url, method, tool);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        isZip = true;
        setdebug(UFunTool.isDebug(mContext));
    }

    public void execute() {
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        String encryptCode = encryptInfo.encryptCode();
        super.addParameter("mkey", encryptCode);
        super.execute();
        Log.i(encryptInfo.toString() + "\n最终加密mkey=" + encryptCode);
    }

}

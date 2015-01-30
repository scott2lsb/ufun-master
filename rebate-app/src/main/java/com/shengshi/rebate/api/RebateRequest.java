package com.shengshi.rebate.api;

import android.content.Context;

import com.shengshi.base.tools.Log;
import com.shengshi.http.net.Request;
import com.shengshi.rebate.utils.RebateTool;

/**
 * <p>Title:        封装返利卡所有的http请求
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-20
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateRequest extends Request {

    private Context mContext;

    public RebateRequest(Context context, String url) {
        super(url);
        init(context);
    }

    public RebateRequest(Context context, String url, RequestMethod method) {
        super(url, method);
        init(context);
    }

    public RebateRequest(Context context, String url, RequestTool tool) {
        super(url, tool);
        init(context);
    }

    public RebateRequest(Context context, String url, RequestMethod method, RequestTool tool) {
        super(url, method, tool);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setdebug(RebateTool.isDebug(mContext));
    }

    public void execute() {
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        String encryptCode = encryptInfo.encryptCode();
        super.addParameter("mkey", encryptCode);
        super.execute();
        Log.i(encryptInfo.toString() + "\n最终加密key=" + encryptCode);
    }

}

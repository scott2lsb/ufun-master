package com.shengshi.rebate.ui;

import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.config.RebateUrls;

/**
 * <p>Title:    待支付列表
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-5
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class NoPayRebateActivity extends MyRebateActivity {

    @Override
    protected void initComponents() {
        super.initComponents();
    }

    @Override
    public String getTopTitle() {
        return "待支付列表";
    }

    @Override
    public void requestUrl(int page) {
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.order.getOrderList");
        encryptInfo.resetParams();
        encryptInfo.putParam("status", 0);
        encryptInfo.putParam("page", page);
        encryptInfo.putParam("page_size", 20);
        request.setCallback(jsonCallback);
        request.execute();
    }

}

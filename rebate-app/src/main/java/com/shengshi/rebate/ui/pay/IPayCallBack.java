package com.shengshi.rebate.ui.pay;

/**
 * <p>Title:      支付回调监听
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-18
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public interface IPayCallBack {

    void onPaySuccess();

    void onPayFailed();
}

package com.shengshi.rebate.ui.pay;

import com.shengshi.rebate.config.RebateConstants;
import com.shengshi.rebate.config.RebateUrls;

/**
 * <p>Title:       淘宝支付参数封装
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
public class PayEntity {

    public String outTradeNo;
    public String subject;
    public String body;
    public String totalFee;
    public String notifyUrl;
    public String timeOut;//设置未付款交易的超时时间

    // 商户id
    public String ALIPAY_DEFAULT_PARTNER;
    // 收款支付宝账号
    public String ALIPAY_DEFAULT_SELLER;
    // 商户私钥，自助生成
    public String ALIPAY_PRIVATE_KEY;
    // 支付宝公钥
    public String ALIPAY_PUBLIC_KEY;

    public PayEntity() {
        notifyUrl = RebateUrls.GET_ALIPAY_NOTIFY_URL();
        ALIPAY_DEFAULT_PARTNER = RebateConstants.ALIPAY_DEFAULT_PARTNER;
        ALIPAY_DEFAULT_SELLER = RebateConstants.ALIPAY_DEFAULT_SELLER;
        ALIPAY_PRIVATE_KEY = RebateConstants.ALIPAY_PRIVATE_KEY;
        ALIPAY_PUBLIC_KEY = RebateConstants.ALIPAY_PUBLIC_KEY;
    }

}

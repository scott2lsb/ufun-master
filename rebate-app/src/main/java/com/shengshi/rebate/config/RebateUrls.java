package com.shengshi.rebate.config;

public class RebateUrls {

    //	public static final String BASIC_URL = "m.xiaoyu.com/"; // 正式
    public static final String BASIC_URL = "mapi.dev.xiaoyu.com/"; // 测试

    public static final String REBATE_ORDER_NOTIFY_URL = "http://mapi.dev.xiaoyu.com/card/alipay/notify";

    /**
     * 获取服务器根目录URL
     */
    public static final String GET_SERVER_ROOT_URL() {
        return "http://" + BASIC_URL;
    }

    /**
     * 支付宝支付回调地址
     *
     * @return
     */
    public static final String GET_ALIPAY_NOTIFY_URL() {
        return GET_SERVER_ROOT_URL() + "card/alipay/notify";
    }
}

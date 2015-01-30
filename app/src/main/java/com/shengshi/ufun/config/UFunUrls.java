package com.shengshi.ufun.config;

public class UFunUrls {

    public static final String BASIC_URL = "mapi.dev.xiaoyu.com"; // 测试
    public final static String SHARE_URL = "http://www.xiaoyu.com/"; //分享默认连接

    /**
     * 获取服务器根目录URL
     */
    public static final String GET_SERVER_ROOT_URL() {
        return "http://" + BASIC_URL;
    }
}

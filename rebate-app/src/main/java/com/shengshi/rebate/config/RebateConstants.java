package com.shengshi.rebate.config;

import com.shengshi.base.common.Constants;

/**
 * <p>Title:     RebateConstants
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-14
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateConstants extends Constants {

    // 有范登录类名
    public static final String CLASS_NAME_UFUN = "com.shengshi.ufun.ui.LoginActivity";

    //==================================以下字段用于 listview 分页时，每页加载几条数据=========================================//
    public static final int PAGE_SIZE_20 = 20;
    public static final int PAGE_SIZE_25 = 25;
    public static final int PAGE_SIZE_50 = 50;
    public static final int PAGE_SIZE_100 = 100;
    public static final int PAGE_SIZE_1000 = 1000;

    //================以下是Activity跳转(startActivityForResult) 用到的requestCode=====================================//
    public static final int ACTIVITY_REQUEST_CODE = 100;


    //===================================以下是微信信息，注:已放在服务端完成 =================================================//

    // 微信 APP_ID
    public static final String WX_APP_ID = "wx34d890a74e92f120";
    /**
     * 微信 商户id
     */
    public static final String WX_PARTNER_ID = "1221934201";
    /**
     * 微信 商户 key
     */
    public static final String WX_PARTNER_KEY = "d226b9b580a1b7c2e61d9a229d735c2a";
    /**
     * 微信 应用密钥
     */
    public static final String WX_APP_SECRET = "fdaee81982659a24ff3a6cd83c6920c1";
    /**
     * 微信 支付专用签名串PaySignKey
     * 微信开放平台和商户约定的支付密钥
     */
    public static final String WX_APP_KEY = "L66bdiYyzfeYzdd1X3txxWR4iDkey6fmkoWLH6SZ4vbKoap6whUY4swtYbf5QaZvc5UFfek0oDmFiY84gzGpWAF0fMRKTSVdLHuoAKcbHJpZeLNnjeVLGwasWnBXMAb4";

    //===================================以下是支付宝信息=================================================//

    //合作身份者id，以2088开头的16位纯数字
    public static final String ALIPAY_DEFAULT_PARTNER = "2088611479728416";

    //收款支付宝账号
    public static final String ALIPAY_DEFAULT_SELLER = "zoudp@shengshi.com.cn";

    //商户私钥，自助生成
    public static final String ALIPAY_PRIVATE_KEY = "MIICWwIBAAKBgQCexJO3rmvolyDgD4bCjN8h3HsZEG17MRplwrDApNa52Mf4EBD9Rf4HdLsdcBSU7OkMtYS6Ze3+7LOrtcTx4rtwniszKIA5X3WQ07gu/5WQdtlqDgN39LBUog2HiniZOTnDq6YjRAm7DDw01wvQ/ZdZjRnNy5Qc7O6SBHV/fXeJ5QIDAQABAoGAICSEE2fIiOovyazbB4AlnGFaupRM1ef3BCsKRbYZkr6EnYADMIN/DltflnIeeJgOBnipSmNgb3/UUCsYmC/i6nQ5Cghjqd+zcXFyIy5KYdXAjk37vvcundaFOnaeSSmfCQe9oltyVnAyEJ9zxfOT2RCxKJYrrWpyI8DDIJaMcoECQQDK+SMIaInjgPJNDFGm61wise0HUWqofBUyaZJGqAAn5ctBowWf0jZFq6QTvUf+3UMR7RtZK0FcV8aaK5hmtC4tAkEAyD78Q1SJD82zCc4eoX2rkQBvZNA11j7NMMIdoc2rXFdkaPUbJqJzhBrGmqBRIWwgHjWMQbSAEVSwCcKpFR9VmQJAYLDWU4cZSNuAkduwegfc2FpSRA4w9RpHLsDEAgy+JkN91ELIxx3o5L+jZhPhXDQjY0LEqHHGrSrTXVYvP53N8QJAH9aD3WCveifGc6SmEh8dDCz5iRnhtXK+xSXX+EXvHPt4J4fxDS9/Fl37fdJHk/zRDnyNFFVSl27Tnnmo0Ibu4QJANrUWdRGsySo/Tt5zZWm2ruel+HBMZs+TYqzair3h5xureZ1eGM8Y8knGHQzgis6jpITYXwXQk7MYg8f78e/Mbw==";

    //支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";


}

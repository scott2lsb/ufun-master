package com.shengshi.rebate.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderResultEntity extends BaseEntity {

    private static final long serialVersionUID = 7735270392272782130L;
    public OrderResultInfo data;

    public class OrderResultInfo implements Serializable {
        private static final long serialVersionUID = 3693544810201937917L;

        /**
         * 当使用客户端支付时，请把这个作为订单号
         */
        @SerializedName("payment_id")
        public String orderSn;

        /**
         * 最终需要支付的金额
         */
        @SerializedName("need_pay_price")
        public String orderPrice;
        /**
         * 传给支付宝 商品标示（如商品名称、打折信息）
         */
        public String title;
        /**
         * 传给支付宝 商品标示
         */
        public String subject;

        /**
         * 未付款交易的超时 时间
         */
        @SerializedName("it_b_pay")
        public String timeOut;

        //以下是微信支付用到的字段，1.0版本 没接入微信支付     -  liaodl
        public String wx_appid;
        public String wx_noncestr;
        public String wx_package;
        public String wx_partnerid;
        public String wx_prepayid;
        public String wx_timestamp;
        public String wx_sign;
    }

}

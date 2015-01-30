package com.shengshi.rebate.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * <p>Title:          订单信息
 * <p>Description:
 * 1.获取服务待支付订单 返回 的 实体 类  也是 此类
 * 2.通过order_id 查询此订单信息，返回账号余额，返利金额，支付金额 等
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-5
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateOrderEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public RebateOrderInfo data;

    public class RebateOrderInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @SerializedName("order_id")
        public String orderId;

        /**
         * 订单价格
         */
        @SerializedName("order_price")
        public String orderPrice;

        /**
         * 账户余额
         */
        @SerializedName("user_balance")
        public String userBalance;

        /**
         * 该订单返利金额
         */
        @SerializedName("return_money")
        public String returnMoney;

        /**
         * 0--正常      1--错误
         */
        public int status;

        /**
         * 错误提示信息
         */
        public String msg;

    }

}

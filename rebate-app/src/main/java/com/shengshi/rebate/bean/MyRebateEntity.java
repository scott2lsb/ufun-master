package com.shengshi.rebate.bean;

import com.google.gson.annotations.SerializedName;
import com.shengshi.rebate.bean.detail.CommentEntity.CommentInfo;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title:      我的返利 实体
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-4
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class MyRebateEntity extends BaseEntity {
    private static final long serialVersionUID = -5190231789565701904L;
    public RebateData data;

    public class RebateData implements Serializable {
        private static final long serialVersionUID = 1L;
        public int count;
        public List<RebateInfo> rows;
    }

    public class RebateInfo implements Serializable {
        private static final long serialVersionUID = 1204908596815330692L;
        public String msg;

        @SerializedName("order_id")
        public String orderId;

        /**
         * 订单状态0--待买单  1--已完成  2--待评价
         */
        @SerializedName("order_status")
        public int orderStatus;

        @SerializedName("pay_price")
        public String payPrice;

        @SerializedName("return_money")
        public String returnMoney;

        @SerializedName("shop_name")
        public String shopName;

        public String date;

        public CommentInfo comment;

    }

}

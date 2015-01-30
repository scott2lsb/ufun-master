package com.shengshi.rebate.bean.detail;

import com.google.gson.annotations.SerializedName;
import com.shengshi.rebate.bean.BaseEntity;
import com.shengshi.rebate.bean.home.ShopInfo;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title:         DetailEntity
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-4
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class DetailEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7419399212997053347L;

    public DetailInfo data;

    public class DetailInfo implements Serializable {
        private static final long serialVersionUID = 3693544810201937917L;

        @SerializedName("item_id")
        public String itemId;

        @SerializedName("order_id")
        public String orderId;

        @SerializedName("brand_name")
        public String brandName;

        @SerializedName("button_msg")
        public String buttonMsg;

        @SerializedName("button_status")
        public Integer buttonStatus;

        public String title;

        @SerializedName("pay_choice")
        public PayChoice payChoice;

        public Comment comment;

        @SerializedName("use_info")
        public UseInfo useInfo;

        public ShopEntity shop;
    }

    public class PayChoice implements Serializable {
        private static final long serialVersionUID = 3693544810201937917L;
        public int count;
        public List<String> rows;
    }

    public class Comment implements Serializable {
        private static final long serialVersionUID = 3693544810201937917L;
        @SerializedName("comment_count")
        public int commentCount;
        @SerializedName("start_num")
        public int starNum;
    }

    public class UseInfo implements Serializable {
        private static final long serialVersionUID = 3693544810201937917L;
        public int count;
        public List<UseTip> rows;

        public class UseTip implements Serializable {
            private static final long serialVersionUID = 3693544810201937917L;
            public String key;
            public String value;
        }
    }

    public class ShopEntity implements Serializable {
        private static final long serialVersionUID = 8836748695308308176L;
        public int count;
        public List<ShopInfo> rows;
    }

}

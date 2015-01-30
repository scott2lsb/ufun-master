package com.shengshi.rebate.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RebateCardEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public CardInfo data;

    public class CardInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @SerializedName("user_balance")
        public String userBalance;
        @SerializedName("return_total")
        public String returnTotal;

    }

}

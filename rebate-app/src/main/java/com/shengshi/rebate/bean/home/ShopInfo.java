package com.shengshi.rebate.bean.home;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShopInfo implements Serializable {

    private static final long serialVersionUID = -3919661836900914933L;

    public String geodist;
    @SerializedName("shop_id")
    public Integer shopId;
    @SerializedName("shop_name")
    public String shopName;
    public String address;
    public double lat;
    public double lng;
    public String tel;

}

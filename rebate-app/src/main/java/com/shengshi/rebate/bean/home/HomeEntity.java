package com.shengshi.rebate.bean.home;

import com.google.gson.annotations.SerializedName;
import com.shengshi.rebate.bean.BaseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeEntity extends BaseEntity {

    private static final long serialVersionUID = 6806570618801761969L;
    public HomeInfo data;

    public class HomeInfo implements Serializable {

        private static final long serialVersionUID = 1026342061529564190L;

        public CardEntiy card;

        public List<ItemConditionEntity> menu;

        public ShopEntity shop;

        public TodayItemEntity wzk;
    }

    public class CardEntiy implements Serializable {
        private static final long serialVersionUID = 1026342061529564190L;
        public String money;
        public String name;
        public String title;
        public String img;
    }

    public class ItemConditionEntity implements Serializable {
        private static final long serialVersionUID = 1L;
        public String label;
        public String name;
        public List<ConditionOption> option = new ArrayList<ConditionOption>();
    }

    public class ConditionOption implements Serializable {
        private static final long serialVersionUID = 5695050363247074847L;
        public String key;
        public String name;
        public List<ConditionOption> sub;
    }

    public class ShopEntity implements Serializable {

        private static final long serialVersionUID = 8836748695308308176L;
        public int count;
        public List<ShopInfoEntity> data;
    }

    public class ShopInfoEntity implements Serializable {

        private static final long serialVersionUID = 8836748695308308176L;
        public ShopItem item;
        public ShopInfo shop;
    }

    public class ShopItem implements Serializable {

        private static final long serialVersionUID = 8836748695308308176L;
        public int count;
        public List<ShopServiceItemInfo> rows;
    }

    public class ShopServiceItemInfo implements Serializable {

        private static final long serialVersionUID = -4800375845229964261L;

        public String day;
        public String week;
        @SerializedName("brand_name")
        public String brandName;
        @SerializedName("return_money")
        public String returnMoney;
        @SerializedName("item_id")
        public String itemId;
        public String title;
        public String img;

    }

    public class TodayItemEntity implements Serializable {

        private static final long serialVersionUID = 3913745225487654996L;

        public int count;
        public List<ShopServiceItemInfo> data;
    }

}

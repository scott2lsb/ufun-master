package com.shengshi.ufun.bean;

import java.io.Serializable;
import java.util.List;

public class UFunMessageEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -3222668616240047019L;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -2881062744438452481L;
        public List<Item> list;
    }

    public class Item implements Serializable {
        private static final long serialVersionUID = 2284406681974007252L;
        public String lid;
        public String authorid;
        public String touid;
        public String pmtype;
        public String addtime;
        public String lastmsg;
        public String title;
        public String lastupdate;
        public String icon;
    }
}

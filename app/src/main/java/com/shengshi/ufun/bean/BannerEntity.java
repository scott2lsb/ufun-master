package com.shengshi.ufun.bean;

import java.io.Serializable;

public class BannerEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 272552492718054204L;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -8730184441076332730L;
        public long end_time;
        public String pic;
        public String url;
        public String cityid;
        public int show_time;
        public String md5;
    }
}

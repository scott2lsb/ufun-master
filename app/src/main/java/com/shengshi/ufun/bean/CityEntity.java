package com.shengshi.ufun.bean;

import java.io.Serializable;
import java.util.List;

public class CityEntity implements Serializable {

    private static final long serialVersionUID = 5877681834566831308L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -4824137520331721979L;
        public List<Result> list;
    }

    public class Result implements Serializable {
        private static final long serialVersionUID = -5038619732505970805L;
        public String firstchar;
        public List<City> list;
    }

    public class City implements Serializable {
        private static final long serialVersionUID = -6067418154325161559L;
        public int id;
        public int cityid;
        public String name;
        public String firstchar;
        public int ifopen;
        public int ishot;
    }

}

package com.shengshi.ufun.bean;

import java.io.Serializable;


public class StatusEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5877681834566831308L;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -8730184441076332730L;
        public Boolean status;
        public String msg;
    }
}

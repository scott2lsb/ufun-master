package com.shengshi.ufun.bean.mine;

import com.shengshi.ufun.bean.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class FansEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5877681834566831308L;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -8730184441076332730L;
        public int count;
        public List<Fans> fans;
        public List<Fans> list;
    }

    public class Fans implements Serializable {
        private static final long serialVersionUID = -1984227915923259361L;
        public int uid;
        public String username;
        public String icon;
        public String signature;
        public int is_attention = 1; //是否关注 1是0否
    }
}

package com.shengshi.ufun.bean.circle;

import java.io.Serializable;
import java.util.List;

public class CircleRuleEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<Admin> admin;
        public int count;
        public Quan quan;
    }

    public class Admin implements Serializable {
        private static final long serialVersionUID = 1L;
        public int uid;
        public String username;
        public String avatar;
        public String signature;

    }

    public class Quan implements Serializable {
        private static final long serialVersionUID = 1L;
        public int qid;
        public int ifjoin;
        public String qname;
        public String descrip;
        public String message;
        public String rule;
        public String icon;


    }
}

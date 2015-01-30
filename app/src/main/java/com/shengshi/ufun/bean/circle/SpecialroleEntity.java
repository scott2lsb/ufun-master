package com.shengshi.ufun.bean.circle;

import java.io.Serializable;
import java.util.List;

public class SpecialroleEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -5038619732505970805L;
        public List<User> list;
        public int count;

    }

    public class User implements Serializable {
        private static final long serialVersionUID = -6067418154325161559L;
        public int uid;
        public int isfriend;
        public String avatar;
        public String username;
        public String signature;
        public List<Photo> imgs;


    }

    public class Photo implements Serializable {
        private static final long serialVersionUID = 1L;
        public String pic;
        public int tid;
    }
}

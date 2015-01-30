package com.shengshi.ufun.bean.circle;

import java.io.Serializable;
import java.util.List;

public class ActivityCircleListEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -5038619732505970805L;
        public List<Bangdan> list;

        public Bangdan member;
    }

    public class Bangdan implements Serializable {
        private static final long serialVersionUID = -6067418154325161559L;
        public int uid;
        public String username;
        public String avatar;
        public String rank_title;//排名称号
        public int isfriend;
        public int credit;//积分
        public int rank;//排名12345
    }

}

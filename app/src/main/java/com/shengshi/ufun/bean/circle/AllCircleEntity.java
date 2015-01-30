package com.shengshi.ufun.bean.circle;

import java.io.Serializable;
import java.util.List;


public class AllCircleEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<ALLCircle> list;
        public int count;
    }

    public class ALLCircle implements Serializable {
        private static final long serialVersionUID = 1L;
        public int id;
        public String name;
        public List<SecondCircle> quanlist;
    }

    public class SecondCircle implements Serializable {
        private static final long serialVersionUID = 1L;
        public int qid;
        public int typeid;
        public int styleid;
        public String qname;
        public String descrip;
        public int cityid;
        public int isindex;
        public int ifucheck;
        public int ifopen;
        public int ifinfo;
        public String icon;
        public int ifjoin;


    }
}

package com.shengshi.ufun.bean.circle;

import java.io.Serializable;
import java.util.List;

public class CircleTuiListEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<Tui> lists;
        public List<Tui> hot;
        public int count;
        public int cid;
        public String name;
        public String img;
        public int ifsign;
        public String desp;
    }

    public class Tui implements Serializable {
        private static final long serialVersionUID = 1L;
        public int tid;
        public String title;
        public String uname;
        public String time;
        public int hasphoto;
        public int scan_count;
        public int repley_count;
        public int ifjoin;

    }
}

package com.shengshi.ufun.bean.circle;

import java.io.Serializable;
import java.util.List;

public class PicCircleTuiListEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<Tui> list;
        public int count;
        public Quan quan;
        public List<Tui> top;

    }

    public class Tui implements Serializable {
        private static final long serialVersionUID = 1L;
        public int tid;
        public String title;
        public String uname;
        public String pic;
        public int scan_count;
        public int repley_count;
        public int ifzan;
        public String time;
        public Double pic_height;
        public Double pic_width;
        public int hasphoto;

    }

    public class Quan implements Serializable {
        private static final long serialVersionUID = 1L;
        public int qid;
        public String qname;
        public String icon;
        public int ifsign;
        public String descrip;
    }
}

package com.shengshi.ufun.bean.home;

import java.io.Serializable;
import java.util.List;


public class HomeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<Threads> list;
        public int newnum;
        public int count;

    }

    public class Threads implements Serializable {
        private static final long serialVersionUID = 1L;
        public int tid;
        public String title;
        public String uname;
        public String avatar;
        public String flag;//标示是否是推广，是返回推广的文字
        public int uid;
        public String time;
        public int imgnum;
        public int scan_count;
        public int reply_count;
        public List<Photo> imgs;


    }

    public class Photo implements Serializable {
        private static final long serialVersionUID = 1L;
        public String pic;

    }
}

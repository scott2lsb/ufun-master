package com.shengshi.ufun.bean.circle;

import java.io.Serializable;
import java.util.List;

public class CircleThreadEntity implements Serializable {
    private static final long serialVersionUID = -6292948127271246988L;
    public int errCode;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = 3064995901164960437L;
        public Thread thread;
        public Tui tui;
        public List<PostEntity> post;
    }

    public class Thread implements Serializable {
        private static final long serialVersionUID = -1241048953726698290L;
        public String authorid;
        //标题
        public String subject;
        public String author;
        public String author_avatar;
        public String rank_title;
        //发表日期
        public String postdate;
        //内容
        public String content;
        public List<Attch> attchs;
        //回帖总数
        public String postcount;
        public String zan_count;
        public int isfavor;
        public int ifzan;
        public int isfriend;
        public int reply_count;
    }

    public class Tui implements Serializable {
        private static final long serialVersionUID = -2285925275290309851L;

    }

    public class PostEntity implements Serializable {

        private static final long serialVersionUID = -1357473153297519764L;
        public String pid;
        public String authorid;
        public String author;
        public String contents;
        public String postdate;
        public String avatar;
        public String rank_title;
        public String subcount;
        public List<SubReply> sublist;
    }

    public class SubReply implements Serializable {
        private static final long serialVersionUID = -2820105528040819613L;
        public String authorid;
        public String author;
        public String subject;
        public String contents;
    }

    public class Attch implements Serializable {
        private static final long serialVersionUID = 6247483136474673655L;
        public String pic;
    }

}

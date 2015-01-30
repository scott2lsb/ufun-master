package com.shengshi.ufun.bean.circle;

import java.io.Serializable;
import java.util.List;

public class CircleIndexEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -5038619732505970805L;
        public List<Circle> join;
        public List<Circle> hot;
    }

    public class Circle implements Serializable {
        private static final long serialVersionUID = -6067418154325161559L;
        public int qid;
        public int credit;
        public int styleid;//1表示文字格式，2表示图片格式
        public String qname;//圈子名称
        public String username;
        public int addtime;
        public String descrip;
        public int ifjoin;
        public List<Photo> imgs;


    }

    public class Photo implements Serializable {
        private static final long serialVersionUID = 1L;
        public String pic;
        public String title;
        public String descrip;
        public int tid;
        public int ifpic;
    }
}

package com.shengshi.ufun.bean.circle;

import java.io.Serializable;
import java.util.List;

/**
 * 我关注的列表，我的粉丝列表 通用
 *
 * @author xmt
 */
public class MyConcernEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public int errCode;
    public String errMessage;
    public Data info;

    public class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public List<Concern> result;
        public int count;
    }


    public class Concern implements Serializable {
        private static final long serialVersionUID = 1L;
        public int id;
        public String name;
        public String descrip;
        public int ifconcern;//是否已经关注
        public int ifcircle;//是否是圈子
        public String img;


    }
}

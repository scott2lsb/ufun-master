package com.shengshi.ufun.bean.mine;

import com.shengshi.ufun.bean.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class MythreadEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5877681834566831308L;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -8730184441076332730L;
        public List<Result> result;
    }

    public class Result implements Serializable {
        private static final long serialVersionUID = -1984227915923259361L;
        public int tid;
        public String title;
        public String uname;
        public String time;
        public int hasphoto;
        public int scan_count;
        public int repley_count;
    }
}

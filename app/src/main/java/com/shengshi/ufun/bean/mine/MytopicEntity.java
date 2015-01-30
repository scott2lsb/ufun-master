package com.shengshi.ufun.bean.mine;

import com.shengshi.ufun.bean.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class MytopicEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5877681834566831308L;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -8730184441076332730L;
        public int count;
        public List<Threads> list;
    }

    public class Threads implements Serializable {
        private static final long serialVersionUID = -2139604540191479259L;
        public int tid;
        public String title;
        public String uname;
        public String time;
        public int hasphoto;
        public String scan_count;
        public String reply_count;
    }
}

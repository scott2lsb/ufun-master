package com.shengshi.ufun.bean.circle;

import java.io.Serializable;
import java.util.List;

public class CircleTagEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -5038619732505970805L;
        public List<Tag> list;

    }

    public class Tag implements Serializable {
        private static final long serialVersionUID = -6067418154325161559L;
        public int partid;
        public String partname;
        public List<SecondTag> taglist;


    }

    public class SecondTag implements Serializable {
        private static final long serialVersionUID = 1L;
        public String name;
        public String tagid;
    }
}

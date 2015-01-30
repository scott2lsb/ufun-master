package com.shengshi.ufun.bean;

import com.shengshi.ufun.common.StringUtils;

import java.io.Serializable;
import java.util.List;

public class UFunChatEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -7775370078698103604L;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = 6192693823014283602L;
        public List<Msg> list;
    }

    public static class Msg implements Serializable {
        private static final long serialVersionUID = -8490638399545837111L;

        public Msg(int authorid, String message) {
            super();
            this.authorid = StringUtils.toString(authorid);
            this.message = message;
        }

        public String to_uid;
        public int state;
        public String pmid;
        public String lid;
        public String authorid;
        public String message;
        public String delstatus;
        public String addtime;
        public String icon;
    }
}

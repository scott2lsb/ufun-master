package com.shengshi.ufun.bean;

import java.io.Serializable;

public class UFunSendResponseEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -5828540995066863182L;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -5906010354220500822L;
        public int lid;
    }
}

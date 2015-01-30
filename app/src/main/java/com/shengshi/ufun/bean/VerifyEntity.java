package com.shengshi.ufun.bean;

import java.io.Serializable;

public class VerifyEntity implements Serializable {

    private static final long serialVersionUID = 5877681834566831308L;
    public int errCode;
    public String errMessage;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -4824137520331721979L;
        public Boolean status; //true 获取短信成功
        public String msg;//提示信息
    }
}

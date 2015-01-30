package com.shengshi.rebate.bean;

import java.io.Serializable;

public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1838756339385319452L;

    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = 1;

    public Integer errCode;
    public String errMessage;

}

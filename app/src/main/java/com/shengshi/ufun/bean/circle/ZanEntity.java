package com.shengshi.ufun.bean.circle;

import java.io.Serializable;

/**
 * zanEntity
 *
 * @author huangxp
 */
public class ZanEntity implements Serializable {
    private static final long serialVersionUID = 3595267425485450180L;
    public int errCode;
    public Data data;
    public String errMessage;

    public class Data implements Serializable {
        public int ifzan;
    }
}

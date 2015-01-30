package com.shengshi.rebate.bean;

import java.io.Serializable;

public class RespEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public RespInfo data;

    public class RespInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        public String msg;
        public String status;

    }

}

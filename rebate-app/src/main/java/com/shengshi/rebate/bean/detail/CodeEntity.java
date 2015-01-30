package com.shengshi.rebate.bean.detail;

import com.shengshi.rebate.bean.BaseEntity;

import java.io.Serializable;

/**
 * <p>Title:     CodeEntity-详情页获取的返利码实体
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-7
 * <p>@author:
 * <p>Update Time: 2014-12-31
 * <p>Updater:
 * <p>Update Comments: 接口字段调整
 */
public class CodeEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7419399212997053347L;
    public CodeInfo data;

    public class CodeInfo implements Serializable {
        private static final long serialVersionUID = 3693544810201937917L;

        public String code;
        public String msg;
        public int status;
    }

}

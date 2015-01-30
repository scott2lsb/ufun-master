package com.shengshi.rebate.location;

import java.io.Serializable;

/**
 * <p>Title:     百度定位结果
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-21
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class LocationResult implements Serializable {

    private static final long serialVersionUID = -41261982024686531L;

    public double latitude;//纬度
    public double longitude;//经度
    public String locationCityCode;
    public String locationCityName;
    public String address;
}

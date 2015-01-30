package com.shengshi.ufun.utils.location;

import java.io.Serializable;

/**
 * <p>Title:     百度定位结果
 * <p>Description:
 * <p>@author:  dengbw
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-30
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

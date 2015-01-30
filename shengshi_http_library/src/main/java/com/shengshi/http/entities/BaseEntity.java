package com.shengshi.http.entities;

import com.google.gson.stream.JsonReader;
import com.shengshi.http.net.AppException;

/**
 * <p>Title:         BaseEntity
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-9-30
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public abstract class BaseEntity {
    public abstract void readFromJson(JsonReader reader) throws AppException;
}

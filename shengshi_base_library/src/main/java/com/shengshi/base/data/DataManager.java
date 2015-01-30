package com.shengshi.base.data;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

/**
 * <p>Title:           数据管理
 * <p>Description:
 * <p>作用：<br/>
 * 1. 缓存数据,该类必须为单例模式<br/>
 * 2. 数据变化监听<br/>
 * </p>
 * <p/>
 * <p>使用：<br/>
 * 1. 继承该类，把类设计成单例模式<br/>
 * 2. 需要监听数据是否变化，请调用方法registerDataSetObserver注册一个观察者<br/>
 * </p>
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-20
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public abstract class DataManager implements IObserver {

    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached View that the underlying data has been changed
     * and it should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

}

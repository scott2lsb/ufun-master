package com.shengshi.base.ui;

import android.os.Bundle;

/**
 * <p>Title:     BasePagerFragment
 * <p>Description:      含有ViewPager的Fragment
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-10
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public abstract class BasePagerFragment extends BaseFragment {

    protected boolean isVisibleToUser;//界面是否可见
    protected boolean isViewInitialed;//界面是否已经创建过
    protected boolean isDataInitialed;//数据是否初始化过

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            prepareFetchData();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitialed = true;
        prepareFetchData();
    }

    /**
     * 让子类只重写fetchData()方法
     */
    @Override
    public final void initData() {
    }

    /**
     * 真正填充数据开始时间
     */
    public abstract void fetchData();

    public boolean prepareFetchData() {
        return prepareFetchData(false);
    }

    public boolean prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitialed && (!isDataInitialed || forceUpdate)) {
            fetchData();
            isDataInitialed = true;
            return true;
        }
        return false;
    }

}

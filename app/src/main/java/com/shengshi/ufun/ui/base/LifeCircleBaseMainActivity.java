package com.shengshi.ufun.ui.base;

import android.os.Bundle;

public class LifeCircleBaseMainActivity extends LifeCircleBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
    }

    @Override
    public String getTopTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub

    }

}

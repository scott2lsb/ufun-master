package com.shengshi.base.ui.widget.swipeback;

import android.os.Bundle;
import android.view.View;

import com.shengshi.base.ui.BasePagerActivity;
import com.shengshi.base.ui.BasePagerFragment;

public abstract class SwipeBackPagerActivity extends BasePagerActivity implements
        SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @Override
    protected void initializeTabs() {
    }

    @Override
    protected BasePagerFragment getFragmentAtIndex(int index) {
        return null;
    }

    @Override
    protected int getFragmentCount() {
        return 0;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        if (getSwipeBackLayout() != null) {
            getSwipeBackLayout().setEnableGesture(enable);
        }
    }

    @Override
    public void scrollToFinishActivity() {
        if (getSwipeBackLayout() != null) {
            getSwipeBackLayout().scrollToFinishActivity();
        }
    }
}

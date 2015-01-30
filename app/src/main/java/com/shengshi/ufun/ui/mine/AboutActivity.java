package com.shengshi.ufun.ui.mine;

import android.widget.TextView;

import com.shengshi.base.tools.AppHelper;
import com.shengshi.ufun.R;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;

public class AboutActivity extends LifeCircleBaseActivity {

    TextView about_version_name;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        about_version_name = findTextViewById(R.id.about_version_name);
        about_version_name.setText("Android " + AppHelper.getVersionName(this) + "版");
    }

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.mine_setting_about);
    }

    @Override
    protected void initData() {

    }

}

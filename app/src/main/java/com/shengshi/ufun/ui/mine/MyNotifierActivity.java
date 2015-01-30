package com.shengshi.ufun.ui.mine;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.shengshi.ufun.R;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;

public class MyNotifierActivity extends LifeCircleBaseActivity implements OnClickListener {

    ImageView my_notice;
    ImageView my_message;
    Boolean notice = true;
    Boolean message = true;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_my_notifier;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        my_notice = findImageViewById(R.id.my_notice);
        my_message = findImageViewById(R.id.my_message);
        my_notice.setOnClickListener(this);
        my_message.setOnClickListener(this);
    }

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.mine_setting_message);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.my_notice) {
            if (notice) {
                my_notice.setBackgroundResource(R.drawable.switch_close);
                notice = false;
            } else {
                my_notice.setBackgroundResource(R.drawable.switch_open);
                notice = true;
            }
        } else if (id == R.id.my_message) {
            if (message) {
                my_message.setBackgroundResource(R.drawable.switch_close);
                message = false;
            } else {
                my_message.setBackgroundResource(R.drawable.switch_open);
                message = true;
            }
        }
    }

}

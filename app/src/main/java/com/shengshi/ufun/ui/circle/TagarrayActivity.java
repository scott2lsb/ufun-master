package com.shengshi.ufun.ui.circle;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.shengshi.base.tools.Log;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.circle.ArrayTagAdapter;
import com.shengshi.ufun.bean.circle.CircleTagEntity;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;

public class TagarrayActivity extends LifeCircleBaseListActivity implements OnClickListener {
    CircleTagEntity circleTagEntity;
    ArrayTagAdapter arraytagAdapter;
    int[] selectp;

    @Override
    protected void initComponents() {
        super.initComponents();
        TopUtil.updateRightTitle(mActivity, R.id.lifecircle_top_right_title,
                R.string.finlish);
        TopUtil.setOnclickListener(mActivity, R.id.lifecircle_top_right_title,
                this);
        mListView = findXListView();
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);

    }

    @Override
    public String getTopTitle() {
        // TODO Auto-generated method stub
        return getResources().getString(R.string.select_tag);
    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.activity_tagarray;
    }

    @Override
    protected void initData() {
        circleTagEntity = (CircleTagEntity) getIntent().getSerializableExtra("circleTagEntity");
        Bundle bundle = getIntent().getExtras();
        selectp = bundle.getIntArray("selectposition");
        try {

            arraytagAdapter = new ArrayTagAdapter(mContext, circleTagEntity, selectp);
            mListView.setAdapter(arraytagAdapter);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lifecircle_top_right_title:
                Intent intent = new Intent(this, PublishActivity.class);
                intent.putExtra("selectposition", arraytagAdapter.selectPosition);
                setResult(RESULT_OK, intent);
                finish();
                break;

            default:
                break;
        }

    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub

    }

}

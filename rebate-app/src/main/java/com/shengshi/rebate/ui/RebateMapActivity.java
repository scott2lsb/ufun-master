package com.shengshi.rebate.ui;

import android.os.Handler;

import com.shengshi.rebate.R;
import com.shengshi.rebate.ui.base.RebateBaseMapActivity;

/**
 * <p>Title:       返利卡地图基类
 * <p>Description:  返利卡中，使用百度地图Activity继承此类,传入经纬度即可
 * <pre>如何使用：
 * Intent intent = new Intent();
 * intent.putExtra("latitude", 24.495402);
 * intent.putExtra("longitude", 118.192794);
 * intent.setClass(this, RebateMapActivity.class);
 * startActivity(intent);
 * </pre>
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-9
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateMapActivity extends RebateBaseMapActivity {

    public Handler mHandler = new Handler();

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_map;
    }

    @Override
    protected void initMapView() {
        super.initMapView();
    }

    @Override
    protected void initData() {
    }

    @Override
    public String getTopTitle() {
        return "地图";
    }


}

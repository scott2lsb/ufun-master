package com.shengshi.base.ui;

import android.content.Intent;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.shengshi.base.R;

/**
 * <p>Title:       百度地图页面基类
 * <p>Description:  使用百度地图Activity继承此类,传入经纬度即可
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
public abstract class BaseMapActivity extends BaseActivity {

    public MapView mMapView = null;
    public BaiduMap mBaiduMap;//地图对象控制器
    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor postionIcon;

    @Override
    protected void initComponents() {
        int mapViewId = getIdentifier("mGeneralMapView", IdentifierType.ID);
        mMapView = (MapView) findViewById(mapViewId);
        if (mMapView != null) {
            mBaiduMap = mMapView.getMap();
            initMapView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            LatLng point = new LatLng(b.getDouble("latitude"), b.getDouble("longitude"));
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(19f));//放置最大级别

            postionIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_location);
            OverlayOptions ooA = new MarkerOptions().position(point).icon(postionIcon).zIndex(1)
                    .draggable(true);
            mBaiduMap.addOverlay(ooA);

            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
            mBaiduMap.animateMapStatus(u);
        }
    }

    /**
     * 初始化地图
     */
    protected abstract void initMapView();

    @Override
    protected void protectApp() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        // 回收 bitmap 资源
        if (postionIcon != null) {
            postionIcon.recycle();
        }
    }

}

package com.shengshi.base.widget.map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.shengshi.base.res.R;

/**
 * <p>Title:       自定义百度地图放大缩小控件
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-9
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class ZoomControlView extends RelativeLayout implements OnClickListener {
    private Button mButtonZoomin;//扩大按钮
    private Button mButtonZoomout;//缩小按钮
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private float maxZoomLevel;
    private float minZoomLevel;
    private float curZoomLevel;

    public ZoomControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.widget_map_zoom_controls_layout, null);
        mButtonZoomin = (Button) view.findViewById(R.id.zoomin);
        mButtonZoomout = (Button) view.findViewById(R.id.zoomout);
        mButtonZoomin.setOnClickListener(this);
        mButtonZoomout.setOnClickListener(this);
        addView(view);
    }

    @Override
    public void onClick(View v) {
        if (mMapView == null) {
            throw new NullPointerException("you can call setMapView(MapView mapView) at first");
        }
        curZoomLevel = mBaiduMap.getMapStatus().zoom;
        int id = v.getId();
        if (id == R.id.zoomin) {
            curZoomLevel++;
        } else if (id == R.id.zoomout) {
            curZoomLevel--;
        }
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(curZoomLevel);
        mBaiduMap.animateMapStatus(u);
    }

    /**
     * 与MapView设置关联
     *
     * @param mapView
     */
    public void setMapView(MapView mapView) {
        this.mMapView = mapView;
        this.mBaiduMap = mapView.getMap();
        // 设置地图手势事件
        mBaiduMap.setOnMapStatusChangeListener(statusListener);
        // 获取最大的缩放级别
        maxZoomLevel = mBaiduMap.getMaxZoomLevel();
        // 获取最大的缩放级别
        minZoomLevel = mBaiduMap.getMinZoomLevel();
        curZoomLevel = mBaiduMap.getMapStatus().zoom;
        refreshZoomBtnStatus();//改变缩放按钮
    }

    OnMapLoadedCallback callback = new OnMapLoadedCallback() {
        /**
         * 地图加载完成回调函数
         */
        public void onMapLoaded() {
            refreshZoomBtnStatus();
        }
    };

    OnMapStatusChangeListener statusListener = new OnMapStatusChangeListener() {
        /**
         * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
         * @param status 地图状态改变开始时的地图状态
         */
        public void onMapStatusChangeStart(MapStatus status) {
        }

        /**
         * 地图状态变化中
         * @param status 当前地图状态
         */
        public void onMapStatusChange(MapStatus status) {
        }

        /**
         * 地图状态改变结束
         * @param status 地图状态改变结束后的地图状态
         */
        public void onMapStatusChangeFinish(MapStatus status) {
            refreshZoomBtnStatus();
        }
    };

    /**
     * 根据MapView的缩放级别更新缩放按钮的状态，当达到最大缩放级别，设置mButtonZoomin
     * 为不能点击，反之设置mButtonZoomout
     * 注：缩放级别范围： [3.0,19.0]
     *
     * @param level
     */
    public void refreshZoomBtnStatus() {
        if (mMapView == null) {
            throw new NullPointerException("you can call setMapView(MapView mapView) at first");
        }
        curZoomLevel = mBaiduMap.getMapStatus().zoom;
        if (curZoomLevel > minZoomLevel && curZoomLevel < maxZoomLevel) {
            if (!mButtonZoomout.isEnabled()) {
                mButtonZoomout.setEnabled(true);
            }
            if (!mButtonZoomin.isEnabled()) {
                mButtonZoomin.setEnabled(true);
            }
        } else if (curZoomLevel == minZoomLevel) {
            mButtonZoomout.setEnabled(false);
        } else if (curZoomLevel == maxZoomLevel) {
            mButtonZoomin.setEnabled(false);
        }
    }

}

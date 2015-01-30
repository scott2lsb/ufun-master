package com.shengshi.rebate.ui.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.ui.widget.swipeback.SwipeBackLayout;
import com.shengshi.base.ui.widget.swipeback.SwipeBackMapActivity;
import com.shengshi.base.widget.LoadingBar;
import com.shengshi.base.widget.map.ZoomControlView;
import com.shengshi.rebate.R;

/**
 * <p>Title:          地图显示页面
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
public abstract class RebateBaseMapActivity extends SwipeBackMapActivity {

    public LoadingBar loadingBar;
    public TextView topTitleView;

    //自定义地图放大缩小控件
    public ZoomControlView mZoomControlView;

    @Override
    protected void initMapView() {
        mMapView.showZoomControls(false);//隐藏缩放控件
        mBaiduMap.setBuildingsEnabled(true);//设置显示楼体
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(14f));//设置地图状态
        mZoomControlView = (ZoomControlView) findViewById(R.id.mapZoomControlView);
        if (mZoomControlView != null) {
            mZoomControlView.setMapView(mMapView);
        }
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        initLoadingBar();
    }

    private void initLoadingBar() {
        loadingBar = findGeneralLoadingBar();
        if (loadingBar == null) {
            return;
        }
        loadingBar.setFailLayoutOnClick(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadingBar.show();
                requestAgain();
            }
        });
    }

    /**
     * 子类重写该方法，点击屏幕，可以重写加载数据，用于加载数据失败，重试情景
     * <p>前提：
     * <p>1.引入LoadingBar布局,<include layout="@layout/widget_loading_bar_layout" />
     * <p>2.使用loadingBar.showFailLayout();显示重试视图
     */
    public void requestAgain() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTopTitle();
        TopUtil.setOnclickListener(mActivity, R.id.rebate_top_btn_return,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        scrollToFinishActivity();
                    }
                });
    }

    /**
     * 子类重写该方法，传入标题
     *
     * @return
     */
    public abstract String getTopTitle();

    /**
     * 要动态更新或重新设置标题时，子类要显示调用该方法
     * 注：如果子类覆写了getTopTitle() 和 getTopTitleResourceId(),
     * 以getTopTitle()取得值为准，如果为空，再去取getTopTitleResourceId里的值
     */
    public void setTopTitle() {
        if (topTitleView == null) {
            topTitleView = (TextView) findViewById(R.id.rebate_top_title);
        }
        if (topTitleView != null) {
            topTitleView.setSelected(true);
            if (!TextUtils.isEmpty(getTopTitle())) {
                topTitleView.setText(getTopTitle());
            } else {
                if (getTopTitleResourceId() > 0) {
                    topTitleView.setText(getTopTitleResourceId());
                }
            }
        }
    }

    /**
     * 子类重写该方法，传入标题
     *
     * @return
     */
    public int getTopTitleResourceId() {
        return 0;
    }

    protected LoadingBar findGeneralLoadingBar() {
        return (LoadingBar) this.findViewById(R.id.mGeneralLoadingBar);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //用手势滑动退出 代替, 解决退出时，百度地图一下子不见 UI 效果不好问题
            SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
            swipeBackLayout.setScrollThresHold(0.9f);
            swipeBackLayout.scrollToFinishActivity();
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

}

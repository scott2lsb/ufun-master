package com.shengshi.rebate.ui.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.rebate.R;

/**
 * <p>Title:       RebateBaseListActivity
 * <p>Description:
 * <p>1.返利卡项目里的简单 单纯含有 XListView 的Activity可以继承此 类
 * <p>2.简单设置了一些通用XListView常用属性，子类可以重写initListView()方法扩展ListView支持的功能
 * <p>3.设置了ListView 快速滑动 不加载图片
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-2
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public abstract class RebateBaseListActivity extends RebateBaseActivity implements
        IXListViewListener {

    protected XListView mListView;
    private OnScrollListener onScrollListener;

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_common_listview;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        int pagerId = getIdentifier("mGeneralListView", IdentifierType.ID);
        mListView = findXListViewById(pagerId/* R.id.mGeneralListView */);
        initListView();
    }

    /**
     * 子类可以重写扩展ListView支持的功能
     */
    protected void initListView() {
        if (mListView != null) {
            mListView.setPullLoadEnable(false);
            mListView.setPullRefreshEnable(true);
            mListView.setXListViewListener(this);
            if (onScrollListener != null) {
                mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader
                        .getImageLoader(), true, false, onScrollListener));
            } else {
                mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader
                        .getImageLoader(), true, false));
            }
            View emptyView = View.inflate(mContext, R.layout.widget_no_data, null);
            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            ((ViewGroup) mListView.getParent()).addView(emptyView, params);
            mListView.setEmptyView(emptyView);
        }
    }

    public OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    protected void refreshXListView() {
        mListView.stopRefresh();
        mListView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
    }

}

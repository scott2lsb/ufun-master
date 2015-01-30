package com.shengshi.rebate.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.DensityUtil;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.widget.swipemenulistview.SwipeMenu;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuCreator;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuItem;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.rebate.R;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.bean.MyRebateEntity;
import com.shengshi.rebate.bean.MyRebateEntity.RebateInfo;
import com.shengshi.rebate.bean.RespEntity;
import com.shengshi.rebate.config.RebateConstants;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.ui.base.RebateBaseActivity;
import com.shengshi.rebate.widget.MyRebateSwipeListView;

import java.util.List;

/**
 * <p>Title:      我的返利界面
 * <p>Description:
 * <p>@author:  huangxp
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-10
 * <p>@author:      liaodl
 * <p>Update Time: 2015-1-4
 * <p>Updater:      liaodl
 * <p>Update Comments:重构，完善功能
 */
public class MyRebateActivity extends RebateBaseActivity implements IXListViewListener {

    public MyRebateSwipeListView swipeListView;
    public MyRebateListViewAdapter mAdapter;
    public int curPage = 1;
    public int totoalCount = 0;

    @Override
    protected void initComponents() {
        super.initComponents();
        swipeListView = (MyRebateSwipeListView) findViewById(R.id.myRebateSwipeListView);
        initListView();
    }

    protected void initListView() {
        if (swipeListView != null) {
            swipeListView.setPullLoadEnable(true);
            swipeListView.setPullRefreshEnable(true);
            swipeListView.setXListViewListener(this);
            swipeListView.setOnScrollListener(new PauseOnScrollListener(imageLoader
                    .getImageLoader(), true, false));
            View emptyView = View.inflate(mContext, R.layout.widget_no_data, null);
            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            ((ViewGroup) swipeListView.getParent()).addView(emptyView, params);
            swipeListView.setEmptyView(emptyView);
        }
    }

    @Override
    protected void initData() {
        requestUrl(curPage);
    }

    protected void requestUrl(int page) {
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.order.getOrderList");
        encryptInfo.resetParams();
        encryptInfo.putParam("page", page);
        encryptInfo.putParam("page_size", RebateConstants.PAGE_SIZE_20);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<MyRebateEntity> jsonCallback = new JsonCallback<MyRebateEntity>() {
        @Override
        public void onSuccess(MyRebateEntity result) {
            fetchData(result);
            refreshXListView();
            hideLoadingBar();
            //FIXME 测试代码
//			String json = FileUtils.readAssetsFile(mContext, "myrebate.json");
//			result = JsonUtil.toObject(json, MyRebateEntity.class);
//			fetchData(result);
//			refreshXListView();
//			hideLoadingBar();
        }

        @Override
        public void onFailure(AppException exception) {
            toast(exception.getMessage());
            hideLoadingBar();
        }
    };

    private void fetchData(MyRebateEntity entity) {
        if (entity == null || entity.data == null) {
            return;
        }
        try {
            if (curPage == 1) {
                mAdapter = new MyRebateListViewAdapter(mContext, entity.data.rows);
                swipeListView.setAdapter(mAdapter);
                initSwipeListView(entity);
                totoalCount = entity.data.count;
                if (totoalCount > mAdapter.getCount()) {
                    swipeListView.setPullLoadEnable(true);
                } else {
                    swipeListView.setPullLoadEnable(false);
                }
            } else {
                mAdapter.addAll(entity.data.rows);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }

    }

    private void initSwipeListView(final MyRebateEntity entity) {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                switch (menu.getViewType()) {
                    case 0:
                        createMenu(menu);
                        break;
                }
            }
        };
        swipeListView.setMenuCreator(creator);
        swipeListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                RebateInfo rebate = entity.data.rows.get(position);
                cancelOrder(rebate);
                return false;
            }
        });
    }

    private void createMenu(SwipeMenu menu) {
        SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
        openItem.setBackground(new ColorDrawable(R.color.dark_red));
        openItem.setWidth(DensityUtil.dip2px(mContext, 90));
        openItem.setTitle(getString(R.string.rebate_my_cancel_order));
        openItem.setTitleSize(18);
        openItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(openItem);
    }

    //取消订单
    protected void cancelOrder(RebateInfo rebate) {
        if (rebate == null || TextUtils.isEmpty(rebate.orderId)) {
            return;
        }
        showDialog("订单取消中");
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.order.cancelOrder");
        encryptInfo.resetParams();
        encryptInfo.putParam("order_id", rebate.orderId);
        request.setCallback(new JsonCallback<RespEntity>() {

            @Override
            public void onSuccess(RespEntity result) {
                hideDialog();
                if (result != null && result.data != null) {
                    toast(result.data.msg);
                    onRefresh();
                }
            }

            @Override
            public void onFailure(AppException exception) {
                hideDialog();
                toast("订单取消失败");
            }
        });
        request.execute();
    }

    class MyRebateListViewAdapter extends SimpleBaseAdapter<RebateInfo> {

        public MyRebateListViewAdapter(Context context, List<RebateInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.rebate_listview_item_body_my_rebate;
        }

        @Override
        public RebateInfo getItem(int position) {
            return data.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            RebateInfo rebate = getItem(position);
            if (rebate.orderStatus == 0) {
                return 0;
            }
            return 1;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            //放到MyRebateMainAdapter处理，解决ui 复用 重复错乱问题
//			RebateInfo rebate = data.get(position);
//			TextView payPrice = holder.getView(R.id.consume_amount);
//			payPrice.setText(rebate.payPrice);
//			TextView returnMoney = holder.getView(R.id.rabate_amount);
//			returnMoney.setText(rebate.returnMoney);

            return convertView;
        }

    }

    @Override
    public String getTopTitle() {
        return "我的返利";
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_my_rebate;
    }

    @Override
    public void onRefresh() {
        curPage = 1;
        requestUrl(curPage);
    }

    @Override
    public void onLoadMore() {
        if (totoalCount <= mAdapter.getCount()) {
            toast(R.string.rebate_load_more_complete);
            swipeListView.stopLoadMore();
            swipeListView.setPullLoadEnable(false);
            return;
        }
        curPage++;
        requestUrl(curPage);
    }

    public void refreshXListView() {
        if (curPage == 1) {
            swipeListView.stopRefresh();
            swipeListView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
        } else {
            swipeListView.stopLoadMore();
        }
        if (mAdapter != null) {
            if (totoalCount <= mAdapter.getCount()) {
                swipeListView.stopLoadMore();
            } else {
                swipeListView.setPullLoadEnable(true);
            }
        }
    }
}

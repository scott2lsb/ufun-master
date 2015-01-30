package com.shengshi.rebate.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.detail.DetailEntity;
import com.shengshi.rebate.bean.home.ShopInfo;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.ui.base.RebateBaseListActivity;
import com.shengshi.rebate.utils.RebateTool;

import java.util.List;

/**
 * <p>Title:      门店列表
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-5
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateShopListActivity extends RebateBaseListActivity {

    DetailEntity mEntity;
    ShopAdapter mAdapter;

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_shop;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mEntity = (DetailEntity) intent.getSerializableExtra(RebateKey.KEY_INTENT_DETAIL_ENTITY);
        mAdapter = new ShopAdapter(mContext, mEntity.data.shop.rows);
        mListView.setAdapter(mAdapter);
        hideLoadingBar();
    }

    class ShopAdapter extends SimpleBaseAdapter<ShopInfo> implements OnClickListener {

        ShopInfo mShopInfo;

        public ShopAdapter(Context context, List<ShopInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.rebate_listview_item_shop_info_layout;
        }

        /**
         * 底层已经帮我们复用，不要被getView()迷惑
         */
        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            mShopInfo = data.get(position);
            TextView shopName = holder.getView(R.id.rebate_detail_shop_name);
            TextView latestFlag = holder.getView(R.id.rebate_shop_distance_latest_flag);
            TextView shopAddress = holder.getView(R.id.rebate_shop_address);
            TextView shopTelphone = holder.getView(R.id.rebate_shop_telphone);
            if (position == 0) {
                latestFlag.setVisibility(View.VISIBLE);
            } else {
                latestFlag.setVisibility(View.GONE);
            }
            shopName.setText(mShopInfo.shopName);
            shopAddress.setText(mShopInfo.address);
            shopTelphone.setText(mShopInfo.tel);
            holder.getView(R.id.rebate_shop_address_layout).setOnClickListener(this);
            holder.getView(R.id.rebate_shop_telphone_layout).setOnClickListener(this);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            int viewId = v.getId();
            if (R.id.rebate_shop_address_layout == viewId) {
                Intent intent = new Intent(mActivity, RebateMapActivity.class);
                intent.putExtra("latitude", mShopInfo.lat);
                intent.putExtra("longitude", mShopInfo.lng);
                startActivity(intent);
            } else if (R.id.rebate_shop_telphone_layout == viewId) {
                RebateTool.doTel(mActivity, mShopInfo.tel);
            }

        }

    }

    @Override
    public void onRefresh() {
        refreshXListView();//没有接口，此处模拟
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public String getTopTitle() {
        return "所有门店";
    }

}

package com.shengshi.rebate.ui.detail;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.detail.DetailEntity;
import com.shengshi.rebate.bean.home.ShopInfo;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.ui.RebateMapActivity;
import com.shengshi.rebate.ui.RebateShopListActivity;
import com.shengshi.rebate.ui.base.RebateBaseFragment;
import com.shengshi.rebate.utils.RebateTool;

import java.util.List;

/**
 * <p>Title:        地图Fragment
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
public class DetailMapFragment extends RebateBaseFragment implements OnClickListener {

    TextView first_shop_name;
    TextView fitst_shop_address;
    View fitst_shop_address_layout;
    TextView first_shop_telphone;
    View first_shop_telphone_layout;
    TextView second_shop_name;
    TextView second_shop_address;
    View second_shop_address_layout;
    TextView second_shop_telphone;
    View second_shop_telphone_layout;
    TextView all_shop_count;
    View second_shop_layout_container;
    View all_shop_tips_layout;
    DetailEntity mEntity;

    @Override
    public void initComponents(View view) {
        first_shop_name = findTextViewById(view, R.id.rebate_detail_first_shop_name);
        fitst_shop_address = findTextViewById(view, R.id.rebate_fitst_shop_address);
        fitst_shop_address_layout = view.findViewById(R.id.rebate_fitst_shop_address_layout);
        first_shop_telphone = findTextViewById(view, R.id.rebate_first_shop_telphone);
        first_shop_telphone_layout = view.findViewById(R.id.rebate_first_shop_telphone_layout);
        second_shop_name = findTextViewById(view, R.id.rebate_detail_second_shop_name);
        second_shop_address = findTextViewById(view, R.id.rebate_second_shop_address);
        second_shop_address_layout = view.findViewById(R.id.rebate_second_shop_address_layout);
        second_shop_telphone = findTextViewById(view, R.id.rebate_second_shop_telphone);
        second_shop_telphone_layout = view.findViewById(R.id.rebate_second_shop_telphone_layout);
        all_shop_count = findTextViewById(view, R.id.rebate_detail_all_shop_count);
        second_shop_layout_container = view.findViewById(R.id.rebate_detail_second_shop_layout);
        all_shop_tips_layout = view.findViewById(R.id.rebate_detail_all_shop_tips_layout);
        fitst_shop_address_layout.setOnClickListener(this);
        first_shop_telphone_layout.setOnClickListener(this);
        second_shop_address_layout.setOnClickListener(this);
        second_shop_telphone_layout.setOnClickListener(this);
        all_shop_tips_layout.setOnClickListener(this);
    }

    public void fetchData(DetailEntity entity) {
        this.mEntity = entity;
        try {
            List<ShopInfo> shops = entity.data.shop.rows;
            if (!CheckUtil.isValidate(shops)) {
                all_shop_tips_layout.setVisibility(View.GONE);
                return;
            }
            int shopSize = shops.size();
            all_shop_count.setText(String.format(
                    getString(R.string.rebate_detail_all_shop_count_tips), shopSize));
            if (shopSize == 1) {
                first_shop_name.setText(shops.get(0).shopName);
                fitst_shop_address.setText(shops.get(0).address);
                first_shop_telphone.setText(shops.get(0).tel);
                second_shop_layout_container.setVisibility(View.GONE);
            }
            if (shopSize >= 2) {
                second_shop_layout_container.setVisibility(View.VISIBLE);
                first_shop_name.setText(shops.get(0).shopName);
                fitst_shop_address.setText(shops.get(0).address);
                first_shop_telphone.setText(shops.get(0).tel);
                second_shop_name.setText(shops.get(1).shopName);
                second_shop_address.setText(shops.get(1).address);
                second_shop_telphone.setText(shops.get(1).tel);
            }
            if (shopSize >= 3) {
                all_shop_tips_layout.setVisibility(View.VISIBLE);
            } else {
                all_shop_tips_layout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public int getMainContentViewId() {
        return R.layout.rebate_fragment_detail_shop_map_layout;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (R.id.rebate_detail_all_shop_tips_layout == viewId) {
            Intent intent = new Intent(mActivity, RebateShopListActivity.class);
            intent.putExtra(RebateKey.KEY_INTENT_DETAIL_ENTITY, mEntity);
            startActivity(intent);
        } else if (R.id.rebate_fitst_shop_address_layout == viewId) {
            Intent intent = new Intent(mActivity, RebateMapActivity.class);
            intent.putExtra("latitude", mEntity.data.shop.rows.get(0).lat);
            intent.putExtra("longitude", mEntity.data.shop.rows.get(0).lng);
            startActivity(intent);
        } else if (R.id.rebate_first_shop_telphone_layout == viewId) {
            RebateTool.doTel(mActivity, mEntity.data.shop.rows.get(0).tel);
        } else if (R.id.rebate_second_shop_address_layout == viewId) {
            Intent intent = new Intent(mActivity, RebateMapActivity.class);
            intent.putExtra("latitude", mEntity.data.shop.rows.get(1).lat);
            intent.putExtra("longitude", mEntity.data.shop.rows.get(1).lng);
            startActivity(intent);
        } else if (R.id.rebate_second_shop_telphone_layout == viewId) {
            RebateTool.doTel(mActivity, mEntity.data.shop.rows.get(1).tel);
        }
    }

}

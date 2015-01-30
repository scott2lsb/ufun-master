package com.shengshi.rebate.ui.pay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.RebateOrderEntity;
import com.shengshi.rebate.bean.detail.DetailEntity;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.ui.base.RebateBaseActivity;
import com.shengshi.rebate.ui.base.RebateBaseFragment;
import com.shengshi.rebate.ui.pay.OrderFragment.onRefreshFragmentListener;
import com.shengshi.rebate.ui.pay.WaittingFragment.onShowOrderFragmentListener;

/**
 * <p>Title:       支付页面
 * <p>Description:   从详情页进来的支付页面
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-7
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebatePayActivity extends RebateBaseActivity implements onShowOrderFragmentListener,
        onRefreshFragmentListener {

    private DetailEntity detailEntity;
    boolean isNewOrder;//是否是新订单，是否含有orderId,有说明是 待支付订单

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_pay;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(RebateKey.KEY_INTENT_DETAIL_ENTITY)) {
                detailEntity = (DetailEntity) bundle
                        .getSerializable(RebateKey.KEY_INTENT_DETAIL_ENTITY);
            }
            isNewOrder = bundle.getBoolean(RebateKey.KEY_INTENT_IS_NEW_ORDER, true);
        }
        Bundle args = new Bundle();
        if (isNewOrder) {
            args.putSerializable(RebateKey.KEY_INTENT_DETAIL_ENTITY, detailEntity);
            turnToFragment(WaittingFragment.class, RebateKey.TAG_REBATE_PAY_WAITTING_FRAGMENT,
                    args, R.id.rebate_pay_fragment_container, waittingFragmentQuitAnimation);
        } else {
            args.putAll(bundle);
            turnToFragment(OrderFragment.class, RebateKey.TAG_REBATE_PAY_ORDER_FRAGMENT, args,
                    R.id.rebate_pay_fragment_container);
        }
    }

    OnSetCustomAnimaitonListener waittingFragmentQuitAnimation = new OnSetCustomAnimaitonListener() {

        @Override
        public void setCustomAnimaiton(FragmentTransaction ft) {
            ft.setCustomAnimations(android.R.anim.fade_in, R.anim.push_left_out);
        }
    };

    OnSetCustomAnimaitonListener OrderFragmentInAnimation = new OnSetCustomAnimaitonListener() {

        @Override
        public void setCustomAnimaiton(FragmentTransaction ft) {
            ft.setCustomAnimations(R.anim.push_left_in, android.R.anim.fade_out);
        }
    };

    @Override
    public String getTopTitle() {
        return "本单返利";
    }

    @Override
    public void onShowOrderFragment(RebateOrderEntity order, DetailEntity detail, boolean isRefresh) {
        Bundle args = new Bundle();
        args.putSerializable(RebateKey.KEY_INTENT_ORDER_ENTITY, order);
        args.putSerializable(RebateKey.KEY_INTENT_DETAIL_ENTITY, detail);
        args.putBoolean(RebateKey.KEY_INTENT_REFRESH_ORDER_FRAGMENT_FLAG, isRefresh);
        RebateBaseFragment fragment = (WaittingFragment) mFragmentManager
                .findFragmentByTag(RebateKey.TAG_REBATE_PAY_WAITTING_FRAGMENT);
        if (!isRefresh) {
            turnToFragment(fragment, OrderFragment.class, RebateKey.TAG_REBATE_PAY_ORDER_FRAGMENT,
                    args, R.id.rebate_pay_fragment_container, OrderFragmentInAnimation);
        } else {
            turnToFragment(fragment, OrderFragment.class, RebateKey.TAG_REBATE_PAY_ORDER_FRAGMENT,
                    args, R.id.rebate_pay_fragment_container);
        }
    }

    @Override
    public void onRefreshOrderFragment() {
        Fragment fragment = mFragmentManager
                .findFragmentByTag(RebateKey.TAG_REBATE_PAY_WAITTING_FRAGMENT);
        if (fragment instanceof WaittingFragment) {
            ((WaittingFragment) fragment).queryPayStatus(true);
        }
    }

}

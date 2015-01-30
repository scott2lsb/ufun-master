package com.shengshi.rebate.ui.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.widget.XScrollView;
import com.shengshi.base.widget.XScrollView.IXScrollViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.rebate.R;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.bean.OrderResultEntity;
import com.shengshi.rebate.bean.RebateOrderEntity;
import com.shengshi.rebate.bean.detail.DetailEntity;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.ui.RebatePayCommentActivity;
import com.shengshi.rebate.ui.base.RebateBaseFragment;
import com.shengshi.rebate.utils.RebateTool;

/**
 * <p>Title:       订单信息界面
 * <p>Description:    包括消费总额，账号余额，返利多少，需支付多少，支付方式等
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-13
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class OrderFragment extends RebateBaseFragment implements OnClickListener,
        IXScrollViewListener {

    private DetailEntity detailEntity;
    private RebateOrderEntity mEntity;
    private TextView rebateMoneyTv;// 即将获得的返利view
    private TextView orderPriceTv;// 消费金额 view
    private TextView cardBalanceTv;// 同城卡余额
    private Button payBtn;//支付按钮

    private RebateRequest request;//发起网络请求
    XScrollView mScrollView;
    boolean isRefresh;

    String orderId = "";//接收 从我的返利-待支付订单 进来 支付，所传的订单号 参数

    @Override
    public void initComponents(View view) {
        mScrollView = findXScrollViewById(view, R.id.rebate_order_xscrollview);
        mScrollView.setIXScrollViewListener(this);
        orderPriceTv = findTextViewById(view, R.id.rebate_pay_order_price);
        rebateMoneyTv = findTextViewById(view, R.id.rebate_pay_get_rebate_money);
        cardBalanceTv = findTextViewById(view, R.id.rebate_pay_card_balance);
        payBtn = findButtonById(view, R.id.rebatePayBtn);
        payBtn.setOnClickListener(this);
    }

    @Override
    public void initData() {
        ((RebatePayActivity) mActivity).setTopTitle("支付即获返利");
        //在onResum()里设置
        //initOrderInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        initOrderInfo();
    }

    private void initOrderInfo() {
        Bundle bundle = getArguments();
        orderId = bundle.getString(RebateKey.KEY_INTENT_ORDER_ID, "");
        if (!TextUtils.isEmpty(orderId)) {
            requestOrderInfo();
            return;
        }
        if (bundle != null && bundle.containsKey(RebateKey.KEY_INTENT_REFRESH_ORDER_FRAGMENT_FLAG)) {
            isRefresh = bundle.getBoolean(RebateKey.KEY_INTENT_REFRESH_ORDER_FRAGMENT_FLAG);
        }
        if (bundle != null && bundle.containsKey(RebateKey.KEY_INTENT_ORDER_ENTITY)) {
            mEntity = (RebateOrderEntity) bundle.getSerializable(RebateKey.KEY_INTENT_ORDER_ENTITY);
            fetchData(mEntity);
        }
        if (bundle != null && bundle.containsKey(RebateKey.KEY_INTENT_DETAIL_ENTITY)) {
            detailEntity = (DetailEntity) bundle
                    .getSerializable(RebateKey.KEY_INTENT_DETAIL_ENTITY);
        }
    }

    //从我的返利-待支付订单 进来 支付,用order_id 获取订单信息
    private void requestOrderInfo() {
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.order.getOrderInfo");
        encryptInfo.resetParams();
        encryptInfo.putParam("order_id", orderId);
        request.setCallback(orderCallback);
        request.execute();
    }

    JsonCallback<RebateOrderEntity> orderCallback = new JsonCallback<RebateOrderEntity>() {
        @Override
        public void onSuccess(RebateOrderEntity result) {
            fetchData(result);
        }

        @Override
        public void onFailure(AppException exception) {
            toast(exception.getMessage());
        }
    };

    protected void fetchData(RebateOrderEntity order) {
        if (order == null || order.data == null) {
            return;
        }
        orderPriceTv.setText(order.data.orderPrice);
        rebateMoneyTv.setText(order.data.returnMoney);
        cardBalanceTv.setText(getString(R.string.rebate_pay_card_balance, order.data.userBalance));
        if (isRefresh) {
            refreshXscrollView();
        }
    }

    private void refreshXscrollView() {
        mScrollView.stopRefresh();
        mScrollView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
    }

    @Override
    public int getMainContentViewId() {
        return R.layout.rebate_fragment_pay_order_info;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (R.id.rebatePayBtn == viewId) {
            buildPayParams();
        }
    }

    /**
     * 点击立即支付，构建支付参数
     */
    private void buildPayParams() {
        showDialog();
        if (request != null && request.isRunningTask()) {
            Log.i("-- 立即支付网络线程 已经 在后台运行--");
            return;
        }
        payBtn.setEnabled(false);
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.order.pay");
        encryptInfo.resetParams();
        if (!TextUtils.isEmpty(orderId)) {
            encryptInfo.putParam("order_id", orderId);//订单ID
        } else {
            encryptInfo.putParam("order_id", mEntity.data.orderId);//订单ID
        }
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<OrderResultEntity> jsonCallback = new JsonCallback<OrderResultEntity>() {

        @Override
        public void onSuccess(OrderResultEntity result) {
            hideDialog();
            payBtn.setEnabled(true);
            if (result == null || result.data == null || TextUtils.isEmpty(result.data.orderSn)) {
                if (result != null && !TextUtils.isEmpty(result.errMessage)) {
                    toast(result.errMessage);
                } else {
                    toast("获取不到订单号!");
                }
                return;
            }
            //FIXME 使用本地测试数据
//			String json = FileUtils.readAssetsFile(mContext, "OrderResult.json");
//			result = JsonUtil.toObject(json, OrderResultEntity.class);
            doAlipay(result);
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
            hideDialog();
            payBtn.setEnabled(true);
        }
    };

    //TODO 待测试 支付宝支付
    private void doAlipay(OrderResultEntity result) {
        if (result == null || result.data == null || TextUtils.isEmpty(result.data.orderSn)) {
            toast("获取不到订单号!");
            return;
        }
        AlipayUtil alipayUtil = AlipayUtil.getInstance(mActivity);
        PayEntity pay = new PayEntity();
        pay.outTradeNo = result.data.orderSn;
        pay.subject = result.data.subject;
        if (TextUtils.isEmpty(result.data.subject) && detailEntity != null
                && detailEntity.data != null) {
            pay.subject = detailEntity.data.brandName;
        }
        pay.body = result.data.title;
        pay.totalFee = RebateTool.formatDouble(result.data.orderPrice);
        pay.timeOut = result.data.timeOut;
        if (TextUtils.isEmpty(result.data.subject)) {
            pay.timeOut = "1c";
        }
        alipayUtil.setCallBack(alipayListener);
        alipayUtil.startPay(pay);
    }

    IPayCallBack alipayListener = new IPayCallBack() {

        @Override
        public void onPaySuccess() {
            toast("支付成功");
            Intent intent = new Intent(mContext, RebatePayCommentActivity.class);
            intent.putExtra(RebateKey.KEY_INTENT_ORDER_ID, mEntity.data.orderId);
            intent.putExtra(RebateKey.KEY_INTENT_REBATE_ITEM_ID, detailEntity.data.itemId);
            Log.i("--传给RebatePayCommentActivity的itemId:" + detailEntity.data.itemId);
            intent.putExtra(RebateKey.KEY_INTENT_IS_NEW_ORDER, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
//			if (mActivity instanceof RebatePayActivity) {
//				((RebatePayActivity) mActivity).finish();
//			}
        }

        @Override
        public void onPayFailed() {
            toast("支付失败");
        }
    };

    @Override
    public void onRefresh() {
        if (mCallback != null) {
            mCallback.onRefreshOrderFragment();
        }
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onRefreshFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onRefreshFragmentListener");
        }
    }

    onRefreshFragmentListener mCallback;

    public interface onRefreshFragmentListener {
        /**
         * 通过Activity通信，刷新OrderFragment 信息展示，防止服务员录入错误，用户只能返回上一页重新买单
         *
         * @param entity
         */
        public void onRefreshOrderFragment();
    }

}

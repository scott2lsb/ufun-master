package com.shengshi.rebate.ui.pay;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.ProgressWheel;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.rebate.R;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.bean.BaseEntity;
import com.shengshi.rebate.bean.RebateOrderEntity;
import com.shengshi.rebate.bean.detail.DetailEntity;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.ui.base.RebateBaseFragment;

/**
 * <p>Title:       等待服务员录入操作界面
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-12
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class WaittingFragment extends RebateBaseFragment {

    private String itemId = "";
    private DetailEntity detailEntity;
    private ProgressWheel wheel;

    private RebateRequest request;//发起网络请求

    @Override
    public void initComponents(View view) {
        wheel = (ProgressWheel) view.findViewById(R.id.rebate_pay_waitting_progressBar);
        wheel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                queryPayStatus(false);
            }
        });
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(RebateKey.KEY_INTENT_DETAIL_ENTITY)) {
                detailEntity = (DetailEntity) bundle
                        .getSerializable(RebateKey.KEY_INTENT_DETAIL_ENTITY);
            }
            if (detailEntity != null && detailEntity.data != null) {
                itemId = detailEntity.data.itemId;
            } else {
                detailEntity = new DetailEntity();
            }
        }
    }

    public boolean isRefresh;

    /**
     * 获取服务待支付订单
     */
    public void queryPayStatus(boolean isRefresh) {
        this.isRefresh = isRefresh;
        startCircleAnimate();
        if (request != null && request.isRunningTask()) {
            Log.i("-- 刷新订单task 已经 在后台运行--");
            return;
        }
        wheel.setEnabled(false);
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.order.getPayOrder");
        encryptInfo.resetParams();
        encryptInfo.putParam("item_id", itemId);
        Log.i("请求的itemId是：" + itemId);
        request.setCallback(orderCallback);
        request.execute();
    }

    JsonCallback<RebateOrderEntity> orderCallback = new JsonCallback<RebateOrderEntity>() {

        @Override
        public void onSuccess(RebateOrderEntity result) {
            stopCircleAnimate();
            wheel.setEnabled(true);
            if (result != null && result.errCode != BaseEntity.RESULT_OK) {
                toast(result.errMessage);
                return;
            }
            if (result == null || result.data == null || TextUtils.isEmpty(result.data.orderId)) {
                toast(R.string.net_loading_server_error);
                return;
            }
            if (result.data.status == 1 || Integer.parseInt(result.data.orderId) <= 0) {
                toast("订单号无效," + result.data.msg);
                return;
            }
            showOrderFragment(result);
            //FIXME 使用本地测试数据
//			String json = FileUtils.readAssetsFile(mContext, "orderInfo.json");
//			if (isRefresh) {
//				json = FileUtils.readAssetsFile(mContext, "orderInfo2.json");
//			}
//			result = JsonUtil.toObject(json, RebateOrderEntity.class);
//			showOrderFragment(result);
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
            stopCircleAnimate();
            wheel.setEnabled(true);
//			String json = FileUtils.readAssetsFile(mContext, "orderInfo.json");
//			if(isRefresh){
//				json = FileUtils.readAssetsFile(mContext, "orderInfo2.json");
//			}
//			OrderEntity result = JsonUtil.toObject(json, OrderEntity.class);
//			showOrderFragment(result);
        }
    };

    private void startCircleAnimate() {
        if (!wheel.isSpinning()) {
            wheel.spin();
        }
        wheel.setText("刷新中,请稍候...");
    }

    private void stopCircleAnimate() {
        if (wheel.isSpinning()) {
            wheel.stopSpinning();
        }
        wheel.setText("刷新");
    }

    /**
     * 通过Activity通信，显示OrderFragment，进行购买等下一步操作
     *
     * @param order
     */
    private void showOrderFragment(RebateOrderEntity order) {
        if (mCallback != null) {
            mCallback.onShowOrderFragment(order, detailEntity, isRefresh);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onShowOrderFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onShowOrderFragmentListener");
        }
    }

    onShowOrderFragmentListener mCallback;

    public interface onShowOrderFragmentListener {
        /**
         * 通过Activity通信，显示OrderFragment，进行购买等下一步操作
         *
         * @param entity
         */
        public void onShowOrderFragment(RebateOrderEntity order, DetailEntity detail,
                                        boolean isRefresh);
    }

    @Override
    public int getMainContentViewId() {
        return R.layout.rebate_fragment_pay_waitting;
    }

}

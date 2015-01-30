package com.shengshi.rebate.ui.detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.AutoWrapLinearLayout;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.rebate.R;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.bean.detail.CodeEntity;
import com.shengshi.rebate.bean.detail.DetailEntity;
import com.shengshi.rebate.config.RebateConstants;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.ui.base.RebateBaseFragment;
import com.shengshi.rebate.ui.pay.RebatePayActivity;
import com.shengshi.rebate.utils.AppMgrUtils;
import com.shengshi.rebate.widget.RebateCalendar;

import java.util.List;

/**
 * <p>Title:       购买视图Fragment
 * <p>Description: 说明：
 * <p>button_type: 6种不同动作行为
 * <p>1.获取核销码
 * <p>2.有未完成的支付订单点击按钮继续完成支付
 * <p>3.未开始，已结束或者已下架，按钮处于不可点状态
 * <p>4.非当前指定日，按钮换成日期
 * <p>5.当前未登录
 * <p>6.今日已用过
 * <p>button_msg:根据button_type不同类型时显示不同的文案
 * <p>1.买单赚返利  2.继续支付  3.未开始，已结束或已下架  5.请先登录  6.今天已使用
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-5
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class DetailBuyFragment extends RebateBaseFragment implements OnClickListener {

    TextView buyRebateTitle;
    AutoWrapLinearLayout remindContainer;
    TextView buyBtn;
    RebateCalendar calendar;
    DetailEntity mEntity;

    @Override
    public void initComponents(View view) {
        if (remindContainer == null) {
            remindContainer = (AutoWrapLinearLayout) view
                    .findViewById(R.id.rebate_remind_container);
        }
        buyRebateTitle = (TextView) view.findViewById(R.id.mBuyRebateTitle);
        buyBtn = (TextView) view.findViewById(R.id.mBuyRebateBtn);
        buyBtn.setOnClickListener(this);
        calendar = (RebateCalendar) view.findViewById(R.id.mDetailCalendar);
    }

    /**
     * 填充数据
     *
     * @param entity
     * @param codeFragmentVisiable 用于下拉刷新时，如果返利码可见情况下，按钮文字就固定为"买单领返利"-R.string.rebate_detail_btn_order
     */
    public void fetchData(DetailEntity entity, boolean codeFragmentVisiable) {
        try {
            mEntity = entity;
            buyRebateTitle.setText(entity.data.title);
            addSupportView(entity);
            handleBtn(entity, codeFragmentVisiable);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    private void handleBtn(DetailEntity entity, boolean codeFragmentVisiable) {
        refreshMsgByType(entity, codeFragmentVisiable);
        refreshUiByType(entity);
    }

    /**
     * 更新按钮上文字信息 <br>
     * 优先使用接口传过来的文字信息
     *
     * @param entity
     * @param codeFragmentVisiable
     */
    private void refreshMsgByType(DetailEntity entity, boolean codeFragmentVisiable) {
        String btnMsg = "";
        btnMsg = entity.data.buttonMsg;
        if (codeFragmentVisiable) {
            buyBtn.setText(getString(R.string.rebate_detail_btn_order));
            return;
        }
        if (!TextUtils.isEmpty(btnMsg)) {
            buyBtn.setText(btnMsg);
            return;
        }
        int btnType = entity.data.buttonStatus;
        switch (btnType) {
            case 1://1.获取核销码
                btnMsg = "买单赚返利";
                break;
            case 2://2.有未完成的支付订单点击按钮继续完成支付
                btnMsg = "继续支付";
                break;
            case 3://3.未开始，已结束或者已下架，按钮处于不可点状态
                btnMsg = "未开始，已结束或已下架 ";
                break;
            case 4://4.非当前指定日，按钮换成日期
                btnMsg = "";
                break;
            case 5://5.当前未登录
                btnMsg = "请先登录";
                break;
            case 6://6.今日已用过
                btnMsg = "今日已优惠";
                break;

            default:
                break;
        }
        buyBtn.setText(btnMsg);
    }

    /**
     * 根据buttonType 刷新按钮样式
     *
     * @param entity
     */
    private void refreshUiByType(DetailEntity entity) {
        int btnType = entity.data.buttonStatus;
        switch (btnType) {
            case 1://1.获取核销码,UI默认保存不变
                break;
            case 2://2.有未完成的支付订单点击按钮继续完成支付,UI默认保存不变
                break;
            case 3://3.未开始，已结束或者已下架，按钮处于不可点状态
                buyBtn.setVisibility(View.VISIBLE);
                calendar.setVisibility(View.GONE);
                buyBtn.setBackgroundDrawable(getDrawable(R.drawable.btn_gray_corner_selector));
                buyBtn.setTextColor(getColor(R.color.white));
                buyBtn.setEnabled(false);
                break;
            case 4://4.非当前指定日，按钮换成日期
                buyBtn.setVisibility(View.INVISIBLE);
                calendar.setVisibility(View.VISIBLE);
                //TODO 接口没有返回日期，看是否需要这功能
//			calendar.setText(entity.data.date);
                break;
            case 5://5.当前未登录

                break;
            case 6://6.今日已用过
                buyBtn.setVisibility(View.VISIBLE);
                calendar.setVisibility(View.GONE);
                buyBtn.setBackgroundDrawable(getDrawable(R.drawable.btn_green_corner));
                buyBtn.setTextColor(getColor(R.color.white));
                buyBtn.setTextSize(12);
                buyBtn.setEnabled(true);
                break;

            default:
                break;
        }
    }

    /**
     * 添加 支持手机买单、支持返利体现等提示视图
     *
     * @param entity
     */
    private void addSupportView(DetailEntity entity) {
        remindContainer.removeAllViews();
        List<String> remindList = entity.data.payChoice.rows;
        if (CheckUtil.isValidate(remindList)) {
            for (String tip : remindList) {
                TextView textView = new TextView(mContext);
                textView.setText(tip);
                textView.setTextSize(14);
                textView.setTextColor(getColor(R.color.black));
                textView.setPadding(0, 0, 10, 0);
                Drawable tipIcon = getDrawable(R.drawable.rebate_remarks_icon);
                tipIcon.setBounds(0, 0, tipIcon.getMinimumWidth(), tipIcon.getMinimumHeight());
                textView.setCompoundDrawables(tipIcon, null, null, null);
                textView.setGravity(Gravity.CENTER);
                remindContainer.addView(textView);
            }
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public int getMainContentViewId() {
        return R.layout.rebate_fragment_detail_buy_layout;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (R.id.mBuyRebateBtn == viewId) {
            doBtnAction();
        }

    }

    /**
     * 根据按钮的type，产生相应的action
     *
     * @param mEntity
     */
    private void doBtnAction() {
        if (mEntity == null) {
            return;
        }
        int btnType = mEntity.data.buttonStatus;
        switch (btnType) {
            case 1://1.获取返利码
                requestRebateCode();
                break;
            case 2://2.有未完成的支付订单点击按钮继续完成支付
                Intent intent = new Intent(mContext, RebatePayActivity.class);
                intent.putExtra(RebateKey.KEY_INTENT_ORDER_ID, mEntity.data.orderId);
                intent.putExtra(RebateKey.KEY_INTENT_IS_NEW_ORDER, false);
                startActivity(intent);
                break;
            case 3://3.未开始，已结束或者已下架，按钮处于不可点状态

                break;
            case 4://4.非当前指定日，按钮换成日期

                break;
            case 5://5.当前未登录
                AppMgrUtils.launchAPP(mActivity, AppHelper.getPackageName(mContext),
                        RebateConstants.CLASS_NAME_UFUN, RebateKey.REQUEST_CODE_REFRESH_USER);
                break;
            case 6://6.今日已用过

                break;

            default:
                break;
        }
    }

    private void requestRebateCode() {
        if (mEntity == null) {
            return;
        }
        showDialog("获取返利码中...");
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.item.getCode");
        encryptInfo.resetParams();
        encryptInfo.putParam("item_id", mEntity.data.itemId);
        Log.i("请求的itemId是：" + mEntity.data.itemId);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<CodeEntity> jsonCallback = new JsonCallback<CodeEntity>() {

        @Override
        public void onSuccess(CodeEntity result) {
            //FIXME 使用本地测试数据
//			String json = FileUtils.readAssetsFile(mContext, "code.json");
//			result = JsonUtil.toObject(json, CodeEntity.class);
//			getRebateCode(result);
//			hideDialog();
            //正式代码
            if (result == null || result.data == null || result.data.status != 1) {
                hideDialog();
                toast("获取返利码失败");
                return;
            }
            getRebateCode(result);
            hideDialog();
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
            hideDialog();
        }
    };

    /**
     * 通过Activity通信，刷新CodeFragment，返利码显示视图
     *
     * @param mEntity
     */
    private void getRebateCode(CodeEntity code) {
        if (mCallback != null) {
            mCallback.refreshRebateCode(code);
            updateBuyBtn(code);
        }
    }

    /**
     * 更新按钮
     * 1.按钮文字 改成 "领取返利"
     * 2.跳转到生成订单页面
     */
    protected void updateBuyBtn(final CodeEntity code) {
        buyBtn.setText(getString(R.string.rebate_detail_btn_order));
        buyBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, RebatePayActivity.class);
                intent.putExtra(RebateKey.KEY_INTENT_DETAIL_ENTITY, mEntity);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented the callback interface. If not, it throws an exception
        try {
            mCallback = (OnGetCodeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnGetCodeListener");
        }
    }

    OnGetCodeListener mCallback;

    // Container Activity must implement this interface
    public interface OnGetCodeListener {
        /**
         * 通过Activity通信，刷新CodeFragment，返利码显示视图
         *
         * @param entity
         */
        public void refreshRebateCode(CodeEntity entity);
    }

}

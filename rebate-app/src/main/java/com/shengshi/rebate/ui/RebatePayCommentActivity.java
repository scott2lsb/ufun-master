package com.shengshi.rebate.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.rebate.R;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.bean.RebateOrderEntity;
import com.shengshi.rebate.bean.RespEntity;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.ui.base.RebateBaseActivity;
import com.shengshi.rebate.ui.detail.RebateInfoDetailActivity;

/**
 * <p>Title:    支付成功后，评价
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-20
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebatePayCommentActivity extends RebateBaseActivity implements OnClickListener {

    private TextView getRebateMoney, cardBalance;
    private RatingBar commentRatingBar;
    private EditText commentEdit;
    private CheckBox isAnonymousCheck;
    private TextView postContentBtn, nothingToSayBtn;
    /**
     * 0–不匿名    1–匿名
     */
    int isAnonymous;
    String itemId, orderId;
    boolean isNewOrder;//是否是新订单

    @Override
    protected void initComponents() {
        super.initComponents();
        // 隐藏Activity刚进来焦点在EditText时的键盘显示
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        TopUtil.HideViewByINVISIBLE(mActivity, R.id.rebate_top_btn_return);

        getRebateMoney = findTextViewById(R.id.rebate_comment_get_rebate_money);
        cardBalance = findTextViewById(R.id.rebate_comment_card_balance);

        commentRatingBar = findRatingBarById(R.id.result_comment_ratingBar);

        commentEdit = findEditTextById(R.id.rebate_comment_content);

        nothingToSayBtn = findTextViewById(R.id.rebate_nothing_to_say);
        nothingToSayBtn.setOnClickListener(this);
        postContentBtn = findTextViewById(R.id.rebate_post_content);
        postContentBtn.setOnClickListener(this);

        isAnonymousCheck = findCheckBoxById(R.id.rebate_is_anonymous);
        isAnonymousCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isCheck) {
                if (isCheck) {
                    isAnonymous = 1;
                } else {
                    isAnonymous = 0;
                }
            }
        });

    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_pay_comment;
    }

    @Override
    protected void initData() {
        orderId = getIntent().getStringExtra(RebateKey.KEY_INTENT_ORDER_ID);
        itemId = getIntent().getStringExtra(RebateKey.KEY_INTENT_REBATE_ITEM_ID);//用于回调更新详情页
        Log.i("--RebatePayCommentActivity接收到的itemId:" + itemId);
        isNewOrder = getIntent().getBooleanExtra(
                RebateKey.KEY_INTENT_IS_NEW_ORDER, false);
        Log.i("--传给RebatePayCommentActivity的fromDeatilActivity: " + isNewOrder);
        requestUrl();
    }

    //获取订单信息
    private void requestUrl() {
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

    @Override
    public String getTopTitle() {
        return "恭喜";
    }

    protected void fetchData(RebateOrderEntity entity) {
        if (entity == null || entity.data == null) {
            toast("数据无效");
            return;
        }
        getRebateMoney.setText(entity.data.returnMoney);
        cardBalance.setText(entity.data.userBalance);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rebate_nothing_to_say) {
            //TODO 如果是从详情页进来 处理
            if (isNewOrder) {
                goToDetailActivity();
            } else {
                finish();
            }
        } else if (id == R.id.rebate_post_content) {
            doComment();
        }
    }

    private void doComment() {
        String commentStr = commentEdit.getText().toString();
        if (TextUtils.isEmpty(commentStr)) {
            toast("请输入评论内容");
            return;
        }
        showDialog("数据提交中");
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.order.comment");
        encryptInfo.resetParams();
        encryptInfo.putParam("order_id", orderId);
        encryptInfo.putParam("star", commentRatingBar.getRating());
        encryptInfo.putParam("content", commentStr);
        encryptInfo.putParam("anonymous", isAnonymous);
        request.setCallback(jsonCallback);
        request.execute();
    }

    private void goToDetailActivity() {
        Intent intent = new Intent(mContext, RebateInfoDetailActivity.class);
        intent.putExtra(RebateKey.KEY_INTENT_REBATE_ITEM_ID, itemId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    JsonCallback<RespEntity> jsonCallback = new JsonCallback<RespEntity>() {
        @Override
        public void onSuccess(RespEntity result) {
            hideDialog();
            if (result != null && result.data != null) {
                toast(result.data.msg);
                goToDetailActivity();
            } else {
                toast("评论失败");
            }
        }

        @Override
        public void onFailure(AppException exception) {
            hideDialog();
            toast(exception.getMessage());
        }
    };

}

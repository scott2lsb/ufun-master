package com.shengshi.rebate.ui;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.rebate.R;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.bean.BaseEntity;
import com.shengshi.rebate.bean.RebateCardEntity;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.ui.base.RebateBaseActivity;

/**
 * <p>Title:       同城卡界面
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-5
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateCardActivity extends RebateBaseActivity implements OnClickListener {

    TextView payBtn, recordBtn;
    TextView cardBalance, cardAllMoney;

    @Override
    public String getTopTitle() {
        return getString(R.string.rebate_card_title);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_rebate_card;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        payBtn = findTextViewById(R.id.rebate_card_pay);
        recordBtn = findTextViewById(R.id.rebate_card_record);
        cardBalance = findTextViewById(R.id.rebate_card_balance);
        cardAllMoney = findTextViewById(R.id.rebate_card_all_money);
        payBtn.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        requestUrl();
    }

    private void requestUrl() {
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.user.mine");
        encryptInfo.resetParams();
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<RebateCardEntity> jsonCallback = new JsonCallback<RebateCardEntity>() {

        @Override
        public void onSuccess(RebateCardEntity result) {
            if (result == null) {
                toast(R.string.net_loading_server_error);
                return;
            }
            if (result != null && result.errCode != BaseEntity.RESULT_OK) {
                toast(result.errMessage);
                return;
            }
            fetchData(result);
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e(exception.getMessage());
            toast(exception.getMessage());
        }
    };

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rebate_card_pay) {
            startActivity(new Intent(mContext, NoPayRebateActivity.class));
        } else if (viewId == R.id.rebate_card_record) {
            startActivity(new Intent(mContext, MyRebateActivity.class));
        }

    }

    protected void fetchData(RebateCardEntity entity) {
        if (entity == null || entity.data == null) {
            return;
        }
        String rmbIcon = getString(R.string.rebate_rmb_icon);
        cardBalance.setText(rmbIcon + entity.data.userBalance);
        cardAllMoney.setText(rmbIcon + entity.data.returnTotal);
    }

}

package com.shengshi.rebate.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.ToastUtils;
import com.shengshi.rebate.R;
import com.shengshi.rebate.config.RebateConstants;
import com.shengshi.rebate.ui.RebatePayCommentActivity;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * <p>Title:      微信支付回调
 * <p>Description:
 * 如果是作为library项目，必须把此类放在主项目wxapi包里，并且在主AndroidManifest.xml 设置，注意全路径，不是library项目包名
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-19
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rebate_activity_pay_comment);

        api = WXAPIFactory.createWXAPI(this, RebateConstants.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i(getString(R.string.pay_wechat_result_callback_msg, String.valueOf(resp.errCode)));

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (BaseResp.ErrCode.ERR_USER_CANCEL == resp.errCode) {
                ToastUtils.showToast(this, R.string.pay_wechat_result_user_cancel,
                        Toast.LENGTH_SHORT);
            } else if (BaseResp.ErrCode.ERR_OK == resp.errCode) {
                ToastUtils.showToast(this, R.string.pay_wechat_result_success, Toast.LENGTH_SHORT);
                startActivity(new Intent(this, RebatePayCommentActivity.class));
            } else {
                ToastUtils.showToast(this, R.string.pay_wechat_result_failed, Toast.LENGTH_SHORT);
            }
        }
        finish();
    }

}
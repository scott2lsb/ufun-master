package com.shengshi.ufun.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.shengshi.base.config.Key;
import com.shengshi.base.tools.JsonUtil;
import com.shengshi.base.tools.Log;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.UserInfoEntity;
import com.shengshi.ufun.bean.WeiXinInfoEntity;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.utils.AccountUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.util.Map;

public class LoginActivity extends LifeCircleBaseActivity implements OnClickListener {

    EditText login_mobile;
    EditText login_pwd;
    Button login_btn;
    LinearLayout login_wx;
    WeiXinInfoEntity weixinInfo = new WeiXinInfoEntity();
    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        login_mobile = findEditTextById(R.id.login_mobile);
        login_pwd = findEditTextById(R.id.login_pwd);
        login_btn = findButtonById(R.id.login_btn);
        login_wx = findLinearLayoutById(R.id.login_wx);
        login_btn.setOnClickListener(this);
        login_wx.setOnClickListener(this);
        TopUtil.setOnclickListener(mActivity, R.id.close, this);
        TopUtil.setOnclickListener(mActivity, R.id.login_register, this);
        UIHelper.hideSoftInputMode(login_mobile, this, false);
    }

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.login);
    }

    @Override
    protected void initData() {
    }

    private void requestUrl(String mobile, String password) {
        showTipDialog("登录中...");
        login_btn.setEnabled(false);
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.login_by_mobile");
        encryptInfo.resetParams();
        encryptInfo.putParam("mobile", mobile);
        encryptInfo.putParam("password", password);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<UserInfoEntity> jsonCallback = new JsonCallback<UserInfoEntity>() {

        @Override
        public void onSuccess(UserInfoEntity result) {
            hideTipDialog();
            login_btn.setEnabled(true);
            if (result != null) {
                if (result.errCode == 0) {
                    AccountUtil.saveAccountInfo(mContext, result);
                    setFinish();
                } else {
                    toast(result.errMessage);
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            hideTipDialog();
            login_btn.setEnabled(true);
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

    public void setFinish() {
        Intent intent = new Intent();
        //add by liaodl -- 通信用
        intent.putExtra(Key.KEY_USER_ENTITY_JSON,
                JsonUtil.toJson(AccountUtil.readAccountInfo(getApplicationContext())));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        UIHelper.hideSoftInputMode(login_mobile, this, true);
        if (id == R.id.close) {
            setFinish();
        } else if (id == R.id.login_btn) {
            String mobile = login_mobile.getText().toString();
            String password = login_pwd.getText().toString();
            if (StringUtils.isEmpty(mobile)) {
                toast("请输入" + getString(R.string.login_mobile_hit));
                return;
            }
            if (StringUtils.isEmpty(password)) {
                toast("请输入" + getString(R.string.login_pwd_hit));
                return;
            }
            requestUrl(mobile, password);
        } else if (id == R.id.login_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("tp", 0);
            startActivity(intent);
            finish();
        } else if (id == R.id.login_wx) {
            uMWX();
        }
    }

    /*
     * 微信平台登录
     */
    public void uMWX() {
        showTipDialog("微信登录中...");
        UMWXHandler wxHandler = new UMWXHandler(this, UFunConstants.WX_APPID,
                UFunConstants.WX_APPSECRET);
        wxHandler.addToSocialSDK();
        mController.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                //toast("授权开始");
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                hideTipDialog();
                toast("授权错误");
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                //toast("授权完成");
                //获取相关授权信息
                mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN,
                        new UMDataListener() {
                            @Override
                            public void onStart() {
                                //toast("获取平台数据开始...");
                            }

                            @Override
                            public void onComplete(int status, Map<String, Object> info) {
                                if (status == 200 && info != null) {
                                    /*
									 * StringBuilder sb = new StringBuilder();
									 * Set<String> keys = info.keySet(); for
									 * (String key : keys) { sb.append(key + "="
									 * + info.get(key).toString() + "\r\n"); }
									 * Log.i("--->TestData==" + sb.toString());
									 */
                                    String openid = null;
                                    String nickname = null;
                                    if (info.containsKey("openid")) {
                                        openid = info.get("openid").toString();
                                    }
                                    if (info.containsKey("nickname")) {
                                        nickname = info.get("nickname").toString();
                                    }
                                    weixinInfo.setMap(info);
                                    requestWeiXinUrl(openid, nickname);
                                } else {
                                    //Log.i("--->TestData==发生错误：" + status);
                                    hideTipDialog();
                                    toast("授权失败请重试.");
                                }
                            }
                        });
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                hideTipDialog();
                toast("授权取消");
            }
        });
    }

    /**
     * 微信登录
     *
     * @param openid   微信openid
     * @param nickname 微信昵称
     */
    private void requestWeiXinUrl(String openid, String nickname) {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.login_by_weixin");
        encryptInfo.resetParams();
        encryptInfo.putParam("openid", openid);
        encryptInfo.putParam("nickname", nickname);
        request.setCallback(jsonWeiXinCallback);
        request.execute();
    }

    JsonCallback<UserInfoEntity> jsonWeiXinCallback = new JsonCallback<UserInfoEntity>() {

        @Override
        public void onSuccess(UserInfoEntity result) {
            hideTipDialog();
            if (result != null) {
                if (result.errCode == 0) {
                    if (result.data != null) {
                        if (result.data.uid == 0) { //没绑定
                            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                            intent.putExtra("tp", 1);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("weiXinInfo", weixinInfo);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 1);
                            finish();
                        } else {
                            AccountUtil.saveAccountInfo(mContext, result);
                            setFinish();
                        }
                    }
                } else {
                    toast(result.errMessage);
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            hideTipDialog();
            login_wx.setEnabled(true);
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

    @Override
    public void onBackPressed() {
        UIHelper.hideSoftInputMode(login_mobile, this, true);
        finish();
    }

}

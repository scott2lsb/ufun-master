package com.shengshi.ufun.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.UserInfoEntity;
import com.shengshi.ufun.bean.VerifyEntity;
import com.shengshi.ufun.bean.WeiXinInfoEntity;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.utils.AccountUtil;

import java.util.Map;

public class RegisterActivity extends LifeCircleBaseActivity implements OnClickListener {

    Button register_btn;
    Button register_verify_btn;
    EditText register_mobile;
    EditText register_verify;
    EditText register_nickname;
    EditText register_password;
    CircleImageView register_icon;
    TimeCount time;
    int tp = 0;
    Map<String, Object> weiXinInfo = null;
    String topTitle = null;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        Intent intent = this.getIntent();
        tp = intent.getIntExtra("tp", 0);
        register_btn = findButtonById(R.id.register_btn);
        register_verify_btn = findButtonById(R.id.register_verify_btn);
        register_mobile = findEditTextById(R.id.register_mobile);
        register_verify = findEditTextById(R.id.register_verify);
        register_nickname = findEditTextById(R.id.register_nickname);
        register_password = findEditTextById(R.id.register_password);
        register_btn.setOnClickListener(this);
        register_verify_btn.setOnClickListener(this);

        if (tp == 1) {
            register_icon = (CircleImageView) findViewById(R.id.register_icon);
            WeiXinInfoEntity getWeiXinInfo = (WeiXinInfoEntity) intent
                    .getSerializableExtra("weiXinInfo");
            if (getWeiXinInfo != null) {
                weiXinInfo = getWeiXinInfo.getMap();
                register_icon.setVisibility(View.VISIBLE);
                if (weiXinInfo.containsKey("headimgurl")) {
                    imageLoader.displayImage(weiXinInfo.get("headimgurl").toString(),
                            register_icon, true);
                }
                if (weiXinInfo.containsKey("nickname")) {
                    String nickname = weiXinInfo.get("nickname").toString();
                    int nicknamelength = StringUtils.getWordCount(nickname);
                    if (nicknamelength < 4 || nicknamelength > 14) {
                        nickname = "";
                    }
                    if (!StringUtils.checkNameChese(nickname)
                            && !StringUtils.checkNameEnglish(nickname)) {
                        nickname = "";
                    }
                    register_nickname.setText(nickname);
                }
            }
        }
    }

    @Override
    public String getTopTitle() {
        if (tp == 0) {
            topTitle = getResources().getString(R.string.register);
        } else if (tp == 1) {
            topTitle = getResources().getString(R.string.weixin);
        }
        return topTitle;
    }

    @Override
    protected void initData() {
    }

    /**
     * 获取验证码
     *
     * @param mobile
     */
    private void requestVerifyUrl(String mobile) {
        showTipDialog("获取" + getString(R.string.register_verify_hit));
        setEnabled(1, false);
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.get_verify");
        encryptInfo.resetParams();
        encryptInfo.putParam("mobile", mobile);
        encryptInfo.putParam("type", "register");
        request.setCallback(jsonVerifyCallback);
        request.execute();
    }

    JsonCallback<VerifyEntity> jsonVerifyCallback = new JsonCallback<VerifyEntity>() {

        @Override
        public void onSuccess(VerifyEntity result) {
            hideTipDialog();
            setEnabled(1, true);
            if (result != null) {
                if (result.errCode == 0 && result.data.status) {
                    setEnabled(1, false);
                    time = new TimeCount(60000, 1000); //构造CountDownTimer对象
                    time.start();//开始计时
                    toast(result.data.msg);
                } else {
                    toast(result.errMessage);
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            hideTipDialog();
            setEnabled(1, true);
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

    /* 定义一个倒计时的内部类 */
    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            setEnabled(1, true);
            register_verify_btn.setText(getString(R.string.register_verify_get));
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            register_verify_btn.setText(millisUntilFinished / 1000 + "秒后重发");
        }
    }

    /**
     * 注册
     *
     * @param mobile   手机号码
     * @param verify   验证码
     * @param password 密码
     * @param username 昵称
     * @param cityid   城市id  默认356100
     */
    private void requestRegisterUrl(String mobile, String verify, String password, String username) {
        showTipDialog(topTitle + "中...");
        setEnabled(2, false);
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.register");
        encryptInfo.resetParams();
        encryptInfo.putParam("mobile", mobile);
        encryptInfo.putParam("verify", verify);
        encryptInfo.putParam("password", password);
        encryptInfo.putParam("username", username);
        encryptInfo.putParam("cityid", encryptInfo.getCityid());
        encryptInfo.putParam("regfrom", "local"); //注册来源 默认local
        request.setCallback(jsonRegisterCallback);
        request.execute();
    }

    /**
     * 微信绑定
     *
     * @param mobile   手机号码
     * @param verify   验证码
     * @param password 密码
     * @param username 昵称
     */
    private void requestWeiXinBindUrl(String mobile, String verify, String password, String username) {
        showTipDialog(topTitle + "中...");
        setEnabled(2, false);
        String openid = "";
        String headimgurl = "";
        String nickname = "";
        int sex = 0;
        if (weiXinInfo != null) {
            if (weiXinInfo.containsKey("openid")) {
                openid = weiXinInfo.get("openid").toString();
            }
            if (weiXinInfo.containsKey("headimgurl")) {
                headimgurl = weiXinInfo.get("headimgurl").toString();
            }
            if (weiXinInfo.containsKey("sex")) {
                sex = StringUtils.toInt(weiXinInfo.get("sex").toString());
            }
            if (weiXinInfo.containsKey("nickname")) {
                nickname = weiXinInfo.get("nickname").toString();
            }
        }
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.weixin_bind");
        encryptInfo.resetParams();
        encryptInfo.putParam("type", "register");
        encryptInfo.putParam("openid", openid);
        encryptInfo.putParam("wxnickname", nickname);
        encryptInfo.putParam("headimgurl", headimgurl);
        encryptInfo.putParam("mobile", mobile);
        encryptInfo.putParam("username", username);
        encryptInfo.putParam("verify", verify);
        encryptInfo.putParam("password", password);
        encryptInfo.putParam("cityid", encryptInfo.getCityid());
        encryptInfo.putParam("gender", sex);
        request.setCallback(jsonRegisterCallback);
        request.execute();
    }

    JsonCallback<UserInfoEntity> jsonRegisterCallback = new JsonCallback<UserInfoEntity>() {

        @Override
        public void onSuccess(UserInfoEntity result) {
            hideTipDialog();
            setEnabled(2, true);
            if (result != null) {
                if (result.errCode == 0) {
                    toast(topTitle + "成功");
                    AccountUtil.saveAccountInfo(mContext, result);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    toast(result.errMessage);
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            hideTipDialog();
            setEnabled(2, true);
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

    /* 设置按扭 */
    @SuppressLint("ResourceAsColor")
    public void setEnabled(int tp, Boolean ifshow) {
        if (tp == 1) {
            /*
			 * if (ifshow) {
			 * register_verify_button.setBackgroundResource(R.drawable
			 * .btn_blue); register_verify_button.setTextColor(R.color.white); }
			 * else {
			 * register_verify_button.setBackgroundResource(R.drawable.btn_gray
			 * ); register_verify_button.setTextColor(R.color.black); }
			 * register_verify_button.setPadding(5, 0, 5, 0);
			 */
            register_verify_btn.setEnabled(ifshow);
        } else if (tp == 2) {
			/*
			 * if (ifshow) {
			 * register_btn.setBackgroundResource(R.drawable.btn_blue);
			 * register_btn.setTextColor(R.color.white); } else {
			 * register_btn.setBackgroundResource(R.drawable.btn_gray);
			 * register_btn.setTextColor(R.color.black); }
			 */
            register_btn.setEnabled(ifshow);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        UIHelper.hideSoftInputMode(register_mobile, this, true);
        if (id == R.id.register_btn) {
            String mobile = register_mobile.getText().toString();
            String verify = register_verify.getText().toString();
            String nickname = register_nickname.getText().toString();
            String password = register_password.getText().toString();
            if (StringUtils.isEmpty(mobile)) {
                toast("请输入" + getString(R.string.login_mobile_hit));
                return;
            }
            if (!StringUtils.checkCellphone(mobile)) {
                toast(getString(R.string.register_mobile) + "格式错误");
                return;
            }
            if (StringUtils.isEmpty(verify)) {
                toast("请输入" + getString(R.string.register_verify));
                return;
            }
            if (StringUtils.isEmpty(nickname)) {
                toast("请输入" + getString(R.string.register_nickname));
                return;
            }
            int nicknamelength = StringUtils.getWordCount(nickname);
            if (nicknamelength < 4 || nicknamelength > 14) {
                toast("用户名至少4个字符，最多14个字符\n当前(" + nicknamelength + "字符)");
                return;
            }
            if (!StringUtils.checkNameChese(nickname) && !StringUtils.checkNameEnglish(nickname)) {
                toast("请输入全中文或全英文的昵称");
                return;
            }
            if (StringUtils.isEmpty(password)) {
                toast("请输入" + getString(R.string.login_pwd_hit));
                return;
            }
            if (password.length() < 6) {
                toast("请输入" + getString(R.string.register_pwd_hit));
                return;
            }
            if (tp == 0) {
                requestRegisterUrl(mobile, verify, password, nickname);
            } else if (tp == 1) {
                requestWeiXinBindUrl(mobile, verify, password, nickname);
            }
        } else if (id == R.id.register_verify_btn) {
            String mobile = register_mobile.getText().toString();
            if (StringUtils.isEmpty(mobile)) {
                toast("请输入" + getString(R.string.login_mobile_hit));
                return;
            }
            if (!StringUtils.checkCellphone(mobile)) {
                toast(getString(R.string.register_mobile) + "格式错误");
                return;
            }
            requestVerifyUrl(mobile);
        }
    }

    @Override
    public void onBackPressed() {
        UIHelper.hideSoftInputMode(register_mobile, this, true);
        finish();
    }

}

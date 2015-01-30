package com.shengshi.ufun.ui.mine;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.StatusEntity;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.LoginActivity;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.utils.AccountUtil;

public class MyUpdatePwdActivity extends LifeCircleBaseActivity implements OnClickListener {

    TextView lifecircle_top_right_title;
    EditText ori_pwd;
    EditText new_pwd;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_my_updatepwd;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        lifecircle_top_right_title = findTextViewById(R.id.lifecircle_top_right_title);
        lifecircle_top_right_title.setText("完成");
        lifecircle_top_right_title.setOnClickListener(this);
        ori_pwd = findEditTextById(R.id.ori_pwd);
        new_pwd = findEditTextById(R.id.new_pwd);
    }

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.mine_updatepwd);
    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.lifecircle_top_right_title) {
            String oripwd = ori_pwd.getText().toString();
            String newpwd = new_pwd.getText().toString();
            if (StringUtils.isEmpty(oripwd)) {
                toast("请输入" + getString(R.string.mine_updatepwd_old_hit));
                return;
            }
            if (StringUtils.isEmpty(newpwd)) {
                toast("请输入" + getString(R.string.register_pwd_hit));
                return;
            }
            if (newpwd.length() < 6) {
                toast("请输入" + getString(R.string.register_pwd_hit));
                return;
            }
            UIHelper.hideSoftInputMode(ori_pwd, this, true);
            requestAttentionUrl(oripwd, newpwd);
        }
    }

    /**
     * 关注/取消用户
     */
    private void requestAttentionUrl(String ori_pwd, String new_pwd) {
        showTipDialog("修改中...");
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.update_pwd");
        encryptInfo.resetParams();
        encryptInfo.putParam("mobile", encryptInfo.getMobile());
        encryptInfo.putParam("ori_pwd", ori_pwd);
        encryptInfo.putParam("new_pwd", new_pwd);
        request.setCallback(jsonAttentionCallback);
        request.execute();
    }

    JsonCallback<StatusEntity> jsonAttentionCallback = new JsonCallback<StatusEntity>() {

        @Override
        public void onSuccess(StatusEntity result) {
            hideTipDialog();
            if (result != null) {
                if (result.errCode == 0) {
                    toast(result.data.msg);
                    if (result.data.status) {
                        AccountUtil.removeAccountInfo(MyUpdatePwdActivity.this);
                        Intent intent = new Intent();
                        intent = new Intent(MyUpdatePwdActivity.this, LoginActivity.class);
                        startActivityForResult(intent, 1);
                        finish();
                    }
                } else {
                    toast(result.errMessage);
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            hideTipDialog();
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

}

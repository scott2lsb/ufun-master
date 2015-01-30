package com.shengshi.ufun.ui.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.UserInfoEntity;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.MainActivity;
import com.shengshi.ufun.ui.base.LifeCircleBaseMainActivity;
import com.shengshi.ufun.ui.message.UFunMessageActivity;
import com.shengshi.ufun.utils.AccountUtil;

public class MineActivity extends LifeCircleBaseMainActivity implements OnClickListener {
    RelativeLayout mine_message_rl;
    RelativeLayout mine_topic_rl;
    RelativeLayout mine_favorite_rl;
    //RelativeLayout mine_mine_card_rl;
    //RelativeLayout mine_wallet_rl;
    RelativeLayout mine_setting_rl;
    LinearLayout mine_attention_ll;
    LinearLayout mine_mine_fans_ll;
    LinearLayout mine_score_ll;
    ScrollView mine_scrollview;
    CircleImageView mine_icon;
    RefreshReceiver mRefreshReceiver;
    TextView mine_signature;
    Button mine_login;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mine, menu);
        return true;
    }

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.mine);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        mine_message_rl = findRelativeLayoutById(R.id.mine_message_rl);
        mine_topic_rl = findRelativeLayoutById(R.id.mine_topic_rl);
        mine_favorite_rl = findRelativeLayoutById(R.id.mine_favorite_rl);
        //mine_mine_card_rl = findRelativeLayoutById(R.id.mine_mine_card_rl);
        //mine_wallet_rl = findRelativeLayoutById(R.id.mine_wallet_rl);
        mine_setting_rl = findRelativeLayoutById(R.id.mine_setting_rl);
        mine_attention_ll = findLinearLayoutById(R.id.mine_attention_ll);
        mine_mine_fans_ll = findLinearLayoutById(R.id.mine_mine_fans_ll);
        mine_score_ll = findLinearLayoutById(R.id.mine_score_ll);
        mine_icon = (CircleImageView) findViewById(R.id.mine_icon);
        mine_signature = findTextViewById(R.id.mine_signature);
        mine_login = findButtonById(R.id.mine_login);
        mine_message_rl.setOnClickListener(this);
        //mine_mine_card_rl.setOnClickListener(this);
        //mine_wallet_rl.setOnClickListener(this);
        mine_topic_rl.setOnClickListener(this);
        mine_favorite_rl.setOnClickListener(this);
        mine_setting_rl.setOnClickListener(this);
        mine_icon.setOnClickListener(this);
        mine_mine_fans_ll.setOnClickListener(this);
        mine_attention_ll.setOnClickListener(this);
        mine_login.setOnClickListener(this);
        setReturnBtnEnable(false);
    }

    @Override
    protected void initData() {
        UserInfoEntity mUserInfoEntity = AccountUtil.getMineUserInfo(this);
        setUserInfo(mUserInfoEntity);
        requestUserInfoUrl();
        initBroadCast();
    }

    /**
     * 获取用户个人资料
     */
    private void requestUserInfoUrl() {
        if (!UIHelper.checkLogin(this)) {
            return;
        }
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.get_mine");
        encryptInfo.resetParams();
        request.setCallback(jsonUserInfoCallback);
        request.execute();
    }

    JsonCallback<UserInfoEntity> jsonUserInfoCallback = new JsonCallback<UserInfoEntity>() {

        @Override
        public void onSuccess(UserInfoEntity result) {
            if (result != null) {
                if (UIHelper.checkErrCode(result.errCode, result.errMessage, mActivity)) {
                    return;
                }
                setUserInfo(result);
                AccountUtil.saveMineUserInfo(mContext, result);
            }
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

    /**
     * 显示用户个人资料
     */
    private void setUserInfo(UserInfoEntity user) {
        String username = "未登录";
        //String level = "Lv.0";
        String signature = "关爱、分享、互助、交流";
        String attentions = "0";
        String fans = "0";
        String credit1 = "0";
        if (UIHelper.checkLogin(this)) {
            mine_signature.setVisibility(View.VISIBLE);
            mine_login.setVisibility(View.GONE);
        } else {
            mine_signature.setVisibility(View.GONE);
            mine_login.setVisibility(View.VISIBLE);
        }
        if (user != null && user.data != null) {
            if (!StringUtils.isEmpty(user.data.icon)) {
                imageLoader.displayImage(user.data.icon, mine_icon, true);
            }
            username = user.data.username;
            //level = "Lv." + user.data.level;
            signature = user.data.signature;
            attentions = StringUtils.toString(user.data.attentions);
            fans = StringUtils.toString(user.data.fans);
            credit1 = StringUtils.toString(user.data.credit1);
        } else {
            mine_icon.setImageResource(R.drawable.avatar);
        }
        findTextViewById(R.id.mine_username).setText(username);
        //findTextViewById(R.id.mine_level).setText(level);
        mine_signature.setText(signature);
        findTextViewById(R.id.mine_attention).setText(attentions);
        findTextViewById(R.id.mine_fans).setText(fans);
        findTextViewById(R.id.mine_score).setText(credit1);
        Log.i("------->setUserInfo==" + username);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();
        if (id != R.id.mine_setting_rl) {
            if (!UIHelper.checkLogin(this)) {
                UIHelper.login(this, 1);
                return;
            }
        }
        if (id == R.id.mine_message_rl) {
            intent = new Intent(this, UFunMessageActivity.class);
            startActivity(intent);
        } else if (id == R.id.mine_message_rl) {
            intent = new Intent(this, UFunMessageActivity.class);
            startActivity(intent);
        } else if (id == R.id.mine_topic_rl) {
            intent = new Intent(this, MyTopicActivity.class);
            startActivity(intent);
        } else if (id == R.id.mine_favorite_rl) {
            intent = new Intent(this, MyFavActivity.class);
            startActivity(intent);
        } else if (id == R.id.mine_setting_rl) {
            intent = new Intent(this, MySettingActivity.class);
            startActivityForResult(intent, 1001);
        } else if (id == R.id.mine_icon) {
            intent = new Intent(this, MyPersonalActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.mine_mine_fans_ll) {
            intent = new Intent(this, MyFansActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.mine_attention_ll) {
            intent = new Intent(this, MyAttentionActivity.class);
            startActivityForResult(intent, 1);
        } else if (id == R.id.mine_login) {
            UIHelper.login(this, 1);
        }
    }

    private void initBroadCast() {
        mRefreshReceiver = new RefreshReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(UFunKey.ACTION_REFRESH_MINE_DATA);
        registerReceiver(mRefreshReceiver, mFilter);
    }

    private class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestUserInfoUrl();
            MainActivity.refresh3 = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            requestUserInfoUrl();
        } else if (requestCode == 1001) {
            setUserInfo(null);
            MainActivity.refresh3 = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            UIHelper.Exit(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

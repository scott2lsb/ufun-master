package com.shengshi.ufun.ui.circle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.BaseEntity;
import com.shengshi.ufun.bean.circle.CircleRuleEntity;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.utils.ImageLoader;

public class CircleintroduceActivity extends LifeCircleBaseActivity implements OnClickListener {
    private int qid;
    TextView mtitleTv;
    CircleImageView circleintroduce_quanavatar;
    TextView circleintroduce_quanname;
    TextView circleintroduce_quandesp;
    Button circleintroduce_addbtn;
    CircleImageView circleintroduce_useravatar;
    ImageLoader loader;
    TextView circleintroduce_username;
    TextView circleintroduce_userdesp;
    TextView circleintroduce_quanintroduce;
    TextView circleintroduce_quanrules;
    private int ifjoincircle;

    @Override
    protected void initComponents() {
        super.initComponents();
        loader = ImageLoader.getInstance(mActivity);
        mtitleTv = findTextViewById(R.id.lifecircle_top_title);
        circleintroduce_quanname = findTextViewById(R.id.circleintroduce_quanname);
        circleintroduce_quandesp = findTextViewById(R.id.circleintroduce_quandesp);
        circleintroduce_addbtn = findButtonById(R.id.circleintroduce_addbtn);
        circleintroduce_quanavatar = (CircleImageView) findViewById(R.id.circleintroduce_quanavatar);
        circleintroduce_useravatar = (CircleImageView) findViewById(R.id.circleintroduce_useravatar);
        circleintroduce_username = findTextViewById(R.id.circleintroduce_username);
        circleintroduce_userdesp = findTextViewById(R.id.circleintroduce_userdesp);
        circleintroduce_quanintroduce = findTextViewById(R.id.circleintroduce_quanintroduce);
        circleintroduce_quanrules = findTextViewById(R.id.circleintroduce_quanrules);
        circleintroduce_addbtn.setOnClickListener(this);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_circleintroduce;
    }

    @Override
    public String getTopTitle() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    protected void initData() {
        setReturnBtnEnable(true);
        qid = getIntent().getIntExtra("qid", 0);
        requestUrl();

    }

    private void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("quan.quaninfo");
        encryptInfo.resetParams();
        encryptInfo.putParam("qid", qid);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<CircleRuleEntity> jsonCallback = new JsonCallback<CircleRuleEntity>() {

        @Override
        public void onFailure(AppException arg0) {
            hideLoadingBar();
        }

        @Override
        public void onSuccess(CircleRuleEntity result) {
            hideLoadingBar();
            if (result == null || result.data == null) {
                loadingBar.showFailLayout();
                return;
            }
            fetchData(result);
        }

    };

    protected void fetchData(CircleRuleEntity result) {
        mtitleTv.setText(result.data.quan.qname);
        loader.displayImage(result.data.quan.icon, circleintroduce_quanavatar, true);
        loader.displayImage(result.data.admin.get(0).avatar, circleintroduce_useravatar, true);
        circleintroduce_quanname.setText(result.data.quan.qname);
        circleintroduce_quandesp.setText(result.data.quan.descrip);
        circleintroduce_username.setText(result.data.admin.get(0).username);
        circleintroduce_userdesp.setText(result.data.admin.get(0).signature);
        circleintroduce_quanintroduce.setText(result.data.quan.message);
        circleintroduce_quanrules.setText(result.data.quan.rule);
        ifjoincircle = result.data.quan.ifjoin;
        if (result.data.quan.ifjoin == 1) {
            circleintroduce_addbtn.setText("退出圈子");
            circleintroduce_addbtn.setBackgroundResource(R.drawable.icon_button_grey);
            circleintroduce_addbtn.setTextColor(getResources().getColor(R.color.black));
        } else if (result.data.quan.ifjoin == -1) {
            circleintroduce_addbtn.setText("加入圈子");
            circleintroduce_addbtn.setBackgroundResource(R.drawable.btn_blue);
            circleintroduce_addbtn.setTextColor(getResources().getColor(R.color.white));

        } else {
            circleintroduce_addbtn.setText("正在审核");
            circleintroduce_addbtn.setBackgroundResource(R.drawable.icon_button_grey);
            circleintroduce_addbtn.setTextColor(getResources().getColor(R.color.black));
        }
        circleintroduce_addbtn.setPadding(7, 7, 7, 7);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circleintroduce_addbtn:
                requestJoinUrl();
                break;

            default:
                break;
        }

    }

    /**
     * 加入/取消圈子
     */
    private void requestJoinUrl() {
        String tip = "";
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        if (ifjoincircle == 1) {
            tip = "正在退出圈子，请稍候...";
            encryptInfo.setCallback("quan.quit");
        } else if (ifjoincircle == -1) {
            tip = "正在加入圈子，请稍候...";
            encryptInfo.setCallback("quan.join");
        }
        encryptInfo.resetParams();
        encryptInfo.putParam("qid", qid);
        request.setCallback(jsonAttentionCallback);
        request.execute();
        showTipDialog(tip);
    }

    JsonCallback<BaseEntity> jsonAttentionCallback = new JsonCallback<BaseEntity>() {

        @Override
        public void onSuccess(BaseEntity result) {
            hideTipDialog();
            if (result != null) {
                toast(result.errMessage);
                if (result.errCode == 0) {
                    requestUrl();
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            hideTipDialog();
            toast(exception.getMessage());
        }
    };

}

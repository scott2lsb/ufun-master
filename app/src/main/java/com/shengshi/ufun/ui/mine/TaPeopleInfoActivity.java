package com.shengshi.ufun.ui.mine;

import android.widget.TextView;

import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.mine.TaPeopleEntity.Base;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;

public class TaPeopleInfoActivity extends LifeCircleBaseActivity {

    CircleImageView mine_personal_icon;
    TextView lifecircle_top_right_title;
    TextView mine_personal_birth;
    TextView mine_personal_gender;
    TextView mine_personal_signature;

    String tousername;

    @Override
    public String getTopTitle() {
        return tousername;
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_ta_people_info;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        mine_personal_birth = findTextViewById(R.id.mine_personal_birth);
        mine_personal_gender = findTextViewById(R.id.mine_personal_gender);
        mine_personal_signature = findTextViewById(R.id.mine_personal_signature);
        mine_personal_icon = (CircleImageView) findViewById(R.id.mine_personal_icon);
    }

    @Override
    protected void initData() {
        Base userInfo = (Base) getIntent().getSerializableExtra("userInfo");
        hideLoadingBar();
        setUserInfo(userInfo);
    }

    /**
     * 显示用户个人资料
     */
    private void setUserInfo(Base user) {
        if (user != null) {
            String gender = "保密";
            if (user.gender == 1) {
                gender = "男";
            } else if (user.gender == 2) {
                gender = "女";
            }
            if (!StringUtils.isEmpty(user.icon)) {
                imageLoader.displayImage(user.icon, mine_personal_icon, true);
            }
            tousername = user.username;
            findTextViewById(R.id.mine_personal_username).setText(user.username);
            mine_personal_birth.setText(user.birth);
            mine_personal_gender.setText(gender);
            mine_personal_signature.setText(user.signature);
        }
    }

}

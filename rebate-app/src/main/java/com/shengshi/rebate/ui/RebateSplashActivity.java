package com.shengshi.rebate.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.shengshi.base.animation.ActivityAnimation;
import com.shengshi.base.config.Key;
import com.shengshi.rebate.R;
import com.shengshi.rebate.app.RebateApplication;
import com.shengshi.rebate.ui.home.RebateHomeActivity;
import com.shengshi.rebate.utils.RebateTool;

/**
 * <p>Title:      欢迎页，如果是作为子library,直接跳到主页
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
public class RebateSplashActivity extends Activity {

    boolean isSub;//宿主标志
    String cityCode;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rebate_activity_splash);
        RebateApplication.forceKillFlag = 0;
        getIntentData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        isSub = intent.getBooleanExtra(Key.KEY_INTENT_FROM_PARENT, false);
        cityCode = intent.getStringExtra(Key.KEY_SELECT_CITY_CODE);
        userId = intent.getIntExtra(Key.KEY_USER_ID, 0);
        RebateTool.saveSubFlag(this, isSub);
        RebateTool.saveCityCode(this, cityCode);
        RebateTool.saveUFunUserId(this, userId);
        if (isSub) {
            goHome();
        } else {
            playSplash();
        }
    }

    private void playSplash() {
        View splashImg = findViewById(R.id.splash_img);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        splashImg.startAnimation(animation);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                goHome();
            }

        });
    }

    private void goHome() {
        RebateApplication.forceKillFlag = 1;
        startActivity(new Intent(this, RebateHomeActivity.class));
        ActivityAnimation.pendingTransitionIn(this);
        finish();
    }

}

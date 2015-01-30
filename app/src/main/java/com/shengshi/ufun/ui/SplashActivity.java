package com.shengshi.ufun.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.shengshi.base.animation.ActivityAnimation;
import com.shengshi.base.config.Key;
import com.shengshi.ufun.R;
import com.shengshi.ufun.app.UFunApplication;
import com.shengshi.ufun.bean.BannerEntity;
import com.shengshi.ufun.bean.UserInfoEntity;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.utils.AccountUtil;
import com.shengshi.ufun.utils.ImageLoader;
import com.shengshi.ufun.utils.UFunTool;
import com.shengshi.ufun.utils.UfunFile;

/**
 * <p>Title:      欢迎页，如果是作为子library,直接跳到首页
 * <p>Description:
 * <p>@author:  dengbw
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-26
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class SplashActivity extends Activity {

    private boolean isSub;//宿主标志
    private boolean mIsRunning = false;
    private String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ufun_activity_splash);
        UFunApplication.forceKillFlag = 0;
        loadImg();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        UserInfoEntity mUserInfoEntity = AccountUtil.getUserInfoEntity(this);
        isSub = intent.getBooleanExtra(Key.KEY_INTENT_FROM_PARENT, false);
        UFunTool.saveSubFlag(getApplicationContext(), isSub);
        if (mUserInfoEntity != null && mUserInfoEntity.data != null) {
            //LifeCircleTool.saveCityId(this, mUserInfoEntity.data.cityid);
            UFunTool.saveMobile(this, mUserInfoEntity.data.mobile);
        }
        /*
		 * if (isSub) { redirectTo(); } else { playSplash(); }
		 */
    }

    private void loadImg() {
        BannerEntity banner = UfunFile.getBanner(this);
        int show_time = 2000;
        if (banner != null && banner.data != null) {
            String mbimg = banner.data.pic;
            show_time = banner.data.show_time * 1000; //启动停留时间
            url = banner.data.url;
            if (!TextUtils.isEmpty(mbimg) && banner.data.end_time >= StringUtils.getTime()) {
                ImageView appstart_image = (ImageView) findViewById(R.id.splash_img);
                ImageLoader imageLoader = ImageLoader.getInstance(getApplicationContext());
                imageLoader.displayImage(mbimg, appstart_image);
                appstart_image.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!mIsRunning) {
                            //redirectTo(url);
                            redirectTo();
                        }
                    }
                });
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mIsRunning) {
                    //redirectTo(null);
                    redirectTo();
                }
            }
        }, show_time);
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
                redirectTo();
            }

        });
    }

    private void redirectTo() {
        mIsRunning = true;
        UFunApplication.forceKillFlag = 1;
        Intent intent;
        if (TextUtils.isEmpty(UFunTool.getCityId(this))) {
            intent = new Intent(this, ChangeCityActivity.class);
            intent.putExtra("tp", 1);
            startActivity(intent);
        } else {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        ActivityAnimation.pendingTransitionIn(this);
        finish();
    }

}

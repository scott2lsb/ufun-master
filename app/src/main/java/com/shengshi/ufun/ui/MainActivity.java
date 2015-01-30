package com.shengshi.ufun.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.shengshi.base.config.Key;
import com.shengshi.base.tools.JsonUtil;
import com.shengshi.rebate.ui.home.RebateHomeActivity;
import com.shengshi.ufun.R;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.ui.circle.CircleActivity;
import com.shengshi.ufun.ui.home.HomeActivity;
import com.shengshi.ufun.ui.mine.MineActivity;
import com.shengshi.ufun.utils.AccountUtil;
import com.shengshi.ufun.utils.UFunTool;

public class MainActivity extends BaseTabActivity {

    public static TabHost mTabHost;

    // 首页 消息 热推 服务 我的
    private static final String TAB_HOME = "homepage";
    private static final String TAB_CIRCLE = "circle";
    private static final String TAB_CARD = "card";
    private static final String TAB_MINE = "mine";
    public static boolean refresh0 = false;
    public static boolean refresh1 = false;
    public static boolean refresh2 = false;
    public static boolean refresh3 = false;
    private int type = 0;

    TabSpec tab2;//卡片Tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        View v0 = createTabView(getApplicationContext(), R.drawable.main_tabbg_home, getResources()
                .getString(R.string.headline));
        TabSpec tab0 = mTabHost.newTabSpec(TAB_HOME).setIndicator(v0)
                .setContent(new Intent(this, HomeActivity.class));

        View v1 = createTabView(getApplicationContext(), R.drawable.main_tabbg_circle,
                getResources().getString(R.string.circle));
        TabSpec tab1 = mTabHost.newTabSpec(TAB_CIRCLE).setIndicator(v1)
                .setContent(new Intent(this, CircleActivity.class));

        View v2 = createTabView(getApplicationContext(), R.drawable.main_tabbg_card, getResources()
                .getString(R.string.card));
        tab2 = mTabHost.newTabSpec(TAB_CARD).setIndicator(v2)
                .setContent(new Intent(this, RebateHomeActivity.class));

        View v3 = createTabView(getApplicationContext(), R.drawable.main_tabbg_mine, getResources()
                .getString(R.string.mine));
        TabSpec tab3 = mTabHost.newTabSpec(TAB_MINE).setIndicator(v3)
                .setContent(new Intent(this, MineActivity.class));

        // 头条
        mTabHost.addTab(tab0);
        // 圈子
        mTabHost.addTab(tab1);
        // 卡片
        mTabHost.addTab(tab2);
        // 我的
        mTabHost.addTab(tab3);

        mTabHost.setCurrentTab(type);

        v0.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (refresh0) {
                    sendBroadcast(0);
                }
                mTabHost.setCurrentTab(0);
            }
        });

        v1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refresh1) {
                }
                mTabHost.setCurrentTab(1);
            }
        });

        v2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//				if (UIHelper.checkLogin(MainActivity.this)) {
//					Intent intent = new Intent(getApplicationContext(), RebateHomeActivity.class);
//					intent.putExtra(Key.KEY_INTENT_FROM_PARENT, true);
//					intent.putExtra(Key.KEY_SELECT_CITY_CODE,
//							UFunTool.getCityId(getApplicationContext()));
//					intent.putExtra(Key.KEY_USER_ENTITY_JSON,
//							JsonUtil.toJson(AccountUtil.readAccountInfo(getApplicationContext())));
//					tab2.setContent(intent);
//					mTabHost.setCurrentTab(2);
//				} else {
//					UIHelper.login(MainActivity.this, 1);
//				}
                Intent intent = new Intent(getApplicationContext(), RebateHomeActivity.class);
                intent.putExtra(Key.KEY_INTENT_FROM_PARENT, true);
                intent.putExtra(Key.KEY_SELECT_CITY_CODE,
                        UFunTool.getCityId(getApplicationContext()));
                intent.putExtra(Key.KEY_USER_ENTITY_JSON,
                        JsonUtil.toJson(AccountUtil.readAccountInfo(getApplicationContext())));
                tab2.setContent(intent);
                mTabHost.setCurrentTab(2);
            }
        });

        v3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refresh3) {
                    sendBroadcast(3);
                }
                mTabHost.setCurrentTab(3);
            }
        });

    }

    /**
     * @param context
     * @param imageResource
     * @param textRes
     * @param bgRes
     * @return
     */
    private View createTabView(Context context, int imageResource, String textRes) {

        View view = LayoutInflater.from(context).inflate(R.layout.main_tab_item, null);

        Drawable drawable = getResources().getDrawable(imageResource);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        TextView tabText = (TextView) view.findViewById(R.id.tv_tab_text);
        tabText.setText(textRes);
        tabText.setCompoundDrawables(null, drawable, null, null);

        return view;
    }

    /*
     * 设置是刷新数据
     */
    private void sendBroadcast(int tp) {
        Intent intent = new Intent();
        if (tp == 0) {
            intent.setAction(UFunKey.ACTION_REFRESH_HOME_DATA);
        } else if (tp == 3) {
            intent.setAction(UFunKey.ACTION_REFRESH_MINE_DATA);
        }
        sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 0) {
            sendBroadcast(0);
            mTabHost.setCurrentTab(0);
        } else if (requestCode == 3) {
            sendBroadcast(3);
            mTabHost.setCurrentTab(3);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            UIHelper.Exit(this);
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}

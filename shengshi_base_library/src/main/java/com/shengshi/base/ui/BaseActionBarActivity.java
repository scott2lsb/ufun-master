package com.shengshi.base.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Toast;

import com.shengshi.base.app.BaseApplication;
import com.shengshi.base.tools.AppManager;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p>Title:       ActionBarActivity基类
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-9
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public abstract class BaseActionBarActivity extends ActionBarActivity {

    protected Context mContext;
    protected Activity mActivity;
    protected Resources mRes;
    protected ActionBar mActionBar;
    protected FragmentManager mFragmentManager;
    /**
     * 注意：此处事务是全局的变量，只能commit一次。否则会报：java.lang.IllegalStateException: commit already called异常
     */
    protected FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE); // remove title
        mContext = getApplicationContext();
        mActivity = this;
        mRes = getResources();
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        try {
            mActionBar = getSupportActionBar();
        } catch (Exception e) {
            Log.e("不支持v7包SupportActionBar");
        }
        setOverflowShowingAlways();
        if (BaseApplication.forceKillFlag == -1) {
            protectApp();
        } else {
            if (mActionBar != null) {
                initActionBar();
            }
            if (getMainContentViewId() > 0) {
                setContentView(getMainContentViewId()); // set view
                initComponents(); // init all components
                initData(); // init the whole activity's data
            }
        }
        //FIXME 是否还用这种方法完全退出应用？ - 已修正
        AppManager.getAppManager().addActivity(this);// 添加Activity到堆栈
    }

    /**
     * 得到布局ID
     */
    protected abstract int getMainContentViewId();

    /**
     * 初始化ActionBar
     */
    protected abstract void initActionBar();

    /**
     * 初始化UI组件
     */
    protected abstract void initComponents();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 应用被强杀后，重新走一遍应用启动流程<br>
     * 注：先启动到主页(例如HomeActivity，类名由子类定)，配合Intent.FLAG_ACTIVITY_CLEAR_TOP，保证主页在栈顶也在栈底，然后在主页里启动欢迎页(例如SplashActivity)
     */
    protected abstract void protectApp();

    /**
     * 获取相应的组件的ID
     *
     * @param idName key的名称，如layout的名称：loading_bar
     * @param idType id的类型，IdentifierType
     * @return
     */
    protected int getIdentifier(String idName, String idType) {
        return getResources().getIdentifier(idName, idType, getPackageName());
    }

    public static class IdentifierType {
        public static final String ID = "id";
        public static final String STRING = "string";
        public static final String COLOR = "color";
        public static final String TYPE = "type";
        public static final String DRAWABLE = "drawable";
        public static final String LAYOUT = "layout";
    }

    public void toast(String tip) {
        ToastUtils.showToast(mActivity, tip, Toast.LENGTH_SHORT);
    }

    public void toast(int resId) {
        ToastUtils.showToast(mActivity, resId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示overflow中隐藏的Action按钮的图标
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",
                            Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(e.getMessage(), e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * 反射屏蔽掉物理Menu键，不然在有物理Menu键的手机上，overflow按钮会显示不出来
     */
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
		 * 用这种方法在首页退出应用，横竖屏切换不处理的话
		 * （配置android:configChanges="orientation|keyboardHidden|screenSize"）,
		 * 直接关闭当前页了，给用户异常退出的感觉。 --不推荐
		 */
        //AppManager.getAppManager().finishActivity(this);// 结束Activity&从堆栈中移除
    }
}

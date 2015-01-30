package com.shengshi.base.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import com.shengshi.base.app.BaseApplication;
import com.shengshi.base.tools.AppManager;
import com.shengshi.base.tools.ToastUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * <p>Title:       Activity基类
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
public abstract class BaseActivity extends Activity {

    protected Context mContext;
    protected Activity mActivity;
    protected Resources mRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE); // remove title
        mContext = getApplicationContext();
        mActivity = this;
        mRes = getResources();
        if (BaseApplication.forceKillFlag == -1) {
            protectApp();
        } else {
            if (getMainContentViewId() > 0) {
                setContentView(getMainContentViewId()); // set view
                initComponents(); // init all components
                initData(); // init the whole activity's data
            }
        }
        AppManager.getAppManager().addActivity(this);// 添加Activity到堆栈
    }

    /**
     * 得到布局ID
     */
    protected abstract int getMainContentViewId();

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

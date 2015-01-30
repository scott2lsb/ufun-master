package com.shengshi.base.animation;

import android.app.Activity;

import com.shengshi.base.res.R;

/**
 * <p>Title:     Activity间跳转动画
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
public class ActivityAnimation {

    /**
     * 进入动画
     *
     * @param activity
     */
    public static void pendingTransitionIn(Activity activity) {
        Activity parent = activity.getParent();
        if (parent == null) {
            parent = activity;
        }
        parent.overridePendingTransition(R.anim.push_left_in, R.anim.nothing);
    }

    /**
     * 退出动画
     *
     * @param activity
     */
    public static void pendingTransitionOut(Activity activity) {
        Activity parent = activity.getParent();
        if (parent == null) {
            parent = activity;
        }
        activity.overridePendingTransition(R.anim.nothing, R.anim.push_right_out);
    }

    /**
     * 首页到几个特殊的activity跳转动画
     *
     * @param activity
     */
    public static void homePendingTransitionIn(Activity activity) {
        activity.overridePendingTransition(R.anim.bottom_push_up_in, R.anim.nothing);
    }

    /**
     * 首页到几个特殊的activity跳转动画
     *
     * @param activity
     */
    public static void homePendingTransitionOut(Activity activity) {
        activity.overridePendingTransition(R.anim.nothing, R.anim.bottom_push_up_out);
    }
}

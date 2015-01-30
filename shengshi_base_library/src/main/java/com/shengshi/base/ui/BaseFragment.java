package com.shengshi.base.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shengshi.base.tools.ToastUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * <p>Title:       Fragment基类
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-8
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;
    protected Context mContext;
    protected Resources mRes;
    protected LayoutInflater mInflater;
    private View rootView;//缓存Fragment view,切换Fragment时避免重复加载UI
    protected boolean isForceRefresh;
    private String mPageName;//umeng统计fragment用

    /**
     * 在Fragment里拿到view参数，做初始化组件动作。
     * 不能直接用findViewById(),而是要用view.findViewById().
     *
     * @param view
     */
    public abstract void initComponents(View view);

    public abstract void initData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = getActivity();
        this.mContext = getActivity().getApplicationContext();
        this.mRes = getResources();
        mPageName = this.getClass().getName();
    }

    //切换Fragment时避免重复加载UI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (isForceRefresh) {
            rootView = null;
        }
        if (rootView == null) {
            rootView = inflater.inflate(getMainContentViewId(), null);
            mInflater = inflater;
            initComponents(rootView);
            initData();
        } else {//缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    public abstract int getMainContentViewId();

    public void setForceRefresh(boolean isForceRefresh) {
        this.isForceRefresh = isForceRefresh;
    }

    public void toast(String tip) {
        ToastUtils.showToast(getActivity(), tip, Toast.LENGTH_SHORT);
    }

    public void toast(int resId) {
        ToastUtils.showToast(getActivity(), resId, Toast.LENGTH_SHORT);
    }

    public int getColor(int colorId) {
        return mRes.getColor(colorId);
    }

    public Drawable getDrawable(int drawableId) {
        return mRes.getDrawable(drawableId);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }

}

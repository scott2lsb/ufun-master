package com.shengshi.base.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.view.ViewGroup;

import com.shengshi.base.tools.Log;

/**
 * <p>Title:       BasePagerActivity,继承BaseActivity
 * <p>Description:    含有ViewPager的ActionBarActivity，如果没有使用ViewPager，请直接继承BaseActivity
 * <p>用法：上层在getMainContentViewId()方法里返回的布局文件id，此布局文件的ViewPager控件id要是 <strong> R.id.mGeneralViewPager </strong>
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time:      2014-10-10
 * <p>@author:
 * <p>Update Time:      2014-11-23
 * <p>Updater:          liaodl
 * <p>Update Comments:  更新ViewPager缓存机制，采用Fragment系统api方法setUserVisibleHint()
 */
public abstract class BasePagerActivity extends BaseActionBarActivity implements
        OnPageChangeListener, android.support.v7.app.ActionBar.TabListener {

    protected ViewPager mPager;
    protected GeneralPagerAdapter mAdapter;

//	private HashMap<Integer, BasePagerFragment> mCachedFragments = new HashMap<Integer, BasePagerFragment>();
//	private HashMap<Integer, Boolean> mCachedFragmentStatus = new HashMap<Integer, Boolean>();

    @Override
    protected void initComponents() {
        int pagerId = getIdentifier("mGeneralViewPager", IdentifierType.ID);
        mPager = (ViewPager) findViewById(pagerId/* R.id.mGeneralViewPager */);
        if (mPager != null) {
            if (setOffscreenPageLimit() == 0) {
                mPager.setOffscreenPageLimit(2);
            } else {
                mPager.setOffscreenPageLimit(setOffscreenPageLimit());
            }
            mPager.setOnPageChangeListener(this);
            mAdapter = new GeneralPagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mAdapter);
        }
        if (mActionBar != null) {
            initializeTabs();
        }
    }

    /**
     * 子类适当重写，大数据缓存页面不宜过多，造成内存溢出
     *
     * @return
     */
    protected int setOffscreenPageLimit() {
        return 2;
    }

    protected abstract void initializeTabs();

    protected abstract BasePagerFragment getFragmentAtIndex(int index);

    protected abstract int getFragmentCount();

    /**
     * 实现ViewPager只加载当前显示的一页
     */
    public class GeneralPagerAdapter extends FragmentStatePagerAdapter {

        public GeneralPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {//先执行instantiateItem(),再执行getItem()
            Log.i("@getItem: " + i);
            BasePagerFragment fragment = getFragmentAtIndex(i);
//			mCachedFragments.put(i, fragment);
//			mCachedFragmentStatus.put(i, false);
            return fragment;
        }

        @Override
        public int getCount() {
            return getFragmentCount();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
//			mCachedFragments.remove(position);
//			mCachedFragmentStatus.remove(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
//			if (mCachedFragmentStatus.containsKey(position) && !mCachedFragmentStatus.get(position)) {
//				boolean status = mCachedFragments.get(position).prepareFetchData();
//				mCachedFragmentStatus.put(position, status);
//			}
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int index) {
        if (mActionBar != null) {
            mActionBar.setSelectedNavigationItem(index);
        }
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (mPager != null) {
            mPager.setCurrentItem(tab.getPosition());
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    /**
     * 此处返回可能会返回null
     *
     * @return
     */
    public ViewPager getViewPager() {
        return mPager;
    }

    public GeneralPagerAdapter getPagerAdapter() {
        return mAdapter;
    }

    public ActionBar getmActionBar() {
        return mActionBar;
    }

}

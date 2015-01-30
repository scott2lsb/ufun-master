package com.shengshi.rebate.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.base.ui.widget.swipeback.SwipeBackPagerActivity;
import com.shengshi.base.widget.CustomProgressDialog;
import com.shengshi.base.widget.LoadingBar;
import com.shengshi.base.widget.XScrollView;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuListView;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.rebate.R;
import com.shengshi.rebate.ui.pay.OrderFragment;
import com.shengshi.rebate.utils.RebateImageLoader;

/**
 * <p>Title:       RebateBaseActivity
 * <p>Description:  为了保持UI的一致性，返利卡项目里的Activity必须继承此 类
 * <p>1.定义了通用的图片加载工具类    imageLoader
 * <p>2.定义了头部工具类TopUtil，引入<include layout="@layout/rebate_topbar" />使用
 * <p>3.定义了通用的加载进度条  loadingBar，子类通过调用相应方法显隐藏 和 设置提示信息 变更
 * <p>①布局引入<include layout="@layout/widget_loading_bar_layout" />
 * <p>②不用上面布局引入，在自己xml布局里直接引入com.shengshi.base.widget.LoadingBar 类，如：
 * <com.shengshi.base.widget.LoadingBar
 * android:id="@id/mGeneralLoadingBar"
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 * android:gravity="center"
 * android:orientation="horizontal" />
 * <p>③一些关键方法，如：showLoadingBar() hideLoadingBar() showFailLayout();具体可参考HomeActivity里用到的。
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-2
 * <p>@author:
 * <p>Update Time:  2014-12-3
 * <p>Updater:      liaodl
 * <p>Update Comments:  强制子类要重写 获取标题 方法
 */
public abstract class RebateBaseActivity extends SwipeBackPagerActivity {

    public RebateImageLoader imageLoader;
    private LoadingBar loadingBar;
    public CustomProgressDialog loadingDialog;
    private TextView returnView;
    private TextView topTitleView;
    private boolean mReturnEnable = true; // 默认显示
    private OnClickListener onReturnClickListener;// 返回监听

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        imageLoader = RebateImageLoader.getInstance(getApplicationContext());//保证优先初始化
        super.onCreate(savedInstanceState);
        setReturnBtnEnable();
        setTopTitle();
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        initLoadingBar();//保证loadingBar在子类onCreate之前初始化
    }

    private void initLoadingBar() {
        loadingBar = findGeneralLoadingBar();
        if (loadingBar == null) {
            return;
        }
        loadingBar.setFailLayoutOnClick(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loadingBar.show();
                requestAgain();
            }
        });
    }

    /**
     * 子类重写该方法，点击屏幕，可以重写加载数据，用于加载数据失败，重试情景
     * <p>前提：
     * <p>1.引入LoadingBar布局,<include layout="@layout/widget_loading_bar_layout" />
     * <p>2.使用loadingBar.showFailLayout();显示重试视图
     */
    public void requestAgain() {
    }

    public void showLoadingBar() {
        if (loadingBar != null) {
            loadingBar.show();
        }
    }

    public void showFailLayout() {
        if (loadingBar != null) {
            loadingBar.showFailLayout();
        }
    }

    public void showFailLayout(String msg) {
        //FIXME 提示图片 最后统一换成logo
        showFailLayout(msg, R.drawable.loadrefresh1);
    }

    public void showFailLayout(String msg, int resId) {
        if (loadingBar != null) {
            loadingBar.setLoadingPic(resId);
            loadingBar.showFailLayout(msg);
        }
    }

    public void showFailLayout(String msg, OnClickListener onlickListener) {
        if (loadingBar != null) {
            loadingBar.showFailLayout(msg, onlickListener);
        }
    }

    public void hideLoadingBar() {
        if (loadingBar != null) {
            loadingBar.hide();
        }
    }

    /**
     * 是否启用返回按钮
     *
     * @param enable
     */
    public void setReturnBtnEnable(boolean enable) {
        if (!enable) {
            mReturnEnable = enable;
        }
    }

    /**
     * 设置返回键
     */
    private void setReturnBtnEnable() {
        returnView = (TextView) findViewById(getIdentifier("rebate_top_btn_return",
                IdentifierType.ID));
        if (returnView != null) {
            if (!mReturnEnable) {
                returnView.setVisibility(View.INVISIBLE);
            } else {
                returnView.setVisibility(View.VISIBLE);
                returnView.setOnClickListener(new OnReturnClickListener());
            }
        }
    }

    /**
     * 设置返回点击监听，替代原来监听
     *
     * @param listener
     */
    public void setOnReturnClickListener(OnClickListener listener) {
        onReturnClickListener = listener;
    }

    private class OnReturnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (onReturnClickListener != null) {
                onReturnClickListener.onClick(v);
            } else {
                onBackPressed();
            }
        }
    }

    /**
     * 要动态更新或重新设置标题时，子类要显式的调用该方法
     * 注：如果子类覆写了getTopTitle() 和 getTopTitleResourceId(),
     * 以getTopTitle()取得值为准，如果为空，再去取getTopTitleResourceId里的值
     */
    public void setTopTitle() {
        setTopTitle("");
    }

    public void setTopTitle(String title) {
        if (topTitleView == null) {
            topTitleView = findTextViewById(R.id.rebate_top_title);
        }
        if (topTitleView != null) {
            topTitleView.setSelected(true);
            if (!TextUtils.isEmpty(title)) {
                topTitleView.setText(title);
            } else if (!TextUtils.isEmpty(getTopTitle())) {
                topTitleView.setText(getTopTitle());
            } else {
                if (getTopTitleResourceId() > 0) {
                    topTitleView.setText(getTopTitleResourceId());
                }
            }
        }
    }

    /**
     * 子类重写该方法，传入标题
     *
     * @return
     */
    public abstract String getTopTitle();

    /**
     * 子类重写该方法，传入标题
     *
     * @return
     */
    public int getTopTitleResourceId() {
        return 0;
    }

    @Override
    protected void protectApp() {
//		Log.d(getClass().getSimpleName() + " protectApp");
//		Intent intent = new Intent(this, RebateHomeActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
//		finish();
    }

    @Override
    protected void initActionBar() {
        //设置是否显示应用程序的标题
        mActionBar.setDisplayShowTitleEnabled(false);
        //设置是否显示应用程序的图标
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayUseLogoEnabled(false);
        //将应用程序图标设置为可点击的按钮
        mActionBar.setHomeButtonEnabled(false);
        //将应用程序图标设置为可点击的按钮,并且在图标上添加向左的箭头
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                /**
                 *  <meta-data
                 *  android:name="android.support.PARENT_ACTIVITY"
                 *  android:value="父类"
                 *  没在AndroidManifest.xml设置父类时，直接finish
                 */
                if (upIntent == null) {
                    finish();
                    return false;
                }
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent)
                            .startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//	public Drawable getDrawable(int drawableId) {
//		if (mRes == null && drawableId < 0) {
//			return null;
//		}
//		return mRes.getDrawable(drawableId);
//	}

    public int getColor(int colorId) {
        if (mRes == null && colorId < 0) {
            return 0;
        }
        return mRes.getColor(colorId);
    }

    /**
     * 显示加载对话框<br/>
     * 默认文字为：数据加载中，请稍候...
     */
    public void showDialog() {
        showDialog("");
    }

    /**
     * 显示自定义提示信息的对话框
     *
     * @param msg
     */
    public void showDialog(String msg) {
        loadingDialog = CustomProgressDialog.getDialog(mActivity);
        if (loadingDialog != null) {
            loadingDialog.setCanceledOnTouchOutside(false);
            if (!TextUtils.isEmpty(msg)) {
                loadingDialog.setMessage(msg);
            } else {
                loadingDialog.setMessage(getString(R.string.common_loading));
            }
            if (!mActivity.isFinishing() && !loadingDialog.isShowing()) {
                loadingDialog.show();
            }
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void hideDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * **********************Fragment 管理，子类需自定义实现**************************
     */

    public void turnToFragment(Class<? extends RebateBaseFragment> fragmentClass, String tag,
                               int container) {
        turnToFragment(fragmentClass, tag, null, container, false, null);
    }

    public void turnToFragment(Class<? extends RebateBaseFragment> fragmentClass, String tag,
                               Bundle args, int container) {
        turnToFragment(fragmentClass, tag, args, container, false, null);
    }

    public void turnToFragment(Class<? extends RebateBaseFragment> fragmentClass, String tag,
                               Bundle args, int container, OnSetCustomAnimaitonListener listener) {
        turnToFragment(fragmentClass, tag, args, container, false, listener);
    }

    public void turnToFragment(Class<? extends RebateBaseFragment> fragmentClass, String tag,
                               Bundle args, int container, boolean addToBackStack) {
        turnToFragment(fragmentClass, tag, args, container, addToBackStack, null);
    }

    public void turnToFragment(Class<? extends RebateBaseFragment> fragmentClass, String tag,
                               Bundle args, int container, boolean addToBackStack, OnSetCustomAnimaitonListener lister) {
        turnToFragment(null, fragmentClass, tag, args, container, addToBackStack, lister);
    }

    public void turnToFragment(RebateBaseFragment fromFragment,
                               Class<OrderFragment> toFragmentClass, String tag, Bundle args, int container) {
        turnToFragment(fromFragment, toFragmentClass, tag, args, container, false, null);
    }

    public void turnToFragment(RebateBaseFragment fromFragment,
                               Class<? extends RebateBaseFragment> toFragmentClass, String tag, Bundle args,
                               int container, OnSetCustomAnimaitonListener lister) {
        turnToFragment(fromFragment, toFragmentClass, tag, args, container, false, lister);
    }

    /**
     * 一个页面内的Fragment跳转
     *
     * @param fromFragment    来源fragment
     * @param toFragmentClass 目标fragment
     * @param tag             设置tag，方便查找
     * @param args            类似activity intent bundle 参数
     * @param container       在布局里说明，fragment的加载容器，例子如rebate_activity_pay.xml里的R.id.rebate_pay_fragment_container
     * @param addToBackStack  是否加入返回堆栈
     * @param lister          自定义动画
     */
    public void turnToFragment(RebateBaseFragment fromFragment,
                               Class<? extends RebateBaseFragment> toFragmentClass, String tag, Bundle args,
                               int container, boolean addToBackStack, OnSetCustomAnimaitonListener lister) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        RebateBaseFragment toFragment = (RebateBaseFragment) mFragmentManager
                .findFragmentByTag(tag);
        boolean isFragmentExist = true;
        if (toFragment == null) {
            try {
                isFragmentExist = false;
                toFragment = toFragmentClass.newInstance();
                toFragment.setArguments(new Bundle());
            } catch (Exception e) {
                Log.e(e.getMessage(), e);
            }
        }
        if (args != null && !args.isEmpty()) {
            toFragment.getArguments().putAll(args);
        }
        if (lister != null) {
            lister.setCustomAnimaiton(ft);
        } else {
//			ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
//					android.R.anim.fade_in, android.R.anim.fade_out);
        }
        if (fromFragment != null) {
            if (!toFragment.isAdded()) { // 先判断是否被add过
                ft.hide(fromFragment).add(container, toFragment, tag); // 隐藏当前的fragment，add下一个到Activity中
            } else {
//				ft.hide(fromFragment).show(toFragment); // 隐藏当前的fragment，显示下一个  
                if (isFragmentExist) {
                    ft.hide(fromFragment).detach(toFragment);
                    ft.attach(toFragment);
                }
            }
        } else {
            if (isFragmentExist) {
                ft.replace(container, toFragment);
            } else {
                ft.replace(container, toFragment, tag);
            }
        }
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.commitAllowingStateLoss();
    }

    public interface OnSetCustomAnimaitonListener {
        public void setCustomAnimaiton(FragmentTransaction ft);
    }

    /**
     * **********************查找view，避免子类一直强转**************************
     */

    protected TextView findTextViewById(int id) {
        return (TextView) this.findViewById(id);
    }

    protected EditText findEditTextById(int id) {
        return (EditText) this.findViewById(id);
    }

    protected Button findButtonById(int id) {
        return (Button) this.findViewById(id);
    }

    protected ImageView findImageViewById(int id) {
        return (ImageView) this.findViewById(id);
    }

    protected CheckBox findCheckBoxById(int id) {
        return (CheckBox) this.findViewById(id);
    }

    protected RadioButton findRadioButtonById(int id) {
        return (RadioButton) this.findViewById(id);
    }

    protected ListView findListViewById(int id) {
        return (ListView) this.findViewById(id);
    }

    protected XListView findXListViewById(int id) {
        return (XListView) this.findViewById(id);
    }

    protected XListView findXListView() {
        return (XListView) this.findViewById(R.id.mGeneralListView);
    }

    protected SwipeMenuListView findSwipeMenuListViewById(int id) {
        return (SwipeMenuListView) this.findViewById(id);
    }

    protected SwipeMenuListView findSwipeMenuListViewById() {
        return (SwipeMenuListView) this.findViewById(R.id.mGeneralListView);
    }

    protected XScrollView findXScrollViewById(int id) {
        return (XScrollView) this.findViewById(id);
    }

    protected XScrollView findXScrollView() {
        return (XScrollView) this.findViewById(R.id.mGeneralScrollView);
    }

    protected SurfaceView findSurfaceViewById(int id) {
        return (SurfaceView) this.findViewById(id);
    }

    protected ProgressBar findProgressBarById(int id) {
        return (ProgressBar) this.findViewById(id);
    }

    protected LinearLayout findLinearLayoutById(int id) {
        return (LinearLayout) this.findViewById(id);
    }

    protected RelativeLayout findRelativeLayoutById(int id) {
        return (RelativeLayout) this.findViewById(id);
    }

    protected RatingBar findRatingBarById(int id) {
        return (RatingBar) this.findViewById(id);
    }

    protected FrameLayout findFrameLayoutById(int id) {
        return (FrameLayout) this.findViewById(id);
    }

    protected LoadingBar findLoadingBarById(int id) {
        return (LoadingBar) this.findViewById(id);
    }

    protected LoadingBar findGeneralLoadingBar() {
        return (LoadingBar) this.findViewById(R.id.mGeneralLoadingBar);
    }
}

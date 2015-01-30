package com.shengshi.rebate.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengshi.base.config.Key;
import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.JsonUtil;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.tools.ToastUtils;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.rebate.R;
import com.shengshi.rebate.adapter.HomeAdapter;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.bean.UserInfoEntity;
import com.shengshi.rebate.bean.home.HomeEntity;
import com.shengshi.rebate.config.RebateConstants;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.location.LocationResultMgr;
import com.shengshi.rebate.ui.RebateCardActivity;
import com.shengshi.rebate.ui.RebateSearchActivity;
import com.shengshi.rebate.ui.base.RebateBaseActivity;
import com.shengshi.rebate.ui.home.NewHeaderRebateCardFragment.OnScorllListViewListener;
import com.shengshi.rebate.utils.AccountUtil;
import com.shengshi.rebate.utils.AppMgrUtils;
import com.shengshi.rebate.utils.RebateTool;

/**
 * <p>Title:      主页
 * <p>Description:  针对不同的宿主app，产生相应的action
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-9
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateHomeActivity extends RebateBaseActivity implements OnClickListener,
        IXListViewListener, OnScorllListViewListener {

    boolean isSub;//宿主标志

    XListView mListView;
    HomeAdapter mAdapter;
    HomeEntity mData;
    View topRebateCard;//顶部卡片
    int curPage = 1;
    int totoalCount = 0;

    EditText mSearchEdit;
    NewHeaderRebateCardFragment headerCardFragment;

    boolean isChoseSearch;

    String categoryId = "1";
    String area = "-1";
    String sort = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIntentData(getIntent());//保证在initData()方法执行前面，保证基础数据先初始化
        super.onCreate(savedInstanceState);
        TopUtil.HideViewByINVISIBLE(mActivity, R.id.rebate_top_btn_return);
        setSwipeBackEnable(false);
        isSub = RebateTool.getSubFlag(mContext);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        getIntentData(intent);
    }

    private void getIntentData(Intent intent) {
        isSub = intent.getBooleanExtra(Key.KEY_INTENT_FROM_PARENT, false);
        String cityCode = intent.getStringExtra(Key.KEY_SELECT_CITY_CODE);
        String userJson = intent.getStringExtra(Key.KEY_USER_ENTITY_JSON);
        UserInfoEntity ufunUser = JsonUtil.toObject(userJson, UserInfoEntity.class);
        RebateTool.saveSubFlag(this, isSub);
        RebateTool.saveCityCode(this, cityCode);
        if (ufunUser != null && ufunUser.data != null) {
            AccountUtil.saveAccountInfo(this, ufunUser);
            Log.i("isSub = " + isSub + " ,cityCode = " + cityCode + " ,userId = "
                    + ufunUser.data.uid);
        } else {
            AccountUtil.removeAccountInfo(this);
        }
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        // 隐藏Activity刚进来焦点在EditText时的键盘显示
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initListView();

        initSearchEditView();

        TopUtil.HideViewByGONE(mActivity, R.id.rebate_top_title);
        TopUtil.showView(mActivity, R.id.rebate_search);
        TopUtil.updateRight(mActivity, R.id.rebate_top_right_img, R.drawable.rebate_card_icon);
        TopUtil.setOnclickListener(mActivity, R.id.rebate_top_right_img, this);
    }

    private void initListView() {
        mListView = findXListView();
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader.getImageLoader(), true,
                false));
        View emptyView = View.inflate(mContext, R.layout.widget_no_data, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        ((ViewGroup) mListView.getParent()).addView(emptyView, params);
        mListView.setEmptyView(emptyView);
    }

    private void initSearchEditView() {
        mSearchEdit = findEditTextById(R.id.rebate_search);
        mSearchEdit.setInputType(InputType.TYPE_NULL);
        mSearchEdit.setOnClickListener(this);
        mSearchEdit.clearFocus();
    }

    @Override
    protected void initData() {
        isChoseSearch = false;
        requestUrl(curPage);
    }

    private void requestUrl(int page) {
        requestUrl(page, categoryId, area, sort);//默认请求
    }

    private void requestUrl(int page, String categoryId, String area, String sort) {
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.index.index");
        encryptInfo.resetParams();
        encryptInfo.putParam("page", page);
        encryptInfo.putParam("page_size", RebateConstants.PAGE_SIZE_20);
        encryptInfo.putParam("category_id", categoryId);//类目
        encryptInfo.putParam("area", area);//区域
        encryptInfo.putParam("sort", sort);//排序
        encryptInfo.putParam("type", 0);//搜索类型，默认值为0，表示搜索结果包括今日五折；传1，表示搜索结果不含今日五折
        LocationResultMgr locationResultMgr = LocationResultMgr.getInstance(mContext);
        encryptInfo.putParam("lat", locationResultMgr.getLatitude());//纬度
        encryptInfo.putParam("lng", locationResultMgr.getLongitude());//经度
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<HomeEntity> jsonCallback = new JsonCallback<HomeEntity>() {

        @Override
        public void onSuccess(HomeEntity result) {
            //FIXME 使用本地测试数据
//			hideLoadingBar();
//			String json = "";
//			if (!isChoseSearch) {
//				json = FileUtils.readAssetsFile(mContext, "home.json");
//			} else {
//				json = FileUtils.readAssetsFile(mContext, "home1.json");
//			}
//			result = JsonUtil.toObject(json, HomeEntity.class);
//			fetchData(result);
//			refreshListView();
            //正式代码
            if (result == null || result.data == null) {
                if (result != null && !TextUtils.isEmpty(result.errMessage)) {
                    showFailLayout(result.errMessage);
                } else {
                    showFailLayout();
                }
                return;
            }
            if (!isChoseSearch && !CheckUtil.isValidate(result.data.wzk.data)
                    && !CheckUtil.isValidate(result.data.shop.data)) {
                showFailLayout("暂无数据，点此刷新");
                return;
            }
            hideLoadingBar();
            fetchData(result);
            refreshListView();
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
//			hideLoadingBar();
            showFailLayout("加载失败，点此刷新");
            refreshListView();
            //FIXME 测试代码
//			String json = FileUtils.readAssetsFile(mContext, "home.json");
//			HomeEntity result = JsonUtil.toObject(json, HomeEntity.class);
//			fetchData(result);
//			refreshListView();
        }
    };

    @Override
    protected void protectApp() {
        Log.i(getClass().getSimpleName() + "protectApp");
//		finish();
    }

    protected void fetchData(HomeEntity result) {
        try {
            if (curPage == 1) {
                if (!isChoseSearch) {
                    mData = result;
                    initRebateCard(result);
                    mAdapter = new HomeAdapter(mContext, mData);
                    mListView.setAdapter(mAdapter);
                } else {
                    mAdapter.replaceAll(result);
                    mAdapter.notifyDataSetChanged();
                }
                totoalCount = result.data.shop.count;
                if (totoalCount > mAdapter.getCount()) {
                    mListView.setPullLoadEnable(true);
                } else {
                    mListView.setPullLoadEnable(false);
                }
            } else {
                mAdapter.addData(result);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    private void initRebateCard(HomeEntity entity) throws Exception {
        if (topRebateCard == null) {
            topRebateCard = LayoutInflater.from(mContext).inflate(
                    R.layout.rebate_listview_header_home_card_container, null);
        } else {
            mListView.removeHeaderView(topRebateCard);
        }
        mListView.addHeaderView(topRebateCard);

        headerCardFragment = (NewHeaderRebateCardFragment) mFragmentManager
                .findFragmentById(R.id.card_header_view_container);
        if (headerCardFragment != null) {
            headerCardFragment.refreshData(entity);
        } else {
            Bundle args = new Bundle();
            args.putSerializable(RebateKey.KEY_INTENT_HOME_ENTITY, entity);
            turnToFragment(NewHeaderRebateCardFragment.class,
                    RebateKey.TAG_HEADER_REBATECARD_FRAGMENT, args, R.id.card_header_view_container);
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_home;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int viewId = v.getId();
        if (viewId == R.id.rebate_top_right_img) {
            intent.setClass(this, RebateCardActivity.class);
//			intent.setClass(this, BrowserActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.rebate_search) {
            intent.setClass(this, RebateSearchActivity.class);
            startActivityForResult(intent, RebateConstants.ACTIVITY_REQUEST_CODE);
        }
    }

    private long exitTime = 0;

    /**
     * 在TabActivity中，重写了dispatchKeyEvent()，会导致此方法失效
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isSub && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showToast(mActivity, R.string.btn_exit_sure, Toast.LENGTH_SHORT);
                exitTime = System.currentTimeMillis();
            } else {
                RebateTool.exitApp(mContext);
            }
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                headerCardFragment = (NewHeaderRebateCardFragment) mFragmentManager
                        .findFragmentById(R.id.card_header_view_container);
                if (headerCardFragment != null) {
                    return headerCardFragment.dismissPopupWindow();
                } else {
                    return false;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        RebateTool.saveSubFlag(mContext, false);
        //FIXME 看具体需求
//		AccountUtil.removeAccountInfo(mContext);
        BaseEncryptInfo.getInstance(mContext).clear();
        super.finish();
    }

    @Override
    public void onRefresh() {
        curPage = 1;
        isChoseSearch = false;
        categoryId = "1";
        area = "-1";
        sort = "-1";
        requestUrl(curPage);
    }

    private void refreshListView() {
        if (curPage == 1) {
            mListView.stopRefresh();
            mListView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
        } else {
            mListView.stopLoadMore();
        }
        if (mAdapter != null) {
            if (totoalCount <= mAdapter.getCommonItemCount()) {
                mListView.stopLoadMore();
            } else {
                mListView.setPullLoadEnable(true);
            }
        }
    }

    @Override
    public void onLoadMore() {
        if (totoalCount <= mAdapter.getCommonItemCount()) {
            toast(R.string.rebate_load_more_complete);
            mListView.stopLoadMore();
            mListView.setPullLoadEnable(false);
            return;
        }
        curPage++;
        requestUrl(curPage);
    }

    @Override
    public void requestAgain() {
        super.requestAgain();
        if (AccountUtil.isAccountExist(mContext)) {
            onRefresh();
        } else {
            AppMgrUtils.launchAPP(this, AppHelper.getPackageName(mContext),
                    RebateConstants.CLASS_NAME_UFUN);
        }
    }

    @Override
    public String getTopTitle() {
        return "";
    }

    @Override
    public void onScorllListView() {
        int index = mListView.getFirstVisiblePosition();
//		View v = mListView.getChildAt(0);
//		int top = (v == null) ? 0 : (v.getTop() - mListView.getPaddingTop());
//		mListView.setSelectionFromTop(index, top);
        mListView.setSelectionFromTop(index, -226);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == RebateConstants.ACTIVITY_REQUEST_CODE) {
//			ToastUtils.showToast(mActivity, "关闭搜索页", 500);
            mSearchEdit.clearFocus();
        }
    }

    @Override
    public void onSearchBySelectKeyword(String key, int index) {
        isChoseSearch = true;
        curPage = 1;
        if (index == 0) {
            categoryId = key;
        } else if (index == 1) {
            area = key;
        } else if (index == 2) {
            sort = key;
        }
        requestUrl(curPage, categoryId, area, sort);
    }

}

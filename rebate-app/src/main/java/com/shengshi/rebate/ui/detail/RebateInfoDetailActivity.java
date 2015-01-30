package com.shengshi.rebate.ui.detail;

import android.content.Intent;

import com.shengshi.base.config.Key;
import com.shengshi.base.tools.JsonUtil;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.widget.AutoWrapLinearLayout;
import com.shengshi.base.widget.XScrollView;
import com.shengshi.base.widget.XScrollView.IXScrollViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.rebate.R;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.bean.UserInfoEntity;
import com.shengshi.rebate.bean.detail.CodeEntity;
import com.shengshi.rebate.bean.detail.DetailEntity;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.location.LocationResultMgr;
import com.shengshi.rebate.ui.base.RebateBaseActivity;
import com.shengshi.rebate.ui.detail.DetailBuyFragment.OnGetCodeListener;
import com.shengshi.rebate.utils.AccountUtil;

/**
 * <p>Title:        返利详情
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-4
 * <p>@author:
 * <p>Update Time: 2014-12-15
 * <p>Updater:      liaodl
 * <p>Update Comments:1.去掉评论  2.去掉返利详情title UI
 * <p>Update Time: 2014-12-31
 * <p>Updater:      liaodl
 * <p>Update Comments:1.加上评论  2.接口返回数据结构大调整
 */
public class RebateInfoDetailActivity extends RebateBaseActivity implements IXScrollViewListener,
        OnGetCodeListener {

    XScrollView mScrollView;
    AutoWrapLinearLayout remindContainer;
    String itemId;
    DetailBuyFragment buyFragment;
    DetailCommentFragment commentFragment;
    DetailTipFragment tipFragment;
    DetailMapFragment mapFragment;
    DetailCodeFragment codeFragment;
    DetailEntity mEntity;

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_detail_info;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //TODO 买完单，评价回来这里 的 处理动作
        itemId = intent.getStringExtra(RebateKey.KEY_INTENT_REBATE_ITEM_ID);
        Log.i("评价那边跳转过来，会执行到这里。itemId： " + itemId);
        requestUrl();
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        mScrollView = findXScrollViewById(R.id.rebate_detail_xscrollview);
        mScrollView.setIXScrollViewListener(this);
        buyFragment = (DetailBuyFragment) mFragmentManager
                .findFragmentById(R.id.rebate_detail_info_buy_fragment);
        commentFragment = (DetailCommentFragment) mFragmentManager
                .findFragmentById(R.id.rebate_detail_info_comment_fragment);
        tipFragment = (DetailTipFragment) mFragmentManager
                .findFragmentById(R.id.rebate_detail_info_tip_fragment);
        mapFragment = (DetailMapFragment) mFragmentManager
                .findFragmentById(R.id.rebate_detail_info_map_fragment);
    }

    @Override
    protected void initData() {
        itemId = getIntent().getStringExtra(RebateKey.KEY_INTENT_REBATE_ITEM_ID);
        requestUrl();
    }

    private void requestUrl() {
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.item.getDetail");
        encryptInfo.resetParams();
        encryptInfo.putParam("item_id", itemId);
        Log.i("请求的itemId是：" + itemId);
        LocationResultMgr locationResultMgr = LocationResultMgr.getInstance(mContext);
        encryptInfo.putParam("lat", locationResultMgr.getLatitude());
        encryptInfo.putParam("lng", locationResultMgr.getLongitude());
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<DetailEntity> jsonCallback = new JsonCallback<DetailEntity>() {

        @Override
        public void onSuccess(DetailEntity result) {
            //FIXME 使用本地测试数据
//			String json = FileUtils.readAssetsFile(mContext, "detail.json");
//			result = JsonUtil.toObject(json, DetailEntity.class);
//			fetchData(result);
//			refreshXscrollView();
//			hideLoadingBar();
            //正式代码
            if (result == null) {
                showFailLayout();
                return;
            }
            hideLoadingBar();
            fetchData(result);
            refreshXscrollView();
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
            hideLoadingBar();
            refreshXscrollView();
            //FIXME 测试代码
//			String json = FileUtils.readAssetsFile(mContext, "detail.json");
//			DetailEntity result = JsonUtil.toObject(json, DetailEntity.class);
//			fetchData(result);
//			refreshXscrollView();
//			hideLoadingBar();
        }
    };

    private void refreshXscrollView() {
        mScrollView.stopRefresh();
        mScrollView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
    }

    protected void fetchData(DetailEntity entity) {
        this.mEntity = entity;
        setTopTitle();
        buyFragment.fetchData(entity, getCodeFragmentVisiable());
        commentFragment.fetchData(entity);
        tipFragment.fetchData(entity);
        mapFragment.fetchData(entity);
    }

    private boolean getCodeFragmentVisiable() {
        if (codeFragment == null) {
            return false;
        }
        return codeFragment.isAdded() && codeFragment.isVisible();
    }

    @Override
    public String getTopTitle() {
        String title = "详情";
        try {
            if (mEntity != null) {
                title = mEntity.data.brandName;
            }
        } catch (Exception e) {
            title = "详情";
            Log.e(e.getMessage(), e);
        }
        return title;
    }

    @Override
    public void onRefresh() {
        requestUrl();
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void requestAgain() {
        super.requestAgain();
        onRefresh();
    }

    @Override
    public void refreshRebateCode(CodeEntity entity) {
        codeFragment = (DetailCodeFragment) mFragmentManager
                .findFragmentById(R.id.rebate_detail_code_fragment_container);
        if (codeFragment != null) {
            codeFragment.fetchData(entity);
        } else {
            codeFragment = DetailCodeFragment.newInstance(entity);
            mFragmentTransaction.setCustomAnimations(R.anim.bottom_push_up_in,
                    R.anim.bottom_push_up_out);
            mFragmentTransaction.replace(R.id.rebate_detail_code_fragment_container, codeFragment);
            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("--invoke DetailActivity onActivityResult--requestCode:" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == RebateKey.REQUEST_CODE_REFRESH_USER) {
            Log.i("--Activity REQUEST_CODE_REFRESH_USER success--");
            String userJson = data.getStringExtra(Key.KEY_USER_ENTITY_JSON);
            UserInfoEntity ufunUser = JsonUtil.toObject(userJson, UserInfoEntity.class);
            if (ufunUser != null && ufunUser.data != null) {
                AccountUtil.saveAccountInfo(this, ufunUser);
                Log.i("userId = " + ufunUser.data.uid);
            }
            onRefresh();
        }
    }

}

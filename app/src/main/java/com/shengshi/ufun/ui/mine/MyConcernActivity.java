package com.shengshi.ufun.ui.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengshi.base.tools.FileUtils;
import com.shengshi.base.tools.JsonUtil;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.circle.MyConcernAdapter;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.circle.MyConcernEntity;
import com.shengshi.ufun.bean.circle.MyConcernEntity.Concern;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.ui.circle.CircleintroduceActivity;

public class MyConcernActivity extends LifeCircleBaseActivity implements
        OnClickListener, IXListViewListener, OnItemClickListener {
    XListView mListView;
    MyConcernEntity mData;
    MyConcernAdapter mAdapter;
    int curPage = 1;
    int totoalCount = 0;
    String mcallback;

    @Override
    protected void initComponents() {
        super.initComponents();
        // 隐藏Activity刚进来焦点在EditText时的键盘显示
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mListView = findXListView();
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader
                .getImageLoader(), true, false));

    }

    @Override
    public String getTopTitle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String title = bundle.getString("title");
        mcallback = bundle.getString("mcallback");
        return title;
    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.activity_my_concern;
    }

    @Override
    protected void initData() {
        setReturnBtnEnable(true);

        requestUrl(curPage);

    }

    private void requestUrl(int curPage) {
        // String url = Urls.GET_SERVER_ROOT_URL();
        // LifeCircleRequest request = new LifeCircleRequest(this, url);
        // BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        // encryptInfo.setCallback("quan.index");
        // encryptInfo.resetParams();
        // encryptInfo.putParam("page", curPage);
        // encryptInfo.putParam("page_size", "25");
        // request.setCallback(jsonCallback);
        // request.execute();

        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("city.getlist");
        encryptInfo.resetParams();
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<MyConcernEntity> jsonCallback = new JsonCallback<MyConcernEntity>() {

        @Override
        public void onFailure(AppException arg0) {
            hideLoadingBar();
        }

        @Override
        public void onSuccess(MyConcernEntity result) {
            // TODO Auto-generated method stub
            // FIXME 使用本地测试数据
            hideLoadingBar();
            String json = FileUtils.readAssetsFile(mContext, "myconcern.json");
            result = JsonUtil.toObject(json, MyConcernEntity.class);
            fetchData(result);
            refreshListView();
            // 正式代码
            // if (result == null || result.info == null) {
            // loadingBar.showFailLayout();
            // return;
            // }
            // hideLoadingBar();
            // fetchData(result);
            // refreshListView();
        }

    };

    private void refreshListView() {
        if (curPage == 1) {
            mListView.stopRefresh();
            mListView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
        } else {
            mListView.stopLoadMore();
        }
        if (mAdapter != null) {
            if (totoalCount <= mAdapter.getItemCount()) {
                mListView.stopLoadMore();
            } else {
                mListView.setPullLoadEnable(true);
            }
        }
    }

    protected void fetchData(MyConcernEntity result) {

        try {

            if (curPage == 1) {
                mData = result;
                mAdapter = new MyConcernAdapter(mContext, mData);
                mListView.setAdapter(mAdapter);
            } else {
                mAdapter.addData(result);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    public void onRefresh() {
        curPage = 1;
        requestUrl(curPage);
    }

    @Override
    public void onLoadMore() {
        if (totoalCount <= mAdapter.getItemCount()) {
            toast("已经到底了，没有更多信息了。");
            mListView.stopLoadMore();
            mListView.setPullLoadEnable(false);
            return;
        }
        curPage++;
        requestUrl(curPage);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lifecircle_top_right:
                // Intent intent = new Intent(this, AddCircleActivity.class);
                // startActivity(intent);
                break;

            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        Concern concern = mData.info.result.get(position);
        Intent intent;
        if (concern.ifcircle == 1) {
            intent = new Intent(this, CircleintroduceActivity.class);
            intent.putExtra("uid", "2");
            startActivity(intent);
        } else {
            intent = new Intent(this, TaPeopleActivity.class);
            intent.putExtra("uid", "2");
            startActivity(intent);
        }

    }

}

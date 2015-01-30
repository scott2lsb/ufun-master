package com.shengshi.ufun.ui.circle;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.circle.SpecialroleAdapter;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.circle.SpecialroleEntity;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.LoadH5UrlActivity;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;
import com.shengshi.ufun.ui.mine.TaPeopleActivity;

public class SpecialroleActivity extends LifeCircleBaseListActivity implements
        OnClickListener, IXListViewListener, OnItemClickListener {
    SpecialroleEntity circleIndexEntity;
    SpecialroleAdapter mAdapter;
    int curPage = 1;
    int qid;
    int rid;

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
        TopUtil.updateRightTitle(mActivity, R.id.lifecircle_top_right_title,
                R.string.applyfor_certification);
        TopUtil.setOnclickListener(mActivity, R.id.lifecircle_top_right_title,
                this);
    }

    @Override
    public String getTopTitle() {
        return getIntent().getStringExtra("title");
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_specialrole;
    }

    @Override
    protected void initData() {
        setReturnBtnEnable(true);
        qid = getIntent().getIntExtra("qid", 1);
        rid = getIntent().getIntExtra("rid", 1);
        requestUrl();
    }

    private void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("quan.quanrolelist");
        encryptInfo.resetParams();
        encryptInfo.putParam("rid", rid);
        encryptInfo.putParam("qid", qid);
        encryptInfo.putParam("page", curPage);
        request.setCallback(jsonCallback);
        request.execute();

    }

    JsonCallback<SpecialroleEntity> jsonCallback = new JsonCallback<SpecialroleEntity>() {

        @Override
        public void onFailure(AppException arg0) {
            hideLoadingBar();
        }

        @Override
        public void onSuccess(SpecialroleEntity result) {

            hideLoadingBar();
            if (result == null || result.data == null) {
                // loadingBar.showFailLayout();
                return;
            }
            fetchData(result);
            refreshListView();
        }

    };

    private void refreshListView() {
        if (curPage == 1) {
            mListView.stopRefresh();
            mListView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
        } else {
            mListView.stopLoadMore();
        }

    }

    private void fetchData(SpecialroleEntity result) {
        try {
            circleIndexEntity = result;
            mAdapter = new SpecialroleAdapter(mContext, result);
            mListView.setAdapter(mAdapter);

        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    public void onRefresh() {
        requestUrl();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lifecircle_top_right_title:
                Intent intent = new Intent(this, LoadH5UrlActivity.class);
                String rooturl = UFunUrls.GET_SERVER_ROOT_URL();
                intent.putExtra("url", rooturl + "/h5/rule/joinrole/" + qid + rid);
                startActivity(intent);
                break;

            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        position = position - 1;
        if (circleIndexEntity != null
                && circleIndexEntity.data.list.size() > position) {
            Intent intent = new Intent(this, TaPeopleActivity.class);
            intent.putExtra("uid",
                    circleIndexEntity.data.list.get(position).uid);
            intent.putExtra("username",
                    circleIndexEntity.data.list.get(position).username);
            startActivity(intent);
        }

    }

    @Override
    public void onLoadMore() {
        // TODO Auto-generated method stub

    }

}

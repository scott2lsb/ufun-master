package com.shengshi.ufun.ui.circle;

import android.content.Intent;
import android.view.KeyEvent;
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
import com.shengshi.ufun.adapter.circle.CircleIndexAdapter;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.circle.CircleIndexEntity;
import com.shengshi.ufun.bean.circle.CircleIndexEntity.Circle;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;

import java.util.ArrayList;
import java.util.List;

public class CircleActivity extends LifeCircleBaseListActivity implements OnClickListener,
        IXListViewListener, OnItemClickListener {
    CircleIndexEntity circleIndexEntity;
    CircleIndexAdapter mAdapter;
    int curPage = 1;
    private List<Circle> mcirclelists;

    @Override
    protected void initComponents() {
        super.initComponents();
        // 隐藏Activity刚进来焦点在EditText时的键盘显示
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mListView = findXListView();
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader.getImageLoader(), true,
                false));
        TopUtil.updateRight(mActivity, R.id.lifecircle_top_right, R.drawable.plus);
        TopUtil.setOnclickListener(mActivity, R.id.lifecircle_top_right, this);
    }

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.circle);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_circle;
    }

    @Override
    protected void initData() {
        setReturnBtnEnable(false);
    }

    public void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("quan.index");
        encryptInfo.resetParams();
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<CircleIndexEntity> jsonCallback = new JsonCallback<CircleIndexEntity>() {

        @Override
        public void onFailure(AppException arg0) {
            hideLoadingBar();
        }

        @Override
        public void onSuccess(CircleIndexEntity result) {

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

    private void fetchData(CircleIndexEntity result) {
        try {
            circleIndexEntity = result;
            mAdapter = new CircleIndexAdapter(mContext, result);
            mListView.setAdapter(mAdapter);
            mcirclelists = new ArrayList<CircleIndexEntity.Circle>();
            if (result != null && result.data.join != null) {
                mcirclelists = result.data.join;
            }
            if (result != null && result.data.hot != null) {
                mcirclelists.addAll(result.data.hot);
            }
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
            case R.id.lifecircle_top_right:
                Intent intent = new Intent(this, AddCircleActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent();
        if (mcirclelists.get(position - 1).styleid == 1) {
            intent.setClass(mContext, PureTextCircleActivity.class);
            intent.putExtra("qid", mcirclelists.get(position - 1).qid);
            startActivity(intent);
        } else if (mcirclelists.get(position - 1).styleid == 2) {
            intent.setClass(mContext, PicCircleActivity.class);
            intent.putExtra("qid", mcirclelists.get(position - 1).qid);
            startActivity(intent);
        }
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            UIHelper.Exit(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        requestUrl();
        super.onRestart();
    }
}

package com.shengshi.ufun.ui.circle;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.circle.ActivityCircleAdapter;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.circle.ActivityCircleListEntity;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.LoadH5UrlActivity;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.ui.mine.TaPeopleActivity;
import com.shengshi.ufun.utils.ImageLoader;

public class ActiveListActivity extends LifeCircleBaseActivity implements
        OnClickListener, IXListViewListener, OnItemClickListener {
    XListView mListView;
    ActivityCircleListEntity mData;
    ActivityCircleAdapter mAdapter;
    int curPage = 1;
    int totoalCount = 0;
    CircleImageView avatar;
    TextView unameTV;
    TextView honnorTv;
    TextView rankTv;
    LinearLayout activitycircle_Lv;
    ImageLoader loader;
    private int qid;


    @Override
    protected void initComponents() {
        super.initComponents();
        mListView = findXListViewById(R.id.activity_list_xlist);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader
                .getImageLoader(), true, false));
        TopUtil.updateRightTitle(mActivity, R.id.lifecircle_top_right_title,
                R.string.ranking_rules);
        TopUtil.setOnclickListener(mActivity, R.id.lifecircle_top_right_title, this);
        avatar = (CircleImageView) findImageViewById(R.id.activitycircle_iv);
        unameTV = findTextViewById(R.id.activitycircle_unametv);
        honnorTv = findTextViewById(R.id.activitycircle_leveltv);
        rankTv = findTextViewById(R.id.activitycircle_scoretv);
        activitycircle_Lv = findLinearLayoutById(R.id.activitycircle_Lv);
        loader = ImageLoader.getInstance(this);
    }

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.circle_activitylist);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_active_list;
    }

    @Override
    protected void initData() {
        setReturnBtnEnable(true);
        Intent dataIntent = this.getIntent();
        if (null != dataIntent) {
            qid = dataIntent.getIntExtra("qid", 0);
        }
        requestUrl();

    }

    private void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("quan.memberlist");
        encryptInfo.resetParams();
        encryptInfo.putParam("qid", qid);
        encryptInfo.putParam("page", curPage);
        request.setCallback(jsonCallback);
        request.execute();

    }

    JsonCallback<ActivityCircleListEntity> jsonCallback = new JsonCallback<ActivityCircleListEntity>() {

        @Override
        public void onFailure(AppException arg0) {
            hideLoadingBar();
        }

        @Override
        public void onSuccess(ActivityCircleListEntity result) {
            // TODO Auto-generated method stub
            // FIXME 使用本地测试数据

            if (result == null || result.data == null) {
                showFailLayout(result.errMessage, null);
                return;
            }
            hideLoadingBar();
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

        if (mAdapter != null) {
            if (totoalCount <= mAdapter.getItemCount()) {
                mListView.stopLoadMore();
            } else {
                mListView.setPullLoadEnable(true);
            }
        }
    }

    protected void fetchData(ActivityCircleListEntity result) {

        try {
            activitycircle_Lv.setVisibility(View.VISIBLE);
            // if (curPage == 1) {
            mData = result;
            if (mData.data.member != null) {
                loader.displayImage(mData.data.member.avatar, avatar, true);
                unameTV.setText(mData.data.member.username);
                if (mData.data.member.rank_title != null && mData.data.member.rank_title.equals("")) {
                    honnorTv.setText(mData.data.member.rank_title);
                } else {
                    honnorTv.setVisibility(View.GONE);
                }
                rankTv.setText("排名：" + String.valueOf(mData.data.member.rank) + "| 积分：" + String.valueOf(mData.data.member.credit));
            } else {
                activitycircle_Lv.setVisibility(View.GONE);
            }
            if (mData.data.list != null) {
                totoalCount = mData.data.list.size();
                mAdapter = new ActivityCircleAdapter(mContext, mData);
                mListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            // } else {
            // mAdapter.addData(result);
            // mAdapter.notifyDataSetChanged();
            // }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    public void onRefresh() {
        curPage = 1;
        requestUrl();
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
        requestUrl();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lifecircle_top_right_title:
                Intent intent = new Intent(this, LoadH5UrlActivity.class);
                String rooturl = UFunUrls.GET_SERVER_ROOT_URL();
                intent.putExtra("url", rooturl + "/h5/rule/memberlist/" + qid);
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
        if (mData != null && mData.data.list.size() > position) {
            Intent intent = new Intent(this, TaPeopleActivity.class);
            intent.putExtra("uid", mData.data.list.get(position).uid);
            intent.putExtra("username", mData.data.list.get(position).username);
            startActivity(intent);
        }
    }
}
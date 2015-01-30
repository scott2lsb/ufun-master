package com.shengshi.ufun.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.mine.FavorEntity;
import com.shengshi.ufun.bean.mine.FavorEntity.Threads;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;
import com.shengshi.ufun.ui.circle.CircleTipActivity;

import java.util.ArrayList;
import java.util.List;

public class MyFavActivity extends LifeCircleBaseListActivity implements OnItemClickListener {

    int page = 1;
    int count = 0;
    List<Threads> mData = new ArrayList<Threads>();
    SetAdapter mAdapter;
    int touid = 0;

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.mine_favorite);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_myfav;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        requestUrl();
    }

    /**
     * 用户收藏列表
     */
    private void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("thread.favorlist");
        encryptInfo.resetParams();
        encryptInfo.putParam("page", page);
        encryptInfo.putParam("page_size", UFunConstants.PAGE_SIZE);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<FavorEntity> jsonCallback = new JsonCallback<FavorEntity>() {

        @Override
        public void onSuccess(FavorEntity result) {
            if (result == null || result.data == null) {
                showFailLayout();
                return;
            }
            fetchData(result);
            hideLoadingBar();
            refreshXListView(page, count);
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
            hideLoadingBar();
            refreshXListView(page, count);
        }
    };

    class SetAdapter extends SimpleBaseAdapter<Threads> {

        public SetAdapter(Context context, List<Threads> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.puretext_circle_listitem;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            final Threads mThreads = data.get(position);
            TextView title = holder.getView(R.id.puretext_listitem_title);
            TextView uname = holder.getView(R.id.puretext_listitem_uname);
            TextView time = holder.getView(R.id.puretext_listitem_time);
            TextView count = holder.getView(R.id.puretext_listitem_count);
            ImageView ifpic = holder.getView(R.id.puretext_listitem_ifpic);
            title.setText(mThreads.title);
            uname.setText(mThreads.uname);
            time.setText(mThreads.time);
            count.setText(mThreads.reply_count + "/" + mThreads.scan_count);
            if (mThreads.hasphoto == 1) {
                ifpic.setVisibility(View.VISIBLE);
            } else {
                ifpic.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    protected void fetchData(FavorEntity result) {
        try {
            count = result.data.list.size();
            List<Threads> list = result.data.list;
            if (page == 1) {
                mData.clear();
                mData.addAll(list);
                mAdapter = new SetAdapter(mContext, list);
                mListView.setAdapter(mAdapter);
            } else {
                mData.addAll(list);
                mAdapter.addAll(list);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestUrl();
    }

    @Override
    public void onLoadMore() {
        if (refreshXListView(count)) {
            return;
        }
        page++;
        requestUrl();
    }

    @Override
    public void requestAgain() {
        super.requestAgain();
        onRefresh();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        position = position - 1;
        if (mData != null && mData.size() > position) {
            Intent intent = new Intent(this, CircleTipActivity.class);
            intent.putExtra("tid", mData.get(position).tid);
            startActivity(intent);
        }
    }

}

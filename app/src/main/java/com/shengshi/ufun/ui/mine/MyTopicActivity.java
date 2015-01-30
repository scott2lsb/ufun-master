package com.shengshi.ufun.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.mine.MytopicEntity;
import com.shengshi.ufun.bean.mine.MytopicEntity.Threads;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;
import com.shengshi.ufun.ui.circle.CircleTipActivity;

import java.util.ArrayList;
import java.util.List;

public class MyTopicActivity extends LifeCircleBaseListActivity implements OnClickListener,
        IXListViewListener, OnItemClickListener {
    TextView minetopic_starttv;
    TextView minetopic_jointv;

    List<Threads> mData0 = new ArrayList<Threads>();
    List<Threads> mData1 = new ArrayList<Threads>();
    int page[] = new int[2];
    int count[] = new int[2];
    SetAdapter mAdapter;
    int tp = 0;

    @Override
    protected void initComponents() {
        super.initComponents();
        minetopic_starttv = findTextViewById(R.id.minetopic_starttv);
        minetopic_jointv = findTextViewById(R.id.minetopic_jointv);
        minetopic_starttv.setOnClickListener(this);
        minetopic_jointv.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.mine_topic);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_mytopic;
    }

    @Override
    protected void initData() {
        page[0] = 1;
        page[1] = 1;
        count[0] = 0;
        count[1] = 0;
        requestUrl();
    }

    private void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        if (tp == 0) {
            encryptInfo.setCallback("thread.mythread");
        } else if (tp == 1) {
            encryptInfo.setCallback("thread.mycomment");
        }
        encryptInfo.resetParams();
        encryptInfo.putParam("page", page[tp]);
        encryptInfo.putParam("page_size", UFunConstants.PAGE_SIZE);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<MytopicEntity> jsonCallback = new JsonCallback<MytopicEntity>() {

        @Override
        public void onSuccess(MytopicEntity result) {
            if (result == null || result.data == null) {
                loadingBar.showFailLayout();
                return;
            }
            fetchData(result);
            hideLoadingBar();
            refreshXListView(page[tp], count[tp]);
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
            hideLoadingBar();
            refreshXListView(page[tp], count[tp]);
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

    protected void fetchData(MytopicEntity result) {
        try {
            List<Threads> list;
            if (result.data.list != null) {
                count[tp] = result.data.list.size();
                list = result.data.list;
            } else {
                count[tp] = 0;
                list = new ArrayList<Threads>();
            }
            if (page[tp] == 1) {
                if (tp == 0) {
                    mData0.clear();
                    mData0.addAll(list);
                } else if (tp == 1) {
                    mData1.clear();
                    mData1.addAll(list);
                }
                mAdapter = new SetAdapter(mContext, list);
                mListView.setAdapter(mAdapter);
            } else {
                if (tp == 0) {
                    mData0.addAll(list);
                } else if (tp == 1) {
                    mData1.addAll(list);
                }
                mAdapter.addAll(list);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    public void onRefresh() {
        page[tp] = 1;
        requestUrl();
    }

    @Override
    public void onLoadMore() {
        if (refreshXListView(count[tp])) {
            return;
        }
        page[tp]++;
        requestUrl();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.minetopic_starttv) {
            tp = 0;
            page[0] = 1;
            minetopic_starttv.setTextColor(getResources().getColor(R.color.main_tab_tv_active));
            minetopic_jointv.setTextColor(getResources().getColor(R.color.headding_text));
            minetopic_jointv.setText(getResources().getString(R.string.mine_mytopic_mythread));
            if (mData0 != null && mData0.size() > 0) {
                mAdapter = new SetAdapter(mContext, mData0);
                mListView.setAdapter(mAdapter);
                refreshXListView(page[tp], count[tp]);
            } else {
                showLoadingBar();
                requestUrl();
            }

        } else if (id == R.id.minetopic_jointv) {
            tp = 1;
            page[0] = 1;
            minetopic_jointv.setTextColor(getResources().getColor(R.color.main_tab_tv_active));
            minetopic_starttv.setTextColor(getResources().getColor(R.color.headding_text));
            minetopic_starttv.setText(getResources().getString(R.string.mine_mytopic_mycomment));
            if (mData1 != null && mData1.size() > 0) {
                mAdapter = new SetAdapter(mContext, mData1);
                mListView.setAdapter(mAdapter);
                refreshXListView(page[tp], count[tp]);
            } else {
                showLoadingBar();
                requestUrl();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        position = position - 1;
        if (tp == 0) {
            if (mData0 != null && mData0.size() > position) {
                Intent intent = new Intent(this, CircleTipActivity.class);
                intent.putExtra("tid", mData0.get(position).tid);
                startActivity(intent);
            }
        } else if (tp == 1) {
            if (mData1 != null && mData1.size() > position) {
                Intent intent = new Intent(this, CircleTipActivity.class);
                intent.putExtra("tid", mData1.get(position).tid);
                startActivity(intent);
            }
        }
    }

}

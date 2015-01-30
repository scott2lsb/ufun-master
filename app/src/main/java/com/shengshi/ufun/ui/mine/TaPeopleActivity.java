package com.shengshi.ufun.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.mine.QuansAdapter;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.BaseEntity;
import com.shengshi.ufun.bean.mine.TaPeopleEntity;
import com.shengshi.ufun.bean.mine.TaPeopleEntity.Base;
import com.shengshi.ufun.bean.mine.TaPeopleEntity.Quans;
import com.shengshi.ufun.bean.mine.TaPeopleEntity.Threads;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;
import com.shengshi.ufun.ui.circle.CircleTipActivity;
import com.shengshi.ufun.ui.circle.CircleintroduceActivity;
import com.shengshi.ufun.ui.message.UFunChatActivity;

import java.util.ArrayList;
import java.util.List;

public class TaPeopleActivity extends LifeCircleBaseListActivity implements OnClickListener,
        IXListViewListener, OnItemClickListener {

    List<Threads> mData = new ArrayList<Threads>();
    List<Quans> mQuans = new ArrayList<Quans>();
    Base userInfo = null;
    threadsAdapter mAdapter;
    QuansAdapter mQuansAdapter;
    int uid;
    String username;
    int page = 1;
    int count = 0;
    int is_attention = 0;
    TextView tapeop_attention;
    GridView quans_gridview;
    LinearLayout quans_ll;
    int totalRecord = 0;
    int pagenum = 5;
    Boolean ifquans_all = false;
    int quans_height = 0;
    TextView quans_all;

    @Override
    protected void initComponents() {
        super.initComponents();
        uid = getIntent().getIntExtra("uid", 0);
        username = getIntent().getStringExtra("username");
        tapeop_attention = findTextViewById(R.id.tapeop_attention);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public String getTopTitle() {
        return username;
    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.activity_ta_people;
    }

    @Override
    protected void initData() {
        View headerView = LayoutInflater.from(mContext).inflate(R.layout.activity_ta_people_header,
                null);
        mListView.addHeaderView(headerView);
        requestUrl();
    }

    private void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.get_others");
        encryptInfo.resetParams();
        encryptInfo.putParam("touid", uid);
        encryptInfo.putParam("page", page);
        encryptInfo.putParam("page_size", UFunConstants.PAGE_SIZE);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<TaPeopleEntity> jsonCallback = new JsonCallback<TaPeopleEntity>() {

        @Override
        public void onSuccess(TaPeopleEntity result) {
            if (result == null || result.data == null) {
                loadingBar.showFailLayout();
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

    class threadsAdapter extends SimpleBaseAdapter<Threads> {

        public threadsAdapter(Context context, List<Threads> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.activity_ta_people_listitem;
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

    protected void fetchData(TaPeopleEntity result) {
        try {
            count = 0;
            if (page == 1) {
                initHeaderView(result);
                if (result.data.threads == null) {
                    mAdapter = new threadsAdapter(mContext, mData);
                    mListView.setAdapter(mAdapter);
                }
            }
            if (result.data.threads != null) {
                count = result.data.threads.size();
                List<Threads> mThreads = result.data.threads;
                if (page == 1) {
                    mData.clear();
                    mData.addAll(mThreads);
                    mAdapter = new threadsAdapter(mContext, mData);
                    mListView.setAdapter(mAdapter);
                } else {
                    mData.addAll(mThreads);
                    mAdapter.addAll(mThreads);
                    mAdapter.notifyDataSetChanged();
                }
            }

        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    private void initHeaderView(TaPeopleEntity entity) throws Exception {
        setUserInfo(entity);
        quans_ll = findLinearLayoutById(R.id.quans_ll);
        if (entity.data.quans != null) {
            quans_gridview = (GridView) findViewById(R.id.quans_gridview);
            mQuans.addAll(entity.data.quans);
            mQuansAdapter = new QuansAdapter(mContext, mQuans);
            quans_gridview.setAdapter(mQuansAdapter);
            quans_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
            quans_gridview.setOnItemClickListener(this);
            totalRecord = entity.data.quans.size();
            if (totalRecord > 4) {
                findLinearLayoutById(R.id.quans_all_ll).setVisibility(View.VISIBLE);
                quans_all = findTextViewById(R.id.quans_all);
                quans_all.setOnClickListener(this);
            }
        }

    }

    /**
     * 显示用户个人资料
     */
    private void setUserInfo(TaPeopleEntity entity) {
        CircleImageView mine_icon = (CircleImageView) findViewById(R.id.mine_icon);
        String username = "未登录";
        String level = "Lv.0";
        String signature = "关爱、分享、互助、交流";
        String attentions = "0";
        String fans = "0";
        String credit1 = "0";
        if (entity.data.base != null) {
            userInfo = entity.data.base;
            if (!StringUtils.isEmpty(userInfo.icon)) {
                imageLoader.displayImage(userInfo.icon, mine_icon, true);
            }
            username = userInfo.username;
            level = "Lv." + userInfo.level;
            signature = userInfo.signature;
            attentions = StringUtils.toString(userInfo.attentions);
            fans = StringUtils.toString(userInfo.fans);
            credit1 = StringUtils.toString(userInfo.credit1);
        } else {
            mine_icon.setImageResource(R.drawable.avatar);
        }
        is_attention = entity.data.is_attention;
        tapeop_attention = findTextViewById(R.id.tapeop_attention);
        if (is_attention == 1) {
            tapeop_attention.setText("取消关注");
        } else {
            tapeop_attention.setText("关注Ta");
        }
        tapeop_attention.setOnClickListener(this);
        mine_icon.setOnClickListener(this);
        findTextViewById(R.id.mine_username).setText(username);
        findTextViewById(R.id.mine_level).setText(level);
        findTextViewById(R.id.mine_signature).setText(signature);
        findTextViewById(R.id.mine_attention).setText(attentions);
        findTextViewById(R.id.mine_fans).setText(fans);
        findTextViewById(R.id.mine_score).setText(credit1);
        TopUtil.setOnclickListener(mActivity, R.id.send_message, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_attention_ll, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_mine_fans_ll, this);
    }

    /**
     * 关注/取消用户
     */
    private void requestAttentionUrl() {
        showTipDialog("操作中...");
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        if (is_attention == 1) {
            encryptInfo.setCallback("user.cancel_attention");
        } else {
            encryptInfo.setCallback("user.pay_attention");
        }
        encryptInfo.resetParams();
        encryptInfo.putParam("touid", uid);
        request.setCallback(jsonAttentionCallback);
        request.execute();
    }

    JsonCallback<BaseEntity> jsonAttentionCallback = new JsonCallback<BaseEntity>() {

        @Override
        public void onSuccess(BaseEntity result) {
            hideTipDialog();
            if (result != null) {
                toast(result.errMessage);
                if (result.errCode == 0) {
                    if (is_attention == 1) {
                        is_attention = 0;
                        tapeop_attention.setText("关注Ta");
                    } else {
                        is_attention = 1;
                        tapeop_attention.setText("取消关注");
                    }
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            hideTipDialog();
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

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
    public void onClick(View v) {
        Intent intent = null;
        int id = v.getId();
        if (id == R.id.tapeop_attention) {
            requestAttentionUrl();
        } else if (id == R.id.quans_all) {
            int height = 0;
            int totalPageNum = 0;
            if (ifquans_all) {
                height = quans_height;
                ifquans_all = false;
                quans_all.setText(getString(R.string.expand_all));
            } else {
                quans_height = quans_height == 0 ? quans_gridview.getHeight() + 5 : quans_height;
                totalPageNum = (totalRecord - 1) / pagenum + 1;
                height = (quans_gridview.getHeight() + 5) * totalPageNum;
                ifquans_all = true;
                quans_all.setText("收缩全部");
            }
            quans_ll.setLayoutParams(new LinearLayout.LayoutParams(StringUtils.getDisplayMetrics(
                    this, 0), height));
        } else if (id == R.id.send_message) {
            intent = new Intent(this, UFunChatActivity.class);
            intent.putExtra(UFunKey.KEY_MSG_TOUID, uid);
            intent.putExtra(UFunKey.KEY_MSG_FROM_NAME, username);
            startActivity(intent);
        } else if (id == R.id.mine_attention_ll) {
            intent = new Intent(this, MyAttentionActivity.class);
            intent.putExtra("touid", uid);
            startActivity(intent);
        } else if (id == R.id.mine_mine_fans_ll) {
            intent = new Intent(this, MyFansActivity.class);
            intent.putExtra("touid", uid);
            startActivity(intent);
        } else if (id == R.id.mine_icon) {
            intent = new Intent(this, TaPeopleInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("userInfo", userInfo);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        int viewId = view.getId();
        if (viewId == R.id.circle_ll_items) {
            if (mQuans != null && mQuans.size() > position) {
                Intent intent = new Intent(this, CircleintroduceActivity.class);
                intent.putExtra("qid", mQuans.get(position).qid);
                startActivity(intent);
            }
        } else {
            position = position >= 2 ? position - 2 : 0;
            if (mData != null && mData.size() > position) {
                Intent intent = new Intent(this, CircleTipActivity.class);
                intent.putExtra("tid", mData.get(position).tid);
                startActivity(intent);
            }
        }
    }

}

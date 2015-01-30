package com.shengshi.ufun.ui.circle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.circle.CircleThreadEntity.PostEntity;
import com.shengshi.ufun.bean.circle.CircleThreadEntity.SubReply;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;

import java.util.List;

public class CircleMoreReplyActivity extends LifeCircleBaseListActivity {
    private String title;
    private MoreReplyAdapter mAdapter;
    private PostEntity post;

    //moreReplyHeader
    private View moreReplyHeader;
    private CircleMoreReplyHearderFragment circleMoreReplyHearderFragment;

    @Override
    protected void initComponents() {
        super.initComponents();
    }

    @Override
    protected void initData() {
        showLoadingBar();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(UFunKey.KEY_CIRCLE_MOREREPLY_LIST_BUNDLE);
        post = (PostEntity) bundle.getSerializable(UFunKey.KEY_CIRCLE_MOREREPLY_LIST);
        //标题
        title = getIntent().getStringExtra(UFunKey.KEY_CIRCLE_MOREREPLY_TITLE);
        initHeaderFragment();
        mAdapter = new MoreReplyAdapter(mContext, post.sublist);
        mListView.setAdapter(mAdapter);
        hideLoadingBar();
    }

    private void initHeaderFragment() {
        if (moreReplyHeader == null) {
            moreReplyHeader = LayoutInflater.from(mContext).inflate(
                    R.layout.ufun_activity_tip_morerepley_header_container, null);
        } else {
            mListView.removeHeaderView(moreReplyHeader);
        }
        mListView.addHeaderView(moreReplyHeader);

        circleMoreReplyHearderFragment = (CircleMoreReplyHearderFragment) mFragmentManager
                .findFragmentById(R.id.ufun_tip_morereply_header_container);
        if (circleMoreReplyHearderFragment != null) {
            circleMoreReplyHearderFragment.initAuthorData(post);
        } else {
            Bundle args = new Bundle();
            args.putSerializable(UFunKey.KEY_CIRCLE_MOREREPLY_LIST, post);
            turnToFragment(CircleMoreReplyHearderFragment.class,
                    UFunKey.TAG_TIP_MOREREPLY_HEADER_FRAGMENT, args,
                    R.id.ufun_tip_morereply_header_container);
        }
    }

    @Override
    public String getTopTitle() {
        return title;
    }

    class MoreReplyAdapter extends SimpleBaseAdapter<SubReply> {
        public MoreReplyAdapter(Context context, List<SubReply> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.subreply_item_layout;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            SubReply reply = data.get(position);
            TextView txt = holder.getView(R.id.ufun_subreply_activity_txt);
            txt.setText(updateTextColor(reply.author, reply.contents));
            return convertView;
        }

        private SpannableStringBuilder updateTextColor(String author, String content) {
            SpannableStringBuilder ssBuilder = new SpannableStringBuilder(author + " : " + content);
            ssBuilder.setSpan(new ForegroundColorSpan(mContext.getResources()
                            .getColor(R.color.reply_author_name)), 0, author.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ssBuilder;
        }

    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.ufun_replyactivity;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

}

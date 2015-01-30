package com.shengshi.ufun.ui.circle;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.circle.CircleThreadEntity.PostEntity;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.ui.base.LifeCircleBaseFragment;

public class CircleMoreReplyHearderFragment extends LifeCircleBaseFragment {

    private View publisherLayer;
    private CircleImageView userAvatar;
    private TextView userName;
    private TextView publishTime;
    private TextView rankTitle;
    private TextView operation;
    private TextView content;
    private PostEntity entity;

    @Override
    public void initComponents(View view) {
        publisherLayer = view.findViewById(R.id.ufun_activity_tip_content_publisher_layer);
        content = findTextViewById(view, R.id.lifecircle_tip_replay_content);
        userAvatar = (CircleImageView) view.findViewById(R.id.lifecircle_tips_reply_publisher_avatar);
        userName = findTextViewById(view, R.id.lifecircle_tips_reply_publisher_name);
        publishTime = findTextViewById(view, R.id.lifecircle_tips_reply_publisher_time);
        rankTitle = findTextViewById(view, R.id.lifecircle_tips_reply_rank_title);
        operation = findTextViewById(view, R.id.lifecircle_reply_operation);
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(UFunKey.KEY_CIRCLE_MOREREPLY_LIST)) {
            entity = (PostEntity) bundle.getSerializable(UFunKey.KEY_CIRCLE_MOREREPLY_LIST);
        }
        if (entity == null) {
            return;
        }
        entity = (PostEntity) bundle.getSerializable(UFunKey.KEY_CIRCLE_MOREREPLY_LIST);
        //层主
        publisherLayer.setBackgroundColor(getResources().getColor(R.color.mine_top_bg));
        initAuthorData(entity);
        //层主回复
        content.setText(entity.contents);
    }

    public void initAuthorData(PostEntity authorEntity) {
        imageLoader.displayImage(authorEntity.avatar, userAvatar);
        userName.setText(authorEntity.author);
        publishTime.setText(authorEntity.postdate);
        if (authorEntity.rank_title == null) {
            rankTitle.setVisibility(View.INVISIBLE);
        } else {
            rankTitle.setText(authorEntity.rank_title);
        }
    }

    @Override
    public int getMainContentViewId() {
        return R.layout.ufun_activity_tip_morereply_header;
    }

}

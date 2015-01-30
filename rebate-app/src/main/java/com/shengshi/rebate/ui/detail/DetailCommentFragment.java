package com.shengshi.rebate.ui.detail;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.detail.DetailEntity;
import com.shengshi.rebate.bean.detail.DetailEntity.Comment;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.ui.RebateCommentListActivity;
import com.shengshi.rebate.ui.base.RebateBaseFragment;

/**
 * <p>Title:        评价Fragment
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-5
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class DetailCommentFragment extends RebateBaseFragment implements OnClickListener {

    RatingBar ratingBar;
    TextView commentCount;
    String itemId;
    View rebate_detail_comment_layout;

    @Override
    public void initComponents(View view) {
        ratingBar = (RatingBar) view.findViewById(R.id.rebate_comment_ratingBar);
        ratingBar.setNumStars(5);
        commentCount = (TextView) view.findViewById(R.id.rebate_comment_count);
        rebate_detail_comment_layout = view.findViewById(R.id.rebate_detail_comment_layout);
        rebate_detail_comment_layout.setOnClickListener(this);
    }

    public void fetchData(DetailEntity entity) {
        try {
            Comment commentInfo = entity.data.comment;
            ratingBar.setRating(commentInfo.starNum);
            String count = String.format(getString(R.string.rebate_comment_cout),
                    commentInfo.commentCount);
            commentCount.setText(count);
            itemId = entity.data.itemId;
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public int getMainContentViewId() {
        return R.layout.rebate_fragment_detail_comment_layout;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (R.id.rebate_detail_comment_layout == viewId) {
            Intent intent = new Intent(mActivity, RebateCommentListActivity.class);
            intent.putExtra(RebateKey.KEY_INTENT_REBATE_ITEM_ID, itemId);
            startActivity(intent);
        }
    }

}

package com.shengshi.rebate.ui;

import android.content.Context;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.DateUtil;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.rebate.R;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.bean.detail.CommentEntity;
import com.shengshi.rebate.bean.detail.CommentEntity.CommentInfo;
import com.shengshi.rebate.config.RebateConstants;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.ui.base.RebateBaseListActivity;

import java.util.List;

/**
 * <p>Title:      评价列表
 * <p>Description:
 * <p>@author:  huangxp
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-6
 * <p>@author:
 * <p>Update Time:   2015-1-5
 * <p>Updater:       liaodl
 * <p>Update Comments: 变更UI，填充数据
 */
public class RebateCommentListActivity extends RebateBaseListActivity {

    CommentAdapter mAdapter;
    String itemId;

    int curPage = 1;
    int totoalCount = 0;

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_comment;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
    }

    @Override
    protected void initData() {
        itemId = getIntent().getStringExtra(RebateKey.KEY_INTENT_REBATE_ITEM_ID);
        requestUrl(curPage);
    }

    private void requestUrl(int page) {
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.order.getCommentList");
        encryptInfo.resetParams();
        encryptInfo.putParam("item_id", itemId);
        encryptInfo.putParam("page", page);
        encryptInfo.putParam("page_size", RebateConstants.PAGE_SIZE_20);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<CommentEntity> jsonCallback = new JsonCallback<CommentEntity>() {
        @Override
        public void onSuccess(CommentEntity result) {
            refreshXListView();
            if (result == null || result.data == null || result.data.data == null) {
                showFailLayout();//showFailLayout("加载数据失败,点此重新加载");
                return;
            }
            hideLoadingBar();
            fetchData(result);
        }

        @Override
        public void onFailure(AppException exception) {
            hideLoadingBar();
            toast(exception.getMessage());
        }
    };

    private void fetchData(CommentEntity entity) {
        try {
            if (curPage == 1) {
                mAdapter = new CommentAdapter(mContext, entity.data.data);
                mListView.setAdapter(mAdapter);
                totoalCount = entity.data.count;
                if (totoalCount > mAdapter.getCount()) {
                    mListView.setPullLoadEnable(true);
                } else {
                    mListView.setPullLoadEnable(false);
                }
            } else {
                mAdapter.addAll(entity.data.data);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    protected void refreshXListView() {
        if (curPage == 1) {
            mListView.stopRefresh();
            mListView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
        } else {
            mListView.stopLoadMore();
        }
        if (mAdapter != null) {
            if (totoalCount <= mAdapter.getCount()) {
                mListView.stopLoadMore();
            } else {
                mListView.setPullLoadEnable(true);
            }
        }
    }

    @Override
    public void onRefresh() {
        curPage = 1;
        requestUrl(curPage);
    }

    @Override
    public void onLoadMore() {
        if (totoalCount <= mAdapter.getCount()) {
            toast(R.string.rebate_load_more_complete);
            mListView.stopLoadMore();
            mListView.setPullLoadEnable(false);
            return;
        }
        curPage++;
        requestUrl(curPage);
    }

    @Override
    public void requestAgain() {
        onRefresh();
    }

    @Override
    public String getTopTitle() {
        return "评价";
    }

    class CommentAdapter extends SimpleBaseAdapter<CommentInfo> {

        public CommentAdapter(Context context, List<CommentInfo> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.rebate_listview_item_comment_layout;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            CommentInfo comment = data.get(position);
            TextView consummer = holder.getView(R.id.rebate_comment_consummer);
            TextView dateText = holder.getView(R.id.rebate_comment_date);
            RatingBar ratingBar = holder.getView(R.id.rebate_comments_ratingBar);
            TextView content = holder.getView(R.id.rebate_comment_content);

            try {
                ratingBar.setNumStars(5);
                ratingBar.setRating(Float.parseFloat(comment.star));
                consummer.setText(comment.userName);
                content.setText(comment.content);
                dateText.setText(DateUtil.dateFormat(comment.date.toString(), "yyyy-MM-dd"));
            } catch (Exception e) {
                Log.e(e.getMessage(), e);
            }
            return convertView;
        }
    }

}

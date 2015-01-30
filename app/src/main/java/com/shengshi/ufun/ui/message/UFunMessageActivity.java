package com.shengshi.ufun.ui.message;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.UFunMessageEntity;
import com.shengshi.ufun.bean.UFunMessageEntity.Item;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;

import java.util.List;

/**
 * <p>Title:    消息列表
 * <p>Description:
 * <p>@author:  huangxp
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-29
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */

public class UFunMessageActivity extends LifeCircleBaseListActivity implements
        IXListViewListener {
    LifeCircleMessageAdapter mAdapter = null;

    @Override
    protected void initData() {
        int uId = getIntent().getIntExtra(UFunKey.KEY_USER_ID, 0);
        requestUrl(uId);
        //设置Item点击事件
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startChatting(--position);
            }
        });
    }

    public void requestUrl(int uId) {
        //temp
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("msg.get_pm_list");
        encryptInfo.resetParams();
        encryptInfo.putParam("page", 1);
        encryptInfo.putParam("page_size", 20);
        request.setCallback(jsonCallback);
        request.execute();
    }

    public void fetchData(UFunMessageEntity entity) {
        mAdapter = new LifeCircleMessageAdapter(mContext, entity.data.list);
        mListView.setAdapter(mAdapter);
        hideLoadingBar();
    }

    JsonCallback<UFunMessageEntity> jsonCallback = new JsonCallback<UFunMessageEntity>() {
        @Override
        public void onFailure(AppException exception) {
            toast("网络连接失败，请检查网络设置，或重试。");
        }

        @Override
        public void onSuccess(UFunMessageEntity entity) {
            fetchData(entity);
        }
    };

    class LifeCircleMessageAdapter extends SimpleBaseAdapter<Item> {
        public LifeCircleMessageAdapter(Context context, List<Item> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.listview_item_ufun_activity_message;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            //INI-COMPONENT
            Item m = (Item) data.get(position);
            CircleImageView avatarImg = (CircleImageView) convertView
                    .findViewById(R.id.lifecircle_message_icon_iv);
            TextView unread = holder.getView(R.id.lifecircle_message_tips_tv);
            TextView fromName = holder.getView(R.id.lifecircle_from_name);
            TextView simpleContent = holder.getView(R.id.lifecircle_simple_content);
            TextView time = holder.getView(R.id.lifecircle_chat_time);

            //SET-DATA
            imageLoader.displayImage(m.icon, avatarImg);
//			unread.setText(m.unread);
            fromName.setText(m.title);
            simpleContent.setText(m.lastmsg);
            time.setText(m.addtime);

            return convertView;
        }
    }

    public void startChatting(int position) {
        Item m = (Item) mAdapter.getItem(position);
        Intent intent = new Intent(mContext, UFunChatActivity.class);
        intent.putExtra(UFunKey.KEY_MSG_LID, Integer.parseInt(m.lid));
        intent.putExtra(UFunKey.KEY_MSG_FROM_AVATAR_URL, m.icon);
        intent.putExtra(UFunKey.KEY_MSG_FROM_NAME, m.title);
//		intent.putExtra(UFunKey.KEY_MSG_TOUID, Integer.parseInt(m.touid));
        startActivity(intent);
    }

    @Override
    public String getTopTitle() {
        return "消息";
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.ufun_activity_message;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}

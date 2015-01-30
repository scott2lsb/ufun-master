package com.shengshi.ufun.ui.message;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.UFunChatEntity;
import com.shengshi.ufun.bean.UFunChatEntity.Msg;
import com.shengshi.ufun.bean.UFunSendResponseEntity;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;
import com.shengshi.ufun.utils.AccountUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Title: 聊天界面
 * <p/>
 * Description:
 * <p/>
 *
 * @author: huangxp
 * <p/>
 * Copyright: Copyright (c) 2014
 * <p/>
 * Company: @小鱼网
 * <p/>
 * Create Time: 2014-12-31
 * <p/>
 * @author: <p>
 * Update Time:
 * <p/>
 * Updater:
 * <p/>
 * Update Comments:
 */

public class UFunChatActivity extends LifeCircleBaseListActivity {
    public final static int TYPE_FROM = 1;
    public final static int TYPE_TO = 2;
    private int userId;
    private int toUid;
    private int lid;
    private String fromName;
    private LifeCircleChatAdapter mAdapter;
    private int position;
    private int TOUID_NULL = 0x123;
    private InputMethodManager imm;
    private int page = 1;
    private int pageSize = 10;

    // 发送框
    private Button btnSend;
    private EditText contentEdit;

    @Override
    protected void initData() {
        Intent i = getIntent();
        userId = AccountUtil.getMineUserInfo(mContext).data.uid;
        fromName = i.getStringExtra(UFunKey.KEY_MSG_FROM_NAME);
        toUid = i.getIntExtra(UFunKey.KEY_MSG_TOUID, TOUID_NULL);
        lid = i.getIntExtra(UFunKey.KEY_MSG_LID, 0);

        getChatList(page, pageSize);

        setTopTitle();
        hideLoadingBar();
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        contentEdit = (EditText) findViewById(R.id.ed_input);
        btnSend = (Button) findViewById(R.id.ufun_tip_msg_send);

        contentEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocused) {
                if (hasFocused) {
                    btnSend.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.ufun_green));
                } else {
                    btnSend.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.ufun_grey));
                }
            }
        });

        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (contentEdit.getText().toString().trim().equals("")) {
                    toast("请输入发送内容");
                } else {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    String msgStr = contentEdit.getText().toString();
                    Msg msg = new Msg(
                            AccountUtil.getUserInfoEntity(mContext).data.uid,
                            msgStr);
                    msg.state = 1;
                    msg.icon = AccountUtil.getUserInfoEntity(mContext).data.icon;
                    List<Msg> temp = new ArrayList<UFunChatEntity.Msg>();
                    temp.add(msg);
                    temp.addAll(mAdapter.getList());
                    mAdapter.replaceAll(temp);
                    mListView.setSelection(mAdapter.getCount() - 1);
                    position = mAdapter.getCount() - 1;
                    sendMsg(toUid, msgStr);
                    contentEdit.setText("");
                }
            }
        });
    }

    private void getChatList(int page, int pageSize) {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("msg.get_session");
        encryptInfo.resetParams();
        if (toUid == TOUID_NULL && lid != 0) {
            encryptInfo.putParam("field", "lid");
            encryptInfo.putParam("value", lid);
        } else if (toUid != TOUID_NULL && lid == 0) {
            encryptInfo.putParam("field", "touid");
            encryptInfo.putParam("value", toUid);
        }
        encryptInfo.putParam("page", page);
        encryptInfo.putParam("page_size", pageSize);
        request.setCallback(getSessionCallback);
        request.execute();
    }

    private void sendMsg(int toUid, String content) {
        position = mAdapter.getCount() - 1;
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("msg.send_msg");
        encryptInfo.resetParams();
        encryptInfo.putParam("touid", toUid);
        encryptInfo.putParam("content", content);
        request.setCallback(sendMsgCallback);
        request.execute();
    }

    private void fetchData(UFunChatEntity entity) {
        if (entity.data.list.size() != 0) {
            if (page == 1) {
                mAdapter = new LifeCircleChatAdapter(mContext, entity.data.list);
                mListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mAdapter.getCount());
            } else {
                List<Msg> temp = new ArrayList<UFunChatEntity.Msg>();
                temp.addAll(mAdapter.getList());
                temp.addAll(entity.data.list);
                mAdapter.replaceAll(temp);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(10);
                mListView.stopRefresh();
            }
        } else {
            toast("已无更多");
            mListView.setSelection(1);
            mListView.stopRefresh();
        }
    }

    JsonCallback<UFunSendResponseEntity> sendMsgCallback = new JsonCallback<UFunSendResponseEntity>() {
        @Override
        public void onSuccess(UFunSendResponseEntity entity) {
            if (entity.errCode == 0) {
                List<Msg> temp = new ArrayList<UFunChatEntity.Msg>();
                temp.addAll(mAdapter.getList());
                temp.get(mAdapter.getCount() - position - 1).state = 0;
                mAdapter.replaceAll(temp);
                mAdapter.notifyDataSetChanged();
            } else {
                // TEMP
                toast("消息发送失败！！！");
            }
        }

        @Override
        public void onFailure(AppException exception) {
            toast("sendMsgCallback-网络连接失败，请检查网络设置，或重试。");
        }
    };

    JsonCallback<UFunChatEntity> getSessionCallback = new JsonCallback<UFunChatEntity>() {
        @Override
        public void onSuccess(UFunChatEntity entity) {
//			if(entity.data.list.size() != 0){
//				toUid = Integer.parseInt(entity.data.list.get(0).to_uid);
//			}
            fetchData(entity);
        }

        @Override
        public void onFailure(AppException exception) {
            toast("getSessionCallback-网络连接失败，请检查网络设置，或重试。");
        }
    };

    @Override
    public String getTopTitle() {
        return fromName;
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.ufun_activity_message_chat;
    }

    @Override
    public void onRefresh() {
        getChatList(++page, 10);
    }

    @Override
    public void onLoadMore() {

    }

    class LifeCircleChatAdapter extends SimpleBaseAdapter<Msg> {

        public LifeCircleChatAdapter(Context context, List<Msg> data) {
            super(context, data);
        }

        @Override
        public int getItemViewType(int position) {
            if (Integer.parseInt(data.get(position).authorid) == userId) {
                return TYPE_TO;
            } else {
                return TYPE_FROM;
            }
        }

        @Override
        public int getItemResource(int position) {
            if (getItemViewType(position) == TYPE_FROM) {
                return R.layout.ufun_chat_list_item_from;
            } else if (getItemViewType(position) == TYPE_TO) {
                return R.layout.ufun_chat_list_item_to;
            }
            return 0;
        }

        public List<Msg> getList() {
            return data;
        }

        @Override
        public View getItemView(int position, View convertView,
                                ViewHolder holder) {
            CircleImageView avatar;
            TextView content;
            ProgressBar sendState;
            Msg line = data.get(getCount() - position - 1);

            switch (getItemViewType(position)) {
                case TYPE_FROM:
                    // INI-COMPONENT
                    avatar = (CircleImageView) holder.getView(R.id.iv_chat_list_item_from_usericon);
                    content = holder.getView(R.id.tv_chat_list_item_from_content);
                    content.setVisibility(View.VISIBLE);
                    // SET-DATA
                    imageLoader.displayImage(line.icon, avatar);
                    content.setText(line.message);
                    break;

                case TYPE_TO:
                    // INI-COMPONENT
                    avatar = (CircleImageView) holder
                            .getView(R.id.iv_chat_list_item_to_usericon);
                    sendState = (ProgressBar) holder
                            .getView(R.id.ufun_chat_list_item_to_sending);
                    content = holder.getView(R.id.tv_chat_list_item_to_content);
                    content.setVisibility(View.VISIBLE);
                    // SET-DATA
                    imageLoader.displayImage(line.icon, avatar);
                    content.setText(line.message);
                    if (line.state == 1) {
                        sendState.setVisibility(View.VISIBLE);
                    } else {
                        sendState.setVisibility(View.GONE);
                    }
                    break;

                default:
                    break;
            }

            return convertView;
        }
    }
}

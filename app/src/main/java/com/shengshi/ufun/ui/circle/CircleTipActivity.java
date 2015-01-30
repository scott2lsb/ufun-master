package com.shengshi.ufun.ui.circle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.base.widget.xlistview.XListView.OnListViewSizeChangedListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.BaseEntity;
import com.shengshi.ufun.bean.circle.CircleThreadEntity;
import com.shengshi.ufun.bean.circle.CircleThreadEntity.PostEntity;
import com.shengshi.ufun.bean.circle.ZanEntity;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;
import com.shengshi.ufun.ui.circle.CircleTipHeaderFragment.WebviewOnLoadFinishedListener;
import com.shengshi.ufun.utils.SpinnerUtil;
import com.shengshi.ufun.utils.SpinnerUtil.OnSpinnerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class CircleTipActivity extends LifeCircleBaseListActivity {
    private ImageView img;
    private View tipHeader;
    private int tid;
    private int pid;
    private ReplyAdapter mAdapter;
    private Handler handler;
    private int page = 1;
    private int zanState;
    private int favorState;

    // 底部控件
    private View tipFooter;
    private LinearLayout footerZanLayer;
    private LinearLayout footerReplyLayer;
    private TextView replyCountTxt;
    private TextView zanCountTxt;

    // 回复操作
    private boolean SHOW_SOFTINPUT = true;
    private boolean APP_READY = false;
    private boolean WEB_LOAD_FINISHED = false;
    private boolean TO_AUTHOR = true;
    private View replyLayer;
    private EditText replyEdit;
    private Button btnSend;
    private InputMethodManager imm;
    CircleTipHeaderFragment circleTipHeaderFragment;

    @Override
    protected void initData() {
        showLoadingBar();
        tid = getIntent().getIntExtra("tid", 0);
        initTopBar();
        getThreadContent(tid, 1);
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        handler = new Handler();
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setOnListViewSizeChangedListener(new OnListViewSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                // if (TOGGLE_STATE && APP_READY && WEB_LOAD_FINISHED) {
                // replyLayer.setVisibility(View.VISIBLE);
                // replyEdit.requestFocus();
//				 btnSend.setBackgroundDrawable(getResources().getDrawable(
//				 R.drawable.ufun_green));
                // TOGGLE_STATE = false;
                // } else
                if (!SHOW_SOFTINPUT && APP_READY && WEB_LOAD_FINISHED) {
                    replyLayer.setVisibility(View.INVISIBLE);
                    replyEdit.setText("");
                } else {
                    SHOW_SOFTINPUT = false;
                }
            }
        });

        // 软键盘
        imm = (InputMethodManager) mActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // 底部操作
        tipFooter = findViewById(R.id.ufun_tip_footer);
        replyCountTxt = (TextView) tipFooter
                .findViewById(R.id.lifecircle_tip_content_reply);
        footerZanLayer = (LinearLayout) tipFooter
                .findViewById(R.id.lifecircle_tip_zan_layer);
        zanCountTxt = (TextView) tipFooter
                .findViewById(R.id.lifecircle_tip_content_zan);
        footerReplyLayer = (LinearLayout) tipFooter
                .findViewById(R.id.lifecircle_tip_reply_layer);
        footerReplyLayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                TO_AUTHOR = true;
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                replyLayer.setVisibility(View.VISIBLE);
                replyEdit.requestFocus();
                btnSend.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.ufun_green));
                SHOW_SOFTINPUT = true;
            }
        });

        // popupwindow回复操作
        replyLayer = findViewById(R.id.ufun_tip_reply_layer);
        replyEdit = (EditText) replyLayer.findViewById(R.id.ed_input);
        btnSend = (Button) replyLayer.findViewById(R.id.ufun_tip_msg_send);
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (replyEdit.getText().toString().equals("")) {
                    toast("请输入回复内容");
                } else {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    if (TO_AUTHOR) {
                        replyPublish(tid, 0, replyEdit.getText().toString());
                    } else {
                        replyPublish(tid, pid, replyEdit.getText().toString());
                    }
                }
            }
        });

    }

    private void getThreadContent(int tid, int page) {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("thread.threadinfo");
        encryptInfo.resetParams();
        encryptInfo.putParam("tid", tid);
        encryptInfo.putParam("page", page);
        request.setCallback(getThreadContentCallBack);
        request.execute();
    }

    private void replyPublish(int tid, int pid, String msg) {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("thread.reply");
        encryptInfo.resetParams();
        encryptInfo.putParam("tid", tid);
        encryptInfo.putParam("message", msg);
        pid = pid == 0 ? 0 : pid;
        encryptInfo.putParam("pid", pid);
        request.setCallback(replyCallBack);
        request.execute();
    }

    private void favorOrUnfavor(int tid) {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        if (favorState == 1) {
            encryptInfo.setCallback("thread.unfavor");
        } else {
            encryptInfo.setCallback("thread.favor");
        }
        encryptInfo.resetParams();
        encryptInfo.putParam("tid", tid);
        request.setCallback(markCallback);
        request.execute();
    }

    private void zanTip(int tid) {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("thread.zan");
        encryptInfo.resetParams();
        encryptInfo.putParam("tid", tid);
        if (zanState == 0) {
            encryptInfo.putParam("action", 1);
        } else if (zanState == 1) {
            encryptInfo.putParam("action", 0);
        }
        request.setCallback(zanCallBack);
        request.execute();
    }

    private void fetchData(CircleThreadEntity entity) {
        if (page == 1) {
            replyCountTxt.setText(entity.data.thread.postcount);
            zanCountTxt.setText(entity.data.thread.zan_count);
            initContentHeader(entity);
            mAdapter = new ReplyAdapter(mContext, entity.data.post);
            mListView.setAdapter(mAdapter);
            zanState = entity.data.thread.ifzan;
            favorState = entity.data.thread.isfavor;
            footerZanLayer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    zanTip(tid);
                }
            });
            refreshXListView();
        } else {
            List<PostEntity> tempList = new ArrayList<CircleThreadEntity.PostEntity>();
            tempList.addAll(entity.data.post);
            tempList.addAll(mAdapter.getlist());
            mAdapter.replaceAll(tempList);
            mAdapter.notifyDataSetChanged();
        }
        APP_READY = true;
    }

    class ReplyAdapter extends SimpleBaseAdapter<PostEntity> {

        public List<PostEntity> getlist() {
            return data;
        }

        public ReplyAdapter(Context context, List<PostEntity> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.ufun_tipactivity_listview_item;
        }

        @Override
        public View getItemView(final int position, View convertView,
                                ViewHolder holder) {

            String floorStr = "";
            if (position == 0) {
                floorStr = "沙发  ";
            } else if (position == 1) {
                floorStr = "板凳  ";
            } else {
                floorStr = (position + 1) + "楼  ";
            }

            final PostEntity post = data.get(position);
            // INI-COMPONENT
            CircleImageView userAvatar = holder
                    .getView(R.id.lifecircle_tips_reply_publisher_avatar);
            TextView operationIcon = holder
                    .getView(R.id.lifecircle_reply_operation);
            TextView author = holder
                    .getView(R.id.lifecircle_tips_reply_publisher_name);
            final TextView postDate = holder
                    .getView(R.id.lifecircle_tips_reply_publisher_time);
            TextView rankTitle = holder
                    .getView(R.id.lifecircle_tips_reply_rank_title);
            TextView content = holder
                    .getView(R.id.lifecircle_tipactivity_replay_content);
            int subCount = Integer.parseInt(post.subcount);

            // subReply1-2
            TextView firstSubReply;
            TextView secondSubReply;

            // 更多回复
            TextView moreReply = holder.getView(R.id.lifecircle_more_reply);

            if (subCount > 1 && subCount < 2) {
                firstSubReply = holder.getView(R.id.lifecircle_tip_replay_1st);
                firstSubReply.setVisibility(View.VISIBLE);
                firstSubReply.setText(updateTextColor(
                        post.sublist.get(0).author,
                        post.sublist.get(0).contents));

            } else if (subCount >= 2) {
                firstSubReply = holder.getView(R.id.lifecircle_tip_replay_1st);
                firstSubReply.setVisibility(View.VISIBLE);
                firstSubReply.setText(updateTextColor(
                        post.sublist.get(0).author,
                        post.sublist.get(0).contents));

                secondSubReply = holder.getView(R.id.lifecircle_tip_replay_2nd);
                secondSubReply.setText(updateTextColor(
                        post.sublist.get(1).author,
                        post.sublist.get(1).contents));
                secondSubReply.setVisibility(View.VISIBLE);

                if (subCount > 2) {
                    moreReply = holder.getView(R.id.lifecircle_more_reply);
                    moreReply.setVisibility(View.VISIBLE);
                    String moreStr = String.format(mContext.getResources()
                                    .getString(R.string.lifecircle_tip_load_more),
                            Integer.parseInt(post.subcount));
                    moreReply.setText(moreStr);

                    // 更多回复
                    moreReply.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            String floor = "";
                            String tempStr = "";
                            // if (position == 0) {
                            // floor = "沙发  ";
                            // } else if (position == 1) {
                            // floor = "板凳  ";
                            // } else {
                            // floor = (position + 1) + "楼  ";
                            // }

                            final Intent intent = new Intent(mContext,
                                    CircleMoreReplyActivity.class);
                            tempStr = postDate.getText().toString();
                            floor = tempStr
                                    .subSequence(0, tempStr.indexOf(" "))
                                    .toString();
                            intent.putExtra(UFunKey.KEY_CIRCLE_MOREREPLY_TITLE,
                                    floor);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(
                                    UFunKey.KEY_CIRCLE_MOREREPLY_LIST, post);
                            intent.putExtra(
                                    UFunKey.KEY_CIRCLE_MOREREPLY_LIST_BUNDLE,
                                    bundle);
                            startActivity(intent);
                        }
                    });
                }
            }

            // SET-DATA
            imageLoader.displayImage(post.avatar, userAvatar);
            author.setText(post.author);
            postDate.setText(floorStr + post.postdate);

            if (post.rank_title == null) {
                rankTitle.setVisibility(View.INVISIBLE);
            } else {
                rankTitle.setText(post.rank_title);
            }

            content.setText(post.contents);

            operationIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    pid = Integer.parseInt(post.pid);
                    TO_AUTHOR = false;
                    SpinnerUtil.createOperationListSpinner(mActivity,
                            findViewById(R.id.mGeneralListView),
                            R.array.lifecircle_reply_operation,
                            new OnSpinnerItemClickListener() {
                                @Override
                                public void onSpinnerItemClick(
                                        int operationPosition) {
                                    if (operationPosition == 0) {
                                        // 回复
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                imm.toggleSoftInput(
                                                        0,
                                                        InputMethodManager.HIDE_NOT_ALWAYS);
                                            }
                                        }, 1300);
                                    } else if (operationPosition == 1) {
                                        // 私信

                                    }
                                }
                            });
                }
            });

            return convertView;
        }

        private SpannableStringBuilder updateTextColor(String author,
                                                       String content) {
            SpannableStringBuilder ssBuilder = new SpannableStringBuilder(
                    author + " : " + content);
            ssBuilder.setSpan(new ForegroundColorSpan(mContext.getResources()
                            .getColor(R.color.reply_author_name)), 0, author.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return ssBuilder;
        }
    }

    private void initTopBar() {
        TopUtil.updateRight(this, R.id.lifecircle_top_right, R.drawable.share);
        img = (ImageView) findViewById(R.id.lifecircle_top_right_another);
        img.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.lifecircle_more));
        img.setVisibility(View.VISIBLE);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SpinnerUtil
                        .createOperationListSpinner(
                                mActivity,
                                findViewById(R.id.mGeneralListView),
                                favorState == 1 ? R.array.lifecircle_tip_operatioin_not_favor
                                        : R.array.lifecircle_tip_operatioin_favor,
                                new OnSpinnerItemClickListener() {
                                    @Override
                                    public void onSpinnerItemClick(int position) {
                                        if (position == 0) {
                                            favorOrUnfavor(tid);
                                        }
                                    }
                                });
            }
        });
    }

    JsonCallback<CircleThreadEntity> getThreadContentCallBack = new JsonCallback<CircleThreadEntity>() {
        @Override
        public void onFailure(AppException exception) {
            toast("ON FAILURE");
        }

        @Override
        public void onSuccess(CircleThreadEntity entity) {
            if (page != 1 && entity.data.post.size() == 0) {
                toast("已无更多");
                mListView.stopLoadMore();
            } else {
                fetchData(entity);
            }
        }
    };

    JsonCallback<BaseEntity> replyCallBack = new JsonCallback<BaseEntity>() {
        @Override
        public void onFailure(AppException exception) {
            toast("ON FAILURE");
        }

        @Override
        public void onSuccess(BaseEntity entity) {
            if (entity.errCode == 0) {
                toast("回复成功");
            } else {
                toast(entity.errMessage);
            }
        }
    };

    JsonCallback<BaseEntity> markCallback = new JsonCallback<BaseEntity>() {
        @Override
        public void onFailure(AppException exception) {
            toast("ON FAILURE");
        }

        @Override
        public void onSuccess(BaseEntity entity) {
            if (entity.errCode == 0) {
                if (entity.errMessage.equals("取消收藏成功")) {
                    favorState = 0;
                } else if (entity.errMessage.equals("收藏成功")) {
                    favorState = 1;
                }
                toast(entity.errMessage);
            } else {
                toast(entity.errMessage);
            }
        }
    };

    JsonCallback<ZanEntity> zanCallBack = new JsonCallback<ZanEntity>() {
        @Override
        public void onSuccess(ZanEntity entity) {
            if (entity.data.ifzan == 1) {
                zanState = 1;
                toast("已赞");
                int zanCount = Integer.parseInt(zanCountTxt.getText()
                        .toString());
                zanCountTxt.setText(StringUtils.toString(++zanCount));
            } else if (entity.data.ifzan == 0) {
                zanState = 0;
                toast("取消赞");
                int zanCount = Integer.parseInt(zanCountTxt.getText()
                        .toString());
                zanCountTxt.setText(StringUtils.toString(--zanCount));
            }
        }

        @Override
        public void onFailure(AppException exception) {
            toast("ONFAILURE");
        }

    };

    private void initContentHeader(CircleThreadEntity entity) {
        if (tipHeader == null) {
            tipHeader = LayoutInflater.from(mContext).inflate(
                    R.layout.ufun_activity_tip_header_container, null);
        } else {
            mListView.removeHeaderView(tipHeader);
        }
        mListView.addHeaderView(tipHeader);

        circleTipHeaderFragment = (CircleTipHeaderFragment) mFragmentManager
                .findFragmentById(R.id.lifecircle_tip_header_container);
        if (circleTipHeaderFragment != null) {
            circleTipHeaderFragment.refreshData(entity.data.thread);
            circleTipHeaderFragment
                    .setWebviewOnLoadFinishedListener(new WebviewOnLoadFinishedListener() {
                        @Override
                        public void onLoadFinished() {
                            WEB_LOAD_FINISHED = true;
                        }
                    });
        } else {
            Bundle args = new Bundle();
            args.putSerializable(UFunKey.KEY_INTENT_TIP_HEADER, entity);
            turnToFragment(CircleTipHeaderFragment.class,
                    UFunKey.TAG_TIP_HEADER_FRAGMENT, args,
                    R.id.lifecircle_tip_header_container);
            circleTipHeaderFragment = (CircleTipHeaderFragment) mFragmentManager
                    .findFragmentByTag(UFunKey.TAG_TIP_HEADER_FRAGMENT);
            if (circleTipHeaderFragment != null) {
                circleTipHeaderFragment
                        .setWebviewOnLoadFinishedListener(new WebviewOnLoadFinishedListener() {
                            @Override
                            public void onLoadFinished() {
                                WEB_LOAD_FINISHED = true;
                                hideLoadingBar();
                            }
                        });
            }
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        getThreadContent(tid, page);
    }

    @Override
    public void onLoadMore() {
        getThreadContent(tid, ++page);
    }

    @Override
    public String getTopTitle() {
        return mContext.getString(R.string.lifecircle_tip_content);
    }

    @Override
    public int getTopTitleResourceId() {
        return R.string.lifecircle_tip_content;
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.ufun_activity_tip_content;
    }
}

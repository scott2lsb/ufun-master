package com.shengshi.ufun.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.circle.PuretextCircleAdapter;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.BaseEntity;
import com.shengshi.ufun.bean.circle.PicCircleTuiListEntity;
import com.shengshi.ufun.bean.mine.FansEntity;
import com.shengshi.ufun.bean.mine.FansEntity.Fans;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseListActivity;
import com.shengshi.ufun.ui.circle.CircleTipActivity;
import com.shengshi.ufun.ui.mine.TaPeopleActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends LifeCircleBaseListActivity implements
        OnItemClickListener, OnClickListener, IXListViewListener {
    private EditText editText;
    String keyword;
    XListView searchresult_topic_list;
    XListView searchresult_user_list;
    TextView searchresult_toptab_topic_tv;
    TextView searchresult_toptab_user_tv;
    FrameLayout searchresult_toptab_topic_fv;
    FrameLayout searchresult_toptab_user_fv;
    PicCircleTuiListEntity mData;
    PuretextCircleAdapter mAdapter;
    List<Fans> muserDatas = new ArrayList<Fans>();
    MineAttentionAdapter muserAdapter;

    int qid;
    int[] curPage;
    int[] totoalCount;
    int type;
    int attentioned = 1;
    TextView mcancelTv;
    int mposition = 0;
    int touid = 1;

    @Override
    protected void initComponents() {
        super.initComponents();
        // 隐藏Activity刚进来焦点在EditText时的键盘显示
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		TopUtil.updateLeft(mActivity, R.id.lifecircle_top_btn_return,
//				R.drawable.close);
        editText = findEditTextById(R.id.lifecircle_search);
        TopUtil.showView(mActivity, R.id.lifecircle_search);
        searchresult_topic_list = findXListViewById(R.id.searchresult_topic_list);
        searchresult_user_list = findXListViewById(R.id.searchresult_user_list);
        searchresult_topic_list.setPullLoadEnable(false);
        searchresult_topic_list.setPullRefreshEnable(true);
        searchresult_topic_list.setXListViewListener(this);
        searchresult_topic_list.setOnItemClickListener(this);
        searchresult_topic_list.setOnScrollListener(new PauseOnScrollListener(
                imageLoader.getImageLoader(), true, false));
        searchresult_user_list.setPullLoadEnable(false);
        searchresult_user_list.setPullRefreshEnable(true);
        searchresult_user_list.setXListViewListener(this);
        searchresult_user_list.setOnItemClickListener(this);
        searchresult_user_list.setOnScrollListener(new PauseOnScrollListener(
                imageLoader.getImageLoader(), true, false));

        searchresult_toptab_topic_tv = findTextViewById(R.id.searchresult_toptab_topic_tv);
        searchresult_toptab_user_tv = findTextViewById(R.id.searchresult_toptab_user_tv);
        searchresult_toptab_topic_fv = findFrameLayoutById(R.id.searchresult_toptab_topic_fv);
        searchresult_toptab_user_fv = findFrameLayoutById(R.id.searchresult_toptab_user_fv);

        searchresult_toptab_topic_tv.setOnClickListener(this);
        searchresult_toptab_user_tv.setOnClickListener(this);
        searchresult_toptab_topic_fv.setOnClickListener(this);
        searchresult_toptab_user_fv.setOnClickListener(this);
        curPage = new int[2];
        curPage[0] = 1;
        curPage[1] = 1;
        totoalCount = new int[2];
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    keyword = editText.getText().toString();
                    requestwhichType(type);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_search_result;
    }

    @Override
    protected void initData() {
        setReturnBtnEnable(true);
        keyword = getIntent().getStringExtra("keyword");
        editText.setText(keyword);
        requestwhichType(0);
    }

    private void requestwhichType(int type) {
        if (type == 0) {
            requestUrl();
        } else if (type == 1) {
            requesusertUrl();
        }
    }

    private void requestUrl() {
        showLoadingBar();
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("thread.search");
        encryptInfo.resetParams();
        encryptInfo.putParam("qid", qid);
        encryptInfo.putParam("keyword", keyword);
        encryptInfo.putParam("page", curPage[0]);
        encryptInfo.putParam("action", 2);
        request.setCallback(jsonCallback);
        request.execute();

    }

    JsonCallback<PicCircleTuiListEntity> jsonCallback = new JsonCallback<PicCircleTuiListEntity>() {

        @Override
        public void onFailure(AppException arg0) {
            hideLoadingBar();
        }

        @Override
        public void onSuccess(PicCircleTuiListEntity result) {
            hideLoadingBar();
            if (result == null || result.data == null) {
                loadingBar.showFailLayout();
                return;
            }

            fetchData(result);
            refreshListView(0, searchresult_topic_list);
        }

    };

    private void refreshListView(int type, XListView listView) {
        if (curPage[0] == 1) {
            listView.stopRefresh();
            listView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
        } else {
            listView.stopLoadMore();
        }
        if (type == 1) {
            if (mAdapter != null) {
                if (totoalCount[0] <= mAdapter.getItemCount()) {
                    listView.stopLoadMore();
                    listView.setPullLoadEnable(false);
                } else {
                    listView.setPullLoadEnable(true);
                }
            }
        } else if (type == 2) {
            if (muserAdapter != null) {
                if (totoalCount[1] <= muserAdapter.getItemCount()) {
                    listView.stopLoadMore();
                    listView.setPullLoadEnable(false);
                } else {
                    listView.setPullLoadEnable(true);
                }
            }
        }

    }

    protected void fetchData(PicCircleTuiListEntity result) {

        try {

            if (curPage[0] == 1) {

                mData = result;
                totoalCount[0] = mData.data.count;
                mAdapter = new PuretextCircleAdapter(mContext, mData);

                searchresult_topic_list.setAdapter(mAdapter);
            } else {
                mAdapter.addData(result);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    public void setAttention(TextView cancelTv, int position) {
        mcancelTv = cancelTv;
        mposition = position;
        requestAttentionUrl();
    }


    /**
     * 获取用户的关注列表
     */
    private void requesusertUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.search");
        encryptInfo.resetParams();
        encryptInfo.putParam("keyword", keyword);
        encryptInfo.putParam("page", curPage[1]);
        encryptInfo.putParam("page_size", UFunConstants.PAGE_SIZE);
        request.setCallback(jsonuserCallback);
        request.execute();
    }

    JsonCallback<FansEntity> jsonuserCallback = new JsonCallback<FansEntity>() {

        @Override
        public void onSuccess(FansEntity result) {
            if (result == null || result.data == null) {
                loadingBar.showFailLayout();
                return;
            }
            hideLoadingBar();
            fetchData(result);
            refreshListView(1, searchresult_user_list);
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
            hideLoadingBar();
            // count = mAdapter != null ? mAdapter.getCount() : 0;
            // refreshXListView(curPage[1], count);
        }
    };

    class MineAttentionAdapter extends SimpleBaseAdapter<Fans> {

        public MineAttentionAdapter(Context context, List<Fans> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.addcircle_second_listitem;
        }

        public int getItemCount() {
            int joincircleSize = 0;
            if (data != null) {
                joincircleSize = data.size();
            }
            return joincircleSize;
        }

        @Override
        public View getItemView(final int position, View convertView,
                                ViewHolder holder) {
            final Fans mFans = data.get(position);
            TextView nameTv = holder.getView(R.id.secondlistitem_name_tv);
            TextView subtitleTv = holder
                    .getView(R.id.secondlistitem_subtitle_tv);
            ImageView iv = holder.getView(R.id.secondlistitem_iv);
//			final ImageView joinIv = holder
//					.getView(R.id.secondlistitem_join_iv);
            final TextView cancelTv = holder
                    .getView(R.id.secondlistitem_cancel_tv);
            imageLoader.displayImage(mFans.icon, iv);
            nameTv.setText(mFans.username);
            subtitleTv.setText(mFans.signature);
            if (muserDatas.get(position).is_attention == 1) {
                cancelTv.setVisibility(View.VISIBLE);
                cancelTv.setBackgroundResource(R.drawable.btn_gray);
                cancelTv.setTextColor(getResources().getColor(R.color.gray_text_selector));
                cancelTv.setText("取消关注");
            } else if (muserDatas.get(position).is_attention == 0) {
                cancelTv.setBackgroundResource(R.drawable.btn_blue);
                cancelTv.setVisibility(View.VISIBLE);
                cancelTv.setTextColor(getResources().getColor(R.color.white));
                cancelTv.setText("关注");
            } else {
                cancelTv.setVisibility(View.GONE);
            }
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touid = mFans.uid;
                    attentioned = muserDatas.get(position).is_attention;
                    setAttention(cancelTv, position);
                }
            });
            return convertView;
        }


    }

    protected void fetchData(FansEntity result) {
        try {
            if (curPage[1] == 1) {
                totoalCount[0] = result.data.count;
                muserDatas.clear();
                muserDatas.addAll(result.data.list);
                muserAdapter = new MineAttentionAdapter(mContext,
                        result.data.list);
                searchresult_user_list.setAdapter(muserAdapter);
            } else {
                muserDatas.addAll(result.data.list);
                muserAdapter.addAll(result.data.list);
                muserAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.searchresult_toptab_topic_tv
                || id == R.id.searchresult_toptab_topic_fv) {
            type = 0;

            searchresult_toptab_topic_tv.setTextColor(getResources().getColor(
                    R.color.main_tab_tv_active));
            searchresult_toptab_user_tv.setTextColor(getResources().getColor(
                    R.color.sub_heading_text));
            TopUtil.updateTopTextViewIconLeft(mActivity,
                    searchresult_toptab_topic_tv, R.drawable.icon_topic_active);
            TopUtil.updateTopTextViewIconLeft(mActivity,
                    searchresult_toptab_user_tv, R.drawable.icon_user);
            searchresult_topic_list.setVisibility(View.VISIBLE);
            searchresult_user_list.setVisibility(View.GONE);
            if (curPage[type] == 1) {
                requestUrl();
            }
        } else if (id == R.id.searchresult_toptab_user_tv
                || id == R.id.searchresult_toptab_user_fv) {
            type = 1;
            searchresult_toptab_topic_tv.setTextColor(getResources().getColor(
                    R.color.sub_heading_text));
            searchresult_toptab_user_tv.setTextColor(getResources().getColor(
                    R.color.main_tab_tv_active));
            TopUtil.updateTopTextViewIconLeft(mActivity,
                    searchresult_toptab_topic_tv, R.drawable.icon_topic);
            TopUtil.updateTopTextViewIconLeft(mActivity,
                    searchresult_toptab_user_tv, R.drawable.icon_user_active);
            searchresult_topic_list.setVisibility(View.GONE);
            searchresult_user_list.setVisibility(View.VISIBLE);
            if (curPage[type] == 1) {
                requesusertUrl();
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {
        position = position - 1;

        switch (adapterView.getId()) {
            case R.id.searchresult_topic_list:
                Intent intent = new Intent(this, CircleTipActivity.class);
                if (mData.data.list != null && mData.data.list.size() > position) {
                    intent.putExtra("tid", mData.data.list.get(position).tid);
                    startActivity(intent);
                }
                break;
            case R.id.searchresult_user_list:
                if (muserDatas != null && muserDatas.size() > position) {
                    Intent userIntent = new Intent(this, TaPeopleActivity.class);
                    userIntent.putExtra("uid", muserDatas.get(position).uid);
                    userIntent.putExtra("username", muserDatas.get(position).username);
                    startActivity(userIntent);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onRefresh() {
        curPage[type] = 1;
        requestwhichType(type);
    }

    @Override
    public void onLoadMore() {
        curPage[type]++;
        requestwhichType(type);

    }

    @Override
    public String getTopTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 关注/取消用户
     */
    private void requestAttentionUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        if (attentioned == 1) {
            encryptInfo.setCallback("user.cancel_attention");
        } else if (attentioned == 0) {
            encryptInfo.setCallback("user.pay_attention");
        }
        encryptInfo.resetParams();
        encryptInfo.putParam("touid", touid);
        request.setCallback(jsonAttentionCallback);
        request.execute();
    }

    JsonCallback<BaseEntity> jsonAttentionCallback = new JsonCallback<BaseEntity>() {

        @Override
        public void onSuccess(BaseEntity result) {
            if (result != null) {
                toast(result.errMessage);
                if (result.errCode == 0) {
                    if (attentioned == 0) {
                        muserDatas.get(mposition).is_attention = 1;
                    } else if (attentioned == 1) {
                        muserDatas.get(mposition).is_attention = 0;
                    }
                    muserAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

}

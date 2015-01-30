package com.shengshi.ufun.ui.circle;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.GridNoScrollView;
import com.shengshi.base.widget.LoadingBar;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.circle.PuretextCircleAdapter;
import com.shengshi.ufun.adapter.circle.TagAdapter;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.StatusEntity;
import com.shengshi.ufun.bean.circle.CircleTagEntity;
import com.shengshi.ufun.bean.circle.CircleTagEntity.SecondTag;
import com.shengshi.ufun.bean.circle.PicCircleTuiListEntity;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.ui.home.HomeSearchActivity;
import com.shengshi.ufun.utils.ImageLoader;
import com.shengshi.ufun.utils.SpinnerUtil;
import com.shengshi.ufun.utils.SpinnerUtil.OnSpinnerItemClickListener;

import java.util.ArrayList;

public class PureTextCircleActivity extends LifeCircleBaseActivity implements
        OnClickListener, IXListViewListener, OnItemClickListener {
    XListView mListView;
    TextView mTopTitle;
    PicCircleTuiListEntity mData;
    PuretextCircleAdapter mAdapter;
    int curPage = 1;
    int totoalCount = 0;

    CircleImageView pureheader_iv;
    TextView pureheader_nametv;
    TextView pureheader_titletv;
    TextView pureheader_topfirsttv;
    TextView pureheader_topsecndtv;
    ImageLoader loader;
    View headerView;
    LinearLayout pureheader_activitylv;
    LinearLayout pureheader_sginlv;
    LinearLayout pureheader_searchv;
    LinearLayout pureheader_taglv;
    LinearLayout pureheader_lin_quan;
    int qid = 0;

    int titleClickindex = 0;
    public static CircleTagEntity circleTagEntity;
    GridNoScrollView tag_gridview;
    TagAdapter tag;
    LoadingBar lb;
    PopupWindow mPopupWindow;
    LinearLayout parenttagLayout;
    public static int[] selectp;
    public static final int SELECTTAGS = 1; // 选择标签
    JsonObject hashtag;// 用来保存选择的标签
    private int tagPosition = -1;
    private Boolean iffirst = true; //是否是第一次点击标签。用于二维的标签第二次进入传数据

    @Override
    protected void initComponents() {
        super.initComponents();
        // 隐藏Activity刚进来焦点在EditText时的键盘显示
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mListView = findXListView();

        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);

        mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader
                .getImageLoader(), true, false));
        TopUtil.updateRight(mActivity, R.id.lifecircle_top_right,
                R.drawable.write);
        findImageViewById(R.id.lifecircle_top_right).setVisibility(View.GONE);

        loader = ImageLoader.getInstance(mActivity);

    }

    @Override
    public String getTopTitle() {
        String[] titleArray = getResources().getStringArray(
                R.array.circle_topbar_titleList);
        return titleArray[0];

    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.activity_pure_text;
    }

    @Override
    protected void initData() {
        setReturnBtnEnable(true);
        qid = getIntent().getIntExtra("qid", 0);
        mTopTitle = findTextViewById(R.id.lifecircle_top_title);
        mTopTitle.setVisibility(View.VISIBLE);
        mTopTitle.setOnClickListener(this);
        TopUtil.updateTextViewIconRight(mActivity, mTopTitle,
                R.drawable.topbar_drop);
        requestUrl(curPage);

    }

    private void requestUrl(int curPage) {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("thread.index");
        encryptInfo.resetParams();
        encryptInfo.putParam("qid", qid);
        encryptInfo.putParam("order", titleClickindex);
        encryptInfo.putParam("tag", hashtag);
        encryptInfo.putParam("page", curPage);
        encryptInfo.putParam("page_size", "25");
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
            refreshListView();
        }

    };

    private void refreshListView() {
        totoalCount = mData.data.count;
        if (curPage == 1) {
            mListView.stopRefresh();
            mListView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
        } else {
            mListView.stopLoadMore();
        }
        if (mAdapter != null) {
            if (totoalCount <= mAdapter.getItemCount()) {
                mListView.stopLoadMore();
                mListView.setPullLoadEnable(false);
            } else {
                mListView.setPullLoadEnable(true);
            }
        }
    }

    protected void fetchData(PicCircleTuiListEntity result) {

        try {

            if (curPage == 1) {
                mData = result;
                mAdapter = new PuretextCircleAdapter(mContext, mData);
                if (mData.data.quan != null) {
                    initHeaderView(mData);
                }
                mListView.setAdapter(mAdapter);
            } else {
                mAdapter.addData(result);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        findImageViewById(R.id.lifecircle_top_right).setVisibility(View.VISIBLE);
        TopUtil.setOnclickListener(mActivity, R.id.lifecircle_top_right, this);
    }

    private void initHeaderView(PicCircleTuiListEntity entity) throws Exception {

        if (headerView == null) {
            headerView = LayoutInflater.from(mContext).inflate(
                    R.layout.puretextcircle_header, null);
        } else {
            mListView.removeHeaderView(headerView);
        }
        mListView.addHeaderView(headerView);
        pureheader_lin_quan = (LinearLayout) headerView.findViewById(R.id.pureheader_lin_quan);
        pureheader_iv = (CircleImageView) headerView
                .findViewById(R.id.pureheader_iv);
        pureheader_nametv = (TextView) headerView
                .findViewById(R.id.pureheader_nametv);
        pureheader_titletv = (TextView) headerView
                .findViewById(R.id.pureheader_titletv);
        pureheader_topfirsttv = (TextView) headerView
                .findViewById(R.id.pureheader_topfirsttv);
        pureheader_topsecndtv = (TextView) headerView
                .findViewById(R.id.pureheader_topsecndtv);
        loader.displayImage(entity.data.quan.icon, pureheader_iv, true);
        pureheader_nametv.setText(entity.data.quan.qname);
        pureheader_titletv.setText(entity.data.quan.descrip);
        if (entity.data.top != null && entity.data.top.size() > 0) {
            if (entity.data.top.get(0) != null) {
                pureheader_topfirsttv.setVisibility(View.VISIBLE);
                pureheader_topfirsttv.setText(entity.data.top.get(0).title);
            }
            if (entity.data.top.get(1) != null) {
                pureheader_topsecndtv.setVisibility(View.VISIBLE);
                pureheader_topsecndtv.setText(entity.data.top.get(1).title);
            }
        }
        pureheader_activitylv = findLinearLayoutById(R.id.pureheader_activitylv);
        pureheader_sginlv = findLinearLayoutById(R.id.pureheader_sginlv);
        pureheader_searchv = findLinearLayoutById(R.id.pureheader_searchv);
        pureheader_taglv = findLinearLayoutById(R.id.pureheader_taglv);
        pureheader_activitylv.setOnClickListener(this);
        pureheader_sginlv.setOnClickListener(this);
        pureheader_searchv.setOnClickListener(this);
        pureheader_taglv.setOnClickListener(this);
        pureheader_lin_quan.setOnClickListener(this);
    }

    @Override
    public void onRefresh() {
        curPage = 1;
        requestUrl(curPage);
    }

    @Override
    public void onLoadMore() {
        if (totoalCount <= mAdapter.getItemCount()) {
            toast("已经到底了，没有更多信息了。");
            mListView.stopLoadMore();
            mListView.setPullLoadEnable(false);
            return;
        }
        curPage++;
        requestUrl(curPage);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        LayoutInflater inflater = LayoutInflater.from(this);
        switch (v.getId()) {
            case R.id.lifecircle_top_right:
                intent.setClass(this, PublishActivity.class);
                intent.putExtra("qid", qid);
                intent.putExtra("qname", pureheader_nametv.getText().toString());
                startActivity(intent);
                break;
            case R.id.pureheader_activitylv:
                intent.setClass(this, ActiveListActivity.class);
                intent.putExtra("qid", qid);
                startActivity(intent);
                break;
            case R.id.pureheader_searchv:
                intent = new Intent(this, HomeSearchActivity.class);
                startActivity(intent);
                break;

            case R.id.lifecircle_top_title:
                final LinearLayout parentLayout = (LinearLayout) inflater.inflate(
                        R.layout.spinner_onlylistview, null);
                SpinnerUtil.createListSpinner(mActivity,
                        (View) findViewById(R.id.topline),
                        R.array.circle_topbar_titleList, titleClickindex,
                        parentLayout, new OnSpinnerItemClickListener() {
                            @Override
                            public void onSpinnerItemClick(int position) {
                                titleClickindex = position;
                                String[] titleArray = getResources()
                                        .getStringArray(
                                                R.array.circle_topbar_titleList);
                                mTopTitle.setText(titleArray[position]);
                                showLoadingBar();
                                curPage = 1;
                                requestUrl(curPage);
                            }
                        });
                break;
            case R.id.pureheader_taglv:

                parenttagLayout = (LinearLayout) inflater.inflate(
                        R.layout.tag_girdview, null);
                mPopupWindow = new PopupWindow(parenttagLayout,
                        AppHelper.getScreenWidth(this), LayoutParams.MATCH_PARENT,
                        true);
                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                lb = (LoadingBar) parenttagLayout
                        .findViewById(R.id.mGeneralLoadingBar);

                requesSelecttUrl("tag.quantaglist", 0);


                break;
            case R.id.pureheader_sginlv:
                requesSelecttUrl("quan.quansign", 1);
                break;
            case R.id.pureheader_lin_quan:
                intent = new Intent(mContext, CircleintroduceActivity.class);
                intent.putExtra("qid", qid);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    private void requesSelecttUrl(String callback, int type) {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback(callback);
        encryptInfo.resetParams();
        encryptInfo.putParam("qid", qid);
        if (type == 0) {
            request.setCallback(tagjsonCallback);
        } else if (type == 1) {
            showTipDialog("保存中...");
            request.setCallback(signjsonCallback);
        }

        request.execute();
    }

    JsonCallback<CircleTagEntity> tagjsonCallback = new JsonCallback<CircleTagEntity>() {

        @Override
        public void onFailure(AppException arg0) {
            lb.setVisibility(View.GONE);
        }

        @Override
        public void onSuccess(CircleTagEntity result) {
            lb.setVisibility(View.GONE);
            if (result == null || result.data == null) {
                // loadingBar.showFailLayout();
                return;
            }
            circleTagEntity = result;
            if (circleTagEntity.data.list != null) {
                fetchData(result);
            }
        }
    };

    private void fetchData(CircleTagEntity result) {
        if (result.data.list.size() == 1) {
            mPopupWindow.showAsDropDown(
                    (View) headerView.findViewById(R.id.pureheader_buttomline),
                    0, 0);
            tag_gridview = (GridNoScrollView) parenttagLayout
                    .findViewById(R.id.tag_gridview);

            // 生成动态数组，并且转入数据
            ArrayList<SecondTag> lstImageItem = new ArrayList<SecondTag>();
            for (int i = 0; i < result.data.list.get(0).taglist.size(); i++) {
                lstImageItem.add(result.data.list.get(0).taglist.get(i));
            }

            // 添加并且显示
            tag = new TagAdapter(this, lstImageItem);
            tag_gridview.setAdapter(tag);
            if (tagPosition != -1) {
                tag.chiceState(tagPosition);
            }
            tag_gridview.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    tagPosition = position;
                    tag.chiceState(position);
                    hashtag = new JsonObject();
                    hashtag.addProperty(
                            String.valueOf(circleTagEntity.data.list.get(0).partid),
                            String.valueOf(circleTagEntity.data.list.get(0).taglist
                                    .get(position).tagid));
                    curPage = 1;
                    requestUrl(curPage);
                    mPopupWindow.dismiss();
                }
            });
        } else {
            if (iffirst) {
                iffirst = false;
                selectp = new int[circleTagEntity.data.list.size()];
                for (int i = 0; i < circleTagEntity.data.list.size(); i++) {
                    selectp[i] = -1;
                }
            }
            Intent intent = new Intent(this, TagarrayActivity.class);
            intent.putExtra("circleTagEntity", circleTagEntity);
            intent.putExtra("selectposition", selectp);
            startActivityForResult(intent, SELECTTAGS);

        }
    }

    JsonCallback<StatusEntity> signjsonCallback = new JsonCallback<StatusEntity>() {

        @Override
        public void onSuccess(StatusEntity result) {
            hideTipDialog();
            if (result != null) {
                if (result.errCode == 0) {
                    toast(result.data.msg);
                } else {
                    toast(result.errMessage);
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
    public void onItemClick(AdapterView<?> adapterView, View view,
                            int position, long id) {

        Intent intent = new Intent(this, CircleTipActivity.class);
        intent.putExtra("tid", mData.data.list.get(position - 2).tid);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == SELECTTAGS) {// 选择标签
            Bundle bundle = data.getExtras();
            selectp = bundle.getIntArray("selectposition");
            hashtag = new JsonObject();
            for (int i = 0; i < selectp.length; i++) {
                if (selectp[i] != -1) {
                    hashtag.addProperty(
                            String.valueOf(circleTagEntity.data.list.get(i).partid),
                            String.valueOf(circleTagEntity.data.list.get(i).taglist
                                    .get(selectp[i]).tagid));
                }
            }
            curPage = 1;
            requestUrl(curPage);

        }
    }

}

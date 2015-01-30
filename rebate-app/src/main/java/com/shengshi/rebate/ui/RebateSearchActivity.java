package com.shengshi.rebate.ui;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.rebate.R;
import com.shengshi.rebate.adapter.HomeAdapter;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.api.RebateRequest;
import com.shengshi.rebate.bean.home.HomeEntity;
import com.shengshi.rebate.config.RebateConstants;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.ui.base.RebateBaseActivity;
import com.shengshi.rebate.utils.RebateTool;

/**
 * <p>Title:        主页搜索
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-8
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateSearchActivity extends RebateBaseActivity implements OnClickListener,
        IXListViewListener {

    XListView mListView;
    HomeAdapter mAdapter;
    HomeEntity mData;
    int curPage = 1;
    int totoalCount = 0;

    EditText mSearchEdit;
    TextView tipTv;//搜索结果提示

    @Override
    protected void initComponents() {
        super.initComponents();
        hideLoadingBar();
        initSearchEditView();
        initListView();
//		TopUtil.HideViewByGONE(mActivity, R.id.rebate_top_title);
        TopUtil.showView(mActivity, R.id.rebate_search);
    }

    private void initSearchEditView() {
        mSearchEdit = findEditTextById(R.id.rebate_search);
        mSearchEdit.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchEdit.requestFocus();
        mSearchEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                RebateTool.showKeybord(mSearchEdit);
            }
        }, 500);
        mSearchEdit.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    requestSearch(curPage, mSearchEdit.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void initListView() {
        mListView = findXListView();
        mListView.setPullLoadEnable(true);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.setOnScrollListener(new PauseOnScrollListener(imageLoader.getImageLoader(), true,
                false));
        View emptyView = View.inflate(mContext, R.layout.widget_no_data, null);
        tipTv = (TextView) emptyView.findViewById(R.id.default_no_data_txt);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        ((ViewGroup) mListView.getParent()).addView(emptyView, params);
        mListView.setEmptyView(emptyView);
    }

    @Override
    protected void initData() {
    }

    private void requestSearch(int page, String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            toast(R.string.rebate_search_hint_tip);
            refreshListView();
            return;
        }
        RebateTool.hideKeybord(mContext, mSearchEdit);
        showLoadingBar();
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        RebateRequest request = new RebateRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("card.index.index");
        encryptInfo.resetParams();
        encryptInfo.putParam("page", page);
        encryptInfo.putParam("page_size", RebateConstants.PAGE_SIZE_20);
        encryptInfo.putParam("category_id", 1);//类目,默认值1
        encryptInfo.putParam("area", -1);//区域,默认值-1
        encryptInfo.putParam("sort", -1);//排序,默认值-1
        encryptInfo.putParam("type", 1);//搜索类型，默认值为0，表示搜索结果包括今日五折；传1，表示搜索结果不含今日五折
        encryptInfo.putParam("keyword", keyword);//搜索关键字
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<HomeEntity> jsonCallback = new JsonCallback<HomeEntity>() {

        @Override
        public void onSuccess(HomeEntity result) {
            //FIXME 使用本地测试数据
//			hideLoadingBar();
//			String json = FileUtils.readAssetsFile(mContext, "home.json");
//			result = JsonUtil.toObject(json, HomeEntity.class);
//			fetchData(result);
//			refreshListView();
            //正式代码
            if (result == null || result.data == null) {
                showFailLayout();
                return;
            }
            hideLoadingBar();
            fetchData(result);
            refreshListView();
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
            hideLoadingBar();
            refreshListView();
            //FIXME 测试代码
//			String json = FileUtils.readAssetsFile(mContext, "home.json");
//			HomeEntity result = JsonUtil.toObject(json, HomeEntity.class);
//			fetchData(result);
//			refreshListView();
        }
    };

    protected void fetchData(HomeEntity result) {
        tipTv.setText(getString(R.string.rebate_search_no_data));
        try {
            if (curPage == 1) {
                mData = result;
                mAdapter = new HomeAdapter(mContext, mData);
                mListView.setAdapter(mAdapter);
                totoalCount = result.data.shop.count;
                if (totoalCount > mAdapter.getCount()) {
                    mListView.setPullLoadEnable(true);
                } else {
                    mListView.setPullLoadEnable(false);
                }
            } else {
                mAdapter.addData(result);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_home_search;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int id = v.getId();
        //TODO 首页一些点击动作
        if (id == R.id.rebate_top_right_img) {
            intent.setClass(this, RebateCardActivity.class);
        }
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        curPage = 1;
        requestSearch(curPage, mSearchEdit.getText().toString());
    }

    private void refreshListView() {
        if (curPage == 1) {
            mListView.stopRefresh();
            mListView.setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
        } else {
            mListView.stopLoadMore();
        }
        if (mAdapter != null) {
            if (totoalCount <= mAdapter.getCommonItemCount()) {
                mListView.stopLoadMore();
            } else {
                mListView.setPullLoadEnable(true);
            }
        }
    }

    @Override
    public void onLoadMore() {
        if (totoalCount <= mAdapter.getCommonItemCount()) {
            toast(R.string.rebate_load_more_complete);
            mListView.stopLoadMore();
            mListView.setPullLoadEnable(false);
            return;
        }
        curPage++;
        requestSearch(curPage, mSearchEdit.getText().toString());
    }

    @Override
    public String getTopTitle() {
        return "搜索";
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        RebateTool.hideKeybord(mContext, mSearchEdit);
        super.finish();
    }

}

package com.shengshi.ufun.ui.home;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.TimeUitls;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.base.widget.xlistview.XListView.IXListViewListener;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.home.HomeAdapter;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.BannerEntity;
import com.shengshi.ufun.bean.home.HomeEntity;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.ChangeCityActivity;
import com.shengshi.ufun.ui.MainActivity;
import com.shengshi.ufun.ui.base.LifeCircleBaseMainActivity;
import com.shengshi.ufun.ui.circle.CircleTipActivity;
import com.shengshi.ufun.utils.ImageLoader;
import com.shengshi.ufun.utils.UFunTool;
import com.shengshi.ufun.utils.UfunFile;
import com.shengshi.ufun.utils.location.LocationResultMgr;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class HomeActivity extends LifeCircleBaseMainActivity implements OnClickListener,
        IXListViewListener, OnItemClickListener {

    TextView lifecircle_top_tv_return;
    String cityName;
    String cityId;
    RefreshHomeReceiver mRefreshHomeReceiver;
    TextView home_toptab_recommed_tv;
    TextView home_toptab_nearby_tv;
    FrameLayout home_toptab_recommed_fv;
    FrameLayout home_toptab_nearby_fv;
    //	ImageView home_toptab_recommed_arrow_iv;
//	ImageView home_toptab_nearby_arrow_iv;
    XListView[] mListView;
    HomeEntity[] mData;
    HomeAdapter[] mAdapter;
    ImageLoader loader;
    int[] curPage;
    int[] totoalCount;
    int type;
    Boolean checkCity = true;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        setReturnBtnEnable(false);
        SetCity();
        lifecircle_top_tv_return = findTextViewById(R.id.lifecircle_top_tv_return);
        lifecircle_top_tv_return.setVisibility(View.VISIBLE);
        lifecircle_top_tv_return.setOnClickListener(this);
        TopUtil.updateRight(mActivity, R.id.lifecircle_top_right, R.drawable.search);
        TopUtil.setOnclickListener(mActivity, R.id.lifecircle_top_right, this);
        TopUtil.updateTextViewIconRight(mActivity, lifecircle_top_tv_return, R.drawable.topbar_drop);
        home_toptab_recommed_tv = findTextViewById(R.id.home_toptab_recommed_tv);
        home_toptab_nearby_tv = findTextViewById(R.id.home_toptab_nearby_tv);
        home_toptab_recommed_fv = findFrameLayoutById(R.id.home_toptab_recommed_fv);
        home_toptab_nearby_fv = findFrameLayoutById(R.id.home_toptab_nearby_fv);
//		home_toptab_recommed_arrow_iv = findImageViewById(R.id.home_toptab_recommed_arrow_iv);
//		home_toptab_nearby_arrow_iv = findImageViewById(R.id.home_toptab_nearby_arrow_iv);
        home_toptab_recommed_fv.setOnClickListener(this);
        home_toptab_nearby_fv.setOnClickListener(this);
        home_toptab_recommed_tv.setOnClickListener(this);
        home_toptab_nearby_tv.setOnClickListener(this);
        mListView = new XListView[2];
        mData = new HomeEntity[2];
        mAdapter = new HomeAdapter[2];
        curPage = new int[2];
        curPage[0] = 1;
        curPage[1] = 1;
        totoalCount = new int[2];
        mListView[0] = findXListViewById(R.id.home_recommed_list);
        mListView[1] = findXListViewById(R.id.home_nearby_list);
        mListView[0].setPullLoadEnable(false);
        mListView[0].setPullRefreshEnable(true);
        mListView[0].setXListViewListener(this);
        mListView[0].setOnItemClickListener(this);
        mListView[0].setOnScrollListener(new PauseOnScrollListener(imageLoader.getImageLoader(),
                true, false));
        mListView[1].setPullLoadEnable(false);
        mListView[1].setPullRefreshEnable(true);
        mListView[1].setXListViewListener(this);
        mListView[1].setOnItemClickListener(this);
        mListView[1].setOnScrollListener(new PauseOnScrollListener(imageLoader.getImageLoader(),
                true, false));
        loader = ImageLoader.getInstance(mActivity);
    }

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.headline);
    }

    @Override
    protected void initData() {
        initBroadCast();
        requestUrl(1);
        banner();
        umengUpdateAgent();
        initBroadCast();
    }

    private void SetCity() {
        String setCityName = UFunTool.getCityName(this);
        cityName = setCityName;
        cityId = UFunTool.getCityId(this);
        if (!TextUtils.isEmpty(setCityName)) {
            setCityName = setCityName.replace("市", "");
        } else {
            setCityName = "选择城市";
        }
        TopUtil.updateLeftTitle(mActivity, R.id.lifecircle_top_tv_return, setCityName);
        Log.i("------->当前城市===" + cityName);
    }

    private void initBroadCast() {
        mRefreshHomeReceiver = new RefreshHomeReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(UFunKey.ACTION_REFRESH_HOME_DATA);
        registerReceiver(mRefreshHomeReceiver, mFilter);
    }

    private class RefreshHomeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UFunKey.ACTION_REFRESH_HOME_DATA)) {
                refreshData();
                MainActivity.refresh0 = false;
            }
        }
    }

    private void requestUrl(int curPage) {
        if (curPage == 1) {
            showLoadingBar();
        }
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("home.index");
        encryptInfo.resetParams();
        encryptInfo.putParam("type", type);
        encryptInfo.putParam("page", curPage);
        encryptInfo.putParam("page_size", UFunConstants.PAGE_SIZE);
        request.setCallback(jsonCallback);
        request.execute();

    }

    JsonCallback<HomeEntity> jsonCallback = new JsonCallback<HomeEntity>() {

        @Override
        public void onFailure(AppException arg0) {
            hideLoadingBar();

        }

        @Override
        public void onSuccess(HomeEntity result) {
            hideLoadingBar();
            if (result == null || result.data == null) {
                loadingBar.showFailLayout();
                return;
            }
            if (result.data.list != null) {
                fetchData(result);
            }
            refreshListView(result);
        }

    };

    private void refreshListView(HomeEntity result) {
        if (curPage[type] == 0) {
            mListView[type].stopRefresh();
            mListView[type].setRefreshTime(TimeUitls.getCurrentTime("MM-dd HH:mm"));
            curPage[type] = 1;
        } else if (curPage[type] > 0 && mAdapter != null && result.data.list != null) {
            if (totoalCount[type] <= mAdapter[type].getItemCount()) {
                mListView[type].setPullLoadEnable(false);
            } else {
                mListView[type].setPullLoadEnable(true);
            }
            mListView[type].stopLoadMore();
        }

    }

    protected void fetchData(HomeEntity result) {
        if (curPage[type] == 0) {
            if (result.data.newnum != 0) {
                HomeEntity topThreads = result;
                topThreads.data.list.addAll(mData[type].data.list);
                mData[type] = topThreads;
                mAdapter[type] = new HomeAdapter(mContext, topThreads);
                mListView[type].setAdapter(mAdapter[type]);
            }

        } else if (curPage[type] == 1) {
            totoalCount[type] = result.data.count;
            mData[type] = result;
            mAdapter[type] = new HomeAdapter(mContext, mData[type]);
            mListView[type].setAdapter(mAdapter[type]);
        } else {
            mAdapter[type].addData(result);
            mAdapter[type].notifyDataSetChanged();
        }

    }

    /* 导航图片 */
    private void banner() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("home.banner");
        request.setCallback(jsonBannerCallback);
        request.execute();
    }

    JsonCallback<BannerEntity> jsonBannerCallback = new JsonCallback<BannerEntity>() {
        @Override
        public void onFailure(AppException arg0) {
        }

        @Override
        public void onSuccess(BannerEntity result) {
            if (result != null) {
                BannerEntity banner = UfunFile.getBanner(mContext);
                String mbimg = banner.data != null ? banner.data.pic : "";
                if (result.data != null && result.data.pic != null) {
                    String img = result.data.pic;
                    ImageView bannerAd = findImageViewById(R.id.bannerAd);
                    if (img.equals(mbimg)) {
                        String md5 = banner.data.md5;
                        String mbmd5 = UfunFile.md5(img);
                        //比对文件完整性
                        if (!md5.equals(mbmd5)) {
                            UfunFile.delete(mbimg);
                            imageLoader.displayImage(img, bannerAd);
                        }
                        //Log.i("---------->mbanner1=="+md5+"==mbmd5=="+mbmd5+"=="+img+"=="+mbimg);
                    } else {
                        UfunFile.delete(mbimg);
                        imageLoader.displayImage(img, bannerAd);
                        //Log.i("---------->mbanner2==");
                    }
                    UfunFile.saveBanner(mContext, result);
                } else {
                    UfunFile.delete(mbimg);
                    UfunFile.saveBanner(mContext, new BannerEntity());
                    //Log.i("---------->mbanner3==");
                }
            }
            checkCityName();
        }
    };

    public void checkCityName() {
        if (checkCity) {
            return;
        }
        checkCity = false;
        String checkCityName = LocationResultMgr.getInstance(this).getCityName();
        Log.i("--------->checkCityName==" + checkCityName + "===cityName===" + cityName);
        if (TextUtils.isEmpty(checkCityName)) {
            return;
        }
        if (checkCityName.equals(cityName)) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String message = "您当前所在的城市为：" + checkCityName + "，是否立即切换？";
        builder.setTitle("提示");
        builder.setMessage(message).setCancelable(true)
                .setPositiveButton("切换", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(HomeActivity.this, ChangeCityActivity.class);
                        intent.putExtra("tp", 2);
                        startActivityForResult(intent, 1);
                    }
                }).setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /* 版本检测 */
    private void umengUpdateAgent() {
        UmengUpdateAgent.update(this);    //从服务器获取更新信息
        UmengUpdateAgent.setUpdateOnlyWifi(false);     //是否在只在wifi下提示更新，默认为 true
        UmengUpdateAgent.setUpdateAutoPopup(false);    //是否自动弹出更新对话框
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case 0:// 有更新
                        UmengUpdateAgent.showUpdateDialog(HomeActivity.this, updateInfo);
                        break;
                    case 1:// 无更新
                        //toast("当前已是最新版.");
                        break;
                    case 2:// 如果设置为wifi下更新且wifi无法打开时调用
                        //toast("没有wifi连接， 只在wifi下更新");
                        break;
                    case 3:// 连接超时
                        //toast("连接超时，请稍候重试");
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            refreshData();
            MainActivity.refresh0 = false;
        }
    }

    /*
     * 更新数据
     */
    public void refreshData() {
        requestUrl(1);
        SetCity();
        banner();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        if (id == R.id.lifecircle_top_tv_return) {
            intent = new Intent(this, ChangeCityActivity.class);
            intent.putExtra("tp", 2);
            startActivityForResult(intent, 1);
        } else if (id == R.id.lifecircle_top_right) {
            intent = new Intent(this, HomeSearchActivity.class);
            startActivity(intent);
        } else if (id == R.id.home_toptab_recommed_fv || id == R.id.home_toptab_recommed_tv) {
            type = 0;
            TopUtil.updateTopTextViewIconLeft(mActivity, home_toptab_recommed_tv,
                    R.drawable.recommend_active);
            TopUtil.updateTopTextViewIconLeft(mActivity, home_toptab_nearby_tv,
                    R.drawable.icon_nearby);
            home_toptab_recommed_tv.setTextColor(getResources()
                    .getColor(R.color.white));
            home_toptab_nearby_tv.setTextColor(getResources().getColor(R.color.sub_heading_text));
//			home_toptab_recommed_arrow_iv.setVisibility(View.VISIBLE);
//			home_toptab_nearby_arrow_iv.setVisibility(View.GONE);
            home_toptab_recommed_fv.setBackgroundResource(R.drawable.hometab_sanjiao);
            home_toptab_nearby_fv.setBackgroundResource(R.color.home_toptab_bg);
            mListView[0].setVisibility(View.VISIBLE);
            mListView[1].setVisibility(View.GONE);
            if (mData[type] == null) {
                requestUrl(curPage[type]);
            }
        } else if (id == R.id.home_toptab_nearby_fv || id == R.id.home_toptab_nearby_tv) {
            type = 1;
            TopUtil.updateTopTextViewIconLeft(mActivity, home_toptab_nearby_tv,
                    R.drawable.icon_nearby_active);
            TopUtil.updateTopTextViewIconLeft(mActivity, home_toptab_recommed_tv,
                    R.drawable.recommend);
            home_toptab_recommed_tv.setTextColor(getResources().getColor(R.color.sub_heading_text));
            home_toptab_nearby_tv.setTextColor(getResources().getColor(R.color.white));
            home_toptab_nearby_fv.setBackgroundResource(R.drawable.hometab_sanjiao);
            home_toptab_recommed_fv.setBackgroundResource(R.color.home_toptab_bg);
            mListView[0].setVisibility(View.GONE);
            mListView[1].setVisibility(View.VISIBLE);
            if (mData[type] == null) {
                requestUrl(curPage[type]);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, CircleTipActivity.class);
        switch (adapterView.getId()) {
            case R.id.home_recommed_list:
                intent.putExtra("tid", mData[0].data.list.get(position - 1).tid);
                startActivity(intent);
                break;
            case R.id.home_nearby_list:
                intent.putExtra("tid", mData[1].data.list.get(position - 1).tid);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    public void onRefresh() {
        requestUrl(0);
        curPage[type] = 0;
    }

    @Override
    public void onLoadMore() {
        curPage[type]++;
        requestUrl(curPage[type]);
    }

    @Override
    public void requestAgain() {
        super.requestAgain();
        onRefresh();
    }

}

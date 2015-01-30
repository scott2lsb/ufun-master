package com.shengshi.rebate.ui.web;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.shengshi.base.config.Key;
import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.NetUtil;
import com.shengshi.base.tools.ToastUtils;
import com.shengshi.rebate.R;
import com.shengshi.rebate.ui.base.RebateBaseActivity;
import com.shengshi.rebate.utils.AppMgrUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.Random;
import java.util.concurrent.Executors;

/**
 * <p>Title:       浏览器
 * <p>Description:  web页面、h5页面统一在此类打开，不要新建。
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-2
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class BrowserActivity extends RebateBaseActivity {

    private WebView mBrowser; // 浏览器控件
    private ProgressBar mProgressBar; // 进度条

    private String mUrl;
    private String topTitle;

    private int mErrorCode;
    private String mTip = "页面找不到,请稍候再试";

    private Handler webMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mErrorCode = msg.what;
            if (!NetUtil.isNetConnectionAvailable(mContext)) {
                mTip = getString(R.string.network_not_connected);
            }
            if (msg.what == 404 || msg.what == -2 || msg.what == -8 || msg.what == -10) {
                //在no_found.html页面根据js接口,改变404、超时、不支持该协议等提示信息
                mBrowser.loadUrl("file:///android_asset/no_found.html");
            }
        }
    };

    @Override
    protected void initComponents() {
        super.initComponents();
        mBrowser = (WebView) findViewById(R.id.rebate_browser_view);
        mProgressBar = (ProgressBar) findViewById(R.id.rebate_browser_progressbar);
    }

    @Override
    protected void initData() {
        initStrictMode();
        initUrl();
        configWebView();
        getHttpMsg();
        //FIXME 测试WebView
//		mUrl = "file:///android_asset/no_found.html";
        mUrl = "http://mapi.dev.xiaoyu.com/test/test.html";
//		mUrl = "http://www.ppche.com/insurance/info";
        mBrowser.loadUrl(mUrl);//此时不会执行shouldOverrideUrlLoading进行页面是否存在的判断
    }

    //用于使网络操作可以在主线程中运行
    private void initStrictMode() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll()
                    .build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void initUrl() {
        mUrl = getIntent().getStringExtra(Key.KEY_BROWSER_URL);
        topTitle = getIntent().getStringExtra(Key.KEY_BROWSER_TITLE);
    }

    /**
     * 配置webview
     */
    private void configWebView() {
        mBrowser.setDownloadListener(new OnDownloadListener(mContext));
        mBrowser.setWebViewClient(new RebateWebClient());
        mBrowser.setWebChromeClient(new OnWebChromeClientListener());
        WebSettings webSettings = mBrowser.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);// javascript可以自动打开窗体
        webSettings.setJavaScriptEnabled(true);// 允许javascript
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);//set xml dom cache
        //set cache
        String appCachePath = getDir("netCache", Context.MODE_PRIVATE).getAbsolutePath();
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(appCachePath);
        if (NetUtil.isNetConnectionAvailable(mContext)) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

//		mBrowser.addJavascriptInterface(new ShowTip(), "android_rebate");
        mBrowser.addJavascriptInterface(new ShowTip(), "WebViewJavascriptBridge");
        mBrowser.addJavascriptInterface(new HtmlOpenActivity(mContext), "openapk");// html調用Android方法,打开页面。

        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);// 可以缩放
        if (AppHelper.isPhoneVersionTen())
            webSettings.setDisplayZoomControls(false);

        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setGeolocationEnabled(true);
        String dir = mContext.getDir("database", Context.MODE_PRIVATE).getPath();//data/data/com.shengshi.rebate/app_database
        webSettings.setDatabasePath(dir);
        webSettings.setGeolocationDatabasePath(dir);
        webSettings.setUserAgentString(webSettings.getUserAgentString() + " microfish/"
                + AppHelper.getVersionName(mContext));
        Log.d("UA: " + webSettings.getUserAgentString());
    }

    /**
     * WebViewClient 设置，继承底层
     */
    class RebateWebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!super.shouldOverrideUrlLoading(view, url)) {
                if (url.startsWith("http://")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    try {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        ToastUtils.showToast(mActivity, R.string.net_loading_web_no_find,
                                Toast.LENGTH_SHORT);
                    }
                    return true;
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        /**
         * 只有设备的网络状态处于断开的情况下、超时等才会进入onReceivedError方法，
         * 如果是URL路径对应的服务端返回了404是不会进入这个方法的。
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                                    String failingUrl) {
            mTip = description;
            webMsgHandler.obtainMessage(errorCode).sendToTarget();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }

    /**
     * webview内核配置
     */
    private class OnWebChromeClientListener extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress >= 90) {
                mProgressBar.setVisibility(View.GONE);
            } else if (mProgressBar.getVisibility() != View.VISIBLE) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            mProgressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }

    }

    /**
     * 回退按钮捕获
     */
    @Override
    public boolean onKeyDown(int keyCoder, KeyEvent event) {
        if (keyCoder == KeyEvent.KEYCODE_BACK) {
            if (mErrorCode != 404 && mErrorCode != -2 && mErrorCode != -8 && mErrorCode != -10) {
                if (mBrowser.canGoBack()) {
                    mBrowser.goBack();
                } else {
                    this.finish();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCoder, event);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_browser;
    }

    /**
     * 获取http响应信息
     */
    private void getHttpMsg() {
        Executors.newSingleThreadExecutor().submit(new Runnable() {

            @Override
            public void run() {
                Message msg = webMsgHandler.obtainMessage();
                if (getRespStatus(mUrl) == 404) {
                    msg.what = 404;
                }
                webMsgHandler.sendMessage(msg);
            }
        });
    }

    /**
     * 获取url链接地址的响应状态
     * 注：耗时操作
     *
     * @param url
     * @return
     */
    private int getRespStatus(String url) {
        int status = -1;
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(httpGet);
            status = resp.getStatusLine().getStatusCode();
        } catch (Exception e) {
            Log.e("--检测不到url链接地址的响应状态--");
        }
        return status;
    }

    /**
     * 供web页面js调用接口
     */
    public class ShowTip {

        public String getTip() {
            Random random = new Random();
            return random.nextInt(100) + "";
//			return mTip;
        }

        public void toast(String msg) {
            ToastUtils.showToast(mActivity, msg, 500);
        }
    }

    public class HtmlOpenActivity {

        private HtmlOpenActivity(Context context) {

        }

        @JavascriptInterface
        public void openLoginPage() {
            ToastUtils.showToast(mActivity, "打开登陆页", 500);
            AppMgrUtils.launchAPP(mActivity, AppHelper.getPackageName(mContext),
                    "com.shengshi.ufun.ui.LoginActivity");
        }

        @JavascriptInterface
        public void openJoinGroup() {
            ToastUtils.showToast(mActivity, "打开pp车抱团页面", 500);
            AppMgrUtils.launchAPP(mActivity, AppHelper.getPackageName(mContext),
                    "com.shengshi.ufun.ui.LoginActivity");
        }
    }

    @Override
    public String getTopTitle() {
        return topTitle;
    }

}

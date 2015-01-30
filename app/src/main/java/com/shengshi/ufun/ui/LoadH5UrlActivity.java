package com.shengshi.ufun.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.shengshi.base.tools.AppHelper;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;

import java.net.URLEncoder;


public class LoadH5UrlActivity extends LifeCircleBaseActivity implements OnClickListener {
    private WebView mWebView;
    private String getUrl;
    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private String baseCodeUrl;
    private TextView mTitle;

    class SetWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                hideLoadingBar();
                mWebView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            // 设置当前标题栏
            mTitle.setText(title);
            super.onReceivedTitle(view, title);
        }

        // 图片上传Android < 3.0 调用这个方法
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }

        // 3.0 + 调用这个方法
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
        }

        // Android > 4.1.1 调用这个方法
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }
    }

    // flipscreen not loading again
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }


    @Override
    protected void initComponents() {
        super.initComponents();
        mTitle = findTextViewById(R.id.lifecircle_top_title);
//		TopUtil.updateRight(mActivity, R.id.lifecircle_top_right,
//				R.drawable.share);
//		TopUtil.setOnclickListener(mActivity, R.id.lifecircle_top_right, this);
        mWebView = (WebView) findViewById(R.id.webview);

    }

    @Override
    public String getTopTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.loading_url;
    }

    @Override
    protected void initData() {
        setReturnBtnEnable(true);
        Intent dataIntent = this.getIntent();
        if (null != dataIntent) {
            getUrl = dataIntent.getStringExtra("url");
            BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
            String baseCode = encryptInfo.encryptCodeByPost();
            if (getUrl.contains("?")) {
                baseCodeUrl = getUrl + "&ukey=" + URLEncoder.encode(baseCode);
            } else {
                baseCodeUrl = getUrl + "?ukey=" + URLEncoder.encode(baseCode);
            }
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setDomStorageEnabled(true); // 是否开启Dom存储Api
            webSettings.setJavaScriptEnabled(true);// 设置js 可用
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);// 支持控件缩放
            webSettings.setAllowFileAccess(true);
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 优先使用cache
            //设置ua,加上版本号
            String user_agent = webSettings.getUserAgentString();
            String myua = user_agent + " microfish/" + AppHelper.getVersionName(this);
            ;
            webSettings.setUserAgentString(myua);
            mWebView.setWebViewClient(new SetWebViewClient());
            mWebView.setWebChromeClient(new SetWebChromeClient());// 覆盖默认后退按钮的作用，替换成WebView里的查看历史页面
            mWebView.setOnKeyListener(new SetOnKeyListener());
            Message msg = new Message();
            handle.sendMessage(msg);
        } else {
            toast("操作异常，请返回重试");
        }
    }

    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            mWebView.loadUrl(baseCodeUrl);
        }

        ;
    };

    class SetWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

        }
    }

    class SetOnKeyListener implements View.OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
            }
            return false;
        }
    }


    @Override
    public void onClick(View v) {

    }
}

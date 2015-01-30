package com.shengshi.ufun.ui.mine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.nostra13.universalimageloader.utils.StorageUtils;
import com.shengshi.base.tools.AppHelper;
import com.shengshi.base.tools.FileUtils;
import com.shengshi.base.tools.Log;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.BaseEntity;
import com.shengshi.ufun.bean.UserInfoEntity;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.utils.AccountUtil;
import com.shengshi.ufun.utils.UmengShare;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import java.io.File;
import java.util.Map;

public class MySettingActivity extends LifeCircleBaseActivity implements OnClickListener {

    TextView mine_setting_version;
    TextView mine_setting_cache;
    TextView mine_setting_binding;
    UserInfoEntity mUserInfoEntity;
    Boolean bind = false;
    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
    String wxnickname;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_my_setting;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        mine_setting_version = findTextViewById(R.id.mine_setting_version);
        mine_setting_cache = findTextViewById(R.id.mine_setting_cache);
        mine_setting_binding = findTextViewById(R.id.mine_setting_binding);
        TopUtil.setOnclickListener(mActivity, R.id.mine_setting_binding_rl, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_setting_cache_rl, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_setting_message_rl, this);
        TopUtil.setOnclickListener(mActivity, R.id.login_out, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_setting_pwd_rl, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_setting_version_rl, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_setting_feedback_rl, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_setting_recommend_rl, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_setting_about_rl, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_setting_cache_rl, this);
    }

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.mine_setting);
    }

    @Override
    protected void initData() {
        mine_setting_version.setText(AppHelper.getVersionName(this));
        mUserInfoEntity = AccountUtil.getUserInfoEntity(this);
        if (mUserInfoEntity != null && mUserInfoEntity.data != null
                && mUserInfoEntity.data.bind != null) {
            mine_setting_binding.setText("已绑定微信：" + mUserInfoEntity.data.bind.wxname);
            bind = true;
        } else {
            bind = false;
        }
        Message msg = new Message();
        handle.sendMessageDelayed(msg, 800);
        UmengShare.setShare(this, "有范手机客户端,口袋里的有范!",
                "有范手机版,是小鱼网官方针对手机用户推出的本地生活分享交流平台.在这里,分享本地热点,交流生活点滴,结识同城好友.", null, null);
    }

    // 计算缓存大小
    @SuppressLint("HandlerLeak")
    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            caclCacheSize();
        }

        ;
    };

    // 计算缓存大小
    @SuppressLint("NewApi")
    private void caclCacheSize() {
        new AsyncTask<Void, Long, Long>() {

            long fileSize = 0;
            String cacheSize = "0KB";

            @Override
            protected void onPreExecute() {
                mine_setting_cache.setText("正在统计大小...");
            }

            @Override
            protected Long doInBackground(Void... params) {
                //fileSize += FileUtils.getDirSize(getFilesDir());
                fileSize += FileUtils.getDirSize(getCacheDir());
                //sd卡图片
                fileSize += FileUtils.getDirSize(StorageUtils.getCacheDirectory(mContext));
                return fileSize;
            }

            @Override
            protected void onPostExecute(Long result) {
                if (result > 0) {
                    cacheSize = FileUtils.formatFileSize(result);
                }
                mine_setting_cache.setText(cacheSize);
            }

        }.execute();
    }

    /* 版本检测 */
    private void umengUpdateAgent() {
        showTipDialog("新版本检测中...");
        UmengUpdateAgent.update(this); //从服务器获取更新信息
        UmengUpdateAgent.setUpdateOnlyWifi(false); //是否在只在wifi下提示更新，默认为 true
        UmengUpdateAgent.setUpdateAutoPopup(false); //是否自动弹出更新对话框
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                hideTipDialog();
                switch (updateStatus) {
                    case 0:// 有更新
                        toast(updateInfo + "");
                        break;
                    case 1:// 无更新
                        toast("当前已是最新版");
                        break;
                    case 2:// 如果设置为wifi下更新且wifi无法打开时调用
                        toast("没有wifi连接， 只在wifi下更新");
                        break;
                    case 3:// 连接超时
                        toast("连接超时，请稍候重试");
                        break;
                }
            }
        });
    }

    /**
     * 清除app缓存
     *
     * @param activity
     */
    @SuppressLint("HandlerLeak")
    public void clearAppCache(Activity activity) {
        showTipDialog("缓存清除中...");
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                hideTipDialog();
                if (msg.what == 1) {
                    mine_setting_cache.setText("0KB");
                    toast("缓存清除成功");
                } else {
                    toast("缓存清除失败");
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        // 清除webview缓存
//		@SuppressWarnings("deprecation")
//		File file = CacheManager.getCacheFileBaseDir();
//		if (file != null && file.exists() && file.isDirectory()) {
//			for (File item : file.listFiles()) {
//				item.delete();
//			}
//			file.delete();
//		}
        deleteDatabase("webview.db");
        deleteDatabase("webview.db-shm");
        deleteDatabase("webview.db-wal");
        deleteDatabase("webviewCache.db");
        deleteDatabase("webviewCache.db-shm");
        deleteDatabase("webviewCache.db-wal");
        // 清除数据缓存
        //clearCacheFolder(getFilesDir(), System.currentTimeMillis());
        clearCacheFolder(getCacheDir(), System.currentTimeMillis());
        clearCacheFolder(StorageUtils.getCacheDirectory(mContext), System.currentTimeMillis());// sd卡图片
    }

    /**
     * 清除缓存目录
     *
     * @param dir     目录
     * @param numDays 当前系统时间
     * @return
     */
    private int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    /*
     * 微信平台登录
     */
    public void uMWX() {
        showTipDialog("微信绑定中...");
        UMWXHandler wxHandler = new UMWXHandler(this, UFunConstants.WX_APPID,
                UFunConstants.WX_APPSECRET);
        wxHandler.addToSocialSDK();
        mController.doOauthVerify(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                //toast("授权开始");
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                hideTipDialog();
                //toast("授权错误");
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                //toast("授权完成");
                //获取相关授权信息
                mController.getPlatformInfo(MySettingActivity.this, SHARE_MEDIA.WEIXIN,
                        new UMDataListener() {
                            @Override
                            public void onStart() {
                                //toast("获取平台数据开始...");
                            }

                            @Override
                            public void onComplete(int status, Map<String, Object> info) {
                                if (status == 200 && info != null) {

                                    String openid = null;
                                    String headimgurl = null;
                                    if (info.containsKey("openid")) {
                                        openid = info.get("openid").toString();
                                    }
                                    if (info.containsKey("nickname")) {
                                        wxnickname = info.get("nickname").toString();
                                    }
                                    if (info.containsKey("headimgurl")) {
                                        headimgurl = info.get("headimgurl").toString();
                                    }
                                    requestWeiXinBindUrl(openid, wxnickname, headimgurl);
                                } else {
                                    hideTipDialog();
                                    toast("授权失败请重试.");
                                }
                            }
                        });
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                hideTipDialog();
                toast("授权取消");
            }
        });
    }

    /**
     * 微信绑定
     *
     * @param openid
     * @param nickname
     * @param headimgurl
     */
    private void requestWeiXinBindUrl(String openid, String nickname, String headimgurl) {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.weixin_bind");
        encryptInfo.resetParams();
        encryptInfo.putParam("type", "bind");
        encryptInfo.putParam("openid", openid);
        encryptInfo.putParam("wxnickname", nickname);
        encryptInfo.putParam("headimgurl", headimgurl);
        encryptInfo.putParam("uid", encryptInfo.getUid());
        request.setCallback(jsonRegisterCallback);
        request.execute();
    }

    JsonCallback<UserInfoEntity> jsonRegisterCallback = new JsonCallback<UserInfoEntity>() {

        @Override
        public void onSuccess(UserInfoEntity result) {
            hideTipDialog();
            if (result != null) {
                if (result.errCode == 0) {
                    toast("微信绑定成功");
                    bind = true;
                    mine_setting_binding.setText("已绑定微信:" + wxnickname);
                    AccountUtil.saveAccountInfo(mContext, result);
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

    /**
     * 注销登录
     */
    private void requestLogoutUrl() {
        showTipDialog("注销登录中...");
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.logout");
        request.setCallback(jsonLogoutCallback);
        request.execute();
    }

    JsonCallback<BaseEntity> jsonLogoutCallback = new JsonCallback<BaseEntity>() {

        @Override
        public void onSuccess(BaseEntity result) {
            hideTipDialog();
            if (result != null) {
                toast(result.errMessage);
                if (result.errCode == 0) {
                    AccountUtil.removeAccountInfo(MySettingActivity.this);
                    setFinish();
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

    public void setFinish() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        if (id == R.id.login_out) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("是否退出当前帐号？").setCancelable(true)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            requestLogoutUrl();
                        }
                    }).setNegativeButton("取消", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (id == R.id.mine_setting_binding_rl) {
            if (UIHelper.checkLogin(this)) {
                if (!bind) {
                    uMWX();
                }
            } else {
                UIHelper.login(this, 1);
            }
        } else if (id == R.id.mine_setting_pwd_rl) {
            if (UIHelper.checkLogin(this)) {
                intent = new Intent(this, MyUpdatePwdActivity.class);
                startActivity(intent);
            } else {
                UIHelper.login(this, 1);
            }
        } else if (id == R.id.mine_setting_feedback_rl) {
            FeedbackAgent agent = new FeedbackAgent(this);
            agent.startFeedbackActivity();
        } else if (id == R.id.mine_setting_version_rl) {
            umengUpdateAgent();
        } else if (id == R.id.mine_setting_recommend_rl) {
            UmengShare.share(this);
        } else if (id == R.id.mine_setting_cache_rl) {
            clearAppCache(this);
        } else if (id == R.id.mine_setting_message_rl) {
            if (UIHelper.checkLogin(this)) {
                intent = new Intent(this, MyNotifierActivity.class);
                startActivity(intent);
            } else {
                UIHelper.login(this, 1);
            }
        } else if (id == R.id.mine_setting_about_rl) {
            intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

}

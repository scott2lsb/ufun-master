package com.shengshi.ufun.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.shengshi.ufun.R;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.config.UFunUrls;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * <p>Title:    友盟工具类
 * <p>Description:
 * <p>@author:        dengbw
 * <p>Copyright:   Copyright (c) 2015
 * <p>Company:       @小鱼网
 * <p>Create Time: 2015-1-15
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class UmengShare {

    static UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

    /**
     * 友盟分享前先设置内容
     */
    public static void setShare(Activity at, String title, String content, String url,
                                String urlImage) {
        configPlatforms(at);
        setShareContent(at, title, content, url, urlImage);
    }

    /**
     * 友盟分享
     */
    public static void share(Activity at) {
        mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.EMAIL,
                SHARE_MEDIA.SMS);
        mController.openShare(at, false);
    }

    /**
     * 配置分享平台参数</br>
     */
    private static void configPlatforms(Activity at) {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加QQ、QZone平台
        addQQQZonePlatform(at);
        // 添加微信、微信朋友圈平台
        addWXPlatform(at);
        addSMS();
        addEmail();
    }

    /**
     * @return
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     * image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     * 要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     * : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     */
    private static void addQQQZonePlatform(Activity at) {
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(at, UFunConstants.QQ_APPID,
                UFunConstants.QQ_APPKEY);
        //qqSsoHandler.setTargetUrl(UFunUrls.TARGET_URL);
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(at, UFunConstants.QQ_APPID,
                UFunConstants.QQ_APPKEY);
        qZoneSsoHandler.addToSocialSDK();
    }

    /**
     * @return
     * @功能描述 : 添加微信平台分享
     */
    private static void addWXPlatform(Context context) {
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(context, UFunConstants.WX_APPID,
                UFunConstants.WX_APPSECRET);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(context, UFunConstants.WX_APPID,
                UFunConstants.WX_APPSECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * 添加短信平台</br>
     */
    private static void addSMS() {
        // 添加短信
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }

    /**
     * 添加Email平台</br>
     */
    private static void addEmail() {
        // 添加email
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.addToSocialSDK();
    }

    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private static void setShareContent(Activity at, String title, String content, String url,
                                        String urlImage) {
        url = !TextUtils.isEmpty(url) ? url : UFunUrls.SHARE_URL;
        String endContent = "\n分享来自" + at.getResources().getString(R.string.app_name)
                + " Android客户端";
        String mShareContent = content + endContent;
        String mShareContentUrl = content + url + endContent;
        UMImage mUMImgBitmap = null;
        if (!TextUtils.isEmpty(urlImage)) {
            mUMImgBitmap = new UMImage(at, urlImage);
        } else {
            mUMImgBitmap = new UMImage(at, R.drawable.applogo);
        }
        //微信
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setTitle(title);
        weixinContent.setShareContent(mShareContent);
        weixinContent.setTargetUrl(url);
        weixinContent.setShareImage(mUMImgBitmap);
        mController.setShareMedia(weixinContent);
        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setTitle(title);
        circleMedia.setShareContent(mShareContent);
        circleMedia.setTargetUrl(url);
        circleMedia.setShareImage(mUMImgBitmap);
        mController.setShareMedia(circleMedia);

        // 设置QQ分享内容
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setTitle(title);
        qqShareContent.setShareContent(mShareContent);
        qqShareContent.setTargetUrl(url);
        qqShareContent.setShareImage(mUMImgBitmap);
        mController.setShareMedia(qqShareContent);
        // 设置QQ空间分享内容
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setTitle(title);
        qzone.setShareContent(mShareContent);
        qzone.setTargetUrl(url);
        qzone.setShareImage(mUMImgBitmap);
        mController.setShareMedia(qzone);

        // 设置新浪微博分享内容
        SinaShareContent sinaContent = new SinaShareContent();
        sinaContent.setTitle(title);
        sinaContent.setShareContent(mShareContentUrl);
        sinaContent.setTargetUrl(url);
        sinaContent.setShareImage(mUMImgBitmap);
        mController.setShareMedia(sinaContent);

        // 设置邮件分享内容， 如果需要分享图片则只支持本地图片
        MailShareContent mail = new MailShareContent();
        mail.setTitle(title);
        mail.setShareContent(mShareContentUrl);
        // 设置tencent分享内容
        mController.setShareMedia(mail);

        // 设置短信分享内容
        SmsShareContent sms = new SmsShareContent();
        sms.setShareContent(mShareContentUrl);
        sms.setShareImage(mUMImgBitmap);
        mController.setShareMedia(sms);

    }

}

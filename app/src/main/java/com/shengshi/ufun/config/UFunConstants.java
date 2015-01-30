package com.shengshi.ufun.config;

import com.shengshi.base.common.Constants;

/**
 * <p>Title:
 * <p>Description:
 * <p>@author:  dengbw
 * <p>Copyright: Copyright (c) 2015
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-01-05
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class UFunConstants extends Constants {

    // 每页条数
    public static final int PAGE_SIZE = 20;

    //===================================以下是分享信息 =================================================//
    // 微信
    public static final String WX_APPID = "wx8fbd37e4da758bbd";
    public static final String WX_APPSECRET = "de1415014dd1437d187042326bd5888e";
    // QQ
    public static final String QQ_APPID = "1104070329";
    public static final String QQ_APPKEY = "Lkk4P8GrTkcliKZo";

    /**
     * 浏览相册
     */
    public static final String SELECTED_PHOTOS = "selected_photos";
    public static final String SELECTED_ALBUM = "selected_album";
    public static final String SELECTED_NUMBER = "selected_number";
    public static final String CHOSE_NUMBER = "chose_number";
    public static final String CALLBACK_PHOTO = "callback_photo";
    public static final String CHOSE_PHOTO = "chose_photo";
    public static final String TAKE_PHOTO = "take_photo";
    public static final String CALLBACK_LOCATION = "callback_location";
    public static final int LOCATION_RESULT = 0;
    public static final int CAMERA_RESULT_ONE = 1;
    public static final int CAMERA_RESULT_TWO = 2;
    public static final int CAMERA_RESULT_THREE = 3;
    public static final int CAMERA_RESULT_FOUR = 4;
    public static final int ALBUM_RESULT = 5;
    public static final int BROWSE_ALBUM_REQUEST_CODE = 6;
    /**
     * 发帖最多只能选择8张图片
     */
    public static final int MAX_CHOICE_PHOTO_NUM_8 = 8;
    public static int MAX_CHOICE_PHOTO_NUM = MAX_CHOICE_PHOTO_NUM_8;
}

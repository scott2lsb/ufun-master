package com.shengshi.rebate.utils;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.shengshi.base.image.BaseImageLoader;
import com.shengshi.rebate.R;

/**
 * <p>Title:           图片显示工具
 * <p>Description:
 * 1.返利卡项目都用此工具加载图片，如需扩展，就继承或者在此添加
 * 2.不要改成单例模式，具体原因自己想想。
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-10-29
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class RebateImageLoader extends BaseImageLoader {

    public DisplayImageOptions.Builder builder;

    public static RebateImageLoader getInstance(Context context) {
        return new RebateImageLoader(context);
    }

    private RebateImageLoader(Context context) {
        super(context);
        builder = getDefaultDisplayOptionsBuilder();
    }

    @Override
    public int getDefaultImageOnLoading() {
//		return R.drawable.circle_default_icon;
        return R.color.login_line;
    }

    @Override
    public int getDefaultImageOnLoadFail() {
//		return R.drawable.circle_default_icon;
        return R.color.login_line;
    }

    /**
     * 改变默认正在加载图片
     *
     * @param loadingImg
     * @return
     */
    public DisplayImageOptions.Builder setImageOnLoading(int loadingImg) {
        builder.showImageOnLoading(loadingImg);
        return builder;
    }

    /**
     * 改变默认加载失败图片
     *
     * @param failImg
     * @return
     */
    public DisplayImageOptions.Builder setImageOnLoadFail(int failImg) {
        builder.showImageOnFail(failImg);
        return builder;
    }

    @Override
    public DisplayImageOptions.Builder getUserDisplayOptionsBuilder() {
        return builder;
    }

    @Override
    public String patternUrl(String url) {
        return url;
    }

}

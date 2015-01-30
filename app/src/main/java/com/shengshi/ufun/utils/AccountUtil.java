package com.shengshi.ufun.utils;

import android.content.Context;

import com.shengshi.base.tools.FileUtils;
import com.shengshi.base.tools.Log;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.bean.UserInfoEntity;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.ui.home.UserMgr;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Title: 账户工具类
 * <p>Description: 包括
 * 1. 保存账户对象信息
 * 2. 删除账户对象信息
 * 3. 判断是否存在该账户信息
 * 4. 读取账户对象信息
 * </p>
 * <p>@author:  dengbw
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-26
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class AccountUtil {

    public static final String ACCOUNT_PREFERENCE = "ufun_accountInfo"; //帐户信息
    public static final String MINEINFO_PREFERENCE = "ufun_mineUserInfo"; //我的用户信息

    /**
     * 保存账户信息
     * 是同步的，量大会卡线程
     *
     * @param context
     * @param account
     */
    public static void saveAccountInfoSync(Context context, UserInfoEntity user) {
        if (context != null && user != null) {
            removeFileAccountInfo(context);
            FileUtils.write(context, ACCOUNT_PREFERENCE, user);
        }
    }

    /**
     * 同步保存帐户信息到内存<br/>
     * 异步保存帐户信息到文件
     *
     * @param context
     * @param user
     */
    public static void saveAccountInfo(final Context context, final UserInfoEntity user) {
        if (user.data != null) {
            UserMgr.getInstance().refreshUserInfoEntity(user);
            UFunTool.saveMobile(context, user.data.mobile);
            UIHelper.setMainRefresh();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    saveAccountInfoSync(context, user);
                }
            });
        }
    }

    /**
     * 删除用户信息
     * 本地 和 内存
     *
     * @param ctx
     */
    public static void removeAccountInfo(Context ctx) {
        UIHelper.setMainRefresh();
        removeMemoryAccountInfo();
        removeFileAccountInfo(ctx);
        UFunTool.saveMobile(ctx, "");
        BaseEncryptInfo.clear();
    }

    /**
     * 删除缓存在内存里的用户信息
     */
    public static void removeMemoryAccountInfo() {
        UserMgr.getInstance().removeUserInfoEntity();
    }

    /**
     * 删除序列化到本地的用户信息文件
     *
     * @param ctx
     */
    public static void removeFileAccountInfo(Context ctx) {
        File accountFile = ctx.getFileStreamPath(ACCOUNT_PREFERENCE);
        if (accountFile != null && accountFile.exists()) {
            accountFile.delete();
        }
        File mineFile = ctx.getFileStreamPath(MINEINFO_PREFERENCE);
        if (mineFile != null && mineFile.exists()) {
            mineFile.delete();
        }
    }

    /**
     * 获取用户信息
     * 先从内存取，如若内存被清除，从序列化文件取出
     *
     * @param context
     * @return
     */
    public static UserInfoEntity readAccountInfo(Context context) {
        UserInfoEntity user = null;
        try {
            user = UserMgr.getInstance().getUserInfoEntity();
            if (user == null || user.data == null) {
                Object obj = FileUtils.read(context, ACCOUNT_PREFERENCE);
                if (obj != null) {
                    user = (UserInfoEntity) obj;
                }
            }
            if (user == null) {
                return new UserInfoEntity();
            }
        } catch (Exception e) {
            Log.e(e.getMessage());
            return new UserInfoEntity();
        }

        return user;
    }

    /**
     * 用户是否存在
     *
     * @param context
     * @param account
     * @return
     */
    public static boolean isAccountExist(Context context) {
        UserInfoEntity user = readAccountInfo(context);
        boolean isExit = false;
        if (user == null || user.data == null) {
            return isExit;
        }
        try {
            if (user.data.uid > 0) {
                isExit = true;
            }
        } catch (Exception e) {
            isExit = false;
        }
        return isExit;
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @return
     */
    public static UserInfoEntity getUserInfoEntity(Context context) {
        UserInfoEntity userInfoEntity = UserMgr.getInstance().getUserInfoEntity();
        if (userInfoEntity == null) {
            userInfoEntity = AccountUtil.readAccountInfo(context);
            UserMgr.getInstance().refreshUserInfoEntity(userInfoEntity);
        }
        return userInfoEntity;
    }

    /**
     * 异步保存我的用户信息到文件
     *
     * @param context
     * @param user
     */
    public static void saveMineUserInfo(final Context context, final UserInfoEntity user) {
        if (user.data != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    File accountFile = context.getFileStreamPath(MINEINFO_PREFERENCE);
                    if (accountFile != null && accountFile.exists()) {
                        accountFile.delete();
                    }
                    FileUtils.write(context, MINEINFO_PREFERENCE, user);
                }
            });
        }
    }

    /**
     * 获取我的用户信息
     *
     * @param context
     * @return
     */
    public static UserInfoEntity getMineUserInfo(Context context) {
        UserInfoEntity user = null;
        try {
            Object obj = FileUtils.read(context, MINEINFO_PREFERENCE);
            if (obj != null) {
                user = (UserInfoEntity) obj;
            }
            if (user == null) {
                return new UserInfoEntity();
            }
        } catch (Exception e) {
            Log.e(e.getMessage());
            return new UserInfoEntity();
        }
        return user;
    }

}

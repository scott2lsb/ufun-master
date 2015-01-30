package com.shengshi.rebate.bean;

import java.io.Serializable;


/**
 * <p>Title:         嵌套在有范客户端时，请保持此类 和  有范同步更新
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-15
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class UserInfoEntity extends BaseEntity {

    private static final long serialVersionUID = 5877681834566831308L;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -4824137520331721979L;
        public int uid; //用户id
        public String username;//用户昵称
        public String mobile;//手机号码
        public String pwd;//密码
        public String token;//token
        public String gid;//组别
        public String level;//级别
        public int gender;//性别
        public String birth;//生日
        public String icon;//头像
        public String signature;//个性签名
        public String cityid;//城市id
        public String regdate;//注册日期
        public String regfrom;//注册来源,local:本地注册，weixin:微信
        public int isnewpm;//是否有新消息 （1有0无）
        public int qnum;//圈子数
        public int tnum;//帖子数
        public int pnum;//回复数
        public int credit1;//积分1
        public int credit2;//积分2
        public int lastpost;//最后回复时间
        public int thisvisit;//最近一次登录时间
        public int attentions;//关注数
        public int fans;//粉丝数
    }
}

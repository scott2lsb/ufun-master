package com.shengshi.ufun.bean.mine;

import com.shengshi.ufun.bean.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class TaPeopleEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5877681834566831308L;
    public Data data;

    public class Data implements Serializable {
        private static final long serialVersionUID = -8730184441076332730L;
        public int is_attention; //是否已关注
        public Base base; //基本信息
        public List<Quans> quans;//加入的圈子
        public int count;
        public List<Threads> threads;//TA话题
    }

    public class Base implements Serializable {
        private static final long serialVersionUID = -1984227915923259361L;
        public int uid; //用户id
        public String username;//用户昵称
        public String level;//级别
        public String signature;//个性签名
        public String icon;//头像
        public int attentions;//关注数
        public int fans;//粉丝数
        public int credit1;//积分1
        public int credit2;//积分2
        public int gender; //性别
        public String birth;//生日
    }

    public class Quans implements Serializable {
        private static final long serialVersionUID = 5497654306757038212L;
        public int id;
        public int qid;
        public String qname;
        public String icon;
    }

    public class Threads implements Serializable {
        private static final long serialVersionUID = -2139604540191479259L;
        public int tid;
        public String title;
        public String uname;
        public String time;
        public int hasphoto;
        public int scan_count;
        public int reply_count;
    }
}

package com.shengshi.rebate.bean.detail;

import com.google.gson.annotations.SerializedName;
import com.shengshi.rebate.bean.BaseEntity;

import java.io.Serializable;
import java.util.List;

public class CommentEntity extends BaseEntity {

    private static final long serialVersionUID = 8281365032796587492L;

    public Comment data;

    public class Comment implements Serializable {
        private static final long serialVersionUID = 2845917562296054034L;
        public List<CommentInfo> data;
        public int count;
        public String status;
    }


    public class CommentInfo implements Serializable {

        private static final long serialVersionUID = 2845917562296054034L;

        @SerializedName("c_time")
        public String date;
        public String content;
        public String id;

        @SerializedName("is_anonymous")
        public String isAnonymous;

        public String star;

        @SerializedName("user_id")
        public String userId;

        @SerializedName("username")
        public String userName;
    }

}

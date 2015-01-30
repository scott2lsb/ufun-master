package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.StatusEntity;
import com.shengshi.ufun.bean.circle.SpecialroleEntity;
import com.shengshi.ufun.bean.circle.SpecialroleEntity.Data;
import com.shengshi.ufun.bean.circle.SpecialroleEntity.Photo;
import com.shengshi.ufun.bean.circle.SpecialroleEntity.User;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SpecialroleAdapter extends SimpleBaseAdapter<SpecialroleEntity> {

    SpecialroleEntity mData;
    ImageLoader loader;
    List<User> userlist;
    int is_attention;
    int clickIndex;
    Context mContext;

    public SpecialroleAdapter(Context context, SpecialroleEntity entity) {
        super(context);
        this.mData = entity;
        loader = ImageLoader.getInstance(context);
        this.mContext = context;
        userlist = new ArrayList<SpecialroleEntity.User>();
        userlist.clear();
        if (entity != null && entity.data.list != null) {
            userlist = entity.data.list;
        }

    }

    /**
     * 返回用户条数
     *
     * @return
     */
    public int getItemCount() {
        int userSize = 0;
        if (mData != null && mData.data != null) {
            SpecialroleEntity info = mData;
            if (info != null && info.data.list != null) {
                userSize = info.data.list.size();
            }

        }

        return userSize;
    }

    @Override
    public int getCount() {
        int usercount = 0;
        if (mData != null && mData.data != null) {
            Data info = mData.data;
            if (info != null && info.list != null) {
                usercount = info.list.size();
                return usercount;
            }

        }
        return usercount;
    }

    @Override
    public int getItemResource(int position) {
        // TODO Auto-generated method stub
        return R.layout.specialrole_listitem;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        int index = position;
        if (mData == null) {
            return convertView;
        }

        try {
            handleTodayItem(holder, index, userlist);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return convertView;
    }

    private void handleTodayItem(ViewHolder holder, int index, List<User> users)
            throws Exception {
        final int mIndex = index;
        final User user = users.get(index);
        CircleImageView avatar = holder.getView(R.id.specialrole_listitem_iv);
        loader.displayImage(user.avatar, avatar, true);
        loader.setImageOnLoading(R.drawable.avatar);
        loader.setImageOnLoadFail(R.drawable.avatar);
        TextView nameTv = holder.getView(R.id.specialrole_item_title);
        nameTv.setText(user.username);
        TextView titleTv = holder.getView(R.id.specialrole_item_subtitle);
        titleTv.setText(user.signature);
        LinearLayout specialrole_item_imglv = holder
                .getView(R.id.specialrole_item_imglv);
        ImageView ifplusIv = holder.getView(R.id.specialrole_item_ifjoin);
        Button cancleBtn = holder.getView(R.id.specialroleitem_join_canclebtn);
        if (user.isfriend == 1) {
            ifplusIv.setVisibility(View.GONE);
            cancleBtn.setVisibility(View.VISIBLE);
        } else if (user.isfriend == 0) {
            ifplusIv.setVisibility(View.VISIBLE);
            cancleBtn.setVisibility(View.GONE);
        } else {
            cancleBtn.setVisibility(View.VISIBLE);
            cancleBtn.setVisibility(View.GONE);
        }
        final List<Photo> imgs = user.imgs;
        ImageView img1 = holder.getView(R.id.specialrole_item_img1);
        ImageView img2 = holder.getView(R.id.specialrole_item_img2);
        ImageView img3 = holder.getView(R.id.specialrole_item_img3);

        if (imgs != null && imgs.size() > 0) {
            specialrole_item_imglv.setVisibility(View.VISIBLE);
            loader.displayImage(imgs.get(0).pic, img1, true);
            loader.displayImage(imgs.get(1).pic, img2, true);
            loader.displayImage(imgs.get(2).pic, img3, true);
        } else {
            specialrole_item_imglv.setVisibility(View.GONE);
        }
        ifplusIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 跳到详情页
                requestAttentionUrl(mIndex, user.isfriend, user);
            }
        });
        cancleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestAttentionUrl(mIndex, user.isfriend, user);

            }
        });

    }

    /**
     * 关注/取消用户
     */
    private void requestAttentionUrl(int index, int if_attention, User bangdan) {
        is_attention = if_attention;
        clickIndex = index;
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(mContext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        if (if_attention == 1) {
            encryptInfo.setCallback("user.cancel_attention");
//			mContext.showTipDialog("正在关注，请稍候...");
        } else if (if_attention == 0) {
            encryptInfo.setCallback("user.pay_attention");
        }
        encryptInfo.resetParams();
        encryptInfo.putParam("touid", bangdan.uid);
        request.setCallback(jsonAttentionCallback);
        request.execute();
    }

    JsonCallback<StatusEntity> jsonAttentionCallback = new JsonCallback<StatusEntity>() {

        @Override
        public void onSuccess(StatusEntity result) {
            if (result != null) {
                Toast.makeText(context, result.errMessage, 1000).show();
                if (result.errCode == 0) {
                    if (is_attention == 0) {
                        mData.data.list.get(clickIndex).isfriend = 1;
                    } else if (is_attention == 1) {
                        mData.data.list.get(clickIndex).isfriend = 0;
                    }
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
            Toast.makeText(context, exception.getMessage(), 1000).show();
        }
    };


}

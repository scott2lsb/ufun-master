package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
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
import com.shengshi.ufun.bean.circle.ActivityCircleListEntity;
import com.shengshi.ufun.bean.circle.ActivityCircleListEntity.Bangdan;
import com.shengshi.ufun.bean.circle.ActivityCircleListEntity.Data;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.List;

public class ActivityCircleAdapter extends
        SimpleBaseAdapter<ActivityCircleListEntity> {

    ActivityCircleListEntity mData;
    ImageLoader loader;
    Context mContext;
    int is_attention;
    int clickIndex;

    public ActivityCircleAdapter(Context context,
                                 ActivityCircleListEntity entity) {
        super(context);
        this.mData = entity;
        loader = ImageLoader.getInstance(context);
        this.mContext = context;
    }

    public void addData(ActivityCircleListEntity entity) {
        Data info = entity.data;
        if (info != null && info.list != null) {
            List<Bangdan> bangdanlist = info.list;
            if (bangdanlist != null && bangdanlist.size() > 0) {
                this.mData.data.list.addAll(bangdanlist);
            }
        }
    }


    public int getItemCount() {
        int circleSize = 0;
        if (mData != null && mData.data != null) {
            ActivityCircleListEntity info = mData;
            if (info != null && info.data.list != null) {
                circleSize = info.data.list.size();
            }

        }
        return circleSize;
    }

    @Override
    public int getCount() {
        int circlecount = 0;
        if (mData != null && mData.data != null) {
            Data info = mData.data;
            if (info != null && info.list != null) {
                circlecount = info.list.size();
                return circlecount;
            }
            if (info != null && info.list != null) {
                circlecount = circlecount + info.list.size();
                return circlecount;
            }
        }
        return circlecount;
    }

    @Override
    public int getItemResource(int position) {
        // TODO Auto-generated method stub
        return R.layout.activitycircle_listitem;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        int index = position;
        if (mData == null) {
            return convertView;
        }
        List<Bangdan> circles = mData.data.list;
        try {
            handleTodayItem(holder, index, circles);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return convertView;
    }

    private void handleTodayItem(ViewHolder holder, int index,
                                 List<Bangdan> bangdans) throws Exception {
        final Bangdan bangdan = bangdans.get(index);
        final int mIndex = index;
        TextView rankTv = holder.getView(R.id.activitycircle_listitem_rankingtv);
        rankTv.setText(String.valueOf(bangdan.rank));
        if (index < 3) {
            rankTv.setTypeface(Typeface.MONOSPACE, Typeface.BOLD_ITALIC);
        } else {
            rankTv.getPaint().setFakeBoldText(false);
        }
        TextView nameTv = holder.getView(R.id.activitycircle_listitem_nametv);
        nameTv.setText(bangdan.username);
        TextView levelTv = holder.getView(R.id.activitycircle_listitem_leveltv);
        if (bangdan.rank_title != null && !bangdan.rank_title.equals("")) {
            levelTv.setText(bangdan.rank_title);
        } else {
            levelTv.setVisibility(View.GONE);
        }
        TextView scoreTv = holder.getView(R.id.activitycircle_listitem_scoretv);
        scoreTv.setText("积分：" + bangdan.credit);
//		ImageView ifplusIv = holder.getView(R.id.activitycircle_listitem_joiniv);
        TextView cancleBtn = holder.getView(R.id.secondlistitem_join_canclebtn);
        CircleImageView avatar = holder.getView(R.id.activitycircle_listitem_rankingtv_iv);
        if (bangdan.isfriend == 1) {
//			ifplusIv.setVisibility(View.GONE);
            cancleBtn.setVisibility(View.VISIBLE);
            cancleBtn.setBackgroundResource(R.drawable.btn_gray);
            cancleBtn.setTextColor(mContext.getResources().getColor(R.color.gray_text_selector));
            cancleBtn.setText("取消关注");
        } else if (bangdan.isfriend == 0) {
//			ifplusIv.setVisibility(View.VISIBLE);
            cancleBtn.setVisibility(View.VISIBLE);
            cancleBtn.setBackgroundResource(R.drawable.btn_blue);
            cancleBtn.setVisibility(View.VISIBLE);
            cancleBtn.setTextColor(mContext.getResources().getColor(R.color.white));
            cancleBtn.setText("关注");
        } else {
//			ifplusIv.setVisibility(View.GONE);
            cancleBtn.setVisibility(View.GONE);
        }

        loader.displayImage(bangdan.avatar, avatar, true);
        loader.setImageOnLoadFail(R.drawable.avatar);

        cancleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestAttentionUrl(mIndex, bangdan.isfriend, bangdan);

            }
        });

    }

    /**
     * 关注/取消用户
     */
    private void requestAttentionUrl(int index, int if_attention, Bangdan bangdan) {
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
                if (result.errCode == 0) {
                    Toast.makeText(context, result.errMessage, 1000).show();
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

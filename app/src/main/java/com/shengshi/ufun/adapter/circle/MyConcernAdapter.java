package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.circle.MyConcernEntity;
import com.shengshi.ufun.bean.circle.MyConcernEntity.Concern;
import com.shengshi.ufun.bean.circle.MyConcernEntity.Data;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.List;

public class MyConcernAdapter extends SimpleBaseAdapter<MyConcernEntity> {


    MyConcernEntity mData;
    ImageLoader loader;

    public MyConcernAdapter(Context context, MyConcernEntity entity) {
        super(context);
        this.mData = entity;
        loader = ImageLoader.getInstance(context);
    }

    public void addData(MyConcernEntity entity) {
        Data info = entity.info;
        if (entity.info != null && info.result != null) {
            List<Concern> hotCircle = info.result;
            if (hotCircle != null && hotCircle.size() > 0) {
                this.mData.info.result.addAll(hotCircle);
            }
        }
    }

    public int getItemCount() {
        int joincircleSize = 0;
        if (mData != null && mData.info != null) {
            MyConcernEntity info = mData;
            if (info != null && info.info.result != null) {

                joincircleSize = info.info.result.size();
            }
        }
        return joincircleSize;
    }

    @Override
    public int getCount() {
        int circlecount = 0;
        if (mData != null && mData.info != null) {
            Data info = mData.info;
            if (info != null && info.result != null) {
                circlecount = info.result.size();
                return circlecount;
            }
        }
        return circlecount;
    }

    @Override
    public int getItemResource(int position) {
        // TODO Auto-generated method stub
        return R.layout.addcircle_second_listitem;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        int index = position;
        if (mData == null) {
            return convertView;
        }
        List<Concern> circles = mData.info.result;
        try {
            handleTodayItem(holder, index, circles);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return convertView;
    }

    private void handleTodayItem(ViewHolder holder, int index, List<Concern> circles)
            throws Exception {
        final Concern concern = circles.get(index);
        TextView nameTv = holder.getView(R.id.secondlistitem_name_tv);
        nameTv.setText(concern.name);
        TextView titleTv = holder.getView(R.id.secondlistitem_subtitle_tv);
        titleTv.setText(concern.descrip);
        ImageView avatar = holder.getView(R.id.secondlistitem_iv);
        loader.displayImage(concern.img, avatar, true);
        ImageView ifplusIv = holder.getView(R.id.secondlistitem_join_iv);
        if (concern.ifconcern == 1) {
            ifplusIv.setVisibility(View.GONE);
        } else {
            ifplusIv.setVisibility(View.VISIBLE);
        }
        ifplusIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 跳到详情页

            }
        });

    }

}

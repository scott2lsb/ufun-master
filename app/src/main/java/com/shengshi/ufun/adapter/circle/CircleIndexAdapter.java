package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.circle.CircleIndexEntity;
import com.shengshi.ufun.bean.circle.CircleIndexEntity.Circle;
import com.shengshi.ufun.bean.circle.CircleIndexEntity.Data;
import com.shengshi.ufun.bean.circle.CircleIndexEntity.Photo;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.ui.circle.CircleintroduceActivity;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class CircleIndexAdapter extends SimpleBaseAdapter<CircleIndexEntity> {

    CircleIndexEntity mAdapterData;
    ImageLoader loader;
    public int firsthotPosition = -1;
    List<Circle> circlelist;
    Context mcontext;

    public CircleIndexAdapter(Context context, CircleIndexEntity entity) {
        super(context);
        this.mAdapterData = entity;
        loader = ImageLoader.getInstance(context);
        circlelist = new ArrayList<CircleIndexEntity.Circle>();
        circlelist.clear();
        this.mcontext = context;
        if (entity != null && entity.data.join != null) {
            circlelist = entity.data.join;
        }
        if (entity != null && entity.data.hot != null) {

            if (entity.data.join.size() > 0) {
                firsthotPosition = entity.data.join.size();
            } else {
                firsthotPosition = 0;
            }
        } else {
            firsthotPosition = -1;
        }

    }

    /**
     * 返回圈子条数
     *
     * @return
     */
    public int getItemCount() {
        int joincircleSize = 0;
        if (mAdapterData != null && mAdapterData.data != null) {
            CircleIndexEntity info = mAdapterData;
            if (info != null && info.data.join != null) {
                joincircleSize = info.data.join.size();
            }
            if (info != null && info.data.hot != null) {
                joincircleSize = joincircleSize + info.data.hot.size();
            }
        }

        return joincircleSize;
    }

    @Override
    public int getCount() {
        int circlecount = 0;
        if (mAdapterData != null && mAdapterData.data != null) {
            Data info = mAdapterData.data;
            if (info != null && info.join != null) {
                circlecount = info.join.size();
                return circlecount;
            }
            if (info != null && info.hot != null) {
                circlecount = circlecount + info.hot.size();
                return circlecount;
            }
        }
        return circlecount;
    }

    @Override
    public int getItemResource(int position) {
        // TODO Auto-generated method stub
        return R.layout.circleindex_listitem;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        int index = position;
        if (mAdapterData == null) {
            return convertView;
        }

        try {
            handleTodayItem(holder, index, circlelist);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return convertView;
    }

    private void handleTodayItem(ViewHolder holder, int index,
                                 List<Circle> circles) throws Exception {
        LinearLayout hottipsLv = holder.getView(R.id.circleindex_item_hottips);

        if (firsthotPosition != -1) {
            if (firsthotPosition == index) {
                hottipsLv.setVisibility(View.VISIBLE);
            } else {
                hottipsLv.setVisibility(View.GONE);
            }
        } else {
            hottipsLv.setVisibility(View.GONE);
        }

        final Circle circle = circles.get(index);
        final int p = index;

        TextView nameTv = holder.getView(R.id.circleindex_item_title);
        nameTv.setText(circle.qname);
        TextView titleTv = holder.getView(R.id.circleindex_item_subtitle);
        titleTv.setText(circle.descrip);
        ImageView ifplusIv = holder.getView(R.id.circleindex_item_ifjoin);
        if (circle.ifjoin == 1 || circle.ifjoin == 0) {
            ifplusIv.setVisibility(View.GONE);
        } else {
            ifplusIv.setVisibility(View.VISIBLE);
        }
        LinearLayout circleindex_item_imglin = holder
                .getView(R.id.circleindex_item_imglin);
        final List<Photo> imgs = circle.imgs;
        final ImageView img1 = holder.getView(R.id.circleindex_item_img1);

        LayoutParams para;
        para = circleindex_item_imglin.getLayoutParams();
        int screenWidth = StringUtils.getDisplayMetrics(context, 0);
        int picdipwidth = StringUtils.dip2px(context, 71);
        para.height = (screenWidth - picdipwidth) / 4;
        circleindex_item_imglin.setLayoutParams(para);
        final LinearLayout circleindex_item_imglin1 = holder
                .getView(R.id.circleindex_item_imglin1);
        final TextView circleindex_item_img_title1 = holder
                .getView(R.id.circleindex_item_img_title1);
        final TextView circleindex_item_img_descrip1 = holder
                .getView(R.id.circleindex_item_img_descrip1);

        final LinearLayout circleindex_item_imglin2 = holder
                .getView(R.id.circleindex_item_imglin2);
        final TextView circleindex_item_img_title2 = holder
                .getView(R.id.circleindex_item_img_title2);
        final TextView circleindex_item_img_descrip2 = holder
                .getView(R.id.circleindex_item_img_descrip2);

        final LinearLayout circleindex_item_imglin3 = holder
                .getView(R.id.circleindex_item_imglin3);
        final TextView circleindex_item_img_title3 = holder
                .getView(R.id.circleindex_item_img_title3);
        final TextView circleindex_item_img_descrip3 = holder
                .getView(R.id.circleindex_item_img_descrip3);

        LinearLayout circleindex_item_imglin4 = holder
                .getView(R.id.circleindex_item_imglin4);
        TextView circleindex_item_img_title4 = holder
                .getView(R.id.circleindex_item_img_title4);
        TextView circleindex_item_img_descrip4 = holder
                .getView(R.id.circleindex_item_img_descrip4);

        ImageView img2 = holder.getView(R.id.circleindex_item_img2);

        ImageView img3 = holder.getView(R.id.circleindex_item_img3);

        ImageView img4 = holder.getView(R.id.circleindex_item_img4);
        if (imgs != null) {
            initItemPic(imgs, 0, circleindex_item_imglin1, img1,
                    circleindex_item_img_title1, circleindex_item_img_descrip1);
            initItemPic(imgs, 1, circleindex_item_imglin2, img2,
                    circleindex_item_img_title2, circleindex_item_img_descrip2);
            initItemPic(imgs, 2, circleindex_item_imglin3, img3,
                    circleindex_item_img_title3, circleindex_item_img_descrip3);
            initItemPic(imgs, 3, circleindex_item_imglin4, img4,
                    circleindex_item_img_title4, circleindex_item_img_descrip4);
        }
        ifplusIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext,
                        CircleintroduceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.putExtra("qid", circlelist.get(p).qid);
                mcontext.startActivity(intent);
            }
        });

    }

    private void initItemPic(List<Photo> imgs, int index,
                             LinearLayout circleindex_item_imglin, ImageView img,
                             TextView titleTv, TextView descripTv) {

        if (imgs.get(index).ifpic == 1) {
            circleindex_item_imglin.setVisibility(View.GONE);
            img.setVisibility(View.VISIBLE);
            loader.displayImage(imgs.get(index).pic, img, true);
            loader.setImageOnLoading(R.color.login_line);
        } else {
            circleindex_item_imglin.setVisibility(View.VISIBLE);
            img.setVisibility(View.GONE);
            titleTv.setText(imgs.get(index).title);
            descripTv.setText(imgs.get(index).descrip);
        }

    }
}

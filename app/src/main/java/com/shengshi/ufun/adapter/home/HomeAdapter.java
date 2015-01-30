package com.shengshi.ufun.adapter.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.home.HomeEntity;
import com.shengshi.ufun.bean.home.HomeEntity.Data;
import com.shengshi.ufun.bean.home.HomeEntity.Threads;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.List;

public class HomeAdapter extends SimpleBaseAdapter<HomeEntity> {

    HomeEntity mData;
    ImageLoader loader;

    public HomeAdapter(Context context, HomeEntity entity) {
        super(context);
        this.mData = entity;
        loader = ImageLoader.getInstance(context);

    }

    public void addData(HomeEntity entity) {
        Data info = entity.data;
        if (info != null && info.list != null) {
            List<Threads> threads = info.list;
            if (threads != null && threads.size() > 0) {
                this.mData.data.list.addAll(threads);
            }
        }
    }

    /**
     * 返回所有的条数
     *
     * @return
     */
    public int getItemCount() {
        int joincircleSize = 0;
        if (mData != null && mData.data != null) {
            HomeEntity info = mData;
            if (info != null && info.data.list != null) {
                joincircleSize = info.data.list.size();
            }

        }
        return joincircleSize;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mData != null && mData.data != null) {
            Data info = mData.data;
            if (info != null && info.list != null) {
                count = info.list.size();
                return count;
            }

        }
        return count;
    }

    @Override
    public int getItemResource(int position) {
        // TODO Auto-generated method stub
        return R.layout.activityhome_listitem;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        int index = position;
        if (mData == null) {
            return convertView;
        }
        List<Threads> threads = mData.data.list;
        try {
            handleTodayItem(holder, index, threads);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }

        return convertView;
    }

    private void handleTodayItem(ViewHolder holder, int index,
                                 List<Threads> threads) throws Exception {

        final Threads thread = threads.get(index);
        ImageView avatariv = holder.getView(R.id.homelistitem_avatar_iv);
        loader.displayImage(thread.avatar, avatariv, true);
        TextView nameTv = holder.getView(R.id.homelistitem_name);
        nameTv.setText(thread.uname);

        TextView countTv = holder.getView(R.id.homelistitem_replycount);
        countTv.setText(thread.reply_count + "/" + thread.scan_count);

        TextView timeTv = holder.getView(R.id.homelistitem_time);
        timeTv.setText(thread.time);
        if (thread.flag != null && !thread.flag.equals("")) {
            TextView flagTv = holder.getView(R.id.homelistitem_flag);
            flagTv.setText(thread.flag);
        }
        TextView titleTv = holder.getView(R.id.homelistitem_title);
        titleTv.setText(thread.title);
        LinearLayout hasimg_lin = holder.getView(R.id.homelistitem_hasimg_lin);
        LinearLayout hasimg_onlyonelin = holder
                .getView(R.id.homelistitem_onlyoneimg_lin);
        LinearLayout homelistitem_hastwoimg_lin = holder
                .getView(R.id.homelistitem_hastwoimg_lin);

        ImageView[] hasimg_ivs = new ImageView[3];
        int[] imageViewIds = new int[]{R.id.homelistitem_hasimg_oneiv,
                R.id.homelistitem_hasimg_twoiv,
                R.id.homelistitem_hasimg_threeiv};
        LayoutParams para;
        para = hasimg_lin.getLayoutParams();
        int screenWidth = StringUtils.getDisplayMetrics(context, 0);
        int picdipwidth = StringUtils.dip2px(context, 40);
        para.height = (screenWidth - picdipwidth) / 3;
        hasimg_lin.setLayoutParams(para);
        hasimg_onlyonelin.setLayoutParams(para);
        homelistitem_hastwoimg_lin.setLayoutParams(para);
//		for (int i = 0; i < 3; i++) {
//			hasimg_ivs[i] = (ImageView) holder.getView(imageViewIds[i]);
//		}
        ImageView onlyoneimg_iv = holder
                .getView(R.id.homelistitem_onlyoneimg_iv);
        ImageView hastwoimg_oneiv = holder
                .getView(R.id.homelistitem_hastwoimg_oneiv);
        ImageView hastwoimg_twoiv = holder
                .getView(R.id.homelistitem_hastwoimg_twoiv);
        if (thread.imgs != null && thread.imgs.size() > 0) {
            if (thread.imgs.size() == 1) {
                hasimg_lin.setVisibility(View.GONE);
                homelistitem_hastwoimg_lin.setVisibility(View.GONE);
                hasimg_onlyonelin.setVisibility(View.VISIBLE);
                onlyoneimg_iv.setVisibility(View.VISIBLE);
                loader.displayImage(thread.imgs.get(0).pic, onlyoneimg_iv, true);
//				loader.setImageOnLoading(R.color.login_line);
            } else {
                if (thread.imgs.size() == 2) {
                    hasimg_lin.setVisibility(View.GONE);
                    hasimg_onlyonelin.setVisibility(View.GONE);
                    homelistitem_hastwoimg_lin.setVisibility(View.VISIBLE);

                    loader.displayImage(thread.imgs.get(0).pic,
                            hastwoimg_oneiv, true);
                    loader.displayImage(thread.imgs.get(1).pic,
                            hastwoimg_twoiv, true);
                    loader.setImageOnLoading(R.color.login_line);
//					}
                } else {
                    for (int i = 0; i < 3; i++) {
                        hasimg_lin.setVisibility(View.VISIBLE);
                        hasimg_onlyonelin.setVisibility(View.GONE);
                        homelistitem_hastwoimg_lin.setVisibility(View.GONE);
                        hasimg_ivs[i] = (ImageView) holder.getView(imageViewIds[i]);
                        hasimg_ivs[i].setVisibility(View.VISIBLE);
                        loader.displayImage(thread.imgs.get(i).pic,
                                hasimg_ivs[i], true);
                        loader.setImageOnLoading(R.color.login_line);
                    }
                }

            }

        } else {
            hasimg_lin.setVisibility(View.GONE);
            hasimg_onlyonelin.setVisibility(View.GONE);
            homelistitem_hastwoimg_lin.setVisibility(View.GONE);
        }

    }

}

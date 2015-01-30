package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.BaseEntity;
import com.shengshi.ufun.bean.circle.PicCircleTuiListEntity;
import com.shengshi.ufun.bean.circle.PicCircleTuiListEntity.Data;
import com.shengshi.ufun.bean.circle.PicCircleTuiListEntity.Tui;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.List;

public class PicCircleAdapter extends SimpleBaseAdapter<PicCircleTuiListEntity> {

    PicCircleTuiListEntity mData;
    ImageLoader loader;
    Context mcontext;
    int ifhaszan;
    int mposition;
    ImageView zanImageView;

    public PicCircleAdapter(Context context, PicCircleTuiListEntity entity) {
        super(context);
        this.mData = entity;
        loader = ImageLoader.getInstance(context);
        this.mcontext = context;

    }

    public void addData(PicCircleTuiListEntity entity) {
        Data info = entity.data;
        if (info != null && info.list != null) {
            List<Tui> tuis = info.list;
            if (tuis != null && tuis.size() > 0) {
                this.mData.data.list.addAll(tuis);
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
            PicCircleTuiListEntity info = mData;
            if (info != null && info.data.list != null) {
                joincircleSize = info.data.list.size();
            }

        }
        return joincircleSize;
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

        }
        return circlecount;
    }

    @Override
    public int getItemResource(int position) {
        // TODO Auto-generated method stub
        return R.layout.pic_circle_listitem;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        int index = position;
        if (mData == null) {
            return convertView;
        }
        List<Tui> circles = mData.data.list;
        try {
            handleTodayItem(holder, index, circles);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return convertView;
    }

    private void handleTodayItem(ViewHolder holder, int index, List<Tui> tuis)
            throws Exception {

        final Tui pictui = tuis.get(index);
        final int position = index;
        TextView nameTv = holder.getView(R.id.picclircle_titletv);
        nameTv.setText(pictui.title);
        TextView uNameTv = holder.getView(R.id.picclircle_unametv);
        uNameTv.setText(pictui.uname);
        TextView countTv = holder.getView(R.id.picclircle_count);
        countTv.setText(pictui.repley_count + "/" + pictui.scan_count);
        ImageView picIv = holder.getView(R.id.picclircle_piciv);
        int picheight = Integer.parseInt(new java.text.DecimalFormat("0")
                .format(pictui.pic_height));
        int picwidth = Integer.parseInt(new java.text.DecimalFormat("0")
                .format(pictui.pic_width));
        int picdipheight = StringUtils.px2dip(context, picheight);
        int picdipwidth = StringUtils.px2dip(context, picwidth);
        int screenWidth = StringUtils.getDisplayMetrics(context, 0);
        int picIvHeight = ((screenWidth - 21) / 2 * picdipheight) / picdipwidth;
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                picIvHeight);
        picIv.setLayoutParams(params);
        loader.displayImage(pictui.pic, picIv, false);
//		loader.setImageOnLoading(R.color.login_line);
        final ImageView zanbtn = holder.getView(R.id.picclircle_zanbtn);
        if (pictui.ifzan == 1) {
            zanbtn.setBackgroundResource(R.drawable.icon_has_praised);
        } else {
            zanbtn.setBackgroundResource(R.drawable.icon_praise);
        }
        zanbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                requestZanUrl(pictui.ifzan, pictui.tid, position, zanbtn);
            }
        });

    }

    /**
     * 赞、取消赞
     */
    private void requestZanUrl(int ifzan, int tid, int position, ImageView zanView) {
        ifhaszan = ifzan;
        mposition = position;
        zanImageView = zanView;
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(mcontext, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mcontext);
        encryptInfo.setCallback("thread.zan");
        encryptInfo.resetParams();
        encryptInfo.putParam("tid", tid);
        if (ifzan == 0) {
            encryptInfo.putParam("action", 1);
        } else if (ifzan == 1) {
            encryptInfo.putParam("action", 0);
        }
        request.setCallback(jsonAttentionCallback);
        request.execute();
    }

    JsonCallback<BaseEntity> jsonAttentionCallback = new JsonCallback<BaseEntity>() {

        @Override
        public void onSuccess(BaseEntity result) {
            if (result != null) {
//				mcontext.toast(result.errMessage);
                if (result.errCode == 0) {
                    if (ifhaszan == 0) {
                        mData.data.list.get(mposition).ifzan = 1;
                        zanImageView.setBackgroundResource(R.drawable.icon_has_praised);
                    } else if (ifhaszan == 1) {
                        mData.data.list.get(mposition).ifzan = 0;
                        zanImageView.setBackgroundResource(R.drawable.icon_praise);
                    }
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            Log.e("--" + exception.getMessage());
//			toast(exception.getMessage());
//			ToastUtils.showShort(mcontext, "保存成功",200);
        }
    };

}

package com.shengshi.ufun.adapter.circle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.circle.PicCircleTuiListEntity;
import com.shengshi.ufun.bean.circle.PicCircleTuiListEntity.Data;
import com.shengshi.ufun.bean.circle.PicCircleTuiListEntity.Tui;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.List;


public class PuretextCircleAdapter extends SimpleBaseAdapter<PicCircleTuiListEntity> {


    PicCircleTuiListEntity mData;
    ImageLoader loader;
    Context mcontext;
    TextView titleTv;
    Tui tui;

    public enum ViewType {
        TYPE_HASPIC(0), TYPE_NORMAL(1);
        private int index;

        ViewType(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    public PuretextCircleAdapter(Context context, PicCircleTuiListEntity entity) {
        super(context);
        this.mData = entity;
        this.mcontext = context;
        loader = ImageLoader.getInstance(context);
    }

    public void addData(PicCircleTuiListEntity entity) {
        Data info = entity.data;
        if (info != null && info.list != null) {
            List<Tui> lists = info.list;
            if (lists != null && lists.size() > 0) {
                this.mData.data.list.addAll(lists);
            }
        }
    }

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

    /**
     * Tip:保证getItemViewType()返回的值要 小于 getViewTypeCount()
     */
    @Override
    public int getItemViewType(int position) {
        try {
            List<Tui> tuis = mData.data.list;
            if (CheckUtil.isValidate(tuis)) {
                if (position <= tuis.size() - 1)
                    return ViewType.TYPE_HASPIC.getIndex();
                else
                    return ViewType.TYPE_NORMAL.getIndex();
            }
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return ViewType.TYPE_NORMAL.getIndex();
    }

    @Override
    public int getViewTypeCount() {
        return ViewType.values().length;
    }

    @Override
    public int getItemResource(int position) {
        // TODO Auto-generated method stub
        return R.layout.puretext_circle_listitem;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        int index = position;
        if (mData == null) {
            return convertView;
        }
        List<Tui> tuis = mData.data.list;
        try {
            handleTodayItem(holder, index, tuis);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return convertView;
    }

    @SuppressLint("NewApi")
    private void handleTodayItem(ViewHolder holder, int index, List<Tui> tuis)
            throws Exception {
        tui = tuis.get(index);
        titleTv = holder.getView(R.id.puretext_listitem_title);
        titleTv.setText(tui.title);
        TextView unameTv = holder.getView(R.id.puretext_listitem_uname);
        unameTv.setText(tui.uname);
        TextView timeTv = holder.getView(R.id.puretext_listitem_time);
        timeTv.setText(tui.time);
        TextView scancountTv = holder.getView(R.id.puretext_listitem_count);
        scancountTv.setText(String.valueOf(tui.repley_count) + "/" + String.valueOf(tui.scan_count));
        ImageView ivifpic = holder.getView(R.id.puretext_listitem_ifpic);
        if (tui.hasphoto == 0) {
            ivifpic.setVisibility(View.GONE);
        } else {
            ivifpic.setVisibility(View.VISIBLE);
        }

    }


}

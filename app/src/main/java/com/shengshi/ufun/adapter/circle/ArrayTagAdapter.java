package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.GridNoScrollView;
import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.circle.CircleTagEntity;
import com.shengshi.ufun.bean.circle.CircleTagEntity.Data;
import com.shengshi.ufun.bean.circle.CircleTagEntity.Tag;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.List;

public class ArrayTagAdapter extends SimpleBaseAdapter<CircleTagEntity> {

    CircleTagEntity mData;
    ImageLoader loader;
    Context mContext;
    public int[] selectPosition;

    public ArrayTagAdapter(Context context, CircleTagEntity entity, int[] selectp) {
        super(context);
        this.mData = entity;
        this.mContext = context;
        this.selectPosition = selectp;
//		selectPosition = new int[mData.data.result.size()];
//		for (int i = 0; i < mData.data.result.size(); i++) {
//			selectPosition[i] = -1;
//		}
        loader = ImageLoader.getInstance(context);

    }

    /**
     * 返回标签条数
     *
     * @return
     */
    public int getItemCount() {
        int circleSize = 0;
        if (mData != null && mData.data != null) {
            CircleTagEntity info = mData;
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

        }
        return circlecount;
    }

    @Override
    public int getItemResource(int position) {
        // TODO Auto-generated method stub
        return R.layout.tagarray_listsitem;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        int index = position;
        if (mData == null) {
            return convertView;
        }
        List<Tag> tag = mData.data.list;
        // circles.addAll(mData.data.list);
        try {
            handleTodayItem(holder, index, tag);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return convertView;
    }

    private void handleTodayItem(ViewHolder holder, int index,
                                 List<Tag> bangdans) throws Exception {
        final int mindex = index;
        final Tag tag = bangdans.get(index);
        TextView partName = holder.getView(R.id.tagarray_item_partname);
        partName.setText(String.valueOf(tag.partname));
        GridNoScrollView girdview = holder.getView(R.id.tagarray_item_girdview);
        // 添加并且显示

        final SecondTagAdapter tagadapter = new SecondTagAdapter(mContext,
                tag.taglist);
        girdview.setAdapter(tagadapter);
        tagadapter.setSelectedPosition(selectPosition[mindex]);
        girdview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                tagadapter.setSelectedPosition(position);
                setp(mindex, position);
                tagadapter.chiceState(position);
            }
        });

    }

    public void setp(int index, int p) {
        selectPosition[index] = p;
    }
}

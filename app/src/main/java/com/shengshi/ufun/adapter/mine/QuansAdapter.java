package com.shengshi.ufun.adapter.mine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.mine.TaPeopleEntity.Quans;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.List;

public class QuansAdapter extends BaseAdapter {
    private List<Quans> mListData;
    private int mItemCount;
    private LayoutInflater mInflater;
    ImageLoader imageLoader;

    public QuansAdapter(Context context, List<Quans> data) {
        this.mListData = data;
        imageLoader = ImageLoader.getInstance(context);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (null != mListData) {
            mItemCount = mListData.size();
        } else {
            mItemCount = 0;
        }
        return mItemCount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ThreadsViewHolder threadsViewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_ta_people_circle_listitem, null);
            threadsViewHolder = newViewHolder(convertView);
            convertView.setTag(threadsViewHolder);
        } else {
            threadsViewHolder = (ThreadsViewHolder) convertView.getTag();
        }
        bindView(position, threadsViewHolder);
        return convertView;
    }

    private ThreadsViewHolder newViewHolder(final View view) {
        ThreadsViewHolder holder = new ThreadsViewHolder();
        holder.circle_icon = (ImageView) view.findViewById(R.id.circle_icon);
        holder.circle_qname = (TextView) view.findViewById(R.id.circle_qname);
        return holder;

    }

    public final class ThreadsViewHolder {
        public ImageView circle_icon;
        public TextView circle_qname;
    }

    private void bindView(int position, ThreadsViewHolder holder) {
        Quans re = mListData.get(position);
        imageLoader.displayImage(re.icon, holder.circle_icon);
        holder.circle_qname.setText(re.qname);
    }

}
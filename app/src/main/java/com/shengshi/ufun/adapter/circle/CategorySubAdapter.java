package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.circle.AllCircleEntity.SecondCircle;
import com.shengshi.ufun.ui.circle.CircleintroduceActivity;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.List;

public class CategorySubAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    //	String[][] cities;
    List<SecondCircle> option;
    public int foodpoition;
    ImageLoader loader;

    public CategorySubAdapter(Context context, List<SecondCircle> op, int position) {
        this.context = context;
        this.option = op;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.foodpoition = position;
        loader = ImageLoader.getInstance(context);
    }

    @Override
    public int getCount() {
        if (option == null) {
            return 0;
        } else {
            // TODO Auto-generated method stub
            return option.size();
        }
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return getItem(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        final int mposition = position;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.addcircle_second_listitem, null);
            viewHolder = new ViewHolder();
            viewHolder.nameTv = (TextView) convertView
                    .findViewById(R.id.secondlistitem_name_tv);
            viewHolder.subtitleTv = (TextView) convertView
                    .findViewById(R.id.secondlistitem_subtitle_tv);
            viewHolder.circleIv = (CircleImageView) convertView
                    .findViewById(R.id.secondlistitem_iv);
            viewHolder.ifjoinIv = (ImageView) convertView
                    .findViewById(R.id.secondlistitem_join_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nameTv.setText(option.get(position).qname);
        viewHolder.subtitleTv.setText(option.get(position).descrip);
        loader.displayImage(option.get(position).icon, viewHolder.circleIv, true);
        viewHolder.ifjoinIv.setVisibility(View.VISIBLE);
        if (option.get(position).ifjoin == 1) {
            viewHolder.ifjoinIv.setBackgroundResource(R.drawable.icon_have_joined);
        } else {
            viewHolder.ifjoinIv.setBackgroundResource(R.drawable.add);
        }
        viewHolder.ifjoinIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, CircleintroduceActivity.class);
                intent.putExtra("qid", option.get(mposition).qid);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public TextView nameTv;
        public TextView subtitleTv;
        public CircleImageView circleIv;
        public ImageView ifjoinIv;
    }

}

package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.circle.AllCircleEntity.ALLCircle;

import java.util.List;


public class CategoryAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<ALLCircle> category;
    int last_item;
    private int selectedPosition = -1;

    public CategoryAdapter(Context context, List<ALLCircle> ca) {
        this.context = context;
        this.category = ca;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return category.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.goods_selectstyle_listitem, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.goodsselect_name);
            holder.imageView = (ImageView) convertView.findViewById(R.id.goodsselect_sanjiao);
            holder.layout = (LinearLayout) convertView.findViewById(R.id.goodsselect_list_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (selectedPosition == position) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textView.setTextColor(context.getResources().getColor(R.color.headding_text));
        } else {
            holder.imageView.setVisibility(View.INVISIBLE);
            holder.textView.setTextColor(context.getResources().getColor(R.color.sub_heading_text));
        }

        holder.textView.setText(category.get(position).name);
        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public LinearLayout layout;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

}

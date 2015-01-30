package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shengshi.ufun.R;

public class UFunTipOperationAdapter extends BaseAdapter {
    private String[] array;
    private Context context;
    private int selectedPosition = -1;

    public UFunTipOperationAdapter(String[] array, Context context) {
        super();
        this.array = array;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return array.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return array[position];
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
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.spinner_citylist_item, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt = (TextView) convertView.findViewById(R.id.spinner_tv);
        holder.txt.setText(array[position]);
        if (selectedPosition == position) {
            holder.txt.setTextColor(context.getResources().getColor(R.color.sub_heading_text));
        } else {
            holder.txt.setTextColor(context.getResources().getColor(R.color.black));
        }
        return convertView;
    }

    class ViewHolder {
        TextView txt;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetInvalidated();
    }
}
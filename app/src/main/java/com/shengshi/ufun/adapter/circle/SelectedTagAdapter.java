package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.shengshi.ufun.R;
import com.shengshi.ufun.ui.circle.PublishActivity;

import java.util.List;

public class SelectedTagAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<String> category;
    int last_item;
    private int selectedPosition = -1;

    public SelectedTagAdapter(Context context, List<String> ca) {
        this.context = context;
        this.category = ca;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return category.size();
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
        ViewHolder holder = null;
        final int mposition = position;
        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.publish_selectedtag_girdviewitem, null);
            holder = new ViewHolder();
            holder.name = (Button) convertView
                    .findViewById(R.id.tagselecteditem_name);
            holder.select = (Button) convertView
                    .findViewById(R.id.tagselecteditem_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setBackgroundResource(R.drawable.btn_blue);
        holder.select.setVisibility(View.VISIBLE);
        holder.name
                .setTextColor(context.getResources().getColor(R.color.white));
        holder.name.setText(category.get(position).toString());
        holder.select.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String deletename = category.get(mposition).toString();
                int deletep = 0;
                for (int i = 0; i < PublishActivity.selectp.length; i++) {
                    if (PublishActivity.selectp[i] != -1) {
                        Boolean b = PublishActivity.circleTagEntity.data.list
                                .get(i).taglist.get(PublishActivity.selectp[i]).name
                                .equals(deletename);
                        if (b) {
                            deletep = i;
                        }
                    }
                }
                PublishActivity.selectp[deletep] = -1;
                category.remove(mposition);
                notifyDataSetChanged();

            }
        });
        return convertView;
    }

    public static class ViewHolder {
        public Button name;
        public Button select;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        this.notifyDataSetChanged();

    }

}

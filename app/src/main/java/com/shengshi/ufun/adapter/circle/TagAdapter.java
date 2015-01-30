package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.shengshi.ufun.R;
import com.shengshi.ufun.bean.circle.CircleTagEntity.SecondTag;

import java.util.List;


public class TagAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<SecondTag> category;
    private boolean isChice[];
    int last_item;
    private int selectedPosition = -1;


    public TagAdapter(Context context, List<SecondTag> ca) {
        this.context = context;
        this.category = ca;
        isChice = new boolean[ca.size()];
        for (int i = 0; i < ca.size(); i++) {
            isChice[i] = true;
        }
        inflater = LayoutInflater.from(context);
    }

    public TagAdapter(ArrayTagAdapter arrayTagAdapter, List<SecondTag> taglist) {
        this.context = context;
        this.category = taglist;
        isChice = new boolean[taglist.size()];
        for (int i = 0; i < taglist.size(); i++) {
            isChice[i] = true;
        }
        inflater = LayoutInflater.from(context);
        // TODO Auto-generated constructor stub
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
            convertView = inflater.inflate(R.layout.publish_tag_girdviewitem, null);
            holder = new ViewHolder();

            holder.name = (Button) convertView.findViewById(R.id.tagitem_name);
            holder.select = (Button) convertView.findViewById(R.id.tagitem_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


//	    if (isChice[position]== true){
//	    	  holder. name.setBackgroundResource(R.drawable.line_whitbg);
//	    	  holder. select.setVisibility(View.GONE);
//	    	 holder.name.setTextColor(context.getResources().getColor(R.color.black));  
//	    	}else{
//	    		  holder. name.setBackgroundResource(R.drawable.btn_blue);
//		    	  holder. select.setVisibility(View.VISIBLE);
//		    	 holder.name.setTextColor(context.getResources().getColor(R.color.white));   
//	    	}

        if (selectedPosition == position) {
            if (isChice[position] == true) {
                holder.name.setBackgroundResource(R.drawable.line_whitbg);
                holder.select.setVisibility(View.GONE);
                holder.name.setTextColor(context.getResources().getColor(R.color.black));
            } else {
                holder.name.setBackgroundResource(R.drawable.btn_blue);
                holder.select.setVisibility(View.VISIBLE);
                holder.name.setTextColor(context.getResources().getColor(R.color.white));
            }
        } else {
            holder.name.setBackgroundResource(R.drawable.line_whitbg);
            holder.select.setVisibility(View.GONE);
            holder.name.setTextColor(context.getResources().getColor(
                    R.color.black));
        }

        holder.name.setText(category.get(position).name);

        return convertView;
    }

    public static class ViewHolder {
        public Button name;
        public Button select;
    }


    public void chiceState(int post) {
        selectedPosition = post;
        isChice[post] = isChice[post] == true ? false : true;
        this.notifyDataSetChanged();
    }

//	public void setSelectedPosition(int position) {
//		selectedPosition = position;
//		this.notifyDataSetChanged();
//
//	}
}

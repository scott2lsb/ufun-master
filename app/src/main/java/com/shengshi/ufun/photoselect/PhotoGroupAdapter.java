package com.shengshi.ufun.photoselect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.shengshi.ufun.R;
import com.shengshi.ufun.photoselect.NativeImageLoader.NativeImageCallBack;
import com.shengshi.ufun.photoselect.OnMeasureImageView.OnMeasureListener;

import java.util.List;

public class PhotoGroupAdapter extends BaseAdapter {
    private List<ImageBean> list;
    private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
    private GridView mGridView;
    protected LayoutInflater mInflater;

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public PhotoGroupAdapter(Context context, List<ImageBean> list, GridView mGridView) {
        this.list = list;
        this.mGridView = mGridView;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        ImageBean mImageBean = list.get(position);
        String path = mImageBean.getTopImagePath();
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_photo_album_group_item, null);
            viewHolder.mImageView = (OnMeasureImageView) convertView.findViewById(R.id.group_image);
            viewHolder.mTextViewTitle = (TextView) convertView.findViewById(R.id.group_title);
            viewHolder.mTextViewCounts = (TextView) convertView.findViewById(R.id.group_count);

            //用来监听ImageView的宽和高
            viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {

                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImageView.setImageResource(R.drawable.default_album_pictures);
        }

        viewHolder.mTextViewTitle.setText(mImageBean.getFolderName());
        viewHolder.mTextViewCounts.setText(Integer.toString(mImageBean.getImageCounts()));
        //给ImageView设置路径Tag,这是异步加载图片的小技巧
        viewHolder.mImageView.setTag(path);

        //利用NativeImageLoader类加载本地图片
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint,
                new NativeImageCallBack() {

                    @Override
                    public void onImageLoader(Bitmap bitmap, String path) {
                        ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
                        if (bitmap != null && mImageView != null) {
                            mImageView.setImageBitmap(bitmap);
                        }
                    }
                });

        if (bitmap != null) {
            viewHolder.mImageView.setImageBitmap(bitmap);
        } else {
            viewHolder.mImageView.setImageResource(R.drawable.default_album_pictures);
        }

        return convertView;
    }

    public static class ViewHolder {
        public OnMeasureImageView mImageView;
        public TextView mTextViewTitle;
        public TextView mTextViewCounts;
    }

}

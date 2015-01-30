package com.shengshi.ufun.photoselect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.shengshi.ufun.R;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.photoselect.NativeImageLoader.NativeImageCallBack;
import com.shengshi.ufun.photoselect.OnMeasureImageView.OnMeasureListener;
import com.shengshi.ufun.ui.ViewImageActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class PhotoChildAdapter extends BaseAdapter {
    private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
    /**
     * 用来存储图片的选中情况
     */
    private LinkedHashMap<Integer, Boolean> mSelectMap = new LinkedHashMap<Integer, Boolean>();
    private GridView mGridView;
    private List<String> list;
    protected LayoutInflater mInflater;
    private Context mContext;

    private ArrayList<String> have_select_photos_list;//已经选择了多少图片
    boolean isChecked;

    public PhotoChildAdapter(Context context, List<String> list, ArrayList<String> have_select_photos_list, GridView mGridView) {
        this.mContext = context;
        this.list = list;
        this.have_select_photos_list = have_select_photos_list;
        this.mGridView = mGridView;
        mInflater = LayoutInflater.from(context);
        for (int i = 0; i < list.size(); i++) {
            mSelectMap.put(i, false);
        }
    }

    public void updateDate(ArrayList<String> have_select_photos_list) {
        this.have_select_photos_list = have_select_photos_list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final String path = list.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_photo_select_child_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (OnMeasureImageView) convertView.findViewById(R.id.child_image);
            viewHolder.mCheckBox = (TextView) convertView.findViewById(R.id.child_checkbox);

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
        viewHolder.mImageView.setTag(path);

        viewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int size = have_select_photos_list.size();
                if (have_select_photos_list.contains(path)) {//多次相册选择情况
                    isChecked = false;
                } else {
                    isChecked = !mSelectMap.get(position);
                    //判断是否超出最大选择图片张数限制
                    boolean flag = !mSelectMap.containsKey(position) || !mSelectMap.get(position);
                    if (size >= UFunConstants.MAX_CHOICE_PHOTO_NUM && flag) {
                        String tip = String.format(mContext.getString(R.string.max_choice_photo_num_tip), UFunConstants.MAX_CHOICE_PHOTO_NUM);
                        UIHelper.ToastMessage(mContext, tip);
                        viewHolder.mCheckBox.setBackgroundResource(R.drawable.pictures_multiple_choice_icon_unselected);
                        return;
                    }

                    //如果是未选中的CheckBox,则添加动画
                    if (flag) {
                        addAnimation(viewHolder.mCheckBox);
                    }
                }

                mSelectMap.put(position, isChecked);

                if (isChecked) {
                    if (!have_select_photos_list.contains(path)) {
                        have_select_photos_list.add(path);
                    }
                    viewHolder.mCheckBox.setText(size + "");
                    viewHolder.mCheckBox.setBackgroundResource(R.drawable.pictures_multiple_choice_icon_selected);
                } else {
                    if (have_select_photos_list.contains(path)) {
                        have_select_photos_list.remove(path);
                    }
                    viewHolder.mCheckBox.setText("");
                    viewHolder.mCheckBox.setBackgroundResource(R.drawable.pictures_multiple_choice_icon_unselected);
                }

                if (mContext instanceof PhotoImageSelectActivity) {
                    ((PhotoImageSelectActivity) mContext).refreshChoiceBtnText();
                }

                notifyDataSetChanged();
            }
        });

        if (have_select_photos_list.contains(path)) {
            for (int i = 0; i < have_select_photos_list.size(); i++) {
                if (have_select_photos_list.get(i).equals(path)) {
                    viewHolder.mCheckBox.setText((i + 1) + "");
                    viewHolder.mCheckBox.setBackgroundResource(R.drawable.pictures_multiple_choice_icon_selected);
                }
            }
        } else {
            viewHolder.mCheckBox.setText("");
            viewHolder.mCheckBox.setBackgroundResource(R.drawable.pictures_multiple_choice_icon_unselected);
        }

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

        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewImageActivity.class);
                Bundle bundle = new Bundle();
//			List<String>	mUrls = new ArrayList<String>();
//				for (int i = 0; i < mUrls.size(); i++) {
//					mUrls.add("file:///"+list.get(i).toString());
//				}
                bundle.putStringArray("urls", list.toArray(new String[]{}));
                bundle.putInt("index", position);
                bundle.putSerializable("ViewType", ViewType.Select);
                bundle.putStringArrayList("have_select_photos_list", have_select_photos_list);
                intent.putExtras(bundle);
                ((Activity) mContext).startActivityForResult(intent, CommonPhotoSelectActivity.FISHPHOTOLOCAL);
            }
        });

        return convertView;
    }

    /**
     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
     *
     * @param view
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }

    public static class ViewHolder {
        public OnMeasureImageView mImageView;
        public TextView mCheckBox;
    }

}

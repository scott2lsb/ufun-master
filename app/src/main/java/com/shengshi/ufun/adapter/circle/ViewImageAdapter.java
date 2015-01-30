package com.shengshi.ufun.adapter.circle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shengshi.ufun.R;
import com.shengshi.ufun.utils.ImageLoader;

import java.util.List;
import java.util.concurrent.Executors;

import uk.co.senab.photoview.PhotoView;

public class ViewImageAdapter extends PagerAdapter {
    private List<String> data;
    private String mCurrentImgUrl;
    private int currPos;
    private Context ctx;
    private PopupWindow popWin;
    private View parent;
    ImageLoader loader;

    public ViewImageAdapter(Context context, int position, List<String> data) {
        this.ctx = context;
        this.currPos = position;
        this.data = data;
        loader = ImageLoader.getInstance(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public int getCurrPos() {
        return currPos;
    }

    public void setCurrPos(int currPos) {
        this.currPos = currPos;
    }

    @Override
    public Object instantiateItem(View container, int position) {
        PhotoView photoView = new PhotoView(ctx);
        parent = container;
        mCurrentImgUrl = data.get(position);
        if (!mCurrentImgUrl.contains("http://")) {
            mCurrentImgUrl = "file:///" + mCurrentImgUrl;
        }
        loader.displayImage(mCurrentImgUrl, photoView, true);
        photoView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                showContextMenu();
                return true;
            }
        });
        ((ViewPager) container).addView(photoView, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        return photoView;
    }

    protected void showContextMenu() {
        TextView textView = new TextView(ctx);
        textView.setText("保存到图片相册");
        textView.setTextSize(18);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundResource(R.drawable.search_icon);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (popWin != null && popWin.isShowing()) {
                    popWin.dismiss();
                }
                saveImgInThread();
            }

        });
        if (popWin == null) {
            popWin = new PopupWindow(textView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            ColorDrawable cd = new ColorDrawable(ctx.getResources().getColor(R.color.transparent));
            popWin.setBackgroundDrawable(cd);
            popWin.update();
            popWin.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popWin.setTouchable(true); // 设置popupwindow可点击
            popWin.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popWin.setFocusable(true); // 获取焦点
            popWin.setTouchInterceptor(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        popWin.dismiss();
                        return true;
                    }
                    return false;
                }
            });
        }
        if (!popWin.isShowing()) {
            popWin.showAtLocation(parent, Gravity.CENTER, 0, 200);
        } else {
            popWin.dismiss();
        }
    }

    public void saveImgInThread() {
        mCurrentImgUrl = data.get(currPos);
        Executors.newSingleThreadExecutor().submit(new Runnable() {

            @Override
            public void run() {
//				Bitmap bitmap = aq.getCachedImage(mCurrentImgUrl);
//				String filePath = ImageTools.savePhotoToSDCard(bitmap, mCurrentImgUrl);
//				// 刷新相册
//				if (filePath.length() > 0) {
//					ImageTools.scanPhotos(filePath, ctx);
////					ctx.toast( "保存成功");
//				}
            }
        });
    }

//	protected void showSaveImgDlg() {
//		mCurrentImgUrl = data.get(currPos);
//		new AlertDialog.Builder(ctx).setTitle("保存图片").setMessage("是否保存此图片？")
//				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						saveImgInThread();
//					}
//				}).setNegativeButton("取消", null).show();
//	}

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewGroup) container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
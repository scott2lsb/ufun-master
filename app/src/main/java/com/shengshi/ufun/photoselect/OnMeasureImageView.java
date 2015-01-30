package com.shengshi.ufun.photoselect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * <p>Title:      OnMeasureImageView                          </p>
 * <p>Description:    将图片测量的大小回调到onMeasureSize()方法中                      </p>
 * <p>@author:       liaodl                 </p>
 * <p>Copyright: Copyright (c) 2014         </p>
 * <p>Company:    @小鱼网                     </p>
 * <p>Create Time:     2014年7月23日                              </p>
 * <p>@author:                              </p>
 * <p>Update Time:                          </p>
 * <p>Updater:                              </p>
 * <p>Update Comments:                      </p>
 */
public class OnMeasureImageView extends ImageView {
    private OnMeasureListener onMeasureListener;

    public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
        this.onMeasureListener = onMeasureListener;
    }

    public OnMeasureImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnMeasureImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //将图片测量的大小回调到onMeasureSize()方法中
        if (onMeasureListener != null) {
            onMeasureListener.onMeasureSize(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    public interface OnMeasureListener {
        public void onMeasureSize(int width, int height);
    }

}

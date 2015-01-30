package com.shengshi.ufun.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CircleTipLayout extends LinearLayout {
    private onSizeChangedListener listener;

    public void setListener(onSizeChangedListener listener) {
        this.listener = listener;
    }

    public interface onSizeChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (listener != null) {
            listener.onSizeChanged(w, h, oldw, oldh);
            super.onSizeChanged(w, h, oldw, oldh);
        }
    }


    public CircleTipLayout(Context context) {
        super(context);
    }

    public CircleTipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}

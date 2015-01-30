package com.shengshi.base.widget.swipemenulistview;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.TypedValue;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class SwipeLinearLayout extends LinearLayout {

    private GestureDetectorCompat mGestureDetector;
    private OnGestureListener mGestureListener;
    private boolean isFling;
    private int MIN_FLING = dp2px(15);
    private int MAX_VELOCITYX = -dp2px(500);
    private SwipeMenuLayout mTouchView;


    public SwipeMenuLayout getmTouchView() {
        return mTouchView;
    }

    public void setmTouchView(SwipeMenuLayout mTouchView) {
        this.mTouchView = mTouchView;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    public SwipeLinearLayout(Context context, SwipeMenuLayout mTouchView) {
        super(context);
        this.mTouchView = mTouchView;
        init();
    }

    private void init() {
        mGestureListener = new SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                isFling = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                // TODO
                if ((e1.getX() - e2.getX()) > MIN_FLING
                        && velocityX < MAX_VELOCITYX) {
                    isFling = true;
                }
                // Log.i("byz", MAX_VELOCITYX + ", velocityX = " + velocityX);
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        };
        mGestureDetector = new GestureDetectorCompat(getContext(),
                mGestureListener);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}

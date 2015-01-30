package com.shengshi.rebate.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;

import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.swipemenulistview.SwipeLinearLayout;
import com.shengshi.base.widget.swipemenulistview.SwipeMenu;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuCreator;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuView;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.rebate.adapter.MyRebateMainAdapter;

public class MyRebateSwipeListView extends XListView {

    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;

    private int MAX_Y = 5;
    private int MAX_X = 3;
    private float mDownX;
    private float mDownY;
    private int mTouchState;
    private int mTouchPosition;
    private SwipeLinearLayout outterLayout;
    private OnSwipeListener mOnSwipeListener;

    private SwipeMenuCreator mMenuCreator;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpolator;

    public MyRebateSwipeListView(Context context) {
        super(context);
        init();
    }

    public MyRebateSwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyRebateSwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        MAX_X = dp2px(MAX_X);
        MAX_Y = dp2px(MAX_Y);
        mTouchState = TOUCH_STATE_NONE;
        setPullLoadEnable(true);
        setPullRefreshEnable(true);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new MyRebateMainAdapter(getContext(), adapter) {
            @Override
            public void createMenu(SwipeMenu menu) {
                if (mMenuCreator != null) {
                    mMenuCreator.create(menu);
                }
            }

            @Override
            public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
                boolean flag = false;
                if (mOnMenuItemClickListener != null) {
                    flag = mOnMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
                }
                if (outterLayout.getmTouchView() != null && !flag) {
                    outterLayout.getmTouchView().smoothCloseMenu();
                }
            }
        });
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        mCloseInterpolator = interpolator;
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        mOpenInterpolator = interpolator;
    }

    public Interpolator getOpenInterpolator() {
        return mOpenInterpolator;
    }

    public Interpolator getCloseInterpolator() {
        return mCloseInterpolator;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("onInterceptTouchEvent -- ListView");
        boolean flag = super.onInterceptTouchEvent(ev);
        return flag;

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() != MotionEvent.ACTION_DOWN && outterLayout == null)
            return super.onTouchEvent(ev);

        int action = MotionEventCompat.getActionMasked(ev);
        action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i("Down -- ListView");
                int oldPos = mTouchPosition;
                mDownX = ev.getX();
                mDownY = ev.getY();
                mTouchState = TOUCH_STATE_NONE;

                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

                if (mTouchPosition == oldPos && outterLayout != null
                        && outterLayout.getmTouchView().isOpen()) {

                    mTouchState = TOUCH_STATE_X;
                    outterLayout.getmTouchView().onSwipe(ev);
                    return true;
                }

                if (outterLayout != null && outterLayout.getmTouchView().isOpen()) {
                    outterLayout.getmTouchView().smoothCloseMenu();
                    outterLayout = null;
                    return super.onTouchEvent(ev);
                }

                if (view instanceof SwipeLinearLayout) {
                    outterLayout = (SwipeLinearLayout) view;
                }

                if (outterLayout != null && outterLayout.getmTouchView() != null) {
                    outterLayout.getmTouchView().onSwipe(ev);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                Log.i("Move -- ListView");
                float dy = Math.abs((ev.getY() - mDownY));
                float dx = Math.abs((ev.getX() - mDownX));
                if (mTouchState == TOUCH_STATE_X) {
                    if (outterLayout.getmTouchView() != null) {
                        outterLayout.getmTouchView().onSwipe(ev);
                    }
                    getSelector().setState(new int[]{0});
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                } else if (mTouchState == TOUCH_STATE_NONE) {
                    if (Math.abs(dy) > MAX_Y) {
                        mTouchState = TOUCH_STATE_Y;
                    } else if (dx > MAX_X) {
                        mTouchState = TOUCH_STATE_X;
                        if (mOnSwipeListener != null) {
                            mOnSwipeListener.onSwipeStart(mTouchPosition);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i("Up -- ListView");
                if (mTouchState == TOUCH_STATE_X) {
                    if (outterLayout.getmTouchView() != null) {
                        outterLayout.getmTouchView().onSwipe(ev);
                        if (!outterLayout.getmTouchView().isOpen()) {
                            mTouchPosition = -1;
                            outterLayout = null;
                        }
                    }
                    if (mOnSwipeListener != null) {
                        mOnSwipeListener.onSwipeEnd(mTouchPosition);
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

//	public void smoothOpenMenu(int position) {
//		if (position >= getFirstVisiblePosition()
//				&& position <= getLastVisiblePosition()) {
//			View view = getChildAt(position - getFirstVisiblePosition());
//			if (view instanceof SwipeLinearLayout) {
//				mTouchPosition = position;
//				if (outterLayout.getmTouchView() != null && outterLayout.getmTouchView().isOpen()) {
//					outterLayout.getmTouchView().smoothCloseMenu();
//				}
//				outterLayout = (SwipeLinearLayout) view;
//				outterLayout.getmTouchView().smoothOpenMenu();
//			}
//		}
//	}

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext()
                .getResources().getDisplayMetrics());
    }

    public void setMenuCreator(SwipeMenuCreator menuCreator) {
        this.mMenuCreator = menuCreator;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipeListener = onSwipeListener;
    }

}

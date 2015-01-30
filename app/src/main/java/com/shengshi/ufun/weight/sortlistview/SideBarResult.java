package com.shengshi.ufun.weight.sortlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.shengshi.ufun.bean.CityEntity.Result;

import java.util.List;

public class SideBarResult extends View {
    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private int choose = -1;// 选中
    private Paint paint = new Paint();

    private TextView mTextDialog;
    private List<Result> mResult = null;
    private int mSize = 0;

    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public void setLetter(List<Result> mResult) {
        this.mResult = mResult;
        invalidate();
    }

    public SideBarResult(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SideBarResult(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBarResult(Context context) {
        super(context);
    }

    /**
     * 重写这个方法
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mResult != null) {
            // 获取焦点改变背景颜色.
            int height = getHeight();// 获取对应高度
            int width = getWidth(); // 获取对应宽度
            mSize = mResult.size();
            int singleHeight = height / mSize;// 获取每一个字母的高度
            for (int i = 0; i < mSize; i++) {
                //paint.setColor(Color.rgb(33, 65, 98));
                paint.setColor(Color.parseColor("#ffffff"));
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                paint.setAntiAlias(true);
                paint.setTextSize(22);
                // 选中的状态
                if (i == choose) {
                    paint.setColor(Color.parseColor("#68d430"));
                    paint.setFakeBoldText(true);
                }
                // x坐标等于中间-字符串宽度的一半.
                float xPos = width / 2 - paint.measureText(mResult.get(i).firstchar) / 2;
                float yPos = singleHeight * i + singleHeight;
                canvas.drawText(mResult.get(i).firstchar, xPos, yPos, paint);
                paint.reset();// 重置画笔
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * mSize);// 点击y坐标所占总高度的比例*mResult数组的长度就等于点击mResult中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                //setBackgroundResource(R.drawable.btn_gray);
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                //setBackgroundResource(R.drawable.sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < mSize) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(mResult.get(c).firstchar);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(mResult.get(c).firstchar);
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     *
     * @author coder
     */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

}
package com.shengshi.rebate.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * <p>Title:       drawableLeft 和 hint 文本 一起居中
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-15
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class DrawableCenterEditText extends EditText {

    public DrawableCenterEditText(Context context) {
        super(context);
    }

    public DrawableCenterEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableLeft = drawables[0];
            if (drawableLeft != null) {
//				float textWidth = getPaint().measureText(getText().toString());
                float hintTextWidth = getPaint().measureText(getHint().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = 0;
                drawableWidth = drawableLeft.getIntrinsicWidth();
//				float bodyWidth = textWidth + drawableWidth + drawablePadding;
                float bodyWidth = hintTextWidth + drawableWidth + drawablePadding;
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);

    }

}

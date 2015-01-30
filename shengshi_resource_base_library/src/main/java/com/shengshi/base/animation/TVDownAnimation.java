package com.shengshi.base.animation;

import android.graphics.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * <p>Title:           电视关闭特效                                            </p>
 * <p>Description:   类似于老式电视关闭的效果                           </p>
 * <p>@author:       liaodl                 </p>
 * <p>Copyright: Copyright (c) 2014         </p>
 * <p>Company:    @小鱼网                     </p>
 * <p>Create Time:     2014年6月25日                                     </p>
 * <p>@author:                              </p>
 * <p>Update Time:                          </p>
 * <p>Updater:                              </p>
 * <p>Update Comments:                      </p>
 */
public class TVDownAnimation extends Animation {
    private int halfWidth;
    private int halfHeight;

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        setDuration(550);
        setFillAfter(true);
        halfWidth = width / 2;
        halfHeight = height / 2;
        setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final Matrix matrix = t.getMatrix();
        if (interpolatedTime < 0.8) {
            matrix.preScale(1 + 0.625f * interpolatedTime, 1 - interpolatedTime / 0.8f + 0.01f,
                    halfWidth, halfHeight);
        } else {
            matrix.preScale(7.5f * (1 - interpolatedTime), 0.01f, halfWidth, halfHeight);
        }
    }
}

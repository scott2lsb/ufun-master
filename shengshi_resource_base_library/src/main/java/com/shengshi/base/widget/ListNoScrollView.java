package com.shengshi.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * <p>Title:        不滚动的ListView           </p>
 * <p>Description:                          </p>
 * <p>@author:       liaodl                 </p>
 * <p>Copyright: Copyright (c) 2014         </p>
 * <p>Company:    @小鱼网                     </p>
 * <p>Create Time:     2014年6月30日                              </p>
 * <p>@author:                              </p>
 * <p>Update Time:                          </p>
 * <p>Updater:                              </p>
 * <p>Update Comments:                      </p>
 */
public class ListNoScrollView extends ListView {

    public ListNoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListNoScrollView(Context context) {
        super(context);
    }

    public ListNoScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

package com.shengshi.rebate.ui.detail;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.detail.DetailEntity;
import com.shengshi.rebate.ui.base.RebateBaseFragment;

/**
 * <p>Title:        使用提示Fragment
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-5
 * <p>@author:
 * <p>Update Time:   2014-12-31
 * <p>Updater:
 * <p>Update Comments:根据key，value 自动填充
 */
public class DetailTipFragment extends RebateBaseFragment {

    LinearLayout container;
    TextView how_to_used;
    TextView fanli_quota;
    TextView use_condition;

    @Override
    public void initComponents(View view) {
        container = findLinearLayoutById(view, R.id.rebate_use_tip_container);
    }

    public void fetchData(DetailEntity entity) {
        try {
            container.removeAllViews();
            int count = entity.data.useInfo.count;
            for (int i = 0; i < count; i++) {
                TextView textView = new TextView(mContext);
                int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                        getResources().getDisplayMetrics());
                textView.setPadding(padding, padding, padding, padding);
                String key = entity.data.useInfo.rows.get(i).key + ":";
                String value = entity.data.useInfo.rows.get(i).value;
                textView.setTextAppearance(mActivity, R.style.black_title_14sp);
                textView.setText(key + value);
                SpannableStringBuilder builder = setSpannableStringBuilder(textView, key.length());
                textView.setText(builder);

                container.addView(textView);
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    private SpannableStringBuilder setSpannableStringBuilder(TextView textView, int len) {
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText().toString());
        ForegroundColorSpan graySpan = new ForegroundColorSpan(getColor(R.color.dark_gray));
        builder.setSpan(graySpan, 0, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public void initData() {
    }

    @Override
    public int getMainContentViewId() {
        return R.layout.rebate_fragment_detail_tip_layout;
    }

}

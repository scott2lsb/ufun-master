package com.shengshi.rebate.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shengshi.base.tools.DensityUtil;
import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.swipemenulistview.SwipeLinearLayout;
import com.shengshi.base.widget.swipemenulistview.SwipeMenu;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuAdapter;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuLayout;
import com.shengshi.base.widget.swipemenulistview.SwipeMenuView;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.MyRebateEntity.RebateInfo;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.ui.RebatePayCommentActivity;
import com.shengshi.rebate.ui.pay.RebatePayActivity;
import com.shengshi.rebate.widget.MyRebateSwipeListView;

/**
 * <p>Title:      我的返利适配器
 * <p>Description:      注意复用时，界面快速翻页 重复ui 问题
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-4
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class MyRebateMainAdapter extends SwipeMenuAdapter {

    int count;

    public MyRebateMainAdapter(Context context, ListAdapter adapter) {
        super(context, adapter);
        this.count = adapter.getCount();
    }

    static class MainHolder {
        public SparseArray<View> views = new SparseArray<View>();
        public RelativeLayout header;
        public SwipeMenuLayout body;
        public LinearLayout footer;
        public LinearLayout marginLayout;

        public MainHolder() {
            super();
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T getView(int resId, View convertView) {
            View v = views.get(resId);
            if (v == null) {
                v = convertView.findViewById(resId);
                views.put(resId, v);
            }
            return (T) v;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SwipeLinearLayout outterLayout = null;
        MainHolder holder;
        RebateInfo rebate = (RebateInfo) mAdapter.getItem(position);
        if (rebate == null || rebate.orderStatus <= -1) {
            return convertView;
        }
        int state = rebate.orderStatus;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            holder = new MainHolder();

            SwipeMenu menu = new SwipeMenu(mContext);
            menu.setViewType(mAdapter.getItemViewType(position));
            createMenu(menu);
            SwipeMenuView menuView = new SwipeMenuView(menu);
            menuView.setOnSwipeItemClickListener(this);
            MyRebateSwipeListView listView = (MyRebateSwipeListView) parent;

            holder.header = (RelativeLayout) View.inflate(mContext,
                    R.layout.rebate_listview_item_header_my_rebate, null);

            holder.body = new SwipeMenuLayout(contentView, menuView,
                    listView.getCloseInterpolator(), listView.getOpenInterpolator());
            holder.body.setPosition(position);

            holder.footer = (LinearLayout) View.inflate(mContext,
                    R.layout.rebate_listview_item_footer_my_rebate, null);

            holder.marginLayout = new LinearLayout(mContext);
            holder.marginLayout.setLayoutParams(new LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(mContext, 20)));
            holder.marginLayout.setBackgroundColor(mContext.getResources().getColor(
                    R.color.light_gray));

            outterLayout = new SwipeLinearLayout(mContext, holder.body);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            outterLayout.setLayoutParams(params);
            outterLayout.setOrientation(LinearLayout.VERTICAL);
            outterLayout.addView(holder.header);
            outterLayout.addView(holder.body);
            outterLayout.addView(holder.footer);
            if (position != count - 1) {
                outterLayout.addView(holder.marginLayout);
            }
            outterLayout.setTag(holder);
        } else {
            holder = (MainHolder) convertView.getTag();
            holder.body.setPosition(position);
            holder.body.closeMenu();
            outterLayout = (SwipeLinearLayout) convertView;
        }

        View footer_btn_layout = holder.footer.findViewById(R.id.myrebate_footer_btn_layout);
        View footer_comment_layout = holder.footer
                .findViewById(R.id.myrebate_footer_comment_layout);
        try {
            //订单状态0--待买单  1--已完成  2--待评价
            if (state == 1) {
                footer_btn_layout.setVisibility(View.GONE);
                footer_comment_layout.setVisibility(View.VISIBLE);
            } else /* if (state == 0 || state == 2) */ {
                footer_btn_layout.setVisibility(View.VISIBLE);
                footer_comment_layout.setVisibility(View.GONE);
            }

            handleData(holder, rebate);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }

        return outterLayout;
    }

    /**
     * 处理数据
     *
     * @param holder
     * @param rebate
     */
    private void handleData(final MainHolder holder, final RebateInfo rebate) throws Exception {
        int state = rebate.orderStatus;

        TextView shopName = holder.getView(R.id.myrebate_header_shopname, holder.header);
        shopName.setText(rebate.shopName);

        TextView orderStatus = holder.getView(R.id.myrebate_header_state, holder.header);
        orderStatus.setText(R.string.rebate_my_waitting_for_pay);
        orderStatus.setTextAppearance(mContext, R.style.white_title_14sp);

        TextView footerTxt = holder.getView(R.id.myrebate_footer_txt, holder.footer);
        if (footerTxt != null) {
            footerTxt.setText(rebate.msg);
            footerTxt.setTextAppearance(mContext, R.style.black_title_18sp);
            setFooterDrawable(footerTxt, R.drawable.rebate_pay);
        }

        if (state != 1) {
            holder.footer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO 添加评论 或 立即买单动作
                    handleClick(rebate);
//					holder.footer.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.listitem_bg_selector));
                }
            });
        } else {
            holder.footer.setOnClickListener(null);
//			holder.footer.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        TextView returnMoney = holder.getView(R.id.rabate_amount, holder.body);
        TextView payPrice = holder.getView(R.id.consume_amount, holder.body);
        returnMoney.setText(rebate.returnMoney);
        payPrice.setText(rebate.payPrice);
        TextView getRebateTip = holder.getView(R.id.rebate_get_rebate_tip, holder.body);
        if (state == 0) {
            getRebateTip.setText(R.string.rebate_my_will_get_rebate);
            returnMoney.setTextAppearance(mContext, R.style.gray_title_20sp);
        } else {
            getRebateTip.setText(R.string.rebate_my_get_rebate);
            returnMoney.setTextAppearance(mContext, R.style.green_title_20sp);
        }

        switch (state) {
            case 0://待买单
                handleUndecidedOrder(holder, footerTxt);
                break;
            case 1://已完成  -- 显示 评价内容
                handleCompleteOrder(holder, rebate, orderStatus);
                break;
            case 2://待评价 -- 显示添加评价按钮
                handleAddCommetOrder(rebate, orderStatus, footerTxt);
                break;
            default:
                break;
        }
    }

    private void handleUndecidedOrder(MainHolder holder, TextView footerTxt) {
        footerTxt.setTextAppearance(mContext, R.style.black_title_18sp);
        setFooterDrawable(footerTxt, R.drawable.rebate_pay);
    }

    private void handleCompleteOrder(MainHolder holder, RebateInfo rebate,
                                     TextView orderStatusTextView) {
        try {
            orderStatusTextView.setText(rebate.date);
            orderStatusTextView.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
            orderStatusTextView.setBackgroundColor(mContext.getResources().getColor(R.color.white));

            View footerCommentLayout = holder.getView(R.id.myrebate_footer_comment_layout,
                    holder.footer);
            RatingBar commentRatingBar = holder.getView(R.id.myrebate_comment_ratingBar,
                    holder.footer);
            TextView comment = holder.getView(R.id.myrebate_comment_content, holder.footer);
            if (rebate.comment == null) {
                footerCommentLayout.setVisibility(View.GONE);
            } else {
                footerCommentLayout.setVisibility(View.VISIBLE);
                comment.setText(rebate.comment.content);
                commentRatingBar.setRating(Float.parseFloat(rebate.comment.star));
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    private void handleAddCommetOrder(RebateInfo rebate, TextView orderStatus, TextView footerTxt) {
        orderStatus.setText(rebate.date);
        orderStatus.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
        orderStatus.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        if (TextUtils.isEmpty(rebate.msg)) {
            footerTxt.setText(R.string.rebate_add_comment);
        } else {
            footerTxt.setText(rebate.msg);
        }
        footerTxt.setTextAppearance(mContext, R.style.gray_title_18sp);
        setFooterDrawable(footerTxt, R.drawable.rebate_add_comment);
    }

    private void setFooterDrawable(TextView footerTxt, int resId) {
        Drawable leftDrawable = mContext.getResources().getDrawable(resId);
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
                leftDrawable.getMinimumHeight());
        footerTxt.setCompoundDrawables(leftDrawable, null, null, null);
    }

    private void handleClick(RebateInfo rebate) {
        int status = rebate.orderStatus;//订单状态0--待买单 1--已完成 2--待评价
        if (status == 0) {
            Intent intent = new Intent(mContext, RebatePayActivity.class);
            intent.putExtra(RebateKey.KEY_INTENT_ORDER_ID, rebate.orderId);
            intent.putExtra(RebateKey.KEY_INTENT_IS_NEW_ORDER, false);
            mContext.startActivity(intent);
        } else if (status == 2) {
            Intent intent = new Intent(mContext, RebatePayCommentActivity.class);
            intent.putExtra(RebateKey.KEY_INTENT_ORDER_ID, rebate.orderId);
            mContext.startActivity(intent);
        }
    }
}

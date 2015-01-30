package com.shengshi.rebate.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.home.HomeEntity;
import com.shengshi.rebate.bean.home.HomeEntity.HomeInfo;
import com.shengshi.rebate.bean.home.HomeEntity.ShopInfoEntity;
import com.shengshi.rebate.bean.home.HomeEntity.ShopServiceItemInfo;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.ui.detail.RebateInfoDetailActivity;
import com.shengshi.rebate.utils.RebateImageLoader;
import com.shengshi.rebate.widget.RebateCalendar;

import java.util.List;

/**
 * <p>Title:           首页适配器
 * <p>Description:
 * <p>1.注意线的逻辑
 * <p>2.为什么采用枚举？见下面tip
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014年11月1日
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class HomeAdapter extends SimpleBaseAdapter<HomeEntity> {

    public enum ViewType {
        TYPE_TODAY(0), TYPE_NORMAL(1), TYPE_EMPTY(2);
        private int index;

        ViewType(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    HomeEntity mData;
    RebateImageLoader loader;

    public HomeAdapter(Context context, HomeEntity entity) {
        super(context);
        this.mData = entity;
        loader = RebateImageLoader.getInstance(context);
    }

    public void addData(HomeEntity entity) {
        HomeInfo info = entity.data;
        if (info != null && info.shop != null) {
            List<ShopInfoEntity> commonItemList = info.shop.data;
            if (commonItemList != null && commonItemList.size() > 0) {
                this.mData.data.shop.data.addAll(commonItemList);
            }
        }
    }

    public void replaceAll(HomeEntity entity) {
        HomeInfo info = entity.data;
        if (info != null && info.shop != null) {
            List<ShopInfoEntity> commonItemList = info.shop.data;
            if (commonItemList != null) {
                this.mData.data.shop.data.clear();
                this.mData.data.shop.data.addAll(commonItemList);
            }
        }
    }

    /**
     * 返回普通返利信息条数
     *
     * @return
     */
    public int getCommonItemCount() {
        int commonRebateSize = 0;
        if (mData != null && mData.data != null) {
            HomeInfo info = mData.data;
            if (info != null && info.shop != null) {
                commonRebateSize = info.shop.data.size();
            }
        }
        return commonRebateSize;
    }

    @Override
    public int getCount() {
        int commonRebateSize = 0;
        int todayRebateSize = 0;
        if (mData != null && mData.data != null) {
            HomeInfo info = mData.data;
            if (info != null) {
                if (info.shop != null) {
                    commonRebateSize = info.shop.data.size();
                }
                if (info.wzk != null) {
                    todayRebateSize = info.wzk.data.size();
                }
                int size = todayRebateSize + commonRebateSize;
                if (size == 0) {
                    return 1;
                }
                return size;
            }
        }
        return 0;
    }

    /**
     * Tip:保证getItemViewType()返回的值要 小于 getViewTypeCount()
     */
    @Override
    public int getItemViewType(int position) {
        try {
            List<ShopInfoEntity> normalItem = mData.data.shop.data;
            List<ShopServiceItemInfo> todayItem = mData.data.wzk.data;
            if (!CheckUtil.isValidate(normalItem) && !CheckUtil.isValidate(todayItem)) {
                return ViewType.TYPE_EMPTY.getIndex();
            }
            if (CheckUtil.isValidate(todayItem)) {
                if (position <= todayItem.size() - 1)
                    return ViewType.TYPE_TODAY.getIndex();
                else
                    return ViewType.TYPE_NORMAL.getIndex();
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return ViewType.TYPE_NORMAL.getIndex();
    }

    @Override
    public int getViewTypeCount() {
        return ViewType.values().length;
    }

    @Override
    public int getItemResource(int position) {
        int type = getItemViewType(position);
        if (type == ViewType.TYPE_TODAY.ordinal()) {
            return R.layout.rebate_listview_item_today;
        } else if (type == ViewType.TYPE_EMPTY.ordinal()) {
            return R.layout.rebate_listview_item_empty;
        } else {
            return R.layout.rebate_listview_item_normal;
        }
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        int type = getItemViewType(position);
        int index = position;
        HomeInfo info = mData.data;
        if (info == null) {
            return convertView;
        }

        List<ShopServiceItemInfo> todayItems = info.wzk.data;
        try {
            if (type == ViewType.TYPE_TODAY.getIndex()) {
                handleTodayItem(holder, index, todayItems);
            } else if (type == ViewType.TYPE_NORMAL.getIndex()) {
                index = position - todayItems.size();//注意下标变化
                if (info.shop != null && CheckUtil.isValidate(info.shop.data)) {
                    convertView.setVisibility(View.VISIBLE);
                    handleCommonItem(index, holder, info.shop.data.get(index));
                } else {
                    convertView.setVisibility(View.GONE);
                }
            } else {
                Log.i("---空empty 逻辑处理---");
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
        return convertView;
    }

    private void handleCommonItem(int index, ViewHolder holder, ShopInfoEntity shopInfoEntity)
            throws Exception {
        TextView normalTip = holder.getView(R.id.mHomeNormalTip);
        normalTip.setVisibility(View.GONE);
        View normalTipDivider = holder
                .getView(R.id.home_normal_tip_and_brand_name_divider_line_layout);
        if (0 == index) {
            normalTipDivider.setVisibility(View.VISIBLE);
        } else {
            normalTipDivider.setVisibility(View.GONE);
        }
        TextView brandName = holder.getView(R.id.mHomeNormalBrandName);
        brandName.setText(shopInfoEntity.shop.shopName);
        TextView brandDistance = holder.getView(R.id.brand_distance);
        brandDistance.setText(shopInfoEntity.shop.geodist);

        LinearLayout container = holder.getView(R.id.mHomeNormalShopServiceContainer);
        container.removeAllViews();
        List<ShopServiceItemInfo> items = shopInfoEntity.item.rows;
        if (CheckUtil.isValidate(items)) {
            for (int i = 0; i < items.size(); i++) {
                View commonRebateService = LayoutInflater.from(context).inflate(
                        R.layout.rebate_listview_item_rebate_info, null);
                container.addView(commonRebateService);
                if (i == items.size() - 1) {
                    commonRebateService.findViewById(R.id.bottom_line_layout).setVisibility(
                            View.GONE);
                }
                commonRebateService.findViewById(R.id.rebate_title).setVisibility(View.INVISIBLE);
                TextView rebateTitle = (TextView) commonRebateService.findViewById(R.id.brand_name);
                final ShopServiceItemInfo info = items.get(i);
                rebateTitle.setText(info.title);
                TextView rebateQuota = (TextView) commonRebateService
                        .findViewById(R.id.rebate_quota);
                rebateQuota.setText(info.returnMoney);
                ImageView brandImg = (ImageView) commonRebateService.findViewById(R.id.brand_img);
                loader.displayImage(info.img, brandImg, true);

                RebateCalendar calendar = (RebateCalendar) commonRebateService
                        .findViewById(R.id.mHomeCalendar);
                if (TextUtils.isEmpty(info.week) && TextUtils.isEmpty(info.day)) {
                    calendar.setVisibility(View.GONE);
                } else {
                    calendar.setVisibility(View.VISIBLE);
                    calendar.setWeek(info.week);
                    calendar.setDay(info.day);
                }

                commonRebateService.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, RebateInfoDetailActivity.class);
                        intent.putExtra(RebateKey.KEY_INTENT_REBATE_ITEM_ID, info.itemId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
            }
        }
    }

    private void handleTodayItem(ViewHolder holder, int index, List<ShopServiceItemInfo> todayItems)
            throws Exception {
        final ShopServiceItemInfo item = todayItems.get(index);
        if (index == 0) {
            holder.getView(R.id.home_today_tip_divider_line_layout).setVisibility(View.VISIBLE);
        } else {
            holder.getView(R.id.home_today_tip_divider_line_layout).setVisibility(View.GONE);
        }
        View bottomLine = holder.getView(R.id.bottom_line_layout);
        if (index == todayItems.size() - 1) {
            bottomLine.setVisibility(View.GONE);
        } else {
            bottomLine.setVisibility(View.VISIBLE);
        }
        TextView brandName = holder.getView(R.id.brand_name);
        brandName.setText(item.brandName);
        //返利条件 如 消费满xx 返回xx
        TextView rebateTitle = holder.getView(R.id.rebate_title);
        rebateTitle.setText(item.title);
        TextView rebateQuota = holder.getView(R.id.rebate_quota);
        rebateQuota.setText(item.returnMoney);
        ImageView brandImg = holder.getView(R.id.brand_img);
        loader.displayImage(item.img, brandImg, true);

        RebateCalendar calendar = holder.getView(R.id.mHomeCalendar);
        if (TextUtils.isEmpty(item.week) && TextUtils.isEmpty(item.day)) {
            calendar.setVisibility(View.GONE);
        } else {
            calendar.setVisibility(View.VISIBLE);
            calendar.setWeek(item.week);
            calendar.setDay(item.day);
        }

        View rebateItem = holder.getView(R.id.rebate_info_item_view);
        rebateItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RebateInfoDetailActivity.class);
                intent.putExtra(RebateKey.KEY_INTENT_REBATE_ITEM_ID, item.itemId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

}

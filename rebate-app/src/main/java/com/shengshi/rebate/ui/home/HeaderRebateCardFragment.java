package com.shengshi.rebate.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shengshi.base.adapter.SimpleBaseAdapter;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.ToastUtils;
import com.shengshi.base.widget.RunningTextView;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.home.HomeEntity;
import com.shengshi.rebate.bean.home.HomeEntity.ConditionOption;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.ui.base.RebateBaseFragment;
import com.shengshi.rebate.utils.PopupWindowUtil;

import java.util.List;

/**
 * <p>Title:     首页 同城卡,已废弃,被NewHeaderRebateCardFragment替代
 * <p>Description:   头部HeaderView
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-12-16
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
@Deprecated
public class HeaderRebateCardFragment extends RebateBaseFragment implements OnClickListener {

    HomeEntity mEntity;
    ImageView cardBg;
    RunningTextView cardMoney;
    LinearLayout categoryView, areaView, sortView;
    TextView cardMoneyTipTv;
    TextView categoryTv, areaTv, sortTv;

    ListView menuList;
    ListView subjectList;
    DataAdapter menuAdapter;
    DataAdapter subjectAdapter;
    PopupWindow popupWindow;

    @Override
    public void initComponents(View view) {
        cardBg = findImageViewById(view, R.id.rebate_card_bg);
        cardMoney = (RunningTextView) view.findViewById(R.id.card_money);
        categoryView = findLinearLayoutById(view, R.id.rebate_filter_category_layout);
        areaView = findLinearLayoutById(view, R.id.rebate_filter_area_layout);
        sortView = findLinearLayoutById(view, R.id.rebate_filter_sort_layout);
        categoryView.setOnClickListener(this);
        areaView.setOnClickListener(this);
        sortView.setOnClickListener(this);
        cardMoneyTipTv = findTextViewById(view, R.id.card_money_tip);
        categoryTv = findTextViewById(view, R.id.rebate_filter_category_tv);
        areaTv = findTextViewById(view, R.id.rebate_filter_area_tv);
        sortTv = findTextViewById(view, R.id.rebate_filter_sort_tv);
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(RebateKey.KEY_INTENT_HOME_ENTITY)) {
            mEntity = (HomeEntity) bundle.getSerializable(RebateKey.KEY_INTENT_HOME_ENTITY);
        }

        if (mEntity == null || mEntity.data == null) {
            return;
        }
        try {
            String cardImg = mEntity.data.card.img;
            if (!TextUtils.isEmpty(cardImg)) {
                imageLoader.setImageOnLoading(R.drawable.rebate_card_bg);
                imageLoader.setImageOnLoadFail(R.drawable.rebate_card_bg);
                imageLoader.displayImage(cardImg, cardBg, imageLoadingListener);
            }
            cardMoneyTipTv.setText(mEntity.data.card.title);
            cardMoney.playNumber(mEntity.data.card.money);
            categoryTv.setText(mEntity.data.menu.get(0).label);
            areaTv.setText(mEntity.data.menu.get(1).label);
            sortTv.setText(mEntity.data.menu.get(2).label);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
    }

    SimpleImageLoadingListener imageLoadingListener = new SimpleImageLoadingListener() {

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            imageLoader.setImageOnLoading(R.color.login_line);
            imageLoader.setImageOnLoadFail(R.color.login_line);
        }

    };

    @Override
    public int getMainContentViewId() {
        return R.layout.rebate_listview_header_home_card_layout;
    }

    public void refreshData(HomeEntity entity) {
        if (cardMoney != null && entity != null && entity.data != null) {
            cardMoney.playNumber(entity.data.card.money);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rebate_filter_category_layout) {
            showPopupWindow(categoryView, 0);
        } else if (viewId == R.id.rebate_filter_area_layout) {
            showPopupWindow(categoryView, 1);
        } else if (viewId == R.id.rebate_filter_sort_layout) {
            showPopupWindow(categoryView, 2);
        }

    }

    // TODO 显示PopupWindow
    private void showPopupWindow(View anchor, int index) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(R.layout.rebate_popupwindow_home_select_container_layout, null);
        menuList = findListViewById(contentView, R.id.rebate_lv_menu);
        subjectList = findListViewById(contentView, R.id.rebate_lv_subject);

        final List<ConditionOption> menuData = mEntity.data.menu.get(index).option;
        if (!CheckUtil.isValidate(menuData)) {
            ToastUtils.showToast(mActivity, R.string.default_no_data, Toast.LENGTH_SHORT);
            return;
        }

        popupWindow = PopupWindowUtil.createPopupWindow(mActivity, contentView, anchor);
        menuAdapter = new DataAdapter(mContext, menuData);
        menuList.setAdapter(menuAdapter);
        List<ConditionOption> sub = menuData.get(0).sub;
        if (!CheckUtil.isValidate(sub)) {
            subjectList.setVisibility(View.GONE);
        } else {
            subjectList.setVisibility(View.VISIBLE);
            subjectAdapter = new DataAdapter(mContext, sub);
            subjectList.setAdapter(subjectAdapter);
        }

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                menuAdapter.checked(position);
                List<ConditionOption> sub = menuData.get(position).sub;
                if (!CheckUtil.isValidate(sub)) {
                    subjectList.setVisibility(View.GONE);
                } else {
                    subjectList.setVisibility(View.VISIBLE);
                    subjectAdapter = new DataAdapter(mContext, sub);
                    subjectList.setAdapter(subjectAdapter);
                }
            }
        });

//		subjectAdapter.checked(-1);
        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                subjectAdapter.checked(i);
            }
        });

        if (mCallback != null) {
            mCallback.onScorllListView();
        }
    }

    public void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    class DataAdapter extends SimpleBaseAdapter<ConditionOption> {

        private int markPosition;

        public DataAdapter(Context mContext) {
            super(mContext);
        }

        public DataAdapter(Context context, List<ConditionOption> data) {
            super(context, data);
        }

        @Override
        public int getItemResource(int position) {
            return R.layout.rebate_listview_item_home_condition_search_layout;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            TextView name = holder.getView(R.id.rebate_home_condition_tv_name);
            name.setText(data.get(position).name);
            if (position == markPosition) {
                name.setEnabled(true);
                name.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                name.setEnabled(false);
                name.setTextColor(mContext.getResources().getColor(R.color.black));
            }
            return convertView;
        }

        public void checked(int markPosition) {
            this.markPosition = markPosition;
            if (markPosition >= 0 && markPosition < data.size()) {
                notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnScorllListViewListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnScorllListViewListener");
        }
    }

    OnScorllListViewListener mCallback;

    public interface OnScorllListViewListener {
        /**
         * ListView滑动
         */
        public void onScorllListView();
    }

}

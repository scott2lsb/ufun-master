package com.shengshi.rebate.ui.home;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shengshi.base.tools.Log;
import com.shengshi.base.tools.ToastUtils;
import com.shengshi.base.widget.RunningTextView;
import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.home.HomeEntity;
import com.shengshi.rebate.bean.home.HomeEntity.ConditionOption;
import com.shengshi.rebate.config.RebateKey;
import com.shengshi.rebate.ui.base.RebateBaseFragment;
import com.shengshi.rebate.ui.home.popup.ExpandTabView;
import com.shengshi.rebate.ui.home.popup.ExpandTabView.OnButtonClickListener;
import com.shengshi.rebate.ui.home.popup.PopView;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:     首页 同城卡
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
public class NewHeaderRebateCardFragment extends RebateBaseFragment {

    HomeEntity mEntity;
    ImageView cardBg;
    RunningTextView cardMoney;
    TextView cardMoneyTipTv;

    ExpandTabView expandTabView;
    ArrayList<String> mTextArray = new ArrayList<String>();
    ArrayList<View> mViewArray = new ArrayList<View>();

    int mIndex;

    @Override
    public void initComponents(View view) {
        cardBg = findImageViewById(view, R.id.rebate_card_bg);
        cardMoney = (RunningTextView) view.findViewById(R.id.card_money);
        cardMoneyTipTv = findTextViewById(view, R.id.card_money_tip);
        expandTabView = (ExpandTabView) view.findViewById(R.id.rebate_home_expandtabview);
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
            //FIXME 加载网络上的卡片背景图片
            String cardImg = mEntity.data.card.img;
            if (!TextUtils.isEmpty(cardImg)) {
                imageLoader.setImageOnLoading(R.drawable.rebate_card_bg);
                imageLoader.setImageOnLoadFail(R.drawable.rebate_card_bg);
                imageLoader.displayImage(cardImg, cardBg, imageLoadingListener);
            }
            cardMoneyTipTv.setText(mEntity.data.card.title);
            cardMoney.playNumber(mEntity.data.card.money);
            initExpandTabView();
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

    private void initExpandTabView() {
        PopView popView = null;
        List<ConditionOption> menuData = null;
        mTextArray.clear();
        mViewArray.clear();
        for (int index = 0; index < mEntity.data.menu.size(); index++) {
            menuData = mEntity.data.menu.get(index).option;
            popView = new PopView(mContext, menuData);
            popView.setId(index);
            initListener(popView);
            mTextArray.add(mEntity.data.menu.get(index).label);
            mViewArray.add(popView);
        }
        expandTabView.setValue(mTextArray, mViewArray);
        expandTabView.setOnButtonClickListener(new OnButtonClickListener() {

            @Override
            public void onClick(View view, int selectPosition) {
                List<ConditionOption> menuData = mEntity.data.menu.get(selectPosition).option;
                if (!CheckUtil.isValidate(menuData)) {
                    ToastUtils.showToast(mActivity, R.string.default_no_data, Toast.LENGTH_SHORT);
                    expandTabView.onPressBack();
                    return;
                }
                mIndex = selectPosition;
                if (mCallback != null) {
                    mCallback.onScorllListView();
                }
                RadioButton radioButton = (RadioButton) view;
                Drawable right = getDrawable(R.drawable.rebate_dropup_icon);
                right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
                radioButton.setCompoundDrawables(null, null, right, null);
            }
        });
    }

    private void initListener(final PopView popView) {
        popView.setOnSelectListener(new PopView.OnSelectListener() {

            @Override
            public void getValue(String showText, String key) {
                onRefresh(popView, showText, key);
            }
        });
    }

    private void onRefresh(View popView, String showText, String key) {
        expandTabView.onPressBack();
        int position = getPositon(popView);
        if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
            expandTabView.setTitle(showText, position);
        }
        if (mCallback != null) {
            mCallback.onSearchBySelectKeyword(key, mIndex);
        }
        expandTabView.resetUI();
    }

    private int getPositon(View popView) {
        for (int i = 0; i < mViewArray.size(); i++) {
            if (i == popView.getId()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getMainContentViewId() {
        return R.layout.rebate_listview_header_home_card_layout;
    }

    public void refreshData(HomeEntity entity) {
        mEntity = entity;
        if (cardMoney != null && entity != null && entity.data != null) {
            cardMoney.playNumber(entity.data.card.money);
            initExpandTabView();
        }
    }

    public boolean dismissPopupWindow() {
        if (expandTabView != null) {
            return expandTabView.onPressBack();
        }
        return false;
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

        public void onSearchBySelectKeyword(String key, int index);
    }

}

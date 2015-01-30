package com.shengshi.ufun.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.circle.ViewImageAdapter;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.photoselect.CommonPhotoSelectActivity;
import com.shengshi.ufun.photoselect.ViewType;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.weight.ViewPagerFixed;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title:        ViewImageActivity查看大图
 * <p>Description:
 * 如何使用：
 * <p>Intent intent = new Intent(mContext, ViewImageActivity.class);
 * <p>Bundle bundle = new Bundle();
 * <p>bundle.putStringArray("urls", list.toArray(new String[]{}));//传路径数组，支持url和本地path
 * <p>bundle.putInt("index", position);// 当前查看的图片的顺序位置
 * <p><strong>以下2个参数非必须，多图发帖等查看大图 可供选择功能 用</strong>
 * <p>bundle.putSerializable("ViewType", ViewType.Select);
 * <p>bundle.putStringArrayList("have_select_photos_list", have_select_photos_list);
 * <p>intent.putExtras(bundle);
 * <p>startActivity(intent);
 * <p/>
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-9-24
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class ViewImageActivity extends LifeCircleBaseActivity implements OnClickListener {

    private ViewPagerFixed mViewPager;
    private ViewImageAdapter adapter;
    private int curPos;
    private List<String> mUrlsdata;
    private TextView page;

    private ViewType viewType;
    private Button choiceCompleteBtn;

    private ArrayList<String> have_select_photos_list;//已经选择了多少图片
    private TextView mCheckBox;
    private boolean isChecked;


    @Override
    protected void initComponents() {
        super.initComponents();
        // 隐藏Activity刚进来焦点在EditText时的键盘显示
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.initView();
        this.initData();
    }


    // 初始化视图控件
    private void initView() {
        mViewPager = (ViewPagerFixed) findViewById(R.id.viewimagePager);
        page = (TextView) findViewById(R.id.head_center_title);
        findViewById(R.id.head_button_left_img).setOnClickListener(this);
        findViewById(R.id.head_button_right).setOnClickListener(this);
    }

    public void buttonClicked(View view) {
        if (adapter != null) {
            adapter.saveImgInThread();
        }
    }

    // 初始化控件数据
    protected void initData() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final String[] mUrls = bundle.getStringArray("urls");
        curPos = bundle.getInt("index", 0);
        viewType = (ViewType) bundle.getSerializable("ViewType");
        mUrlsdata = new ArrayList<String>();
        for (int i = 0; i < mUrls.length; i++) {
            mUrlsdata.add(mUrls[i]);
        }
        page.setText((curPos + 1) + "/" + mUrls.length);
        adapter = new ViewImageAdapter(this, curPos, mUrlsdata);
        mViewPager.setAdapter(adapter);
        // 定位点击传送来的position
        mViewPager.setCurrentItem(curPos);
        adapter.setCurrPos(curPos);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int postion) {
                if (postion > mUrls.length) {
                    postion = (mUrls.length - 1);
                }
                curPos = postion;
                page.setText((postion + 1) + "/" + mUrls.length);
                adapter.setCurrPos(postion);
                refreshCheckBoxText();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {// 从1到2滑动，在1滑动前调用
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {// 状态有三个0空闲，1是增在滑行中，2目标加载完毕
            }
        });
        viewType = (ViewType) bundle.getSerializable("ViewType");
        if (viewType == ViewType.Select) {
            have_select_photos_list = (ArrayList<String>) bundle
                    .getStringArrayList("have_select_photos_list");
            initSeletTypeView();
        }
    }

    private void initSeletTypeView() {
        findViewById(R.id.head_button_right).setVisibility(View.GONE);
        choiceCompleteBtn = (Button) findViewById(R.id.choiceCompleteBtn);
        choiceCompleteBtn.setOnClickListener(this);
        findViewById(R.id.widget_photo_img_select_bottom).setVisibility(View.VISIBLE);
        findViewById(R.id.widget_photo_img_select_bottom).setBackgroundColor(getResources().getColor(R.color.main_tab_tv_active));
        mCheckBox = (TextView) findViewById(R.id.child_checkbox);
        mCheckBox.setOnClickListener(this);
        mCheckBox.setVisibility(View.VISIBLE);
        refreshCheckBoxText();
        initChoiceBtnText();
    }

    /**
     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
     *
     * @param view
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f,
                1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }

    private void initChoiceBtnText() {
        if (!StringUtils.isValidate(have_select_photos_list)) {
            return;
        }
        String result = String.format(getString(R.string.choice_pics_num_tip),
                have_select_photos_list.size(), UFunConstants.MAX_CHOICE_PHOTO_NUM);
        choiceCompleteBtn.setText(result);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.head_button_left_img) {
            onBackPressed();
        } else if (id == R.id.child_checkbox) {
            doChoice();
        } else if (id == R.id.choiceCompleteBtn) {
            doComplete(true);
        }
    }

    private void doChoice() {
        String curPth = mUrlsdata.get(curPos);
        int size = have_select_photos_list.size();
        if (have_select_photos_list.contains(curPth)) {//多次相册选择情况
            isChecked = false;
        } else {
            isChecked = true;
            if (size >= UFunConstants.MAX_CHOICE_PHOTO_NUM) {
                String tip = String.format(getString(R.string.max_choice_photo_num_tip),
                        UFunConstants.MAX_CHOICE_PHOTO_NUM);
                toast(tip);
                mCheckBox
                        .setBackgroundResource(R.drawable.pictures_multiple_choice_icon_unselected);
                return;
            }
        }

        if (isChecked) {
            if (!have_select_photos_list.contains(curPth)) {
                have_select_photos_list.add(curPth);
            }
            refreshCheckBoxCount(++size);
            addAnimation(mCheckBox);
        } else {
            if (have_select_photos_list.contains(curPth)) {
                have_select_photos_list.remove(curPth);
            }
            resetTopCheckBox();
        }

        refreshChoiceBtnText();
    }

    private void refreshCheckBoxText() {
        if (viewType != ViewType.Select || !StringUtils.isValidate(have_select_photos_list)) {
            return;
        }
        String curPath = mUrlsdata.get(curPos);
        if (have_select_photos_list.contains(curPath)) {
            for (int i = 0; i < have_select_photos_list.size(); i++) {
                if (have_select_photos_list.get(i).equals(curPath)) {
                    refreshCheckBoxCount(i + 1);
                }
            }
        } else {
            resetTopCheckBox();
        }
    }

    private void refreshCheckBoxCount(int size) {
        mCheckBox.setText(size + "");
        mCheckBox.setBackgroundResource(R.drawable.pictures_multiple_choice_icon_selected);
    }

    private void resetTopCheckBox() {
        mCheckBox.setText("");
        mCheckBox.setBackgroundResource(R.drawable.pictures_multiple_choice_icon_unselected);
    }

    protected void refreshChoiceBtnText() {
        if (viewType != ViewType.Select) {
            return;
        }
        int size = have_select_photos_list.size();
        String result = String.format(getString(R.string.choice_pics_num_tip), size,
                UFunConstants.MAX_CHOICE_PHOTO_NUM);
        choiceCompleteBtn.setText(result);
    }

    @Override
    public void onBackPressed() {
        doComplete(false);
        super.onBackPressed();
    }

    /**
     * 选择完毕,往上层传给PhotoImageSelectActivity 或者 CommonPhotoSelectActivity
     */
    private void doComplete(boolean isManualComplete) {
        if (viewType != ViewType.Select) {
            finish();
            return;
        }
        ArrayList<String> selectPath = new ArrayList<String>(have_select_photos_list);
        Intent data = new Intent();
        data.putExtra("select_photos_path", selectPath);
        data.putExtra("isManualComplete", isManualComplete);
        if (isManualComplete) {
            setResult(RESULT_OK, data);
        } else {
            setResult(CommonPhotoSelectActivity.RESULT_MANUAL_CANCELED, data);
        }
        finish();
    }

    @Override
    public String getTopTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.activity_viewimage;
    }

}

package com.shengshi.rebate.ui.home.popup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.shengshi.http.utilities.CheckUtil;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.home.HomeEntity.ConditionOption;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Title:       自定义弹出窗体
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2015-1-9
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class PopView extends LinearLayout implements ViewBaseAction {

    private ListView menuList;
    private ListView subjectList;
    private TextAdapter menuAdapter;
    private TextAdapter subjectAdapter;
    private ArrayList<String> groups = new ArrayList<String>();
    private ArrayList<String> groupsKeywordArray = new ArrayList<String>();
    private ArrayList<String> childrenKeywordArray = new ArrayList<String>();
    private LinkedList<String> childrenItem = new LinkedList<String>();
    private SparseArray<LinkedList<String>> childrenMap = new SparseArray<LinkedList<String>>();
    private OnSelectListener mOnSelectListener;
    private int menuPosition = 0;
    private int subjectPosition = 0;
    private String showString = "不限";

    public PopView(Context context) {
        super(context);
        init(context, null);
    }

    public PopView(Context context, List<ConditionOption> groupsData) {
        super(context);
        init(context, groupsData);
    }

    public PopView(Context context, AttributeSet attrs, List<ConditionOption> groupsData) {
        super(context, attrs);
        init(context, groupsData);
    }

    private void init(Context context, List<ConditionOption> groupsData) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.rebate_popupwindow_home_select_container_layout, this, true);
        menuList = (ListView) findViewById(R.id.rebate_lv_menu);
        subjectList = (ListView) findViewById(R.id.rebate_lv_subject);
        //FIXME 设置弹出背景
//		setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_left));

        if (!CheckUtil.isValidate(groupsData)) {
            return;
        }
        for (int i = 0; i < groupsData.size(); i++) {
            ConditionOption option = groupsData.get(i);
            groups.add(option.name);
            groupsKeywordArray.add(option.key);
            LinkedList<String> tItem = new LinkedList<String>();
            if (option != null && CheckUtil.isValidate(option.sub)) {
                for (int j = 0; j < option.sub.size(); j++) {
                    tItem.add(option.sub.get(j).name);
                    childrenKeywordArray.add(option.sub.get(j).key);
                }
                childrenMap.put(i, tItem);
            }
        }

        if (childrenMap.size() > 0) {
            menuAdapter = new TextAdapter(context, groups, R.drawable.rebate_arrow_left_pressed,
                    R.drawable.rebate_arrow_left_more);
        } else {
            menuAdapter = new TextAdapter(context, groups, R.drawable.rebate_check_pressed_icon, 0);
        }
        int selectBgId = getContext().getResources().getColor(R.color.white);
        int normalBgId = getContext().getResources().getColor(R.color.white_f8f8f8);
        menuAdapter.setViewBackgroud(selectBgId, normalBgId);
        menuAdapter.setTextSize(17);
        menuAdapter.setSelectedPositionNoNotify(menuPosition);//默认选中0行
        menuList.setAdapter(menuAdapter);
        menuAdapter.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (childrenMap.size() > 0) {
                    childrenItem.clear();
                    if (childrenMap.get(position) == null) {
                        childrenItem.addAll(new LinkedList<String>());
                        showString = groups.get(position);
                        String keyword = childrenKeywordArray.get(position);
                        if (mOnSelectListener != null) {
                            mOnSelectListener.getValue(showString, keyword);
                        }
                    } else {
                        childrenItem.addAll(childrenMap.get(position));
                    }
                    subjectAdapter.notifyDataSetChanged();
                } else {
                    showString = groups.get(position);
                    String keyword = groupsKeywordArray.get(position);
                    if (mOnSelectListener != null) {
                        mOnSelectListener.getValue(showString, keyword);
                    }
                }
            }
        });

        if (menuPosition < childrenMap.size())
            childrenItem.addAll(childrenMap.get(menuPosition));

        if (!CheckUtil.isValidate(childrenItem)) {
            subjectList.setVisibility(View.GONE);
            return;
        }
        subjectAdapter = new TextAdapter(context, childrenItem,
                R.drawable.rebate_check_pressed_icon, 0);
        subjectAdapter.setViewBackgroud(selectBgId, selectBgId);//子类背景都是白色
        subjectAdapter.setTextSize(15);
//		subjectAdapter.setSelectedPositionNoNotify(subjectPosition);//默认选中0列
        subjectList.setAdapter(subjectAdapter);
        subjectAdapter.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, final int position) {
                showString = childrenItem.get(position);
                if (mOnSelectListener != null) {
                    String keyword = childrenKeywordArray.get(position);
                    mOnSelectListener.getValue(showString, keyword);
                }
            }
        });
        if (subjectPosition < childrenItem.size())
            showString = childrenItem.get(subjectPosition);

//		setDefaultSelect();

    }

    public void setDefaultSelect() {
        menuList.setSelection(menuPosition);
        subjectList.setSelection(subjectPosition);
    }

    public String getShowText() {
        return showString;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        public void getValue(String showText, String key);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }
}

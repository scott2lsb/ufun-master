package com.shengshi.rebate.ui.home.popup;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shengshi.rebate.R;

import java.util.List;

/**
 * <p>Title:      选择适配器
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
public class TextAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mListData;
    private int mSelectedPos = -1;
    private String mSelectedText = "";
    private Drawable selectedDrawble, normalDrawble;
    private float textSize;
    private OnClickListener onClickListener;
    private OnItemClickListener mOnItemClickListener;
    private int selectBgColorId, normalBgColorId;
    private int selectTextColorId, normalTextColorId;

    public TextAdapter(Context context, List<String> listData, int nId) {
        this(context, listData, 0, nId);
    }

    /**
     * TextAdapter 构造器
     *
     * @param context
     * @param listData
     * @param sId      textview drawableRight 右边选择图片
     * @param nId      textview drawableRight 右边正常图片
     */
    public TextAdapter(Context context, List<String> listData, int sId, int nId) {
        super(context, R.string.default_no_data, listData);
        mContext = context;
        mListData = listData;
        if (sId > 0) {
            selectedDrawble = mContext.getResources().getDrawable(sId);
        }
        if (nId > 0) {
            normalDrawble = mContext.getResources().getDrawable(nId);
        }
        init();
    }

    /**
     * 设置选中时 整体背景的颜色
     *
     * @param selectBgId 选中时的背景颜色，默认白色
     * @param normalBgId 正常时的背景颜色，默认灰色
     */
    public void setViewBackgroud(int selectBgColorId, int normalBgColorId) {
        this.selectBgColorId = selectBgColorId;
        this.normalBgColorId = normalBgColorId;
    }

    /**
     * 设置选中的文本颜色
     *
     * @param selectTextColorId 选中时的文本颜色，默认红色
     * @param normalTextColorId 未选中时的文本颜色，默认黑色
     */
    public void setTextColor(int selectTextColorId, int normalTextColorId) {
        this.selectTextColorId = selectTextColorId;
        this.normalTextColorId = normalTextColorId;
    }

    private void init() {
        selectBgColorId = mContext.getResources().getColor(R.color.white);
        normalBgColorId = mContext.getResources().getColor(R.color.white_f8f8f8);
        selectTextColorId = mContext.getResources().getColor(R.color.light_green);
        normalTextColorId = mContext.getResources().getColor(R.color.black);

        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedPos = (Integer) view.getTag();
                setSelectedPosition(mSelectedPos);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, mSelectedPos);
                }
            }
        };
    }

    /**
     * 设置选中的position,并通知列表刷新
     */
    public void setSelectedPosition(int pos) {
        if (mListData != null && pos < mListData.size()) {
            mSelectedPos = pos;
            mSelectedText = mListData.get(pos);
            notifyDataSetChanged();
        }

    }

    /**
     * 设置选中的position,但不通知刷新
     */
    public void setSelectedPositionNoNotify(int pos) {
        if (mListData != null && pos < mListData.size()) {
            mSelectedPos = pos;
            mSelectedText = mListData.get(pos);
        }
    }

    /**
     * 获取选中的position
     */
    public int getSelectedPosition() {
        if (mListData != null && mSelectedPos < mListData.size()) {
            return mSelectedPos;
        }
        return -1;
    }

    /**
     * 设置列表字体大小
     */
    public void setTextSize(float tSize) {
        textSize = tSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView == null) {
            view = (TextView) LayoutInflater.from(mContext).inflate(
                    R.layout.rebate_popupwindow_home_select_item, parent, false);
        } else {
            view = (TextView) convertView;
        }
        view.setTag(position);
        String mString = "";
        if (mListData != null && position < mListData.size()) {
            mString = mListData.get(position);
        }
        view.setText(mString);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        if (mSelectedText != null && mSelectedText.equals(mString)) {
            if (selectedDrawble != null) {
                selectedDrawble.setBounds(0, 0, selectedDrawble.getMinimumWidth(),
                        selectedDrawble.getMinimumHeight());
            }
            view.setCompoundDrawables(null, null, selectedDrawble, null);
            view.setBackgroundColor(selectBgColorId);//设置选中的背景颜色
            view.setTextColor(selectTextColorId);
        } else {
            if (normalDrawble != null) {
                normalDrawble.setBounds(0, 0, normalDrawble.getMinimumWidth(),
                        normalDrawble.getMinimumHeight());
            }
            view.setCompoundDrawables(null, null, normalDrawble, null);
            view.setBackgroundColor(normalBgColorId);//设置未选中状态背景颜色
            view.setTextColor(normalTextColorId);
        }
        view.setPadding(20, 0, 20, 0);//文本距离左边间距
        view.setOnClickListener(onClickListener);
        return view;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    /**
     * 重新定义菜单选项单击接口
     */
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

}

package com.shengshi.ufun.ui.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shengshi.base.ui.BaseFragment;
import com.shengshi.base.widget.CustomProgressDialog;
import com.shengshi.base.widget.LoadingBar;
import com.shengshi.base.widget.XScrollView;
import com.shengshi.base.widget.xlistview.XListView;
import com.shengshi.ufun.R;
import com.shengshi.ufun.utils.ImageLoader;

/**
 * <p>Title:      返利卡项目用到的Fragment必须继承此类
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-6
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public abstract class LifeCircleBaseFragment extends BaseFragment {

    public CustomProgressDialog loadingDialog;
    public ImageLoader imageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        imageLoader = ImageLoader.getInstance(mContext);//保证优先初始化
        super.onCreate(savedInstanceState);
    }

    /**
     * 显示加载对话框
     */
    public void showDialog() {
        showDialog("");
    }

    /**
     * 显示自定义提示信息的对话框
     *
     * @param msg
     */
    public void showDialog(String msg) {
        loadingDialog = CustomProgressDialog.getDialog(mActivity);
        if (loadingDialog != null) {
            loadingDialog.setCanceledOnTouchOutside(false);
            if (!TextUtils.isEmpty(msg)) {
                loadingDialog.setMessage(msg);
            }
            if (!getActivity().isFinishing() && !loadingDialog.isShowing()) {
                loadingDialog.show();
            }
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void hideDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * **********************查找view，避免子类一直强转**************************
     */

    protected TextView findTextViewById(View view, int id) {
        return (TextView) view.findViewById(id);
    }

    protected EditText findEditTextById(View view, int id) {
        return (EditText) view.findViewById(id);
    }

    protected Button findButtonById(View view, int id) {
        return (Button) view.findViewById(id);
    }

    protected ImageView findImageViewById(View view, int id) {
        return (ImageView) view.findViewById(id);
    }

    protected CheckBox findCheckBoxById(View view, int id) {
        return (CheckBox) view.findViewById(id);
    }

    protected RadioButton findRadioButtonById(View view, int id) {
        return (RadioButton) view.findViewById(id);
    }

    protected ListView findListViewById(View view, int id) {
        return (ListView) view.findViewById(id);
    }

    protected XListView findXListViewById(View view, int id) {
        return (XListView) view.findViewById(id);
    }

    protected XListView findXListView(View view) {
        return (XListView) view.findViewById(R.id.mGeneralListView);
    }

    protected XScrollView findXScrollViewById(View view, int id) {
        return (XScrollView) view.findViewById(id);
    }

    protected XScrollView findXScrollView(View view) {
        return (XScrollView) view.findViewById(R.id.mGeneralScrollView);
    }

    protected SurfaceView findSurfaceViewById(View view, int id) {
        return (SurfaceView) view.findViewById(id);
    }

    protected ProgressBar findProgressBarById(View view, int id) {
        return (ProgressBar) view.findViewById(id);
    }

    protected LinearLayout findLinearLayoutById(View view, int id) {
        return (LinearLayout) view.findViewById(id);
    }

    protected RelativeLayout findRelativeLayoutById(View view, int id) {
        return (RelativeLayout) view.findViewById(id);
    }

    protected FrameLayout findFrameLayoutById(View view, int id) {
        return (FrameLayout) view.findViewById(id);
    }

    protected LoadingBar findLoadingBarById(View view, int id) {
        return (LoadingBar) view.findViewById(id);
    }

    protected LoadingBar findGeneralLoadingBar(View view) {
        return (LoadingBar) view.findViewById(R.id.mGeneralLoadingBar);
    }

}

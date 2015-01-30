package com.shengshi.rebate.ui.detail;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.rebate.R;
import com.shengshi.rebate.bean.detail.CodeEntity;
import com.shengshi.rebate.ui.base.RebateBaseFragment;

/**
 * <p>Title:       返利码Fragment
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-11-5
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class DetailCodeFragment extends RebateBaseFragment {

    View codeLayout;
    TextView tips;
    TextView code1, code2, code3, code4, code5, code6;

    public static DetailCodeFragment newInstance(CodeEntity entity) {
        DetailCodeFragment fragment = new DetailCodeFragment();
        Bundle args = new Bundle();
        args.putSerializable("key_fragment_code_entity", entity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initComponents(View view) {
        codeLayout = view.findViewById(R.id.rebate_code_layout);
        tips = (TextView) view.findViewById(R.id.rebate_detail_code_tips);
        code1 = (TextView) view.findViewById(R.id.rebate_code1);
        code2 = (TextView) view.findViewById(R.id.rebate_code2);
        code3 = (TextView) view.findViewById(R.id.rebate_code3);
        code4 = (TextView) view.findViewById(R.id.rebate_code4);
        code5 = (TextView) view.findViewById(R.id.rebate_code5);
        code6 = (TextView) view.findViewById(R.id.rebate_code6);
    }

    public void fetchData(CodeEntity entity) {
        try {
            initCode(entity);
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("key_fragment_code_entity")) {
            CodeEntity entity = (CodeEntity) bundle.getSerializable("key_fragment_code_entity");
            initCode(entity);
        }
    }

    private void initCode(CodeEntity entity) {
        if (entity == null || entity.data == null) {
            return;
        }
        tips.setText(entity.data.msg);
        String code = entity.data.code;
        if (TextUtils.isEmpty(code)) {
            return;
        }
        char[] codeArray = code.toCharArray();
        if (codeArray.length == 6) {
            code1.setText(String.valueOf(codeArray[0]));
            code2.setText(String.valueOf(codeArray[1]));
            code3.setText(String.valueOf(codeArray[2]));
            code4.setText(String.valueOf(codeArray[3]));
            code5.setText(String.valueOf(codeArray[4]));
            code6.setText(String.valueOf(codeArray[5]));
        } else {
            codeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getMainContentViewId() {
        return R.layout.rebate_fragment_detail_code_layout;
    }

}

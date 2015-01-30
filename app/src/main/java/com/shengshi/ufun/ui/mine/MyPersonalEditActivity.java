package com.shengshi.ufun.ui.mine;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.shengshi.ufun.R;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;

public class MyPersonalEditActivity extends LifeCircleBaseActivity implements OnClickListener {

    EditText mSignature;

    @Override
    protected int getMainContentViewId() {
        return R.layout.mypersonal_edit;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        setReturnBtnEnable(false);
        mSignature = findEditTextById(R.id.signature);
        TextView lifecircle_top_tv_return = findTextViewById(R.id.lifecircle_top_tv_return);
        lifecircle_top_tv_return.setText("返回");
        lifecircle_top_tv_return.setVisibility(View.VISIBLE);
        lifecircle_top_tv_return.setOnClickListener(this);
    }

    @Override
    public String getTopTitle() {
        return "个性签名";
    }

    @Override
    protected void initData() {
        mSignature.setText(this.getIntent().getStringExtra("signature"));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.lifecircle_top_tv_return) {
            String signature = mSignature.getText().toString();
            Intent intent = new Intent(this, MyPersonalActivity.class);
            intent.putExtra("signature", signature);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}

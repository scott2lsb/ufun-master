package com.shengshi.ufun.ui.mine;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.BaseEntity;
import com.shengshi.ufun.bean.UserInfoEntity;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.utils.AccountUtil;

public class MyPersonalActivity extends LifeCircleBaseActivity implements OnClickListener {

    CircleImageView mine_personal_icon;
    TextView lifecircle_top_right_title;
    TextView mine_personal_birth;
    TextView mine_personal_gender;
    TextView mine_personal_signature;
    UserInfoEntity mUserInfoEntity;
    int DATA_PICKER_ID = 1;
    String birth;
    String signature;
    String icon;
    int setGender;
    int checkGender;

    int mYear;
    int mMonth;
    int mDay;
    String setIcon;

    @Override
    public String getTopTitle() {
        return getResources().getString(R.string.mine_personal);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_my_personal;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        lifecircle_top_right_title = findTextViewById(R.id.lifecircle_top_right_title);
        lifecircle_top_right_title.setText("完成");
        lifecircle_top_right_title.setOnClickListener(this);
        mine_personal_birth = findTextViewById(R.id.mine_personal_birth);
        mine_personal_gender = findTextViewById(R.id.mine_personal_gender);
        mine_personal_signature = findTextViewById(R.id.mine_personal_signature);
        mine_personal_icon = (CircleImageView) findViewById(R.id.mine_personal_icon);
        mine_personal_icon.setOnClickListener(this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_personal_birth_rl, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_personal_gender_rl, this);
        TopUtil.setOnclickListener(mActivity, R.id.mine_personal_signature_rl, this);
        setOnReturnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isEmpty(setIcon)) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        mUserInfoEntity = AccountUtil.getMineUserInfo(this);
        setUserInfo(mUserInfoEntity);
    }

    /**
     * 修改个人资料
     */
    private void requestUrl() {
        showTipDialog("保存中...");
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("user.update_user_info");
        encryptInfo.resetParams();
        encryptInfo.putParam("birth", birth);
        encryptInfo.putParam("gender", setGender);
        encryptInfo.putParam("signature", signature);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<BaseEntity> jsonCallback = new JsonCallback<BaseEntity>() {

        @Override
        public void onSuccess(BaseEntity result) {
            hideTipDialog();
            if (result != null) {
                toast(result.errMessage);
                if (result.errCode == 0) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            hideTipDialog();
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

    /**
     * 显示用户个人资料
     */
    private void setUserInfo(UserInfoEntity user) {
        if (user != null && user.data != null) {
            setGender = user.data.gender;
            String gender = "保密";
            if (user.data.gender == 1) {
                gender = "男";
            } else if (user.data.gender == 2) {
                gender = "女";
            }
            if (!StringUtils.isEmpty(user.data.icon)) {
                imageLoader.displayImage(user.data.icon, mine_personal_icon, true);
                icon = user.data.icon;
            }
            birth = user.data.birth;
            signature = user.data.signature;
            findTextViewById(R.id.mine_personal_mobile).setText(user.data.mobile);
            findTextViewById(R.id.mine_personal_username).setText(user.data.username);
            mine_personal_birth.setText(user.data.birth);
            mine_personal_gender.setText(gender);
            mine_personal_signature.setText(signature);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.mine_personal_birth_rl) {
            if (!StringUtils.isEmpty(birth)) {
                String[] births = birth.split("-");
                mYear = StringUtils.toInt(births[0], 1900);
                mMonth = StringUtils.toInt(births[1], 1) - 1;
                mDay = StringUtils.toInt(births[2], 1);
                showDialog(0);
            }
        } else if (id == R.id.mine_personal_gender_rl) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请选择性别");
            final String[] sex = {"保密", "男", "女"};
            //    设置一个单项选择下拉框
            /**
             * 第一个参数指定我们要显示的一组下拉单选框的数据集合
             * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
             * 第三个参数给每一个单选项绑定一个监听器
             */
            builder.setSingleChoiceItems(sex, setGender, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //toast("性别为：" + sex[which]);
                    checkGender = which;
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setGender = checkGender;
                    mine_personal_gender.setText(sex[setGender]);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        } else if (id == R.id.lifecircle_top_right_title) {
            if (mUserInfoEntity != null) {
                if (mUserInfoEntity.data.birth == null
                        || mUserInfoEntity.data.signature == null
                        || !mUserInfoEntity.data.birth.equals(birth)
                        || mUserInfoEntity.data.gender != setGender
                        || !mUserInfoEntity.data.signature.equals(signature)) {
                    requestUrl();
                } else {
                    toast("请选择修改项");
                }
            }

        } else if (id == R.id.mine_personal_signature_rl) {
            Intent intent = new Intent(this, MyPersonalEditActivity.class);
            intent.putExtra("signature", signature);
            startActivityForResult(intent, 1);
        } else if (id == R.id.mine_personal_icon) {
            Intent intent = new Intent(this, MyUpdateIconActivity.class);
            intent.putExtra("icon", icon);
            startActivityForResult(intent, 2);
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 0) {
            //返回一个日期对话框
            return new DatePickerDialog(this, setDateCallBack, mYear, mMonth, mDay);
        }
        return super.onCreateDialog(id);
    }

    //回调函数，int year, int monthOfYear,int dayOfMonth三个参数为日期对话框设置的日期
    private OnDateSetListener setDateCallBack = new OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear + 1;
            mDay = dayOfMonth;
            birth = mYear + "-" + mMonth + "-" + mDay;
            mine_personal_birth.setText(birth);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null || data.getExtras() == null) {
            return;
        }
        Bundle bundle = data.getExtras();
        if (requestCode == 1) {
            String setSignature = bundle.getString("signature");
            signature = setSignature;
            mine_personal_signature.setText(setSignature);
        } else if (requestCode == 2) {
            setIcon = bundle.getString("icon");
            if (!StringUtils.isEmpty(setIcon)) {
                imageLoader.displayImage(setIcon, mine_personal_icon, true);
            }
        }

    }

}

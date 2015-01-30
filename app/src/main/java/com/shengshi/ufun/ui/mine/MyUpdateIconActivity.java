package com.shengshi.ufun.ui.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.shengshi.base.tools.Log;
import com.shengshi.base.widget.CustomProgressDialog;
import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.Request;
import com.shengshi.http.net.Request.RequestMethod;
import com.shengshi.http.net.Request.RequestTool;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.http.utilities.UploadUtil;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.app.UFunApplication;
import com.shengshi.ufun.bean.mine.UpdateIconEntity;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.weight.SelectPopupWindow;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MyUpdateIconActivity extends LifeCircleBaseActivity implements OnClickListener {

    TextView lifecircle_top_right_title;
    CircleImageView mine_personal_icon;
    final int FISHPHOTOHRAPH = 1; // 拍照
    final int FISHPHOTOLOCAL = 2; // 本地图片
    final int TAKE_SMALL_PICTURE = 2;
    final int CROP_SMALL_PICTURE = 4;
    final int CHOOSE_SMALL_PICTURE = 6;
    Bitmap mImages = null; // 图片数据
    String icon;
    SelectPopupWindow menuWindow;
    String IMAGE_FILE_LOCATION;
    Uri imageUri;
    CustomProgressDialog customProgressDialog;
    Map<String, Object> mParams;

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_my_updateicon;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        setReturnBtnEnable(false);

        TextView lifecircle_top_tv_return = findTextViewById(R.id.lifecircle_top_tv_return);
        lifecircle_top_tv_return.setText("返回");
        lifecircle_top_tv_return.setVisibility(View.VISIBLE);
        lifecircle_top_tv_return.setOnClickListener(this);

        lifecircle_top_right_title = findTextViewById(R.id.lifecircle_top_right_title);
        lifecircle_top_right_title.setText("完成");
        lifecircle_top_right_title.setOnClickListener(this);

        mine_personal_icon = (CircleImageView) findViewById(R.id.mine_personal_icon);
        mine_personal_icon.setOnClickListener(this);
    }

    @Override
    public String getTopTitle() {
        return "修改头像";
    }

    @Override
    protected void initData() {
        icon = this.getIntent().getStringExtra("icon");
        if (!StringUtils.isEmpty(icon)) {
            imageLoader.displayImage(icon, mine_personal_icon, true);
        }
        if (UFunApplication.getApplication().getImgCacheDir() != null) {
            File file = new File(UFunApplication.getApplication().getImgCacheDir(),
                    "uFun_icon");
            IMAGE_FILE_LOCATION = "file://" + file.getPath();
            imageUri = Uri.parse(IMAGE_FILE_LOCATION);
        }
        customProgressDialog = UIHelper.customProgressDialog(this, "上传头像中...");
    }

    /**
     * 上传头像
     */
    private void requestUrl() {
        showTipDialog("上传头像中...");
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        Request request = new Request(url, RequestMethod.POST, RequestTool.HTTPURLCONNECTION);
        request.addHeader("Connection", "Keep-Alive");
        request.addHeader("Content-Type", "multipart/form-data;charset=utf-8;boundary="
                + UploadUtil.boundary);
        request.setCallback(jsonCallback);
        request.execute();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mImages.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
        encryptInfo.setCallback("user.update_icon");
        encryptInfo.resetParams();
        mParams = new HashMap<String, Object>();
        Map<String, Object> file = new HashMap<String, Object>();
        file.put("icon.jpg", data);
        mParams.put("icon", file);
        mParams.put("mkey", encryptInfo.encryptCodeByPost());
    }

    JsonCallback<UpdateIconEntity> jsonCallback = new JsonCallback<UpdateIconEntity>() {

        @Override
        public boolean onCustomOutput(OutputStream out) throws AppException {
            UploadUtil.upload(new DataOutputStream(out), mParams);
            return false;
        }

        ;

        @Override
        public void onSuccess(UpdateIconEntity result) {
            hideTipDialog();
            if (result != null) {
                if (result.errCode == 0) {
                    toast(result.data.msg);
                    if (result.data.status) {
                        Intent intent = new Intent(MyUpdateIconActivity.this, MyPersonalActivity.class);
                        intent.putExtra("icon", result.data.icon);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else {
                    toast(result.errMessage);
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
     * 加载图片弹出框
     */
    private void loadImage() {
        menuWindow = new SelectPopupWindow(this, itemsOnClick, 1, "");
        // 显示窗口
        menuWindow.showAtLocation(findViewById(R.id.mine_personal_icon_ll), Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    // 为弹出窗口实现监听类
    private OnClickListener itemsOnClick = new OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            Intent intent = null;
            int id = v.getId();
            if (id == R.id.btn_take_photo) {
                if (imageUri != null) {
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, TAKE_SMALL_PICTURE);
                } else {
                    toast("没有检测到SD卡，暂时无法使用拍照功能哦~");
                }
            } else if (id == R.id.btn_pick_photo) {
                intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true); // no face detection
                startActivityForResult(intent, CHOOSE_SMALL_PICTURE);
            }
        }
    };

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {//result is not correct
            return;
        } else {
            switch (requestCode) {
                case CHOOSE_SMALL_PICTURE:
                    if (data != null) {
                        Bitmap bitmap = data.getParcelableExtra("data");
                        if (bitmap != null) {
                            mImages = bitmap;
                            mine_personal_icon.setImageBitmap(mImages);
                        }
                    }
                    break;
                case TAKE_SMALL_PICTURE:
                    cropImageUri(imageUri, 200, 200, CROP_SMALL_PICTURE);
                    break;
                case CROP_SMALL_PICTURE:
                    if (imageUri != null) {
                        Bitmap bitmap = decodeUriAsBitmap(imageUri);
                        if (bitmap != null) {
                            mImages = bitmap;
                            mine_personal_icon.setImageBitmap(mImages);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.lifecircle_top_tv_return) {
            Intent intent = new Intent(this, MyPersonalActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        } else if (id == R.id.lifecircle_top_right_title) {
            if (mImages != null) {
                requestUrl();
            } else {
                toast("请选择新头像修改");
            }
        } else if (id == R.id.mine_personal_icon) {
            loadImage();
        }
    }

}

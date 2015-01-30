package com.shengshi.rebate.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.shengshi.base.tools.Log;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.Request;
import com.shengshi.http.net.Request.RequestMethod;
import com.shengshi.http.net.Request.RequestTool;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.http.net.callback.StringCallback;
import com.shengshi.http.utilities.UploadUtil;
import com.shengshi.rebate.R;
import com.shengshi.rebate.api.BaseEncryptInfo;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.rebate.ui.base.RebateBaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class TestUplaodActivity extends RebateBaseActivity {

    Bitmap imageBitmap;
    String imgUrl = "http://d.hiphotos.baidu.com/image/w%3D230/sign=800fdafbd762853592e0d522a0ee76f2/32fa828ba61ea8d3e640d40d950a304e251f585c.jpg";

    ImageView imageView;

    @Override
    public String getTopTitle() {
        return "测试类";
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.rebate_activity_test;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        imageView = findImageViewById(R.id.image);
    }

    @Override
    protected void initData() {
        imageBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.applogo);
        imgUrl = "http://g.hiphotos.baidu.com/image/pic/item/3b87e950352ac65cf6d124c1f8f2b21192138aa8.jpg";
        imageLoader.displayImage(imgUrl, imageView);
    }

    public void testFindCacheFile(View view) {
        File file = DiskCacheUtils.findInCache(imgUrl, ImageLoader.getInstance().getDiskCache());
        if (file != null && file.isFile() && file.exists()) {
            toast("找到缓存图片文件");
        } else {
            toast("error-找不到缓存图片文件");
        }

//		List<Bitmap> cachedBitmaps = MemoryCacheUtils.findCachedBitmapsForImageUri(imgUrl, ImageLoader.getInstance().getMemoryCache());
    }

    public void testUpload(View view) {
//		requestUrl();//使用自身http框架
        uploadMultiPic();
    }

    /**
     * 上传头像
     */
    public void requestUrl() {
        showDialog("上传头像中...");
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        Request request = new Request(url, RequestMethod.POST, RequestTool.HTTPURLCONNECTION);
        request.addHeader("Connection", "Keep-Alive");
        request.addHeader("Content-Type", "multipart/form-data;charset=utf-8;boundary="
                + UploadUtil.boundary);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<UpdateIconEntity> jsonCallback = new JsonCallback<UpdateIconEntity>() {

        @Override
        public boolean onCustomOutput(OutputStream out) throws AppException {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();

            BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
            encryptInfo.setCallback("user.update_icon");
            encryptInfo.resetParams();

            Map<String, Object> params = new HashMap<String, Object>();
            Map<String, Object> file = new HashMap<String, Object>();
            file.put("icon.jpg", data);
            params.put("icon", file);
            params.put("mkey", encryptInfo.encryptCodeByPost());
            UploadUtil.upload(new DataOutputStream(out), params);
            return false;
        }

        ;

        @Override
        public void onSuccess(UpdateIconEntity result) {
            hideDialog();
            if (result != null) {
                if (result.errCode == 0) {
                    toast(result.data.msg);
                    if (result.data.status) {
                        toast(result.data.icon);
                    }
                } else {
                    toast(result.errMessage);
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            hideDialog();
            Log.e("--" + exception.getMessage());
            toast(exception.getMessage());
        }
    };

    public void uploadMultiPic() {
        showDialog("上传多张中...");
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        Request request = new Request(url, RequestMethod.POST, RequestTool.HTTPURLCONNECTION);
        request.addHeader("Connection", "Keep-Alive");
        request.addHeader("Content-Type", "multipart/form-data;charset=utf-8;boundary="
                + UploadUtil.boundary);
        request.setCallback(listener);
        request.execute();
    }

    StringCallback listener = new StringCallback() {

        @Override
        public boolean onCustomOutput(OutputStream out) throws AppException {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();

            BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
            encryptInfo.setCallback("thread.postthread");
            encryptInfo.resetParams();
            encryptInfo.putParam("qid", 1);
            encryptInfo.putParam("subject", "测试多张图片");
            encryptInfo.putParam("message", "上传内容测试多张图片 ");

            Map<String, Object> params = new HashMap<String, Object>();
            Map<String, Object> file = null;
            for (int i = 0; i < 2; i++) {
                file = new HashMap<String, Object>();
                file.put("img" + (i + 1) + ".jpg", data);
                params.put("img" + (i + 1), file);
            }
            params.put("mkey", encryptInfo.encryptCodeByPost());
            UploadUtil.upload(new DataOutputStream(out), params);
            return false;
        }

        ;

        @Override
        public void onSuccess(String result) {
            hideDialog();
            toast(result);
        }

        @Override
        public void onFailure(AppException exception) {
            hideDialog();
            toast(exception.getMessage());
        }
    };

}

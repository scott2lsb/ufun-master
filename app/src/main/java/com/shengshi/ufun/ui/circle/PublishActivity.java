package com.shengshi.ufun.ui.circle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.shengshi.base.tools.NetUtil;
import com.shengshi.base.ui.tools.TopUtil;
import com.shengshi.base.widget.GridNoScrollView;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.Request;
import com.shengshi.http.net.Request.RequestMethod;
import com.shengshi.http.net.Request.RequestTool;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.http.utilities.UploadUtil;
import com.shengshi.rebate.config.RebateUrls;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.circle.SelectedTagAdapter;
import com.shengshi.ufun.adapter.circle.TagAdapter;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.circle.CircleTagEntity;
import com.shengshi.ufun.bean.circle.CircleTagEntity.SecondTag;
import com.shengshi.ufun.bean.mine.UpdateIconEntity;
import com.shengshi.ufun.common.StringUtils;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.photoselect.CommonPhotoSelectActivity;
import com.shengshi.ufun.photoselect.ImageTools;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublishActivity extends CommonPhotoSelectActivity implements
        OnItemClickListener, OnClickListener {
    EditText tvTitle;
    EditText tvContent;
    GridNoScrollView publish_gridview;
    TagAdapter tag;
    LinearLayout publish_onedimen_taglv;
    LinearLayout publish_seconddimen_taglv;
    TextView publish_seconddimen_addtagtv;
    private int qid;
    public static CircleTagEntity circleTagEntity;
    GridNoScrollView publish_seconddimen_gridview;
    public static final int SELECTTAG = 8; // 选择标签
    public static int[] selectp;
    Map<String, Object> mParams;
    JsonObject hashtag;// 用来保存选择的标签
    Boolean isFromPiccircle;

    @Override
    protected void initComponents() {
        super.initComponents();
        // 隐藏Activity刚进来焦点在EditText时的键盘显示
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        TopUtil.updateRightTitle(mActivity, R.id.lifecircle_top_right_title,
                R.string.publish);
        TopUtil.setOnclickListener(mActivity, R.id.lifecircle_top_right_title,
                this);
        tvTitle = (EditText) findViewById(R.id.publish_edit_title);
        tvContent = (EditText) findViewById(R.id.publish_edit_content);
        publish_gridview = (GridNoScrollView) findViewById(R.id.tag_gridview);
        publish_seconddimen_gridview = (GridNoScrollView) findViewById(R.id.publish_seconddimen_gridview);
        publish_onedimen_taglv = findLinearLayoutById(R.id.publish_onedimen_taglv);
        publish_seconddimen_taglv = findLinearLayoutById(R.id.publish_seconddimen_taglv);
        publish_seconddimen_addtagtv = findTextViewById(R.id.publish_seconddimen_addtagtv);
        publish_seconddimen_addtagtv.setOnClickListener(this);
        hashtag = new JsonObject();
    }

    @Override
    public String getTopTitle() {

        return getIntent().getStringExtra("qname");
    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.activity_publish;
    }


    @Override
    protected void initData() {
        setReturnBtnEnable(true);
        qid = getIntent().getIntExtra("qid", 0);
        isFromPiccircle = getIntent().getBooleanExtra("isFromPiccircle", false);
        requestUrl();
    }

    private void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("tag.quantaglist");
        encryptInfo.resetParams();
        encryptInfo.putParam("qid", qid);
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<CircleTagEntity> jsonCallback = new JsonCallback<CircleTagEntity>() {

        @Override
        public void onFailure(AppException arg0) {
            hideLoadingBar();
        }

        @Override
        public void onSuccess(CircleTagEntity result) {
            hideLoadingBar();
            if (result == null || result.data == null) {
                loadingBar.showFailLayout();
                return;
            }
            circleTagEntity = result;
            if (result.data.list != null) {
                fetchData(result);
            }

        }

    };

    private void fetchData(CircleTagEntity result) {
        if (result.data.list.size() == 1) {
            publish_onedimen_taglv.setVisibility(View.VISIBLE);
            publish_seconddimen_taglv.setVisibility(View.GONE);
            // 生成动态数组，并且转入数据
            ArrayList<SecondTag> lstImageItem = new ArrayList<SecondTag>();
            for (int i = 0; i < result.data.list.get(0).taglist.size(); i++) {
                lstImageItem.add(result.data.list.get(0).taglist.get(i));
            }

            // 添加并且显示
            tag = new TagAdapter(this, lstImageItem);
            publish_gridview.setAdapter(tag);
            publish_gridview.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    tag.chiceState(position);
                    hashtag.addProperty(
                            String.valueOf(circleTagEntity.data.list.get(0).partid),
                            String.valueOf(circleTagEntity.data.list.get(0).taglist
                                    .get(position).tagid));
                }
            });
        } else {
            publish_onedimen_taglv.setVisibility(View.GONE);
            publish_seconddimen_taglv.setVisibility(View.VISIBLE);
            selectp = new int[circleTagEntity.data.list.size()];
            for (int i = 0; i < circleTagEntity.data.list.size(); i++) {
                selectp[i] = -1;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish_seconddimen_addtagtv:
                Intent intent = new Intent(this, TagarrayActivity.class);
                intent.putExtra("circleTagEntity", circleTagEntity);
                intent.putExtra("selectposition", selectp);
                startActivityForResult(intent, SELECTTAG);
                break;

            case R.id.lifecircle_top_right_title:
                // requestPublishUrl();
                uploadMultiPic();
                break;

            default:
                break;
        }
    }

    @Override
    public void onImageClick(boolean isBadgeView) {
        super.onImageClick(isBadgeView);
        UIHelper.hideSoftInputMode(tvContent, this, true);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == SELECTTAG) {// 选择标签
            Bundle bundle = data.getExtras();
            selectp = bundle.getIntArray("selectposition");
            List<String> name = new ArrayList<String>();
            for (int i = 0; i < selectp.length; i++) {
                if (selectp[i] != -1) {
                    name.add(circleTagEntity.data.list.get(i).taglist
                            .get(selectp[i]).name);
                    hashtag.addProperty(
                            String.valueOf(circleTagEntity.data.list.get(i).partid),
                            String.valueOf(circleTagEntity.data.list.get(i).taglist
                                    .get(selectp[i]).tagid));
                }
            }
            SelectedTagAdapter se = new SelectedTagAdapter(this, name);
            publish_seconddimen_gridview.setVisibility(View.VISIBLE);
            publish_seconddimen_gridview.setAdapter(se);

        }
    }

    /**
     * 发布
     */
    List<byte[]> imageData = new ArrayList<byte[]>();

    public void uploadMultiPic() {
        if (!checkPublish()) {
            return;
        }
        showTipDialog("正在发布，请稍候...");
        String url = RebateUrls.GET_SERVER_ROOT_URL();
        Request request = new Request(url, RequestMethod.POST,
                RequestTool.HTTPURLCONNECTION);
        request.addHeader("Connection", "Keep-Alive");
        request.addHeader("Content-Type",
                "multipart/form-data;charset=utf-8;boundary="
                        + UploadUtil.boundary);
        request.setCallback(publishjsonCallback);
        request.execute();
    }

    JsonCallback<UpdateIconEntity> publishjsonCallback = new JsonCallback<UpdateIconEntity>() {

        @Override
        public boolean onCustomOutput(OutputStream out) throws AppException {
            boolean isCompress = ImageTools.getCompressFlag(mContext);
            if (mImages != null) {

                for (int i = 0; i < mImages.length; i++) {
                    if (mImages[i] != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        if (isCompress) {
                            mImages[i].compress(Bitmap.CompressFormat.JPEG, 75,
                                    stream);
                        } else {
                            mImages[i].compress(Bitmap.CompressFormat.JPEG,
                                    100, stream);
                        }
                        imageData.add(stream.toByteArray());
                    }
                }

            }

            BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
            encryptInfo.setCallback("thread.postthread");
            encryptInfo.resetParams();
            encryptInfo.putParam("qid", qid);
            encryptInfo.putParam("tag", hashtag);
            encryptInfo.putParam("subject", tvTitle.getText().toString());
            encryptInfo.putParam("message", tvContent.getText().toString());
            Map<String, Object> params = new HashMap<String, Object>();
            for (int i = 0; i < imageData.size(); i++) {
                Map<String, Object> file = new HashMap<String, Object>();
                file.put("img" + (i + 1) + ".jpg", imageData.get(i));
                params.put("img" + (i + 1), file);
            }
            params.put("mkey", encryptInfo.encryptCodeByPost());
            UploadUtil.upload(new DataOutputStream(out), params);
            return false;
        }

        ;

        @Override
        public void onSuccess(UpdateIconEntity result) {
            hideTipDialog();
            if (result.errCode == 0) {
                finish();
            }
            toast(result.errMessage);
        }

        @Override
        public void onFailure(AppException exception) {
            hideTipDialog();
            toast(exception.getMessage());
        }
    };

    /**
     * 检查发布条件 true 满足条件，可以发布 false 不满足条件，不能发布
     */
    private boolean checkPublish() {
        UIHelper.hideSoftInputMode(tvContent, this, true);

        if (StringUtils.isEmpty(tvTitle.getText().toString())) {
            toast(R.string.ThreadPosting_title_empty);
            return false;
        }
        if (StringUtils.isEmpty(tvContent.getText().toString())) {
            toast(R.string.ThreadPosting_content_empty);
            return false;
        }
        int tvTitlelength = StringUtils.getWordCount(tvTitle.getText()
                .toString());
        if (tvTitlelength < 4) {
            toast("标题至少4个字符，当前(" + tvTitlelength + "字符)");
            return false;
        }
        String content2 = tvContent.getText().toString();
        int tvContent = StringUtils.getWordCount(content2);
        if (tvContent < 4) {
            toast("正文至少4个字符，当前(" + tvContent + "字符)");
            return false;
        }

        if (isFromPiccircle) {
            if (mSelectPaths.size() < 1) {
                toast("至少需要添加一张图片");
                return false;
            }
        }
        if (!NetUtil.checkNet(this, true)) {
            return false;
        }
        return true;
    }

}

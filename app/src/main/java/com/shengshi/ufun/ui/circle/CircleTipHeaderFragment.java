package com.shengshi.ufun.ui.circle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shengshi.base.widget.roundimageview.CircleImageView;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.BaseEntity;
import com.shengshi.ufun.bean.circle.CircleThreadEntity;
import com.shengshi.ufun.bean.circle.CircleThreadEntity.Attch;
import com.shengshi.ufun.bean.circle.CircleThreadEntity.Thread;
import com.shengshi.ufun.config.UFunKey;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.ViewImageActivity;
import com.shengshi.ufun.ui.base.LifeCircleBaseFragment;

import java.util.ArrayList;
import java.util.List;

public class CircleTipHeaderFragment extends LifeCircleBaseFragment {
    private CircleThreadEntity entity;
    private TextView title;

    // 楼主信息
    private CircleImageView userAvatar;
    private TextView userName;
    private TextView publishTime;
    private TextView rankTitle;
    private TextView attentionTxt;
    private WebView contentWebView;
    private LinearLayout imgsContainer;
    private boolean FRIEND_STATE = true;
    private List<String> imgUrls;

    //
    // tui
    // private ImageView tuiImg;
    // private TextView tuiTitle;
    // private TextView tuiDesc;

    @Override
    public void initComponents(View view) {
        title = findTextViewById(view, R.id.lifecircle_activity_tip_title);

        // 楼主信息
        View authorLayer = view.findViewById(R.id.tip_content_author);
        authorLayer.setBackgroundColor(getResources().getColor(R.color.white));
        userAvatar = (CircleImageView) view
                .findViewById(R.id.lifecircle_tips_reply_publisher_avatar);
        userName = findTextViewById(view,
                R.id.lifecircle_tips_reply_publisher_name);
        publishTime = findTextViewById(view,
                R.id.lifecircle_tips_reply_publisher_time);
        rankTitle = findTextViewById(view,
                R.id.lifecircle_tips_reply_rank_title);
        attentionTxt = findTextViewById(view, R.id.lifecircle_reply_operation);

        // webview
        contentWebView = (WebView) view.findViewById(R.id.tip_header_content);
        contentWebView.getSettings().setDefaultTextEncodingName("utf-8");
        contentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webviewOnLoadFinishedListener.onLoadFinished();
            }
        });

        // imgs
        imgsContainer = (LinearLayout) view
                .findViewById(R.id.tip_imgs_container);
        imgUrls = new ArrayList<String>();
        // tui
        // tuiImg = findImageViewById(view, R.id.lifecircle_tip_tui_img);
        // tuiDesc = findTextViewById(view, R.id.lifecircle_tip_tui_desc);
        // tuiTitle = findTextViewById(view, R.id.lifecircle_tip_tui_title);
    }

    public void refreshData(final Thread entity) {
        int i = 0;
        title.setText(entity.subject);
        // 楼主
        imageLoader.displayImage(entity.author_avatar, userAvatar);
        userName.setText(entity.author);
        publishTime.setText(entity.postdate);
        if (entity.rank_title == null) {
            rankTitle.setVisibility(View.INVISIBLE);
        } else {
            rankTitle.setText(entity.rank_title);
        }

        FRIEND_STATE = entity.isfriend == 1 ? true : false;
        if (FRIEND_STATE) {
            attentionTxt.setBackgroundColor(getResources().getColor(
                    R.color.white));
            attentionTxt.setText("取消关注");
        } else {
            attentionTxt.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.plus));
        }
        attentionTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attention(entity);
            }
        });

        // webview
        contentWebView.loadDataWithBaseURL(null, entity.content, "text/html",
                "utf-8", null);

        // imgs
        if (entity.attchs.size() > 0) {
            for (Attch attch : entity.attchs) {
                imgUrls.add(attch.pic);
            }
            String imgUrl[] = new String[entity.attchs.size()];
            Bundle bundle = new Bundle();
            bundle.putStringArray("urls", imgUrls.toArray(imgUrl));
            for (Attch attch : entity.attchs) {
                imgUrls = new ArrayList<String>();
                if (!imgUrls.contains(attch.pic)) {
                    ImageView img = new ImageView(mContext);
                    bundle.putInt("index", i++);
                    final Intent intent = new Intent(mContext, ViewImageActivity.class);
                    intent.putExtras(bundle);
                    img.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(intent);
                        }
                    });
                    imgsContainer.addView(img);
                    imageLoader.displayImage(attch.pic, img);
                }
            }
        }
    }

    @Override
    public void initData() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(UFunKey.KEY_INTENT_TIP_HEADER)) {
            entity = (CircleThreadEntity) bundle
                    .getSerializable(UFunKey.KEY_INTENT_TIP_HEADER);
        }
        if (entity == null) {
            return;
        }
        refreshData(entity.data.thread);
    }

    private void attention(Thread entity) {
        if (FRIEND_STATE) {
            // 取消关注
            String url = UFunUrls.GET_SERVER_ROOT_URL();
            UFunRequest request = new UFunRequest(mContext, url);
            BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
            encryptInfo.setCallback("user.cancel_attention");
            encryptInfo.resetParams();
            encryptInfo.putParam("touid", entity.authorid);
            request.setCallback(attentionCallback);
            request.execute();
        } else {
            // 关注
            String url = UFunUrls.GET_SERVER_ROOT_URL();
            UFunRequest request = new UFunRequest(mContext, url);
            BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(mContext);
            encryptInfo.setCallback("user.pay_attention");
            encryptInfo.resetParams();
            encryptInfo.putParam("touid", entity.authorid);
            request.setCallback(attentionCallback);
            request.execute();
        }
    }

    JsonCallback<BaseEntity> attentionCallback = new JsonCallback<BaseEntity>() {
        @Override
        public void onSuccess(BaseEntity entity) {
            if (entity.errCode == 0) {
                if (entity.errMessage.equals("关注成功")) {
                    toast("关注成功");
                    attentionTxt.setBackgroundColor(getResources().getColor(
                            R.color.white));
                    attentionTxt.setText("取消关注");
                    FRIEND_STATE = true;
                } else if (entity.errMessage.equals("取消关注成功")) {
                    toast("取消关注成功");
                    attentionTxt.setBackgroundDrawable(getResources()
                            .getDrawable(R.drawable.plus));
                    attentionTxt.setText("");
                    FRIEND_STATE = false;
                }
            }
        }

        @Override
        public void onFailure(AppException exception) {
            toast("ON FAILURE");
        }
    };

    @Override
    public int getMainContentViewId() {
        return R.layout.ufun_activity_tip_content_header;
    }

    private WebviewOnLoadFinishedListener webviewOnLoadFinishedListener;

    public interface WebviewOnLoadFinishedListener {
        void onLoadFinished();
    }

    public void setWebviewOnLoadFinishedListener(
            WebviewOnLoadFinishedListener listener) {
        this.webviewOnLoadFinishedListener = listener;
    }

}

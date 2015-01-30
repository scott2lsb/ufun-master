package com.shengshi.ufun.photoselect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shengshi.ufun.R;
import com.shengshi.ufun.app.UFunApplication;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.ui.ViewImageActivity;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.utils.Functions;
import com.shengshi.ufun.weight.BadgeView;
import com.shengshi.ufun.weight.RoundedImageView;
import com.shengshi.ufun.weight.SelectPopupWindow;

import java.io.File;
import java.util.ArrayList;


/**
 * <p>Title:       抽出图片选择积累，让发帖、回帖直接继承此类，免得代码臃肿，只专注图片选择
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-9-23
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class CommonPhotoSelectActivity extends LifeCircleBaseActivity {

    //自定义的弹出框类
    protected SelectPopupWindow menuWindow;

    public static final int FISHPHOTOHRAPH = 1; //拍照
    public static final int FISHPHOTOLOCAL = 2; //本地图片
    public static final int FISHIMAGEPREVIEW = 3; //预览
    public static final int RESULT_MANUAL_CANCELED = 4;

    protected Bitmap[] mImages; //图片数据
    protected RoundedImageView[] imageViews;
    protected FrameLayout[] flayout;
    protected int[] imageViewIds = new int[]{R.id.one_pic, R.id.two_pic, R.id.three_pic,
            R.id.four_pic, R.id.five_pic, R.id.six_pic, R.id.seven_pic, R.id.eight_pic};
    protected int[] flViewIds = new int[]{R.id.onefv_pic, R.id.twofv_pic, R.id.threefv_pic,
            R.id.fourfv_pic, R.id.fivefv_pic, R.id.sixfv_pic, R.id.sevenfv_pic, R.id.eightfv_pic};
    protected BadgeView[] badgeViews;
    protected TextView onePicTextTip;
    protected View secondPreviewLayout;
    protected int mCurImageIndex = 0; //当前点击图片索引
    protected ArrayList<String> mSelectPaths = new ArrayList<String>();//选择图片的路径  用于8张图片分多次选择情况


    @Override
    protected void initComponents() {
        super.initComponents();
        // 隐藏Activity刚进来焦点在EditText时的键盘显示
        mImages = new Bitmap[UFunConstants.MAX_CHOICE_PHOTO_NUM]; //图片数据
        imageViews = new RoundedImageView[UFunConstants.MAX_CHOICE_PHOTO_NUM];
        flayout = new FrameLayout[UFunConstants.MAX_CHOICE_PHOTO_NUM];
        badgeViews = new BadgeView[UFunConstants.MAX_CHOICE_PHOTO_NUM];
        initSelectImageView();
    }

    /**
     * 初始化选择图片控件
     */
    public void initSelectImageView() {
        for (int i = 0; i < UFunConstants.MAX_CHOICE_PHOTO_NUM; i++) {
            imageViews[i] = (RoundedImageView) findViewById(imageViewIds[i]);
            flayout[i] = findFrameLayoutById(flViewIds[i]);
            imageViews[i].setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int viewId = v.getId();
                    boolean isBadgeView = false;
                    if (v instanceof BadgeView) {
                        viewId = ((BadgeView) v).getTarget().getId();
                        isBadgeView = true;
                    }
                    for (int i = 0; i < imageViewIds.length; i++) {
                        if (viewId == imageViewIds[i]) {
                            mCurImageIndex = i;
                            onImageClick(isBadgeView);
                            break;
                        }
                    }
                }
            });
            badgeViews[i] = new BadgeView(this, flayout[i]);
            badgeViews[i].setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badgeViews[i].setBackgroundResource(R.drawable.icon_bubble_delete);
            badgeViews[i].setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int viewId = v.getId();
                    boolean isBadgeView = false;
                    if (v instanceof BadgeView) {
                        viewId = ((BadgeView) v).getTarget().getId();
                        isBadgeView = true;
                    }
                    for (int i = 0; i < flViewIds.length; i++) {
                        if (viewId == flViewIds[i]) {
                            mCurImageIndex = i;
                            onImageClick(isBadgeView);
                            break;
                        }
                    }

                }
            });
        }
        onePicTextTip = (TextView) findViewById(R.id.one_pic_text_tip);
        onePicTextTip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                loadImage();
            }
        });
        secondPreviewLayout = findViewById(R.id.picture_preview_second_layout);
        secondPreviewLayout.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK && resultCode != RESULT_MANUAL_CANCELED) {
            return;
        }
        if (requestCode == FISHPHOTOHRAPH) { //拍照
            handlePhotohraph(data);
        } else if (requestCode == FISHPHOTOLOCAL) {
            handlePhotoLocal(data);
        }
        refreshPreviewUI();
    }

    @SuppressWarnings("unchecked")
    public void handlePhotoLocal(Intent data) {
        if (data != null) {
            ArrayList<String> selectPaths = (ArrayList<String>) data.getSerializableExtra("select_photos_path");
            mSelectPaths = selectPaths;
        }
    }

    public void handlePhotohraph(Intent data) {
        if (UFunApplication.getApplication().getImgCacheDir() != null) {//sdcard
            File file = new File(UFunApplication.getApplication().getImgCacheDir(), "fish_photo" + mCurImageIndex);
            mSelectPaths.add(file.getPath());
        }
    }

    /**
     * 点击图片控件 动作
     *
     * @param isBadgeView
     */
    public void onImageClick(boolean isBadgeView) {
        if (isBadgeView) {
            deleteImage();
        } else {
            if (mImages[mCurImageIndex] != null) {
                Intent intent = new Intent(this, ViewImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("urls", mSelectPaths.toArray(new String[]{}));
                bundle.putInt("index", mCurImageIndex);
                //增加预览删除功能
                bundle.putSerializable("ViewType", ViewType.Select);
                bundle.putStringArrayList("have_select_photos_list", mSelectPaths);
                intent.putExtras(bundle);
                startActivityForResult(intent, FISHPHOTOLOCAL);
            } else {
                loadImage();
            }
        }
    }

    public void deleteImage() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("是否删除此图片?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mSelectPaths.remove(mCurImageIndex);
                        refreshPreviewUI();
                        toast("成功删除图片");
                    }
                }).setNegativeButton("取消", null).show();
    }

    /**
     * 加载图片
     *
     * @param index: 图片索引
     */
    private void loadImage() {
        menuWindow = new SelectPopupWindow(this, itemsOnClick, 1, "");
        // 设置layout在PopupWindow中显示的位置
        menuWindow.showAtLocation(getRootView(this), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    //为弹出窗口实现监听类
    private OnClickListener itemsOnClick = new OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            if (v.getId() == R.id.btn_take_photo) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (UFunApplication.getApplication().getImgCacheDir() != null) {//判断是否有SD卡
                    intent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(new File(UFunApplication.getApplication().getImgCacheDir(), "fish_photo"
                                    + mCurImageIndex)));
                    startActivityForResult(intent, FISHPHOTOHRAPH);
                } else {
                    toast("没有检测到SD卡，暂时无法使用拍照功能哦~");
                }
            } else if (v.getId() == R.id.btn_pick_photo) {
//				Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
//				intent1.setType("image/*");
//				startActivityForResult(intent1, FISHPHOTOLOCAL);
                Intent intent = new Intent(getApplicationContext(), PhotoAlbumActivity.class);
                intent.putExtra("have_select_photos_list", mSelectPaths);
                startActivityForResult(intent, FISHPHOTOLOCAL);
            }
        }
    };

    /**
     * 刷新预览控件的显示
     */
    public void refreshPreviewUI() {
        for (int i = 0; i < UFunConstants.MAX_CHOICE_PHOTO_NUM; i++) {
            mImages[i] = null;
            imageViews[i].setImageResource(R.drawable.icon_photo_add_middle);
            if (i == mSelectPaths.size()) {
                badgeViews[i].hide();
            }
            if (i <= mSelectPaths.size()) {
                imageViews[i].setVisibility(View.VISIBLE);
            } else {
                imageViews[i].setVisibility(View.GONE);
                badgeViews[i].hide();
            }
            if (mSelectPaths.size() < 4) {
                secondPreviewLayout.setVisibility(View.GONE);
            } else {
                secondPreviewLayout.setVisibility(View.VISIBLE);
            }
        }
        for (int i = 0; i < mSelectPaths.size(); i++) {
            mImages[i] = Functions.decodeBitmapFile(mSelectPaths.get(i));
            mImages[i] = Functions.rotateBitmapFile(mImages[i], mSelectPaths.get(i)); //判断旋转
            imageViews[i].setVisibility(View.VISIBLE);
            imageViews[i].setImageBitmap(mImages[i]);
            imageViews[i].setBackgroundDrawable(null);
//			badgeViews[i].setVisibility(View.VISIBLE);
//			badgeViews[i].setGravity(Gravity.TOP);
//			badgeViews[i].setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            badgeViews[i].show();
        }
        if (mSelectPaths.size() == 0) {
            onePicTextTip.setVisibility(View.VISIBLE);
        } else {
            onePicTextTip.setVisibility(View.GONE);
        }
    }

    /**
     * 确认是否放弃编辑
     */
    public void showBackDlg() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("您确定是否放弃编辑并返回上一页？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                }).setNegativeButton("取消", null).show();
    }

    @Override
    public String getTopTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub

    }

}

package com.shengshi.ufun.photoselect;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;

import com.shengshi.ufun.R;
import com.shengshi.ufun.config.UFunConstants;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title:          支持图片多张选择                                         </p>
 * <p>Description:                          </p>
 * <p>@author:       liaodl                 </p>
 * <p>Copyright: Copyright (c) 2014         </p>
 * <p>Company:    @小鱼网                     </p>
 * <p>Create Time:     2014年7月29日                              </p>
 * <p>@author:                              </p>
 * <p>Update Time:                          </p>
 * <p>Updater:                              </p>
 * <p>Update Comments:                      </p>
 */
public class PhotoImageSelectActivity extends LifeCircleBaseActivity {
    private GridView mGridView;
    private List<String> list;
    private PhotoChildAdapter adapter;
    private ArrayList<String> have_select_photos_list = new ArrayList<String>();//已经选择了多少图片

    private CheckBox originalPhotoCheckbox;
    private Button choiceCompleteBtn;
    private boolean isCompress;//是否压缩


    @Override
    protected void initComponents() {
        super.initComponents();
        have_select_photos_list = (ArrayList<String>) getIntent().getSerializableExtra(
                "have_select_photos_list");

        mGridView = (GridView) findViewById(R.id.photo_multiple_grid);
        list = getIntent().getStringArrayListExtra("data");

        adapter = new PhotoChildAdapter(this, list, have_select_photos_list, mGridView);
        mGridView.setAdapter(adapter);

        originalPhotoCheckbox = (CheckBox) findViewById(R.id.original_photo_checkbox);
        originalPhotoCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCompress = !isChecked;//isChecked为true，代表上传原图，不压缩
                ImageTools.saveCompressFlag(getApplicationContext(), isCompress);
                if (isChecked) {
                    originalPhotoCheckbox.setTextColor(0xff14a1e8);
                } else {
                    originalPhotoCheckbox.setTextColor(Color.BLACK);
                }
            }
        });

        choiceCompleteBtn = (Button) findViewById(R.id.choiceCompleteBtn);
        choiceCompleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doComplete(true);
            }
        });

        refreshChoiceBtnText();


    }


    protected void refreshChoiceBtnText() {
        if (have_select_photos_list == null) {
            return;
        }
        int size = have_select_photos_list.size();
        String result = String.format(getString(R.string.choice_pics_num_tip), size,
                UFunConstants.MAX_CHOICE_PHOTO_NUM);
        choiceCompleteBtn.setText(result);
    }

    @Override
    public void onBackPressed() {
        doComplete(false);
        super.onBackPressed();
    }

    boolean isManualComplete;
    Intent selectImgUrl;

    /**
     * 选择完毕
     */
    private void doComplete(boolean isManualComplete) {
        this.isManualComplete = isManualComplete;
        selectImgUrl = new Intent();
        selectImgUrl.putExtra("select_photos_path", have_select_photos_list);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CommonPhotoSelectActivity.FISHPHOTOLOCAL) {
            if (data != null) {
                doBackInit(data);
                finish();
            }
        } else if (resultCode == CommonPhotoSelectActivity.RESULT_MANUAL_CANCELED
                && requestCode == CommonPhotoSelectActivity.FISHPHOTOLOCAL) {
            if (data != null) {
                doBackInit(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("unchecked")
    private void doBackInit(Intent data) {
        isManualComplete = data.getBooleanExtra("isManualComplete", false);
        have_select_photos_list = (ArrayList<String>) data
                .getSerializableExtra("select_photos_path");
        adapter.updateDate(have_select_photos_list);
        refreshChoiceBtnText();
        selectImgUrl = data;
    }

    @Override
    public void finish() {
        if (isManualComplete) {//收到ViewImageActivity反馈，继续往上层传给PhotoAlbumActivity
            setResult(RESULT_OK, selectImgUrl);
        } else {
            setResult(CommonPhotoSelectActivity.RESULT_MANUAL_CANCELED, selectImgUrl);
        }
        ImageTools.saveCompressFlag(getApplicationContext(), true);//复位，默认压缩
        super.finish();
    }

    @Override
    public String getTopTitle() {
        return "图片选择";
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_photo_image_select;
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub

    }

}

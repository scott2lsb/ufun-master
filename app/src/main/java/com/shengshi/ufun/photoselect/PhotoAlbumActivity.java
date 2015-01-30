package com.shengshi.ufun.photoselect;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.shengshi.base.widget.CustomProgressDialog;
import com.shengshi.ufun.R;
import com.shengshi.ufun.common.UIHelper;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Title:            相册分类                                                  </p>
 * <p>Description:                          </p>
 * <p>@author:       liaodl                 </p>
 * <p>Copyright: Copyright (c) 2014         </p>
 * <p>Company:    @小鱼网                     </p>
 * <p>Create Time:     2014年7月23日                              </p>
 * <p>@author:                              </p>
 * <p>Update Time:                          </p>
 * <p>Updater:                              </p>
 * <p>Update Comments:                      </p>
 */
public class PhotoAlbumActivity extends LifeCircleBaseActivity {
    /**
     * 将图片按照文件夹进行分类,Key是文件夹名，Value是文件夹中的图片路径的List
     */
    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    private List<ImageBean> list = new ArrayList<ImageBean>();
    private final static int SCAN_OK = 1;
    private CustomProgressDialog mProgressDialog;
    private PhotoGroupAdapter adapter;
    private GridView mGroupGridView;
    private ArrayList<String> have_select_photos_list;//已经选择了多少图片

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    //关闭进度条
                    mProgressDialog.dismiss();
                    adapter = new PhotoGroupAdapter(PhotoAlbumActivity.this,
                            list = subGroupOfImage(mGruopMap), mGroupGridView);
                    mGroupGridView.setAdapter(adapter);
                    break;
            }
        }

    };


    @Override
    protected void initComponents() {
        super.initComponents();

        mGroupGridView = (GridView) findViewById(R.id.photo_album_grid);

        have_select_photos_list = (ArrayList<String>) getIntent().getSerializableExtra(
                "have_select_photos_list");

        getImages();

        mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> childList = mGruopMap.get(list.get(position).getFolderName());
                Intent mIntent = new Intent(PhotoAlbumActivity.this, PhotoImageSelectActivity.class);
                mIntent.putStringArrayListExtra("data", (ArrayList<String>) childList);
                mIntent.putExtra("have_select_photos_list", have_select_photos_list);
                startActivityForResult(mIntent, CommonPhotoSelectActivity.FISHPHOTOLOCAL);
            }
        });
    }


    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }

        //显示进度条
        mProgressDialog = UIHelper.customProgressDialog(this, "加载中...");
        mProgressDialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = PhotoAlbumActivity.this.getContentResolver();

                //只查询jpeg和png的图片
//				Cursor mCursor = mContentResolver.query(mImageUri, null,
//						MediaStore.Images.Media.MIME_TYPE + "=? or "
//								+ MediaStore.Images.Media.MIME_TYPE + "=?",
//						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
                Cursor mCursor = mContentResolver.query(mImageUri, null, null, null,
                        MediaStore.Images.Media.DATE_MODIFIED + " DESC");

                while (mCursor.moveToNext()) {
                    //获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();

                    //根据父路径名将图片放入到mGruopMap中
                    if (!mGruopMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        if (!mGruopMap.get(parentName).contains(path)) {
                            mGruopMap.get(parentName).add(path);
                        }
                    }
                }

                mCursor.close();

                //通知Handler扫描图片完成
                mHandler.sendEmptyMessage(SCAN_OK);

            }
        }).start();

    }

    /**
     * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     *
     * @param mGruopMap
     * @return
     */
    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
        if (mGruopMap.size() == 0) {
            return null;
        }
        List<ImageBean> list = new ArrayList<ImageBean>();

        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();

            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片

            list.add(mImageBean);
        }

        return list;

    }

    Intent selectImgUrl;

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CommonPhotoSelectActivity.FISHPHOTOLOCAL) {
            if (data != null) {
                selectImgUrl = data;
                have_select_photos_list = (ArrayList<String>) data
                        .getSerializableExtra("select_photos_path");
                finish();
            }
        } else if (resultCode == CommonPhotoSelectActivity.RESULT_MANUAL_CANCELED
                && requestCode == CommonPhotoSelectActivity.FISHPHOTOLOCAL) {
            if (data != null) {
                selectImgUrl = null;
                have_select_photos_list = (ArrayList<String>) data
                        .getSerializableExtra("select_photos_path");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        if (selectImgUrl != null) {//继续往上层传CommonPhotoSelectActivity
            setResult(RESULT_OK, selectImgUrl);
        } else {
            setResult(RESULT_OK);
        }
        super.finish();
    }

    @Override
    public String getTopTitle() {
        // TODO Auto-generated method stub
        return "相册选择";
    }

    @Override
    protected int getMainContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.activity_photo_album;
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub

    }

}

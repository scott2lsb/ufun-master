package com.shengshi.ufun.ui.circle;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.shengshi.base.tools.Log;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.ufun.R;
import com.shengshi.ufun.adapter.circle.CategoryAdapter;
import com.shengshi.ufun.adapter.circle.CategorySubAdapter;
import com.shengshi.ufun.api.BaseEncryptInfo;
import com.shengshi.ufun.api.UFunRequest;
import com.shengshi.ufun.bean.circle.AllCircleEntity;
import com.shengshi.ufun.config.UFunUrls;
import com.shengshi.ufun.ui.base.LifeCircleBaseActivity;
import com.shengshi.ufun.weight.CategoryListView;

public class AddCircleActivity extends LifeCircleBaseActivity {
    CategoryListView addcircle_firstlist;
    CategoryListView addcircle_secondlist;
    CategoryAdapter categoryAdapter;
    CategorySubAdapter categorySubAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setReturnBtnEnable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_circle, menu);
        return true;
    }

    @Override
    public String getTopTitle() {
        // TODO Auto-generated method stub
        return getResources().getString(R.string.add_circle);
    }

    @Override
    protected int getMainContentViewId() {
        return R.layout.activity_add_circle;
    }

    @Override
    protected void initData() {
        addcircle_firstlist = (CategoryListView) findListViewById(R.id.addcircle_firstlist);
        addcircle_secondlist = (CategoryListView) findListViewById(R.id.addcircle_secondlist);

    }

    private void requestUrl() {
        String url = UFunUrls.GET_SERVER_ROOT_URL();
        UFunRequest request = new UFunRequest(this, url);
        BaseEncryptInfo encryptInfo = BaseEncryptInfo.getInstance(this);
        encryptInfo.setCallback("quan.getlist");
        encryptInfo.resetParams();
        request.setCallback(jsonCallback);
        request.execute();
    }

    JsonCallback<AllCircleEntity> jsonCallback = new JsonCallback<AllCircleEntity>() {

        @Override
        public void onFailure(AppException arg0) {
            hideLoadingBar();
        }

        @Override
        public void onSuccess(AllCircleEntity result) {
            // TODO Auto-generated method stub
            // FIXME 使用本地测试数据
            if (result == null || result.data == null) {
                showFailLayout(result.errMessage, null);
                return;
            }
            hideLoadingBar();
            fetchData(result);


        }

    };

    protected void fetchData(AllCircleEntity result) {
        final AllCircleEntity rt = result;
        try {
            categoryAdapter = new CategoryAdapter(mContext, result.data.list);
            addcircle_firstlist.setAdapter(categoryAdapter);
            selectDefult(result);
            addcircle_firstlist
                    .setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int position, long arg3) {

                            final int location = position;
                            categoryAdapter.setSelectedPosition(position);
                            categoryAdapter.notifyDataSetInvalidated();
                            categorySubAdapter = new CategorySubAdapter(
                                    getApplicationContext(), rt.data.list
                                    .get(position).quanlist, 0);
                            addcircle_secondlist.setAdapter(categorySubAdapter);
                            addcircle_secondlist
                                    .setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(
                                                AdapterView<?> arg0, View arg1,
                                                int position, long arg3) {
                                            Intent intent = new Intent();
                                            if (rt.data.list.get(location).quanlist.get(position).styleid == 1) {
                                                intent.setClass(mContext, PureTextCircleActivity.class);
                                                intent.putExtra("qid", rt.data.list.get(location).id);
                                                startActivity(intent);
                                            } else if (rt.data.list.get(location).quanlist.get(position).styleid == 2) {
                                                intent.setClass(mContext, PicCircleActivity.class);
                                                intent.putExtra("qid", rt.data.list.get(location).id);
                                                startActivity(intent);
                                            }
                                        }

                                    });
                        }
                    });
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }
    }

    private void selectDefult(AllCircleEntity result) {
        final int location = 0;
        final AllCircleEntity rt = result;
        categoryAdapter.setSelectedPosition(0);
        categoryAdapter.notifyDataSetInvalidated();
        categorySubAdapter = new CategorySubAdapter(getApplicationContext(),
                result.data.list.get(0).quanlist, 0);
        addcircle_secondlist.setAdapter(categorySubAdapter);
        addcircle_secondlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Intent intent = new Intent();
                if (rt.data.list.get(location).quanlist.get(position).styleid == 1) {
                    intent.setClass(mContext, PureTextCircleActivity.class);
                    intent.putExtra("qid", rt.data.list.get(location).id);
                    startActivity(intent);
                } else if (rt.data.list.get(location).quanlist.get(position).styleid == 2) {
                    intent.setClass(mContext, PicCircleActivity.class);
                    intent.putExtra("qid", rt.data.list.get(location).id);
                    startActivity(intent);
                }
            }

        });
    }

    @Override
    protected void onResume() {
        requestUrl();
        super.onRestart();
    }
}

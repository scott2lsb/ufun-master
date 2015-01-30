package com.shengshi.http.net;

import android.os.Environment;
import android.test.AndroidTestCase;

import com.shengshi.http.entities.ModuleEntity;
import com.shengshi.http.net.callback.AbstractCallback;
import com.shengshi.http.net.callback.ICallback;
import com.shengshi.http.net.callback.JsonArrayCallback;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.http.net.callback.JsonObjectCallback;
import com.shengshi.http.utilities.Trace;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * <p>Title:
 * <p>Description:
 * <p>@author:  liaodl
 * <p>Copyright: Copyright (c) 2014
 * <p>Company: @小鱼网
 * <p>Create Time: 2014-9-30
 * <p>@author:
 * <p>Update Time:
 * <p>Updater:
 * <p>Update Comments:
 */
public class TestJson extends AndroidTestCase {
    public void testJson() throws Throwable {
        ICallback<ArrayList<ModuleEntity>> callback = new JsonCallback<ArrayList<ModuleEntity>>() {

            @Override
            public void onSuccess(ArrayList<ModuleEntity> result) {
                for (ModuleEntity moduleEntity : result) {
                    Trace.d(moduleEntity.getModuleName());
                }
            }

            @Override
            public void onFailure(AppException exception) {

            }
        };

        String content = getJson();
        ArrayList<ModuleEntity> entities = callback.bindData(content);
        callback.onSuccess(entities);

    }

    public void testJson2() throws Throwable {
        AbstractCallback<ArrayList<ModuleEntity>> callback = new JsonArrayCallback<ModuleEntity>() {

            @Override
            public void onSuccess(ArrayList<ModuleEntity> result) {
                for (ModuleEntity moduleEntity : result) {
                    Trace.d(moduleEntity.getModuleName());
                }
            }

            @Override
            public void onFailure(AppException exception) {

            }
        };
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "module.json";
        callback.cache(path);
        ArrayList<ModuleEntity> entities = callback.bindData(path);
        callback.onSuccess(entities);
    }

    public void testJson3() throws Throwable {
        AbstractCallback<ModuleEntity> callback = new JsonObjectCallback<ModuleEntity>() {

            @Override
            public void onSuccess(ModuleEntity result) {
                Trace.d(result.getModuleName());
            }

            @Override
            public void onFailure(AppException exception) {

            }
        };
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "module_object.json";
        callback.cache(path);
        ModuleEntity entity = callback.bindData(path);
        callback.onSuccess(entity);
    }

    private String getJson() throws Exception {
        FileInputStream fis = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "module.json"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len = -1;
        long curPos = 0;
        while ((len = fis.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        fis.close();
        out.flush();
        String content = new String(out.toByteArray(), "UTF-8");
        out.close();

        return content;
    }
}

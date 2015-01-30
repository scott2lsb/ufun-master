package com.shengshi.http;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

import com.shengshi.http.entities.ModuleEntity;
import com.shengshi.http.net.AppException;
import com.shengshi.http.net.AppException.ExceptionStatus;
import com.shengshi.http.net.IRequestListener;
import com.shengshi.http.net.Request;
import com.shengshi.http.net.Request.RequestMethod;
import com.shengshi.http.net.Request.RequestTool;
import com.shengshi.http.net.RequestManager;
import com.shengshi.http.net.callback.FileCallback;
import com.shengshi.http.net.callback.JsonCallback;
import com.shengshi.http.net.callback.StringCallback;
import com.shengshi.http.utilities.Trace;
import com.shengshi.http.utilities.UploadUtil;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;

public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // testJson();
        testDownload();
    }

    public void testJson() {
        final Request request = new Request("http://bcs.duapp.com/jsontest/project/module.dat");
        request.addHeader("Content-Type", "*/*");
        request.setCallback(new JsonCallback<ArrayList<ModuleEntity>>() {
            public ArrayList<ModuleEntity> preRequest() {
//				TODO query from database
//				request.url = "http://www.stay4it.com";
                return null;
            }

            public ArrayList<ModuleEntity> postRequest(ArrayList<ModuleEntity> entities) {
//				TODO insert database
//				TODO comparator
                return entities;
            }


            @Override
            public void onSuccess(ArrayList<ModuleEntity> result) {
                for (ModuleEntity moduleEntity : result) {
                    Trace.d(moduleEntity.getModuleName());
                }
            }

            @Override
            public void onFailure(AppException result) {
                result.printStackTrace();
            }

            @Override
            public int retryCount() {
                return 3;
            }
        });
        request.execute();
    }

    public void testDownload() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "demo.apk";
        final Request request = new Request("http://bcs.duapp.com/jsontest/gfan.apk");
        request.addHeader("Content-Type", "*/*");
        request.setCallback(new FileCallback() {

            @Override
            public void onSuccess(String path) {
                Trace.d(path);
            }

            @Override
            public void onFailure(AppException exception) {
                if (exception.getStatus() == ExceptionStatus.CancelException) {
                    Trace.d("cancel success");
                }
                Trace.e(exception.getMessage());
            }
        }.cache(path));
        request.setRequestListener(new IRequestListener() {

            @Override
            public void onProgressUpdate(int curPos, int contentLength) {
                Trace.d("downloading: " + curPos + "/" + contentLength);
//				if (curPos > 100) {
//					request.cancel();
//				}
            }


        });
//		request.execute();
        RequestManager.getInstance().execute(toString(), request);
    }


    public void testUpload() {
        final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "demo.apk";
        String url = "";
        final Request request = new Request(url, RequestMethod.POST, RequestTool.HTTPURLCONNECTION);
        request.addHeader("Content-Type", "*/*");
        request.setCallback(new StringCallback() {

            @Override
            public void onSuccess(String path) {
                Trace.d(path);
            }

            @Override
            public boolean onCustomOutput(OutputStream out) throws AppException {
//				TODO upload
                UploadUtil.upload(out, filePath);
//				UploadUtil.upload(out, postContent, entities);
                return false;
            }


            @Override
            public void onFailure(AppException exception) {
                if (exception.getStatus() == ExceptionStatus.CancelException) {
                    Trace.d("cancel success");
                }
                Trace.e(exception.getMessage());
            }

        });
        RequestManager.getInstance().execute(toString(), request);
    }


    @Override
    protected void onStop() {
        super.onStop();
        RequestManager.getInstance().cancel(toString());
    }

}

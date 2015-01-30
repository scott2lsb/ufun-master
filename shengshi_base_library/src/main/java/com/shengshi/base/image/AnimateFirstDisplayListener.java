package com.shengshi.base.image;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

    private static AnimateFirstDisplayListener listener;
    static final Object sInstanceSync = new Object();

    private AnimateFirstDisplayListener() {
    }

    public static AnimateFirstDisplayListener getListener() {
        synchronized (sInstanceSync) {
            if (listener == null) {
                listener = new AnimateFirstDisplayListener();
            }
        }

        return listener;
    }

    private static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (loadedImage != null) {
            ImageView imageView = (ImageView) view;
            boolean firstDisplay = !displayedImages.contains(imageUri);
            if (firstDisplay) {
                FadeInBitmapDisplayer.animate(imageView, 1500);
            } else {
                imageView.setImageBitmap(loadedImage);
            }
            displayedImages.add(imageUri);
        }
    }
}

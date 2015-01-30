package com.shengshi.ufun.photoselect;

import android.graphics.Point;

/**
 * <p>Title:          本地图片请求封装                                               </p>
 * <p>Description:   用于优先加载可视化区域的图片封装                   </p>
 * <p>@author:       liaodl                 </p>
 * <p>Copyright: Copyright (c) 2014         </p>
 * <p>Company:    @小鱼网                     </p>
 * <p>Create Time:     2014年8月7日                              </p>
 * <p>@author:                              </p>
 * <p>Update Time:                          </p>
 * <p>Updater:                              </p>
 * <p>Update Comments:                      </p>
 */
public class ImageRequest {
    /**
     * 图片路径
     */
    private String mPath = "";

    /**
     * 图片size
     */
    private Point mSize = null;

    /**
     * 加载图片后回调
     */
    private NativeImageLoader.NativeImageCallBack mCallBack = null;

    public ImageRequest(String mPath, Point mSize, NativeImageLoader.NativeImageCallBack mCallBack) {
        super();
        this.mPath = mPath;
        this.mSize = mSize;
        this.mCallBack = mCallBack;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public Point getSize() {
        return mSize;
    }

    public void setSize(Point size) {
        this.mSize = size;
    }

    public NativeImageLoader.NativeImageCallBack getCallBack() {
        return mCallBack;
    }

    public void setCallBack(NativeImageLoader.NativeImageCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ImageRequest other = (ImageRequest) obj;
        if (this.mPath.equals(other.mPath)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ImageRequest [mPath=" + mPath + ", mSize=" + mSize;
    }

}

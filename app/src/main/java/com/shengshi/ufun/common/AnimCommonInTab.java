package com.shengshi.ufun.common;

/**
 * @author chenyh
 *         tabhost�????�?activity跳转??��?�使???
 */
public class AnimCommonInTab {

    public static int ANIM_IN = 0;
    public static int ANIM_OUT = 0;

    public static void set(int in, int out) {
        ANIM_IN = in;
        ANIM_OUT = out;
    }

    public static void clear() {
        ANIM_IN = 0;
        ANIM_OUT = 0;
    }
}

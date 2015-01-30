package com.shengshi.ufun.ui;

import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;

import com.shengshi.ufun.common.AnimCommonInTab;


public class BaseTabActivity extends TabActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Context context = getApplicationContext();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (AnimCommonInTab.ANIM_IN != 0 && AnimCommonInTab.ANIM_OUT != 0) {
            super.overridePendingTransition(AnimCommonInTab.ANIM_IN,
                    AnimCommonInTab.ANIM_OUT);
            AnimCommonInTab.clear();
        }

    }

}

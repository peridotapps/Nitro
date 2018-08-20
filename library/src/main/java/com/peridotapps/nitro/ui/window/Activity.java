package com.peridotapps.nitro.ui.window;

import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;

public abstract class Activity extends NitroActivity {

    public Activity(@LayoutRes int layoutResourceId) {
        super(layoutResourceId);
    }

    public Activity(@LayoutRes int layoutResourceId, @Nullable @MenuRes Integer menuResourceId) {
        super(layoutResourceId, menuResourceId);
    }

    @Override
    public final void attachLayout() {
        super.attachLayout();
    }

}

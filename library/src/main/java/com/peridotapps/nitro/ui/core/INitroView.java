package com.peridotapps.nitro.ui.core;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;

public interface INitroView extends INitroUiConcurrent {

    @LayoutRes
    int getLayoutResourceId();

    void bindData();

    void gatherControls();

    @Nullable
    <T extends View> T findViewById(@IdRes int resourceId);

}

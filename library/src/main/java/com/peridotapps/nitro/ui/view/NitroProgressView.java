package com.peridotapps.nitro.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.peridotapps.nitro.ui.core.INitroCustomControl;

public abstract class NitroProgressView extends LinearLayout implements INitroCustomControl {

    public NitroProgressView(Context context) {
        this(context, null);
    }

    public NitroProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
        gatherControls();
        bindData();
    }

    public final void showProgressView() {
        toggleVisibility(View.VISIBLE);
    }

    public final void hideProgressView() {
        toggleVisibility(View.GONE);
    }

    private void toggleVisibility(int visibility) {
        if (isMainThread()) {
            setVisibility(visibility);
        } else {
            runOnUiThread(() -> toggleVisibility(visibility));
        }
    }

    @Override
    public final ViewGroup getViewGroup() {
        return this;
    }
}

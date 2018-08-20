package com.peridotapps.nitro.ui.window;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.peridotapps.nitro.ui.core.INitroDataboundWindow;

public abstract class DataboundActivity<B extends ViewDataBinding> extends NitroActivity implements INitroDataboundWindow<B> {

    private B binding;

    public DataboundActivity(@LayoutRes int layoutResourceId) {
        super(layoutResourceId);
    }

    public DataboundActivity(@LayoutRes int layoutResourceId, @Nullable @MenuRes Integer menuResourceId) {
        super(layoutResourceId, menuResourceId);
    }

    @Override
    public final void attachLayout() {
        binding = DataBindingUtil.setContentView(this, getLayoutResourceId());
    }

    @NonNull
    @Override
    public final B getBinding() {
        return binding;
    }

    @Override
    public final void gatherControls() {
        // All controls are available from the binding object, so this method is stubbed and ignored
    }

    @Override
    public void bindData(@NonNull B binding) {
        // Stub
    }

    @Override
    public final void bindData() {
        bindData(getBinding());
    }

}

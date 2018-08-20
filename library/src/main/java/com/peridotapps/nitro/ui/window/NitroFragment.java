package com.peridotapps.nitro.ui.window;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peridotapps.nitro.hardware.Network;
import com.peridotapps.nitro.ui.core.INitroWindow;
import com.peridotapps.nitro.ui.view.NitroProgressView;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class NitroFragment extends Fragment implements INitroWindow, Network.NetworkStatusObserver {

    private LayoutInflater inflater;
    private ViewGroup container;
    private View view;
    private static final AtomicBoolean hasNetworkConnection = new AtomicBoolean(true);

    @Override
    public void attachLayout() {
        view = inflater.inflate(getLayoutResourceId(), container, shouldAttachToRoot());
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setCreateProperties(inflater, container);
        setHasOptionsMenu(hasMenu());
        attachLayout();
        gatherControls();
        handleCreateViewSavedInstanceState(savedInstanceState);
        return view;
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        Network.getNetworkMonitor()
                .addObserver(this);
        bindData();
    }

    @CallSuper
    @Override
    public void onPause() {
        Network.getNetworkMonitor()
                .removeObserver(this);
        super.onPause();
    }

    @Override
    public Integer getMenuResourceId() {
        return null;
    }

    @Override
    public void gatherControls() {
        // Stub
    }

    @Override
    public void bindData() {
        // Stub
    }

    @CallSuper
    @Override
    public void onNetworkConnected(@Nullable String networkType) {
        setHasNetworkConnection(true);
    }

    @CallSuper
    @Override
    public void onNetworkDisconnected() {
        setHasNetworkConnection(false);
    }

    @Override
    public final NitroProgressView getProgressView() {
        if (getNitroActivity() != null) {
            return getNitroActivity().getProgressView();
        }

        return null;
    }

    public final NitroActivity getNitroActivity() {
        return (getActivity() instanceof NitroActivity) ? (NitroActivity) getActivity() : null;
    }

    @Override
    public final <T extends View> T findViewById(@IdRes int id) {
        if (this.view != null) {
            return view.findViewById(id);
        }
        return null;
    }

    public final boolean hasMenu() {
        return this.getMenuResourceId() != null;
    }

    @Override
    public final void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (hasMenu()) {
            inflater.inflate(getMenuResourceId(), menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected boolean shouldAttachToRoot() {
        return false;
    }

    protected void handleCreateViewSavedInstanceState(@Nullable Bundle savedInstanceState) {
        // Stub
    }

    protected final LayoutInflater getInflater() {
        return inflater;
    }

    protected final ViewGroup getContainer() {
        return container;
    }

    private void setCreateProperties(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        this.inflater = inflater;
        this.container = container;
    }

    private void setHasNetworkConnection(boolean isConnected) {
        synchronized (hasNetworkConnection) {
            hasNetworkConnection.set(isConnected);
        }
    }
}

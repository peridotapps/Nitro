package com.peridotapps.nitro.ui.window;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.peridotapps.nitro.hardware.Network;
import com.peridotapps.nitro.ui.core.INitroWindow;
import com.peridotapps.nitro.ui.view.NitroProgressView;

abstract class NitroFragment extends Fragment implements INitroWindow, Network.NetworkStatusObserver {
  
  private LayoutInflater inflater;
  private ViewGroup container;
  private View view;
  
  @Override
  public void attachLayout() {
    view = inflater.inflate(getLayoutResourceId(), container, shouldAttachToRoot());
  }
  
  @Nullable
  @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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
    Network.getNetworkMonitor().addObserver(this);
    bindData();
  }
  
  @CallSuper
  @Override
  public void onPause() {
    Network.getNetworkMonitor().removeObserver(this);
    super.onPause();
  }
  
  @Override
  public void gatherControls() {
    // Stub
  }
  
  @Override
  public void bindData() {
    // Stub
  }
  
  @Override
  public final NitroProgressView getProgressView() {
    if (getActivity() != null && getActivity() instanceof NitroActivity) {
      return ((NitroActivity) getActivity()).getProgressView();
    }
    
    return null;
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
  public final void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    if (hasMenu()) {
      inflater.inflate(getMenuResourceId(), menu);
    }
    super.onCreateOptionsMenu(menu, inflater);
  }
  
  protected boolean shouldAttachToRoot() {
    return false;
  }
  
  protected void handleCreateViewSavedInstanceState(Bundle savedInstanceState) {
    // Stub
  }
  
  protected final LayoutInflater getInflater() {
    return inflater;
  }
  
  protected final ViewGroup getContainer() {
    return container;
  }
  
  private void setCreateProperties(LayoutInflater inflater, @Nullable ViewGroup container) {
    this.inflater = inflater;
    this.container = container;
  }
}

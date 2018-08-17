package com.peridotapps.nitro.ui.window;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.peridotapps.nitro.hardware.Network;
import com.peridotapps.nitro.ui.core.INitroWindow;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class NitroActivity extends FragmentActivity implements INitroWindow, Network.NetworkStatusObserver {
  
  @LayoutRes
  private int layoutResourceId;
  
  @MenuRes
  private Integer menuResourceId;
  
  private static final AtomicBoolean hasNetworkConnection = new AtomicBoolean(true);
  
  public NitroActivity (@LayoutRes int layoutResourceId) {
    this(layoutResourceId, null);
  }
  
  public NitroActivity (@LayoutRes int layoutResourceId, @Nullable @MenuRes Integer menuResourceId) {
    this.layoutResourceId = layoutResourceId;
    this.menuResourceId = menuResourceId;
  }
  
  @LayoutRes
  @Override
  public final int getLayoutResourceId () {
    return layoutResourceId;
  }
  
  @MenuRes
  @Override
  public final Integer getMenuResourceId () {
    return menuResourceId;
  }
  
  @Override
  public final boolean onCreateOptionsMenu (@NonNull Menu menu) {
    if (getMenuResourceId() != null) {
      MenuInflater inflater = new MenuInflater(this);
      inflater.inflate(getMenuResourceId(), menu);
    }
  
    return super.onCreateOptionsMenu(menu);
  }
  
  @Override
  public void attachLayout () {
    setContentView(getLayoutResourceId());
  }
  
  @CallSuper
  @Override
  protected void onCreate (@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    attachLayout();
    gatherControls();
  }
  
  @CallSuper
  @Override
  protected void onResume () {
    super.onResume();
  
    Network.getNetworkMonitor()
           .addObserver(this);
  
    bindData();
  }
  
  @CallSuper
  @Override
  protected void onPause () {
    Network.getNetworkMonitor()
           .removeObserver(this);
  
    super.onPause();
  }
  
  @Override
  public void gatherControls () {
    // Stub
  }
  
  @Override
  public void bindData () {
    // Stub
  }
  
  private final void setHasNetworkConnection (boolean isConnected) {
    synchronized (hasNetworkConnection) {
      hasNetworkConnection.set(isConnected);
    }
  }
  
  @CallSuper
  @Override
  public void onNetworkConnected (@NonNull String networkType) {
    setHasNetworkConnection(true);
  }
  
  @CallSuper
  @Override
  public void onNetworkDisconnected () {
    setHasNetworkConnection(false);
  }
}

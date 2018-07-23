package com.peridotapps.nitro.ui.window;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import com.peridotapps.nitro.hardware.Network;
import com.peridotapps.nitro.ui.core.INitroWindow;

abstract class NitroActivity extends Activity implements INitroWindow, Network.NetworkStatusObserver {
  
  @LayoutRes
  private int layoutResourceId;
  
  @MenuRes
  private Integer menuResourceId;
  
  public NitroActivity (@LayoutRes int layoutResourceId) {
    this(layoutResourceId, null);
  }
  
  public NitroActivity (@LayoutRes int layoutResourceId, @MenuRes Integer menuResourceId) {
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
  public final boolean onCreateOptionsMenu (Menu menu) {
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
}

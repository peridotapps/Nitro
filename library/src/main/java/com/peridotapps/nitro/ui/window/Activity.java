package com.peridotapps.nitro.ui.window;

public abstract class Activity extends NitroActivity {
  
  public Activity(int layoutResourceId) {
    super(layoutResourceId);
  }
  
  public Activity(int layoutResourceId, Integer menuResourceId) {
    super(layoutResourceId, menuResourceId);
  }
  
  @Override
  public final void attachLayout() {
    super.attachLayout();
  }

}

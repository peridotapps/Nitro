package com.peridotapps.nitro.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
  
  @Override
  public final void inflateLayout(Context context) {
    LayoutInflater inflater = LayoutInflater.from(context);
    inflater.inflate(getLayoutResourceId(), this, true);
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
}

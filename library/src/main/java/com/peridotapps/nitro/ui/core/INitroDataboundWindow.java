package com.peridotapps.nitro.ui.core;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

public interface INitroDataboundWindow<B extends ViewDataBinding> extends INitroWindow {
  
  @NonNull
  B getBinding ();
  
  void bindData (@NonNull B binding);

}

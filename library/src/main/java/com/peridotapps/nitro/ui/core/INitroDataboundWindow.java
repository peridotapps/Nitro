package com.peridotapps.nitro.ui.core;

import android.databinding.ViewDataBinding;

public interface INitroDataboundWindow<B extends ViewDataBinding> extends INitroWindow {

  B getBinding ();

  void bindData (B binding);

}

package com.peridotapps.nitro.ui.window;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;

import com.peridotapps.nitro.ui.core.INitroDataboundWindow;

public abstract class DataboundFragment<B extends ViewDataBinding> extends NitroFragment implements INitroDataboundWindow<B> {

  private B binding;
  
  @NonNull
  @Override
  public final B getBinding () {
    return binding;
  }

  @Override
  public final void attachLayout () {
    binding = DataBindingUtil.inflate(getInflater(), getLayoutResourceId(), getContainer(), shouldAttachToRoot());
  }

  @Override
  public void bindData (@NonNull B binding) {
    // stub
  }

  @Override
  public final void bindData () {
    bindData(getBinding());
  }
}

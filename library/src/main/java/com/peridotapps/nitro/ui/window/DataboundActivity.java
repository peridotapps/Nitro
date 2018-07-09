package com.peridotapps.nitro.ui.window;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import com.peridotapps.nitro.ui.core.INitroDataboundWindow;

public abstract class DataboundActivity<B extends ViewDataBinding> extends NitroActivity implements INitroDataboundWindow<B> {
  
  private B binding;
  
  public DataboundActivity(int layoutResourceId) {
    super(layoutResourceId);
  }
  
  public DataboundActivity(int layoutResourceId, Integer menuResourceId) {
    super(layoutResourceId, menuResourceId);
  }
  
  @Override
  public final void attachLayout() {
    binding = DataBindingUtil.setContentView(this, getLayoutResourceId());
  }
  
  @Override
  public final B getBinding() {
    return binding;
  }
  
  @Override
  public final void gatherControls() {
    // All controls are available from the binding object, so this method is stubbed and ignored
  }
  
  @Override
  public void bindData(B binding) {
    // Stub
  }
  
  @Override
  public final void bindData() {
    bindData(getBinding());
  }
  
}

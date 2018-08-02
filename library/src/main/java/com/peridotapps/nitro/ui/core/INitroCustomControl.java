package com.peridotapps.nitro.ui.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface INitroCustomControl extends INitroView {
  
  default boolean getAttachToRoot () {
    return true;
  }
  
  ViewGroup getViewGroup ();
  
  default View inflateLayout (@NonNull Context context) {
    return LayoutInflater.from(context)
                         .inflate(getLayoutResourceId(), getViewGroup(), getAttachToRoot());
  }
  
}

package com.peridotapps.nitro.intent;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.peridotapps.nitro.NitroApplication;
abstract class IntentBuilder {
  
  public final Intent build (){
    return this.build(null);
  }
  
  public abstract Intent build (@Nullable Integer flags);
  
  @NonNull
  protected static String processNull(String value) {
    return processNull(value, "");
  }
  
  @NonNull
  protected static String processNull(String value, @NonNull String defaultValue) {
    return (value != null) ? value : defaultValue;
  }
  
  protected static String getString (@StringRes int stringResId) {
    return NitroApplication.getSharedInstance().getString(stringResId);
  }
  
  
}

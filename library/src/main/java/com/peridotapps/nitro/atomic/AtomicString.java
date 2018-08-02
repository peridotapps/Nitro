package com.peridotapps.nitro.atomic;

import android.support.annotation.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public final class AtomicString extends AtomicReference<String> {
  
  public AtomicString () {
    super();
  }
  
  public AtomicString (@Nullable String value) {
    super(value);
  }
  
  @Nullable
  @Override
  public String toString () {
    return get();
  }
  
}

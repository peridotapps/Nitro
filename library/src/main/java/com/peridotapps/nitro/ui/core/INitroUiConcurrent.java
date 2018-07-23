package com.peridotapps.nitro.ui.core;

import android.os.Looper;
import com.peridotapps.nitro.concurrent.ConcurrentHandler;

public interface INitroUiConcurrent {
  
  default boolean isMainThread () {
    return isMainThread(Thread.currentThread());
  }
  
  default boolean isMainThread (Thread thread) {
    return thread.getId() == Looper.getMainLooper()
                                   .getThread()
                                   .getId();
  }
  
  default void runOnNewThread (Runnable action) {
    ConcurrentHandler.getSharedInstance()
                     .runOnNewThread(action);
  }
  
  default void runOnNewThread (Runnable action, long delay) {
    ConcurrentHandler.getSharedInstance()
                     .runOnNewThread(action, delay);
  }
  
  default void runOnUiThread (Runnable action) {
    ConcurrentHandler.getSharedInstance()
                     .runOnUiThread(action);
  }
  
  default void runOnUiThread (Runnable action, long delay) {
    ConcurrentHandler.getSharedInstance()
                     .runOnUiThread(action, delay);
  }
  
}
